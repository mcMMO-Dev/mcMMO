package com.gmail.nossr50.config.mods;

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.material.MaterialData;

import com.gmail.nossr50.config.ConfigLoader;
import com.gmail.nossr50.datatypes.mods.CustomEntity;

import org.apache.commons.lang.ClassUtils;

public class CustomEntityConfig extends ConfigLoader {
    private static CustomEntityConfig instance;

    private HashMap<String, CustomEntity> customEntityClassMap = new HashMap<String, CustomEntity>();
    private HashMap<String, CustomEntity> customEntityTypeMap  = new HashMap<String, CustomEntity>();

    public CustomEntityConfig() {
        super("mods", "entities.yml");
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
        for (String entityName : config.getKeys(false)) {
            Class<?> clazz = null;
            String className = config.getString(entityName + ".Class", "");

            try {
                clazz = ClassUtils.getClass(className);
            }
            catch (ClassNotFoundException e) {
                plugin.getLogger().warning("Invalid class (" + className + ") detected for " + entityName + ".");
                plugin.getLogger().warning("This custom entity may not function properly.");
            }

            String entityTypeName = entityName.replace("_", ".");
            double xpMultiplier = config.getDouble(entityName + ".XP_Multiplier", 1.0D);

            boolean canBeTamed = config.getBoolean(entityName + ".Tameable");
            int tamingXp = config.getInt(entityName + ".Taming_XP");

            boolean canBeSummoned = config.getBoolean(entityName + ".CanBeSummoned");
            Material callOfTheWildMaterial = Material.matchMaterial(config.getString(entityName + ".COTW_Material", ""));
            byte callOfTheWildData = (byte) config.getInt(entityName + ".COTW_Material_Data");
            int callOfTheWildAmount = config.getInt(entityName + ".COTW_Material_Amount");

            if (canBeSummoned && (callOfTheWildMaterial == null || callOfTheWildAmount == 0)) {
                plugin.getLogger().warning("Incomplete Call of the Wild information. This entity will not be able to be summoned by Call of the Wild.");
                canBeSummoned = false;
            }

            CustomEntity entity = new CustomEntity(xpMultiplier, canBeTamed, tamingXp, canBeSummoned, (canBeSummoned ? new MaterialData(callOfTheWildMaterial, callOfTheWildData).toItemStack(1) : null), callOfTheWildAmount);

            customEntityTypeMap.put(entityTypeName, entity);
            customEntityClassMap.put(clazz == null ? null : clazz.getName(), entity);
        }
    }

    public boolean isCustomEntity(Entity entity) {
        if (customEntityTypeMap.containsKey(entity.getType().toString())) {
            return true;
        }

        try {
            return customEntityClassMap.containsKey(((Class<?>) entity.getClass().getDeclaredField("entityClass").get(entity)).getName());
        }
        catch (Exception e) {
            if (e instanceof NoSuchFieldException || e instanceof IllegalArgumentException || e instanceof IllegalAccessException) {
                return customEntityClassMap.containsKey(entity.getClass().getName());
            }

            e.printStackTrace();
            return false;
        }
    }

    public CustomEntity getCustomEntity(Entity entity) {
        CustomEntity customEntity = customEntityTypeMap.get(entity.getType().toString());

        if (customEntity == null) {
            try {
                customEntity = customEntityClassMap.get(((Class<?>) entity.getClass().getDeclaredField("entityClass").get(entity)).getName());
            }
            catch (Exception e) {
                if (e instanceof NoSuchFieldException || e instanceof IllegalArgumentException || e instanceof IllegalAccessException) {
                    customEntity = customEntityClassMap.get(entity.getClass().getName());
                }
                else {
                    e.printStackTrace();
                }
            }
        }

        return customEntity;
    }

    public void addEntity(CustomEntity customEntity, String className, String entityName) {
        customEntityTypeMap.put(entityName, customEntity);
        customEntityClassMap.put(className, customEntity);
    }
}
