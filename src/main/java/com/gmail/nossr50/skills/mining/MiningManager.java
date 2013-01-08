package com.gmail.nossr50.skills.mining;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.Users;

public class MiningManager {
    private Player player;
    private PlayerProfile profile;
    private int skillLevel;
    private Permissions permissionsInstance;

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

        float chance = (float) (((double) Mining.DOUBLE_DROPS_MAX_CHANCE / Mining.DOUBLE_DROPS_MAX_BONUS_LEVEL) * eventHandler.skillModifier);

        if (chance > Mining.getRandom().nextInt(randomChance)) {
            eventHandler.processDrops();
        }
    }

    protected int getSkillLevel() {
        return skillLevel;
    }

    protected Permissions getPermissionsInstance() {
        return permissionsInstance;
    }

    protected Player getPlayer() {
        return player;
    }

    protected PlayerProfile getProfile() {
        return profile;
    }
}
