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
    static String anniversaryFilePath = mcMMO.getFlatFileDirectory() + "anniversary";
    private static ArrayList<String> hasCelebrated;

    private HolidayManager() {}

    // This gets called onEnable
    public static void createAnniversaryFile() {
        File anniversaryFile = new File(anniversaryFilePath);

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
            BufferedReader reader = new BufferedReader(new FileReader(anniversaryFilePath));
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
            BufferedWriter writer = new BufferedWriter(new FileWriter(anniversaryFilePath));
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

        if (!getDateRange(day.getTime(), anniversaryStart.getTime(), anniversaryEnd.getTime())) {
            return;
        }

        sender.sendMessage(ChatColor.BLUE + "Happy 3 Year Anniversary!  In honor of all of");
        sender.sendMessage(ChatColor.BLUE + "nossr50's work and all the devs, here's a firework show!");
        if (sender instanceof Player) {
            final int firework_amount = 10;
            for (int i = 0; i < firework_amount; i++) {
                int delay = (int) (Misc.getRandom().nextDouble() * 3 * 20) + 4;
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
            String colorA = chatColorChoose();
            String colorB = chatColorChoose();
            String colorC = chatColorChoose();
            String colorD = chatColorChoose();
            String colorE = chatColorChoose();
            String colorF = chatColorChoose();
            String colorG = chatColorChoose();
            String colorH = chatColorChoose();
            String colorI = chatColorChoose();
            String colorJ = chatColorChoose();
            String colorK = chatColorChoose();
            Object[] colorParams = new Object[]{colorA, colorB, colorC, colorD, colorE, colorF, colorG, colorH, colorI, colorJ, colorK};
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

    private static boolean getDateRange(Date date, Date start, Date end) {
        return !(date.before(start) || date.after(end));
    }

    private static void spawnFireworks(Player player) {
        int power = (int) (Misc.getRandom().nextDouble() * 3) + 1;
        Type fireworkType = Type.values()[Misc.getRandom().nextInt(Type.values().length)];
        double varX = Misc.getRandom().nextGaussian() * 3;
        double varZ = Misc.getRandom().nextGaussian() * 3;

        Firework fireworks = (Firework) player.getWorld().spawnEntity(player.getLocation().add(varX, 0, varZ), EntityType.FIREWORK);
        FireworkMeta fireworkmeta = fireworks.getFireworkMeta();
        FireworkEffect effect = FireworkEffect.builder().flicker(Misc.getRandom().nextBoolean()).withColor(colorChoose()).withFade(colorChoose()).with(fireworkType).trail(Misc.getRandom().nextBoolean()).build();
        fireworkmeta.addEffect(effect);
        fireworkmeta.setPower(power);
        fireworks.setFireworkMeta(fireworkmeta);
    }

    private static List<Color> colorChoose() {
        Collections.shuffle(ALL_COLORS, Misc.getRandom());

        int numberOfColors = Misc.getRandom().nextInt(ALL_COLORS.size() + 1);

        if (numberOfColors == 0) {
            numberOfColors = 1;
        }

        List<Color> chosenColors = ALL_COLORS.subList(0, numberOfColors);

        return new ArrayList<Color>(chosenColors); // don't let caller modify ALL_COLORS
    }

    private static String chatColorChoose() {
        StringBuilder ret = new StringBuilder(ALL_CHAT_COLORS.get(Misc.getRandom().nextInt(ALL_CHAT_COLORS.size())).toString());

        for (ChatColor CHAT_FORMAT : CHAT_FORMATS) {
            if (Misc.getRandom().nextInt(4) == 0) {
                ret.append(CHAT_FORMAT);
            }
        }

        return ret.toString();
    }

    private static final List<Color> ALL_COLORS;
    private static final List<ChatColor> ALL_CHAT_COLORS;
    private static final ChatColor[] CHAT_FORMATS = new ChatColor[]{ChatColor.BOLD, ChatColor.ITALIC, ChatColor.UNDERLINE, ChatColor.STRIKETHROUGH};

    static {
        ALL_COLORS = Arrays.asList(
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
        ALL_CHAT_COLORS = new ArrayList<ChatColor>(16);
        for (ChatColor c : ChatColor.values()) {
            if (c.isColor()) {
                ALL_CHAT_COLORS.add(c);
            }
        }
        Collections.shuffle(ALL_CHAT_COLORS, Misc.getRandom());
    }
}
