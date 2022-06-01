package com.gmail.nossr50.util;

import com.gmail.nossr50.commands.party.PartySubcommandType;
import com.gmail.nossr50.datatypes.skills.ItemType;
import com.gmail.nossr50.datatypes.skills.MaterialType;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.datatypes.skills.subskills.AbstractSubSkill;
import com.gmail.nossr50.mcMMO;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.PluginManager;

import java.util.Locale;

public final class Permissions {
    private Permissions() {}

    /*
     * GENERAL
     */
    public static boolean motd(Permissible permissible) { return permissible.hasPermission("mcmmo.motd"); }
    public static boolean levelUpBroadcast(Permissible permissible) { return permissible.hasPermission("mcmmo.broadcast.levelup"); }
    public static boolean updateNotifications(Permissible permissible) {return permissible.hasPermission("mcmmo.tools.updatecheck"); }
    public static boolean chimaeraWing(Permissible permissible) { return permissible.hasPermission("mcmmo.item.chimaerawing"); }
    public static boolean showversion(Permissible permissible) { return permissible.hasPermission("mcmmo.showversion"); }

    /* BYPASS */
    public static boolean hardcoreBypass(Permissible permissible) { return permissible.hasPermission("mcmmo.bypass.hardcoremode"); }
    public static boolean arcaneBypass(Permissible permissible) { return permissible.hasPermission("mcmmo.bypass.arcanebypass"); }

    /* CHAT */
    public static boolean partyChat(Permissible permissible) { return permissible.hasPermission("mcmmo.chat.partychat"); }
    public static boolean adminChat(Permissible permissible) { return permissible.hasPermission("mcmmo.chat.adminchat"); }
    public static boolean colorChat(Permissible permissible) { return permissible.hasPermission("mcmmo.chat.colors"); }

    /*
     * COMMANDS
     */

    public static boolean mmoinfo(Permissible permissible) { return permissible.hasPermission("mcmmo.commands.mmoinfo"); }
    public static boolean addlevels(Permissible permissible) { return permissible.hasPermission("mcmmo.commands.addlevels"); }
    public static boolean addlevelsOthers(Permissible permissible) { return permissible.hasPermission("mcmmo.commands.addlevels.others"); }

    public static boolean addxp(Permissible permissible) { return permissible.hasPermission("mcmmo.commands.addxp"); }
    public static boolean addxpOthers(Permissible permissible) { return permissible.hasPermission("mcmmo.commands.addxp.others"); }

    public static boolean hardcoreModify(Permissible permissible) { return permissible.hasPermission("mcmmo.commands.hardcore.modify"); }
    public static boolean hardcoreToggle(Permissible permissible) { return permissible.hasPermission("mcmmo.commands.hardcore.toggle"); }

    public static boolean inspect(Permissible permissible) { return (permissible.hasPermission("mcmmo.commands.inspect")); }
    public static boolean inspectFar(Permissible permissible) { return (permissible.hasPermission("mcmmo.commands.inspect.far")); }
    public static boolean inspectHidden(Permissible permissible) { return (permissible.hasPermission("mcmmo.commands.inspect.hidden")); }

    public static boolean mcability(Permissible permissible) { return (permissible.hasPermission("mcmmo.commands.mcability")); }
    public static boolean mcabilityOthers(Permissible permissible) { return (permissible.hasPermission("mcmmo.commands.mcability.others")); }

    public static boolean adminChatSpy(Permissible permissible) { return permissible.hasPermission("mcmmo.commands.mcchatspy");}
    public static boolean adminChatSpyOthers(Permissible permissible) { return permissible.hasPermission("mcmmo.commands.mcchatspy.others");}

    public static boolean mcgod(Permissible permissible) { return permissible.hasPermission("mcmmo.commands.mcgod"); }
    public static boolean mcgodOthers(Permissible permissible) { return permissible.hasPermission("mcmmo.commands.mcgod.others"); }

    public static boolean mcmmoDescription(Permissible permissible) { return permissible.hasPermission("mcmmo.commands.mcmmo.description"); }
    public static boolean mcmmoHelp(Permissible permissible) { return permissible.hasPermission("mcmmo.commands.mcmmo.help"); }

    public static boolean mcrank(Permissible permissible) { return (permissible.hasPermission("mcmmo.commands.mcrank")); }
    public static boolean mcrankOthers(Permissible permissible) { return (permissible.hasPermission("mcmmo.commands.mcrank.others")); }
    public static boolean mcrankFar(Permissible permissible) { return (permissible.hasPermission("mcmmo.commands.mcrank.others.far")); }
    public static boolean mcrankOffline(Permissible permissible) { return (permissible.hasPermission("mcmmo.commands.mcrank.others.offline")); }

    public static boolean mcrefresh(Permissible permissible) { return (permissible.hasPermission("mcmmo.commands.mcrefresh")); }
    public static boolean mcrefreshOthers(Permissible permissible) { return (permissible.hasPermission("mcmmo.commands.mcrefresh.others")); }

    public static boolean mctop(Permissible permissible, PrimarySkillType skill) { return permissible.hasPermission("mcmmo.commands.mctop." + skill.toString().toLowerCase(Locale.ENGLISH)); }

    public static boolean mmoedit(Permissible permissible) { return permissible.hasPermission("mcmmo.commands.mmoedit"); }
    public static boolean mmoeditOthers(Permissible permissible) { return permissible.hasPermission("mcmmo.commands.mmoedit.others"); }

    public static boolean skillreset(Permissible permissible) { return permissible.hasPermission("mcmmo.commands.skillreset"); }
    public static boolean skillreset(Permissible permissible, PrimarySkillType skill) { return permissible.hasPermission("mcmmo.commands.skillreset." + skill.toString().toLowerCase(Locale.ENGLISH)); }
    public static boolean skillresetOthers(Permissible permissible) { return permissible.hasPermission("mcmmo.commands.skillreset.others"); }
    public static boolean skillresetOthers(Permissible permissible, PrimarySkillType skill) { return permissible.hasPermission("mcmmo.commands.skillreset.others." + skill.toString().toLowerCase(Locale.ENGLISH)); }

    public static boolean xplock(Permissible permissible, PrimarySkillType skill) { return permissible.hasPermission("mcmmo.commands.xplock." + skill.toString().toLowerCase(Locale.ENGLISH)); }

    public static boolean xprateSet(Permissible permissible) { return permissible.hasPermission("mcmmo.commands.xprate.set"); }
    public static boolean xprateReset(Permissible permissible) { return permissible.hasPermission("mcmmo.commands.xprate.reset"); }

    public static boolean mcpurge(Permissible permissible) { return permissible.hasPermission("mcmmo.commands.mcpurge"); }
    public static boolean mcremove(Permissible permissible) { return permissible.hasPermission("mcmmo.commands.mcremove"); }
    public static boolean mmoupdate(Permissible permissible) { return permissible.hasPermission("mcmmo.commands.mmoupdate"); }
    public static boolean reloadlocale(Permissible permissible) { return permissible.hasPermission("mcmmo.commands.reloadlocale"); }

    /*
     * PERKS
     */

    /* BYPASS PERKS */

    public static boolean hasRepairEnchantBypassPerk(Permissible permissible) { return permissible.hasPermission("mcmmo.perks.bypass.repairenchant"); }
    public static boolean hasSalvageEnchantBypassPerk(Permissible permissible) { return permissible.hasPermission("mcmmo.perks.bypass.salvageenchant"); }

    public static boolean lucky(Permissible permissible, PrimarySkillType skill) { return permissible.hasPermission("mcmmo.perks.lucky." + skill.toString().toLowerCase(Locale.ENGLISH)); }

    /* XP PERKS */
    public static boolean quadrupleXp(Permissible permissible, PrimarySkillType skill) { 
        return permissible.hasPermission("mcmmo.perks.xp.quadruple.all")
            || permissible.hasPermission("mcmmo.perks.xp.quadruple." + skill.toString().toLowerCase(Locale.ENGLISH)); 
    }
    
    public static boolean tripleXp(Permissible permissible, PrimarySkillType skill) { 
        return permissible.hasPermission("mcmmo.perks.xp.triple.all")
            || permissible.hasPermission("mcmmo.perks.xp.triple." + skill.toString().toLowerCase(Locale.ENGLISH)); 
    }
    
    public static boolean doubleAndOneHalfXp(Permissible permissible, PrimarySkillType skill) { 
        return permissible.hasPermission("mcmmo.perks.xp.150percentboost.all")
            || permissible.hasPermission("mcmmo.perks.xp.150percentboost." + skill.toString().toLowerCase(Locale.ENGLISH)); 
    }
    
    public static boolean doubleXp(Permissible permissible, PrimarySkillType skill) { 
        return permissible.hasPermission("mcmmo.perks.xp.double.all")
            || permissible.hasPermission("mcmmo.perks.xp.double." + skill.toString().toLowerCase(Locale.ENGLISH)); 
    }
    
    public static boolean oneAndOneHalfXp(Permissible permissible, PrimarySkillType skill) { 
        return permissible.hasPermission("mcmmo.perks.xp.50percentboost.all")
            || permissible.hasPermission("mcmmo.perks.xp.50percentboost." + skill.toString().toLowerCase(Locale.ENGLISH)); 
    }

    public static boolean oneAndAQuarterXp(Permissible permissible, PrimarySkillType skill) {
        return permissible.hasPermission("mcmmo.perks.xp.25percentboost.all")
                || permissible.hasPermission("mcmmo.perks.xp.25percentboost." + skill.toString().toLowerCase(Locale.ENGLISH));
    }

    public static boolean oneAndOneTenthXp(Permissible permissible, PrimarySkillType skill) { 
        return permissible.hasPermission("mcmmo.perks.xp.10percentboost.all")
            || permissible.hasPermission("mcmmo.perks.xp.10percentboost." + skill.toString().toLowerCase(Locale.ENGLISH)); 
    }

    public static boolean customXpBoost(Permissible permissible, PrimarySkillType skill) {
        return permissible.hasPermission("mcmmo.perks.xp.customboost.all")
            || permissible.hasPermission("mcmmo.perks.xp.customboost." + skill.toString().toLowerCase(Locale.ENGLISH));
    }


    /* ACTIVATION PERKS */
    public static boolean twelveSecondActivationBoost(Permissible permissible) { return permissible.hasPermission("mcmmo.perks.activationtime.twelveseconds"); }
    public static boolean eightSecondActivationBoost(Permissible permissible) { return permissible.hasPermission("mcmmo.perks.activationtime.eightseconds"); }
    public static boolean fourSecondActivationBoost(Permissible permissible) { return permissible.hasPermission("mcmmo.perks.activationtime.fourseconds"); }

    /* COOLDOWN PERKS */
    public static boolean halvedCooldowns(Permissible permissible) { return permissible.hasPermission("mcmmo.perks.cooldowns.halved"); }
    public static boolean thirdedCooldowns(Permissible permissible) { return permissible.hasPermission("mcmmo.perks.cooldowns.thirded"); }
    public static boolean quarteredCooldowns(Permissible permissible) { return permissible.hasPermission("mcmmo.perks.cooldowns.quartered"); }

    /*
     * SKILLS
     */

    public static boolean skillEnabled(Permissible permissible, PrimarySkillType skill) {return permissible.hasPermission("mcmmo.skills." + skill.toString().toLowerCase(Locale.ENGLISH)); }
    public static boolean vanillaXpBoost(Permissible permissible, PrimarySkillType skill) { return permissible.hasPermission("mcmmo.ability." + skill.toString().toLowerCase(Locale.ENGLISH) + ".vanillaxpboost"); }
    public static boolean isSubSkillEnabled(Permissible permissible, SubSkillType subSkillType) { return permissible.hasPermission(subSkillType.getPermissionNodeAddress()); }
    public static boolean isSubSkillEnabled(Permissible permissible, AbstractSubSkill abstractSubSkill) { return permissible.hasPermission(abstractSubSkill.getPermissionNode()); }

    /* ACROBATICS */
    public static boolean dodge(Permissible permissible) { return permissible.hasPermission("mcmmo.ability.acrobatics.dodge"); }
    public static boolean gracefulRoll(Permissible permissible) { return permissible.hasPermission("mcmmo.ability.acrobatics.gracefulroll"); }
    public static boolean roll(Permissible permissible) { return permissible.hasPermission("mcmmo.ability.acrobatics.roll"); }

    /* ALCHEMY */
    public static boolean catalysis(Permissible permissible) { return permissible.hasPermission("mcmmo.ability.alchemy.catalysis"); }
    public static boolean concoctions(Permissible permissible) { return permissible.hasPermission("mcmmo.ability.alchemy.concoctions"); }

    /* ARCHERY */
    public static boolean arrowRetrieval(Permissible permissible) { return permissible.hasPermission("mcmmo.ability.archery.trackarrows"); }
    public static boolean daze(Permissible permissible) { return permissible.hasPermission("mcmmo.ability.archery.daze"); }

    /* AXES */
    public static boolean skullSplitter(Permissible permissible) { return permissible.hasPermission("mcmmo.ability.axes.skullsplitter"); }

    /* EXCAVATION */
    public static boolean gigaDrillBreaker(Permissible permissible) { return permissible.hasPermission("mcmmo.ability.excavation.gigadrillbreaker"); }

    /* HERBALISM */
    public static boolean greenTerra(Permissible permissible) { return permissible.hasPermission("mcmmo.ability.herbalism.greenterra"); }
    public static boolean greenThumbBlock(Permissible permissible, Material material) { return permissible.hasPermission("mcmmo.ability.herbalism.greenthumb.blocks." + material.toString().replace("_", "").toLowerCase(Locale.ENGLISH)); }
    public static boolean greenThumbPlant(Permissible permissible, Material material) { return permissible.hasPermission("mcmmo.ability.herbalism.greenthumb.plants." + material.toString().replace("_", "").toLowerCase(Locale.ENGLISH)); }

    /* MINING */
    public static boolean biggerBombs(Permissible permissible) { return permissible.hasPermission("mcmmo.ability.mining.blastmining.biggerbombs"); }
    public static boolean demolitionsExpertise(Permissible permissible) { return permissible.hasPermission("mcmmo.ability.mining.blastmining.demolitionsexpertise"); }
    public static boolean remoteDetonation(Permissible permissible) { return permissible.hasPermission("mcmmo.ability.mining.blastmining.detonate"); }
    public static boolean superBreaker(Permissible permissible) { return permissible.hasPermission("mcmmo.ability.mining.superbreaker"); }

    /* REPAIR */
    public static boolean repairItemType(Permissible permissible, ItemType repairItemType) { return permissible.hasPermission("mcmmo.ability.repair." + repairItemType.toString().toLowerCase(Locale.ENGLISH) + "repair"); }
    public static boolean repairMaterialType(Permissible permissible, MaterialType repairMaterialType) { return permissible.hasPermission("mcmmo.ability.repair." + repairMaterialType.toString().toLowerCase(Locale.ENGLISH) + "repair"); }

    /* SALVAGE */
    public static boolean arcaneSalvage(Permissible permissible) { return permissible.hasPermission("mcmmo.ability.salvage.arcanesalvage"); }

    public static boolean salvageItemType(Permissible permissible, ItemType salvageItemType) { return permissible.hasPermission("mcmmo.ability.salvage." + salvageItemType.toString().toLowerCase(Locale.ENGLISH) + "salvage"); }
    public static boolean salvageMaterialType(Permissible permissible, MaterialType salvageMaterialType) { return permissible.hasPermission("mcmmo.ability.salvage." + salvageMaterialType.toString().toLowerCase(Locale.ENGLISH) + "salvage"); }

    /* SMELTING */
    public static boolean fluxMining(Permissible permissible) { return permissible.hasPermission("mcmmo.ability.smelting.fluxmining"); }
    public static boolean fuelEfficiency(Permissible permissible) { return permissible.hasPermission("mcmmo.ability.smelting.fuelefficiency"); }

    /* SWORDS */
    public static boolean serratedStrikes(Permissible permissible) { return permissible.hasPermission("mcmmo.ability.swords.serratedstrikes"); }

    /* TAMING */
    public static boolean callOfTheWild(Permissible permissible, EntityType type) { return permissible.hasPermission("mcmmo.ability.taming.callofthewild." + type.toString().toLowerCase(Locale.ENGLISH)); }

    /* UNARMED */
    public static boolean berserk(Permissible permissible) { return permissible.hasPermission("mcmmo.ability.unarmed.berserk"); }

    /* WOODCUTTING */
    public static boolean treeFeller(Permissible permissible) { return permissible.hasPermission("mcmmo.ability.woodcutting.treefeller"); }

    /*
     * PARTY
     */
    public static boolean partySizeBypass(Permissible permissible) { return permissible.hasPermission("mcmmo.bypass.partylimit" ); }
    public static boolean party(Permissible permissible) { return permissible.hasPermission("mcmmo.commands.party"); }
    public static boolean partySubcommand(Permissible permissible, PartySubcommandType subcommand) { return permissible.hasPermission("mcmmo.commands.party." + subcommand.toString().toLowerCase(Locale.ENGLISH)); }
    public static boolean friendlyFire(Permissible permissible) { return permissible.hasPermission("mcmmo.party.friendlyfire"); }

    /* TELEPORT */
    public static boolean partyTeleportSend(Permissible permissible) { return permissible.hasPermission("mcmmo.commands.ptp.send"); }
    public static boolean partyTeleportAccept(Permissible permissible) { return permissible.hasPermission("mcmmo.commands.ptp.accept"); }
    public static boolean partyTeleportAcceptAll(Permissible permissible) { return permissible.hasPermission("mcmmo.commands.ptp.acceptall"); }
    public static boolean partyTeleportToggle(Permissible permissible) { return permissible.hasPermission("mcmmo.commands.ptp.toggle"); }

    public static boolean partyTeleportAllWorlds(Permissible permissible) { return permissible.hasPermission("mcmmo.commands.ptp.world.all"); }
    public static boolean partyTeleportWorld(Permissible permissible, World world) { return permissible.hasPermission("mcmmo.commands.ptp.world." + world.getName()); }

    public static void generateWorldTeleportPermissions() {
        Server server = mcMMO.p.getServer();
        PluginManager pluginManager = server.getPluginManager();

        for (World world : server.getWorlds()) {
            addDynamicPermission("mcmmo.commands.ptp.world." + world.getName(), PermissionDefault.OP, pluginManager);
        }
    }

    private static void addDynamicPermission(String permissionName, PermissionDefault permissionDefault, PluginManager pluginManager) {
        Permission permission = new Permission(permissionName);
        permission.setDefault(permissionDefault);
        pluginManager.addPermission(permission);
    }
}
