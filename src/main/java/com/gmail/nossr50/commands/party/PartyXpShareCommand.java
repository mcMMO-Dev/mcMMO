package com.gmail.nossr50.commands.party;

import com.gmail.nossr50.datatypes.party.Party;
import com.gmail.nossr50.datatypes.party.PartyFeature;
import com.gmail.nossr50.datatypes.party.ShareMode;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.StringUtils;
import com.gmail.nossr50.util.commands.CommandUtils;
import com.gmail.nossr50.util.player.UserManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PartyXpShareCommand implements CommandExecutor {

    private mcMMO pluginRef;

    public PartyXpShareCommand(mcMMO pluginRef) {
        this.pluginRef = pluginRef;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (UserManager.getPlayer((Player) sender) == null) {
            sender.sendMessage(pluginRef.getLocaleManager().getString("Profile.PendingLoad"));
            return true;
        }

        Party party = UserManager.getPlayer((Player) sender).getParty();

        if (party.getLevel() < pluginRef.getPartyManager().getPartyFeatureUnlockLevel(PartyFeature.XP_SHARE)) {
            sender.sendMessage(pluginRef.getLocaleManager().getString("Party.Feature.Disabled.5"));
            return true;
        }

        switch (args.length) {
            case 2:
                if (args[1].equalsIgnoreCase("none") || CommandUtils.shouldDisableToggle(args[1])) {
                    handleChangingShareMode(party, ShareMode.NONE);
                } else if (args[1].equalsIgnoreCase("equal") || args[1].equalsIgnoreCase("even") || CommandUtils.shouldEnableToggle(args[1])) {
                    handleChangingShareMode(party, ShareMode.EQUAL);
                } else {
                    sender.sendMessage(pluginRef.getLocaleManager().getString("Commands.Usage.2", "party", "xpshare", "<NONE | EQUAL>"));
                }

                return true;

            default:
                sender.sendMessage(pluginRef.getLocaleManager().getString("Commands.Usage.2", "party", "xpshare", "<NONE | EQUAL>"));
                return true;
        }
    }

    private void handleChangingShareMode(Party party, ShareMode mode) {
        party.setXpShareMode(mode);

        String changeModeMessage = pluginRef.getLocaleManager().getString("Commands.Party.SetSharing", pluginRef.getLocaleManager().getString("Party.ShareType.Xp"), pluginRef.getLocaleManager().getString("Party.ShareMode." + StringUtils.getCapitalized(mode.toString())));

        for (Player member : party.getOnlineMembers()) {
            member.sendMessage(changeModeMessage);
        }
    }
}
