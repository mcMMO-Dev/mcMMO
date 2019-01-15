package com.gmail.nossr50.commands.skills;

import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.config.treasure.TreasureConfig;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.datatypes.treasure.Rarity;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.skills.fishing.Fishing;
import com.gmail.nossr50.skills.fishing.Fishing.Tier;
import com.gmail.nossr50.skills.fishing.FishingManager;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.TextComponentFactory;
import com.gmail.nossr50.util.player.UserManager;
import com.gmail.nossr50.util.skills.RankUtils;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

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
        super(PrimarySkillType.FISHING);
    }

    @Override
    protected void dataCalculations(Player player, float skillValue, boolean isLucky) {
        FishingManager fishingManager = UserManager.getPlayer(player).getFishingManager();

        // TREASURE HUNTER
        if (canTreasureHunt) {
            lootTier = fishingManager.getLootTier();

            // Item drop rates
            trapTreasure = percent.format(TreasureConfig.getInstance().getItemDropRate(lootTier, Rarity.TRAP) / 100.0);
            commonTreasure = percent.format(TreasureConfig.getInstance().getItemDropRate(lootTier, Rarity.COMMON) / 100.0);
            uncommonTreasure = percent.format(TreasureConfig.getInstance().getItemDropRate(lootTier, Rarity.UNCOMMON) / 100.0);
            rareTreasure = percent.format(TreasureConfig.getInstance().getItemDropRate(lootTier, Rarity.RARE) / 100.0);
            epicTreasure = percent.format(TreasureConfig.getInstance().getItemDropRate(lootTier, Rarity.EPIC) / 100.0);
            legendaryTreasure = percent.format(TreasureConfig.getInstance().getItemDropRate(lootTier, Rarity.LEGENDARY) / 100.0);
            recordTreasure = percent.format(TreasureConfig.getInstance().getItemDropRate(lootTier, Rarity.RECORD) / 100.0);

            // Magic hunter drop rates
            double totalEnchantChance = 0;

            for (Rarity rarity : Rarity.values()) {
                if (rarity != Rarity.TRAP && rarity != Rarity.RECORD) {
                    totalEnchantChance += TreasureConfig.getInstance().getEnchantmentDropRate(lootTier, rarity);
                }
            }

            magicChance = percent.format(totalEnchantChance / 100.0);
        }

        // FISHING_SHAKE
        if (canShake) {
            String[] shakeStrings = calculateAbilityDisplayValues(UserManager.getPlayer(player).getFishingManager().getShakeProbability(), isLucky);
            shakeChance = shakeStrings[0];
            shakeChanceLucky = shakeStrings[1];
        }

        // FISHERMAN'S DIET
        if (canFishermansDiet) {
            fishermansDietRank = calculateRank(skillValue, Fishing.fishermansDietMaxLevel, Fishing.fishermansDietRankLevel1);
        }

        // MASTER ANGLER
        if (canMasterAngler) {
            double rawBiteChance = 1.0 / (player.getWorld().hasStorm() ? 300 : 500);
            Location location = fishingManager.getHookLocation();

            if (location == null) {
                location = player.getLocation();
            }

            if (Fishing.masterAnglerBiomes.contains(location.getBlock().getBiome())) {
                rawBiteChance = rawBiteChance * AdvancedConfig.getInstance().getMasterAnglerBiomeModifier();
            }

            if (player.isInsideVehicle() && player.getVehicle().getType() == EntityType.BOAT) {
                rawBiteChance = rawBiteChance * AdvancedConfig.getInstance().getMasterAnglerBoatModifier();
            }

            biteChance = calculateAbilityDisplayValues(rawBiteChance * 100.0, isLucky)[0];
        }
    }

    @Override
    protected void permissionsCheck(Player player) {
        canTreasureHunt = canUseSubskill(player, SubSkillType.FISHING_TREASURE_HUNTER);
        canMagicHunt = canUseSubskill(player, SubSkillType.FISHING_MAGIC_HUNTER);
        canShake = canUseSubskill(player, SubSkillType.FISHING_SHAKE);
        canFishermansDiet = canUseSubskill(player, SubSkillType.FISHING_FISHERMANS_DIET);
        canMasterAngler = canUseSubskill(player, SubSkillType.FISHING_MASTER_ANGLER);
        canIceFish = canUseSubskill(player, SubSkillType.FISHING_ICE_FISHING);
    }

    @Override
    protected List<String> effectsDisplay() {
        List<String> messages = new ArrayList<String>();

        if (canTreasureHunt) {
            messages.add(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Fishing.Effect.0"), LocaleLoader.getString("Fishing.Effect.1")));
        }

        if (canMagicHunt) {
            messages.add(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Fishing.Effect.2"), LocaleLoader.getString("Fishing.Effect.3")));
        }

        if (canIceFish) {
            messages.add(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Fishing.Effect.10"), LocaleLoader.getString("Fishing.Effect.11")));
        }

        if (canMasterAngler) {
            messages.add(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Fishing.Effect.8"), LocaleLoader.getString("Fishing.Effect.9")));
        }

        if (canShake) {
            messages.add(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Fishing.Effect.4"), LocaleLoader.getString("Fishing.Effect.5")));
        }

        if (canFishermansDiet) {
            messages.add(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Fishing.Effect.6"), LocaleLoader.getString("Fishing.Effect.7")));
        }

        return messages;
    }

    @Override
    protected List<String> statsDisplay(Player player, float skillValue, boolean hasEndurance, boolean isLucky) {
        List<String> messages = new ArrayList<String>();

        if (canTreasureHunt) {
            messages.add(LocaleLoader.getString("Fishing.Ability.Rank", lootTier, Tier.values().length));
            messages.add(LocaleLoader.getString("Fishing.Ability.TH.DropRate", trapTreasure, commonTreasure, uncommonTreasure, rareTreasure, epicTreasure, legendaryTreasure, recordTreasure));
        }

        if (canMagicHunt) {
            messages.add(LocaleLoader.getString("Fishing.Ability.TH.MagicRate", magicChance));
        }

        if (canIceFish) {
            int unlockLevel = AdvancedConfig.getInstance().getIceFishingUnlockLevel();

            if (skillValue < unlockLevel) {
                messages.add(LocaleLoader.getString("Ability.Generic.Template.Lock", LocaleLoader.getString("Fishing.Ability.Locked.1", unlockLevel)));
            }
            else {
                messages.add(LocaleLoader.getString("Fishing.Ability.IceFishing"));
            }
        }

        if (canMasterAngler) {
            int unlockLevel = AdvancedConfig.getInstance().getMasterAnglerUnlockLevel();

            if (skillValue < unlockLevel) {
                messages.add(LocaleLoader.getString("Ability.Generic.Template.Lock", LocaleLoader.getString("Fishing.Ability.Locked.2", unlockLevel)));
            }
            else {
                messages.add(LocaleLoader.getString("Fishing.Ability.Chance", biteChance));
            }
        }

        if (canShake) {
            int unlockLevel = AdvancedConfig.getInstance().getFishingTierLevel(Tier.ONE);

            if (skillValue < unlockLevel) {
                messages.add(LocaleLoader.getString("Ability.Generic.Template.Lock", LocaleLoader.getString("Fishing.Ability.Locked.0", unlockLevel)));
            }
            else {
                messages.add(LocaleLoader.getString("Fishing.Ability.Shake", shakeChance) + (isLucky ? LocaleLoader.getString("Perks.Lucky.Bonus", shakeChanceLucky) : ""));
            }
        }

        if (canFishermansDiet) {
            messages.add(LocaleLoader.getString("Fishing.Ability.FD", fishermansDietRank));
        }

        return messages;
    }

    @Override
    protected List<TextComponent> getTextComponents(Player player) {
        List<TextComponent> textComponents = new ArrayList<>();

        TextComponentFactory.getSubSkillTextComponents(player, textComponents, PrimarySkillType.FISHING);

        return textComponents;
    }
}
