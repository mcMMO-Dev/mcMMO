package com.gmail.nossr50.datatypes.skills.subskills.acrobatics;

import org.bukkit.event.entity.EntityDamageEvent;

import static java.util.Objects.requireNonNull;

/**
 * Immutable class representing the result of a roll action in acrobatics.
 */
public class RollResult {
    private final boolean rollSuccess;
    private final boolean isGraceful;
    private final double eventDamage;
    private final double modifiedDamage;
    private final boolean isFatal;
    private final boolean isExploiting;
    private final float xpGain;

    private RollResult(Builder builder) {
        this.rollSuccess = builder.rollSuccess;
        this.isGraceful = builder.isGraceful;
        this.eventDamage = builder.eventDamage;
        this.modifiedDamage = builder.modifiedDamage;
        this.isFatal = builder.isFatal;
        this.isExploiting = builder.isExploiting;
        this.xpGain = builder.xpGain;
    }

    public boolean isRollSuccess() {
        return rollSuccess;
    }

    public boolean isGraceful() {
        return isGraceful;
    }

    public double getEventDamage() {
        return eventDamage;
    }

    public double getModifiedDamage() {
        return modifiedDamage;
    }

    public boolean isFatal() {
        return isFatal;
    }

    public boolean isExploiting() {
        return isExploiting;
    }

    public float getXpGain() {
        return xpGain;
    }

    /**
     * Builder class for constructing {@code RollResult} instances.
     */
    public static class Builder {
        private final boolean isGraceful;
        private final double eventDamage;
        private double modifiedDamage;
        private boolean isFatal;
        private boolean rollSuccess;
        private boolean isExploiting;
        private float xpGain;

        /**
         * Constructs a new {@code Builder} with required parameters.
         *
         * @param entityDamageEvent the damage event, must not be null
         * @param isGracefulRoll    whether the roll is graceful
         */
        public Builder(EntityDamageEvent entityDamageEvent, boolean isGracefulRoll) {
            requireNonNull(entityDamageEvent, "EntityDamageEvent cannot be null");
            this.eventDamage = entityDamageEvent.getDamage();
            this.isGraceful = isGracefulRoll;
        }

        public Builder modifiedDamage(double modifiedDamage) {
            this.modifiedDamage = modifiedDamage;
            return this;
        }

        public Builder fatal(boolean isFatal) {
            this.isFatal = isFatal;
            return this;
        }

        public Builder rollSuccess(boolean rollSuccess) {
            this.rollSuccess = rollSuccess;
            return this;
        }

        public Builder exploiting(boolean isExploiting) {
            this.isExploiting = isExploiting;
            return this;
        }

        public Builder xpGain(float xpGain) {
            this.xpGain = xpGain;
            return this;
        }

        /**
         * Builds and returns a {@code RollResult} instance.
         *
         * @return a new {@code RollResult}
         */
        public RollResult build() {
            return new RollResult(this);
        }
    }
}
