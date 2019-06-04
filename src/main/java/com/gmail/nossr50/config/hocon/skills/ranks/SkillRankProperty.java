package com.gmail.nossr50.config.hocon.skills.ranks;

import com.gmail.nossr50.api.exceptions.MissingSkillPropertyDefinition;
import com.gmail.nossr50.datatypes.skills.properties.SkillProperty;

import java.util.HashMap;

public class SkillRankProperty implements SkillProperty {

    private HashMap<Integer, Integer> standardRanks;
    private HashMap<Integer, Integer> retroRanks;

    public SkillRankProperty(Integer... rankDefinitions) {
        initRankMaps();

        for(int x = 0; x < rankDefinitions.length; x++) {
            int curRank = x+1;

            //Avoid negative numbers
            if(rankDefinitions[x] < 0) {
                standardRanks.put(curRank, 0);
            } else {
                standardRanks.put(curRank, rankDefinitions[x]);
            }
        }
    }

    public SkillRankProperty(HashMap<Integer, Integer> standardRanks, HashMap<Integer, Integer> retroRanks) {
        this.standardRanks = standardRanks;
        this.retroRanks = retroRanks;
    }

    /**
     * Convenience method to add Standard and Retro at the same time, shouldn't be used for anything other than the default values since admins may only edit Retro values and not touch Standard ones
     * @param curRank
     * @param rankUnlockLevel
     */
    private void addStandardAndRetroRank(int curRank, int rankUnlockLevel) {
        standardRanks.put(curRank, rankUnlockLevel);
        retroRanks.put(curRank, rankUnlockLevel * 10);
    }

    private void initRankMaps() {
        standardRanks = new HashMap<>();
        retroRanks = new HashMap<>();
    }

    /**
     * Gets the unlock level for this skill as defined by this SkillRankProperty
     * @param retroMode whether or not mcMMO is using RetroMode, true for if it is
     * @param targetRank the rank to get the unlock level for
     * @return the unlock level for target rank
     */
    public int getUnlockLevel(boolean retroMode, int targetRank) throws MissingSkillPropertyDefinition {
        if(retroMode) {
            if(retroRanks.get(targetRank) == null) {
                throw new MissingSkillPropertyDefinition("No definition found for rank:"+targetRank+" using Retro scaling");
            }
            return retroRanks.get(targetRank);
        } else {
            if(standardRanks.get(targetRank) == null) {
                throw new MissingSkillPropertyDefinition("No definition found for rank:"+targetRank+" using Standard scaling");
            }
            return standardRanks.get(targetRank);
        }
    }

    public void setStandardRanks(HashMap<Integer, Integer> standardRanks) {
        this.standardRanks = standardRanks;
    }

    public void setRetroRanks(HashMap<Integer, Integer> retroRanks) {
        this.retroRanks = retroRanks;
    }
}
