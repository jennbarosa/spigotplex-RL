package org.shufygoth.npcrl.npc;

import net.minecraft.server.v1_8_R3.EntityLiving;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

// handles running the physics for an NPC in Bukkit/Spigot
public class NpcPhysicsHandler extends BukkitRunnable implements PhysicsHandler {
    public NpcPhysicsHandler(Npc npc) {
        this.npc = npc;
    }

    @Override
    public void run() {
        if(!npc.exists()) {
            cancel();
            Bukkit.getLogger().warning("NpcPhysicsHandler task automatically cancelled as contained NPC no longer exists");
            return;
        }

        if(!physicsAllowed) return;
        step();
        npc.forcePositionReplication();
        moveForward = 0;
        moveStrafe = 0;
    }

    @Override
    public synchronized BukkitTask runTaskTimer(Plugin plugin, long delay, long period) throws IllegalArgumentException, IllegalStateException {
        this.enable();
        return super.runTaskTimer(plugin, delay, period);
    }

    @Override
    public synchronized void cancel() throws IllegalStateException {
        this.disable();
        super.cancel();
    }

    @Override
    public void step() {
        EntityLiving entity = npc.entity();
        // g(float, float) = moveWithHeading
        // K() = entity tick
        entity.K();
        entity.g(moveStrafe, moveForward);
    }

    @Override
    public void enable() {
        physicsAllowed = true;
    }

    @Override
    public void disable() {
        physicsAllowed = false;
    }

    @Override
    public void travel(float forward, float strafe) {
        this.moveForward = forward;
        this.moveStrafe = strafe;
    }

    public final Npc npc;
    private boolean physicsAllowed = false;

    private float moveForward;
    private float moveStrafe;
}
