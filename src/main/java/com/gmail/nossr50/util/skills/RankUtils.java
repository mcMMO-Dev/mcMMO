package com.gmail.nossr50.util.skills;

import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.datatypes.skills.subskills.AbstractSubSkill;
import com.gmail.nossr50.util.player.UserManager;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class RankUtils {
    public static HashMap<String, HashMap<Integer, Integer>> subSkillRanks;

    /* NEW SYSTEM */
    private static void addRanks(AbstractSubSkill abstractSubSkill)
    {
        //Fill out the rank array
        for(int i = 0; i < abstractSubSkill.getNumRanks(); i++)
        {
            //This adds the highest ranks first
            addRank(abstractSubSkill, abstractSubSkill.getNumRanks()-i);

            //TODO: Remove debug code
            /*System.out.println("DEBUG: Adding rank "+(numRanks-i)+" to "+subSkillType.toString());*/
        }
    }

    private static void addRanks(SubSkillType subSkillType)
    {
        //Fill out the rank array
        for(int i = 0; i < subSkillType.getNumRanks(); i++)
        {
            //This adds the highest ranks first
            addRank(subSkillType, subSkillType.getNumRanks()-i);

            //TODO: Remove debug code
            /*System.out.println("DEBUG: Adding rank "+(numRanks-i)+" to "+subSkillType.toString());*/
        }
    }

    /**
     * Gets the current rank of the subskill for the player
     * @param player The player in question
     * @param subSkillType Target subskill
     * @return The rank the player currently has achieved in this skill. -1 for skills without ranks.
     */
    public static int getRank(Player player, SubSkillType subSkillType)
    {
        String skillName = subSkillType.toString();
        int numRanks = subSkillType.getNumRanks();

        if(subSkillRanks == null)
            subSkillRanks = new HashMap<>();

        if(numRanks == 0)
            return -1; //-1 Means the skill doesn't have ranks

        if(subSkillRanks.get(skillName) == null && numRanks > 0)
            addRanks(subSkillType);

        //Get our rank map
        HashMap<Integer, Integer> rankMap = subSkillRanks.get(skillName);

        //Skill level of parent skill
        int currentSkillLevel = UserManager.getPlayer(player).getSkillLevel(subSkillType.getParentSkill());

        for(int i = 0; i < numRanks; i++)
        {
            //Compare against the highest to lowest rank in that order
            int rank = numRanks-i;
            int unlockLevel = getUnlockLevel(subSkillType, rank);

            //If we check all ranks and still cannot unlock the skill, we return rank 0
            if(rank == 0)
                return 0;

            //True if our skill level can unlock the current rank
            if(currentSkillLevel >= unlockLevel)
                return rank;
        }

        return 0; //We should never reach this
    }

    /**
     * Gets the current rank of the subskill for the player
     * @param player The player in question
     * @param abstractSubSkill Target subskill
     * @return The rank the player currently has achieved in this skill. -1 for skills without ranks.
     */
    public static int getRank(Player player, AbstractSubSkill abstractSubSkill)
    {
        String skillName = abstractSubSkill.getConfigKeyName();
        int numRanks = abstractSubSkill.getNumRanks();

        if(subSkillRanks == null)
            subSkillRanks = new HashMap<>();

        if(numRanks == 0)
            return -1; //-1 Means the skill doesn't have ranks

        if(subSkillRanks.get(skillName) == null && numRanks > 0)
            addRanks(abstractSubSkill);

        //Get our rank map
        HashMap<Integer, Integer> rankMap = subSkillRanks.get(skillName);

        //Skill level of parent skill
        int currentSkillLevel = UserManager.getPlayer(player).getSkillLevel(abstractSubSkill.getPrimarySkill());

        for(int i = 0; i < numRanks; i++)
        {
            //Compare against the highest to lowest rank in that order
            int rank = numRanks-i;
            int unlockLevel = getUnlockLevel(abstractSubSkill, rank);

            //If we check all ranks and still cannot unlock the skill, we return rank 0
            if(rank == 0)
                return 0;

            //True if our skill level can unlock the current rank
            if(currentSkillLevel >= unlockLevel)
                return rank;
        }

        return 0; //We should never reach this
    }

    /**
     * Adds ranks to our map
     * @param abstractSubSkill The subskill to add ranks for
     * @param rank The rank to add
     */
    private static void addRank(AbstractSubSkill abstractSubSkill, int rank)
    {
        initMaps(abstractSubSkill.getConfigKeyName());

        HashMap<Integer, Integer> rankMap = subSkillRanks.get(abstractSubSkill.getConfigKeyName());

        //TODO: Remove this debug code
        //System.out.println("[DEBUG]: Rank "+rank+" for "+subSkillName.toString()+" requires skill level "+getUnlockLevel(subSkillType, rank));
        rankMap.put(rank, getUnlockLevel(abstractSubSkill, rank));
    }

    @Deprecated
    private static void addRank(SubSkillType subSkillType, int rank)
    {
        initMaps(subSkillType.toString());

        HashMap<Integer, Integer> rankMap = subSkillRanks.get(subSkillType.toString());

        //TODO: Remove this debug code
        //System.out.println("[DEBUG]: Rank "+rank+" for "+subSkillName.toString()+" requires skill level "+getUnlockLevel(subSkillType, rank));
        rankMap.put(rank, getUnlockLevel(subSkillType, rank));
    }

    private static void initMaps(String s) {
        if (subSkillRanks == null)
            subSkillRanks = new HashMap<>();

        if (subSkillRanks.get(s) == null)
            subSkillRanks.put(s, new HashMap<>());
    }

    /**
     * Gets the unlock level for a specific rank in a subskill
     * @param subSkillType The target subskill
     * @param rank The target rank
     * @return The level at which this rank unlocks
     */
    @Deprecated
    private static int getUnlockLevel(SubSkillType subSkillType, int rank)
    {
        return AdvancedConfig.getInstance().getSubSkillUnlockLevel(subSkillType, rank);
    }

    private static int getUnlockLevel(AbstractSubSkill abstractSubSkill, int rank)
    {
        return AdvancedConfig.getInstance().getSubSkillUnlockLevel(abstractSubSkill, rank);
    }
}
