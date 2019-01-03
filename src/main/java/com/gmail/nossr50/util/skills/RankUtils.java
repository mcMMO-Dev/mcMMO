package com.gmail.nossr50.util.skills;

import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.datatypes.skills.SubSkill;
import com.gmail.nossr50.util.player.UserManager;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class RankUtils {
    public static HashMap<SubSkill, HashMap<Integer, Integer>> subSkillRanks;

    /**
     * Adds ranks to subSkillRanks for target SubSkill
     * @param subSkill Target SubSkill
     */
    private static void addRanks(SubSkill subSkill) {
        int numRanks = subSkill.getNumRanks();

        //Fill out the rank array
        for(int i = 0; i < numRanks; i++)
        {
            //This adds the highest ranks first
            addRank(subSkill, numRanks-i);

            //TODO: Remove debug code
            /*System.out.println("DEBUG: Adding rank "+(numRanks-i)+" to "+subSkill.toString());*/
        }
    }

    /**
     * Gets the current rank of the subskill for the player
     * @param player The player in question
     * @param subSkill Target subskill
     * @return The rank the player currently has achieved in this skill. -1 for skills without ranks.
     */
    public static int getRank(Player player, SubSkill subSkill)
    {
        if(subSkillRanks == null)
            subSkillRanks = new HashMap<>();

        if(subSkill.getNumRanks() == 0)
            return -1; //-1 Means the skill doesn't have ranks

        if(subSkillRanks.get(subSkill) == null && subSkill.getNumRanks() > 0)
            addRanks(subSkill);

        //Get our rank map
        HashMap<Integer, Integer> rankMap = subSkillRanks.get(subSkill);

        //Skill level of parent skill
        int currentSkillLevel = UserManager.getPlayer(player).getSkillLevel(subSkill.getParentSkill());

        for(int i = 0; i < subSkill.getNumRanks(); i++)
        {
            //Compare against the highest to lowest rank in that order
            int rank = subSkill.getNumRanks()-i;
            int unlockLevel = getUnlockLevel(subSkill, rank);

            //TODO: Remove this debug code
            /*System.out.println("[DEBUG RANKCHECK] Checking rank "+rank+" of "+subSkill.getNumRanks());
            System.out.println("[DEBUG RANKCHECK] Rank "+rank+" -- Unlock level: "+unlockLevel);
            System.out.println("[DEBUG RANKCHECK] Rank" +rank+" -- Player Skill Level: "+currentSkillLevel);*/

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
     * @param subSkill The subskill to add ranks for
     * @param rank The rank to add
     */
    private static void addRank(SubSkill subSkill, int rank)
    {
        if(subSkillRanks == null)
            subSkillRanks = new HashMap<>();

        if(subSkillRanks.get(subSkill) == null)
            subSkillRanks.put(subSkill, new HashMap<>());

        HashMap<Integer, Integer> rankMap = subSkillRanks.get(subSkill);

        System.out.println("[DEBUG]: Rank "+rank+" for "+subSkill.toString()+" requires skill level "+getUnlockLevel(subSkill, rank));
        rankMap.put(rank, getUnlockLevel(subSkill, rank));
    }

    /**
     * Gets the unlock level for a specific rank in a subskill
     * @param subSkill The target subskill
     * @param rank The target rank
     * @return The level at which this rank unlocks
     */
    private static int getUnlockLevel(SubSkill subSkill, int rank)
    {
        return AdvancedConfig.getInstance().getSubSkillUnlockLevel(subSkill, rank);
    }
}
