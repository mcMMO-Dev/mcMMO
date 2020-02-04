package com.gmail.nossr50.util.commands;

import com.gmail.nossr50.core.MetadataConstants;
import com.gmail.nossr50.datatypes.player.BukkitMMOPlayer;
import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.StringUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class CommandTools {
    private final mcMMO pluginRef;

    public CommandTools(mcMMO pluginRef) {
        this.pluginRef = pluginRef;
    }

    public boolean isChildSkill(CommandSender sender, PrimarySkillType skill) {
        if (skill == null || !pluginRef.getSkillTools().isChildSkill(skill)) {
            return false;
        }

        sender.sendMessage("Child skills are not supported by this command."); // TODO: Localize this
        return true;
    }

    public boolean tooFar(CommandSender sender, Player target, boolean hasPermission) {
        if(!target.isOnline() && !hasPermission) {
            sender.sendMessage(pluginRef.getLocaleManager().getString("Inspect.Offline"));
            return true;
        } else if (pluginRef.getConfigManager().getConfigCommands().isLimitInspectRange()
                        && sender instanceof Player
                && !pluginRef.getMiscTools().isNear(((Player) sender).getLocation(), target.getLocation(),
                        pluginRef.getConfigManager().getConfigCommands().getInspectCommandMaxDistance())
                && !hasPermission) {
            sender.sendMessage(pluginRef.getLocaleManager().getString("Inspect.TooFar"));
            return true;
        }

        return false;
    }

    public boolean hidden(CommandSender sender, Player target, boolean hasPermission) {
        return sender instanceof Player && !((Player) sender).canSee(target) && !hasPermission;
    }

    public boolean noConsoleUsage(CommandSender sender) {
        if (sender instanceof Player) {
            return false;
        }

        sender.sendMessage(pluginRef.getLocaleManager().getString("Commands.NoConsole"));
        return true;
    }

    /**
     * Checks if there is a valid mcMMOPlayer object.
     *
     * @param sender      CommandSender who used the command
     * @param playerName  name of the target player
     * @param mcMMOPlayer mcMMOPlayer object of the target player
     * @return true if the player is online and a valid mcMMOPlayer object was found
     */
    public boolean checkPlayerExistence(CommandSender sender, String playerName, BukkitMMOPlayer mcMMOPlayer) {
        if (mcMMOPlayer != null) {
            if (hidden(sender, mcMMOPlayer.getNative(), false)) {
                sender.sendMessage(pluginRef.getLocaleManager().getString("Commands.Offline"));
                return false;
            }
            return true;
        }

        PlayerProfile profile = new PlayerProfile(pluginRef, playerName, null, false);

        if (unloadedProfile(sender, profile)) {
            return false;
        }

        sender.sendMessage(pluginRef.getLocaleManager().getString("Commands.DoesNotExist"));
        return false;
    }

    public boolean unloadedProfile(CommandSender sender, PlayerProfile profile) {
        if (profile.isLoaded()) {
            return false;
        }

        sender.sendMessage(pluginRef.getLocaleManager().getString("Commands.Offline"));
        return true;
    }

    public boolean hasPlayerDataKey(CommandSender sender) {
        if (!(sender instanceof Player)) {
            return false;
        }

        boolean hasPlayerDataKey = ((Player) sender).hasMetadata(MetadataConstants.PLAYER_DATA_METAKEY);

        if (!hasPlayerDataKey) {
            sender.sendMessage(pluginRef.getLocaleManager().getString("Commands.NotLoaded"));
        }

        return hasPlayerDataKey;
    }

    public boolean isLoaded(CommandSender sender, PlayerProfile profile) {
        if (profile.isLoaded()) {
            return true;
        }

        sender.sendMessage(pluginRef.getLocaleManager().getString("Commands.NotLoaded"));
        return false;
    }

    public boolean isInvalidInteger(CommandSender sender, String value) {
        if (StringUtils.isInt(value)) {
            return false;
        }

        sender.sendMessage("That is not a valid integer."); // TODO: Localize
        return true;
    }

    public boolean isInvalidDouble(CommandSender sender, String value) {
        if (StringUtils.isDouble(value)) {
            return false;
        }

        sender.sendMessage("That is not a valid percentage."); // TODO: Localize
        return true;
    }

    public boolean isInvalidSkill(CommandSender sender, String skillName) {
        if (pluginRef.getSkillTools().isSkill(skillName)) {
            return false;
        }

        sender.sendMessage(pluginRef.getLocaleManager().getString("Commands.Skill.Invalid"));
        return true;
    }

    public boolean shouldEnableToggle(String arg) {
        return arg.equalsIgnoreCase("on") || arg.equalsIgnoreCase("true") || arg.equalsIgnoreCase("enabled");
    }

    public boolean shouldDisableToggle(String arg) {
        return arg.equalsIgnoreCase("off") || arg.equalsIgnoreCase("false") || arg.equalsIgnoreCase("disabled");
    }

    /**
     * Print out details on Gathering skills. Only for online players.
     *
     * @param inspect The player to retrieve stats for
     * @param display The sender to display stats to
     */
    public void printGatheringSkills(Player inspect, CommandSender display) {
        printGroupedSkillData(inspect, display, pluginRef.getLocaleManager().getString("Stats.Header.Gathering"), pluginRef.getSkillTools().GATHERING_SKILLS);
    }

    public void printGatheringSkills(Player player) {
        printGatheringSkills(player, player);
    }

    /**
     * Print out details on Combat skills. Only for online players.
     *
     * @param inspect The player to retrieve stats for
     * @param display The sender to display stats to
     */
    public void printCombatSkills(Player inspect, CommandSender display) {
        printGroupedSkillData(inspect, display, pluginRef.getLocaleManager().getString("Stats.Header.Combat"), pluginRef.getSkillTools().COMBAT_SKILLS);
    }

    public void printCombatSkills(Player player) {
        printCombatSkills(player, player);
    }

    /**
     * Print out details on Misc skills. Only for online players.
     *
     * @param inspect The player to retrieve stats for
     * @param display The sender to display stats to
     */
    public void printMiscSkills(Player inspect, CommandSender display) {
        printGroupedSkillData(inspect, display, pluginRef.getLocaleManager().getString("Stats.Header.Misc"), pluginRef.getSkillTools().MISC_SKILLS);
    }

    public void printMiscSkills(Player player) {
        printMiscSkills(player, player);
    }

    public String displaySkill(PlayerProfile profile, PrimarySkillType skill) {
        if (pluginRef.getSkillTools().isChildSkill(skill)) {
            return pluginRef.getLocaleManager().getString("Skills.ChildStats", pluginRef.getLocaleManager().getString(StringUtils.getCapitalized(skill.toString()) + ".Listener") + " ", profile.getSkillLevel(skill));
        }

        return pluginRef.getLocaleManager().getString("Skills.Stats", pluginRef.getLocaleManager().getString(StringUtils.getCapitalized(skill.toString()) + ".Listener") + " ", profile.getSkillLevel(skill), profile.getSkillXpLevel(skill), profile.getXpToLevel(skill));
    }

    private void printGroupedSkillData(Player inspect, CommandSender display, String header, List<PrimarySkillType> skillGroup) {
        if (pluginRef.getUserManager().getPlayer(inspect) == null)
            return;

        PlayerProfile profile = pluginRef.getUserManager().getPlayer(inspect).getProfile();

        List<String> displayData = new ArrayList<>();
        displayData.add(header);

        for (PrimarySkillType primarySkillType : skillGroup) {
            if (pluginRef.getSkillTools().doesPlayerHaveSkillPermission(primarySkillType, inspect)) {
                displayData.add(displaySkill(profile, primarySkillType));
            }
        }

        int size = displayData.size();

        if (size > 1) {
            display.sendMessage(displayData.toArray(new String[size]));
        }
    }

    public List<String> getOnlinePlayerNames(CommandSender sender) {
        Player player = sender instanceof Player ? (Player) sender : null;
        List<String> onlinePlayerNames = new ArrayList<>();

        for (Player onlinePlayer : pluginRef.getServer().getOnlinePlayers()) {
            if (player != null && player.canSee(onlinePlayer)) {
                onlinePlayerNames.add(onlinePlayer.getName());
            }
        }

        return onlinePlayerNames;
    }

    /**
     * Get a matched player name if one was found in the database.
     *
     * @param partialName Name to match
     * @return Matched name or {@code partialName} if no match was found
     */
    public String getMatchedPlayerName(String partialName) {
        if (pluginRef.getConfigManager().getConfigCommands().getMisc().isMatchOfflinePlayers()) {
            List<String> matches = matchPlayer(partialName);

            if (matches.size() == 1) {
                partialName = matches.get(0);
            }
        } else {
            Player player = pluginRef.getServer().getPlayer(partialName);

            if (player != null) {
                partialName = player.getName();
            }
        }

        return partialName;
    }

    /**
     * Attempts to match any player names with the given name, and returns a list of all possibly matches.
     * <p>
     * This list is not sorted in any particular order.
     * If an exact match is found, the returned list will only contain a single result.
     *
     * @param partialName Name to match
     * @return List of all possible names
     */
    private List<String> matchPlayer(String partialName) {
        List<String> matchedPlayers = new ArrayList<>();

        for (OfflinePlayer offlinePlayer : pluginRef.getServer().getOfflinePlayers()) {
            String playerName = offlinePlayer.getName();

            if (playerName == null) { //Do null checking here to detect corrupted data before sending it throuogh .equals
                System.err.println("[McMMO] Player data file with UIID " + offlinePlayer.getUniqueId() + " is missing a player name. This may be a legacy file from before bukkit.lastKnownName. This should be okay to ignore.");
                continue; //Don't let an error here interrupt the loop
            }

            if (partialName.equalsIgnoreCase(playerName)) {
                // Exact match
                matchedPlayers.clear();
                matchedPlayers.add(playerName);
                break;
            }

            if (playerName.toLowerCase(Locale.ENGLISH).contains(partialName.toLowerCase(Locale.ENGLISH))) {
                // Partial match
                matchedPlayers.add(playerName);
            }
        }

        return matchedPlayers;
    }
}
