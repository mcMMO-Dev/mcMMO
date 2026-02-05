package com.gmail.nossr50.util.text;

import com.gmail.nossr50.config.RankConfig;
import com.gmail.nossr50.datatypes.json.McMMOUrl;
import com.gmail.nossr50.datatypes.json.McMMOWebLinks;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.datatypes.skills.subskills.AbstractSubSkill;
import com.gmail.nossr50.listeners.InteractionManager;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.player.UserManager;
import com.gmail.nossr50.util.skills.RankUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * This class handles many of the JSON components that mcMMO makes and uses
 */
public class TextComponentFactory {

    /**
     * Makes a text component using strings from a locale and supports passing an undefined number
     * of variables to the LocaleLoader
     *
     * @param localeKey target locale string address
     * @param values vars to be passed to the locale loader
     * @return
     */
    public static TextComponent getNotificationMultipleValues(String localeKey, String... values) {
        String preColoredString = LocaleLoader.getString(localeKey, (Object[]) values);
        return Component.text(preColoredString);
    }

    public static Component getNotificationTextComponentFromLocale(String localeKey) {
        return getNotificationTextComponent(LocaleLoader.getString(localeKey));
    }

    public static Component getNotificationLevelUpTextComponent(PrimarySkillType skill,
            int levelsGained, int currentLevel) {
        return Component.text(LocaleLoader.getString("Overhaul.Levelup", LocaleLoader.getString(
                        "Overhaul.Name." + StringUtils.getCapitalized(skill.toString())), levelsGained,
                currentLevel));
    }

    private static TextComponent getNotificationTextComponent(String text) {
        //textComponent.setColor(getNotificationColor(notificationType));
        return Component.text(text);
    }

    public static String getSubSkillWikiLink(SubSkillType subSkillType) {
        return "https://wiki.mcmmo.org/en/skills/"
                + subSkillType.getParentSkill().toString().toLowerCase(Locale.ENGLISH) + "#"
                + subSkillType.getWikiUrl().toLowerCase(Locale.ENGLISH);
    }

    public static void sendPlayerSubSkillWikiLink(Player player, String subskillformatted,
            SubSkillType subSkillType) {
        if (!mcMMO.p.getGeneralConfig().getUrlLinksEnabled()) {
            return;
        }

        TextComponent.Builder wikiLinkComponent = Component.text()
                .content(LocaleLoader.getString("Overhaul.mcMMO.MmoInfo.Wiki"));
        wikiLinkComponent.decoration(TextDecoration.UNDERLINED, true);

        final String subSkillWikiLink = getSubSkillWikiLink(subSkillType);
        wikiLinkComponent.clickEvent(ClickEvent.openUrl(subSkillWikiLink));

        TextComponent.Builder componentBuilder = Component.text().content(subskillformatted)
                .append(Component.newline()).append(Component.text(subSkillWikiLink))
                .color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, true);

        wikiLinkComponent.hoverEvent(HoverEvent.showText(componentBuilder.build()));

        mcMMO.getAudiences().player(player).sendMessage(wikiLinkComponent);
    }

    public static void sendPlayerUrlHeader(Player player) {
        TextComponent prefix = Component.text(
                LocaleLoader.getString("Overhaul.mcMMO.Url.Wrap.Prefix") + " ");
        /*prefix.setColor(ChatColor.DARK_AQUA);*/
        TextComponent suffix = Component.text(
                " " + LocaleLoader.getString("Overhaul.mcMMO.Url.Wrap.Suffix"));
        /*suffix.setColor(ChatColor.DARK_AQUA);*/

        TextComponent emptySpace = Component.space();

        // TODO: Update system msg API
        mcMMO.getAudiences().player(player).sendMessage(Component.textOfChildren(
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
        ));
    }

    /**
     * Sends a player a bunch of text components that represent a list of sub-skills Styling and
     * formatting is applied before sending the messages
     *
     * @param player target player
     * @param subSkillComponents the text components representing the sub-skills by name
     */
    public static void sendPlayerSubSkillList(@NotNull Player player,
            @NotNull List<Component> subSkillComponents) {
        final Audience audience = mcMMO.getAudiences().player(player);

        //@ Signs, done for style
        Component space = Component.space();
        TextComponent atSignComponent = Component.text(
                LocaleLoader.getString("JSON.Hover.AtSymbolSkills"));

        //Only send 3 sub-skills per line
        Component[][] splitSubSkills = TextUtils.splitComponentsIntoGroups(subSkillComponents, 3);
        ArrayList<Component> individualLinesToSend = new ArrayList<>();

        //Create each line
        for (Component[] componentArray : splitSubSkills) {
            individualLinesToSend.add(TextUtils.fromArray(componentArray, atSignComponent, space));
        }

        //Send each group
        for (Component curLine : individualLinesToSend) {
            audience.sendMessage(curLine);
        }
    }

    private static Component getWebLinkTextComponent(McMMOWebLinks webLinks) {
        TextComponent.Builder webTextComponent;

        switch (webLinks) {
            case WEBSITE -> {
                webTextComponent = Component.text()
                        .content(LocaleLoader.getString("JSON.Hover.AtSymbolURL"));
                TextUtils.addChildWebComponent(webTextComponent, "Web");
                webTextComponent.clickEvent(getUrlClickEvent(McMMOUrl.urlWebsite));
            }
            case SPIGOT -> {
                webTextComponent = Component.text()
                        .content(LocaleLoader.getString("JSON.Hover.AtSymbolURL"));
                TextUtils.addChildWebComponent(webTextComponent, "Spigot");
                webTextComponent.clickEvent(getUrlClickEvent(McMMOUrl.urlSpigot));
            }
            case DISCORD -> {
                webTextComponent = Component.text()
                        .content(LocaleLoader.getString("JSON.Hover.AtSymbolURL"));
                TextUtils.addChildWebComponent(webTextComponent, "Discord");
                webTextComponent.clickEvent(getUrlClickEvent(McMMOUrl.urlDiscord));
            }
            case PATREON -> {
                webTextComponent = Component.text()
                        .content(LocaleLoader.getString("JSON.Hover.AtSymbolURL"));
                TextUtils.addChildWebComponent(webTextComponent, "Patreon");
                webTextComponent.clickEvent(getUrlClickEvent(McMMOUrl.urlPatreon));
            }
            case WIKI -> {
                webTextComponent = Component.text()
                        .content(LocaleLoader.getString("JSON.Hover.AtSymbolURL"));
                TextUtils.addChildWebComponent(webTextComponent, "Wiki");
                webTextComponent.clickEvent(getUrlClickEvent(McMMOUrl.urlWiki));
            }
            case HELP_TRANSLATE -> {
                webTextComponent = Component.text()
                        .content(LocaleLoader.getString("JSON.Hover.AtSymbolURL"));
                TextUtils.addChildWebComponent(webTextComponent, "Lang");
                webTextComponent.clickEvent(getUrlClickEvent(McMMOUrl.urlTranslate));
            }
            default -> webTextComponent = Component.text().content("NOT DEFINED");
        }

        TextUtils.addNewHoverComponentToTextComponent(webTextComponent, getUrlHoverEvent(webLinks));
        webTextComponent.insertion(webLinks.getUrl());

        return webTextComponent.build();
    }

    private static Component getUrlHoverEvent(McMMOWebLinks webLinks) {
        TextComponent.Builder componentBuilder = Component.text().content(webLinks.getNiceTitle());

        switch (webLinks) {
            case WEBSITE -> {
                addUrlHeaderHover(webLinks, componentBuilder);
                componentBuilder.append(Component.newline()).append(Component.newline());
                componentBuilder.append(
                        Component.text(webLinks.getLocaleDescription(), NamedTextColor.GREEN));
                componentBuilder.append(Component.text(
                        "\nDev Blogs, and information related to mcMMO can be found here",
                        NamedTextColor.GRAY));
            }
            case SPIGOT -> {
                addUrlHeaderHover(webLinks, componentBuilder);
                componentBuilder.append(Component.newline()).append(Component.newline());
                componentBuilder.append(
                        Component.text(webLinks.getLocaleDescription(), NamedTextColor.GREEN));
                componentBuilder.append(
                        Component.text("\nI post regularly in the discussion thread here!",
                                NamedTextColor.GRAY));
            }
            case PATREON -> {
                addUrlHeaderHover(webLinks, componentBuilder);
                componentBuilder.append(Component.newline()).append(Component.newline());
                componentBuilder.append(
                        Component.text(webLinks.getLocaleDescription(), NamedTextColor.GREEN));
                componentBuilder.append(Component.newline());
                componentBuilder.append(Component.text("Show support by buying me a coffee :)",
                        NamedTextColor.GRAY));
            }
            case WIKI -> {
                addUrlHeaderHover(webLinks, componentBuilder);
                componentBuilder.append(Component.newline()).append(Component.newline());
                componentBuilder.append(
                        Component.text(webLinks.getLocaleDescription(), NamedTextColor.GREEN));
                componentBuilder.append(Component.newline());
                componentBuilder.append(Component.text(
                        "I'm looking for more wiki staff, contact me on our discord!",
                        NamedTextColor.DARK_GRAY));
            }
            case DISCORD -> {
                addUrlHeaderHover(webLinks, componentBuilder);
                componentBuilder.append(Component.newline()).append(Component.newline());
                componentBuilder.append(
                        Component.text(webLinks.getLocaleDescription(), NamedTextColor.GREEN));
            }
            case HELP_TRANSLATE -> {
                addUrlHeaderHover(webLinks, componentBuilder);
                componentBuilder.append(Component.newline()).append(Component.newline());
                componentBuilder.append(
                        Component.text(webLinks.getLocaleDescription(), NamedTextColor.GREEN));
                componentBuilder.append(Component.newline());
                componentBuilder.append(Component.text(
                        "You can use this website to help translate mcMMO into your language!" +
                                "\nIf you want to know more contact me in discord.",
                        NamedTextColor.DARK_GRAY));
            }
        }

        return componentBuilder.build();
    }

    private static void addUrlHeaderHover(McMMOWebLinks webLinks,
            TextComponent.Builder componentBuilder) {
        componentBuilder.append(Component.newline());
        componentBuilder.append(
                Component.text(webLinks.getUrl(), NamedTextColor.GRAY, TextDecoration.ITALIC));
    }

    private static ClickEvent getUrlClickEvent(String url) {
        return ClickEvent.openUrl(url);
    }

    private static Component getSubSkillTextComponent(Player player, SubSkillType subSkillType) {
        //Get skill name and strip it of color
        final String skillName = ChatColor.stripColor(subSkillType.getLocaleName());

        boolean skillUnlocked = RankUtils.hasUnlockedSubskill(player, subSkillType);

        TextComponent.Builder textComponent = initNewSkillTextComponent(player, skillName,
                subSkillType, skillUnlocked);

        //Hover Event
        TextUtils.addNewHoverComponentToTextComponent(textComponent,
                getSubSkillHoverComponent(player, subSkillType));

        //Insertion
        textComponent.insertion(skillName);

        return textComponent.build();
    }

    private static TextComponent getSubSkillTextComponent(Player player,
            AbstractSubSkill abstractSubSkill) {
        //String key = abstractSubSkill.getConfigKeyName();
        String skillName = abstractSubSkill.getNiceName();

        //Setup Text Component
        SubSkillType subSkillType = abstractSubSkill.getSubSkillType();

        boolean skillUnlocked = RankUtils.hasUnlockedSubskill(player, subSkillType);

        TextComponent.Builder textComponent = initNewSkillTextComponent(player, skillName,
                subSkillType, skillUnlocked);

        //Hover Event
        TextUtils.addNewHoverComponentToTextComponent(textComponent,
                getSubSkillHoverComponent(player, abstractSubSkill));

        //Insertion
        textComponent.insertion(skillName);

        return textComponent.build();
    }

    private static TextComponent.Builder initNewSkillTextComponent(Player player, String skillName,
            SubSkillType subSkillType, boolean skillUnlocked) {
        TextComponent.Builder textComponent;
        if (skillUnlocked) {
            if (RankUtils.getHighestRank(subSkillType) == RankUtils.getRank(player, subSkillType)
                    && subSkillType.getNumRanks() > 1) {
                textComponent = Component.text()
                        .content(LocaleLoader.getString("JSON.Hover.MaxRankSkillName", skillName));
            } else {
                textComponent = Component.text()
                        .content(LocaleLoader.getString("JSON.Hover.SkillName", skillName));
            }

            textComponent.clickEvent(ClickEvent.runCommand(
                    "/mmoinfo " + subSkillType.getNiceNameNoSpaces(subSkillType)));

        } else {
            textComponent = Component.text().content(LocaleLoader.getString("JSON.Hover.Mystery",
                    String.valueOf(RankUtils.getUnlockLevel(subSkillType))));

            textComponent.clickEvent(ClickEvent.runCommand("/mmoinfo ???"));
        }
        return textComponent;
    }

    private static Component getSubSkillHoverComponent(Player player,
            AbstractSubSkill abstractSubSkill) {
        return getSubSkillHoverEventJSON(abstractSubSkill, player);
    }

    private static Component getSubSkillHoverComponent(Player player, SubSkillType subSkillType) {
        return getSubSkillHoverEventJSON(subSkillType, player);
    }

    /**
     * Used for the skill in the new skill system (Deriving from AbstractSubSkill)
     *
     * @param abstractSubSkill this subskill
     * @param player the player who owns this subskill
     * @return the hover basecomponent object for this subskill
     */
    private static Component getSubSkillHoverEventJSON(AbstractSubSkill abstractSubSkill,
            Player player) {
        String skillName = ChatColor.stripColor(abstractSubSkill.getNiceName());

        /*
         * Hover Event BaseComponent color table
         */
        TextColor ccLocked = NamedTextColor.DARK_GRAY;
        TextColor ccLevelRequirement = NamedTextColor.BLUE;
        TextColor ccLevelRequired = NamedTextColor.RED;

        SubSkillType subSkillType = abstractSubSkill.getSubSkillType();

        //SubSkillType Name
        TextComponent.Builder componentBuilder = setupSkillComponentNameStyle(player, skillName,
                subSkillType, RankUtils.hasUnlockedSubskill(player, abstractSubSkill));

        if (!RankUtils.hasUnlockedSubskill(player, abstractSubSkill)) {
            //Skill is not unlocked yet
            addLocked(abstractSubSkill, ccLocked, ccLevelRequirement, ccLevelRequired,
                    componentBuilder);
        } else {
            addSubSkillTypeToHoverEventJSON(abstractSubSkill, componentBuilder);

            //RANK
            int curRank = RankUtils.getRank(player, abstractSubSkill);
            int nextRank = 0;

            if (curRank < abstractSubSkill.getNumRanks() && abstractSubSkill.getNumRanks() > 0) {
                nextRank = RankUtils.getRankUnlockLevel(abstractSubSkill, curRank + 1);
            }

            addRanked(componentBuilder, abstractSubSkill.getNumRanks(),
                    RankUtils.getRank(player, abstractSubSkill), nextRank);

            componentBuilder.append(
                    Component.text(LocaleLoader.getString("JSON.DescriptionHeader")));
            componentBuilder.append(Component.newline())
                    .append(Component.text(abstractSubSkill.getDescription()))
                    .append(Component.newline());

            //Empty line
            componentBuilder.append(Component.newline()).decoration(TextDecoration.BOLD, false);
            componentBuilder.append(Component.newline());

            //Finally, add details to the tooltip
            // TODO: pass in McMMOPlayer instead
            abstractSubSkill.addStats(componentBuilder, UserManager.getPlayer(player));
        }

        return componentBuilder.build();
    }

    private static TextComponent.Builder setupSkillComponentNameStyle(Player player,
            String skillName, SubSkillType subSkillType, boolean skillUnlocked) {
        TextComponent.Builder componentBuilder;
        if (skillUnlocked) {
            if (RankUtils.getHighestRank(subSkillType) == RankUtils.getRank(player, subSkillType)
                    && subSkillType.getNumRanks() > 1) {
                componentBuilder = getNewComponentBuilder(
                        LocaleLoader.getString("JSON.Hover.MaxRankSkillName", skillName));
            } else {
                componentBuilder = getNewComponentBuilder(
                        LocaleLoader.getString("JSON.Hover.SkillName", skillName));
            }
        } else {
            componentBuilder = getNewComponentBuilder(LocaleLoader.getString("JSON.Hover.Mystery",
                    String.valueOf(RankUtils.getUnlockLevel(subSkillType))));
        }
        return componentBuilder;
    }

    private static TextComponent.Builder getNewComponentBuilder(String skillName) {
        TextComponent.Builder componentBuilder = Component.text().content(skillName);
        componentBuilder.append(Component.newline());
        return componentBuilder;
    }

    private static void addRanked(TextComponent.Builder componentBuilder, int numRanks, int rank,
            int nextRank) {
        if (numRanks > 0) {
            //Rank: x
            componentBuilder.append(
                            Component.text(LocaleLoader.getString("JSON.Hover.Rank", String.valueOf(rank))))
                    .append(Component.newline());

            //Next Rank: x
            if (nextRank > rank) {
                componentBuilder.append(Component.text(
                                LocaleLoader.getString("JSON.Hover.NextRank", String.valueOf(nextRank))))
                        .append(Component.newline());
            }
        }
    }

    private static void addLocked(SubSkillType subSkillType, TextColor ccLocked,
            TextColor ccLevelRequirement, TextColor ccLevelRequired,
            TextComponent.Builder componentBuilder) {
        addLocked(ccLocked, ccLevelRequirement, componentBuilder);
        componentBuilder.append(Component.text(
                String.valueOf(RankConfig.getInstance().getSubSkillUnlockLevel(subSkillType, 1)),
                ccLevelRequired));
        //componentBuilder.append(Component.newline());
    }

    private static void addLocked(AbstractSubSkill abstractSubSkill, TextColor ccLocked,
            TextColor ccLevelRequirement, TextColor ccLevelRequired,
            TextComponent.Builder componentBuilder) {
        addLocked(ccLocked, ccLevelRequirement, componentBuilder);
        componentBuilder.append(Component.text(String.valueOf(
                        RankConfig.getInstance().getSubSkillUnlockLevel(abstractSubSkill, 1)),
                ccLevelRequired));
        //componentBuilder.append(Component.newline());
    }

    private static void addLocked(TextColor ccLocked, TextColor ccLevelRequirement,
            TextComponent.Builder componentBuilder) {
        componentBuilder.append(Component.text(LocaleLoader.getString("JSON.Locked"), ccLocked,
                TextDecoration.BOLD));
        componentBuilder.append(Component.newline()).append(Component.newline());
        componentBuilder.append(
                Component.text(LocaleLoader.getString("JSON.LevelRequirement") + ": ",
                        ccLevelRequirement));
    }

    private static Component getSubSkillHoverEventJSON(SubSkillType subSkillType, Player player) {
        // Get skill name and strip it of color
        String skillName = ChatColor.stripColor(subSkillType.getLocaleName());

        /*
         * Hover Event BaseComponent color table
         */
        TextColor ccDescriptionHeader = NamedTextColor.DARK_PURPLE;
        TextColor ccDescription = NamedTextColor.DARK_GRAY;
        TextColor ccLocked = NamedTextColor.DARK_GRAY;
        TextColor ccLevelRequirement = NamedTextColor.BLUE;
        TextColor ccLevelRequired = NamedTextColor.RED;

        //SubSkillType Name
        TextComponent.Builder componentBuilder = setupSkillComponentNameStyle(player, skillName,
                subSkillType, RankUtils.hasUnlockedSubskill(player, subSkillType));

        if (!RankUtils.hasUnlockedSubskill(player, subSkillType)) {
            //Skill is not unlocked yet
            addLocked(subSkillType, ccLocked, ccLevelRequirement, ccLevelRequired,
                    componentBuilder);
        } else {
            //addSubSkillTypeToHoverEventJSON(subSkillType, componentBuilder);

            //RANK
            if (subSkillType.getNumRanks() > 0) {
                int curRank = RankUtils.getRank(player, subSkillType);
                int nextRank = 0;

                if (curRank < subSkillType.getNumRanks()) {
                    nextRank = RankUtils.getRankUnlockLevel(subSkillType, curRank + 1);
                }

                addRanked(componentBuilder, subSkillType.getNumRanks(),
                        RankUtils.getRank(player, subSkillType), nextRank);
            }

            componentBuilder.append(Component.newline());
            componentBuilder.append(Component.text(LocaleLoader.getString("JSON.DescriptionHeader"))
                    .color(ccDescriptionHeader));
            componentBuilder.append(Component.newline());
            componentBuilder.append(
                    Component.text(ChatColor.stripColor(subSkillType.getLocaleDescription()))
                            .color(ccDescription));
        }

        return componentBuilder.build();
    }

    private static void addSubSkillTypeToHoverEventJSON(AbstractSubSkill abstractSubSkill,
            TextComponent.Builder componentBuilder) {
        if (abstractSubSkill.isSuperAbility()) {
            componentBuilder.append(Component.text(LocaleLoader.getString("JSON.Type.SuperAbility"),
                    NamedTextColor.LIGHT_PURPLE, TextDecoration.BOLD));
        } else if (abstractSubSkill.isActiveUse()) {
            componentBuilder.append(Component.text(LocaleLoader.getString("JSON.Type.Active"),
                    NamedTextColor.DARK_RED, TextDecoration.BOLD));
        } else {
            componentBuilder.append(Component.text(LocaleLoader.getString("JSON.Type.Passive"),
                    NamedTextColor.GREEN, TextDecoration.BOLD));
        }

        componentBuilder.append(Component.newline());
    }

    /**
     * @deprecated use appendSubSkillTextComponents(Player, List<Component>, PrimarySkillType)
     * @param player target player
     * @param textComponents list to append to
     * @param parentSkill the parent skill
     */
    @Deprecated(since = "2.2.046", forRemoval = true)
    public static void getSubSkillTextComponents(Player player, List<Component> textComponents,
        PrimarySkillType parentSkill) {
        appendSubSkillTextComponents(player, textComponents, parentSkill);
    }

    /**
     * Appends sub-skill text components to a list for a given parent skill
     * @param player target player
     * @param textComponents list to append to
     * @param parentSkill the parent skill
     */
    public static void appendSubSkillTextComponents(Player player, List<Component> textComponents,
            PrimarySkillType parentSkill) {
        for (SubSkillType subSkillType : SubSkillType.values()) {
            if (subSkillType.getParentSkill() == parentSkill) {
                //TODO: Hacky rewrite later
                //Only some versions of MC have this skill
                if (subSkillType == SubSkillType.FISHING_MASTER_ANGLER
                        && mcMMO.getCompatibilityManager().getMasterAnglerCompatibilityLayer()
                        == null) {
                    continue;
                }

                if (Permissions.isSubSkillEnabled(player, subSkillType)) {
                    if (!InteractionManager.hasSubSkill(subSkillType)) {
                        textComponents.add(TextComponentFactory.getSubSkillTextComponent(player,
                                subSkillType));
                    }
                }
            }
        }

        /* NEW SKILL SYSTEM */
        for (AbstractSubSkill abstractSubSkill : InteractionManager.getSubSkillList()) {
            if (abstractSubSkill.getPrimarySkill() == parentSkill) {
                if (Permissions.isSubSkillEnabled(player, abstractSubSkill.getSubSkillType())) {
                    textComponents.add(TextComponentFactory.getSubSkillTextComponent(player,
                            abstractSubSkill));
                }
            }
        }
    }

    public static TextComponent getSubSkillUnlockedNotificationComponents(Player player,
            SubSkillType subSkillType) {
        TextComponent.Builder unlockMessage = Component.text().content(
                LocaleLoader.getString("JSON.SkillUnlockMessage", subSkillType.getLocaleName(),
                        RankUtils.getRank(player, subSkillType)));
        unlockMessage.hoverEvent(
                HoverEvent.showText(getSubSkillHoverComponent(player, subSkillType)));
        unlockMessage.clickEvent(ClickEvent.runCommand(
                "/" + subSkillType.getParentSkill().toString().toLowerCase(Locale.ENGLISH)));
        return unlockMessage.build();
    }
}


