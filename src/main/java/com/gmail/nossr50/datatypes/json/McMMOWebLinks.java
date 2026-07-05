package com.gmail.nossr50.datatypes.json;

import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.text.StringUtils;
import net.kyori.adventure.text.TextComponent;

public enum McMMOWebLinks {
    WEBSITE,
    DISCORD,
    SPIGOT,
    WIKI;

    public String getUrl() {
        return McMMOUrl.getUrl(this);
    }

    public String getNiceTitle() {
        return StringUtils.getCapitalized(toString());
    }

    /** Returns the raw locale description string (legacy compatibility). */
    public String getLocaleDescription() {
        return switch (this) {
            case WEBSITE -> LocaleLoader.getString("JSON.URL.Website");
            case DISCORD -> LocaleLoader.getString("JSON.URL.Discord");
            case SPIGOT -> LocaleLoader.getString("JSON.URL.Spigot");
            case WIKI -> LocaleLoader.getString("JSON.URL.Wiki");
        };
    }

    /** Returns the colored description as a proper Adventure component. */
    public TextComponent getDescriptionComponent() {
        return switch (this) {
            case WEBSITE -> LocaleLoader.getTextComponent("JSON.URL.Website");
            case DISCORD -> LocaleLoader.getTextComponent("JSON.URL.Discord");
            case SPIGOT -> LocaleLoader.getTextComponent("JSON.URL.Spigot");
            case WIKI -> LocaleLoader.getTextComponent("JSON.URL.Wiki");
        };
    }

    /**
     * Returns the colored detail line component for the hover tooltip,
     * or {@code null} if this link has no detail line.
     */
    public TextComponent getDetailComponent() {
        return switch (this) {
            case WEBSITE -> LocaleLoader.getTextComponent("JSON.URL.Website.Detail");
            case SPIGOT -> LocaleLoader.getTextComponent("JSON.URL.Spigot.Detail");
            case WIKI -> LocaleLoader.getTextComponent("JSON.URL.Wiki.Detail");
            case DISCORD -> null;
        };
    }

    /** Returns the colored label component used in the URL row (e.g. &9Web). */
    public TextComponent getLabelComponent() {
        return switch (this) {
            case WEBSITE -> LocaleLoader.getTextComponent("JSON.URL.Label.Website");
            case DISCORD -> LocaleLoader.getTextComponent("JSON.URL.Label.Discord");
            case SPIGOT -> LocaleLoader.getTextComponent("JSON.URL.Label.Spigot");
            case WIKI -> LocaleLoader.getTextComponent("JSON.URL.Label.Wiki");
        };
    }
}
