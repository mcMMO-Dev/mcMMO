package com.gmail.nossr50.commands.experience;

import com.gmail.nossr50.datatypes.experience.XPGainReason;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.EventUtils;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.player.UserManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MmoeditCommand extends ExperienceCommand {
    @Override
    protected boolean permissionsCheckSelf(CommandSender sender) {
        return Permissions.mmoedit(sender);
    }

    @Override
    protected boolean permissionsCheckOthers(CommandSender sender) {
        return Permissions.mmoeditOthers(sender);
    }

    @Override
    protected void handleCommand(Player player, PlayerProfile profile, PrimarySkillType skill, int value) {
        int skillLevel = profile.getSkillLevel(skill);
        float xpRemoved = profile.getSkillXpLevelRaw(skill);

        profile.modifySkill(skill, value);

        if (player == null) {
            profile.scheduleAsyncSave();
            return;
        }

        if (value == skillLevel) {
            return;
        }

        McMMOPlayer mmoPlayer = UserManager.getPlayer(player);

        if (mmoPlayer != null) {
            EventUtils.tryLevelEditEvent(mmoPlayer, skill, value, xpRemoved, value > skillLevel, XPGainReason.COMMAND, skillLevel);
        } else {
            EventUtils.tryLevelEditEvent(player, skill, value, xpRemoved, value > skillLevel, XPGainReason.COMMAND, skillLevel);
        }

    }

    @Override
    protected void handlePlayerMessageAll(Player player, int value, boolean isSilent) {
        if (isSilent)
            return;

        player.sendMessage(LocaleLoader.getString("Commands.mmoedit.AllSkills.1", value));
    }

    @Override
    protected void handlePlayerMessageSkill(Player player, int value, PrimarySkillType skill, boolean isSilent) {
        if (isSilent)
            return;

        player.sendMessage(LocaleLoader.getString("Commands.mmoedit.Modified.1", mcMMO.p.getSkillTools().getLocalizedSkillName(skill), value));
    }
}
