package com.gmail.nossr50.commands.skills;

import java.text.DecimalFormat;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.commands.CommandHelper;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Page;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.Users;

public class WoodcuttingCommand implements CommandExecutor {
    private float skillValue;
    private String treeFellerLength;
    private String doubleDropChance;

    private boolean canTreeFell;
    private boolean canLeafBlow;
    private boolean canDoubleDrop;
    private boolean doubleDropsDisabled;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (CommandHelper.noConsoleUsage(sender)) {
            return true;
        }

        if (CommandHelper.noCommandPermissions(sender, "mcmmo.skills.woodcutting")) {
            return true;
        }

        Player player = (Player) sender;
        PlayerProfile PP = Users.getProfile(player);

        skillValue = (float) PP.getSkillLevel(SkillType.UNARMED);
        dataCalculations(skillValue);
        permissionsCheck(player);

        player.sendMessage(LocaleLoader.getString("Skills.Header", new Object[] { LocaleLoader.getString("Woodcutting.SkillName") }));
        player.sendMessage(LocaleLoader.getString("Commands.XPGain", new Object[] { LocaleLoader.getString("Commands.XPGain.WoodCutting") }));
        player.sendMessage(LocaleLoader.getString("Effects.Level", new Object[] { PP.getSkillLevel(SkillType.WOODCUTTING), PP.getSkillXpLevel(SkillType.WOODCUTTING), PP.getXpToLevel(SkillType.WOODCUTTING) }));

        if ((canDoubleDrop && !doubleDropsDisabled ) || canLeafBlow || canTreeFell) {
            player.sendMessage(LocaleLoader.getString("Skills.Header", new Object[] { LocaleLoader.getString("Effects.Effects") }));
        }

        if (canTreeFell) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Woodcutting.Effect.0"), LocaleLoader.getString("Woodcutting.Effect.1") }));
        }

        if (canLeafBlow) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Woodcutting.Effect.2"), LocaleLoader.getString("Woodcutting.Effect.3") }));
        }

        if (canDoubleDrop && !doubleDropsDisabled) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Woodcutting.Effect.4"), LocaleLoader.getString("Woodcutting.Effect.5") }));
        }

        if ((canDoubleDrop && !doubleDropsDisabled ) || canLeafBlow || canTreeFell) {
            player.sendMessage(LocaleLoader.getString("Skills.Header", new Object[] { LocaleLoader.getString("Commands.Stats.Self") }));
        }

        //TODO: Remove? Basically duplicates the above.
        if (canLeafBlow) {
            if (PP.getSkillLevel(SkillType.WOODCUTTING) < 100) {
                player.sendMessage(LocaleLoader.getString("Ability.Generic.Template.Lock", new Object[] { LocaleLoader.getString("Woodcutting.Ability.Locked.0") }));
            }
            else {
                player.sendMessage(LocaleLoader.getString("Ability.Generic.Template", new Object[] { LocaleLoader.getString("Woodcutting.Ability.0"), LocaleLoader.getString("Woodcutting.Ability.1") }));
            }
        }

        if (canDoubleDrop && !doubleDropsDisabled) {
            player.sendMessage(LocaleLoader.getString("Woodcutting.Ability.Chance.DDrop", new Object[] { doubleDropChance }));
        }

        if (canTreeFell) {
            player.sendMessage(LocaleLoader.getString("Woodcutting.Ability.Length", new Object[] { treeFellerLength }));
        }

        Page.grabGuidePageForSkill(SkillType.WOODCUTTING, player, args);

        return true;
    }

    private void dataCalculations(float skillValue) {
        DecimalFormat percent = new DecimalFormat("##0.00%");

        treeFellerLength = String.valueOf(2 + ((int) skillValue / 50));

        if (skillValue >= 1000) {
            doubleDropChance = "100.00%";
        }
        else {
            doubleDropChance = percent.format(skillValue / 1000);
        }
    }

    private void permissionsCheck(Player player) {
        Permissions permInstance = Permissions.getInstance();
        Config configInstance = Config.getInstance();

        canTreeFell = permInstance.treeFeller(player);
        canDoubleDrop = permInstance.woodcuttingDoubleDrops(player);
        canLeafBlow = permInstance.leafBlower(player);
        doubleDropsDisabled = configInstance.woodcuttingDoubleDropsDisabled();
    }
}
