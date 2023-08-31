package org.shufygoth.npcrl.npc;

import net.minecraft.server.v1_8_R3.Packet;

/**
 * Responsible for sending packets to players in the server.
 * The implementation chooses what players to send the packet to
 */
public interface PacketSender {
    /**
     * Attempts to send a packet to zero or more players using their {@code playerConnection}
      * @param packet The packet to send to players on the server
     * @param data Extra data for the PacketSender. Some implementations require to pass in something here
     * @return true if the packet was sent, false if not
     */
    boolean send(Packet<?> packet, Object... data);
}
