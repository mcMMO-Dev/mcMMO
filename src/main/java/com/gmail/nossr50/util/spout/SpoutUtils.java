package com.gmail.nossr50.util.spout;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.event.spout.SpoutCraftEnableEvent;
import org.getspout.spoutapi.keyboard.Keyboard;
import org.getspout.spoutapi.player.FileManager;
import org.getspout.spoutapi.player.SpoutPlayer;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.config.spout.SpoutConfig;
import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.listeners.SpoutListener;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.StringUtils;
import com.gmail.nossr50.util.player.UserManager;
import com.gmail.nossr50.util.skills.SkillUtils;

public class SpoutUtils {
    private static mcMMO plugin = mcMMO.p;

    public final static String spoutDirectory = mcMMO.getMainDirectory() + "Resources" + File.separator;
    public final static String hudDirectory = spoutDirectory + "HUD" + File.separator;
    public final static String hudStandardDirectory = hudDirectory + "Standard" + File.separator;
    public final static String hudRetroDirectory = hudDirectory + "Retro" + File.separator;
    public final static String soundDirectory = spoutDirectory + "Sound" + File.separator;

    public static boolean showPowerLevel;

    private final static SpoutListener spoutListener = new SpoutListener();
    public static Keyboard menuKey;

    /**
     * Write file to disk.
     *
     * @param theFileName The name of the file
     * @param theFilePath The name of the file path
     */
    private static void writeFile(String theFileName, String theFilePath) {
        InputStream is = null;
        OutputStream os = null;
        JarFile jar = null;

        try {
            File currentFile = new File(theFilePath + theFileName);

            // No point in writing the file again if it already exists.
            if (currentFile.exists()) {
                return;
            }

            jar = new JarFile(mcMMO.mcmmo);
            JarEntry entry = jar.getJarEntry("resources/" + theFileName);
            is = jar.getInputStream(entry);

            byte[] buf = new byte[2048];
            int nbRead;

            os = new BufferedOutputStream(new FileOutputStream(currentFile));

            while ((nbRead = is.read(buf)) != -1) {
                os.write(buf, 0, nbRead);
            }

            os.flush();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (is != null) {
                try {
                    is.close();
                }
                catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            if (os != null) {
                try {
                    os.close();
                }
                catch (IOException ex) {
                    ex.printStackTrace();
                }
            }

            if (jar != null) {
                try {
                    jar.close();
                }
                catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    /**
     * Extract Spout files to the Resources directory.
     */
    public static void extractFiles() {
        // Setup directories
        new File(spoutDirectory).mkdir();
        new File(hudDirectory).mkdir();
        new File(hudStandardDirectory).mkdir();
        new File(hudRetroDirectory).mkdir();
        new File(soundDirectory).mkdir();

        // XP Bar images
        for (int x = 0; x < 255; x++) {
            String theFileName;

            if (x < 10) {
                theFileName = "xpbar_inc00" + x + ".png";
            }
            else if (x < 100) {
                theFileName = "xpbar_inc0" + x + ".png";
            }
            else {
                theFileName = "xpbar_inc" + x + ".png";
            }

            writeFile(theFileName, hudStandardDirectory);
        }

        // Standard XP Icons
        for (SkillType skillType : SkillType.values()) {
            if (skillType.isChildSkill()) {
                continue;
            }

            String skillTypeString = StringUtils.getCapitalized(skillType.toString());

            writeFile(skillTypeString + ".png", hudStandardDirectory);
            writeFile(skillTypeString + "_r.png", hudRetroDirectory);
        }

        // Blank icons
        writeFile("Icon.png", hudStandardDirectory);
        writeFile("Icon_r.png", hudRetroDirectory);

        // Sound FX
        writeFile("level.wav", soundDirectory);
    }

    /**
     * Setup Spout config options
     */
    public static void setupSpoutConfigs() {
        showPowerLevel = SpoutConfig.getInstance().getShowPowerLevel();
        String temp = SpoutConfig.getInstance().getMenuKey();

        for (Keyboard x : Keyboard.values()) {
            if (x.toString().equalsIgnoreCase(temp)) {
                menuKey = x;
            }
        }

        if (menuKey == null) {
            mcMMO.p.getLogger().warning("Invalid KEY for Menu.Key, using KEY_M");
            menuKey = Keyboard.KEY_M;
        }
    }

    /**
     * Get all the Spout files in the Resources folder.
     *
     * @return a list of all files is the Resources folder
     */
    public static ArrayList<File> getFiles() {
        ArrayList<File> files = new ArrayList<File>();

        // XP BAR
        for (int x = 0; x < 255; x++) {
            if (x < 10) {
                files.add(new File(hudStandardDirectory + "xpbar_inc00" + x + ".png"));
            }
            else if (x < 100) {
                files.add(new File(hudStandardDirectory + "xpbar_inc0" + x + ".png"));
            }
            else {
                files.add(new File(hudStandardDirectory + "xpbar_inc" + x + ".png"));
            }
        }

        // Standard XP Icons
        for (SkillType skillType : SkillType.values()) {
            if (skillType.isChildSkill()) {
                continue;
            }

            String skillTypeString = StringUtils.getCapitalized(skillType.toString());

            files.add(new File(hudStandardDirectory + skillTypeString + ".png"));
            files.add(new File(hudRetroDirectory + skillTypeString + "_r.png"));
        }

        // Blank icons
        files.add(new File(hudStandardDirectory + "Icon.png"));
        files.add(new File(hudRetroDirectory + "Icon_r.png"));

        // Level SFX
        files.add(new File(soundDirectory + "level.wav"));

        return files;
    }

    /**
     * Register custom Spout events.
     */
    public static void registerCustomEvent() {
        plugin.getServer().getPluginManager().registerEvents(spoutListener, plugin);
    }

    /**
     * Handle level-up notifications through Spout.
     *
     * @param skillType The skill that leveled up
     * @param spoutPlayer The player that leveled up
     */
    public static void levelUpNotification(SkillType skillType, SpoutPlayer spoutPlayer) {
        PlayerProfile profile = UserManager.getPlayer(spoutPlayer).getProfile();
        int notificationTier = getNotificationTier(profile.getSkillLevel(skillType));
        Material mat = null;

        switch (skillType) {
            case TAMING:
                switch (notificationTier) {
                    case 1:
                    case 2:
                        mat = Material.PORK;
                        break;

                    case 3:
                    case 4:
                        mat = Material.GRILLED_PORK;
                        break;

                    case 5:
                        mat = Material.BONE;
                        break;

                    default:
                        break;
                }

                break;

            case MINING:
                switch (notificationTier) {
                    case 1:
                        mat = Material.COAL_ORE;
                        break;

                    case 2:
                        mat = Material.IRON_ORE;
                        break;

                    case 3:
                        mat = Material.GOLD_ORE;
                        break;

                    case 4:
                        mat = Material.DIAMOND_ORE;
                        break;

                    case 5:
                        mat = Material.EMERALD_ORE;
                        break;

                    default:
                        break;
                }

                break;

            case WOODCUTTING:
                switch (notificationTier) {
                    case 1:
                        mat = Material.STICK;
                        break;

                    case 2:
                    case 3:
                        mat = Material.WOOD;
                        break;

                    case 4:
                    case 5:
                        mat = Material.LOG;
                        break;

                    default:
                        break;
                }

                break;

            case REPAIR:
                mat = Material.ANVIL;
                break;

            case HERBALISM:
                switch (notificationTier) {
                    case 1:
                        mat = Material.YELLOW_FLOWER;
                        break;

                    case 2:
                        mat = Material.RED_ROSE;
                        break;

                    case 3:
                        mat = Material.BROWN_MUSHROOM;
                        break;

                    case 4:
                        mat = Material.RED_MUSHROOM;
                        break;

                    case 5:
                        mat = Material.PUMPKIN;
                        break;

                    default:
                        break;
                }

                break;

            case ACROBATICS:
                switch (notificationTier) {
                    case 1:
                        mat = Material.LEATHER_BOOTS;
                        break;

                    case 2:
                        mat = Material.CHAINMAIL_BOOTS;
                        break;

                    case 3:
                        mat = Material.IRON_BOOTS;
                        break;

                    case 4:
                        mat = Material.GOLD_BOOTS;
                        break;

                    case 5:
                        mat = Material.DIAMOND_BOOTS;
                        break;

                    default:
                        break;
                }

                break;

            case SWORDS:
                switch (notificationTier) {
                    case 1:
                        mat = Material.WOOD_SWORD;
                        break;

                    case 2:
                        mat = Material.STONE_SWORD;
                        break;

                    case 3:
                        mat = Material.IRON_SWORD;
                        break;

                    case 4:
                        mat = Material.GOLD_SWORD;
                        break;

                    case 5:
                        mat = Material.DIAMOND_SWORD;
                        break;

                    default:
                        break;
                }

                break;

            case ARCHERY:
                switch (notificationTier) {
                    case 1:
                    case 2:
                    case 3:
                        mat = Material.ARROW;
                        break;

                    case 4:
                    case 5:
                        mat = Material.BOW;
                        break;

                    default:
                        break;
                }

                break;

            case UNARMED:
                switch (notificationTier) {
                    case 1:
                        mat = Material.LEATHER_HELMET;
                        break;

                    case 2:
                        mat = Material.CHAINMAIL_HELMET;
                        break;

                    case 3:
                        mat = Material.IRON_HELMET;
                        break;

                    case 4:
                        mat = Material.GOLD_HELMET;
                        break;

                    case 5:
                        mat = Material.DIAMOND_HELMET;
                        break;

                    default:
                        break;
                }

                break;

            case EXCAVATION:
                switch (notificationTier) {
                    case 1:
                        mat = Material.WOOD_SPADE;
                        break;

                    case 2:
                        mat = Material.STONE_SPADE;
                        break;

                    case 3:
                        mat = Material.IRON_SPADE;
                        break;

                    case 4:
                        mat = Material.GOLD_SPADE;
                        break;

                    case 5:
                        mat = Material.DIAMOND_SPADE;
                        break;

                    default:
                        break;
                }

                break;

            case AXES:
                switch (notificationTier) {
                    case 1:
                        mat = Material.WOOD_AXE;
                        break;

                    case 2:
                        mat = Material.STONE_AXE;
                        break;

                    case 3:
                        mat = Material.IRON_AXE;
                        break;

                    case 4:
                        mat = Material.GOLD_AXE;
                        break;

                    case 5:
                        mat = Material.DIAMOND_AXE;
                        break;

                    default:
                        break;
                }

                break;

            case FISHING:
                switch (notificationTier) {
                    case 1:
                    case 2:
                        mat = Material.RAW_FISH;
                        break;

                    case 3:
                    case 4:
                        mat = Material.COOKED_FISH;
                        break;

                    case 5:
                        mat = Material.FISHING_ROD;
                        break;

                    default:
                        break;
                }

                break;

            default:
                mat = Material.WATCH;
                break;
        }

        spoutPlayer.sendNotification(LocaleLoader.getString("Spout.LevelUp.1"), LocaleLoader.getString("Spout.LevelUp.2", SkillUtils.getSkillName(skillType), profile.getSkillLevel(skillType)), mat);
        SpoutSoundUtils.playLevelUpNoise(spoutPlayer, plugin);
    }

    /**
     * Gets the notification tier of a skill.
     *
     * @param level The level of the skill
     * @return the notification tier of the skill
     */
    private static int getNotificationTier(int level) {
        if (level >= AdvancedConfig.getInstance().getSpoutNotificationTier4()) {
            return 5;
        }
        else if (level >= AdvancedConfig.getInstance().getSpoutNotificationTier3()) {
            return 4;
        }
        else if (level >= AdvancedConfig.getInstance().getSpoutNotificationTier2()) {
            return 3;
        }
        else if (level >= AdvancedConfig.getInstance().getSpoutNotificationTier1()) {
            return 2;
        }
        else {
            return 1;
        }
    }

    /**
     * Re-enable SpoutCraft for players after a /reload
     */
    public static void reloadSpoutPlayers() {
        for (SpoutPlayer spoutPlayer : SpoutManager.getPlayerChunkMap().getOnlinePlayers()) {
            mcMMO.p.getServer().getPluginManager().callEvent(new SpoutCraftEnableEvent(spoutPlayer));
        }
    }

    public static void reloadSpoutPlayer(Player player) {
        SpoutPlayer spoutPlayer = SpoutManager.getPlayer(player);

        if (spoutPlayer != null) {
            mcMMO.p.getServer().getPluginManager().callEvent(new SpoutCraftEnableEvent(spoutPlayer));
        }
    }

    public static void preCacheFiles() {
        extractFiles(); // Extract source materials

        FileManager FM = SpoutManager.getFileManager();
        FM.addToPreLoginCache(plugin, getFiles());
    }
}
