package org.shufygoth.npcrl.plugin.commands.npcrl;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.shufygoth.npcrl.NpcRL;
import org.shufygoth.npcrl.environment.EnvironmentBox;
import org.shufygoth.npcrl.environment.point.GetThePointArena;
import org.shufygoth.npcrl.environment.point.GetThePointEnvironment;
import org.shufygoth.npcrl.plugin.ArgHandler;

import java.util.List;

public class CleanHandler implements ArgHandler {
    @Override
    public boolean handle(CommandSender sender, List<String> args) {
        sender.sendMessage(String.format("%sCleaning...", ChatColor.LIGHT_PURPLE));
        if (!(NpcRL.plugin.env instanceof GetThePointEnvironment env))
            return false;
        EnvironmentBox arena = env.getArena();
        Block center = arena.getCenter();
        for (int x = -25; x < 25; x++) {
            for (int y = -25; y < 25; y++) {
                for (int z = -25; z < 25; z++) {
                    center.getRelative(x, y, z).setType(Material.AIR);
                }
            }
        }
        sender.sendMessage(String.format("%sCleaned", ChatColor.GREEN));
        return true;
    }
}
