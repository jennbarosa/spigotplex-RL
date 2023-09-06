import matplotlib.pyplot as plt

from dqn_neural_network import DQN_Agent

# Create empty lists to store data
episode_list = []
score_list = []
epsilon_list = []

# Create a figure and axis for the plot
fig, ax = plt.subplots()

# Initialize the legend labels
ax.set_xlabel('Episode')
ax.set_ylabel('Score', color='tab:blue')
ax2 = ax.twinx()
ax2.set_ylabel('Epsilon', color='tab:red')

score_line, = ax.plot([], [], label='Score', color='tab:blue')
epsilon_line, = ax2.plot([], [], label='Epsilon', color='tab:red')

# Add legend only once
lines = [score_line, epsilon_line]
labels = [line.get_label() for line in lines]
ax.legend(lines, labels, loc='upper right')

def update_plot(episode, score, epsilon):
    # Append the new data to the lists
    episode_list.append(episode)
    score_list.append(score)
    epsilon_list.append(epsilon)

    # Update the data for Score and Epsilon lines
    score_line.set_data(episode_list, score_list)
    epsilon_line.set_data(episode_list, epsilon_list)

    # Automatically adjust the plot limits if needed
    ax.relim()
    ax.autoscale_view()
    ax2.relim()
    ax2.autoscale_view()

    # Redraw the plot
    plt.draw()
    plt.pause(0.01)

# Example usage:
# Call update_plot(episode, score, epsilon) within your training loop to update the graph

