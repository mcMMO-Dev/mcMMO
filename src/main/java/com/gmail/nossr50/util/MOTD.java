package com.gmail.nossr50.util;

import org.bukkit.entity.Player;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.skills.SkillType;

public class MOTD {
    private Player player;
    private String perkPrefix = LocaleLoader.getString("MOTD.PerksPrefix");

    public MOTD(Player player) {
        this.player = player;
    }

    /**
     * Display version info.
     * @param version The plugin version
     */
    public void displayVersion(String version) {
        player.sendMessage(LocaleLoader.getString("MOTD.Version", new Object[] {version}));
    }

    /**
     * Display Hardcore Mode settings.
     */
    public void displayHardcoreSettings() {
        if (Config.getInstance().getHardcoreEnabled()) {
            if (Config.getInstance().getHardcoreVampirismEnabled()) {
                player.sendMessage(LocaleLoader.getString("MOTD.Hardcore.VampireOn"));
                player.sendMessage(LocaleLoader.getString("MOTD.Hardcore.Stats", new Object[] {Config.getInstance().getHardcoreDeathStatPenaltyPercentage()}));
                player.sendMessage(LocaleLoader.getString("MOTD.Vampire.Stats", new Object[] {Config.getInstance().getHardcoreVampirismStatLeechPercentage()}));
            }
            else {
                player.sendMessage(LocaleLoader.getString("MOTD.Hardcore.VampireOff"));
                player.sendMessage(LocaleLoader.getString("MOTD.Hardcore.Stats", new Object[] {Config.getInstance().getHardcoreDeathStatPenaltyPercentage()}));
            }
        }
    }

    /**
     * Display XP perks.
     */
    public void displayXpPerks() {
        if (Permissions.xpQuadruple(player)) {
            player.sendMessage(perkPrefix + LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Perks.xp.name"), LocaleLoader.getString("Perks.xp.desc", new Object[] { 4 }) }));
        }
        else if (Permissions.xpTriple(player)) {
            player.sendMessage(perkPrefix + LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Perks.xp.name"), LocaleLoader.getString("Perks.xp.desc", new Object[] { 3 }) }));
        }
        else if (Permissions.xpDoubleAndOneHalf(player)) {
            player.sendMessage(perkPrefix + LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Perks.xp.name"), LocaleLoader.getString("Perks.xp.desc", new Object[] { 2.5 }) }));
        }
        else if (Permissions.xpDouble(player)) {
            player.sendMessage(perkPrefix + LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Perks.xp.name"), LocaleLoader.getString("Perks.xp.desc", new Object[] { 2 }) }));
        }
        else if (Permissions.xpOneAndOneHalf(player)) {
            player.sendMessage(perkPrefix + LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Perks.xp.name"), LocaleLoader.getString("Perks.xp.desc", new Object[] { 1.5 }) }));
        }
    }

    /**
     * Display cooldown perks.
     */
    public void displayCooldownPerks() {
        if (Permissions.cooldownsHalved(player)) {
            player.sendMessage(perkPrefix + LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Perks.cooldowns.name"), LocaleLoader.getString("Perks.cooldowns.desc", new Object[] { "1/2" }) }));
        }
        else if (Permissions.cooldownsThirded(player)) {
            player.sendMessage(perkPrefix + LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Perks.cooldowns.name"), LocaleLoader.getString("Perks.cooldowns.desc", new Object[] { "1/3" }) }));
        }
        else if (Permissions.cooldownsQuartered(player)) {
            player.sendMessage(perkPrefix + LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Perks.cooldowns.name"), LocaleLoader.getString("Perks.cooldowns.desc", new Object[] { "1/4" }) }));
        }
    }

    /**
     * Display activiation perks.
     */
    public void displayActivationPerks() {
        if (Permissions.activationTwelve(player)) {
            player.sendMessage(perkPrefix + LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Perks.activationtime.name"), LocaleLoader.getString("Perks.activationtime.desc", new Object[] { 12 }) }));
        }
        else if (Permissions.activationEight(player)) {
            player.sendMessage(perkPrefix + LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Perks.activationtime.name"), LocaleLoader.getString("Perks.activationtime.desc", new Object[] { 8 }) }));
        }
        else if (Permissions.activationFour(player)) {
            player.sendMessage(perkPrefix + LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Perks.activationtime.name"), LocaleLoader.getString("Perks.activationtime.desc", new Object[] { 4 }) }));
        }
    }

    /**
     * Display "lucky" perks.
     */
    public void displayLuckyPerks() {
        for (SkillType skill : SkillType.values()) {
            if (Permissions.lucky(player, skill)) {
                player.sendMessage(perkPrefix + LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Perks.lucky.name"), LocaleLoader.getString("Perks.lucky.desc.login") }));
                return;
            }
        }
    }

    /**
     * Display website info.
     * @param website The plugin website
     */
    public void displayWebsite(String website) {
        player.sendMessage(LocaleLoader.getString("MOTD.Website", new Object[] {website}));
    }
}
