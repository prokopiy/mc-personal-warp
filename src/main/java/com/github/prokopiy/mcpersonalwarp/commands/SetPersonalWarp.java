package com.github.prokopiy.mcpersonalwarp.commands;

//import com.crycode.mc.placerestrict.placerestrict.Config;

import com.github.prokopiy.mcpersonalwarp.Main;
import io.github.prokopiy.mcplacelimiter.Main;
import io.github.prokopiy.mcplacelimiter.data.GroupData;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.UUID;


public class SetPersonalWarp implements CommandExecutor {
    private final Main plugin;

    public SetPersonalWarp(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        String warpName = args.<String>getOne("WarpName").get();
        if (!(src instanceof Player)) {
            throw new CommandException(Text.of("Console users cannot use this command"));
        }

        Player player = (Player) src;
//        String playerName = player;
//        final java.util.List<ItemData> items = new ArrayList<ItemData>(plugin.getBlocksData());

        if (plugin.playerNameExists(player.getName())) {
            throw new CommandException(Text.of("Group already exists!"));
        } else {
            plugin.addGroup(new GroupData(playerName, groupLimit));
            try {
                plugin.saveData();
            } catch (Exception e) {
                player.sendMessage(Text.of("Data was not saved correctly."));
                e.printStackTrace();
            }
            player.sendMessage(plugin.fromLegacy("&e" + playerName + " &6was added witch &e" + groupLimit + "&6 limit."));
        }

        return CommandResult.success();
    }
}
