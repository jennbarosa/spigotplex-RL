package org.shufygoth.npcrl.environment;

import org.bukkit.block.Block;

/**
 * An environment arena that has a definable "center" or "origin" point within 3D space.
 */
public interface EnvironmentBox extends EnvironmentArena {
    Block getCenter();
}
