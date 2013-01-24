package com.gmail.nossr50.listeners;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
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
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.runnables.BleedTimer;
import com.gmail.nossr50.skills.SkillType;
import com.gmail.nossr50.skills.Skills;
import com.gmail.nossr50.skills.fishing.Fishing;
import com.gmail.nossr50.skills.herbalism.Herbalism;
import com.gmail.nossr50.skills.mining.BlastMining;
import com.gmail.nossr50.skills.mining.MiningManager;
import com.gmail.nossr50.skills.repair.Repair;
import com.gmail.nossr50.skills.repair.Salvage;
import com.gmail.nossr50.skills.taming.TamingManager;
import com.gmail.nossr50.util.BlockChecks;
import com.gmail.nossr50.util.ChatManager;
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

        if (plugin.isXPEventEnabled()) {
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
     * Handle PlayerInteract events that involve modifying the event.
     *
     * @param event The event to watch
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerInteractLowest(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (Misc.isNPC(player)) {
            return;
        }

        Block block = event.getClickedBlock();
        ItemStack heldItem = event.getItem();

        switch (event.getAction()) {
        case RIGHT_CLICK_BLOCK:
            int blockID = block.getTypeId();

            /* REPAIR CHECKS */
            if (blockID == Repair.anvilID && Permissions.repair(player) && mcMMO.repairManager.isRepairable(heldItem)) {
                mcMMO.repairManager.handleRepair(player, heldItem);
                event.setCancelled(true);
                player.updateInventory();
            }

            /* SALVAGE CHECKS */
            else if (blockID == Salvage.anvilID && Permissions.salvage(player) && Salvage.isSalvageable(heldItem)) {
                Salvage.handleSalvage(player, block.getLocation(), heldItem);
                event.setCancelled(true);
                player.updateInventory();
            }

            /* BLAST MINING CHECK */
            else if (player.isSneaking() && Permissions.blastMining(player) && heldItem.getTypeId() == BlastMining.detonatorID) {
                MiningManager miningManager = new MiningManager(player);
                miningManager.detonate(event);
            }

            break;

        case RIGHT_CLICK_AIR:

            /* BLAST MINING CHECK */
            if (player.isSneaking() && Permissions.blastMining(player) && heldItem.getTypeId() == BlastMining.detonatorID) {
                MiningManager miningManager = new MiningManager(player);
                miningManager.detonate(event);
            }

            break;

        default:
            break;
        }
    }

    /**
     * Monitor PlayerInteract events.
     *
     * @param event The event to watch
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (Misc.isNPC(player)) {
            return;
        }

        Block block = event.getClickedBlock();
        ItemStack heldItem = event.getItem();

        switch (event.getAction()) {
        case RIGHT_CLICK_BLOCK:

            /* ACTIVATION & ITEM CHECKS */
            if (BlockChecks.canActivateAbilities(block)) {
                if (Skills.abilitiesEnabled) {
                    if (BlockChecks.canActivateHerbalism(block)) {
                        Skills.activationCheck(player, SkillType.HERBALISM);
                    }

                    Skills.activationCheck(player, SkillType.AXES);
                    Skills.activationCheck(player, SkillType.EXCAVATION);
                    Skills.activationCheck(player, SkillType.MINING);
                    Skills.activationCheck(player, SkillType.SWORDS);
                    Skills.activationCheck(player, SkillType.UNARMED);
                    Skills.activationCheck(player, SkillType.WOODCUTTING);
                }

                Item.itemChecks(player);
            }

            /* GREEN THUMB CHECK */
            if (heldItem.getType() == Material.SEEDS && BlockChecks.canMakeMossy(block) && Permissions.greenThumbBlocks(player)) {
                Herbalism.greenThumbBlocks(heldItem, player, block);
            }

            break;

        case RIGHT_CLICK_AIR:

            /* ACTIVATION CHECKS */
            if (Skills.abilitiesEnabled) {
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

            break;

        case LEFT_CLICK_AIR:
        case LEFT_CLICK_BLOCK:

            /* CALL OF THE WILD CHECKS */
            if (player.isSneaking()) {
                Material type = heldItem.getType();

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
            ChatManager chatManager = new ChatManager(plugin, player, event);
            chatManager.handlePartyChat();
        }
        else if (profile.getAdminChatMode()) {
            ChatManager chatManager = new ChatManager(plugin, player, event);
            chatManager.handleAdminChat();
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
            String commandAlias = plugin.getCommandAlias(lowerCaseCommand);

            //TODO: We should find a better way to avoid string replacement where the alias is equals to the command
            if (command.equals(commandAlias)) {
                return;
            }

            event.setMessage(message.replace(command, plugin.getCommandAlias(lowerCaseCommand)));
        }
    }
}
