package org.shufygoth.npcrl.environment.beam;

import org.shufygoth.npcrl.environment.EnvironmentState;

public record BeamEnvironmentState (
        int npcBlockX, // pos x
        int npcBlockZ, // pos z
        float nextBlockDistX,
        float nextBlockDistZ,
        float npcVelocityX,
        float npcVelocityZ,
        float distanceFromBlockX, // center of block
        float distanceFromBlockZ, // center of block
        float score, // the amount of blocks the npc made it through, the distance score
        boolean done, // terminal
        boolean fell // did npc fall
) implements EnvironmentState
{}
