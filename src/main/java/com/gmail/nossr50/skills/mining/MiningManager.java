package com.gmail.nossr50.skills.mining;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.skills.SkillManager;
import com.gmail.nossr50.skills.SkillType;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;

public class MiningManager extends SkillManager{
    public MiningManager (Player player) {
        super(player, SkillType.MINING);
    }

    /**
     * Detonate TNT for Blast Mining
     *
     * @param event The PlayerInteractEvent
     * @param player Player detonating the TNT
     * @param plugin mcMMO plugin instance
     */
    public void detonate(PlayerInteractEvent event) {
        if (skillLevel < BlastMining.rank1) {
            return;
        }

        RemoteDetonationEventHandler eventHandler = new RemoteDetonationEventHandler(this, event);

        eventHandler.targetTNT();

        if (eventHandler.block.getType() != Material.TNT) {
            return;
        }

        if (!Misc.blockBreakSimulate(eventHandler.block, player, true)) {
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
        if (Misc.isNPCPlayer(player)) {
            return;
        }

        if (skillLevel < BlastMining.rank1) {
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
     * Decreases damage dealt by the explosion from TNT activated by Blast Mining.
     *
     * @param event Event whose explosion damage is being reduced
     */
    public void demolitionsExpertise(EntityDamageEvent event) {
        if (Misc.isNPCPlayer(player)) {
            return;
        }

        DemoltionsExpertiseEventHandler eventHandler = new DemoltionsExpertiseEventHandler(this, event);

        eventHandler.calculateDamageModifier();
        eventHandler.modifyEventDamage();
    }

    /**
     * Increases the blast radius of the explosion.
     *
     * @param player Player triggering the explosion
     * @param event Event whose explosion radius is being changed
     */
    public void biggerBombs(ExplosionPrimeEvent event) {
        if (Misc.isNPCPlayer(player)) {
            return;
        }

        BiggerBombsEventHandler eventHandler = new BiggerBombsEventHandler(this, event);

        eventHandler.calculateRadiusIncrease();
        eventHandler.modifyBlastRadius();
    }

    /**
     * Process Mining block drops.
     *
     * @param block The block being broken
     */
    public void miningBlockCheck(Block block) {
        if (mcMMO.placeStore.isTrue(block)) {
            return;
        }

        MiningBlockEventHandler eventHandler = new MiningBlockEventHandler(this, block);

        eventHandler.processXPGain();

        if (!Permissions.miningDoubleDrops(player)) {
            return;
        }

        float chance = ((float) Mining.doubleDropsMaxChance / Mining.doubleDropsMaxLevel) * eventHandler.skillModifier;

        if (chance > Misc.getRandom().nextInt(activationChance)) {
            eventHandler.processDrops();
        }
    }

    /**
     * Handle the Super Breaker ability.
     *
     * @param player The player using the ability
     * @param block The block being affected
     */
    public void superBreakerBlockCheck(Block block) {
        if (mcMMO.placeStore.isTrue(block) || !Misc.blockBreakSimulate(block, player, true)) {
            return;
        }

        SuperBreakerEventHandler eventHandler = new SuperBreakerEventHandler(this, block);

        if (!eventHandler.tierCheck()) {
            return;
        }

        eventHandler.callFakeArmswing();
        eventHandler.processDurabilityLoss();
        eventHandler.processDropsAndXP();
        eventHandler.playSpoutSound();
    }
}
