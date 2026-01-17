package org.shufygoth.npcrl.plugin.commands.npcrl;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import org.shufygoth.npcrl.NpcRL;
import org.shufygoth.npcrl.plugin.ArgHandler;

import java.util.List;

public class VelocityTestHandler implements ArgHandler {
    @Override
    public boolean handle(CommandSender sender, List<String> args) {
        if (!(sender instanceof Player player))
            return false;

        BukkitTask task = Bukkit.getScheduler().runTaskTimer(NpcRL.plugin, () -> {
            if (player != null && player.isOnline()) {
                Vector velocity = player.getVelocity();
                Bukkit.broadcastMessage(String.format("%sYour Velocity: %s%.03f %.03f %.03f", ChatColor.YELLOW, ChatColor.RESET, velocity.getX(), velocity.getY(), velocity.getZ()));
            }
        }, 0, 1);
        Bukkit.getScheduler().runTaskLater(NpcRL.plugin, task::cancel, 20L*60);

        return true;
    }
}
