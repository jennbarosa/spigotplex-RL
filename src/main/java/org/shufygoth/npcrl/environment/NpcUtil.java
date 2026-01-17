package org.shufygoth.npcrl.environment;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.util.Vector;
import org.shufygoth.npcrl.npc.BasicNpc;
import org.shufygoth.npcrl.npc.Npc;

public final class NpcUtil {
    /**
     * Simulates the Npc for 1 - 5 time steps and determines if the npc has fell
     * @return fell
     */
    public static boolean isFalling(Npc npc) {
        int yBefore = (int) npc.entity().locY;
        for (int i = 0; i < 5; i++) {
            if (npc instanceof BasicNpc baseNpc) {
                baseNpc.travel(0, 0);
                baseNpc.physicsHandler.step();
                baseNpc.forcePositionReplication();
            } else {
                return false; // non-base npcs not supported
            }
        }
        int yAfter = (int) npc.entity().locY;
        return yAfter < yBefore;
    }

    /**
     * Gets the underlying bukkit entity implementation from the npc.
     * This method allows you to set the type of the variable assigned to the return value of this method to any value, as long as it is entity-based
     * @param npc npc
     * @return whatever you want, as long as it extends the bukkit CraftEntity class
     * @param <T> The type you want to return that extends CraftEntity
     */
    public static <T extends CraftEntity> T getEntity(Npc npc) {
        return (T) npc.entity().getBukkitEntity();
    }

    /**
     * Absolute vector lengths (distance) of > 0.79 is generally considered falling off the edge of the block 99.9% of the time
     * @return Vector from npc pos to center of feet block. Used for edge calculation
     */
    public static Vector getNpcDistanceFromCenterOfBlock(Npc npc, Block block) {
        Vector centerOfBlock = block.getLocation().add(0.5, 0, 0.5).toVector();
        Vector playerLocation = getEntity(npc).getLocation().toVector();
        return centerOfBlock.clone().subtract(playerLocation);
    }

    public static Block getFeetBlock(Npc npc) {
        return getEntity(npc).getLocation().getBlock().getRelative(BlockFace.DOWN);
    }

    private double getNpcAngleTowardBlock(Npc npc, Block block) {
        Location npcLocation = getEntity(npc).getLocation();
        Vector npcToBlock = block.getLocation().toVector().subtract(npcLocation.toVector()).normalize();
        Vector npcForward = npcLocation.getDirection().setY(0).normalize();
        return Math.toDegrees(Math.acos(npcToBlock.dot(npcForward)));
    }
}
