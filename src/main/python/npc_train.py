import time
import torch
import os
from dqn_neural_network import DQN_Agent
from env.npc_beam_env import NpcBeamEnv
from env.npc_get_point_env import NpcLandmarkEnv
from env.npc_parkour_env import NpcParkourEnv
from env_episode_runner import EpisodeRunner
from npc_train_plot import update_plot


env = NpcParkourEnv()

checkpoint_interval = 200
input_dim = env.observation_space.shape[0]
output_dim = env.action_space.n  # everything but yaw and pitch controls
exp_replay_size = 128
agent = DQN_Agent(seed = 6548, layer_sizes = [input_dim, 24, output_dim], lr = 0.01, sync_freq = 5, exp_replay_size = exp_replay_size)


def init_exp_replay():
    # initiliaze experiance replay      
    index = 0
    for _ in range(exp_replay_size):
        if( index > exp_replay_size ):
            break
        obs = env.reset()
        done = False
        while(done != True):
            A = agent.get_action(obs, output_dim, epsilon=1)
            obs_next, reward, done, _ = env.step(A.item())
            agent.collect_experience([obs, A.item(), reward, obs_next])
            obs = obs_next
            index += 1
            if( index > exp_replay_size ):
                break

# Main training loop
def train(model_name, episodes, epsilon_start=0.99, target_network_update_interval=128, checkpoint_index=0, evaluating=False):
    index = 128
    time_print_interval = 5
    epsilon = epsilon_start

    for i in range(episodes):
        obs = env.reset()
        done = False
        score = 0
        while(done != True):
            start_time = time.time()
            A = agent.get_action(obs, output_dim, epsilon)
            
            obs_next, reward, done, _ = env.step(A.item())
            score += reward

            if not evaluating:
                agent.collect_experience([obs, A.item(), reward, obs_next])
        
            obs = obs_next
            index += 1
            
            if(index > target_network_update_interval):
                index = 0
                if not evaluating:
                    for j in range(4):
                        loss = agent.train(batch_size=16)

            end_time = time.time()
            time_print_interval -= 1
            elapsed_time_ms = (end_time - start_time) * 1000
            if time_print_interval == 0:
                #print(f"Step time: {elapsed_time_ms:.1f} ms")
                time_print_interval = 40

        epsilon = max(0.01, epsilon_start * (0.999 ** i))
        print(f'Episode {i+checkpoint_index}: Score: {score:.2f}, Epsilon: {epsilon:.2f}')
        update_plot(i, score, epsilon)

        #for name, param in agent.q_net.named_parameters():
        #    if 'weight' in name or 'bias' in name:
        #        print(f'Layer: {name}, Parameters: {param.data}')

        # Saving a checkpoint (e.g., inside your training loop)
        if not evaluating:
            if i % checkpoint_interval == 0:
                if not os.path.exists(model_name):
                    os.makedirs(model_name)
                    print(f"Directory '{model_name}' created.")
                torch.save(agent.q_net.state_dict(), f'{model_name}/{model_name}_checkpoint_{checkpoint_index+i}.pth')
    

model_name = input("Model name: ")
episodes = int(input("Episodes to run for: "))
checkpoint_index = input("Checkpoint?: ")
if len(checkpoint_index):
    checkpoint_index = int(checkpoint_index)
    checkpoint = torch.load(f'{model_name}/{model_name}_checkpoint_{checkpoint_index}.pth')
    agent.q_net.load_state_dict(checkpoint)
else:
    checkpoint_index = 0

init_exp_replay()
train(model_name=model_name, episodes=episodes, checkpoint_index=checkpoint_index, epsilon_start=0.01)