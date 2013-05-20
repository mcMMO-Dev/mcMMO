package com.gmail.nossr50.runnables.database;

import com.gmail.nossr50.datatypes.experience.FormulaType;

import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.util.player.UserManager;

public class FormulaConversionTask extends BukkitRunnable {
    private CommandSender sender;
    private FormulaType formulaType;

    public FormulaConversionTask(CommandSender sender, FormulaType formulaType) {
        this.sender = sender;
        this.formulaType = formulaType;
    }

    @Override
    public void run() {
        for (String playerName : mcMMO.getDatabaseManager().getStoredUsers()) {
            McMMOPlayer mcMMOPlayer = UserManager.getPlayer(playerName);
            PlayerProfile profile;

            // If the mcMMOPlayer doesn't exist, create a temporary profile and check if it's present in the database. If it's not, abort the process.
            if (mcMMOPlayer == null) {
                profile = mcMMO.getDatabaseManager().loadPlayerProfile(playerName, false);

                if (!profile.isLoaded()) {
                    mcMMO.p.debug("Profile not loaded");
                    continue;
                }

                editValues(profile);
                profile.save(); // Since this is a temporary profile, we save it here.
            }
            else {
                profile = mcMMOPlayer.getProfile();
                editValues(profile);
            }
        }
        mcMMO.getFormulaManager().setPreviousFormulaType(formulaType);

        sender.sendMessage(LocaleLoader.getString("Commands.mcconvert.Finish", formulaType.toString()));
    }

    private void editValues(PlayerProfile profile) {
        mcMMO.p.debug("========================================================================");
        mcMMO.p.debug("Conversion report for " + profile.getPlayerName() + ":");
        for (SkillType skillType : SkillType.values()) {
            if (skillType.isChildSkill()) {
                continue;
            }

            int[] oldExperienceValues = new int[2];
            oldExperienceValues[0] = profile.getSkillLevel(skillType);
            oldExperienceValues[1] = profile.getSkillXpLevel(skillType);

            int totalOldXP = mcMMO.getFormulaManager().calculateTotalExperience(oldExperienceValues);
            if (totalOldXP == 0) {
                continue;
            }

            double modifier = ExperienceConfig.getInstance().getExpModifier();
            if (modifier <= 0) {
                modifier = 1;
                mcMMO.p.getLogger().warning("Invalid value found for Conversion.Exp_Modifier! Skipping using the modifier...");
            }

            int[] newExperienceValues = mcMMO.getFormulaManager().calculateNewLevel(skillType, (int) Math.floor(totalOldXP / modifier), formulaType);
            int newLevel = newExperienceValues[0];
            int newXPlevel = newExperienceValues[1];

            mcMMO.p.debug("  Skill: " + skillType.toString());

            mcMMO.p.debug("    OLD:");
            mcMMO.p.debug("      Level: " + oldExperienceValues[0]);
            mcMMO.p.debug("      XP " + oldExperienceValues[1]);
            mcMMO.p.debug("      Total XP " + totalOldXP);

            mcMMO.p.debug("    NEW:");
            mcMMO.p.debug("      Level " + newLevel);
            mcMMO.p.debug("      XP " + newXPlevel);
            mcMMO.p.debug("------------------------------------------------------------------------");

            profile.modifySkill(skillType, newLevel);
            profile.setSkillXpLevel(skillType, newXPlevel);
        }
    }
}
