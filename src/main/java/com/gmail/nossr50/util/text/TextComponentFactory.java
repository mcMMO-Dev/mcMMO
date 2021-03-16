package com.gmail.nossr50.util.text;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.config.RankConfig;
import com.gmail.nossr50.datatypes.json.McMMOUrl;
import com.gmail.nossr50.datatypes.json.McMMOWebLinks;
import com.gmail.nossr50.datatypes.skills.CoreSkills;
import com.gmail.nossr50.util.Misc;
import com.neetgames.mcmmo.player.OnlineMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.datatypes.skills.subskills.AbstractSubSkill;
import com.gmail.nossr50.listeners.InteractionManager;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.skills.RankUtils;
import com.neetgames.mcmmo.skill.RootSkill;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.MessageType;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * This class handles many of the JSON components that mcMMO makes and uses
 */
public class TextComponentFactory {

    /**
     * Makes a text component using strings from a locale and supports passing an undefined number of variables to the LocaleLoader
     * @param localeKey target locale string address
     * @param values vars to be passed to the locale loader
     *
     * @return
     */
    public static TextComponent getNotificationMultipleValues(@NotNull String localeKey, String... values)
    {
        String preColoredString = LocaleLoader.getString(localeKey, (Object[]) values);
        return Component.text(preColoredString);
    }

    public static Component getNotificationTextComponentFromLocale(@NotNull String localeKey)
    {
        return getNotificationTextComponent(LocaleLoader.getString(localeKey));
    }

    public static Component getNotificationLevelUpTextComponent(@NotNull PrimarySkillType skill, int levelsGained, int currentLevel)
    {
        return Component.text(LocaleLoader.getString("Overhaul.Levelup", LocaleLoader.getString("Overhaul.Name."+ StringUtils.getCapitalized(skill.toString())), levelsGained, currentLevel));
    }

    private static TextComponent getNotificationTextComponent(@NotNull String text)
    {
        return Component.text(text);
    }

    public static void sendPlayerSubSkillWikiLink(OnlineMMOPlayer mmoPlayer, String subskillformatted)
    {
        if(!Config.getInstance().getUrlLinksEnabled())
            return;

        TextComponent.Builder wikiLinkComponent = Component.text().content(LocaleLoader.getString("Overhaul.mcMMO.MmoInfo.Wiki"));
        wikiLinkComponent.decoration(TextDecoration.UNDERLINED, true);

        String wikiUrl = "https://mcmmo.org/wiki/"+subskillformatted;

        wikiLinkComponent.clickEvent(ClickEvent.openUrl(wikiUrl));

        TextComponent.Builder componentBuilder = Component.text().content(subskillformatted).append(Component.newline()).append(Component.text(wikiUrl)).color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, true);

        wikiLinkComponent.hoverEvent(HoverEvent.showText(componentBuilder.build()));

        mcMMO.getAudiences().player(mmoPlayer.getPlayer()).sendMessage(Identity.nil(), wikiLinkComponent, MessageType.SYSTEM);
    }

    public static void sendPlayerUrlHeader(@NotNull OnlineMMOPlayer mmoPlayer) {
        TextComponent prefix = Component.text(LocaleLoader.getString("Overhaul.mcMMO.Url.Wrap.Prefix") + " ");
        /*prefix.setColor(ChatColor.DARK_AQUA);*/
        TextComponent suffix = Component.text(" "+LocaleLoader.getString("Overhaul.mcMMO.Url.Wrap.Suffix"));
        /*suffix.setColor(ChatColor.DARK_AQUA);*/

        TextComponent emptySpace = Component.space();

        mcMMO.getAudiences().player(mmoPlayer.getPlayer()).sendMessage(Identity.nil(),TextComponent.ofChildren(
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

    /**
     * Sends a player a bunch of text components that represent a list of sub-skills
     * Styling and formatting is applied before sending the messages
     *
     * @param player target player
     * @param subSkillComponents the text components representing the sub-skills by name
     */
    public static void sendPlayerSubSkillList(@NotNull Player player, @NotNull List<Component> subSkillComponents) {
        final Audience audience = mcMMO.getAudiences().player(player);

        //@ Signs, done for style
        Component space = Component.space();
        TextComponent atSignComponent = Component.text(LocaleLoader.getString("JSON.Hover.AtSymbolSkills"));

        //Only send 3 sub-skills per line
        Component[][] splitSubSkills = TextUtils.splitComponentsIntoGroups(subSkillComponents, 3);
        ArrayList<Component> individualLinesToSend = new ArrayList<>();

        //Create each line
        for (Component[] componentArray : splitSubSkills) {
            individualLinesToSend.add(TextUtils.fromArray(componentArray, atSignComponent, space));
        }

        //Send each group
        for(Component curLine : individualLinesToSend) {
            audience.sendMessage(Identity.nil(), curLine);
        }
    }

    private static Component getWebLinkTextComponent(@NotNull McMMOWebLinks webLinks)
    {
        TextComponent.Builder webTextComponent;

        switch(webLinks)
        {
            case WEBSITE:
                webTextComponent = Component.text().content(LocaleLoader.getString("JSON.Hover.AtSymbolURL"));
                TextUtils.addChildWebComponent(webTextComponent, "Web");
                webTextComponent.clickEvent(getUrlClickEvent(McMMOUrl.urlWebsite));
                break;
            case SPIGOT:
                webTextComponent = Component.text().content(LocaleLoader.getString("JSON.Hover.AtSymbolURL"));
                TextUtils.addChildWebComponent(webTextComponent, "Spigot");
                webTextComponent.clickEvent(getUrlClickEvent(McMMOUrl.urlSpigot));
                break;
            case DISCORD:
                webTextComponent = Component.text().content(LocaleLoader.getString("JSON.Hover.AtSymbolURL"));
                TextUtils.addChildWebComponent(webTextComponent, "Discord");
                webTextComponent.clickEvent(getUrlClickEvent(McMMOUrl.urlDiscord));
                break;
            case PATREON:
                webTextComponent = Component.text().content(LocaleLoader.getString("JSON.Hover.AtSymbolURL"));
                TextUtils.addChildWebComponent(webTextComponent, "Patreon");
                webTextComponent.clickEvent(getUrlClickEvent(McMMOUrl.urlPatreon));
                break;
            case WIKI:
                webTextComponent = Component.text().content(LocaleLoader.getString("JSON.Hover.AtSymbolURL"));
                TextUtils.addChildWebComponent(webTextComponent, "Wiki");
                webTextComponent.clickEvent(getUrlClickEvent(McMMOUrl.urlWiki));
                break;
            case HELP_TRANSLATE:
                webTextComponent = Component.text().content(LocaleLoader.getString("JSON.Hover.AtSymbolURL"));
                TextUtils.addChildWebComponent(webTextComponent, "Lang");
                webTextComponent.clickEvent(getUrlClickEvent(McMMOUrl.urlTranslate));
                break;
            default:
                webTextComponent = Component.text().content("NOT DEFINED");
        }

        TextUtils.addNewHoverComponentToTextComponent(webTextComponent, getUrlHoverEvent(webLinks));
        webTextComponent.insertion(webLinks.getUrl());

        return webTextComponent.build();
    }

    private static Component getUrlHoverEvent(@NotNull McMMOWebLinks webLinks)
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

    private static void addUrlHeaderHover(@NotNull McMMOWebLinks webLinks, @NotNull TextComponent.Builder componentBuilder) {
        componentBuilder.append(Component.newline());
        componentBuilder.append(Component.text(webLinks.getUrl(), NamedTextColor.GRAY, TextDecoration.ITALIC));
    }

    private static ClickEvent getUrlClickEvent(String url)
    {
        return ClickEvent.openUrl(url);
    }

    private static Component getSubSkillTextComponent(@NotNull OnlineMMOPlayer mmoPlayer, @NotNull SubSkillType subSkillType)
    {
        //Get skill name
        String skillName = subSkillType.getLocaleName();

        boolean skillUnlocked = RankUtils.hasUnlockedSubskill(mmoPlayer, subSkillType);

        TextComponent.Builder textComponent = initNewSkillTextComponent(mmoPlayer, skillName, subSkillType, skillUnlocked);

        //Hover Event
        TextUtils.addNewHoverComponentToTextComponent(textComponent, getSubSkillHoverComponent(mmoPlayer, subSkillType));

        //Insertion
        textComponent.insertion(skillName);

        return textComponent.build();
    }

    private static TextComponent getSubSkillTextComponent(@NotNull OnlineMMOPlayer mmoPlayer, @NotNull AbstractSubSkill abstractSubSkill)
    {
        //String key = abstractSubSkill.getConfigKeyName();
        String skillName = abstractSubSkill.getNiceName();

        //Setup Text Component
        SubSkillType subSkillType = abstractSubSkill.getSubSkillType();

        boolean skillUnlocked = RankUtils.hasUnlockedSubskill(mmoPlayer, subSkillType);

        TextComponent.Builder textComponent = initNewSkillTextComponent(mmoPlayer, skillName, subSkillType, skillUnlocked);

        //Hover Event
        TextUtils.addNewHoverComponentToTextComponent(textComponent, getSubSkillHoverComponent(mmoPlayer, abstractSubSkill));

        //Insertion
        textComponent.insertion(skillName);

        return textComponent.build();
    }

    private static TextComponent.Builder initNewSkillTextComponent(@NotNull OnlineMMOPlayer mmoPlayer, @NotNull String skillName, @NotNull SubSkillType subSkillType, boolean skillUnlocked) {
        TextComponent.Builder textComponent;
        if (skillUnlocked) {
            if (RankUtils.getHighestRank(subSkillType) == RankUtils.getRank(mmoPlayer, subSkillType) && subSkillType.getNumRanks() > 1)
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

    private static Component getSubSkillHoverComponent(@NotNull OnlineMMOPlayer mmoPlayer, @NotNull AbstractSubSkill abstractSubSkill)
    {
        return getSubSkillHoverEventJSON(abstractSubSkill, mmoPlayer);
    }

    private static Component getSubSkillHoverComponent(@NotNull OnlineMMOPlayer mmoPlayer, SubSkillType subSkillType)
    {
        return getSubSkillHoverEventJSON(subSkillType, mmoPlayer);
    }

    /**
     * Used for the skill in the new skill system (Deriving from AbstractSubSkill)
     * @param abstractSubSkill this subskill
     * @param mmoPlayer the player who owns this subskill
     * @return the hover basecomponent object for this subskill
     */
    private static Component getSubSkillHoverEventJSON(@NotNull AbstractSubSkill abstractSubSkill, @NotNull OnlineMMOPlayer mmoPlayer)
    {
        String skillName = abstractSubSkill.getNiceName();

        /*
         * Hover Event BaseComponent color table
         */
        TextColor ccLocked              = NamedTextColor.DARK_GRAY;
        TextColor ccLevelRequirement    = NamedTextColor.BLUE;
        TextColor ccLevelRequired       = NamedTextColor.RED;

        SubSkillType subSkillType = abstractSubSkill.getSubSkillType();

        //SubSkillType Name
        TextComponent.Builder componentBuilder = setupSkillComponentNameStyle(mmoPlayer, skillName, subSkillType, RankUtils.hasUnlockedSubskill(mmoPlayer, abstractSubSkill));

        if(!RankUtils.hasUnlockedSubskill(mmoPlayer, abstractSubSkill))
        {
            //Skill is not unlocked yet
            addLocked(abstractSubSkill, ccLocked, ccLevelRequirement, ccLevelRequired, componentBuilder);
        } else {
            addSubSkillTypeToHoverEventJSON(abstractSubSkill, componentBuilder);

            //RANK
            int curRank = RankUtils.getRank(mmoPlayer, abstractSubSkill);
            int nextRank = 0;

            if(curRank < abstractSubSkill.getNumRanks() && abstractSubSkill.getNumRanks() > 0)
            {
                nextRank = RankUtils.getRankUnlockLevel(abstractSubSkill, curRank+1);
            }

            addRanked(componentBuilder, abstractSubSkill.getNumRanks(), RankUtils.getRank(mmoPlayer, abstractSubSkill), nextRank);

            componentBuilder.append(Component.text(LocaleLoader.getString("JSON.DescriptionHeader")));
            componentBuilder.append(Component.newline()).append(Component.text(abstractSubSkill.getDescription())).append(Component.newline());

            //Empty line
            componentBuilder.append(Component.newline()).decoration(TextDecoration.BOLD, false);
            componentBuilder.append(Component.newline());

            //Finally, add details to the tooltip
            abstractSubSkill.addStats(componentBuilder, mmoPlayer);
        }

        return componentBuilder.build();
    }

    private static TextComponent.Builder setupSkillComponentNameStyle(@NotNull OnlineMMOPlayer mmoPlayer, @NotNull String skillName, @NotNull SubSkillType subSkillType, boolean skillUnlocked) {
        TextComponent.Builder componentBuilder;
        if (skillUnlocked) {
            if (RankUtils.getHighestRank(subSkillType) == RankUtils.getRank(mmoPlayer, subSkillType) && subSkillType.getNumRanks() > 1)
                componentBuilder = getNewComponentBuilder(LocaleLoader.getString("JSON.Hover.MaxRankSkillName", skillName));
            else
                componentBuilder = getNewComponentBuilder(LocaleLoader.getString("JSON.Hover.SkillName", skillName));
        } else
            componentBuilder = getNewComponentBuilder(LocaleLoader.getString("JSON.Hover.Mystery",
                    String.valueOf(RankUtils.getUnlockLevel(subSkillType))));
        return componentBuilder;
    }

    private static TextComponent.Builder getNewComponentBuilder(@NotNull String skillName) {
        TextComponent.Builder componentBuilder = Component.text().content(skillName);
        componentBuilder.append(Component.newline());
        return componentBuilder;
    }

    private static void addRanked(@NotNull TextComponent.Builder componentBuilder, int numRanks, int rank, int nextRank) {
        if (numRanks > 0) {
            //Rank: x
            componentBuilder.append(Component.text(LocaleLoader.getString("JSON.Hover.Rank", String.valueOf(rank)))).append(Component.newline());

            //Next Rank: x
            if(nextRank > rank)
                componentBuilder.append(Component.text(LocaleLoader.getString("JSON.Hover.NextRank", String.valueOf(nextRank)))).append(Component.newline());
        }
    }

    private static void addLocked(@NotNull SubSkillType subSkillType, @NotNull TextColor ccLocked, @NotNull TextColor ccLevelRequirement,
                                  @NotNull TextColor ccLevelRequired, @NotNull TextComponent.Builder componentBuilder) {
        addLocked(ccLocked, ccLevelRequirement, componentBuilder);
        componentBuilder.append(Component.text(String.valueOf(RankConfig.getInstance().getSubSkillUnlockLevel(subSkillType, 1)), ccLevelRequired));
        //componentBuilder.append(Component.newline());
    }

    private static void addLocked(@NotNull AbstractSubSkill abstractSubSkill, @NotNull TextColor ccLocked,
                                  @NotNull TextColor ccLevelRequirement, @NotNull TextColor ccLevelRequired,
                                  @NotNull TextComponent.Builder componentBuilder) {
        addLocked(ccLocked, ccLevelRequirement, componentBuilder);
        componentBuilder.append(Component.text(String.valueOf(RankConfig.getInstance().getSubSkillUnlockLevel(abstractSubSkill, 1)), ccLevelRequired));
        //componentBuilder.append(Component.newline());
    }

    private static void addLocked(@NotNull TextColor ccLocked,
                                  @NotNull TextColor ccLevelRequirement,
                                  @NotNull TextComponent.Builder componentBuilder) {
        componentBuilder.append(Component.text(LocaleLoader.getString("JSON.Locked"), ccLocked, TextDecoration.BOLD));
        componentBuilder.append(Component.newline()).append(Component.newline());
        componentBuilder.append(Component.text(LocaleLoader.getString("JSON.LevelRequirement") + ": ", ccLevelRequirement));
    }

    @Deprecated
    private static @NotNull Component getSubSkillHoverEventJSON(@NotNull SubSkillType subSkillType, @NotNull OnlineMMOPlayer mmoPlayer)
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
        TextComponent.Builder componentBuilder = setupSkillComponentNameStyle(mmoPlayer, skillName, subSkillType, RankUtils.hasUnlockedSubskill(mmoPlayer, subSkillType));

        if(!RankUtils.hasUnlockedSubskill(mmoPlayer, subSkillType))
        {
            //Skill is not unlocked yet
            addLocked(subSkillType, ccLocked, ccLevelRequirement, ccLevelRequired, componentBuilder);
        } else {
            //addSubSkillTypeToHoverEventJSON(subSkillType, componentBuilder);

            //RANK
            if(subSkillType.getNumRanks() > 0)
            {
                int curRank = RankUtils.getRank(mmoPlayer, subSkillType);
                int nextRank = 0;

                if(curRank < subSkillType.getNumRanks())
                {
                    nextRank = RankUtils.getRankUnlockLevel(subSkillType, curRank+1);
                }

                addRanked(componentBuilder, subSkillType.getNumRanks(), RankUtils.getRank(mmoPlayer, subSkillType), nextRank);

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

    private static void addSubSkillTypeToHoverEventJSON(@NotNull AbstractSubSkill abstractSubSkill, @NotNull TextComponent.Builder componentBuilder)
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

    public static void getSubSkillTextComponents(@NotNull OnlineMMOPlayer mmoPlayer, @NotNull List<Component> textComponents, @NotNull RootSkill rootSkill) {
        return getSubSkillTextComponents(mmoPlayer, textComponents, PrimarySkillType.getSkill(rootSkill));
    }

    public static void getSubSkillTextComponents(@NotNull OnlineMMOPlayer mmoPlayer, @NotNull List<Component> textComponents, @NotNull PrimarySkillType parentSkill) {
        for(SubSkillType subSkillType : SubSkillType.values())
        {
            if(subSkillType.getParentSkill() == parentSkill)
            {
                //TODO: Hacky rewrite later
                //Only some versions of MC have this skill
                if(subSkillType == SubSkillType.FISHING_MASTER_ANGLER && mcMMO.getCompatibilityManager().getMasterAnglerCompatibilityLayer() == null)
                    continue;

                if(Permissions.isSubSkillEnabled(Misc.adaptPlayer(mmoPlayer), subSkillType))
                {
                    if(!InteractionManager.hasSubSkill(subSkillType))
                        textComponents.add(TextComponentFactory.getSubSkillTextComponent(mmoPlayer, subSkillType));
                }
            }
        }

        /* NEW SKILL SYSTEM */
        for(AbstractSubSkill abstractSubSkill : InteractionManager.getSubSkillList())
        {
            if(abstractSubSkill.getPrimarySkill() == parentSkill)
            {
                if(Permissions.isSubSkillEnabled(Misc.adaptPlayer(mmoPlayer), abstractSubSkill))
                    textComponents.add(TextComponentFactory.getSubSkillTextComponent(mmoPlayer, abstractSubSkill));
            }
        }
    }

    public static @NotNull TextComponent getSubSkillUnlockedNotificationComponents(@NotNull OnlineMMOPlayer mmoPlayer, @NotNull SubSkillType subSkillType)
    {
        TextComponent.Builder unlockMessage = Component.text().content(LocaleLoader.getString("JSON.SkillUnlockMessage", subSkillType.getLocaleName(), RankUtils.getRank(mmoPlayer, subSkillType)));
        unlockMessage.hoverEvent(HoverEvent.showText(getSubSkillHoverComponent(mmoPlayer, subSkillType)));
        unlockMessage.clickEvent(ClickEvent.runCommand("/"+subSkillType.getParentSkill().toString().toLowerCase(Locale.ENGLISH)));
        return unlockMessage.build();
    }
}


