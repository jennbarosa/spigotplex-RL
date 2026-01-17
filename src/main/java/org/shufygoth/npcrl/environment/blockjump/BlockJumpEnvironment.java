package org.shufygoth.npcrl.environment.blockjump;

import net.minecraft.server.v1_8_R3.GenericAttributes;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftLivingEntity;
import org.bukkit.util.Vector;
import org.shufygoth.npcrl.NpcController;
import org.shufygoth.npcrl.environment.*;
import org.shufygoth.npcrl.npc.Npc;
import org.shufygoth.npcrl.npc.TrainingNpc;

import java.util.Map;
import java.util.function.Function;

import static org.shufygoth.npcrl.environment.NpcUtil.getEntity;

/**
 * NPC AI training environment where the NPC attempts to jump to the next block in the parkour.
 */
public class BlockJumpEnvironment implements NpcEnv {
    private final Location start;
    private final BlockFace direction;
    private NpcController npcController;
    private Npc npc;
    private EnvironmentCourse arena;
    private Block lastValidBlock;
    private float score;
    private boolean terminal;
    private boolean justJumped;
    private boolean fell;

    public BlockJumpEnvironment(Location start, BlockFace direction /* doesn't really matter */) {
        this.start = start;
        this.direction = direction;
        this.lastValidBlock = start.getBlock();
        this.npc = new TrainingNpc(this.lastValidBlock.getRelative(BlockFace.UP).getLocation().add(0.5, 0, 0.5), "luffyfornite2012");
        this.npcController = new NpcController(this.npc);
        this.npcController.simulateNpc(5);

        reset();
    }
    @Override
    public EnvironmentState step(NpcAction action) {
        // dont run if we're done
        if (this.terminal)
            return getState();

        this.justJumped = false;

        // execute action
        if (action.equals(NpcAction.JUMP)) {
            if (this.npc.entity().onGround) {
                this.npcController.simulateNpc(NpcAction.JUMP, 1);
                this.justJumped = true;
            }
        }
        else this.npcController.applyAction(action);

        Block currentBlock = NpcUtil.getFeetBlock(this.npc);

        // determine if NPC has fall off the course
        if (this.lastValidBlock.getY() - currentBlock.getY() > 3) {
            this.terminal = true;
            this.fell = true;
            return getState();
        }

        // determine if NPC has made it to a new parkour block 
        if (!currentBlock.equals(this.lastValidBlock) && currentBlock.getType().equals(Material.IRON_BLOCK) && this.npc.entity().onGround) {
            this.lastValidBlock = currentBlock; // the npc made it to a new block!
        }

        // determine of NPC has completed the course
        Block nextBlock = getNextBlock();
        if (nextBlock == null) {
            // npc made it to the last black of the course
            this.terminal = true;
            return getState();
        }

        return getState();
    }

    @Override
    public EnvironmentState reset() {
        if (this.arena != null)
            this.arena.destroy();

        this.lastValidBlock = start.getBlock();
        this.arena = new BlockJumpArena(start.getBlock(), direction, 1);
        this.npc.teleport(start.getBlock().getRelative(BlockFace.UP).getLocation().add(0.5, 0, 0.5));
        this.npc.entity().getBukkitEntity().setVelocity(new Vector(0, 0, 0));
        this.npc.look(new Vector(0, 0, 1)); // toward 0,0 pos z

        this.score = 0;
        this.terminal = false;
        this.fell = false;

        return getState();
    }

    @Override
    public void destroy() {
        if (this.arena != null)
            this.arena.destroy();
        if (this.npc != null && this.npc.exists())
            this.npc.destroy();
    }

    @Override
    public EnvironmentState getState() {
        CraftLivingEntity entity = getEntity(this.npc);
        Block npcNextBlock = getNextBlock();

        //float npcAngleToNextBlock = (float) getNpcAngleTowardBlock(npcNextBlock); // TODO
        Vector npcDistanceToNextCenterOfBlock =  npcNextBlock == null ? new Vector(0, 0, 0) : npcNextBlock.getLocation().add(0.5, 0, 0.5).toVector().subtract(entity.getLocation().toVector());
        float npcDistanceToNextCenterOfBlockX = (float) npcDistanceToNextCenterOfBlock.getX();
        float npcDistanceToNextCenterOfBlockY = (float) npcDistanceToNextCenterOfBlock.getY();
        float npcDistanceToNextCenterOfBlockZ = (float) npcDistanceToNextCenterOfBlock.getZ();
        float npcCurrentMovementSpeedFactor = (float) this.npc.entity().getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).getValue();

        // how fast npc moving in xyz
        Vector npcVelocity = getEntity(this.npc).getVelocity();
        float npcVelocityX = (float) npcVelocity.getX();
        float npcVelocityY = (float) npcVelocity.getY();
        float npcVelocityZ = (float) npcVelocity.getZ();

        // the distance from the center of the npcs last valid course block. can determine if the npc is off the edge
        Vector fromNpcFeetToCenterOfLastValidBlock = NpcUtil.getNpcDistanceFromCenterOfBlock(this.npc, this.lastValidBlock);
        float npcDistFromCenterOfBlockX = (float) fromNpcFeetToCenterOfLastValidBlock.getX();
        float npcDistFromCenterOfBlockY = (float) fromNpcFeetToCenterOfLastValidBlock.getY();
        float npcDistFromCenterOfBlockZ = (float) fromNpcFeetToCenterOfLastValidBlock.getZ();

        return new BlockJumpState(
                this.npc.entity().yaw,
                this.npc.entity().pitch,
                npcVelocityX, npcVelocityY, npcVelocityZ,
                npcDistFromCenterOfBlockX, npcDistFromCenterOfBlockY, npcDistFromCenterOfBlockZ,
                npcDistanceToNextCenterOfBlockX, npcDistanceToNextCenterOfBlockY, npcDistanceToNextCenterOfBlockZ,
                npcCurrentMovementSpeedFactor, this.terminal, this.score,
                (float) this.lastValidBlock.getX(), (float) this.lastValidBlock.getY(), (float) this.lastValidBlock.getZ(),
                (float) entity.getLocation().getX(), (float) entity.getLocation().getY(), (float) entity.getLocation().getZ(),
                this.justJumped, this.npc.entity().onGround, this.fell
        );
    }

    private Block getNextBlock() {
        Map<String, ?> arenaInfo = this.arena.getInfo();
        return ArenaInfoUtil.<Function<Block, Block>>unbox(arenaInfo, "getBlockAfter()").apply(this.lastValidBlock);
    }

    @Override
    public Npc getNpc() {
        return this.npc;
    }
}
