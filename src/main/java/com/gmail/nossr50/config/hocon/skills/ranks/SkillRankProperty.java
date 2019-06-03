package com.gmail.nossr50.config.hocon.skills.ranks;

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

    public void setStandardRanks(HashMap<Integer, Integer> standardRanks) {
        this.standardRanks = standardRanks;
    }

    public void setRetroRanks(HashMap<Integer, Integer> retroRanks) {
        this.retroRanks = retroRanks;
    }
}
