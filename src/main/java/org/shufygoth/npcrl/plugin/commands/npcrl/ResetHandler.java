package org.shufygoth.npcrl.plugin.commands.npcrl;

import org.bukkit.command.CommandSender;
import org.shufygoth.npcrl.NpcRL;
import org.shufygoth.npcrl.plugin.ArgHandler;

import java.util.List;

public class ResetHandler implements ArgHandler {
    @Override
    public boolean handle(CommandSender sender, List<String> args) {
        NpcRL.plugin.env.reset();
        return true;
    }
}
