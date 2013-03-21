package com.gmail.nossr50.commands.experience;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.commands.CommandUtils;
import com.gmail.nossr50.util.player.UserManager;
import com.gmail.nossr50.util.skills.SkillUtils;

public class SkillresetCommand extends ExperienceCommand {
    private CommandSender sender;
    private Command command;
    private int argsLength;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        this.command = command;
        this.sender = sender;
        argsLength = args.length;

        switch (args.length) {
            case 1:
                if (CommandUtils.noConsoleUsage(sender)) {
                    return true;
                }

                if (!Permissions.skillreset(sender)) {
                    sender.sendMessage(command.getPermissionMessage());
                    return true;
                }

                if (isInvalidSkill(sender, args[0])) {
                    return true;
                }

                mcMMOPlayer = UserManager.getPlayer(sender.getName());
                player = mcMMOPlayer.getPlayer();
                profile = mcMMOPlayer.getProfile();

                editValues();
                return true;

            case 2:
                if (!Permissions.skillresetOthers(sender)) {
                    sender.sendMessage(command.getPermissionMessage());
                    return true;
                }

                if (isInvalidSkill(sender, args[1])) {
                    return true;
                }

                mcMMOPlayer = UserManager.getPlayer(args[0]);

                // If the mcMMOPlayer doesn't exist, create a temporary profile and check if it's present in the database. If it's not, abort the process.
                if (mcMMOPlayer == null) {
                    profile = new PlayerProfile(args[0], false);

                    if (CommandUtils.unloadedProfile(sender, profile)) {
                        return true;
                    }

                    editValues();
                    profile.save(); // Since this is a temporary profile, we save it here.
                }
                else {
                    profile = mcMMOPlayer.getProfile();
                    player = mcMMOPlayer.getPlayer();

                    editValues();
                }

                handleSenderMessage(sender, args[0]);
                return true;

            default:
                return false;
        }
    }

    @Override
    protected boolean permissionsCheckSelf(CommandSender sender) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    protected boolean permissionsCheckOthers(CommandSender sender) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    protected void handleCommand(SkillType skill) {
        if (argsLength == 1 && !Permissions.skillreset(sender, skill) || (argsLength == 2 && !Permissions.skillresetOthers(sender, skill))) {
            sender.sendMessage(command.getPermissionMessage());
            return;
        }

        profile.modifySkill(skill, 0);
    }

    @Override
    protected void handlePlayerMessageAll() {
        player.sendMessage(LocaleLoader.getString("Commands.Reset.All"));
    }

    @Override
    protected void handlePlayerMessageSkill() {
        player.sendMessage(LocaleLoader.getString("Commands.Reset.Single", SkillUtils.getSkillName(skill)));
    }
}
