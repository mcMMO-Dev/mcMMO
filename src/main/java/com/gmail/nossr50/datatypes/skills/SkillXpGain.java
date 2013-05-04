package com.gmail.nossr50.datatypes.skills;

public class SkillXpGain {
    private final long time;
    private final float xp;

    public SkillXpGain(long time, float xp) {
        this.time = time;
        this.xp = xp;
    }

    public long getTime() {
        return time;
    }

    public float getXp() {
        return xp;
    }
}
