package com.gmail.nossr50.commands.party;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.mcMMO;
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
        switch (args.length) {
            case 0:
            case 1:
                McMMOPlayer mcMMOPlayer = UserManager.getPlayer(sender.getName());
                player = mcMMOPlayer.getPlayer();
                playerParty = mcMMOPlayer.getParty();

                displayPartyHeader();
                displayShareModeInfo();
                displayMemberInfo();
                return true;
            default:
                sender.sendMessage(LocaleLoader.getString("Commands.Usage.1", "party", "info"));
                return true;
        }
    }

    private String createMembersList() {
        StringBuilder memberList = new StringBuilder();

        for (String memberName : playerParty.getMembers()) {
            OfflinePlayer member = mcMMO.p.getServer().getOfflinePlayer(memberName);

            if (playerParty.getLeader().equalsIgnoreCase(memberName)) {
                memberList.append(ChatColor.GOLD);
            }
            else if (member.isOnline()) {
                memberList.append(ChatColor.WHITE);
            }
            else {
                memberList.append(ChatColor.GRAY);
            }

            memberList.append(memberName).append(" ");
        }

        return memberList.toString();
    }

    private void displayShareModeInfo() {
        boolean xpShareEnabled = Config.getInstance().getExpShareEnabled();
        boolean itemShareEnabled = Config.getInstance().getItemShareEnabled();
        boolean itemSharingActive = (playerParty.getItemShareMode() != ShareHandler.ShareMode.NONE);

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
        player.sendMessage(createMembersList());
    }
}
