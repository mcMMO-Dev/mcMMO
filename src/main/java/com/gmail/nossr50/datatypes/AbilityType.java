package com.gmail.nossr50.datatypes;

import org.bukkit.entity.Player;

import com.gmail.nossr50.mcPermissions;
import com.gmail.nossr50.config.LoadProperties;
import com.gmail.nossr50.locale.mcLocale;

public enum AbilityType
{
	BERSERK(LoadProperties.berserkCooldown, mcLocale.getString("Skills.BerserkOn"), mcLocale.getString("Skills.BerserkOff"), "Skills.BerserkPlayer", mcLocale.getString("Skills.YourBerserk")),
	SUPER_BREAKER(LoadProperties.superBreakerCooldown, mcLocale.getString("Skills.SuperBreakerOn"), mcLocale.getString("Skills.SuperBreakerOff"), "Skills.SuperBreakerPlayer", mcLocale.getString("Skills.YourSuperBreaker")),
	GIGA_DRILL_BREAKER(LoadProperties.gigaDrillBreakerCooldown, mcLocale.getString("Skills.GigaDrillBreakerOn"), mcLocale.getString("Skills.GigaDrillBreakerOff"), "Skills.GigaDrillBreakerPlayer", mcLocale.getString("Skills.YourGigaDrillBreaker")),
	GREEN_TERRA(LoadProperties.greenTerraCooldown, mcLocale.getString("Skills.GreenTerraOn"), mcLocale.getString("Skills.GreenTerraOff"), "Skills.GreenTerraPlayer", mcLocale.getString("Skills.YourGreenTerra")),
	SKULL_SPLIITER(LoadProperties.skullSplitterCooldown, mcLocale.getString("Skills.SkullSplitterOn"), mcLocale.getString("Skills.SkullSplitterOff"), "Skills.SkullSplitterPlayer", mcLocale.getString("Skills.YourSkullSplitter")),
	TREE_FELLER(LoadProperties.treeFellerCooldown, mcLocale.getString("Skills.TreeFellerOn"), mcLocale.getString("Skills.TreeFellerOff"), "Skills.TreeFellerPlayer", mcLocale.getString("Skills.YourTreeFeller")),
	SERRATED_STRIKES(LoadProperties.skullSplitterCooldown, mcLocale.getString("Skills.SerratedStrikesOn"), mcLocale.getString("Skills.SerratedStrikesOff"), "Skills.SerratedStrikesPlayer", mcLocale.getString("Skills.YourSerratedStrikes"));
	
	private int cooldown;
	private String abilityOn;
	private String abilityOff;
	private String abilityPlayer;
	private String abilityRefresh;
	
	private AbilityType(int cooldown, String abilityOn, String abilityOff, String abilityPlayer, String abilityRefresh)
	{
		this.cooldown = cooldown;
		this.abilityOn = abilityOn;
		this.abilityOff = abilityOff;
		this.abilityPlayer = abilityPlayer;
		this.abilityRefresh = abilityRefresh;
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
		case SUPER_BREAKER:
			PP.setSuperBreakerMode(bool);
		case GIGA_DRILL_BREAKER:
			PP.setGigaDrillBreakerMode(bool);
		case GREEN_TERRA:
			PP.setGreenTerraMode(bool);
		case SKULL_SPLIITER:
			PP.setSkullSplitterMode(bool);
		case TREE_FELLER:
			PP.setTreeFellerMode(bool);
		case SERRATED_STRIKES:
			PP.setSerratedStrikesMode(bool);
		}
	}
	
	public boolean getInformed(PlayerProfile PP)
	{
		switch(this)
		{
		case BERSERK:
			return PP.getBerserkInformed();
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
		case SUPER_BREAKER:
			PP.setSuperBreakerInformed(bool);
		case GIGA_DRILL_BREAKER:
			PP.setGigaDrillBreakerInformed(bool);
		case GREEN_TERRA:
			PP.setGreenTerraInformed(bool);
		case SKULL_SPLIITER:
			PP.setSkullSplitterInformed(bool);
		case TREE_FELLER:
			PP.setTreeFellerInformed(bool);
		case SERRATED_STRIKES:
			PP.setSerratedStrikesInformed(bool);
		}
	}
	
	public boolean getPermissions(Player player)
	{
		switch(this)
		{
		case BERSERK:
			return mcPermissions.getInstance().unarmedAbility(player);
		case GIGA_DRILL_BREAKER:
			return mcPermissions.getInstance().excavationAbility(player);
		case GREEN_TERRA:
			return mcPermissions.getInstance().herbalismAbility(player);
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
}