package com.gmail.nossr50.util.platform;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.compat.CompatibilityManager;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 *
 * These classes are a band-aid solution for adding NMS support into 2.1.XXX
 * In 2.2 we are switching to modules and that will clean things up significantly
 *
 */
public class PlatformManager {
    protected Platform platform; //current platform

    public PlatformManager() {
        init();
    }

    private void init() {
        platform = loadPlatform();
    }

    public Platform getPlatform() {
        return platform;
    }

    private @Nullable Platform loadPlatform() {
        ServerSoftwareType serverSoftwareType = determinePlatformType();
        PlatformBuilder platformBuilder = new PlatformBuilder();
        MinecraftGameVersion gameVersion = determineGameVersion(Bukkit.getBukkitVersion());

        return platformBuilder
                .setMinecraftGameVersion(gameVersion)
                .setSoftwareType(serverSoftwareType)
                .build();
    }

    //TODO: make this work on things other than bukkit
    @Deprecated //Only good for determining bukkit game versions
    private @NotNull MinecraftGameVersion determineGameVersion(String platformVersionString) {
        int major = 0, minor = 0, patch = 0;

        String[] splitVersion = platformVersionString.split("\\.", 3);

        mcMMO.p.getLogger().info("Platform String: " + platformVersionString);

        //TODO: this is very hacky and probably isn't reliable
        //Grab all consecutive digits
        major = getSubsequentDigits(splitVersion[0].toCharArray(), 0);
        minor = getSubsequentDigits(splitVersion[1].toCharArray(), 0);
        //Not all versions of Minecraft have a patch digit
        //If the first character isn't a digit it's not a patch number and its some crap we don't care about
        if(splitVersion.length > 2 && Character.isDigit(splitVersion[2].toCharArray()[0]))
            patch = getSubsequentDigits(splitVersion[2].toCharArray(), 0);

        mcMMO.p.getLogger().info("Minecraft version determined to be - "
                + major + "."
                + minor + "."
                + patch);

        return new MinecraftGameVersion(major, minor, patch);
    }

    /**
     * Get all consecutive digits in a char array from position
     * @param charArray target char array
     * @param position starting position
     * @return all consecutive digits from position
     */
    private int getSubsequentDigits(char[] charArray, int position) {
        ArrayList<Character> digitArrayList = new ArrayList<>();

        do {
            if(Character.isDigit(charArray[position])) {
                digitArrayList.add(charArray[position]);
                position++;
            } else {
                break;
            }
        } while (position < charArray.length);

        //Convert List<Character> -> String
        String digits = digitArrayList
                .stream()
                .map(String::valueOf)
                .collect(Collectors.joining());

        //Copy value
        return Integer.parseInt(digits);
    }

    //TODO: Rewrite this properly once we actually support a not-bukkit platform
    private @NotNull ServerSoftwareType determinePlatformType() {
        if(Bukkit.getVersion().toLowerCase(Locale.ENGLISH).contains("paper"))
            return ServerSoftwareType.PAPER;
        else if(Bukkit.getVersion().toLowerCase(Locale.ENGLISH).contains("spigot"))
            return ServerSoftwareType.SPIGOT;
        else
            return ServerSoftwareType.CRAFT_BUKKIT;
    }

    public ServerSoftwareType getServerSoftware()
    {
        return platform.getServerSoftwareType();
    }

    public String getServerSoftwareStr()
    {
        switch(getServerSoftware())
        {
            case PAPER:
                return "Paper";
            case SPIGOT:
                return "Spigot";
            default:
                return "CraftBukkit";
        }
    }

    public @Nullable CompatibilityManager getCompatibilityManager() {
        return platform.getCompatibilityManager();
    }
}
