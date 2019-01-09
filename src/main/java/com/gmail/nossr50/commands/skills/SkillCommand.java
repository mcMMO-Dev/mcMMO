package com.gmail.nossr50.commands.skills;

import java.text.DecimalFormat;
import java.util.List;

import com.gmail.nossr50.datatypes.skills.PrimarySkill;

import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.TextComponentFactory;
import com.gmail.nossr50.util.StringUtils;
import com.gmail.nossr50.util.commands.CommandUtils;
import com.gmail.nossr50.util.player.UserManager;
import com.gmail.nossr50.util.scoreboards.ScoreboardManager;
import com.gmail.nossr50.util.skills.PerksUtils;

import com.google.common.collect.ImmutableList;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

public abstract class SkillCommand implements TabExecutor {
    protected PrimarySkill skill;
    private String skillName;

    protected DecimalFormat percent = new DecimalFormat("##0.00%");
    protected DecimalFormat decimal = new DecimalFormat("##0.00");

    private CommandExecutor skillGuideCommand;

    public SkillCommand(PrimarySkill skill) {
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

                sendSkillCommandHeader(player, mcMMOPlayer, (int) skillValue);

                //Make JSON text components
                List<TextComponent> subskillTextComponents = getTextComponents(player);

                //Subskills Header
                player.sendMessage(LocaleLoader.getString("Skills.Overhaul.Header", LocaleLoader.getString("Effects.SubSkills.Overhaul")));

                //Send JSON text components

                TextComponentFactory.sendPlayerSubSkillList(player, subskillTextComponents);

                /*for(TextComponent tc : subskillTextComponents)
                {
                    player.spigot().sendMessage(new TextComponent[]{tc, new TextComponent(": TESTING")});
                }*/

                //Stats
                getStatMessages(player, isLucky, hasEndurance, skillValue);

                ChatColor hd1 = ChatColor.DARK_AQUA;
                ChatColor c1 = ChatColor.GOLD;
                ChatColor c2 = ChatColor.RED;

                //Header
                player.sendMessage(hd1+"[]=====[]"+c1+" mcMMO "+c2+"Overhaul"+c1+" Era "+hd1+"[]=====[]");
                //Link Header
                TextComponentFactory.sendPlayerUrlHeader(player);

                return true;

            default:
                return skillGuideCommand.onCommand(sender, command, label, args);
        }
    }

    private void getStatMessages(Player player, boolean isLucky, boolean hasEndurance, float skillValue) {
        List<String> statsMessages = statsDisplay(player, skillValue, hasEndurance, isLucky);

        if (!statsMessages.isEmpty()) {
            player.sendMessage(LocaleLoader.getString("Skills.Overhaul.Header", LocaleLoader.getString("Commands.Stats.Self.Overhaul")));

            for (String message : statsMessages) {
                player.sendMessage(message);
            }
        }

        player.sendMessage(LocaleLoader.getString("Guides.Available", skillName, skillName.toLowerCase()));
    }

    private void sendSkillCommandHeader(Player player, McMMOPlayer mcMMOPlayer, int skillValue) {

        if(!skill.isChildSkill())
        {
            ChatColor hd1 = ChatColor.DARK_AQUA;
            ChatColor c1 = ChatColor.GOLD;
            ChatColor c2 = ChatColor.RED;

            player.sendMessage(hd1+"[]=====[]"+c1+" "+skillName+" "+hd1+"[]=====[]");

            //XP GAIN METHOD
            player.sendMessage(LocaleLoader.getString("Commands.XPGain.Overhaul", LocaleLoader.getString("Commands.XPGain." + StringUtils.getCapitalized(skill.toString()))));

            //LEVEL
            player.sendMessage(LocaleLoader.getString("Effects.Level.Overhaul", skillValue, mcMMOPlayer.getSkillXpLevel(skill), mcMMOPlayer.getXpToLevel(skill)));

        } else {
            ChatColor hd1 = ChatColor.DARK_AQUA;
            ChatColor c1 = ChatColor.GOLD;
            ChatColor c2 = ChatColor.DARK_PURPLE;
            //Header
            player.sendMessage(hd1+"[]=====[]"+c1+" mcMMO "+c2+"Overhaul"+c1+" Era "+hd1+"[]=====[]");
            //Link Header
            TextComponentFactory.sendPlayerUrlHeader(player);
            player.sendMessage(hd1+"[]=====[]"+c1+" "+skillName+" "+hd1+"[]=====[]");

            //XP GAIN METHOD
            player.sendMessage(LocaleLoader.getString("Commands.XPGain", LocaleLoader.getString("Commands.XPGain." + StringUtils.getCapitalized(skill.toString()))));

            //LEVEL
            player.sendMessage(LocaleLoader.getString("Effects.Level", skillValue, mcMMOPlayer.getSkillXpLevel(skill), mcMMOPlayer.getXpToLevel(skill)));

        }

        /*
        if (!skill.isChildSkill()) {
            player.sendMessage(LocaleLoader.getString("Skills.Header", skillName));
            player.sendMessage(LocaleLoader.getString("Commands.XPGain", LocaleLoader.getString("Commands.XPGain." + StringUtils.getCapitalized(skill.toString()))));
            player.sendMessage(LocaleLoader.getString("Effects.Level", skillValue, mcMMOPlayer.getSkillXpLevel(skill), mcMMOPlayer.getXpToLevel(skill)));
        } else {
            player.sendMessage(LocaleLoader.getString("Skills.Header", skillName + " " + LocaleLoader.getString("Skills.Child")));
            player.sendMessage(LocaleLoader.getString("Commands.XPGain", LocaleLoader.getString("Commands.XPGain.Child")));
            player.sendMessage(LocaleLoader.getString("Effects.Child", skillValue));

            player.sendMessage(LocaleLoader.getString("Skills.Header", LocaleLoader.getString("Skills.Parents")));
            Set<PrimarySkill> parents = FamilyTree.getParents(skill);

            for (PrimarySkill parent : parents) {
                player.sendMessage(parent.getName() + " - " + LocaleLoader.getString("Effects.Level", mcMMOPlayer.getSkillLevel(parent), mcMMOPlayer.getSkillXpLevel(parent), mcMMOPlayer.getXpToLevel(parent)));
            }
        }
        */
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

    protected String[] calculateAbilityDisplayValues(float skillValue, SubSkillType subSkill, boolean isLucky) {
        int maxBonusLevel = AdvancedConfig.getInstance().getMaxBonusLevel(subSkill);

        return calculateAbilityDisplayValues((AdvancedConfig.getInstance().getMaxChance(subSkill) / maxBonusLevel) * Math.min(skillValue, maxBonusLevel), isLucky);
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

    protected abstract List<TextComponent> getTextComponents(Player player);
}
