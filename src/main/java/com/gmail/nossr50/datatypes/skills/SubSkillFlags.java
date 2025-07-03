package com.gmail.nossr50.datatypes.skills;

public class SubSkillFlags {
    /*
     * Bitwise Flags
     * These are so I can flag properties for subskills
     * Flags are in the power of 2 because binary is a base-2 system
     */
    public static final int ACTIVE = 1; //Active subskills are ones that aren't passive
    public static final int SUPERABILITY = 2; // Super abilities are redundantly active
    public static final int RNG = 4; //If the subskill makes use of RNG
    public static final int PVP = 8; //If the subskill has properties that change in PVP conditions
    public static final int TIMED = 16; //If the subskill has a duration or time component
    public static final int TARGET_COLLECTION = 32; //If the subskill has multiple target types
    public static final int REWARD_COLLECTION = 64; //If the subskill has multiple reward types
    public static final int CHARGES = 128;
    public static final int LIMITED = 256;
    //public static final int RANDOM_ACTIVATION           = 128; //If the subskill has random activation
}
