package com.gmail.nossr50.commands.party;

import com.gmail.nossr50.datatypes.party.Party;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.commands.CommandUtils;
import com.gmail.nossr50.util.player.UserManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PartyJoinCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
            @NotNull String label, String[] args) {
        switch (args.length) {
            case 2:
            case 3:
                String targetName = CommandUtils.getMatchedPlayerName(args[1]);
                McMMOPlayer mcMMOTarget = UserManager.getPlayer(targetName);

                if (!CommandUtils.checkPlayerExistence(sender, targetName, mcMMOTarget)) {
                    return true;
                }

                Player target = mcMMOTarget.getPlayer();

                if (!mcMMOTarget.inParty()) {
                    sender.sendMessage(
                            LocaleLoader.getString("Party.PlayerNotInParty", targetName));
                    return true;
                }

                final Player player = (Player) sender;

                if (UserManager.getPlayer((Player) sender) == null) {
                    sender.sendMessage(LocaleLoader.getString("Profile.PendingLoad"));
                    return true;
                }

                final McMMOPlayer mmoPlayer = UserManager.getPlayer(player);
                Party targetParty = mcMMOTarget.getParty();

                if (player.equals(target) || (mmoPlayer.inParty() && mmoPlayer.getParty()
                        .equals(targetParty))) {
                    sender.sendMessage(LocaleLoader.getString("Party.Join.Self"));
                    return true;
                }

                String password = getPassword(args);

                // Make sure party passwords match
                if (!mcMMO.p.getPartyManager().checkPartyPassword(player, targetParty, password)) {
                    return true;
                }

                String partyName = targetParty.getName();

                // Changing parties
                if (!mcMMO.p.getPartyManager().changeOrJoinParty(mmoPlayer, partyName)) {
                    return true;
                }

                if (mcMMO.p.getPartyManager().isPartyFull(player, targetParty)) {
                    player.sendMessage(LocaleLoader.getString("Commands.Party.PartyFull",
                            targetParty.toString()));
                    return true;
                }

                player.sendMessage(LocaleLoader.getString("Commands.Party.Join", partyName));
                mcMMO.p.getPartyManager().addToParty(mmoPlayer, targetParty);
                return true;

            default:
                sender.sendMessage(LocaleLoader.getString("Commands.Usage.3", "party", "join",
                        "<" + LocaleLoader.getString("Commands.Usage.Player") + ">",
                        "[" + LocaleLoader.getString("Commands.Usage.Password") + "]"));
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
