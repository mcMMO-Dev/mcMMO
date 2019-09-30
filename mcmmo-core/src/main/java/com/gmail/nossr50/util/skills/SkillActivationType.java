package com.gmail.nossr50.util.skills;

/**
 * Defines the type of random calculations to use with a given skill
 */
public enum SkillActivationType {
    //RANDOM_LINEAR_100_SCALE_NO_CAP, //A skill level of 100 would guarantee the proc with this
    RANDOM_LINEAR_100_SCALE_WITH_CAP, //This one is based on a scale of 1-100 but with a specified cap for max bonus
    RANDOM_STATIC_CHANCE, //The skill always has a SPECIFIC chance to succeed
    ALWAYS_FIRES //This skill isn't chance based and always fires
}
