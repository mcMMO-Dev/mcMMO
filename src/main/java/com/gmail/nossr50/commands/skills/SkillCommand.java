package com.gmail.nossr50.commands.skills;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Set;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.skills.child.FamilyTree;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.StringUtils;
import com.gmail.nossr50.util.commands.CommandUtils;
import com.gmail.nossr50.util.player.UserManager;
import com.gmail.nossr50.util.scoreboards.ScoreboardManager;
import com.gmail.nossr50.util.skills.PerksUtils;
import com.gmail.nossr50.util.skills.SkillUtils;

import com.google.common.collect.ImmutableList;

public abstract class SkillCommand implements TabExecutor {
    protected SkillType skill;
    protected String skillName;

    protected Player player;
    protected PlayerProfile profile;
    protected McMMOPlayer mcMMOPlayer;

    protected float skillValue;
    protected boolean isLucky;
    protected boolean hasEndurance;

    protected DecimalFormat percent = new DecimalFormat("##0.00%");
    protected DecimalFormat decimal = new DecimalFormat("##0.00");

    private CommandExecutor skillGuideCommand;

    public SkillCommand(SkillType skill) {
        this.skill = skill;
        skillName = SkillUtils.getSkillName(skill);
        skillGuideCommand = new SkillGuideCommand(skill);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (CommandUtils.noConsoleUsage(sender)) {
            return true;
        }

        mcMMOPlayer = UserManager.getPlayer(sender.getName());
        player = mcMMOPlayer.getPlayer();

        switch (args.length) {
            case 0:
                profile = mcMMOPlayer.getProfile();

                skillValue = profile.getSkillLevel(skill);
                isLucky = Permissions.lucky(sender, skill);
                hasEndurance = (PerksUtils.handleActivationPerks(player, 0, 0) != 0);

                permissionsCheck();
                dataCalculations();

                if (!skill.isChildSkill()) {
                    player.sendMessage(LocaleLoader.getString("Skills.Header", skillName));
                    player.sendMessage(LocaleLoader.getString("Commands.XPGain", LocaleLoader.getString("Commands.XPGain." + StringUtils.getCapitalized(skill.toString()))));

                    if (Config.getInstance().getSkillScoreboardEnabled()) {
                        ScoreboardManager.setupPlayerScoreboard(player.getName());
                        ScoreboardManager.enablePlayerSkillScoreboard(mcMMOPlayer, skill);
                    }
                    else {
                        player.sendMessage(LocaleLoader.getString("Effects.Level", (int) skillValue, profile.getSkillXpLevel(skill), profile.getXpToLevel(skill)));
                    }
                }
                else {
                    player.sendMessage(LocaleLoader.getString("Skills.Header", skillName + " " + LocaleLoader.getString("Skills.Child")));
                    player.sendMessage(LocaleLoader.getString("Commands.XPGain", LocaleLoader.getString("Commands.XPGain.Child")));
                    player.sendMessage(LocaleLoader.getString("Effects.Child", (int) skillValue));

                    player.sendMessage(LocaleLoader.getString("Skills.Header", LocaleLoader.getString("Skills.Parents")));
                    Set<SkillType> parents = FamilyTree.getParents(skill);

                    for (SkillType parent : parents) {
                        player.sendMessage(SkillUtils.getSkillName(parent) + " - " + LocaleLoader.getString("Effects.Level", profile.getSkillLevel(parent), profile.getSkillXpLevel(parent), profile.getXpToLevel(parent)));
                    }
                }

                if (effectsHeaderPermissions()) {
                    player.sendMessage(LocaleLoader.getString("Skills.Header", LocaleLoader.getString("Effects.Effects")));
                }

                effectsDisplay();

                if (statsHeaderPermissions()) {
                    player.sendMessage(LocaleLoader.getString("Skills.Header", LocaleLoader.getString("Commands.Stats.Self")));
                }

                statsDisplay();

                player.sendMessage(LocaleLoader.getString("Guides.Available", skillName, skillName.toLowerCase()));
                return true;

            default:
                return skillGuideCommand.onCommand(sender, command, label, args);
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        switch (args.length) {
            case 1:
                return ImmutableList.of("?");
            default:
                return ImmutableList.of();
        }
    }

    protected int calculateRank(int maxLevel, int rankChangeLevel) {
        return Math.min((int) skillValue, maxLevel) / rankChangeLevel;
    }

    protected String[] calculateAbilityDisplayValues(double chance) {
        String[] displayValues = new String[2];

        displayValues[0] = percent.format(Math.min(chance, 100.0D) / 100.0D);
        displayValues[1] = isLucky ? percent.format(Math.min(chance * 1.3333D, 100.0D) / 100.0D) : null;

        return displayValues;
    }

    protected String[] calculateAbilityDisplayValues(int maxBonusLevel, double maxChance) {
        return calculateAbilityDisplayValues((maxChance / maxBonusLevel) * Math.min(skillValue, maxBonusLevel));
    }

    protected String[] calculateLengthDisplayValues() {
        int maxLength = skill.getAbility().getMaxTicks();
        int length = 2 + (int) (skillValue / AdvancedConfig.getInstance().getAbilityLength());
        int enduranceLength = PerksUtils.handleActivationPerks(player, length, maxLength);

        if (maxLength != 0) {
            length = Math.min(length, maxLength);
        }

        return new String[] { String.valueOf(length), String.valueOf(enduranceLength) };
    }

    protected void luckyEffectsDisplay() {
        if (isLucky) {
            String perkPrefix = LocaleLoader.getString("MOTD.PerksPrefix");
            player.sendMessage(perkPrefix + LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Perks.lucky.name"), LocaleLoader.getString("Perks.lucky.desc", skillName)));
        }
    }

    protected abstract void dataCalculations();

    protected abstract void permissionsCheck();

    protected abstract boolean effectsHeaderPermissions();

    protected abstract void effectsDisplay();

    protected abstract boolean statsHeaderPermissions();

    protected abstract void statsDisplay();
}
