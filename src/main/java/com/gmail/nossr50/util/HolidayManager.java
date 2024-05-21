//package com.gmail.nossr50.util;
//
//import com.gmail.nossr50.commands.skills.AprilCommand;
//import com.gmail.nossr50.config.Config;
//import com.gmail.nossr50.datatypes.interactions.NotificationType;
//import com.gmail.nossr50.datatypes.player.McMMOPlayer;
//import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
//import com.gmail.nossr50.locale.LocaleLoader;
//import com.gmail.nossr50.mcMMO;
//import com.gmail.nossr50.util.player.NotificationManager;
//import com.gmail.nossr50.util.player.UserManager;
//import com.gmail.nossr50.util.sounds.SoundManager;
//import com.gmail.nossr50.util.sounds.SoundType;
//import com.gmail.nossr50.util.text.StringUtils;
//import com.google.common.collect.ImmutableList;
//import org.bukkit.ChatColor;
//import org.bukkit.Color;
//import org.bukkit.Statistic;
//import org.bukkit.command.CommandSender;
//import org.bukkit.command.PluginCommand;
//import org.bukkit.entity.Player;
//import org.bukkit.event.player.PlayerStatisticIncrementEvent;
//
//import java.io.*;
//import java.util.*;
//import java.util.regex.Pattern;
//
//public final class HolidayManager {
//    private final ArrayList<String> hasCelebrated;
//    private final int currentYear;
//    private static final int START_YEAR = 2011;
//
//    private static final List<Color> ALL_COLORS;
//    private static final List<ChatColor> ALL_CHAT_COLORS;
//    private static final List<ChatColor> CHAT_FORMATS;
//
//    public enum FakeSkillType {
//        MACHO,
//        JUMPING,
//        THROWING,
//        WRECKING,
//        CRAFTING,
//        WALKING,
//        SWIMMING,
//        FALLING,
//        CLIMBING,
//        FLYING,
//        DIVING,
//        PIGGY,
//        UNKNOWN;
//
//        public static FakeSkillType getByName(String skillName) {
//            for (FakeSkillType type : values()) {
//                if (type.name().equalsIgnoreCase(skillName)) {
//                    return type;
//                }
//            }
//            return null;
//        }
//
//        public static FakeSkillType getByStatistic(Statistic statistic) {
//            switch (statistic) {
//                case DAMAGE_TAKEN:
//                    return FakeSkillType.MACHO;
//                case JUMP:
//                    return FakeSkillType.JUMPING;
//                case DROP:
//                    return FakeSkillType.THROWING;
//                case MINE_BLOCK:
//                case BREAK_ITEM:
//                    return FakeSkillType.WRECKING;
//                case CRAFT_ITEM:
//                    return FakeSkillType.CRAFTING;
//                case WALK_ONE_CM:
//                    return FakeSkillType.WALKING;
//                case SWIM_ONE_CM:
//                    return FakeSkillType.SWIMMING;
//                case FALL_ONE_CM:
//                    return FakeSkillType.FALLING;
//                case CLIMB_ONE_CM:
//                    return FakeSkillType.CLIMBING;
//                case FLY_ONE_CM:
//                    return FakeSkillType.FLYING;
//                case WALK_UNDER_WATER_ONE_CM:
//                    return FakeSkillType.DIVING;
//                case PIG_ONE_CM:
//                    return FakeSkillType.PIGGY;
//                default:
//                    return FakeSkillType.UNKNOWN;
//            }
//        }
//    }
//
//    public final Set<Statistic> movementStatistics = EnumSet.of(
//            Statistic.WALK_ONE_CM, Statistic.SWIM_ONE_CM, Statistic.FALL_ONE_CM,
//            Statistic.CLIMB_ONE_CM, Statistic.FLY_ONE_CM, Statistic.WALK_UNDER_WATER_ONE_CM,
//            Statistic.PIG_ONE_CM);
//
//    static {
//        List<Color> colors = new ArrayList<>();
//        List<ChatColor> chatColors = new ArrayList<>();
//        List<ChatColor> chatFormats = new ArrayList<>();
//
//        for (ChatColor color : ChatColor.values()) {
//            if (color.isColor()) {
//                chatColors.add(color);
//            }
//            else {
//                chatFormats.add(color);
//            }
//        }
//
////        for (DyeColor color : DyeColor.values()) {
////            colors.add(color.getFireworkColor());
////        }
//
//        Collections.shuffle(chatColors, Misc.getRandom());
//        Collections.shuffle(colors, Misc.getRandom());
//        Collections.shuffle(chatFormats, Misc.getRandom());
//
//        ALL_CHAT_COLORS = ImmutableList.copyOf(chatColors);
//        ALL_COLORS = ImmutableList.copyOf(colors);
//        CHAT_FORMATS = ImmutableList.copyOf(chatFormats);
//    }
//
//    // This gets called onEnable
//    public HolidayManager() {
//        currentYear = Calendar.getInstance().get(Calendar.YEAR);
//
//        File anniversaryFile = new File(mcMMO.getFlatFileDirectory(), "anniversary." + currentYear + ".yml");
//
//        if (!anniversaryFile.exists()) {
//            try {
//                anniversaryFile.createNewFile();
//            }
//            catch (IOException ex) {
//                mcMMO.p.getLogger().severe(ex.toString());
//            }
//        }
//
//        hasCelebrated = new ArrayList<>();
//
//        try {
//            hasCelebrated.clear();
//            BufferedReader reader = new BufferedReader(new FileReader(anniversaryFile.getPath()));
//            String line = reader.readLine();
//
//            while (line != null) {
//                hasCelebrated.add(line);
//                line = reader.readLine();
//            }
//
//            reader.close();
//        }
//        catch (Exception ex) {
//            mcMMO.p.getLogger().severe(ex.toString());
//        }
//
//        cleanupFiles();
//    }
//
//    private void cleanupFiles() {
//        File FlatFileDir = new File(mcMMO.getFlatFileDirectory());
//        File legacy = new File(FlatFileDir, "anniversary.yml");
//        List<File> toDelete = new ArrayList<>();
//
//        if (legacy.exists()) {
//            toDelete.add(legacy);
//        }
//
//        Pattern pattern = Pattern.compile("anniversary\\.(?:.+)\\.yml");
//
//        for (String fileName : FlatFileDir.list()) {
//            if (!pattern.matcher(fileName).matches() || fileName.equals("anniversary." + currentYear + ".yml")) {
//                continue;
//            }
//
//            File file = new File(FlatFileDir, fileName);
//
//            if (file.isDirectory()) {
//                continue;
//            }
//
//            toDelete.add(file);
//        }
//
//        for (File file : toDelete) {
//            if (file.delete()) {
//                LogUtils.debug(mcMMO.p.getLogger(), "Deleted: " + file.getName());
//            }
//        }
//    }
//
//    // This gets called onDisable
//    public void saveAnniversaryFiles() {
//        LogUtils.debug(mcMMO.p.getLogger(), "Saving anniversary files...");
//        String anniversaryFilePath = mcMMO.getFlatFileDirectory() + "anniversary." + currentYear + ".yml";
//
//        try {
//            BufferedWriter writer = new BufferedWriter(new FileWriter(anniversaryFilePath));
//            for (String player : hasCelebrated) {
//                writer.write(player);
//                writer.newLine();
//            }
//            writer.close();
//        }
//        catch (Exception ex) {
//            mcMMO.p.getLogger().severe(ex.toString());
//        }
//    }
//
//    // This gets called from /mcmmo command
//    public void anniversaryCheck(final CommandSender sender) {
//        GregorianCalendar anniversaryStart = new GregorianCalendar(currentYear, Calendar.FEBRUARY, 3);
//        GregorianCalendar anniversaryEnd = new GregorianCalendar(currentYear, Calendar.FEBRUARY, 6);
//        GregorianCalendar day = new GregorianCalendar();
//
//        if (hasCelebrated.contains(sender.getName())) {
//            return;
//        }
//
//        if (!getDateRange(day.getTime(), anniversaryStart.getTime(), anniversaryEnd.getTime())) {
//            return;
//        }
//
//        sender.sendMessage(LocaleLoader.getString("Holiday.Anniversary", (currentYear - START_YEAR)));
//        /*if (sender instanceof Player) {
//            final int firework_amount = 10;
//            for (int i = 0; i < firework_amount; i++) {
//                int delay = (int) (Misc.getRandom().nextDouble() * 3 * Misc.TICK_CONVERSION_FACTOR) + 4;
//                mcMMO.p.getServer().getScheduler().runTaskLater(mcMMO.p, new Runnable() {
//                    @Override
//                    public void run() {
//                        spawnFireworks((Player) sender);
//                    }
//                }, delay);
//            }
//        }*/
////        else {
//                /*
//                 * Credit: http://www.geocities.com/spunk1111/
//                 *  (good luck finding that in 3 years heh)
//                 *       .''.      .        *''*    :_\/_:     .
//                 *      :_\/_:   _\(/_  .:.*_\/_*   : /\ :  .'.:.'.
//                 *  .''.: /\ :    /)\   ':'* /\ *  : '..'.  -=:o:=-
//                 * :_\/_:'.:::.    ' *''*    * '.\'/.'_\(/_ '.':'.'
//                 * : /\ : :::::     *_\/_*     -= o =- /)\     '
//                 *  '..'  ':::'     * /\ *     .'/.\'.  '      *
//                 *      *            *..*         :           *
//                 *       *                        *          *
//                 *       *                        *          *
//                 */
//
//                /*
//                 * Color map
//                 *       AAAA      D        GGGG    JJJJJJ     K
//                 *      AAAAAA   DDDDD  EEEGGGGGG   JJJJJJ  KKKKKKK
//                 *  BBBBAAAAAA    DDD   EEEGGGGGG  I JJJJJ  KKKKKKK
//                 * BBBBBBACCCCC    D FFFF    G IIIIIIIHHHHH KKKKKKK
//                 * BBBBBB CCCCC     FFFFFF     IIIIIII HHH     K
//                 *  BBBB  CCCCC     FFFFFF     IIIIIII  H      k
//                 *      b            FFFF         I           k
//                 *       b                        i          k
//                 *       b                        i          k
//                 */
//            Object[] colorParams = new Object[]{chatColorChoose(), chatColorChoose(), chatColorChoose(), chatColorChoose(), chatColorChoose(), chatColorChoose(), chatColorChoose(), chatColorChoose(), chatColorChoose(), chatColorChoose(), chatColorChoose()};
//            sender.sendMessage(String.format("      %1$s.''.      %4$s.        %7$s*''*    %10$s:_\\/_:     %11$s.", colorParams));
//            sender.sendMessage(String.format("     %1$s:_\\/_:   %4$s_\\(/_  %5$s.:.%7$s*_\\/_*   %10$s: /\\ :  %11$s.'.:.'.", colorParams));
//            sender.sendMessage(String.format(" %2$s.''.%1$s: /\\ :    %4$s/)\\   %5$s':'%7$s* /\\ *  %9$s: %10$s'..'.  %11$s-=:o:=-", colorParams));
//            sender.sendMessage(String.format("%2$s:_\\/_:%1$s'%3$s.:::.    %4$s' %6$s*''*    %7$s* %9$s'.\\'/.'%8$s_\\(/_ %11$s'.':'.'", colorParams));
//            sender.sendMessage(String.format("%2$s: /\\ : %3$s:::::     %6$s*_\\/_*     %9$s-= o =-%8$s /)\\     %11$s'", colorParams));
//            sender.sendMessage(String.format(" %2$s'..'  %3$s':::'     %6$s* /\\ *     %9$s.'/.\\'.  %8$s'      %11$s*", colorParams));
//            sender.sendMessage(String.format("     %2$s*            %6$s*..*         %9$s:           %11$s*", colorParams));
//            sender.sendMessage(String.format("      %2$s*                        %9$s*          %11$s*", colorParams));
//            sender.sendMessage(String.format("      %2$s*                        %9$s*          %11$s*", colorParams));
////        }
//
//        hasCelebrated.add(sender.getName());
//    }
//
//    public boolean getDateRange(Date date, Date start, Date end) {
//        return !(date.before(start) || date.after(end));
//    }
//
////    public void spawnFireworks(Player player) {
////        int power = Misc.getRandom().nextInt(3) + 1;
////        Type fireworkType = Type.values()[Misc.getRandom().nextInt(Type.values().length)];
////        double varX = Misc.getRandom().nextGaussian() * 3;
////        double varZ = Misc.getRandom().nextGaussian() * 3;
////
////        Firework fireworks = (Firework) player.getWorld().spawnEntity(player.getLocation().add(varX, 0, varZ), EntityType.FIREWORK);
////        FireworkMeta fireworkmeta = fireworks.getFireworkMeta();
////        FireworkEffect effect = FireworkEffect.builder().flicker(Misc.getRandom().nextBoolean()).withColor(colorChoose()).withFade(colorChoose()).with(fireworkType).trail(Misc.getRandom().nextBoolean()).build();
////        fireworkmeta.addEffect(effect);
////        fireworkmeta.setPower(power);
////        fireworks.setFireworkMeta(fireworkmeta);
////    }
//
//    private static List<Color> colorChoose() {
//        return ALL_COLORS.subList(0, Math.max(Misc.getRandom().nextInt(ALL_COLORS.size() + 1), 1));
//    }
//
//    private static String chatColorChoose() {
//        StringBuilder ret = new StringBuilder(ALL_CHAT_COLORS.get(Misc.getRandom().nextInt(ALL_CHAT_COLORS.size())).toString());
//
//        for (ChatColor chatFormat : CHAT_FORMATS) {
//            if (Misc.getRandom().nextInt(CHAT_FORMATS.size()) == 0) {
//                ret.append(chatFormat);
//            }
//        }
//
//        return ret.toString();
//    }
//
//    public boolean isAprilFirst() {
//        if (!Config.getInstance().isAprilFoolsAllowed())
//            return false;
//
//        GregorianCalendar aprilFirst = new GregorianCalendar(currentYear, Calendar.APRIL, 1);
//        GregorianCalendar aprilSecond = new GregorianCalendar(currentYear, Calendar.APRIL, 2);
//        GregorianCalendar day = new GregorianCalendar();
//        return getDateRange(day.getTime(), aprilFirst.getTime(), aprilSecond.getTime());
//    }
//
//    public boolean nearingAprilFirst() {
//        if (!Config.getInstance().isAprilFoolsAllowed())
//            return false;
//
//        GregorianCalendar start = new GregorianCalendar(Calendar.getInstance().get(Calendar.YEAR), Calendar.MARCH, 28);
//        GregorianCalendar end = new GregorianCalendar(Calendar.getInstance().get(Calendar.YEAR), Calendar.APRIL, 2);
//        GregorianCalendar day = new GregorianCalendar();
//
//        return mcMMO.getHolidayManager().getDateRange(day.getTime(), start.getTime(), end.getTime());
//    }
//
//    public void handleStatisticEvent(PlayerStatisticIncrementEvent event) {
//        Player player = event.getPlayer();
//        Statistic statistic = event.getStatistic();
//        int newValue = event.getNewValue();
//
//        int modifier;
//        switch (statistic) {
//            case DAMAGE_TAKEN:
//                modifier = 500;
//                break;
//            case JUMP:
//                modifier = 500;
//                break;
//            case DROP:
//                modifier = 200;
//                break;
//            case MINE_BLOCK:
//            case BREAK_ITEM:
//                modifier = 500;
//                break;
//            case CRAFT_ITEM:
//                modifier = 100;
//                break;
//            default:
//                return;
//        }
//
//        if (newValue % modifier == 0) {
//            mcMMO.getHolidayManager().levelUpApril(player, FakeSkillType.getByStatistic(statistic));
//        }
//    }
//
//    public void levelUpApril(Player player, FakeSkillType fakeSkillType) {
//        if (!Config.getInstance().isAprilFoolsAllowed())
//            return;
//
//        final McMMOPlayer mmoPlayer = UserManager.getPlayer(player);
//        if (mmoPlayer == null) return;
//
//        int levelTotal = Misc.getRandom().nextInt(1 + mmoPlayer.getSkillLevel(PrimarySkillType.MINING)) + 1;
//        SoundManager.sendSound(player, player.getLocation(), SoundType.LEVEL_UP);
//        NotificationManager.sendPlayerInformation(player, NotificationType.HOLIDAY, "Holiday.AprilFools.Levelup", StringUtils.getCapitalized(fakeSkillType.toString()), String.valueOf(levelTotal));
////        ParticleEffectUtils.fireworkParticleShower(player, ALL_COLORS.get(Misc.getRandom().nextInt(ALL_COLORS.size())));
//    }
//
//    public void registerAprilCommand() {
//        if (!Config.getInstance().isAprilFoolsAllowed())
//            return;
//
//        PluginCommand command = mcMMO.p.getCommand("crafting");
//        command.setExecutor(new AprilCommand());
//    }
//}
