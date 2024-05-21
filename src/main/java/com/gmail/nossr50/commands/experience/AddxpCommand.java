package com.gmail.nossr50.commands.experience;

import com.gmail.nossr50.datatypes.experience.XPGainReason;
import com.gmail.nossr50.datatypes.experience.XPGainSource;
import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.player.UserManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AddxpCommand extends ExperienceCommand {
    @Override
    protected boolean permissionsCheckSelf(CommandSender sender) {
        return Permissions.addxp(sender);
    }

    @Override
    protected boolean permissionsCheckOthers(CommandSender sender) {
        return Permissions.addxpOthers(sender);
    }

    @Override
    protected void handleCommand(Player player, PlayerProfile profile, PrimarySkillType skill, int value) {
        if (player != null) {
            //Check if player profile is loaded
            if (UserManager.getPlayer(player) == null)
                return;

            UserManager.getPlayer(player).applyXpGain(skill, value, XPGainReason.COMMAND, XPGainSource.COMMAND);
        } else {
            profile.addXp(skill, value);
            profile.scheduleAsyncSave();
        }
    }

    @Override
    protected void handlePlayerMessageAll(Player player, int value, boolean isSilent) {
        if (isSilent)
            return;

        player.sendMessage(LocaleLoader.getString("Commands.addxp.AwardAll", value));
    }

    @Override
    protected void handlePlayerMessageSkill(Player player, int value, PrimarySkillType skill, boolean isSilent) {
        if (isSilent)
            return;

        player.sendMessage(LocaleLoader.getString("Commands.addxp.AwardSkill", value, mcMMO.p.getSkillTools().getLocalizedSkillName(skill)));
    }
}
