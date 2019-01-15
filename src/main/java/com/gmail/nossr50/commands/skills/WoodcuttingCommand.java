package com.gmail.nossr50.commands.skills;

import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.TextComponentFactory;
import com.gmail.nossr50.util.skills.RankUtils;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class WoodcuttingCommand extends SkillCommand {
    private String treeFellerLength;
    private String treeFellerLengthEndurance;
    private String doubleDropChance;
    private String doubleDropChanceLucky;

    private boolean canTreeFell;
    private boolean canLeafBlow;
    private boolean canDoubleDrop;
    private boolean canSplinter;
    private boolean canBarkSurgeon;
    private boolean canNaturesBounty;

    public WoodcuttingCommand() {
        super(PrimarySkillType.WOODCUTTING);
    }

    @Override
    protected void dataCalculations(Player player, float skillValue, boolean isLucky) {
        // TREE FELLER
        if (canTreeFell) {
            String[] treeFellerStrings = calculateLengthDisplayValues(player, skillValue);
            treeFellerLength = treeFellerStrings[0];
            treeFellerLengthEndurance = treeFellerStrings[1];
        }

        // DOUBLE DROPS
        if (canDoubleDrop) {
            setDoubleDropClassicChanceStrings(skillValue, isLucky);
        }
    }

    private void setDoubleDropClassicChanceStrings(float skillValue, boolean isLucky) {
        String[] doubleDropStrings = calculateAbilityDisplayValues(skillValue, SubSkillType.WOODCUTTING_HARVEST_LUMBER, isLucky);
        doubleDropChance = doubleDropStrings[0];
        doubleDropChanceLucky = doubleDropStrings[1];
    }

    @Override
    protected void permissionsCheck(Player player) {
        canTreeFell = RankUtils.hasUnlockedSubskill(player, SubSkillType.WOODCUTTING_TREE_FELLER) && Permissions.treeFeller(player);
        canDoubleDrop = canUseSubskill(player, SubSkillType.WOODCUTTING_HARVEST_LUMBER) && !skill.getDoubleDropsDisabled() && RankUtils.getRank(player, SubSkillType.WOODCUTTING_HARVEST_LUMBER) >= 1;
        canLeafBlow = canUseSubskill(player, SubSkillType.WOODCUTTING_LEAF_BLOWER);
        canSplinter = canUseSubskill(player, SubSkillType.WOODCUTTING_SPLINTER);
        canBarkSurgeon = canUseSubskill(player, SubSkillType.WOODCUTTING_BARK_SURGEON);
        canNaturesBounty = canUseSubskill(player, SubSkillType.WOODCUTTING_NATURES_BOUNTY);
    }

    @Override
    protected List<String> effectsDisplay() {
        List<String> messages = new ArrayList<String>();

        if (canTreeFell) {
            messages.add(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Woodcutting.Effect.0"), LocaleLoader.getString("Woodcutting.Effect.1")));
        }

        if (canLeafBlow) {
            messages.add(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Woodcutting.Effect.2"), LocaleLoader.getString("Woodcutting.Effect.3")));
        }

        if (canDoubleDrop) {
            messages.add(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Woodcutting.Effect.4"), LocaleLoader.getString("Woodcutting.Effect.5")));
        }

        if (canSplinter) {
            messages.add(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Woodcutting.Effect.6"), LocaleLoader.getString("Woodcutting.Effect.7")));
        }

        if(canBarkSurgeon) {
            messages.add(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Woodcutting.Effect.8"), LocaleLoader.getString("Woodcutting.Effect.9")));
        }

        if(canNaturesBounty) {
            messages.add(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Woodcutting.Effect.10"), LocaleLoader.getString("Woodcutting.Effect.11")));
        }



        return messages;
    }

    @Override
    protected List<String> statsDisplay(Player player, float skillValue, boolean hasEndurance, boolean isLucky) {
        List<String> messages = new ArrayList<String>();

        if (canLeafBlow) {
            int leafBlowerUnlockLevel = AdvancedConfig.getInstance().getLeafBlowUnlockLevel();

            if (skillValue < leafBlowerUnlockLevel) {
                messages.add(LocaleLoader.getString("Ability.Generic.Template.Lock", LocaleLoader.getString("Woodcutting.Ability.Locked.0", leafBlowerUnlockLevel)));
            }
            else {
                messages.add(LocaleLoader.getString("Ability.Generic.Template", LocaleLoader.getString("Woodcutting.Ability.0"), LocaleLoader.getString("Woodcutting.Ability.1")));
            }
        }

        if (canDoubleDrop) {
            messages.add(LocaleLoader.getString("Woodcutting.Ability.Chance.DDrop", doubleDropChance) + (isLucky ? LocaleLoader.getString("Perks.Lucky.Bonus", doubleDropChanceLucky) : ""));
        }

        if (canTreeFell) {
            messages.add(LocaleLoader.getString("Woodcutting.Ability.Length", treeFellerLength) + (hasEndurance ? LocaleLoader.getString("Perks.ActivationTime.Bonus", treeFellerLengthEndurance) : ""));
        }

        return messages;
    }

    @Override
    protected List<TextComponent> getTextComponents(Player player) {
        List<TextComponent> textComponents = new ArrayList<>();

        TextComponentFactory.getSubSkillTextComponents(player, textComponents, PrimarySkillType.WOODCUTTING);

        return textComponents;
    }


}
