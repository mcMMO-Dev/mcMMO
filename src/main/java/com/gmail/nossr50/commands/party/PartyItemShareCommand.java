package com.gmail.nossr50.commands.party;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.party.Party;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.party.ShareHandler;
import com.gmail.nossr50.party.ShareHandler.ShareMode;
import com.gmail.nossr50.util.StringUtils;
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
                playerParty = UserManager.getPlayer((Player) sender).getParty();

                if (args[1].equalsIgnoreCase("none") || args[1].equalsIgnoreCase("off") || args[1].equalsIgnoreCase("false")) {
                    handleChangingShareMode(ShareMode.NONE);
                }
                else if (args[1].equalsIgnoreCase("equal") || args[1].equalsIgnoreCase("even")) {
                    handleChangingShareMode(ShareMode.EQUAL);
                }
                else if (args[1].equalsIgnoreCase("random")) {
                    handleChangingShareMode(ShareMode.RANDOM);
                }
                else {
                    sender.sendMessage(LocaleLoader.getString("Commands.Usage.2", "party", "itemshare", "<NONE | EQUAL | RANDOM>"));
                }

                return true;

            case 3:
                playerParty = UserManager.getPlayer((Player) sender).getParty();
                boolean toggle = false;

                if (args[2].equalsIgnoreCase("true") || args[2].equalsIgnoreCase("on") || args[2].equalsIgnoreCase("enabled")) {
                    toggle = true;
                }
                else if (args[2].equalsIgnoreCase("false") || args[2].equalsIgnoreCase("off") || args[2].equalsIgnoreCase("disabled")) {
                    toggle = false;
                }

                if (args[1].equalsIgnoreCase("loot")) {
                    playerParty.setSharingLootDrops(toggle);
                }
                else if (args[1].equalsIgnoreCase("mining")) {
                    playerParty.setSharingMiningDrops(toggle);
                }
                else if (args[1].equalsIgnoreCase("herbalism")) {
                    playerParty.setSharingHerbalismDrops(toggle);
                }
                else if (args[1].equalsIgnoreCase("woodcutting")) {
                    playerParty.setSharingWoodcuttingDrops(toggle);
                }
                else {
                    sender.sendMessage(LocaleLoader.getString("Commands.Usage.2", "party", "itemshare", "<loot | mining | herbalism | woodcutting> <true | false>"));
                }

                notifyToggleItemShareCategory(args, toggle);
                return true;

            default:
                sender.sendMessage(LocaleLoader.getString("Commands.Usage.2", "party", "itemshare", "<NONE | EQUAL | RANDOM>"));
                sender.sendMessage(LocaleLoader.getString("Commands.Usage.2", "party", "itemshare", "<loot | mining | herbalism | woodcutting> <true | false>"));
                return true;
        }
    }

    private void handleChangingShareMode(ShareHandler.ShareMode mode) {
        playerParty.setItemShareMode(mode);

        for (Player member : playerParty.getOnlineMembers()) {
            member.sendMessage(LocaleLoader.getString("Commands.Party.SetSharing", LocaleLoader.getString("Party.ShareType.Item"), LocaleLoader.getString("Party.ShareMode." + StringUtils.getCapitalized(mode.toString()))));
        }
    }

    private void notifyToggleItemShareCategory(String[] args, boolean toggle) {
        String state = "disabled";

        if (toggle) {
            state = "enabled";
        }

        for (Player member : playerParty.getOnlineMembers()) {
            member.sendMessage(LocaleLoader.getString("Commands.Party.ToggleShareCategory", StringUtils.getCapitalized(args[1]), state));
        }
    }
}
