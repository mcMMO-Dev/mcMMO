package com.gmail.nossr50.party.commands;

import org.bukkit.ChatColor;
import org.bukkit.Server;
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
import com.gmail.nossr50.party.ShareHandler;
import com.gmail.nossr50.util.Users;

public class PartyCommand implements CommandExecutor {
    private McMMOPlayer mcMMOPlayer;
    private Player player;

    private CommandExecutor partyJoinCommand = new PartyJoinCommand();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (CommandHelper.noConsoleUsage(sender)) {
            return true;
        }

        if (CommandHelper.noCommandPermissions(sender, "mcmmo.commands.party")) {
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
            return accept();
        }
        else if (args[0].equalsIgnoreCase("create")) {
            return create(args);
        }
        else if (args[0].equalsIgnoreCase("?") || args[0].equalsIgnoreCase("help")) {
            return printHelp();
        }

        if (mcMMOPlayer.inParty()) {
            if (args[0].equalsIgnoreCase("quit") || args[0].equalsIgnoreCase("q") || args[0].equalsIgnoreCase("leave")) {
                return quit();
            }
            else if (args[0].equalsIgnoreCase("expshare")) {
                return shareExp(args);
            }
            else if (args[0].equalsIgnoreCase("itemshare")) {
                return shareItem();
            }
            else if (args[0].equalsIgnoreCase("invite")) {
                return invite(args);
            }
            else if (args[0].equalsIgnoreCase("kick")) {
                return kick(args);
            }
            else if (args[0].equalsIgnoreCase("disband")) {
                return disband();
            }
            else if (args[0].equalsIgnoreCase("owner")) {
                return changeOwner(args);
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

        player.sendMessage(LocaleLoader.getString("Commands.Party.None"));
        return printUsage();
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

            Server server = mcMMO.p.getServer();
            String leader = party.getLeader();
            StringBuilder tempList = new StringBuilder();

            int membersNear = PartyManager.getNearMembers(player, party, Config.getInstance().getPartyShareRange()).size();
            int membersOnline = party.getOnlineMembers().size() - 1;

            String ItemShare = "";
            String ExpShare = "";
            String Split = "";
            String itemShareMode = "NONE";

            for (String otherPlayerName : party.getMembers()) {
                if (leader.equals(otherPlayerName)) {
                    tempList.append(ChatColor.GOLD);
                }
                else if (server.getPlayer(otherPlayerName) != null) {
                    tempList.append(ChatColor.WHITE);
                }
                else {
                    tempList.append(ChatColor.GRAY);
                }
                tempList.append(otherPlayerName).append(" ");
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
                ItemShare = LocaleLoader.getString("Commands.Party.ItemShare", itemShareMode);
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

    private boolean accept() {
        if (CommandHelper.noCommandPermissions(player, "mcmmo.commands.party.accept")) {
            return true;
        }

        if (mcMMOPlayer.hasPartyInvite()) {
            if (mcMMOPlayer.inParty()) {
                Party party = mcMMOPlayer.getParty();
                McMMOPartyChangeEvent event = new McMMOPartyChangeEvent(player, party.getName(), mcMMOPlayer.getInvite().getName(), EventReason.CHANGED_PARTIES);

                mcMMO.p.getServer().getPluginManager().callEvent(event);

                if (event.isCancelled()) {
                    return true;
                }

                PartyManager.removeFromParty(player.getName(), party);
            }
            else {
                McMMOPartyChangeEvent event = new McMMOPartyChangeEvent(player, null, mcMMOPlayer.getInvite().getName(), EventReason.JOINED_PARTY);
                mcMMO.p.getServer().getPluginManager().callEvent(event);

                if (event.isCancelled()) {
                    return true;
                }
            }

            PartyManager.joinInvitedParty(player, mcMMOPlayer);
        }
        else {
            player.sendMessage(LocaleLoader.getString("mcMMO.NoInvites"));
        }

        return true;
    }

    private boolean create(String[] args) {
        if (CommandHelper.noCommandPermissions(player, "mcmmo.commands.party.create")) {
            return true;
        }

        String playerName = player.getName();
        Party party = mcMMOPlayer.getParty();

        if (args.length < 2) {
            player.sendMessage(LocaleLoader.getString("Party.Help.1"));
            return true;
        }

        String partyname = args[1];
        String password = null;

        if (args.length > 2) {
            password = args[2];
        }

        Party newParty = PartyManager.getParty(partyname);
        // Check to see if the party exists, and if it does cancel creating a new party
        if (newParty != null) {
            player.sendMessage(LocaleLoader.getString("Commands.Party.AlreadyExists", partyname));
            return true;
        }

        if (mcMMOPlayer.inParty()) {
            String oldPartyName = party.getName();
            McMMOPartyChangeEvent event = new McMMOPartyChangeEvent(player, oldPartyName, partyname, EventReason.CHANGED_PARTIES);
            mcMMO.p.getServer().getPluginManager().callEvent(event);

            if (event.isCancelled()) {
                return true;
            }

            PartyManager.removeFromParty(playerName, party);
            PartyManager.createParty(player, mcMMOPlayer, partyname, password);
        }
        else {
            McMMOPartyChangeEvent event = new McMMOPartyChangeEvent(player, null, partyname, EventReason.JOINED_PARTY);
            mcMMO.p.getServer().getPluginManager().callEvent(event);

            if (event.isCancelled()) {
                return true;
            }

            PartyManager.createParty(player, mcMMOPlayer, partyname, password);
            return true;
        }

        return true;
    }

    private boolean quit() {
        if (CommandHelper.noCommandPermissions(player, "mcmmo.commands.party.quit")) {
            return true;
        }

        String playerName = player.getName();
        Party party = mcMMOPlayer.getParty();

        if (party != null) {
            McMMOPartyChangeEvent event = new McMMOPartyChangeEvent(player, party.getName(), null, EventReason.LEFT_PARTY);
            mcMMO.p.getServer().getPluginManager().callEvent(event);

            if (event.isCancelled()) {
                return true;
            }
            PartyManager.removeFromParty(playerName, party);
            player.sendMessage(LocaleLoader.getString("Commands.Party.Leave"));
        }
        else {
            player.sendMessage(LocaleLoader.getString("Commands.Party.None"));
        }

        return false;
    }

    private boolean shareExp(String[] args) {
        if (CommandHelper.noCommandPermissions(player, "mcmmo.commands.party.expshare")) {
            return true;
        }

        if (args.length < 2) {
            player.sendMessage(LocaleLoader.getString("Commands.Usage.2", "party", "expshare", "[NONE / EQUAL]"));
            return true;
        }

        String playerName = player.getName();
        Party party = mcMMOPlayer.getParty();

        if (party.getLeader().equals(playerName)) {
            if (args[1].equalsIgnoreCase("none") || args[1].equalsIgnoreCase("false")) {
                party.setXpShareMode(ShareHandler.XpShareMode.NONE);

                for (Player onlineMembers : party.getOnlineMembers()) {
                    onlineMembers.sendMessage(LocaleLoader.getString("Commands.Party.SetSharing", LocaleLoader.getString("Party.ShareType.Exp"), LocaleLoader.getString("Party.ShareMode.NoShare")));
                }
            }
            else if (args[1].equalsIgnoreCase("equal") || args[1].equalsIgnoreCase("even")) {
                party.setXpShareMode(ShareHandler.XpShareMode.EQUAL);

                for (Player onlineMembers : party.getOnlineMembers()) {
                    onlineMembers.sendMessage(LocaleLoader.getString("Commands.Party.SetSharing", LocaleLoader.getString("Party.ShareType.Exp"), LocaleLoader.getString("Party.ShareMode.Equal")));
                }
            }
            else {
                player.sendMessage(LocaleLoader.getString("Commands.Usage.2", "party", "expshare", "[NONE / EQUAL]"));
                return true;
            }
        }

        return true;
    }

    private boolean shareItem() {
        return (!CommandHelper.noCommandPermissions(player, "mcmmo.commands.party.itemshare"));
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

    private boolean invite(String[] args) {
        if (CommandHelper.noCommandPermissions(player, "mcmmo.commands.party.invite")) {
            return true;
        }

        switch (args.length) {
        case 2:
            if (!mcMMOPlayer.inParty()) {
                player.sendMessage(LocaleLoader.getString("Commands.Party.None"));
                return true;
            }

            Player target = mcMMO.p.getServer().getPlayer(args[1]);

            if (target != null) {
                if (PartyManager.inSameParty(player, target)) {
                    player.sendMessage(LocaleLoader.getString("Party.Player.InSameParty", target.getName()));
                    return true;
                }

                if (PartyManager.canInvite(player, mcMMOPlayer)) {
                    Party party = mcMMOPlayer.getParty();

                    Users.getPlayer(target).setInvite(party);
                    player.sendMessage(LocaleLoader.getString("Commands.Invite.Success"));
                    target.sendMessage(LocaleLoader.getString("Commands.Party.Invite.0", party.getName(), player.getName()));
                    target.sendMessage(LocaleLoader.getString("Commands.Party.Invite.1"));
                    return true;
                }

                player.sendMessage(LocaleLoader.getString("Party.Locked"));
                return true;
            }

            player.sendMessage(LocaleLoader.getString("Party.Player.Invalid"));
            return true;

        default:
            player.sendMessage(LocaleLoader.getString("Commands.Usage.2", "party", "invite", "<" + LocaleLoader.getString("Commands.Usage.Player") + ">"));
            return true;
        }
    }

    /**
     * Kick a party member
     */
    private boolean kick(String[] args) {
        if (CommandHelper.noCommandPermissions(player, "mcmmo.commands.party.kick")) {
            return true;
        }

        switch (args.length) {
        case 2:
            String playerName = player.getName();
            Party party = mcMMOPlayer.getParty();

            if (party.getLeader().equals(playerName)) {
                if (!party.getMembers().contains(args[1])) {
                    player.sendMessage(LocaleLoader.getString("Party.NotInYourParty", args[1]));
                    return true;
                }

                Player target = mcMMO.p.getServer().getOfflinePlayer(args[1]).getPlayer();

                if (target != null) {
                    String partyName = party.getName();
                    McMMOPartyChangeEvent event = new McMMOPartyChangeEvent(target, partyName, null, EventReason.KICKED_FROM_PARTY);

                    mcMMO.p.getServer().getPluginManager().callEvent(event);

                    if (event.isCancelled()) {
                        return true;
                    }

                    target.sendMessage(LocaleLoader.getString("Commands.Party.Kick", partyName));
                }

                PartyManager.removeFromParty(args[1], party);
            }
            else {
                player.sendMessage(LocaleLoader.getString("Party.NotOwner"));
            }

            return true;

        default:
            player.sendMessage(LocaleLoader.getString("Commands.Usage.2", "party", "kick", "<" + LocaleLoader.getString("Commands.Usage.Player") + ">"));
            return true;
        }
    }

    /**
     * Disband the current party, kicks out all party members.
     */
    private boolean disband() {
        if (CommandHelper.noCommandPermissions(player, "mcmmo.commands.party.disband")) {
            return true;
        }

        String playerName = player.getName();
        Party party = mcMMOPlayer.getParty();

        if (party.getLeader().equals(playerName)) {
            for (Player onlineMembers : party.getOnlineMembers()) {
                McMMOPartyChangeEvent event = new McMMOPartyChangeEvent(onlineMembers, party.getName(), null, EventReason.KICKED_FROM_PARTY);
                mcMMO.p.getServer().getPluginManager().callEvent(event);

                if (event.isCancelled()) {
                    return true;
                }

                onlineMembers.sendMessage(LocaleLoader.getString("Party.Disband"));
            }

            PartyManager.disbandParty(party);
        }
        else {
            player.sendMessage(LocaleLoader.getString("Party.NotOwner"));
        }

        return true;
    }

    /**
     * Change the owner of the current party
     */
    private boolean changeOwner(String[] args) {
        if (CommandHelper.noCommandPermissions(player, "mcmmo.commands.party.owner")) {
            return true;
        }

        String playerName = player.getName();
        Party party = mcMMOPlayer.getParty();

        if (args.length < 2) {
            player.sendMessage(LocaleLoader.getString("Commands.Usage.2", "party", "owner", "[" + LocaleLoader.getString("Commands.Usage.Player") + "]"));
            return true;
        }

        if (party.getLeader().equals(playerName)) {
            if (!party.getMembers().contains(args[1])) {
                player.sendMessage(LocaleLoader.getString("Party.NotInYourParty", args[1]));
                return true;
            }

            PartyManager.setPartyLeader(args[1], party);
        }

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
