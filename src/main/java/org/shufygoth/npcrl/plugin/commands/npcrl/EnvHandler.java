package org.shufygoth.npcrl.plugin.commands.npcrl;

import org.bukkit.ChatColor;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.shufygoth.npcrl.NpcRL;
import org.shufygoth.npcrl.environment.beam.BeamEnvironment;
import org.shufygoth.npcrl.environment.blockjump.BlockJumpEnvironment;
import org.shufygoth.npcrl.environment.point.GetThePointEnvironment;
import org.shufygoth.npcrl.plugin.ArgHandler;

import java.util.List;

public class EnvHandler implements ArgHandler {
    @Override
    public boolean handle(CommandSender sender, List<String> args) {
        String arg = "destroy"; // safest
        if (args.size() == 2)
            arg = args.get(1).toLowerCase();

        if (!(sender instanceof Player player))
            return false;

        boolean special = true;
        switch (arg) {
            case "reset" -> NpcRL.plugin.env.reset();
            case "destroy" -> NpcRL.plugin.env.destroy();
            default -> special = false;
        }
        if (special)
            return false;

        player.sendMessage(String.format("%sCreating environment...", ChatColor.GOLD));
        NpcRL.plugin.env = switch (arg) {
            case "point" -> new GetThePointEnvironment();
            case "beam" -> new BeamEnvironment(player.getLocation());
            case "blockjump" -> new BlockJumpEnvironment(player.getLocation(), BlockFace.SOUTH);
            default -> new BlockJumpEnvironment(player.getLocation(), BlockFace.SOUTH);
        };
        return true;
    }
}
