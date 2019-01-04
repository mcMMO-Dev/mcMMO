package com.gmail.nossr50.datatypes.skills;

public class SubSkillFlags {
    /*
     * Bitwise Flags
     * These are so I can establish properties of each subskill quite easily
     */
    public static final byte ACTIVE = 0x01; //Active subskills are ones that aren't passive
    public static final byte SUPERABILITY = 0x02; //If the subskill is a super ability
    public static final byte RNG = 0x04; //If the subskill makes use of RNG
    public static final byte PVP = 0x08; //If the subskill is PVP specific
}
