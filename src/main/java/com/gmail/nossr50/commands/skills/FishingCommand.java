package com.gmail.nossr50.commands.skills;

import com.gmail.nossr50.commands.SkillCommand;
import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.skills.gathering.Fishing;
import com.gmail.nossr50.util.Permissions;

public class FishingCommand extends SkillCommand {

    AdvancedConfig advancedConfig = AdvancedConfig.getInstance();

    private int lootTier;
    private String magicChance;
    private String magicChanceLucky;
    private String chanceRaining = "";
    private String shakeChance;
    private String shakeChanceLucky;
    private String fishermansDietRank;

    private boolean canTreasureHunt;
    private boolean canMagicHunt;
    private boolean canShake;
    private boolean canFishermansDiet;

    public FishingCommand() {
        super(SkillType.FISHING);
    }

    @Override
    protected void dataCalculations() {
        lootTier = Fishing.getFishingLootTier(profile);

        //TREASURE HUNTER
        double enchantChance = lootTier * Fishing.magicHunterMultiplier;

        if (player.getWorld().hasStorm()) {
            chanceRaining = LocaleLoader.getString("Fishing.Chance.Raining");
            enchantChance = enchantChance * 1.1D;
        }

        String[] treasureHunterStrings = calculateAbilityDisplayValues(enchantChance);
        magicChance = treasureHunterStrings[0];
        magicChanceLucky = treasureHunterStrings[1];

        //SHAKE
        String[] shakeStrings = calculateAbilityDisplayValues(Fishing.getShakeChance(lootTier));
        shakeChance = shakeStrings[0];
        shakeChanceLucky = shakeStrings[1];

        //FISHERMAN'S DIET
        fishermansDietRank = calculateRank(Fishing.fishermanDietMaxLevel, Fishing.fishermanDietRankChangeLevel);
    }

    @Override
    protected void permissionsCheck() {
        canTreasureHunt = Permissions.fishingTreasures(player);
        canMagicHunt = Permissions.fishingMagic(player);
        canShake = Permissions.shakeMob(player);
        canFishermansDiet = Permissions.fishermansDiet(player);
    }

    @Override
    protected boolean effectsHeaderPermissions() {
        return canTreasureHunt || canMagicHunt || canShake;
    }

    @Override
    protected void effectsDisplay() {
        luckyEffectsDisplay();

        if (canTreasureHunt) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Fishing.Effect.0"), LocaleLoader.getString("Fishing.Effect.1") }));
        }

        if (canMagicHunt) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Fishing.Effect.2"), LocaleLoader.getString("Fishing.Effect.3") }));
        }

        if (canShake) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Fishing.Effect.4"), LocaleLoader.getString("Fishing.Effect.5") }));
        }

        if (canFishermansDiet) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Fishing.Effect.6"), LocaleLoader.getString("Fishing.Effect.7") }));
        }
    }

    @Override
    protected boolean statsHeaderPermissions() {
        return canTreasureHunt || canMagicHunt || canShake;
    }

    @Override
    protected void statsDisplay() {
        if (canTreasureHunt) {
            player.sendMessage(LocaleLoader.getString("Fishing.Ability.Rank", new Object[] { lootTier }));
        }

        if (canMagicHunt) {
            if (isLucky) {
                player.sendMessage(LocaleLoader.getString("Fishing.Enchant.Chance", new Object[] { magicChance}) + chanceRaining +  LocaleLoader.getString("Perks.lucky.bonus", new Object[] { magicChanceLucky }));
            }
            else {
                player.sendMessage(LocaleLoader.getString("Fishing.Enchant.Chance", new Object[] { magicChance}) + chanceRaining);
            }
        }

        if (canShake) {
            if (skillValue < Fishing.shakeUnlockLevel) {
                player.sendMessage(LocaleLoader.getString("Ability.Generic.Template.Lock", new Object[] { LocaleLoader.getString("Fishing.Ability.Locked.0", new Object[] { Fishing.shakeUnlockLevel }) }));
            }
            else {
                if (isLucky) {
                    player.sendMessage(LocaleLoader.getString("Fishing.Ability.Shake", new Object[] { shakeChance }) + LocaleLoader.getString("Perks.lucky.bonus", new Object[] { shakeChanceLucky }));
                }
                else {
                    player.sendMessage(LocaleLoader.getString("Fishing.Ability.Shake", new Object[] { shakeChance }));
                }
            }
        }

        if (canFishermansDiet) {
            player.sendMessage(LocaleLoader.getString("Fishing.Ability.FD", new Object[] { fishermansDietRank }));
        }
    }
}
