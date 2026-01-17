package org.shufygoth.npcrl;

import org.bukkit.scheduler.BukkitRunnable;
import org.shufygoth.npcrl.environment.NpcAction;
import org.shufygoth.npcrl.npc.BasicNpc;
import org.shufygoth.npcrl.npc.Npc;

import javax.annotation.Nullable;

/**
 * Given an npc, gives the ability to apply enum actions to the npc which result in said action.
 */
public class NpcController {
    private final Npc npc;

    public NpcController(Npc npc) {
        this.npc = npc;
    }

    public void applyAction(NpcAction action) {
        switch (action) {
            case FORWARD -> npc.travel(1, 0);
            case BACKWARD -> npc.travel(-1, 0);
            case LEFT -> npc.travel(0, 1);
            case RIGHT -> npc.travel(0, -1);
            case YAW_UP -> this.rotation(5, 0);
            case YAW_DOWN -> this.rotation(-5, 0);
            case PITCH_UP -> this.rotation(0, 5);
            case PITCH_DOWN -> this.rotation(0, -5);
            case JUMP -> npc.jump();
            case START_SPRINTING -> npc.startSprinting();
            case STOP_SPRINTING -> npc.stopSprinting();
        }
    }

    private void rotation(float yaw, float pitch) {
        this.npc.rotate(yaw, pitch);
        if (this.npc instanceof BasicNpc baseNpc) {
            baseNpc.physicsHandler.step();
            baseNpc.forcePositionReplication();
        }
    }

    /**
     *
     * Blocks the calling thread until npc has been simulated for the given amount of ticks
     * @param action an optional action
     * @param iterations how many steps to simulate npc for
     */
    public void simulateNpc(@Nullable NpcAction action, int iterations) {
        for (int i = 0; i < iterations; i++) {
            if (action != null)
                this.applyAction(action);
            if (this.npc instanceof BasicNpc baseNpc) {
                baseNpc.physicsHandler.step();
                baseNpc.forcePositionReplication();
            }
        }
    }

    public void simulateNpc(int iterations) {
        this.simulateNpc(null, iterations);
    }
}
