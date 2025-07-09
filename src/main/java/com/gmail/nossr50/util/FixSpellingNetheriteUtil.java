package com.gmail.nossr50.util;

import com.gmail.nossr50.datatypes.database.UpgradeType;
import com.gmail.nossr50.mcMMO;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FixSpellingNetheriteUtil {

    public static void processFileCheck(mcMMO pluginRef, String fileName, UpgradeType upgradeType) {
        LogUtils.debug(mcMMO.p.getLogger(), "Checking " + fileName + " config material names...");

        File configFile = new File(pluginRef.getDataFolder(), fileName);
        if (configFile.exists()) {
            BufferedReader bufferedReader = null;
            FileWriter fileWriter = null;
            try {
                bufferedReader = new BufferedReader(new FileReader(configFile));
                StringBuilder stringBuilder = new StringBuilder();
                String curLine;

                while ((curLine = bufferedReader.readLine()) != null) {
                    String fixedLine = curLine.replace("NETHERRITE", "NETHERITE");
                    stringBuilder.append(fixedLine);
                    stringBuilder.append("\r\n");
                }

                //Close
                bufferedReader.close();

                fileWriter = new FileWriter(configFile);
                fileWriter.write(stringBuilder.toString());
                fileWriter.close();

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (fileWriter != null) {
                        try {
                            fileWriter.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        pluginRef.getLogger()
                .info("Finished checking " + fileName + " for certain misspelled material names.");

        mcMMO.getUpgradeManager().setUpgradeCompleted(upgradeType);
    }
}
