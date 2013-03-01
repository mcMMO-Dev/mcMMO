package com.gmail.nossr50.util;

import java.text.DecimalFormat;

import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.skills.PerksUtils;

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
     *
     * @param player Target player
     * @param version Plugin version
     */
    public static void displayVersion(Player player, String version) {
        player.sendMessage(LocaleLoader.getString("MOTD.Version", version));
    }

    /**
     * Display Hardcore Mode settings.
     *
     * @param player Target player
     */
    public static void displayHardcoreSettings(Player player) {
        if (Config.getInstance().getHardcoreEnabled()) {
            if (Config.getInstance().getHardcoreVampirismEnabled()) {
                player.sendMessage(LocaleLoader.getString("MOTD.Hardcore.VampireOn"));
                player.sendMessage(LocaleLoader.getString("MOTD.Hardcore.Stats", Config.getInstance().getHardcoreDeathStatPenaltyPercentage()));
                player.sendMessage(LocaleLoader.getString("MOTD.Vampire.Stats", Config.getInstance().getHardcoreVampirismStatLeechPercentage()));
            }
            else {
                player.sendMessage(LocaleLoader.getString("MOTD.Hardcore.VampireOff"));
                player.sendMessage(LocaleLoader.getString("MOTD.Hardcore.Stats", Config.getInstance().getHardcoreDeathStatPenaltyPercentage()));
            }
        }
    }

    /**
     * Display XP perks.
     *
     * @param player Target player
     */
    public static void displayXpPerks(Player player) {
        int perkAmount = PerksUtils.handleXpPerks(player, 1);

        if (perkAmount > 1) {
            player.sendMessage(perkPrefix + LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Perks.xp.name"), LocaleLoader.getString("Perks.xp.desc", perkAmount)));
        }
    }

    /**
     * Display cooldown perks.
     *
     * @param player Target player
     */
    public static void displayCooldownPerks(Player player) {
        double cooldownReduction = 1 - (PerksUtils.handleCooldownPerks(player, 12) / 12.0);

        if (cooldownReduction > 0.0) {
            DecimalFormat percent = new DecimalFormat("##0.00%");
            player.sendMessage(perkPrefix + LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Perks.cooldowns.name"), LocaleLoader.getString("Perks.cooldowns.desc", percent.format(cooldownReduction))));
        }
    }

    /**
     * Display activiation perks.
     *
     * @param player Target player
     */
    public static void displayActivationPerks(Player player) {
        int perkAmount = PerksUtils.handleActivationPerks(player, 0, 0);

        if (perkAmount > 0) {
            player.sendMessage(perkPrefix + LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Perks.activationtime.name"), LocaleLoader.getString("Perks.activationtime.desc", perkAmount)));
        }
    }

    /**
     * Display "lucky" perks.
     *
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
     *
     * @param player Target player
     * @param website Plugin website
     */
    public static void displayWebsite(Player player, String website) {
        player.sendMessage(LocaleLoader.getString("MOTD.Website", website));
    }
}
