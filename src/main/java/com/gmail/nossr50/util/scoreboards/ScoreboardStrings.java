package com.gmail.nossr50.util.scoreboards;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SuperAbilityType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.Misc;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.bukkit.ChatColor;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Temporary class
 * Scoreboard code was a mess, in the process of unmaking it a static singleton cluster-@#$% I decided to hold off on rewriting that abomination and use this class a temporary band-aid fix
 */
//TODO: Rewrite scoreboard code so this thing doesn't need to exist
public class ScoreboardStrings {
    private final mcMMO pluginRef;
    // do not localize; these are internal identifiers
    public final String SIDEBAR_OBJECTIVE = "mcmmo_sidebar";
    public final String POWER_OBJECTIVE = "mcmmo_pwrlvl";

    public String HEADER_STATS;
    public final String HEADER_COOLDOWNS;
    public final String HEADER_RANK;
    public final String TAG_POWER_LEVEL;

    public final String POWER_LEVEL;

    public final String LABEL_POWER_LEVEL;
    public final String LABEL_LEVEL;
    public final String LABEL_CURRENT_XP;
    public final String LABEL_REMAINING_XP;
    public final String LABEL_ABILITY_COOLDOWN;
    public final String LABEL_OVERALL;

    public Map<PrimarySkillType, String> skillLabels;
    public Map<SuperAbilityType, String> abilityLabelsColored;
    public Map<SuperAbilityType, String> abilityLabelsSkill;

    public ScoreboardStrings(mcMMO pluginRef) {
        this.pluginRef = pluginRef;

        HEADER_STATS = pluginRef.getLocaleManager().getString("Scoreboard.Header.PlayerStats");
        HEADER_COOLDOWNS = pluginRef.getLocaleManager().getString("Scoreboard.Header.PlayerCooldowns");
        HEADER_RANK = pluginRef.getLocaleManager().getString("Scoreboard.Header.PlayerRank");
        TAG_POWER_LEVEL = pluginRef.getLocaleManager().getString("Scoreboard.Header.PowerLevel");

        POWER_LEVEL = pluginRef.getLocaleManager().getString("Scoreboard.Misc.PowerLevel");

        LABEL_POWER_LEVEL = POWER_LEVEL;
        LABEL_LEVEL = pluginRef.getLocaleManager().getString("Scoreboard.Misc.Level");
        LABEL_CURRENT_XP = pluginRef.getLocaleManager().getString("Scoreboard.Misc.CurrentXP");
        LABEL_REMAINING_XP = pluginRef.getLocaleManager().getString("Scoreboard.Misc.RemainingXP");
        LABEL_ABILITY_COOLDOWN = pluginRef.getLocaleManager().getString("Scoreboard.Misc.Cooldown");
        LABEL_OVERALL = pluginRef.getLocaleManager().getString("Scoreboard.Misc.Overall");

        init(pluginRef);
    }

    /*
     * Initializes the properties of this class
     */
    private void init(mcMMO pluginRef) {
        /*
         * We need immutable objects for our ConfigScoreboard's labels
         */
        ImmutableMap.Builder<PrimarySkillType, String> skillLabelBuilder = ImmutableMap.builder();
        ImmutableMap.Builder<SuperAbilityType, String> abilityLabelBuilder = ImmutableMap.builder();
        ImmutableMap.Builder<SuperAbilityType, String> abilityLabelSkillBuilder = ImmutableMap.builder();

        /*
         * Builds the labels for our ScoreBoards
         * Stylizes the targetBoard in a Rainbow Pattern
         * This is off by default
         */
        if (pluginRef.getScoreboardSettings().getUseRainbowSkillStyling()) {
            // Everything but black, gray, gold
            List<ChatColor> colors = Lists.newArrayList(
                    ChatColor.WHITE,
                    ChatColor.YELLOW,
                    ChatColor.LIGHT_PURPLE,
                    ChatColor.RED,
                    ChatColor.AQUA,
                    ChatColor.GREEN,
                    ChatColor.DARK_GRAY,
                    ChatColor.BLUE,
                    ChatColor.DARK_PURPLE,
                    ChatColor.DARK_RED,
                    ChatColor.DARK_AQUA,
                    ChatColor.DARK_GREEN,
                    ChatColor.DARK_BLUE);

            Collections.shuffle(colors, Misc.getRandom());

            int i = 0;
            for (PrimarySkillType primarySkillType : PrimarySkillType.values()) {
                // Include child skills
                skillLabelBuilder.put(primarySkillType, getShortenedName(colors.get(i) + pluginRef.getSkillTools().getLocalizedSkillName(primarySkillType), false));

                if (pluginRef.getSkillTools().getSuperAbility(primarySkillType) != null) {
                    abilityLabelBuilder.put(pluginRef.getSkillTools().getSuperAbility(primarySkillType), getShortenedName(colors.get(i) + pluginRef.getSkillTools().getSuperAbility(primarySkillType).getName()));

                    if (primarySkillType == PrimarySkillType.MINING) {
                        abilityLabelBuilder.put(SuperAbilityType.BLAST_MINING, getShortenedName(colors.get(i) + SuperAbilityType.BLAST_MINING.getName()));
                    }
                }

                if (++i == colors.size()) {
                    i = 0;
                }
            }
        }
        /*
         * Builds the labels for our ScoreBoards
         * Stylizes the targetBoard using our normal color scheme
         */
        else {
            for (PrimarySkillType primarySkillType : PrimarySkillType.values()) {
                // Include child skills
                skillLabelBuilder.put(primarySkillType, getShortenedName(ChatColor.GREEN + pluginRef.getSkillTools().getLocalizedSkillName(primarySkillType)));

                if (pluginRef.getSkillTools().getSuperAbility(primarySkillType) != null) {
                    abilityLabelBuilder.put(pluginRef.getSkillTools().getSuperAbility(primarySkillType), formatAbility(pluginRef.getSkillTools().getSuperAbility(primarySkillType).getName()));

                    if (primarySkillType == PrimarySkillType.MINING) {
                        abilityLabelBuilder.put(SuperAbilityType.BLAST_MINING, formatAbility(SuperAbilityType.BLAST_MINING.getName()));
                    }
                }
            }
        }

        for (SuperAbilityType type : SuperAbilityType.values()) {
            abilityLabelSkillBuilder.put(type, formatAbility((type == SuperAbilityType.BLAST_MINING ? ChatColor.BLUE : ChatColor.AQUA), type.getName()));
        }

        skillLabels = skillLabelBuilder.build();
        abilityLabelsColored = abilityLabelBuilder.build();
        abilityLabelsSkill = abilityLabelSkillBuilder.build();
    }

    private String formatAbility(String abilityName) {
        return formatAbility(ChatColor.AQUA, abilityName);
    }

    private String formatAbility(ChatColor color, String abilityName) {
        if (pluginRef.getScoreboardSettings().getUseAbilityNamesOverGenerics()) {
            return getShortenedName(color + abilityName);
        } else {
            return color + pluginRef.getLocaleManager().getString("Scoreboard.Misc.Ability");
        }
    }

    private String getShortenedName(String name) {
        return getShortenedName(name, true);
    }

    private String getShortenedName(String name, boolean useDots) {
        if (name.length() > 16) {
            name = useDots ? name.substring(0, 14) + ".." : name.substring(0, 16);
        }

        return name;
    }
}
