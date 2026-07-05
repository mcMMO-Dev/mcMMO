package com.gmail.nossr50.util.text;

import com.gmail.nossr50.config.RankConfig;
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
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Builds Adventure {@link Component} objects for mcMMO's JSON hover tooltips and clickable text.
 *
 * <p>All colors and text are sourced from locale strings so that server operators can fully
 * customize the presentation by editing locale files. Three color formats are supported in
 * locale values:
 * <ul>
 *   <li>{@code &X} codes — e.g. {@code &a}, {@code &l} (standard Minecraft shorthand)</li>
 *   <li>{@code &#RRGGBB} hex codes — e.g. {@code &#FF0000} for red</li>
 *   <li>{@code [[NAME]]} tokens — e.g. {@code [[RED]]}, {@code [[DARK_AQUA]]} (mcMMO legacy format)</li>
 * </ul>
 * No {@link net.kyori.adventure.text.format.NamedTextColor} constants are used here directly —
 * color belongs in the locale, not in Java code.
 */
public class TextComponentFactory {

    private TextComponentFactory() {
    }

    // ---------------------------------------------------------------------------
    // Public notification helpers
    // ---------------------------------------------------------------------------

    /**
     * Makes a text component from a locale key, injecting string variables via
     * {@link LocaleLoader}.
     *
     * @param localeKey target locale key
     * @param values    values to substitute into the locale string
     * @return a properly colored text component
     */
    public static TextComponent getNotificationMultipleValues(
            @NotNull String localeKey, @NotNull String... values) {
        return LocaleLoader.getTextComponent(localeKey, (Object[]) values);
    }

    public static Component getNotificationTextComponentFromLocale(@NotNull String localeKey) {
        return LocaleLoader.getTextComponent(localeKey);
    }

    public static Component getNotificationLevelUpTextComponent(
            @NotNull PrimarySkillType skill, int levelsGained, int currentLevel) {
        final String skillName = LocaleLoader.getString(
                "Overhaul.Name." + StringUtils.getCapitalized(skill.toString()));
        return LocaleLoader.getTextComponent("Overhaul.Levelup", skillName, levelsGained,
                currentLevel);
    }

    // ---------------------------------------------------------------------------
    // Wiki link
    // ---------------------------------------------------------------------------

    public static String getSubSkillWikiLink(@NotNull SubSkillType subSkillType) {
        return "https://wiki.mcmmo.org/en/skills/"
                + subSkillType.getParentSkill().toString().toLowerCase(Locale.ENGLISH) + "#"
                + subSkillType.getWikiUrl().toLowerCase(Locale.ENGLISH);
    }

    public static void sendPlayerSubSkillWikiLink(
            @NotNull Player player,
            @NotNull String subskillformatted,
            @NotNull SubSkillType subSkillType) {
        if (!mcMMO.p.getGeneralConfig().getUrlLinksEnabled()) {
            return;
        }

        final String wikiLink = getSubSkillWikiLink(subSkillType);

        // Hover shows the sub-skill name and the URL in gray italic (JSON.URL.Header)
        final Component hoverText = Component.text()
                .append(Component.text(subskillformatted))
                .append(Component.newline())
                .append(LocaleLoader.getTextComponent("JSON.URL.Header", wikiLink))
                .build();

        final TextComponent wikiLinkComponent = Component.text()
                .content(LocaleLoader.getString("Overhaul.mcMMO.MmoInfo.Wiki"))
                .decoration(TextDecoration.UNDERLINED, true)
                .clickEvent(ClickEvent.openUrl(wikiLink))
                .hoverEvent(HoverEvent.showText(hoverText))
                .build();

        mcMMO.getAudiences().player(player).sendMessage(wikiLinkComponent);
    }

    // ---------------------------------------------------------------------------
    // URL header bar
    // ---------------------------------------------------------------------------

    public static void sendPlayerUrlHeader(@NotNull Player player) {
        final TextComponent prefix = LocaleLoader.getTextComponent(
                "Overhaul.mcMMO.Url.Wrap.Prefix");
        final TextComponent suffix = LocaleLoader.getTextComponent(
                "Overhaul.mcMMO.Url.Wrap.Suffix");
        final Component space = Component.space();

        // TODO: Update system msg API
        mcMMO.getAudiences().player(player).sendMessage(Component.textOfChildren(
                prefix, space,
                getWebLinkTextComponent(McMMOWebLinks.WEBSITE), space,
                getWebLinkTextComponent(McMMOWebLinks.DISCORD), space,
                getWebLinkTextComponent(McMMOWebLinks.WIKI), space,
                getWebLinkTextComponent(McMMOWebLinks.SPIGOT), space,
                suffix));
    }

    // ---------------------------------------------------------------------------
    // Sub-skill list
    // ---------------------------------------------------------------------------

    /**
     * Sends a player the list of sub-skill buttons for a parent skill, formatted as clickable
     * {@link Component}s with hover tooltips.
     *
     * @param player             target player
     * @param subSkillComponents the per-sub-skill button components
     */
    public static void sendPlayerSubSkillList(
            @NotNull Player player, @NotNull List<Component> subSkillComponents) {
        final Audience audience = mcMMO.getAudiences().player(player);
        final Component atSign = LocaleLoader.getTextComponent("JSON.Hover.AtSymbolSkills");
        final Component space = Component.space();

        final Component[][] groups = TextUtils.splitComponentsIntoGroups(subSkillComponents, 3);
        final ArrayList<Component> lines = new ArrayList<>();

        for (final Component[] row : groups) {
            lines.add(TextUtils.fromArray(row, atSign, space));
        }

        for (final Component line : lines) {
            audience.sendMessage(line);
        }
    }

    // ---------------------------------------------------------------------------
    // Sub-skill unlock notification
    // ---------------------------------------------------------------------------

    public static TextComponent getSubSkillUnlockedNotificationComponents(
            @NotNull Player player, @NotNull SubSkillType subSkillType) {
        return Component.text()
                .append(LocaleLoader.getTextComponent("JSON.SkillUnlockMessage",
                        subSkillType.getLocaleName(),
                        RankUtils.getRank(player, subSkillType)))
                .hoverEvent(HoverEvent.showText(buildSubSkillHover(player, subSkillType)))
                .clickEvent(ClickEvent.runCommand(
                        "/" + subSkillType.getParentSkill().toString().toLowerCase(Locale.ENGLISH)))
                .build();
    }

    // ---------------------------------------------------------------------------
    // Sub-skill component list builders (public API used by skill commands)
    // ---------------------------------------------------------------------------

    /**
     * @deprecated use {@link #appendSubSkillTextComponents(Player, List, PrimarySkillType)}
     */
    @Deprecated(since = "2.2.046", forRemoval = true)
    public static void getSubSkillTextComponents(
            @NotNull Player player,
            @NotNull List<Component> textComponents,
            @NotNull PrimarySkillType parentSkill) {
        appendSubSkillTextComponents(player, textComponents, parentSkill);
    }

    /**
     * Appends clickable sub-skill button components with hover tooltips to {@code textComponents}.
     *
     * @param player         target player
     * @param textComponents list to append into
     * @param parentSkill    the parent skill whose sub-skills to list
     */
    public static void appendSubSkillTextComponents(
            @NotNull Player player,
            @NotNull List<Component> textComponents,
            @NotNull PrimarySkillType parentSkill) {
        for (final SubSkillType subSkillType : SubSkillType.values()) {
            if (subSkillType.getParentSkill() == parentSkill
                    && Permissions.isSubSkillEnabled(player, subSkillType)
                    && !InteractionManager.hasSubSkill(subSkillType)) {
                textComponents.add(buildSubSkillButton(player, subSkillType));
            }
        }

        for (final AbstractSubSkill abstractSubSkill : InteractionManager.getSubSkillList()) {
            if (abstractSubSkill.getPrimarySkill() == parentSkill
                    && Permissions.isSubSkillEnabled(player, abstractSubSkill.getSubSkillType())) {
                textComponents.add(buildSubSkillButton(player, abstractSubSkill));
            }
        }
    }

    // ---------------------------------------------------------------------------
    // Internal — web link components
    // ---------------------------------------------------------------------------

    private static Component getWebLinkTextComponent(@NotNull McMMOWebLinks webLinks) {
        return Component.text()
                .append(LocaleLoader.getTextComponent("JSON.Hover.AtSymbolURL"))
                .append(webLinks.getLabelComponent())
                .clickEvent(ClickEvent.openUrl(webLinks.getUrl()))
                .insertion(webLinks.getUrl())
                .hoverEvent(HoverEvent.showText(buildUrlHoverTooltip(webLinks)))
                .build();
    }

    private static Component buildUrlHoverTooltip(@NotNull McMMOWebLinks webLinks) {
        final TextComponent.Builder tooltip = Component.text()
                .content(webLinks.getNiceTitle())
                .append(Component.newline())
                // URL displayed in gray italic — JSON.URL.Header=&7&o{0}
                .append(LocaleLoader.getTextComponent("JSON.URL.Header", webLinks.getUrl()))
                .append(Component.newline())
                .append(Component.newline())
                .append(webLinks.getDescriptionComponent());

        final @Nullable TextComponent detail = webLinks.getDetailComponent();
        if (detail != null) {
            tooltip.append(Component.newline()).append(detail);
        }

        return tooltip.build();
    }

    // ---------------------------------------------------------------------------
    // Internal — sub-skill button (the clickable text in the skill list)
    // ---------------------------------------------------------------------------

    private static Component buildSubSkillButton(
            @NotNull Player player, @NotNull SubSkillType subSkillType) {
        final String skillName = ChatColor.stripColor(subSkillType.getLocaleName());
        final boolean unlocked = RankUtils.hasUnlockedSubskill(player, subSkillType);

        return buildSkillNameComponent(player, skillName, subSkillType, unlocked)
                .hoverEvent(HoverEvent.showText(buildSubSkillHover(player, subSkillType)))
                .insertion(skillName)
                .build();
    }

    private static Component buildSubSkillButton(
            @NotNull Player player, @NotNull AbstractSubSkill abstractSubSkill) {
        final String skillName = abstractSubSkill.getNiceName();
        final SubSkillType subSkillType = abstractSubSkill.getSubSkillType();
        final boolean unlocked = RankUtils.hasUnlockedSubskill(player, subSkillType);

        return buildSkillNameComponent(player, skillName, subSkillType, unlocked)
                .hoverEvent(HoverEvent.showText(buildSubSkillHover(player, abstractSubSkill)))
                .insertion(skillName)
                .build();
    }

    /**
     * Builds the colored skill-name button component (the text players click on).
     * Color and formatting come entirely from locale strings.
     */
    private static TextComponent.Builder buildSkillNameComponent(
            @NotNull Player player,
            @NotNull String skillName,
            @NotNull SubSkillType subSkillType,
            boolean unlocked) {
        if (!unlocked) {
            return Component.text()
                    .append(LocaleLoader.getTextComponent("JSON.Hover.Mystery",
                            String.valueOf(RankUtils.getUnlockLevel(subSkillType))))
                    .clickEvent(ClickEvent.runCommand("/mmoinfo ???"));
        }

        final boolean isMaxRank = subSkillType.getNumRanks() > 1
                && RankUtils.getHighestRank(subSkillType) == RankUtils.getRank(player,
                subSkillType);
        final String localeKey = isMaxRank ? "JSON.Hover.MaxRankSkillName" : "JSON.Hover.SkillName";

        return Component.text()
                .append(LocaleLoader.getTextComponent(localeKey, skillName))
                .clickEvent(ClickEvent.runCommand(
                        "/mmoinfo " + subSkillType.getNiceNameNoSpaces(subSkillType)));
    }

    // ---------------------------------------------------------------------------
    // Internal — sub-skill hover tooltip construction
    // ---------------------------------------------------------------------------

    /**
     * Builds the hover tooltip for a legacy {@link SubSkillType} sub-skill.
     * All colors come from locale string {@code &} codes.
     */
    private static Component buildSubSkillHover(
            @NotNull Player player, @NotNull SubSkillType subSkillType) {
        final String skillName = ChatColor.stripColor(subSkillType.getLocaleName());
        final boolean unlocked = RankUtils.hasUnlockedSubskill(player, subSkillType);

        final TextComponent.Builder tooltip = buildHoverNameHeader(
                player, skillName, subSkillType, unlocked);

        if (!unlocked) {
            appendLockedSection(tooltip, subSkillType);
        } else {
            if (subSkillType.getNumRanks() > 0) {
                final int curRank = RankUtils.getRank(player, subSkillType);
                final int nextRank = curRank < subSkillType.getNumRanks()
                        ? RankUtils.getRankUnlockLevel(subSkillType, curRank + 1) : 0;
                appendRankSection(tooltip, subSkillType.getNumRanks(), curRank, nextRank);
            }

            tooltip.append(Component.newline());
            // JSON.DescriptionHeader=&5Description:
            tooltip.append(LocaleLoader.getTextComponent("JSON.DescriptionHeader"));
            tooltip.append(Component.newline());
            tooltip.append(LocaleLoader.getTextComponent(
                    subSkillType.getLocaleKeyRoot() + ".Description"));
        }

        return tooltip.build();
    }

    /**
     * Builds the hover tooltip for a new-system {@link AbstractSubSkill}.
     * All colors come from locale string {@code &} codes.
     */
    private static Component buildSubSkillHover(
            @NotNull Player player, @NotNull AbstractSubSkill abstractSubSkill) {
        final String skillName = abstractSubSkill.getNiceName();
        final SubSkillType subSkillType = abstractSubSkill.getSubSkillType();
        final boolean unlocked = RankUtils.hasUnlockedSubskill(player, abstractSubSkill);

        final TextComponent.Builder tooltip = buildHoverNameHeader(
                player, skillName, subSkillType, unlocked);

        if (!unlocked) {
            appendLockedSection(tooltip, abstractSubSkill);
        } else {
            appendSubSkillTypeTag(abstractSubSkill, tooltip);

            final int curRank = RankUtils.getRank(player, abstractSubSkill);
            final int nextRank = (curRank < abstractSubSkill.getNumRanks()
                    && abstractSubSkill.getNumRanks() > 0)
                    ? RankUtils.getRankUnlockLevel(abstractSubSkill, curRank + 1) : 0;
            appendRankSection(tooltip, abstractSubSkill.getNumRanks(), curRank, nextRank);

            // JSON.DescriptionHeader=&5Description:
            tooltip.append(LocaleLoader.getTextComponent("JSON.DescriptionHeader"));
            tooltip.append(Component.newline())
                    .append(LocaleLoader.getTextComponent(
                            abstractSubSkill.getPrimaryKeyName() + ".SubSkill."
                                    + abstractSubSkill.getConfigKeyName() + ".Description"))
                    .append(Component.newline());

            tooltip.append(Component.newline()).decoration(TextDecoration.BOLD, false);
            tooltip.append(Component.newline());

            // TODO: pass in McMMOPlayer instead of looking it up
            abstractSubSkill.addStats(tooltip, UserManager.getPlayer(player));
        }

        return tooltip.build();
    }

    /**
     * Builds the skill-name header line at the top of a hover tooltip.
     * The name color comes from locale (e.g. {@code JSON.Hover.SkillName=&3{0}&r}).
     */
    private static TextComponent.Builder buildHoverNameHeader(
            @NotNull Player player,
            @NotNull String skillName,
            @NotNull SubSkillType subSkillType,
            boolean unlocked) {
        final String nameArg;
        final String localeKey;
        if (!unlocked) {
            localeKey = "JSON.Hover.Mystery";
            nameArg = String.valueOf(RankUtils.getUnlockLevel(subSkillType));
        } else if (subSkillType.getNumRanks() > 1
                && RankUtils.getHighestRank(subSkillType) == RankUtils.getRank(player,
                subSkillType)) {
            localeKey = "JSON.Hover.MaxRankSkillName";
            nameArg = skillName;
        } else {
            localeKey = "JSON.Hover.SkillName";
            nameArg = skillName;
        }

        return Component.text()
                .append(LocaleLoader.getTextComponent(localeKey, nameArg))
                .append(Component.newline());
    }

    /**
     * Appends rank and next-rank lines. Colors live in locale:
     * {@code JSON.Hover.Rank=&e&lRank:&r &f{0}} and
     * {@code JSON.Hover.NextRank=&7&oNext upgrade at level {0}}.
     */
    private static void appendRankSection(
            @NotNull TextComponent.Builder tooltip, int numRanks, int rank, int nextRank) {
        if (numRanks <= 0) {
            return;
        }

        tooltip.append(LocaleLoader.getTextComponent("JSON.Hover.Rank", String.valueOf(rank)))
                .append(Component.newline());

        if (nextRank > rank) {
            tooltip.append(LocaleLoader.getTextComponent(
                            "JSON.Hover.NextRank", String.valueOf(nextRank)))
                    .append(Component.newline());
        }
    }

    /**
     * Appends the locked-skill section.
     * Colors live in locale:
     * {@code JSON.Locked=&8-=[LOCKED]=-} and {@code JSON.LevelRequirement=&9Level Requirement}.
     */
    private static void appendLockedSection(
            @NotNull TextComponent.Builder tooltip, @NotNull SubSkillType subSkillType) {
        appendLockedCore(tooltip, String.valueOf(
                RankConfig.getInstance().getSubSkillUnlockLevel(subSkillType, 1)));
    }

    private static void appendLockedSection(
            @NotNull TextComponent.Builder tooltip, @NotNull AbstractSubSkill abstractSubSkill) {
        appendLockedCore(tooltip, String.valueOf(
                RankConfig.getInstance().getSubSkillUnlockLevel(abstractSubSkill, 1)));
    }

    private static void appendLockedCore(
            @NotNull TextComponent.Builder tooltip, @NotNull String unlockLevel) {
        // JSON.Locked=&8-=[LOCKED]=-
        tooltip.append(LocaleLoader.getTextComponent("JSON.Locked"));
        tooltip.append(Component.newline()).append(Component.newline());
        // JSON.LevelRequirement=&9Level Requirement  (colon + level appended inline)
        tooltip.append(LocaleLoader.getTextComponent("JSON.LevelRequirement"))
                .append(Component.text(": " + unlockLevel));
    }

    /**
     * Appends the Passive / Active / Super Ability type tag.
     * Colors and bold formatting live entirely in locale:
     * {@code JSON.Type.Passive=&a&lPassive&r},
     * {@code JSON.Type.Active=&4&lActive&r},
     * {@code JSON.Type.SuperAbility=&d&lSuper Ability&r}.
     */
    private static void appendSubSkillTypeTag(
            @NotNull AbstractSubSkill abstractSubSkill,
            @NotNull TextComponent.Builder tooltip) {
        final String typeKey;
        if (abstractSubSkill.isSuperAbility()) {
            typeKey = "JSON.Type.SuperAbility";
        } else if (abstractSubSkill.isActiveUse()) {
            typeKey = "JSON.Type.Active";
        } else {
            typeKey = "JSON.Type.Passive";
        }

        tooltip.append(LocaleLoader.getTextComponent(typeKey)).append(Component.newline());
    }
}


