package com.gmail.nossr50.datatypes.skills.subskills.interfaces;

public interface Rank {
    /**
     * Gets the number of ranks for this subskill, 0 for no ranks
     *
     * @return the number of ranks for this subskill, 0 for no ranks
     */
    int getNumRanks();

    /**
     * Not all skills have ranks
     *
     * @return true if the skill has ranks
     */
    boolean hasRanks();

    /*
      An sequential collection of rank level requirements
      @return level requirements
     */
    //Collection<Integer> getUnlockLevels();
}
