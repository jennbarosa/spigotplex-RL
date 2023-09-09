package org.shufygoth.npcrl.npc;

// represents something that handles running physics for an NPC manually without the server's help
public interface PhysicsHandler {
    /**
     * Run a single step of physics
     * It is recommended to use this while  automatic NPC physics are disableds are disabled
     */
    void step();

    void enable();
    void disable();

    void travel(float forward, float strafe);
}
