package com.gmail.nossr50.util;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Permissions {
    public static boolean hasPermission(CommandSender sender, String perm)
    {
        if (sender.hasPermission(perm))
            return true;

        return false;
    }

    public static boolean hasPermission(Player sender, String perm)
    {        
        if (sender.hasPermission(perm))
            return true;

        return false;
    }

    /*
     * GENERIC PERMISSIONS
     */

    public static boolean motd(Player player) {
        return hasPermission(player, "mcmmo.motd");
    }

    public static boolean admin(Player player) {
        return hasPermission(player, "mcmmo.admin");
    }

    /*
     * MCMMO.BYPASS.*
     */
    public static boolean hardcoremodeBypass(Player player) {
        return hasPermission(player, "mcmmo.bypass.hardcoremode");
    }

    public static boolean arcaneBypass(Player player) {
        return hasPermission(player, "mcmmo.bypass.arcanebypass");
    }

    public static boolean inspectDistanceBypass(Player player) {
        return hasPermission(player, "mcmmo.bypass.inspect.distance");
    }

    public static boolean inspectOfflineBypass(Player player) {
        return hasPermission(player, "mcmmo.bypass.inspect.offline");
    }

    /*
     * MCMMO.TOOLS.*
     */

    public static boolean mcrefresh(Player player) {
        return hasPermission(player, "mcmmo.tools.mcrefresh");
    }

    public static boolean mcremove(Player player) {
        return hasPermission(player, "mcmmo.tools.mcremove");
    }

    public static boolean mmoedit(Player player) {
        return hasPermission(player, "mcmmo.tools.mmoedit");
    }

    public static boolean mcgod(Player player) {
        return hasPermission(player, "mcmmo.tools.mcgod");
    }

    /*
     * MCMMO.PERKS.LUCKY*
     */

    public static boolean luckyAcrobatics(Player player) {
        return hasPermission(player, "mcmmo.perks.lucky.acrobatics");
    }

    public static boolean luckyArchery(Player player) {
        return hasPermission(player, "mcmmo.perks.lucky.archery");
    }

    public static boolean luckyAxes(Player player) {
        return hasPermission(player, "mcmmo.perks.lucky.axes");
    }

    public static boolean luckyExcavation(Player player) {
        return hasPermission(player, "mcmmo.perks.lucky.excavation");
    }

    public static boolean luckyFishing(Player player) {
        return hasPermission(player, "mcmmo.perks.lucky.fishing");
    }

    public static boolean luckyHerbalism(Player player) {
        return hasPermission(player, "mcmmo.perks.lucky.herbalism");
    }

    public static boolean luckyMining(Player player) {
        return hasPermission(player, "mcmmo.perks.lucky.mining");
    }

    public static boolean luckyRepair(Player player) {
        return hasPermission(player, "mcmmo.perks.lucky.repair");
    }

    public static boolean luckySwords(Player player) {
        return hasPermission(player, "mcmmo.perks.lucky.swords");
    }

    public static boolean luckyTaming(Player player) {
        return hasPermission(player, "mcmmo.perks.lucky.taming");
    }

    public static boolean luckyUnarmed(Player player) {
        return hasPermission(player, "mcmmo.perks.lucky.unarmed");
    }

    public static boolean luckyWoodcutting(Player player) {
        return hasPermission(player, "mcmmo.perks.lucky.woodcutting");
    }

    /*
     * MCMMO.PERKS.XP*
     */

    public static boolean xpQuadruple(Player player) {
        return hasPermission(player, "mcmmo.perks.xp.quadruple");
    }

    public static boolean xpTriple(Player player) {
        return hasPermission(player, "mcmmo.perks.xp.triple");
    }

    public static boolean xpDoubleAndOneHalf(Player player) {
        return hasPermission(player, "mcmmo.perks.xp.150percentboost");
    }

    public static boolean xpDouble(Player player) {
        return hasPermission(player, "mcmmo.perks.xp.double");
    }

    public static boolean xpOneAndOneHalf(Player player) {
        return hasPermission(player, "mcmmo.perks.xp.50percentboost");
    }

    /*
     * MCMMO.PERKS.COOLDOWNS*
     */

    public static boolean cooldownsHalved(Player player) {
        return hasPermission(player, "mcmmo.perks.cooldowns.halved");
    }

    public static boolean cooldownsThirded(Player player) {
        return hasPermission(player, "mcmmo.perks.cooldowns.thirded");
    }

    public static boolean cooldownsQuartered(Player player) {
        return hasPermission(player, "mcmmo.perks.cooldowns.quartered");
    }

    /*
     * MCMMO.PERKS.ACTIVATIONTIME*
     */

    public static boolean activationTwelve(Player player) {
        return hasPermission(player, "mcmmo.perks.activationtime.twelveseconds");
    }

    public static boolean activationEight(Player player) {
        return hasPermission(player, "mcmmo.perks.activationtime.eightseconds");
    }

    public static boolean activationFour(Player player) {
        return hasPermission(player, "mcmmo.perks.activationtime.fourseconds");
    }

    /*
     * MCMMO.ABILITY.TAMING.*
     */

    public static boolean fastFoodService(Player player) {
        return hasPermission(player, "mcmmo.ability.taming.fastfoodservice");
    }

    public static boolean sharpenedClaws(Player player) {
        return hasPermission(player, "mcmmo.ability.taming.sharpenedclaws");
    }

    public static boolean gore(Player player) {
        return hasPermission(player, "mcmmo.ability.taming.gore");
    }

    public static boolean callOfTheWild(Player player) {
        return hasPermission(player, "mcmmo.ability.taming.callofthewild");
    }

    public static boolean environmentallyAware(Player player) {
        return hasPermission(player, "mcmmo.ability.taming.environmentallyaware");
    }

    public static boolean thickFur(Player player) {
        return hasPermission(player, "mcmmo.ability.taming.thickfur");
    }

    public static boolean shockProof(Player player) {
        return hasPermission(player, "mcmmo.ability.taming.shockproof");
    }

    public static boolean beastLore(Player player) {
        return hasPermission(player, "mcmmo.ability.taming.beastlore");
    }

    /*
     * MCMMO.ABILITY.FISHING.*
     */

    public static boolean shakeMob(Player player) {
        return hasPermission(player, "mcmmo.ability.fishing.shakemob");
    }

    public static boolean fishingTreasures(Player player) {
        return hasPermission(player, "mcmmo.ability.fishing.treasures");
    }

    public static boolean fishingMagic(Player player) {
        return hasPermission(player, "mcmmo.ability.fishing.magic");
    }

    public static boolean fishermansDiet(Player player) {
        return hasPermission(player, "mcmmo.ability.fishing.fishermansdiet");
    }

    /*
     * MCMMO.ABILITY.MINING.*
     */

    public static boolean superBreaker(Player player) {
        return hasPermission(player, "mcmmo.ability.mining.superbreaker");
    }

    public static boolean miningDoubleDrops(Player player) {
        return hasPermission(player, "mcmmo.ability.mining.doubledrops");
    }

    /*
     * MCMMO.ABILITY.WOODCUTTING.*
     */

    public static boolean treeFeller(Player player) {
        return hasPermission(player, "mcmmo.ability.woodcutting.treefeller");
    }

    public static boolean leafBlower(Player player) {
        return hasPermission(player, "mcmmo.ability.woodcutting.leafblower");
    }

    public static boolean woodcuttingDoubleDrops(Player player) {
        return hasPermission(player, "mcmmo.ability.woodcutting.doubledrops");
    }

    /*
     * MCMMO.ABILITY.REPAIR.*
     */

    public static boolean repairBonus(Player player) {
        return hasPermission(player, "mcmmo.ability.repair.repairbonus");
    }

    public static boolean repairMastery(Player player) {
        return hasPermission(player, "mcmmo.ability.repair.repairmastery");
    }

    public static boolean arcaneForging(Player player) {
        return hasPermission(player, "mcmmo.ability.repair.arcaneforging");
    }

    public static boolean woodRepair(Player player) {
        return hasPermission(player, "mcmmo.ability.repair.woodrepair");
    }

    public static boolean stoneRepair(Player player) {
        return hasPermission(player, "mcmmo.ability.repair.stonerepair");
    }

    public static boolean leatherRepair(Player player) {
        return hasPermission(player, "mcmmo.ability.repair.leatherrepair");
    }

    public static boolean ironRepair(Player player) {
        return hasPermission(player, "mcmmo.ability.repair.ironrepair");
    }

    public static boolean goldRepair(Player player) {
        return hasPermission(player, "mcmmo.ability.repair.goldrepair");
    }

    public static boolean diamondRepair(Player player) {
        return hasPermission(player, "mcmmo.ability.repair.diamondrepair");
    }

    public static boolean armorRepair(Player player) {
        return hasPermission(player, "mcmmo.ability.repair.armorrepair");
    }

    public static boolean toolRepair(Player player) {
        return hasPermission(player, "mcmmo.ability.repair.toolrepair");
    }

    public static boolean otherMaterialRepair(Player player) {
        return hasPermission(player, "mcmmo.ability.repair.othermaterialrepair");
    }

    public static boolean otherRepair(Player player) {
        return hasPermission(player, "mcmmo.ability.repair.otherrepair");
    }

    public static boolean stringRepair(Player player) {
        return hasPermission(player, "mcmmo.ability.repair.stringrepair");
    }

    public static boolean salvage(Player player) {
        return hasPermission(player, "mcmmo.ability.repair.salvage");
    }


    /*
     * MCMMO.ABILITY.UNARMED.*
     */

    public static boolean unarmedBonus(Player player) {
        return hasPermission(player, "mcmmo.ability.unarmed.bonusdamage");
    }

    public static boolean disarm(Player player) {
        return hasPermission(player, "mcmmo.ability.unarmed.disarm");
    }

    public static boolean berserk(Player player) {
        return hasPermission(player, "mcmmo.ability.unarmed.berserk");
    }

    public static boolean deflect(Player player) {
        return hasPermission(player, "mcmmo.ability.unarmed.deflect");
    }

    public static boolean ironGrip(Player player) {
        return hasPermission(player, "mcmmo.ability.unarmed.irongrip");
    }

    /*
     * MCMMO.ABILITY.ARCHERY.*
     */

    public static boolean trackArrows(Player player) {
        return hasPermission(player, "mcmmo.ability.archery.trackarrows");
    }

    public static boolean daze(Player player) {
        return hasPermission(player, "mcmmo.ability.archery.daze");
    }

    public static boolean archeryBonus(Player player) {
        return hasPermission(player, "mcmmo.ability.archery.bonusdamage");
    }

    /*
     * MCMMO.ABILITY.HERBALISM.*
     */

    public static boolean herbalismDoubleDrops(Player player) {
        return hasPermission(player, "mcmmo.ability.herbalism.doubledrops");
    }

    public static boolean greenTerra(Player player) {
        return hasPermission(player, "mcmmo.ability.herbalism.greenterra");
    }

    public static boolean greenThumbBlocks(Player player) {
        return hasPermission(player, "mcmmo.ability.herbalism.greenthumbblocks");
    }

    public static boolean greenThumbCarrots(Player player) {
        return hasPermission(player, "mcmmo.ability.herbalism.greenthumbcarrots");
    }

    public static boolean greenThumbCocoa(Player player) {
        return hasPermission(player, "mcmmo.ability.herbalism.greenthumbcocoa");
    }

    public static boolean greenThumbNetherwart(Player player) {
        return hasPermission(player, "mcmmo.ability.herbalism.greenthumbnetherwart");
    }

    public static boolean greenThumbPotatoes(Player player) {
        return hasPermission(player, "mcmmo.ability.herbalism.greenthumbpotatoes");
    }

    public static boolean greenThumbWheat(Player player) {
        return hasPermission(player, "mcmmo.ability.herbalism.greenthumbwheat");
    }

    public static boolean farmersDiet(Player player) {
        return hasPermission(player, "mcmmo.ability.herbalism.farmersdiet");
    }

    /*
     * MCMMO.ABILITY.EXCAVATION.*
     */

    public static boolean gigaDrillBreaker(Player player) {
        return hasPermission(player, "mcmmo.ability.excavation.gigadrillbreaker");
    }

    public static boolean excavationTreasures(Player player) {
        return hasPermission(player, "mcmmo.ability.excavation.treasures");
    }

    /*
     * MCMMO.ABILITY.SWORDS.*
     */

    public static boolean swordsBleed(Player player) {
        return hasPermission(player, "mcmmo.ability.swords.bleed");
    }

    public static boolean serratedStrikes(Player player) {
        return hasPermission(player, "mcmmo.ability.swords.serratedstrikes");
    }

    public static boolean counterAttack(Player player) {
        return hasPermission(player, "mcmmo.ability.swords.counterattack");
    }

    /*
     * MCMMO.ABILITY.AXES.*
     */

    public static boolean skullSplitter(Player player) {
        return hasPermission(player, "mcmmo.ability.axes.skullsplitter");
    }

    public static boolean axeBonus(Player player) {
        return hasPermission(player, "mcmmo.ability.axes.bonusdamage");
    }

    public static boolean criticalHit(Player player) {
        return hasPermission(player, "mcmmo.ability.axes.criticalhit");
    }

    public static boolean impact(Player player) {
        return hasPermission(player, "mcmmo.ability.axes.impact");
    }

    public static boolean greaterImpact(Player player) {
        return hasPermission(player, "mcmmo.ability.axes.greaterimpact");
    }

    /*
     * MCMMO.ABILITY.ACROBATICS.*
     */

    public static boolean roll(Player player) {
        return hasPermission(player, "mcmmo.ability.acrobatics.roll");
    }

    public static boolean gracefulRoll(Player player) {
        return hasPermission(player, "mcmmo.ability.acrobatics.gracefulroll");
    }

    public static boolean dodge(Player player) {
        return hasPermission(player, "mcmmo.ability.acrobatics.dodge");
    }

    /*
     * MCMMO.ABILITY.BLASTMINING.*
     */

    public static boolean biggerBombs(Player player) {
        return hasPermission(player, "mcmmo.ability.blastmining.biggerbombs");
    }

    public static boolean demolitionsExpertise(Player player) {
        return hasPermission(player, "mcmmo.ability.blastmining.demolitionsexpertise");
    }

    public static boolean blastMining(Player player) {
        return hasPermission(player, "mcmmo.ability.blastmining.detonate");
    }

    /*
     * MCMMO.ITEM.*
     */

    public static boolean chimaeraWing(Player player) {
        return hasPermission(player, "mcmmo.item.chimaerawing");
    }

    /*
     * MCMMO.COMMANDS.*
     */

    public static boolean mcAbility(Player player) {
        return hasPermission(player, "mcmmo.commands.ability");
    }

    public static boolean partyTeleport(Player player) {
        return hasPermission(player, "mcmmo.commands.ptp");
    }

    public static boolean inspect(Player player) {
        return hasPermission(player, "mcmmo.commands.inspect");
    }

    public static boolean party(Player player) {
        return hasPermission(player, "mcmmo.commands.party");
    }

    public static boolean skillReset(Player player) {
        return hasPermission(player, "mcmmo.skillreset");
    }


    /*
     * MCMMO.CHAT.*
     */

    public static boolean partyChat(Player player) {
        return hasPermission(player, "mcmmo.chat.partychat");
    }

    public static boolean partyLock(Player player) {
        return hasPermission(player, "mcmmo.chat.partylock");
    }

    public static boolean adminChat(Player player) {
        return hasPermission(player, "mcmmo.chat.adminchat");
    }

    /*
     * MCMMO.SKILLS.*
     */

    public static boolean taming(Player player) {
        return hasPermission(player, "mcmmo.skills.taming");
    }

    public static boolean mining(Player player) {
        return hasPermission(player, "mcmmo.skills.mining");
    }

    public static boolean fishing(Player player) {
        return hasPermission(player, "mcmmo.skills.fishing");
    }

    public static boolean woodcutting(Player player) {
        return hasPermission(player, "mcmmo.skills.woodcutting");
    }

    public static boolean repair(Player player) {
        return hasPermission(player, "mcmmo.skills.repair");
    }

    public static boolean unarmed(Player player) {
        return hasPermission(player, "mcmmo.skills.unarmed");
    }

    public static boolean archery(Player player) {
        return hasPermission(player, "mcmmo.skills.archery");
    }

    public static boolean herbalism(Player player) {
        return hasPermission(player, "mcmmo.skills.herbalism");
    }

    public static boolean excavation(Player player) {
        return hasPermission(player, "mcmmo.skills.excavation");
    }

    public static boolean swords(Player player) {
        return hasPermission(player, "mcmmo.skills.swords");
    }

    public static boolean axes(Player player) {
        return hasPermission(player, "mcmmo.skills.axes");
    }

    public static boolean acrobatics(Player player) {
        return hasPermission(player, "mcmmo.skills.acrobatics");
    }
}
