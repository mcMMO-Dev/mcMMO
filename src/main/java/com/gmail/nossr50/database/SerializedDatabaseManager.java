package com.gmail.nossr50.database;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.OfflinePlayer;

import com.gmail.nossr50.datatypes.database.DatabaseType;
import com.gmail.nossr50.datatypes.database.PlayerStat;
import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.datatypes.player.UserData;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.Misc;

public class SerializedDatabaseManager implements DatabaseManager {
    private final HashMap<SkillType, List<PlayerStat>> playerStatHash = new HashMap<SkillType, List<PlayerStat>>();
    private final List<PlayerStat> powerLevels = new ArrayList<PlayerStat>();
    private long lastUpdate = 0;

    private final long UPDATE_WAIT_TIME = 600000L; // 10 minutes

    protected SerializedDatabaseManager() {
        updateLeaderboards();
    }

    public void purgePowerlessUsers() {
        int purgedUsers = 0;

        mcMMO.p.getLogger().info("Purging powerless users...");

        File usersDirectory = new File(mcMMO.getUsersDirectory());
        File[] userFiles = usersDirectory.listFiles();

        if (userFiles == null) {
            return;
        }

        for (File file : userFiles) {
            if (!file.isFile() || file.isDirectory()) {
                continue;
            }

            UserData data = deserialize(file);

            if (data == null) {
                continue;
            }

            boolean powerless = true;
            for (int skill : data.getSkillLevels().values()) {
                if (skill != 0) {
                    powerless = false;
                    break;
                }
            }

            if (powerless && file.delete()) {
                purgedUsers++;
                Misc.profileCleanup(data.getName());
            }
        }

        mcMMO.p.getLogger().info("Purged " + purgedUsers + " users from the database.");
    }

    public void purgeOldUsers() {
        int removedPlayers = 0;
        long currentTime = System.currentTimeMillis();

        mcMMO.p.getLogger().info("Purging old users...");

        File usersDirectory = new File(mcMMO.getUsersDirectory());
        File[] userFiles = usersDirectory.listFiles();

        if (userFiles == null) {
            return;
        }

        for (File file : userFiles) {
            if (!file.isFile() || file.isDirectory()) {
                continue;
            }

            UserData data = deserialize(file);

            if (data == null) {
                continue;
            }

            boolean rewrite = false;
            String name = data.getName();
            long lastPlayed = data.getLastPlayed();

            if (lastPlayed == 0) {
                OfflinePlayer player = mcMMO.p.getServer().getOfflinePlayer(name);
                lastPlayed = player.getLastPlayed();
                rewrite = true;
            }

            if (currentTime - lastPlayed > PURGE_TIME) {
                if (file.delete()) {
                    removedPlayers++;
                    Misc.profileCleanup(name);
                }
            }

            if (rewrite) {
                data.setLastPlayed(lastPlayed);
                serialize(file, data);
            }
        }

        mcMMO.p.getLogger().info("Purged " + removedPlayers + " users from the database.");
    }

    public boolean removeUser(String playerName) {
        boolean worked = false;

        File usersDirectory = new File(mcMMO.getUsersDirectory());
        File[] userFiles = usersDirectory.listFiles();

        if (userFiles == null) {
            return worked;
        }

        for (File file : userFiles) {
            if (!file.isFile() || file.isDirectory()) {
                continue;
            }

            UserData data = deserialize(file);

            if (data != null && data.getName().equalsIgnoreCase(playerName)) {
                mcMMO.p.getLogger().info("User found, removing...");
                worked = file.delete();
                break;
            }
        }

        return worked;
    }

    public boolean saveUser(PlayerProfile profile) {
        UserData data = new UserData(profile);

        return serialize(new File(mcMMO.getUsersDirectory(), data.getName() + ".mcmmoplayer"), data);
    }

    public List<PlayerStat> readLeaderboard(SkillType skill, int pageNumber, int statsPerPage) {
        updateLeaderboards();

        List<PlayerStat> statsList = skill == null ? powerLevels : playerStatHash.get(skill);
        int fromIndex = (Math.max(pageNumber, 1) - 1) * statsPerPage;

        return statsList.subList(Math.min(fromIndex, statsList.size()), Math.min(fromIndex + statsPerPage, statsList.size()));
    }

    public Map<SkillType, Integer> readRank(String playerName) {
        updateLeaderboards();

        Map<SkillType, Integer> skills = new HashMap<SkillType, Integer>();

        for (SkillType skill : SkillType.NON_CHILD_SKILLS) {
            skills.put(skill, getPlayerRank(playerName, playerStatHash.get(skill)));
        }

        skills.put(null, getPlayerRank(playerName, powerLevels));

        return skills;
    }

    public void newUser(String playerName) {
        serialize(new File(mcMMO.getUsersDirectory(), playerName + ".mcmmoplayer"), new UserData(new PlayerProfile(playerName)));
    }

    public PlayerProfile loadPlayerProfile(String playerName, boolean createNew) {
        File usersDirectory = new File(mcMMO.getUsersDirectory());
        File[] userFiles = usersDirectory.listFiles();

        if (userFiles == null) {
            return new PlayerProfile(playerName, createNew);
        }

        for (File file : userFiles) {
            if (!file.isFile() || file.isDirectory()) {
                continue;
            }

            UserData data = deserialize(file);

            if (data != null && data.getName().equalsIgnoreCase(playerName)) {
                return loadFromData(data);
            }
        }

        return new PlayerProfile(playerName, createNew);
    }

    public List<String> getStoredUsers() {
        ArrayList<String> users = new ArrayList<String>();

        File usersDirectory = new File(mcMMO.getUsersDirectory());
        File[] userFiles = usersDirectory.listFiles();

        if (userFiles == null) {
            return users;
        }

        for (File file : userFiles) {
            if (!file.isFile() || file.isDirectory()) {
                continue;
            }

            UserData data = deserialize(file);

            if (data == null) {
                continue;
            }

            users.add(data.getName());
        }

        return users;
    }

    public void convertUsers(DatabaseManager destination) {
        File usersDirectory = new File(mcMMO.getUsersDirectory());
        File[] userFiles = usersDirectory.listFiles();

        if (userFiles == null) {
            //TODO: log issue
            return;
        }

        int convertedUsers = 0;
        long startMillis = System.currentTimeMillis();

        for (File file : userFiles) {
            if (!file.isFile() || file.isDirectory()) {
                continue;
            }

            UserData data = deserialize(file);

            if (data == null) {
                continue;
            }

            try {
                if (destination.saveUser(loadFromData(data))) {
                    convertedUsers++;
                    Misc.printProgress(convertedUsers, progressInterval, startMillis);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public DatabaseType getDatabaseType() {
        return DatabaseType.SERIALIZED;
    }

    private UserData deserialize(File file) {
        UserData data = null;

        try {
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fis);

            data = (UserData) ois.readObject();
            ois.close();
        }
        catch (Exception e) {
            mcMMO.p.getLogger().severe("Exception while reading " + file.getName() + ": " + e.toString());
        }

        return data;
    }

    private boolean serialize(File file, UserData data) {
        boolean success = true;

        try {
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(data);
            oos.close();
        }
        catch (Exception e) {
            mcMMO.p.getLogger().severe("Exception while writing " + file.getName() + ": " + e.toString());
            success = false;
        }

        return success;
    }

    private PlayerProfile loadFromData(UserData data) {
        return new PlayerProfile(data.getName(), data.getSkillLevels(), data.getSkillXp(), data.getAbilityData(), data.getMobHealthbarType());
    }

    /**
     * Update the leader boards.
     */
    private void updateLeaderboards() {
        // Only update leaderboards every 10 minutes.. this puts a lot of strain on the server (depending on the size of the database) and should not be done frequently
        if (System.currentTimeMillis() < lastUpdate + UPDATE_WAIT_TIME) {
            return;
        }

        File usersDirectory = new File(mcMMO.getUsersDirectory());
        File[] userFiles = usersDirectory.listFiles();

        if (userFiles == null) {
            return;
        }

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
        List<PlayerStat> alchemy = new ArrayList<PlayerStat>();

        for (File file : userFiles) {
            if (!file.isFile() || file.isDirectory()) {
                continue;
            }

            UserData data = deserialize(file);

            if (data == null) {
                continue;
            }

            int powerLevel = 0;
            String playerName = data.getName();
            Map<SkillType, Integer> skills = data.getSkillLevels();

            powerLevel += putStat(acrobatics, playerName, skills.get(SkillType.ACROBATICS));
            powerLevel += putStat(alchemy, playerName, skills.get(SkillType.ALCHEMY));
            powerLevel += putStat(archery, playerName, skills.get(SkillType.ARCHERY));
            powerLevel += putStat(axes, playerName, skills.get(SkillType.AXES));
            powerLevel += putStat(excavation, playerName, skills.get(SkillType.EXCAVATION));
            powerLevel += putStat(fishing, playerName, skills.get(SkillType.FISHING));
            powerLevel += putStat(herbalism, playerName, skills.get(SkillType.HERBALISM));
            powerLevel += putStat(mining, playerName, skills.get(SkillType.MINING));
            powerLevel += putStat(repair, playerName, skills.get(SkillType.REPAIR));
            powerLevel += putStat(swords, playerName, skills.get(SkillType.SWORDS));
            powerLevel += putStat(taming, playerName, skills.get(SkillType.TAMING));
            powerLevel += putStat(unarmed, playerName, skills.get(SkillType.UNARMED));
            powerLevel += putStat(woodcutting, playerName, skills.get(SkillType.WOODCUTTING));

            putStat(powerLevels, playerName, powerLevel);
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
        Collections.sort(alchemy, c);
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
        playerStatHash.put(SkillType.ALCHEMY, alchemy);
    }

    private int putStat(List<PlayerStat> statList, String playerName, int statValue) {
        statList.add(new PlayerStat(playerName, statValue));
        return statValue;
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

    private class SkillComparator implements Comparator<PlayerStat> {
        @Override
        public int compare(PlayerStat o1, PlayerStat o2) {
            return (o2.statVal - o1.statVal);
        }
    }
}
