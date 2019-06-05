package com.gmail.nossr50.datatypes.experience;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.mcMMO;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public class SkillXpGain implements Delayed {
    private final long expiryTime;
    private final double xp;
    private final PrimarySkillType type;

    public SkillXpGain(PrimarySkillType type, double xp) {
        this.expiryTime = System.currentTimeMillis() + getDuration();
        this.xp = xp;
        this.type = type;
    }

    private static long getDuration() {
        return TimeUnit.MINUTES.toMillis(mcMMO.getConfigManager().getConfigLeveling().getDimishedReturnTimeInterval());
    }

    public PrimarySkillType getSkill() {
        return type;
    }

    public double getXp() {
        return xp;
    }

    public int compareTo(SkillXpGain other) {
        if (this.expiryTime < other.expiryTime) {
            return -1;
        } else if (this.expiryTime > other.expiryTime) {
            return 1;
        }
        return 0;
    }

    @Override
    public int compareTo(Delayed other) {
        if (other instanceof SkillXpGain) {
            // Use more efficient method if possible (private fields)
            return this.compareTo((SkillXpGain) other);
        }
        return (int) (getDelay(TimeUnit.MILLISECONDS) - other.getDelay(TimeUnit.MILLISECONDS));
    }

    @Override
    public long getDelay(TimeUnit arg0) {
        return arg0.convert(expiryTime - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
    }
}
