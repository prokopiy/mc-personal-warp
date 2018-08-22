package com.github.prokopiy.mcpersonalwarp;

import com.github.prokopiy.mcpersonalwarp.commands.Help;
import com.github.prokopiy.mcpersonalwarp.commands.SetPersonalWarp;
import com.github.prokopiy.mcpersonalwarp.data.PlayerData;
import com.github.prokopiy.mcpersonalwarp.data.WarpData;
import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;
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
    private Logger logger;

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
        Sponge.getEventManager().registerListeners(this, new EventListener(this));

        TypeSerializers.getDefaultSerializers().registerType(TypeToken.of(PlayerData.class), new PlayerData.PlayerDataSerializer());
        TypeSerializers.getDefaultSerializers().registerType(TypeToken.of(WarpData.class), new WarpData.WarpDataSerializer());

        loadCommands(); logger.info("Load commands...");
        loadData(); logger.info("Load data...");
    }


    @Listener
    public void onServerStart(GameStartedServerEvent event) {
    }


    @Listener
    public void onPluginReload(GameReloadEvent event) throws IOException, ObjectMappingException {
        this.config = new Config(this);
        loadData();
        logger.info("MC Personal Warp reloaded.");
    }



    public PlayerData addPlayer(PlayerData player) {
        return this.players.put(player.getPlayerName(), player);
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


    public WarpData addWarp(WarpData warp) {
        return this.warps.put(warp.getWarpName(), warp);
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

        // /personalwarp whatsthis
        CommandSpec setPersonalWarp = CommandSpec.builder()
                .description(Text.of(""))
                .executor(new SetPersonalWarp(this))
                .arguments(GenericArguments.string(Text.of("WarpName")))
                .permission(Permissions.WARP_SET)
                .build();

//        // /placelimiter addgroup
//        CommandSpec groupAdd = CommandSpec.builder()
//                .description(Text.of("Add group"))
//                .executor(new AddGroup(this))
//                .arguments(GenericArguments.optional(GenericArguments.string(Text.of("GroupName"))),
//                        GenericArguments.optional(GenericArguments.integer(Text.of("GroupLimit"))))
//                .permission(Permissions.GROUP_ADD)
//                .build();
//
//        // /placelimiter removegroup
//        CommandSpec groupRemove = CommandSpec.builder()
//                .description(Text.of("Remove group and all block in this"))
//                .executor(new RemoveGroup(this))
//                .arguments(GenericArguments.string(Text.of("GroupName")))
//                .permission(Permissions.GROUP_REMOVE)
//                .build();
//
//        // /placelimiter updategroup
//        CommandSpec groupUpdate = CommandSpec.builder()
//                .description(Text.of("Update group limit"))
//                .executor(new UpdateGroup(this))
//                .arguments(GenericArguments.optional(GenericArguments.string(Text.of("GroupName"))),
//                        GenericArguments.optional(GenericArguments.integer(Text.of("GroupLimit"))))
//                .permission(Permissions.GROUP_UPDATE)
//                .build();
//
//        // /placelimiter group info
//        CommandSpec groupInfo = CommandSpec.builder()
//                .description(Text.of("Info group limit"))
//                .executor(new InfoGroup(this))
//                .arguments(GenericArguments.optional(GenericArguments.string(Text.of("GroupName"))))
//                .permission(Permissions.GROUP_INFO)
//                .build();
//
//        // /placelimiter group list
//        CommandSpec groupList = CommandSpec.builder()
//                .description(Text.of("Groups list"))
//                .executor(new ListGroup(this))
//                .permission(Permissions.GROUP_LIST)
//                .build();
//
//        // /placelimiter block add
//        CommandSpec blockAddLookAt = CommandSpec.builder()
//                .description(Text.of("Add the block, the player is looking at, to the group of limited blocks"))
//                .executor(new LookAt(this))
//                .arguments(GenericArguments.string(Text.of("GroupName")))
//                .permission(Permissions.BLOCK_ADD)
//                .build();
//
//
//        // /placelimiter block remove
//        CommandSpec blockRemove = CommandSpec.builder()
//                .description(Text.of("Remove limited block"))
//                .executor(new RemoveBlock(this))
//                .arguments(GenericArguments.remainingJoinedStrings(Text.of("BlockId")))
//                .permission(Permissions.BLOCK_REMOVE)
//                .build();
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
//        // /placerestrict
        CommandSpec pwarp = CommandSpec.builder()
                .description(Text.of("Base personalwarp command"))
                .executor(new Help(this))
                .child(setPersonalWarp, "set")
                .build();

//        cmdManager.register(this, bannedList, "banneditems");
//        cmdManager.register(this, whatsThis, "whatsthis");
//        cmdManager.register(this, itemAddLookAt, "itemAddLookAt");
//        cmdManager.register(this, itemAddHand, "itemAddHand");
        cmdManager.register(this, pwarp, "pwarp");
    }




    public Text fromLegacy(String legacy) {
        return TextSerializers.FORMATTING_CODE.deserializeUnchecked(legacy);
    }


}
