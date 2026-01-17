from npc_env import NpcEnv
from gym.spaces import Box
from npc_observation_ranges import yaw, pitch
import numpy as np
import time
import math

npc_velocity_min_xz = -0.15
npc_velocity_max_xz = 0.15
npc_center_block_dist_min = -10
npc_center_block_dist_max = 10
npc_next_center_block_dist_min = -20
npc_next_center_block_dist_min = 20
npc_movement_factor_min = 0.05
npc_movement_factor_max = 0.5

npc_parkour_box_space = Box(
    low= np.array([yaw["min"], pitch["min"], 
                   npc_velocity_min_xz, npc_velocity_min_xz, npc_velocity_min_xz,
                   npc_center_block_dist_min, npc_center_block_dist_min, npc_center_block_dist_min,
                   npc_next_center_block_dist_min, npc_next_center_block_dist_min, npc_next_center_block_dist_min,
                   npc_movement_factor_min]),
    high=np.array([yaw["max"], pitch["max"], 
                   npc_velocity_max_xz, npc_velocity_max_xz, npc_velocity_max_xz,
                   npc_center_block_dist_max, npc_center_block_dist_max, npc_center_block_dist_max,
                   npc_next_center_block_dist_min, npc_next_center_block_dist_min, npc_next_center_block_dist_min,
                   npc_movement_factor_max]),
    dtype=np.float64
)

class NpcParkourState():
    def __init__(self, state_json):
        # state payload
        self.npcYaw = float(state_json["npcYaw"])
        self.npcPitch = float(state_json["npcPitch"])

        self.npcVelocityX = float(state_json["npcVelocityX"])
        self.npcVelocityY = float(state_json["npcVelocityY"])
        self.npcVelocityZ = float(state_json["npcVelocityZ"])

        self.npcDistFromCenterOfBlockX = float(state_json["npcDistFromCenterOfBlockX"])
        self.npcDistFromCenterOfBlockY = float(state_json["npcDistFromCenterOfBlockY"])
        self.npcDistFromCenterOfBlockZ = float(state_json["npcDistFromCenterOfBlockZ"])

        self.npcDistanceToNextCenterOfBlockX = float(state_json["npcDistanceToNextCenterOfBlockX"])
        self.npcDistanceToNextCenterOfBlockY = float(state_json["npcDistanceToNextCenterOfBlockY"])
        self.npcDistanceToNextCenterOfBlockZ = float(state_json["npcDistanceToNextCenterOfBlockZ"])

        self.npcCurrentMovementSpeedFactor = float(state_json["npcCurrentMovementSpeedFactor"])

        # misc 
        self.terminal = bool(state_json["terminal"])
        self.done = self.terminal
        self.score = float(state_json["score"])
        self.lastValidBlockX = float(state_json["lastValidBlockX"])
        self.lastValidBlockY = float(state_json["lastValidBlockY"])
        self.lastValidBlockZ = float(state_json["lastValidBlockZ"])
        self.currentBlockX = float(state_json["currentBlockX"])
        self.currentBlockY = float(state_json["currentBlockY"])
        self.currentBlockZ = float(state_json["currentBlockZ"])
        self.justJumped = bool(state_json["justJumped"])
        self.npcIsOnGround = bool(state_json["npcIsOnGround"])
        self.fell = bool(state_json["fell"])

        self.array = np.array([self.npcYaw, self.npcPitch, 
                               self.npcVelocityX, self.npcVelocityY, self.npcVelocityZ, 
                               self.npcDistFromCenterOfBlockX, self.npcDistFromCenterOfBlockY, self.npcDistFromCenterOfBlockZ,
                               self.npcDistanceToNextCenterOfBlockX, self.npcDistanceToNextCenterOfBlockY, self.npcDistanceToNextCenterOfBlockZ,
                               self.npcCurrentMovementSpeedFactor])



class NpcParkourEnv(NpcEnv):
    def __init__(self):
        super(NpcParkourEnv, self).__init__(npc_parkour_box_space)

    def step(self, action: int):
        # adapt json response to np array
        super_state_ = super(NpcParkourEnv, self).step(action)
        npc_state_ = NpcParkourState(super_state_["state"]).array, super_state_["reward"], super_state_["done"], super_state_["info"]
        return npc_state_

    def reset(self):
        super_state_ = super(NpcParkourEnv, self).reset()
        #time.sleep(0.1)
        return NpcParkourState(super_state_).array

    def terminal_func(self, state) -> bool:
        return NpcParkourState(state).done

    def reward_func(self, state_before, state_after) -> float:
        state = NpcParkourState(state_before)
        state_ = NpcParkourState(state_after)
 
        npc_velocity_magnitude = state_.npcVelocityX**2 + state_.npcVelocityY**2 + state_.npcVelocityZ**2
        reward = 0


        ####
        # Terminal Rewards
        ####
        if state_.done: # new state is terminal
            if state_.fell:
                return -50
            elif state_.npcIsOnGround:
                return +100
                
        ####
        # Step Rewards
        ####

        prev_block_x = state.npcDistFromCenterOfBlockX
        block_x = state_.npcDistFromCenterOfBlockX

        prev_block_z = state.npcDistFromCenterOfBlockZ
        block_z = state_.npcDistFromCenterOfBlockZ

        prev_next_block_x = state.npcDistanceToNextCenterOfBlockX
        next_block_x = state_.npcDistanceToNextCenterOfBlockX

        prev_next_block_z = state.npcDistanceToNextCenterOfBlockZ
        next_block_z = state_.npcDistanceToNextCenterOfBlockZ


        # if we're going away from the center of the current block
        if (block_x > prev_block_x): 
            reward += 0.5
        if (block_z > prev_block_z):
            reward += 0.5

        # if we're going away from the center of the NEXT block
        if (next_block_x < prev_next_block_x):
            reward += 0.5
        if (next_block_z < prev_next_block_z):
            reward += 0.5

        if (block_x > 0.6 or block_z > 0.6) and state_.justJumped:
            reward += 0.5


        return reward