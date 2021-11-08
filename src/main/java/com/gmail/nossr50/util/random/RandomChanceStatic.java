package com.gmail.nossr50.util.random;

public class RandomChanceStatic implements RandomChanceExecution {
    private final double xPos;
    private final double probabilityCap;
    private final boolean isLucky;

    public RandomChanceStatic(double xPos, double probabilityCap, boolean isLucky) {
        this.xPos = xPos;
        this.probabilityCap = probabilityCap;
        this.isLucky = isLucky;
    }

    /**
     * Gets the XPos used in the formula for success
     *
     * @return value of x for our success probability graph
     */
    @Override
    public double getXPos() {
        return xPos;
    }

    /**
     * The maximum odds for this RandomChanceExecution
     * For example, if this value is 10, then 10% odds would be the maximum and would be achieved only when xPos equaled the LinearCurvePeak
     *
     * @return maximum probability odds from 0.00 (no chance of ever happened) to 100.0 (probability can be guaranteed)
     */
    @Override
    public double getProbabilityCap() {
        return probabilityCap;
    }

    public boolean isLucky() {
        return isLucky;
    }
}
