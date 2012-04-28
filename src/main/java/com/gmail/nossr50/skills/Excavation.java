package com.gmail.nossr50.skills;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.spout.SpoutSounds;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.Users;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.config.LoadTreasures;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.datatypes.treasure.ExcavationTreasure;
import com.gmail.nossr50.events.fake.FakePlayerAnimationEvent;

import org.getspout.spoutapi.sound.SoundEffect;

public class Excavation {

    private static Random random = new Random();

    /**
     * Check to see if a block can be broken by Giga Drill Breaker.
     *
     * @param material The type of block to check
     * @return
     */
    public static boolean canBeGigaDrillBroken(Material type) {
        switch (type) {
        case CLAY:
        case DIRT:
        case GRASS:
        case GRAVEL:
        case MYCEL:
        case SAND:
        case SOUL_SAND:
            return true;

        default:
            return false;
        }
    }

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

        int xp = Config.getInstance().getExcavationBaseXP();

        if (Permissions.getInstance().excavationTreasures(player)) {
            switch (type) {
            case DIRT:
                treasures = LoadTreasures.getInstance().excavationFromDirt;
                break;

            case GRASS:
                treasures = LoadTreasures.getInstance().excavationFromGrass;
                break;

            case SAND:
                treasures = LoadTreasures.getInstance().excavationFromSand;
                break;

            case GRAVEL:
                treasures = LoadTreasures.getInstance().excavationFromGravel;
                break;

            case CLAY:
                treasures = LoadTreasures.getInstance().excavationFromClay;
                break;

            case MYCEL:
                treasures = LoadTreasures.getInstance().excavationFromMycel;
                break;

            case SOUL_SAND:
                treasures = LoadTreasures.getInstance().excavationFromSoulSand;
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
                    Misc.mcDropItem(loc, x);
                }
            }
        }

        //Handle XP related tasks
        PP.addXP(SkillType.EXCAVATION, xp);
        Skills.XpCheckSkill(SkillType.EXCAVATION, player);
    }

    /**
     * Handle triple drops from Giga Drill Breaker.
     *
     * @param player The player using the ability
     * @param block The block to check
     */
    public static void gigaDrillBreaker(Player player, Block block) {
        Skills.abilityDurabilityLoss(player.getItemInHand(), Config.getInstance().getAbilityToolDamage());

        if (!block.hasMetadata("mcmmoPlacedBlock")) {
            FakePlayerAnimationEvent armswing = new FakePlayerAnimationEvent(player);
            mcMMO.p.getServer().getPluginManager().callEvent(armswing);

            Excavation.excavationProcCheck(block, player);
            Excavation.excavationProcCheck(block, player);
        }

        if (Config.getInstance().spoutEnabled) {
            SpoutSounds.playSoundForPlayer(SoundEffect.POP, player, block.getLocation());
        }
    }
}