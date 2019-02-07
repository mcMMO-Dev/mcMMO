package com.gmail.nossr50.config;

import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.datatypes.skills.subskills.AbstractSubSkill;

import java.util.ArrayList;
import java.util.List;

public class RankConfig extends AutoUpdateConfigLoader {
    private static RankConfig instance;

    public RankConfig()
    {
        super("skillranks.yml");
        validate();
        this.instance = this;
    }

    @Override
    protected void loadKeys() {

    }

    public static RankConfig getInstance()
    {
        if(instance == null)
            return new RankConfig();

        return instance;
    }

    @Override
    protected boolean validateKeys() {
        List<String> reason = new ArrayList<String>();

        /*
         * In the future this method will check keys for all skills, but for now it only checks overhauled skills
         */
        checkKeys(reason);

        return noErrorsInConfig(reason);
    }

    /**
     * Returns the unlock level for a subskill depending on the gamemode
     * @param subSkillType target subskill
     * @param rank the rank we are checking
     * @return the level requirement for a subskill at this particular rank
     */
    public int getSubSkillUnlockLevel(SubSkillType subSkillType, int rank)
    {
        String key = subSkillType.getRankConfigAddress();

        return findRankByRootAddress(rank, key);
    }

    /**
     * Returns the unlock level for a subskill depending on the gamemode
     * @param abstractSubSkill target subskill
     * @param rank the rank we are checking
     * @return the level requirement for a subskill at this particular rank
     */
    public int getSubSkillUnlockLevel(AbstractSubSkill abstractSubSkill, int rank)
    {
        String key = abstractSubSkill.getPrimaryKeyName()+"."+abstractSubSkill.getConfigKeyName();

        return findRankByRootAddress(rank, key);
    }

    /**
     * Returns the unlock level for a subskill depending on the gamemode
     * @param key root address of the subskill in the rankskills.yml file
     * @param rank the rank we are checking
     * @return the level requirement for a subskill at this particular rank
     */
    private int findRankByRootAddress(int rank, String key) {
        String scalingKey = Config.getInstance().getIsRetroMode() ? ".RetroMode." : ".Standard.";

        String targetRank = "Rank_" + rank;

        key += scalingKey;
        key += targetRank;

        return config.getInt(key);
    }

    /**
     * Checks for valid keys for subskill ranks
     */
    private void checkKeys(List<String> reasons)
    {
        //For now we will only check ranks of stuff I've overhauled
        for(SubSkillType subSkillType : SubSkillType.values())
        {
            //Keeping track of the rank requirements and making sure there are no logical errors
            int curRank = 0;
            int prevRank = 0;

            for(int x = 0; x < subSkillType.getNumRanks(); x++)
            {
                if(curRank > 0)
                    prevRank = curRank;

                curRank = getSubSkillUnlockLevel(subSkillType, x);

                //Do we really care if its below 0? Probably not
                if(curRank < 0)
                {
                    reasons.add(subSkillType.getAdvConfigAddress() + ".Rank_Levels.Rank_"+curRank+".LevelReq should be above or equal to 0!");
                }

                if(prevRank > curRank)
                {
                    //We're going to allow this but we're going to warn them
                    plugin.getLogger().info("You have the ranks for the subskill "+ subSkillType.toString()+" set up poorly, sequential ranks should have ascending requirements");
                }
            }
        }
    }
}
