package com.gmail.nossr50.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
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
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.commands.general.XprateCommand;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.AbilityType;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.events.chat.McMMOAdminChatEvent;
import com.gmail.nossr50.events.chat.McMMOPartyChatEvent;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.party.Party;
import com.gmail.nossr50.runnables.BleedTimer;
import com.gmail.nossr50.skills.gathering.BlastMining;
import com.gmail.nossr50.skills.gathering.Fishing;
import com.gmail.nossr50.skills.gathering.Herbalism;
import com.gmail.nossr50.skills.taming.TamingManager;
import com.gmail.nossr50.util.BlockChecks;
import com.gmail.nossr50.util.Item;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.Skills;
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

        if (profile == null) {
            return;
        }

        if (profile.getGodMode()) {
            if (!Permissions.getInstance().mcgod(player)) {
                profile.toggleGodMode();
                player.sendMessage(LocaleLoader.getString("Commands.GodMode.Forbidden"));
            }
        }

        if (profile.inParty()) {
            if (!Permissions.getInstance().party(player)) {
                profile.removeParty();
                player.sendMessage(LocaleLoader.getString("Party.Forbidden"));
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

        if (Permissions.getInstance().fishing(player)) {
            State state = event.getState();

            switch (state) {
            case CAUGHT_FISH:
                Fishing.processResults(event);
                break;

            case CAUGHT_ENTITY:
                if (!(event.getCaught() instanceof LivingEntity)) {
                    return;
                }

                if (Users.getProfile(player).getSkillLevel(SkillType.FISHING) >= 150 && Permissions.getInstance().shakeMob(player)) {
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
        PlayerProfile profile = Users.getProfile(event.getPlayer());

        if (profile == null) {
            return;
        }

        if (profile.getAbilityMode(AbilityType.BERSERK)) {
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
        Users.addUser(event.getPlayer()).getProfile().actualizeRespawnATS();
    }

    /**
     * Monitor PlayerQuit events.
     *
     * @param event The event to watch
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        /* GARBAGE COLLECTION */

        //Bleed it out
        BleedTimer.bleedOut(player);
    }

    /**
     * Monitor PlayerJoin events.
     *
     * @param event The event to watch
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        //TODO: Locale ALL the things.
        if (Config.getInstance().getMOTDEnabled() && Permissions.getInstance().motd(player)) {
            String prefix = ChatColor.GOLD + "[mcMMO] ";
            String perkPrefix = ChatColor.RED + "[mcMMO Perks] ";

            player.sendMessage(prefix + ChatColor.YELLOW + "Running version " + ChatColor.DARK_AQUA + plugin.getDescription().getVersion()); //TODO: Locale

            if (Config.getInstance().getHardcoreEnabled()) {
                if (Config.getInstance().getHardcoreVampirismEnabled()) {
                    player.sendMessage(prefix + ChatColor.DARK_RED + "Hardcore & Vampirism enabled.");
                    player.sendMessage(prefix + ChatColor.DARK_AQUA + "Skill Death Penalty: " + ChatColor.DARK_RED + Config.getInstance().getHardcoreDeathStatPenaltyPercentage() + "% " + ChatColor.DARK_AQUA + "Vampirism Stat Leech: " + ChatColor.DARK_RED + Config.getInstance().getHardcoreVampirismStatLeechPercentage() + "%");
                }
                else {
                    player.sendMessage(prefix + ChatColor.DARK_RED + "Hardcore enabled.");
                    player.sendMessage(prefix + ChatColor.DARK_AQUA + "Skill Death Penalty: " + ChatColor.DARK_RED + Config.getInstance().getHardcoreDeathStatPenaltyPercentage() + "%");
                }
            }   

            if (player.hasPermission("mcmmo.perks.xp.quadruple")) {
                player.sendMessage(perkPrefix + ChatColor.DARK_AQUA + "Quadruple XP - Receive 4x XP.");
            }
            else if (player.hasPermission("mcmmo.perks.xp.triple")) {
                player.sendMessage(perkPrefix + ChatColor.DARK_AQUA + "Triple XP - Receive 3x XP.");
            }
            else if (player.hasPermission("mcmmo.perks.xp.double")) {
                player.sendMessage(perkPrefix + ChatColor.DARK_AQUA + "Double XP - Receive 2x XP.");
            }

            player.sendMessage(ChatColor.GOLD+"[mcMMO] " + ChatColor.GREEN + "http://www.mcmmo.info" + ChatColor.YELLOW + " - mcMMO Website & Forums"); //TODO: Locale
            //player.sendMessage(LocaleLoader.getString("mcMMO.MOTD", new Object[] {plugin.getDescription().getVersion()}));
            //player.sendMessage(LocaleLoader.getString("mcMMO.Website"));
        }

        //THIS IS VERY BAD WAY TO DO THINGS, NEED BETTER WAY
        if (XprateCommand.isXpEventRunning()) {
            player.sendMessage(LocaleLoader.getString("XPRate.Event", new Object[] {Config.getInstance().xpGainMultiplier}));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        PlayerProfile profile = Users.getProfile(event.getPlayer());

        if (profile != null) {
            profile.actualizeRespawnATS();
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
            if (Permissions.getInstance().repair(player) && block.getTypeId() == Config.getInstance().getRepairAnvilId()) {
                if (mcMMO.repairManager.isRepairable(inHand)) {
                    mcMMO.repairManager.handleRepair(player, inHand);
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
            if (inHand.getType().equals(Material.SEEDS) && BlockChecks.makeMossy(block) && Permissions.getInstance().greenThumbBlocks(player)) {
                Herbalism.greenThumbBlocks(inHand, player, block);
            }

            /* ITEM CHECKS */
            if (BlockChecks.abilityBlockCheck(block)) {
                Item.itemChecks(player);
            }

            /* BLAST MINING CHECK */
            if (player.isSneaking() && inHand.getTypeId() == Config.getInstance().getDetonatorItemID() && Permissions.getInstance().blastMining(player)) {
                BlastMining.detonate(event, player, plugin);
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
            if (player.isSneaking() && inHand.getTypeId() == Config.getInstance().getDetonatorItemID() && Permissions.getInstance().blastMining(player)) {
                BlastMining.detonate(event, player, plugin);
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
    public void onPlayerChat(PlayerChatEvent event) {
        Player player = event.getPlayer();
        PlayerProfile profile = Users.getProfile(player);

        if (profile == null) {
            return;
        }

        if (profile.getPartyChatMode()) {
            Party party = profile.getParty();

            if (party == null) {
                player.sendMessage("You're not in a party, type /p to leave party chat mode."); //TODO: Use mcLocale
                return;
            }

            String partyName = party.getName();
            String playerName = player.getName();
            McMMOPartyChatEvent chatEvent = new McMMOPartyChatEvent(playerName, partyName, event.getMessage());
            plugin.getServer().getPluginManager().callEvent(chatEvent);

            if (chatEvent.isCancelled()) {
                return;
            }

            String prefix = ChatColor.GREEN + "(" + ChatColor.WHITE + playerName + ChatColor.GREEN + ") ";

            plugin.getLogger().info("[P](" + partyName + ")" + "<" + playerName + "> " + chatEvent.getMessage());

            for (Player member : party.getOnlineMembers()) {
                member.sendMessage(prefix + chatEvent.getMessage());
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

            String prefix = ChatColor.AQUA + "{" + ChatColor.WHITE + playerName + ChatColor.AQUA + "} ";

            plugin.getLogger().info("[A]<" + playerName + "> " + chatEvent.getMessage());

            for (Player otherPlayer : plugin.getServer().getOnlinePlayers()) {
                if (Permissions.getInstance().adminChat(otherPlayer) || otherPlayer.isOp()) {
                    otherPlayer.sendMessage(prefix + chatEvent.getMessage());
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
