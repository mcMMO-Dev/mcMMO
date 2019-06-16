package com.gmail.nossr50.skills.mining;

import com.gmail.nossr50.core.MetadataConstants;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.player.UserManager;
import com.gmail.nossr50.util.skills.RankUtils;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageModifier;

public class BlastMining {

    public final static int MAXIMUM_REMOTE_DETONATION_DISTANCE = 100;

    public static double getBlastRadiusModifier(int rank) {
        return mcMMO.getConfigManager().getConfigMining().getBlastMining().getRadius(rank);
    }


    public static double getBlastDamageDecrease(int rank) {
        return mcMMO.getConfigManager().getConfigMining().getBlastMining().getDamageDecrease(rank);
    }


    public static int getDemolitionExpertUnlockLevel() {

        for (int i = 0; i < SubSkillType.MINING_BLAST_MINING.getNumRanks() - 1; i++) {
            if (getBlastDamageDecrease(i + 1) > 0)
                return RankUtils.getRankUnlockLevel(SubSkillType.MINING_BLAST_MINING, i + 1);
        }

        return 0;
    }

    public static int getBiggerBombsUnlockLevel() {

        for (int i = 0; i < SubSkillType.MINING_BLAST_MINING.getNumRanks() - 1; i++) {
            if (getBlastRadiusModifier(i + 1) > 0)
                return RankUtils.getRankUnlockLevel(SubSkillType.MINING_BLAST_MINING, i + 1);
        }

        return 0;
    }

    public static boolean processBlastMiningExplosion(EntityDamageByEntityEvent event, TNTPrimed tnt, Player defender) {
        if (!tnt.hasMetadata(MetadataConstants.TNT_TRACKING_METAKEY) || !UserManager.hasPlayerDataKey(defender)) {
            return false;
        }

        // We can make this assumption because we (should) be the only ones using this exact metadata
        Player player = mcMMO.p.getServer().getPlayerExact(tnt.getMetadata(MetadataConstants.TNT_TRACKING_METAKEY).get(0).asString());

        if (!player.equals(defender)) {
            return false;
        }

        if (UserManager.getPlayer(defender) == null) {
            return false;
        }

        MiningManager miningManager = UserManager.getPlayer(defender).getMiningManager();

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
