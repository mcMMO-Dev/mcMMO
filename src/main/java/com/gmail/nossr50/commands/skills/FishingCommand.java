package com.gmail.nossr50.commands.skills;

import org.bukkit.Location;
import org.bukkit.block.Biome;
import org.bukkit.entity.EntityType;

import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.config.treasure.TreasureConfig;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.datatypes.treasure.Rarity;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.skills.fishing.Fishing;
import com.gmail.nossr50.skills.fishing.Fishing.Tier;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.player.UserManager;

public class FishingCommand extends SkillCommand {
    private int lootTier;
    private String shakeChance;
    private String shakeChanceLucky;
    private int fishermansDietRank;
    private String biteChance;

    private String trapTreasure;
    private String commonTreasure;
    private String uncommonTreasure;
    private String rareTreasure;
    private String epicTreasure;
    private String legendaryTreasure;
    private String recordTreasure;

    private String magicChance;

    private boolean canTreasureHunt;
    private boolean canMagicHunt;
    private boolean canShake;
    private boolean canFishermansDiet;
    private boolean canMasterAngler;
    private boolean canIceFish;

    public FishingCommand() {
        super(SkillType.FISHING);
    }

    @Override
    protected void dataCalculations() {
        boolean isStorming = player.getWorld().hasStorm();

        // TREASURE HUNTER
        if (canTreasureHunt) {
            lootTier = mcMMOPlayer.getFishingManager().getLootTier();

            // Item drop rates
            trapTreasure = calculateAbilityDisplayValues(TreasureConfig.getInstance().getItemDropRate(lootTier, Rarity.TRAP))[0];
            commonTreasure = calculateAbilityDisplayValues(TreasureConfig.getInstance().getItemDropRate(lootTier, Rarity.COMMON))[0];
            uncommonTreasure = calculateAbilityDisplayValues(TreasureConfig.getInstance().getItemDropRate(lootTier, Rarity.UNCOMMON))[0];
            rareTreasure = calculateAbilityDisplayValues(TreasureConfig.getInstance().getItemDropRate(lootTier, Rarity.RARE))[0];
            epicTreasure = calculateAbilityDisplayValues(TreasureConfig.getInstance().getItemDropRate(lootTier, Rarity.EPIC))[0];
            legendaryTreasure = calculateAbilityDisplayValues(TreasureConfig.getInstance().getItemDropRate(lootTier, Rarity.LEGENDARY))[0];
            recordTreasure = calculateAbilityDisplayValues(TreasureConfig.getInstance().getItemDropRate(lootTier, Rarity.RECORD))[0];

            // Magic hunter drop rates
            double commonEnchantment = TreasureConfig.getInstance().getEnchantmentDropRate(lootTier, Rarity.COMMON);
            double uncommonEnchantment = TreasureConfig.getInstance().getEnchantmentDropRate(lootTier, Rarity.UNCOMMON);
            double rareEnchantment = TreasureConfig.getInstance().getEnchantmentDropRate(lootTier, Rarity.RARE);
            double epicEnchantment = TreasureConfig.getInstance().getEnchantmentDropRate(lootTier, Rarity.EPIC);
            double legendaryEnchantment = TreasureConfig.getInstance().getEnchantmentDropRate(lootTier, Rarity.LEGENDARY);

            magicChance = calculateAbilityDisplayValues(commonEnchantment + uncommonEnchantment + rareEnchantment + epicEnchantment + legendaryEnchantment)[0];
        }

        // SHAKE
        if (canShake) {
            String[] shakeStrings = calculateAbilityDisplayValues(UserManager.getPlayer(player).getFishingManager().getShakeProbability());
            shakeChance = shakeStrings[0];
            shakeChanceLucky = shakeStrings[1];
        }

        // FISHERMAN'S DIET
        if (canFishermansDiet) {
            fishermansDietRank = calculateRank(Fishing.fishermansDietMaxLevel, Fishing.fishermansDietRankLevel1);
        }

        // MASTER ANGLER
        if (canMasterAngler) {
            double rawBiteChance = 1.0 / (isStorming ? 300 : 500);
            Location location = (mcMMOPlayer.getHookLocation() != null) ? mcMMOPlayer.getHookLocation() : player.getLocation();

            Biome biome = location.getBlock().getBiome();

            if (biome == Biome.RIVER || biome == Biome.OCEAN) {
                rawBiteChance = rawBiteChance * AdvancedConfig.getInstance().getMasterAnglerBiomeModifier();
            }

            if (player.isInsideVehicle() && player.getVehicle().getType() == EntityType.BOAT) {
                rawBiteChance = rawBiteChance * AdvancedConfig.getInstance().getMasterAnglerBoatModifier();
            }

            biteChance = calculateAbilityDisplayValues(rawBiteChance * 100.0)[0];
        }
    }

    @Override
    protected void permissionsCheck() {
        canTreasureHunt = Permissions.fishingTreasureHunter(player);
        canMagicHunt = Permissions.magicHunter(player);
        canShake = Permissions.shake(player);
        canFishermansDiet = Permissions.fishermansDiet(player);
        canMasterAngler = Permissions.masterAngler(player);
        canIceFish = Permissions.iceFishing(player);
    }

    @Override
    protected boolean effectsHeaderPermissions() {
        return canTreasureHunt || canMagicHunt || canShake || canMasterAngler || canFishermansDiet || canIceFish;
    }

    @Override
    protected void effectsDisplay() {
        luckyEffectsDisplay();

        if (canTreasureHunt) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Fishing.Effect.0"), LocaleLoader.getString("Fishing.Effect.1")));
        }

        if (canMagicHunt) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Fishing.Effect.2"), LocaleLoader.getString("Fishing.Effect.3")));
        }

        if (canIceFish) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Fishing.Effect.10"), LocaleLoader.getString("Fishing.Effect.11")));
        }

        if (canMasterAngler) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Fishing.Effect.8"), LocaleLoader.getString("Fishing.Effect.9")));
        }

        if (canShake) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Fishing.Effect.4"), LocaleLoader.getString("Fishing.Effect.5")));
        }

        if (canFishermansDiet) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Fishing.Effect.6"), LocaleLoader.getString("Fishing.Effect.7")));
        }
    }

    @Override
    protected boolean statsHeaderPermissions() {
        return canTreasureHunt || canMagicHunt || canShake || canMasterAngler || canFishermansDiet || canIceFish;
    }

    @Override
    protected void statsDisplay() {
        if (canTreasureHunt) {
            player.sendMessage(LocaleLoader.getString("Fishing.Ability.Rank", lootTier, Tier.EIGHT.toNumerical()));
            player.sendMessage(LocaleLoader.getString("Fishing.Ability.TH.DropRate", trapTreasure, commonTreasure, uncommonTreasure, rareTreasure, epicTreasure, legendaryTreasure, recordTreasure));
        }

        if (canMagicHunt) {
            player.sendMessage(LocaleLoader.getString("Fishing.Ability.TH.MagicRate", magicChance));
        }

        if (canIceFish) {
            int unlockLevel = AdvancedConfig.getInstance().getIceFishingUnlockLevel();

            if (skillValue < unlockLevel) {
                player.sendMessage(LocaleLoader.getString("Ability.Generic.Template.Lock", LocaleLoader.getString("Fishing.Ability.Locked.1", unlockLevel)));
            }
            else {
                player.sendMessage(LocaleLoader.getString("Fishing.Ability.IceFishing"));
            }
        }

        if (canMasterAngler) {
            int unlockLevel = AdvancedConfig.getInstance().getMasterAnglerUnlockLevel();

            if (skillValue < unlockLevel) {
                player.sendMessage(LocaleLoader.getString("Ability.Generic.Template.Lock", LocaleLoader.getString("Fishing.Ability.Locked.2", unlockLevel)));
            }
            else {
                player.sendMessage(LocaleLoader.getString("Fishing.Ability.Chance", biteChance));
            }
        }

        if (canShake) {
            int unlockLevel = AdvancedConfig.getInstance().getFishingTierLevel(Tier.ONE);

            if (skillValue < unlockLevel) {
                player.sendMessage(LocaleLoader.getString("Ability.Generic.Template.Lock", LocaleLoader.getString("Fishing.Ability.Locked.0", unlockLevel)));
            }
            else {
                player.sendMessage(LocaleLoader.getString("Fishing.Ability.Shake", shakeChance) + (isLucky ? LocaleLoader.getString("Perks.Lucky.Bonus", shakeChanceLucky) : ""));
            }
        }

        if (canFishermansDiet) {
            player.sendMessage(LocaleLoader.getString("Fishing.Ability.FD", fishermansDietRank));
        }
    }
}
