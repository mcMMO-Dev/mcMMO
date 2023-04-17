package com.gmail.nossr50.config;

import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.datatypes.skills.subskills.AbstractSubSkill;
import com.gmail.nossr50.mcMMO;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class RankConfig extends BukkitConfig {
    private static RankConfig instance;

    public RankConfig() {
        super("skillranks.yml");
        validate();
        instance = this;
    }

    public static RankConfig getInstance() {
        if (instance == null)
            return new RankConfig();

        return instance;
    }

    @Override
    protected void loadKeys() {

    }

    @Override
    protected boolean validateKeys() {
        List<String> reason = new ArrayList<>();

        /*
         * In the future this method will check keys for all skills, but for now it only checks overhauled skills
         */
        checkKeys(reason);

        return noErrorsInConfig(reason);
    }

    /**
     * Returns the unlock level for a subskill depending on the gamemode
     *
     * @param subSkillType target subskill
     * @param rank         the rank we are checking
     *
     * @return the level requirement for a subskill at this particular rank
     */
    public int getSubSkillUnlockLevel(SubSkillType subSkillType, int rank) {
        String key = subSkillType.getRankConfigAddress();

        return findRankByRootAddress(rank, key);
    }

    /**
     * Returns the unlock level for a subskill depending on the gamemode
     *
     * @param subSkillType target subskill
     * @param rank         the rank we are checking
     *
     * @return the level requirement for a subskill at this particular rank
     */
    public int getSubSkillUnlockLevel(SubSkillType subSkillType, int rank, boolean retroMode) {
        String key = getRankAddressKey(subSkillType, rank, retroMode);
        return config.getInt(key, defaultYamlConfig.getInt(key));
    }

    /**
     * Returns the unlock level for a subskill depending on the gamemode
     *
     * @param abstractSubSkill target subskill
     * @param rank             the rank we are checking
     *
     * @return the level requirement for a subskill at this particular rank
     */
    public int getSubSkillUnlockLevel(AbstractSubSkill abstractSubSkill, int rank) {
        String key = abstractSubSkill.getPrimaryKeyName() + "." + abstractSubSkill.getConfigKeyName();

        return findRankByRootAddress(rank, key);
    }

    /**
     * Returns the unlock level for a subskill depending on the gamemode
     *
     * @param key  root address of the subskill in the rankskills.yml file
     * @param rank the rank we are checking
     *
     * @return the level requirement for a subskill at this particular rank
     */
    private int findRankByRootAddress(int rank, String key) {
        String scalingKey = mcMMO.p.getGeneralConfig().getIsRetroMode() ? ".RetroMode." : ".Standard.";

        String targetRank = "Rank_" + rank;

        key += scalingKey;
        key += targetRank;

        return config.getInt(key);
    }

    public String getRankAddressKey(SubSkillType subSkillType, int rank, boolean retroMode) {
        String key = subSkillType.getRankConfigAddress();
        String scalingKey = retroMode ? ".RetroMode." : ".Standard.";

        String targetRank = "Rank_" + rank;

        key += scalingKey;
        key += targetRank;

        return key;
    }

    public String getRankAddressKey(AbstractSubSkill subSkillType, int rank, boolean retroMode) {
        String key = subSkillType.getPrimaryKeyName() + "." + subSkillType.getConfigKeyName();
        String scalingKey = retroMode ? ".RetroMode." : ".Standard.";

        String targetRank = "Rank_" + rank;

        key += scalingKey;
        key += targetRank;

        return key;
    }

    private void resetRankValue(@NotNull SubSkillType subSkillType, int rank, boolean retroMode) {
        String key = getRankAddressKey(subSkillType, rank, retroMode);
        int defaultValue = defaultYamlConfig.getInt(key);
        config.set(key, defaultValue);
        mcMMO.p.getLogger().info(key + " SET -> " + defaultValue);
    }

    /**
     * Checks for valid keys for subskill ranks
     */
    private void checkKeys(@NotNull List<String> reasons) {
        HashSet<SubSkillType> badSkillSetup = new HashSet<>();

        //For now we will only check ranks of stuff I've overhauled
        checkConfig(reasons, badSkillSetup, true);
        checkConfig(reasons, badSkillSetup, false);

        //Fix bad entries
        if (badSkillSetup.isEmpty())
            return;

        mcMMO.p.getLogger().info("(FIXING CONFIG) mcMMO is correcting a few mistakes found in your skill rank config setup");

        for (SubSkillType subSkillType : badSkillSetup) {
            mcMMO.p.getLogger().info("(FIXING CONFIG) Resetting rank config settings for skill named - " + subSkillType.toString());
            fixBadEntries(subSkillType);
        }
    }

    private void checkConfig(@NotNull List<String> reasons, @NotNull HashSet<SubSkillType> badSkillSetup, boolean retroMode) {
        for (SubSkillType subSkillType : SubSkillType.values()) {
            //Keeping track of the rank requirements and making sure there are no logical errors
            int curRank = 0;
            int prevRank = 0;

            for (int x = 0; x < subSkillType.getNumRanks(); x++) {
                int index = x + 1;

                if (curRank > 0)
                    prevRank = curRank;

                curRank = getSubSkillUnlockLevel(subSkillType, index, retroMode);

                //Do we really care if its below 0? Probably not
                if (curRank < 0) {
                    reasons.add("(CONFIG ISSUE) " + subSkillType + " should not have any ranks that require a negative level!");
                    badSkillSetup.add(subSkillType);
                    continue;
                }

                if (prevRank > curRank) {
                    //We're going to allow this but we're going to warn them
                    mcMMO.p.getLogger().info("(CONFIG ISSUE) You have the ranks for the subskill " + subSkillType + " set up poorly, sequential ranks should have ascending requirements");
                    badSkillSetup.add(subSkillType);
                }
            }
        }
    }

    private void fixBadEntries(@NotNull SubSkillType subSkillType) {
        for (int x = 0; x < subSkillType.getNumRanks(); x++) {
            int index = x + 1;

            //Reset Retromode entries
            resetRankValue(subSkillType, index, true);
            //Reset Standard Entries
            resetRankValue(subSkillType, index, false);
        }

        updateFile();
    }
}
