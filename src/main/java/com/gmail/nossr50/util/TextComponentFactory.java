package com.gmail.nossr50.util;

import com.gmail.nossr50.commands.skills.McMMOWebLinks;
import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.datatypes.interactions.NotificationType;
import com.gmail.nossr50.datatypes.json.McMMOUrl;
import com.gmail.nossr50.datatypes.skills.PrimarySkill;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.datatypes.skills.subskills.AbstractSubSkill;
import com.gmail.nossr50.listeners.InteractionManager;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.skills.RankUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TextComponentFactory {
    public static HashMap<String, TextComponent> subSkillTextComponents;

    //Yeah there's probably a better way to do this
    public static HashMap<String, BaseComponent[]> lockedComponentMap;

    public static BaseComponent[] webComponents;

    public static TextComponent getNotificationTextComponent(String localeKey, NotificationType notificationType)
    {
        TextComponent textComponent = new TextComponent(LocaleLoader.getString(localeKey));
        return textComponent;
    }

    public static void sendPlayerUrlHeader(Player player) {
        Player.Spigot spigotPlayer = player.spigot();

        if(webComponents != null)
        {
            player.spigot().sendMessage(webComponents);
            return;
        }

        TextComponent prefix = new TextComponent("[| ");
        prefix.setColor(ChatColor.DARK_AQUA);
        TextComponent suffix = new TextComponent(" |]");
        suffix.setColor(ChatColor.DARK_AQUA);

        TextComponent emptySpace = new TextComponent(" ");

        BaseComponent[] baseComponents = {new TextComponent(prefix),
                getWebLinkTextComponent(McMMOWebLinks.WEBSITE),
                emptySpace,
                getWebLinkTextComponent(McMMOWebLinks.DISCORD),
                emptySpace,
                getWebLinkTextComponent(McMMOWebLinks.PATREON),
                emptySpace,
                getWebLinkTextComponent(McMMOWebLinks.WIKI),
                emptySpace,
                getWebLinkTextComponent(McMMOWebLinks.SPIGOT),
                emptySpace,
                getWebLinkTextComponent(McMMOWebLinks.HELP_TRANSLATE),
                new TextComponent(suffix)};

        //Cache into memory since the links wont change
        webComponents = baseComponents;
        spigotPlayer.sendMessage(webComponents);
    }

    public static void sendPlayerSubSkillList(Player player, List<TextComponent> textComponents)
    {
        TextComponent emptySpace = new TextComponent(" ");
        //TextComponent atSymbolText = new TextComponent(atSymbol);

        ArrayList<BaseComponent> bulkMessage = new ArrayList<>();
        int newLineCount = 0; //Hacky solution to wordwrap problems

        for (TextComponent textComponent : textComponents) {
            //Don't send more than 3 subskills per line to avoid MOST wordwrap problems
            if(newLineCount > 2)
            {
                TextComponent[] bulkArray = new TextComponent[bulkMessage.size()];
                bulkArray = bulkMessage.toArray(bulkArray);

                player.spigot().sendMessage(bulkArray);
                bulkMessage = new ArrayList<>();
                newLineCount = 0;
            }
            //Style the skills into @links
            final String originalTxt = textComponent.getText();

            TextComponent stylizedText = new TextComponent("@");
            stylizedText.setColor(ChatColor.YELLOW);
            addChild(stylizedText, originalTxt);

            if(textComponent.getHoverEvent() != null)
                stylizedText.setHoverEvent(textComponent.getHoverEvent());

            if(textComponent.getClickEvent() != null)
                stylizedText.setClickEvent(textComponent.getClickEvent());

            bulkMessage.add(stylizedText);
            bulkMessage.add(emptySpace);

            newLineCount++;
        }

        /*
         * Convert our list into an array
         */
        TextComponent[] bulkArray = new TextComponent[bulkMessage.size()];
        bulkArray = bulkMessage.toArray(bulkArray);

        player.spigot().sendMessage(bulkArray);
    }

    public static TextComponent getWebLinkTextComponent(McMMOWebLinks webLinks)
    {
        TextComponent webTextComponent;

        switch(webLinks)
        {
            case WEBSITE:
                webTextComponent = new TextComponent("@");
                webTextComponent.setColor(ChatColor.YELLOW);
                addChild(webTextComponent, "Web");
                webTextComponent.setClickEvent(getUrlClickEvent(McMMOUrl.urlWebsite));
                break;
            case SPIGOT:
                webTextComponent = new TextComponent("@");
                webTextComponent.setColor(ChatColor.YELLOW);
                addChild(webTextComponent, "Spigot");
                webTextComponent.setClickEvent(getUrlClickEvent(McMMOUrl.urlSpigot));
                break;
            case DISCORD:
                webTextComponent = new TextComponent("@");
                webTextComponent.setColor(ChatColor.YELLOW);
                addChild(webTextComponent, "Discord");
                webTextComponent.setClickEvent(getUrlClickEvent(McMMOUrl.urlDiscord));
                break;
            case PATREON:
                webTextComponent = new TextComponent("@");
                webTextComponent.setColor(ChatColor.YELLOW);
                addChild(webTextComponent, "Patreon");
                webTextComponent.setClickEvent(getUrlClickEvent(McMMOUrl.urlPatreon));
                break;
            case WIKI:
                webTextComponent = new TextComponent("@");
                webTextComponent.setColor(ChatColor.YELLOW);
                addChild(webTextComponent, "Wiki");
                webTextComponent.setClickEvent(getUrlClickEvent(McMMOUrl.urlWiki));
                break;
            case HELP_TRANSLATE:
                webTextComponent = new TextComponent("@");
                webTextComponent.setColor(ChatColor.YELLOW);
                addChild(webTextComponent, "Lang");
                webTextComponent.setClickEvent(getUrlClickEvent(McMMOUrl.urlTranslate));
                break;
            default:
                webTextComponent = new TextComponent("NOT DEFINED");
        }

        webTextComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, getUrlHoverEvent(webLinks)));
        webTextComponent.setInsertion(webLinks.getUrl());

        return webTextComponent;
    }

    private static void addChild(TextComponent webTextComponent, String childName) {
        TextComponent childComponent = new TextComponent(childName);
        childComponent.setColor(ChatColor.BLUE);
        webTextComponent.addExtra(childComponent);
    }

    private static BaseComponent[] getUrlHoverEvent(McMMOWebLinks webLinks)
    {
        ComponentBuilder componentBuilder = new ComponentBuilder(webLinks.getNiceTitle());

        switch(webLinks)
        {
            case WEBSITE:
                addUrlHeaderHover(webLinks, componentBuilder);
                componentBuilder.append("\n\n").italic(false);
                componentBuilder.append("The official mcMMO Website!").color(ChatColor.GREEN);
                break;
            case SPIGOT:
                addUrlHeaderHover(webLinks, componentBuilder);
                componentBuilder.append("\n\n").italic(false);
                componentBuilder.append("The official mcMMO Spigot Resource Page!").color(ChatColor.GREEN);
                componentBuilder.append("\nI post regularly in the discussion thread here!").color(ChatColor.GRAY);
                break;
            case PATREON:
                addUrlHeaderHover(webLinks, componentBuilder);
                componentBuilder.append("\n\n").italic(false);
                componentBuilder.append("Support nossr50 and development of mcMMO on Patreon!").color(ChatColor.GREEN);
                break;
            case WIKI:
                addUrlHeaderHover(webLinks, componentBuilder);
                componentBuilder.append("\n\n").italic(false);
                componentBuilder.append("The official mcMMO wiki!").color(ChatColor.GREEN);
                componentBuilder.append("\n");
                componentBuilder.append("I'm looking for more wiki staff, contact me on our discord!").italic(false).color(ChatColor.DARK_GRAY);
                break;
            case DISCORD:
                addUrlHeaderHover(webLinks, componentBuilder);
                componentBuilder.append("\n\n").italic(false);
                componentBuilder.append("The official mcMMO Discord server!").color(ChatColor.GREEN);
                break;
            case HELP_TRANSLATE:
                addUrlHeaderHover(webLinks, componentBuilder);
                componentBuilder.append("\n\n").italic(false);
                componentBuilder.append("mcMMO's translation service!").color(ChatColor.GREEN);
                componentBuilder.append("\n");
                componentBuilder.append("You can use this website to help translate mcMMO into your language!" +
                        "\nIf you want to know more contact me in discord.").italic(false).color(ChatColor.DARK_GRAY);
        }

        return componentBuilder.create();
    }

    private static void addUrlHeaderHover(McMMOWebLinks webLinks, ComponentBuilder componentBuilder) {
        componentBuilder.append("\n");
        componentBuilder.append(webLinks.getUrl()).color(ChatColor.GRAY).italic(true);
    }

    private static ClickEvent getUrlClickEvent(String url)
    {
        return new ClickEvent(ClickEvent.Action.OPEN_URL, url);
    }

    public static TextComponent getSubSkillTextComponent(Player player, SubSkillType subSkillType)
    {
        //Init our maps
        if (subSkillTextComponents == null)
        {
            subSkillTextComponents = new HashMap<>();
            lockedComponentMap = new HashMap<>();
            //hoverComponentOuterMap = new HashMap<>();
        }

        //Get skill name & description from our locale file
        String key = subSkillType.toString();
        String skillName = subSkillType.getLocaleName();

        if(subSkillTextComponents.get(key) == null)
        {
            //Setup Text Component
            TextComponent textComponent = new TextComponent(skillName);
            //textComponent.setColor(ChatColor.DARK_AQUA);

            //Hover Event
            textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, getBaseComponent(player, subSkillType)));

            //Insertion
            textComponent.setInsertion(skillName);

            subSkillTextComponents.put(key, textComponent);
            return subSkillTextComponents.get(key);
        } else {
            return subSkillTextComponents.get(key);
        }
    }

    public static TextComponent getSubSkillTextComponent(Player player, AbstractSubSkill abstractSubSkill)
    {
        //Init our maps
        if (subSkillTextComponents == null)
        {
            subSkillTextComponents = new HashMap<>();
            lockedComponentMap = new HashMap<>();
            //hoverComponentOuterMap = new HashMap<>();
        }

        //Get skill name & description from our locale file
        String key = abstractSubSkill.getConfigKeyName();
        String skillName = abstractSubSkill.getNiceName();

        if(subSkillTextComponents.get(key) == null)
        {
            //Setup Text Component
            TextComponent textComponent = new TextComponent(skillName);
            textComponent.setColor(ChatColor.DARK_AQUA);

            //Hover Event
            textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, getBaseComponent(player, abstractSubSkill)));

            //Insertion
            textComponent.setInsertion(skillName);

            subSkillTextComponents.put(key, textComponent);
            return subSkillTextComponents.get(key);
        } else {
            return subSkillTextComponents.get(key);
        }
    }

    private static BaseComponent[] getBaseComponent(Player player, AbstractSubSkill abstractSubSkill)
    {

        int curRank = RankUtils.getRank(player, abstractSubSkill);
        String key = abstractSubSkill.getConfigKeyName();

        //If the player hasn't unlocked this skill yet we use a different JSON template
        if(abstractSubSkill.getNumRanks() > 0 && curRank == 0)
        {
            //If the JSON component already exists
            if(lockedComponentMap.get(key) != null)
                return lockedComponentMap.get(key);

            BaseComponent[] newComponents = getSubSkillHoverEventJSON(abstractSubSkill, player, curRank);
            lockedComponentMap.put(key, newComponents);
            return lockedComponentMap.get(key);
        }

        //If the inner hashmap for this rank isn't made yet
        /*if(hoverComponentOuterMap.get(curRank) == null)
            hoverComponentOuterMap.put(curRank, new HashMap<>());*/

        //Inner Hashmap for current rank
        //HashMap<String, BaseComponent[]> innerMap = hoverComponentOuterMap.get(curRank);

        /*if(innerMap.get(key) == null)
            innerMap.put(key, getSubSkillHoverEventJSON(abstractSubSkill, player, curRank));*/

        return getSubSkillHoverEventJSON(abstractSubSkill, player, curRank);
    }

    private static BaseComponent[] getBaseComponent(Player player, SubSkillType subSkillType)
    {
        int curRank = RankUtils.getRank(player, subSkillType);
        String key = subSkillType.toString();

        //If the player hasn't unlocked this skill yet we use a different JSON template
        if(subSkillType.getNumRanks() > 0 && curRank == 0)
        {
            //If the JSON component already exists
            if(lockedComponentMap.get(key) != null)
                return lockedComponentMap.get(key);

            BaseComponent[] newComponents = getSubSkillHoverEventJSON(subSkillType, player, curRank);
            lockedComponentMap.put(key, newComponents);
            return lockedComponentMap.get(key);
        }

        //If the inner hashmap for this rank isn't made yet
        /*if(hoverComponentOuterMap.get(curRank) == null)
            hoverComponentOuterMap.put(curRank, new HashMap<>());

        //Inner Hashmap for current rank
        HashMap<String, BaseComponent[]> innerMap = hoverComponentOuterMap.get(curRank);*/

        /*if(innerMap.get(key) == null)
            innerMap.put(key, getSubSkillHoverEventJSON(subSkillType, player, curRank));

        return innerMap.get(key);*/

        return getSubSkillHoverEventJSON(subSkillType, player, curRank);
    }

    /**
     * Used for the skill in the new skill system (Deriving from AbstractSubSkill)
     * @param abstractSubSkill this subskill
     * @param player
     * @param curRank
     * @return
     */
    private static BaseComponent[] getSubSkillHoverEventJSON(AbstractSubSkill abstractSubSkill, Player player, int curRank)
    {
        String skillName = abstractSubSkill.getNiceName();

        /*
         * Hover Event BaseComponent color table
         */
        ChatColor ccSubSkillHeader      = ChatColor.GOLD;
        ChatColor ccRank                = ChatColor.BLUE;
        ChatColor ccCurRank             = ChatColor.GREEN;
        ChatColor ccPossessive          = ChatColor.WHITE;
        ChatColor ccNumRanks            = ccCurRank;
        //ChatColor ccDescriptionHeader   = ChatColor.DARK_PURPLE;
        //ChatColor ccDescription         = ChatColor.WHITE;
        ChatColor ccLocked              = ChatColor.DARK_GRAY;
        ChatColor ccLevelRequirement    = ChatColor.BLUE;
        ChatColor ccLevelRequired       = ChatColor.RED;

        //SubSkillType Name
        ComponentBuilder componentBuilder = getNewComponentBuilder(skillName, ccSubSkillHeader);

        if(RankUtils.getRank(player, abstractSubSkill) == 0)
        {
            //Skill is not unlocked yet
            addLocked(abstractSubSkill, ccLocked, ccLevelRequirement, ccLevelRequired, componentBuilder);
        } else {
            addSubSkillTypeToHoverEventJSON(abstractSubSkill, componentBuilder);

            //RANK
            addRanked(ccRank, ccCurRank, ccPossessive, ccNumRanks, componentBuilder, abstractSubSkill.getNumRanks(), RankUtils.getRank(player, abstractSubSkill));

            componentBuilder.append(LocaleLoader.getString("JSON.DescriptionHeader"));
            componentBuilder.append("\n").append(abstractSubSkill.getDescription()).append("\n");

            //Empty line
            componentBuilder.append("\n").bold(false);
            componentBuilder.append("\n");

            //Finally, add details to the tooltip
            abstractSubSkill.addStats(componentBuilder, player);
        }

        return componentBuilder.create();
    }

    private static ComponentBuilder getNewComponentBuilder(String skillName, ChatColor ccSubSkillHeader) {
        ComponentBuilder componentBuilder = new ComponentBuilder(skillName);
        componentBuilder.bold(true).color(ccSubSkillHeader);
        componentBuilder.append("\n");
        return componentBuilder;
    }

    private static void addRanked(ChatColor ccRank, ChatColor ccCurRank, ChatColor ccPossessive, ChatColor ccNumRanks, ComponentBuilder componentBuilder, int numRanks, int rank) {
        if (numRanks > 0) {
            //Rank
            componentBuilder.append(LocaleLoader.getString("JSON.Rank") + ": ").bold(false).color(ccRank);

            //x of y
            componentBuilder.append(String.valueOf(rank)).color(ccCurRank);
            componentBuilder.append(" " + LocaleLoader.getString("JSON.RankPossesive") + " ").color(ccPossessive);
            componentBuilder.append(String.valueOf(numRanks)).color(ccNumRanks);
        }
    }

    private static void addLocked(AbstractSubSkill abstractSubSkill, ChatColor ccLocked, ChatColor ccLevelRequirement, ChatColor ccLevelRequired, ComponentBuilder componentBuilder) {
        componentBuilder.append(LocaleLoader.getString("JSON.Locked")).color(ccLocked).bold(true);
        componentBuilder.append("\n").append("\n").bold(false);
        componentBuilder.append(LocaleLoader.getString("JSON.LevelRequirement") +": ").color(ccLevelRequirement);
        componentBuilder.append(String.valueOf(AdvancedConfig.getInstance().getSubSkillUnlockLevel(abstractSubSkill, 1))).color(ccLevelRequired);
    }

    @Deprecated
    private static BaseComponent[] getSubSkillHoverEventJSON(SubSkillType subSkillType, Player player, int curRank)
    {
        String skillName = subSkillType.getLocaleName();

        /*
         * Hover Event BaseComponent color table
         */
        ChatColor ccSubSkillHeader      = ChatColor.GOLD;
        ChatColor ccRank                = ChatColor.BLUE;
        ChatColor ccCurRank             = ChatColor.GREEN;
        ChatColor ccPossessive          = ChatColor.WHITE;
        ChatColor ccNumRanks            = ccCurRank;
        ChatColor ccDescriptionHeader   = ChatColor.DARK_PURPLE;
        ChatColor ccDescription         = ChatColor.DARK_GRAY;
        ChatColor ccLocked              = ChatColor.DARK_GRAY;
        ChatColor ccLevelRequirement    = ChatColor.BLUE;
        ChatColor ccLevelRequired       = ChatColor.RED;

        //SubSkillType Name
        ComponentBuilder componentBuilder = getNewComponentBuilder(skillName, ccSubSkillHeader);

        if(RankUtils.getRank(player, subSkillType) == 0)
        {
            //Skill is not unlocked yet
            componentBuilder.append(LocaleLoader.getString("JSON.Locked")).color(ccLocked).bold(true);
            componentBuilder.append("\n").append("\n").bold(false);
            componentBuilder.append(LocaleLoader.getString("JSON.LevelRequirement") +": ").color(ccLevelRequirement);
            componentBuilder.append(String.valueOf(AdvancedConfig.getInstance().getSubSkillUnlockLevel(subSkillType, 1))).color(ccLevelRequired);

        } else {
            //addSubSkillTypeToHoverEventJSON(subSkillType, componentBuilder);

            //RANK
            if(subSkillType.getNumRanks() > 0)
            {
                addRanked(ccRank, ccCurRank, ccPossessive, ccNumRanks, componentBuilder, subSkillType.getNumRanks(), RankUtils.getRank(player, subSkillType));

                //Empty line
                componentBuilder.append("\n").bold(false);
            }
        }

        componentBuilder.append(LocaleLoader.getString("JSON.DescriptionHeader"));
        componentBuilder.color(ccDescriptionHeader);
        componentBuilder.append("\n");
        componentBuilder.append(subSkillType.getLocaleDescription());
        componentBuilder.color(ccDescription);

        return componentBuilder.create();
    }

    private static void addSubSkillTypeToHoverEventJSON(AbstractSubSkill abstractSubSkill, ComponentBuilder componentBuilder)
    {
        if(abstractSubSkill.isSuperAbility())
        {
            componentBuilder.append(LocaleLoader.getString("JSON.Type.SuperAbility")).color(ChatColor.LIGHT_PURPLE);
            componentBuilder.bold(true);
        } else if(abstractSubSkill.isActiveUse())
        {
            componentBuilder.append(LocaleLoader.getString("JSON.Type.Active")).color(ChatColor.DARK_RED);
            componentBuilder.bold(true);
        } else {
            componentBuilder.append(LocaleLoader.getString("JSON.Type.Passive")).color(ChatColor.GREEN);
            componentBuilder.bold(true);
        }

        componentBuilder.append("\n");
    }

    public static void getSubSkillTextComponents(Player player, List<TextComponent> textComponents, PrimarySkill parentSkill) {
        for(SubSkillType subSkillType : SubSkillType.values())
        {
            if(subSkillType.getParentSkill() == parentSkill)
            {
                if(Permissions.isSubSkillEnabled(player, subSkillType))
                {
                    textComponents.add(TextComponentFactory.getSubSkillTextComponent(player, subSkillType));
                }
            }
        }

        /* NEW SKILL SYSTEM */
        for(AbstractSubSkill abstractSubSkill : InteractionManager.getSubSkillList())
        {
            if(abstractSubSkill.getPrimarySkill() == parentSkill)
            {
                if(Permissions.isSubSkillEnabled(player, abstractSubSkill))
                    textComponents.add(TextComponentFactory.getSubSkillTextComponent(player, abstractSubSkill));
            }
        }
    }
}


