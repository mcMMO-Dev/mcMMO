//package com.gmail.nossr50.util.compat.layers.attackcooldown;
//
//import com.gmail.nossr50.mcMMO;
//import com.gmail.nossr50.util.compat.layers.AbstractNMSCompatibilityLayer;
//import com.gmail.nossr50.util.nms.NMSConstants;
//import com.gmail.nossr50.util.nms.NMSVersion;
//import org.bukkit.entity.Player;
//import org.jetbrains.annotations.NotNull;
//import org.jetbrains.annotations.Nullable;
//
//import java.lang.reflect.Field;
//import java.lang.reflect.InvocationTargetException;
//import java.lang.reflect.Method;
//
///**
// *
// * These classes are a band-aid solution for adding NMS support into 2.1.XXX
// * In 2.2 we are switching to modules and that will clean things up significantly
// *
// */
//public class PlayerAttackCooldownToolLayer extends AbstractNMSCompatibilityLayer implements PlayerAttackCooldownMethods {
//
//    private final String cbNMSVersionPath;
//
//    protected Class<?> craftPlayerClass;
//    protected Class<?> entityHumanClass;
//    protected Class<?> entityLivingClass;
//
//    protected Method playerAttackCooldownMethod;
//    protected Method playerAttackStrengthMethod;
//    protected Method resetPlayerAttackCooldownMethod;
//    protected Method setPlayerAttackStrengthMethod;
//    protected Method getHandleMethod;
//    protected Field attackCooldownField;
//    protected String attackStrengthFieldName;
//
//    public PlayerAttackCooldownToolLayer(@NotNull NMSVersion nmsVersion) {
//        super(nmsVersion);
//        mcMMO.p.getLogger().info("Loading Compatibility Layer... (Player Attack Cooldown Exploit Prevention)");
//        if (!isCompatibleWithMinecraftVersion(nmsVersion)) {
//            mcMMO.p.getLogger().severe("this version of mcMMO does not support NMS for this version of Minecraft, try updating mcMMO or updating Minecraft. Not all versions of Minecraft will have NMS support built into mcMMO.");
//            cbNMSVersionPath = "";
//        } else {
//            if (NMSConstants.getCraftBukkitVersionPath(nmsVersion) != null) {
//                cbNMSVersionPath = NMSConstants.getCraftBukkitVersionPath(nmsVersion);
//                noErrorsOnInitialize = initializeLayer();
//
//                if (noErrorsOnInitialize) {
//                    mcMMO.p.getLogger().info("Successfully Loaded Compatibility Layer! (Player Attack Cooldown Exploit Prevention)");
//                }
//            } else {
//                mcMMO.p.getLogger().info("Failed to load - CL (Player Attack Cooldown Exploit Prevention) Could not find CB NMS path for CL");
//                flagErrorsDuringStartup();
//                mcMMO.p.getLogger().warning("Could not wire NMS package path for CraftBukkit!");
//                cbNMSVersionPath = "";
//            }
//        }
//    }
//
//    public static boolean isCompatibleWithMinecraftVersion(@NotNull NMSVersion nmsVersion) {
//        switch(nmsVersion) {
//            case NMS_1_13_2:
//            case NMS_1_14_4:
//            case NMS_1_15_2:
//            case NMS_1_16_4:
//            case NMS_1_16_5:
//                return true;
//            default:
//                return false;
//        }
//    }
//
//    /**
//     * Cache all reflection methods/types/classes needed for the NMS of this CompatibilityLayer
//     * @param cooldownMethodName the cooldown method name
//     * @param attackStrengthMethodName the attack strength method name
//     * @param resetAttackCooldownMethodName the reset attack cooldown method name
//     * @param getHandleMethodName the get handle method name
//     * @return true if NMS was successfully wired
//     */
//    public boolean wireNMS(@NotNull String cooldownMethodName, @NotNull String attackStrengthMethodName, @NotNull String resetAttackCooldownMethodName, @NotNull String getHandleMethodName, @NotNull String attackStrengthFieldName) {
//        entityHumanClass = initEntityHumanClass();
//
//        if (entityHumanClass != null) {
//            entityLivingClass = entityHumanClass.getSuperclass();
//        }
//
//        craftPlayerClass = initCraftPlayerClass();
//        this.attackStrengthFieldName = attackStrengthFieldName;
//
//        try {
//            this.attackCooldownField = entityLivingClass.getDeclaredField(attackStrengthFieldName);
//            this.attackCooldownField.setAccessible(true);
//        } catch (NoSuchFieldException e) {
//            e.printStackTrace();
//        }
//
//        try {
//            this.playerAttackCooldownMethod = entityHumanClass.getMethod(cooldownMethodName);
//            this.playerAttackStrengthMethod = entityHumanClass.getMethod(attackStrengthMethodName, float.class);
//            this.resetPlayerAttackCooldownMethod = entityHumanClass.getMethod(resetAttackCooldownMethodName);
//
//            if (craftPlayerClass != null) {
//                this.getHandleMethod = craftPlayerClass.getMethod(getHandleMethodName);
//            } else {
//                return false;
//            }
//            return true;
//        } catch (NoSuchMethodException e) {
//            flagErrorsDuringStartup();
//            e.printStackTrace();
//            return false;
//        }
//    }
//
//    /**
//     * Get the cached player attack cooldown method
//     * @return the cached player attack cooldown method
//     */
//    private @Nullable Method getPlayerAttackCooldownMethod() {
//        return playerAttackCooldownMethod;
//    }
//
//    /**
//     * Get the cached player attack strength method
//     * @return the cached player attack strength method
//     */
//    private @Nullable Method getPlayerAttackStrengthMethod() {
//        return playerAttackStrengthMethod;
//    }
//
//    /**
//     * Get the cached player attack cooldown reset method
//     * @return the cached player attack cooldown reset method
//     */
//    private @Nullable Method getResetPlayerAttackCooldownMethod() {
//        return resetPlayerAttackCooldownMethod;
//    }
//
//    /**
//     * Grab the CraftPlayer class type from NMS
//     * @return the CraftPlayer class type from NMS
//     */
//    private @Nullable Class<?> initCraftPlayerClass() {
//        try {
//            return Class.forName(NMSConstants.getCraftPlayerClassPath(cbNMSVersionPath));
//        } catch (ClassNotFoundException e) {
//            flagErrorsDuringStartup();
//            e.printStackTrace();
//            return null;
//        }
//    }
//
//    /**
//     * Grab the EntityHuman class type from NMS
//     * @return the EntityHuman class type from NMS
//     */
//    private @Nullable Class<?> initEntityHumanClass() {
//        try {
//            return Class.forName(NMSConstants.getEntityHumanClassPath(cbNMSVersionPath));
//        } catch (ClassNotFoundException e) {
//            flagErrorsDuringStartup();
//            e.printStackTrace();
//            return null;
//        }
//    }
//
//    private void flagErrorsDuringStartup() {
//        noErrorsOnInitialize = false;
//    }
//
//    /**
//     * Grabs the attack strength for a player
//     * Should be noted that as of today there is no way to capture a players current attack strength in spigot when they attack an entity outside of network packet listening
//     * @param player target player
//     * @return the float value of the player's attack strength
//     */
//    @Override
//    public float getAttackStrength(@NotNull Player player) throws InvocationTargetException, IllegalAccessException {
//        Object craftPlayer = craftPlayerClass.cast(player);
//        Object entityHuman = entityHumanClass.cast(getHandleMethod.invoke(craftPlayer));
//
//        return (float) playerAttackStrengthMethod.invoke(entityHuman, 0F); //Add no adjustment ticks
//    }
//
//    @Override
//    public float getCooldownValue(@NotNull Player player) throws InvocationTargetException, IllegalAccessException {
//        Object craftPlayer = craftPlayerClass.cast(player);
//        Object entityHuman = entityHumanClass.cast(getHandleMethod.invoke(craftPlayer));
//
//        return (float) playerAttackCooldownMethod.invoke(entityHuman); //Add no adjustment ticks
//    }
//
//    @Override
//    public void resetAttackStrength(@NotNull Player player) throws InvocationTargetException, IllegalAccessException {
//        Object craftPlayer = craftPlayerClass.cast(player);
//        Object entityHuman = entityHumanClass.cast(getHandleMethod.invoke(craftPlayer));
//        Object entityLiving = entityLivingClass.cast(entityHuman);
//
//        resetPlayerAttackCooldownMethod.invoke(entityLiving);
//    }
//
//    @Override
//    public int getCooldownFieldValue(@NotNull Player player) throws InvocationTargetException, IllegalAccessException {
//        Object craftPlayer = craftPlayerClass.cast(player);
//        Object entityHuman = entityHumanClass.cast(getHandleMethod.invoke(craftPlayer));
//        Object entityLiving = entityLivingClass.cast(entityHuman);
//
//        return attackCooldownField.getInt(entityLiving);
//    }
//
//    @Override
//    public void setCooldownFieldValue(@NotNull Player player, int fieldValue) throws InvocationTargetException, IllegalAccessException {
//        Object craftPlayer = craftPlayerClass.cast(player);
//        Object entityHuman = entityHumanClass.cast(getHandleMethod.invoke(craftPlayer));
//
//        attackCooldownField.setInt(entityHuman, fieldValue);
//    }
//
//    @Override
//    public boolean initializeLayer() {
//        switch(nmsVersion) {
//            case NMS_1_12_2:
//                return wireNMS("dr", "n", "ds",  "getHandle", "at");
//            case NMS_1_13_2:
//                return wireNMS("dG", "r", "dH", "getHandle", "at");
//            case NMS_1_14_4:
//                return wireNMS("dY", "s", "dZ",  "getHandle", "at");
//            case NMS_1_15_2:
//                return wireNMS("ex", "s", "ey",  "getHandle", "at");
//            case NMS_1_16_4:
//                return wireNMS("eR", "getAttackCooldown", "resetAttackCooldown",  "getHandle", "at");
//            default:
//                throw new RuntimeException("Unexpected NMS version support in PlayerAttackCooldown compatibility layer initialization!");
//        }
//    }
//}
//
