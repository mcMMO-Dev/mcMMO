package com.gmail.nossr50.skills.ranching;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Sheep;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.skills.SkillManager;
import com.gmail.nossr50.skills.ranching.Ranching.Tier;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.skills.SkillUtils;

public class RanchingManager extends SkillManager {
    public RanchingManager(McMMOPlayer mcMMOPlayer) {
        super(mcMMOPlayer, SkillType.RANCHING);
    }

    public boolean canUseMultipleBirth() {
        return getSkillLevel() >= Ranching.multipleBirthIncreaseLevel && Permissions.multipleBirth(getPlayer());
    }

    public boolean canUseMasterHerder() {
        return getSkillLevel() >= Ranching.masterHerderIncreaseLevel && Permissions.masterHerder(getPlayer());
    }

    

    public boolean canUseVanillaXpBoost() {
        return getSkillLevel() >= Ranching.Tier.ONE.getLevel() && Permissions.vanillaXpBoost(getPlayer(), skill);
    }

    /**
     * Handle the Carnivore's Diet ability
     *
     * @param rankChange The # of levels to change rank for the food
     * @param eventFoodLevel The initial change in hunger from the event
     * @return the modified change in hunger for the event
     */
    public int handleCarnivoresDiet(int rankChange, int eventFoodLevel) {
        return SkillUtils.handleFoodSkills(getPlayer(), skill, eventFoodLevel, Ranching.carnivoresDietRankLevel1, Ranching.carnivoresDietMaxLevel, rankChange);
    }

    public void handleShearsMasterySheep(Entity entity) {
        if (SkillUtils.activationSuccessful(getSkillLevel(), getActivationChance(), Ranching.shearsMasteryMaxChance, Ranching.shearsMasteryMaxLevel)) {
            Location location = entity.getLocation();
            Sheep sheep = (Sheep) entity;
            MaterialData wool = new MaterialData(Material.WOOL, sheep.getColor().getDyeData());

            Misc.randomDropItems(location, wool.toItemStack(), Ranching.shearsMasteryMaxBonus);
        }
    }

    public void handleShearsMasteryMooshroom(Entity entity) {
        if (SkillUtils.activationSuccessful(getSkillLevel(), getActivationChance(), Ranching.shearsMasteryMaxChance, Ranching.shearsMasteryMaxLevel)) {
            Location location = entity.getLocation();
            ItemStack itemStack = new ItemStack(Material.RED_MUSHROOM);

            Misc.randomDropItems(location, itemStack, Ranching.shearsMasteryMaxBonus);
        }
    }

    public void handleArtisanButcher(Entity entity) {
        if (SkillUtils.activationSuccessful(getSkillLevel(), getActivationChance(), Ranching.artisanButcherMaxChance, Ranching.artisanButcherMaxLevel)) {
            Material material = null;

            switch (entity.getType()) {
                case CHICKEN:
                    material = Material.RAW_CHICKEN;
                    break;
                case COW:
                    material = Material.RAW_BEEF;
                    break;
                case PIG:
                    material = Material.PORK;
                    break;
                default:
                    return;
            }

            Misc.randomDropItems(entity.getLocation(), new ItemStack(material), Ranching.artisanButcherMaxBonus);
        }
    }

    /**
     * Handle the vanilla XP boost for Ranching
     *
     * @param experience The amount of experience initially awarded by the event
     * @return the modified event damage
     */
    public int handleVanillaXpBoost(int experience) {
        return experience * getVanillaXpMultiplier();
    }

    /**
     * Gets the vanilla XP multiplier
     *
     * @return the vanilla XP multiplier
     */
    private int getVanillaXpMultiplier() {
        int skillLevel = getSkillLevel();

        for (Tier tier : Tier.values()) {
            if (skillLevel >= tier.getLevel()) {
                return tier.getVanillaXPBoostModifier();
            }
        }

        return 0;
    }

    public void handleMasterHerder(Entity entity, ItemStack inHand) {
        // TODO Auto-generated method stub
        
    }

    public boolean isBreedFood(Entity entity, ItemStack inHand) {
        // TODO Auto-generated method stub
        // Check if the entity and the item type match
        return false;
    }
}
