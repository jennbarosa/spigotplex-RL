package org.shufygoth.npcrl.environment.beam;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.shufygoth.npcrl.environment.EnvironmentArena;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class BeamArena implements EnvironmentArena {
    protected final Map<Block, BeamBlockInfo> blockToNextBlockMap;
    private final int courseLength;
    private final int maxSegments;
    private final List<BlockFace> allowedDirections;
    private BlockFace courseHeading;
    protected Block lastBlock;

    public BeamArena(Block start, List<BlockFace> allowedDirections, int courseLength, int maxSegments) {
        this.maxSegments = maxSegments;
        this.allowedDirections = allowedDirections;
        this.courseHeading = this.getRandomValidTurn();
        this.blockToNextBlockMap = new HashMap<>();
        this.blockToNextBlockMap.put(start, new BeamBlockInfo(this.courseHeading, 0));
        this.lastBlock = start;
        this.courseLength = courseLength;

        start.setType(Material.GLOWSTONE);
        build();
    }

    public BeamArena(Block start, int courseLength, int maxSegments) {
        this(start, List.of(BlockFace.NORTH, BlockFace.EAST, BlockFace.WEST, BlockFace.NORTH_EAST, BlockFace.NORTH_WEST), courseLength, maxSegments);
    }


    private void addBlockToCourse(BlockFace direction)  {
        // point the last block's next face toward the new block's direction
        BeamBlockInfo lastBlockInfo = this.blockToNextBlockMap.get(this.lastBlock); // usually SELF
        this.blockToNextBlockMap.put(this.lastBlock, new BeamBlockInfo(direction, lastBlockInfo.index())); // replace SELF with new nextFace

        // register new block and add to course
        Block thisBlock = this.lastBlock.getRelative(direction);
        BeamBlockInfo thisBlockInfo = new BeamBlockInfo(BlockFace.SELF, lastBlockInfo.index() + 1);
        this.blockToNextBlockMap.put(thisBlock, thisBlockInfo);

        // update and show
        this.lastBlock = thisBlock;
        this.lastBlock.setType(Material.IRON_BLOCK);
    }
    private BlockFace getRandomValidTurn() {
        // we don't want to go back toward the direction we came from
        // remove direction from possible turns ON THIS CALL only
        List<BlockFace> validTurns = new ArrayList<>(this.allowedDirections);
        if (this.courseHeading != null)
            validTurns.remove(this.courseHeading.getOppositeFace());
        validTurns.remove(this.courseHeading); // do we really want the same direction twice?

        int randomTurn = ThreadLocalRandom.current().nextInt(validTurns.size());
        return validTurns.get(randomTurn);
    }

    @Override
    public void build() {
        int budget = this.courseLength;
        while (budget > 0) {
            // amount of blocks used for this "turn"
            int spend = ThreadLocalRandom.current().nextInt(budget+1);
            spend = Math.max(2, spend); // clamp spend value to 2 minimum, to allow at least one block of air between U-turns
            spend = Math.min(courseLength/this.maxSegments, spend); // clamp maximum spend to allow a more diverse course

            // choose a direction to go in, then build "spend" amount of blocks toward it
            this.courseHeading = this.getRandomValidTurn();
            for (int i = 0; i < spend; i++)
                this.addBlockToCourse(this.courseHeading);
            budget -= spend;
        }
    }

    @Override
    public void destroy() {
        this.blockToNextBlockMap.forEach((block, face) -> block.setType(Material.AIR));
        this.blockToNextBlockMap.clear();
    }

    @Override
    public Collection<Block> getBlocks() {
        return this.blockToNextBlockMap.keySet();
    }

    public Block getBlockAfter(Block block) {
        if (this.blockToNextBlockMap.get(block) == null)
            return null;

        BlockFace nextFace = this.blockToNextBlockMap.get(block).nextFace();
        if (nextFace.equals(BlockFace.SELF))
            return null; // this is the last block in the course
        return block.getRelative(nextFace);
    }
}
