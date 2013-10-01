package com.gmail.nossr50.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
        GregorianCalendar anniversaryStart = new GregorianCalendar(2014, Calendar.FEBRUARY, 3);
        GregorianCalendar anniversaryEnd = new GregorianCalendar(2014, Calendar.FEBRUARY, 6);
        GregorianCalendar day = new GregorianCalendar();

        if (hasCelebrated == null) {
            createAnniversaryFile();
        }

        if (hasCelebrated.contains(sender.getName())) {
            return;
        }
        if (getDateRange(day.getTime(), anniversaryStart.getTime(), anniversaryEnd.getTime())) {
            sender.sendMessage(ChatColor.BLUE + "Happy 3 Year Anniversary!  In honor of all of");
            sender.sendMessage(ChatColor.BLUE + "nossr50's work and all the devs, here's a firework show!");
            if (sender instanceof Player) {
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
            }
            else {
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
                String colorA = chatcolorchoose();
                String colorB = chatcolorchoose();
                String colorC = chatcolorchoose();
                String colorD = chatcolorchoose();
                String colorE = chatcolorchoose();
                String colorF = chatcolorchoose();
                String colorG = chatcolorchoose();
                String colorH = chatcolorchoose();
                String colorI = chatcolorchoose();
                String colorJ = chatcolorchoose();
                String colorK = chatcolorchoose();
                Object[] colorParams = new Object[] { colorA, colorB, colorC, colorD, colorE, colorF, colorG, colorH, colorI, colorJ, colorK };
                sender.sendMessage(String.format("      %1$s.''.      %4$s.        %7$s*''*    %10$s:_\\/_:     %11$s.", colorParams));
                sender.sendMessage(String.format("     %1$s:_\\/_:   %4$s_\\(/_  %5$s.:.%7$s*_\\/_*   %10$s: /\\ :  %11$s.'.:.'.", colorParams));
                sender.sendMessage(String.format(" %2$s.''.%1$s: /\\ :    %4$s/)\\   %5$s':'%7$s* /\\ *  %9$s: %10$s'..'.  %11$s-=:o:=-", colorParams));
                sender.sendMessage(String.format("%2$s:_\\/_:%1$s'%3$s.:::.    %4$s' %6$s*''*    %7$s* %9$s'.\\'/.'%8$s_\\(/_ %11$s'.':'.'", colorParams));
                sender.sendMessage(String.format("%2$s: /\\ : %3$s:::::     %6$s*_\\/_*     %9$s-= o =-%8$s /)\\     %11$s'", colorParams));
                sender.sendMessage(String.format(" %2$s'..'  %3$s':::'     %6$s* /\\ *     %9$s.'/.\\'.  %8$s'      %11$s*", colorParams));
                sender.sendMessage(String.format("     %2$s*            %6$s*..*         %9$s:           %11$s*", colorParams));
                sender.sendMessage(String.format("      %2$s*                        %9$s*          %11$s*", colorParams));
                sender.sendMessage(String.format("      %2$s*                        %9$s*          %11$s*", colorParams));
            }
            hasCelebrated.add(sender.getName());
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
        Collections.shuffle(allcolors, rand);

        int numberofcolors = rand.nextInt(allcolors.size());
        List<Color> choosencolors = allcolors.subList(0, numberofcolors);

        return new ArrayList<Color>(choosencolors); // don't let caller modify allcolors
    }

    private static String chatcolorchoose() {
        StringBuilder ret = new StringBuilder(allchatcolors.get(rand.nextInt(allchatcolors.size())).toString());
        for (int i = 0; i < chatFormats.length; i++) {
            if (rand.nextInt(4) == 0) {
                ret.append(chatFormats[i]);
            }
        }
        return ret.toString();
    }

    private static final List<Color> allcolors;
    private static final List<ChatColor> allchatcolors;
    private static final ChatColor[] chatFormats = new ChatColor[] { ChatColor.BOLD, ChatColor.ITALIC, ChatColor.UNDERLINE, ChatColor.STRIKETHROUGH };

    static {
        allcolors = Arrays.asList(
            Color.AQUA,
            Color.BLACK,
            Color.BLUE,
            Color.FUCHSIA,
            Color.GRAY,
            Color.GREEN,
            Color.LIME,
            Color.MAROON,
            Color.NAVY,
            Color.OLIVE,
            Color.ORANGE,
            Color.PURPLE,
            Color.RED,
            Color.SILVER,
            Color.TEAL,
            Color.WHITE,
            Color.YELLOW
        );
        allchatcolors = new ArrayList<ChatColor>(16);
        for (ChatColor c : ChatColor.values()) {
            if (c.isColor()) {
                allchatcolors.add(c);
            }
        }
        Collections.shuffle(allchatcolors, rand);
    }
}
