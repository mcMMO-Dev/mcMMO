package com.gmail.nossr50.util;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.runnables.player.PlayerProfileLoadingTask;
import com.gmail.nossr50.util.player.UserManager;
import com.google.common.collect.ImmutableSet;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Random;
import java.util.Set;

public final class Misc {
    private static final @NotNull Random random = new Random();

    public static final int TIME_CONVERSION_FACTOR = 1000;
    public static final int TICK_CONVERSION_FACTOR = 20;

    public static final int PLAYER_RESPAWN_COOLDOWN_SECONDS = 5;
    public static final double SKILL_MESSAGE_MAX_SENDING_DISTANCE = 10.0;

    // Sound Pitches & Volumes from CB
/*    public static final float ANVIL_USE_PITCH  = 0.3F;  // Not in CB directly, I went off the place sound values
    public static final float ANVIL_USE_VOLUME = 1.0F * Config.getInstance().getMasterVolume();  // Not in CB directly, I went off the place sound values
    public static final float FIZZ_VOLUME      = 0.5F * Config.getInstance().getMasterVolume();
    public static final float POP_VOLUME       = 0.2F * Config.getInstance().getMasterVolume();
    public static final float BAT_VOLUME       = 1.0F * Config.getInstance().getMasterVolume();
    public static final float BAT_PITCH        = 0.6F;
    public static final float GHAST_VOLUME     = 1.0F * Config.getInstance().getMasterVolume();
    public static final float LEVELUP_PITCH    = 0.5F;  // Reduced to differentiate between vanilla level-up
    public static final float LEVELUP_VOLUME   = 0.75F * Config.getInstance().getMasterVolume(); // Use max volume always*/

    public static final @NotNull Set<String> modNames = ImmutableSet.of("LOTR", "BUILDCRAFT", "ENDERIO",
            "ENHANCEDBIOMES", "IC2", "METALLURGY", "FORESTRY", "GALACTICRAFT", "RAILCRAFT", "TWILIGHTFOREST",
            "THAUMCRAFT", "GRAVESTONEMOD", "GROWTHCRAFT", "ARCTICMOBS", "DEMONMOBS", "INFERNOMOBS", "SWAMPMOBS",
            "MARICULTURE", "MINESTRAPPOLATION");

    private Misc() {}

    /**
     * Determines if an entity is an NPC but not a villager
     * This method aims to establish compatibility between mcMMO and other plugins which create "NPCs"
     *
     * It does this by checking the following
     * 1) The entity is not a Villager
     * 2) The entity can be considered an NPC
     *
     * In this context, an NPC is a bit hard to define. Various plugins determine what an NPC is in different ways.
     * @see Misc::isNPCIncludingVillagers
     * @param entity target entity
     * @return true if the entity is not a Villager and is not a "NPC"
     */
    public static boolean isNPCEntityExcludingVillagers(@NotNull Entity entity) {
        return (!isVillager(entity)
                && isNPCIncludingVillagers(entity)); //Compatibility with some mod..
    }

    public static boolean isNPCClassType(Entity entity) {
        return entity instanceof NPC;
    }

    public static boolean hasNPCMetadataTag(Entity entity) {
        return entity.hasMetadata("NPC");
    }

    public static boolean isVillager(Entity entity) {
        String entityType = entity.getType().toString();
        //This weird code is for 1.13 & 1.14 compatibility
        return entityType.equalsIgnoreCase("wandering_trader") || entity instanceof Villager;
    }

    public static boolean isNPCIncludingVillagers(@Nullable Entity entity) {
        return (entity == null
                || (hasNPCMetadataTag(entity))
                || (isNPCClassType(entity))
                || entity.getClass().getName().equalsIgnoreCase("cofh.entity.PlayerFake"));
    }

    /**
     * Determine if two locations are near each other.
     *
     * @param first The first location
     * @param second The second location
     * @param maxDistance The max distance apart
     * @return true if the distance between {@code first} and {@code second} is less than {@code maxDistance}, false otherwise
     */
    public static boolean isNear(@NotNull Location first, @NotNull Location second, double maxDistance) {
        return (first.getWorld() == second.getWorld()) && (first.distanceSquared(second) < (maxDistance * maxDistance) || maxDistance == 0);
    }

    /**
     * Get the center of the given block.
     * 
     * @param blockState The {@link BlockState} of the block
     * @return A {@link Location} lying at the center of the block
     */
    public static Location getBlockCenter(BlockState blockState) {
        return getBlockCenter(blockState.getLocation());
    }

    public static Location getBlockCenter(Block block) {
        return getBlockCenter(block.getLocation());
    }

    public static Location getBlockCenter(Location location) {
        return location.clone().add(0.5, 0.5, 0.5);
    }

    public static void profileCleanup(@NotNull String playerName) {
        Player player = mcMMO.p.getServer().getPlayerExact(playerName);

        if (player != null) {
            UserManager.remove(player);
            mcMMO.p.getFoliaLib().getScheduler().runLaterAsync(new PlayerProfileLoadingTask(player), 1); // 1 Tick delay to ensure the player is marked as online before we begin loading
        }
    }

    public static void printProgress(int convertedUsers, int progressInterval, long startMillis) {
        if ((convertedUsers % progressInterval) == 0) {
            mcMMO.p.getLogger().info(String.format("Conversion progress: %d users at %.2f users/second",
                    convertedUsers,
                    convertedUsers / (double) ((System.currentTimeMillis() - startMillis) / TIME_CONVERSION_FACTOR)));
        }
    }

    public static String getModName(@NotNull String materialName) {
        for (String mod : modNames) {
            if (materialName.contains(mod)) {
                return mod;
            }
        }

        String[] materialSplit = materialName.split("_");

        if (materialSplit.length > 1) {
            return materialSplit[0].toLowerCase(Locale.ENGLISH);
        }

        return "UnknownMods";
    }

    /**
     * Gets a random location near the specified location
     */
    public static Location getLocationOffset(@NotNull Location location, double strength) {
        double blockX = location.getBlockX();
        double blockZ = location.getBlockZ();

        double distance;
        distance = strength * random.nextDouble();
        blockX = (random.nextBoolean()) ? blockX + (distance) : blockX - (distance);

        distance = strength * random.nextDouble();
        blockZ = (random.nextBoolean()) ? blockZ + (distance) : blockZ - (distance);

        return new Location(location.getWorld(), blockX, location.getY(), blockZ);
    }

    public static @NotNull Random getRandom() {
        return random;
    }

    /**
     * Whether a player is the party leader of a party
     *
     * @param mmoPlayer target player
     * @return true if the player is the party leader
     */
    public static boolean isPartyLeader(@NotNull McMMOPlayer mmoPlayer) {
        return mmoPlayer.getParty().getLeader().getUniqueId().equals(mmoPlayer.getPlayer().getUniqueId());
    }

//    public static void spawnExperienceOrb(@NotNull Location location, int orbAmount, int experienceValue) {
//        for (int i = 0; i < orbAmount; i++) {
//            new SpawnOrbTask(location, experienceValue).runTaskLater(mcMMO.p, 20);
//        }
//    }

    public static void spawnExperienceOrb(@NotNull Location location, int experienceValue) {
        if (location.getWorld() == null)
            return;

        ExperienceOrb experienceOrb = (ExperienceOrb) location.getWorld().spawnEntity(location, EntityType.EXPERIENCE_ORB);
        experienceOrb.setExperience(experienceValue);
    }

    private static class SpawnOrbTask implements Runnable {
        private final Location location;
        private int orbExpValue;

        private SpawnOrbTask(Location location, int orbExpValue) {
            this.location = location;
            this.orbExpValue = orbExpValue;
        }

        @Override
        public void run() {
            if (location == null || location.getWorld() == null)
                return;

            ExperienceOrb experienceOrb = (ExperienceOrb) location.getWorld().spawnEntity(location, EntityType.EXPERIENCE_ORB);
            experienceOrb.setExperience(orbExpValue);
        }
    }

//    public static void hackyUnitTest(@NotNull McMMOPlayer normalPlayer) {
//        mcMMO.p.getLogger().info("Starting hacky unit test...");
//        int iterations = 1000000;
//        double ratioDivisor = 10000; //10000 because we run the test 1,000,000 times
//        double expectedFailRate = 100.0D - RandomChanceUtil.getRandomChanceExecutionSuccess(normalPlayer.getPlayer(), SubSkillType.MINING_MOTHER_LODE, true);
//
//        double win = 0, loss = 0;
//        for(int x = 0; x < iterations; x++) {
//            if (RandomChanceUtil.checkRandomChanceExecutionSuccess(normalPlayer.getPlayer(), SubSkillType.MINING_MOTHER_LODE, true)) {
//                win++;
//            } else {
//                loss++;
//            }
//        }
//
//        double lossRatio = (loss / ratioDivisor);
//        mcMMO.p.getLogger().info("Expected Fail Rate: "+expectedFailRate);
//        mcMMO.p.getLogger().info("Loss Ratio for hacky test: "+lossRatio);
////        Assert.assertEquals(lossRatio, expectedFailRate, 0.01D);
//    }

}
