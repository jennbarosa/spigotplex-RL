from gym import Env
from gym.spaces import Discrete, Box
from npc_controller import NpcActionSpace, NpcEnvController

class NpcEnv(Env):
    def __init__(self, observation_space: Box):
        # Movement: forward, backward, left, right
        # Mouse: up, down, left, right
        self.action_space = Discrete(len(NpcActionSpace))
        self.observation_space = observation_space
        self.controller = NpcEnvController(8755)
        self.state = self.reset()

    def reward_func(self, state_before, state_after) -> float:
        pass

    def terminal_func(self, state) -> bool:
        pass

    def step(self, action: int):
        state_ = self.controller.apply_action(action)
        reward = self.reward_func(self.state, state_)
        done = self.terminal_func(state_)
        info = {} # placeholder
        
        self.state = state_
        return {"state": self.state, "reward": reward, "done": done, "info": info}
    
    def render(self):
        pass

    def reset(self):
        self.state = self.controller.reset_env()
        return self.state


        
