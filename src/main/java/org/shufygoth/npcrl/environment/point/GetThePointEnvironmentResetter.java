package org.shufygoth.npcrl.environment.point;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.shufygoth.npcrl.environment.ArenaInfoUtil;
import org.shufygoth.npcrl.environment.EnvironmentBox;
import org.shufygoth.npcrl.environment.NpcArenaEnv;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

// TODO
// TODO
// TODO
// Do not use this class. After the interfaces rework, this is too coupled to work
final class GetThePointEnvironmentResetter {
    private final NpcArenaEnv env;

    public GetThePointEnvironmentResetter(GetThePointEnvironment env) {
        this.env = env;
    }

    public Block getRandomSuitableArenaBlock() {
        // we dont want our landmark or agent to be in walls!
        Map<String, ?> arenaInfo = env.getArena().getInfo();
        Supplier<Integer> floorRadiusGetter = ArenaInfoUtil.unbox(arenaInfo, "floorRadius()");
        int floorRadius = floorRadiusGetter.get();

        List<Block> suitableBlocks = env.getArena().getBlocks()
                .stream()
                .filter(block -> block.getLocation().distanceSquared(((EnvironmentBox)env.getArena()).getCenter().getLocation()) < Math.pow(floorRadius-5, 2))
                .filter(block -> env.getNpc().entity().getBukkitEntity().getLocation().distanceSquared(block.getLocation()) > 10*10)
                .toList();
        return suitableBlocks
                .stream()
                .skip(ThreadLocalRandom.current().nextInt(suitableBlocks.size()))
                .findFirst()
                .orElse(null);
    }

    public GetThePointEnvironmentState resetEnvToInitialState() {
        // the beginning of a new training episode
        GetThePointArenaLandmark landmark = (GetThePointArenaLandmark) env.getArena().getInfo().get("landmark");
        if (env.getArena() == null) return null;
        if (landmark == null) return null;
        if (env.getNpc() == null) return null;

//        // Get a random position in the arena to place the NPC at

        // reset npc if he fell off
        if (env.getNpc().entity().getBukkitEntity().getLocation().getBlockY() <= landmark.getBlock().getY()-3) {
            Block randomNpcEpisodeStartBlock = getRandomSuitableArenaBlock();
            if (randomNpcEpisodeStartBlock == null)
                return null;
            env.getNpc().teleport(randomNpcEpisodeStartBlock.getRelative(BlockFace.UP, 2).getLocation());
        }
        GetThePointEnvironmentStepper.npcFell = false;

        // Get a random block from the arena to move the next landmark to
        Block randomLandmarkBlock = getRandomSuitableArenaBlock();
        if (randomLandmarkBlock == null)
            return null; // there must not be the necessary amount of blocks?

        // reset the landmark to our new chosen location
        landmark.hide();
      // TODO  env.setLandmark(new GetThePointArenaLandmark(randomLandmarkBlock, Material.REDSTONE_BLOCK));
        landmark.show();

        // reset environment stepper
       // TODO env.getStepper().resetStepCounter();

      // TODO
        return null;
    }
}
