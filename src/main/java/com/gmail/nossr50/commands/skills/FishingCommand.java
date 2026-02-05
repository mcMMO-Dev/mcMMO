package com.gmail.nossr50.commands.skills;

import com.gmail.nossr50.config.treasure.FishingTreasureConfig;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.datatypes.treasure.Rarity;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.skills.fishing.FishingManager;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.random.Probability;
import com.gmail.nossr50.util.random.ProbabilityUtil;
import com.gmail.nossr50.util.skills.RankUtils;
import com.gmail.nossr50.util.text.StringUtils;
import com.gmail.nossr50.util.text.TextComponentFactory;
import java.util.ArrayList;
import java.util.List;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public class FishingCommand extends SkillCommand {
    private int lootTier;
    private String shakeChance;
    private String shakeChanceLucky;
    private int fishermansDietRank;

    private String commonTreasure;
    private String uncommonTreasure;
    private String rareTreasure;
    private String epicTreasure;
    private String legendaryTreasure;
    private String mythicTreasure;

    private String magicChance;

    private boolean canTreasureHunt;
    private boolean canMagicHunt;
    private boolean canShake;
    private boolean canFishermansDiet;
    private boolean canMasterAngler;
    private boolean canIceFish;

    private String maMinWaitTime, maMaxWaitTime;

    public FishingCommand() {
        super(PrimarySkillType.FISHING);
    }

    @Override
    protected void dataCalculations(Player player, float skillValue) {
        FishingManager fishingManager = mmoPlayer.getFishingManager();

        // TREASURE HUNTER
        if (canTreasureHunt) {
            lootTier = fishingManager.getLootTier();

            // Item drop rates
            commonTreasure = percent.format(
                    FishingTreasureConfig.getInstance().getItemDropRate(lootTier, Rarity.COMMON)
                            / 100.0);
            uncommonTreasure = percent.format(
                    FishingTreasureConfig.getInstance().getItemDropRate(lootTier, Rarity.UNCOMMON)
                            / 100.0);
            rareTreasure = percent.format(
                    FishingTreasureConfig.getInstance().getItemDropRate(lootTier, Rarity.RARE)
                            / 100.0);
            epicTreasure = percent.format(
                    FishingTreasureConfig.getInstance().getItemDropRate(lootTier, Rarity.EPIC)
                            / 100.0);
            legendaryTreasure = percent.format(
                    FishingTreasureConfig.getInstance().getItemDropRate(lootTier, Rarity.LEGENDARY)
                            / 100.0);
            mythicTreasure = percent.format(
                    FishingTreasureConfig.getInstance().getItemDropRate(lootTier, Rarity.MYTHIC)
                            / 100.0);

            // Magic hunter drop rates
            double totalEnchantChance = 0;

            for (Rarity rarity : Rarity.values()) {
                if (rarity != Rarity.MYTHIC) {
                    totalEnchantChance += FishingTreasureConfig.getInstance()
                            .getEnchantmentDropRate(lootTier, rarity);
                }
            }

            if (totalEnchantChance >= 1) {
                magicChance = percent.format(totalEnchantChance / 100.0);
            } else {
                magicChance = percent.format(0);
            }
        }

        // FISHING_SHAKE
        if (canShake) {
            Probability shakeProbability = Probability.ofPercent(fishingManager.getShakeChance());
            String[] shakeStrings = ProbabilityUtil.getRNGDisplayValues(shakeProbability);
            shakeChance = shakeStrings[0];
            shakeChanceLucky = shakeStrings[1];
        }

        // FISHERMAN'S DIET
        if (canFishermansDiet) {
            fishermansDietRank = RankUtils.getRank(player, SubSkillType.FISHING_FISHERMANS_DIET);
        }

        // MASTER ANGLER
        if (canMasterAngler) {
            maMinWaitTime = StringUtils.ticksToSeconds(
                    fishingManager.getMasterAnglerTickMinWaitReduction(
                            RankUtils.getRank(player, SubSkillType.FISHING_MASTER_ANGLER), false));
            maMaxWaitTime = StringUtils.ticksToSeconds(
                    fishingManager.getMasterAnglerTickMaxWaitReduction(
                            RankUtils.getRank(player, SubSkillType.FISHING_MASTER_ANGLER), false,
                            0));
        }
    }

    @Override
    protected void permissionsCheck(Player player) {
        canTreasureHunt = Permissions.canUseSubSkill(player, SubSkillType.FISHING_TREASURE_HUNTER);
        canMagicHunt = Permissions.canUseSubSkill(player, SubSkillType.FISHING_MAGIC_HUNTER)
                && Permissions.canUseSubSkill(player, SubSkillType.FISHING_TREASURE_HUNTER);
        canShake = Permissions.canUseSubSkill(player, SubSkillType.FISHING_SHAKE);
        canFishermansDiet = Permissions.canUseSubSkill(player,
                SubSkillType.FISHING_FISHERMANS_DIET);
        canMasterAngler =
                mcMMO.getCompatibilityManager().getMasterAnglerCompatibilityLayer() != null
                        && Permissions.canUseSubSkill(player, SubSkillType.FISHING_MASTER_ANGLER);
        canIceFish = Permissions.canUseSubSkill(player, SubSkillType.FISHING_ICE_FISHING);
    }

    @Override
    protected List<String> statsDisplay(Player player, float skillValue, boolean hasEndurance,
            boolean isLucky) {
        List<String> messages = new ArrayList<>();

        if (canFishermansDiet) {
            messages.add(getStatMessage(false, true, SubSkillType.FISHING_FISHERMANS_DIET,
                    String.valueOf(fishermansDietRank)));
        }

        if (canIceFish) {
            messages.add(getStatMessage(SubSkillType.FISHING_ICE_FISHING,
                    SubSkillType.FISHING_ICE_FISHING.getLocaleStatDescription()));
        }

        if (canMagicHunt) {
            messages.add(getStatMessage(SubSkillType.FISHING_MAGIC_HUNTER, magicChance));
        }

        if (canMasterAngler) {
            messages.add(getStatMessage(false, true,
                    SubSkillType.FISHING_MASTER_ANGLER,
                    maMinWaitTime));

            messages.add(getStatMessage(true, true,
                    SubSkillType.FISHING_MASTER_ANGLER,
                    maMaxWaitTime));
        }

        if (canShake) {
            messages.add(getStatMessage(SubSkillType.FISHING_SHAKE, shakeChance)
                    + (isLucky ? LocaleLoader.getString("Perks.Lucky.Bonus", shakeChanceLucky)
                    : ""));
        }

        if (canTreasureHunt) {
            messages.add(getStatMessage(false, true, SubSkillType.FISHING_TREASURE_HUNTER,
                    String.valueOf(lootTier), String.valueOf(
                            RankUtils.getHighestRank(SubSkillType.FISHING_TREASURE_HUNTER))));
            messages.add(getStatMessage(true, true, SubSkillType.FISHING_TREASURE_HUNTER,
                    String.valueOf(commonTreasure),
                    String.valueOf(uncommonTreasure),
                    String.valueOf(rareTreasure),
                    String.valueOf(epicTreasure),
                    String.valueOf(legendaryTreasure),
                    String.valueOf(mythicTreasure)));
        }

        return messages;
    }

    @Override
    protected List<Component> getTextComponents(Player player) {
        List<Component> textComponents = new ArrayList<>();

        TextComponentFactory.appendSubSkillTextComponents(player, textComponents,
                PrimarySkillType.FISHING);

        return textComponents;
    }
}
