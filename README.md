# Spigotplex NPC RL - Minecraft Reinforcement Learning Training System

A sophisticated system for training NPC agents in Minecraft using Deep Q-Network (DQN) reinforcement learning. This project combines a Spigot plugin backend with a PyTorch-based training frontend to create agents capable of doing whatever you want in minecraft

![Video Demonstration](video_demonstration.gif)

## Overview

This project this a several example training environments that use the Spigotplex system. 

The system uses a client-server architecture where Minecraft serves as the physics simulation environment, while Python handles the neural network training.

## Architecture

### Java Backend (Spigot Plugin)
- **Framework**: Spigot 1.8.8
- **HTTP/WebSocket Server**: Javalin 5.6.1 on port 8755
- **Purpose**: Creates controllable Minecraft environments and NPCs

### Python Frontend (RL Training)
- **Framework**: PyTorch + OpenAI Gym
- **Algorithm**: Deep Q-Network (DQN)
- **Purpose**: Handles training loops and neural network learning

## Features

### Example Training Environments

1. **Beam Walking** - NPC learns to walk across randomly generated beams without falling
2. **Parkour Course** - NPC masters jumping and precise positioning
3. **Landmark Navigation** - NPC navigates toward specific points in space

## Installation

### Prerequisites

**Java**:
- Java 17+
- Maven
- Spigot 1.8.8 server

**Python**:
- Python 3.8+
- PyTorch (with CUDA support recommended)
- OpenAI Gym
- NumPy
- Websockets

### Setup

1. **Build the Spigot plugin**:
```bash
mvn clean package
```

2. **Install Python dependencies**:
```bash
pip install torch gym numpy websockets pygame
```

3. **Configure Spigot server**:
   - Place the compiled JAR in your Spigot plugins folder
   - Start the Spigot server
   - The REST server will start on port 8755

## Usage

### Training an Agent

```bash
python src/main/python/npc_train.py
```

The training script will:
- Connect to the Minecraft server at `localhost:8755`
- Initialize the DQN neural network
- Run training episodes with epsilon-greedy exploration
- Save checkpoints every 200 episodes

### Manual Control with PS4 Controller

```bash
python src/main/python/control_npc_with_ps4.py
```

### Visualize Training Progress

```bash
python src/main/python/npc_train_plot.py
```

## API Reference

### REST Endpoints

- `GET /step?action={actionIndex}` - Execute one environment step
- `GET /reset` - Reset environment to initial state
- `GET /state` - Get current environment state

### WebSocket

- `/npc_rl` - Real-time bi-directional training communication

## Configuration

Training hyperparameters can be adjusted in [npc_train.py](src/main/python/npc_train.py):
- Learning rate (default: 0.01)
- Epsilon decay (default: 0.999)
- Batch size (default: 16)
- Experience replay size (default: 128)
- Target network sync frequency (default: 5)

## Project Structure

```
├── src/main/java/org/shufygoth/npcrl/
│   ├── environment/           # RL environment definitions
│   │   ├── beam/             # Beam walking environment
│   │   ├── blockjump/        # Block jumping environment
│   │   └── point/            # Landmark navigation
│   ├── rest/                 # REST API and WebSocket servers
│   └── plugin/               # Spigot plugin utilities
├── src/main/python/
│   ├── npc_train.py          # Main training script
│   ├── dqn_neural_network.py # DQN implementation
│   ├── npc_controller.py     # Server communication client
│   └── env/                  # Environment implementations
└── pom.xml                   # Maven configuration
```

## How It Works

1. **Environment Setup**: The Spigot plugin creates a controllable NPC in a specific environment
2. **State Observation**: The environment provides observations (position, velocity, distances, etc.)
3. **Action Selection**: The DQN neural network selects actions using epsilon-greedy policy
4. **Action Execution**: Actions are sent via WebSocket/HTTP to the Spigot server
5. **Reward Calculation**: Environment calculates rewards based on progress and failures
6. **Network Training**: DQN updates using experience replay and target networks
7. **Repeat**: Process continues until the agent learns optimal behavior

## Technical Details

### DQN Neural Network
- Configurable layer sizes (default: [input_dim, 24, output_dim])
- Tanh activation functions
- Experience replay buffer
- Target network for stable training
- CUDA GPU acceleration support

### NPC Controller
- Async WebSocket communication
- Event-driven message handling
- Automatic reconnection handling


