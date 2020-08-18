package com.gmail.nossr50.commands.party;

import com.gmail.nossr50.datatypes.party.Party;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.commands.CommandUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PartyJoinCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        switch (args.length) {
            case 2:
            case 3:
                String targetName = CommandUtils.getMatchedPlayerName(args[1]);
                McMMOPlayer mcMMOTarget = mcMMO.getUserManager().getPlayer(targetName);

                if (!CommandUtils.checkPlayerExistence(sender, targetName, mcMMOTarget)) {
                    return true;
                }

                Player target = mcMMOTarget.getPlayer();

                if (!mcMMOTarget.inParty()) {
                    sender.sendMessage(LocaleLoader.getString("Party.PlayerNotInParty", targetName));
                    return true;
                }

                Player player = (Player) sender;

                if(mcMMO.getUserManager().getPlayer((Player) sender) == null)
                {
                    sender.sendMessage(LocaleLoader.getString("Profile.PendingLoad"));
                    return true;
                }

                McMMOPlayer mmoPlayer = mcMMO.getUserManager().getPlayer(player);
                Party targetParty = mcMMOTarget.getParty();

                if (player.equals(target) || (mmoPlayer.inParty() && mmoPlayer.getParty().equals(targetParty))) {
                    sender.sendMessage(LocaleLoader.getString("Party.Join.Self"));
                    return true;
                }

                String password = getPassword(args);

                // Make sure party passwords match
                if (!mcMMO.getPartyManager().checkPartyPassword(player, targetParty, password)) {
                    return true;
                }

                String partyName = targetParty.getPartyName();

                // Changing parties
                if (!mcMMO.getPartyManager().changeOrJoinParty(mmoPlayer, partyName)) {
                    return true;
                }

                if(mcMMO.getPartyManager().isPartyFull(player, targetParty))
                {
                    player.sendMessage(LocaleLoader.getString("Commands.Party.PartyFull", targetParty.toString()));
                    return true;
                }

                player.sendMessage(LocaleLoader.getString("Commands.Party.Join", partyName));
                mcMMO.getPartyManager().addToParty(mmoPlayer, targetParty);
                return true;

            default:
                sender.sendMessage(LocaleLoader.getString("Commands.Usage.3", "party", "join", "<" + LocaleLoader.getString("Commands.Usage.Player") + ">", "[" + LocaleLoader.getString("Commands.Usage.Password") + "]"));
                return true;
        }
    }

    private String getPassword(String[] args) {
        if (args.length == 3) {
            return args[2];
        }

        return null;
    }
}
