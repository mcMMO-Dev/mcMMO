package com.gmail.nossr50.commands.party;

import com.gmail.nossr50.datatypes.party.Party;
import com.gmail.nossr50.events.party.McMMOPartyChangeEvent.EventReason;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.party.PartyManager;
import com.gmail.nossr50.util.commands.CommandUtils;
import com.gmail.nossr50.util.player.UserManager;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PartyKickCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch (args.length) {
            case 2:
                if(UserManager.getPlayer((Player) sender) == null)
                {
                    sender.sendMessage(LocaleLoader.getString("Profile.PendingLoad"));
                    return true;
                }

                Party playerParty = UserManager.getPlayer((Player) sender).getParty();
                String targetName = CommandUtils.getMatchedPlayerName(args[1]);

                if (!playerParty.hasMember(targetName)) {
                    sender.sendMessage(LocaleLoader.getString("Party.NotInYourParty", targetName));
                    return true;
                }

                OfflinePlayer target = mcMMO.p.getServer().getOfflinePlayer(targetName);

                if (target.isOnline()) {
                    Player onlineTarget = target.getPlayer();
                    String partyName = playerParty.getName();

                    if (!PartyManager.handlePartyChangeEvent(onlineTarget, partyName, null, EventReason.KICKED_FROM_PARTY)) {
                        return true;
                    }

                    PartyManager.processPartyLeaving(UserManager.getPlayer(onlineTarget));
                    onlineTarget.sendMessage(LocaleLoader.getString("Commands.Party.Kick", partyName));
                }

                PartyManager.removeFromParty(target, playerParty);
                return true;

            default:
                sender.sendMessage(LocaleLoader.getString("Commands.Usage.2", "party", "kick", "<" + LocaleLoader.getString("Commands.Usage.Player") + ">"));
                return true;
        }
    }
}
