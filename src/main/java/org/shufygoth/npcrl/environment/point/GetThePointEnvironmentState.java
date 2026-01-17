package org.shufygoth.npcrl.environment.point;

import org.shufygoth.npcrl.environment.EnvironmentState;

public record GetThePointEnvironmentState(
        float npcYaw,
        float npcPitch,
        float npcX,
        float npcZ,
        float landmarkX,
        float landmarkZ,
        float npcDistanceToLandmark,
        boolean terminal,
        boolean npcFell,
        boolean npcAtLandmark,
        String info
) implements EnvironmentState
{}
