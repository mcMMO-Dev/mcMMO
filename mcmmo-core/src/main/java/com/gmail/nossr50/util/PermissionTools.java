package com.gmail.nossr50.util;

import com.gmail.nossr50.commands.party.PartySubcommandType;
import com.gmail.nossr50.datatypes.experience.CustomXPPerk;
import com.gmail.nossr50.datatypes.skills.ItemMaterialCategory;
import com.gmail.nossr50.datatypes.skills.ItemType;
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
import org.bukkit.plugin.SimplePluginManager;

public final class PermissionTools {
    private final mcMMO pluginRef;

    public PermissionTools(mcMMO pluginRef) {
        this.pluginRef = pluginRef;
    }

    /*
     * GENERAL
     */
    public boolean motd(Permissible permissible) {
        return permissible.hasPermission("mcmmo.motd");
    }

    public boolean mobHealthDisplay(Permissible permissible) {
        return permissible.hasPermission("mcmmo.mobhealthdisplay");
    }

    public boolean updateNotifications(Permissible permissible) {
        return permissible.hasPermission("mcmmo.tools.updatecheck");
    }

    public boolean chimaeraWing(Permissible permissible) {
        return permissible.hasPermission("mcmmo.item.chimaerawing");
    }

    public boolean showversion(Permissible permissible) {
        return permissible.hasPermission("mcmmo.showversion");
    }

    /* BYPASS */
    public boolean hardcoreBypass(Permissible permissible) {
        return permissible.hasPermission("mcmmo.bypass.hardcoremode");
    }

    public boolean arcaneBypass(Permissible permissible) {
        return permissible.hasPermission("mcmmo.bypass.arcanebypass");
    }

    public boolean trapsBypass(Permissible permissible) {
        return permissible.hasPermission("mcmmo.bypass.fishingtraps");
    }

    /* CHAT */
    public boolean partyChat(Permissible permissible) {
        return permissible.hasPermission("mcmmo.chat.partychat");
    }

    public boolean adminChat(Permissible permissible) {
        return permissible.hasPermission("mcmmo.chat.adminchat");
    }

    /*
     * COMMANDS
     */

    public boolean mmoinfo(Permissible permissible) {
        return permissible.hasPermission("mcmmo.commands.mmoinfo");
    }

    public boolean addlevels(Permissible permissible) {
        return permissible.hasPermission("mcmmo.commands.addlevels");
    }

    public boolean addlevelsOthers(Permissible permissible) {
        return permissible.hasPermission("mcmmo.commands.addlevels.others");
    }

    public boolean addxp(Permissible permissible) {
        return permissible.hasPermission("mcmmo.commands.addxp");
    }

    public boolean addxpOthers(Permissible permissible) {
        return permissible.hasPermission("mcmmo.commands.addxp.others");
    }

    public boolean hardcoreModify(Permissible permissible) {
        return permissible.hasPermission("mcmmo.commands.hardcore.modify");
    }

    public boolean hardcoreToggle(Permissible permissible) {
        return permissible.hasPermission("mcmmo.commands.hardcore.toggle");
    }

    public boolean inspect(Permissible permissible) {
        return (permissible.hasPermission("mcmmo.commands.inspect"));
    }

    public boolean inspectFar(Permissible permissible) {
        return (permissible.hasPermission("mcmmo.commands.inspect.far"));
    }

    public boolean inspectHidden(Permissible permissible) {
        return (permissible.hasPermission("mcmmo.commands.inspect.hidden"));
    }

    public boolean inspectOffline(Permissible permissible) {
        return (permissible.hasPermission("mcmmo.commands.inspect.offline"));
    }

    public boolean mcability(Permissible permissible) {
        return (permissible.hasPermission("mcmmo.commands.mcability"));
    }

    public boolean mcabilityOthers(Permissible permissible) {
        return (permissible.hasPermission("mcmmo.commands.mcability.others"));
    }

    public boolean adminChatSpy(Permissible permissible) {
        return permissible.hasPermission("mcmmo.commands.mcchatspy");
    }

    public boolean adminChatSpyOthers(Permissible permissible) {
        return permissible.hasPermission("mcmmo.commands.mcchatspy.others");
    }

    public boolean mcgod(Permissible permissible) {
        return permissible.hasPermission("mcmmo.commands.mcgod");
    }

    public boolean mcgodOthers(Permissible permissible) {
        return permissible.hasPermission("mcmmo.commands.mcgod.others");
    }

    public boolean mcmmoDescription(Permissible permissible) {
        return permissible.hasPermission("mcmmo.commands.mcmmo.description");
    }

    public boolean mcmmoHelp(Permissible permissible) {
        return permissible.hasPermission("mcmmo.commands.mcmmo.help");
    }

    public boolean mcrank(Permissible permissible) {
        return (permissible.hasPermission("mcmmo.commands.mcrank"));
    }

    public boolean mcrankOthers(Permissible permissible) {
        return (permissible.hasPermission("mcmmo.commands.mcrank.others"));
    }

    public boolean mcrankFar(Permissible permissible) {
        return (permissible.hasPermission("mcmmo.commands.mcrank.others.far"));
    }

    public boolean mcrankOffline(Permissible permissible) {
        return (permissible.hasPermission("mcmmo.commands.mcrank.others.offline"));
    }

    public boolean mcrefresh(Permissible permissible) {
        return (permissible.hasPermission("mcmmo.commands.mcrefresh"));
    }

    public boolean mcrefreshOthers(Permissible permissible) {
        return (permissible.hasPermission("mcmmo.commands.mcrefresh.others"));
    }

    public boolean mctop(Permissible permissible, PrimarySkillType skill) {
        return permissible.hasPermission("mcmmo.commands.mctop." + skill.toString().toLowerCase());
    }

    public boolean mmoedit(Permissible permissible) {
        return permissible.hasPermission("mcmmo.commands.mmoedit");
    }

    public boolean reload(Permissible permissible) {
        return permissible.hasPermission("mcmmo.commands.reload");
    }

    public boolean mmoeditOthers(Permissible permissible) {
        return permissible.hasPermission("mcmmo.commands.mmoedit.others");
    }

    public boolean skillreset(Permissible permissible) {
        return permissible.hasPermission("mcmmo.commands.skillreset");
    }

    public boolean skillreset(Permissible permissible, PrimarySkillType skill) {
        return permissible.hasPermission("mcmmo.commands.skillreset." + skill.toString().toLowerCase());
    }

    public boolean skillresetOthers(Permissible permissible) {
        return permissible.hasPermission("mcmmo.commands.skillreset.others");
    }

    public boolean skillresetOthers(Permissible permissible, PrimarySkillType skill) {
        return permissible.hasPermission("mcmmo.commands.skillreset.others." + skill.toString().toLowerCase());
    }

    public boolean xplock(Permissible permissible, PrimarySkillType skill) {
        return permissible.hasPermission("mcmmo.commands.xplock." + skill.toString().toLowerCase());
    }

    public boolean xprateSet(Permissible permissible) {
        return permissible.hasPermission("mcmmo.commands.xprate.set");
    }

    public boolean xprateReset(Permissible permissible) {
        return permissible.hasPermission("mcmmo.commands.xprate.reset");
    }

    public boolean vampirismModify(Permissible permissible) {
        return permissible.hasPermission("mcmmo.commands.vampirism.modify");
    }

    public boolean vampirismToggle(Permissible permissible) {
        return permissible.hasPermission("mcmmo.commands.vampirism.toggle");
    }

    public boolean mcpurge(Permissible permissible) {
        return permissible.hasPermission("mcmmo.commands.mcpurge");
    }

    public boolean mcremove(Permissible permissible) {
        return permissible.hasPermission("mcmmo.commands.mcremove");
    }

    public boolean mmoupdate(Permissible permissible) {
        return permissible.hasPermission("mcmmo.commands.mmoupdate");
    }

    public boolean reloadlocale(Permissible permissible) {
        return permissible.hasPermission("mcmmo.commands.reloadlocale");
    }

    /*
     * PERKS
     */

    /* BYPASS PERKS */

    public boolean hasRepairEnchantBypassPerk(Permissible permissible) {
        return permissible.hasPermission("mcmmo.perks.bypass.repairenchant");
    }

    public boolean hasSalvageEnchantBypassPerk(Permissible permissible) {
        return permissible.hasPermission("mcmmo.perks.bypass.salvageenchant");
    }

    public boolean lucky(Permissible permissible, PrimarySkillType skill) {
        return permissible.hasPermission("mcmmo.perks.lucky." + skill.toString().toLowerCase());
    }

    /* XP PERKS */
    public boolean quadrupleXp(Permissible permissible, PrimarySkillType skill) {
        return permissible.hasPermission("mcmmo.perks.xp.quadruple." + skill.toString().toLowerCase());
    }

    public boolean tripleXp(Permissible permissible, PrimarySkillType skill) {
        return permissible.hasPermission("mcmmo.perks.xp.triple." + skill.toString().toLowerCase());
    }

    public boolean doubleAndOneHalfXp(Permissible permissible, PrimarySkillType skill) {
        return permissible.hasPermission("mcmmo.perks.xp.150percentboost." + skill.toString().toLowerCase());
    }

    public boolean doubleXp(Permissible permissible, PrimarySkillType skill) {
        return permissible.hasPermission("mcmmo.perks.xp.double." + skill.toString().toLowerCase());
    }

    public boolean oneAndOneHalfXp(Permissible permissible, PrimarySkillType skill) {
        return permissible.hasPermission("mcmmo.perks.xp.50percentboost." + skill.toString().toLowerCase());
    }

    public boolean oneAndOneTenthXp(Permissible permissible, PrimarySkillType skill) {
        return permissible.hasPermission("mcmmo.perks.xp.10percentboost." + skill.toString().toLowerCase());
    }

    public boolean hasCustomXPPerk(Permissible permissible, CustomXPPerk customXPPerk) {
        return permissible.hasPermission(customXPPerk.getPerkPermissionAddress());
    }

    /* ACTIVATION PERKS */
    public boolean twelveSecondActivationBoost(Permissible permissible) {
        return permissible.hasPermission("mcmmo.perks.activationtime.twelveseconds");
    }

    public boolean eightSecondActivationBoost(Permissible permissible) {
        return permissible.hasPermission("mcmmo.perks.activationtime.eightseconds");
    }

    public boolean fourSecondActivationBoost(Permissible permissible) {
        return permissible.hasPermission("mcmmo.perks.activationtime.fourseconds");
    }

    /* COOLDOWN PERKS */
    public boolean halvedCooldowns(Permissible permissible) {
        return permissible.hasPermission("mcmmo.perks.cooldowns.halved");
    }

    public boolean thirdedCooldowns(Permissible permissible) {
        return permissible.hasPermission("mcmmo.perks.cooldowns.thirded");
    }

    public boolean quarteredCooldowns(Permissible permissible) {
        return permissible.hasPermission("mcmmo.perks.cooldowns.quartered");
    }

    /*
     * SKILLS
     */

    public boolean skillEnabled(Permissible permissible, PrimarySkillType skill) {
        return permissible.hasPermission("mcmmo.skills." + skill.toString().toLowerCase());
    }

    public boolean vanillaXpBoost(Permissible permissible, PrimarySkillType skill) {
        return permissible.hasPermission("mcmmo.ability." + skill.toString().toLowerCase() + ".vanillaxpboost");
    }

    public boolean isSubSkillEnabled(Permissible permissible, SubSkillType subSkillType) {
        return permissible.hasPermission(subSkillType.getPermissionNodeAddress(pluginRef));
    }

    public boolean isSubSkillEnabled(Permissible permissible, AbstractSubSkill abstractSubSkill) {
        return permissible.hasPermission(abstractSubSkill.getPermissionNode());
    }

    public boolean bonusDamage(Permissible permissible, PrimarySkillType skill) {
        return permissible.hasPermission("mcmmo.ability." + skill.toString().toLowerCase() + ".bonusdamage");
    }

    /* ACROBATICS */
    public boolean dodge(Permissible permissible) {
        return permissible.hasPermission("mcmmo.ability.acrobatics.dodge");
    }

    public boolean gracefulRoll(Permissible permissible) {
        return permissible.hasPermission("mcmmo.ability.acrobatics.gracefulroll");
    }

    public boolean roll(Permissible permissible) {
        return permissible.hasPermission("mcmmo.ability.acrobatics.roll");
    }

    /* ALCHEMY */
    public boolean catalysis(Permissible permissible) {
        return permissible.hasPermission("mcmmo.ability.alchemy.catalysis");
    }

    public boolean concoctions(Permissible permissible) {
        return permissible.hasPermission("mcmmo.ability.alchemy.concoctions");
    }

    /* ARCHERY */
    public boolean arrowRetrieval(Permissible permissible) {
        return permissible.hasPermission("mcmmo.ability.archery.trackarrows");
    }

    public boolean daze(Permissible permissible) {
        return permissible.hasPermission("mcmmo.ability.archery.daze");
    }

    /* AXES */
    public boolean skullSplitter(Permissible permissible) {
        return permissible.hasPermission("mcmmo.ability.axes.skullsplitter");
    }

    /* EXCAVATION */
    public boolean gigaDrillBreaker(Permissible permissible) {
        return permissible.hasPermission("mcmmo.ability.excavation.gigadrillbreaker");
    }

    /* HERBALISM */
    public boolean greenTerra(Permissible permissible) {
        return permissible.hasPermission("mcmmo.ability.herbalism.greenterra");
    }

    public boolean greenThumbBlock(Permissible permissible, Material material) {
        return permissible.hasPermission("mcmmo.ability.herbalism.greenthumb.blocks." + material.toString().replace("_", "").toLowerCase());
    }

    public boolean greenThumbPlant(Permissible permissible, Material material) {
        return permissible.hasPermission("mcmmo.ability.herbalism.greenthumb.plants." + material.toString().replace("_", "").toLowerCase());
    }

    /* MINING */
    public boolean biggerBombs(Permissible permissible) {
        return permissible.hasPermission("mcmmo.ability.SuperAbility.BlastMining.biggerbombs");
    }

    public boolean demolitionsExpertise(Permissible permissible) {
        return permissible.hasPermission("mcmmo.ability.SuperAbility.BlastMining.demolitionsexpertise");
    }

    public boolean remoteDetonation(Permissible permissible) {
        return permissible.hasPermission("mcmmo.ability.SuperAbility.BlastMining.detonate");
    }

    public boolean superBreaker(Permissible permissible) {
        return permissible.hasPermission("mcmmo.ability.mining.superbreaker");
    }

    /* REPAIR */
    public boolean repairItemType(Permissible permissible, ItemType repairItemType) {
        return permissible.hasPermission("mcmmo.ability.repair." + repairItemType.toString().toLowerCase() + "repair");
    }

    public boolean repairMaterialType(Permissible permissible, ItemMaterialCategory repairItemMaterialCategory) {
        return permissible.hasPermission("mcmmo.ability.repair." + repairItemMaterialCategory.toString().toLowerCase() + "repair");
    }

    /* SALVAGE */
    public boolean advancedSalvage(Permissible permissible) {
        return permissible.hasPermission("mcmmo.ability.salvage.advancedsalvage");
    }

    public boolean arcaneSalvage(Permissible permissible) {
        return permissible.hasPermission("mcmmo.ability.salvage.arcanesalvage");
    }

    public boolean salvageItemType(Permissible permissible, ItemType salvageItemType) {
        return permissible.hasPermission("mcmmo.ability.salvage." + salvageItemType.toString().toLowerCase() + "salvage");
    }

    public boolean salvageMaterialType(Permissible permissible, ItemMaterialCategory salvageItemMaterialCategory) {
        return permissible.hasPermission("mcmmo.ability.salvage." + salvageItemMaterialCategory.toString().toLowerCase() + "salvage");
    }

    /* SMELTING */
    public boolean fluxMining(Permissible permissible) {
        return permissible.hasPermission("mcmmo.ability.smelting.fluxmining");
    }

    public boolean fuelEfficiency(Permissible permissible) {
        return permissible.hasPermission("mcmmo.ability.smelting.fuelefficiency");
    }

    /* SWORDS */
    public boolean serratedStrikes(Permissible permissible) {
        return permissible.hasPermission("mcmmo.ability.swords.serratedstrikes");
    }

    /* TAMING */
    public boolean callOfTheWild(Permissible permissible, EntityType type) {
        return permissible.hasPermission("mcmmo.ability.taming.callofthewild." + type.toString().toLowerCase());
    }

    /* UNARMED */
    public boolean berserk(Permissible permissible) {
        return permissible.hasPermission("mcmmo.ability.unarmed.berserk");
    }

    /* WOODCUTTING */
    public boolean treeFeller(Permissible permissible) {
        return permissible.hasPermission("mcmmo.ability.woodcutting.treefeller");
    }

    /*
     * PARTY
     */
    public boolean partySizeBypass(Permissible permissible) {
        return permissible.hasPermission("mcmmo.bypass.partylimit");
    }

    public boolean party(Permissible permissible) {
        return permissible.hasPermission("mcmmo.commands.party");
    }

    public boolean partySubcommand(Permissible permissible, PartySubcommandType subcommand) {
        return permissible.hasPermission("mcmmo.commands.party." + subcommand.toString().toLowerCase());
    }

    public boolean friendlyFire(Permissible permissible) {
        return permissible.hasPermission("mcmmo.party.friendlyfire");
    }

    /* TELEPORT */
    public boolean partyTeleportSend(Permissible permissible) {
        return permissible.hasPermission("mcmmo.commands.ptp.send");
    }

    public boolean partyTeleportAccept(Permissible permissible) {
        return permissible.hasPermission("mcmmo.commands.ptp.accept");
    }

    public boolean partyTeleportAcceptAll(Permissible permissible) {
        return permissible.hasPermission("mcmmo.commands.ptp.acceptall");
    }

    public boolean partyTeleportToggle(Permissible permissible) {
        return permissible.hasPermission("mcmmo.commands.ptp.toggle");
    }

    public boolean partyTeleportAllWorlds(Permissible permissible) {
        return permissible.hasPermission("mcmmo.commands.ptp.world.all");
    }

    public boolean partyTeleportWorld(Permissible permissible, World world) {
        return permissible.hasPermission("mcmmo.commands.ptp.world." + world.getName());
    }

    public void generateWorldTeleportPermissions() {
        Server server = pluginRef.getServer();
        PluginManager pluginManager = server.getPluginManager();

        for (World world : server.getWorlds()) {
            addDynamicPermission("mcmmo.commands.ptp.world." + world.getName(), pluginManager);
        }
    }

    /**
     * XP Perks are defined by user config files and are not known until runtime
     * This method registers Permissions with the server software as needed
     */
    public void addCustomXPPerks() {
        pluginRef.getLogger().info("Registering custom XP perks with server software...");
        PluginManager pluginManager = pluginRef.getServer().getPluginManager();

        for (CustomXPPerk customXPPerk : pluginRef.getConfigManager().getConfigExperience().getCustomXPBoosts()) {
            Permission permission = new Permission(customXPPerk.getPerkPermissionAddress());
            permission.setDefault(PermissionDefault.FALSE);

            try {
                ((SimplePluginManager) pluginManager).addPermission(permission);
            } catch (IllegalArgumentException e) {
                pluginManager.removePermission(customXPPerk.getPerkPermissionAddress());
                pluginManager.addPermission(permission);
            }
        }
    }

    private void addDynamicPermission(String permissionName, PluginManager pluginManager) {
        Permission permission = new Permission(permissionName);
        permission.setDefault(PermissionDefault.OP);
        pluginManager.addPermission(permission);
    }
}
