package com.gmail.nossr50.datatypes.skills.subskills.taming;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;

/**
 * Data Container for properties used in summoning an entity via COTW
 */
public class TamingSummon {

    private final Material itemType;
    private final int itemAmountRequired;
    private final int entitiesSummoned;
    private final int summonLifespan;
    private final int summonCap;
    private final CallOfTheWildType callOfTheWildType;
    private EntityType entityType;

    public TamingSummon(CallOfTheWildType callOfTheWildType, Material itemType, int itemAmountRequired, int entitiesSummoned, int summonLifespan, int summonCap) {
        this.callOfTheWildType = callOfTheWildType;
        this.itemType = itemType;
        this.itemAmountRequired = Math.max(itemAmountRequired, 1);
        this.entitiesSummoned = Math.max(entitiesSummoned, 1);
        this.summonLifespan = summonLifespan;
        this.summonCap = Math.max(summonCap, 1);

        initEntityType();
    }

    private void initEntityType() {
        switch(callOfTheWildType) {
            case WOLF:
                entityType = EntityType.WOLF;
                break;
            case HORSE:
                entityType = EntityType.HORSE;
                break;
            case CAT:
                if (shouldSpawnCatInsteadOfOcelot()) {
                    //Server is on 1.14 or above
                    entityType = EntityType.CAT;
                } else {
                    //Server is not on 1.14 or above
                    entityType = EntityType.OCELOT;
                }
        }
    }

    private boolean shouldSpawnCatInsteadOfOcelot() {
        try {
            Class<?> clazz = Class.forName("org.bukkit.entity.Panda");
            //Panda exists which means this is at least 1.14, so we should spawn a cat instead of ocelot
            return true;
        } catch (ClassNotFoundException e) {
            /*e.printStackTrace();*/
            return false;
        }
    }

    public EntityType getEntityType() {
        return entityType;
    }

    public Material getItemType() {
        return itemType;
    }

    public int getItemAmountRequired() {
        return itemAmountRequired;
    }

    public int getEntitiesSummoned() {
        return entitiesSummoned;
    }

    public int getSummonLifespan() {
        return summonLifespan;
    }

    public int getSummonCap() {
        return summonCap;
    }

    public CallOfTheWildType getCallOfTheWildType() {
        return callOfTheWildType;
    }
}
