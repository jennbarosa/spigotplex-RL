package org.shufygoth.npcrl.environment.point;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.shufygoth.npcrl.environment.EnvironmentBox;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public class GetThePointArena implements EnvironmentBox {
    private final Location center;
    private final Material floorType;
    private final List<Block> blocks;
    private final int floorRadius;
    private final int wallHeight;

    public GetThePointArena(int floorRadius, int wallHeight, Material floorType) {
        this.floorRadius = floorRadius;
        this.wallHeight = wallHeight;
        this.floorType = floorType;

        this.center = new Location(Bukkit.getWorlds().get(0), 0, 150, 0);
        this.blocks = new ArrayList<>((wallHeight*floorRadius*4)+(floorRadius*floorRadius));

        build();
    }
    private void buildFloor(int radius) {
        if (this.center == null) return;
        Block centerBlock = this.center.getBlock();
        this.blocks.add(centerBlock);
        for (int x = -radius; x < radius; x++) {
            for (int z = -radius; z < radius; z++) {
                Block relative = centerBlock.getRelative(x, 0, z);
                relative.setType(this.floorType);
                this.blocks.add(relative);
            }
        }
    }
    private void buildWalls(int height) {
        // todo
    }

    @Override
    public Block getCenter() {
        return center.getBlock();
    }

    public List<Block> getBlocks() {
        return blocks;
    }

    @Override
    public void build() {
        this.buildFloor(floorRadius);
        this.buildWalls(wallHeight);
    }

    @Override
    public void destroy() {
        this.blocks.forEach(block -> block.setType(Material.AIR));
    }

    @Override
    public Map<String, ?> getInfo() {
        return Map.ofEntries(
                Map.entry("floorRadius()", (Supplier<Integer>) () -> floorRadius),
                Map.entry("wallHeight()", (Supplier<Integer>) () -> wallHeight),
                Map.entry("floorType()",   (Supplier<Material>) () -> floorType)
        );
    }
}
