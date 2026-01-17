package org.shufygoth.npcrl.plugin;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class SnakeBreadthCaveHandler implements ArgHandler {
    private void makeRow(Block origin, int radius) {
        for (int i = -radius; i < radius; i++) {
            Block col = origin.getRelative(BlockFace.WEST, i);
            col.setType(Material.AIR);
            col.getRelative(BlockFace.UP).setType(Material.AIR);
        }
    }
    @Override
    public boolean handle(CommandSender sender, List<String> args) {
        if (!(sender instanceof Player player))
            return false;

        final int SNAKE_FORWARD_LENGTH = 10;
        final int RADIUS = 20;
        Block current = player.getLocation().getBlock();
        for (int i = 0; i < SNAKE_FORWARD_LENGTH; i++) {
            makeRow(current, RADIUS);
            current.getRelative(BlockFace.WEST, RADIUS).getRelative(BlockFace.SOUTH).setType(Material.AIR);
            current = current.getRelative(BlockFace.SOUTH, 2);
        }

        return false;
    }
}
