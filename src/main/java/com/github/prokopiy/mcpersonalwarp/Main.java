package com.github.prokopiy.mcpersonalwarp;

import com.github.prokopiy.mcpersonalwarp.commands.*;
import com.github.prokopiy.mcpersonalwarp.data.PlayerData;
import com.github.prokopiy.mcpersonalwarp.data.WarpData;
import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;
import io.github.nucleuspowered.nucleus.api.service.NucleusWarpService;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializers;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandManager;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.profile.GameProfileManager;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

@Plugin(
        id = "mc-personal-warp",
        name = "MC Personal Warp"
)
public class Main {

    private static Main instance;

    @Inject
    public Logger logger;

    @Inject
    @DefaultConfig(sharedRoot = false)
    public Path defaultConf;

    @Inject
    @org.spongepowered.api.config.ConfigDir(sharedRoot = false)
    public Path ConfigDir;

    private CommandManager cmdManager = Sponge.getCommandManager();

    private Map<String, PlayerData> players;
    private Map<String, WarpData> warps;

    private Config config;




    @Listener
    public void Init(GameInitializationEvent event) throws IOException, ObjectMappingException {
        instance = this;
        this.config = new Config(this);

        TypeSerializers.getDefaultSerializers().registerType(TypeToken.of(PlayerData.class), new PlayerData.PlayerDataSerializer());
        TypeSerializers.getDefaultSerializers().registerType(TypeToken.of(WarpData.class), new WarpData.WarpDataSerializer());

        loadCommands(); logger.info("Load commands...");
        loadData(); logger.info("Load data...");
    }


    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        logger.info("Sync witch warp service...!");
        syncWarpService();
        logger.info("MC Personal Warp started!");
    }


    @Listener
    public void onPluginReload(GameReloadEvent event) throws IOException, ObjectMappingException {
        this.config = new Config(this);
        loadData();
        logger.info("MC Personal Warp reloaded.");
    }


    public void syncWarpService(){
        Optional<NucleusWarpService> warpService = Sponge.getServiceManager().provide(NucleusWarpService.class);
        if (warpService.isPresent()) {
            final List<WarpData> list = new ArrayList<WarpData>(getWarpsData());
            for (WarpData i : list) {
                String warpName = i.getWarpName();

                if (!warpService.get().warpExists(warpName)) {
                    removePWarp(warpName);
                }
            }
        }
    }

    public PlayerData addPlayer(PlayerData player) {
        return this.players.put(player.getPlayerName(), player);
    }

    public PlayerData updatePlayer(PlayerData player) {
        return this.players.replace(player.getPlayerName(), player);
    }


    public boolean playerNameExists(String name) {
        final List<PlayerData> list = new ArrayList<PlayerData>(getPlayersData());
        for (PlayerData i : list) {
            if (i.getPlayerName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public PlayerData getPlayerData(String name) {
        final List<PlayerData> list = new ArrayList<PlayerData>(getPlayersData());
        for (PlayerData i : list) {
            if (i.getPlayerName().equals(name)) {
                return i;
            }
        }
        return null;
    }

    public Integer getPlayerWarpLimit(String playerName) {
        final List<PlayerData> list = new ArrayList<PlayerData>(getPlayersData());
        for (PlayerData i : list) {
            if (i.getPlayerName().equals(playerName)) {
                return i.getWarpLimit();
            }
        }
        return 0;
    }

    public boolean warpNameExists(String warpName) {
//        return this.players.containsKey(warpName);
        final List<WarpData> list = new ArrayList<WarpData>(getWarpsData());
        for (WarpData i : list) {
            if (i.getWarpName().equals(warpName)) {
                return true;
            }
        }
        return false;
    }


    public WarpData addPWarp(WarpData warp) {
        return this.warps.put(warp.getWarpName(), warp);
    }

    public WarpData removePWarp(String warpName) {
        return this.warps.remove(warpName);
    }


    public String getWarpOwner(String warpName) {
        final List<WarpData> list = new ArrayList<WarpData>(getWarpsData());
        for (WarpData i : list) {
            if (i.getWarpName().equals(warpName)) {
                return i.getOwnerName();
            }
        }
        return null;
    }

    public Integer getWarpCount(String playerName) {
        Integer cnt = 0;
        final List<WarpData> list = new ArrayList<WarpData>(getWarpsData());
        for (WarpData i : list) {
            if (i.getOwnerName().equals(playerName)) {
                cnt += 1;
            }
        }
        return cnt;
    }




    public Collection<PlayerData> getPlayersData() {
        return Collections.unmodifiableCollection(this.players.values());
    }
    public Collection<WarpData> getWarpsData() {
        return Collections.unmodifiableCollection(this.warps.values());
    }

    public HoconConfigurationLoader getDataLoader() {
        return HoconConfigurationLoader.builder().setPath(this.ConfigDir.resolve("data.conf")).build();
    }

    private void loadData() throws IOException, ObjectMappingException {
        HoconConfigurationLoader loader = getDataLoader();
        ConfigurationNode rootNode = loader.load();
        List<WarpData> warpList = rootNode.getNode("Warps").getList(TypeToken.of(WarpData.class));
        this.warps = new HashMap<String, WarpData>();
        for (WarpData i : warpList) {
            this.warps.put(i.getWarpName(), i);
        }
        List<PlayerData> playerList = rootNode.getNode("Players").getList(TypeToken.of(PlayerData.class));
        this.players = new HashMap<String, PlayerData>();
        for (PlayerData i : playerList) {
            this.players.put(i.getPlayerUUID(), i);
        }
    }

    public void saveData() throws IOException, ObjectMappingException {
        HoconConfigurationLoader loader = getDataLoader();
        ConfigurationNode rootNode = loader.load();
        rootNode.getNode("Players").setValue(PlayerData.PlayerDataSerializer.token, new ArrayList<PlayerData>(this.players.values()));
        rootNode.getNode("Warps").setValue(WarpData.WarpDataSerializer.token, new ArrayList<WarpData>(this.warps.values()));
        loader.save(rootNode);
    }






    private void loadCommands() {

        // /pwarp set
        CommandSpec setPersonalWarp = CommandSpec.builder()
                .description(Text.of(""))
                .executor(new SetPersonalWarp(this))
                .arguments(GenericArguments.string(Text.of("WarpName")))
                .permission(Permissions.PWARP_SET)
                .build();

        // /pwarp setlimit
        CommandSpec setLimit = CommandSpec.builder()
                .description(Text.of(""))
                .executor(new SetLimit(this))
                .arguments(GenericArguments.optional(GenericArguments.string(Text.of("PlayerName"))),
                        GenericArguments.optional(GenericArguments.integer(Text.of("NewLimit"))))
                .permission(Permissions.PWARP_SETLIMIT)
                .build();

        // /pwarp inclimit
        CommandSpec incLimit = CommandSpec.builder()
                .description(Text.of(""))
                .executor(new IncLimit(this))
                .arguments(GenericArguments.optional(GenericArguments.string(Text.of("PlayerName"))),
                        GenericArguments.optional(GenericArguments.integer(Text.of("Count"))))
                .permission(Permissions.PWARP_INCLIMIT)
                .build();

        // /pwarp info
        CommandSpec playerInfo = CommandSpec.builder()
                .description(Text.of("Player's warp info"))
                .executor(new PlayerInfo(this))
                .permission(Permissions.PWARP_PLAYERINFO)
                .build();

        // /pwarp sync
        CommandSpec sync = CommandSpec.builder()
                .description(Text.of("Syncronize with warp service"))
                .executor(new Sync(this))
                .permission(Permissions.PWARP_SYNC)
                .build();


        // /pwarp remove
        CommandSpec removePersonalWarp = CommandSpec.builder()
                .description(Text.of("Remove player's warp"))
                .executor(new RemovePersonalWarp(this))
                .arguments(GenericArguments.string(Text.of("WarpName")))
                .permission(Permissions.PWARP_REMOVE)
                .build();


//
//
//        // /placelimiter block
//        CommandSpec block = CommandSpec.builder()
//                .description(Text.of("Base placerestrict block command"))
//                .executor(new Help(this))
//                .child(blockAddLookAt, "add")
//                .child(blockRemove, "remove")
//                .build();
//
//        // /placelimiter group
//        CommandSpec group = CommandSpec.builder()
//                .description(Text.of("Base placerestrict block command"))
//                .executor(new Help(this))
//                .child(groupAdd, "add")
//                .child(groupRemove, "remove")
//                .child(groupUpdate, "update")
//                .child(groupInfo, "info")
//                .child(groupList, "list")
//                .build();
//
//
        // /pwarp
        CommandSpec pwarp = CommandSpec.builder()
                .description(Text.of("Base Personal Warp command"))
                .executor(new Help(this))
                .child(setPersonalWarp, "set")
                .child(removePersonalWarp, "remove")
                .child(setLimit, "setlimit")
                .child(incLimit, "inclimit")
                .child(playerInfo, "info")
                .child(sync, "sync")
                .build();

//        cmdManager.register(this, whatsThis, "whatsthis");
        cmdManager.register(this, pwarp, "pwarp");
    }




    public Text fromLegacy(String legacy) {
        return TextSerializers.FORMATTING_CODE.deserializeUnchecked(legacy);
    }


}
