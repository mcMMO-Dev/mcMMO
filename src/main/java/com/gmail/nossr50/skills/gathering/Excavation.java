package com.gmail.nossr50.skills.gathering;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.getspout.spoutapi.sound.SoundEffect;

import com.gmail.nossr50.mcMMO;
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

    /**
     * Check to see if treasures were found.
     *
     * @param block The block to check
     * @param player The player who broke the block
     */
    public static void excavationProcCheck(Block block, Player player) {
        if (player == null)
            return;

        Material type = block.getType();
        Location location = block.getLocation();

        PlayerProfile profile = Users.getProfile(player);
        int skillLevel = profile.getSkillLevel(SkillType.EXCAVATION);
        ArrayList<ItemStack> is = new ArrayList<ItemStack>();

        List<ExcavationTreasure> treasures = new ArrayList<ExcavationTreasure>();

        int xp;

        ItemStack item = (new MaterialData(block.getTypeId(), block.getData())).toItemStack(1);

        if (Config.getInstance().getBlockModsEnabled() && CustomBlocksConfig.getInstance().customExcavationBlocks.contains(item)) {
            xp = ModChecks.getCustomBlock(block).getXpGain();
        }
        else {
            xp = Config.getInstance().getExcavationBaseXP();
        }

        if (Permissions.excavationTreasures(player)) {
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
                    int randomChance = 100;

                    if (Permissions.luckyExcavation(player)) {
                        randomChance = (int) (randomChance * 0.75);
                    }

                    if (Misc.getRandom().nextDouble() * randomChance <= treasure.getDropChance()) {
                        xp += treasure.getXp();
                        is.add(treasure.getDrop());
                    }
                }
            }

            //Drop items
            for (ItemStack x : is) {
                if (x != null) {
                    Misc.dropItem(location, x);
                }
            }
        }

        Skills.xpProcessing(player, profile, SkillType.EXCAVATION, xp);
    }

    /**
     * Handle triple drops from Giga Drill Breaker.
     *
     * @param player The player using the ability
     * @param block The block to check
     */
    public static void gigaDrillBreaker(Player player, Block block) {
        if (player == null)
            return;

        Skills.abilityDurabilityLoss(player.getItemInHand(), Config.getInstance().getAbilityToolDamage());

        if (!mcMMO.placeStore.isTrue(block) && Misc.blockBreakSimulate(block, player, true)) {
            FakePlayerAnimationEvent armswing = new FakePlayerAnimationEvent(player);
            mcMMO.p.getServer().getPluginManager().callEvent(armswing);

            Excavation.excavationProcCheck(block, player);
            Excavation.excavationProcCheck(block, player);
        }

        if (mcMMO.spoutEnabled) {
            SpoutSounds.playSoundForPlayer(SoundEffect.POP, player, block.getLocation());
        }
    }
}