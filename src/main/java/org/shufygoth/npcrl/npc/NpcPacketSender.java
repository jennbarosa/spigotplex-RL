package org.shufygoth.npcrl.npc;

import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class NpcPacketSender implements PacketSender {
    @Override
    public boolean send(Packet<?> packet, Object... data) {
        if (packet == null)
            return false;
        if (data.length == 0 || !(data[0] instanceof final Npc npc)) {
            Bukkit.getLogger().warning("Tried to send packet from" + this.getClass().getName() + " with no Npc passed into data");
            return false; // need npc
        }
        if (!npc.exists()) {
            Bukkit.getLogger().severe(String.format("Tried to send %s packet for an Npc that no longer exists in the server!", packet.getClass().getSimpleName()));
            return false;
        }

        final CraftLivingEntity entity = (CraftLivingEntity) npc.entity().getBukkitEntity();
        for (Player player : entity.getWorld().getPlayers()) {
            if (player == null || !player.isOnline())
                continue;

            PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
            connection.sendPacket(packet);
        }

        return false;
    }
}
