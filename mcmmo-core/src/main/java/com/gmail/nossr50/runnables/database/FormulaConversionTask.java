package com.gmail.nossr50.runnables.database;

import com.gmail.nossr50.datatypes.experience.FormulaType;
import com.gmail.nossr50.datatypes.player.BukkitMMOPlayer;
import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.mcmmo.api.platform.scheduler.Task;

import org.bukkit.command.CommandSender;

import java.util.function.Consumer;

//TODO: PUUUUUUUUUUURGE
/**
 * This whole design of this thing is Jank
 * The name is Jank
 * Everything about this is Jank
 *
 * Fix this at some point in the future
 */
public class FormulaConversionTask implements Consumer<Task> {
    private CommandSender sender;
    private FormulaType previousFormula;
    private mcMMO pluginRef;

    public FormulaConversionTask(mcMMO pluginRef, CommandSender sender, FormulaType previousFormula) {
        this.pluginRef = pluginRef;
        this.sender = sender;
        this.previousFormula = previousFormula;
    }

    @Override
    public void accept(Task task) {
        int convertedUsers = 0;
        long startMillis = System.currentTimeMillis();
        for (String playerName : pluginRef.getDatabaseManager().getStoredUsers()) {
            BukkitMMOPlayer mcMMOPlayer = pluginRef.getUserManager().getOfflinePlayer(playerName);
            PlayerProfile profile;

            // If the mcMMOPlayer doesn't exist, create a temporary profile and check if it's present in the database. If it's not, abort the process.
            if (mcMMOPlayer == null) {
                profile = pluginRef.getDatabaseManager().loadPlayerProfile(playerName, false);

                if (!profile.isLoaded()) {
                    pluginRef.debug("Profile not loaded.");
                    continue;
                }

                editValues(profile);
                // Since this is a temporary profile, we save it here.
                profile.scheduleAsyncSave();
            } else {
                profile = mcMMOPlayer.getProfile();
                editValues(profile);
            }
            convertedUsers++;
            pluginRef.getDatabaseManager().printProgress(convertedUsers, startMillis, pluginRef.getLogger());
        }

        sender.sendMessage(pluginRef.getLocaleManager().getString("Commands.mcconvert.Experience.Finish", pluginRef.getConfigManager().getConfigLeveling().getConfigExperienceFormula().toString()));
    }

    private void editValues(PlayerProfile profile) {
        pluginRef.debug("========================================================================");
        pluginRef.debug("Conversion report for " + profile.getPlayerName() + ":");
        for (PrimarySkillType primarySkillType : pluginRef.getSkillTools().NON_CHILD_SKILLS) {
            int oldLevel = profile.getSkillLevel(primarySkillType);
            int oldXPLevel = profile.getSkillXpLevel(primarySkillType);
            int totalOldXP = pluginRef.getFormulaManager().calculateTotalExperience(oldLevel, oldXPLevel, previousFormula);

            if (totalOldXP == 0) {
                continue;
            }

            int[] newExperienceValues = pluginRef.getFormulaManager().calculateNewLevel(primarySkillType, (int) Math.floor(totalOldXP / 1.0));
            int newLevel = newExperienceValues[0];
            int newXPlevel = newExperienceValues[1];

            pluginRef.debug("  Skill: " + primarySkillType.toString());

            pluginRef.debug("    OLD:");
            pluginRef.debug("      Level: " + oldLevel);
            pluginRef.debug("      XP " + oldXPLevel);
            pluginRef.debug("      Total XP " + totalOldXP);

            pluginRef.debug("    NEW:");
            pluginRef.debug("      Level " + newLevel);
            pluginRef.debug("      XP " + newXPlevel);
            pluginRef.debug("------------------------------------------------------------------------");

            profile.modifySkill(primarySkillType, newLevel);
            profile.setSkillXpLevel(primarySkillType, newXPlevel);
        }
    }
}
