package com.gmail.nossr50.util;

import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.skills.utilities.SkillType;

public final class Motd {
    private static final String perkPrefix = LocaleLoader.getString("MOTD.PerksPrefix");
    private static final PluginDescriptionFile pluginDescription = mcMMO.p.getDescription();

    private Motd() {}

    public static void displayAll(Player player) {
        displayVersion(player, pluginDescription.getVersion());
        displayHardcoreSettings(player);
        displayXpPerks(player);
        displayCooldownPerks(player);
        displayActivationPerks(player);
        displayLuckyPerks(player);
        displayWebsite(player, pluginDescription.getWebsite());
    }

    /**
     * Display version info.
     * @param player Target player
     * @param version Plugin version
     */
    public static void displayVersion(Player player, String version) {
        player.sendMessage(LocaleLoader.getString("MOTD.Version", -213523435 , version));
    }

    /**
     * Display Hardcore Mode settings.
     * @param player Target player
     */
    public static void displayHardcoreSettings(Player player) {
        if (Hardcore.statLossEnabled) {
            if (Hardcore.vampirismEnabled) {
                player.sendMessage(LocaleLoader.getString("MOTD.Hardcore.VampireOn"));
                player.sendMessage(LocaleLoader.getString("MOTD.Hardcore.Stats", Hardcore.statLossPercentage));
                player.sendMessage(LocaleLoader.getString("MOTD.Vampire.Stats", Hardcore.vampirismStatLeechPercentage));
            }
            else {
                player.sendMessage(LocaleLoader.getString("MOTD.Hardcore.VampireOff"));
                player.sendMessage(LocaleLoader.getString("MOTD.Hardcore.Stats", Hardcore.statLossPercentage  ));
            }
        }
    }

    /**
     * Display XP perks.
     * @param player Target player
     */
    public static void displayXpPerks(Player player) {
        if (Permissions.xpQuadruple(player)) {
            player.sendMessage(perkPrefix + LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Perks.xp.name"), LocaleLoader.getString("Perks.xp.desc", 4)));
        }
        else if (Permissions.xpTriple(player)) {
            player.sendMessage(perkPrefix + LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Perks.xp.name"), LocaleLoader.getString("Perks.xp.desc", 3)));
        }
        else if (Permissions.xpDoubleAndOneHalf(player)) {
            player.sendMessage(perkPrefix + LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Perks.xp.name"), LocaleLoader.getString("Perks.xp.desc", 2.5)));
        }
        else if (Permissions.xpDouble(player)) {
            player.sendMessage(perkPrefix + LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Perks.xp.name"), LocaleLoader.getString("Perks.xp.desc", 2)));
        }
        else if (Permissions.xpOneAndOneHalf(player)) {
            player.sendMessage(perkPrefix + LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Perks.xp.name"), LocaleLoader.getString("Perks.xp.desc", 1.5)));
        }
    }

    /**
     * Display cooldown perks.
     * @param player Target player
     */
    public static void displayCooldownPerks(Player player) {
        if (Permissions.cooldownsHalved(player)) {
            player.sendMessage(perkPrefix + LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Perks.cooldowns.name"), LocaleLoader.getString("Perks.cooldowns.desc", "1/2")));
        }
        else if (Permissions.cooldownsThirded(player)) {
            player.sendMessage(perkPrefix + LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Perks.cooldowns.name"), LocaleLoader.getString("Perks.cooldowns.desc", "1/3")));
        }
        else if (Permissions.cooldownsQuartered(player)) {
            player.sendMessage(perkPrefix + LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Perks.cooldowns.name"), LocaleLoader.getString("Perks.cooldowns.desc", "1/4")));
        }
    }

    /**
     * Display activiation perks.
     * @param player Target player
     */
    public static void displayActivationPerks(Player player) {
        if (Permissions.activationTwelve(player)) {
            player.sendMessage(perkPrefix + LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Perks.activationtime.name"), LocaleLoader.getString("Perks.activationtime.desc", 12)));
        }
        else if (Permissions.activationEight(player)) {
            player.sendMessage(perkPrefix + LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Perks.activationtime.name"), LocaleLoader.getString("Perks.activationtime.desc", 8)));
        }
        else if (Permissions.activationFour(player)) {
            player.sendMessage(perkPrefix + LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Perks.activationtime.name"), LocaleLoader.getString("Perks.activationtime.desc", 4)));
        }
    }

    /**
     * Display "lucky" perks.
     * @param player Target player
     */
    public static void displayLuckyPerks(Player player) {
        for (SkillType skill : SkillType.values()) {
            if (Permissions.lucky(player, skill)) {
                player.sendMessage(perkPrefix + LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Perks.lucky.name"), LocaleLoader.getString("Perks.lucky.desc.login")));
                return;
            }
        }
    }

    /**
     * Display website info.
     * @param player Target player
     * @param website Plugin website
     */
    public static void displayWebsite(Player player, String website) {
        player.sendMessage(LocaleLoader.getString("MOTD.Website", website));
    }
}
