package com.gmail.nossr50.datatypes.skills.subskills.interfaces;

public interface RandomChance {
    /**
     * Gets the maximum chance for this interaction to succeed
     * @return maximum chance for this outcome to succeed
     */
    double getRandomChanceMaxChance();

    /**
     * The maximum bonus level for this skill
     * This is when the skills level no longer increases the odds of success
     * For example, setting this to 25 will mean the RandomChance success chance no longer grows after 25
     * @return the maximum bonus from skill level for this skill
     */
    int getRandomChanceMaxBonus();
}
