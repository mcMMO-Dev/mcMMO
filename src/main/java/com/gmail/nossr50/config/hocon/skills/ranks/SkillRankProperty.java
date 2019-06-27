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

            addStandardAndRetroRank(curRank, rankDefinitions[x]);
        }
    }

    public SkillRankProperty(HashMap<Integer, Integer> standardRanks, HashMap<Integer, Integer> retroRanks) {
        this.standardRanks = standardRanks;
        this.retroRanks = retroRanks;
    }

    /**
     * Convenience method to add Standard and Retro at the same time for default initialization of values
     * Only requires standard values be passed
     * @param curRank the rank to fill in the value for
     * @param standardValue the value of the rank in Standard
     */
    private void addStandardAndRetroRank(int curRank, int standardValue) {
        //Retro will be equal to standards rank requirement multiplied by 10 unless that value is 1, in which case it will also be 1
        int retroValue = standardValue == 1 ? 1 : standardValue * 10;

        //Avoid negative numbers
        if(standardValue < 0) {
            standardRanks.put(curRank, 0);
            retroRanks.put(curRank, 0);
        } else {
            standardRanks.put(curRank, standardValue);
            retroRanks.put(curRank, retroValue);
        }
    }

    /**
     * Convenience method to add Standard and Retro at the same time
     * @param curRank the rank to fill in the value for
     * @param standardValue the value of the rank in Standard
     * @param retroValue the value of the rank in Retro
     */
    private void addStandardAndRetroRank(int curRank, int standardValue, int retroValue) {
        //Avoid negative numbers
        standardValue = Math.max(0, standardValue);
        retroValue = Math.max(0, retroValue);

        standardRanks.put(curRank, standardValue);
        retroRanks.put(curRank, retroValue);
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

    public HashMap<Integer, Integer> getStandardRanks() {
        return standardRanks;
    }

    public HashMap<Integer, Integer> getRetroRanks() {
        return retroRanks;
    }
}
