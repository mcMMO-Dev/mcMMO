package com.gmail.nossr50.listeners;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerFishEvent.State;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.BlockChecks;
import com.gmail.nossr50.Combat;
import com.gmail.nossr50.Item;
import com.gmail.nossr50.ItemChecks;
import com.gmail.nossr50.Users;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.mcPermissions;
import com.gmail.nossr50.commands.general.XprateCommand;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.runnables.RemoveProfileFromMemoryTask;
import com.gmail.nossr50.spout.SpoutStuff;
import com.gmail.nossr50.datatypes.AbilityType;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.events.chat.McMMOAdminChatEvent;
import com.gmail.nossr50.events.chat.McMMOPartyChatEvent;
import com.gmail.nossr50.locale.mcLocale;
import com.gmail.nossr50.party.Party;
import com.gmail.nossr50.skills.BlastMining;
import com.gmail.nossr50.skills.Fishing;
import com.gmail.nossr50.skills.Herbalism;
import com.gmail.nossr50.skills.Repair;
import com.gmail.nossr50.skills.Skills;
import com.gmail.nossr50.skills.Taming;

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
        PlayerProfile PP = Users.getProfile(player);

        if (PP.getGodMode()) {
            if (!mcPermissions.getInstance().mcgod(player)) {
                PP.toggleGodMode();
                player.sendMessage(mcLocale.getString("Commands.GodMode.Forbidden"));
            }
        }

        if (PP.inParty()) {
            if (!mcPermissions.getInstance().party(player)) {
                PP.removeParty();
                player.sendMessage(mcLocale.getString("Party.Forbidden"));
            }
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

        if (mcPermissions.getInstance().fishing(player)) {
            State state = event.getState();

            switch (state) {
            case CAUGHT_FISH:
                Fishing.processResults(event);
                break;

            case CAUGHT_ENTITY:
                if (Users.getProfile(player).getSkillLevel(SkillType.FISHING) >= 150 && mcPermissions.getInstance().shakeMob(player)) {
                    Fishing.shakeMob(event);
                }
                break;

            default:
                break;
            }
        }
    }

    /**
     * Monitor PlaterPickupItem events.
     *
     * @param event The event to watch
     */
    @EventHandler(ignoreCancelled = true)
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        if (Users.getProfile(event.getPlayer()).getAbilityMode(AbilityType.BERSERK)) {
             event.setCancelled(true);
        }
    }

    /**
     * Monitor PlayerLogin events.
     *
     * @param event The event to watch
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerLogin(PlayerLoginEvent event) {
        Users.addUser(event.getPlayer());
    }

    /**
     * Monitor PlayerQuit events.
     *
     * @param event The event to watch
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        PlayerProfile PP = Users.getProfile(player);

        /* GARBAGE COLLECTION */

        //Remove Spout Stuff
        if (Config.spoutEnabled && SpoutStuff.playerHUDs.containsKey(player)) {
            SpoutStuff.playerHUDs.remove(player);
        }

        //Bleed it out
        if(PP.getBleedTicks() > 0) {
            Combat.dealDamage(player, PP.getBleedTicks() * 2);
        }

        //Schedule PlayerProfile removal 2 minutes after quitting
        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new RemoveProfileFromMemoryTask(player), 2400);
    }

    /**
     * Monitor PlayerJoin events.
     *
     * @param event The event to watch
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (mcPermissions.getInstance().motd(player) && Config.enableMotd) {
            player.sendMessage(mcLocale.getString("mcMMO.MOTD", new Object[] {plugin.getDescription().getVersion()}));
            player.sendMessage(mcLocale.getString("mcMMO.Wiki"));
        }

        //THIS IS VERY BAD WAY TO DO THINGS, NEED BETTER WAY
        if (XprateCommand.xpevent) {
            player.sendMessage(mcLocale.getString("XPRate.Event", new Object[] {Config.xpGainMultiplier}));
        }
    }

    /**
     * Monitor PlayerInteract events.
     *
     * @param event The event to watch
     */
    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        Action action = event.getAction();
        Block block = event.getClickedBlock();
        ItemStack is = player.getItemInHand();
        Material mat;

        /* Fix for NPE on interacting with air */
        if (block == null) {
            mat = Material.AIR;
        }
        else {
            mat = block.getType();
        }

        switch (action) {
        case RIGHT_CLICK_BLOCK:

            /* REPAIR CHECKS */
            if (mcPermissions.getInstance().repair(player) && block.getTypeId() == Config.anvilID && (ItemChecks.isTool(is) || ItemChecks.isArmor(is))) {
                Repair.repairCheck(player, is);
                event.setCancelled(true);
                player.updateInventory();
            }

            /* ACTIVATION CHECKS */
            if (Config.enableAbilities && BlockChecks.abilityBlockCheck(mat)) {
                if (!mat.equals(Material.DIRT) && !mat.equals(Material.GRASS) && !mat.equals(Material.SOIL)) {
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
            if (mcPermissions.getInstance().greenThumbBlocks(player) && Herbalism.makeMossy(mat) && is.getType().equals(Material.SEEDS)) {
                Herbalism.greenThumbBlocks(is, player, block);
            }

            /* ITEM CHECKS */
            if (BlockChecks.abilityBlockCheck(mat)) {
                Item.itemchecks(player);
            }

            /* BLAST MINING CHECK */
            if (mcPermissions.getInstance().blastMining(player) && is.getTypeId() == Config.getDetonatorItemID()) {
                BlastMining.remoteDetonation(player, plugin);
            }

            break;

        case RIGHT_CLICK_AIR:

            /* ACTIVATION CHECKS */
            if (Config.enableAbilities) {
                Skills.activationCheck(player, SkillType.AXES);
                Skills.activationCheck(player, SkillType.EXCAVATION);
                Skills.activationCheck(player, SkillType.HERBALISM);
                Skills.activationCheck(player, SkillType.MINING);
                Skills.activationCheck(player, SkillType.SWORDS);
                Skills.activationCheck(player, SkillType.UNARMED);
                Skills.activationCheck(player, SkillType.WOODCUTTING);
            }

            /* ITEM CHECKS */
            Item.itemchecks(player);

            /* BLAST MINING CHECK */
            if (mcPermissions.getInstance().blastMining(player) && is.getTypeId() == Config.getDetonatorItemID()) {
                BlastMining.remoteDetonation(player, plugin);
            }

            break;

        case LEFT_CLICK_AIR:
        case LEFT_CLICK_BLOCK:

            /* CALL OF THE WILD CHECKS */
            if (player.isSneaking() && mcPermissions.getInstance().taming(player)) {
                if (is.getType().equals(Material.RAW_FISH)) {
                    Taming.animalSummon(EntityType.OCELOT, player, plugin);
                }
                else if (is.getType().equals(Material.BONE)) {
                    Taming.animalSummon(EntityType.WOLF, player, plugin);
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
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerChat(PlayerChatEvent event) {
        Player player = event.getPlayer();
        PlayerProfile PP = Users.getProfile(player);
        boolean partyChat = PP.getPartyChatMode();
        boolean adminChat = PP.getAdminChatMode();
        Set<Player> recipients = event.getRecipients();

        Set<Player> intendedRecipients = new HashSet<Player>();
        ChatColor color = null;

        if (partyChat || adminChat) {
            if (partyChat) {
                if (!PP.inParty()) {
                    player.sendMessage("You're not in a party, type /p to leave party chat mode."); //TODO: Use mcLocale
                    return;
                }

                color = ChatColor.GREEN;

                McMMOPartyChatEvent chatEvent = new McMMOPartyChatEvent(player.getName(), PP.getParty(), event.getMessage());
                plugin.getServer().getPluginManager().callEvent(chatEvent);

                if(chatEvent.isCancelled()) return;

                event.setMessage(chatEvent.getMessage());

                for (Player x : Party.getInstance().getOnlineMembers(player)) {
                    intendedRecipients.add(x);
                }

                event.setFormat(color + "(" + ChatColor.WHITE + "%1$s" + color + ") %2$s");
            }

            if (adminChat) {
                color = ChatColor.AQUA;

                McMMOAdminChatEvent chatEvent = new McMMOAdminChatEvent(player.getName(), event.getMessage());
                plugin.getServer().getPluginManager().callEvent(chatEvent);

                if(chatEvent.isCancelled()) return;

                event.setMessage(chatEvent.getMessage());

                for (Player x : plugin.getServer().getOnlinePlayers()) {
                    if (x.isOp() || mcPermissions.getInstance().adminChat(x)) {
                        intendedRecipients.add(x);
                    }
                }

                event.setFormat(color + "{" + ChatColor.WHITE + "%1$s" + color + "} %2$s");
            }

            recipients.retainAll(intendedRecipients);
        }
    }

    // Dynamically aliasing commands need to be re-done.
    // For now, using a command with an alias will send both the original command, and the mcMMO command

    /**
     * Monitor PlayerCommandPreprocess events.
     *
     * @param event The event to watch
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        String message = event.getMessage();
        
        if (!message.startsWith("/")) {
            return;
        }

        String command = message.substring(1).split(" ")[0];

        if (plugin.aliasMap.containsKey(command)) {
            if (command.equalsIgnoreCase(plugin.aliasMap.get(command))) {
                return;
            }
            event.getPlayer().chat(message.replaceFirst(command, plugin.aliasMap.get(command)));
        }
    }
}
