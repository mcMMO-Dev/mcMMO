package com.gmail.nossr50.commands.skills;

import java.util.ArrayList;
import java.util.List;

import com.gmail.nossr50.datatypes.skills.PrimarySkill;
import com.gmail.nossr50.datatypes.skills.SkillMilestone;
import com.gmail.nossr50.datatypes.skills.SubSkill;
import com.gmail.nossr50.util.player.UserManager;
import com.gmail.nossr50.util.skills.SkillMilestoneFactory;
import org.bukkit.entity.Player;

import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Permissions;

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
        super(PrimarySkill.WOODCUTTING);
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
            if(AdvancedConfig.getInstance().isSubSkillClassic(SubSkill.WOODCUTTING_HARVEST_LUMBER))
                setDoubleDropClassicChanceStrings(skillValue, isLucky);
            else
            {
                //TODO: Set up datastrings for new harvest
            }
        }
    }

    private void setDoubleDropClassicChanceStrings(float skillValue, boolean isLucky) {
        String[] doubleDropStrings = calculateAbilityDisplayValues(skillValue, SubSkill.WOODCUTTING_HARVEST_LUMBER, isLucky);
        doubleDropChance = doubleDropStrings[0];
        doubleDropChanceLucky = doubleDropStrings[1];
    }

    @Override
    protected void permissionsCheck(Player player) {
        canTreeFell = Permissions.treeFeller(player);
        canDoubleDrop = Permissions.isSubSkillEnabled(player, SubSkill.WOODCUTTING_HARVEST_LUMBER) && !skill.getDoubleDropsDisabled();
        canLeafBlow = Permissions.isSubSkillEnabled(player, SubSkill.WOODCUTTING_LEAF_BLOWER);
        canSplinter = Permissions.isSubSkillEnabled(player, SubSkill.WOODCUTTING_SPLINTER);
        canBarkSurgeon = Permissions.isSubSkillEnabled(player, SubSkill.WOODCUTTING_BARK_SURGEON);
        canNaturesBounty = Permissions.isSubSkillEnabled(player, SubSkill.WOODCUTTING_NATURES_BOUNTY);
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
}
