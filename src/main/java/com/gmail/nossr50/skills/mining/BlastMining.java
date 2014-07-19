package com.gmail.nossr50.skills.mining;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageModifier;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.util.player.UserManager;

public class BlastMining {
    // The order of the values is extremely important, a few methods depend on it to work properly
    public enum Tier {
        EIGHT(8),
        SEVEN(7),
        SIX(6),
        FIVE(5),
        FOUR(4),
        THREE(3),
        TWO(2),
        ONE(1);

        int numerical;

        private Tier(int numerical) {
            this.numerical = numerical;
        }

        public int toNumerical() {
            return numerical;
        }

        protected int getLevel() {
            return AdvancedConfig.getInstance().getBlastMiningRankLevel(this);
        }

        protected double getBlastRadiusModifier() {
            return AdvancedConfig.getInstance().getBlastRadiusModifier(this);
        }

        protected double getOreBonus() {
            return AdvancedConfig.getInstance().getOreBonus(this);
        }

        protected double getDebrisReduction() {
            return AdvancedConfig.getInstance().getDebrisReduction(this);
        }

        protected double getBlastDamageDecrease() {
            return AdvancedConfig.getInstance().getBlastDamageDecrease(this);
        }

        protected int getDropMultiplier() {
            return AdvancedConfig.getInstance().getDropMultiplier(this);
        }
    }

    public static Material detonator = Config.getInstance().getDetonatorItem();

    public final static int MAXIMUM_REMOTE_DETONATION_DISTANCE = 100;

    public static int getDemolitionExpertUnlockLevel() {
        List<Tier> tierList = Arrays.asList(Tier.values());
        for (Tier tier : tierList) {
            if (tier.getBlastDamageDecrease() > 0) {
                continue;
            }

            return tier == Tier.EIGHT ? tier.getLevel() : tierList.get(tierList.indexOf(tier) - 1).getLevel();
        }

        return 0;
    }

    public static int getBiggerBombsUnlockLevel() {
        List<Tier> tierList = Arrays.asList(Tier.values());
        for (Tier tier : tierList) {
            if (tier.getBlastRadiusModifier() > 1.0) {
                continue;
            }

            return tier == Tier.EIGHT ? tier.getLevel() : tierList.get(tierList.indexOf(tier) - 1).getLevel();
        }

        return 0;
    }

    public static boolean processBlastMiningExplosion(EntityDamageByEntityEvent event, TNTPrimed tnt, Player defender) {
        if (!tnt.hasMetadata(mcMMO.tntMetadataKey) || !UserManager.hasPlayerDataKey(defender)) {
            return false;
        }

        // We can make this assumption because we (should) be the only ones using this exact metadata
        Player player = mcMMO.p.getServer().getPlayerExact(tnt.getMetadata(mcMMO.tntMetadataKey).get(0).asString());

        if (!player.equals(defender)) {
            return false;
        }

        MiningManager miningManager =  UserManager.getPlayer(defender).getMiningManager();

        if (!miningManager.canUseDemolitionsExpertise()) {
            return false;
        }

        event.setDamage(DamageModifier.BASE, miningManager.processDemolitionsExpertise(event.getDamage()));

        if (event.getFinalDamage() == 0) {
            event.setCancelled(true);
            return false;
        }

        return true;
    }
}
