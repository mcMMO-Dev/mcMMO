package com.gmail.nossr50.spout;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.keyboard.Keyboard;
import org.getspout.spoutapi.player.SpoutPlayer;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.datatypes.HUDmmo;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.popups.PopupMMO;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.listeners.SpoutInputListener;
import com.gmail.nossr50.listeners.SpoutListener;
import com.gmail.nossr50.listeners.SpoutScreenListener;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Users;

public class SpoutStuff {
    private static mcMMO plugin = mcMMO.p;

    public final static String spoutDirectory = mcMMO.p.mainDirectory + "Resources" + File.separator;
    public final static String hudDirectory = spoutDirectory + "HUD" + File.separator;
    public final static String hudStandardDirectory = hudDirectory + "Standard" + File.separator;
    public final static String hudRetroDirectory = hudDirectory + "Retro" + File.separator;
    public final static String soundDirectory = spoutDirectory + "Sound" + File.separator;

    private final static SpoutListener spoutListener = new SpoutListener(plugin);
    private final static SpoutInputListener spoutInputListener = new SpoutInputListener(plugin);
    private final static SpoutScreenListener spoutScreenListener = new SpoutScreenListener(plugin);

    public static HashMap<Player, HUDmmo> playerHUDs = new HashMap<Player, HUDmmo>();
    public static HashMap<SpoutPlayer, PopupMMO> playerScreens = new HashMap<SpoutPlayer, PopupMMO>();

    public static Keyboard keypress;

    /**
     * Write file to disk.
     *
     * @param theFileName The name of the file
     * @param theFilePath The name of the file path
     */
    private static void writeFile(String theFileName, String theFilePath) {
        try {
            File currentFile = new File(theFilePath + theFileName);

            JarFile jar = new JarFile(mcMMO.mcmmo);
            JarEntry entry = jar.getJarEntry("resources/" + theFileName);
            InputStream is = jar.getInputStream(entry);

            byte[] buf = new byte[2048];
            int nbRead;

            OutputStream os = new BufferedOutputStream(new FileOutputStream(currentFile));

            while ((nbRead = is.read(buf)) != -1) {
                os.write(buf, 0, nbRead);
            }

            os.flush();
            os.close();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Extract Spout files to the Resources directory.
     */
    public static void extractFiles() {

        //Setup directories
        new File(spoutDirectory).mkdir();
        new File(hudDirectory).mkdir();
        new File(hudStandardDirectory).mkdir();
        new File(hudRetroDirectory).mkdir();
        new File(soundDirectory).mkdir();

        //XP Bar images
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

        //Standard XP Icons
        for (SkillType y : SkillType.values()) {
            if (y.equals(SkillType.ALL)) {
                continue;
            }

            String standardFileName = Misc.getCapitalized(y.toString())+".png";
            String retroFileName = Misc.getCapitalized(y.toString())+"_r.png";

            writeFile(standardFileName, hudStandardDirectory);
            writeFile(retroFileName, hudRetroDirectory);
        }

        //Blank icons
        writeFile("Icon.png", hudStandardDirectory);
        writeFile("Icon_r.png", hudRetroDirectory);

        //Sound FX
        writeFile("repair.wav", soundDirectory);
        writeFile("level.wav", soundDirectory);
    }

    /**
     * Setup Spout config options
     */
    public static void setupSpoutConfigs() {
        String temp = plugin.getConfig().getString("Spout.Menu.Key", "KEY_M");

        for (Keyboard x : Keyboard.values()) {
            if (x.toString().equalsIgnoreCase(temp)) {
                keypress = x;
            }
        }

        if (keypress == null) {
            System.out.println("Invalid KEY for Spout.Menu.Key, using KEY_M");
            keypress = Keyboard.KEY_M;
        }
    }

    /**
     * Get all the Spout files in the Resources folder.
     *
     * @return a list of all files is the Resources folder
     */
    public static ArrayList<File> getFiles() {
        ArrayList<File> files = new ArrayList<File>();

        /* XP BAR */
        for (int x = 0; x < 255; x++) {
            if (x < 10) {
                files.add(new File(hudStandardDirectory + "xpbar_inc00" + x + ".png"));
            }
            else if (x < 100) {
                files.add(new File(hudStandardDirectory  + "xpbar_inc0" + x + ".png"));
            }
            else {
                files.add(new File(hudStandardDirectory  + "xpbar_inc" + x + ".png"));
            }
        }

        /* Standard XP Icons */
        for (SkillType y : SkillType.values()) {
            if (y.equals(SkillType.ALL)) {
                continue;
            }

            files.add(new File(hudStandardDirectory + Misc.getCapitalized(y.toString()) + ".png"));
            files.add(new File(hudRetroDirectory + Misc.getCapitalized(y.toString()) + "_r.png"));
        }
        
        /* Blank icons */
        files.add(new File(hudStandardDirectory + "Icon.png"));
        files.add(new File(hudRetroDirectory + "Icon_r.png"));

        //Repair SFX
        files.add(new File(soundDirectory + "repair.wav"));

        //Level SFX
        files.add(new File(soundDirectory + "level.wav"));

        return files;
    }

    /**
     * Register custom Spout events.
     */
    public static void registerCustomEvent() {
        plugin.getServer().getPluginManager().registerEvents(spoutListener, plugin);
        plugin.getServer().getPluginManager().registerEvents(spoutInputListener, plugin);
        plugin.getServer().getPluginManager().registerEvents(spoutScreenListener, plugin);
    }

    /**
     * Gets a Spout player from a player name.
     *
     * @param playerName The player name
     * @return the SpoutPlayer related to this player name, null if there's no player online with that name.
     */
    public static SpoutPlayer getSpoutPlayer(String playerName) {
        for (Player x : plugin.getServer().getOnlinePlayers()) {
            if (x.getName().equalsIgnoreCase(playerName)) {
                return SpoutManager.getPlayer(x);
            }
        }
        return null;
    }

    /**
     * Handle level-up notifications through Spout.
     *
     * @param skillType The skill that leveled up
     * @param sPlayer The player that leveled up
     */
    public static void levelUpNotification(SkillType skillType, SpoutPlayer sPlayer) {
        PlayerProfile PP = Users.getProfile(sPlayer);
        int notificationTier = getNotificationTier(PP.getSkillLevel(skillType));
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
                mat = Material.LAPIS_ORE;
                break;

            case 5:
                mat = Material.DIAMOND_ORE;
                break;

            default:
                break;
            }

            break;

        case WOODCUTTING:
            switch (notificationTier) {
            case 1:
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
            switch (notificationTier) {
            case 1:
                mat = Material.COBBLESTONE;
                break;

            case 2:
                mat = Material.IRON_BLOCK;
                break;

            case 3:
                mat = Material.GOLD_BLOCK;
                break;

            case 4:
                mat = Material.LAPIS_BLOCK;
                break;

            case 5:
                mat = Material.DIAMOND_BLOCK;
                break;

            default:
                break;
            }

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

        //TODO: Use Locale
        sPlayer.sendNotification(ChatColor.GREEN + "Level Up!", ChatColor.YELLOW + Misc.getCapitalized(skillType.toString()) + ChatColor.DARK_AQUA + " (" + ChatColor.GREEN + PP.getSkillLevel(skillType) + ChatColor.DARK_AQUA + ")", mat);
        SpoutSounds.playLevelUpNoise(sPlayer, plugin);
    }

    /**
     * Gets the notification tier of a skill.
     *
     * @param level The level of the skill
     * @return the notification tier of the skill
     */
    private static Integer getNotificationTier(Integer level) {
        if (level >= 800) {
            return 5;
        }
        else if (level >= 600) {
            return 4;
        }
        else if (level >= 400) {
            return 3;
        }
        else if (level >= 200) {
            return 2;
        }
        else {
            return 1;
        }
    }

    /**
     * Update a player's Spout XP bar.
     *
     * @param player The player whose bar to update
     */
    public static void updateXpBar(Player player) {
        playerHUDs.get(player).updateXpBarDisplay(Users.getProfile(player).getHUDType(), player); //Is there a reason we can't just do HUDmmo.updateXpBarDisplay?
    }
}
