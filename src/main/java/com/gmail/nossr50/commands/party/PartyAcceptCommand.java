package com.gmail.nossr50.commands.party;

import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mcMMO;
import com.neetgames.mcmmo.player.OnlineMMOPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PartyAcceptCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length == 1) {
            Player player = (Player) sender;

            //Check if player profile is loaded
            if (mcMMO.getUserManager().queryPlayer(player) == null) {
                sender.sendMessage(LocaleLoader.getString("Profile.PendingLoad"));
                return true;
            }

            OnlineMMOPlayer mmoPlayer = mcMMO.getUserManager().queryPlayer(player);


            if (!mmoPlayer.hasPartyInvite()) {
                sender.sendMessage(LocaleLoader.getString("mcMMO.NoInvites"));
                return true;
            }

            // Changing parties
            if (!mcMMO.getPartyManager().changeOrJoinParty(mmoPlayer, mmoPlayer.getPartyInvite().getName())) {
                return true;
            }

            mcMMO.getPartyManager().joinInvitedParty(mmoPlayer);
            return true;
        }
        sender.sendMessage(LocaleLoader.getString("Commands.Usage.1", "party", "accept"));
        return true;
    }
}
