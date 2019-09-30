package com.gmail.nossr50.config.hocon.playerleveling;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;

import java.util.HashMap;

@ConfigSerializable
public class ConfigExperienceBars {

    private static final boolean PARTY_XP_DEFAULT = false;
    private static final boolean PASSIVE_XP_DEFAULT = true;
    private static final boolean DETAILED_XP_BARS_DEFAULT = false;
    private static final boolean XP_BARS_DEFAULT = true;
    private static final HashMap<PrimarySkillType, Boolean> SKILL_XPBAR_TOGGLES_DEFAULT;
    private static final HashMap<PrimarySkillType, BarColor> SKILL_XPBAR_COLOR_DEFAULT;
    private static final HashMap<PrimarySkillType, BarStyle> SKILL_XPBAR_STYLE_DEFAULT;

    static {
        SKILL_XPBAR_TOGGLES_DEFAULT = new HashMap<>();
        SKILL_XPBAR_TOGGLES_DEFAULT.put(PrimarySkillType.ACROBATICS, true);
        SKILL_XPBAR_TOGGLES_DEFAULT.put(PrimarySkillType.ARCHERY, true);
        SKILL_XPBAR_TOGGLES_DEFAULT.put(PrimarySkillType.AXES, true);
        SKILL_XPBAR_TOGGLES_DEFAULT.put(PrimarySkillType.ALCHEMY, true);
        SKILL_XPBAR_TOGGLES_DEFAULT.put(PrimarySkillType.WOODCUTTING, true);
        SKILL_XPBAR_TOGGLES_DEFAULT.put(PrimarySkillType.UNARMED, true);
        SKILL_XPBAR_TOGGLES_DEFAULT.put(PrimarySkillType.SWORDS, true);
        SKILL_XPBAR_TOGGLES_DEFAULT.put(PrimarySkillType.HERBALISM, true);
        SKILL_XPBAR_TOGGLES_DEFAULT.put(PrimarySkillType.MINING, true);
        SKILL_XPBAR_TOGGLES_DEFAULT.put(PrimarySkillType.EXCAVATION, true);
        SKILL_XPBAR_TOGGLES_DEFAULT.put(PrimarySkillType.TAMING, true);
        SKILL_XPBAR_TOGGLES_DEFAULT.put(PrimarySkillType.REPAIR, true);
        SKILL_XPBAR_TOGGLES_DEFAULT.put(PrimarySkillType.FISHING, true);

        SKILL_XPBAR_COLOR_DEFAULT = new HashMap<>();
        SKILL_XPBAR_COLOR_DEFAULT.put(PrimarySkillType.ACROBATICS, BarColor.PINK);
        SKILL_XPBAR_COLOR_DEFAULT.put(PrimarySkillType.ARCHERY, BarColor.BLUE);
        SKILL_XPBAR_COLOR_DEFAULT.put(PrimarySkillType.AXES, BarColor.BLUE);
        SKILL_XPBAR_COLOR_DEFAULT.put(PrimarySkillType.ALCHEMY, BarColor.PURPLE);
        SKILL_XPBAR_COLOR_DEFAULT.put(PrimarySkillType.WOODCUTTING, BarColor.GREEN);
        SKILL_XPBAR_COLOR_DEFAULT.put(PrimarySkillType.UNARMED, BarColor.BLUE);
        SKILL_XPBAR_COLOR_DEFAULT.put(PrimarySkillType.SWORDS, BarColor.BLUE);
        SKILL_XPBAR_COLOR_DEFAULT.put(PrimarySkillType.HERBALISM, BarColor.GREEN);
        SKILL_XPBAR_COLOR_DEFAULT.put(PrimarySkillType.MINING, BarColor.YELLOW);
        SKILL_XPBAR_COLOR_DEFAULT.put(PrimarySkillType.EXCAVATION, BarColor.YELLOW);
        SKILL_XPBAR_COLOR_DEFAULT.put(PrimarySkillType.TAMING, BarColor.RED);
        SKILL_XPBAR_COLOR_DEFAULT.put(PrimarySkillType.REPAIR, BarColor.PURPLE);
        SKILL_XPBAR_COLOR_DEFAULT.put(PrimarySkillType.FISHING, BarColor.PURPLE);

        SKILL_XPBAR_STYLE_DEFAULT = new HashMap<>();
        SKILL_XPBAR_STYLE_DEFAULT.put(PrimarySkillType.ACROBATICS, BarStyle.SEGMENTED_6);
        SKILL_XPBAR_STYLE_DEFAULT.put(PrimarySkillType.ARCHERY, BarStyle.SEGMENTED_6);
        SKILL_XPBAR_STYLE_DEFAULT.put(PrimarySkillType.AXES, BarStyle.SEGMENTED_6);
        SKILL_XPBAR_STYLE_DEFAULT.put(PrimarySkillType.ALCHEMY, BarStyle.SEGMENTED_6);
        SKILL_XPBAR_STYLE_DEFAULT.put(PrimarySkillType.WOODCUTTING, BarStyle.SEGMENTED_6);
        SKILL_XPBAR_STYLE_DEFAULT.put(PrimarySkillType.UNARMED, BarStyle.SEGMENTED_6);
        SKILL_XPBAR_STYLE_DEFAULT.put(PrimarySkillType.SWORDS, BarStyle.SEGMENTED_6);
        SKILL_XPBAR_STYLE_DEFAULT.put(PrimarySkillType.HERBALISM, BarStyle.SEGMENTED_6);
        SKILL_XPBAR_STYLE_DEFAULT.put(PrimarySkillType.MINING, BarStyle.SEGMENTED_6);
        SKILL_XPBAR_STYLE_DEFAULT.put(PrimarySkillType.EXCAVATION, BarStyle.SEGMENTED_6);
        SKILL_XPBAR_STYLE_DEFAULT.put(PrimarySkillType.TAMING, BarStyle.SEGMENTED_6);
        SKILL_XPBAR_STYLE_DEFAULT.put(PrimarySkillType.REPAIR, BarStyle.SEGMENTED_6);
        SKILL_XPBAR_STYLE_DEFAULT.put(PrimarySkillType.FISHING, BarStyle.SEGMENTED_6);
    }

    @Setting(value = "Party-Experience-Triggers-XP-Bars", comment = "Whether or not shared XP gains from parties will trigger XP bar displays" +
            "\nThis can result in a very cluttered UI even in smaller parties, I recommend leaving this off." +
            "\nDefault value: " + PARTY_XP_DEFAULT)
    private boolean partyExperienceTriggerXpBarDisplay = PARTY_XP_DEFAULT;

    @Setting(value = "Passive-Experience-Trigger-XP-Bars", comment = "Whether or not XP gained from stuff like furnaces or brewing stands will trigger XP bars" +
            "\nThis is on by default, but this can also cause a lot of clutter, turn it off it you'd like a cleaner UI." +
            "\nDefault value: " + PASSIVE_XP_DEFAULT)
    private boolean passiveGainXPBars = PASSIVE_XP_DEFAULT;

    @Setting(value = "Extra-Details", comment = "Adds extra details to the XP bar, these cause a bit more performance overhead and may not look very pretty" +
            "\nYou can customize the more detailed XP bar in the locale" +
            "\nLocaleManager key - XPBar.Complex.Template" +
            "\nThe default extra detailed XP bar will include quite a few extra things, you can actually remove each thing you don't want displayed in the locale" +
            "\nFor tips on editing the locale check - https://mcmmo.org/wiki/Locale" +
            "\nDefault value: " + DETAILED_XP_BARS_DEFAULT)
    private boolean moreDetailedXPBars = DETAILED_XP_BARS_DEFAULT;

    @Setting(value = "Enable-XP-Bars", comment = "Whether or not XP bars will be displayed on certain XP gains" +
            "\nBy default, some XP gains do not show as they would create a lot of UI clutter, see the other options to change this" +
            "\nDefault value: " + XP_BARS_DEFAULT)
    private boolean enableXPBars = XP_BARS_DEFAULT;

    @Setting(value = "Skill-XP-Bar-Toggle", comment = "Turn on or off specific XP bars" +
            "\nFilter which skills you'd like to enable XP bars for" +
            "\nBy default all skills are enabled, undefined entries will be true by default as well")
    private HashMap<PrimarySkillType, Boolean> xpBarSpecificToggles = SKILL_XPBAR_TOGGLES_DEFAULT;

    @Setting(value = "Skill-XP-Bar-Colors", comment = "Compatible Settings - https://hub.spigotmc.org/javadocs/spigot/org/bukkit/boss/BarColor.html" +
            "\nThese are the only valid colors for Experience Bars, use the name found here" +
            "\nBLUE, GREEN, PINK, PURPLE, RED, WHITE, YELLOW (As of the time of this update these are the only Bar colors available, this could change in the future so check the BarColor enum to see if it has)")
    private HashMap<PrimarySkillType, BarColor> xpBarColorMap = SKILL_XPBAR_COLOR_DEFAULT;

    @Setting(value = "Skill-XP-Bar-Styles", comment = "This is the display style for the XP bars." +
            "\nHere are the available options" +
            "\nSEGMENTED_6" +
            "\nSEGMENTED_10" +
            "\nSEGMENTED_12" +
            "\nSEGMENTED_20" +
            "\nSOLID - No segments in the bar" +
            "\nI liked the way SEGMENTED_6 looked, so that's the default look for all bars")
    private HashMap<PrimarySkillType, BarStyle> xpBarStyleMap = SKILL_XPBAR_STYLE_DEFAULT;

    public boolean isPartyExperienceTriggerXpBarDisplay() {
        return partyExperienceTriggerXpBarDisplay;
    }

    public boolean isPassiveGainXPBars() {
        return passiveGainXPBars;
    }

    public boolean isMoreDetailedXPBars() {
        return moreDetailedXPBars;
    }

    public boolean isEnableXPBars() {
        return enableXPBars;
    }

    public boolean getXPBarToggle(PrimarySkillType primarySkillType) {
        if (xpBarSpecificToggles.get(primarySkillType) == null)
            return true;

        return xpBarSpecificToggles.get(primarySkillType);
    }

    public BarColor getXPBarColor(PrimarySkillType primarySkillType) {
        if (xpBarColorMap.get(primarySkillType) == null)
            return BarColor.WHITE;

        return xpBarColorMap.get(primarySkillType);
    }

    public BarStyle getXPBarStyle(PrimarySkillType primarySkillType) {
        if (xpBarStyleMap.get(primarySkillType) == null)
            return BarStyle.SEGMENTED_6;

        return xpBarStyleMap.get(primarySkillType);
    }

    public HashMap<PrimarySkillType, Boolean> getXpBarSpecificToggles() {
        return xpBarSpecificToggles;
    }

    public HashMap<PrimarySkillType, BarColor> getXpBarColorMap() {
        return xpBarColorMap;
    }

    public HashMap<PrimarySkillType, BarStyle> getXpBarStyleMap() {
        return xpBarStyleMap;
    }
}
