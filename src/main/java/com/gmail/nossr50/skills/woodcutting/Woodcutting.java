package com.gmail.nossr50.skills.woodcutting;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.TreeSpecies;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.getspout.spoutapi.sound.SoundEffect;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.mods.CustomBlock;
import com.gmail.nossr50.events.fake.FakePlayerAnimationEvent;
import com.gmail.nossr50.skills.SkillType;
import com.gmail.nossr50.skills.Skills;
import com.gmail.nossr50.spout.SpoutSounds;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.ModChecks;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.Users;

public abstract class Woodcutting {
    public static final int DOUBLE_DROP_MAX_LEVEL = AdvancedConfig.getInstance().getMiningDoubleDropMaxLevel();
    public static final double DOUBLE_DROP_CHANCE = AdvancedConfig.getInstance().getMiningDoubleDropChance();
    public static final int LEAF_BLOWER_UNLOCK_LEVEL = AdvancedConfig.getInstance().getLeafBlowUnlockLevel();
    public static final boolean DOUBLE_DROP_DISABLED = Config.getInstance().woodcuttingDoubleDropsDisabled();

    /**
     * Begins the Tree Feller ability
     *
     * @param event Event to process
     */
    public static void beginTreeFeller(BlockBreakEvent event) {
        TreeFeller.process(event);
    }

    /**
     * Begins the Leaf Blower ability
     *
     * @param player Player using the ability
     * @param block Block being broken
     */
    public static void beginLeafBlower(Player player, Block block) {
        mcMMO.p.getServer().getPluginManager().callEvent(new FakePlayerAnimationEvent(player));

        if (mcMMO.spoutEnabled) {
            SpoutSounds.playSoundForPlayer(SoundEffect.POP, player, block.getLocation());
        }
    }

    /**
     * Begins Woodcutting
     *
     * @param player Player breaking the block
     * @param block Block being broken
     */
    public static void beginWoodcutting(Player player, Block block) {
        int xp = 0;

        if (Config.getInstance().getBlockModsEnabled() && ModChecks.isCustomLogBlock(block)) {
            xp = ModChecks.getCustomBlock(block).getXpGain();
        }
        else {
            try {
                xp = getExperienceFromLog(block);
            }
            catch (IllegalArgumentException exception) {
                return;
            }
        }

        checkDoubleDrop(player, block);
        Skills.xpProcessing(player,  Users.getProfile(player), SkillType.WOODCUTTING, xp);
    }

    /**
     * Retrieves the experience reward from a log
     *
     * @param log Log being broken
     * @return Amount of experience
     * @throws IllegalArgumentException if 'log' is invalid
     */
    protected static int getExperienceFromLog(Block log) {
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
            return Config.getInstance().getWoodcuttingXPJungle();
        default:
            throw new IllegalArgumentException();
        }
    }

    /**
     * Checks for double drops
     *
     * @param player Player breaking the block
     * @param block Block being broken
     */
    protected static void checkDoubleDrop(Player player, Block block) {
        if (!Permissions.woodcuttingDoubleDrops(player)) {
            return;
        }

        int chance = (int) ((DOUBLE_DROP_CHANCE / DOUBLE_DROP_MAX_LEVEL) * Users.getProfile(player).getSkillLevel(SkillType.WOODCUTTING));
        int activationChance = Misc.calculateActivationChance(Permissions.luckyWoodcutting(player));

        if (chance > DOUBLE_DROP_CHANCE) {
            chance = (int) DOUBLE_DROP_CHANCE;
        }

        if (chance <= Misc.getRandom().nextInt(activationChance)) {
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
                Misc.randomDropItems(location, item, 50, maximumDropAmount - minimumDropAmount);
            }
        }
        else {
            byte blockData = block.getData();
            Location location = block.getLocation();
            ItemStack item = new ItemStack(Material.LOG, 1, blockData);

            switch (TreeSpecies.getByData(extractLogItemData(blockData))) {
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
