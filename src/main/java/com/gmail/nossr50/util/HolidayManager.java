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
import java.util.regex.Pattern;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;

import com.gmail.nossr50.mcMMO;

import com.google.common.collect.ImmutableList;

public final class HolidayManager {
    private ArrayList<String> hasCelebrated;
    private int currentYear;
    private final int startYear = 2011;

    private static final List<Color> ALL_COLORS;
    private static final List<ChatColor> ALL_CHAT_COLORS;
    private static final List<ChatColor> CHAT_FORMATS;

    static {
        List<Color> colors = new ArrayList<Color>();
        List<ChatColor> chatColors = new ArrayList<ChatColor>();
        List<ChatColor> chatFormats = new ArrayList<ChatColor>();

        for (ChatColor color : ChatColor.values()) {
            if (color.isColor()) {
                chatColors.add(color);
            }
            else {
                chatFormats.add(color);
            }
        }

        for (DyeColor color : DyeColor.values()) {
            colors.add(color.getFireworkColor());
        }

        Collections.shuffle(chatColors, Misc.getRandom());
        Collections.shuffle(colors, Misc.getRandom());
        Collections.shuffle(chatFormats, Misc.getRandom());

        ALL_CHAT_COLORS = ImmutableList.copyOf(chatColors);
        ALL_COLORS = ImmutableList.copyOf(colors);
        CHAT_FORMATS = ImmutableList.copyOf(chatFormats);
    }

    // This gets called onEnable
    public HolidayManager() {
        currentYear = Calendar.getInstance().get(Calendar.YEAR);

        File anniversaryFile = new File(mcMMO.getFlatFileDirectory(), "anniversary." + currentYear + ".yml");

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
            BufferedReader reader = new BufferedReader(new FileReader(anniversaryFile.getPath()));
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

        cleanupFiles();
    }

    private void cleanupFiles() {
        File FlatFileDir = new File(mcMMO.getFlatFileDirectory());
        File legacy = new File(FlatFileDir, "anniversary.yml");
        List<File> toDelete = new ArrayList<File>();

        if (legacy.exists()) {
            toDelete.add(legacy);
        }

        Pattern pattern = Pattern.compile("anniversary\\.(?:.+)\\.yml");

        for (String fileName : FlatFileDir.list()) {
            if (!pattern.matcher(fileName).matches() || fileName.equals("anniversary." + currentYear + ".yml")) {
                continue;
            }

            File file = new File(FlatFileDir, fileName);

            if (file.isDirectory()) {
                continue;
            }

            toDelete.add(file);
        }

        for (File file : toDelete) {
            if (file.delete()) {
                mcMMO.p.debug("Deleted: " + file.getName());
            }
        }
    }

    // This gets called onDisable
    public void saveAnniversaryFiles() {
        mcMMO.p.debug("Saving anniversary files...");
        String anniversaryFilePath = mcMMO.getFlatFileDirectory() + "anniversary." + currentYear + ".yml";

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
    public void anniversaryCheck(final CommandSender sender) {
        GregorianCalendar anniversaryStart = new GregorianCalendar(currentYear, Calendar.FEBRUARY, 3);
        GregorianCalendar anniversaryEnd = new GregorianCalendar(currentYear, Calendar.FEBRUARY, 6);
        GregorianCalendar day = new GregorianCalendar();

        if (hasCelebrated.contains(sender.getName())) {
            return;
        }

        if (!getDateRange(day.getTime(), anniversaryStart.getTime(), anniversaryEnd.getTime())) {
            return;
        }

        sender.sendMessage(ChatColor.BLUE + "Happy " + (currentYear - startYear) + " Year Anniversary!  In honor of all of");
        sender.sendMessage(ChatColor.BLUE + "nossr50's work and all the devs, here's a firework show!");
        if (sender instanceof Player) {
            final int firework_amount = 10;
            for (int i = 0; i < firework_amount; i++) {
                int delay = (int) (Misc.getRandom().nextDouble() * 3 * Misc.TICK_CONVERSION_FACTOR) + 4;
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
            Object[] colorParams = new Object[]{chatColorChoose(), chatColorChoose(), chatColorChoose(), chatColorChoose(), chatColorChoose(), chatColorChoose(), chatColorChoose(), chatColorChoose(), chatColorChoose(), chatColorChoose(), chatColorChoose()};
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

    private boolean getDateRange(Date date, Date start, Date end) {
        return !(date.before(start) || date.after(end));
    }

    private void spawnFireworks(Player player) {
        int power = Misc.getRandom().nextInt(3) + 1;
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
        return ALL_COLORS.subList(0, Math.max(Misc.getRandom().nextInt(ALL_COLORS.size() + 1), 1));
    }

    private static String chatColorChoose() {
        StringBuilder ret = new StringBuilder(ALL_CHAT_COLORS.get(Misc.getRandom().nextInt(ALL_CHAT_COLORS.size())).toString());

        for (ChatColor chatFormat : CHAT_FORMATS) {
            if (Misc.getRandom().nextInt(CHAT_FORMATS.size()) == 0) {
                ret.append(chatFormat);
            }
        }

        return ret.toString();
    }
}
