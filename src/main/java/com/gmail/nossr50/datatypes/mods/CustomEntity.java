package com.gmail.nossr50.datatypes.mods;

import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public class CustomEntity {
    private int entityID;
    private EntityType entityType;
    private double xpMultiplier;
    private boolean canBeTamed;
    private int tamingXP;
    private boolean canBeSummoned;
    private ItemStack callOfTheWildItem;
    private int callOfTheWildAmount;

    public CustomEntity(int entityID, EntityType entityType, double xpMultiplier, boolean canBeTamed, int tamingXP, boolean canBeSummoned, ItemStack callOfTheWildItem, int callOfTheWildAmount) {
        this.entityID = entityID;
        this.entityType = entityType;
        this.xpMultiplier = xpMultiplier;
        this.canBeTamed = canBeTamed;
        this.tamingXP = tamingXP;
        this.canBeSummoned = canBeSummoned;
        this.callOfTheWildItem = callOfTheWildItem;
        this.callOfTheWildAmount = callOfTheWildAmount;
    }

    public int getEntityID() {
        return entityID;
    }

    public void setEntityID(int entityID) {
        this.entityID = entityID;
    }

    public EntityType getEntityType() {
        return entityType;
    }

    public void setEntityType(EntityType entityType) {
        this.entityType = entityType;
    }

    public double getXpMultiplier() {
        return xpMultiplier;
    }

    public void setXpMultiplier(double xpMultiplier) {
        this.xpMultiplier = xpMultiplier;
    }

    public boolean isCanBeTamed() {
        return canBeTamed;
    }

    public void setCanBeTamed(boolean canBeTamed) {
        this.canBeTamed = canBeTamed;
    }

    public int getTamingXP() {
        return tamingXP;
    }

    public void setTamingXP(int tamingXP) {
        this.tamingXP = tamingXP;
    }

    public boolean isCanBeSummoned() {
        return canBeSummoned;
    }

    public void setCanBeSummoned(boolean canBeSummoned) {
        this.canBeSummoned = canBeSummoned;
    }

    public ItemStack getCallOfTheWildItem() {
        return callOfTheWildItem;
    }

    public void setCallOfTheWildItem(ItemStack callOfTheWildItem) {
        this.callOfTheWildItem = callOfTheWildItem;
    }

    public int getCallOfTheWildAmount() {
        return callOfTheWildAmount;
    }

    public void setCallOfTheWildAmount(int callOfTheWildAmount) {
        this.callOfTheWildAmount = callOfTheWildAmount;
    }
}
