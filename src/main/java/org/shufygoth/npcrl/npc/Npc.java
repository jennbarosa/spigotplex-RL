package org.shufygoth.npcrl.npc;

import net.minecraft.server.v1_8_R3.EntityLiving;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

import java.util.Set;

/**
 * A fake NMS EntityPlayer that uses native minecraft and spigot functionality to trick the server into thinking that the NPC is a real player on the server.
 * Except some functionality not being available such as, but not limited to:
 * spectating, damaging, eating, dying
 */
public interface Npc {
    /**
     * Moves the NPC according to the <b>forward</b> and <b>strafe</b> factors
     * using its PhysicsHandler.
     * travel(1.0, 0.0) would move the NPC forward in the direction it is facing,
     * and travel(-1.0, 0.0) would do the opposite.
     * <p></p>The intended range for the values is -1.0 to 1.0
     * @param forward How much to move forward in the direction the NPC is looking
     * @param strafe How much to move sideways in the direction the NPC is looking
     */
    void travel(float forward, float strafe);

    /**
     * Rotate the Npc to be facing the {@code direction} given. Normalised.
     * @param direction The direction vector which the Npc will be facing
     */
    void look(final Vector direction);

    /**
     * Rotate the Npc by the delta amounts given
     * @param deltaYaw the change in yaw to apply to the Npc's rotation
     * @param deltaPitch the change in pitch to apply to the Npc's rotation
     */
    void rotate(final float deltaYaw, final float deltaPitch);

    /**
     * Makes the NPC jump
     */
    void jump();


    void startSprinting();

    void stopSprinting();

    boolean isSprinting();

    /**
     * Swings the Npc's arm and sets {@code block}'s material to MATERIAL_AIR
     * The npc does not pick up the block. No tile entity is dropped.
     * @param block The block to "mine".
     */
    void mine(final Block block);

    /**
     * Swings the Npc's arm and sets the block {@code at} to the {@code type} given
     * @param type The material of the block to "place"
     */
    void place(final Block at, final Material type);

    // please find a better way to do this !
    void forcePositionReplication();

    /**
     * Permanently destroys the Npc and removes from the world
     * @return
     */
    boolean destroy();

    /**
     * Get a view of all the blocks in the NPCS FOV
     * @return Set of blocks that the NPC can see with the given FOV and radius
     */
    Set<Block> view(final float fov, final float radius);
    void teleport(Location location);

    /**
     * @return the underlying entity of the NPC
     */
    EntityLiving entity();

    boolean exists();
}