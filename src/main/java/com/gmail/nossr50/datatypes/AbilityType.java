package com.gmail.nossr50.datatypes;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.gmail.nossr50.mcPermissions;
import com.gmail.nossr50.config.LoadProperties;
import com.gmail.nossr50.locale.mcLocale;
import com.gmail.nossr50.skills.Excavation;
import com.gmail.nossr50.skills.Herbalism;
import com.gmail.nossr50.skills.Mining;

public enum AbilityType
{
	BERSERK(LoadProperties.berserkCooldown, mcLocale.getString("Skills.BerserkOn"), mcLocale.getString("Skills.BerserkOff"), "Skills.BerserkPlayer", mcLocale.getString("Skills.YourBerserk"), "Skills.BerserkPlayerOff"),
	SUPER_BREAKER(LoadProperties.superBreakerCooldown, mcLocale.getString("Skills.SuperBreakerOn"), mcLocale.getString("Skills.SuperBreakerOff"), "Skills.SuperBreakerPlayer", mcLocale.getString("Skills.YourSuperBreaker"), "Skills.SuperBreakerPlayerOff"),
	GIGA_DRILL_BREAKER(LoadProperties.gigaDrillBreakerCooldown, mcLocale.getString("Skills.GigaDrillBreakerOn"), mcLocale.getString("Skills.GigaDrillBreakerOff"), "Skills.GigaDrillBreakerPlayer", mcLocale.getString("Skills.YourGigaDrillBreaker"), "Skills.GigaDrillBreakerPlayerOff"),
	GREEN_TERRA(LoadProperties.greenTerraCooldown, mcLocale.getString("Skills.GreenTerraOn"), mcLocale.getString("Skills.GreenTerraOff"), "Skills.GreenTerraPlayer", mcLocale.getString("Skills.YourGreenTerra"), mcLocale.getString("Skills.GreenTerraPlayerOff")),
	SKULL_SPLIITER(LoadProperties.skullSplitterCooldown, mcLocale.getString("Skills.SkullSplitterOn"), mcLocale.getString("Skills.SkullSplitterOff"), "Skills.SkullSplitterPlayer", mcLocale.getString("Skills.YourSkullSplitter"), "Skills.SkullSplitterPlayerOff"),
	TREE_FELLER(LoadProperties.treeFellerCooldown, mcLocale.getString("Skills.TreeFellerOn"), mcLocale.getString("Skills.TreeFellerOff"), "Skills.TreeFellerPlayer", mcLocale.getString("Skills.YourTreeFeller"), "Skills.TreeFellerPlayerOff"),
	SERRATED_STRIKES(LoadProperties.skullSplitterCooldown, mcLocale.getString("Skills.SerratedStrikesOn"), mcLocale.getString("Skills.SerratedStrikesOff"), "Skills.SerratedStrikesPlayer", mcLocale.getString("Skills.YourSerratedStrikes"), "Skills.SerratedStrikesPlayerOff"),
	BLAST_MINING(LoadProperties.blastMiningCooldown, null, null, "Skills.BlastMiningPlayer", mcLocale.getString("Skills.YourBlastMining"), null),
	LEAF_BLOWER(0, null, null, null, null, null);

	private int cooldown;
	private String abilityOn;
	private String abilityOff;
	private String abilityPlayer;
	private String abilityRefresh;
	private String abilityPlayerOff;
	
	private AbilityType(int cooldown, String abilityOn, String abilityOff, String abilityPlayer, String abilityRefresh, String abilityPlayerOff)
	{
		this.cooldown = cooldown;
		this.abilityOn = abilityOn;
		this.abilityOff = abilityOff;
		this.abilityPlayer = abilityPlayer;
		this.abilityRefresh = abilityRefresh;
		this.abilityPlayerOff = abilityPlayerOff;
	}
	
	public int getCooldown()
	{
		return this.cooldown;
	}
	
	public String getAbilityOn()
	{
		return this.abilityOn;
	}
	
	public String getAbilityOff()
	{
		return this.abilityOff;
	}
	
	public String getAbilityPlayer(Player player)
	{
		return mcLocale.getString(this.abilityPlayer, new Object[] {player.getName()});
	}
	
	public String getAbilityPlayerOff(Player player)
	{
	    return mcLocale.getString(this.abilityPlayerOff, new Object[] {player.getName()});
	}
	
	public String getAbilityRefresh()
	{
		return this.abilityRefresh;
	}
	
	public boolean getMode(PlayerProfile PP)
	{
		switch(this)
		{
		case BERSERK:
			return PP.getBerserkMode();
		case SUPER_BREAKER:
			return PP.getSuperBreakerMode();
		case GIGA_DRILL_BREAKER:
			return PP.getGigaDrillBreakerMode();
		case GREEN_TERRA:
			return PP.getGreenTerraMode();
		case SKULL_SPLIITER:
			return PP.getSkullSplitterMode();
		case TREE_FELLER:
			return PP.getTreeFellerMode();
		case SERRATED_STRIKES:
			return PP.getSerratedStrikesMode();
		}
		return false;
	}

	public void setMode(PlayerProfile PP, boolean bool)
	{
		switch(this)
		{
		case BERSERK:
			PP.setBerserkMode(bool);
			break;
		case SUPER_BREAKER:
			PP.setSuperBreakerMode(bool);
			break;
		case GIGA_DRILL_BREAKER:
			PP.setGigaDrillBreakerMode(bool);
			break;
		case GREEN_TERRA:
			PP.setGreenTerraMode(bool);
			break;
		case SKULL_SPLIITER:
			PP.setSkullSplitterMode(bool);
			break;
		case TREE_FELLER:
			PP.setTreeFellerMode(bool);
			break;
		case SERRATED_STRIKES:
			PP.setSerratedStrikesMode(bool);
			break;
		}
	}
	
	public boolean getInformed(PlayerProfile PP)
	{
		switch(this)
		{
		case BERSERK:
			return PP.getBerserkInformed();
		case BLAST_MINING:
		    return PP.getBlastMiningInformed();
		case SUPER_BREAKER:
			return PP.getSuperBreakerInformed();
		case GIGA_DRILL_BREAKER:
			return PP.getGigaDrillBreakerInformed();
		case GREEN_TERRA:
			return PP.getGreenTerraInformed();
		case SKULL_SPLIITER:
			return PP.getSkullSplitterInformed();
		case TREE_FELLER:
			return PP.getTreeFellerInformed();
		case SERRATED_STRIKES:
			return PP.getSerratedStrikesInformed();
		}
		return false;
	}
	
	public void setInformed(PlayerProfile PP, boolean bool)
	{
		switch(this)
		{
		case BERSERK:
			PP.setBerserkInformed(bool);
			break;
		case BLAST_MINING:
		    PP.setBlastMiningInformed(bool);
		    break;
		case SUPER_BREAKER:
			PP.setSuperBreakerInformed(bool);
			break;
		case GIGA_DRILL_BREAKER:
			PP.setGigaDrillBreakerInformed(bool);
			break;
		case GREEN_TERRA:
			PP.setGreenTerraInformed(bool);
			break;
		case SKULL_SPLIITER:
			PP.setSkullSplitterInformed(bool);
			break;
		case TREE_FELLER:
			PP.setTreeFellerInformed(bool);
			break;
		case SERRATED_STRIKES:
			PP.setSerratedStrikesInformed(bool);
			break;
		}
	}
	
	public boolean getPermissions(Player player)
	{
		switch(this)
		{
		case BERSERK:
			return mcPermissions.getInstance().unarmedAbility(player);
		case BLAST_MINING:
		    return mcPermissions.getInstance().blastMining(player);
		case GIGA_DRILL_BREAKER:
			return mcPermissions.getInstance().excavationAbility(player);
		case GREEN_TERRA:
			return mcPermissions.getInstance().herbalismAbility(player);
		case LEAF_BLOWER:
			return mcPermissions.getInstance().woodcutting(player);
		case SERRATED_STRIKES:
			return mcPermissions.getInstance().swordsAbility(player);
		case SKULL_SPLIITER:
			return mcPermissions.getInstance().axesAbility(player);
		case SUPER_BREAKER:
			return mcPermissions.getInstance().miningAbility(player);
		case TREE_FELLER:
			return mcPermissions.getInstance().woodCuttingAbility(player);
		}
		return false;
	}
	
	public boolean blockCheck(Material material) {
		switch (this) {
		case BERSERK:
			return (Excavation.canBeGigaDrillBroken(material) || material.equals(Material.SNOW));
		case GIGA_DRILL_BREAKER:
			return Excavation.canBeGigaDrillBroken(material);
		case GREEN_TERRA:
			return Herbalism.makeMossy(material);
		case LEAF_BLOWER:
			return material.equals(Material.LEAVES);
		case SUPER_BREAKER:
			return Mining.canBeSuperBroken(material);
		case TREE_FELLER:
			return material.equals(Material.LOG);
		default:
			return false;
		}
	}
}