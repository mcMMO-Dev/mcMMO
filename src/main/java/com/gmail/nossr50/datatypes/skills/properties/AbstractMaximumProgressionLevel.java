package com.gmail.nossr50.datatypes.skills.properties;

import com.gmail.nossr50.datatypes.skills.SubSkillType;

public class AbstractMaximumProgressionLevel implements MaximumProgressionLevel {

    private SubSkillType subSkillType;

    private int standardMaxLevel;
    private int retroMaxLevel;

    public AbstractMaximumProgressionLevel(SubSkillType subSkillType, int standardMaxLevel, int retroMaxLevel) {
        this.subSkillType = subSkillType;
        this.standardMaxLevel = standardMaxLevel;
        this.retroMaxLevel = retroMaxLevel;
    }

    @Override
    public SubSkillType getSubSkillType() {
        return subSkillType;
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
