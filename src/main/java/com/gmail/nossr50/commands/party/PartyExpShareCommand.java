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

public class PartyExpShareCommand implements CommandExecutor {
    private Party playerParty;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!Config.getInstance().getExpShareEnabled()) {
            sender.sendMessage(LocaleLoader.getString("Party.ExpShare.Disabled"));
            return true;
        }

        switch (args.length) {
            case 2:
                playerParty = UserManager.getPlayer((Player) sender).getParty();

                if (args[1].equalsIgnoreCase("none") || args[1].equalsIgnoreCase("off") || args[1].equalsIgnoreCase("false")) {
                    handleChangingShareMode(ShareMode.NONE);
                }
                else if (args[1].equalsIgnoreCase("equal") || args[1].equalsIgnoreCase("even") || args[1].equalsIgnoreCase("on") || args[1].equalsIgnoreCase("true")) {
                    handleChangingShareMode(ShareMode.EQUAL);
                }
                else {
                    sender.sendMessage(LocaleLoader.getString("Commands.Usage.2", "party", "expshare", "[NONE | EQUAL]"));
                }

                return true;

            default:
                sender.sendMessage(LocaleLoader.getString("Commands.Usage.2", "party", "expshare", "<NONE | EQUAL>"));
                return true;
        }
    }

    private void handleChangingShareMode(ShareHandler.ShareMode mode) {
        playerParty.setXpShareMode(mode);

        for (Player member : playerParty.getOnlineMembers()) {
            member.sendMessage(LocaleLoader.getString("Commands.Party.SetSharing", LocaleLoader.getString("Party.ShareType.Exp"), LocaleLoader.getString("Party.ShareMode." + StringUtils.getCapitalized(mode.toString()))));
        }
    }
}
