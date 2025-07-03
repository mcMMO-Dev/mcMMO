package com.gmail.nossr50.commands.party;

import com.gmail.nossr50.datatypes.party.Party;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.commands.CommandUtils;
import com.gmail.nossr50.util.player.UserManager;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PartyChangeOwnerCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
            @NotNull String label, String[] args) {
        if (args.length == 2) {//Check if player profile is loaded
            if (UserManager.getPlayer((Player) sender) == null) {
                sender.sendMessage(LocaleLoader.getString("Profile.PendingLoad"));
                return true;
            }

            Party playerParty = UserManager.getPlayer((Player) sender).getParty();
            String targetName = CommandUtils.getMatchedPlayerName(args[1]);
            OfflinePlayer target = mcMMO.p.getServer().getOfflinePlayer(targetName);

            if (!playerParty.hasMember(target.getUniqueId())) {
                sender.sendMessage(LocaleLoader.getString("Party.NotInYourParty", targetName));
                return true;
            }

            mcMMO.p.getPartyManager().setPartyLeader(target.getUniqueId(), playerParty);
            return true;
        }
        sender.sendMessage(LocaleLoader.getString("Commands.Usage.2", "party", "owner",
                "<" + LocaleLoader.getString("Commands.Usage.Player") + ">"));
        return true;
    }
}
