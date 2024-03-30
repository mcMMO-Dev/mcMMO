package com.gmail.nossr50.commands.party.alliance;

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

public class PartyAllianceInviteCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length == 3) {
            String targetName = CommandUtils.getMatchedPlayerName(args[2]);
            McMMOPlayer mcMMOTarget = UserManager.getOfflinePlayer(targetName);

            if (!CommandUtils.checkPlayerExistence(sender, targetName, mcMMOTarget)) {
                return false;
            }

            Player target = mcMMOTarget.getPlayer();

            if (UserManager.getPlayer((Player) sender) == null) {
                sender.sendMessage(LocaleLoader.getString("Profile.PendingLoad"));
                return true;
            }

            Player player = (Player) sender;
            McMMOPlayer mcMMOPlayer = UserManager.getPlayer(player);
            String playerName = player.getName();

            if (player.equals(target)) {
                sender.sendMessage(LocaleLoader.getString("Party.Invite.Self"));
                return true;
            }

            if (!mcMMOTarget.inParty()) {
                player.sendMessage(LocaleLoader.getString("Party.PlayerNotInParty", targetName));
                return true;
            }

            if (mcMMO.p.getPartyManager().inSameParty(player, target)) {
                sender.sendMessage(LocaleLoader.getString("Party.Player.InSameParty", targetName));
                return true;
            }

            if (!mcMMOTarget.getParty().getLeader().getUniqueId().equals(target.getUniqueId())) {
                player.sendMessage(LocaleLoader.getString("Party.Target.NotOwner", targetName));
                return true;
            }

            Party playerParty = mcMMOPlayer.getParty();

            if (playerParty.getAlly() != null) {
                player.sendMessage(LocaleLoader.getString("Commands.Party.Alliance.AlreadyAllies"));
                return true;
            }

            mcMMOTarget.setPartyAllianceInvite(playerParty);

            sender.sendMessage(LocaleLoader.getString("Commands.Invite.Success"));
            target.sendMessage(LocaleLoader.getString("Commands.Party.Alliance.Invite.0", playerParty.getName(), playerName));
            target.sendMessage(LocaleLoader.getString("Commands.Party.Alliance.Invite.1"));
            return true;
        }
        sender.sendMessage(LocaleLoader.getString("Commands.Usage.3", "party", "alliance", "invite", "<" + LocaleLoader.getString("Commands.Usage.Player") + ">"));
        return true;
    }
}
