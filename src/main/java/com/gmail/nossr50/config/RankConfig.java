package com.gmail.nossr50.config;

import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.datatypes.skills.subskills.AbstractSubSkill;
import com.gmail.nossr50.mcMMO;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.ArrayList;
import java.util.List;

@ConfigSerializable
public class RankConfig extends ConfigValidated {
    public static final String RETRO_MODE = "RetroMode";
    public static final String STANDARD = "Standard";
    //private static RankConfig instance;

    public RankConfig() {
        //super(McmmoCore.getDataFolderPath().getAbsoluteFile(),"skillranks.yml", true);
        super("skillranks", mcMMO.p.getDataFolder().getAbsoluteFile(), ConfigConstants.RELATIVE_PATH_CONFIG_DIR, true, true, true, true);
        //this.instance = this;
    }

    /**
     * This grabs an instance of this config class from the Config Manager
     * This method is deprecated and will be removed in the future
     * @see mcMMO#getConfigManager()
     * @return the instance of this config
     * @deprecated Please use mcMMO.getConfigManager() to grab a specific config instead
     */
    @Deprecated
    public static RankConfig getInstance() {
        return mcMMO.getConfigManager().getRankConfig();
    }

    @Override
    public void unload() {
        //Do nothing
    }

    /**
     * The version of this config
     *
     * @return
     */
    @Override
    public double getConfigVersion() {
        return 1;
    }

    @Override
    public List<String> validateKeys() {
        List<String> reason = new ArrayList<String>();

        /*
         * In the future this method will check keys for all skills, but for now it only checks overhauled skills
         */
        checkKeys(reason);

        return reason;
    }

    /**
     * Returns the unlock level for a subskill depending on the gamemode
     *
     * @param subSkillType target subskill
     * @param rank         the rank we are checking
     * @return the level requirement for a subskill at this particular rank
     */
    public int getSubSkillUnlockLevel(SubSkillType subSkillType, int rank) {
        return findRankByRootAddress(rank, subSkillType.getRankConfigAddress());
    }

    /**
     * Returns the unlock level for a subskill depending on the gamemode
     *
     * @param abstractSubSkill target subskill
     * @param rank             the rank we are checking
     * @return the level requirement for a subskill at this particular rank
     */
    public int getSubSkillUnlockLevel(AbstractSubSkill abstractSubSkill, int rank) {
        return findRankByRootAddress(rank, abstractSubSkill.getSubSkillType().getRankConfigAddress());
    }

    /**
     * Returns the unlock level for a subskill depending on the gamemode
     *
     * @param key  root address of the subskill in the rankskills.yml file
     * @param rank the rank we are checking
     * @return the level requirement for a subskill at this particular rank
     */
    private int findRankByRootAddress(int rank, String[] key) {
        String scalingKey = mcMMO.isRetroModeEnabled() ? RETRO_MODE : STANDARD;

        String targetRank = "Rank_" + rank;

        //key[0] = parent skill config node, key[1] subskill child node, scalingkey = retro/standard, targetrank = rank node
        return getIntValue(key[0], key[1], scalingKey, targetRank);
    }

    /**
     * Checks for valid keys for subskill ranks
     */
    private void checkKeys(List<String> reasons) {
        //For now we will only check ranks of stuff I've overhauled
        for (SubSkillType subSkillType : SubSkillType.values()) {
            //Keeping track of the rank requirements and making sure there are no logical errors
            int curRank = 0;
            int prevRank = 0;

            for (int x = 0; x < subSkillType.getNumRanks(); x++) {
                if (curRank > 0)
                    prevRank = curRank;

                curRank = getSubSkillUnlockLevel(subSkillType, x);

                if (prevRank > curRank) {
                    //We're going to allow this but we're going to warn them
                    mcMMO.p.getLogger().severe("You have the ranks for the subskill " + subSkillType.toString() + " in skillranks config set up poorly, sequential ranks should have ascending requirements");
                }
            }
        }
    }
}
