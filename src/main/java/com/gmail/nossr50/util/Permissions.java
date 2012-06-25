package com.gmail.nossr50.util;

import org.bukkit.entity.Player;

public class Permissions {
    private static volatile Permissions instance;

    public boolean permission(Player player, String perm) {
        return player.hasPermission(perm);
    }

    public static Permissions getInstance() {
        if (instance == null) {
            instance = new Permissions();
        }

        return instance;
    }

    /*
     * GENERIC PERMISSIONS
     */

    public boolean motd(Player player) {
        return player.hasPermission("mcmmo.motd");
    }

    public boolean admin(Player player) {
        return player.hasPermission("mcmmo.admin");
    }

    /*
     * MCMMO.BYPASS.*
     */
    public boolean hardcoremodeBypass(Player player) {
        return player.hasPermission("mcmmo.bypass.hardcoremode");
    }

    public boolean arcaneBypass(Player player) {
        return player.hasPermission("mcmmo.bypass.arcanebypass");
    }

    public boolean inspectDistanceBypass(Player player) {
        return player.hasPermission("mcmmo.bypass.inspect.distance");
    }

    public boolean inspectOfflineBypass(Player player) {
        return player.hasPermission("mcmmo.bypass.inspect.offline");
    }

    /*
     * MCMMO.TOOLS.*
     */

    public boolean mcrefresh(Player player) {
        return player.hasPermission("mcmmo.tools.mcrefresh");
    }

    public boolean mcremove(Player player) {
        return player.hasPermission("mcmmo.tools.mcremove");
    }

    public boolean mmoedit(Player player) {
        return player.hasPermission("mcmmo.tools.mmoedit");
    }

    public boolean mcgod(Player player) {
        return player.hasPermission("mcmmo.tools.mcgod");
    }

    /*
     * MCMMO.ABILITY.TAMING.*
     */

    public boolean fastFoodService(Player player) {
        return player.hasPermission("mcmmo.ability.taming.fastfoodservice");
    }

    public boolean sharpenedClaws(Player player) {
        return player.hasPermission("mcmmo.ability.taming.sharpenedclaws");
    }

    public boolean gore(Player player) {
        return player.hasPermission("mcmmo.ability.taming.gore");
    }

    public boolean callOfTheWild(Player player) {
        return player.hasPermission("mcmmo.ability.taming.callofthewild");
    }

    public boolean environmentallyAware(Player player) {
        return player.hasPermission("mcmmo.ability.taming.environmentallyaware");
    }

    public boolean thickFur(Player player) {
        return player.hasPermission("mcmmo.ability.taming.thickfur");
    }

    public boolean shockProof(Player player) {
        return player.hasPermission("mcmmo.ability.taming.shockproof");
    }

    public boolean beastLore(Player player) {
        return player.hasPermission("mcmmo.ability.taming.beastlore");
    }

    /*
     * MCMMO.ABILITY.FISHING.*
     */

    public boolean shakeMob(Player player) {
        return player.hasPermission("mcmmo.ability.fishing.shakemob");
    }

    public boolean fishingTreasures(Player player) {
        return player.hasPermission("mcmmo.ability.fishing.treasures");
    }

    public boolean fishingMagic(Player player) {
        return player.hasPermission("mcmmo.ability.fishing.magic");
    }

    /*
     * MCMMO.ABILITY.MINING.*
     */

    public boolean superBreaker(Player player) {
        return player.hasPermission("mcmmo.ability.mining.superbreaker");
    }

    public boolean miningDoubleDrops(Player player) {
        return player.hasPermission("mcmmo.ability.mining.doubledrops");
    }

    /*
     * MCMMO.ABILITY.WOODCUTTING.*
     */

    public boolean treeFeller(Player player) {
        return player.hasPermission("mcmmo.ability.woodcutting.treefeller");
    }

    public boolean leafBlower(Player player) {
        return player.hasPermission("mcmmo.ability.woodcutting.leafblower");
    }

    public boolean woodcuttingDoubleDrops(Player player) {
        return player.hasPermission("mcmmo.ability.woodcutting.doubledrops");
    }

    /*
     * MCMMO.ABILITY.REPAIR.*
     */

    public boolean repairBonus(Player player) {
        return player.hasPermission("mcmmo.ability.repair.repairbonus");
    }

    public boolean repairMastery(Player player) {
        return player.hasPermission("mcmmo.ability.repair.repairmastery");
    }

    public boolean arcaneForging(Player player) {
        return player.hasPermission("mcmmo.ability.repair.arcaneforging");
    }

    public boolean woodRepair(Player player) {
        return player.hasPermission("mcmmo.ability.repair.woodrepair");
    }

    public boolean stoneRepair(Player player) {
        return player.hasPermission("mcmmo.ability.repair.stonerepair");
    }

    public boolean leatherRepair(Player player) {
        return player.hasPermission("mcmmo.ability.repair.leatherrepair");
    }

    public boolean ironRepair(Player player) {
        return player.hasPermission("mcmmo.ability.repair.ironrepair");
    }

    public boolean goldRepair(Player player) {
        return player.hasPermission("mcmmo.ability.repair.goldrepair");
    }

    public boolean diamondRepair(Player player) {
        return player.hasPermission("mcmmo.ability.repair.diamondrepair");
    }

    public boolean armorRepair(Player player) {
        return player.hasPermission("mcmmo.ability.repair.armorrepair");
    }

    public boolean toolRepair(Player player) {
        return player.hasPermission("mcmmo.ability.repair.toolrepair");
    }

    public boolean otherMaterialRepair(Player player) {
        return player.hasPermission("mcmmo.ability.repair.othermaterialrepair");
    }

    public boolean otherRepair(Player player) {
        return player.hasPermission("mcmmo.ability.repair.otherrepair");
    }

    public boolean stringRepair(Player player) {
        return player.hasPermission("mcmmo.ability.repair.stringrepair");
    }

    /*
     * MCMMO.ABILITY.UNARMED.*
     */

    public boolean unarmedBonus(Player player) {
        return player.hasPermission("mcmmo.ability.unarmed.bonusdamage");
    }

    public boolean disarm(Player player) {
        return player.hasPermission("mcmmo.ability.unarmed.disarm");
    }

    public boolean berserk(Player player) {
        return player.hasPermission("mcmmo.ability.unarmed.berserk");
    }

    public boolean deflect(Player player) {
        return player.hasPermission("mcmmo.ability.unarmed.deflect");
    }

    public boolean ironGrip(Player player) {
        return player.hasPermission("mcmmo.ability.unarmed.irongrip");
    }

    /*
     * MCMMO.ABILITY.ARCHERY.*
     */

    public boolean trackArrows(Player player) {
        return player.hasPermission("mcmmo.ability.archery.trackarrows");
    }

    public boolean daze(Player player) {
        return player.hasPermission("mcmmo.ability.archery.daze");
    }

    public boolean archeryBonus(Player player) {
        return player.hasPermission("mcmmo.ability.archery.bonusdamage");
    }

    /*
     * MCMMO.ABILITY.HERBALISM.*
     */

    public boolean herbalismDoubleDrops(Player player) {
        return player.hasPermission("mcmmo.ability.herbalism.doubledrops");
    }

    public boolean greenTerra(Player player) {
        return player.hasPermission("mcmmo.ability.herbalism.greenterra");
    }

    public boolean greenThumbBlocks(Player player) {
        return player.hasPermission("mcmmo.ability.herbalism.greenthumbblocks");
    }

    public boolean greenThumbWheat(Player player) {
        return player.hasPermission("mcmmo.ability.herbalism.greenthumbwheat");
    }

    public boolean farmersDiet(Player player) {
        return player.hasPermission("mcmmo.ability.herbalism.farmersdiet");
    }

    /*
     * MCMMO.ABILITY.EXCAVATION.*
     */

    public boolean gigaDrillBreaker(Player player) {
        return player.hasPermission("mcmmo.ability.excavation.gigadrillbreaker");
    }

    public boolean excavationTreasures(Player player) {
        return player.hasPermission("mcmmo.ability.excavation.treasures");
    }

    /*
     * MCMMO.ABILITY.SWORDS.*
     */

    public boolean swordsBleed(Player player) {
        return player.hasPermission("mcmmo.ability.swords.bleed");
    }

    public boolean serratedStrikes(Player player) {
        return player.hasPermission("mcmmo.ability.swords.serratedstrikes");
    }

    public boolean counterAttack(Player player) {
        return player.hasPermission("mcmmo.ability.swords.counterattack");
    }

    /*
     * MCMMO.ABILITY.AXES.*
     */

    public boolean skullSplitter(Player player) {
        return player.hasPermission("mcmmo.ability.axes.skullsplitter");
    }

    public boolean axeBonus(Player player) {
        return player.hasPermission("mcmmo.ability.axes.bonusdamage");
    }

    public boolean criticalHit(Player player) {
        return player.hasPermission("mcmmo.ability.axes.criticalhit");
    }

    public boolean impact(Player player) {
        return player.hasPermission("mcmmo.ability.axes.impact");
    }

    public boolean greaterImpact(Player player) {
        return player.hasPermission("mcmmo.ability.axes.greaterimpact");
    }

    /*
     * MCMMO.ABILITY.ACROBATICS.*
     */

    public boolean roll(Player player) {
        return player.hasPermission("mcmmo.ability.acrobatics.roll");
    }

    public boolean gracefulRoll(Player player) {
        return player.hasPermission("mcmmo.ability.acrobatics.gracefulroll");
    }

    public boolean dodge(Player player) {
        return player.hasPermission("mcmmo.ability.acrobatics.dodge");
    }

    /*
     * MCMMO.ABILITY.BLASTMINING.*
     */

    public boolean biggerBombs(Player player) {
        return player.hasPermission("mcmmo.ability.blastmining.biggerbombs");
    }

    public boolean demolitionsExpertise(Player player) {
        return player.hasPermission("mcmmo.ability.blastmining.demolitionsexpertise");
    }

    public boolean blastMining(Player player) {
        return player.hasPermission("mcmmo.ability.blastmining.detonate");
    }

    /*
     * MCMMO.ITEM.*
     */

    public boolean chimaeraWing(Player player) {
        return player.hasPermission("mcmmo.item.chimaerawing");
    }

    /*
     * MCMMO.COMMANDS.*
     */

    public boolean mcAbility(Player player) {
        return player.hasPermission("mcmmo.commands.ability");
    }

    public boolean partyTeleport(Player player) {
        return player.hasPermission("mcmmo.commands.ptp");
    }

    public boolean inspect(Player player) {
        return player.hasPermission("mcmmo.commands.inspect");
    }

    public boolean party(Player player) {
        return player.hasPermission("mcmmo.commands.party");
    }

    /*
     * MCMMO.CHAT.*
     */

    public boolean partyChat(Player player) {
        return player.hasPermission("mcmmo.chat.partychat");
    }

    public boolean partyLock(Player player) {
        return player.hasPermission("mcmmo.chat.partylock");
    }

    public boolean adminChat(Player player) {
        return player.hasPermission("mcmmo.chat.adminchat");
    }

    /*
     * MCMMO.SKILLS.*
     */

    public boolean taming(Player player) {
        return player.hasPermission("mcmmo.skills.taming");
    }

    public boolean mining(Player player) {
        return player.hasPermission("mcmmo.skills.mining");
    }

    public boolean fishing(Player player) {
        return player.hasPermission("mcmmo.skills.fishing");
    }

    public boolean woodcutting(Player player) {
        return player.hasPermission("mcmmo.skills.woodcutting");
    }

    public boolean repair(Player player) {
        return player.hasPermission("mcmmo.skills.repair");
    }

    public boolean unarmed(Player player) {
        return player.hasPermission("mcmmo.skills.unarmed");
    }

    public boolean archery(Player player) {
        return player.hasPermission("mcmmo.skills.archery");
    }

    public boolean herbalism(Player player) {
        return player.hasPermission("mcmmo.skills.herbalism");
    }

    public boolean excavation(Player player) {
        return player.hasPermission("mcmmo.skills.excavation");
    }

    public boolean swords(Player player) {
        return player.hasPermission("mcmmo.skills.swords");
    }

    public boolean axes(Player player) {
        return player.hasPermission("mcmmo.skills.axes");
    }

    public boolean acrobatics(Player player) {
        return player.hasPermission("mcmmo.skills.acrobatics");
    }
}
