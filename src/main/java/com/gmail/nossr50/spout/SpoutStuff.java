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

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.keyboard.Keyboard;
import org.getspout.spoutapi.player.SpoutPlayer;

import com.gmail.nossr50.Users;
import com.gmail.nossr50.m;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.datatypes.HUDmmo;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.popups.PopupMMO;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.listeners.mcSpoutInputListener;
import com.gmail.nossr50.listeners.mcSpoutListener;
import com.gmail.nossr50.listeners.mcSpoutScreenListener;

public class SpoutStuff {

    static mcMMO plugin = (mcMMO) Bukkit.getServer().getPluginManager().getPlugin("mcMMO");

    private final static mcSpoutListener spoutListener = new mcSpoutListener(plugin);
    private final static mcSpoutInputListener spoutInputListener = new mcSpoutInputListener(plugin);
    private final static mcSpoutScreenListener spoutScreenListener = new mcSpoutScreenListener(plugin);

    public static HashMap<Player, HUDmmo> playerHUDs = new HashMap<Player, HUDmmo>();
    public static HashMap<SpoutPlayer, PopupMMO> playerScreens = new HashMap<SpoutPlayer, PopupMMO>();

    public static Keyboard keypress;

    /**
     * Write file to disk.
     *
     * @param theFileName The name of the file
     * @param theFilePath The name of the file path
     */
    public static void writeFile(String theFileName, String theFilePath) {
        try {
            File currentFile = new File("plugins/mcMMO/Resources/" + theFilePath + theFileName);

            @SuppressWarnings("static-access")
            JarFile jar = new JarFile(plugin.mcmmo);
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
        new File("plugins/mcMMO/Resources/").mkdir();
        new File("plugins/mcMMO/Resources/HUD/").mkdir();
        new File("plugins/mcMMO/Resources/HUD/Standard/").mkdir();
        new File("plugins/mcMMO/Resources/HUD/Retro/").mkdir();
        new File("plugins/mcMMO/Resources/Sound/").mkdir();

        //XP Bar images
        for (int x = 0; x < 255; x++) {
            String theFilePath = "HUD/Standard/";
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

            writeFile(theFileName, theFilePath);
        }

        //Standard XP Icons
        String standardFilePath = "HUD/Standard/";
        String retroFilePath = "HUD/Retro/";

        for (SkillType y : SkillType.values()) {
            if (y.equals(SkillType.ALL)) {
                continue;
            }

            String standardFileName = m.getCapitalized(y.toString())+".png";
            String retroFileName = m.getCapitalized(y.toString())+"_r.png";

            writeFile(standardFileName, standardFilePath);
            writeFile(retroFileName, retroFilePath);
        }

        //Blank icons
        writeFile("Icon.png", standardFilePath);
        writeFile("Icon_r.png", retroFilePath);

        //Sound FX
        String theSoundFilePath = "Sound/";

        writeFile("repair.wav", theSoundFilePath);
        writeFile("level.wav", theSoundFilePath);
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
        String dir = "plugins/mcMMO/Resources/";

        /* XP BAR */
        for (int x = 0; x < 255; x++) {
            if (x < 10) {
                files.add(new File(dir + "HUD/Standard/xpbar_inc00" + x + ".png"));
            }
            else if (x < 100) {
                files.add(new File(dir + "HUD/Standard/xpbar_inc0" + x + ".png"));
            }
            else {
                files.add(new File(dir + "HUD/Standard/xpbar_inc" + x + ".png"));
            }
        }

        /* Standard XP Icons */
        for (SkillType y : SkillType.values()) {
            if (y.equals(SkillType.ALL)) {
                continue;
            }

            files.add(new File(dir + "HUD/Standard/" + m.getCapitalized(y.toString()) + ".png"));
            files.add(new File(dir + "HUD/Retro/" + m.getCapitalized(y.toString()) + "_r.png"));
        }
        
        /* Blank icons */
        files.add(new File(dir + "HUD/Standard/Icon.png"));
        files.add(new File(dir + "HUD/Retro/Icon_r.png"));

        //Repair SFX
        files.add(new File(dir + "Sound/repair.wav"));

        //Level SFX
        files.add(new File(dir + "Sound/level.wav"));

        return files;
    }

    /**
     * Register custom Spout events.
     */
    public static void registerCustomEvent() {
        Bukkit.getServer().getPluginManager().registerEvents(spoutListener, plugin);
        Bukkit.getServer().getPluginManager().registerEvents(spoutInputListener, plugin);
        Bukkit.getServer().getPluginManager().registerEvents(spoutScreenListener, plugin);
    }

    /**
     * Gets a Spout player from a player name.
     *
     * @param playerName The player name
     * @return the SpoutPlayer related to this player name, null if there's no player online with that name.
     */
    public static SpoutPlayer getSpoutPlayer(String playerName) {
        for (Player x : Bukkit.getServer().getOnlinePlayers()) {
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
        sPlayer.sendNotification(ChatColor.GREEN + "Level Up!", ChatColor.YELLOW + m.getCapitalized(skillType.toString()) + ChatColor.DARK_AQUA + " (" + ChatColor.GREEN + PP.getSkillLevel(skillType) + ChatColor.DARK_AQUA + ")", mat);
        SpoutSounds.playLevelUpNoise(sPlayer);
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
