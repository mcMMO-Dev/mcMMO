package com.gmail.nossr50.util.skills;

import com.gmail.nossr50.config.RankConfig;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.datatypes.skills.SuperAbilityType;
import com.gmail.nossr50.datatypes.skills.subskills.AbstractSubSkill;
import com.gmail.nossr50.listeners.InteractionManager;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.runnables.skills.SkillUnlockNotificationTask;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.player.UserManager;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class RankUtils {
    // Unlock levels per subskill indexed by rank - 1, loaded from the rank config once and
    // rebuilt by populateRanks; rank lookups run on region threads on Folia
    private static final Map<SubSkillType, int[]> subSkillUnlockLevels = new ConcurrentHashMap<>();
    private static final Map<String, int[]> abstractSubSkillUnlockLevels =
            new ConcurrentHashMap<>();

    /**
     * @param plugin plugin instance ref
     * @param mmoPlayer target player
     * @param primarySkillType the skill to check
     * @param newLevel the new level of this skill
     */
    public static void executeSkillUnlockNotifications(Plugin plugin, McMMOPlayer mmoPlayer,
            PrimarySkillType primarySkillType, int newLevel) {
        for (SubSkillType subSkillType : mcMMO.p.getSkillTools().getSubSkills(primarySkillType)) {
            //If the skill doesn't have registered ranks gtfo
            if (subSkillType.getNumRanks() == 0) {
                continue;
            }

            int playerRankInSkill = getRank(mmoPlayer.getPlayer(), subSkillType);

            if (playerRankInSkill < 1) {
                continue;
            }

            //Don't send notifications if the player lacks the permission node
            if (!Permissions.isSubSkillEnabled(mmoPlayer.getPlayer(), subSkillType)) {
                continue;
            }

            //The players level is the exact level requirement for this skill
            if (newLevel == getUnlockLevels(subSkillType)[playerRankInSkill - 1]) {
                final long delayTicks = SkillUnlockNotificationPacer.reserveSlotDelayTicks(
                        mmoPlayer.getPlayer().getUniqueId());

                mcMMO.p.getFoliaLib().getScheduler()
                        .runAtEntityLater(mmoPlayer.getPlayer(),
                                new SkillUnlockNotificationTask(mmoPlayer, subSkillType,
                                        newLevel), delayTicks);
            }
        }
    }

    /**
     * Reset the interval between skill unlock notifications
     *
     * @deprecated Unlock notification pacing is handled automatically per player; this does
     * nothing anymore.
     */
    @Deprecated(forRemoval = true, since = "2.3.000")
    public static void resetUnlockDelayTimer() {
    }

    /**
     * Populates the ranks for every skill we know about
     */
    public static void populateRanks() {
        subSkillUnlockLevels.clear();
        for (SubSkillType subSkillType : SubSkillType.values()) {
            if (subSkillType.getNumRanks() > 0) {
                subSkillUnlockLevels.put(subSkillType, loadUnlockLevels(subSkillType));
            }
        }

        abstractSubSkillUnlockLevels.clear();
        for (AbstractSubSkill abstractSubSkill : InteractionManager.getSubSkillList()) {
            if (abstractSubSkill.getNumRanks() > 0) {
                abstractSubSkillUnlockLevels.put(abstractSubSkill.getConfigKeyName(),
                        loadUnlockLevels(abstractSubSkill));
            }
        }
    }

    private static int[] getUnlockLevels(SubSkillType subSkillType) {
        return subSkillUnlockLevels.computeIfAbsent(subSkillType, RankUtils::loadUnlockLevels);
    }

    private static int[] getUnlockLevels(AbstractSubSkill abstractSubSkill) {
        return abstractSubSkillUnlockLevels.computeIfAbsent(abstractSubSkill.getConfigKeyName(),
                key -> loadUnlockLevels(abstractSubSkill));
    }

    private static int[] loadUnlockLevels(SubSkillType subSkillType) {
        final int[] unlockLevels = new int[subSkillType.getNumRanks()];
        for (int rank = 1; rank <= unlockLevels.length; rank++) {
            unlockLevels[rank - 1] = getRankUnlockLevel(subSkillType, rank);
        }
        return unlockLevels;
    }

    private static int[] loadUnlockLevels(AbstractSubSkill abstractSubSkill) {
        final int[] unlockLevels = new int[abstractSubSkill.getNumRanks()];
        for (int rank = 1; rank <= unlockLevels.length; rank++) {
            unlockLevels[rank - 1] = getRankUnlockLevel(abstractSubSkill, rank);
        }
        return unlockLevels;
    }

    /**
     * Returns whether the player has unlocked the first rank in target subskill
     *
     * @param player the player
     * @param subSkillType the target subskill
     * @return true if the player has at least one rank in the skill
     */
    public static boolean hasUnlockedSubskill(Player player, SubSkillType subSkillType) {
        int curRank = getRank(player, subSkillType);

        //-1 means the skill has no unlockable levels and is therefor unlocked
        return curRank == -1 || curRank >= 1;
    }

    /**
     * Returns whether the player has unlocked the first rank in target subskill
     *
     * @param player the player
     * @param abstractSubSkill the target subskill
     * @return true if the player has at least one rank in the skill
     */
    public static boolean hasUnlockedSubskill(Player player, AbstractSubSkill abstractSubSkill) {
        int curRank = getRank(player, abstractSubSkill);

        //-1 means the skill has no unlockable levels and is therefor unlocked
        return curRank == -1 || curRank >= 1;
    }

    /**
     * Returns whether the player has reached the specified rank in target subskill
     *
     * @param rank the target rank
     * @param player the player
     * @param subSkillType the target subskill
     * @return true if the player is at least that rank in this subskill
     */
    public static boolean hasReachedRank(int rank, Player player, SubSkillType subSkillType) {
        return getRank(player, subSkillType) >= rank;
    }

    /**
     * Returns whether the player has reached the specified rank in target subskill
     *
     * @param rank the target rank
     * @param player the player
     * @param abstractSubSkill the target subskill
     * @return true if the player is at least that rank in this subskill
     */
    public static boolean hasReachedRank(int rank, Player player,
            AbstractSubSkill abstractSubSkill) {
        return getRank(player, abstractSubSkill) >= rank;
    }

    /**
     * Gets the current rank of the subskill for the player
     *
     * @param mmoPlayer The player in question
     * @param subSkillType Target subskill
     * @return The rank the player currently has achieved in this skill. -1 for skills without
     * ranks.
     */
    public static int getRank(McMMOPlayer mmoPlayer, SubSkillType subSkillType) {
        return getRank(mmoPlayer.getPlayer(), subSkillType);
    }

    /**
     * Gets the current rank of the subskill for the player
     *
     * @param player The player in question
     * @param subSkillType Target subskill
     * @return The rank the player currently has achieved in this skill. -1 for skills without
     * ranks.
     */
    public static int getRank(Player player, SubSkillType subSkillType) {
        final int numRanks = subSkillType.getNumRanks();

        if (numRanks == 0) {
            return -1; //-1 Means the skill doesn't have ranks
        }

        final McMMOPlayer mmoPlayer = UserManager.getPlayer(player);

        if (mmoPlayer == null) {
            return 0;
        }

        //Skill level of parent skill
        final int currentSkillLevel = mmoPlayer.getSkillLevel(subSkillType.getParentSkill());
        final int[] unlockLevels = getUnlockLevels(subSkillType);

        //Compare against the highest to lowest rank in that order
        for (int rank = numRanks; rank >= 1; rank--) {
            //True if our skill level can unlock the current rank
            if (currentSkillLevel >= unlockLevels[rank - 1]) {
                return rank;
            }
        }

        return 0;
    }

    /**
     * Gets the current rank of the subskill for the player
     *
     * @param player The player in question
     * @param abstractSubSkill Target subskill
     * @return The rank the player currently has achieved in this skill. -1 for skills without
     * ranks.
     */
    public static int getRank(Player player, AbstractSubSkill abstractSubSkill) {
        final int numRanks = abstractSubSkill.getNumRanks();

        if (numRanks == 0) {
            return -1; //-1 Means the skill doesn't have ranks
        }

        final McMMOPlayer mmoPlayer = UserManager.getPlayer(player);

        if (mmoPlayer == null) {
            return 0;
        }

        //Skill level of parent skill
        final int currentSkillLevel = mmoPlayer.getSkillLevel(abstractSubSkill.getPrimarySkill());
        final int[] unlockLevels = getUnlockLevels(abstractSubSkill);

        //Compare against the highest to lowest rank in that order
        for (int rank = numRanks; rank >= 1; rank--) {
            //True if our skill level can unlock the current rank
            if (currentSkillLevel >= unlockLevels[rank - 1]) {
                return rank;
            }
        }

        return 0;
    }

    /**
     * Gets the unlock level for a specific rank in a subskill
     *
     * @param subSkillType The target subskill
     * @param rank The target rank
     * @return The level at which this rank unlocks
     */
    public static int getRankUnlockLevel(SubSkillType subSkillType, int rank) {
        return RankConfig.getInstance().getSubSkillUnlockLevel(subSkillType, rank);
    }

    public static int getRankUnlockLevel(AbstractSubSkill abstractSubSkill, int rank) {
        return RankConfig.getInstance().getSubSkillUnlockLevel(abstractSubSkill, rank);
    }

    /**
     * Get the level at which a skill is unlocked for a player (this is the first rank of a skill)
     *
     * @param subSkillType target subskill
     * @return The unlock requirements for rank 1 in this skill
     */
    public static int getUnlockLevel(SubSkillType subSkillType) {
        return RankConfig.getInstance().getSubSkillUnlockLevel(subSkillType, 1);
    }

    /**
     * Get the level at which a skill is unlocked for a player (this is the first rank of a skill)
     *
     * @param abstractSubSkill target subskill
     * @return The unlock requirements for rank 1 in this skill
     */
    public static int getUnlockLevel(AbstractSubSkill abstractSubSkill) {
        return RankConfig.getInstance().getSubSkillUnlockLevel(abstractSubSkill, 1);
    }

    /**
     * Get the highest rank of a subskill
     *
     * @param subSkillType target subskill
     * @return the last rank of a subskill
     */
    public static int getHighestRank(SubSkillType subSkillType) {
        return subSkillType.getNumRanks();
    }

    public static String getHighestRankStr(SubSkillType subSkillType) {
        return String.valueOf(subSkillType.getNumRanks());
    }

    /**
     * Get the highest rank of a subskill
     *
     * @param abstractSubSkill target subskill
     * @return the last rank of a subskill
     */
    public static int getHighestRank(AbstractSubSkill abstractSubSkill) {
        return abstractSubSkill.getNumRanks();
    }

    public static int getSuperAbilityUnlockRequirement(SuperAbilityType superAbilityType) {
        return getRankUnlockLevel(superAbilityType.getSubSkillTypeDefinition(), 1);
    }

    public static boolean isPlayerMaxRankInSubSkill(Player player, SubSkillType subSkillType) {
        int playerRank = getRank(player, subSkillType);
        int highestRank = getHighestRank(subSkillType);

        return playerRank == highestRank;
    }
}