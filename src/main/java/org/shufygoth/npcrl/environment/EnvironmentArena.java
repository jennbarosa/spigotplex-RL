package org.shufygoth.npcrl.environment;

import org.bukkit.block.Block;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;

/**
 * Creates and destroys blocks used for the duration of the environment.
 */
public interface EnvironmentArena {
    void build();
    void destroy();
    Collection<Block> getBlocks();

    /**
     * Gives access to objects that when invoked can supply various info about this environment arena, such as arena dimensions, material, etc.
     * If the implementing class does not provide info, returns an empty Map by default
     * @return Key-value pairs of arena information.
     */
    default Map<String, ?> getInfo() {
        return Map.of();
    }
}
