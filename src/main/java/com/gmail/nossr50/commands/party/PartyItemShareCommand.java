package com.gmail.nossr50.commands.party;

import com.gmail.nossr50.datatypes.party.ItemShareType;
import com.gmail.nossr50.datatypes.party.Party;
import com.gmail.nossr50.datatypes.party.PartyFeature;
import com.gmail.nossr50.datatypes.party.ShareMode;
import com.gmail.nossr50.util.StringUtils;
import com.gmail.nossr50.util.commands.CommandUtils;
import com.gmail.nossr50.util.player.UserManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PartyItemShareCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (UserManager.getPlayer((Player) sender) == null) {
            sender.sendMessage(pluginRef.getLocaleManager().getString("Profile.PendingLoad"));
            return true;
        }

        Party party = UserManager.getPlayer((Player) sender).getParty();

        if (party.getLevel() < pluginRef.getPartyManager().getPartyFeatureUnlockLevel(PartyFeature.ITEM_SHARE)) {
            sender.sendMessage(pluginRef.getLocaleManager().getString("Party.Feature.Disabled.4"));
            return true;
        }

        switch (args.length) {
            case 2:
                ShareMode mode = ShareMode.getShareMode(args[1].toUpperCase());

                if (mode == null) {
                    sender.sendMessage(pluginRef.getLocaleManager().getString("Commands.Usage.2", "party", "itemshare", "<NONE | EQUAL | RANDOM>"));
                    return true;
                }

                handleChangingShareMode(party, mode);
                return true;

            case 3:
                boolean toggle;

                if (CommandUtils.shouldEnableToggle(args[2])) {
                    toggle = true;
                } else if (CommandUtils.shouldDisableToggle(args[2])) {
                    toggle = false;
                } else {
                    sender.sendMessage(pluginRef.getLocaleManager().getString("Commands.Usage.2", "party", "itemshare", "<loot | mining | herbalism | woodcutting | misc> <true | false>"));
                    return true;
                }

                try {
                    handleToggleItemShareCategory(party, ItemShareType.valueOf(args[1].toUpperCase()), toggle);
                } catch (IllegalArgumentException ex) {
                    sender.sendMessage(pluginRef.getLocaleManager().getString("Commands.Usage.2", "party", "itemshare", "<loot | mining | herbalism | woodcutting | misc> <true | false>"));
                }

                return true;

            default:
                sender.sendMessage(pluginRef.getLocaleManager().getString("Commands.Usage.2", "party", "itemshare", "<NONE | EQUAL | RANDOM>"));
                sender.sendMessage(pluginRef.getLocaleManager().getString("Commands.Usage.2", "party", "itemshare", "<loot | mining | herbalism | woodcutting | misc> <true | false>"));
                return true;
        }
    }

    private void handleChangingShareMode(Party party, ShareMode mode) {
        party.setItemShareMode(mode);

        String changeModeMessage = pluginRef.getLocaleManager().getString("Commands.Party.SetSharing", pluginRef.getLocaleManager().getString("Party.ShareType.Item"), pluginRef.getLocaleManager().getString("Party.ShareMode." + StringUtils.getCapitalized(mode.toString())));

        for (Player member : party.getOnlineMembers()) {
            member.sendMessage(changeModeMessage);
        }
    }

    private void handleToggleItemShareCategory(Party party, ItemShareType type, boolean toggle) {
        party.setSharingDrops(type, toggle);

        String toggleMessage = pluginRef.getLocaleManager().getString("Commands.Party.ToggleShareCategory", StringUtils.getCapitalized(type.toString()), toggle ? "enabled" : "disabled");

        for (Player member : party.getOnlineMembers()) {
            member.sendMessage(toggleMessage);
        }
    }
}
