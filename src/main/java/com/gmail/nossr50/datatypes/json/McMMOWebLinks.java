package com.gmail.nossr50.datatypes.json;

import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.text.StringUtils;

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
                return LocaleLoader.getString("JSON.URL.Website");
            case DISCORD:
                return LocaleLoader.getString("JSON.URL.Discord");
            case PATREON:
                return LocaleLoader.getString("JSON.URL.Patreon");
            case HELP_TRANSLATE:
                return LocaleLoader.getString("JSON.URL.Translation");
            case SPIGOT:
                return LocaleLoader.getString("JSON.URL.Spigot");
            case WIKI:
                return LocaleLoader.getString("JSON.URL.Wiki");
            default:
                return "";
        }
    }
}
