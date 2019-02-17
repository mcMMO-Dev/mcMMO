package com.gmail.nossr50.core.runnables.database;

import com.gmail.nossr50.core.McmmoCore;
import com.gmail.nossr50.core.config.experience.ExperienceConfig;
import com.gmail.nossr50.core.data.UserManager;
import com.gmail.nossr50.core.data.database.DatabaseManager;
import com.gmail.nossr50.core.datatypes.experience.FormulaType;
import com.gmail.nossr50.core.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.core.datatypes.player.PlayerProfile;
import com.gmail.nossr50.core.locale.LocaleLoader;
import com.gmail.nossr50.core.mcmmo.commands.CommandSender;
import com.gmail.nossr50.core.skills.PrimarySkillType;
import com.gmail.nossr50.core.util.Misc;

public class FormulaConversionTask implements Runnable {
    private CommandSender sender;
    private FormulaType formulaType;

    public FormulaConversionTask(CommandSender sender, FormulaType formulaType) {
        this.sender = sender;
        this.formulaType = formulaType;
    }

    @Override
    public void run() {
        int convertedUsers = 0;
        long startMillis = System.currentTimeMillis();
        for (String playerName : McmmoCore.getDatabaseManager().getStoredUsers()) {
            McMMOPlayer mcMMOPlayer = UserManager.getOfflinePlayer(playerName);
            PlayerProfile profile;

            // If the mcMMOPlayer doesn't exist, create a temporary profile and check if it's present in the database. If it's not, abort the process.
            if (mcMMOPlayer == null) {
                profile = McmmoCore.getDatabaseManager().loadPlayerProfile(playerName, false);

                if (!profile.isLoaded()) {
                    McmmoCore.getLogger().severe("Profile not loaded.");
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
            Misc.printProgress(convertedUsers, DatabaseManager.progressInterval, startMillis);
        }
        McmmoCore.getFormulaManager().setPreviousFormulaType(formulaType);

        sender.sendMessage(LocaleLoader.getString("Commands.mcconvert.Experience.Finish", formulaType.toString()));
    }

    private void editValues(PlayerProfile profile) {
        McmmoCore.getLogger().info("========================================================================");
        McmmoCore.getLogger().info("Conversion report for " + profile.getPlayerName() + ":");
        for (PrimarySkillType primarySkillType : PrimarySkillType.NON_CHILD_SKILLS) {
            int oldLevel = profile.getSkillLevel(primarySkillType);
            int oldXPLevel = profile.getSkillXpLevel(primarySkillType);
            int totalOldXP = McmmoCore.getFormulaManager().calculateTotalExperience(oldLevel, oldXPLevel);

            if (totalOldXP == 0) {
                continue;
            }

            int[] newExperienceValues = McmmoCore.getFormulaManager().calculateNewLevel(primarySkillType, (int) Math.floor(totalOldXP / ExperienceConfig.getInstance().getExpModifier()), formulaType);
            int newLevel = newExperienceValues[0];
            int newXPlevel = newExperienceValues[1];

            /*McmmoCore.getLogger().info("  Skill: " + primarySkillType.toString());

            mcMMO.p.debug("    OLD:");
            mcMMO.p.debug("      Level: " + oldLevel);
            mcMMO.p.debug("      XP " + oldXPLevel);
            mcMMO.p.debug("      Total XP " + totalOldXP);

            mcMMO.p.debug("    NEW:");
            mcMMO.p.debug("      Level " + newLevel);
            mcMMO.p.debug("      XP " + newXPlevel);
            mcMMO.p.debug("------------------------------------------------------------------------");*/

            profile.modifySkill(primarySkillType, newLevel);
            profile.setSkillXpLevel(primarySkillType, newXPlevel);
        }
    }
}
