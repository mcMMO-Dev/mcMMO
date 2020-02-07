package com.gmail.nossr50.config.skills.ranks;

import com.gmail.nossr50.api.exceptions.MissingSkillPropertyDefinition;
import com.gmail.nossr50.datatypes.skills.properties.SkillProperty;
import com.gmail.nossr50.mcMMO;

import java.util.HashMap;

public class SkillRankProperty implements SkillProperty {

    private HashMap<Integer, Integer> ranks;

    public SkillRankProperty(Integer... rankDefinitions) {
        initRankMaps();

        for(int x = 0; x < rankDefinitions.length; x++) {
            int curRank = x+1;

            addRank(curRank, rankDefinitions[x]);
        }
    }

    public SkillRankProperty(HashMap<Integer, Integer> ranks) {
        this.ranks = ranks;
    }

    /**
     * Fill in the rank map and mutate it by the cosmetic modifier
     * @param curRank the rank to fill in the value for
     * @param rankValue the value of the rank in Standard
     */
    private void addRank(int curRank, int rankValue) {
        //Avoid negative numbers
        rankValue = Math.max(0, rankValue);

        ranks.put(curRank, rankValue);
    }

    private void initRankMaps() {
        ranks = new HashMap<>();
    }

    /**
     * Gets the unlock level for this skill as defined by this SkillRankProperty
     * @param targetRank the rank to get the unlock level for
     * @return the unlock level for target rank
     */
    public int getUnlockLevel(mcMMO pluginRef, int targetRank) throws MissingSkillPropertyDefinition {
        if(ranks.get(targetRank) == null) {
            throw new MissingSkillPropertyDefinition("No definition found for rank:"+targetRank+" using Standard scaling");
        }

        //Avoid zero or lower
        int cosmeticModifier = Math.max(1, pluginRef.getPlayerLevelingSettings().getCosmeticLevelScaleModifier());

        if(cosmeticModifier == 1)
            return ranks.get(targetRank);

        //Mutate rank
        int rankValue = ranks.get(targetRank);
        rankValue = rankValue / cosmeticModifier;

        return rankValue;
    }

    public void setRanks(HashMap<Integer, Integer> ranks) {
        this.ranks = ranks;
    }

    public HashMap<Integer, Integer> getRanks() {
        return ranks;
    }

}
