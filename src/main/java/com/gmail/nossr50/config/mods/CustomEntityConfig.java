//
//package com.gmail.nossr50.config.mods;
//
//import com.gmail.nossr50.config.Config;
//import com.gmail.nossr50.datatypes.mods.CustomEntity;
//import com.gmail.nossr50.mcMMO;
//import org.apache.commons.lang.ClassUtils;
//import org.bukkit.Material;
//import org.bukkit.inventory.ItemStack;
//
//import java.util.HashMap;
//
//public class CustomEntityConfig extends Config {
//    public HashMap<String, CustomEntity> customEntityClassMap = new HashMap<String, CustomEntity>();
//    public HashMap<String, CustomEntity> customEntityTypeMap = new HashMap<String, CustomEntity>();
//
//    protected CustomEntityConfig(String fileName) {
//        //super(McmmoCore.getDataFolderPath().getPath() + "mods", fileName, false);
//        super(mcMMO.p.getDataFolder().getPath() + "mods", fileName, false);
//    }
//
//    @Override
//    protected void loadKeys() {
//        if (config.getConfigurationSection("Hostile") != null) {
//            backup();
//            return;
//        }
//
//        for (String entityName : config.getKeys(false)) {
//            Class<?> clazz = null;
//            String className = getStringValue(entityName + ".Class", "");
//
//            try {
//                clazz = ClassUtils.getClass(className);
//            } catch (ClassNotFoundException e) {
//                plugin.getLogger().warning("Invalid class (" + className + ") detected for " + entityName + ".");
//                plugin.getLogger().warning("This custom entity may not function properly.");
//            }
//
//            String entityTypeName = entityName.replace("_", ".");
//            double xpMultiplier = getDoubleValue(entityName + ".XP_Multiplier", 1.0D);
//
//            boolean canBeTamed = getBooleanValue(entityName + ".Tameable");
//            int tamingXp = getIntValue(entityName + ".Taming_XP");
//
//            boolean canBeSummoned = getBooleanValue(entityName + ".CanBeSummoned");
//            Material callOfTheWildMaterial = Material.matchMaterial(getStringValue(entityName + ".COTW_Material", ""));
//            byte callOfTheWildData = (byte) getIntValue(entityName + ".COTW_Material_Data");
//            int callOfTheWildAmount = getIntValue(entityName + ".COTW_Material_Amount");
//
//            if (canBeSummoned && (callOfTheWildMaterial == null || callOfTheWildAmount == 0)) {
//                plugin.getLogger().warning("Incomplete Call of the Wild information. This entity will not be able to be summoned by Call of the Wild.");
//                canBeSummoned = false;
//            }
//
//            CustomEntity entity = new CustomEntity(xpMultiplier, canBeTamed, tamingXp, canBeSummoned, (canBeSummoned ? new ItemStack(callOfTheWildMaterial) : null), callOfTheWildAmount);
//
//            customEntityTypeMap.put(entityTypeName, entity);
//            customEntityClassMap.put(clazz == null ? null : clazz.getName(), entity);
//        }
//    }
//}
//*/
