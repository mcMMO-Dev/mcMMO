package com.gmail.nossr50.datatypes.skills;

import java.util.List;
import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.BlockUtils;
import com.gmail.nossr50.util.StringUtils;

public class AbilityType {
	private static List<AbilityType> abilityTypes = new ArrayList<AbilityType>();
	private static List<String> abilityNames = new ArrayList<String>();
	private static List<String> lowerAbilityNames = new ArrayList<String>();
	
    public static final AbilityType berserk = new AbilityType(
    		"BERSERK",
            "Unarmed.Skills.Berserk.On",
            "Unarmed.Skills.Berserk.Off",
            "Unarmed.Skills.Berserk.Other.On",
            "Unarmed.Skills.Berserk.Refresh",
            "Unarmed.Skills.Berserk.Other.Off") {
    	@Override
    	public boolean blockCheck(BlockState blockState) {
            return (BlockUtils.affectedByGigaDrillBreaker(blockState) || blockState.getType() == Material.SNOW);
    	}
    };

    public static final AbilityType superBreaker = new AbilityType(
    		"SUPER_BREAKER",
            "Mining.Skills.SuperBreaker.On",
            "Mining.Skills.SuperBreaker.Off",
            "Mining.Skills.SuperBreaker.Other.On",
            "Mining.Skills.SuperBreaker.Refresh",
            "Mining.Skills.SuperBreaker.Other.Off") {
    	@Override
    	public boolean blockCheck(BlockState blockState) {
            return BlockUtils.affectedBySuperBreaker(blockState);
    	}
    };

    public static final AbilityType gigaDrillBreaker = new AbilityType(
    		"GIGA_DRILL_BREAKER",
            "Excavation.Skills.GigaDrillBreaker.On",
            "Excavation.Skills.GigaDrillBreaker.Off",
            "Excavation.Skills.GigaDrillBreaker.Other.On",
            "Excavation.Skills.GigaDrillBreaker.Refresh",
            "Excavation.Skills.GigaDrillBreaker.Other.Off") {
    	@Override
    	public boolean blockCheck(BlockState blockState) {
            return BlockUtils.affectedByGigaDrillBreaker(blockState);
    	}
    };

    public static final AbilityType greenTerra = new AbilityType(
    		"GREEN_TERRA",
            "Herbalism.Skills.GTe.On",
            "Herbalism.Skills.GTe.Off",
            "Herbalism.Skills.GTe.Other.On",
            "Herbalism.Skills.GTe.Refresh",
            "Herbalism.Skills.GTe.Other.Off") {
    	@Override
    	public boolean blockCheck(BlockState blockState) {
            return BlockUtils.canMakeMossy(blockState);
    	}
    };

    public static final AbilityType skullSplitter = new AbilityType(
    		"SKULL_SPLITTER",
            "Axes.Skills.SS.On",
            "Axes.Skills.SS.Off",
            "Axes.Skills.SS.Other.On",
            "Axes.Skills.SS.Refresh",
            "Axes.Skills.SS.Other.Off");

    public static final AbilityType treeFeller = new AbilityType(
    		"TREE_FELLER",
            "Woodcutting.Skills.TreeFeller.On",
            "Woodcutting.Skills.TreeFeller.Off",
            "Woodcutting.Skills.TreeFeller.Other.On",
            "Woodcutting.Skills.TreeFeller.Refresh",
            "Woodcutting.Skills.TreeFeller.Other.Off") {
    	@Override
    	public boolean blockCheck(BlockState blockState) {
            return BlockUtils.isLog(blockState);
    	}
    };

    public static final AbilityType serratedStrikes = new AbilityType(
    		"SERRATED_STRIKES",
            "Swords.Skills.SS.On",
            "Swords.Skills.SS.Off",
            "Swords.Skills.SS.Other.On",
            "Swords.Skills.SS.Refresh",
            "Swords.Skills.SS.Other.Off");

    /**
     * Has cooldown - but has to share a skill with Super Breaker, so needs special treatment
     */
    public static final AbilityType blastMining = new AbilityType(
    		"BLAST_MINING",
            null,
            null,
            "Mining.Blast.Other.On",
            "Mining.Blast.Refresh",
            null);

    private String name;
    private String abilityOn;
    private String abilityOff;
    private String abilityPlayer;
    private String abilityRefresh;
    private String abilityPlayerOff;

    private AbilityType(String name, String abilityOn, String abilityOff, String abilityPlayer, String abilityRefresh, String abilityPlayerOff) {
        this.name = name;
    	this.abilityOn = abilityOn;
        this.abilityOff = abilityOff;
        this.abilityPlayer = abilityPlayer;
        this.abilityRefresh = abilityRefresh;
        this.abilityPlayerOff = abilityPlayerOff;
        abilityTypes.add(this);
        abilityNames.add(this.getUnprettyName());
        lowerAbilityNames.add(this.getUnprettyName().toLowerCase());
    }

    public int getCooldown() {
        return Config.getInstance().getCooldown(this);
    }

    public int getMaxLength() {
        return Config.getInstance().getMaxLength(this);
    }

    public String getAbilityOn() {
        return LocaleLoader.getString(this.abilityOn);
    }

    public String getAbilityOff() {
        return LocaleLoader.getString(this.abilityOff);
    }

    public String getAbilityPlayer(Player player) {
        return LocaleLoader.getString(this.abilityPlayer, player.getName());
    }

    public String getAbilityPlayerOff(Player player) {
        return LocaleLoader.getString(this.abilityPlayerOff, player.getName());
    }

    public String getAbilityRefresh() {
        return LocaleLoader.getString(this.abilityRefresh);
    }

    public String getName() {
        return StringUtils.getPrettyAbilityString(this);
    }
    
    public String getUnprettyName() {
    	return this.name;
    }

    @Override
    public String toString() {
        String baseString = name;
        String[] substrings = baseString.split("_");
        String formattedString = "";

        int size = 1;

        for (String string : substrings) {
            formattedString = formattedString.concat(StringUtils.getCapitalized(string));

            if (size < substrings.length) {
                formattedString = formattedString.concat("_");
            }

            size++;
        }

        return formattedString;
    }

    /**
     * Get the permissions for this ability.
     *
     * @param player Player to check permissions for
     * @return true if the player has permissions, false otherwise
     */
    public boolean getPermissions(Player player) {
    	return player.hasPermission("mcmmo.ability." 
    			+ SkillType.byAbility(this).getName().toLowerCase() 
    			+ "." + name.replace("_", "").toLowerCase());
    }

    /**
     * Check if a block is affected by this ability.
     *
     * @param blockState the block to check
     * @return true if the block is affected by this ability, false otherwise
     */
    public boolean blockCheck(BlockState blockState) {
        return false;
    }
    
    public static List<AbilityType> getAbilities() {
    	return abilityTypes;
    }
    
    public static List<String> getAbilitieNames() {
    	return abilityNames;
    }
    
    public static List<String> getLowerAbilitieNames() {
    	return lowerAbilityNames;
    }
}
