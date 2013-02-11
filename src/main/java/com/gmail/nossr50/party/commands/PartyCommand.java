package com.gmail.nossr50.party.commands;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.commands.CommandHelper;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.McMMOPlayer;
import com.gmail.nossr50.events.party.McMMOPartyChangeEvent;
import com.gmail.nossr50.events.party.McMMOPartyChangeEvent.EventReason;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.party.Party;
import com.gmail.nossr50.party.PartyManager;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.Users;

public class PartyCommand implements CommandExecutor {
    private McMMOPlayer mcMMOPlayer;
    private Player player;

    private CommandExecutor partyJoinCommand = new PartyJoinCommand();
    private CommandExecutor partyAcceptCommand = new PartyAcceptCommand();
    private CommandExecutor partyCreateCommand = new PartyCreateCommand();
    private CommandExecutor partyQuitCommand = new PartyQuitCommand();
    private CommandExecutor partyExpShareCommand = new PartyExpShareCommand();
    private CommandExecutor partyItemShareCommand = new PartyItemShareCommand();
    private CommandExecutor partyInviteCommand = new PartyInviteCommand();
    private CommandExecutor partyKickCommand = new PartyKickCommand();
    private CommandExecutor partyDisbandCommand = new PartyDisbandCommand();
    private CommandExecutor partyChangeOwnerCommand = new PartyChangeOwnerCommand();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (CommandHelper.noConsoleUsage(sender)) {
            return true;
        }

        if (!Permissions.hasPermission(sender, "mcmmo.commands.party")) {
            sender.sendMessage(command.getPermissionMessage());
            return true;
        }

        player = (Player) sender;
        mcMMOPlayer = Users.getPlayer(player);

        if (args.length < 1 || args[0].equalsIgnoreCase("info")) {
            return party();
        }

        if (args[0].equalsIgnoreCase("join")) {
            return partyJoinCommand.onCommand(sender, command, label, args);
        }
        else if (args[0].equalsIgnoreCase("accept")) {
            return partyAcceptCommand.onCommand(sender, command, label, args);
        }
        else if (args[0].equalsIgnoreCase("create")) {
            return partyCreateCommand.onCommand(sender, command, label, args);
        }
        else if (args[0].equalsIgnoreCase("?") || args[0].equalsIgnoreCase("help")) {
            return printHelp();
        }

        if (!mcMMOPlayer.inParty()) {
            sender.sendMessage(LocaleLoader.getString("Commands.Party.None"));
            return printUsage();
        }

        if (args[0].equalsIgnoreCase("quit") || args[0].equalsIgnoreCase("q") || args[0].equalsIgnoreCase("leave")) {
            return partyQuitCommand.onCommand(sender, command, label, args);
        }
        else if (args[0].equalsIgnoreCase("expshare") || args[0].equalsIgnoreCase("xpshare") || args[0].equalsIgnoreCase("sharexp") || args[0].equalsIgnoreCase("shareexp")) {
            return partyExpShareCommand.onCommand(sender, command, label, args);
        }
        else if (args[0].equalsIgnoreCase("itemshare") || args[0].equalsIgnoreCase("shareitem") || args[0].equalsIgnoreCase("shareitems")) {
            return partyItemShareCommand.onCommand(sender, command, label, args);
        }
        else if (args[0].equalsIgnoreCase("invite")) {
            return partyInviteCommand.onCommand(sender, command, label, args);
        }
        else if (args[0].equalsIgnoreCase("kick")) {
            return partyKickCommand.onCommand(sender, command, label, args);
        }
        else if (args[0].equalsIgnoreCase("disband")) {
            return partyDisbandCommand.onCommand(sender, command, label, args);
        }
        else if (args[0].equalsIgnoreCase("owner")) {
            return partyChangeOwnerCommand.onCommand(sender, command, label, args);
        }
        else if (args[0].equalsIgnoreCase("lock")) {
            return lock();
        }
        else if (args[0].equalsIgnoreCase("unlock")) {
            return unlock();
        }
        else if (args[0].equalsIgnoreCase("password")) {
            return changePassword(args);
        }
        else if (args[0].equalsIgnoreCase("rename")) {
            return rename(args);
        }
        else {
            return printUsage();
        }
    }

    private boolean printUsage() {
        player.sendMessage(LocaleLoader.getString("Party.Help.0"));
        player.sendMessage(LocaleLoader.getString("Party.Help.1"));
        player.sendMessage(LocaleLoader.getString("Party.Help.2"));
        return true;
    }

    private boolean party() {
        if (mcMMOPlayer.inParty()) {
            Party party = mcMMOPlayer.getParty();
            String leader = party.getLeader();
            StringBuilder tempList = new StringBuilder();

            int membersNear = PartyManager.getNearMembers(player, party, Config.getInstance().getPartyShareRange()).size();
            int membersOnline = party.getOnlineMembers().size() - 1;

            String ItemShare = "";
            String ExpShare = "";
            String Split = "";

            for (OfflinePlayer otherMember : party.getMembers()) {
                if (leader.equals(otherMember.getName())) {
                    tempList.append(ChatColor.GOLD);
                }
                else if (otherMember.isOnline()) {
                    tempList.append(ChatColor.WHITE);
                }
                else {
                    tempList.append(ChatColor.GRAY);
                }

                tempList.append(otherMember.getName()).append(" ");
            }

            String status = LocaleLoader.getString("Party.Status.Locked");
            if (!party.isLocked()) {
                status = LocaleLoader.getString("Party.Status.Unlocked");
            }

            player.sendMessage(LocaleLoader.getString("Commands.Party.Header"));
            player.sendMessage(LocaleLoader.getString("Commands.Party.Status", party.getName(), status));

            boolean xpShareEnabled = Config.getInstance().getExpShareEnabled();
            boolean itemShareEnabled = Config.getInstance().getItemShareEnabled();

            if (xpShareEnabled) {
                ExpShare = LocaleLoader.getString("Commands.Party.ExpShare", party.getXpShareMode().toString());
            }

            if (itemShareEnabled) {
                ItemShare = LocaleLoader.getString("Commands.Party.ItemShare", party.getItemShareMode().toString());
            }

            if (xpShareEnabled && itemShareEnabled) {
                Split = ChatColor.DARK_GRAY + " || ";
            }

            if (xpShareEnabled || itemShareEnabled) {
                player.sendMessage(LocaleLoader.getString("Commands.Party.ShareMode") + ExpShare + Split + ItemShare);
            }

            player.sendMessage(LocaleLoader.getString("Commands.Party.Members.Header"));
            player.sendMessage(LocaleLoader.getString("Commands.Party.MembersNear", membersNear, membersOnline));
            player.sendMessage(LocaleLoader.getString("Commands.Party.Members", tempList));
        }
        else {
            return printUsage();
        }

        return true;
    }

    private boolean printHelp() {
        player.sendMessage(LocaleLoader.getString("Party.Help.3"));
        player.sendMessage(LocaleLoader.getString("Party.Help.1"));
        player.sendMessage(LocaleLoader.getString("Party.Help.4"));
        player.sendMessage(LocaleLoader.getString("Party.Help.5"));
        player.sendMessage(LocaleLoader.getString("Party.Help.6"));
        player.sendMessage(LocaleLoader.getString("Party.Help.7"));
        player.sendMessage(LocaleLoader.getString("Party.Help.8"));
        return true;
    }

    /**
     * Lock the current party
     */
    private boolean lock() {
        if (CommandHelper.noCommandPermissions(player, "mcmmo.commands.party.lock")) {
            return true;
        }

        String playerName = player.getName();
        Party party = mcMMOPlayer.getParty();

        if (party == null) {
            player.sendMessage("Commands.Party.None");
            return true;
        }

        if (!party.getLeader().equals(playerName)) {
            player.sendMessage(LocaleLoader.getString("Party.NotOwner"));
            return true;
        }

        if (party.isLocked()) {
            player.sendMessage(LocaleLoader.getString("Party.IsLocked"));
        }
        else {
            party.setLocked(true);
            player.sendMessage(LocaleLoader.getString("Party.Locked"));
        }

        return true;
    }

    /**
     * Unlock the current party
     */
    private boolean unlock() {
        if (CommandHelper.noCommandPermissions(player, "mcmmo.commands.party.unlock")) {
            return true;
        }

        String playerName = player.getName();
        Party party = mcMMOPlayer.getParty();

        if (party == null) {
            player.sendMessage("Commands.Party.None");
            return true;
        }

        if (!party.getLeader().equals(playerName)) {
            player.sendMessage(LocaleLoader.getString("Party.NotOwner"));
            return true;
        }

        if (!party.isLocked()) {
            player.sendMessage(LocaleLoader.getString("Party.IsntLocked"));
        }
        else {
            party.setLocked(false);
            player.sendMessage(LocaleLoader.getString("Party.Unlocked"));
        }
        return true;
    }

    private boolean changePassword(String[] args) {
        if (CommandHelper.noCommandPermissions(player, "mcmmo.commands.party.password")) {
            return true;
        }

        String playerName = player.getName();
        Party party = mcMMOPlayer.getParty();

        if (!party.getLeader().equals(playerName)) {
            player.sendMessage(LocaleLoader.getString("Party.NotOwner"));
            return true;
        }

        if (args.length < 2) {
            party.setLocked(true);
            party.setPassword(null);
            player.sendMessage(LocaleLoader.getString("Party.Password.Removed"));
            return true;
        }

        party.setLocked(true);
        party.setPassword(args[1]);
        player.sendMessage(LocaleLoader.getString("Party.Password.Set", args[1]));

        return true;
    }

    /**
     * Rename the current party
     */
    private boolean rename(String[] args) {
        if (CommandHelper.noCommandPermissions(player, "mcmmo.commands.party.rename")) {
            return true;
        }

        String playerName = player.getName();
        Party party = mcMMOPlayer.getParty();
        String leader = party.getLeader();

        if (party.getLeader().equals(playerName)) {
            if (args.length < 2) {
                player.sendMessage(LocaleLoader.getString("Commands.Usage.2", "party", "rename", "<" + LocaleLoader.getString("Commands.Usage.PartyName") + ">"));
                return true;
            }

            String newPartyName = args[1];

            // This is to prevent party leaders from spamming other players with the rename message
            if (!party.getName().equals(newPartyName)) {
                Party newParty = PartyManager.getParty(newPartyName);

                // Check to see if the party exists, and if it does cancel renaming the party
                if (newParty != null) {
                    player.sendMessage(LocaleLoader.getString("Commands.Party.AlreadyExists", newPartyName));
                    return true;
                }

                for (Player onlineMembers : party.getOnlineMembers()) {
                    McMMOPartyChangeEvent event = new McMMOPartyChangeEvent(onlineMembers, party.getName(), newPartyName, EventReason.CHANGED_PARTIES);
                    mcMMO.p.getServer().getPluginManager().callEvent(event);

                    if (event.isCancelled()) {
                        return true;
                    }

                    if (!onlineMembers.getName().equals(leader)) {
                        onlineMembers.sendMessage(LocaleLoader.getString("Party.InformedOnNameChange", leader, newPartyName));
                    }
                }

                party.setName(newPartyName);
            }

            player.sendMessage(LocaleLoader.getString("Commands.Party.Rename", newPartyName));
        }
        else {
            player.sendMessage(LocaleLoader.getString("Party.NotOwner"));
        }

        return true;
    }
}
