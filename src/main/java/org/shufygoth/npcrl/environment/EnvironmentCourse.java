package org.shufygoth.npcrl.environment;

import org.bukkit.block.Block;

/**
 * Represents an environment that has an origin point not within a center of a space.
 * <p>
 * For example: a parkour course would be suitable for this interface, since its starting point at the "start"
 */
public interface EnvironmentCourse extends EnvironmentArena {
    Block getStart();
}
