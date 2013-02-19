package com.gmail.nossr50.skills.woodcutting;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.TreeSpecies;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.McMMOPlayer;
import com.gmail.nossr50.events.fake.FakePlayerAnimationEvent;
import com.gmail.nossr50.mods.ModChecks;
import com.gmail.nossr50.mods.datatypes.CustomBlock;
import com.gmail.nossr50.skills.utilities.PerksUtils;
import com.gmail.nossr50.skills.utilities.SkillType;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.Users;

public final class Woodcutting {
    static final AdvancedConfig ADVANCED_CONFIG = AdvancedConfig.getInstance();
    static final Config CONFIG = Config.getInstance();

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
    public static void beginTreeFeller(McMMOPlayer mcMMOPlayer, Block block) {
        TreeFeller.process(mcMMOPlayer, block);
    }

    /**
     * Begins the Leaf Blower ability
     *
     * @param player Player using the ability
     * @param block Block being broken
     */
    public static void beginLeafBlower(Player player, Block block) {
        mcMMO.p.getServer().getPluginManager().callEvent(new FakePlayerAnimationEvent(player));

        player.playSound(block.getLocation(), Sound.ITEM_PICKUP, Misc.POP_VOLUME, Misc.POP_PITCH);
    }

    /**
     * Begins Woodcutting
     *
     * @param mcMMOPlayer Player breaking the block
     * @param block Block being broken
     */
    public static void beginWoodcutting(McMMOPlayer mcMMOPlayer, Block block) {
        int xp = 0;

        if (Config.getInstance().getBlockModsEnabled() && ModChecks.isCustomLogBlock(block)) {
            xp = ModChecks.getCustomBlock(block).getXpGain();
        }
        else {
            try {
                xp = getExperienceFromLog(block, ExperienceGainMethod.DEFAULT);
            }
            catch (IllegalArgumentException exception) {
                return;
            }
        }

        Player player = mcMMOPlayer.getPlayer();

        if (Permissions.doubleDrops(player, SkillType.WOODCUTTING)) {
            checkForDoubleDrop(mcMMOPlayer, block);
        }

        mcMMOPlayer.beginXpGain(SkillType.WOODCUTTING, xp);
    }

    /**
     * Retrieves the experience reward from a log
     *
     * @param log Log being broken
     * @param experienceGainMethod How the log is being broken
     * @return Amount of experience
     * @throws IllegalArgumentException if 'log' is invalid
     */
    protected static int getExperienceFromLog(Block log, ExperienceGainMethod experienceGainMethod) {
        // Mushrooms aren't trees so we could never get species data from them
        switch (log.getType()) {
        case HUGE_MUSHROOM_1:
            return Config.getInstance().getWoodcuttingXPHugeBrownMushroom();
        case HUGE_MUSHROOM_2:
            return Config.getInstance().getWoodcuttingXPHugeRedMushroom();
        default:
            break;
        }

        TreeSpecies logType = TreeSpecies.getByData(extractLogItemData(log.getData()));

        // Apparently species can be null in certain cases (custom server mods?)
        // https://github.com/mcMMO-Dev/mcMMO/issues/229
        if (logType == null) {
            throw new IllegalArgumentException();
        }

        switch (logType) {
        case GENERIC:
            return Config.getInstance().getWoodcuttingXPOak();
        case REDWOOD:
            return Config.getInstance().getWoodcuttingXPSpruce();
        case BIRCH:
            return Config.getInstance().getWoodcuttingXPBirch();
        case JUNGLE:
            int xp = Config.getInstance().getWoodcuttingXPJungle();

            switch (experienceGainMethod) {
            case TREE_FELLER:
                return (int) (xp * 0.5);
            default:
                return xp;
            }
        default:
            throw new IllegalArgumentException();
        }
    }

    /**
     * Checks for double drops
     *
     * @param mcMMOPlayer Player breaking the block
     * @param block Block being broken
     */
    protected static void checkForDoubleDrop(McMMOPlayer mcMMOPlayer, Block block) {
        Player player = mcMMOPlayer.getPlayer();
        double configDoubleDropChance = ADVANCED_CONFIG.getWoodcuttingDoubleDropChance();
        int configDoubleDropMaxLevel = ADVANCED_CONFIG.getWoodcuttingDoubleDropMaxLevel();
        int probability = (int) ((configDoubleDropChance / configDoubleDropMaxLevel) * Users.getPlayer(player).getProfile().getSkillLevel(SkillType.WOODCUTTING));
        int activationChance = PerksUtils.handleLuckyPerks(player, SkillType.WOODCUTTING);

        if (probability > configDoubleDropChance) {
            probability = (int) configDoubleDropChance;
        }

        if (probability <= Misc.getRandom().nextInt(activationChance)) {
            return;
        }

        if (Config.getInstance().getBlockModsEnabled() && ModChecks.isCustomLogBlock(block)) {
            CustomBlock customBlock = ModChecks.getCustomBlock(block);
            int minimumDropAmount = customBlock.getMinimumDropAmount();
            int maximumDropAmount = customBlock.getMaximumDropAmount();
            Location location = block.getLocation();
            ItemStack item = customBlock.getItemDrop();

            Misc.dropItems(location, item, minimumDropAmount);

            if (minimumDropAmount != maximumDropAmount) {
                Misc.randomDropItems(location, item, maximumDropAmount - minimumDropAmount);
            }
        }
        else {
            byte itemData = extractLogItemData(block.getData());
            Location location = block.getLocation();
            ItemStack item = new ItemStack(Material.LOG, 1, itemData);

            switch (TreeSpecies.getByData(itemData)) {
            case GENERIC:
                if (Config.getInstance().getOakDoubleDropsEnabled()) {
                    Misc.dropItem(location, item);
                }
                break;
            case REDWOOD:
                if (Config.getInstance().getSpruceDoubleDropsEnabled()) {
                    Misc.dropItem(location, item);
                }
                break;
            case BIRCH:
                if (Config.getInstance().getBirchDoubleDropsEnabled()) {
                    Misc.dropItem(location, item);
                }
                break;
            case JUNGLE:
                if (Config.getInstance().getJungleDoubleDropsEnabled()) {
                    Misc.dropItem(location, item);
                }
                break;
            default:
                break;
            }
        }
    }

    /**
     * Extracts the log type from the block data (i.e. removes rotation)
     *
     * @param data Original block data
     * @return Extracted log type
     */
    protected static byte extractLogItemData(byte data) {
        return (byte) (data & 0x3);
    }
}
