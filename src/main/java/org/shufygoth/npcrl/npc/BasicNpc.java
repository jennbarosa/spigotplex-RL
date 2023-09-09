package org.shufygoth.npcrl.npc;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_8_R3.util.CraftMagicNumbers;
import org.bukkit.util.Vector;
import org.shufygoth.npcrl.NpcRL;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class BasicNpc implements Npc {
    public EntityPlayer entity;
    public final MinecraftServer minecraftServer;
    public final WorldServer worldServer;
    public final GameProfile gameProfile;
    final net.minecraft.server.v1_8_R3.World nmsWorld;
    static final IBlockData NMS_AIR_IBD = CraftMagicNumbers.getBlock(org.bukkit.Material.AIR).getBlockData();
    public static final PacketSender packetSender = new NpcPacketSender();
    public final NpcPhysicsHandler physicsHandler;
    private boolean sprinting;
    double baseSprintAttribute;

    public BasicNpc(final Location spawn, final String name) {
        this.minecraftServer = ((CraftServer) Bukkit.getServer()).getServer();
        this.worldServer = ((CraftWorld) spawn.getWorld()).getHandle();

        this.gameProfile = new GameProfile(UUID.randomUUID(), name);
        this.nmsWorld = ((CraftWorld) spawn.getWorld()).getHandle();
        this.entity = new EntityPlayer(this.minecraftServer, this.worldServer, this.gameProfile, new PlayerInteractManager(this.worldServer));
        this.entity.setLocation(spawn.getX(), spawn.getY(), spawn.getZ(), spawn.getYaw(), spawn.getPitch());
        this.baseSprintAttribute = this.entity.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).getValue();

        this.entity.playerInteractManager.setGameMode(WorldSettings.EnumGamemode.CREATIVE);

        this.physicsHandler = new NpcPhysicsHandler(this);
        physicsHandler.runTaskTimer(NpcRL.plugin, 0L, 1L);

        packetSender.send(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, this.entity), this);
        packetSender.send(new PacketPlayOutNamedEntitySpawn(this.entity), this);
        packetSender.send(new PacketPlayOutEntityHeadRotation(this.entity, (byte)(entity.yaw * 256 / 360)), this);
    }

    //@Override
    // keeping this in here because idk what to do with it and i dont want to delete it for no reason
    public void step(final float factor) {
        final Vector delta = entity.getBukkitEntity().getLocation().getDirection().normalize().multiply(factor);
        final double lengthSq = delta.lengthSquared();
        if (Double.isNaN(lengthSq) || Double.isInfinite(lengthSq))
            return;

        entity.setPosition(delta.getX() + entity.locX, delta.getY() + entity.locY, delta.getZ() + entity.locZ);
        packetSender.send(new PacketPlayOutEntityTeleport(entity), this);
    }

    @Override
    public void travel(float forward, float strafe) {
        physicsHandler.travel(forward, strafe);
    }

    @Override
    public void forcePositionReplication() {
        packetSender.send(new PacketPlayOutEntityTeleport(entity), this);
    }

    @Override
    public void jump() {
        if(!this.entity.onGround) return;
        this.entity.bF(); // the "jump" function
    }


    @Override
    public void startSprinting() {
        this.entity.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(baseSprintAttribute * 1.3);
        this.sprinting = true;
    }

    @Override
    public void stopSprinting() {
        this.entity.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(baseSprintAttribute);
        this.sprinting = false;
    }

    @Override
    public boolean isSprinting() {
        return this.sprinting;
    }

    @Override
    public void look(final Vector direction) {
        final double lengthSq = direction.lengthSquared();
        if (Double.isNaN(lengthSq) || Double.isInfinite(lengthSq))
            return;

        final float yaw = (float) Math.toDegrees(Math.atan2(direction.getZ(), direction.getX())) - 90;
        final float pitch = (float) Math.toDegrees(Math.asin(direction.getY())) * -1;

        entity.yaw =    yaw;  // clamp
        entity.pitch =  (float) Math.min(Math.max(-90.0, pitch), 90.0);

        packetSender.send(new PacketPlayOutEntityHeadRotation(entity, (byte)(yaw * 256 / 360)), this);
        packetSender.send(new PacketPlayOutEntity.PacketPlayOutEntityLook(entity.getId(), (byte)(yaw * 256 / 360), (byte)(pitch * 256 / 360), true), this);
    }

    @Override
    public void rotate(final float deltaYaw, final float deltaPitch) {
        float newYaw = entity.yaw + deltaYaw;
        float newPitch = entity.pitch + deltaPitch;

        entity.yaw = newYaw;
        entity.pitch = (float) Math.min(Math.max(-90.0, newPitch), 90.0);

        packetSender.send(new PacketPlayOutEntityHeadRotation(entity, (byte)(entity.yaw * 256 / 360)), this);
        packetSender.send(new PacketPlayOutEntity.PacketPlayOutEntityLook(entity.getId(), (byte)(entity.yaw * 256 / 360), (byte)(entity.pitch * 256 / 360), true), this);
    }


    @Override
    public void mine(final Block block) {
        packetSender.send(new PacketPlayOutAnimation(entity, 0), this);

        // mine
        BlockPosition bp = new BlockPosition(block.getX(), block.getY(), block.getZ());
        nmsWorld.setTypeAndData(bp, NMS_AIR_IBD, 2);
    }

    @Override
    public void place(final Block block, final org.bukkit.Material type) {
        packetSender.send(new PacketPlayOutAnimation(entity, 0), this);

        // place
        BlockPosition bp = new BlockPosition(block.getX(), block.getY(), block.getZ());
        IBlockData ibd = CraftMagicNumbers.getBlock(type).getBlockData();
        nmsWorld.setTypeAndData(bp, ibd, 2);
    }

    @Override
    public boolean destroy() {
        if (!exists())
            return false;

        packetSender.send(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, entity), this);
        packetSender.send(new PacketPlayOutEntityDestroy(entity.getId()), this);

        this.entity = null;
        return true;
    }

    @Override
    public Set<Block> view(float fov, float radius) {
        if (fov < 1.f) return Collections.EMPTY_SET;
        if (radius < 1.f) return Collections.EMPTY_SET;

        final CraftLivingEntity bukkitEntity = (CraftLivingEntity) entity().getBukkitEntity();
        final Vector fovRay = bukkitEntity.getEyeLocation().getDirection();
        final Vector fovStart = bukkitEntity.getLocation().clone().add(0, 1, 0).toVector();
        final Vector fovRayOneNormalizedStep = fovStart.add(fovRay.normalize().multiply(0.5f)); // step in the direction one block to avoid overlap with npc

        return getPositionsInView(fovRayOneNormalizedStep, radius, fov, fovRay);
    }

    @Override
    public void teleport(Location location) {
        entity.setPosition(location.getX(), location.getY(), location.getZ());
        packetSender.send(new PacketPlayOutEntityTeleport(entity), this);
    }

    @Override
    public EntityLiving entity() {
        return this.entity;
    }

    @Override
    public boolean exists() {
        return entity != null;
    }

    private Set<Block> getPositionsInView(Vector startPos, float radius, float degrees, Vector direction) {
        Set<Block> positions = new HashSet<>();        //    Returned list
        float squaredRadius = radius * radius;                     //    We don't want to use square root

        for (float x=startPos.getBlockX()-radius; x<startPos.getBlockX()+radius; x++)
            for (float y=startPos.getBlockY()-radius; y<startPos.getBlockY()+radius; y++)
                for (float z=startPos.getBlockZ()-radius; z<startPos.getBlockZ()+radius; z++) {
                    Vector relative = new Vector(x,y,z);
                    relative.subtract(startPos);
                    if (relative.lengthSquared() > squaredRadius) continue;            //    First check : distance
                    if (Math.abs((float)Math.toDegrees(direction.angle(relative))) > degrees) continue;    //    Second check : angle
                    positions.add(new Location(worldServer.getWorld(), x,y,z).getBlock());                                                //    The position v is in the cone
                }

        return positions;
    }
}