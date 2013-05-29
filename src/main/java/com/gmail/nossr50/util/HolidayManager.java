package com.gmail.nossr50.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;

import com.gmail.nossr50.mcMMO;

public final class HolidayManager {
    public static ArrayList<String> hasCelebrated;
    private static final Random rand = new Random();

    private HolidayManager() {}

    // This gets called onEnable
    public static void createAnniversaryFile() {
        File anniversaryFile = new File(mcMMO.p.getDataFolder().getAbsolutePath() + File.separator + "anniversary");

        if (!anniversaryFile.exists()) {
            try {
                anniversaryFile.createNewFile();
            }
            catch (IOException ex) {
                mcMMO.p.getLogger().severe(ex.toString());
            }
        }

        hasCelebrated = new ArrayList<String>();

        try {
            hasCelebrated.clear();
            BufferedReader reader = new BufferedReader(new FileReader(mcMMO.p.getDataFolder().getAbsolutePath() + File.separator + "anniversary"));
            String line = reader.readLine();

            while (line != null) {
                hasCelebrated.add(line);
                line = reader.readLine();
            }

            reader.close();
        }
        catch (Exception ex) {
            mcMMO.p.getLogger().severe(ex.toString());
        }
    }

    // This gets called onDisable
    public static void saveAnniversaryFiles() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(mcMMO.p.getDataFolder().getAbsolutePath() + File.separator + "anniversary"));
            for (String player : hasCelebrated) {
                writer.write(player);
                writer.newLine();
            }
            writer.close();
        }
        catch (Exception ex) {
            mcMMO.p.getLogger().severe(ex.toString());
        }
    }

    // This gets called from /mcmmo command
    public static void anniversaryCheck(final CommandSender sender) {
        if (sender instanceof Player) {
            GregorianCalendar anniversaryStart = new GregorianCalendar(2013, Calendar.FEBRUARY, 3);
            GregorianCalendar anniversaryEnd = new GregorianCalendar(2013, Calendar.FEBRUARY, 6);
            GregorianCalendar day = new GregorianCalendar();

            if (hasCelebrated == null) {
                createAnniversaryFile();
            }

            if (hasCelebrated.contains(sender.getName())) {
                return;
            }

            if (getDateRange(day.getTime(), anniversaryStart.getTime(), anniversaryEnd.getTime())) {
                sender.sendMessage(ChatColor.BLUE + "Happy 2 Year Anniversary!  In honor of all of");
                sender.sendMessage(ChatColor.BLUE + "nossr50's work and all the devs, here's a firework show!");
                final int firework_amount = 10;
                for (int i = 0; i < firework_amount; i++) {
                    int delay = (int) (rand.nextDouble() * 3 * 20) + 4;
                    mcMMO.p.getServer().getScheduler().runTaskLater(mcMMO.p, new Runnable() {
                        @Override
                        public void run() {
                            spawnFireworks((Player) sender);
                        }
                    }, delay);
                }
                hasCelebrated.add(sender.getName());
            }
        } else {
            sender.sendMessage(ChatColor.BLUE + "Happy 2 Year Anniversary!  In honor of all of");
            sender.sendMessage(ChatColor.BLUE + "nossr50's work and all the devs, here's a firework show!");
            /*
             * Credit: http://www.geocities.com/spunk1111/
             *  (good luck finding that in 3 years heh)
             *       .''.      .        *''*    :_\/_:     .
             *      :_\/_:   _\(/_  .:.*_\/_*   : /\ :  .'.:.'.
             *  .''.: /\ :    /)\   ':'* /\ *  : '..'.  -=:o:=-
             * :_\/_:'.:::.    ' *''*    * '.\'/.'_\(/_ '.':'.'
             * : /\ : :::::     *_\/_*     -= o =- /)\     '
             *  '..'  ':::'     * /\ *     .'/.\'.  '      *
             *      *            *..*         :           *
             *       *                        *          *
             *       *                        *          *
             */

            /*
             * Color map
             *       AAAA      D        GGGG    JJJJJJ     K
             *      AAAAAA   DDDDD  EEEGGGGGG   JJJJJJ  KKKKKKK
             *  BBBBAAAAAA    DDD   EEEGGGGGG  I JJJJJ  KKKKKKK
             * BBBBBBACCCCC    D FFFF    G IIIIIIIHHHHH KKKKKKK
             * BBBBBB CCCCC     FFFFFF     IIIIIII HHH     K
             *  BBBB  CCCCC     FFFFFF     IIIIIII  H      k
             *      b            FFFF         I           k
             *       b                        i          k
             *       b                        i          k
             */
            ChatColor colorA = chatcolorchoose();
            ChatColor colorB = chatcolorchoose();
            ChatColor colorC = chatcolorchoose();
            ChatColor colorD = chatcolorchoose();
            ChatColor colorE = chatcolorchoose();
            ChatColor colorF = chatcolorchoose();
            ChatColor colorG = chatcolorchoose();
            ChatColor colorH = chatcolorchoose();
            ChatColor colorI = chatcolorchoose();
            ChatColor colorJ = chatcolorchoose();
            ChatColor colorK = chatcolorchoose();
            sender.sendMessage(String.format("      %1$s.''.      %4$s.        %7$s*''*    %10$s:_\/_:     %11$s.", colorA, colorB, colorC, colorD, colorE, colorF, colorG, colorH, colorI, colorJ, colorK));
            sender.sendMessage(String.format("     %1$s:_\/_:   %4$s_\(/_  %5$s.:.%7$s*_\/_*   %10$s: /\ :  %11$s.'.:.'.")
        }
    }

    private static boolean getDateRange(Date date, Date start, Date end) {
        return !(date.before(start) || date.after(end));
    }

    private static void spawnFireworks(Player player) {
        int power = (int) (rand.nextDouble() * 3) + 1;
        int type = (int) (rand.nextDouble() * 5) + 1;
        double varX = rand.nextGaussian() * 3;
        double varZ = rand.nextGaussian() * 3;

        Type typen;
        switch (type) {
            case 2:
                typen = Type.BALL_LARGE;
                break;

            case 3:
                typen = Type.BURST;
                break;

            case 4:
                typen = Type.CREEPER;
                break;

            case 5:
                typen = Type.STAR;
                break;

            default:
                typen = Type.BALL;
        }

        Firework fireworks = (Firework) player.getWorld().spawnEntity(player.getLocation().add(varX, 0, varZ), EntityType.FIREWORK);
        FireworkMeta fireworkmeta = fireworks.getFireworkMeta();
        FireworkEffect effect = FireworkEffect.builder().flicker(rand.nextBoolean()).withColor(colorchoose()).withFade(colorchoose()).with(typen).trail(rand.nextBoolean()).build();
        fireworkmeta.addEffect(effect);
        fireworkmeta.setPower(power);
        fireworks.setFireworkMeta(fireworkmeta);
    }

    private static List<Color> colorchoose() {
        // Thanks Zomis and Tejpbit for the help with this function!
        List<Color> acolors = Collections.shuffle(allcolors);

        int numberofcolors = rand.nextInt(acolors.size());
        List<Color> choosencolors = acolors.subList(0, numberofcolors);

        return choosencolors.clone(); // don't let caller modify allcolors
    }
    
    private static Color chatcolorchoose() {
        return allchatcolors.get(rand.nextInt(allchatcolors.size());
    }

    private static List<Color> allcolors; // do not modify
    private static List<ChatColor> allchatcolors;

    static {
        allcolors = new ArrayList<Color>();
        allcolors.add(Color.AQUA);
        allcolors.add(Color.BLACK);
        allcolors.add(Color.BLUE);
        allcolors.add(Color.FUCHSIA);
        allcolors.add(Color.GRAY);
        allcolors.add(Color.GREEN);
        allcolors.add(Color.LIME);
        allcolors.add(Color.MAROON);
        allcolors.add(Color.NAVY);
        allcolors.add(Color.OLIVE);
        allcolors.add(Color.ORANGE);
        allcolors.add(Color.PURPLE);
        allcolors.add(Color.RED);
        allcolors.add(Color.SILVER);
        allcolors.add(Color.TEAL);
        allcolors.add(Color.WHITE);
        allcolors.add(Color.YELLOW);
        allchatcolors = new ArrayList<ChatColor>();
        allchatcolors(ChatColor.AQUA);
        allchatcolors(ChatColor.BLACK);
        allchatcolors(ChatColor.BLUE);
        allchatcolors(ChatColor.DARK_AQUA);
        allchatcolors(ChatColor.DARK_BLUE);
        allchatcolors(ChatColor.DARK_GRAY);
        allchatcolors(ChatColor.DARK_GREEN);
        allchatcolors(ChatColor.DARK_PURPLE);
        allchatcolors(ChatColor.DARK_RED);
        allchatcolors(ChatColor.GOLD);
        allchatcolors(ChatColor.GRAY);
        allchatcolors(ChatColor.GREEN);
        allchatcolors(ChatColor.LIGHT_PURPLE);
        allchatcolors(ChatColor.RED);
        allchatcolors(ChatColor.WHITE);
        allchatcolors(ChatColor.YELLOW);
    }
}
