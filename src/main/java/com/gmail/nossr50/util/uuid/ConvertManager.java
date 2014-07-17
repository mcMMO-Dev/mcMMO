package com.gmail.nossr50.util.uuid;

import java.io.File;

import org.bukkit.configuration.file.YamlConfiguration;

import com.gmail.nossr50.mcMMO;

public class ConvertManager {
    private static File convertFile = new File(mcMMO.getFlatFileDirectory() + "convert.yml");

    private boolean uuidConversionCompleted;

    public ConvertManager() {
        load();
    }

    public boolean isUUIDConversionCompleted() {
        return uuidConversionCompleted;
    }

    public void setUUIDConversionCompleted(boolean conversionCompleted) {
        this.uuidConversionCompleted = conversionCompleted;
    }

    /**
     * Load convert file.
     */
    public void load() {
        if (!convertFile.exists()) {
            uuidConversionCompleted = false;
            return;
        }

        uuidConversionCompleted = YamlConfiguration.loadConfiguration(convertFile).getBoolean("UUID.Conversion_Complete", false);
    }

    /**
     * Save convert file.
     */
    public void save() {
        mcMMO.p.debug("Saving convert status...");
        YamlConfiguration convertStatusFile = new YamlConfiguration();
        convertStatusFile.set("UUID.Conversion_Complete", uuidConversionCompleted);

        try {
            convertStatusFile.save(convertFile);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
