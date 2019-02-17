package com.gmail.nossr50.config.collectionconfigs;

import com.gmail.nossr50.config.ConfigCollection;
import com.gmail.nossr50.config.ConfigCollections;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.skills.repair.repairables.Repairable;
import com.gmail.nossr50.skills.salvage.salvageables.Salvageable;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Represents a collection of config files that serve a similar purpose
 * For example, files named repair.*.yml are all loaded into memory, this lets admins keep their config files clean
 *
 * To be honest I'm not sure how many people make use of this system, but I'm keeping it since its been in mcMMO for like 6+ years
 */
public final class MultiConfigManager {

    public static final String DEFAULT_MULTICONFIG_FILENAME_SUFFIX = ".vanilla.yml";

    //Configs
    public com.gmail.nossr50.config.collectionconfigs.RepairConfig vanillaRepairConfig; //This is the main config file that mcMMO will copy out
    public com.gmail.nossr50.config.collectionconfigs.SalvageConfig vanillaSalvageConfig;

    private static List<Repairable> repairables;
    private static List<Salvageable> salvageables;

    public MultiConfigManager(String fileNamePrefix)
    {
        //init Collections
        repairables = new ArrayList<>();
        salvageables = new ArrayList<>();

        //init vanilla configs
        vanillaRepairConfig = new com.gmail.nossr50.config.collectionconfigs.RepairConfig(getVanillaConfigName("repair"));
        vanillaSalvageConfig = new com.gmail.nossr50.config.collectionconfigs.SalvageConfig(getVanillaConfigName("salvage"));

        //add valid vanilla collections to main collection
        repairables.addAll(vanillaRepairConfig.getLoadedCollection());
        salvageables.addAll(vanillaSalvageConfig.getLoadedCollection());

        //add valid custom collections to main collection
        loadCustomCollections("repair", repairables, com.gmail.nossr50.config.collectionconfigs.RepairConfig.class);
        loadCustomCollections("salvage", salvageables, com.gmail.nossr50.config.collectionconfigs.SalvageConfig.class);
    }

    /**
     * mcMMO allows collection config files to be named things like repair.whatevernameyouwanthere.yml and so on,
     *  these files are treated in the same way as the vanilla file. They serve the purpose of organization
     * @param configPrefix the prefix of the file name, for example "repair", "salvage", etc
     * @param collection the collection that will be added to
     */
    public void loadCustomCollections(String configPrefix, Collection<?> collection, Class<? extends ConfigCollection> configClass)
    {
        String vanillaConfigFileName = getVanillaConfigName(configPrefix);

        //Find other files
        Pattern pattern = Pattern.compile(configPrefix+"\\.(?:.+)\\.yml");
        //File dataFolder = McmmoCore.getDataFolderPath();
        File dataFolder = mcMMO.p.getDataFolder();

        for (String fileName : dataFolder.list()) {
            //Vanilla Config is already loaded
            if(fileName.equalsIgnoreCase(vanillaConfigFileName))
                continue;

            //Find files that match the pattern
            if (!pattern.matcher(fileName).matches()) {
                continue;
            }

            //Init file
            File currentFile = new File(dataFolder, fileName);

            //Make sure its not a directory (needed?)
            if(currentFile.isDirectory())
                continue;

            try {
                //TODO: Valid?
                ConfigCollections customConfig = (ConfigCollections) getConfigClass(configPrefix).getConstructor(String.class).newInstance(configPrefix);
                collection.addAll(customConfig.getLoadedCollection());

            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
    }

    private String getVanillaConfigName(String configPrefix)
    {
        return configPrefix+DEFAULT_MULTICONFIG_FILENAME_SUFFIX;
    }

    private Class getConfigClass(String configPrefix)
    {
        switch(configPrefix) {
            case "repair":
                return RepairConfig.class;
            case "salvage":
                return SalvageConfig.class;
            default:
                mcMMO.p.getLogger().severe("[DEBUG] Config Class is null!");
                return null;
        }
    }

}
