package com.gmail.nossr50.mcmmo.api.platform;

public enum ServerSoftwareType {
    PAPER("Paper"),
    SPIGOT("Spigot"),
    CRAFTBUKKIT("CraftBukkit");

    private final String friendlyName;

    ServerSoftwareType(String friendlyName) {

        this.friendlyName = friendlyName;
    }

    public String getFriendlyName() {
        return friendlyName;
    }
}