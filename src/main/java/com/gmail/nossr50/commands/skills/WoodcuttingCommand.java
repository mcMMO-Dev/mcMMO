package com.gmail.nossr50.commands.skills;

import org.bukkit.ChatColor;

import com.gmail.nossr50.commands.SkillCommand;
import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Permissions;

public class WoodcuttingCommand extends SkillCommand {
    AdvancedConfig advancedConfig = AdvancedConfig.getInstance();
    private String treeFellerLength;
    private String treeFellerLengthEndurance;
    private String doubleDropChance;
    private String doubleDropChanceLucky;

    private int abilityLengthIncreaseLevel = advancedConfig.getAbilityLength();
    private double doubleDropsMaxBonus = advancedConfig.getWoodcuttingDoubleDropChance();
    private int doubleDropsMaxLevel = advancedConfig.getWoodcuttingDoubleDropMaxLevel();
    private int leafBlowUnlock = advancedConfig.getLeafBlowUnlockLevel();

    private boolean canTreeFell;
    private boolean canLeafBlow;
    private boolean canDoubleDrop;
    private boolean doubleDropsDisabled;
    private boolean lucky;
    private boolean endurance;

    public WoodcuttingCommand() {
        super(SkillType.WOODCUTTING);
    }

    @Override
    protected void dataCalculations() {
        float doubleDropChanceF;

        //Tree Feller
        int length = 2 + (int) ((double) skillValue / (double) abilityLengthIncreaseLevel);
        treeFellerLength = String.valueOf(length);

        if (Permissions.activationTwelve(player)) {
            length = length + 12;
        }
        else if (Permissions.activationEight(player)) {
            length = length + 8;
        }
        else if (Permissions.activationFour(player)) {
            length = length + 4;
        }
        int maxLength = SkillType.WOODCUTTING.getAbility().getMaxTicks();
        if (maxLength != 0 && length > maxLength) {
            length = maxLength;
        }
        treeFellerLengthEndurance = String.valueOf(length);

        //Double Drops
        if (skillValue >= doubleDropsMaxLevel) doubleDropChanceF = (float) (doubleDropsMaxBonus);
        else doubleDropChanceF = (float) ((doubleDropsMaxBonus / doubleDropsMaxLevel) * skillValue);
        doubleDropChance = percent.format(doubleDropChanceF / 100D);
        if (doubleDropChanceF * 1.3333D >= 100D) doubleDropChanceLucky = percent.format(1D);
        else doubleDropChanceLucky = percent.format(doubleDropChanceF * 1.3333D / 100D);
    }

    @Override
    protected void permissionsCheck() {
        Config configInstance = Config.getInstance();

        canTreeFell = Permissions.treeFeller(player);
        canDoubleDrop = Permissions.woodcuttingDoubleDrops(player);
        canLeafBlow = Permissions.leafBlower(player);
        doubleDropsDisabled = configInstance.woodcuttingDoubleDropsDisabled();
        lucky = Permissions.luckyWoodcutting(player);
        endurance = Permissions.activationTwelve(player) || Permissions.activationEight(player) || Permissions.activationFour(player);
    }

    @Override
    protected boolean effectsHeaderPermissions() {
        return (canDoubleDrop && !doubleDropsDisabled) || canLeafBlow || canTreeFell;
    }

    @Override
    protected void effectsDisplay() {
        if (lucky) {
            String perkPrefix = ChatColor.RED + "[mcMMO Perks] ";
            player.sendMessage(perkPrefix + LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Perks.lucky.name"), LocaleLoader.getString("Perks.lucky.desc", new Object[] { "Woodcutting" }) }));
        }

        if (canTreeFell) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Woodcutting.Effect.0"), LocaleLoader.getString("Woodcutting.Effect.1") }));
        }

        if (canLeafBlow) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Woodcutting.Effect.2"), LocaleLoader.getString("Woodcutting.Effect.3") }));
        }

        if (canDoubleDrop && !doubleDropsDisabled) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Woodcutting.Effect.4"), LocaleLoader.getString("Woodcutting.Effect.5") }));
        }
    }

    @Override
    protected boolean statsHeaderPermissions() {
        return (canDoubleDrop && !doubleDropsDisabled) || canLeafBlow || canTreeFell;
    }

    @Override
    protected void statsDisplay() {
        //TODO: Remove? Basically duplicates the above.
        if (canLeafBlow) {
            if (skillValue < leafBlowUnlock) {
                player.sendMessage(LocaleLoader.getString("Ability.Generic.Template.Lock", new Object[] { LocaleLoader.getString("Woodcutting.Ability.Locked.0", new Object[] { leafBlowUnlock }) }));
            }
            else {
                player.sendMessage(LocaleLoader.getString("Ability.Generic.Template", new Object[] { LocaleLoader.getString("Woodcutting.Ability.0"), LocaleLoader.getString("Woodcutting.Ability.1") }));
            }
        }

        if (canDoubleDrop && !doubleDropsDisabled) {
            if (lucky)
                player.sendMessage(LocaleLoader.getString("Woodcutting.Ability.Chance.DDrop", new Object[] { doubleDropChance }) + LocaleLoader.getString("Perks.lucky.bonus", new Object[] { doubleDropChanceLucky }));
            else
                player.sendMessage(LocaleLoader.getString("Woodcutting.Ability.Chance.DDrop", new Object[] { doubleDropChance }));
        }

        if (canTreeFell) {
            if (endurance)
                player.sendMessage(LocaleLoader.getString("Woodcutting.Ability.Length", new Object[] { treeFellerLength }) + LocaleLoader.getString("Perks.activationtime.bonus", new Object[] { treeFellerLengthEndurance }));
            else
                player.sendMessage(LocaleLoader.getString("Woodcutting.Ability.Length", new Object[] { treeFellerLength }));
        }
    }
}
