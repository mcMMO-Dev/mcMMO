package com.gmail.nossr50.skills.mining;

import org.bukkit.Material;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import com.gmail.nossr50.datatypes.McMMOPlayer;
import com.gmail.nossr50.skills.SkillManager;
import com.gmail.nossr50.skills.utilities.SkillTools;
import com.gmail.nossr50.skills.utilities.SkillType;
import com.gmail.nossr50.util.Misc;

public class MiningManager extends SkillManager{
    public MiningManager (McMMOPlayer mcMMOPlayer) {
        super(mcMMOPlayer, SkillType.MINING);
    }

    /**
     * Detonate TNT for Blast Mining
     *
     * @param event The PlayerInteractEvent
     */
    public void detonate(PlayerInteractEvent event) {
        if (getSkillLevel() < BlastMining.rank1) {
            return;
        }

        RemoteDetonationEventHandler eventHandler = new RemoteDetonationEventHandler(this, event);

        eventHandler.targetTNT();

        if (eventHandler.getBlock().getType() != Material.TNT) {
            return;
        }

        if (!SkillTools.blockBreakSimulate(eventHandler.getBlock(), mcMMOPlayer.getPlayer(), true)) {
            return;
        }

        if (!eventHandler.cooldownOver()) {
            return;
        }

        eventHandler.sendMessages();
        eventHandler.handleDetonation();
        eventHandler.setProfileData();
    }

    /**
     * Handler for explosion drops and XP gain.
     *
     * @param event Event whose explosion is being processed
     */
    public void blastMiningDropProcessing(EntityExplodeEvent event) {
        if (Misc.isNPCEntity(mcMMOPlayer.getPlayer())) {
            return;
        }

        if (getSkillLevel() < BlastMining.rank1) {
            return;
        }

        BlastMiningDropEventHandler eventHandler = new BlastMiningDropEventHandler(this, event);

        eventHandler.sortExplosionBlocks();
        eventHandler.modifyEventYield();

        eventHandler.calcuateDropModifiers();
        eventHandler.processDroppedBlocks();

        eventHandler.processXPGain();
    }

    /**
     * Increases the blast radius of the explosion.
     *
     * @param event Event whose explosion radius is being changed
     */
    public void biggerBombs(ExplosionPrimeEvent event) {
        if (Misc.isNPCEntity(mcMMOPlayer.getPlayer())) {
            return;
        }

        BiggerBombsEventHandler eventHandler = new BiggerBombsEventHandler(this, event);

        eventHandler.calculateRadiusIncrease();
        eventHandler.modifyBlastRadius();
    }
}
