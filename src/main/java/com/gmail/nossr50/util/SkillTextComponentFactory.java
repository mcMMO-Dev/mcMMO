package com.gmail.nossr50.util;

import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.datatypes.skills.SubSkill;
import com.gmail.nossr50.datatypes.skills.SubSkillFlags;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.player.UserManager;
import com.gmail.nossr50.util.skills.RankUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class SkillTextComponentFactory {
    public static HashMap<SubSkill, TextComponent> subSkillTextComponents;

    //Yeah there's probably a better way to do this
    public static HashMap<SubSkill, BaseComponent[]> lockedComponentMap;

    //This is a nested map because each JSON component for a different rank is going to be a bit different.
    public static HashMap<Integer, HashMap<SubSkill, BaseComponent[]>> hoverComponentOuterMap;

    public static TextComponent getSubSkillTextComponent(Player player, SubSkill subSkill, int localeKeyName, int localeKeyDescription)
    {
        //Init our maps
        if (subSkillTextComponents == null)
        {
            subSkillTextComponents = new HashMap<>();
            lockedComponentMap = new HashMap<>();
            hoverComponentOuterMap = new HashMap<>();
        }

        //The skill milestone holds relevant information about the ranks of a skill
        PlayerProfile playerProfile = UserManager.getPlayer(player).getProfile();

        //Get skill name & description from our locale file
        String skillName = LocaleLoader.getString(subSkill.getLocalKeyRoot()+localeKeyName);

        if(subSkillTextComponents.get(subSkill) == null)
        {
            //Setup Text Component
            TextComponent textComponent = new TextComponent(skillName);
            textComponent.setColor(ChatColor.DARK_AQUA);

            //Hover Event
            textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, getBaseComponent(player, subSkill, localeKeyName, localeKeyDescription)));

            //Insertion
            textComponent.setInsertion(skillName);

            subSkillTextComponents.put(subSkill, textComponent);
            return subSkillTextComponents.get(subSkill);
        } else {
            return subSkillTextComponents.get(subSkill);
        }
    }

    private static BaseComponent[] getBaseComponent(Player player, SubSkill subSkill, int localeKeyName, int localeKeyDescription)
    {
        //If the player hasn't unlocked this skill yet we use a different JSON template
        if(subSkill.getNumRanks() > 0 && RankUtils.getRank(player, subSkill) == 0)
        {
            //If the JSON component already exists
            if(lockedComponentMap.get(subSkill) != null)
                return lockedComponentMap.get(subSkill);

            BaseComponent[] newComponents = getSubSkillHoverEventJSON(subSkill, player, localeKeyName, localeKeyDescription);
            lockedComponentMap.put(subSkill, newComponents);
            return lockedComponentMap.get(subSkill);
        }

        int curRank = RankUtils.getRank(player, subSkill);

        //If the inner hashmap for this rank isn't made yet
        if(hoverComponentOuterMap.get(curRank) == null)
            hoverComponentOuterMap.put(curRank, new HashMap<>());

        //Inner Hashmap for current rank
        HashMap<SubSkill, BaseComponent[]> innerMap = hoverComponentOuterMap.get(curRank);

        if(innerMap.get(subSkill) == null)
            innerMap.put(subSkill, getSubSkillHoverEventJSON(subSkill, player, localeKeyName, localeKeyDescription));

        return innerMap.get(subSkill);
    }

    /**
     * Checks to see if a bit is flagged in the subskill
     * @param flag1 The flag to check for
     * @param subSkill The target subskill
     * @return returns true if the bit is flagged in the subskill
     */
    private static boolean checkFlags(int flag1, SubSkill subSkill)
    {
        return (flag1 & subSkill.getFlags()) == flag1;
    }

    private static BaseComponent[] getSubSkillHoverEventJSON(SubSkill subSkill, Player player, int localeKeyName, int localeKeyDescription)
    {
        String skillName = LocaleLoader.getString(subSkill.getLocalKeyRoot()+localeKeyName);
        String skillDescription = LocaleLoader.getString(subSkill.getLocalKeyRoot()+localeKeyDescription);

        /*
         * Hover Event BaseComponent color table
         */
        ChatColor ccSubSkillHeader      = ChatColor.GOLD;
        ChatColor ccRank                = ChatColor.BLUE;
        ChatColor ccCurRank             = ChatColor.GREEN;
        ChatColor ccPossessive          = ChatColor.WHITE;
        ChatColor ccNumRanks            = ccCurRank;
        ChatColor ccDescriptionHeader   = ChatColor.DARK_PURPLE;
        ChatColor ccDescription         = ChatColor.WHITE;
        ChatColor ccLocked              = ChatColor.DARK_GRAY;
        ChatColor ccLevelRequirement    = ChatColor.BLUE;
        ChatColor ccLevelRequired       = ChatColor.RED;

        //SubSkill Name
        ComponentBuilder componentBuilder = new ComponentBuilder(skillName);
        componentBuilder.bold(true).color(ccSubSkillHeader);
        componentBuilder.append("\n");

        if(RankUtils.getRank(player, subSkill) == 0)
        {
            //Skill is not unlocked yet
            componentBuilder.append(LocaleLoader.getString("JSON.Locked")).color(ccLocked).bold(true);
            componentBuilder.append("\n").append("\n").bold(false);
            componentBuilder.append(LocaleLoader.getString("JSON.LevelRequirement") +": ").color(ccLevelRequirement);
            componentBuilder.append(String.valueOf(AdvancedConfig.getInstance().getSubSkillUnlockLevel(subSkill, 1))).color(ccLevelRequired);

        } else {
            addSubSkillTypeToHoverEventJSON(subSkill, componentBuilder);

            //RANK
            if(subSkill.getNumRanks() > 0)
            {
                //Rank
                componentBuilder.append(LocaleLoader.getString("JSON.Rank") + ": ").bold(false).color(ccRank);

                //x of y
                componentBuilder.append(String.valueOf(RankUtils.getRank(player, subSkill))).color(ccCurRank);
                componentBuilder.append(" "+LocaleLoader.getString("JSON.RankPossesive")+" ").color(ccPossessive);
                componentBuilder.append(String.valueOf(subSkill.getNumRanks())).color(ccNumRanks);
            }

            //Empty line
            componentBuilder.append("\n").bold(false);
            componentBuilder.append("\n");

            //Description Header
            componentBuilder.append(LocaleLoader.getString("JSON.DescriptionHeader")).bold(false).color(ccDescriptionHeader);
            componentBuilder.append("\n").bold(false);

            //Description
            componentBuilder.append(skillDescription).color(ccDescription);
            //componentBuilder.append("\n");
        }

        return componentBuilder.create();
    }

    private static void addSubSkillTypeToHoverEventJSON(SubSkill subSkill, ComponentBuilder componentBuilder)
    {
        if(checkFlags(SubSkillFlags.SUPERABILITY, subSkill))
        {
            componentBuilder.append(LocaleLoader.getString("JSON.Type.SuperAbility")).color(ChatColor.LIGHT_PURPLE);
            componentBuilder.bold(true);
        } else if(checkFlags(SubSkillFlags.ACTIVE, subSkill))
        {
            componentBuilder.append(LocaleLoader.getString("JSON.Type.Active")).color(ChatColor.DARK_RED);
            componentBuilder.bold(true);
        } else {
            componentBuilder.append(LocaleLoader.getString("JSON.Type.Passive")).color(ChatColor.GREEN);
            componentBuilder.bold(true);
        }

        componentBuilder.append("\n");
    }
}


