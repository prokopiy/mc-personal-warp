package com.github.prokopiy.mcpersonalwarp;

import com.flowpowered.math.vector.Vector3i;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.world.Chunk;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Date;
import java.util.regex.Pattern;


public class EventListener {

    private static final Pattern PATTERN_META = Pattern.compile("\\.[\\d+]*$");

    private Main plugin;
    public EventListener(Main instance) {
        plugin = instance;
    }


    @Listener
    public void onBlockPlace(ChangeBlockEvent.Place event, @Root Player player) {
//        player.sendMessage(plugin.fromLegacy("&conBlockPlace"));
        if (event.getTransactions().get(0).getFinal().getState().getType().equals(BlockTypes.AIR)) {return;}
        if (player.hasPermission(Permissions.BYPASS_LIMITED_BLOCK)) {return;}


    }




}
