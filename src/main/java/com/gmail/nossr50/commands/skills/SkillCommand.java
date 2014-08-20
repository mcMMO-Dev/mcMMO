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
import com.gmail.nossr50.datatypes.skills.SecondaryAbility;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.skills.child.FamilyTree;
import com.gmail.nossr50.util.Motd;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.StringUtils;
import com.gmail.nossr50.util.commands.CommandUtils;
import com.gmail.nossr50.util.player.UserManager;
import com.gmail.nossr50.util.scoreboards.ScoreboardManager;
import com.gmail.nossr50.util.skills.PerksUtils;

import com.google.common.collect.ImmutableList;

public abstract class SkillCommand implements TabExecutor {
    protected SkillType skill;
    private String skillName;

    protected DecimalFormat percent = new DecimalFormat("##0.00%");
    protected DecimalFormat decimal = new DecimalFormat("##0.00");

    private CommandExecutor skillGuideCommand;

    public SkillCommand(SkillType skill) {
        this.skill = skill;
        skillName = skill.getName();
        skillGuideCommand = new SkillGuideCommand(skill);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (CommandUtils.noConsoleUsage(sender)) {
            return true;
        }

        if (!CommandUtils.hasPlayerDataKey(sender)) {
            return true;
        }

        switch (args.length) {
            case 0:
                Player player = (Player) sender;
                McMMOPlayer mcMMOPlayer = UserManager.getPlayer(player);

                boolean isLucky = Permissions.lucky(player, skill);
                boolean hasEndurance = (PerksUtils.handleActivationPerks(player, 0, 0) != 0);
                float skillValue = mcMMOPlayer.getSkillLevel(skill);

                permissionsCheck(player);
                dataCalculations(player, skillValue, isLucky);

                if (Config.getInstance().getSkillUseBoard()) {
                    ScoreboardManager.enablePlayerSkillScoreboard(player, skill);
                }

                if (!skill.isChildSkill()) {
                    player.sendMessage(LocaleLoader.getString("Skills.Header", skillName));
                    player.sendMessage(LocaleLoader.getString("Commands.XPGain", LocaleLoader.getString("Commands.XPGain." + StringUtils.getCapitalized(skill.toString()))));
                    player.sendMessage(LocaleLoader.getString("Effects.Level", (int) skillValue, mcMMOPlayer.getSkillXpLevel(skill), mcMMOPlayer.getXpToLevel(skill)));
                }
                else {
                    player.sendMessage(LocaleLoader.getString("Skills.Header", skillName + " " + LocaleLoader.getString("Skills.Child")));
                    player.sendMessage(LocaleLoader.getString("Commands.XPGain", LocaleLoader.getString("Commands.XPGain.Child")));
                    player.sendMessage(LocaleLoader.getString("Effects.Child", (int) skillValue));

                    player.sendMessage(LocaleLoader.getString("Skills.Header", LocaleLoader.getString("Skills.Parents")));
                    Set<SkillType> parents = FamilyTree.getParents(skill);

                    for (SkillType parent : parents) {
                        player.sendMessage(parent.getName() + " - " + LocaleLoader.getString("Effects.Level", mcMMOPlayer.getSkillLevel(parent), mcMMOPlayer.getSkillXpLevel(parent), mcMMOPlayer.getXpToLevel(parent)));
                    }
                }

                List<String> effectMessages = effectsDisplay();

                if (!effectMessages.isEmpty()) {
                    player.sendMessage(LocaleLoader.getString("Skills.Header", LocaleLoader.getString("Effects.Effects")));

                    if (isLucky) {
                        player.sendMessage(Motd.PERK_PREFIX + LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Perks.Lucky.Name"), LocaleLoader.getString("Perks.Lucky.Desc", skillName)));
                    }

                    for (String message : effectMessages) {
                        player.sendMessage(message);
                    }
                }

                List<String> statsMessages = statsDisplay(player, skillValue, hasEndurance, isLucky);

                if (!statsMessages.isEmpty()) {
                    player.sendMessage(LocaleLoader.getString("Skills.Header", LocaleLoader.getString("Commands.Stats.Self")));

                    for (String message : statsMessages) {
                        player.sendMessage(message);
                    }
                }

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

    protected int calculateRank(float skillValue, int maxLevel, int rankChangeLevel) {
        return Math.min((int) skillValue, maxLevel) / rankChangeLevel;
    }

    protected String[] calculateAbilityDisplayValues(double chance, boolean isLucky) {
        String[] displayValues = new String[2];

        displayValues[0] = percent.format(Math.min(chance, 100.0D) / 100.0D);
        displayValues[1] = isLucky ? percent.format(Math.min(chance * 1.3333D, 100.0D) / 100.0D) : null;

        return displayValues;
    }

    protected String[] calculateAbilityDisplayValues(float skillValue, SecondaryAbility skillAbility, boolean isLucky) {
        int maxBonusLevel = AdvancedConfig.getInstance().getMaxBonusLevel(skillAbility);

        return calculateAbilityDisplayValues((AdvancedConfig.getInstance().getMaxChance(skillAbility) / maxBonusLevel) * Math.min(skillValue, maxBonusLevel), isLucky);
    }

    protected String[] calculateLengthDisplayValues(Player player, float skillValue) {
        int maxLength = skill.getAbility().getMaxLength();
        int length = 2 + (int) (skillValue / AdvancedConfig.getInstance().getAbilityLength());
        int enduranceLength = PerksUtils.handleActivationPerks(player, length, maxLength);

        if (maxLength != 0) {
            length = Math.min(length, maxLength);
        }

        return new String[] { String.valueOf(length), String.valueOf(enduranceLength) };
    }

    protected abstract void dataCalculations(Player player, float skillValue, boolean isLucky);

    protected abstract void permissionsCheck(Player player);

    protected abstract List<String> effectsDisplay();

    protected abstract List<String> statsDisplay(Player player, float skillValue, boolean hasEndurance, boolean isLucky);
}
