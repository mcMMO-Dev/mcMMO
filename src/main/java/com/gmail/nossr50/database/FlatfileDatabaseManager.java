package com.gmail.nossr50.database;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.MobHealthbarType;
import com.gmail.nossr50.datatypes.database.PlayerStat;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.datatypes.skills.AbilityType;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.datatypes.spout.huds.HudType;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.player.UserManager;

public final class FlatfileDatabaseManager implements DatabaseManager {
    private final HashMap<SkillType, List<PlayerStat>> playerStatHash = new HashMap<SkillType, List<PlayerStat>>();
    private final List<PlayerStat> powerLevels = new ArrayList<PlayerStat>();
    private long lastUpdate = 0;

    private final long UPDATE_WAIT_TIME = 600000L; // 10 minutes
    private final File usersFile;

    protected FlatfileDatabaseManager() {
        usersFile = new File(mcMMO.getUsersFilePath());
        createDatabase();
        updateLeaderboards();
    }

    public void purgePowerlessUsers() {
        int purgedUsers = 0;

        mcMMO.p.getLogger().info("Purging powerless users...");

        for (PlayerStat stat : powerLevels) {
            if (stat.statVal == 0 && mcMMO.p.getServer().getPlayerExact(stat.name) == null && removeUser(stat.name)) {
                purgedUsers++;
            }
        }

        mcMMO.p.getLogger().info("Purged " + purgedUsers + " users from the database.");
    }

    public void purgeOldUsers() {
        int removedPlayers = 0;
        long currentTime = System.currentTimeMillis();

        mcMMO.p.getLogger().info("Purging old users...");

        for (McMMOPlayer mcMMOPlayer : UserManager.getPlayers().values()) {
            Player player = mcMMOPlayer.getPlayer();

            if (currentTime - player.getLastPlayed() > PURGE_TIME && removeUser(player.getName())) {
                removedPlayers++;
            }
        }

        mcMMO.p.getLogger().info("Purged " + removedPlayers + " users from the database.");
    }

    public boolean removeUser(String playerName) {
        boolean worked = false;

        BufferedReader in = null;
        FileWriter out = null;
        String usersFilePath = mcMMO.getUsersFilePath();

        try {
            in = new BufferedReader(new FileReader(usersFilePath));
            StringBuilder writer = new StringBuilder();
            String line = "";

            while ((line = in.readLine()) != null) {
                // Write out the same file but when we get to the player we want to remove, we skip his line.
                if (!worked && line.split(":")[0].equalsIgnoreCase(playerName)) {
                    mcMMO.p.getLogger().info("User found, removing...");
                    worked = true;
                    continue; // Skip the player
                }

                writer.append(line).append("\r\n");
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

        Misc.profileCleanup(playerName);

        return worked;
    }

    public void saveUser(PlayerProfile profile) {
        String playerName = profile.getPlayerName();

        BufferedReader in = null;
        FileWriter out = null;
        String usersFilePath = mcMMO.getUsersFilePath();

        try {
            // Open the file
            in = new BufferedReader(new FileReader(usersFilePath));
            StringBuilder writer = new StringBuilder();
            String line;

            // While not at the end of the file
            while ((line = in.readLine()) != null) {
                // Read the line in and copy it to the output it's not the player we want to edit
                if (!line.split(":")[0].equalsIgnoreCase(playerName)) {
                    writer.append(line).append("\r\n");
                }
                else {
                    // Otherwise write the new player information
                    writer.append(playerName).append(":");
                    writer.append(profile.getSkillLevel(SkillType.MINING)).append(":");
                    writer.append(":");
                    writer.append(":");
                    writer.append(profile.getSkillXpLevel(SkillType.MINING)).append(":");
                    writer.append(profile.getSkillLevel(SkillType.WOODCUTTING)).append(":");
                    writer.append(profile.getSkillXpLevel(SkillType.WOODCUTTING)).append(":");
                    writer.append(profile.getSkillLevel(SkillType.REPAIR)).append(":");
                    writer.append(profile.getSkillLevel(SkillType.UNARMED)).append(":");
                    writer.append(profile.getSkillLevel(SkillType.HERBALISM)).append(":");
                    writer.append(profile.getSkillLevel(SkillType.EXCAVATION)).append(":");
                    writer.append(profile.getSkillLevel(SkillType.ARCHERY)).append(":");
                    writer.append(profile.getSkillLevel(SkillType.SWORDS)).append(":");
                    writer.append(profile.getSkillLevel(SkillType.AXES)).append(":");
                    writer.append(profile.getSkillLevel(SkillType.ACROBATICS)).append(":");
                    writer.append(profile.getSkillXpLevel(SkillType.REPAIR)).append(":");
                    writer.append(profile.getSkillXpLevel(SkillType.UNARMED)).append(":");
                    writer.append(profile.getSkillXpLevel(SkillType.HERBALISM)).append(":");
                    writer.append(profile.getSkillXpLevel(SkillType.EXCAVATION)).append(":");
                    writer.append(profile.getSkillXpLevel(SkillType.ARCHERY)).append(":");
                    writer.append(profile.getSkillXpLevel(SkillType.SWORDS)).append(":");
                    writer.append(profile.getSkillXpLevel(SkillType.AXES)).append(":");
                    writer.append(profile.getSkillXpLevel(SkillType.ACROBATICS)).append(":");
                    writer.append(":");
                    writer.append(profile.getSkillLevel(SkillType.TAMING)).append(":");
                    writer.append(profile.getSkillXpLevel(SkillType.TAMING)).append(":");
                    writer.append((int) profile.getSkillDATS(AbilityType.BERSERK)).append(":");
                    writer.append((int) profile.getSkillDATS(AbilityType.GIGA_DRILL_BREAKER)).append(":");
                    writer.append((int) profile.getSkillDATS(AbilityType.TREE_FELLER)).append(":");
                    writer.append((int) profile.getSkillDATS(AbilityType.GREEN_TERRA)).append(":");
                    writer.append((int) profile.getSkillDATS(AbilityType.SERRATED_STRIKES)).append(":");
                    writer.append((int) profile.getSkillDATS(AbilityType.SKULL_SPLITTER)).append(":");
                    writer.append((int) profile.getSkillDATS(AbilityType.SUPER_BREAKER)).append(":");
                    HudType hudType = profile.getHudType();
                    writer.append(hudType == null ? "STANDARD" : hudType.toString()).append(":");
                    writer.append(profile.getSkillLevel(SkillType.FISHING)).append(":");
                    writer.append(profile.getSkillXpLevel(SkillType.FISHING)).append(":");
                    writer.append((int) profile.getSkillDATS(AbilityType.BLAST_MINING)).append(":");
                    writer.append(System.currentTimeMillis() / Misc.TIME_CONVERSION_FACTOR).append(":");
                    MobHealthbarType mobHealthbarType = profile.getMobHealthbarType();
                    writer.append(mobHealthbarType == null ? Config.getInstance().getMobHealthbarDefault().toString() : mobHealthbarType.toString()).append(":");
                    writer.append("\r\n");
                }
            }

            // Write the new file
            out = new FileWriter(usersFilePath);
            out.write(writer.toString());
        }
        catch (Exception e) {
            e.printStackTrace();
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
    }

    public List<PlayerStat> readLeaderboard(String skillName, int pageNumber, int statsPerPage) {
        updateLeaderboards();
        List<PlayerStat> statsList = skillName.equalsIgnoreCase("all") ? powerLevels : playerStatHash.get(SkillType.getSkill(skillName));
        int fromIndex = (Math.max(pageNumber, 1) - 1) * statsPerPage;

        return statsList.subList(Math.min(fromIndex, statsList.size()), Math.min(fromIndex + statsPerPage, statsList.size()));
    }

    public Map<String, Integer> readRank(String playerName) {
        updateLeaderboards();

        Map<String, Integer> skills = new HashMap<String, Integer>();

        for (SkillType skill : SkillType.nonChildSkills()) {
            skills.put(skill.name(), getPlayerRank(playerName, playerStatHash.get(skill)));
        }

        skills.put("ALL", getPlayerRank(playerName, powerLevels));

        return skills;
    }

    public void newUser(String playerName) {
        try {
            // Open the file to write the player
            BufferedWriter out = new BufferedWriter(new FileWriter(mcMMO.getUsersFilePath(), true));

            // Add the player to the end
            out.append(playerName).append(":");
            out.append("0:"); // Mining
            out.append(":");
            out.append(":");
            out.append("0:"); // Xp
            out.append("0:"); // Woodcutting
            out.append("0:"); // WoodCuttingXp
            out.append("0:"); // Repair
            out.append("0:"); // Unarmed
            out.append("0:"); // Herbalism
            out.append("0:"); // Excavation
            out.append("0:"); // Archery
            out.append("0:"); // Swords
            out.append("0:"); // Axes
            out.append("0:"); // Acrobatics
            out.append("0:"); // RepairXp
            out.append("0:"); // UnarmedXp
            out.append("0:"); // HerbalismXp
            out.append("0:"); // ExcavationXp
            out.append("0:"); // ArcheryXp
            out.append("0:"); // SwordsXp
            out.append("0:"); // AxesXp
            out.append("0:"); // AcrobaticsXp
            out.append(":");
            out.append("0:"); // Taming
            out.append("0:"); // TamingXp
            out.append("0:"); // DATS
            out.append("0:"); // DATS
            out.append("0:"); // DATS
            out.append("0:"); // DATS
            out.append("0:"); // DATS
            out.append("0:"); // DATS
            out.append("0:"); // DATS
            out.append("STANDARD").append(":"); // HUD
            out.append("0:"); // Fishing
            out.append("0:"); // FishingXp
            out.append("0:"); // Blast Mining
            out.append(String.valueOf(System.currentTimeMillis() / Misc.TIME_CONVERSION_FACTOR)).append(":"); // LastLogin
            out.append(Config.getInstance().getMobHealthbarDefault().toString()).append(":"); // Mob Healthbar HUD

            // Add more in the same format as the line above

            out.newLine();
            out.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<String> loadPlayerData(String playerName) {
        List<String> playerData = new ArrayList<String>();
        try {
            // Open the user file
            FileReader file = new FileReader(mcMMO.getUsersFilePath());
            BufferedReader in = new BufferedReader(file);
            String line;

            while ((line = in.readLine()) != null) {
                // Find if the line contains the player we want.
                String[] character = line.split(":");

                if (!character[0].equalsIgnoreCase(playerName)) {
                    continue;
                }

                // Skill levels
                playerData.add(character[24]); // Taming
                playerData.add(character[1]); // Mining
                playerData.add(character[7]); // Repair
                playerData.add(character[5]); // Woodcutting
                playerData.add(character[8]); // Unarmed
                playerData.add(character[9]); // Herbalism
                playerData.add(character[10]); // Excavation
                playerData.add(character[11]); // Archery
                playerData.add(character[12]); // Swords
                playerData.add(character[13]); // Axes
                playerData.add(character[14]); // Acrobatics
                playerData.add(character[34]); // Fishing

                // Experience
                playerData.add(character[25]); // Taming
                playerData.add(character[4]); // Mining
                playerData.add(character[15]); // Repair
                playerData.add(character[6]); // Woodcutting
                playerData.add(character[16]); // Unarmed
                playerData.add(character[17]); // Herbalism
                playerData.add(character[18]); // Excavation
                playerData.add(character[19]); // Archery
                playerData.add(character[20]); // Swords
                playerData.add(character[21]); // Axes
                playerData.add(character[22]); // Acrobatics
                playerData.add(character[35]); // Fishing

                // Cooldowns
                playerData.add(null); // Taming
                playerData.add(character[32]); // SuperBreaker
                playerData.add(null); // Repair
                playerData.add(character[28]); // Tree Feller
                playerData.add(character[26]); // Beserk
                playerData.add(character[29]); // Green Terra
                playerData.add(character[27]); // Giga Drill Breaker
                playerData.add(null); // Archery
                playerData.add(character[30]); // Serrated Strikes
                playerData.add(character[31]); // Skull Splitter
                playerData.add(null); // Acrobatics
                playerData.add(character[36]); // Blast Mining

                playerData.add(character.length > 33 ? character[33] : null); // HudType
                playerData.add(character.length > 38 ? character[38] : null); // MobHealthBar
            }

            in.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return playerData;
    }

    public boolean convert(String[] character) throws Exception {
        // Not implemented
        return false;
    }

    public boolean checkConnected() {
        // Not implemented
        return false;
    }

    /**
* Update the leader boards.
*/
    private void updateLeaderboards() {
        // Only update FFS leaderboards every 10 minutes.. this puts a lot of strain on the server (depending on the size of the database) and should not be done frequently
        if (System.currentTimeMillis() < lastUpdate + UPDATE_WAIT_TIME) {
            return;
        }

        String usersFilePath = mcMMO.getUsersFilePath();
        lastUpdate = System.currentTimeMillis(); // Log when the last update was run
        powerLevels.clear(); // Clear old values from the power levels

        // Initialize lists
        List<PlayerStat> mining = new ArrayList<PlayerStat>();
        List<PlayerStat> woodcutting = new ArrayList<PlayerStat>();
        List<PlayerStat> herbalism = new ArrayList<PlayerStat>();
        List<PlayerStat> excavation = new ArrayList<PlayerStat>();
        List<PlayerStat> acrobatics = new ArrayList<PlayerStat>();
        List<PlayerStat> repair = new ArrayList<PlayerStat>();
        List<PlayerStat> swords = new ArrayList<PlayerStat>();
        List<PlayerStat> axes = new ArrayList<PlayerStat>();
        List<PlayerStat> archery = new ArrayList<PlayerStat>();
        List<PlayerStat> unarmed = new ArrayList<PlayerStat>();
        List<PlayerStat> taming = new ArrayList<PlayerStat>();
        List<PlayerStat> fishing = new ArrayList<PlayerStat>();

        // Read from the FlatFile database and fill our arrays with information
        try {
            BufferedReader in = new BufferedReader(new FileReader(usersFilePath));
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

                powerLevel += loadStat(mining, playerName, data, 1);
                powerLevel += loadStat(woodcutting, playerName, data, 5);
                powerLevel += loadStat(repair, playerName, data, 7);
                powerLevel += loadStat(unarmed, playerName, data, 8);
                powerLevel += loadStat(herbalism, playerName, data, 9);
                powerLevel += loadStat(excavation, playerName, data, 10);
                powerLevel += loadStat(archery, playerName, data, 11);
                powerLevel += loadStat(swords, playerName, data, 12);
                powerLevel += loadStat(axes, playerName, data, 13);
                powerLevel += loadStat(acrobatics, playerName, data, 14);
                powerLevel += loadStat(taming, playerName, data, 24);
                powerLevel += loadStat(fishing, playerName, data, 34);

                powerLevels.add(new PlayerStat(playerName, powerLevel));
            }
            in.close();
        }
        catch (Exception e) {
            mcMMO.p.getLogger().severe("Exception while reading " + usersFilePath + " (Are you sure you formatted it correctly?)" + e.toString());
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

    private void createDatabase() {
        if (usersFile.exists()) {
            return;
        }

        usersFile.getParentFile().mkdir();

        try {
            mcMMO.p.debug("Creating mcmmo.users file...");
            new File(mcMMO.getUsersFilePath()).createNewFile();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Integer getPlayerRank(String playerName, List<PlayerStat> statsList) {
        if (statsList == null) {
            return null;
        }

        int currentPos = 1;

        for (PlayerStat stat : statsList) {
            if (stat.name.equalsIgnoreCase(playerName)) {
                return currentPos;
            }

            currentPos++;
        }

        return null;
    }

    private int loadStat(List<PlayerStat> statList, String playerName, String[] data, int dataIndex) {
        if (data.length <= dataIndex) {
            return 0;
        }

        int statValue = Integer.parseInt(data[dataIndex]);
        statList.add(new PlayerStat(playerName, statValue));

        return statValue;
    }

    private class SkillComparator implements Comparator<PlayerStat> {
        @Override
        public int compare(PlayerStat o1, PlayerStat o2) {
            return (o2.statVal - o1.statVal);
        }
    }
}
