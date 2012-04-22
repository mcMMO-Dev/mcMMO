package com.gmail.nossr50.commands.party;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.Users;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.commands.CommandHelper;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.events.party.McMMOPartyChangeEvent;
import com.gmail.nossr50.events.party.McMMOPartyChangeEvent.EventReason;
import com.gmail.nossr50.locale.mcLocale;
import com.gmail.nossr50.party.Party;

public class PartyCommand implements CommandExecutor {
    private final mcMMO plugin;

    public PartyCommand (mcMMO plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (CommandHelper.noConsoleUsage(sender)) {
            return true;
        }

        if (CommandHelper.noCommandPermissions(sender, "mcmmo.commands.party")) {
            return true;
        }

        Player player = (Player) sender;
        String playerName = player.getName();
        PlayerProfile PP = Users.getProfile(player);

        Party partyInstance = Party.getInstance();
        boolean inParty = PP.inParty();
        String partyName = PP.getParty();
        boolean isLeader = partyInstance.isPartyLeader(playerName, partyName);

        if (PP.inParty() && (!partyInstance.isParty(PP.getParty()) || !partyInstance.isInParty(player, PP))) {
            partyInstance.addToParty(player, PP, PP.getParty(), false, null);
        }

        switch (args.length) {
        case 0:
            if (!inParty) {
                player.sendMessage(mcLocale.getString("Party.Help.0"));
                player.sendMessage(mcLocale.getString("Party.Help.1"));
                player.sendMessage(mcLocale.getString("Party.Help.2"));
            }
            else {
                String tempList = "";

                for (Player p : partyInstance.getAllMembers(player)) {
                    if (p.equals(partyInstance.getPartyLeader(partyName))) {
                        tempList = ChatColor.GOLD + p.getName();
                    }
                    else {
                        tempList = ChatColor.WHITE + p.getName();
                    }
                }

                if (isLeader) {
                    tempList += ChatColor.GOLD + playerName;
                }
                else {
                    tempList += ChatColor.WHITE + playerName;
                }

                player.sendMessage(mcLocale.getString("Commands.Party.InParty", new Object[] { partyName }));
                player.sendMessage(mcLocale.getString("Commands.Party.Members", new Object[] { tempList }));
            }

            return true;

        case 1:
            if (!partyInstance.isParty(args[0])) {
                sender.sendMessage(mcLocale.getString("Party.InvalidName"));
                return true;
            }

            if (args[0].equalsIgnoreCase("q")) {
                if (inParty) {
                    McMMOPartyChangeEvent event = new McMMOPartyChangeEvent(player, partyName, null, EventReason.LEFT_PARTY);
                    plugin.getServer().getPluginManager().callEvent(event);

                    if (event.isCancelled()) {
                        return true;
                    }

                    partyInstance.removeFromParty(player, PP);
                    player.sendMessage(mcLocale.getString("Commands.Party.Leave"));
                }
                else {
                    player.sendMessage("Commands.Party.None");
                }
            }
            else if (args[0].equals("?")) {
                player.sendMessage(mcLocale.getString("Party.Help.3"));
                player.sendMessage(mcLocale.getString("Party.Help.1"));
                player.sendMessage(mcLocale.getString("Party.Help.4"));
                player.sendMessage(mcLocale.getString("Party.Help.5"));
                player.sendMessage(mcLocale.getString("Party.Help.6"));
                player.sendMessage(mcLocale.getString("Party.Help.7"));
            }
            else if (args[0].equalsIgnoreCase("lock")) {
                if (inParty) {
                    if (isLeader) {
                        if (partyInstance.isPartyLocked(partyName)) {
                            player.sendMessage(mcLocale.getString("Party.IsLocked"));
                        }
                        else {
                            partyInstance.lockParty(partyName);
                            player.sendMessage(mcLocale.getString("Party.Locked"));
                        }
                    }
                    else {
                        player.sendMessage(mcLocale.getString("Party.NotOwner"));
                    }
                }
                else {
                    player.sendMessage("Commands.Party.None");
                }
            }
            else if (args[0].equalsIgnoreCase("unlock")) {
                if (inParty) {
                    if (isLeader) {
                        if (!partyInstance.isPartyLocked(partyName)) {
                            player.sendMessage(mcLocale.getString("Party.IsntLocked"));
                        }
                        else {
                            partyInstance.unlockParty(partyName);
                            player.sendMessage(mcLocale.getString("Party.Unlocked"));
                        }
                    }
                    else {
                        player.sendMessage(mcLocale.getString("Party.NotOwner"));
                    }
                }
                else {
                    player.sendMessage("Commands.Party.None");
                }
            }
            else {
                if (inParty) {
                    McMMOPartyChangeEvent event = new McMMOPartyChangeEvent(player, partyName, args[0], EventReason.CHANGED_PARTIES);
                    plugin.getServer().getPluginManager().callEvent(event);

                    if (event.isCancelled()) {
                        return true;
                    }

                    partyInstance.removeFromParty(player, PP);
                }
                else {
                    McMMOPartyChangeEvent event = new McMMOPartyChangeEvent(player, null, args[0], EventReason.JOINED_PARTY);
                    plugin.getServer().getPluginManager().callEvent(event);

                    if (event.isCancelled()) {
                        return true;
                    }
                }

                partyInstance.addToParty(player, PP, args[0], false, null);
            }

            return true;

        case 2:
            if (!partyInstance.isParty(args[0])) {
                sender.sendMessage(mcLocale.getString("Party.InvalidName"));
                return true;
            }

            if (PP.inParty()) {
                if (args[0].equalsIgnoreCase("password")) {
                    if (isLeader) {
                        if (!partyInstance.isPartyLocked(partyName)) {
                            partyInstance.lockParty(partyName);
                        }

                        partyInstance.setPartyPassword(partyName, args[1]);
                        player.sendMessage(mcLocale.getString("Party.PasswordSet", new Object[] { args[1] }));
                    }
                    else {
                        player.sendMessage(mcLocale.getString("Party.NotOwner"));
                    }
                }
                else if (args[0].equalsIgnoreCase("kick")) {
                    if (isLeader) {
                        Player target = plugin.getServer().getPlayer(args[1]);

                        if (target == null) {
                            player.sendMessage(mcLocale.getString("Party.Player.Invalid"));
                            return true;
                        }

                        PlayerProfile PPt = Users.getProfile(target);
                        String targetName = target.getName();

                        if (!partyInstance.inSameParty(player, (Player) target)) {
                            player.sendMessage(mcLocale.getString("Party.NotInYourParty", new Object[] { targetName }));
                            return true;
                        }

                        else {
                            McMMOPartyChangeEvent event = new McMMOPartyChangeEvent(player, partyName, null, EventReason.KICKED_FROM_PARTY);
                            plugin.getServer().getPluginManager().callEvent(event);

                            if (event.isCancelled()) {
                                return true;
                            }

                            partyInstance.removeFromParty(target, PPt);
                            target.sendMessage(mcLocale.getString("Commands.Party.Kick", new Object[] { partyName }));
                        }
                    }
                    else {
                        player.sendMessage(mcLocale.getString("Party.NotOwner"));
                    }
                }
                else if (args[0].equalsIgnoreCase("owner")) {
                    if (isLeader) {
                        Player target = plugin.getServer().getPlayer(args[1]);

                        if (target == null) {
                            player.sendMessage(mcLocale.getString("Party.Player.Invalid"));
                            return true;
                        }

                        String targetName = target.getName();

                        if (!partyInstance.inSameParty(player, (Player) target)) {
                            player.sendMessage(mcLocale.getString("Party.NotInYourParty", new Object[] { targetName }));
                            return true;
                        }

                        else {
                            partyInstance.setPartyLeader(partyName, targetName);
                        }
                    }
                    else {
                        player.sendMessage(mcLocale.getString("Party.NotOwner"));
                    }
                }
                else {
                    McMMOPartyChangeEvent event = new McMMOPartyChangeEvent(player, partyName, args[0], EventReason.CHANGED_PARTIES);
                    plugin.getServer().getPluginManager().callEvent(event);

                    if (event.isCancelled()) {
                        return true;
                    }

                    partyInstance.removeFromParty(player, PP);
                    partyInstance.addToParty(player, PP, args[0], false, args[1]);
                }
            }
            else {
                McMMOPartyChangeEvent event = new McMMOPartyChangeEvent(player, null, args[0], EventReason.JOINED_PARTY);
                plugin.getServer().getPluginManager().callEvent(event);

                if (event.isCancelled()) {
                    return true;
                }

                partyInstance.addToParty(player, PP, args[0], false, args[1]);
            }

            return true;

        default:
            player.sendMessage(mcLocale.getString("Party.Help.0"));
            player.sendMessage(mcLocale.getString("Party.Help.1"));
            player.sendMessage(mcLocale.getString("Party.Help.2"));
            return true;
        }
    }
}
