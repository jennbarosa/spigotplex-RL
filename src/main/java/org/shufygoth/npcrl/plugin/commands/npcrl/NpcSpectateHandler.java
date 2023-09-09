package org.shufygoth.npcrl.plugin.commands.npcrl;

import org.bukkit.ChatColor;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.shufygoth.npcrl.NpcRL;
import org.shufygoth.npcrl.NpcSpectator;
import org.shufygoth.npcrl.npc.Npc;
import org.shufygoth.npcrl.plugin.ArgHandler;

import java.util.List;

public class NpcSpectateHandler implements ArgHandler {
    NpcSpectator npcSpectatorController;
    @Override
    public boolean handle(CommandSender sender, List<String> args) {
        if (npcSpectatorController != null) {
            // toggle off
            npcSpectatorController.disable();
            return false;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "You have to be in-game to spectate npc...");
            return false;
        }

        Npc currentEnvNpc = NpcRL.plugin.env.getNpc();
        if (currentEnvNpc == null) {
            player.sendMessage(ChatColor.RED + "There is no NPC rn...");
            return false;
        }

        npcSpectatorController = new NpcSpectator(player, currentEnvNpc, BlockFace.SOUTH);
        npcSpectatorController.enable();
        player.sendMessage(ChatColor.GOLD + "Marone and barosa approve of this message");
        return true;
    }
}
