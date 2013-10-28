package com.gmail.nossr50.commands.party;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.party.Party;
import com.gmail.nossr50.datatypes.party.ShareMode;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.party.PartyManager;
import com.gmail.nossr50.util.player.UserManager;

public class PartyInfoCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch (args.length) {
            case 0:
            case 1:
                Player player = (Player) sender;
                McMMOPlayer mcMMOPlayer = UserManager.getPlayer(player);
                Party party = mcMMOPlayer.getParty();

                displayPartyHeader(player, party);
                displayShareModeInfo(party, player);
                displayMemberInfo(player, mcMMOPlayer, party);
                return true;

            default:
                sender.sendMessage(LocaleLoader.getString("Commands.Usage.1", "party", "info"));
                return true;
        }
    }

    private String createMembersList(Party party) {
        StringBuilder memberList = new StringBuilder();

        for (String memberName : party.getMembers()) {
            Player member = mcMMO.p.getServer().getPlayerExact(memberName);

            if (party.getLeader().equalsIgnoreCase(memberName)) {
                memberList.append(ChatColor.GOLD);
            }
            else if (member != null) {
                memberList.append(ChatColor.WHITE);
            }
            else {
                memberList.append(ChatColor.GRAY);
            }

            memberList.append(memberName).append(" ");
        }

        return memberList.toString();
    }

    private void displayShareModeInfo(Party party, Player player) {
        boolean xpShareEnabled = Config.getInstance().getExpShareEnabled();
        boolean itemShareEnabled = Config.getInstance().getItemShareEnabled();
        boolean itemSharingActive = (party.getItemShareMode() != ShareMode.NONE);

        if (!xpShareEnabled && !itemShareEnabled) {
            return;
        }

        String expShareInfo = "";
        String itemShareInfo = "";
        String separator = "";

        if (xpShareEnabled) {
            expShareInfo = LocaleLoader.getString("Commands.Party.ExpShare", party.getXpShareMode().toString());
        }

        if (itemShareEnabled) {
            itemShareInfo = LocaleLoader.getString("Commands.Party.ItemShare", party.getItemShareMode().toString());
        }

        if (xpShareEnabled && itemShareEnabled) {
            separator = ChatColor.DARK_GRAY + " || ";
        }

        player.sendMessage(LocaleLoader.getString("Commands.Party.ShareMode") + expShareInfo + separator + itemShareInfo);

        if (itemSharingActive) {
            player.sendMessage(LocaleLoader.getString("Commands.Party.ItemShareCategories", party.getItemShareCategories()));
        }
    }

    private void displayPartyHeader(Player player, Party party) {
        player.sendMessage(LocaleLoader.getString("Commands.Party.Header"));
        player.sendMessage(LocaleLoader.getString("Commands.Party.Status", party.getName(), LocaleLoader.getString("Party.Status." + (party.isLocked() ? "Locked" : "Unlocked"))));

        if (party.getAlly() != null) {
            player.sendMessage(LocaleLoader.getString("Commands.Party.Status.Alliance", party.getAlly().getName()));
        }
    }

    private void displayMemberInfo(Player player, McMMOPlayer mcMMOPlayer, Party party) {
        int membersNear = PartyManager.getNearMembers(mcMMOPlayer).size();
        int membersOnline = party.getOnlineMembers().size() - 1;

        player.sendMessage(LocaleLoader.getString("Commands.Party.Members.Header"));
        player.sendMessage(LocaleLoader.getString("Commands.Party.MembersNear", membersNear, membersOnline));
        player.sendMessage(createMembersList(party));
    }
}
