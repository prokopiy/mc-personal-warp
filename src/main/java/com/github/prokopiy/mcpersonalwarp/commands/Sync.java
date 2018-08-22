package com.github.prokopiy.mcpersonalwarp.commands;


import com.github.prokopiy.mcpersonalwarp.Main;
import com.github.prokopiy.mcpersonalwarp.data.PlayerData;
import com.github.prokopiy.mcpersonalwarp.data.WarpData;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;

import java.util.ArrayList;
import java.util.List;


public class Sync implements CommandExecutor {
    private final Main plugin;

    public Sync(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Player player = (Player) src;

        plugin.syncWarpService();
        player.sendMessage(plugin.fromLegacy("&6Sync successfull!"));
        return CommandResult.success();
    }


}
