package com.gmail.nossr50.skills;

import java.text.DecimalFormat;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.commands.CommandHelper;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Page;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.Users;

public abstract class SkillCommand implements CommandExecutor {
    private SkillType skill;
    private String skillString;
    private String permission;

    protected Player player;
    protected PlayerProfile profile;
    protected float skillValue;
    protected boolean isLucky;
    protected boolean hasEndurance;

    protected DecimalFormat percent = new DecimalFormat("##0.00%");

    public SkillCommand(SkillType skill) {
        this.skill = skill;
        this.skillString = Misc.getCapitalized(skill.toString());
        this.permission = "mcmmo.skills." + skillString.toLowerCase();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (CommandHelper.noConsoleUsage(sender)) {
            return true;
        }

        if (CommandHelper.noCommandPermissions(sender, permission)) {
            return true;
        }

        player = (Player) sender;
        profile = Users.getProfile(player);

        if (profile == null) {
            sender.sendMessage(LocaleLoader.getString("Commands.DoesNotExist"));
            return true;
        }

        skillValue = profile.getSkillLevel(skill);
        isLucky = Permissions.lucky(player, skill);
        hasEndurance = (Permissions.activationTwelve(player) || Permissions.activationEight(player) || Permissions.activationFour(player));

        dataCalculations();
        permissionsCheck();

        player.sendMessage(LocaleLoader.getString("Skills.Header", new Object[] { LocaleLoader.getString(skillString + ".SkillName") }));
        player.sendMessage(LocaleLoader.getString("Commands.XPGain", new Object[] { LocaleLoader.getString("Commands.XPGain." + skillString) }));
        player.sendMessage(LocaleLoader.getString("Effects.Level", new Object[] { profile.getSkillLevel(skill), profile.getSkillXpLevel(skill), profile.getXpToLevel(skill) }));

        if (effectsHeaderPermissions()) {
            player.sendMessage(LocaleLoader.getString("Skills.Header", new Object[] { LocaleLoader.getString("Effects.Effects") }));
        }

        effectsDisplay();

        if (statsHeaderPermissions()) {
            player.sendMessage(LocaleLoader.getString("Skills.Header", new Object[] { LocaleLoader.getString("Commands.Stats.Self") }));
        }

        statsDisplay();

        Page.grabGuidePageForSkill(skill, player, args);

        return true;
    }

    protected String calculateRank(int maxLevel, int rankChangeLevel) {
        if (skillValue >= maxLevel) {
            return String.valueOf(maxLevel / rankChangeLevel);
        }

        return String.valueOf((int) (skillValue / rankChangeLevel));
    }

    protected String[] calculateAbilityDisplayValues(double chance) {
        if (isLucky) {
            double luckyChance = chance * 1.3333D;

            if (luckyChance >= 100D) {
                return new String[] { percent.format(chance / 100.0D), percent.format(1.0D) };
            }

            return new String[] { percent.format(chance / 100.0D), percent.format(luckyChance / 100.0D) };
        }

        return new String[] { percent.format(chance / 100.0D), null };
    }

    protected String[] calculateAbilityDisplayValues(int maxBonusLevel, double maxChance) {
        double abilityChance;

        if (skillValue >= maxBonusLevel) {
            abilityChance = maxChance;
        }
        else {
            abilityChance = (maxChance / maxBonusLevel) * skillValue;
        }

        if (isLucky) {
            double luckyChance = abilityChance * 1.3333D;

            if (luckyChance >= 100D) {
                return new String[] { percent.format(abilityChance / 100.0D), percent.format(1.0D) };
            }

            return new String[] { percent.format(abilityChance / 100.0D), percent.format(luckyChance / 100.0D) };
        }

        return new String[] { percent.format(abilityChance / 100.0D), null };
    }

    protected String[] calculateLengthDisplayValues() {
        int maxLength = skill.getAbility().getMaxTicks();
        int length = 2 + (int) (skillValue / Misc.abilityLengthIncreaseLevel);
        int enduranceLength = 0;

        if (Permissions.activationTwelve(player)) {
            enduranceLength = length + 12;
        }
        else if (Permissions.activationEight(player)) {
            enduranceLength = length + 8;
        }
        else if (Permissions.activationFour(player)) {
            enduranceLength = length + 4;
        }

        if (maxLength != 0) {
            if (length > maxLength) {
                length = maxLength;
            }

            if (enduranceLength > maxLength) {
                enduranceLength = maxLength;
            }
        }

        return new String[] { String.valueOf(length), String.valueOf(enduranceLength) };
    }

    protected void luckyEffectsDisplay() {
        if (isLucky) {
            String perkPrefix = LocaleLoader.getString("MOTD.PerksPrefix");
            player.sendMessage(perkPrefix + LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Perks.lucky.name"), LocaleLoader.getString("Perks.lucky.desc", new Object[] { Skills.localizeSkillName(skill) }) }));
        }
    }

    protected abstract void dataCalculations();

    protected abstract void permissionsCheck();

    protected abstract boolean effectsHeaderPermissions();

    protected abstract void effectsDisplay();

    protected abstract boolean statsHeaderPermissions();

    protected abstract void statsDisplay();
}
