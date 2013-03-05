package com.gmail.nossr50.util;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;

public class StringUtils {
    /**
     * Gets a capitalized version of the target string.
     *
     * @param target String to capitalize
     * @return the capitalized string
     */
    public static String getCapitalized(String target) {
        String firstLetter = target.substring(0, 1);
        String remainder = target.substring(1);
        String capitalized = firstLetter.toUpperCase() + remainder.toLowerCase();

        return capitalized;
    }

    /**
     * Gets a nicely formatted string version of an item name from a given item ID.
     *
     * @param itemID The ID of the item to convert to string.
     * @return the nicely formatted string
     */
    public static String getPrettyItemString(int itemID) {
        return createPrettyEnumString(Material.getMaterial(itemID).toString());
    }

    public static String getPrettyItemString(Material material) {
        return createPrettyEnumString(material.toString());
    }

    public static String getPrettyEntityTypeString(EntityType entity) {
        return createPrettyEnumString(entity.toString());
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
     * Determine if a string represents a Long
     *
     * @param string String to check
     * @return true if the string is a Long, false otherwise
     */
    public static boolean isLong(String string) {
        try {
            Long.parseLong(string);
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
