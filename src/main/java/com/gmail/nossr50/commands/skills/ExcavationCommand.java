package com.gmail.nossr50.commands.skills;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.commands.CommandHelper;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Page;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.Users;

public class ExcavationCommand implements CommandExecutor {
    private float skillValue;
    private String gigaDrillBreakerLength;

    private boolean canGigaDrill;
    private boolean canTreasureHunt;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (CommandHelper.noConsoleUsage(sender)) {
            return true;
        }

        if (CommandHelper.noCommandPermissions(sender, "mcmmo.skills.excavation")) {
            return true;
        }

        Player player = (Player) sender;
        PlayerProfile PP = Users.getProfile(player);

        skillValue = (float) PP.getSkillLevel(SkillType.EXCAVATION);
        dataCalculations(skillValue);
        permissionsCheck(player);

        player.sendMessage(LocaleLoader.getString("Skills.Header", new Object[] { LocaleLoader.getString("Excavation.SkillName") }));
        player.sendMessage(LocaleLoader.getString("Commands.XPGain", new Object[] { LocaleLoader.getString("Commands.XPGain.Excavation") }));
        player.sendMessage(LocaleLoader.getString("Effects.Level", new Object[] { PP.getSkillLevel(SkillType.EXCAVATION), PP.getSkillXpLevel(SkillType.EXCAVATION), PP.getXpToLevel(SkillType.EXCAVATION) }));

        if (canGigaDrill || canTreasureHunt) {
            player.sendMessage(LocaleLoader.getString("Skills.Header", new Object[] { LocaleLoader.getString("Effects.Effects") }));
        }

        if (canGigaDrill) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Excavation.Effect.0"), LocaleLoader.getString("Excavation.Effect.1") }));
        }

        if (canTreasureHunt) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Excavation.Effect.2"), LocaleLoader.getString("Excavation.Effect.3") }));
        }

        if (canGigaDrill) {
            player.sendMessage(LocaleLoader.getString("Skills.Header", new Object[] { LocaleLoader.getString("Commands.Stats.Self") }));
            player.sendMessage(LocaleLoader.getString("Excavation.Effect.Length", new Object[] { gigaDrillBreakerLength }));
        }

        Page.grabGuidePageForSkill(SkillType.EXCAVATION, player, args);

        return true;
    }

    private void dataCalculations(float skillValue) {
        gigaDrillBreakerLength = String.valueOf(2 + ((int) skillValue / 50));
    }

    private void permissionsCheck(Player player) {
        Permissions permInstance = Permissions.getInstance();

        canGigaDrill = permInstance.gigaDrillBreaker(player);
        canTreasureHunt = permInstance.excavationTreasures(player);
    }
}
