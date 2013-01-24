package com.gmail.nossr50.listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerFishEvent.State;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginDescriptionFile;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.commands.general.XprateCommand;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.events.chat.McMMOAdminChatEvent;
import com.gmail.nossr50.events.chat.McMMOPartyChatEvent;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.party.Party;
import com.gmail.nossr50.runnables.BleedTimer;
import com.gmail.nossr50.skills.SkillType;
import com.gmail.nossr50.skills.Skills;
import com.gmail.nossr50.skills.fishing.Fishing;
import com.gmail.nossr50.skills.herbalism.Herbalism;
import com.gmail.nossr50.skills.mining.MiningManager;
import com.gmail.nossr50.skills.repair.Salvage;
import com.gmail.nossr50.skills.taming.TamingManager;
import com.gmail.nossr50.util.BlockChecks;
import com.gmail.nossr50.util.Item;
import com.gmail.nossr50.util.MOTD;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.Users;

public class PlayerListener implements Listener {

    private final mcMMO plugin;

    public PlayerListener(final mcMMO plugin) {
        this.plugin = plugin;
    }

    /**
     * Monitor PlayerChangedWorld events.
     *
     * @param event The event to watch
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerWorldChangeEvent(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        PlayerProfile profile = Users.getProfile(player);

        if (Misc.isNPC(player, profile)) {
            return;
        }

        if (profile.getGodMode() && !Permissions.mcgod(player)) {
            profile.toggleGodMode();
            player.sendMessage(LocaleLoader.getString("Commands.GodMode.Forbidden"));
        }

        if (profile.inParty() && !Permissions.party(player)) {
            profile.removeParty();
            player.sendMessage(LocaleLoader.getString("Party.Forbidden"));
        }
    }

    /**
     * Monitor PlayerFish events.
     *
     * @param event The event to watch
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerFish(PlayerFishEvent event) {
        Player player = event.getPlayer();

        if (Misc.isNPC(player)) {
            return;
        }

        if (Permissions.fishing(player)) {
            State state = event.getState();

            switch (state) {
            case CAUGHT_FISH:
                Fishing.processResults(event);
                break;

            case CAUGHT_ENTITY:
                Fishing.shakeMob(event);
                break;

            default:
                break;
            }
        }
    }

    /**
     * Monitor PlayerLogin events.
     *
     * @param event The event to watch
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();

        /* We can't use the other check here because a profile hasn't been created yet.*/
        if (player == null || player.hasMetadata("NPC")) {
            return;
        }

        Users.addUser(player).getProfile().actualizeRespawnATS();
    }

    /**
     * Monitor PlayerQuit events.
     *
     * @param event The event to watch
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (Misc.isNPC(player)) {
            return;
        }

        /* GARBAGE COLLECTION */
        BleedTimer.bleedOut(player); //Bleed it out
    }

    /**
     * Monitor PlayerJoin events.
     *
     * @param event The event to watch
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (Misc.isNPC(player)) {
            return;
        }

        if (Config.getInstance().getMOTDEnabled() && Permissions.motd(player)) {
            PluginDescriptionFile pluginDescription = plugin.getDescription();
            MOTD motd = new MOTD(player);

            motd.displayVersion(pluginDescription.getVersion());
            motd.displayHardcoreSettings();
            motd.displayXpPerks();
            motd.displayCooldownPerks();
            motd.displayActivationPerks();
            motd.displayLuckyPerks();
            motd.displayWebsite(pluginDescription.getWebsite());
        }

        //TODO: MAKE THIS SUCK LESS. THIS IS VERY BAD WAY TO DO THINGS, NEED BETTER WAY
        if (XprateCommand.isXpEventRunning()) {
            player.sendMessage(LocaleLoader.getString("XPRate.Event", new Object[] {Config.getInstance().xpGainMultiplier}));
        }
    }

    /**
     * Monitor PlayerRespawn events.
     * @param event The event to watch
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        PlayerProfile profile = Users.getProfile(player);

        if (Misc.isNPC(player, profile)) {
            return;
        }

        profile.actualizeRespawnATS();
    }

    /**
     * Monitor PlayerInteract events.
     *
     * @param event The event to watch
     */
    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (player.hasMetadata("NPC")) return; // Check if this player is a Citizens NPC
        Action action = event.getAction();
        Block block = event.getClickedBlock();
        ItemStack inHand = player.getItemInHand();
        Material material;

        /* Fix for NPE on interacting with air */
        if (block == null) {
            material = Material.AIR;
        }
        else {
            material = block.getType();
        }

        switch (action) {
        case RIGHT_CLICK_BLOCK:

            /* REPAIR CHECKS */
            if (Permissions.repair(player) && block.getTypeId() == Config.getInstance().getRepairAnvilId()) {
                if (mcMMO.repairManager.isRepairable(inHand)) {
                    mcMMO.repairManager.handleRepair(player, inHand);
                    event.setCancelled(true);
                    player.updateInventory();
                }
            }
            /* SALVAGE CHECKS */
            if (Permissions.salvage(player) && block.getTypeId() == Config.getInstance().getSalvageAnvilId()) {
                if (Salvage.isSalvageable(inHand)) {
                    final Location location = block.getLocation();
                    Salvage.handleSalvage(player, location, inHand);
                    event.setCancelled(true);
                    player.updateInventory();
                }
            }

            /* ACTIVATION CHECKS */
            if (Config.getInstance().getAbilitiesEnabled() && BlockChecks.abilityBlockCheck(block)) {
                if (!material.equals(Material.DIRT) && !material.equals(Material.GRASS) && !material.equals(Material.SOIL)) {
                    Skills.activationCheck(player, SkillType.HERBALISM);
                }

                Skills.activationCheck(player, SkillType.AXES);
                Skills.activationCheck(player, SkillType.EXCAVATION);
                Skills.activationCheck(player, SkillType.MINING);
                Skills.activationCheck(player, SkillType.SWORDS);
                Skills.activationCheck(player, SkillType.UNARMED);
                Skills.activationCheck(player, SkillType.WOODCUTTING);
            }

            /* GREEN THUMB CHECK */
            if (inHand.getType().equals(Material.SEEDS) && BlockChecks.makeMossy(block) && Permissions.greenThumbBlocks(player)) {
                Herbalism.greenThumbBlocks(inHand, player, block);
            }

            /* ITEM CHECKS */
            if (BlockChecks.abilityBlockCheck(block)) {
                Item.itemChecks(player);
            }

            /* BLAST MINING CHECK */
            if (player.isSneaking() && inHand.getTypeId() == Config.getInstance().getDetonatorItemID() && Permissions.blastMining(player)) {
                MiningManager miningManager = new MiningManager(player);
                miningManager.detonate(event);
            }

            break;

        case RIGHT_CLICK_AIR:

            /* ACTIVATION CHECKS */
            if (Config.getInstance().getAbilitiesEnabled()) {
                Skills.activationCheck(player, SkillType.AXES);
                Skills.activationCheck(player, SkillType.EXCAVATION);
                Skills.activationCheck(player, SkillType.HERBALISM);
                Skills.activationCheck(player, SkillType.MINING);
                Skills.activationCheck(player, SkillType.SWORDS);
                Skills.activationCheck(player, SkillType.UNARMED);
                Skills.activationCheck(player, SkillType.WOODCUTTING);
            }

            /* ITEM CHECKS */
            Item.itemChecks(player);

            /* BLAST MINING CHECK */
            if (player.isSneaking() && inHand.getTypeId() == Config.getInstance().getDetonatorItemID() && Permissions.blastMining(player)) {
                MiningManager miningManager = new MiningManager(player);
                miningManager.detonate(event);
            }

            break;

        case LEFT_CLICK_AIR:
        case LEFT_CLICK_BLOCK:

            /* CALL OF THE WILD CHECKS */
            if (player.isSneaking()) {
                Material type = inHand.getType();

                if (type == Material.RAW_FISH) {
                    TamingManager tamingManager = new TamingManager(player);
                    tamingManager.summonOcelot();
                }
                else if (type == Material.BONE) {
                    TamingManager tamingManager = new TamingManager(player);
                    tamingManager.summonWolf();
                }
            }

            break;

        default:
            break;
        }
    }

    /**
     * Monitor PlayerChat events.
     *
     * @param event The event to watch
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        PlayerProfile profile = Users.getProfile(player);

        if (Misc.isNPC(player, profile)) {
            return;
        }

        if (profile.getPartyChatMode()) {
            Party party = profile.getParty();

            if (party == null) {
                player.sendMessage(LocaleLoader.getString("Commands.Party.None"));
                return;
            }

            String partyName = party.getName();
            String playerName = player.getName();
            McMMOPartyChatEvent chatEvent = new McMMOPartyChatEvent(playerName, partyName, event.getMessage());
            plugin.getServer().getPluginManager().callEvent(chatEvent);

            if (chatEvent.isCancelled()) {
                return;
            }

            plugin.getLogger().info("[P](" + partyName + ")" + "<" + playerName + "> " + chatEvent.getMessage());

            for (Player member : party.getOnlineMembers()) {
                member.sendMessage(LocaleLoader.getString("Commands.Party.Chat.Prefix", new Object[] {playerName}) + chatEvent.getMessage());
            }

            event.setCancelled(true);
        }
        else if (profile.getAdminChatMode()) {
            String playerName = player.getName();
            McMMOAdminChatEvent chatEvent = new McMMOAdminChatEvent(playerName, event.getMessage());
            plugin.getServer().getPluginManager().callEvent(chatEvent);

            if (chatEvent.isCancelled()) {
                return;
            }

            plugin.getLogger().info("[A]<" + playerName + "> " + chatEvent.getMessage());

            for (Player otherPlayer : plugin.getServer().getOnlinePlayers()) {
                if (Permissions.adminChat(otherPlayer) || otherPlayer.isOp()) {
                    otherPlayer.sendMessage(LocaleLoader.getString("Commands.AdminChat.Prefix", new Object[] {playerName}) + chatEvent.getMessage());
                }
            }

            event.setCancelled(true);
        }
    }

    /**
     * Monitor PlayerCommandPreprocess events.
     *
     * @param event The event to watch
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        String message = event.getMessage();
        String command = message.substring(1).split(" ")[0];
        String lowerCaseCommand = command.toLowerCase();

        if (plugin.commandIsAliased(lowerCaseCommand)) {
            //We should find a better way to avoid string replacement where the alias is equals to the command
            if (command.equals(plugin.getCommandAlias(lowerCaseCommand))) {
                return;
            }

            event.setMessage(message.replace(command, plugin.getCommandAlias(lowerCaseCommand)));
        }
    }
}
