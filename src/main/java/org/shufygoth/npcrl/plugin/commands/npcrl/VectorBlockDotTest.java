package org.shufygoth.npcrl.plugin.commands.npcrl;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import org.shufygoth.npcrl.NpcRL;
import org.shufygoth.npcrl.plugin.ArgHandler;

import java.util.List;

public class VectorBlockDotTest implements ArgHandler {
    @Override
    public boolean handle(CommandSender sender, List<String> args) {
        if (!(sender instanceof Player player))
            return false;

        player.getLocation().setDirection(new Vector(1, 0, 0));

        Block playerBlock = player.getLocation().getBlock();
        Block testBlock = playerBlock.getRelative(4, 0, 0);

        testBlock.setType(Material.GOLD_BLOCK);
        playerBlock.setType(Material.GOLD_BLOCK);

        BukkitTask task = Bukkit.getScheduler().runTaskTimer(NpcRL.plugin, () -> {
            Location playerLocation = player.getLocation();
            Vector playerToBlock = testBlock.getLocation().toVector().subtract(playerLocation.toVector()).normalize();
            Vector playerDirection = playerLocation.getDirection().setY(0).normalize();

            // Calculate the angle between the player's direction and the vector to the block
            double angle = Math.toDegrees(Math.acos(playerDirection.dot(playerToBlock)));
            player.sendMessage(String.format("%sAngle: %s%.03f", ChatColor.WHITE, ChatColor.GOLD, angle));
        },0, 1);

        Bukkit.getScheduler().runTaskLater(NpcRL.plugin, task::cancel, 20L*30);

        return false;
    }
}
