package com.gmail.nossr50.commands.party;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.Users;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.commands.CommandHelper;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.events.party.McMMOPartyChangeEvent;
import com.gmail.nossr50.events.party.McMMOPartyChangeEvent.EventReason;
import com.gmail.nossr50.locale.mcLocale;
import com.gmail.nossr50.party.Party;

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
            Party partyInstance = Party.getInstance();

            if (PP.inParty()) {
                McMMOPartyChangeEvent event = new McMMOPartyChangeEvent(player, PP.getParty(), PP.getInvite(), EventReason.CHANGED_PARTIES);
                plugin.getServer().getPluginManager().callEvent(event);

                if (event.isCancelled()) {
                    return true;
                }

                partyInstance.removeFromParty(player, PP);
            }
            else {
                McMMOPartyChangeEvent event = new McMMOPartyChangeEvent(player, null, PP.getInvite(), EventReason.JOINED_PARTY);
                plugin.getServer().getPluginManager().callEvent(event);

                if (event.isCancelled()) {
                    return true;
                }
            }
            PP.acceptInvite();
            partyInstance.addToParty(player, PP, PP.getParty(), true, null);

        }
        else {
            player.sendMessage(mcLocale.getString("mcMMO.NoInvites"));
        }

        return true;
    }
}
