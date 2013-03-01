package com.gmail.nossr50.config.mods;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.config.ConfigLoader;
import com.gmail.nossr50.datatypes.mods.CustomEntity;

public class CustomEntityConfig extends ConfigLoader {
    private static CustomEntityConfig instance;

    public List<Integer> customEntityIds        = new ArrayList<Integer>();
    public List<Integer> customHostileEntityIds = new ArrayList<Integer>();
    public List<Integer> customNeutralEntityIds = new ArrayList<Integer>();
    public List<Integer> customPassiveEntityIds = new ArrayList<Integer>();

    public List<EntityType> customEntityTypes = new ArrayList<EntityType>();
    public List<CustomEntity> customEntities = new ArrayList<CustomEntity>();

    public CustomEntityConfig() {
        super("ModConfigs", "entities.yml");
        loadKeys();
    }

    public static CustomEntityConfig getInstance() {
        if (instance == null) {
            instance = new CustomEntityConfig();
        }

        return instance;
    }

    @Override
    protected void loadKeys() {
        loadMobs("Hostile", customHostileEntityIds);
        loadMobs("Neutral", customNeutralEntityIds);
        loadMobs("Passive", customPassiveEntityIds);
    }

    private void loadMobs(String entityType, List<Integer> entityIdList) {
        ConfigurationSection entitySection = config.getConfigurationSection(entityType);

        if (entitySection == null) {
            return;
        }

        Set<String> entityConfigSet = entitySection.getKeys(false);

        for (String entityName : entityConfigSet) {
            int id = config.getInt(entityType + "." + entityName + ".ID", 0);
            EntityType type = EntityType.fromId(id);
            double xpMultiplier = config.getDouble(entityType + "." + entityName + ".XP_Multiplier", 1.0D);
            boolean canBeTamed = config.getBoolean(entityType + "." + entityName + ".Tameable", false);
            int tamingXp = config.getInt(entityType + "." + entityName + "Taming_XP", 0);
            boolean canBeSummoned = config.getBoolean(entityType + "." + entityName + "CanBeSummoned", false);
            int callOfTheWildId = config.getInt(entityType + "." + entityName + "COTW_Material_ID", 0);
            int callOfTheWildData = config.getInt(entityType + "." + entityName + "COTW_Material_Data", 0);
            int callOfTheWildAmount = config.getInt(entityType + "." + entityName + "COTW_Material_Amount", 0);

            CustomEntity entity;

            if (id == 0) {
                plugin.getLogger().warning("Missing ID. This block will be skipped.");
                continue;
            }

            if (canBeSummoned && (callOfTheWildId == 0 || callOfTheWildAmount == 0)) {
                plugin.getLogger().warning("Incomplete Call of the Wild information. This enitity will not be able to be summoned by Call of the Wild.");
                canBeSummoned = false;
            }

            entity = new CustomEntity(id, type, xpMultiplier, canBeTamed, tamingXp, canBeSummoned, new ItemStack(callOfTheWildId, callOfTheWildData), callOfTheWildAmount);

            entityIdList.add(id);
            customEntityTypes.add(type);
            customEntities.add(entity);
        }
    }
}
