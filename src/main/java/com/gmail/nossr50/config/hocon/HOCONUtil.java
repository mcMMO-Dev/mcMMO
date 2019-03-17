package com.gmail.nossr50.config.hocon;

import com.gmail.nossr50.util.StringUtils;

public final class HOCONUtil {

    public static String serializeENUMName(String hyphenedString)
    {
        String[] split = hyphenedString.split("_");
        String formattedString = "";

        for(int x = 0; x < split.length; x++)
        {
            if(x + 1 >= split.length)
                formattedString += StringUtils.getCapitalized(split[x]);
            else
                formattedString+= (StringUtils.getCapitalized(split[x]) + '-');
        }

        return formattedString;
    }

    public static String deserializeENUMName(String serializedName)
    {
        String[] split = serializedName.split("-");
        String formattedString = "";

        for(int x = 0; x < split.length; x++)
        {
            if(x + 1 >= split.length)
                formattedString += split[x].toUpperCase();
            else
                formattedString+= (split[x] + '_');
        }

        return formattedString;
    }
}
