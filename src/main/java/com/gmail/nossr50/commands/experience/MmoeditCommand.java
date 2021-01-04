package com.gmail.nossr50.commands.experience;

import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.EventUtils;
import com.gmail.nossr50.util.Permissions;
import com.neetgames.mcmmo.skill.RootSkill;
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
    protected void handleCommand(Player player, PlayerProfile profile, RootSkill rootSkill, int value) {
        int skillLevel = profile.getSkillLevel(rootSkill);
        float xpRemoved = profile.getSkillXpLevelRaw(rootSkill);

        profile.modifySkill(rootSkill, value);

        if (player == null) {
            profile.scheduleAsyncSave();
            return;
        }

        if (value == skillLevel) {
            return;
        }

        EventUtils.tryLevelEditEvent(player, rootSkill, value, xpRemoved, value > skillLevel, XPGainReason.COMMAND, skillLevel);
    }

    @Override
    protected void handlePlayerMessageAll(Player player, int value, boolean isSilent) {
        if(isSilent)
            return;

        player.sendMessage(LocaleLoader.getString("Commands.mmoedit.AllSkills.1", value));
    }

    @Override
    protected void handlePlayerMessageSkill(Player player, int value, RootSkill rootSkill, boolean isSilent) {
        if(isSilent)
            return;

        player.sendMessage(LocaleLoader.getString("Commands.mmoedit.Modified.1", rootSkill.getLocalizedName(), value));
    }
}
