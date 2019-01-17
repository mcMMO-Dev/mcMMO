package com.gmail.nossr50.util;

import com.gmail.nossr50.datatypes.json.McMMOWebLinks;
import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.config.RankConfig;
import com.gmail.nossr50.datatypes.interactions.NotificationType;
import com.gmail.nossr50.datatypes.json.McMMOUrl;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.datatypes.skills.subskills.AbstractSubSkill;
import com.gmail.nossr50.listeners.InteractionManager;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.skills.RankUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * This class handles many of the JSON components that mcMMO makes and uses
 */
public class TextComponentFactory {

    /**
     * Makes a text component using strings from a locale and supports passing an undefined number of variables to the LocaleLoader
     * @param localeKey target locale string address
     * @param notificationType type of notification
     * @param values vars to be passed to the locale loader
     * @return
     */
    public static TextComponent getNotificationMultipleValues(String localeKey, NotificationType notificationType, String... values)
    {
        String preColoredString = LocaleLoader.getString(localeKey, (Object[]) values);
        TextComponent msg = new TextComponent(preColoredString);
        return new TextComponent(msg);
    }

    public static TextComponent getNotificationTextComponentFromLocale(String localeKey, NotificationType notificationType)
    {
        return getNotificationTextComponent(LocaleLoader.getString(localeKey));
    }

    public static TextComponent getNotificationLevelUpTextComponent(McMMOPlayer player, PrimarySkillType skill, int currentLevel)
    {
        //player.sendMessage(LocaleLoader.getString(StringUtils.getCapitalized(primarySkill.toString()) + ".Skillup", levelsGained, getSkillLevel(primarySkill)));
        TextComponent textComponent = new TextComponent(LocaleLoader.getString("JSON."+StringUtils.getCapitalized(skill.toString()))
                +" "+LocaleLoader.getString("JSON.LevelUp"));
        textComponent.setColor(AdvancedConfig.getInstance().getJSONActionBarColor(NotificationType.LEVEL_UP_MESSAGE));
        TextComponent childComponent = new TextComponent(" "+currentLevel);
        //TODO: Config
        childComponent.setColor(ChatColor.GREEN);
        childComponent.setBold(true);
        textComponent.addExtra(childComponent);
        return textComponent;
    }

    private static TextComponent getNotificationTextComponent(String text)
    {
        //textComponent.setColor(getNotificationColor(notificationType));
        return new TextComponent(text);
    }

    public static void sendPlayerUrlHeader(Player player) {
        if(!Config.getInstance().getUrlLinksEnabled())
            return;

        Player.Spigot spigotPlayer = player.spigot();

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

        spigotPlayer.sendMessage(baseComponents);
    }

    public static void sendPlayerSubSkillList(Player player, List<TextComponent> textComponents)
    {
        TextComponent emptySpace = new TextComponent(" ");

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

    private static TextComponent getWebLinkTextComponent(McMMOWebLinks webLinks)
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

        addNewHoverComponentToTextComponent(webTextComponent, getUrlHoverEvent(webLinks));
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
                componentBuilder.append(webLinks.getLocaleDescription()).color(ChatColor.GREEN);
                componentBuilder.append("\nDev Blogs, and information related to mcMMO can be found here").color(ChatColor.GRAY);
                break;
            case SPIGOT:
                addUrlHeaderHover(webLinks, componentBuilder);
                componentBuilder.append("\n\n").italic(false);
                componentBuilder.append(webLinks.getLocaleDescription()).color(ChatColor.GREEN);
                componentBuilder.append("\nI post regularly in the discussion thread here!").color(ChatColor.GRAY);
                break;
            case PATREON:
                addUrlHeaderHover(webLinks, componentBuilder);
                componentBuilder.append("\n\n").italic(false);
                componentBuilder.append(webLinks.getLocaleDescription()).color(ChatColor.GREEN);
                componentBuilder.append("\n");
                componentBuilder.append("Show support by buying me a coffee :)").italic(false).color(ChatColor.GRAY);
                break;
            case WIKI:
                addUrlHeaderHover(webLinks, componentBuilder);
                componentBuilder.append("\n\n").italic(false);
                componentBuilder.append(webLinks.getLocaleDescription()).color(ChatColor.GREEN);
                componentBuilder.append("\n");
                componentBuilder.append("I'm looking for more wiki staff, contact me on our discord!").italic(false).color(ChatColor.DARK_GRAY);
                break;
            case DISCORD:
                addUrlHeaderHover(webLinks, componentBuilder);
                componentBuilder.append("\n\n").italic(false);
                componentBuilder.append(webLinks.getLocaleDescription()).color(ChatColor.GREEN);
                break;
            case HELP_TRANSLATE:
                addUrlHeaderHover(webLinks, componentBuilder);
                componentBuilder.append("\n\n").italic(false);
                componentBuilder.append(webLinks.getLocaleDescription()).color(ChatColor.GREEN);
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

    private static TextComponent getSubSkillTextComponent(Player player, SubSkillType subSkillType)
    {
        //Get skill name
        String skillName = subSkillType.getLocaleName();
        TextComponent textComponent;

        //Setup Text Component
        if(RankUtils.hasUnlockedSubskill(player, subSkillType))
        {
            textComponent = new TextComponent(skillName);
            textComponent.setColor(ChatColor.DARK_AQUA);
            textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mmoinfo "+subSkillType.getNiceNameNoSpaces(subSkillType)));
        }
        else {
            textComponent = new TextComponent("???");
            textComponent.setColor(ChatColor.DARK_GRAY);
            textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mmoinfo ???"));
        }

        //Hover Event
        addNewHoverComponentToTextComponent(textComponent, getSubSkillHoverComponent(player, subSkillType));

        //Insertion
        textComponent.setInsertion(skillName);

        return textComponent;
    }

    private static void addNewHoverComponentToTextComponent(TextComponent textComponent, BaseComponent[] baseComponent) {
        textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, baseComponent));
    }

    private static TextComponent getSubSkillTextComponent(Player player, AbstractSubSkill abstractSubSkill)
    {
        //String key = abstractSubSkill.getConfigKeyName();
        String skillName = abstractSubSkill.getNiceName();

        //Setup Text Component
        TextComponent textComponent;

        //Setup Text Component
        if(RankUtils.hasUnlockedSubskill(player, abstractSubSkill))
        {
            textComponent = new TextComponent(skillName);
            textComponent.setColor(ChatColor.DARK_AQUA);
            textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mmoinfo "+abstractSubSkill.getConfigKeyName()));
        }
        else {
            textComponent = new TextComponent("???");
            textComponent.setColor(ChatColor.DARK_GRAY);
            textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mmoinfo ???"));
        }

        //Hover Event
        addNewHoverComponentToTextComponent(textComponent, getSubSkillHoverComponent(player, abstractSubSkill));

        //Insertion
        textComponent.setInsertion(skillName);

        return textComponent;
    }

    private static BaseComponent[] getSubSkillHoverComponent(Player player, AbstractSubSkill abstractSubSkill)
    {
        return getSubSkillHoverEventJSON(abstractSubSkill, player);
    }

    private static BaseComponent[] getSubSkillHoverComponent(Player player, SubSkillType subSkillType)
    {
        return getSubSkillHoverEventJSON(subSkillType, player);
    }

    /**
     * Used for the skill in the new skill system (Deriving from AbstractSubSkill)
     * @param abstractSubSkill this subskill
     * @param player the player who owns this subskill
     * @return the hover basecomponent object for this subskill
     */
    private static BaseComponent[] getSubSkillHoverEventJSON(AbstractSubSkill abstractSubSkill, Player player)
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

        ComponentBuilder componentBuilder;

        //SubSkillType Name
        if(RankUtils.hasUnlockedSubskill(player, abstractSubSkill))
            componentBuilder = getNewComponentBuilder(skillName, ccSubSkillHeader);
        else
            componentBuilder = getNewComponentBuilder("???", ccSubSkillHeader);

        if(!RankUtils.hasUnlockedSubskill(player, abstractSubSkill))
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

    private static void addLocked(SubSkillType subSkillType, ChatColor ccLocked, ChatColor ccLevelRequirement, ChatColor ccLevelRequired, ComponentBuilder componentBuilder) {
        addLocked(ccLocked, ccLevelRequirement, componentBuilder);
        componentBuilder.append(String.valueOf(RankConfig.getInstance().getSubSkillUnlockLevel(subSkillType, 1))).color(ccLevelRequired);
        //componentBuilder.append("\n");
    }

    private static void addLocked(AbstractSubSkill abstractSubSkill, ChatColor ccLocked, ChatColor ccLevelRequirement, ChatColor ccLevelRequired, ComponentBuilder componentBuilder) {
        addLocked(ccLocked, ccLevelRequirement, componentBuilder);
        componentBuilder.append(String.valueOf(RankConfig.getInstance().getSubSkillUnlockLevel(abstractSubSkill, 1))).color(ccLevelRequired);
        //componentBuilder.append("\n");
    }

    private static void addLocked(ChatColor ccLocked, ChatColor ccLevelRequirement, ComponentBuilder componentBuilder) {
        componentBuilder.append(LocaleLoader.getString("JSON.Locked")).color(ccLocked).bold(true);
        componentBuilder.append("\n").append("\n").bold(false);
        componentBuilder.append(LocaleLoader.getString("JSON.LevelRequirement") + ": ").color(ccLevelRequirement);
    }

    @Deprecated
    private static BaseComponent[] getSubSkillHoverEventJSON(SubSkillType subSkillType, Player player)
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

        ComponentBuilder componentBuilder;

        //SubSkillType Name
        if(RankUtils.hasUnlockedSubskill(player, subSkillType))
            componentBuilder = getNewComponentBuilder(skillName, ccSubSkillHeader);
        else
            componentBuilder = getNewComponentBuilder("???", ccSubSkillHeader);

        if(!RankUtils.hasUnlockedSubskill(player, subSkillType))
        {
            //Skill is not unlocked yet
            addLocked(subSkillType, ccLocked, ccLevelRequirement, ccLevelRequired, componentBuilder);
        } else {
            //addSubSkillTypeToHoverEventJSON(subSkillType, componentBuilder);

            //RANK
            if(subSkillType.getNumRanks() > 0)
            {
                addRanked(ccRank, ccCurRank, ccPossessive, ccNumRanks, componentBuilder, subSkillType.getNumRanks(), RankUtils.getRank(player, subSkillType));

                //Empty line
                componentBuilder.append("\n").bold(false);
            }

            componentBuilder.append(LocaleLoader.getString("JSON.DescriptionHeader"));
            componentBuilder.color(ccDescriptionHeader);
            componentBuilder.append("\n");
            componentBuilder.append(subSkillType.getLocaleDescription());
            componentBuilder.color(ccDescription);
        }

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

    public static void getSubSkillTextComponents(Player player, List<TextComponent> textComponents, PrimarySkillType parentSkill) {
        for(SubSkillType subSkillType : SubSkillType.values())
        {
            if(subSkillType.getParentSkill() == parentSkill)
            {
                if(Permissions.isSubSkillEnabled(player, subSkillType))
                {
                    if(!InteractionManager.hasSubSkill(subSkillType))
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


