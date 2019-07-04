package com.gmail.nossr50.commands.experience;

import com.gmail.nossr50.datatypes.experience.XPGainReason;
import com.gmail.nossr50.datatypes.experience.XPGainSource;
import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.Permissions;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AddXPCommand extends ExperienceCommand {

    public AddXPCommand(mcMMO pluginRef) {
        super(pluginRef);
    }

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
            if (pluginRef.getUserManager().getPlayer(player) == null)
                return;

            pluginRef.getUserManager().getPlayer(player).applyXpGain(skill, value, XPGainReason.COMMAND, XPGainSource.COMMAND);
        } else {
            profile.addXp(skill, value);
            profile.scheduleAsyncSave();
        }
    }

    @Override
    protected void handlePlayerMessageAll(Player player, int value) {
        player.sendMessage(pluginRef.getLocaleManager().getString("Commands.addxp.AwardAll", value));
    }

    @Override
    protected void handlePlayerMessageSkill(Player player, int value, PrimarySkillType skill) {
        player.sendMessage(pluginRef.getLocaleManager().getString("Commands.addxp.AwardSkill", value, skill.getLocalizedSkillName()));
    }
}
