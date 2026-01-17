package org.shufygoth.npcrl.plugin;

import com.google.common.base.Functions;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_8_R3.GenericAttributes;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import org.shufygoth.npcrl.npc.BasicNpc;
import org.shufygoth.npcrl.npc.Npc;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class InGameUnitTestHandler implements ArgHandler {
    private void runTest(final String name, final Runnable setup, final Test<Boolean> test, final Runnable teardown) {
        setup.run();

        boolean passed = test.run();
        Bukkit.broadcastMessage(String.format("%s[%sUnit Test%s] %s -> %s%s%s%s",
                ChatColor.WHITE,
                ChatColor.LIGHT_PURPLE,
                ChatColor.RESET,
                name,
                passed ? ChatColor.GREEN : ChatColor.RED,
                ChatColor.BOLD,
                String.format("%s",passed).toUpperCase(),
                ChatColor.RESET
        ));

        teardown.run();
    }
    private void runTests(CommandSender sender, Location location) {
        if (sender == null)
            return;
        if (location == null)
            return;

        List<Block> testBlocks = new ArrayList<>(100);
        AtomicReference<Npc> npc = new AtomicReference<>(null);
        Runnable createNpc = () -> npc.set(new BasicNpc(location, "tester123"));
        Runnable destroyNpc = () -> { npc.get().destroy(); npc.set(null); };
        Runnable createWalkwayForNpc = () -> {
            for (int i = 0; i < 100; i++) {
                Block walkwayBlock = location.getBlock().getRelative(BlockFace.DOWN).getRelative(BlockFace.SOUTH, i);
                walkwayBlock.setType(Material.DIAMOND_BLOCK);
                testBlocks.add(walkwayBlock);
            }
        };
        Runnable destroyWalkwayForNpc = () -> testBlocks.forEach(block -> block.setType(Material.AIR));

        runTest("npcSprintingMovementSpeed",
                createNpc,
                () -> {
                    if (npc.get().isSprinting()) return false;
                    double baseSpeed = npc.get().entity().getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).getValue();
                    npc.get().startSprinting();
                    if (npc.get().entity().getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).getValue() <= baseSpeed) return false;
                    npc.get().stopSprinting();
                    if (npc.get().entity().getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).getValue() != baseSpeed) return false;
                    return true;
                },
                destroyNpc
        );

        runTest("npcSprintingPracticalSpeed",
                createNpc,
                () -> {
                    createWalkwayForNpc.run();

                    final int ACTION_COUNT = 2;
                    Runnable[] actions = new Runnable[] { () -> npc.get().stopSprinting(), () -> npc.get().startSprinting() };
                    double[] results = new double[ACTION_COUNT];

                    for (int i = 0; i < results.length; i++) {
                        Vector start = npc.get().entity().getBukkitEntity().getLocation().toVector();
                        actions[i].run();

                        for (int step = 0; step < 50; step++)
                            npc.get().travel(1, 0);

                        Vector end = npc.get().entity().getBukkitEntity().getLocation().toVector();
                        results[i] = end.subtract(start).length();
                    }

                    destroyWalkwayForNpc.run();
                    return results[1] > results[0]; // sprinting went further than not sprinting
                },
                destroyNpc
        );

        runTest("npcJump",
                createNpc,
                () -> {
                    createWalkwayForNpc.run();

                    double startNpcY = npc.get().entity().locY;
                    npc.get().jump();
                    double endNpcY = npc.get().entity().locY;

                    destroyWalkwayForNpc.run();
                    return endNpcY > startNpcY;
                },
                destroyNpc
        );
    }

    @Override
    public boolean handle(CommandSender sender, List<String> args) {
        /*  /command --> unit <--  */
        String root = args.get(0);

        /*  /command unit --> [arg...] <--   */
        List<String> rootArgs = args.subList(1, args.size());

        Location testLocation;
        if (sender instanceof Player player) testLocation = player.getLocation();
        else testLocation = new Location(Bukkit.getWorlds().get(0), 0, 150, 0);

        runTests(sender, testLocation);

        return false;
    }
}
