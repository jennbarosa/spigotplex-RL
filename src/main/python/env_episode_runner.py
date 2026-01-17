import numpy as np
from gym import Env
import torch as T
import matplotlib.pyplot as plt
from dqn_neural_network import DQN_Agent
from npc_controller import NpcActionSpace

# runs environment episodes and trains a deep q neural network on episode states
class EpisodeRunner():
    def __init__(self, env: Env, agent: DQN_Agent,  name: str = "training", checkpoint_interval=50, plot=True):
        self.env = env
        self.agent = agent
        self.name = name
        self.should_plot = plot
        self.checkpoint_interval = checkpoint_interval
        self.loaded_checkpoint = 0
        self.scores = []
        self.losses_list, self.reward_list, self.episode_len_list, self.epsilon_list  = [], [], [], []
        self.index = 128
        self.epsilon = 1

        self.init_experience_replay(256)
    
    def load(self, checkpoint: int):
        self.dqn.Q_eval.load_model_weights(f"{self.name}/{self.name}_model_checkpoint_{checkpoint}")
        self.loaded_checkpoint = checkpoint
    
    def save(self, episode):
        self.dqn.Q_eval.save_model_weights(f"{self.name}/{self.name}_model_checkpoint_{episode}")
    
    def init_experience_replay(self, exp_replay_size: int):
        # initiliaze experiance replay      
        index = 0
        for i in range(exp_replay_size):
            obs = self.env.reset()
            done = False
            while(done != True):
                A = self.agent.get_action(obs, self.env.action_space.n, epsilon=1)
                obs_next, reward, done, _ = self.env.step(A.item())
                self.agent.collect_experience([obs, A.item(), reward, obs_next])
                obs = obs_next
                index += 1
                if( index > exp_replay_size ):
                    break


    def run(self, n):
        fig, ax = plt.subplots()

        for episode in range(1, n+1):
            state = self.env.reset()
            done = False
            score = 0

            while not done:
                action = self.dqn.choose_action(state)
                state_, reward, done, info = self.env.step(action)
                self.dqn.store_transition(state, action, reward, state_, done)
                state = state_
                score += reward
                    
            
            self.dqn.learn() 

            self.scores.append(score)
            avg_score = np.mean(self.scores[-100:])
            self.on_episode_terminate(episode, score, avg_score, self.dqn.epsilon)

            if self.should_plot:
                ax.clear()
                ax.plot(range(1, episode + 1), self.scores, label='Episode Score')
                if episode >= 100:
                    moving_avg = [np.mean(self.scores[i-100:i]) for i in range(100, episode+1)]
                    ax.plot(range(100, episode+1), moving_avg, label='Moving Average', linestyle='--')
                ax.set_xlabel('Episode (Relative)')
                ax.set_ylabel('Score')
                ax.set_title('Training Progress')
                plt.pause(0.1)

            if episode > 0 and episode % self.checkpoint_interval == 0:
                self.save(episode+self.loaded_checkpoint)
    
    def on_step(self, episode: int, state_, reward: float, done: bool):
        pass

    def on_episode_terminate(self, episode: int, score: float, current_avg_score: float, eps: float):
        print(f"Episode {episode+self.loaded_checkpoint} Score: {score:.2f} Avg. Score: {current_avg_score:.2f} Epsilon: {eps:.2f}")
