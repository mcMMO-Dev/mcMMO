package com.gmail.nossr50.config;

import com.gmail.nossr50.mcMMO;
import com.google.common.io.Files;
import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.yaml.YAMLConfigurationLoader;
import org.yaml.snakeyaml.DumperOptions;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Handles loading and cacheing configuration settings from a configurable compatible config file
 */
public abstract class Config implements VersionedConfig, Unload {

    /* SETTINGS */
    private boolean mergeNewKeys; //Whether or not to merge keys found in the default config
    private boolean removeOldKeys; //Whether or not to remove unused keys form the config
    private boolean copyDefaults; //Whether or not to copy the default config when first creating the file

    /* PATH VARS */

    public final File DIRECTORY_DATA_FOLDER; //Directory that the file is in
    public final String FILE_RELATIVE_PATH; //Relative Path to the file
    protected final String DIRECTORY_DEFAULTS = "defaults";

    /* LOADERS */

    private ConfigurationLoader<CommentedConfigurationNode> defaultCopyLoader;
    private ConfigurationLoader<CommentedConfigurationNode> userCopyLoader;

    /* CONFIG FILES */

    private File resourceConfigCopy; //Copy of the default config from the JAR (file is copied so that admins can easily compare to defaults)
    private File resourceUserCopy; //File in the /$MCMMO_ROOT/mcMMO/ directory that may contain user edited settings

    /* ROOT NODES */

    private CommentedConfigurationNode userRootNode = null;
    private CommentedConfigurationNode defaultRootNode = null;

    /* CONFIG MANAGER */
    //private ConfigurationLoader<CommentedConfigurationNode> configManager;

    public Config(String pathToParentFolder, String relativePath, boolean mergeNewKeys, boolean copyDefaults, boolean removeOldKeys) {
        //TODO: Check if this works...
        this(new File(pathToParentFolder), relativePath, mergeNewKeys, copyDefaults, removeOldKeys);
        System.out.println("mcMMO Debug: Don't forget to check if loading config file by string instead of File works...");
    }

    public Config(File pathToParentFolder, String relativePath, boolean mergeNewKeys, boolean copyDefaults, boolean removeOldKeys) {
        /*
         * These must be at the top
         */
        this.copyDefaults = copyDefaults;
        this.mergeNewKeys = mergeNewKeys; //Whether or not we add new keys when they are found
        mkdirDefaults(); // Make our default config dir
        DIRECTORY_DATA_FOLDER = pathToParentFolder; //Data Folder for our plugin
        FILE_RELATIVE_PATH = relativePath; //Relative path to config from a parent folder

        registerUnload();

        //Attempt IO Operations
        try {
            //Makes sure we have valid Files corresponding to this config
            initConfigFiles();

            //Init MainConfig Loaders
            initConfigLoaders();

            //Load MainConfig Nodes
            loadConfig();

            //Attempt to update user file, and then load it into memory
            readConfig();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Registers with the config managers unloader
     * The unloader runs when the plugin gets disabled which cleans up registries to make reloading safe
     */
    private void registerUnload()
    {
        mcMMO.getConfigManager().registerUnloadable(this);
    }

    /**
     * Initializes the default copy File and the user config File
     * @throws IOException
     */
    private void initConfigFiles() throws IOException {
        //Init our config copy
        resourceConfigCopy = initDefaultConfig();

        //Init the user file
        resourceUserCopy = initUserConfig();
    }

    /**
     * Loads the root node for the default config File and user config File
     */
    private void loadConfig()
    {
        try {
            final CommentedConfigurationNode defaultConfig = this.defaultCopyLoader.load();
            defaultRootNode = defaultConfig;

            final CommentedConfigurationNode userConfig = this.userCopyLoader.load();
            userRootNode = userConfig;

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Initializes the YAMLConfigurationLoaders for this config
     */
    private void initConfigLoaders()
    {
        this.defaultCopyLoader = YAMLConfigurationLoader.builder().setPath(resourceConfigCopy.toPath()).setFlowStyle(DumperOptions.FlowStyle.BLOCK).build();
        this.userCopyLoader = YAMLConfigurationLoader.builder().setPath(resourceUserCopy.toPath()).setFlowStyle(DumperOptions.FlowStyle.FLOW).build();
    }

    /**
     * Copies a new file from the JAR to the defaults directory and uses that new file to initialize our resourceConfigCopy
     * @see Config#resourceConfigCopy
     * @throws IOException
     */
    private File initDefaultConfig() throws IOException {
        return copyDefaultFromJar(getDefaultConfigCopyRelativePath(), true);
    }

    /**
     * Attemps to load the config file if it exists, if it doesn't it copies a new one from within the JAR
     * @return user config File
     * @see Config#resourceUserCopy
     * @throws IOException
     */
    private File initUserConfig() throws IOException {
        File userCopy = new File(DIRECTORY_DATA_FOLDER, FILE_RELATIVE_PATH); //Load the user file;

        if(userCopy.exists())
        {
            // Yay
            return userCopy;
        }
        else
        {
            //If it's gone we copy default files
            //Note that we don't copy the values from the default copy put in /defaults/ that file exists only as a reference to admins and is unreliable
            if(copyDefaults)
                return copyDefaultFromJar(FILE_RELATIVE_PATH, false);
            else
            {
                //Make a new empty file
                userCopy.createNewFile();
                return userCopy;
            }
        }
    }

    /**
     * Used to make a new config file at a specified relative output path inside the data directory by copying the matching file found in that same relative path within the JAR
     * @param relativeOutputPath the path to the output file
     * @param deleteOld whether or not to delete the existing output file on disk
     * @return a copy of the default config within the JAR
     * @throws IOException
     */
    private File copyDefaultFromJar(String relativeOutputPath, boolean deleteOld) throws IOException
    {
        /*
         * Gen a Default config from inside the JAR
         */
        mcMMO.p.getLogger().info("Preparing to copy internal resource file (in JAR) - "+FILE_RELATIVE_PATH);
        //InputStream inputStream = McmmoCore.getResource(FILE_RELATIVE_PATH);
        InputStream inputStream = mcMMO.p.getResource(FILE_RELATIVE_PATH);

        byte[] buffer = new byte[inputStream.available()];
        inputStream.read(buffer);

        //This is a copy of the default file, which we will overwrite every time mcMMO loads
        File targetFile = new File(DIRECTORY_DATA_FOLDER, relativeOutputPath);

        //Wipe old default file on disk
        if (targetFile.exists() && deleteOld)
        {
            mcMMO.p.getLogger().info("Updating file " + relativeOutputPath);
            targetFile.delete(); //Necessary?
        }

        if(!targetFile.exists())
        {
            targetFile.getParentFile().mkdirs();
            targetFile.createNewFile(); //New File Boys
        }

        Files.write(buffer, targetFile);
        mcMMO.p.getLogger().info("Created config file - " + relativeOutputPath);

        inputStream.close(); //Close the input stream

        return targetFile;
    }

    /**
     * The path to the defaults directory
     * @return the path to the defaults directory
     */
    private String getDefaultConfigCopyRelativePath() {
        return DIRECTORY_DEFAULTS + File.separator + FILE_RELATIVE_PATH;
    }

    /**
     * Creates the defaults directory
     */
    private void mkdirDefaults() {
        //Make Default Subdirectory
        File defaultsDir = new File (DIRECTORY_DATA_FOLDER, "defaults");

        if(!defaultsDir.exists())
            defaultsDir.mkdir();
    }

    /**
     * Configs are versioned based on when they had significant changes to keys
     * @return current MainConfig Version String
     */
    public String getVersion()
    {
         return String.valueOf(getConfigVersion());
    }

    /**
     * Attempts to read the loaded config file
     * MainConfig will have any necessary updates applied
     * MainConfig will be compared to the default config to see if it is missing any nodes
     * MainConfig will have any missing nodes inserted with their default value
     */
    public void readConfig() {
        mcMMO.p.getLogger().info("Attempting to read " + FILE_RELATIVE_PATH + ".");

        int version = this.userRootNode.getNode("ConfigVersion").getInt();
        mcMMO.p.getLogger().info(FILE_RELATIVE_PATH + " version is " + version);

        //Update our config
        updateConfig();
    }

    /**
     * Compares the users config file to the default and adds any missing nodes and applies any necessary updates
     */
    private void updateConfig()
    {
        mcMMO.p.getLogger().info(defaultRootNode.getChildrenMap().size() +" items in default children map");
        mcMMO.p.getLogger().info(userRootNode.getChildrenMap().size() +" items in default root map");

        // Merge Values from default
        if(mergeNewKeys)
            userRootNode = userRootNode.mergeValuesFrom(defaultRootNode);

        removeOldKeys();

        // Update config version
        updateConfigVersion();

        //Attempt to save
        try {
            saveUserCopy();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Finds any keys in the users config that are not present in the default config and removes them
     */
    //TODO: Finish this
    private void removeOldKeys()
    {
        if(!removeOldKeys)
            return;

        for(CommentedConfigurationNode configurationNode : defaultRootNode.getChildrenList())
        {

        }
    }

    /**
     * Saves the current state information of the config to the users copy (which they may edit)
     * @throws IOException
     */
    private void saveUserCopy() throws IOException
    {
        mcMMO.p.getLogger().info("Saving new node");
        userCopyLoader.save(userRootNode);
    }

    /**
     * Performs any necessary operations to update this config
     */
    private void updateConfigVersion() {
        // Set a version for our config
        this.userRootNode.getNode("ConfigVersion").setValue(getConfigVersion());
        mcMMO.p.getLogger().info("Updated config to ["+getConfigVersion()+"] - " + FILE_RELATIVE_PATH);
    }

    /**
     * Returns the root node of this config
     * @return the root node of this config
     */
    protected CommentedConfigurationNode getUserRootNode() {
        return userRootNode;
    }

    /**
     * Gets an int from the config and casts it to short before returning
     * @param path the path to the int
     * @return the value of the int after being cast to short at the node, null references will zero initialize
     */
    public short getShortValue(String... path) { return (short) userRootNode.getNode(path).getInt();}

    /**
     * Grabs an int from the specified node
     * @param path
     * @return the int from the node, null references will zero initialize
     */
    public int getIntValue(String... path)
    {
        return userRootNode.getNode(path).getInt();
    }

    /**
     * Grabs a double from the specified node
     * @param path
     * @return the double from the node, null references will zero initialize
     */
    public double getDoubleValue(String... path)
    {
        return userRootNode.getNode(path).getDouble();
    }

    /**
     * Grabs a long from the specified node
     * @param path
     * @return the long from the node, null references will zero initialize
     */
    public long getLongValue(String... path)
    {
        return userRootNode.getNode(path).getLong();
    }

    /**
     * Grabs a boolean from the specified node
     * @param path
     * @return the boolean from the node, null references will zero initialize
     */
    public boolean getBooleanValue(String... path)
    {
        return userRootNode.getNode(path).getBoolean();
    }

    /**
     * Grabs a string from the specified node
     * @param path
     * @return the string from the node, null references will zero initialize
     */
    public String getStringValue(String... path)
    {
        return userRootNode.getNode(path).getString();
    }

    /**
     * Checks to see if a node exists in the user's config file
     * @param path path to the node
     * @return true if the node exists
     */
    public boolean hasNode(String... path) {
        return (userRootNode.getNode(path) != null);
    }

    /**
     * Gets a a List of type String from the Configuration file
     * @param path path to the node
     * @return a list of strings at the node, if null it will most likely zero initialize (empty list)
     * @throws ObjectMappingException
     */
    public List<String> getStringValueList(String... path) throws ObjectMappingException {
        return userRootNode.getList(TypeToken.of(String.class));
    }
}
