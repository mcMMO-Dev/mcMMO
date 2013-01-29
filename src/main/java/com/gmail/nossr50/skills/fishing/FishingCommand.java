package com.gmail.nossr50.skills.fishing;

import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.skills.SkillCommand;
import com.gmail.nossr50.skills.SkillType;
import com.gmail.nossr50.util.Permissions;

public class FishingCommand extends SkillCommand {
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
        lootTier = Fishing.getLootTier((int) skillValue);

        //TREASURE HUNTER
        double enchantChance = lootTier * AdvancedConfig.getInstance().getFishingMagicMultiplier();

        if (player.getWorld().hasStorm()) {
            chanceRaining = LocaleLoader.getString("Fishing.Chance.Raining");
            enchantChance = enchantChance * 1.1D;
        }

        String[] treasureHunterStrings = calculateAbilityDisplayValues(enchantChance);
        magicChance = treasureHunterStrings[0];
        magicChanceLucky = treasureHunterStrings[1];

        //SHAKE
        String[] shakeStrings = calculateAbilityDisplayValues(ShakeMob.getShakeProbability(lootTier));
        shakeChance = shakeStrings[0];
        shakeChanceLucky = shakeStrings[1];

        //FISHERMAN'S DIET
        fishermansDietRank = calculateRank(Fishing.fishermansDietMaxLevel, Fishing.fishermansDietRankLevel1);
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
            if (skillValue < AdvancedConfig.getInstance().getShakeUnlockLevel()) {
                player.sendMessage(LocaleLoader.getString("Ability.Generic.Template.Lock", new Object[] { LocaleLoader.getString("Fishing.Ability.Locked.0", new Object[] { AdvancedConfig.getInstance().getShakeUnlockLevel() }) }));
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
