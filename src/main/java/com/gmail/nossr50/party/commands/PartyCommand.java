package com.gmail.nossr50.party.commands;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.commands.CommandHelper;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.events.party.McMMOPartyChangeEvent;
import com.gmail.nossr50.events.party.McMMOPartyChangeEvent.EventReason;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.party.Party;
import com.gmail.nossr50.party.PartyManager;
import com.gmail.nossr50.party.ShareHandler;
import com.gmail.nossr50.util.Users;

public class PartyCommand implements CommandExecutor {
    private Player player;
    private PlayerProfile playerProfile;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (CommandHelper.noConsoleUsage(sender)) {
            return true;
        }

        if (CommandHelper.noCommandPermissions(sender, "mcmmo.commands.party")) {
            return true;
        }

        this.player = (Player) sender;
        this.playerProfile = Users.getProfile(player);

        if (args.length < 1 || args[0].equalsIgnoreCase("info")) {
            return party();
        }

        if (args[0].equalsIgnoreCase("join")) {
            return join(args);
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

        if (playerProfile.inParty()) {
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
                return kick(args[1]);
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
        if (playerProfile.inParty()) {
            Party party = playerProfile.getParty();

            Server server = mcMMO.p.getServer();
            String leader = party.getLeader();
            StringBuffer tempList = new StringBuffer();

            int membersNear = PartyManager.getNearMembers(player, party, ShareHandler.partyShareRange).size();
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
                tempList.append(otherPlayerName + " ");
            }

            String status = LocaleLoader.getString("Party.Status.Locked");
            if (!party.isLocked()) {
                status = LocaleLoader.getString("Party.Status.Unlocked");
            }

            player.sendMessage(LocaleLoader.getString("Commands.Party.Header"));
            player.sendMessage(LocaleLoader.getString("Commands.Party.Status", new Object[] {party.getName(), status}));

            if (ShareHandler.expShareEnabled) {
                ExpShare = LocaleLoader.getString("Commands.Party.ExpShare", new Object[] { party.getExpShareMode() });
            }
            if (ShareHandler.itemShareEnabled) {
                ItemShare = LocaleLoader.getString("Commands.Party.ItemShare", new Object[] { itemShareMode });
            }
            if (ShareHandler.expShareEnabled && ShareHandler.itemShareEnabled) {
                Split = ChatColor.DARK_GRAY + " || ";
            }
            if (ShareHandler.expShareEnabled || ShareHandler.itemShareEnabled) {
                player.sendMessage(LocaleLoader.getString("Commands.Party.ShareMode") + ExpShare + Split + ItemShare);
            }

            player.sendMessage(LocaleLoader.getString("Commands.Party.Members.Header"));
            player.sendMessage(LocaleLoader.getString("Commands.Party.MembersNear", new Object[] { membersNear, membersOnline }));
            player.sendMessage(LocaleLoader.getString("Commands.Party.Members", new Object[] {tempList}));
        }
        else {
            return printUsage();
        }
        return true;
    }

    private boolean join(String[] args) {
        if (CommandHelper.noCommandPermissions(player, "mcmmo.commands.party.join")) {
            return true;
        }

        String playerName = player.getName();
        Party party = playerProfile.getParty();

        if (args.length < 2) {
            player.sendMessage(LocaleLoader.getString("Party.Help.0"));
            return true;
        }

        Player target = mcMMO.p.getServer().getPlayer(args[1]);

        if (target == null) {
            player.sendMessage(LocaleLoader.getString("Party.NotOnline", new Object[] {args[1]}));
            return false;
        }

        if (!Users.getProfile(target).inParty()) {
            player.sendMessage(LocaleLoader.getString("Party.PlayerNotInParty", new Object[] {args[1]}));
            return false;
        }

        if (player.equals(target)) {
            player.sendMessage(LocaleLoader.getString("Party.Join.Self"));
            return true;
        }

        if (party != null && party.equals(Users.getProfile(target).getParty())) {
            player.sendMessage(LocaleLoader.getString("Party.Join.Self"));
            return true;
        }

        String password = null;
        
        if (args.length > 2) {
            password = args[2];
        }

        String partyTarget = PartyManager.getPlayerParty(target.getName()).getName();
        Party newParty = PartyManager.getParty(args[0]);

        // Check to see if the party exists, and if it does, can the player join it?
        if (newParty != null && !PartyManager.checkJoinability(player, newParty, null)) {
            return true; // End before any event is fired.
        }

        if (party != null) {
            McMMOPartyChangeEvent event = new McMMOPartyChangeEvent(player, party.getName(), partyTarget, EventReason.CHANGED_PARTIES);
            mcMMO.p.getServer().getPluginManager().callEvent(event);

            if (event.isCancelled()) {
                return true;
            }

            PartyManager.removeFromParty(playerName, party);
        }
        else {
            McMMOPartyChangeEvent event = new McMMOPartyChangeEvent(player, null, partyTarget, EventReason.JOINED_PARTY);
            mcMMO.p.getServer().getPluginManager().callEvent(event);

            if (event.isCancelled()) {
                return true;
            }
        }

        PartyManager.joinParty(player, playerProfile, partyTarget, password);
        return true;
    }

    private boolean accept() {
        if (CommandHelper.noCommandPermissions(player, "mcmmo.commands.party.accept")) {
            return true;
        }

        if (playerProfile.hasPartyInvite()) {
            if (playerProfile.inParty()) {
                Party party = playerProfile.getParty();
                McMMOPartyChangeEvent event = new McMMOPartyChangeEvent(player, party.getName(), playerProfile.getInvite().getName(), EventReason.CHANGED_PARTIES);

                mcMMO.p.getServer().getPluginManager().callEvent(event);

                if (event.isCancelled()) {
                    return true;
                }

                PartyManager.removeFromParty(player.getName(), party);
            }
            else {
                McMMOPartyChangeEvent event = new McMMOPartyChangeEvent(player, null, playerProfile.getInvite().getName(), EventReason.JOINED_PARTY);
                mcMMO.p.getServer().getPluginManager().callEvent(event);

                if (event.isCancelled()) {
                    return true;
                }
            }

            PartyManager.joinInvitedParty(player, playerProfile);
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
        Party party = playerProfile.getParty();

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
            player.sendMessage(LocaleLoader.getString("Commands.Party.AlreadyExists", new Object[] {partyname}));
            return true;
        }

        if (playerProfile.inParty()) {
            String oldPartyName = party.getName();
            McMMOPartyChangeEvent event = new McMMOPartyChangeEvent(player, oldPartyName, partyname, EventReason.CHANGED_PARTIES);
            mcMMO.p.getServer().getPluginManager().callEvent(event);

            if (event.isCancelled()) {
                return true;
            }

            PartyManager.removeFromParty(playerName, party);
            PartyManager.createParty(player, playerProfile, partyname, password);
        }
        else {
            McMMOPartyChangeEvent event = new McMMOPartyChangeEvent(player, null, partyname, EventReason.JOINED_PARTY);
            mcMMO.p.getServer().getPluginManager().callEvent(event);

            if (event.isCancelled()) {
                return true;
            }

            PartyManager.createParty(player, playerProfile, partyname, password);
            return true;
        }

        return true;
    }

    private boolean quit() {
        if (CommandHelper.noCommandPermissions(player, "mcmmo.commands.party.quit")) {
            return true;
        }

        String playerName = player.getName();
        Party party = playerProfile.getParty();

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
        if (CommandHelper.noCommandPermissions(player, "mcmmo.commands.party.expshare"))
            return true;
        String playerName = player.getName();
        PlayerProfile playerProfile = Users.getProfile(player);
        Party party = playerProfile.getParty();

        if (args.length < 2) {
            player.sendMessage(LocaleLoader.getString("Commands.Usage.2", new Object[] {"party", "expshare", "[sharemode]"}));
            return true;
        }

        if (party.getLeader().equals(playerName)) {
            if(args[1].equalsIgnoreCase("noshare") || args[1].equalsIgnoreCase("none") || args[1].equalsIgnoreCase("false")) {
                party.setExpShareMode("NONE");
                for (Player onlineMembers : party.getOnlineMembers()) {
                    onlineMembers.sendMessage(LocaleLoader.getString("Commands.Party.SetSharing", new Object[] {LocaleLoader.getString("Party.ShareType.Exp"), LocaleLoader.getString("Party.ShareMode.NoShare")}));
                }
            } else if(args[1].equalsIgnoreCase("equal") || args[1].equalsIgnoreCase("even")) {
                party.setExpShareMode("EQUAL");
                for (Player onlineMembers : party.getOnlineMembers()) {
                    onlineMembers.sendMessage(LocaleLoader.getString("Commands.Party.SetSharing", new Object[] {LocaleLoader.getString("Party.ShareType.Exp"), LocaleLoader.getString("Party.ShareMode.Equal")}));
                }
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
            if (!playerProfile.inParty()) {
                player.sendMessage(LocaleLoader.getString("Commands.Party.None"));
                return true;
            }

            Player target = mcMMO.p.getServer().getPlayer(args[1]);

            if (target != null) {
                if (PartyManager.inSameParty(player, target)) {
                    player.sendMessage(LocaleLoader.getString("Party.Player.InSameParty"));
                    return true;
                }
                if (PartyManager.canInvite(player, playerProfile)) {
                    Party party = playerProfile.getParty();

                    Users.getProfile(target).setInvite(party);
                    player.sendMessage(LocaleLoader.getString("Commands.Invite.Success"));
                    target.sendMessage(LocaleLoader.getString("Commands.Party.Invite.0", new Object[] {party.getName(), player.getName()}));
                    target.sendMessage(LocaleLoader.getString("Commands.Party.Invite.1"));
                    return true;
                }

                player.sendMessage(LocaleLoader.getString("Party.Locked"));
                return true;
            }

            player.sendMessage(LocaleLoader.getString("Party.Player.Invalid"));
            return true;

        default:
            player.sendMessage(LocaleLoader.getString("Commands.Usage.2", new Object[] {"party", "invite", "<" + LocaleLoader.getString("Commands.Usage.Player") + ">"}));
            return true;
        }
    }

    /**
     * Kick a party member
     */
    private boolean kick(String targetName) {
        if (CommandHelper.noCommandPermissions(player, "mcmmo.commands.party.kick")) {
            return true;
        }

        String playerName = player.getName();
        Party party = playerProfile.getParty();

        if (party.getLeader().equals(playerName)) {
            if (!party.getMembers().contains(targetName)) {
                player.sendMessage(LocaleLoader.getString("Party.NotInYourParty", new Object[] {targetName}));
                return true;
            }

            Player target = mcMMO.p.getServer().getOfflinePlayer(targetName).getPlayer();

            if (target != null) {
                String partyName = party.getName();
                McMMOPartyChangeEvent event = new McMMOPartyChangeEvent(target, partyName, null, EventReason.KICKED_FROM_PARTY);

                mcMMO.p.getServer().getPluginManager().callEvent(event);

                if (event.isCancelled()) {
                    return true;
                }

                target.sendMessage(LocaleLoader.getString("Commands.Party.Kick", new Object[] {partyName}));
            }

            PartyManager.removeFromParty(targetName, party);
        }
        else {
            player.sendMessage(LocaleLoader.getString("Party.NotOwner"));
        }

        return true;
    }

    /**
     * Disband the current party, kicks out all party members.
     */
    private boolean disband() {
        if (CommandHelper.noCommandPermissions(player, "mcmmo.commands.party.disband")) {
            return true;
        }

        String playerName = player.getName();
        Party party = playerProfile.getParty();

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
        Party party = playerProfile.getParty();

        if (args.length < 2) {
            player.sendMessage(LocaleLoader.getString("Commands.Usage.2", new Object[] {"party", "owner", "[" + LocaleLoader.getString("Commands.Usage.Player") + "]"}));
            return true;
        }

        if (party.getLeader().equals(playerName)) {
            if (!party.getMembers().contains(args[1])) {
                player.sendMessage(LocaleLoader.getString("Party.NotInYourParty", new Object[] {args[1]}));
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
        Party party = playerProfile.getParty();

        if (party != null) {
            if (party.getLeader().equals(playerName)) {
                if (party.isLocked()) {
                    player.sendMessage(LocaleLoader.getString("Party.IsLocked"));
                }
                else {
                    party.setLocked(true);
                    player.sendMessage(LocaleLoader.getString("Party.Locked"));
                }
            }
            else {
                player.sendMessage(LocaleLoader.getString("Party.NotOwner"));
            }
        }
        else {
            player.sendMessage("Commands.Party.None");
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
        Party party = playerProfile.getParty();

        if (party != null) {
            if (party.getLeader().equals(playerName)) {
                if (!party.isLocked()) {
                    player.sendMessage(LocaleLoader.getString("Party.IsntLocked"));
                }
                else {
                    party.setLocked(false);
                    player.sendMessage(LocaleLoader.getString("Party.Unlocked"));
                }
            }
            else {
                player.sendMessage(LocaleLoader.getString("Party.NotOwner"));
            }
        }
        else {
            player.sendMessage("Commands.Party.None");
        }

        return true;
    }

    private boolean changePassword(String[] args) {
        if (CommandHelper.noCommandPermissions(player, "mcmmo.commands.party.password")) {
            return true;
        }

        String playerName = player.getName();
        Party party = playerProfile.getParty();

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
        player.sendMessage(LocaleLoader.getString("Party.Password.Set", new Object[] {args[1]}));

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
        Party party = playerProfile.getParty();
        String leader = party.getLeader();

        if (party.getLeader().equals(playerName)) {
            if (args.length < 2) {
                player.sendMessage(LocaleLoader.getString("Commands.Usage.2", new Object[] {"party", "rename", "<" + LocaleLoader.getString("Commands.Usage.PartyName") + ">"}));
                return true;
            }

            String newPartyName = args[1];

            // This is to prevent party leaders from spamming other players with the rename message
            if (!party.getName().equals(newPartyName)) {
                Party newParty = PartyManager.getParty(newPartyName);

                // Check to see if the party exists, and if it does cancel renaming the party
                if (newParty != null) {
                    player.sendMessage(LocaleLoader.getString("Commands.Party.AlreadyExists", new Object[] {newPartyName}));
                    return true;
                }

                for (Player onlineMembers : party.getOnlineMembers()) {
                    McMMOPartyChangeEvent event = new McMMOPartyChangeEvent(onlineMembers, party.getName(), newPartyName, EventReason.CHANGED_PARTIES);
                    mcMMO.p.getServer().getPluginManager().callEvent(event);

                    if (event.isCancelled()) {
                        return true;
                    }

                    if (!onlineMembers.getName().equals(leader)) {
                        onlineMembers.sendMessage(LocaleLoader.getString("Party.InformedOnNameChange", new Object[] {leader, newPartyName}));
                    }
                }

                party.setName(newPartyName);
            }

            player.sendMessage(LocaleLoader.getString("Commands.Party.Rename", new Object[] {newPartyName}));
        }
        else {
            player.sendMessage(LocaleLoader.getString("Party.NotOwner"));
        }

        return true;
    }
}
