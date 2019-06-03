package com.gmail.nossr50.datatypes.skills.properties;

public class AbstractSkillCeiling implements SkillCeiling {

    private int standardMaxLevel;
    private int retroMaxLevel;

    public AbstractSkillCeiling(int standardMaxLevel, int retroMaxLevel) {
        this.standardMaxLevel = standardMaxLevel;
        this.retroMaxLevel = retroMaxLevel;
    }

    @Override
    public int getRetroMaxLevel() {
        return retroMaxLevel;
    }

    @Override
    public int getStandardMaxLevel() {
        return standardMaxLevel;
    }

}
