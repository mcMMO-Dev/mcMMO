package com.gmail.nossr50.skills.woodcutting;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Tree;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.events.fake.FakePlayerAnimationEvent;
import com.gmail.nossr50.mods.ModChecks;
import com.gmail.nossr50.mods.datatypes.CustomBlock;
import com.gmail.nossr50.skills.utilities.SkillTools;
import com.gmail.nossr50.skills.utilities.SkillType;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.Users;

public final class Woodcutting {
    public static double doubleDropsMaxChance = AdvancedConfig.getInstance().getWoodcuttingDoubleDropChance();
    public static int doubleDropsMaxLevel = AdvancedConfig.getInstance().getWoodcuttingDoubleDropMaxLevel();

    protected enum ExperienceGainMethod {
        DEFAULT,
        TREE_FELLER,
    };

    private Woodcutting() {}

    /**
     * Begins the Tree Feller ability
     *
     * @param mcMMOPlayer Player using the ability
     * @param block Block being broken
     */
    public static void beginTreeFeller(BlockState blockState, Player player) {
        TreeFeller.process(blockState, player);
    }

    /**
     * Begins the Leaf Blower ability
     *
     * @param player Player using the ability
     * @param block Block being broken
     */
    public static void beginLeafBlower(Player player, BlockState blockState) {
        mcMMO.p.getServer().getPluginManager().callEvent(new FakePlayerAnimationEvent(player));
        player.playSound(blockState.getLocation(), Sound.ITEM_PICKUP, Misc.POP_VOLUME, Misc.POP_PITCH);
    }

    /**
     * Begins Woodcutting
     *
     * @param mcMMOPlayer Player breaking the block
     * @param block Block being broken
     */
    public static void beginWoodcutting(Player player, BlockState blockState) {
        int xp = getExperienceFromLog(blockState, ExperienceGainMethod.DEFAULT);

        if (Permissions.doubleDrops(player, SkillType.WOODCUTTING)) {
            Material blockType = blockState.getType();

            if (blockType != Material.HUGE_MUSHROOM_1 && blockType != Material.HUGE_MUSHROOM_2) {
                checkForDoubleDrop(player, blockState);
            }
        }

        Users.getPlayer(player).beginXpGain(SkillType.WOODCUTTING, xp);
    }

    /**
     * Retrieves the experience reward from a log
     *
     * @param blockState Log being broken
     * @param experienceGainMethod How the log is being broken
     * @return Amount of experience
     */
    protected static int getExperienceFromLog(BlockState blockState, ExperienceGainMethod experienceGainMethod) {
        // Mushrooms aren't trees so we could never get species data from them
        switch (blockState.getType()) {
        case HUGE_MUSHROOM_1:
            return Config.getInstance().getWoodcuttingXPHugeBrownMushroom();

        case HUGE_MUSHROOM_2:
            return Config.getInstance().getWoodcuttingXPHugeRedMushroom();

        default:
            break;
        }

        if (ModChecks.isCustomLogBlock(blockState)) {
            return ModChecks.getCustomBlock(blockState).getXpGain();
        }

        switch (((Tree) blockState.getData()).getSpecies()) {
        case GENERIC:
            return Config.getInstance().getWoodcuttingXPOak();

        case REDWOOD:
            return Config.getInstance().getWoodcuttingXPSpruce();

        case BIRCH:
            return Config.getInstance().getWoodcuttingXPBirch();

        case JUNGLE:
            int xp = Config.getInstance().getWoodcuttingXPJungle();

            if (experienceGainMethod == ExperienceGainMethod.TREE_FELLER) {
                xp *= 0.5;
            }

            return xp;

        default:
            return 0;
        }
    }

    /**
     * Checks for double drops
     *
     * @param mcMMOPlayer Player breaking the block
     * @param blockState Block being broken
     */
    protected static void checkForDoubleDrop(Player player, BlockState blockState) {
        if (!SkillTools.activationSuccessful(player, SkillType.WOODCUTTING, doubleDropsMaxChance, doubleDropsMaxLevel)) {
            return;
        }

        if (ModChecks.isCustomLogBlock(blockState)) {
            CustomBlock customBlock = ModChecks.getCustomBlock(blockState);
            int minimumDropAmount = customBlock.getMinimumDropAmount();
            int maximumDropAmount = customBlock.getMaximumDropAmount();
            Location location = blockState.getLocation();
            ItemStack item = customBlock.getItemDrop();

            Misc.dropItems(location, item, minimumDropAmount);

            if (minimumDropAmount != maximumDropAmount) {
                Misc.randomDropItems(location, item, maximumDropAmount - minimumDropAmount);
            }
        }
        else {
            Location location = blockState.getLocation();
            ItemStack item = blockState.getData().toItemStack();

            switch (((Tree) blockState.getData()).getSpecies()) {
            case GENERIC:
                if (Config.getInstance().getOakDoubleDropsEnabled()) {
                    Misc.dropItem(location, item);
                }
                return;

            case REDWOOD:
                if (Config.getInstance().getSpruceDoubleDropsEnabled()) {
                    Misc.dropItem(location, item);
                }
                return;

            case BIRCH:
                if (Config.getInstance().getBirchDoubleDropsEnabled()) {
                    Misc.dropItem(location, item);
                }
                return;

            case JUNGLE:
                if (Config.getInstance().getJungleDoubleDropsEnabled()) {
                    Misc.dropItem(location, item);
                }
                return;

            default:
                return;
            }
        }
    }
}
