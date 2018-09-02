package com.github.prokopiy.mcpersonalwarp.commands;


import com.github.prokopiy.mcpersonalwarp.Main;
import com.github.prokopiy.mcpersonalwarp.data.PlayerData;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.Text;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;


public class IncLimit implements CommandExecutor {
    private final Main plugin;

    public IncLimit(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        String playerName = args.<String>getOne("PlayerName").get();
        Integer count = args.<Integer>getOne("Count").get();
        PlayerData playerData;
        if (!plugin.playerNameExists(playerName)) {
            Optional<UserStorageService> userStorage = Sponge.getServiceManager().provide(UserStorageService.class);
            if (userStorage.get().get(playerName).isPresent()) {
                String uuid = userStorage.get().get(playerName).get().getUniqueId().toString();
                plugin.addPlayer(new PlayerData(playerName, uuid, 0));
            } else {
                throw new CommandException(Text.of("Player not found!"));
            }
        }
        playerData = plugin.getPlayerData(playerName);
        Integer newlimit = playerData.getWarpLimit() + count;
        playerData.setWarpLimit(newlimit);
        plugin.addPlayer(playerData);
//        player.sendMessage(plugin.fromLegacy("&6Set personal warp limit for &e" + playerName + " &6to &e" + newlimit.toString()));

        try {
            plugin.saveData();
        } catch (Exception e) {
//            player.sendMessage(Text.of("Data was not saved correctly."));
            e.printStackTrace();
        }

        return CommandResult.success();
    }
}
