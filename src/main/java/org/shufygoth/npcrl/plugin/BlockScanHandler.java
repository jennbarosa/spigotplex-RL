package org.shufygoth.npcrl.plugin;

import net.minecraft.server.v1_8_R3.ChunkSection;
import net.minecraft.server.v1_8_R3.IBlockData;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.util.CraftMagicNumbers;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.shufygoth.npcrl.NpcRL;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.*;

public class BlockScanHandler implements ArgHandler, Listener {
    org.bukkit.World bukkitWorld = Bukkit.getWorlds().get(0);
    net.minecraft.server.v1_8_R3.World nmsWorld = ((CraftWorld) bukkitWorld).getHandle();
    Queue<Block> setBlockQueue = new ArrayBlockingQueue<>(16*255*16*2000);
    boolean chunksShouldNotUnload = false;
    public BlockScanHandler(NpcRL plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }


    private void scanBlocks(List<BlockMeta> arr, Chunk chunk) {
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = 0; y < 130; y++) {
                    Block b = chunk.getBlock(x, y, z);
//                    if (b.getLightLevel() == 15)
//                        continue;
//
//                    Block bup = b.getRelative(BlockFace.UP);
//                    if (b.getLightLevel() < 15 && bup.getLightLevel() == 15)
//                        setBlockQueue.add(bup);

                    BlockMeta blockMeta = new BlockMeta(x, y, z, b.getType());
                    setBlockQueue.add(b);

                    arr.add(blockMeta);
                }
            }
        }
    }


    /**
     * Incredibly fast method that uses NMS to set blocks.
     * Can set ~ 10 million blocks per second easily
     * Caveat: blocks do not appear until you rejoin
     */
    private void setBlockNmsFast(int x, int y, int z, Material type) {
        net.minecraft.server.v1_8_R3.Chunk nmsChunk = nmsWorld.getChunkAt(x >> 4, z >> 4);
        IBlockData ibd = CraftMagicNumbers.getBlock(type).getBlockData();

        ChunkSection cs = nmsChunk.getSections()[y >> 4];
        if (cs == null) {
            cs = new ChunkSection(y >> 4 << 4, true);
            nmsChunk.getSections()[y >> 4] = cs;
        }
        cs.setType(x & 15, y & 15, z & 15, ibd);
    }
    public List<Chunk> getChunksInRange(Location centerLocation, int range) {
        int centerX = centerLocation.getBlockX() >> 4; // Convert block coordinates to chunk coordinates
        int centerZ = centerLocation.getBlockZ() >> 4;

        List<Chunk> chunksInRange = new ArrayList<>(range*2);

        for (int xOffset = -range; xOffset <= range; xOffset++) {
            for (int zOffset = -range; zOffset <= range; zOffset++) {
                int chunkX = centerX + xOffset;
                int chunkZ = centerZ + zOffset;

                Chunk chunk = bukkitWorld.getChunkAt(chunkX, chunkZ);
                chunksInRange.add(chunk);
            }
        }

        return chunksInRange;
    }

    public boolean handle(CommandSender sender, List<String> args) {
        if (!(sender instanceof Player player))
            return false;

        chunksShouldNotUnload = true;
        NumberFormat numberFormat = NumberFormat.getInstance();
        try {
            int range = args.size() > 1 ? Integer.parseInt(args.get(1)) : 5;

            player.sendMessage(String.format("%sScanning...", ChatColor.GOLD));
            List<Chunk> chunksToScan = getChunksInRange(player.getLocation(), range);
            chunksToScan.forEach(chunk -> chunk.load(true));


            List<List<BlockMeta>> chunkBlockMetas = new ArrayList<>(chunksToScan.size()+1);
            for (int i = 0; i < chunksToScan.size(); ++i)
                chunkBlockMetas.add(new ArrayList<>(16*16*255));

            int corePoolSize = Runtime.getRuntime().availableProcessors(); // Adjust as needed
            int maxPoolSize = chunksToScan.size(); // Adjust as needed
            int queueCapacity = chunksToScan.size() + 1; // Adjust as needed
            ExecutorService executor = new ThreadPoolExecutor(corePoolSize, maxPoolSize, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(queueCapacity));

            Runnable[] tasks = new Runnable[chunksToScan.size()];

            long millisBefore = System.currentTimeMillis();
            for (int i = 0; i < chunksToScan.size(); i++) {
                final Chunk chunk = chunksToScan.get(i);
                int finalI = i;
                chunk.load(true);
                tasks[i] = () -> scanBlocks(chunkBlockMetas.get(finalI), chunk);
            }

            // Execute all tasks
            for (Runnable task : tasks) {
                executor.execute(task);
            }
            // Shutdown the executor after all tasks are created
            executor.shutdown();
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
            long millisElapsed = System.currentTimeMillis() - millisBefore;

            Location before = player.getLocation();
            player.teleport(new Location(Bukkit.getWorlds().get(1), 0, 0, 0), PlayerTeleportEvent.TeleportCause.NETHER_PORTAL);
            player.teleport(before, PlayerTeleportEvent.TeleportCause.UNKNOWN);

            long blockSetBegin = System.currentTimeMillis();
            setBlockQueue.forEach(block -> setBlockNmsFast(block.getX(), block.getY(), block.getZ(), Material.GOLD_BLOCK));
            long timeToSetBlocks = System.currentTimeMillis() - blockSetBegin;
            player.sendMessage(String.format("%s%sWow! %sIt took %s%s%d ms %sto set %s%s%s %sblocks",
                    ChatColor.GREEN,
                    ChatColor.BOLD,
                    ChatColor.GREEN,
                    ChatColor.LIGHT_PURPLE,
                    ChatColor.BOLD,
                    timeToSetBlocks,
                    ChatColor.GREEN,
                    ChatColor.GOLD,
                    ChatColor.BOLD,
                    numberFormat.format(setBlockQueue.size()),
                    ChatColor.GREEN));
            setBlockQueue.clear();

            int integrityBadAmount = 0;
            for (List<BlockMeta> chunkBlockMeta : chunkBlockMetas) {
                for (BlockMeta blockMeta : chunkBlockMeta) {
                    if (blockMeta == null) {
                        integrityBadAmount++;
                    }
                }
            }
            if (integrityBadAmount > 0) {
                player.sendMessage(String.format("%s%sWarning %sgrand array integrity is bad %s(%s%d %sbad blocks%s)",
                        ChatColor.RED,
                        ChatColor.BOLD,
                        ChatColor.LIGHT_PURPLE,
                        ChatColor.RESET,
                        ChatColor.RED,
                        integrityBadAmount,
                        ChatColor.GOLD,
                        ChatColor.RESET));
            }


            String blockCountFormatted = numberFormat.format((long) chunksToScan.size() * 16*16*255);
            String msg = String.format("%s%sHot damn! %sIt took %s%s%d %s %sto scan %s%s%s %sblocks (/%d) (%d chunks)",
                    ChatColor.GOLD,
                    ChatColor.BOLD,
                    ChatColor.GOLD,
                    ChatColor.LIGHT_PURPLE,
                    ChatColor.BOLD,
                    millisElapsed >= 1000 ? (int)millisElapsed/1000 : millisElapsed,
                    millisElapsed >= 1000 ? "s" : "ms",
                    ChatColor.GOLD,
                    ChatColor.RED,
                    ChatColor.BOLD,
                    blockCountFormatted,
                    ChatColor.GOLD,
                    chunksToScan.size() * 16*16*255,
                    chunksToScan.size());

            if (player == null || !player.isOnline())
                Bukkit.broadcastMessage(msg);
            else player.sendMessage(msg);
        } catch (Exception ex) {
            String exceptionMsg = String.format("%s%sError! %s%s",
                    ChatColor.DARK_RED,
                    ChatColor.BOLD,
                    ChatColor.RED,
                    ex.getMessage());
            if (player == null || !player.isOnline())
                Bukkit.broadcastMessage(exceptionMsg);
            else player.sendMessage(exceptionMsg);
        } finally {
            chunksShouldNotUnload = false;
        }

        return false;
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {
        if (chunksShouldNotUnload) {
            event.setCancelled(true);
        }
    }
}
