package com.gmail.nossr50.commands.spout;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.commands.CommandHelper;
import com.gmail.nossr50.config.SpoutConfig;
import com.gmail.nossr50.datatypes.HudType;
import com.gmail.nossr50.datatypes.SpoutHud;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Users;

public class MchudCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String usage = ChatColor.RED + "Proper usage is /mchud <hud-type>"; //TODO: Locale
        String invalid = ChatColor.RED + "That is not a valid HUD type."; //TODO: Locale

        if (CommandHelper.noConsoleUsage(sender)) {
            return true;
        }

        if (!mcMMO.spoutEnabled || !SpoutConfig.getInstance().getXPBarEnabled()) {
            sender.sendMessage(LocaleLoader.getString("Commands.Disabled"));
            return true;
        }

        Player player = (Player) sender;
        PlayerProfile playerProfile = Users.getProfile(player);
        SpoutHud spoutHud = playerProfile.getSpoutHud();

        if (spoutHud == null) {
            sender.sendMessage(LocaleLoader.getString("Commands.Disabled"));
            return true;
        }

        switch (args.length) {
        case 1:
            for (HudType hudType : HudType.values()) {
                if (hudType.toString().equalsIgnoreCase(args[0])) {
                    playerProfile.setHudType(hudType);
                    spoutHud.initializeXpBar();
                    spoutHud.updateXpBar();

                    return true;
                }
            }

            player.sendMessage(invalid);
            return true;

        default:
            player.sendMessage(usage);
            return true;
        }
    }
}
