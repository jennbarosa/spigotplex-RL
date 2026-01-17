package org.shufygoth.npcrl;

import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;
import org.shufygoth.npcrl.environment.EnvironmentState;
import org.shufygoth.npcrl.environment.NpcEnv;
import org.shufygoth.npcrl.plugin.CommandHandler;
import org.shufygoth.npcrl.plugin.SpigotWatchdogCheese;
import org.shufygoth.npcrl.plugin.TickMethod;
import org.shufygoth.npcrl.rest.NpcEnvironmentRestServer;
import org.shufygoth.npcrl.rest.SpigotCrossThreadWaiter;

import java.util.Arrays;

/**
 * Main plugin. Contains everything used within the entire plugin
 */
public final class NpcRL extends JavaPlugin {
    public static NpcRL plugin;
    public NpcEnv env;
    public NpcEnvironmentRestServer restServer;
    public TickMethod tickMethod;
    public CommandHandler commandHandler;

    @Override
    public void onEnable() {
        NpcRL.plugin = this;
        SpigotWatchdogCheese.releaseTheDogs();
        this.restServer = new NpcEnvironmentRestServer();
        this.tickMethod = new TickMethod();
        this.commandHandler = new CommandHandler(this);
    }

    @Override
    public void onDisable() {
        this.env.destroy();
        this.restServer.stop();
    }
}
