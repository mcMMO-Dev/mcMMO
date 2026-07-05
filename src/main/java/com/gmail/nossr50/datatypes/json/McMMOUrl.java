package com.gmail.nossr50.datatypes.json;

public class McMMOUrl {
    public static final String urlWebsite = "https://www.mcmmo.org";
    public static final String urlDiscord = "https://discord.gg/bJ7pFS9";
    public static final String urlWiki = "https://wiki.mcmmo.org/";
    public static final String urlSpigot = "https://spigot.mcmmo.org";

    public static String getUrl(McMMOWebLinks webLinks) {
        return switch (webLinks) {
            case WIKI -> urlWiki;
            case SPIGOT -> urlSpigot;
            case DISCORD -> urlDiscord;
            case WEBSITE -> urlWebsite;
        };
    }
}
