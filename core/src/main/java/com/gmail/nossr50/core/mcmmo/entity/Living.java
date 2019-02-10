package com.gmail.nossr50.core.mcmmo.entity;

/**
 * Living means you can die, you have health, and you can be damaged
 */
public interface Living {
    /**
     * Whether or not this entity is still alive
     * @return true if the entity is alive
     */
    Boolean isAlive();

    /**
     * Change the health of an entity
     * @param newHealth the new health value for the entity
     */
    void setHealth(int newHealth);

    /**
     * Damage an entity
     * This damage will be reduced by any defensive modifiers such as armor
     * @param damage the damage to deal to this entity
     */
    void damage(int damage);

    /**
     * Damage an entity and attribute it to a source
     * This damage will be reduced by any defensive modifiers such as armor
     * @param source the source responsible for the damage
     * @param damage the damage to deal to this entity
     */
    void damage(Entity source, int damage);
}
