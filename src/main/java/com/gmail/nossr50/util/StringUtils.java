package com.gmail.nossr50.util;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import com.gmail.nossr50.datatypes.skills.AbilityType;

public class StringUtils {
    /**
     * Gets a capitalized version of the target string.
     *
     * @param target String to capitalize
     * @return the capitalized string
     */
    public static String getCapitalized(String target) {
        return target.substring(0, 1).toUpperCase() + target.substring(1).toLowerCase();
    }

    public static String getPrettyItemString(Material material) {
        return createPrettyEnumString(material.toString());
    }

    public static String getPrettyEntityTypeString(EntityType entity) {
        return createPrettyEnumString(entity.toString());
    }

    public static String getPrettyAbilityString(AbilityType ability) {
        return createPrettyEnumString(ability.toString());
    }

    private static String createPrettyEnumString(String baseString) {
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
     * Gets the int represented by this string.
     *
     * @param string The string to parse
     * @return the int represented by this string
     */
    public static int getInt(String string) {
        try {
            return Integer.parseInt(string);
        }
        catch (NumberFormatException nFE) {
            return 0;
        }
    }

    /**
     * Gets the long represented by this string.
     *
     * @param string The string to parse
     * @return the long represented by this string
     */
    public static long getLong(String string) {
        try {
            return Long.parseLong(string);
        }
        catch (NumberFormatException nFE) {
            return 0;
        }
    }

    /**
     * Determine if a string represents an Integer
     *
     * @param string String to check
     * @return true if the string is an Integer, false otherwise
     */
    public static boolean isInt(String string) {
        try {
            Integer.parseInt(string);
            return true;
        }
        catch (NumberFormatException nFE) {
            return false;
        }
    }

    /**
     * Determine if a string represents a Double
     *
     * @param string String to check
     * @return true if the string is a Double, false otherwise
     */
    public static boolean isDouble(String string) {
        try {
            Double.parseDouble(string);
            return true;
        }
        catch (NumberFormatException nFE) {
            return false;
        }
    }
}
