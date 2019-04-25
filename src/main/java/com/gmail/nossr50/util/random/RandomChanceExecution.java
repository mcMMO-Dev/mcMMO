package com.gmail.nossr50.util.random;

public interface RandomChanceExecution {
    /**
     * Gets the XPos used in the formula for success
     *
     * @return value of x for our success probability graph
     */
    double getXPos();

    /**
     * The maximum odds for this RandomChanceExecution
     * For example, if this value is 10, then 10% odds would be the maximum and would be achieved only when xPos equaled the LinearCurvePeak
     *
     * @return maximum probability odds from 0.00 (no chance of ever happened) to 100.0 (probability can be guaranteed)
     */
    double getProbabilityCap();
}
