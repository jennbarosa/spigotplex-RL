package org.shufygoth.npcrl.environment.point;

import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.shufygoth.npcrl.environment.*;
import org.shufygoth.npcrl.npc.BasicNpc;
import org.shufygoth.npcrl.npc.Npc;

public final class GetThePointEnvironment implements NpcEnv, NpcArenaEnv {
    private final EnvironmentBox arena;
    private final Npc npc;
    private GetThePointArenaLandmark landmark;
    private final GetThePointEnvironmentStepper stepper;
    private final GetThePointEnvironmentResetter resetter;

    public GetThePointEnvironment() {
        this.arena = new GetThePointArena(20, 10, Material.IRON_BLOCK);
        this.landmark = new GetThePointArenaLandmark(this.arena.getCenter(), Material.REDSTONE_BLOCK);
        this.stepper = new GetThePointEnvironmentStepper();
        this.resetter = new GetThePointEnvironmentResetter(this);
        this.npc = new BasicNpc(this.arena.getCenter().getRelative(BlockFace.UP, 3).getLocation(), "Agent");

        reset();
    }


    public GetThePointEnvironmentState step(NpcAction action) {
        return this.stepper.stepOnce(this.npc, action, this.landmark);
    }


    public GetThePointEnvironmentState reset() {
        return this.resetter.resetEnvToInitialState();
    }


    @Override
    public void destroy() {
        this.resetter.resetEnvToInitialState();
        this.arena.getBlocks().forEach(block -> block.setType(Material.AIR));
        this.npc.destroy();
    }

    @Override
    public GetThePointEnvironmentState getState() {
        return this.stepper.createState(this.npc.entity().getBukkitEntity().getLocation(), this.landmark.getBlock().getLocation());
    }

    public Npc getNpc() {
        return npc;
    }

    public GetThePointArenaLandmark getLandmark() {
        return landmark;
    }

    public void setLandmark(GetThePointArenaLandmark landmark) {
        this.landmark = landmark;
    }

    public GetThePointEnvironmentStepper getStepper() {
        return stepper;
    }

    public GetThePointEnvironmentResetter getResetter() {
        return resetter;
    }

    @Override
    public EnvironmentBox getArena() {
        return this.arena;
    }
}
