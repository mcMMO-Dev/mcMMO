package com.gmail.nossr50.util.text;

import com.gmail.nossr50.datatypes.party.PartyFeature;
import com.gmail.nossr50.datatypes.skills.SuperAbilityType;
import org.bukkit.Material;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.Locale;

public class StringUtils {

    protected static DecimalFormat percent = new DecimalFormat("##0.00%");
    protected static DecimalFormat shortDecimal = new DecimalFormat("##0.0");

    /**
     * Gets a capitalized version of the target string.
     *
     * @param target
     *            String to capitalize
     * @return the capitalized string
     */
    public static String getCapitalized(String target) {
        return target.substring(0, 1).toUpperCase() + target.substring(1).toLowerCase(Locale.ENGLISH);
    }

    public static String ticksToSeconds(double ticks) {
        return shortDecimal.format(ticks / 20);
    }

    public static String convertToCamelCaseString(String baseString, String splitBy) {
        String[] substrings = baseString.split(splitBy);
        String prettyString = "";
        int size = 1;

        for (String string : substrings) {
            prettyString = prettyString.concat(getCapitalized(string));

            if (size < substrings.length) {
                prettyString = prettyString.concat("");
            }

            size++;
        }

        return prettyString;
    }

    public static String getPrettyCamelCaseName(Object o) {
        return StringUtils.convertToCamelCaseString(o.toString(), "_");
    }

    public static String getPrettySuperAbilityName(SuperAbilityType superAbilityType) {
        return StringUtils.getPrettySuperAbilityString(superAbilityType);
    }

    public static String getPrettySuperAbilityString(SuperAbilityType ability) {
        return createPrettyString(ability.toString());
    }

    /**
     * Creates a string from an array skipping the first n elements
     * @param args the array to iterate over when forming the string
     * @param index the amount of elements to skip over
     * @return the "trimmed" string
     */
    public static String buildStringAfterNthElement(@NotNull String @NotNull []args, int index) {
        StringBuilder trimMessage = new StringBuilder();

        for (int i = index; i < args.length; i++) {
            if(i + 1 >= args.length)
                trimMessage.append(args[i]);
            else
                trimMessage.append(args[i]).append(" ");
        }

        return trimMessage.toString();
    }

    public static String getPrettyItemString(Material material) {
        return createPrettyString(material.toString());
    }

    public static String getPrettyEntityTypeString(EntityType entity) {
        return createPrettyString(entity.toString());
    }

    public static String getPrettyAbilityString(SuperAbilityType ability) {
        return createPrettyString(ability.toString());
    }
    
    public static String getWildcardConfigBlockDataString(BlockData data) {
        return getWildcardConfigMaterialString(data.getMaterial());
    }

    public static String getWildcardConfigMaterialString(Material data) {
        return StringUtils.getPrettyItemString(data).replace(" ", "_") + "|*";
    }

    public static String getFriendlyConfigBlockDataString(BlockData data) {
        switch(data.getMaterial()){
            case CHORUS_FLOWER:
            case COCOA:
            case WHEAT:
            case BEETROOTS:
            case CARROTS:
            case POTATOES:
            case NETHER_WART: {
                if (data instanceof Ageable ageData) {
                    if (ageData.getAge() == ageData.getMaximumAge()) {
                        return getPrettyItemString(data.getMaterial()).replace(" ", "_") + "_Ripe";
                    }
                }
                return getPrettyItemString(data.getMaterial()).replace(" ", "_") + "_Ungrown";
            }
        }
        return getPrettyItemString(data.getMaterial()).replace(" ", "_");
    }

    public static String getFriendlyConfigMaterialString(Material data) {
        return getPrettyItemString(data).replace(" ", "_");
    }

    public static String getExplicitConfigBlockDataString(BlockData data) {
        return getExplicitConfigMaterialString(data.getMaterial());
    }

    public static String getExplicitConfigMaterialString(Material data) {
        return StringUtils.getPrettyItemString(data).replace(" ", "_");
    }

    public static String getPrettyPartyFeatureString(PartyFeature partyFeature) {
        return createPrettyString(partyFeature.toString());
    }

    private static String createPrettyString(String baseString) {
        String[] substrings = baseString.split("_");
        String prettyString = "";
        int size = 1;

        for (String string : substrings) {
            prettyString = prettyString.concat(getCapitalized(string));

            if (size < substrings.length) {
                prettyString = prettyString.concat(" ");
            }

            size++;
        }

        return prettyString;
    }

    /**
     * Determine if a string represents an Integer
     *
     * @param string
     *            String to check
     * @return true if the string is an Integer, false otherwise
     */
    public static boolean isInt(String string) {
        try {
            Integer.parseInt(string);
            return true;
        } catch (NumberFormatException nFE) {
            return false;
        }
    }

    /**
     * Determine if a string represents a Double
     *
     * @param string
     *            String to check
     * @return true if the string is a Double, false otherwise
     */
    public static boolean isDouble(String string) {
        try {
            Double.parseDouble(string);
            return true;
        } catch (NumberFormatException nFE) {
            return false;
        }
    }

    public static String convertKeyToName(@NotNull String key) {
        // used when no display name is given for a potion
        final String noUnderscores = key.replace("_", " ").toLowerCase(Locale.ENGLISH);
        return org.codehaus.plexus.util.StringUtils.capitalise(noUnderscores);
    }
}
