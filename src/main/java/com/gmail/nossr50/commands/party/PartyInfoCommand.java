package com.gmail.nossr50.commands.party;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.party.Party;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.party.PartyManager;
import com.gmail.nossr50.party.ShareHandler;
import com.gmail.nossr50.util.player.UserManager;

public class PartyInfoCommand implements CommandExecutor {
    private Player player;
    private Party playerParty;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        player = (Player) sender;
        McMMOPlayer mcMMOPlayer = UserManager.getPlayer(player);
        playerParty = mcMMOPlayer.getParty();

        displayPartyHeader();
        displayShareModeInfo();
        displayMemberInfo();
        return true;
    }

    private String createMembersList() {
        StringBuilder memberList = new StringBuilder();

        for (OfflinePlayer member : playerParty.getMembers()) {
            if (playerParty.getLeader().equals(member.getName())) {
                memberList.append(ChatColor.GOLD).append(member.getName()).append(" ");
            }
            else if (member.isOnline()) {
                memberList.append(ChatColor.WHITE).append(member.getName()).append(" ");
            }
            else {
                memberList.append(ChatColor.GRAY).append(member.getName()).append(" ");
            }
        }

        return memberList.toString();
    }

    private void displayShareModeInfo() {
        boolean xpShareEnabled = Config.getInstance().getExpShareEnabled();
        boolean itemShareEnabled = Config.getInstance().getItemShareEnabled();
        boolean itemSharingActive = playerParty.getItemShareMode() != ShareHandler.ShareMode.NONE;

        if (!xpShareEnabled && !itemShareEnabled) {
            return;
        }

        String expShareInfo = "";
        String itemShareInfo = "";
        String separator = "";

        if (xpShareEnabled) {
            expShareInfo = LocaleLoader.getString("Commands.Party.ExpShare", playerParty.getXpShareMode().toString());
        }

        if (itemShareEnabled) {
            itemShareInfo = LocaleLoader.getString("Commands.Party.ItemShare", playerParty.getItemShareMode().toString());
        }

        if (xpShareEnabled && itemShareEnabled) {
            separator = ChatColor.DARK_GRAY + " || ";
        }

        player.sendMessage(LocaleLoader.getString("Commands.Party.ShareMode") + expShareInfo + separator + itemShareInfo);
        if (itemSharingActive) {
            player.sendMessage(LocaleLoader.getString("Commands.Party.ItemShareCategories", playerParty.getItemShareCategories()));
        }
    }

    private void displayPartyHeader() {
        player.sendMessage(LocaleLoader.getString("Commands.Party.Header"));

        if (playerParty.isLocked()) {
            player.sendMessage(LocaleLoader.getString("Commands.Party.Status", playerParty.getName(), LocaleLoader.getString("Party.Status.Locked")));
        }
        else {
            player.sendMessage(LocaleLoader.getString("Commands.Party.Status", playerParty.getName(), LocaleLoader.getString("Party.Status.Unlocked")));
        }
    }

    private void displayMemberInfo() {
        int membersNear = PartyManager.getNearMembers(player, playerParty, Config.getInstance().getPartyShareRange()).size();
        int membersOnline = playerParty.getOnlineMembers().size() - 1;

        player.sendMessage(LocaleLoader.getString("Commands.Party.Members.Header"));
        player.sendMessage(LocaleLoader.getString("Commands.Party.MembersNear", membersNear, membersOnline));
        player.sendMessage(LocaleLoader.getString("Commands.Party.Members", createMembersList()));
    }
}
