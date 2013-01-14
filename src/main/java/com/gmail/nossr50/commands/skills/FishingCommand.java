package com.gmail.nossr50.commands.skills;

import org.bukkit.ChatColor;

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
    private String chanceRaining;
    private int shakeUnlockLevel;
    private String shakeChance;
    private String shakeChanceLucky;
    private String fishermansDietRank;

    private int magicHunterMultiplier = advancedConfig.getFishingMagicMultiplier();
    private int fishermansDietRankChange = advancedConfig.getFarmerDietRankChange();
    private int fishermansDietRankMaxLevel = fishermansDietRankChange * 5;

    private boolean canTreasureHunt;
    private boolean canMagicHunt;
    private boolean canShake;
    private boolean canFishermansDiet;
    private boolean lucky;
    private boolean raining;

    public FishingCommand() {
        super(SkillType.FISHING);
    }

    @Override
    protected void dataCalculations() {
        raining = player.getWorld().hasStorm();
        chanceRaining = "";
        //Treasure Hunter
        lootTier = Fishing.getFishingLootTier(profile);
        double magicChanceD = lootTier * magicHunterMultiplier;
        if (raining) {
            chanceRaining = LocaleLoader.getString("Fishing.Chance.Raining");
            magicChanceD = magicChanceD * 1.1D;
        }
        magicChance = percent.format(magicChanceD / 100D);
        if (magicChanceD * 1.3333D >= 100D) magicChanceLucky = percent.format(1D);
        else magicChanceLucky = percent.format(magicChanceD * 1.3333D / 100D);

        //Shake
        int dropChance = Fishing.getShakeChance(lootTier);
        shakeChance = percent.format(dropChance / 100D);
        if (dropChance * 1.3333D >= 100D) shakeChanceLucky = percent.format(1D);
        else shakeChanceLucky = percent.format(dropChance * 1.3333D / 100D);
        shakeUnlockLevel = advancedConfig.getShakeUnlockLevel();

        //Fishermans Diet
        if (skillValue >= fishermansDietRankMaxLevel) fishermansDietRank = "5";
        else fishermansDietRank = String.valueOf((int) ((double) skillValue / (double) fishermansDietRankChange));
    }

    @Override
    protected void permissionsCheck() {
        canTreasureHunt = Permissions.fishingTreasures(player);
        canMagicHunt = Permissions.fishingMagic(player);
        canShake = Permissions.shakeMob(player);
        canFishermansDiet = Permissions.fishermansDiet(player);
        lucky = Permissions.luckyFishing(player);
    }

    @Override
    protected boolean effectsHeaderPermissions() {
        return canTreasureHunt || canMagicHunt || canShake;
    }

    @Override
    protected void effectsDisplay() {
        if (lucky) {
            String perkPrefix = ChatColor.RED + "[mcMMO Perks] ";
            player.sendMessage(perkPrefix + LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Perks.lucky.name"), LocaleLoader.getString("Perks.lucky.desc", new Object[] { "Fishing" }) }));
        }

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
            if (lucky)
                player.sendMessage(LocaleLoader.getString("Fishing.Enchant.Chance", new Object[] { magicChance}) + chanceRaining +  LocaleLoader.getString("Perks.lucky.bonus", new Object[] { magicChanceLucky }));
            else
                player.sendMessage(LocaleLoader.getString("Fishing.Enchant.Chance", new Object[] { magicChance}) + chanceRaining);
        }

        if (canShake) {
            if (skillValue < advancedConfig.getShakeUnlockLevel()) {
                player.sendMessage(LocaleLoader.getString("Ability.Generic.Template.Lock", new Object[] { LocaleLoader.getString("Fishing.Ability.Locked.0", new Object[] { shakeUnlockLevel }) }));
            }
            else {
                if (lucky)
                    player.sendMessage(LocaleLoader.getString("Fishing.Ability.Shake", new Object[] { shakeChance }) + LocaleLoader.getString("Perks.lucky.bonus", new Object[] { shakeChanceLucky }));
                else
                    player.sendMessage(LocaleLoader.getString("Fishing.Ability.Shake", new Object[] { shakeChance }));
            }
        }

        if (canFishermansDiet) {
            player.sendMessage(LocaleLoader.getString("Fishing.Ability.FD", new Object[] { fishermansDietRank }));
        }
    }
}
