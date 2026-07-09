package com.gmail.nossr50.util.text;

import com.gmail.nossr50.datatypes.party.PartyFeature;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

/**
 * Utility class for String operations, including formatting and caching deterministic results to
 * improve performance.
 */
public class ConfigStringUtils {
    public static final String UNDERSCORE = "_";
    public static final String SPACE = " ";

    // Using concurrent hash maps to avoid concurrency issues (Folia)
    private static final Map<EntityType, String> configEntityStrings = new ConcurrentHashMap<>();
    private static final Map<Material, String> configMaterialStrings = new ConcurrentHashMap<>();
    private static final Map<PartyFeature, String> configPartyFeatureStrings = new ConcurrentHashMap<>();

    public static String getMaterialConfigString(Material material) {
        return configMaterialStrings.computeIfAbsent(material,
                ConfigStringUtils::createConfigFriendlyString);
    }

    public static String getConfigEntityTypeString(EntityType entityType) {
        return configEntityStrings.computeIfAbsent(entityType,
                ConfigStringUtils::createConfigFriendlyString);
    }

    public static String getConfigPartyFeatureString(PartyFeature partyFeature) {
        return configPartyFeatureStrings.computeIfAbsent(partyFeature,
                // For whatever dumb reason, party feature enums got formatted like this...
                pf -> createConfigFriendlyString(pf.name()).replace(UNDERSCORE, ""));
    }

    private static String createConfigFriendlyString(String baseString) {
        return StringUtils.capitalizeAndRejoin(baseString, '_');
    }

    private static String createConfigFriendlyString(Object object) {
        return createConfigFriendlyString(object.toString());
    }
}
