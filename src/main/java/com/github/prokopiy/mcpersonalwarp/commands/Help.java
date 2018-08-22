package com.github.prokopiy.mcpersonalwarp.commands;

import com.github.prokopiy.mcpersonalwarp.Main;
import com.github.prokopiy.mcpersonalwarp.Permissions;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;

import java.util.ArrayList;
import java.util.List;

public class Help implements CommandExecutor {

    private final Main plugin;
    public Help(Main instance) {
        plugin = instance;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        showHelp(src);
        return CommandResult.success();
    }

    void showHelp(CommandSource sender) {
        PaginationService paginationService = Sponge.getServiceManager().provide(PaginationService.class).get();

        List<Text> contents = new ArrayList<>();
        if (sender.hasPermission(Permissions.PWARP_PLAYERINFO))
            contents.add(plugin.fromLegacy("&3/pwarp &binfo - &7Show personal warp info"));
        if (sender.hasPermission(Permissions.PWARP_SET))
            contents.add(plugin.fromLegacy("&3/pwarp &bset [WarpName] - &7Set personal warp in current position."));
        if (sender.hasPermission(Permissions.PWARP_SETLIMIT))
            contents.add(plugin.fromLegacy("&3/pwarp &bsetlimit [PlayerName] [NewLimit] - &7Set players warps limit."));
        if (sender.hasPermission(Permissions.PWARP_INCLIMIT))
            contents.add(plugin.fromLegacy("&3/pwarp &binclimit [PlayerName] [Count] - &7Increase players warps limit by Count."));
        if (sender.hasPermission(Permissions.PWARP_REMOVE) | sender.hasPermission(Permissions.PWARP_REMOVEOTHER))
            contents.add(plugin.fromLegacy("&3/pwarp &bremove [WarpName] - &7Remove personal warp."));

        if (contents.isEmpty()) {
            contents.add(plugin.fromLegacy("&cYou currently do not have any permissions for this plugin."));
        }
        paginationService.builder()
                .title(plugin.fromLegacy("&6Personal Warp Help"))
                .contents(contents)
                .header(plugin.fromLegacy("&3[] = required  () = optional"))
                .padding(Text.of("="))
                .sendTo(sender);
    }
}
