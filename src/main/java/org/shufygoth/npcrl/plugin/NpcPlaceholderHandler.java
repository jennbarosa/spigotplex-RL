package org.shufygoth.npcrl.plugin;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.shufygoth.npcrl.npc.BasicNpc;
import org.shufygoth.npcrl.npc.Npc;

import java.util.List;

public class NpcPlaceholderHandler implements ArgHandler {
    private static Npc npc;
    @Override
    public boolean handle(CommandSender sender, List<String> args) {
        if (npc != null) {
            npc.destroy();
            npc = null;
            sender.sendMessage(String.format("%sNpc destroyed", ChatColor.RED));
            return true;
        }

        if (!(sender instanceof Player player))
            return false;

        npc = new BasicNpc(player.getLocation(), "Excavator");
        npc.look(new Vector(0, 0, 1)); // forward
        player.sendMessage(String.format("%sNpc spawned", ChatColor.GOLD));

        return true;
    }
}
