package com.gmail.nossr50.database;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.database.PlayerStat;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.util.StringUtils;

public final class LeaderboardManager {
    private static HashMap<SkillType, List<PlayerStat>> playerStatHash = new HashMap<SkillType, List<PlayerStat>>();
    private static List<PlayerStat> powerLevels = new ArrayList<PlayerStat>();
    private static long lastUpdate = 0;

    private static final long UPDATE_WAIT_TIME = 600000L; // 10 minutes
    private static final long ONE_MONTH = 2630000000L;

    private LeaderboardManager() {}

    /**
     * Update the leader boards.
     */
    public static void updateLeaderboards() {
        // Only update FFS leaderboards every 10 minutes.. this puts a lot of strain on the server (depending on the size of the database) and should not be done frequently
        if (System.currentTimeMillis() < lastUpdate + UPDATE_WAIT_TIME) {
            return;
        }

        lastUpdate = System.currentTimeMillis(); // Log when the last update was run
        powerLevels.clear(); // Clear old values from the power levels

        // Initialize lists
        List<PlayerStat> mining      = new ArrayList<PlayerStat>();
        List<PlayerStat> woodcutting = new ArrayList<PlayerStat>();
        List<PlayerStat> herbalism   = new ArrayList<PlayerStat>();
        List<PlayerStat> excavation  = new ArrayList<PlayerStat>();
        List<PlayerStat> acrobatics  = new ArrayList<PlayerStat>();
        List<PlayerStat> repair      = new ArrayList<PlayerStat>();
        List<PlayerStat> swords      = new ArrayList<PlayerStat>();
        List<PlayerStat> axes        = new ArrayList<PlayerStat>();
        List<PlayerStat> archery     = new ArrayList<PlayerStat>();
        List<PlayerStat> unarmed     = new ArrayList<PlayerStat>();
        List<PlayerStat> taming      = new ArrayList<PlayerStat>();
        List<PlayerStat> fishing     = new ArrayList<PlayerStat>();

        // Read from the FlatFile database and fill our arrays with information
        try {
            FileReader file = new FileReader(mcMMO.getUsersFilePath());
            BufferedReader in = new BufferedReader(file);
            String line = "";
            ArrayList<String> players = new ArrayList<String>();

            while ((line = in.readLine()) != null) {
                String[] data = line.split(":");

                String playerName = data[0];
                int powerLevel = 0;

                // Prevent the same player from being added multiple times (I'd like to note that this shouldn't happen...)
                if (players.contains(playerName)) {
                    continue;
                }

                players.add(playerName);

                if (data.length > 1 && StringUtils.isInt(data[1])) {
                    mining.add(new PlayerStat(playerName, Integer.parseInt(data[1])));
                    powerLevel += Integer.parseInt(data[1]);
                }

                if (data.length > 5 && StringUtils.isInt(data[5])) {
                    woodcutting.add(new PlayerStat(playerName, Integer.parseInt(data[5])));
                    powerLevel += Integer.parseInt(data[5]);
                }

                if (data.length > 7 && StringUtils.isInt(data[7])) {
                    repair.add(new PlayerStat(playerName, Integer.parseInt(data[7])));
                    powerLevel += Integer.parseInt(data[7]);
                }

                if (data.length > 8 && StringUtils.isInt(data[8])) {
                    unarmed.add(new PlayerStat(playerName, Integer.parseInt(data[8])));
                    powerLevel += Integer.parseInt(data[8]);
                }

                if (data.length > 9 && StringUtils.isInt(data[9])) {
                    herbalism.add(new PlayerStat(playerName, Integer.parseInt(data[9])));
                    powerLevel += Integer.parseInt(data[9]);
                }

                if (data.length > 10 && StringUtils.isInt(data[10])) {
                    excavation.add(new PlayerStat(playerName, Integer.parseInt(data[10])));
                    powerLevel += Integer.parseInt(data[10]);
                }

                if (data.length > 11 && StringUtils.isInt(data[11])) {
                    archery.add(new PlayerStat(playerName, Integer.parseInt(data[11])));
                    powerLevel += Integer.parseInt(data[11]);
                }

                if (data.length > 12 && StringUtils.isInt(data[12])) {
                    swords.add(new PlayerStat(playerName, Integer.parseInt(data[12])));
                    powerLevel += Integer.parseInt(data[12]);
                }

                if (data.length > 13 && StringUtils.isInt(data[13])) {
                    axes.add(new PlayerStat(playerName, Integer.parseInt(data[13])));
                    powerLevel += Integer.parseInt(data[13]);
                }

                if (data.length > 14 && StringUtils.isInt(data[14])) {
                    acrobatics.add(new PlayerStat(playerName, Integer.parseInt(data[14])));
                    powerLevel += Integer.parseInt(data[14]);
                }

                if (data.length > 24 && StringUtils.isInt(data[24])) {
                    taming.add(new PlayerStat(playerName, Integer.parseInt(data[24])));
                    powerLevel += Integer.parseInt(data[24]);
                }

                if (data.length > 34 && StringUtils.isInt(data[34])) {
                    fishing.add(new PlayerStat(playerName, Integer.parseInt(data[34])));
                    powerLevel += Integer.parseInt(data[34]);
                }

                powerLevels.add(new PlayerStat(playerName, powerLevel));
            }
            in.close();
        }
        catch (Exception e) {
            mcMMO.p.getLogger().severe("Exception while reading " + mcMMO.getUsersFilePath() + " (Are you sure you formatted it correctly?)" + e.toString());
        }

        SkillComparator c = new SkillComparator();

        Collections.sort(mining, c);
        Collections.sort(woodcutting, c);
        Collections.sort(repair, c);
        Collections.sort(unarmed, c);
        Collections.sort(herbalism, c);
        Collections.sort(excavation, c);
        Collections.sort(archery, c);
        Collections.sort(swords, c);
        Collections.sort(axes, c);
        Collections.sort(acrobatics, c);
        Collections.sort(taming, c);
        Collections.sort(fishing, c);
        Collections.sort(powerLevels, c);

        playerStatHash.put(SkillType.MINING, mining);
        playerStatHash.put(SkillType.WOODCUTTING, woodcutting);
        playerStatHash.put(SkillType.REPAIR, repair);
        playerStatHash.put(SkillType.UNARMED, unarmed);
        playerStatHash.put(SkillType.HERBALISM, herbalism);
        playerStatHash.put(SkillType.EXCAVATION, excavation);
        playerStatHash.put(SkillType.ARCHERY, archery);
        playerStatHash.put(SkillType.SWORDS, swords);
        playerStatHash.put(SkillType.AXES, axes);
        playerStatHash.put(SkillType.ACROBATICS, acrobatics);
        playerStatHash.put(SkillType.TAMING, taming);
        playerStatHash.put(SkillType.FISHING, fishing);
    }

    /**
     * Retrieve leaderboard info.
     *
     * @param skillType Skill to retrieve info on.
     * @param pageNumber Which page in the leaderboards to retrieve
     * @return the requested leaderboard information
     */
    public static String[] retrieveInfo(String skillType, int pageNumber) {
        String[] info = new String[10];
        List<PlayerStat> statsList;

        if (skillType.equalsIgnoreCase("all")) {
            statsList = powerLevels;
        }
        else {
            statsList = playerStatHash.get(SkillType.getSkill(skillType));
        }

        int destination = (pageNumber - 1) * 10;

        for (int i = 0; i < 10; i++) {
            PlayerStat ps = statsList.get(destination + i);
            info[i] = ps.name + ":" + ps.statVal;
        }

        return info;
    }

    public static int[] getPlayerRank(String playerName) {
        return getPlayerRank(playerName, powerLevels);
    }

    public static int[] getPlayerRank(String playerName, SkillType skillType) {
        return getPlayerRank(playerName, playerStatHash.get(skillType));
    }

    public static boolean removeFlatFileUser(String playerName) {
        boolean worked = false;

        BufferedReader in = null;
        FileWriter out = null;
        String usersFilePath = mcMMO.getUsersFilePath();

        try {
            FileReader file = new FileReader(usersFilePath);
            in = new BufferedReader(file);
            StringBuilder writer = new StringBuilder();
            String line = "";

            while ((line = in.readLine()) != null) {

                // Write out the same file but when we get to the player we want to remove, we skip his line.
                if (!line.split(":")[0].equalsIgnoreCase(playerName)) {
                    writer.append(line).append("\r\n");
                }
                else {
                    mcMMO.p.getLogger().info("User found, removing...");
                    worked = true;
                    continue; // Skip the player
                }
            }

            out = new FileWriter(usersFilePath); // Write out the new file
            out.write(writer.toString());
        }
        catch (Exception e) {
            mcMMO.p.getLogger().severe("Exception while reading " + usersFilePath + " (Are you sure you formatted it correctly?)" + e.toString());
        }
        finally {
            if (in != null) {
                try {
                    in.close();
                }
                catch (IOException ex) {
                    ex.printStackTrace();
                }
            }

            if (out != null) {
                try {
                    out.close();
                }
                catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }

        return worked;
    }

    public static void purgePowerlessFlatfile() {
        mcMMO.p.getLogger().info("Purging powerless users...");

        int purgedUsers = 0;

        for (PlayerStat stat : powerLevels) {
            if (stat.statVal == 0 && !mcMMO.p.getServer().getOfflinePlayer(stat.name).isOnline() && removeFlatFileUser(stat.name)) {
                purgedUsers++;
            }
        }

        mcMMO.p.getLogger().info("Purged " + purgedUsers + " users from the database.");
    }

    public static void purgeOldFlatfile() {
        mcMMO.p.getLogger().info("Purging old users...");
        int purgedUsers = removeOldFlatfileUsers();
        mcMMO.p.getLogger().info("Purged " + purgedUsers + " users from the database.");
    }

    private static int removeOldFlatfileUsers() {
        int removedPlayers = 0;
        long currentTime = System.currentTimeMillis();
        long purgeTime = ONE_MONTH * Config.getInstance().getOldUsersCutoff();

        BufferedReader in = null;
        FileWriter out = null;
        String usersFilePath = mcMMO.getUsersFilePath();

        try {
            FileReader file = new FileReader(usersFilePath);
            in = new BufferedReader(file);
            StringBuilder writer = new StringBuilder();
            String line = "";

            while ((line = in.readLine()) != null) {

                // Write out the same file but when we get to the player we want to remove, we skip his line.
                String[] splitLine = line.split(":");

                if (splitLine.length > 37) {
                    if (currentTime - (StringUtils.getLong(line.split(":")[37]) * 1000) <= purgeTime) {
                        writer.append(line).append("\r\n");
                    }
                    else {
                        mcMMO.p.getLogger().info("User found, removing...");
                        removedPlayers++;
                        continue; // Skip the player
                    }
                }
                else {
                    writer.append(line).append("\r\n");
                }
            }

            out = new FileWriter(usersFilePath); // Write out the new file
            out.write(writer.toString());
        }
        catch (Exception e) {
            mcMMO.p.getLogger().severe("Exception while reading " + usersFilePath + " (Are you sure you formatted it correctly?)" + e.toString());
        }
        finally {
            if (in != null) {
                try {
                    in.close();
                }
                catch (IOException ex) {
                    ex.printStackTrace();
                }
            }

            if (out != null) {
                try {
                    out.close();
                }
                catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }

        return removedPlayers;
    }

    private static int[] getPlayerRank(String playerName, List<PlayerStat> statsList) {
        int currentPos = 1;

        if (statsList == null) {
            return new int[] {0, 0};
        }

        for (PlayerStat stat : statsList) {
            if (stat.name.equalsIgnoreCase(playerName)) {
                return new int[] {currentPos, stat.statVal};
            }

            currentPos++;
        }

        return new int[] {0, 0};
    }

    private static class SkillComparator implements Comparator<PlayerStat> {
        @Override
        public int compare(PlayerStat o1, PlayerStat o2) {
            return (o2.statVal - o1.statVal);
        }
    }
}
