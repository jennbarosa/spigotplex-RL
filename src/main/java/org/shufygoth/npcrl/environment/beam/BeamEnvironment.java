package org.shufygoth.npcrl.environment.beam;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftLivingEntity;
import org.bukkit.util.Vector;
import org.shufygoth.npcrl.NpcController;
import org.shufygoth.npcrl.environment.NpcAction;
import org.shufygoth.npcrl.environment.NpcEnv;
import org.shufygoth.npcrl.environment.NpcUtil;
import org.shufygoth.npcrl.npc.BasicNpc;
import org.shufygoth.npcrl.npc.Npc;
import org.shufygoth.npcrl.npc.TrainingNpc;

/**
 * Npc AI environment where the npc must walk across different connected flat beams.
 * The beam course is randomly generated each time
 */
public class BeamEnvironment implements NpcEnv {
    private final Npc npc;
    private final NpcController npcController;
    private final Location start;
    private BeamArena arena;
    private Block lastValidBlock;
    private long episodeCounter;
    private int score;
    private boolean terminal;

    public BeamEnvironment(Location start) {
        this.arena = new BeamArena(start.getBlock(), 20,4);
        this.npc = new TrainingNpc(start.getBlock().getRelative(BlockFace.UP).getLocation().add(0.5, 0, 0.5), "Agent");
        this.npcController = new NpcController(this.npc);

        this.start = start;
        this.lastValidBlock = start.getBlock();
        this.episodeCounter = -1;
        this.score = 0;
        this.terminal = false;
    }

    @Override
    public BeamEnvironmentState step(NpcAction action) {
        // act
        this.npcController.applyAction(action);

        // find the current block in the course we're on
        Block npcBlock = getNpcCurrentBlock();

        // set this as the furthest we've gone
        this.score = this.arena.blockToNextBlockMap.get(npcBlock).index();
        this.episodeCounter--;
        return getState();
    }

    @Override
    public BeamEnvironmentState reset() {
        this.arena.destroy();
        this.arena = new BeamArena(this.start.getBlock(), 20, 4);
        this.npc.teleport(this.start.getBlock().getRelative(BlockFace.UP).getLocation().add(0.5, 0, 0.5));
        NpcUtil.getEntity(this.npc).setVelocity(new Vector(0, 0, 0));
        this.score = 0;
        this.episodeCounter = 20L*60;
        return getState();
    }

    @Override
    public void destroy() {
        this.arena.destroy();
        this.npc.destroy();
        this.episodeCounter = -1;
    }

    @Override
    public BeamEnvironmentState getState() {
        CraftLivingEntity entity = NpcUtil.getEntity(this.npc);
        Block nextBlock = getNpcNextBlock();
        Vector headingToNextBlock = nextBlock.getLocation().toVector().subtract(entity.getLocation().toVector());
        Vector distFromCurrentBlock = getNpcDistanceFromCenterOfCurrentBlock();
        Vector npcVelocity = entity.getVelocity();
        BeamEnvironmentTermination termination = isEnvironmentTerminal();

        return new BeamEnvironmentState(
                (int) Math.floor(this.npc.entity().locX),
                (int) Math.floor(this.npc.entity().locZ),
                (float) headingToNextBlock.getX(),
                (float) headingToNextBlock.getZ(),
                (float) npcVelocity.getX(),
                (float) npcVelocity.getZ(),
                (float) distFromCurrentBlock.getX(),
                (float) distFromCurrentBlock.getZ(),
                this.score,
                termination.done(),
                termination.reason().equals(BeamEnvironmentTerminationReason.HE_FELL)
        );
    }

    @Override
    public Npc getNpc() {
        return this.npc;
    }

    private Vector getNpcDistanceFromCenterOfCurrentBlock() {
        Vector centerOfLastValidBlock = this.getNpcCurrentBlock().getLocation().add(0.5, 0, 0.5).toVector();
        Vector playerLocation = NpcUtil.getEntity(this.npc).getLocation().toVector();

        return centerOfLastValidBlock.clone().subtract(playerLocation);
    }

    private Block getNpcCurrentBlock() {
        Block npcActualBlock = NpcUtil.getEntity(this.npc).getLocation().getBlock().getRelative(BlockFace.DOWN);
        if (this.lastValidBlock.equals(npcActualBlock))
            return npcActualBlock;
        if (!npcActualBlock.getType().equals(Material.IRON_BLOCK) && !npcActualBlock.getType().equals(Material.GLOWSTONE))
            return this.lastValidBlock;
        this.lastValidBlock = npcActualBlock;

        return this.lastValidBlock;
    }

    private Block getNpcNextBlock() {
        BlockFace nextFace = this.arena.blockToNextBlockMap.get(getNpcCurrentBlock()).nextFace();
        return getNpcCurrentBlock().getRelative(nextFace);
    }

    private BeamEnvironmentTermination isEnvironmentTerminal() {
        if (this.terminal)
            return new BeamEnvironmentTermination(BeamEnvironmentTerminationReason.NONE, true);
        if (this.npc == null || !this.npc.exists())
            return new BeamEnvironmentTermination(BeamEnvironmentTerminationReason.ENVIRONMENT_INVALID, true);
        if (this.arena == null)
            return new BeamEnvironmentTermination(BeamEnvironmentTerminationReason.ENVIRONMENT_INVALID, true);
        if ((int)this.npc.entity().locY < this.start.getBlockY()-1)
            return new BeamEnvironmentTermination(BeamEnvironmentTerminationReason.HE_FELL, true); // fell

        // it was tested that distances over 0.8 or less than -0.8 resulted in falling.
        //  ~0.79 is the last point on the block where you can stand freely.
        // so instead of blocking the thread waiting to see if he falls, we can simply check this value.
        Vector distFromCurrentBlock = getNpcDistanceFromCenterOfCurrentBlock();
        if (Math.abs((float)distFromCurrentBlock.getX()) > 0.8 || Math.abs((float)distFromCurrentBlock.getZ()) > 0.8) {
            // 99% chance npc is going to fall
            // even if he doesnt fall, it's out of bounds for this env anyways.
            // lets simulate his physics and see if he falls.
            if (NpcUtil.isFalling(this.npc))
                return new BeamEnvironmentTermination(BeamEnvironmentTerminationReason.HE_FELL, true);
        }

        if (this.arena.getBlockAfter(getNpcCurrentBlock()) == null) {
            // agent PROBABLY made it to the last block
            return new BeamEnvironmentTermination(BeamEnvironmentTerminationReason.HE_MADE_IT, true);
        }

        return new BeamEnvironmentTermination(BeamEnvironmentTerminationReason.NONE, false);
    }
}
