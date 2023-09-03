from npc_env import NpcEnv
from gym.spaces import Box
from npc_observation_ranges import yaw, pitch
import time
import numpy as np

arena_pos_x_min = -1000.0  # Minimum x location
arena_pos_x_max = 1000.0   # Maximum x location

arena_pos_z_min = -1000.0  # Minimum z location
arena_pos_z_max = 1000.0   # Maximum z location

landmark_x_min = -5000
landmark_x_max = 5000

landmark_z_min = -5000
landmark_z_max = 5000

npc_landmark_box_space = Box(
    low=np.array([yaw['min'], pitch['min'], arena_pos_x_min, arena_pos_z_min, landmark_x_min, landmark_z_min]),
    high=np.array([yaw['max'], pitch['max'], arena_pos_x_max, arena_pos_z_max, landmark_x_max, landmark_z_max]),
    dtype=np.float64
)

class NpcLandmarkState():
    def __init__(self, state):
        self.yaw = float(state["npcYaw"])
        self.pitch = float(state["npcPitch"])
        self.x = float(state["npcX"])
        self.z = float(state["npcZ"])
        self.landmark_x = float(state["landmarkX"])
        self.landmark_z = float(state["landmarkZ"])
        self.done = bool(state["terminal"])
        self.npcFell = bool(state["npcFell"])
        self.npcAtLandmark = bool(state["npcAtLandmark"])
        self.npcDistanceToLandmark = float(state["npcDistanceToLandmark"])
        self.array = np.array([self.yaw, self.pitch, self.x, self.z, self.landmark_x, self.landmark_z])

class NpcLandmarkEnv(NpcEnv):
    def __init__(self):
        super(NpcLandmarkEnv, self).__init__(npc_landmark_box_space)
    
    def step(self, action: int):
        # adapt json response to np array
        time.sleep(0.05)
        super_state_ = super(NpcLandmarkEnv, self).step(action)
        state_ = (NpcLandmarkState(super_state_[0]).array, super_state_[1], super_state_[2], super_state_[3])
        return state_

    def reset(self):
        super_state_ = super(NpcLandmarkEnv, self).reset()
        return NpcLandmarkState(super_state_).array

    def terminal_func(self, state) -> bool:
        return NpcLandmarkState(state).done

    def reward_func(self, state_before, state_after) -> float:
        state = NpcLandmarkState(state_before)
        state_ = NpcLandmarkState(state_after)
        
        # reward for going in the right direction.
        dist = state.npcDistanceToLandmark
        dist_ = state_.npcDistanceToLandmark
        travel_dist = abs(dist_ - dist)
        if dist_ < dist:
            direction_reward = 1.0 / (dist_ ** 2 + 1.0)*200 # reward that scales higher as agent gets closer to goal
        elif dist_ == dist:
            direction_reward = -5 # penalize idling HEAVILY
        else: direction_reward = -((dist_**1.2)*0.5)

        # terminal rewards
        goal_reward = 0
        fall_penalty = 0
        if state_.done:
            if state_.npcAtLandmark:
                goal_reward = 100
            if state_.npcFell:
                fall_penalty = -200

        return direction_reward + goal_reward + fall_penalty