package com.gmail.nossr50.datatypes.skills.properties;

public interface MaxBonusLevel {

    /**
     * Get the max level for this skill for Retro scaling
     * @return Retro Mode max bonus level
     */
    int getRetroScaleValue();

    /**
     * Get the max level for this skill for Standard scaling
     * @return Standard Mode max bonus level
     */
    int getStandardScaleValue();

}
