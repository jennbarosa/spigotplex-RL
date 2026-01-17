package org.shufygoth.npcrl.environment.blockjump;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.shufygoth.npcrl.environment.EnvironmentCourse;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.stream.Stream;

public class BlockJumpArena implements EnvironmentCourse {
    private final List<Block> blocks;
    private final Block startingBlock;
    private final int courseLength;
    private final List<BlockFace> possibleDirections;

    public BlockJumpArena(final Block startingBlock, final BlockFace courseDirection, final int courseLength) {
        this.courseLength = courseLength;
        this.blocks = new ArrayList<>(courseLength + 1);
        this.startingBlock = startingBlock;
        this.possibleDirections =  Stream.of(BlockFace.NORTH,
                BlockFace.NORTH_EAST,
                BlockFace.EAST,
                BlockFace.SOUTH_EAST,
                BlockFace.SOUTH,
                BlockFace.SOUTH_WEST,
                BlockFace.WEST,
                BlockFace.NORTH_WEST)
                .filter(face -> !face.equals(courseDirection.getOppositeFace()))
                .toList(); // we don't want the course to deviate toward the opposite of our course direction

        build();
    }
    @Override
    public void build() {
        this.startingBlock.setType(Material.IRON_BLOCK);
        this.blocks.add(this.startingBlock);

        Block currentBlock = this.startingBlock;
        for (int i = 0; i < courseLength; i++) {
            // choose a direction
            int randomAmount = ThreadLocalRandom.current().nextInt(this.possibleDirections.size());
            BlockFace direction = this.possibleDirections.stream().skip(randomAmount).findFirst().orElse(BlockFace.SELF);

            // skip an amount of blocks defined by our skip generator method
            Block after = this.skipBlocks(currentBlock, direction, 2, 2);

            // place the destination block
            after.setType(Material.IRON_BLOCK);

            // next block
            this.blocks.add(after);
            currentBlock = after;
        }
    }

    @Override
    public void destroy() {
        this.blocks.forEach(((block) -> block.setType(Material.AIR)));
    }

    @Override
    public Collection<Block> getBlocks() {
        return this.blocks;
    }

    @Override
    public Block getStart() {
        return this.startingBlock;
    }

    /**
     * Skips a random amount of blocks of amount [min, max] and returns the resulting block
     * @param start starting point
     * @param direction the direction to skip in
     * @param min inclusive minimum length
     * @param max inclusive maximum length
     * @return the block we're on after skipping the random amount
     */
    private Block skipBlocks(Block start, BlockFace direction, int min, int max) {
        int amount = ThreadLocalRandom.current().nextInt(min, max+1);
        return start.getRelative(direction, amount);
    }

    private Block getBlockAfter(Block block) {
        int blockIndex = this.blocks.indexOf(block);
        int nextBlockIndex = blockIndex + 1;
        if (blockIndex == -1 || nextBlockIndex >= this.blocks.size())
            return null;
        return this.blocks.get(nextBlockIndex);
    }

    @Override
    public Map<String, Function<?, ?>> getInfo() {
        return Map.ofEntries(
                Map.entry("getBlockAfter()", (Function<Block, Block>)this::getBlockAfter)
        );
    }
}
