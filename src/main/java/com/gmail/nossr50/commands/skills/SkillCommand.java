package com.gmail.nossr50.commands.skills;

import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.skills.child.FamilyTree;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.commands.CommandUtils;
import com.gmail.nossr50.util.player.NotificationManager;
import com.gmail.nossr50.util.random.RandomChanceUtil;
import com.gmail.nossr50.util.scoreboards.ScoreboardManager;
import com.gmail.nossr50.util.skills.PerksUtils;
import com.gmail.nossr50.util.skills.RankUtils;
import com.gmail.nossr50.util.skills.SkillActivationType;
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
import java.util.Set;

public abstract class SkillCommand implements TabExecutor {
    protected PrimarySkillType skill;
    private final String skillName;

    protected DecimalFormat percent = new DecimalFormat("##0.00%");
    protected DecimalFormat decimal = new DecimalFormat("##0.00");

    private final CommandExecutor skillGuideCommand;

    public SkillCommand(PrimarySkillType skill) {
        this.skill = skill;
        skillName = skill.getName();
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

        McMMOPlayer mmoPlayer = mcMMO.getUserManager().queryPlayer((Player) sender);

        if(mmoPlayer == null) {
            sender.sendMessage(LocaleLoader.getString("Profile.PendingLoad"));
            return true;
        }

        if (args.length == 0) {
            Player player = mmoPlayer.getPlayer();

            boolean isLucky = Permissions.lucky(player, skill);
            boolean hasEndurance = (PerksUtils.handleActivationPerks(player, 0, 0) != 0);
            float skillValue = mmoPlayer.getExperienceManager().getSkillLevel(skill);

            //Send the players a few blank lines to make finding the top of the skill command easier
            if (AdvancedConfig.getInstance().doesSkillCommandSendBlankLines())
                for (int i = 0; i < 2; i++) {
                    player.sendMessage("");
                }

            permissionsCheck(mmoPlayer);
            dataCalculations(mmoPlayer, skillValue);

            sendSkillCommandHeader(player, mmoPlayer, (int) skillValue);

            //Make JSON text components
            List<Component> subskillTextComponents = getTextComponents(mmoPlayer);

            //Subskills Header
            player.sendMessage(LocaleLoader.getString("Skills.Overhaul.Header", LocaleLoader.getString("Effects.SubSkills.Overhaul")));

            //Send JSON text components

            TextComponentFactory.sendPlayerSubSkillList(player, subskillTextComponents);

                /*for(TextComponent tc : subskillTextComponents)
                {
                    player.spigot().sendMessage(new TextComponent[]{tc, new TextComponent(": TESTING")});
                }*/

            //Stats
            sendStatMessages(mmoPlayer, isLucky, hasEndurance, skillValue);

            //Header


            //Link Header
            if (Config.getInstance().getUrlLinksEnabled()) {
                player.sendMessage(LocaleLoader.getString("Overhaul.mcMMO.Header"));
                TextComponentFactory.sendPlayerUrlHeader(player);
            }


            if (Config.getInstance().getScoreboardsEnabled() && Config.getInstance().getSkillUseBoard()) {
                ScoreboardManager.enablePlayerSkillScoreboard(player, skill);
            }

            return true;
        }
        return skillGuideCommand.onCommand(sender, command, label, args);
    }

    private void sendStatMessages(@NotNull McMMOPlayer mmoPlayer, boolean isLucky, boolean hasEndurance, float skillValue) {
        List<String> statsMessages = statsDisplay(mmoPlayer, skillValue, hasEndurance, isLucky);

        if (!statsMessages.isEmpty()) {
            mmoPlayer.getPlayer().sendMessage(LocaleLoader.getString("Skills.Overhaul.Header", LocaleLoader.getString("Commands.Stats.Self.Overhaul")));

            for (String message : statsMessages) {
                mmoPlayer.getPlayer().sendMessage(message);
            }
        }

        mmoPlayer.getPlayer().sendMessage(LocaleLoader.getString("Guides.Available", skillName, skillName.toLowerCase(Locale.ENGLISH)));
    }

    private void sendSkillCommandHeader(Player player, McMMOPlayer mmoPlayer, int skillValue) {
        ChatColor hd1 = ChatColor.DARK_AQUA;
        ChatColor c1 = ChatColor.GOLD;
        ChatColor c2 = ChatColor.RED;


        player.sendMessage(LocaleLoader.getString("Skills.Overhaul.Header", skillName));

        if(!skill.isChildSkill())
        {
            /*
             * NON-CHILD SKILLS
             */

            //XP GAIN METHOD
            player.sendMessage(LocaleLoader.getString("Commands.XPGain.Overhaul", LocaleLoader.getString("Commands.XPGain." + StringUtils.getCapitalized(skill.toString()))));

            //LEVEL
            player.sendMessage(LocaleLoader.getString("Effects.Level.Overhaul", skillValue, mmoPlayer.getExperienceManager().getSkillXpValue(skill), mmoPlayer.getExperienceManager().getXpToLevel(skill)));

        } else {
            /*
             * CHILD SKILLS
             */


            Set<PrimarySkillType> parents = FamilyTree.getParents(skill);

            //TODO: Add JSON here
            /*player.sendMessage(parent.getName() + " - " + LocaleLoader.getString("Effects.Level.Overhaul", mmoPlayer.getSkillLevel(parent), mmoPlayer.getSkillXpLevel(parent), mmoPlayer.getXpToLevel(parent)))*/
            ArrayList<PrimarySkillType> parentList = new ArrayList<>(parents);

            StringBuilder parentMessage = new StringBuilder();

            for(int i = 0; i < parentList.size(); i++)
            {
                if(i+1 < parentList.size())
                {
                    parentMessage.append(LocaleLoader.getString("Effects.Child.ParentList", parentList.get(i).getName(), mmoPlayer.getExperienceManager().getSkillLevel(parentList.get(i))));
                    parentMessage.append(ChatColor.GRAY).append(", ");
                } else {
                    parentMessage.append(LocaleLoader.getString("Effects.Child.ParentList", parentList.get(i).getName(), mmoPlayer.getExperienceManager().getSkillLevel(parentList.get(i))));
                }
            }

            //XP GAIN METHOD
            player.sendMessage(LocaleLoader.getString("Commands.XPGain.Overhaul", LocaleLoader.getString("Commands.XPGain.Child")));

            player.sendMessage(LocaleLoader.getString("Effects.Child.Overhaul", skillValue, parentMessage.toString()));
            //LEVEL
            //player.sendMessage(LocaleLoader.getString("Effects.Child.Overhaul", skillValue, skillValue));

        }
        /*
        if (!skill.isChildSkill()) {
            player.sendMessage(LocaleLoader.getString("Skills.Header", skillName));
            player.sendMessage(LocaleLoader.getString("Commands.XPGain", LocaleLoader.getString("Commands.XPGain." + StringUtils.getCapitalized(skill.toString()))));
            player.sendMessage(LocaleLoader.getString("Effects.Level", skillValue, mmoPlayer.getSkillXpLevel(skill), mmoPlayer.getXpToLevel(skill)));
        } else {
            player.sendMessage(LocaleLoader.getString("Skills.Header", skillName + " " + LocaleLoader.getString("Skills.Child")));
            player.sendMessage(LocaleLoader.getString("Commands.XPGain", LocaleLoader.getString("Commands.XPGain.Child")));
            player.sendMessage(LocaleLoader.getString("Effects.Child", skillValue));

            player.sendMessage(LocaleLoader.getString("Skills.Header", LocaleLoader.getString("Skills.Parents")));
            Set<PrimarySkillType> parents = FamilyTree.getParents(skill);

            for (PrimarySkillType parent : parents) {
                player.sendMessage(parent.getName() + " - " + LocaleLoader.getString("Effects.Level", mmoPlayer.getSkillLevel(parent), mmoPlayer.getSkillXpLevel(parent), mmoPlayer.getXpToLevel(parent)));
            }
        }
        */
    }

    @Override
    public @NotNull List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        if (args.length == 1) {
            return ImmutableList.of("?");
        }
        return ImmutableList.of();
    }

    protected int calculateRank(float skillValue, int maxLevel, int rankChangeLevel) {
        return Math.min((int) skillValue, maxLevel) / rankChangeLevel;
    }

    protected @NotNull String[] getAbilityDisplayValues(@NotNull SkillActivationType skillActivationType, @NotNull McMMOPlayer mmoPlayer, @NotNull SubSkillType subSkill) {
        return RandomChanceUtil.calculateAbilityDisplayValues(skillActivationType, mmoPlayer.getPlayer(), subSkill);
    }

    protected @NotNull String[] calculateLengthDisplayValues(@NotNull McMMOPlayer mmoPlayer, float skillValue) {
        int maxLength = skill.getSuperAbilityType().getMaxLength();
        int abilityLengthVar = AdvancedConfig.getInstance().getAbilityLength();
        int abilityLengthCap = AdvancedConfig.getInstance().getAbilityLengthCap();

        int length;

        if(abilityLengthCap <= 0)
        {
            length = 2 + (int) (skillValue / abilityLengthVar);
        }
        else {
            length = 2 + (int) (Math.min(abilityLengthCap, skillValue) / abilityLengthVar);
        }

        int enduranceLength = PerksUtils.handleActivationPerks(mmoPlayer.getPlayer(), length, maxLength);

        if (maxLength != 0) {
            length = Math.min(length, maxLength);
        }

        return new String[] { String.valueOf(length), String.valueOf(enduranceLength) };
    }

    protected @NotNull String getStatMessage(SubSkillType subSkillType, String... vars)
    {
        return getStatMessage(false, false, subSkillType, vars);
    }

    protected @NotNull String getStatMessage(boolean isExtra, boolean isCustom, SubSkillType subSkillType, String... vars)
    {
        String templateKey = isCustom ? "Ability.Generic.Template.Custom" : "Ability.Generic.Template";
        String statDescriptionKey = !isExtra ? subSkillType.getLocaleKeyStatDescription() : subSkillType.getLocaleKeyStatExtraDescription();

        if(isCustom)
            return LocaleLoader.getString(templateKey, LocaleLoader.getString(statDescriptionKey, vars));
        else
        {
            String[] mergedList = NotificationManager.addItemToFirstPositionOfArray(LocaleLoader.getString(statDescriptionKey), vars);
            return LocaleLoader.getString(templateKey, mergedList);
        }
    }

    protected @NotNull String getLimitBreakDescriptionParameter() {
        if(AdvancedConfig.getInstance().canApplyLimitBreakPVE()) {
            return "(PVP/PVE)";
        } else {
            return "(PVP)";
        }
    }

    protected abstract void dataCalculations(@NotNull McMMOPlayer mmoPlayer, float skillValue);

    protected abstract void permissionsCheck(@NotNull McMMOPlayer mmoPlayer);

    //protected abstract List<String> effectsDisplay();

    protected abstract @NotNull List<String> statsDisplay(@NotNull McMMOPlayer mmoPlayer, float skillValue, boolean hasEndurance, boolean isLucky);

    protected abstract @NotNull List<Component> getTextComponents(@NotNull McMMOPlayer player);

    /**
     * Checks if a player can use a skill
     * @param mmoPlayer target player
     * @param subSkillType target subskill
     * @return true if the player has permission and has the skill unlocked
     */
    protected boolean canUseSubskill(@NotNull McMMOPlayer mmoPlayer, SubSkillType subSkillType) {
        return Permissions.isSubSkillEnabled(mmoPlayer.getPlayer(), subSkillType) && RankUtils.hasUnlockedSubskill(mmoPlayer, subSkillType);
    }
}
