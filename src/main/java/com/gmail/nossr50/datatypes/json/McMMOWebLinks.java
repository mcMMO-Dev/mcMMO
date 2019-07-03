package com.gmail.nossr50.datatypes.json;

import com.gmail.nossr50.util.StringUtils;

public enum McMMOWebLinks {
    WEBSITE,
    DISCORD,
    PATREON,
    SPIGOT,
    HELP_TRANSLATE,
    WIKI;

    public String getUrl() {
        return McMMOUrl.getUrl(this);
    }

    public String getNiceTitle() {
        return StringUtils.getCapitalized(toString());
    }

    public String getLocaleKey() {
        switch (this) {
            case WEBSITE:
                return "JSON.URL.Website";
            case DISCORD:
                return "JSON.URL.Discord";
            case PATREON:
                return "JSON.URL.Patreon";
            case HELP_TRANSLATE:
                return "JSON.URL.Translation";
            case SPIGOT:
                return "JSON.URL.Spigot";
            case WIKI:
                return "JSON.URL.Wiki";
            default:
                return "";
        }
    }
}
