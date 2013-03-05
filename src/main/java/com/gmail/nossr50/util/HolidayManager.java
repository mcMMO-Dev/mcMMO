package com.gmail.nossr50.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.bukkit.Bukkit;
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
                    int delay = (int) (Math.random() * 3) + 4;
                    Bukkit.getScheduler().scheduleSyncDelayedTask(mcMMO.p, new Runnable() {
                        @Override
                        public void run() {
                            spawnFireworks((Player) sender);
                        }
                    }, 20 * delay);
                }
                hasCelebrated.add(sender.getName());
            }
        }
    }

    private static boolean getDateRange(Date date, Date start, Date end) {
        return !(date.before(start) || date.after(end));
    }

    private static void spawnFireworks(Player player) {
        int power = (int) (Math.random() * 3) + 1;
        int type = (int) (Math.random() * 5) + 1;

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

        Firework fireworks = (Firework) player.getWorld().spawnEntity(player.getLocation(), EntityType.FIREWORK);
        FireworkMeta fireworkmeta = fireworks.getFireworkMeta();
        FireworkEffect effect = FireworkEffect.builder().flicker(Misc.getRandom().nextBoolean()).withColor(colorchoose()).withFade(colorchoose()).with(typen).trail(Misc.getRandom().nextBoolean()).build();
        fireworkmeta.addEffect(effect);
        fireworkmeta.setPower(power);
        fireworks.setFireworkMeta(fireworkmeta);
    }

    private static List<Color> colorchoose() {
        // Thanks Zomis and Tejpbit for the help with this function!

        int numberofcolors = Misc.getRandom().nextInt(17) + 1;

        List<Color> allcolors = new ArrayList<Color>();
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

        List<Color> choosencolors = new ArrayList<Color>();

        for (int i = 0; i < numberofcolors; i++) {
            choosencolors.add(allcolors.remove(Misc.getRandom().nextInt(allcolors.size())));
        }
        return choosencolors;
    }
}
