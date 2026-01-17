package org.shufygoth.npcrl.plugin.commands.npcrl;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.shufygoth.npcrl.plugin.ArgHandler;

import java.util.List;
import java.util.Set;

public class HelpHandler implements ArgHandler {
    private Set<String> availableArgs;

    public HelpHandler(Set<String> availableArgs) {
        this.availableArgs = availableArgs;
    }

    @Override
    public boolean handle(CommandSender sender, List<String> args) {
        sender.sendMessage(ChatColor.GOLD + "Available commands for /npcrl :)");
        sender.sendMessage(ChatColor.GOLD + "==================================");
        this.availableArgs.forEach(arg -> sender.sendMessage(String.format("%s/npcrl %s%s", ChatColor.LIGHT_PURPLE, ChatColor.GOLD, arg)));
        sender.sendMessage(ChatColor.GOLD + "==================================");

        return true;
    }
}
