package com.gmail.nossr50.util;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.skills.PerksUtils;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;

public final class Motd {
    public static final String PERK_PREFIX = LocaleLoader.getString("MOTD.PerksPrefix") + " ";
    private static final PluginDescriptionFile pluginDescription = mcMMO.p.getDescription();

    private Motd() {
    }

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
        if (Permissions.showversion(player)) {
            player.sendMessage(LocaleLoader.getString("MOTD.Version.Overhaul", version));
        }
    }

    /**
     * Display Hardcore Mode settings.
     *
     * @param player Target player
     */
    public static void displayHardcoreSettings(Player player) {
        boolean deathStatLossEnabled = HardcoreManager.isStatLossEnabled();
        boolean vampirismEnabled = HardcoreManager.isVampirismEnabled();

        if (!deathStatLossEnabled && !vampirismEnabled) {
            return;
        }

        String statLossInfo = "";
        String vampirismInfo = "";
        String seperator = "";

        if (deathStatLossEnabled) {
            statLossInfo = LocaleLoader.getString("Hardcore.DeathStatLoss.Name");
        }

        if (vampirismEnabled) {
            vampirismInfo = LocaleLoader.getString("Hardcore.Vampirism.Name");
        }

        if (deathStatLossEnabled && vampirismEnabled) {
            seperator = " & ";
        }

        player.sendMessage(LocaleLoader.getString("MOTD.Hardcore.Enabled",
                statLossInfo + seperator + vampirismInfo));

        if (deathStatLossEnabled) {
            player.sendMessage(LocaleLoader.getString("MOTD.Hardcore.DeathStatLoss.Stats",
                    mcMMO.p.getGeneralConfig().getHardcoreDeathStatPenaltyPercentage()));
        }

        if (vampirismEnabled) {
            player.sendMessage(LocaleLoader.getString("MOTD.Hardcore.Vampirism.Stats",
                    mcMMO.p.getGeneralConfig().getHardcoreVampirismStatLeechPercentage()));
        }
    }

    /**
     * Display XP perks.
     *
     * @param player Target player
     */
    public static void displayXpPerks(Player player) {
        for (PrimarySkillType skill : PrimarySkillType.values()) {
            //TODO: Wow this is horrifying...
            if (PerksUtils.handleXpPerks(player, 1, skill) > 1) {
                player.sendMessage(PERK_PREFIX + LocaleLoader.getString("Effects.Template",
                        LocaleLoader.getString("Perks.XP.Name"),
                        LocaleLoader.getString("Perks.XP.Desc")));
                return;
            }
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
            DecimalFormat percent = new DecimalFormat("##0.00%",
                    DecimalFormatSymbols.getInstance(Locale.US));
            player.sendMessage(PERK_PREFIX + LocaleLoader.getString("Effects.Template",
                    LocaleLoader.getString("Perks.Cooldowns.Name"),
                    LocaleLoader.getString("Perks.Cooldowns.Desc",
                            percent.format(cooldownReduction))));
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
            player.sendMessage(PERK_PREFIX + LocaleLoader.getString("Effects.Template",
                    LocaleLoader.getString("Perks.ActivationTime.Name"),
                    LocaleLoader.getString("Perks.ActivationTime.Desc", perkAmount)));
        }
    }

    /**
     * Display "lucky" perks.
     *
     * @param player Target player
     */
    public static void displayLuckyPerks(Player player) {
        for (PrimarySkillType skill : PrimarySkillType.values()) {
            if (Permissions.lucky(player, skill)) {
                player.sendMessage(PERK_PREFIX + LocaleLoader.getString("Effects.Template",
                        LocaleLoader.getString("Perks.Lucky.Name"),
                        LocaleLoader.getString("Perks.Lucky.Desc.Login")));
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
