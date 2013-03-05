package com.gmail.nossr50.commands.party;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.datatypes.party.Party;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.party.PartyManager;
import com.gmail.nossr50.util.player.UserManager;

public class PartyJoinCommand implements CommandExecutor {
    private McMMOPlayer mcMMOTarget;
    private Player target;
    private Party targetParty;

    private McMMOPlayer mcMMOPlayer;
    private Player player;
    private Party playerParty;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch (args.length) {
            case 2:
                // Fallthrough
            case 3:
                // Verify target exists and is in a different party than the player
                if (!canJoinParty(sender, args[1])) {
                    return true;
                }

                String password = getPassword(args);

                // Make sure party passwords match
                if (!PartyManager.checkPartyPassword(player, targetParty, password)) {
                    return true;
                }

                // Changing parties
                if (!PartyManager.changeOrJoinParty(mcMMOPlayer, player, playerParty, targetParty.getName())) {
                    return true;
                }

                PartyManager.joinParty(player, mcMMOPlayer, targetParty, password);
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

    private boolean canJoinParty(CommandSender sender, String targetName) {
        if (!mcMMO.p.getServer().getOfflinePlayer(targetName).isOnline()) {
            sender.sendMessage(LocaleLoader.getString("Party.NotOnline", targetName));
            return false;
        }

        mcMMOTarget = UserManager.getPlayer(targetName);

        if (mcMMOTarget == null) {
            sender.sendMessage(LocaleLoader.getString("Party.Player.Invalid"));
            return false;
        }

        target = mcMMOTarget.getPlayer();

        if (!mcMMOTarget.inParty()) {
            sender.sendMessage(LocaleLoader.getString("Party.PlayerNotInParty", targetName));
            return false;
        }

        player = (Player) sender;
        mcMMOPlayer = UserManager.getPlayer(player);
        playerParty = mcMMOPlayer.getParty();
        targetParty = mcMMOTarget.getParty();

        if (player.equals(target) || (mcMMOPlayer.inParty() && playerParty.equals(targetParty))) {
            sender.sendMessage(LocaleLoader.getString("Party.Join.Self"));
            return false;
        }

        return true;
    }
}
