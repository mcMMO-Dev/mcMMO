package com.gmail.nossr50.party.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.datatypes.McMMOPlayer;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.events.party.McMMOPartyChangeEvent;
import com.gmail.nossr50.events.party.McMMOPartyChangeEvent.EventReason;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.party.Party;
import com.gmail.nossr50.party.PartyManager;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.Users;

public class PartyJoinCommand implements CommandExecutor {
    private McMMOPlayer mcMMOTarget;
    private Player target;
    private Party targetParty;

    private McMMOPlayer mcMMOPlayer;
    private Player player;
    private Party playerParty;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!Permissions.hasPermission(sender, "mcmmo.commands.party.join")) {
            sender.sendMessage(command.getPermissionMessage());
            return true;
        }

        switch (args.length) {
        case 2:
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
            if (mcMMOPlayer.inParty()) {
                handlePartyChangeEvent(playerParty.getName(), EventReason.CHANGED_PARTIES);
                PartyManager.removeFromParty(player.getName(), playerParty);
            }
            else {
                handlePartyChangeEvent(null, EventReason.JOINED_PARTY);
            }

            PartyManager.joinParty(player, mcMMOPlayer, targetParty.getName(), password);
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
        mcMMOTarget = Users.getPlayer(targetName);

        if (mcMMOTarget == null) {
            PlayerProfile playerProfile = new PlayerProfile(targetName, false);

            if (!playerProfile.isLoaded()) {
                sender.sendMessage(LocaleLoader.getString("Commands.DoesNotExist"));
                return false;
            }
        }

        target = mcMMOTarget.getPlayer();

        if (!target.isOnline()) {
            sender.sendMessage(LocaleLoader.getString("Party.NotOnline", targetName));
            return false;
        }

        if (!mcMMOTarget.inParty()) {
            sender.sendMessage(LocaleLoader.getString("Party.PlayerNotInParty", targetName));
            return false;
        }

        player = (Player) sender;
        mcMMOPlayer = Users.getPlayer(player);
        targetParty = mcMMOTarget.getParty();

        if (player.equals(target) || (mcMMOPlayer.inParty() && playerParty.equals(targetParty))) {
            sender.sendMessage(LocaleLoader.getString("Party.Join.Self"));
            return false;
        }

        return true;
    }

    /**
     * Handle party change event.
     *
     * @param partyName The name of the old party
     * @param reason The reason for changing parties
     * @return true if the change event was successful, false otherwise
     */
    private boolean handlePartyChangeEvent(String oldPartyName, EventReason reason) {
        McMMOPartyChangeEvent event = new McMMOPartyChangeEvent(player, oldPartyName, targetParty.getName(), reason);
        mcMMO.p.getServer().getPluginManager().callEvent(event);

        return !event.isCancelled();
    }
}
