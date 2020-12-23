package com.gmail.nossr50.commands.party;

import com.gmail.nossr50.config.Config;
import com.neetgames.mcmmo.party.Party;
import com.neetgames.mcmmo.player.OnlineMMOPlayer;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.commands.CommandUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PartyInviteCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length == 2) {
            String targetName = CommandUtils.getMatchedPlayerName(args[1]);
            OnlineMMOPlayer mcMMOTarget = mcMMO.getUserManager().queryPlayerName(targetName);

            if (!CommandUtils.checkPlayerExistence(sender, targetName, mcMMOTarget)) {
                return false;
            }

            Player target = mcMMOTarget.getPlayer();

            if (mcMMO.getUserManager().queryPlayer((Player) sender) == null) {
                sender.sendMessage(LocaleLoader.getString("Profile.PendingLoad"));
                return true;
            }

            Player player = (Player) sender;
            OnlineMMOPlayer mmoPlayer = mcMMO.getUserManager().queryPlayer(player);
            String playerName = player.getName();

            if (player.equals(target)) {
                sender.sendMessage(LocaleLoader.getString("Party.Invite.Self"));
                return true;
            }

            if (mcMMO.getPartyManager().inSameParty(player, target)) {
                sender.sendMessage(LocaleLoader.getString("Party.Player.InSameParty", targetName));
                return true;
            }

            if (!mcMMO.getPartyManager().canInvite(mmoPlayer)) {
                player.sendMessage(LocaleLoader.getString("Party.Locked"));
                return true;
            }

            Party playerParty = mmoPlayer.getParty();

            if (mcMMO.getPartyManager().isPartyFull(target, playerParty)) {
                player.sendMessage(LocaleLoader.getString("Commands.Party.PartyFull.Invite", target.getName(), playerParty.toString(), Config.getInstance().getPartyMaxSize()));
                return true;
            }

            mcMMOTarget.setPartyInvite(playerParty);

            sender.sendMessage(LocaleLoader.getString("Commands.Invite.Success"));
            target.sendMessage(LocaleLoader.getString("Commands.Party.Invite.0", playerParty.getPartyName(), playerName));
            target.sendMessage(LocaleLoader.getString("Commands.Party.Invite.1"));
            return true;
        }
        sender.sendMessage(LocaleLoader.getString("Commands.Usage.2", "party", "invite", "<" + LocaleLoader.getString("Commands.Usage.Player") + ">"));
        return true;
    }
}
