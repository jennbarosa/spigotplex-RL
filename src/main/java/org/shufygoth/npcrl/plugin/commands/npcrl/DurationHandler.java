package org.shufygoth.npcrl.plugin.commands.npcrl;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.shufygoth.npcrl.NpcRL;
import org.shufygoth.npcrl.environment.point.GetThePointEnvironment;
import org.shufygoth.npcrl.environment.point.GetThePointEnvironmentStepper;
import org.shufygoth.npcrl.plugin.ArgHandler;

import java.util.List;

public class DurationHandler implements ArgHandler {
    @Override
    public boolean handle(CommandSender sender, List<String> args) {
        if (args.size() == 1)
            return false;
        if (!(NpcRL.plugin.env instanceof GetThePointEnvironment)) {
            sender.sendMessage(String.format("%sThis command requires %s %sto be running.", ChatColor.DARK_RED, ChatColor.RED, ChatColor.DARK_RED));
            return false;
        }

        String duration = args.get(0);
        try {
            int durationTicks = Integer.parseInt(duration);
            GetThePointEnvironmentStepper.durationTicks = durationTicks;
            sender.sendMessage(String.format("%sEpisode duration changed to %s ticks", ChatColor.GOLD, durationTicks));
            return true;
        } catch (Exception ex) {
            sender.sendMessage(String.format("%s\"%s\" is not a valid duration in ticks", ChatColor.RED, duration));
            return false;
        }
    }
}
