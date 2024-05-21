package com.gmail.nossr50.datatypes.json;

public class McMMOUrl {
    public static final String urlWebsite   = "https://www.mcmmo.org";
    public static final String urlDiscord   = "https://discord.gg/bJ7pFS9";
    public static final String urlPatreon   = "https://www.patreon.com/nossr50";
    public static final String urlWiki      = "https://wiki.mcmmo.org/";
    public static final String urlSpigot    = "https://spigot.mcmmo.org";
    public static final String urlTranslate = "https://translate.mcmmo.org/";

    public static String getUrl(McMMOWebLinks webLinks) {
        switch(webLinks) {
            case WIKI:
                return urlWiki;
            case PATREON:
                return urlPatreon;
            case SPIGOT:
                return urlSpigot;
            case DISCORD:
                return urlDiscord;
            case WEBSITE:
                return urlWebsite;
            case HELP_TRANSLATE:
                return urlTranslate;
            default:
                return "https://www.mcmmo.org";
        }
    }
}
