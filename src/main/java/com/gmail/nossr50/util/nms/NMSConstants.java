package com.gmail.nossr50.util.nms;

import org.jetbrains.annotations.Nullable;

public class NMSConstants {
    public final static String BUKKIT_PACKAGE_PATH = "org.bukkit";
    public final static String CRAFT_BUKKIT_PACKAGE_PATH = "org.bukkit.craftbukkit";
    public final static String NET_MINECRAFT_SERVER = "net.minecraft.server";

    private final static String CRAFT_PLAYER_CLASS_PATH = "entity.CraftPlayer";
    private final static String ENTITY_HUMAN_CLASS_PATH = "EntityHuman";

    /**
     * Grabs the fully qualified path of a class from CB
     *
     * @param targetClass source root path
     * @return the fully qualified path of a CB class
     */
    protected static String getFullyQualifiedCraftBukkitPath(String cbVersionPackage,
            String targetClass) {
        return CRAFT_BUKKIT_PACKAGE_PATH + "." + cbVersionPackage + "." + targetClass;
    }

    protected static String getFullQualifiedBukkitPath(String fromSourceRoot) {
        return BUKKIT_PACKAGE_PATH + "." + fromSourceRoot;
    }

    protected static String getFullyQualifiedNMSPath(String cbVersionPackage,
            String fromSourceRoot) {
        return NET_MINECRAFT_SERVER + "." + cbVersionPackage + "." + fromSourceRoot;
    }

    public static String getCraftPlayerClassPath(String cbVersionPackage) {
        return getFullyQualifiedCraftBukkitPath(cbVersionPackage, CRAFT_PLAYER_CLASS_PATH);
    }

    public static String getEntityHumanClassPath(String cbVersionPackage) {
        return getFullyQualifiedNMSPath(cbVersionPackage, ENTITY_HUMAN_CLASS_PATH);
    }

    public static @Nullable String getCraftBukkitVersionPath(NMSVersion nmsVersion) {
        switch (nmsVersion) {

            case NMS_1_8_8:
                break;
            case NMS_1_12_2:
                return "v1_12_R1";
            case NMS_1_13_2:
                return "v1_13_R2";
            case NMS_1_14_4:
                return "v1_14_R1";
            case NMS_1_15_2:
                return "v1_15_R1";
            case NMS_1_16_1:
                return "v1_16_R1";
            case NMS_1_16_4:
                return "v1_16_R3";
            case UNSUPPORTED:
                break;
        }

        return null;
    }
}
