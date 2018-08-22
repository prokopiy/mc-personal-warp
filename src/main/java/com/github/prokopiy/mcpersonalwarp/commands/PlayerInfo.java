package com.github.prokopiy.mcpersonalwarp.commands;


import com.github.prokopiy.mcpersonalwarp.Main;
import com.github.prokopiy.mcpersonalwarp.Permissions;
import com.github.prokopiy.mcpersonalwarp.data.PlayerData;
import com.github.prokopiy.mcpersonalwarp.data.WarpData;
import com.google.common.reflect.TypeToken;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;


public class PlayerInfo implements CommandExecutor {
    private final Main plugin;

    public PlayerInfo(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Player player = (Player) src;
        if (!plugin.playerNameExists(player.getName())) {
            plugin.addPlayer(new PlayerData(player.getName(), player.getUniqueId().toString(), 0));
        }
        showInfo(src);
        return CommandResult.success();
    }


    void showInfo(CommandSource sender) {
        PaginationService paginationService = Sponge.getServiceManager().provide(PaginationService.class).get();
        Player player = (Player) sender;
        List<Text> contents = new ArrayList<>();

        final List<WarpData> list = new ArrayList<WarpData>(plugin.getWarpsData());
        for (WarpData i : list) {
            if (i.getOwnerName().equals(player.getName())) {
                contents.add(plugin.fromLegacy("&e" + i.getWarpName()));
            }
        }
        if (contents.isEmpty()) {
            contents.add(plugin.fromLegacy("&cYou currently do not have any personal warps."));
        }
        paginationService.builder()
                .title(plugin.fromLegacy("&6Personal Warp Player Info"))
                .contents(contents)
                .header(plugin.fromLegacy("&e" + player.getName() + " &6has &e" + plugin.getWarpCount(player.getName()).toString() + " (" + plugin.getPlayerWarpLimit(player.getName())+" free) &6personal warps:"))
                .padding(Text.of("="))
                .sendTo(sender);
    }
}
