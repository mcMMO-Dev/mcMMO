package com.gmail.nossr50.util;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.config.RankConfig;
import com.gmail.nossr50.datatypes.json.McMMOUrl;
import com.gmail.nossr50.datatypes.json.McMMOWebLinks;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.datatypes.skills.subskills.AbstractSubSkill;
import com.gmail.nossr50.listeners.InteractionManager;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.skills.RankUtils;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.MessageType;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilder;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;

/**
 * This class handles many of the JSON components that mcMMO makes and uses
 */
public class TextComponentFactory {

    /**
     * Makes a text component using strings from a locale and supports passing an undefined number of variables to the LocaleLoader
     * @param localeKey target locale string address
     * @param values vars to be passed to the locale loader
     * @return
     */
    public static TextComponent getNotificationMultipleValues(String localeKey, String... values)
    {
        String preColoredString = LocaleLoader.getString(localeKey, (Object[]) values);
        return Component.text(preColoredString);
    }

    public static Component getNotificationTextComponentFromLocale(String localeKey)
    {
        return getNotificationTextComponent(LocaleLoader.getString(localeKey));
    }

    public static Component getNotificationLevelUpTextComponent(PrimarySkillType skill, int levelsGained, int currentLevel)
    {
        return Component.text(LocaleLoader.getString("Overhaul.Levelup", LocaleLoader.getString("Overhaul.Name."+StringUtils.getCapitalized(skill.toString())), levelsGained, currentLevel));
    }

    private static TextComponent getNotificationTextComponent(String text)
    {
        //textComponent.setColor(getNotificationColor(notificationType));
        return Component.text(text);
    }

    public static void sendPlayerSubSkillWikiLink(Player player, String subskillformatted)
    {
        if(!Config.getInstance().getUrlLinksEnabled())
            return;

        TextComponent.Builder wikiLinkComponent = Component.text().content(LocaleLoader.getString("Overhaul.mcMMO.MmoInfo.Wiki"));
        wikiLinkComponent.decoration(TextDecoration.UNDERLINED, true);

        String wikiUrl = "https://mcmmo.org/wiki/"+subskillformatted;

        wikiLinkComponent.clickEvent(ClickEvent.openUrl(wikiUrl));

        TextComponent.Builder componentBuilder = Component.text().content(subskillformatted).append(Component.newline()).append(Component.text(wikiUrl)).color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, true);

        wikiLinkComponent.hoverEvent(HoverEvent.showText(componentBuilder.build()));

        mcMMO.getAudiences().player(player).sendMessage(Identity.nil(), wikiLinkComponent, MessageType.SYSTEM);
    }

    public static void sendPlayerUrlHeader(Player player) {
        TextComponent prefix = Component.text(LocaleLoader.getString("Overhaul.mcMMO.Url.Wrap.Prefix") + " ");
        /*prefix.setColor(ChatColor.DARK_AQUA);*/
        TextComponent suffix = Component.text(" "+LocaleLoader.getString("Overhaul.mcMMO.Url.Wrap.Suffix"));
        /*suffix.setColor(ChatColor.DARK_AQUA);*/

        TextComponent emptySpace = Component.space();

        mcMMO.getAudiences().player(player).sendMessage(Identity.nil(),TextComponent.ofChildren(
          prefix,
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
          suffix
        ), MessageType.SYSTEM);
    }

    public static void sendPlayerSubSkillList(Player player, List<Component> textComponents)
    {
        TextComponent emptySpace = Component.space();

        AtomicReference<Component> messageToSend = new AtomicReference<>();
        int newLineCount = 0; //Hacky solution to wordwrap problems

        final Audience audience = mcMMO.getAudiences().player(player);
        for (Component textComponent : textComponents) {
            //Don't send more than 3 subskills per line to avoid MOST wordwrap problems
            if(newLineCount > 2)
            {
                Component toSend = messageToSend.get();
                if (toSend != null) {
                    audience.sendMessage(Identity.nil(), toSend.append(emptySpace));
                }

                messageToSend.set(null);
                newLineCount = 0;
            }
            //Style the skills into @links
            final String originalTxt = textComponent instanceof TextComponent ? ((TextComponent) textComponent).content() : "";

            TextComponent.Builder stylizedText = Component.text().content(LocaleLoader.getString("JSON.Hover.AtSymbolSkills"));
            addChild(stylizedText, originalTxt);

            if(textComponent.hoverEvent() != null)
                stylizedText.hoverEvent(textComponent.hoverEvent());

            if(textComponent.clickEvent() != null)
                stylizedText.clickEvent(textComponent.clickEvent());

            messageToSend.set(stylizedText.build().append(emptySpace));

            newLineCount++;
        }

        Component toSend = messageToSend.get();
        if (toSend != null) {
            audience.sendMessage(Identity.nil(), toSend.append(emptySpace));
        }
    }

    private static Component getWebLinkTextComponent(McMMOWebLinks webLinks)
    {
        TextComponent.Builder webTextComponent;

        switch(webLinks)
        {
            case WEBSITE:
                webTextComponent = Component.text().content(LocaleLoader.getString("JSON.Hover.AtSymbolURL"));
                addChild(webTextComponent, "Web");
                webTextComponent.clickEvent(getUrlClickEvent(McMMOUrl.urlWebsite));
                break;
            case SPIGOT:
                webTextComponent = Component.text().content(LocaleLoader.getString("JSON.Hover.AtSymbolURL"));
                addChild(webTextComponent, "Spigot");
                webTextComponent.clickEvent(getUrlClickEvent(McMMOUrl.urlSpigot));
                break;
            case DISCORD:
                webTextComponent = Component.text().content(LocaleLoader.getString("JSON.Hover.AtSymbolURL"));
                addChild(webTextComponent, "Discord");
                webTextComponent.clickEvent(getUrlClickEvent(McMMOUrl.urlDiscord));
                break;
            case PATREON:
                webTextComponent = Component.text().content(LocaleLoader.getString("JSON.Hover.AtSymbolURL"));
                addChild(webTextComponent, "Patreon");
                webTextComponent.clickEvent(getUrlClickEvent(McMMOUrl.urlPatreon));
                break;
            case WIKI:
                webTextComponent = Component.text().content(LocaleLoader.getString("JSON.Hover.AtSymbolURL"));
                addChild(webTextComponent, "Wiki");
                webTextComponent.clickEvent(getUrlClickEvent(McMMOUrl.urlWiki));
                break;
            case HELP_TRANSLATE:
                webTextComponent = Component.text().content(LocaleLoader.getString("JSON.Hover.AtSymbolURL"));
                addChild(webTextComponent, "Lang");
                webTextComponent.clickEvent(getUrlClickEvent(McMMOUrl.urlTranslate));
                break;
            default:
                webTextComponent = Component.text().content("NOT DEFINED");
        }

        addNewHoverComponentToTextComponent(webTextComponent, getUrlHoverEvent(webLinks));
        webTextComponent.insertion(webLinks.getUrl());

        return webTextComponent.build();
    }

    private static void addChild(Component webTextComponent, String childName) {
        TextComponent childComponent = Component.text(childName);
        childComponent.color(NamedTextColor.BLUE);
        webTextComponent.append(childComponent);
    }

    private static void addChild(ComponentBuilder<?, ?> webTextComponent, String childName) {
        TextComponent childComponent = Component.text(childName);
        childComponent.color(NamedTextColor.BLUE);
        webTextComponent.append(childComponent);
    }

    private static Component getUrlHoverEvent(McMMOWebLinks webLinks)
    {
        TextComponent.Builder componentBuilder = Component.text().content(webLinks.getNiceTitle());

        switch(webLinks)
        {
            case WEBSITE:
                addUrlHeaderHover(webLinks, componentBuilder);
                componentBuilder.append(Component.newline()).append(Component.newline());
                componentBuilder.append(Component.text(webLinks.getLocaleDescription(), NamedTextColor.GREEN));
                componentBuilder.append(Component.text("\nDev Blogs, and information related to mcMMO can be found here", NamedTextColor.GRAY));
                break;
            case SPIGOT:
                addUrlHeaderHover(webLinks, componentBuilder);
                componentBuilder.append(Component.newline()).append(Component.newline());
                componentBuilder.append(Component.text(webLinks.getLocaleDescription(), NamedTextColor.GREEN));
                componentBuilder.append(Component.text("\nI post regularly in the discussion thread here!", NamedTextColor.GRAY));
                break;
            case PATREON:
                addUrlHeaderHover(webLinks, componentBuilder);
                componentBuilder.append(Component.newline()).append(Component.newline());
                componentBuilder.append(Component.text(webLinks.getLocaleDescription(), NamedTextColor.GREEN));
                componentBuilder.append(Component.newline());
                componentBuilder.append(Component.text("Show support by buying me a coffee :)", NamedTextColor.GRAY));
                break;
            case WIKI:
                addUrlHeaderHover(webLinks, componentBuilder);
                componentBuilder.append(Component.newline()).append(Component.newline());
                componentBuilder.append(Component.text(webLinks.getLocaleDescription(), NamedTextColor.GREEN));
                componentBuilder.append(Component.newline());
                componentBuilder.append(Component.text("I'm looking for more wiki staff, contact me on our discord!", NamedTextColor.DARK_GRAY));
                break;
            case DISCORD:
                addUrlHeaderHover(webLinks, componentBuilder);
                componentBuilder.append(Component.newline()).append(Component.newline());
                componentBuilder.append(Component.text(webLinks.getLocaleDescription(), NamedTextColor.GREEN));
                break;
            case HELP_TRANSLATE:
                addUrlHeaderHover(webLinks, componentBuilder);
                componentBuilder.append(Component.newline()).append(Component.newline());
                componentBuilder.append(Component.text(webLinks.getLocaleDescription(), NamedTextColor.GREEN));
                componentBuilder.append(Component.newline());
                componentBuilder.append(Component.text("You can use this website to help translate mcMMO into your language!" +
                  "\nIf you want to know more contact me in discord.", NamedTextColor.DARK_GRAY));
        }

        return componentBuilder.build();
    }

    private static void addUrlHeaderHover(McMMOWebLinks webLinks, TextComponent.Builder componentBuilder) {
        componentBuilder.append(Component.newline());
        componentBuilder.append(Component.text(webLinks.getUrl(), NamedTextColor.GRAY, TextDecoration.ITALIC));
    }

    private static ClickEvent getUrlClickEvent(String url)
    {
        return ClickEvent.openUrl(url);
    }

    private static Component getSubSkillTextComponent(Player player, SubSkillType subSkillType)
    {
        //Get skill name
        String skillName = subSkillType.getLocaleName();

        boolean skillUnlocked = RankUtils.hasUnlockedSubskill(player, subSkillType);

        TextComponent.Builder textComponent = initNewSkillTextComponent(player, skillName, subSkillType, skillUnlocked);

        //Hover Event
        addNewHoverComponentToTextComponent(textComponent, getSubSkillHoverComponent(player, subSkillType));

        //Insertion
        textComponent.insertion(skillName);

        return textComponent.build();
    }

    private static void addNewHoverComponentToTextComponent(TextComponent.Builder textComponent, Component baseComponent) {
        textComponent.hoverEvent(HoverEvent.showText(baseComponent));
    }

    private static TextComponent getSubSkillTextComponent(Player player, AbstractSubSkill abstractSubSkill)
    {
        //String key = abstractSubSkill.getConfigKeyName();
        String skillName = abstractSubSkill.getNiceName();

        //Setup Text Component
        SubSkillType subSkillType = abstractSubSkill.getSubSkillType();

        boolean skillUnlocked = RankUtils.hasUnlockedSubskill(player, subSkillType);

        TextComponent.Builder textComponent = initNewSkillTextComponent(player, skillName, subSkillType, skillUnlocked);

        //Hover Event
        addNewHoverComponentToTextComponent(textComponent, getSubSkillHoverComponent(player, abstractSubSkill));

        //Insertion
        textComponent.insertion(skillName);

        return textComponent.build();
    }

    private static TextComponent.Builder initNewSkillTextComponent(Player player, String skillName, SubSkillType subSkillType, boolean skillUnlocked) {
        TextComponent.Builder textComponent;
        if (skillUnlocked) {
            if (RankUtils.getHighestRank(subSkillType) == RankUtils.getRank(player, subSkillType) && subSkillType.getNumRanks() > 1)
                textComponent = Component.text().content(LocaleLoader.getString("JSON.Hover.MaxRankSkillName", skillName));
            else
                textComponent = Component.text().content(LocaleLoader.getString("JSON.Hover.SkillName", skillName));

            textComponent.clickEvent(ClickEvent.runCommand("/mmoinfo " + subSkillType.getNiceNameNoSpaces(subSkillType)));

        } else {
            textComponent = Component.text().content(LocaleLoader.getString("JSON.Hover.Mystery",
                    String.valueOf(RankUtils.getUnlockLevel(subSkillType))));

            textComponent.clickEvent(ClickEvent.runCommand("/mmoinfo ???"));
        }
        return textComponent;
    }

    private static Component getSubSkillHoverComponent(Player player, AbstractSubSkill abstractSubSkill)
    {
        return getSubSkillHoverEventJSON(abstractSubSkill, player);
    }

    private static Component getSubSkillHoverComponent(Player player, SubSkillType subSkillType)
    {
        return getSubSkillHoverEventJSON(subSkillType, player);
    }

    /**
     * Used for the skill in the new skill system (Deriving from AbstractSubSkill)
     * @param abstractSubSkill this subskill
     * @param player the player who owns this subskill
     * @return the hover basecomponent object for this subskill
     */
    private static Component getSubSkillHoverEventJSON(AbstractSubSkill abstractSubSkill, Player player)
    {
        String skillName = abstractSubSkill.getNiceName();

        /*
         * Hover Event BaseComponent color table
         */
        TextColor ccSubSkillHeader      = NamedTextColor.GOLD;
        TextColor ccRank                = NamedTextColor.BLUE;
        TextColor ccCurRank             = NamedTextColor.GREEN;
        TextColor ccPossessive          = NamedTextColor.WHITE;
        //ChatColor ccDescriptionHeader   = ChatColor.DARK_PURPLE;
        //ChatColor ccDescription         = ChatColor.WHITE;
        TextColor ccLocked              = NamedTextColor.DARK_GRAY;
        TextColor ccLevelRequirement    = NamedTextColor.BLUE;
        TextColor ccLevelRequired       = NamedTextColor.RED;

        SubSkillType subSkillType = abstractSubSkill.getSubSkillType();

        //SubSkillType Name
        TextComponent.Builder componentBuilder = setupSkillComponentNameStyle(player, skillName, subSkillType, RankUtils.hasUnlockedSubskill(player, abstractSubSkill));

        if(!RankUtils.hasUnlockedSubskill(player, abstractSubSkill))
        {
            //Skill is not unlocked yet
            addLocked(abstractSubSkill, ccLocked, ccLevelRequirement, ccLevelRequired, componentBuilder);
        } else {
            addSubSkillTypeToHoverEventJSON(abstractSubSkill, componentBuilder);

            //RANK
            int curRank = RankUtils.getRank(player, abstractSubSkill);
            int nextRank = 0;

            if(curRank < abstractSubSkill.getNumRanks() && abstractSubSkill.getNumRanks() > 0)
            {
                nextRank = RankUtils.getRankUnlockLevel(abstractSubSkill, curRank+1);
            }

            addRanked(ccRank, ccCurRank, ccPossessive, ccCurRank, componentBuilder, abstractSubSkill.getNumRanks(), RankUtils.getRank(player, abstractSubSkill), nextRank);

            componentBuilder.append(Component.text(LocaleLoader.getString("JSON.DescriptionHeader")));
            componentBuilder.append(Component.newline()).append(Component.text(abstractSubSkill.getDescription())).append(Component.newline());

            //Empty line
            componentBuilder.append(Component.newline()).decoration(TextDecoration.BOLD, false);
            componentBuilder.append(Component.newline());

            //Finally, add details to the tooltip
            abstractSubSkill.addStats(componentBuilder, player);
        }

        return componentBuilder.build();
    }

    private static TextComponent.Builder setupSkillComponentNameStyle(Player player, String skillName, SubSkillType subSkillType, boolean skillUnlocked) {
        TextComponent.Builder componentBuilder;
        if (skillUnlocked) {
            if (RankUtils.getHighestRank(subSkillType) == RankUtils.getRank(player, subSkillType) && subSkillType.getNumRanks() > 1)
                componentBuilder = getNewComponentBuilder(LocaleLoader.getString("JSON.Hover.MaxRankSkillName", skillName));
            else
                componentBuilder = getNewComponentBuilder(LocaleLoader.getString("JSON.Hover.SkillName", skillName));
        } else
            componentBuilder = getNewComponentBuilder(LocaleLoader.getString("JSON.Hover.Mystery",
                    String.valueOf(RankUtils.getUnlockLevel(subSkillType))));
        return componentBuilder;
    }

    private static TextComponent.Builder getNewComponentBuilder(String skillName) {
        TextComponent.Builder componentBuilder = Component.text().content(skillName);
        componentBuilder.append(Component.newline());
        return componentBuilder;
    }

    private static void addRanked(TextColor ccRank, TextColor ccCurRank, TextColor ccPossessive, TextColor ccNumRanks, TextComponent.Builder componentBuilder, int numRanks, int rank, int nextRank) {
        if (numRanks > 0) {
            //Rank: x
            componentBuilder.append(Component.text(LocaleLoader.getString("JSON.Hover.Rank", String.valueOf(rank)))).append(Component.newline());

            //Next Rank: x
            if(nextRank > rank)
                componentBuilder.append(Component.text(LocaleLoader.getString("JSON.Hover.NextRank", String.valueOf(nextRank)))).append(Component.newline());

            /*componentBuilder.append(" " + LocaleLoader.getString("JSON.RankPossesive") + " ").color(ccPossessive);
            componentBuilder.append(String.valueOf(numRanks)).color(ccNumRanks);*/
        }
    }

    private static void addLocked(SubSkillType subSkillType, TextColor ccLocked, TextColor ccLevelRequirement, TextColor ccLevelRequired, TextComponent.Builder componentBuilder) {
        addLocked(ccLocked, ccLevelRequirement, componentBuilder);
        componentBuilder.append(Component.text(String.valueOf(RankConfig.getInstance().getSubSkillUnlockLevel(subSkillType, 1)), ccLevelRequired));
        //componentBuilder.append(Component.newline());
    }

    private static void addLocked(AbstractSubSkill abstractSubSkill, TextColor ccLocked, TextColor ccLevelRequirement, TextColor ccLevelRequired, TextComponent.Builder componentBuilder) {
        addLocked(ccLocked, ccLevelRequirement, componentBuilder);
        componentBuilder.append(Component.text(String.valueOf(RankConfig.getInstance().getSubSkillUnlockLevel(abstractSubSkill, 1)), ccLevelRequired));
        //componentBuilder.append(Component.newline());
    }

    private static void addLocked(TextColor ccLocked, TextColor ccLevelRequirement, TextComponent.Builder componentBuilder) {
        componentBuilder.append(Component.text(LocaleLoader.getString("JSON.Locked"), ccLocked, TextDecoration.BOLD));
        componentBuilder.append(Component.newline()).append(Component.newline());
        componentBuilder.append(Component.text(LocaleLoader.getString("JSON.LevelRequirement") + ": ", ccLevelRequirement));
    }

    @Deprecated
    private static Component getSubSkillHoverEventJSON(SubSkillType subSkillType, Player player)
    {
        String skillName = subSkillType.getLocaleName();

        /*
         * Hover Event BaseComponent color table
         */
        TextColor ccSubSkillHeader      = NamedTextColor.GOLD;
        TextColor ccRank                = NamedTextColor.BLUE;
        TextColor ccCurRank             = NamedTextColor.GREEN;
        TextColor ccPossessive          = NamedTextColor.WHITE;
        TextColor ccDescriptionHeader   = NamedTextColor.DARK_PURPLE;
        TextColor ccDescription         = NamedTextColor.DARK_GRAY;
        TextColor ccLocked              = NamedTextColor.DARK_GRAY;
        TextColor ccLevelRequirement    = NamedTextColor.BLUE;
        TextColor ccLevelRequired       = NamedTextColor.RED;

        //SubSkillType Name
        TextComponent.Builder componentBuilder = setupSkillComponentNameStyle(player, skillName, subSkillType, RankUtils.hasUnlockedSubskill(player, subSkillType));

        if(!RankUtils.hasUnlockedSubskill(player, subSkillType))
        {
            //Skill is not unlocked yet
            addLocked(subSkillType, ccLocked, ccLevelRequirement, ccLevelRequired, componentBuilder);
        } else {
            //addSubSkillTypeToHoverEventJSON(subSkillType, componentBuilder);

            //RANK
            if(subSkillType.getNumRanks() > 0)
            {
                int curRank = RankUtils.getRank(player, subSkillType);
                int nextRank = 0;

                if(curRank < subSkillType.getNumRanks())
                {
                    nextRank = RankUtils.getRankUnlockLevel(subSkillType, curRank+1);
                }

                addRanked(ccRank, ccCurRank, ccPossessive, ccCurRank, componentBuilder, subSkillType.getNumRanks(), RankUtils.getRank(player, subSkillType), nextRank);

            }

            componentBuilder.append(Component.newline());
            componentBuilder.append(Component.text(LocaleLoader.getString("JSON.DescriptionHeader")));
            componentBuilder.color(ccDescriptionHeader);
            componentBuilder.append(Component.newline());
            componentBuilder.append(Component.text(subSkillType.getLocaleDescription()));
            componentBuilder.color(ccDescription);
        }

        return componentBuilder.build();
    }

    private static void addSubSkillTypeToHoverEventJSON(AbstractSubSkill abstractSubSkill, TextComponent.Builder componentBuilder)
    {
        if(abstractSubSkill.isSuperAbility())
        {
            componentBuilder.append(Component.text(LocaleLoader.getString("JSON.Type.SuperAbility"), NamedTextColor.LIGHT_PURPLE, TextDecoration.BOLD));
        } else if(abstractSubSkill.isActiveUse())
        {
            componentBuilder.append(Component.text(LocaleLoader.getString("JSON.Type.Active"), NamedTextColor.DARK_RED, TextDecoration.BOLD));
        } else {
            componentBuilder.append(Component.text(LocaleLoader.getString("JSON.Type.Passive"), NamedTextColor.GREEN, TextDecoration.BOLD));
        }

        componentBuilder.append(Component.newline());
    }

    public static void getSubSkillTextComponents(Player player, List<Component> textComponents, PrimarySkillType parentSkill) {
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

    public static TextComponent getSubSkillUnlockedNotificationComponents(Player player, SubSkillType subSkillType)
    {
        TextComponent.Builder unlockMessage = Component.text().content(LocaleLoader.getString("JSON.SkillUnlockMessage", subSkillType.getLocaleName(), RankUtils.getRank(player, subSkillType)));
        unlockMessage.hoverEvent(HoverEvent.showText(getSubSkillHoverComponent(player, subSkillType)));
        unlockMessage.clickEvent(ClickEvent.runCommand("/"+subSkillType.getParentSkill().toString().toLowerCase(Locale.ENGLISH)));
        return unlockMessage.build();
    }
}


