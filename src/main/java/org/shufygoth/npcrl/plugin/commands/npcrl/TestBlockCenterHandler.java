package org.shufygoth.npcrl.plugin.commands.npcrl;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.shufygoth.npcrl.NpcRL;
import org.shufygoth.npcrl.plugin.ArgHandler;

import java.util.List;

public class TestBlockCenterHandler implements ArgHandler {
    @Override
    public boolean handle(CommandSender sender, List<String> args) {
        if (!(sender instanceof Player player))
            return false;

        this.blockCenterTestTask(player).runTaskTimer(NpcRL.plugin, 0, 1);
        return true;
    }

    private BukkitRunnable blockCenterTestTask(Player player) {
        return new BukkitRunnable() {
            long ticks = Long.MAX_VALUE;
            Block currentValidBlock = null;
            @Override
            public void run() {
                Block blockStandingOn = player.getLocation().getBlock().getRelative(BlockFace.DOWN);
                if (blockStandingOn.getType().equals(Material.IRON_BLOCK))
                    currentValidBlock = blockStandingOn;

                if (currentValidBlock == null) {
                    player.sendMessage(String.format("%sYou are on %s%s%s so it's not working", ChatColor.RESET, ChatColor.LIGHT_PURPLE, blockStandingOn.getType().name(), ChatColor.RESET));
                    return;
                }

                Vector centerOfLastValidBlock = this.currentValidBlock.getLocation().add(0.5, 0, 0.5).toVector();
                Vector playerLocation = player.getLocation().toVector();
                Vector distanceVector = centerOfLastValidBlock.clone().subtract(playerLocation);

                double xDistance = distanceVector.getX();
                double zDistance = distanceVector.getZ();

                player.sendMessage(String.format("%sX Distance:%s %.02f %s| %sZ Distance:%s %.02f", ChatColor.GOLD, ChatColor.RESET, xDistance, ChatColor.RESET, ChatColor.GOLD, ChatColor.RESET, zDistance));
                ticks--;
                if (ticks <= 0)
                    cancel();
            }
        };
    }
}
