import pygame
from npc_controller import NpcActionSpace, NpcEnvController
import time

npc_control = NpcEnvController(8755)

class MyController:
    def __init__(self):
        pygame.init()
        pygame.joystick.init()
        self.controller = pygame.joystick.Joystick(0)
        self.controller.init()

    def run(self):
        while True:
            time.sleep(0.05)
            pygame.event.pump()
            
            # Read joystick axis values
            left_stick_x = self.controller.get_axis(2)  # Adjust axis number as needed
            left_stick_y = self.controller.get_axis(3)  # Adjust axis number as needed
            
            # Map joystick inputs to NPC actions
            if left_stick_x > 0.5:
                npc_control.apply_action(NpcActionSpace.RIGHT.value)
            elif left_stick_x < -0.5:
                npc_control.apply_action(NpcActionSpace.LEFT.value)
                
            if left_stick_y > 0.5:
                npc_control.apply_action(NpcActionSpace.BACKWARD.value)
            elif left_stick_y < -0.5:
                npc_control.apply_action(NpcActionSpace.FORWARD.value)

if __name__ == "__main__":
    controller = MyController()
    controller.run()
