package org.shufygoth.npcrl.plugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.shufygoth.npcrl.NpcRL;
import org.shufygoth.npcrl.plugin.commands.npcrl.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CommandHandler implements CommandExecutor {
    Map<String, ArgHandler> argMap;

    public CommandHandler(NpcRL plugin) {
        plugin.getCommand("npcrl").setExecutor(this);
        this.argMap = new HashMap<>();
        this.argMap.put("tpsrape",          new TpsRapeHandler());
        this.argMap.put("duration",         new DurationHandler());
        this.argMap.put("env",              new EnvHandler());
        this.argMap.put("reset",            new ResetHandler());
        this.argMap.put("clean",        new CleanHandler());
        this.argMap.put("testblockcenter",  new TestBlockCenterHandler());
        this.argMap.put("npcvelocity",      new NpcVelocityHandler());
        this.argMap.put("npcspectate",      new NpcSpectateHandler());
        this.argMap.put("npcsprint",        new NpcSprintTestHandler());
        this.argMap.put("blockdot",         new VectorBlockDotTest());
        this.argMap.put("help",             new HelpHandler(this.argMap.keySet()));
        this.argMap.put("unit",             new InGameUnitTestHandler());
        this.argMap.put("velocity",         new VelocityTestHandler());
        this.argMap.put("npc",              new NpcPlaceholderHandler());
        this.argMap.put("chunknuke",        new ChunkEraserHandler());
        this.argMap.put("snake",            new SnakeBreadthCaveHandler());
        this.argMap.put("blockscan",        new BlockScanHandler(plugin));
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!s.equalsIgnoreCase("npcrl"))
            return false;
        if (args.length == 0)
            return false;
        String cmd = args[0];
        return argMap.get(cmd).handle(commandSender, Arrays.stream(args).toList());
    }
}
