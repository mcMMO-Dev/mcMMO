package com.gmail.nossr50.util;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.mcMMO;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;

import java.text.DecimalFormat;

public final class MessageOfTheDayUtils {
    private final mcMMO pluginRef;
    public final String PERK_PREFIX;
    private final PluginDescriptionFile pluginDescription;

    public MessageOfTheDayUtils(mcMMO pluginRef) {
        this.pluginRef = pluginRef;
        PERK_PREFIX = pluginRef.getLocaleManager().getString("MOTD.PerksPrefix") + " ";
        pluginDescription = pluginRef.getDescription();
    }

    public void displayAll(Player player) {
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
     * @param player  Target player
     * @param version Plugin version
     */
    public void displayVersion(Player player, String version) {
        if (pluginRef.getPermissionTools().showversion(player)) {
            player.sendMessage(pluginRef.getLocaleManager().getString("MOTD.Version.Overhaul", version));
        }
    }

    /**
     * Display Hardcore Mode settings.
     *
     * @param player Target player
     */
    public void displayHardcoreSettings(Player player) {
        boolean deathStatLossEnabled = pluginRef.getHardcoreManager().isStatLossEnabled();
        boolean vampirismEnabled = pluginRef.getHardcoreManager().isVampirismEnabled();

        if (!deathStatLossEnabled && !vampirismEnabled) {
            return;
        }

        String statLossInfo = "";
        String vampirismInfo = "";
        String seperator = "";

        if (deathStatLossEnabled) {
            statLossInfo = pluginRef.getLocaleManager().getString("Hardcore.DeathStatLoss.Name");
        }

        if (vampirismEnabled) {
            vampirismInfo = pluginRef.getLocaleManager().getString("Hardcore.Vampirism.Name");
        }

        if (deathStatLossEnabled && vampirismEnabled) {
            seperator = " & ";
        }

        player.sendMessage(pluginRef.getLocaleManager().getString("MOTD.Hardcore.Enabled", statLossInfo + seperator + vampirismInfo));

        if (deathStatLossEnabled) {
            player.sendMessage(pluginRef.getLocaleManager().getString("MOTD.Hardcore.DeathStatLoss.Stats", pluginRef.getConfigManager().getConfigHardcore().getDeathPenalty().getPenaltyPercentage()));
        }

        if (vampirismEnabled) {
            player.sendMessage(pluginRef.getLocaleManager().getString("MOTD.Hardcore.Vampirism.Stats", pluginRef.getConfigManager().getConfigHardcore().getVampirism().getPenaltyPercentage()));
        }
    }

    /**
     * Display XP perks.
     *
     * @param player Target player
     */
    public void displayXpPerks(Player player) {
        for (PrimarySkillType skill : PrimarySkillType.values()) {
//            if (pluginRef.getPerkUtils().handleXpPerks(player, 1, skill) > 1) {
//                player.sendMessage(PERK_PREFIX + pluginRef.getLocaleManager().getString("Effects.Template", pluginRef.getLocaleManager().getString("Perks.XP.Name"), pluginRef.getLocaleManager().getString("Perks.XP.Desc")));
//                return;
//            }
        }
    }

    /**
     * Display cooldown perks.
     *
     * @param player Target player
     */
    public void displayCooldownPerks(Player player) {
        double cooldownReduction = 1 - (pluginRef.getPerkUtils().handleCooldownPerks(player, 12) / 12.0);

        if (cooldownReduction > 0.0) {
            DecimalFormat percent = new DecimalFormat("##0.00%");
            player.sendMessage(PERK_PREFIX + pluginRef.getLocaleManager().getString("Effects.Template", pluginRef.getLocaleManager().getString("Perks.Cooldowns.Name"), pluginRef.getLocaleManager().getString("Perks.Cooldowns.Desc", percent.format(cooldownReduction))));
        }
    }

    /**
     * Display activiation perks.
     *
     * @param player Target player
     */
    public void displayActivationPerks(Player player) {
        int perkAmount = pluginRef.getSkillTools().getEnduranceLength(player);

        if (perkAmount > 0) {
            player.sendMessage(PERK_PREFIX + pluginRef.getLocaleManager().getString("Effects.Template", pluginRef.getLocaleManager().getString("Perks.ActivationTime.Name"), pluginRef.getLocaleManager().getString("Perks.ActivationTime.Desc", perkAmount)));
        }
    }

    /**
     * Display "lucky" perks.
     *
     * @param player Target player
     */
    public void displayLuckyPerks(Player player) {
        for (PrimarySkillType skill : PrimarySkillType.values()) {
            if (pluginRef.getPermissionTools().lucky(player, skill)) {
                player.sendMessage(PERK_PREFIX + pluginRef.getLocaleManager().getString("Effects.Template", pluginRef.getLocaleManager().getString("Perks.Lucky.Name"), pluginRef.getLocaleManager().getString("Perks.Lucky.Desc.Login")));
                return;
            }
        }
    }

    /**
     * Display website info.
     *
     * @param player  Target player
     * @param website Plugin website
     */
    public void displayWebsite(Player player, String website) {
        player.sendMessage(pluginRef.getLocaleManager().getString("MOTD.Website", website));
    }
}
