package com.gmail.nossr50.core.mcmmo.world;

import java.util.Objects;

/**
 * A World in MC
 * Stuff mcMMO does not require will not be in this class
 */
public abstract class AbstractWorld implements World {

    private final String worldName;

    public AbstractWorld(String worldName) {
        this.worldName = worldName;
    }

    /**
     * Gets the name of this World
     *
     * @return the name of this world
     */
    @Override
    public String getName() {
        return worldName;
    }

    /**
     * Compares this object to another to see if they are equal
     *
     * @param o the other object
     * @return true if they are equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractWorld)) return false;
        AbstractWorld that = (AbstractWorld) o;
        return worldName.equals(that.worldName);
    }

    /**
     * The hash code for the object, used for comparisons
     *
     * @return hash code for this object
     */
    @Override
    public int hashCode() {
        return Objects.hash(worldName);
    }
}
