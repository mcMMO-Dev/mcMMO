package com.gmail.nossr50.listeners;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.chat.ChatManager;
import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.McMMOPlayer;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.party.Party;
import com.gmail.nossr50.party.ShareHandler;
import com.gmail.nossr50.skills.fishing.Fishing;
import com.gmail.nossr50.skills.herbalism.Herbalism;
import com.gmail.nossr50.skills.mining.BlastMining;
import com.gmail.nossr50.skills.mining.MiningManager;
import com.gmail.nossr50.skills.repair.Repair;
import com.gmail.nossr50.skills.repair.Salvage;
import com.gmail.nossr50.skills.runnables.BleedTimer;
import com.gmail.nossr50.skills.taming.TamingManager;
import com.gmail.nossr50.skills.utilities.AbilityType;
import com.gmail.nossr50.skills.utilities.SkillTools;
import com.gmail.nossr50.skills.utilities.SkillType;
import com.gmail.nossr50.util.BlockChecks;
import com.gmail.nossr50.util.ItemChecks;
import com.gmail.nossr50.util.Motd;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.StringUtils;
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

        if (Misc.isNPCPlayer(player)) {
            return;
        }

        McMMOPlayer mcMMOPlayer = Users.getPlayer(player);
        PlayerProfile profile = mcMMOPlayer.getProfile();

        if (profile.getGodMode() && !Permissions.mcgod(player)) {
            profile.toggleGodMode();
            player.sendMessage(LocaleLoader.getString("Commands.GodMode.Forbidden"));
        }

        if (mcMMOPlayer.inParty() && !Permissions.party(player)) {
            mcMMOPlayer.removeParty();
            player.sendMessage(LocaleLoader.getString("Party.Forbidden"));
        }
    }

    /**
     * Handle PlayerDropItem events that involve modifying the event.
     *
     * @param event The event to modify
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerDropItemEvent(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        PlayerProfile playerProfile = Users.getPlayer(player).getProfile();

        if (playerProfile.getAbilityMode(AbilityType.GIGA_DRILL_BREAKER) || playerProfile.getAbilityMode(AbilityType.SUPER_BREAKER)) {
            event.setCancelled(true);
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

        if (Misc.isNPCPlayer(player) || !Permissions.fishing(player)) {
            return;
        }

        McMMOPlayer mcMMOPlayer = Users.getPlayer(player);
        int skillLevel = mcMMOPlayer.getProfile().getSkillLevel(SkillType.FISHING);

        switch (event.getState()) {
        case CAUGHT_FISH:
            Fishing.beginFishing(mcMMOPlayer, skillLevel, event);
            break;

        case CAUGHT_ENTITY:
            Entity entity = event.getCaught();

            if (entity instanceof LivingEntity && skillLevel >= AdvancedConfig.getInstance().getShakeUnlockLevel() && Permissions.shakeMob(player)) {
                Fishing.beginShakeMob(player, (LivingEntity) entity, skillLevel);
            }

            break;
        default:
            break;
        }
    }

    /**
     * Monitor PlayerPickupItem events.
     *
     * @param event The event to watch
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();
        Item item = event.getItem();

        if (Misc.isNPCPlayer(player)) {
            return;
        }

        McMMOPlayer mcMMOPlayer = Users.getPlayer(player);

        if (mcMMOPlayer.inParty() && ItemChecks.isShareable(item.getItemStack())) {
            ShareHandler.handleItemShare(event, mcMMOPlayer);
        }
    }

    /**
     * Monitor PlayerQuit events.
     *
     * @param event The event to watch
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (Misc.isNPCPlayer(player)) {
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

        /* We can't use the other check here because a profile hasn't been created yet.*/
        if (player == null || player.hasMetadata("NPC")) {
            return;
        }

        Users.addUser(player).getProfile().actualizeRespawnATS();

        if (Config.getInstance().getMOTDEnabled() && Permissions.motd(player)) {
            Motd.displayAll(player);
        }

        if (plugin.isXPEventEnabled()) {
            player.sendMessage(LocaleLoader.getString("XPRate.Event", Config.getInstance().getExperienceGainsGlobalMultiplier()));
        }
    }

    /**
     * Monitor PlayerRespawn events.
     * @param event The event to watch
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();

        if (Misc.isNPCPlayer(player)) {
            return;
        }

        Users.getPlayer(player).getProfile().actualizeRespawnATS();
    }

    /**
     * Handle PlayerInteract events that involve modifying the event.
     *
     * @param event The event to modify
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerInteractLowest(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (Misc.isNPCPlayer(player)) {
            return;
        }

        Block block = event.getClickedBlock();
        ItemStack heldItem = player.getItemInHand();

        switch (event.getAction()) {
        case RIGHT_CLICK_BLOCK:
            int blockID = block.getTypeId();

            /* REPAIR CHECKS */
            if (blockID == Repair.anvilID && Permissions.repair(player) && mcMMO.repairManager.isRepairable(heldItem)) {
                mcMMO.repairManager.handleRepair(Users.getPlayer(player), heldItem);
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
                MiningManager miningManager = new MiningManager(Users.getPlayer(player));
                miningManager.detonate(event);
            }

            break;

        case RIGHT_CLICK_AIR:
            /* BLAST MINING CHECK */
            if (player.isSneaking() && Permissions.blastMining(player) && heldItem.getTypeId() == BlastMining.detonatorID) {
                MiningManager miningManager = new MiningManager(Users.getPlayer(player));
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

        if (Misc.isNPCPlayer(player)) {
            return;
        }

        Block block = event.getClickedBlock();
        ItemStack heldItem = player.getItemInHand();

        switch (event.getAction()) {
        case RIGHT_CLICK_BLOCK:

            /* ACTIVATION & ITEM CHECKS */
            if (BlockChecks.canActivateAbilities(block)) {
                if (SkillTools.abilitiesEnabled) {
                    if (BlockChecks.canActivateHerbalism(block)) {
                        SkillTools.activationCheck(player, SkillType.HERBALISM);
                    }

                    SkillTools.activationCheck(player, SkillType.AXES);
                    SkillTools.activationCheck(player, SkillType.EXCAVATION);
                    SkillTools.activationCheck(player, SkillType.MINING);
                    SkillTools.activationCheck(player, SkillType.SWORDS);
                    SkillTools.activationCheck(player, SkillType.UNARMED);
                    SkillTools.activationCheck(player, SkillType.WOODCUTTING);
                }
            }

            /* GREEN THUMB CHECK */
            if (heldItem.getType() == Material.SEEDS && BlockChecks.canMakeMossy(block) && Permissions.greenThumbBlocks(player)) {
                Herbalism.greenThumbBlocks(heldItem, player, block);
            }

            break;

        case RIGHT_CLICK_AIR:

            /* ACTIVATION CHECKS */
            if (SkillTools.abilitiesEnabled) {
                SkillTools.activationCheck(player, SkillType.AXES);
                SkillTools.activationCheck(player, SkillType.EXCAVATION);
                SkillTools.activationCheck(player, SkillType.HERBALISM);
                SkillTools.activationCheck(player, SkillType.MINING);
                SkillTools.activationCheck(player, SkillType.SWORDS);
                SkillTools.activationCheck(player, SkillType.UNARMED);
                SkillTools.activationCheck(player, SkillType.WOODCUTTING);
            }

            break;

        case LEFT_CLICK_AIR:
        case LEFT_CLICK_BLOCK:

            /* CALL OF THE WILD CHECKS */
            if (player.isSneaking()) {
                Material type = heldItem.getType();

                if (type == Material.RAW_FISH) {
                    TamingManager tamingManager = new TamingManager(Users.getPlayer(player));
                    tamingManager.summonOcelot();
                }
                else if (type == Material.BONE) {
                    TamingManager tamingManager = new TamingManager(Users.getPlayer(player));
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

        if (Misc.isNPCPlayer(player)) {
            return;
        }

        McMMOPlayer mcMMOPlayer = Users.getPlayer(player);

        if (mcMMOPlayer.getPartyChatMode()) {
            Party party = mcMMOPlayer.getParty();

            if (party == null) {
                player.sendMessage(LocaleLoader.getString("Commands.Party.None"));
                return;
            }

            ChatManager.handlePartyChat(plugin, party, player.getName(), player.getDisplayName(), event.getMessage());
            event.setCancelled(true);
        }
        else if (mcMMOPlayer.getAdminChatMode()) {
            ChatManager.handleAdminChat(plugin, player.getName(), player.getDisplayName(), event.getMessage());
            event.setCancelled(true);
        }
    }

    /**
     * Handle "ugly" aliasing /skillname commands, since setAliases doesn't work.
     *
     * @param event The event to watch
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        if (!Config.getInstance().getLocale().equalsIgnoreCase("en_US")) {
            String message = event.getMessage();
            String command = message.substring(1).split(" ")[0];
            String lowerCaseCommand = command.toLowerCase();

            for (SkillType skill : SkillType.values()) {
                String skillName = skill.toString().toLowerCase();
                String localizedName = LocaleLoader.getString(StringUtils.getCapitalized(skillName) + ".SkillName").toLowerCase();

                if (lowerCaseCommand.equals(localizedName)) {
                    event.setMessage(message.replace(command, skillName));
                    break;
                }

                if (lowerCaseCommand.equals(skillName)) {
                    break;
                }
            }
        }
    }
}
