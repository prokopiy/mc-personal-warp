package com.github.prokopiy.mcpersonalwarp.commands;


import com.github.prokopiy.mcpersonalwarp.Main;
import com.github.prokopiy.mcpersonalwarp.Permissions;
import com.github.prokopiy.mcpersonalwarp.data.PlayerData;
import com.github.prokopiy.mcpersonalwarp.data.WarpData;
import io.github.nucleuspowered.nucleus.api.service.NucleusWarpService;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.Optional;


public class RemovePersonalWarp implements CommandExecutor {
    private final Main plugin;

    public RemovePersonalWarp(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        String warpName = args.<String>getOne("WarpName").get();
        if (!(src instanceof Player)) {
            throw new CommandException(Text.of("Console users cannot use this command"));
        }
        Player player = (Player) src;

        if (plugin.warpNameExists(warpName)) {
            if (plugin.getWarpOwner(warpName).equals(player.getName()) | player.hasPermission(Permissions.PWARP_REMOVEOTHER) ) {
                plugin.removePWarp(warpName);
                Optional<NucleusWarpService> warpService = Sponge.getServiceManager().provide(NucleusWarpService.class);
                if (warpService.isPresent()) {
                    warpService.get().removeWarp(warpName);
                }
                player.sendMessage(plugin.fromLegacy("&6Warp removed!"));
                try {
                    plugin.saveData();
                } catch (Exception e) {
                    player.sendMessage(Text.of("Data was not saved correctly."));
                    e.printStackTrace();
                }
            }
        } else {
            throw new CommandException(Text.of("This warp not exists!"));
        }



        return CommandResult.success();
    }
}
