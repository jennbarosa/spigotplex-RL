package org.shufygoth.npcrl.npc;

import org.bukkit.Location;

public class TrainingNpc extends BasicNpc {
    public TrainingNpc(Location spawn, String name) {
        super(spawn, name);
        this.physicsHandler.disable(); // do not automatically update
    }

    @Override
    public void travel(float forward, float strafe) {
        super.travel(forward, strafe);
        this.physicsHandler.step();
        this.forcePositionReplication();
    }
}
