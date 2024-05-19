package com.gmail.nossr50.commands.skills;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.commands.CommandUtils;
import com.gmail.nossr50.util.player.NotificationManager;
import com.gmail.nossr50.util.player.UserManager;
import com.gmail.nossr50.util.scoreboards.ScoreboardManager;
import com.gmail.nossr50.util.skills.PerksUtils;
import com.gmail.nossr50.util.skills.SkillTools;
import com.gmail.nossr50.util.text.StringUtils;
import com.gmail.nossr50.util.text.TextComponentFactory;
import com.google.common.collect.ImmutableList;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public abstract class SkillCommand implements TabExecutor {
    protected PrimarySkillType skill;

    protected DecimalFormat percent = new DecimalFormat("##0.00%");
    protected DecimalFormat decimal = new DecimalFormat("##0.00");
    protected McMMOPlayer mmoPlayer;

    private final CommandExecutor skillGuideCommand;

    public SkillCommand(PrimarySkillType skill) {
        this.skill = skill;
        skillGuideCommand = new SkillGuideCommand(skill);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (CommandUtils.noConsoleUsage(sender)) {
            return true;
        }

        if (!CommandUtils.hasPlayerDataKey(sender)) {
            return true;
        }

        Player player = (Player) sender;
        mmoPlayer = UserManager.getPlayer(player);

        if (mmoPlayer == null) {
            sender.sendMessage(LocaleLoader.getString("Profile.PendingLoad"));
            return true;
        }

        if (args.length == 0) {
            boolean isLucky = Permissions.lucky(player, skill);
            boolean hasEndurance = PerksUtils.handleActivationPerks(player, 0, 0) != 0;
            float skillValue = mmoPlayer.getSkillLevel(skill);

            //Send the players a few blank lines to make finding the top of the skill command easier
            if (mcMMO.p.getAdvancedConfig().doesSkillCommandSendBlankLines())
                for (int i = 0; i < 2; i++) {
                    player.sendMessage("");
                }

            permissionsCheck(player);
            dataCalculations(player, skillValue);

            sendSkillCommandHeader(mcMMO.p.getSkillTools().getLocalizedSkillName(skill),
                    player, mmoPlayer, (int) skillValue);

            //Make JSON text components
            List<Component> subskillTextComponents = getTextComponents(player);

            //Subskills Header
            player.sendMessage(LocaleLoader.getString("Skills.Overhaul.Header", LocaleLoader.getString("Effects.SubSkills.Overhaul")));

            //Send JSON text components

            TextComponentFactory.sendPlayerSubSkillList(player, subskillTextComponents);

                /*for(TextComponent tc : subskillTextComponents) {
                    player.spigot().sendMessage(new TextComponent[]{tc, new TextComponent(": TESTING")});
                }*/

            //Stats
            getStatMessages(player, isLucky, hasEndurance, skillValue);

            //Header


            //Link Header
            if (mcMMO.p.getGeneralConfig().getUrlLinksEnabled()) {
                player.sendMessage(LocaleLoader.getString("Overhaul.mcMMO.Header"));
                TextComponentFactory.sendPlayerUrlHeader(player);
            }


            if (mcMMO.p.getGeneralConfig().getScoreboardsEnabled() && mcMMO.p.getGeneralConfig().getSkillUseBoard()) {
                ScoreboardManager.enablePlayerSkillScoreboard(player, skill);
            }

            return true;
        } else if ("keep".equals(args[0].toLowerCase())) {
            if (!mcMMO.p.getGeneralConfig().getAllowKeepBoard()
                    || !mcMMO.p.getGeneralConfig().getScoreboardsEnabled()
                    || !mcMMO.p.getGeneralConfig().getSkillUseBoard()) {
                sender.sendMessage(LocaleLoader.getString("Commands.Disabled"));
                return true;
            }

            ScoreboardManager.enablePlayerSkillScoreboard(player, skill);
            ScoreboardManager.keepBoard(sender.getName());
            sender.sendMessage(LocaleLoader.getString("Commands.Scoreboard.Keep"));
            return true;
        }

        return skillGuideCommand.onCommand(sender, command, label, args);
    }

    private void getStatMessages(Player player, boolean isLucky, boolean hasEndurance, float skillValue) {
        List<String> statsMessages = statsDisplay(player, skillValue, hasEndurance, isLucky);

        if (!statsMessages.isEmpty()) {
            player.sendMessage(LocaleLoader.getString("Skills.Overhaul.Header", LocaleLoader.getString("Commands.Stats.Self.Overhaul")));

            for (String message : statsMessages) {
                player.sendMessage(message);
            }
        }

        final String skillName = mcMMO.p.getSkillTools().getLocalizedSkillName(skill);
        player.sendMessage(LocaleLoader.getString("Guides.Available",
                skillName,
                skillName.toLowerCase(Locale.ENGLISH)));
    }

    private void sendSkillCommandHeader(String skillName, Player player, McMMOPlayer mcMMOPlayer, int skillValue) {
        // send header
        player.sendMessage(LocaleLoader.getString("Skills.Overhaul.Header", skillName));

        if (!SkillTools.isChildSkill(skill)) {
            /*
             * NON-CHILD SKILLS
             */

            //XP GAIN METHOD
            player.sendMessage(LocaleLoader.getString("Commands.XPGain.Overhaul", LocaleLoader.getString("Commands.XPGain." + StringUtils.getCapitalized(skill.toString()))));

            //LEVEL
            player.sendMessage(LocaleLoader.getString("Effects.Level.Overhaul", skillValue, mcMMOPlayer.getSkillXpLevel(skill), mcMMOPlayer.getXpToLevel(skill)));

        } else {
            /*
             * CHILD SKILLS
             */


            var parents = mcMMO.p.getSkillTools().getChildSkillParents(skill);

            //TODO: Add JSON here
            /*player.sendMessage(parent.getName() + " - " + LocaleLoader.getString("Effects.Level.Overhaul", mcMMOPlayer.getSkillLevel(parent), mcMMOPlayer.getSkillXpLevel(parent), mcMMOPlayer.getXpToLevel(parent)))*/
            ArrayList<PrimarySkillType> parentList = new ArrayList<>(parents);

            StringBuilder parentMessage = new StringBuilder();

            for(int i = 0; i < parentList.size(); i++) {
                if (i+1 < parentList.size()) {
                    parentMessage.append(LocaleLoader.getString("Effects.Child.ParentList", mcMMO.p.getSkillTools().getLocalizedSkillName(parentList.get(i)), mcMMOPlayer.getSkillLevel(parentList.get(i))));
                    parentMessage.append(ChatColor.GRAY).append(", ");
                } else {
                    parentMessage.append(LocaleLoader.getString("Effects.Child.ParentList", mcMMO.p.getSkillTools().getLocalizedSkillName(parentList.get(i)), mcMMOPlayer.getSkillLevel(parentList.get(i))));
                }
            }

            //XP GAIN METHOD
            player.sendMessage(LocaleLoader.getString("Commands.XPGain.Overhaul", LocaleLoader.getString("Commands.XPGain.Child")));

            player.sendMessage(LocaleLoader.getString("Effects.Child.Overhaul", skillValue, parentMessage.toString()));
            //LEVEL
            //player.sendMessage(LocaleLoader.getString("Effects.Child.Overhaul", skillValue, skillValue));

        }
        /*
        if (!SkillTools.isChildSkill(skill)) {
            player.sendMessage(LocaleLoader.getString("Skills.Header", skillName));
            player.sendMessage(LocaleLoader.getString("Commands.XPGain", LocaleLoader.getString("Commands.XPGain." + StringUtils.getCapitalized(skill.toString()))));
            player.sendMessage(LocaleLoader.getString("Effects.Level", skillValue, mcMMOPlayer.getSkillXpLevel(skill), mcMMOPlayer.getXpToLevel(skill)));
        } else {
            player.sendMessage(LocaleLoader.getString("Skills.Header", skillName + " " + LocaleLoader.getString("Skills.Child")));
            player.sendMessage(LocaleLoader.getString("Commands.XPGain", LocaleLoader.getString("Commands.XPGain.Child")));
            player.sendMessage(LocaleLoader.getString("Effects.Child", skillValue));

            player.sendMessage(LocaleLoader.getString("Skills.Header", LocaleLoader.getString("Skills.Parents")));
            Set<PrimarySkillType> parents = FamilyTree.getParents(skill);

            for (PrimarySkillType parent : parents) {
                player.sendMessage(parent.getName() + " - " + LocaleLoader.getString("Effects.Level", mcMMOPlayer.getSkillLevel(parent), mcMMOPlayer.getSkillXpLevel(parent), mcMMOPlayer.getXpToLevel(parent)));
            }
        }
        */
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        if (args.length == 1) {
            return ImmutableList.of("?", "keep");
        }
        return ImmutableList.of();
    }

    protected int calculateRank(float skillValue, int maxLevel, int rankChangeLevel) {
        return Math.min((int) skillValue, maxLevel) / rankChangeLevel;
    }

//    protected String[] getAbilityDisplayValues(SkillActivationType skillActivationType, Player player, SubSkillType subSkill) {
//        return RandomChanceUtil.calculateAbilityDisplayValues(skillActivationType, player, subSkill);
//    }

    protected String[] calculateLengthDisplayValues(Player player, float skillValue) {
        int maxLength = mcMMO.p.getSkillTools().getSuperAbilityMaxLength(mcMMO.p.getSkillTools().getSuperAbility(skill));
        int abilityLengthVar = mcMMO.p.getAdvancedConfig().getAbilityLength();
        int abilityLengthCap = mcMMO.p.getAdvancedConfig().getAbilityLengthCap();

        int length;

        if (abilityLengthCap <= 0) {
            length = 2 + (int) (skillValue / abilityLengthVar);
        } else {
            length = 2 + (int) (Math.min(abilityLengthCap, skillValue) / abilityLengthVar);
        }

        int enduranceLength = PerksUtils.handleActivationPerks(player, length, maxLength);

        if (maxLength != 0) {
            length = Math.min(length, maxLength);
        }

        return new String[] { String.valueOf(length), String.valueOf(enduranceLength) };
    }

    protected String getStatMessage(SubSkillType subSkillType, String... vars) {
        return getStatMessage(false, false, subSkillType, vars);
    }

    protected String getStatMessage(boolean isExtra, boolean isCustom, SubSkillType subSkillType, String... vars) {
        String templateKey = isCustom ? "Ability.Generic.Template.Custom" : "Ability.Generic.Template";
        String statDescriptionKey = !isExtra ? subSkillType.getLocaleKeyStatDescription() : subSkillType.getLocaleKeyStatExtraDescription();

        if (isCustom)
            return LocaleLoader.getString(templateKey, LocaleLoader.getString(statDescriptionKey, vars));
        else {
            String[] mergedList = NotificationManager.addItemToFirstPositionOfArray(LocaleLoader.getString(statDescriptionKey), vars);
            return LocaleLoader.getString(templateKey, mergedList);
        }
    }

    protected String getLimitBreakDescriptionParameter() {
        if (mcMMO.p.getAdvancedConfig().canApplyLimitBreakPVE()) {
            return "(PVP/PVE)";
        } else {
            return "(PVP)";
        }
    }

    protected abstract void dataCalculations(Player player, float skillValue);

    protected abstract void permissionsCheck(Player player);

    //protected abstract List<String> effectsDisplay();

    protected abstract List<String> statsDisplay(Player player, float skillValue, boolean hasEndurance, boolean isLucky);

    protected abstract List<Component> getTextComponents(Player player);

}
