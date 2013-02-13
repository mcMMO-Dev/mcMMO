package com.gmail.nossr50.util;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.PluginManager;

import com.gmail.nossr50.skills.utilities.SkillType;

public final class Permissions {
    private Permissions() {}

    public static boolean hasPermission(CommandSender sender, String perm) {
        return (sender.hasPermission(perm));
    }

    public static boolean hasDynamicPermission(CommandSender sender, String perm, String defaultType) {
        Map<String, Object> m = new HashMap<String, Object>();

        if(defaultType != null) {
            m.put("default", defaultType);
        }

        PluginManager manager = Bukkit.getPluginManager();

        if (manager.getPermission(perm) == null) {
            Permission.loadPermission(perm, m);
        }

        return hasPermission(sender, perm);
    }

    /*
     * GENERIC PERMISSIONS
     */

    public static boolean motd(Player player) {
        return player.hasPermission("mcmmo.motd");
    }

    /**
     * @deprecated Use the permission "mcmmo.all" instead.
     */
    @Deprecated
    public static boolean admin(Player player) {
        return player.hasPermission("mcmmo.admin");
    }

    /*
     * MCMMO.BYPASS.*
     */
    public static boolean hardcoremodeBypass(Player player) {
        return player.hasPermission("mcmmo.bypass.hardcoremode");
    }

    public static boolean arcaneBypass(Player player) {
        return player.hasPermission("mcmmo.bypass.arcanebypass");
    }

    /**
     * @deprecated Use {@link #inspectFar(player)} instead.
     */
    @Deprecated
    public static boolean inspectDistanceBypass(Player player) {
        return player.hasPermission("mcmmo.bypass.inspect.distance");
    }

    public static boolean inspectFar(Player player) {
        return (player.hasPermission("mcmmo.commands.inspect.far"));
    }

    /**
     * @deprecated Use {@link #inspectOffline(player)} instead.
     */
    @Deprecated
    public static boolean inspectOfflineBypass(Player player) {
        return player.hasPermission("mcmmo.bypass.inspect.offline");
    }

    public static boolean inspectOffline(Player player) {
        return (player.hasPermission("mcmmo.commands.inspect.offline"));
    }

    /*
     * MCMMO.TOOLS.*
     */

    public static boolean mcrefresh(Player player) {
        return player.hasPermission("mcmmo.tools.mcrefresh");
    }

    public static boolean mcremove(Player player) {
        return player.hasPermission("mcmmo.tools.mcremove");
    }

    /**
     * @deprecated Use {@link #mmoeditCommand(player)} instead.
     */
    @Deprecated
    public static boolean mmoedit(CommandSender sender) {
        return player.hasPermission("mcmmo.tools.mmoedit");
    }

    /**
     * @deprecated Use {@link #mcgodCommand(player)} instead.
     */
    @Deprecated
    public static boolean mcgod(CommandSender sender) {
        return player.hasPermission("mcmmo.tools.mcgod");
    }

    /*
     * MCMMO.PERKS.LUCKY*
     */

    public static boolean lucky(Player player, SkillType skill) {
        return player.hasPermission("mcmmo.perks.lucky." + skill.toString().toLowerCase());
    }

    public static boolean luckyAcrobatics(Player player) {
        return player.hasPermission("mcmmo.perks.lucky.acrobatics");
    }

    public static boolean luckyArchery(Player player) {
        return player.hasPermission("mcmmo.perks.lucky.archery");
    }

    public static boolean luckyAxes(Player player) {
        return player.hasPermission("mcmmo.perks.lucky.axes");
    }

    public static boolean luckyExcavation(Player player) {
        return player.hasPermission("mcmmo.perks.lucky.excavation");
    }

    public static boolean luckyFishing(Player player) {
        return player.hasPermission("mcmmo.perks.lucky.fishing");
    }

    public static boolean luckyHerbalism(Player player) {
        return player.hasPermission("mcmmo.perks.lucky.herbalism");
    }

    public static boolean luckyMining(Player player) {
        return player.hasPermission("mcmmo.perks.lucky.mining");
    }

    public static boolean luckyRepair(Player player) {
        return player.hasPermission("mcmmo.perks.lucky.repair");
    }

    public static boolean luckySmelting(Player player) {
        return player.hasPermission("mcmmo.perks.lucky.smelting");
    }

    public static boolean luckySwords(Player player) {
        return player.hasPermission("mcmmo.perks.lucky.swords");
    }

    public static boolean luckyTaming(Player player) {
        return player.hasPermission("mcmmo.perks.lucky.taming");
    }

    public static boolean luckyUnarmed(Player player) {
        return player.hasPermission("mcmmo.perks.lucky.unarmed");
    }

    public static boolean luckyWoodcutting(Player player) {
        return player.hasPermission("mcmmo.perks.lucky.woodcutting");
    }

    /*
     * MCMMO.PERKS.XP*
     */

    public static boolean xpQuadruple(Player player) {
        return player.hasPermission("mcmmo.perks.xp.quadruple");
    }

    public static boolean xpTriple(Player player) {
        return player.hasPermission("mcmmo.perks.xp.triple");
    }

    public static boolean xpDoubleAndOneHalf(Player player) {
        return player.hasPermission("mcmmo.perks.xp.150percentboost");
    }

    public static boolean xpDouble(Player player) {
        return player.hasPermission("mcmmo.perks.xp.double");
    }

    public static boolean xpOneAndOneHalf(Player player) {
        return player.hasPermission("mcmmo.perks.xp.50percentboost");
    }

    /*
     * MCMMO.PERKS.COOLDOWNS*
     */

    public static boolean cooldownsHalved(Player player) {
        return player.hasPermission("mcmmo.perks.cooldowns.halved");
    }

    public static boolean cooldownsThirded(Player player) {
        return player.hasPermission("mcmmo.perks.cooldowns.thirded");
    }

    public static boolean cooldownsQuartered(Player player) {
        return player.hasPermission("mcmmo.perks.cooldowns.quartered");
    }

    /*
     * MCMMO.PERKS.ACTIVATIONTIME*
     */

    public static boolean activationTwelve(Player player) {
        return player.hasPermission("mcmmo.perks.activationtime.twelveseconds");
    }

    public static boolean activationEight(Player player) {
        return player.hasPermission("mcmmo.perks.activationtime.eightseconds");
    }

    public static boolean activationFour(Player player) {
        return player.hasPermission("mcmmo.perks.activationtime.fourseconds");
    }

    /*
     * MCMMO.ABILITY.TAMING.*
     */

    public static boolean fastFoodService(Player player) {
        return player.hasPermission("mcmmo.ability.taming.fastfoodservice");
    }

    public static boolean sharpenedClaws(Player player) {
        return player.hasPermission("mcmmo.ability.taming.sharpenedclaws");
    }

    public static boolean gore(Player player) {
        return player.hasPermission("mcmmo.ability.taming.gore");
    }

    public static boolean callOfTheWild(Player player) {
        return player.hasPermission("mcmmo.ability.taming.callofthewild");
    }

    public static boolean environmentallyAware(Player player) {
        return player.hasPermission("mcmmo.ability.taming.environmentallyaware");
    }

    public static boolean thickFur(Player player) {
        return player.hasPermission("mcmmo.ability.taming.thickfur");
    }

    public static boolean shockProof(Player player) {
        return player.hasPermission("mcmmo.ability.taming.shockproof");
    }

    public static boolean beastLore(Player player) {
        return player.hasPermission("mcmmo.ability.taming.beastlore");
    }

    /*
     * MCMMO.ABILITY.FISHING.*
     */

    public static boolean shakeMob(Player player) {
        return player.hasPermission("mcmmo.ability.fishing.shakemob");
    }

    public static boolean fishingTreasures(Player player) {
        return player.hasPermission("mcmmo.ability.fishing.treasures");
    }

    public static boolean fishingMagic(Player player) {
        return player.hasPermission("mcmmo.ability.fishing.magic");
    }

    public static boolean fishermansDiet(Player player) {
        return player.hasPermission("mcmmo.ability.fishing.fishermansdiet");
    }

    public static boolean fishingVanillaXPBoost(Player player) {
        return player.hasPermission("mcmmo.ability.fishing.vanillaxpboost");
    }

    /*
     * MCMMO.ABILITY.MINING.*
     */

    public static boolean superBreaker(Player player) {
        return player.hasPermission("mcmmo.ability.mining.superbreaker");
    }

    public static boolean miningDoubleDrops(Player player) {
        return player.hasPermission("mcmmo.ability.mining.doubledrops");
    }

    /*
     * MCMMO.ABILITY.WOODCUTTING.*
     */

    public static boolean treeFeller(Player player) {
        return player.hasPermission("mcmmo.ability.woodcutting.treefeller");
    }

    public static boolean leafBlower(Player player) {
        return player.hasPermission("mcmmo.ability.woodcutting.leafblower");
    }

    public static boolean woodcuttingDoubleDrops(Player player) {
        return player.hasPermission("mcmmo.ability.woodcutting.doubledrops");
    }

    /*
     * MCMMO.ABILITY.REPAIR.*
     */

    public static boolean repairBonus(Player player) {
        return player.hasPermission("mcmmo.ability.repair.repairbonus");
    }

    public static boolean repairMastery(Player player) {
        return player.hasPermission("mcmmo.ability.repair.repairmastery");
    }

    public static boolean arcaneForging(Player player) {
        return player.hasPermission("mcmmo.ability.repair.arcaneforging");
    }

    public static boolean woodRepair(Player player) {
        return player.hasPermission("mcmmo.ability.repair.woodrepair");
    }

    public static boolean stoneRepair(Player player) {
        return player.hasPermission("mcmmo.ability.repair.stonerepair");
    }

    public static boolean leatherRepair(Player player) {
        return player.hasPermission("mcmmo.ability.repair.leatherrepair");
    }

    public static boolean ironRepair(Player player) {
        return player.hasPermission("mcmmo.ability.repair.ironrepair");
    }

    public static boolean goldRepair(Player player) {
        return player.hasPermission("mcmmo.ability.repair.goldrepair");
    }

    public static boolean diamondRepair(Player player) {
        return player.hasPermission("mcmmo.ability.repair.diamondrepair");
    }

    public static boolean armorRepair(Player player) {
        return player.hasPermission("mcmmo.ability.repair.armorrepair");
    }

    public static boolean toolRepair(Player player) {
        return player.hasPermission("mcmmo.ability.repair.toolrepair");
    }

    public static boolean otherMaterialRepair(Player player) {
        return player.hasPermission("mcmmo.ability.repair.othermaterialrepair");
    }

    public static boolean otherRepair(Player player) {
        return player.hasPermission("mcmmo.ability.repair.otherrepair");
    }

    public static boolean stringRepair(Player player) {
        return player.hasPermission("mcmmo.ability.repair.stringrepair");
    }

    public static boolean salvage(Player player) {
        return player.hasPermission("mcmmo.ability.repair.salvage");
    }


    /*
     * MCMMO.ABILITY.UNARMED.*
     */

    public static boolean unarmedBonus(Player player) {
        return player.hasPermission("mcmmo.ability.unarmed.bonusdamage");
    }

    public static boolean disarm(Player player) {
        return player.hasPermission("mcmmo.ability.unarmed.disarm");
    }

    public static boolean berserk(Player player) {
        return player.hasPermission("mcmmo.ability.unarmed.berserk");
    }

    public static boolean deflect(Player player) {
        return player.hasPermission("mcmmo.ability.unarmed.deflect");
    }

    public static boolean ironGrip(Player player) {
        return player.hasPermission("mcmmo.ability.unarmed.irongrip");
    }

    /*
     * MCMMO.ABILITY.ARCHERY.*
     */

    public static boolean trackArrows(Player player) {
        return player.hasPermission("mcmmo.ability.archery.trackarrows");
    }

    public static boolean daze(Player player) {
        return player.hasPermission("mcmmo.ability.archery.daze");
    }

    public static boolean archeryBonus(Player player) {
        return player.hasPermission("mcmmo.ability.archery.bonusdamage");
    }

    /*
     * MCMMO.ABILITY.HERBALISM.*
     */

    public static boolean herbalismDoubleDrops(Player player) {
        return player.hasPermission("mcmmo.ability.herbalism.doubledrops");
    }

    public static boolean greenTerra(Player player) {
        return player.hasPermission("mcmmo.ability.herbalism.greenterra");
    }

    public static boolean greenThumbBlocks(Player player) {
        return player.hasPermission("mcmmo.ability.herbalism.greenthumbblocks");
    }

    public static boolean greenThumbCarrots(Player player) {
        return player.hasPermission("mcmmo.ability.herbalism.greenthumbcarrots");
    }

    public static boolean greenThumbCocoa(Player player) {
        return player.hasPermission("mcmmo.ability.herbalism.greenthumbcocoa");
    }

    public static boolean greenThumbNetherwart(Player player) {
        return player.hasPermission("mcmmo.ability.herbalism.greenthumbnetherwart");
    }

    public static boolean greenThumbPotatoes(Player player) {
        return player.hasPermission("mcmmo.ability.herbalism.greenthumbpotatoes");
    }

    public static boolean greenThumbWheat(Player player) {
        return player.hasPermission("mcmmo.ability.herbalism.greenthumbwheat");
    }

    public static boolean farmersDiet(Player player) {
        return player.hasPermission("mcmmo.ability.herbalism.farmersdiet");
    }

    public static boolean hylianLuck(Player player) {
        return player.hasPermission("mcmmo.ability.herbalism.hylianluck");
    }

    /*
     * MCMMO.ABILITY.EXCAVATION.*
     */

    public static boolean gigaDrillBreaker(Player player) {
        return player.hasPermission("mcmmo.ability.excavation.gigadrillbreaker");
    }

    public static boolean excavationTreasures(Player player) {
        return player.hasPermission("mcmmo.ability.excavation.treasures");
    }

    /*
     * MCMMO.ABILITY.SWORDS.*
     */

    public static boolean swordsBleed(Player player) {
        return player.hasPermission("mcmmo.ability.swords.bleed");
    }

    public static boolean serratedStrikes(Player player) {
        return player.hasPermission("mcmmo.ability.swords.serratedstrikes");
    }

    public static boolean counterAttack(Player player) {
        return player.hasPermission("mcmmo.ability.swords.counterattack");
    }

    /*
     * MCMMO.ABILITY.AXES.*
     */

    public static boolean skullSplitter(Player player) {
        return player.hasPermission("mcmmo.ability.axes.skullsplitter");
    }

    public static boolean axeBonus(Player player) {
        return player.hasPermission("mcmmo.ability.axes.bonusdamage");
    }

    public static boolean criticalHit(Player player) {
        return player.hasPermission("mcmmo.ability.axes.criticalhit");
    }

    public static boolean impact(Player player) {
        return player.hasPermission("mcmmo.ability.axes.impact");
    }

    public static boolean greaterImpact(Player player) {
        return player.hasPermission("mcmmo.ability.axes.greaterimpact");
    }

    /*
     * MCMMO.ABILITY.ACROBATICS.*
     */

    public static boolean roll(Player player) {
        return player.hasPermission("mcmmo.ability.acrobatics.roll");
    }

    public static boolean gracefulRoll(Player player) {
        return player.hasPermission("mcmmo.ability.acrobatics.gracefulroll");
    }

    public static boolean dodge(Player player) {
        return player.hasPermission("mcmmo.ability.acrobatics.dodge");
    }

    /*
     * MCMMO.ABILITY.BLASTMINING.*
     */

    public static boolean biggerBombs(Player player) {
        return player.hasPermission("mcmmo.ability.mining.blastmining.biggerbombs");
    }

    public static boolean demolitionsExpertise(Player player) {
        return player.hasPermission("mcmmo.ability.mining.blastmining.demolitionsexpertise");
    }

    public static boolean blastMining(Player player) {
        return player.hasPermission("mcmmo.ability.mining.blastmining.detonate");
    }

    /*
     * MCMMO.ABILITY.SMELTING.* 
     */

    public static boolean fuelEfficiency(Player player) {
        return player.hasPermission("mcmmo.ability.smelting.fuelefficiency");
    }

    public static boolean secondSmelt(Player player) {
        return player.hasPermission("mcmmo.ability.smelting.secondsmelt");
    }

    public static boolean fluxMining(Player player) {
        return player.hasPermission("mcmmo.ability.smelting.fluxmining");
    }

    public static boolean smeltingVanillaXPBoost(Player player) {
        return player.hasPermission("mcmmo.ability.smelting.vanillaxpboost");
    }

    /*
     * MCMMO.COMMANDS.*
     */

    public static boolean mmoeditCommand(Player player) {
        return (player.hasPermission("mcmmo.commands.mmoedit"));
    }

    public static boolean skillResetCommand(Player player) {
        return (player.hasPermission("mcmmo.commands.skillreset"));
    }

    public static boolean mcAbilityCommand(Player player) {
        return (player.hasPermission("mcmmo.commands.mcability"));
    }

    public static boolean mcgodCommand(CommandSender sender) {
        return (hasPermission(sender, "mcmmo.commands.mcgod"));
    }

    /**
     * @deprecated Use {@link #mcAbilityCommand(player)} instead.
     */
    @Deprecated
    public static boolean mcAbility(Player player) {
        return player.hasPermission("mcmmo.commands.ability");
    }

    public static boolean partyTeleport(Player player) {
        return player.hasPermission("mcmmo.commands.ptp");
    }

    public static boolean inspect(Player player) {
        return player.hasPermission("mcmmo.commands.inspect");
    }

    public static boolean party(Player player) {
        return player.hasPermission("mcmmo.commands.party");
    }

    /**
     * @deprecated Use {@link #skillResetCommand(player)} instead.
     */
    @Deprecated
    public static boolean skillReset(Player player) {
        return player.hasPermission("mcmmo.skillreset");
    }

    /*
     * MCMMO.CHAT.*
     */

    public static boolean partyChat(Player player) {
        return player.hasPermission("mcmmo.chat.partychat");
    }

    public static boolean partyLock(Player player) {
        return player.hasPermission("mcmmo.chat.partylock");
    }

    public static boolean adminChat(Player player) {
        return player.hasPermission("mcmmo.chat.adminchat");
    }

    /*
     * MCMMO.SKILLS.*
     */

    public static boolean taming(Player player) {
        return player.hasPermission("mcmmo.skills.taming");
    }

    public static boolean mining(Player player) {
        return player.hasPermission("mcmmo.skills.mining");
    }

    public static boolean fishing(Player player) {
        return player.hasPermission("mcmmo.skills.fishing");
    }

    public static boolean woodcutting(Player player) {
        return player.hasPermission("mcmmo.skills.woodcutting");
    }

    public static boolean repair(Player player) {
        return player.hasPermission("mcmmo.skills.repair");
    }

    public static boolean unarmed(Player player) {
        return player.hasPermission("mcmmo.skills.unarmed");
    }

    public static boolean archery(Player player) {
        return player.hasPermission("mcmmo.skills.archery");
    }

    public static boolean herbalism(Player player) {
        return player.hasPermission("mcmmo.skills.herbalism");
    }

    public static boolean excavation(Player player) {
        return player.hasPermission("mcmmo.skills.excavation");
    }

    public static boolean swords(Player player) {
        return player.hasPermission("mcmmo.skills.swords");
    }

    public static boolean axes(Player player) {
        return player.hasPermission("mcmmo.skills.axes");
    }

    public static boolean acrobatics(Player player) {
        return player.hasPermission("mcmmo.skills.acrobatics");
    }

    public static boolean smelting(Player player) {
        return player.hasPermission("mcmmo.skills.smelting");
    }

    /*
     * MCMMO.PARTY.*
     */

    public static boolean friendlyFire(Player player) {
        return player.hasPermission("mcmmo.party.friendlyfire");
    }
}
