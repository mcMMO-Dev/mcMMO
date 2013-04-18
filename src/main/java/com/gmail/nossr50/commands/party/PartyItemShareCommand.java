package com.gmail.nossr50.commands.party;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.party.ItemShareType;
import com.gmail.nossr50.datatypes.party.Party;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.party.ShareHandler.ShareMode;
import com.gmail.nossr50.util.StringUtils;
import com.gmail.nossr50.util.commands.CommandUtils;
import com.gmail.nossr50.util.player.UserManager;

public class PartyItemShareCommand implements CommandExecutor {
    private Party playerParty;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!Config.getInstance().getItemShareEnabled()) {
            sender.sendMessage(LocaleLoader.getString("Party.ItemShare.Disabled"));
            return true;
        }

        switch (args.length) {
            case 2:
                playerParty = UserManager.getPlayer(sender.getName()).getParty();
                ShareMode mode = ShareMode.getShareMode(args[1].toUpperCase());

                if (mode == null) {
                    sender.sendMessage(LocaleLoader.getString("Commands.Usage.2", "party", "itemshare", "<NONE | EQUAL | RANDOM>"));
                    return true;
                }

                handleChangingShareMode(mode);
                return true;

            case 3:
                playerParty = UserManager.getPlayer(sender.getName()).getParty();
                boolean toggle = false;

                if (CommandUtils.shouldEnableToggle(args[2])) {
                    toggle = true;
                }
                else if (CommandUtils.shouldDisableToggle(args[2])) {
                    toggle = false;
                }
                else {
                    sender.sendMessage(LocaleLoader.getString("Commands.Usage.2", "party", "itemshare", "<loot | mining | herbalism | woodcutting | misc> <true | false>"));
                    return true;
                }

                try {
                    handleToggleItemShareCategory(ItemShareType.valueOf(args[1].toUpperCase()), toggle);
                }
                catch (IllegalArgumentException ex) {
                    sender.sendMessage(LocaleLoader.getString("Commands.Usage.2", "party", "itemshare", "<loot | mining | herbalism | woodcutting | misc> <true | false>"));
                }

                return true;

            default:
                sender.sendMessage(LocaleLoader.getString("Commands.Usage.2", "party", "itemshare", "<NONE | EQUAL | RANDOM>"));
                sender.sendMessage(LocaleLoader.getString("Commands.Usage.2", "party", "itemshare", "<loot | mining | herbalism | woodcutting | misc> <true | false>"));
                return true;
        }
    }

    private void handleChangingShareMode(ShareMode mode) {
        playerParty.setItemShareMode(mode);

        String changeModeMessage = LocaleLoader.getString("Commands.Party.SetSharing", LocaleLoader.getString("Party.ShareType.Item"), LocaleLoader.getString("Party.ShareMode." + StringUtils.getCapitalized(mode.toString())));

        for (Player member : playerParty.getOnlineMembers()) {
            member.sendMessage(changeModeMessage);
        }
    }

    private void handleToggleItemShareCategory(ItemShareType type, boolean toggle) {
        playerParty.setSharingDrops(type, toggle);

        String toggleMessage = LocaleLoader.getString("Commands.Party.ToggleShareCategory", StringUtils.getCapitalized(type.toString()), toggle ? "enabled" : "disabled");

        for (Player member : playerParty.getOnlineMembers()) {
            member.sendMessage(toggleMessage);
        }
    }
}
