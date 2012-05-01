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

public class TamingCommand implements CommandExecutor {
    private float skillValue;
    private String goreChance;

    private boolean canBeastLore;
    private boolean canGore;
    private boolean canSharpenedClaws;
    private boolean canEnvironmentallyAware;
    private boolean canThickFur;
    private boolean canShockProof;
    private boolean canCallWild;
    private boolean canFastFood;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (CommandHelper.noConsoleUsage(sender)) {
            return true;
        }

        if (CommandHelper.noCommandPermissions(sender, "mcmmo.skills.taming")) {
            return true;
        }

        Player player = (Player) sender;
        PlayerProfile PP = Users.getProfile(player);

        skillValue = (float) PP.getSkillLevel(SkillType.TAMING);
        dataCalculations(skillValue);
        permissionsCheck(player);

        player.sendMessage(LocaleLoader.getString("Skills.Header", new Object[] { LocaleLoader.getString("Taming.SkillName") }));
        player.sendMessage(LocaleLoader.getString("Commands.XPGain", new Object[] { LocaleLoader.getString("Commands.XPGain.Taming") }));
        player.sendMessage(LocaleLoader.getString("Effects.Level", new Object[] { PP.getSkillLevel(SkillType.TAMING), PP.getSkillXpLevel(SkillType.TAMING), PP.getXpToLevel(SkillType.TAMING) }));

        if (canBeastLore || canCallWild || canEnvironmentallyAware || canFastFood || canGore || canSharpenedClaws || canShockProof || canThickFur) {
            player.sendMessage(LocaleLoader.getString("Skills.Header", new Object[] { LocaleLoader.getString("Effects.Effects") }));
        }

        if (canBeastLore) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Taming.Effect.0"), LocaleLoader.getString("Taming.Effect.1") }));
        }

        if (canGore) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Taming.Effect.2"), LocaleLoader.getString("Taming.Effect.3") }));
        }

        if (canSharpenedClaws) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Taming.Effect.4"), LocaleLoader.getString("Taming.Effect.5") }));
        }

        if (canEnvironmentallyAware) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Taming.Effect.6"), LocaleLoader.getString("Taming.Effect.7") }));
        }

        if (canThickFur) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Taming.Effect.8"), LocaleLoader.getString("Taming.Effect.9") }));
        }

        if (canShockProof) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Taming.Effect.10"), LocaleLoader.getString("Taming.Effect.11") }));
        }

        if (canFastFood) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Taming.Effect.16"), LocaleLoader.getString("Taming.Effect.17") }));
        }

        if (canCallWild) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Taming.Effect.12"), LocaleLoader.getString("Taming.Effect.13") }));
            player.sendMessage(LocaleLoader.getString("Taming.Effect.14", new Object[] { Config.getInstance().getTamingCOTWOcelotCost() }));
            player.sendMessage(LocaleLoader.getString("Taming.Effect.15", new Object[] { Config.getInstance().getTamingCOTWWolfCost() }));
        }

        if (canEnvironmentallyAware || canFastFood || canGore || canSharpenedClaws || canShockProof || canThickFur) {
            player.sendMessage(LocaleLoader.getString("Skills.Header", new Object[] { LocaleLoader.getString("Commands.Stats.Self") }));
        }

        if (canFastFood) {
            if (PP.getSkillLevel(SkillType.TAMING) < 50) {
                player.sendMessage(LocaleLoader.getString("Ability.Generic.Template.Lock", new Object[] { LocaleLoader.getString("Taming.Ability.Locked.4") }));
            }
            else {
                player.sendMessage(LocaleLoader.getString("Ability.Generic.Template", new Object[] { LocaleLoader.getString("Taming.Ability.Bonus.8"), LocaleLoader.getString("Taming.Ability.Bonus.9") }));
            }
        }

        if (canEnvironmentallyAware) {
            if (PP.getSkillLevel(SkillType.TAMING) < 100) {
                player.sendMessage(LocaleLoader.getString("Ability.Generic.Template.Lock", new Object[] { LocaleLoader.getString("Taming.Ability.Locked.0") }));
            }
            else {
                player.sendMessage(LocaleLoader.getString("Ability.Generic.Template", new Object[] { LocaleLoader.getString("Taming.Ability.Bonus.0"), LocaleLoader.getString("Taming.Ability.Bonus.1") }));
            }
        }

        if (canThickFur) {
            if (PP.getSkillLevel(SkillType.TAMING) < 250) {
                player.sendMessage(LocaleLoader.getString("Ability.Generic.Template.Lock", new Object[] { LocaleLoader.getString("Taming.Ability.Locked.1") }));
            }
            else {
                player.sendMessage(LocaleLoader.getString("Ability.Generic.Template", new Object[] { LocaleLoader.getString("Taming.Ability.Bonus.2"), LocaleLoader.getString("Taming.Ability.Bonus.3") }));
            }
        }

        if (canShockProof) {
            if (PP.getSkillLevel(SkillType.TAMING) < 500) {
                player.sendMessage(LocaleLoader.getString("Ability.Generic.Template.Lock", new Object[] { LocaleLoader.getString("Taming.Ability.Locked.2") }));
            }
            else {
                player.sendMessage(LocaleLoader.getString("Ability.Generic.Template", new Object[] { LocaleLoader.getString("Taming.Ability.Bonus.4"), LocaleLoader.getString("Taming.Ability.Bonus.5") }));
            }
        }

        if (canSharpenedClaws) {
            if (PP.getSkillLevel(SkillType.TAMING) < 750) {
                player.sendMessage(LocaleLoader.getString("Ability.Generic.Template.Lock", new Object[] { LocaleLoader.getString("Taming.Ability.Locked.3") }));
            }
            else {
                player.sendMessage(LocaleLoader.getString("Ability.Generic.Template", new Object[] { LocaleLoader.getString("Taming.Ability.Bonus.6"), LocaleLoader.getString("Taming.Ability.Bonus.7") }));
            }
        }

        if (canGore) {
            player.sendMessage(LocaleLoader.getString("Taming.Combat.Chance.Gore", new Object[] { goreChance }));
        }

        Page.grabGuidePageForSkill(SkillType.TAMING, player, args);

        return true;
    }

    private void dataCalculations(float skillValue) {
        DecimalFormat percent = new DecimalFormat("##0.00%");

        if (skillValue >= 1000) {
            goreChance = "100.00%";
        }
        else {
            goreChance = percent.format(skillValue / 1000);
        }
    }

    private void permissionsCheck(Player player) {
        Permissions permInstance = Permissions.getInstance();

        canBeastLore = permInstance.beastLore(player);
        canCallWild = permInstance.callOfTheWild(player);
        canEnvironmentallyAware = permInstance.environmentallyAware(player);
        canFastFood = permInstance.fastFoodService(player);
        canGore = permInstance.gore(player);
        canSharpenedClaws = permInstance.sharpenedClaws(player);
        canShockProof = permInstance.shockProof(player);
        canThickFur = permInstance.thickFur(player);
    }
}
