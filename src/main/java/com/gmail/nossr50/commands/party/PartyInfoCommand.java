package com.gmail.nossr50.commands.party;

import com.gmail.nossr50.datatypes.party.Party;
import com.gmail.nossr50.datatypes.party.PartyFeature;
import com.gmail.nossr50.datatypes.party.ShareMode;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.player.UserManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PartyInfoCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        switch (args.length) {
            case 0:
            case 1:
                if(UserManager.getPlayer((Player) sender) == null)
                {
                    sender.sendMessage(LocaleLoader.getString("Profile.PendingLoad"));
                    return true;
                }
                Player player = (Player) sender;
                McMMOPlayer mcMMOPlayer = UserManager.getPlayer(player);
                Party party = mcMMOPlayer.getParty();

                displayPartyHeader(player, party);
                displayShareModeInfo(player, party);
                displayPartyFeatures(player, party);
                displayMemberInfo(player, mcMMOPlayer, party);
                return true;

            default:
                sender.sendMessage(LocaleLoader.getString("Commands.Usage.1", "party", "info"));
                return true;
        }
    }

    private void displayPartyHeader(Player player, Party party) {
        player.sendMessage(LocaleLoader.getString("Commands.Party.Header"));

        StringBuilder status = new StringBuilder();
        status.append(LocaleLoader.getString("Commands.Party.Status", party.getName(), LocaleLoader.getString("Party.Status." + (party.isLocked() ? "Locked" : "Unlocked")), party.getLevel()));

        if (!party.hasReachedLevelCap()) {
            status.append(" (").append(party.getXpToLevelPercentage()).append(")");
        }

        player.sendMessage(status.toString());
    }

    private void displayPartyFeatures(Player player, Party party) {
        player.sendMessage(LocaleLoader.getString("Commands.Party.Features.Header"));

        List<String> unlockedPartyFeatures = new ArrayList<>();
        List<String> lockedPartyFeatures = new ArrayList<>();

        for (PartyFeature partyFeature : PartyFeature.values()) {
            if (!partyFeature.hasPermission(player)) {
                continue;
            }

            if (isUnlockedFeature(party, partyFeature)) {
                unlockedPartyFeatures.add(partyFeature.getLocaleString());
            }
            else {
                lockedPartyFeatures.add(partyFeature.getFeatureLockedLocaleString());
            }
        }

        player.sendMessage(LocaleLoader.getString("Commands.Party.UnlockedFeatures", unlockedPartyFeatures.isEmpty() ? "None" : unlockedPartyFeatures));

        for (String message : lockedPartyFeatures) {
            player.sendMessage(message);
        }
    }

    private boolean isUnlockedFeature(Party party, PartyFeature partyFeature) {
        return party.getLevel() >= mcMMO.p.getGeneralConfig().getPartyFeatureUnlockLevel(partyFeature);
    }

    private void displayShareModeInfo(Player player, Party party) {
        boolean xpShareEnabled = isUnlockedFeature(party, PartyFeature.XP_SHARE);
        boolean itemShareEnabled = isUnlockedFeature(party, PartyFeature.ITEM_SHARE);
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

    private void displayMemberInfo(Player player, McMMOPlayer mcMMOPlayer, Party party) {
        /*
         * Only show members of the party that this member can see
         */

        List<Player> nearMembers = mcMMO.p.getPartyManager().getNearVisibleMembers(mcMMOPlayer);
        int membersOnline = party.getVisibleMembers(player).size();

        player.sendMessage(LocaleLoader.getString("Commands.Party.Members.Header"));
        player.sendMessage(LocaleLoader.getString("Commands.Party.MembersNear", nearMembers.size()+1, membersOnline));
        player.sendMessage(party.createMembersList(player));
    }
}
