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

    public String getLocaleDescription() {
        switch (this) {
            case WEBSITE:
                return pluginRef.getLocaleManager().getString("JSON.URL.Website");
            case DISCORD:
                return pluginRef.getLocaleManager().getString("JSON.URL.Discord");
            case PATREON:
                return pluginRef.getLocaleManager().getString("JSON.URL.Patreon");
            case HELP_TRANSLATE:
                return pluginRef.getLocaleManager().getString("JSON.URL.Translation");
            case SPIGOT:
                return pluginRef.getLocaleManager().getString("JSON.URL.Spigot");
            case WIKI:
                return pluginRef.getLocaleManager().getString("JSON.URL.Wiki");
            default:
                return "";
        }
    }
}
