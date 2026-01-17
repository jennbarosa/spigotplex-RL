from npc_env import NpcEnv
from gym.spaces import Box
from npc_observation_ranges import yaw, pitch
import time
import numpy as np

min_distance_to_next_block = -20
max_distance_to_next_block = 20

max_distx = 1.00
min_distx = -max_distx
max_distz = 1.00
min_distz = -max_distz

npc_velocity_min_xz = -0.15
npc_velocity_max_xz = 0.15

next_block_dist_min_xz = -20
next_block_dist_max_xz = 20

npc_beam_box_space = Box(
    low= np.array([next_block_dist_min_xz, next_block_dist_min_xz, npc_velocity_min_xz, npc_velocity_min_xz, min_distx, min_distz]),
    high=np.array([next_block_dist_max_xz, next_block_dist_max_xz, npc_velocity_max_xz, npc_velocity_max_xz, max_distx, max_distz]),
    dtype=np.float64
)

TIME_LIMIT = 20*60

class NpcBeamState():
    def __init__(self, state):
        self.npcBlockX = int(state["npcBlockX"])
        self.npcBlockZ = int(state["npcBlockZ"])
        self.nextBlockDistX = float(state["nextBlockDistX"])
        self.nextBlockDistZ = float(state["nextBlockDistZ"])
        self.npcVelocityX = float(state["npcVelocityX"])
        self.npcVelocityZ = float(state["npcVelocityZ"])
        self.distXFromBlock = float(state["distanceFromBlockX"])
        self.distZFromBlock = float(state["distanceFromBlockZ"])
        self.score = int(state["score"])
        self.done = bool(state["done"])
        self.fell = bool(state["fell"])
        self.array = np.array([self.nextBlockDistX, self.nextBlockDistZ, self.npcVelocityX, self.npcVelocityZ, self.distXFromBlock, self.distZFromBlock])

class NpcBeamEnv(NpcEnv):
    def __init__(self):
        super(NpcBeamEnv, self).__init__(npc_beam_box_space)
        self.high_score = 0
        self.time_left = TIME_LIMIT

    def step(self, action: int):
        # adapt json response to np array
        super_state_ = super(NpcBeamEnv, self).step(action)
        npc_state_ = NpcBeamState(super_state_["state"]).array, super_state_["reward"], super_state_["done"], super_state_["info"]
        self.time_left -= 1
        return npc_state_

    def reset(self):
        self.time_left = TIME_LIMIT
        self.high_score = 0
        super_state_ = super(NpcBeamEnv, self).reset()
        return NpcBeamState(super_state_).array

    def terminal_func(self, state) -> bool:
        return NpcBeamState(state).done or self.time_left <= 0

    def reward_func(self, state_before, state_after) -> float:
        state = NpcBeamState(state_before)
        state_ = NpcBeamState(state_after)
        
        reward = 0
        new_high_score = False

        if state_.done:
            if state_.fell:
                reward -= 50 # less penalty for falling off later in the course
        else: # episode not done
            if state_.score > self.high_score:
                self.high_score = state_.score
                new_high_score = True
                reward += 50
                
            if (state_.distXFromBlock > 0.6 or state_.distZFromBlock > 0.6):
                reward -= 0.15

            if (state_.nextBlockDistX < state.nextBlockDistX):
                reward += 0.1
            elif state_.nextBlockDistX > state.nextBlockDistX and not new_high_score:
                reward -= 1

            if (state_.nextBlockDistZ < state.nextBlockDistZ):
                reward += 0.5
            elif state_.nextBlockDistZ > state.nextBlockDistZ and not new_high_score:
                reward -= 1

            # the equivalent of whipping a horse to make it go faster
            if (state_.npcVelocityX < 0.1):
                reward -= 0.5
            if state_.npcVelocityZ < 0.1:
                reward -= 0.5
        
        return reward