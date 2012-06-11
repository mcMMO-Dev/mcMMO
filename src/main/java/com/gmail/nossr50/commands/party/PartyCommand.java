package com.gmail.nossr50.commands.party;

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
import com.gmail.nossr50.util.Users;

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
        PlayerProfile playerProfile = Users.getProfile(player);

        PartyManager partyManagerInstance = PartyManager.getInstance();
        Party party = playerProfile.getParty();

        switch (args.length) {
        case 0:
            if (party == null) {
                player.sendMessage(LocaleLoader.getString("Party.Help.0"));
                player.sendMessage(LocaleLoader.getString("Party.Help.1"));
                player.sendMessage(LocaleLoader.getString("Party.Help.2"));
            }
            else {
                Server server = plugin.getServer();
                String leader = party.getLeader();
                StringBuffer tempList = new StringBuffer();

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

                player.sendMessage(LocaleLoader.getString("Commands.Party.InParty", new Object[] {party.getName()}));
                player.sendMessage(LocaleLoader.getString("Commands.Party.Members", new Object[] {tempList}));
            }

            return true;

        case 1:
            if (args[0].equalsIgnoreCase("q")) {
                if (party != null) {
                    McMMOPartyChangeEvent event = new McMMOPartyChangeEvent(player, party.getName(), null, EventReason.LEFT_PARTY);
                    plugin.getServer().getPluginManager().callEvent(event);

                    if (event.isCancelled()) {
                        return true;
                    }

                    partyManagerInstance.removeFromParty(playerName, party);
                    player.sendMessage(LocaleLoader.getString("Commands.Party.Leave"));
                }
                else {
                    player.sendMessage("Commands.Party.None");
                }
            }
            else if (args[0].equals("?")) {
                player.sendMessage(LocaleLoader.getString("Party.Help.3"));
                player.sendMessage(LocaleLoader.getString("Party.Help.1"));
                player.sendMessage(LocaleLoader.getString("Party.Help.4"));
                player.sendMessage(LocaleLoader.getString("Party.Help.5"));
                player.sendMessage(LocaleLoader.getString("Party.Help.6"));
                player.sendMessage(LocaleLoader.getString("Party.Help.7"));
            }
            else if (args[0].equalsIgnoreCase("lock")) {
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
            }
            else if (args[0].equalsIgnoreCase("unlock")) {
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
            }
            else {
                if (party != null) {
                    McMMOPartyChangeEvent event = new McMMOPartyChangeEvent(player, party.getName(), args[0], EventReason.CHANGED_PARTIES);
                    plugin.getServer().getPluginManager().callEvent(event);

                    if (event.isCancelled()) {
                        return true;
                    }

                    partyManagerInstance.removeFromParty(playerName, party);
                }
                else {
                    McMMOPartyChangeEvent event = new McMMOPartyChangeEvent(player, null, args[0], EventReason.JOINED_PARTY);
                    plugin.getServer().getPluginManager().callEvent(event);

                    if (event.isCancelled()) {
                        return true;
                    }
                }

                partyManagerInstance.joinParty(player, playerProfile, args[0], null);
            }

            return true;

        case 2:
            if (playerProfile.inParty()) {
                if (args[0].equalsIgnoreCase("password")) {
                    if (party.getLeader().equals(playerName)) {
                        party.setLocked(true);
                        party.setPassword(args[1]);
                        player.sendMessage(LocaleLoader.getString("Party.PasswordSet", new Object[] {args[1]}));
                    }
                    else {
                        player.sendMessage(LocaleLoader.getString("Party.NotOwner"));
                    }
                }
                else if (args[0].equalsIgnoreCase("kick")) {
                    if (party.getLeader().equals(playerName)) {
                        if (!party.getMembers().contains(args[1])) {
                            player.sendMessage(LocaleLoader.getString("Party.NotInYourParty", new Object[] {args[1]}));
                            return true;
                        }
                        else {
                            String partyName = party.getName();
                            McMMOPartyChangeEvent event = new McMMOPartyChangeEvent(player, partyName, null, EventReason.KICKED_FROM_PARTY);

                            plugin.getServer().getPluginManager().callEvent(event);

                            if (event.isCancelled()) {
                                return true;
                            }

                            partyManagerInstance.removeFromParty(args[1], party);
                        }
                    }
                    else {
                        player.sendMessage(LocaleLoader.getString("Party.NotOwner"));
                    }
                }
                else if (args[0].equalsIgnoreCase("owner")) {
                    if (party.getLeader().equals(playerName)) {
                        if (!party.getMembers().contains(args[1])) {
                            player.sendMessage(LocaleLoader.getString("Party.NotInYourParty", new Object[] {args[1]}));
                            return true;
                        }
                        else {
                            partyManagerInstance.setPartyLeader(args[1], party);
                        }
                    }
                }
                else {
                    McMMOPartyChangeEvent event = new McMMOPartyChangeEvent(player, party.getName(), args[0], EventReason.CHANGED_PARTIES);
                    plugin.getServer().getPluginManager().callEvent(event);

                    if (event.isCancelled()) {
                        return true;
                    }

                    partyManagerInstance.removeFromParty(playerName, party);
                    partyManagerInstance.joinParty(player, playerProfile, args[0], args[1]);
                }
            }
            else {
                McMMOPartyChangeEvent event = new McMMOPartyChangeEvent(player, null, args[0], EventReason.JOINED_PARTY);
                plugin.getServer().getPluginManager().callEvent(event);

                if (event.isCancelled()) {
                    return true;
                }

                partyManagerInstance.joinParty(player, playerProfile, args[0], args[1]);
            }

            return true;

        default:
            player.sendMessage(LocaleLoader.getString("Party.Help.0"));
            player.sendMessage(LocaleLoader.getString("Party.Help.1"));
            player.sendMessage(LocaleLoader.getString("Party.Help.2"));
            return true;
        }
    }
}
