package com.gmail.nossr50.commands.skills;

import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.skills.CoreSkills;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.skills.child.FamilyTree;
import com.gmail.nossr50.util.Misc;
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
import com.neetgames.mcmmo.player.OnlineMMOPlayer;
import com.neetgames.mcmmo.skill.RootSkill;
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
    protected @NotNull RootSkill rootSkill;
    protected @NotNull RootSkill rootSkill;
    private final String skillName;

    protected DecimalFormat percent = new DecimalFormat("##0.00%");
    protected DecimalFormat decimal = new DecimalFormat("##0.00");

    private final CommandExecutor skillGuideCommand;

    public SkillCommand(@NotNull RootSkill rootSkill) {
        this.rootSkill = CoreSkills.getSkill(primarySkillType);
        this.primarySkillType = primarySkillType;
        skillName = rootSkill.getSkillName();
        skillGuideCommand = new SkillGuideCommand(rootSkill);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (CommandUtils.noConsoleUsage(sender)) {
            return true;
        }

        if (!CommandUtils.hasPlayerDataKey(sender)) {
            return true;
        }

        OnlineMMOPlayer mmoPlayer = mcMMO.getUserManager().queryPlayer((Player) sender);

        if(mmoPlayer == null) {
            sender.sendMessage(LocaleLoader.getString("Profile.PendingLoad"));
            return true;
        }

        if (args.length == 0) {
            Player player = Misc.adaptPlayer(mmoPlayer);

            boolean isLucky = Permissions.lucky(player, rootSkill);
            boolean hasEndurance = (PerksUtils.handleActivationPerks(player, 0, 0) != 0);
            float skillValue = mmoPlayer.getExperienceHandler().getSkillLevel(rootSkill);

            //Send the players a few blank lines to make finding the top of the skill command easier
            if (AdvancedConfig.getInstance().doesSkillCommandSendBlankLines())
                for (int i = 0; i < 2; i++) {
                    player.sendMessage("");
                }

            permissionsCheck(mmoPlayer);
            dataCalculations(mmoPlayer, skillValue);

            sendSkillCommandHeader(mmoPlayer, (int) skillValue);

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
                TextComponentFactory.sendPlayerUrlHeader(mmoPlayer);
            }


            if (Config.getInstance().getScoreboardsEnabled() && Config.getInstance().getSkillUseBoard()) {
                ScoreboardManager.enablePlayerSkillScoreboard(player, primarySkillType);
            }

            return true;
        }
        return skillGuideCommand.onCommand(sender, command, label, args);
    }

    private void sendStatMessages(@NotNull OnlineMMOPlayer mmoPlayer, boolean isLucky, boolean hasEndurance, float skillValue) {
        List<String> statsMessages = statsDisplay(mmoPlayer, skillValue, hasEndurance, isLucky);

        if (!statsMessages.isEmpty()) {
            Misc.adaptPlayer(mmoPlayer).sendMessage(LocaleLoader.getString("Skills.Overhaul.Header", LocaleLoader.getString("Commands.Stats.Self.Overhaul")));

            for (String message : statsMessages) {
                Misc.adaptPlayer(mmoPlayer).sendMessage(message);
            }
        }

        Misc.adaptPlayer(mmoPlayer).sendMessage(LocaleLoader.getString("Guides.Available", skillName, skillName.toLowerCase(Locale.ENGLISH)));
    }

    private void sendSkillCommandHeader(@NotNull OnlineMMOPlayer mmoPlayer, int skillValue) {
        ChatColor hd1 = ChatColor.DARK_AQUA;
        ChatColor c1 = ChatColor.GOLD;
        ChatColor c2 = ChatColor.RED;


        Misc.adaptPlayer(mmoPlayer).sendMessage(LocaleLoader.getString("Skills.Overhaul.Header", skillName));

        if(!CoreSkills.isChildSkill(rootSkill))
        {
            /*
             * NON-CHILD SKILLS
             */

            //XP GAIN METHOD
            Misc.adaptPlayer(mmoPlayer).sendMessage(LocaleLoader.getString("Commands.XPGain.Overhaul", LocaleLoader.getString("Commands.XPGain." + StringUtils.getCapitalized(rootSkill.toString()))));

            //LEVEL
            Misc.adaptPlayer(mmoPlayer).sendMessage(LocaleLoader.getString("Effects.Level.Overhaul", skillValue, mmoPlayer.getExperienceHandler().getSkillXpValue(rootSkill), mmoPlayer.getExperienceHandler().getExperienceToNextLevel(rootSkill)));

        } else {
            /*
             * CHILD SKILLS
             */


            Set<RootSkill> parents = FamilyTree.getParentSkills(rootSkill);

            //TODO: Add JSON here
            /*player.sendMessage(parent.getName() + " - " + LocaleLoader.getString("Effects.Level.Overhaul", mmoPlayer.getSkillLevel(parent), mmoPlayer.getSkillXpLevel(parent), mmoPlayer.getXpToLevel(parent)))*/
            ArrayList<RootSkill> parentList = new ArrayList<>(parents);

            StringBuilder parentMessage = new StringBuilder();

            for(int i = 0; i < parentList.size(); i++)
            {
                if(i+1 < parentList.size())
                {
                    parentMessage.append(LocaleLoader.getString("Effects.Child.ParentList", parentList.get(i).getSkillName(), mmoPlayer.getExperienceHandler().getSkillLevel(parentList.get(i))));
                    parentMessage.append(ChatColor.GRAY).append(", ");
                } else {
                    parentMessage.append(LocaleLoader.getString("Effects.Child.ParentList", parentList.get(i).getSkillName(), mmoPlayer.getExperienceHandler().getSkillLevel(parentList.get(i))));
                }
            }

            //XP GAIN METHOD
            Misc.adaptPlayer(mmoPlayer).sendMessage(LocaleLoader.getString("Commands.XPGain.Overhaul", LocaleLoader.getString("Commands.XPGain.Child")));

            Misc.adaptPlayer(mmoPlayer).sendMessage(LocaleLoader.getString("Effects.Child.Overhaul", skillValue, parentMessage.toString()));

        }
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

    protected @NotNull String[] getAbilityDisplayValues(@NotNull SkillActivationType skillActivationType, @NotNull OnlineMMOPlayer mmoPlayer, @NotNull SubSkillType subSkill) {
        return RandomChanceUtil.calculateAbilityDisplayValues(skillActivationType, Misc.adaptPlayer(mmoPlayer), subSkill);
    }

    protected @NotNull String[] calculateLengthDisplayValues(@NotNull OnlineMMOPlayer mmoPlayer, float skillValue) {
        int maxLength = primarySkillType.getSuperAbilityType().getMaxLength();
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

        int enduranceLength = PerksUtils.handleActivationPerks(Misc.adaptPlayer(mmoPlayer), length, maxLength);

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

    protected abstract void dataCalculations(@NotNull OnlineMMOPlayer mmoPlayer, float skillValue);

    protected abstract void permissionsCheck(@NotNull OnlineMMOPlayer mmoPlayer);

    //protected abstract List<String> effectsDisplay();

    protected abstract @NotNull List<String> statsDisplay(@NotNull OnlineMMOPlayer mmoPlayer, float skillValue, boolean hasEndurance, boolean isLucky);

    protected abstract @NotNull List<Component> getTextComponents(@NotNull OnlineMMOPlayer player);

    /**
     * Checks if a player can use a skill
     * @param mmoPlayer target player
     * @param subSkillType target subskill
     * @return true if the player has permission and has the skill unlocked
     */
    protected boolean canUseSubskill(@NotNull OnlineMMOPlayer mmoPlayer, SubSkillType subSkillType) {
        return Permissions.isSubSkillEnabled(Misc.adaptPlayer(mmoPlayer), subSkillType) && RankUtils.hasUnlockedSubskill(mmoPlayer, subSkillType);
    }
}
