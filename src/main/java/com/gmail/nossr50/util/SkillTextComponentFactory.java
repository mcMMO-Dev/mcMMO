package com.gmail.nossr50.util;

import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.datatypes.skills.SubSkill;
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
    public static HashMap<SubSkill, BaseComponent[]> subSkillHoverComponents;

    public static TextComponent getSubSkillTextComponent(Player player, SubSkill subSkill, int localeKeyName, int localeKeyDescription)
    {
        boolean playerHasUnlocked = false;

        //Init our maps
        if (subSkillTextComponents == null)
        {
            subSkillTextComponents = new HashMap<>();
            subSkillHoverComponents = new HashMap<>();
        }

        int curRank = RankUtils.getRank(player, subSkill);

        if(curRank > 0)
            playerHasUnlocked = true;

        //The skill milestone holds relevant information about the ranks of a skill
        PlayerProfile playerProfile = UserManager.getPlayer(player).getProfile();

        //Get skill name & description from our locale file
        String skillName = LocaleLoader.getString(subSkill.getLocalKeyRoot()+localeKeyName);
        String skillDescription = LocaleLoader.getString(subSkill.getLocalKeyRoot()+localeKeyDescription);

        if(subSkillTextComponents.get(subSkill) == null)
        {
            //Setup Text Component
            TextComponent textComponent = new TextComponent(skillName);
            textComponent.setColor(ChatColor.DARK_AQUA);

            //Hover Event
            textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, getBaseComponent(player, subSkill, skillName, skillDescription, curRank, playerHasUnlocked)));

            //Insertion
            textComponent.setInsertion(skillName);

            subSkillTextComponents.put(subSkill, textComponent);
            return subSkillTextComponents.get(subSkill);
        } else {
            return subSkillTextComponents.get(subSkill);
        }
    }

    private static BaseComponent[] getBaseComponent(Player player, SubSkill subSkill, String skillName, String skillDescription, int curRank, boolean playerHasUnlocked)
    {
        if(subSkillHoverComponents.get(subSkill) != null)
        {
            return subSkillHoverComponents.get(subSkill);
        }

        BaseComponent[] newComponents;

        //TODO: Clean this up
        if(subSkill.getNumRanks() == 0)
            newComponents = new ComponentBuilder(skillName).bold(true).color(ChatColor.GOLD).append("\n\nDescription").bold(true).color(ChatColor.GREEN).append("\n"+skillDescription).bold(false).color(ChatColor.WHITE).create();
        else if(playerHasUnlocked)
            newComponents = new ComponentBuilder(skillName).bold(true).color(ChatColor.GOLD).append("\nRank "+curRank).bold(false).color(ChatColor.GREEN).append(" of ").color(ChatColor.WHITE).append(String.valueOf(subSkill.getNumRanks())).color(ChatColor.GOLD).append("\n\nDescription").bold(true).color(ChatColor.GREEN).append("\n"+skillDescription).bold(false).color(ChatColor.WHITE).create();
        else
            newComponents = new ComponentBuilder(skillName).bold(true).color(ChatColor.RED).append("\n-=LOCKED=-").color(ChatColor.GRAY).append("\n\nUnlock Requirements").color(ChatColor.YELLOW).append("\nLevel "+ AdvancedConfig.getInstance().getSubSkillUnlockLevel(subSkill, 1)+" "+subSkill.getParentNiceNameLocale()).bold(false).create();

        subSkillHoverComponents.put(subSkill, newComponents);
        return subSkillHoverComponents.get(subSkill);
    }
}
