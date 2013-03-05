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
                String[] character = line.split(":");

                String p = character[0];
                int powerLevel = 0;

                // Prevent the same player from being added multiple times (I'd like to note that this shouldn't happen...)
                if (players.contains(p)) {
                    continue;
                }

                players.add(p);

                if (character.length > 1 && StringUtils.isInt(character[1])) {
                    mining.add(new PlayerStat(p, Integer.parseInt(character[1])));
                    powerLevel += Integer.parseInt(character[1]);
                }

                if (character.length > 5 && StringUtils.isInt(character[5])) {
                    woodcutting.add(new PlayerStat(p, Integer.parseInt(character[5])));
                    powerLevel += Integer.parseInt(character[5]);
                }

                if (character.length > 7 && StringUtils.isInt(character[7])) {
                    repair.add(new PlayerStat(p, Integer.parseInt(character[7])));
                    powerLevel += Integer.parseInt(character[7]);
                }

                if (character.length > 8 && StringUtils.isInt(character[8])) {
                    unarmed.add(new PlayerStat(p, Integer.parseInt(character[8])));
                    powerLevel += Integer.parseInt(character[8]);
                }

                if (character.length > 9 && StringUtils.isInt(character[9])) {
                    herbalism.add(new PlayerStat(p, Integer.parseInt(character[9])));
                    powerLevel += Integer.parseInt(character[9]);
                }

                if (character.length > 10 && StringUtils.isInt(character[10])) {
                    excavation.add(new PlayerStat(p, Integer.parseInt(character[10])));
                    powerLevel += Integer.parseInt(character[10]);
                }

                if (character.length > 11 && StringUtils.isInt(character[11])) {
                    archery.add(new PlayerStat(p, Integer.parseInt(character[11])));
                    powerLevel += Integer.parseInt(character[11]);
                }

                if (character.length > 12 && StringUtils.isInt(character[12])) {
                    swords.add(new PlayerStat(p, Integer.parseInt(character[12])));
                    powerLevel += Integer.parseInt(character[12]);
                }

                if (character.length > 13 && StringUtils.isInt(character[13])) {
                    axes.add(new PlayerStat(p, Integer.parseInt(character[13])));
                    powerLevel += Integer.parseInt(character[13]);
                }

                if (character.length > 14 && StringUtils.isInt(character[14])) {
                    acrobatics.add(new PlayerStat(p, Integer.parseInt(character[14])));
                    powerLevel += Integer.parseInt(character[14]);
                }

                if (character.length > 24 && StringUtils.isInt(character[24])) {
                    taming.add(new PlayerStat(p, Integer.parseInt(character[24])));
                    powerLevel += Integer.parseInt(character[24]);
                }

                if (character.length > 34 && StringUtils.isInt(character[34])) {
                    fishing.add(new PlayerStat(p, Integer.parseInt(character[34])));
                    powerLevel += Integer.parseInt(character[34]);
                }

                powerLevels.add(new PlayerStat(p, powerLevel));
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

        int destination;

        // How many lines to skip through
        if (pageNumber == 1) {
            destination = 0;
        }
        else {
            destination = (pageNumber * 10) - 9;
        }

        int currentPos = 0;

        for (PlayerStat ps : statsList) {
            if (currentPos == 10) {
                break;
            }

            if (destination > 1) {
                destination--;
                continue;
            }

            info[currentPos] = ps.name + ":" + ps.statVal;
            currentPos++;
        }

        return info;
    }

    public static int[] getPlayerRank(String playerName) {
        int currentPos = 1;

        if (powerLevels != null) {
            for (PlayerStat stat : powerLevels) {
                if (stat.name.equalsIgnoreCase(playerName)) {
                    return new int[] {currentPos, stat.statVal};
                }

                currentPos++;
                continue;
            }

            return new int[] {0, 0};
        }

        return new int[] {0, 0};
    }

    public static int[] getPlayerRank(String playerName, SkillType skillType) {
        int currentPos = 1;
        List<PlayerStat> statsList = playerStatHash.get(skillType);

        if (statsList != null) {
            for (PlayerStat stat : statsList) {
                if (stat.name.equalsIgnoreCase(playerName)) {
                    return new int[] {currentPos, stat.statVal};
                }

                currentPos++;
                continue;
            }

            return new int[] {0, 0};
        }

        return new int[] {0, 0};
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
            if (stat.statVal == 0 && removeFlatFileUser(stat.name) && !mcMMO.p.getServer().getOfflinePlayer(stat.name).isOnline()) {
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

    private static class SkillComparator implements Comparator<PlayerStat> {
        @Override
        public int compare(PlayerStat o1, PlayerStat o2) {
            return (o2.statVal - o1.statVal);
        }
    }
}
