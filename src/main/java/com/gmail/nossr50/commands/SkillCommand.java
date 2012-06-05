package com.gmail.nossr50.commands;

import java.text.DecimalFormat;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Page;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.Users;

public abstract class SkillCommand implements CommandExecutor {
    private SkillType skill;
    private String skillString;
    private String permission;

    protected Player player;
    protected PlayerProfile profile;
    protected float skillValue;

    protected DecimalFormat percent = new DecimalFormat("##0.00%");
    protected Permissions permInstance = Permissions.getInstance();

    public SkillCommand(SkillType skill) {
        this.skill = skill;
        this.skillString = Misc.getCapitalized(skill.toString());
        this.permission = "mcmmo.skills." + skillString.toLowerCase();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (CommandHelper.noConsoleUsage(sender)) {
            return true;
        }

        if (CommandHelper.noCommandPermissions(sender, permission)) {
            return true;
        }

        player = (Player) sender;
        profile = Users.getProfile(player);

        skillValue = profile.getSkillLevel(skill);
        dataCalculations();
        permissionsCheck();

        player.sendMessage(LocaleLoader.getString("Skills.Header", new Object[] { LocaleLoader.getString(skillString + ".SkillName") }));
        player.sendMessage(LocaleLoader.getString("Commands.XPGain", new Object[] { LocaleLoader.getString("Commands.XPGain." + skillString) }));
        player.sendMessage(LocaleLoader.getString("Effects.Level", new Object[] { profile.getSkillLevel(skill), profile.getSkillXpLevel(skill), profile.getXpToLevel(skill) }));

        if (effectsHeaderPermissions()) {
            player.sendMessage(LocaleLoader.getString("Skills.Header", new Object[] { LocaleLoader.getString("Effects.Effects") }));
        }

        effectsDisplay();

        if (statsHeaderPermissions()) {
            player.sendMessage(LocaleLoader.getString("Skills.Header", new Object[] { LocaleLoader.getString("Commands.Stats.Self") }));
        }

        statsDisplay();

        Page.grabGuidePageForSkill(skill, player, args);

        return true;
    }

    protected abstract void dataCalculations();

    protected abstract void permissionsCheck();

    protected abstract boolean effectsHeaderPermissions();

    protected abstract void effectsDisplay();

    protected abstract boolean statsHeaderPermissions();

    protected abstract void statsDisplay();
}
