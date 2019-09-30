package com.gmail.nossr50.config;

import com.gmail.nossr50.util.StringUtils;

public final class HOCONUtil {

    public static String serializeENUMName(String hyphenedString) {
        String[] split = hyphenedString.split("_");
        StringBuilder formattedString = new StringBuilder();

        for (int x = 0; x < split.length; x++) {
            if (x + 1 >= split.length)
                formattedString.append(StringUtils.getCapitalized(split[x]));
            else
                formattedString.append(StringUtils.getCapitalized(split[x])).append('-');
        }

        return formattedString.toString();
    }

    public static String deserializeENUMName(String serializedName) {
        String[] split = serializedName.split("-");
        StringBuilder formattedString = new StringBuilder();

        for (int x = 0; x < split.length; x++) {
            if (x + 1 >= split.length)
                formattedString.append(split[x].toUpperCase());
            else
                formattedString.append(split[x]).append('_');
        }

        return formattedString.toString();
    }
}
