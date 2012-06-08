package com.gmail.nossr50.commands.party;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.commands.CommandHelper;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.events.party.McMMOPartyChangeEvent;
import com.gmail.nossr50.events.party.McMMOPartyChangeEvent.EventReason;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.party.PartyManager;
import com.gmail.nossr50.util.Users;

public class AcceptCommand implements CommandExecutor {
    private final mcMMO plugin;

    public AcceptCommand (mcMMO plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (CommandHelper.noConsoleUsage(sender)) {
            return true;
        }

        if (CommandHelper.noCommandPermissions(sender, "mcmmo.commands.party")) {
            return true;
        }

        Player player = (Player) sender;
        PlayerProfile PP = Users.getProfile(player);

        if (PP.hasPartyInvite()) {
            PartyManager partyManagerInstance = PartyManager.getInstance();

            if (PP.inParty()) {
                McMMOPartyChangeEvent event = new McMMOPartyChangeEvent(player, PP.getParty().getName(), PP.getInvite().getName(), EventReason.CHANGED_PARTIES);
                plugin.getServer().getPluginManager().callEvent(event);

                if (event.isCancelled()) {
                    return true;
                }

                partyManagerInstance.removeFromParty(player, PP);
            }
            else {
                McMMOPartyChangeEvent event = new McMMOPartyChangeEvent(player, null, PP.getInvite().getName(), EventReason.JOINED_PARTY);
                plugin.getServer().getPluginManager().callEvent(event);

                if (event.isCancelled()) {
                    return true;
                }
            }

            partyManagerInstance.addToInvitedParty(player, PP, PP.getInvite());
        }
        else {
            player.sendMessage(LocaleLoader.getString("mcMMO.NoInvites"));
        }

        return true;
    }
}
