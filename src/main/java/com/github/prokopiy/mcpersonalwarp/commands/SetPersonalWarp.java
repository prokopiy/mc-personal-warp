package com.github.prokopiy.mcpersonalwarp.commands;


import com.github.prokopiy.mcpersonalwarp.Main;
import com.github.prokopiy.mcpersonalwarp.data.PlayerData;
import com.github.prokopiy.mcpersonalwarp.data.WarpData;
import io.github.nucleuspowered.nucleus.api.service.NucleusWarpService;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.Sponge;

import java.util.Optional;


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

        Optional<NucleusWarpService> warpService = Sponge.getServiceManager().provide(NucleusWarpService.class);
        if (warpService.isPresent()) {
            if (plugin.warpNameExists(warpName) | (warpService.get().warpExists(warpName))) {
                throw new CommandException(Text.of(warpName + " already exists!"));
            } else {
                if (!plugin.playerNameExists(player.getName())) {
                    plugin.addPlayer(new PlayerData(player.getName(), player.getUniqueId().toString(), 0));
                    try {
                        plugin.saveData();
                    } catch (Exception e) {
                        player.sendMessage(Text.of("Data was not saved correctly."));
                        e.printStackTrace();
                    }
                }
                Integer warpLimit = plugin.getPlayerWarpLimit(player.getName());
                if (warpLimit > 0) {
                    if (warpService.get().setWarp(warpName, player.getLocation(), player.getRotation())) {
                        warpService.get().setWarpDescription(warpName, plugin.fromLegacy(" by " + player.getName()));
                        plugin.addPWarp(new WarpData(warpName, player.getName(), player.getUniqueId().toString()));

                        PlayerData playerData = plugin.getPlayerData(player.getName());
                        Integer newlimit = playerData.getWarpLimit() - 1;
                        playerData.setWarpLimit(newlimit);
                        plugin.addPlayer(playerData);

                        player.sendMessage(Text.of("Personal warp created!"));
                        try {
                            plugin.saveData();
                        } catch (Exception e) {
                            player.sendMessage(Text.of("Data was not saved correctly."));
                            e.printStackTrace();
                        }
                    }
                } else {
                    throw new CommandException(Text.of("Personal warp limited!"));
                }
            }
        } else {
            throw new CommandException(Text.of("NucleusWarpService not present!"));
        }
        return CommandResult.success();
    }
}
