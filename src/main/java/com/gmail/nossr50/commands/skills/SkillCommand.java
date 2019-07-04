package com.gmail.nossr50.commands.skills;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.skills.child.FamilyTree;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.StringUtils;
import com.gmail.nossr50.util.skills.RankUtils;
import com.gmail.nossr50.util.skills.SkillActivationType;
import com.google.common.collect.ImmutableList;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public abstract class SkillCommand implements TabExecutor {
    protected PrimarySkillType skill;
    protected DecimalFormat percent = new DecimalFormat("##0.00%");
    protected DecimalFormat decimal = new DecimalFormat("##0.00");
    private String skillName;
    private CommandExecutor skillGuideCommand;
    protected mcMMO pluginRef;

    public SkillCommand(PrimarySkillType primarySkillType, mcMMO pluginRef) {
        this.pluginRef = pluginRef;
        this.skill = primarySkillType;
        skillName = pluginRef.getSkillTools().getLocalizedSkillName(primarySkillType);
        skillGuideCommand = new SkillGuideCommand(primarySkillType, pluginRef);
    }

    public static String[] addItemToFirstPositionOfArray(String itemToAdd, String... existingArray) {
        String[] newArray = new String[existingArray.length + 1];
        newArray[0] = itemToAdd;

        System.arraycopy(existingArray, 0, newArray, 1, existingArray.length);

        return newArray;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (pluginRef.getCommandTools().noConsoleUsage(sender)) {
            return true;
        }

        if (!pluginRef.getCommandTools().hasPlayerDataKey(sender)) {
            return true;
        }

        if (pluginRef.getUserManager().getPlayer((Player) sender) == null) {
            sender.sendMessage(pluginRef.getLocaleManager().getString("Profile.PendingLoad"));
            return true;
        }

        switch (args.length) {
            case 0:
                Player player = (Player) sender;
                McMMOPlayer mcMMOPlayer = pluginRef.getUserManager().getPlayer(player);

                boolean isLucky = Permissions.lucky(player, skill);
                boolean hasEndurance = pluginRef.getSkillTools().getEnduranceLength(player) > 0;
                double skillValue = mcMMOPlayer.getSkillLevel(skill);

                //Send the players a few blank lines to make finding the top of the skill command easier
                if (pluginRef.getConfigManager().getConfigCommands().isSendBlankLines())
                    for (int i = 0; i < 2; i++) {
                        player.sendMessage("");
                    }

                permissionsCheck(player);
                dataCalculations(player, skillValue);

                sendSkillCommandHeader(player, mcMMOPlayer, (int) skillValue);

                //Make JSON text components
                List<TextComponent> subskillTextComponents = getTextComponents(player);

                //Subskills Header
                player.sendMessage(pluginRef.getLocaleManager().getString("Skills.Overhaul.Header", pluginRef.getLocaleManager().getString("Effects.SubSkills.Overhaul")));

                //Send JSON text components

                pluginRef.getTextComponentFactory().sendPlayerSubSkillList(player, subskillTextComponents);

                /*for(TextComponent tc : subskillTextComponents)
                {
                    player.spigot().sendMessage(new TextComponent[]{tc, new TextComponent(": TESTING")});
                }*/

                //Stats
                getStatMessages(player, isLucky, hasEndurance, skillValue);

                //Header


                //Link Header
                if (pluginRef.getConfigManager().getConfigAds().isShowWebsiteLinks()) {
                    player.sendMessage(pluginRef.getLocaleManager().getString("Overhaul.mcMMO.Header"));
                    pluginRef.getTextComponentFactory().sendPlayerUrlHeader(player);
                }


                if (pluginRef.getScoreboardSettings().getScoreboardsEnabled()
                        && pluginRef.getScoreboardSettings().getConfigSectionScoreboardTypes()
                        .getConfigSectionSkillBoard().isUseThisBoard()) {
                    pluginRef.getScoreboardManager().enablePlayerSkillScoreboard(player, skill);
                }

                return true;
            default:
                return skillGuideCommand.onCommand(sender, command, label, args);
        }
    }

    private void getStatMessages(Player player, boolean isLucky, boolean hasEndurance, double skillValue) {
        List<String> statsMessages = statsDisplay(player, skillValue, hasEndurance, isLucky);

        if (!statsMessages.isEmpty()) {
            player.sendMessage(pluginRef.getLocaleManager().getString("Skills.Overhaul.Header", pluginRef.getLocaleManager().getString("Commands.Stats.Self.Overhaul")));

            for (String message : statsMessages) {
                player.sendMessage(message);
            }
        }

        player.sendMessage(pluginRef.getLocaleManager().getString("Guides.Available", skillName, skillName.toLowerCase()));
    }

    private void sendSkillCommandHeader(Player player, McMMOPlayer mcMMOPlayer, int skillValue) {
        ChatColor hd1 = ChatColor.DARK_AQUA;
        ChatColor c1 = ChatColor.GOLD;
        ChatColor c2 = ChatColor.RED;


        player.sendMessage(pluginRef.getLocaleManager().getString("Skills.Overhaul.Header", skillName));

        if (!pluginRef.getSkillTools().isChildSkill(skill)) {
            /*
             * NON-CHILD SKILLS
             */

            //XP GAIN METHOD
            player.sendMessage(pluginRef.getLocaleManager().getString("Commands.XPGain.Overhaul", pluginRef.getLocaleManager().getString("Commands.XPGain." + StringUtils.getCapitalized(skill.toString()))));

            //LEVEL
            player.sendMessage(pluginRef.getLocaleManager().getString("Effects.Level.Overhaul", skillValue, mcMMOPlayer.getSkillXpLevel(skill), mcMMOPlayer.getXpToLevel(skill)));

        } else {
            /*
             * CHILD SKILLS
             */


            Set<PrimarySkillType> parents = FamilyTree.getParents(skill);
            ArrayList<PrimarySkillType> parentList = new ArrayList<>();

            //TODO: Add JSON here
            /*player.sendMessage(parent.getName() + " - " + pluginRef.getLocaleManager().getString("Effects.Level.Overhaul", mcMMOPlayer.getSkillLevel(parent), mcMMOPlayer.getSkillXpLevel(parent), mcMMOPlayer.getXpToLevel(parent)))*/
            parentList.addAll(parents);

            StringBuilder parentMessage = new StringBuilder();

            for (int i = 0; i < parentList.size(); i++) {
                if (i + 1 < parentList.size()) {
                    parentMessage.append(pluginRef.getLocaleManager().getString("Effects.Child.ParentList", pluginRef.getSkillTools().getLocalizedSkillName(parentList.get(i)), mcMMOPlayer.getSkillLevel(parentList.get(i))));
                    parentMessage.append(ChatColor.GRAY + ", ");
                } else {
                    parentMessage.append(pluginRef.getLocaleManager().getString("Effects.Child.ParentList", pluginRef.getSkillTools().getLocalizedSkillName(parentList.get(i)), mcMMOPlayer.getSkillLevel(parentList.get(i))));
                }
            }

            //XP GAIN METHOD
            player.sendMessage(pluginRef.getLocaleManager().getString("Commands.XPGain.Overhaul", pluginRef.getLocaleManager().getString("Commands.XPGain.Child")));

            player.sendMessage(pluginRef.getLocaleManager().getString("Effects.Child.Overhaul", skillValue, parentMessage.toString()));
            //LEVEL
            //player.sendMessage(pluginRef.getLocaleManager().getString("Effects.Child.Overhaul", skillValue, skillValue));

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

    protected int calculateRank(double skillValue, int maxLevel, int rankChangeLevel) {
        return Math.min((int) skillValue, maxLevel) / rankChangeLevel;
    }

    protected String[] getAbilityDisplayValues(Player player, SubSkillType subSkill) {
        return pluginRef.getRandomChanceTools().calculateAbilityDisplayValues(SkillActivationType.RANDOM_LINEAR_100_SCALE_WITH_CAP, player, subSkill);
    }

    protected String[] formatLengthDisplayValues(Player player, double skillValue) {

        int length = pluginRef.getSkillTools().calculateAbilityLength(pluginRef.getUserManager().getPlayer(player), skill, pluginRef.getSkillTools().getSuperAbility(skill));

        int enduranceLength = pluginRef.getSkillTools().calculateAbilityLengthPerks(pluginRef.getUserManager().getPlayer(player), skill, pluginRef.getSkillTools().getSuperAbility(skill));

        return new String[]{String.valueOf(length), String.valueOf(enduranceLength)};
    }

    protected String getStatMessage(SubSkillType subSkillType, String... vars) {
        return getStatMessage(false, false, subSkillType, vars);
    }

    protected String getStatMessage(boolean isExtra, boolean isCustom, SubSkillType subSkillType, String... vars) {
        String templateKey = isCustom ? "Ability.Generic.Template.Custom" : "Ability.Generic.Template";
        String statDescriptionKey = !isExtra ? subSkillType.getLocaleKeyStatDescription(pluginRef) : subSkillType.getLocaleKeyStatExtraDescription(pluginRef);

        if (isCustom)
            return pluginRef.getLocaleManager().getString(templateKey, pluginRef.getLocaleManager().getString(statDescriptionKey, vars));
        else {
            String[] mergedList = pluginRef.getNotificationManager().addItemToFirstPositionOfArray(pluginRef.getLocaleManager().getString(statDescriptionKey), vars);
            return pluginRef.getLocaleManager().getString(templateKey, mergedList);
        }
    }

    protected abstract void dataCalculations(Player player, double skillValue);

    protected abstract void permissionsCheck(Player player);

    //protected abstract List<String> effectsDisplay();

    protected abstract List<String> statsDisplay(Player player, double skillValue, boolean hasEndurance, boolean isLucky);

    protected abstract List<TextComponent> getTextComponents(Player player);

    /**
     * Checks if a player can use a skill
     *
     * @param player       target player
     * @param subSkillType target subskill
     * @return true if the player has permission and has the skill unlocked
     */
    protected boolean canUseSubskill(Player player, SubSkillType subSkillType) {
        return Permissions.isSubSkillEnabled(player, subSkillType) && RankUtils.hasUnlockedSubskill(player, subSkillType);
    }
}
