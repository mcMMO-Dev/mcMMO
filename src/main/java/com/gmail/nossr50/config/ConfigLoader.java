package com.gmail.nossr50.config;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.gmail.nossr50.mcMMO;

public abstract class ConfigLoader {
    protected static final mcMMO plugin = mcMMO.p;
    protected String fileName;
    protected File configFile;
    protected FileConfiguration config;

    public ConfigLoader(String relativePath, String fileName) {
        this.fileName = fileName;
        configFile = new File(plugin.getDataFolder(), relativePath + File.separator + fileName);
        loadFile();
    }

    public ConfigLoader(String fileName) {
        this.fileName = fileName;
        configFile = new File(plugin.getDataFolder(), fileName);
        loadFile();
    }

    protected void loadFile() {
        if (!configFile.exists()) {
            plugin.getLogger().info("Creating mcMMO " + fileName + " File...");
            createFile();
        }
        else {
            plugin.getLogger().info("Loading mcMMO " + fileName + " File...");
        }

        config = YamlConfiguration.loadConfiguration(configFile);
    }

    protected abstract void loadKeys();

    protected void createFile() {
        if (configFile.exists()) {
            return;
        }

        configFile.getParentFile().mkdirs();

        InputStream inputStream = plugin.getResource(fileName);

        if (inputStream != null) {
            try {
                copyStreamToFile(inputStream, configFile);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            plugin.getLogger().severe("Missing resource file: '" + fileName + "' please notify the plugin authors");
        }
    }

    private static void copyStreamToFile(InputStream inputStream, File file) throws Exception {
        OutputStream outputStream = new FileOutputStream(file);

        int read = 0;
        byte[] bytes = new byte[1024];

        while ((read = inputStream.read(bytes)) != -1) {
            outputStream.write(bytes, 0, read);
        }

        inputStream.close();
        outputStream.close();
    }
}
