package org.shufygoth.npcrl.environment.blockjump;

import org.shufygoth.npcrl.environment.EnvironmentState;

public record BlockJumpState(
        // actual AI input parameters
    float npcYaw,
    float npcPitch,
    float npcVelocityX,
    float npcVelocityY,
    float npcVelocityZ,
    float npcDistFromCenterOfBlockX,
    float npcDistFromCenterOfBlockY,
    float npcDistFromCenterOfBlockZ,
    float npcDistanceToNextCenterOfBlockX,
    float npcDistanceToNextCenterOfBlockY,
    float npcDistanceToNextCenterOfBlockZ,
    float npcCurrentMovementSpeedFactor,
        // misc data
    boolean terminal,
    float score,
    float lastValidBlockX,
    float lastValidBlockY,
    float lastValidBlockZ,
    float currentBlockX,
    float currentBlockY,
    float currentBlockZ,
    boolean justJumped,
    boolean npcIsOnGround,
    boolean fell
) implements EnvironmentState
{}
