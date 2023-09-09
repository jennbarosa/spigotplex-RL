package org.shufygoth.npcrl;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftLivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.shufygoth.npcrl.npc.Npc;

/**
 * Client side player spectate npc
 */
public class NpcSpectator {
    private final Npc npc;
    private BlockFace orientation;
    private final Player player;
    private boolean enabled;
    private BukkitTask task;
    private int springArmDistance = 5;
    private int springArmLimit = springArmDistance + 3;
    private int springArmMinimum = springArmDistance - 2;
    public NpcSpectator(final Player player, final Npc npc, final BlockFace orientation) {
        this.npc = npc;
        this.orientation = orientation;
        this.enabled = false;
        this.player = player;
    }

    // 9.06.2023 - this does NOT work like you think it would
    private BukkitRunnable spectatorStep() {
        return new BukkitRunnable() {
            final CraftLivingEntity entity = (CraftLivingEntity) npc.entity().getBukkitEntity();
            private void orientSpectator() {
                Location newPosition = entity.getEyeLocation().add(orientation.getModX()*springArmDistance, 0, orientation.getModZ()*springArmDistance);
                if (player.isOnline() && !player.isDead()) {
                    Location locationWithPlayerRotation = newPosition
                            .setDirection(player.getLocation().getDirection());
                    locationWithPlayerRotation.setY(entity.getLocation().getY()+springArmDistance);
                    player.teleport(locationWithPlayerRotation);
                }
            }
            @Override
            public void run() {
                if (entity == null || npc == null || player == null) {
                    disable();
                    return;
                }

                double dist = entity.getLocation().distanceSquared(player.getLocation());
                if (dist > springArmLimit*springArmLimit || dist < springArmMinimum)
                    orientSpectator();

                // follow the npcs lead
                player.setVelocity(entity.getVelocity().setY(0));
            }
        };
    }
    public void enable() {
        this.enabled = true;
        this.task = this.spectatorStep().runTaskTimer(NpcRL.plugin, 0, 1);
    }

    public void disable() {
        this.task.cancel();
        this.enabled = false;
    }

    public int getSpringArmDistance() {
        return springArmDistance;
    }

    public void setSpringArmDistance(int springArmDistance) {
        this.springArmDistance = springArmDistance;
    }
}
