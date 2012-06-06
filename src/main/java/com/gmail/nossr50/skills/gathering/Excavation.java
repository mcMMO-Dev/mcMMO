package com.gmail.nossr50.skills.gathering;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.getspout.spoutapi.sound.SoundEffect;

import com.gmail.nossr50.McMMO;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.config.TreasuresConfig;
import com.gmail.nossr50.config.mods.CustomBlocksConfig;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.datatypes.treasure.ExcavationTreasure;
import com.gmail.nossr50.events.fake.FakePlayerAnimationEvent;
import com.gmail.nossr50.spout.SpoutSounds;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.ModChecks;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.Skills;
import com.gmail.nossr50.util.Users;

public class Excavation {

    private static Random random = new Random();

    /**
     * Check to see if treasures were found.
     *
     * @param block The block to check
     * @param player The player who broke the block
     */
    public static void excavationProcCheck(Block block, Player player) {
        Material type = block.getType();
        Location loc = block.getLocation();

        PlayerProfile PP = Users.getProfile(player);
        int skillLevel = PP.getSkillLevel(SkillType.EXCAVATION);
        ArrayList<ItemStack> is = new ArrayList<ItemStack>();

        List<ExcavationTreasure> treasures = new ArrayList<ExcavationTreasure>();

        int xp;

        if (Config.getInstance().getBlockModsEnabled() && CustomBlocksConfig.getInstance().customExcavationBlocks.contains(new ItemStack(block.getTypeId(), 1, (short) 0, block.getData()))) {
            xp = ModChecks.getCustomBlock(block).getXpGain();
        }
        else {
            xp = Config.getInstance().getExcavationBaseXP();
        }

        if (Permissions.getInstance().excavationTreasures(player)) {
            switch (type) {
            case DIRT:
                treasures = TreasuresConfig.getInstance().excavationFromDirt;
                break;

            case GRASS:
                treasures = TreasuresConfig.getInstance().excavationFromGrass;
                break;

            case SAND:
                treasures = TreasuresConfig.getInstance().excavationFromSand;
                break;

            case GRAVEL:
                treasures = TreasuresConfig.getInstance().excavationFromGravel;
                break;

            case CLAY:
                treasures = TreasuresConfig.getInstance().excavationFromClay;
                break;

            case MYCEL:
                treasures = TreasuresConfig.getInstance().excavationFromMycel;
                break;

            case SOUL_SAND:
                treasures = TreasuresConfig.getInstance().excavationFromSoulSand;
                break;

            default:
                break;
            }

            for (ExcavationTreasure treasure : treasures) {
                if (skillLevel >= treasure.getDropLevel()) {
                    if (random.nextDouble() * 100 <= treasure.getDropChance()) {
                        xp += treasure.getXp();
                        is.add(treasure.getDrop());
                    }
                }
            }

            //Drop items
            for (ItemStack x : is) {
                if (x != null) {
                    Misc.dropItem(loc, x);
                }
            }
        }

        Skills.xpProcessing(player, PP, SkillType.EXCAVATION, xp);
    }

    /**
     * Handle triple drops from Giga Drill Breaker.
     *
     * @param player The player using the ability
     * @param block The block to check
     */
    public static void gigaDrillBreaker(Player player, Block block) {
        Skills.abilityDurabilityLoss(player.getItemInHand(), Config.getInstance().getAbilityToolDamage());

        if (!McMMO.placeStore.isTrue(block)) {
            FakePlayerAnimationEvent armswing = new FakePlayerAnimationEvent(player);
            McMMO.p.getServer().getPluginManager().callEvent(armswing);

            Excavation.excavationProcCheck(block, player);
            Excavation.excavationProcCheck(block, player);
        }

        if (McMMO.spoutEnabled) {
            SpoutSounds.playSoundForPlayer(SoundEffect.POP, player, block.getLocation());
        }
    }
}