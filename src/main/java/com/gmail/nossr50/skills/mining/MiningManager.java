package com.gmail.nossr50.skills.mining;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.Users;

public class MiningManager {
    private Player player;
    private PlayerProfile profile;
    private int skillLevel;

    public MiningManager (Player player) {
        this.player = player;
        this.profile = Users.getProfile(player);

        this.skillLevel = profile.getSkillLevel(SkillType.MINING);
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

        eventHandler.processXP();

        if (!Permissions.miningDoubleDrops(player)) {
            return;
        }

        int randomChance = 100;
        if (Permissions.luckyMining(player)) {
            randomChance = (int) (randomChance * 0.75);
        }

        float chance = ((float) Mining.DOUBLE_DROPS_MAX_CHANCE / Mining.DOUBLE_DROPS_MAX_BONUS_LEVEL) * eventHandler.skillModifier;

        if (chance > Mining.getRandom().nextInt(randomChance)) {
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

        MiningManager manager = new MiningManager(player);
        SuperBreakerEventHandler eventHandler = new SuperBreakerEventHandler(manager, block);

        if (eventHandler.tierCheck()) {
            return;
        }

        eventHandler.callFakeArmswing();
        eventHandler.processDurabilityLoss();
        eventHandler.processDropsAndXP();
        eventHandler.playSpoutSound();
    }

    protected int getSkillLevel() {
        return skillLevel;
    }

    protected Player getPlayer() {
        return player;
    }

    protected PlayerProfile getProfile() {
        return profile;
    }
}
