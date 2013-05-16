package com.gmail.nossr50.util.spout;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.jar.JarFile;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.event.spout.SpoutCraftEnableEvent;
import org.getspout.spoutapi.player.SpoutPlayer;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.spout.SpoutConfig;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.skills.repair.Repair;
import com.gmail.nossr50.util.StringUtils;
import com.gmail.nossr50.util.player.UserManager;
import com.gmail.nossr50.util.skills.SkillUtils;

public class SpoutUtils {
    // The order of the values is extremely important, a few methods depend on it to work properly
    protected enum Tier {
        FOUR(4) {
            @Override public int getLevel() { return SpoutConfig.getInstance().getNotificationTier4(); }
            @Override protected Material getAcrobaticsNotificationItem() { return Material.DIAMOND_BOOTS; }
            @Override protected Material getArcheryNotificationItem() { return Material.BOW; }
            @Override protected Material getAxesNotificationItem() { return Material.DIAMOND_AXE; }
            @Override protected Material getExcavationNotificationItem() { return Material.CLAY; }
            @Override protected Material getFishingNotificationItem() { return Material.FISHING_ROD; }
            @Override protected Material getHerbalismNotificationItem() { return Material.WATER_LILY; }
            @Override protected Material getMiningNotificationItem() { return Material.EMERALD_ORE; }
            @Override protected Material getSwordsNotificationItem() { return Material.DIAMOND_SWORD; }
            @Override protected Material getTamingNotificationItem() { return Material.BONE; }
            @Override protected Material getUnarmedNotificationItem() { return Material.DIAMOND_HELMET; }
            @Override protected Material getWoodcuttingNotificationItem() { return Material.LOG; }},
        THREE(3) {
            @Override public int getLevel() { return SpoutConfig.getInstance().getNotificationTier3(); }
            @Override protected Material getAcrobaticsNotificationItem() { return Material.GOLD_BOOTS; }
            @Override protected Material getArcheryNotificationItem() { return Material.ARROW; }
            @Override protected Material getAxesNotificationItem() { return Material.GOLD_AXE; }
            @Override protected Material getExcavationNotificationItem() { return Material.SAND; }
            @Override protected Material getFishingNotificationItem() { return Material.COOKED_FISH; }
            @Override protected Material getHerbalismNotificationItem() { return Material.RED_ROSE; }
            @Override protected Material getMiningNotificationItem() { return Material.DIAMOND_ORE; }
            @Override protected Material getSwordsNotificationItem() { return Material.GOLD_SWORD; }
            @Override protected Material getTamingNotificationItem() { return Material.GRILLED_PORK; }
            @Override protected Material getUnarmedNotificationItem() { return Material.GOLD_HELMET; }
            @Override protected Material getWoodcuttingNotificationItem() { return Material.WOOD; }},
        TWO(2) {
            @Override public int getLevel() { return SpoutConfig.getInstance().getNotificationTier2(); }
            @Override protected Material getAcrobaticsNotificationItem() { return Material.IRON_BOOTS; }
            @Override protected Material getArcheryNotificationItem() { return Material.ARROW; }
            @Override protected Material getAxesNotificationItem() { return Material.IRON_AXE; }
            @Override protected Material getExcavationNotificationItem() { return Material.GRAVEL; }
            @Override protected Material getFishingNotificationItem() { return Material.COOKED_FISH; }
            @Override protected Material getHerbalismNotificationItem() { return Material.YELLOW_FLOWER; }
            @Override protected Material getMiningNotificationItem() { return Material.GOLD_ORE; }
            @Override protected Material getSwordsNotificationItem() { return Material.IRON_SWORD; }
            @Override protected Material getTamingNotificationItem() { return Material.GRILLED_PORK; }
            @Override protected Material getUnarmedNotificationItem() { return Material.IRON_HELMET; }
            @Override protected Material getWoodcuttingNotificationItem() { return Material.LEAVES; }},
        ONE(1) {
            @Override public int getLevel() { return SpoutConfig.getInstance().getNotificationTier1(); }
            @Override protected Material getAcrobaticsNotificationItem() { return Material.CHAINMAIL_BOOTS; }
            @Override protected Material getArcheryNotificationItem() { return Material.FLINT; }
            @Override protected Material getAxesNotificationItem() { return Material.STONE_AXE; }
            @Override protected Material getExcavationNotificationItem() { return Material.GRASS; }
            @Override protected Material getFishingNotificationItem() { return Material.RAW_FISH; }
            @Override protected Material getHerbalismNotificationItem() { return Material.CACTUS; }
            @Override protected Material getMiningNotificationItem() { return Material.IRON_ORE; }
            @Override protected Material getSwordsNotificationItem() { return Material.STONE_SWORD; }
            @Override protected Material getTamingNotificationItem() { return Material.PORK; }
            @Override protected Material getUnarmedNotificationItem() { return Material.CHAINMAIL_HELMET; }
            @Override protected Material getWoodcuttingNotificationItem() { return Material.SAPLING; }};

        int numerical;

        private Tier(int numerical) {
            this.numerical = numerical;
        }

        public int toNumerical() {
            return numerical;
        }

        abstract protected int getLevel();
        abstract protected Material getAcrobaticsNotificationItem();
        abstract protected Material getArcheryNotificationItem();
        abstract protected Material getAxesNotificationItem();
        abstract protected Material getExcavationNotificationItem();
        abstract protected Material getFishingNotificationItem();
        abstract protected Material getHerbalismNotificationItem();
        abstract protected Material getMiningNotificationItem();
        abstract protected Material getSwordsNotificationItem();
        abstract protected Material getTamingNotificationItem();
        abstract protected Material getUnarmedNotificationItem();
        abstract protected Material getWoodcuttingNotificationItem();
    }

    private final static String spoutDirectory = mcMMO.getMainDirectory() + "Resources" + File.separator;
    private final static String hudDirectory = spoutDirectory + "HUD" + File.separator;
    private final static String hudStandardDirectory = hudDirectory + "Standard" + File.separator;
    private final static String hudRetroDirectory = hudDirectory + "Retro" + File.separator;
    private final static String soundDirectory = spoutDirectory + "Sound" + File.separator;

    /**
     * Write file to disk.
     *
     * @param fileName The name of the file
     * @param filePath The name of the file path
     */
    private static File writeFile(String fileName, String filePath) {
        File currentFile = new File(filePath + fileName);
        BufferedOutputStream os = null;
        JarFile jar = null;

        // No point in writing the file again if it already exists.
        if (currentFile.exists()) {
            return currentFile;
        }

        try {
            jar = new JarFile(mcMMO.mcmmo);

            @SuppressWarnings("resource")
            InputStream is = jar.getInputStream(jar.getJarEntry("resources/" + fileName));

            byte[] buf = new byte[2048];
            int nbRead;

            os = new BufferedOutputStream(new FileOutputStream(currentFile));

            while ((nbRead = is.read(buf)) != -1) {
                os.write(buf, 0, nbRead);
            }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (jar != null) {
                try {
                    jar.close();
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
        }

        return currentFile;
    }

    /**
     * Extract Spout files to the Resources directory.
     */
    public static ArrayList<File> extractFiles() {
        ArrayList<File> files = new ArrayList<File>();

        // Setup directories
        new File(spoutDirectory).mkdir();
        new File(hudDirectory).mkdir();
        new File(hudStandardDirectory).mkdir();
        new File(hudRetroDirectory).mkdir();
        new File(soundDirectory).mkdir();

        // XP Bar images
        for (int x = 0; x < 255; x++) {
            String fileName;

            if (x < 10) {
                fileName = "xpbar_inc00" + x + ".png";
            }
            else if (x < 100) {
                fileName = "xpbar_inc0" + x + ".png";
            }
            else {
                fileName = "xpbar_inc" + x + ".png";
            }

            files.add(writeFile(fileName, hudStandardDirectory));
        }

        // Standard XP Icons
        for (SkillType skillType : SkillType.values()) {
            if (skillType.isChildSkill()) {
                continue;
            }

            String skillName = StringUtils.getCapitalized(skillType.toString());

            files.add(writeFile(skillName + ".png", hudStandardDirectory));
            files.add(writeFile(skillName + "_r.png", hudRetroDirectory));
        }

        // Blank icons
        files.add(writeFile("Icon.png", hudStandardDirectory));
        files.add(writeFile("Icon_r.png", hudRetroDirectory));

        // Sound FX
        files.add(writeFile("level.wav", soundDirectory));

        return files;
    }

    /**
     * Handle level-up notifications through Spout.
     *
     * @param skillType The skill that leveled up
     * @param spoutPlayer The player that leveled up
     */
    public static void levelUpNotification(SkillType skillType, SpoutPlayer spoutPlayer) {
        PlayerProfile profile = UserManager.getPlayer(spoutPlayer).getProfile();
        int skillLevel = profile.getSkillLevel(skillType);
        Material notificationItem;

        switch (skillType) {
            case ACROBATICS:
                notificationItem = getAcrobaticsNotificationItem(skillLevel);
                break;

            case ARCHERY:
                notificationItem = getArcheryNotificationItem(skillLevel);
                break;

            case AXES:
                notificationItem = getAxesNotificationItem(skillLevel);
                break;

            case EXCAVATION:
                notificationItem = getExcavationNotificationItem(skillLevel);
                break;

            case FISHING:
                notificationItem = getFishingNotificationItem(skillLevel);
                break;

            case HERBALISM:
                notificationItem = getHerbalismNotificationItem(skillLevel);
                break;

            case MINING:
                notificationItem = getMiningNotificationItem(skillLevel);
                break;

            case REPAIR:
                notificationItem = Material.ANVIL;
                break;

            case SWORDS:
                notificationItem = getSwordsNotificationItem(skillLevel);
                break;

            case TAMING:
                notificationItem = getTamingNotificationItem(skillLevel);
                break;

            case UNARMED:
                notificationItem = getUnarmedNotificationItem(skillLevel);
                break;

            case WOODCUTTING:
                notificationItem = getWoodcuttingNotificationItem(skillLevel);
                break;

            default:
                notificationItem = Material.MAP;
                break;
        }

        spoutPlayer.sendNotification(LocaleLoader.getString("Spout.LevelUp.1"), LocaleLoader.getString("Spout.LevelUp.2", SkillUtils.getSkillName(skillType), skillLevel), notificationItem);
        SpoutManager.getSoundManager().playCustomSoundEffect(mcMMO.p, spoutPlayer, "level.wav", false);
    }

    private static Material getAcrobaticsNotificationItem(int skillLevel) {
        for (Tier tier : Tier.values()) {
            if (skillLevel >= tier.getLevel()) {
                return tier.getAcrobaticsNotificationItem();
            }
        }

        return Material.LEATHER_BOOTS;
    }

    private static Material getArcheryNotificationItem(int skillLevel) {
        for (Tier tier : Tier.values()) {
            if (skillLevel >= tier.getLevel()) {
                return tier.getArcheryNotificationItem();
            }
        }

        return Material.FEATHER;
    }

    private static Material getAxesNotificationItem(int skillLevel) {
        for (Tier tier : Tier.values()) {
            if (skillLevel >= tier.getLevel()) {
                return tier.getAxesNotificationItem();
            }
        }

        return Material.WOOD_AXE;
    }

    private static Material getExcavationNotificationItem(int skillLevel) {
        for (Tier tier : Tier.values()) {
            if (skillLevel >= tier.getLevel()) {
                return tier.getExcavationNotificationItem();
            }
        }

        return Material.DIRT;
    }

    private static Material getFishingNotificationItem(int skillLevel) {
        for (Tier tier : Tier.values()) {
            if (skillLevel >= tier.getLevel()) {
                return tier.getFishingNotificationItem();
            }
        }

        return Material.RAW_FISH;
    }

    private static Material getHerbalismNotificationItem(int skillLevel) {
        for (Tier tier : Tier.values()) {
            if (skillLevel >= tier.getLevel()) {
                return tier.getHerbalismNotificationItem();
            }
        }

        return Material.VINE;
    }

    private static Material getMiningNotificationItem(int skillLevel) {
        for (Tier tier : Tier.values()) {
            if (skillLevel >= tier.getLevel()) {
                return tier.getMiningNotificationItem();
            }
        }

        return Material.COAL_ORE;
    }

    private static Material getSwordsNotificationItem(int skillLevel) {
        for (Tier tier : Tier.values()) {
            if (skillLevel >= tier.getLevel()) {
                return tier.getSwordsNotificationItem();
            }
        }

        return Material.WOOD_SWORD;
    }

    private static Material getTamingNotificationItem(int skillLevel) {
        for (Tier tier : Tier.values()) {
            if (skillLevel >= tier.getLevel()) {
                return tier.getTamingNotificationItem();
            }
        }

        return Material.PORK;
    }

    private static Material getUnarmedNotificationItem(int skillLevel) {
        for (Tier tier : Tier.values()) {
            if (skillLevel >= tier.getLevel()) {
                return tier.getUnarmedNotificationItem();
            }
        }

        return Material.LEATHER_HELMET;
    }

    private static Material getWoodcuttingNotificationItem(int skillLevel) {
        for (Tier tier : Tier.values()) {
            if (skillLevel >= tier.getLevel()) {
                return tier.getWoodcuttingNotificationItem();
            }
        }

        return Material.STICK;
    }

    /**
     * Re-enable SpoutCraft for players after a /reload
     */
    public static void reloadSpoutPlayers() {
        PluginManager pluginManager = mcMMO.p.getServer().getPluginManager();

        for (SpoutPlayer spoutPlayer : SpoutManager.getPlayerChunkMap().getOnlinePlayers()) {
            pluginManager.callEvent(new SpoutCraftEnableEvent(spoutPlayer));
        }
    }

    public static void reloadSpoutPlayer(Player player) {
        SpoutPlayer spoutPlayer = SpoutManager.getPlayer(player);

        if (spoutPlayer != null) {
            mcMMO.p.getServer().getPluginManager().callEvent(new SpoutCraftEnableEvent(spoutPlayer));
        }
    }

    public static void preCacheFiles() {
        SpoutManager.getFileManager().addToPreLoginCache(mcMMO.p, extractFiles());
    }

    public static void processLevelup(McMMOPlayer mcMMOPlayer, SkillType skillType, int levelsGained) {
        Player player = mcMMOPlayer.getPlayer();
        SpoutPlayer spoutPlayer = SpoutManager.getPlayer(player);

        if (spoutPlayer.isSpoutCraftEnabled()) {
            levelUpNotification(skillType, spoutPlayer);

            /* Update custom titles */
            if (SpoutConfig.getInstance().getShowPowerLevel()) {
                spoutPlayer.setTitle(LocaleLoader.getString("Spout.Title", spoutPlayer.getName(), mcMMOPlayer.getPowerLevel()));
            }
        }
        else {
            player.sendMessage(LocaleLoader.getString(StringUtils.getCapitalized(skillType.toString()) + ".Skillup", levelsGained, mcMMOPlayer.getProfile().getSkillLevel(skillType)));
        }
    }

    public static void processXpGain(Player player, PlayerProfile profile) {
        SpoutPlayer spoutPlayer = SpoutManager.getPlayer(player);

        if (spoutPlayer.isSpoutCraftEnabled() && SpoutConfig.getInstance().getXPBarEnabled()) {
            profile.getSpoutHud().updateXpBar();
        }
    }

    public static void sendRepairNotifications(Player player, int anvilId) {
        SpoutPlayer spoutPlayer = SpoutManager.getPlayer(player);

        if (spoutPlayer.isSpoutCraftEnabled()) {
            String[] spoutMessages = Repair.getSpoutAnvilMessages(anvilId);
            spoutPlayer.sendNotification(spoutMessages[0], spoutMessages[1], Material.getMaterial(anvilId));
        }
        else {
            player.sendMessage(Repair.getAnvilMessage(anvilId));
        }
    }

    public static void sendDonationNotification(Player player) {
        SpoutPlayer spoutPlayer = SpoutManager.getPlayer(player);

        if (spoutPlayer.isSpoutCraftEnabled()) {
            spoutPlayer.sendNotification(LocaleLoader.getString("Spout.Donate"), ChatColor.GREEN + "gjmcferrin@gmail.com", Material.DIAMOND);
        }
        else {
            player.sendMessage(LocaleLoader.getString("MOTD.Donate"));
            player.sendMessage(ChatColor.GOLD + " - " + ChatColor.GREEN + "gjmcferrin@gmail.com" + ChatColor.GOLD + " Paypal");
        }
    }
}
