package org.shufygoth.npcrl.plugin;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class ChunkEraserHandler implements ArgHandler {
    final static Map<Chunk, Map<Block, Material>> chunkBackupMap = new HashMap<>();
    final static Map<BlockState, MaterialData> blockTileEntityMap = new HashMap<>();
    private void forBlockInChunk(Chunk chunk, Consumer<Block> func) {
        forBlockInChunkPred(chunk, func, (block) -> true);
    }
    private void forBlockInChunkPred(Chunk chunk , Consumer<Block> func, Predicate<Block> condition) {
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = 0; y < 255; y++) {
                    final Block block = chunk.getBlock(x, y, z);
                    if (condition.test(block))
                        func.accept(block);
                }
            }
        }
    }
    private boolean restoreChunk(Chunk chunk) {
        final Map<Block, Material> backup = chunkBackupMap.get(chunk);
        if (backup == null) {
            return false;
        }
        forBlockInChunk(chunk, (block) -> block.setType(backup.get(block)));
        return true;
    }
    @Override
    public boolean handle(CommandSender sender, List<String> args) {
        if (!(sender instanceof Player player))
            return false;

        final Chunk chunk = player.getLocation().getChunk();
        if (args.size() > 1) {
            // restore chunk
            final String subArg = args.get(1).toLowerCase();
            switch(subArg) {
                case "restore" -> {
                    if (!restoreChunk(chunk)) {
                        player.sendMessage(String.format("%s%sUhoh..%sThere is no backup of the chunk you're currently in..",
                                ChatColor.DARK_RED,
                                ChatColor.BOLD,
                                ChatColor.RED));
                    } else {
                        chunkBackupMap.remove(chunk); // user is free to nuke again
                        player.sendMessage(String.format("%sRestored", ChatColor.GREEN));
                    }
                    return true;
                }
                case "tiles" -> {
                    if (args.size() > 2) {
                        if (args.get(2).equalsIgnoreCase("restore")) {
                            player.sendMessage(String.format("%sRestoring tile entity data...", ChatColor.GOLD));
                            for (BlockState tileEntity : chunk.getTileEntities()) {
                                tileEntity.setData(blockTileEntityMap.get(tileEntity));
                            }
                            blockTileEntityMap.clear();
                            player.sendMessage(String.format("%sRestored tile entities", ChatColor.GREEN));
                            return true;
                        }
                    }
                    player.sendMessage(String.format("%sBacking up tile entities and destroying...", ChatColor.GOLD));
                    for (BlockState tileEntity : chunk.getTileEntities()) {
                        blockTileEntityMap.put(tileEntity, tileEntity.getData());
                        tileEntity.setType(Material.AIR);
                    }
                    player.sendMessage(String.format("%sDestroyed %s%d%s tile entities",
                            ChatColor.GOLD,
                            ChatColor.RED,
                            blockTileEntityMap.size(),
                            ChatColor.GOLD));
                    return true;
                }
            }
        }

        // nuke chunk
        player.sendMessage(String.format("%sBacking up blocks in chunk %s%d %d%s and removing...",
                ChatColor.GOLD,
                ChatColor.LIGHT_PURPLE,
                chunk.getX(),
                chunk.getZ(),
                ChatColor.GOLD));

        final Map<Block, Material> blockHistory = new HashMap<>(255*16*16);
        forBlockInChunk(chunk, (block) -> {
            blockHistory.put(block, block.getType());
            block.setType(Material.AIR); // NUKE
        });

        if (chunkBackupMap.containsKey(chunk)) {
            player.sendMessage(String.format("%sThere's already a pending chunk backup for the current chunk. Restore before nuking again", ChatColor.RED));
            return false;
        }
        chunkBackupMap.put(chunk, blockHistory);

        player.sendMessage(String.format("%sNuked %s%d%s blocks successfully", ChatColor.GOLD, ChatColor.RED, blockHistory.size(), ChatColor.GOLD));

        return false;
    }
}
