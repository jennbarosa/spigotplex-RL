package org.shufygoth.npcrl.environment.point;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftLivingEntity;
import org.shufygoth.npcrl.NpcController;
import org.shufygoth.npcrl.environment.NpcAction;
import org.shufygoth.npcrl.npc.Npc;

public class GetThePointEnvironmentStepper {
    public static int durationTicks = 200;
    public static boolean npcFell = false;
    private int stepsTaken = 0;
    protected GetThePointEnvironmentState createState(Location npcLocation, Location landmarkLocation) {
        return new GetThePointEnvironmentState(npcLocation.getYaw(),
                npcLocation.getPitch(),
                (float)npcLocation.getX(),
                (float)npcLocation.getZ(),
                landmarkLocation.getBlock().getX(),
                landmarkLocation.getBlock().getZ(),
                (float)landmarkLocation.distance(npcLocation),
                determineIfStateIsTerminal(npcLocation, landmarkLocation),
                GetThePointEnvironmentStepper.npcFell,
                npcLocation.getBlockX() == landmarkLocation.getBlockX() && npcLocation.getBlockZ() == landmarkLocation.getBlockZ(),
                "{}");
    }

    private void applyActionToNpc(NpcAction action, Npc npc) {
        // apply a decision
        NpcController npcController = new NpcController(npc);
        npcController.applyAction(action);
    }

    private boolean determineIfStateIsTerminal(Location npcLocation, Location landmarkLocation) {
        if (npcLocation.distanceSquared(landmarkLocation) < 1.5*1.5)
            return true;
        if (npcLocation.getBlockY() < landmarkLocation.getBlockY()-3) {
            npcFell = true;
            return true; // npc fell off
        }
        if (stepsTaken >= durationTicks)
            return true;
        return false;
    }

    public GetThePointEnvironmentState stepOnce(Npc npc, NpcAction action, GetThePointArenaLandmark landmark) {
        // change the current state of our environment based on the given action
        // return the new state, reward, etc
        CraftLivingEntity entity = (CraftLivingEntity) npc.entity().getBukkitEntity();

        // act on the environment
        this.applyActionToNpc(action, npc);
        this.stepsTaken++;

        // return data to model
        return this.createState(entity.getLocation(), landmark.getBlock().getLocation());
    }

    public void resetStepCounter() {
        this.stepsTaken = 0;
    }
}
