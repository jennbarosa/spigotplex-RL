package org.shufygoth.npcrl.environment;

import org.shufygoth.npcrl.npc.Npc;

/**
 * A bare minimum reinforcement learning environment for NPC AI.
 * <p>
 * Usually controlled externally via an HTTP or Websocket endpoint.
 * <p>
 * The environment lifecycle:
 * <p>
 * Given our current state {@code S_i}, we choose an action {@code A_i} to pass into our policy.
 * <p>
 * We pass the action into our policy with the {@code step()} method. This will run a full training step in the environment implementation.
 * Once you receive an {@code EnvironmentState} ({@code S_i+1}) you can calculate the reward, terminal condition, and losses.
 * <p>
 * Once we reach a terminal state, we can conclude the episode (epoch) and use {@code reset()} to prepare our environment
 * for the next training episode.
 */
public interface NpcEnv {
    EnvironmentState step(NpcAction action);
    EnvironmentState reset();
    void destroy();
    EnvironmentState getState();
    Npc getNpc();
}
