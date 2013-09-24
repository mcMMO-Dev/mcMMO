package com.gmail.nossr50.config.mods;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.ClassUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.config.ConfigLoader;
import com.gmail.nossr50.datatypes.mods.CustomEntity;

public class CustomEntityConfig extends ConfigLoader {
    private static CustomEntityConfig instance;

    public List<String> customHostileEntityTypes = new ArrayList<String>();
    public List<String> customNeutralEntityTypes = new ArrayList<String>();
    public List<String> customPassiveEntityTypes = new ArrayList<String>();
    public List<String> customEntityTypes        = new ArrayList<String>();

    public HashMap<String, CustomEntity> customEntityClassMap = new HashMap<String, CustomEntity>();
    public HashMap<String, CustomEntity> customEntityTypeMap  = new HashMap<String, CustomEntity>();

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
        loadMobs("Hostile", customHostileEntityTypes);
        loadMobs("Neutral", customNeutralEntityTypes);
        loadMobs("Passive", customPassiveEntityTypes);
    }

    private void loadMobs(String entityType, List<String> entityTypeList) {
        ConfigurationSection entitySection = config.getConfigurationSection(entityType);

        if (entitySection == null) {
            return;
        }

        Set<String> entityConfigSet = entitySection.getKeys(false);

        for (String entityName : entityConfigSet) {
            Class<?> clazz = null;
            String className = config.getString(entityType + "." + entityName + ".Class", "");

            try {
                clazz = ClassUtils.getClass(className);
            }
            catch (ClassNotFoundException e) {
                plugin.getLogger().warning("Invalid class (" + className + ") detected for " + entityName + ".");
                plugin.getLogger().warning("This custom entity may not function properly.");
            }

            String entityTypeName = entityName.replace("_", ".");
            double xpMultiplier = config.getDouble(entityType + "." + entityName + ".XP_Multiplier", 1.0D);
            boolean canBeTamed = config.getBoolean(entityType + "." + entityName + ".Tameable", false);
            int tamingXp = config.getInt(entityType + "." + entityName + "Taming_XP", 0);
            boolean canBeSummoned = config.getBoolean(entityType + "." + entityName + "CanBeSummoned", false);
            int callOfTheWildId = config.getInt(entityType + "." + entityName + "COTW_Material_ID", 0);
            int callOfTheWildData = config.getInt(entityType + "." + entityName + "COTW_Material_Data", 0);
            int callOfTheWildAmount = config.getInt(entityType + "." + entityName + "COTW_Material_Amount", 0);

            CustomEntity entity;

            if (canBeSummoned && (callOfTheWildId == 0 || callOfTheWildAmount == 0)) {
                plugin.getLogger().warning("Incomplete Call of the Wild information. This enitity will not be able to be summoned by Call of the Wild.");
                canBeSummoned = false;
            }

            entity = new CustomEntity(xpMultiplier, canBeTamed, tamingXp, canBeSummoned, new ItemStack(callOfTheWildId, callOfTheWildData), callOfTheWildAmount);

            entityTypeList.add(entityTypeName);
            customEntityTypeMap.put(entityTypeName, entity);
            customEntityClassMap.put(clazz == null ? null : clazz.getName(), entity);
            customEntityTypes.add(entityTypeName);
        }
    }
}
