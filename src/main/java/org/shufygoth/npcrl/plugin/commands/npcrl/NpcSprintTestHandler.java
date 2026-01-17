package org.shufygoth.npcrl.plugin.commands.npcrl;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import org.shufygoth.npcrl.NpcRL;
import org.shufygoth.npcrl.npc.BasicNpc;
import org.shufygoth.npcrl.npc.Npc;
import org.shufygoth.npcrl.npc.TrainingNpc;
import org.shufygoth.npcrl.plugin.ArgHandler;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class NpcSprintTestHandler implements ArgHandler {
    @Override
    public boolean handle(CommandSender sender, List<String> args) {
        if (!(sender instanceof Player player))
            return false;

        String root = args.get(0);
        List<String> rootArgs = args.subList(1, args.size());

        String mainSubArg = rootArgs.get(0);
        switch (mainSubArg) {
            case "test" -> {
                Block testLocation = player.getWorld().getHighestBlockAt(player.getLocation());
                Npc npc = new BasicNpc(testLocation.getLocation(), "OlympicSprinter");
                player.teleport(npc.entity().getBukkitEntity().getLocation().add(player.getLocation().getDirection().multiply(-3).add(new Vector(2, 1, 2))));

                AtomicInteger ticks = new AtomicInteger(0);
                BukkitTask task = Bukkit.getScheduler().runTaskTimer(NpcRL.plugin, () -> {
                    if (npc.exists()) {
                        npc.travel(1, 0);
                        npc.rotate(3, 0);
                        int t = ticks.getAndIncrement();

                        Vector npcVel = npc.entity().getBukkitEntity().getVelocity();
                        Vector playerVel = player.getVelocity();

                        player.sendMessage(String.format("%sVelocity diff: %sX:(%s%.03f%s) Z:(%s%.03f%s)",
                                ChatColor.LIGHT_PURPLE,
                                ChatColor.RESET,
                                ChatColor.GOLD,
                                npcVel.getX()-playerVel.getX(),
                                ChatColor.RESET,
                                ChatColor.GOLD,
                                npcVel.getZ()-playerVel.getZ(),
                                ChatColor.RESET));

                        if (t % 120 == 0) {
                            player.sendMessage(String.format("Sprinting mode %s%s", npc.isSprinting() ? ChatColor.RED : ChatColor.GREEN, npc.isSprinting() ? "OFF" : "ON"));
                            if (npc.isSprinting()) {
                                npc.stopSprinting();
                            } else {
                                npc.startSprinting();
                            }
                        }
                    }
                }, 0, 1);
                Bukkit.getScheduler().runTaskLater(NpcRL.plugin, () -> {
                    task.cancel();
                    npc.destroy();
                }, 20L*30);
            }
        }

        return false;
    }
}
