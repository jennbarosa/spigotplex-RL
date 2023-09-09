package org.shufygoth.npcrl.rest;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.shufygoth.npcrl.NpcRL;
import org.shufygoth.npcrl.environment.EnvironmentState;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

/**
 * DO NOT USE FROM THE MAIN THREAD!!!
 * @param <T> The type to return after waiting for spigot thread
 */
public class SpigotCrossThreadWaiter<T> {
    /**
     * BLOCKS THE CALLING THREAD
     * <p>
     * Do NOT use this from the main spigot/bukkit thread! You still stall the server!
     * <p>
     * Only use this method from another thread
     * <p>
     * <p>
     * Schedules the given function (supplier) to run on the main bukkit thread.
     * Blocks the calling thread until the supplier returns with the result of type T
     * @param func The supplier that returns the type T
     * @return the type T
     */
    public T schedule(Supplier<T> func) {
        AtomicReference<T> result = new AtomicReference<>(null);
        Bukkit.getScheduler().runTask(NpcRL.plugin, () -> result.set(func.get()));
        while (result.get() == null){} //   :)
        return result.get();
    }
}
