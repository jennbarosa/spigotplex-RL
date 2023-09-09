package org.shufygoth.npcrl.environment.point;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.shufygoth.npcrl.NpcRL;

final class GetThePointArenaLandmark {
    private final Block block;
    private final Material landmarkType;
    private final Material originalType;
    public GetThePointArenaLandmark(Block block, Material landmarkType) {
        this.block = block;
        this.originalType = this.block.getType();
        this.landmarkType = landmarkType;

        show();
    }

    public void hide() {
        if (this.block == null) return;
        // make sure we run bukkit api calls as synchronous, in case this is called from our web server
        Bukkit.getScheduler().runTask(NpcRL.plugin, () -> this.block.setType(originalType));
    }

    public void show() {
        if (this.block == null) return;
        // make sure we run bukkit api calls as synchronous, in case this is called from our web server
        Bukkit.getScheduler().runTask(NpcRL.plugin, () -> this.block.setType(landmarkType));
    }

    public Block getBlock() {
        return block;
    }
}
