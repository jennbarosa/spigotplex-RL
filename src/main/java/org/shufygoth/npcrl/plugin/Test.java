package org.shufygoth.npcrl.plugin;

/**
 * Represents a single unit test that can run and return a result based on its success or failure.
 */
public interface Test<Result> {
    Result run();
}
