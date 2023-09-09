package org.shufygoth.npcrl.plugin.commands.npcrl;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import org.shufygoth.npcrl.NpcRL;
import org.shufygoth.npcrl.npc.Npc;
import org.shufygoth.npcrl.plugin.ArgHandler;

import java.util.List;

public class NpcVelocityHandler implements ArgHandler {
    static BukkitTask npcVelocityInfoTask;
    @Override
    public boolean handle(CommandSender sender, List<String> args) {
        if (npcVelocityInfoTask != null) {
            // toggle off
            npcVelocityInfoTask.cancel();
            return false;
        }
        npcVelocityInfoTask = this.npcVelocityInfo(sender).runTaskTimer(NpcRL.plugin, 0, 1);
        return true;
    }

    private BukkitRunnable npcVelocityInfo(final CommandSender sender) {
        return new BukkitRunnable() {
            long ticks = Long.MAX_VALUE;
            @Override
            public void run() {
                Npc npc = NpcRL.plugin.env.getNpc();
                Vector npcVelocity = npc.entity().getBukkitEntity().getVelocity();
                sender.sendMessage(String.format("%sVelocity:%s %.05f %.05f %.05f", ChatColor.GOLD, ChatColor.RESET, npcVelocity.getX(), npcVelocity.getY(), npcVelocity.getZ()));
                ticks--;
                if (ticks <= 0)
                    cancel();
            }
        };
    }
}
