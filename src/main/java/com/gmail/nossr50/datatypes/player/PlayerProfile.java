package com.gmail.nossr50.datatypes.player;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.config.spout.SpoutConfig;
import com.gmail.nossr50.database.DatabaseManager;
import com.gmail.nossr50.datatypes.skills.AbilityType;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.datatypes.spout.huds.HudType;
import com.gmail.nossr50.datatypes.spout.huds.McMMOHud;
import com.gmail.nossr50.skills.child.FamilyTree;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.StringUtils;

public class PlayerProfile {
    private String playerName;

    // HUD
    private McMMOHud spoutHud;
    private HudType  hudType;

    // mySQL Stuff
    private int userId;

    private boolean loaded;

    private Map<SkillType, Integer>   skills     = new HashMap<SkillType, Integer>(); // Skills and Levels
    private Map<SkillType, Integer>   skillsXp   = new HashMap<SkillType, Integer>(); // Skills and Xp
    private Map<AbilityType, Integer> skillsDATS = new HashMap<AbilityType, Integer>();

    private final static String location = mcMMO.getUsersFilePath();

    public PlayerProfile(String playerName, boolean addNew) {
        this.playerName = playerName;

        if (mcMMO.spoutEnabled) {
            hudType = SpoutConfig.getInstance().defaultHudType;
        }
        else {
            hudType = HudType.DISABLED;
        }

        for (AbilityType abilityType : AbilityType.values()) {
            skillsDATS.put(abilityType, 0);
        }

        for (SkillType skillType : SkillType.values()) {
            if (!skillType.isChildSkill()) {
                skills.put(skillType, 0);
                skillsXp.put(skillType, 0);
            }
        }

        if (Config.getInstance().getUseMySQL()) {
            if (!loadMySQL() && addNew) {
                addMySQLPlayer();
                loaded = true;
            }
        }
        else if (!load() && addNew) {
            addPlayer();
            loaded = true;
        }
    }

    public String getPlayerName() {
        return playerName;
    }

    public boolean loadMySQL() {
        String tablePrefix = Config.getInstance().getMySQLTablePrefix();

        userId = DatabaseManager.getInt("SELECT id FROM " + tablePrefix + "users WHERE user = '" + playerName + "'");

        if (userId == 0) {
            return false;
        }

        HashMap<Integer, ArrayList<String>> huds = DatabaseManager.read("SELECT hudtype FROM " + tablePrefix + "huds WHERE user_id = " + userId);

        if (huds.get(1) == null) {
            DatabaseManager.write("INSERT INTO " + tablePrefix + "huds (user_id) VALUES (" + userId + ")");
        }
        else {
            for (HudType type : HudType.values()) {
                if (type.toString().equalsIgnoreCase(huds.get(1).get(0))) {
                    hudType = type;
                }
            }
        }

        HashMap<Integer, ArrayList<String>> cooldowns = DatabaseManager.read("SELECT mining, woodcutting, unarmed, herbalism, excavation, swords, axes, blast_mining FROM " + tablePrefix + "cooldowns WHERE user_id = " + userId);
        ArrayList<String> cooldownValues = cooldowns.get(1);

        if (cooldownValues == null) {
            DatabaseManager.write("INSERT INTO " + tablePrefix + "cooldowns (user_id) VALUES (" + userId + ")");
            mcMMO.p.getLogger().warning(playerName + "does not exist in the cooldown table. Their cooldowns will be reset.");
        }
        else {
            skillsDATS.put(AbilityType.SUPER_BREAKER, Integer.valueOf(cooldownValues.get(0)));
            skillsDATS.put(AbilityType.TREE_FELLER, Integer.valueOf(cooldownValues.get(1)));
            skillsDATS.put(AbilityType.BERSERK, Integer.valueOf(cooldownValues.get(2)));
            skillsDATS.put(AbilityType.GREEN_TERRA, Integer.valueOf(cooldownValues.get(3)));
            skillsDATS.put(AbilityType.GIGA_DRILL_BREAKER, Integer.valueOf(cooldownValues.get(4)));
            skillsDATS.put(AbilityType.SERRATED_STRIKES, Integer.valueOf(cooldownValues.get(5)));
            skillsDATS.put(AbilityType.SKULL_SPLITTER, Integer.valueOf(cooldownValues.get(6)));
            skillsDATS.put(AbilityType.BLAST_MINING, Integer.valueOf(cooldownValues.get(7)));
        }

        HashMap<Integer, ArrayList<String>> stats = DatabaseManager.read("SELECT taming, mining, repair, woodcutting, unarmed, herbalism, excavation, archery, swords, axes, acrobatics, fishing FROM " + tablePrefix + "skills WHERE user_id = " + userId);
        ArrayList<String> statValues = stats.get(1);

        if (statValues == null) {
            DatabaseManager.write("INSERT INTO " + tablePrefix + "skills (user_id) VALUES (" + userId + ")");
            mcMMO.p.getLogger().warning(playerName + "does not exist in the skills table. Their stats will be reset.");
        }
        else {
            skills.put(SkillType.TAMING, Integer.valueOf(statValues.get(0)));
            skills.put(SkillType.MINING, Integer.valueOf(statValues.get(1)));
            skills.put(SkillType.REPAIR, Integer.valueOf(statValues.get(2)));
            skills.put(SkillType.WOODCUTTING, Integer.valueOf(statValues.get(3)));
            skills.put(SkillType.UNARMED, Integer.valueOf(statValues.get(4)));
            skills.put(SkillType.HERBALISM, Integer.valueOf(statValues.get(5)));
            skills.put(SkillType.EXCAVATION, Integer.valueOf(statValues.get(6)));
            skills.put(SkillType.ARCHERY, Integer.valueOf(statValues.get(7)));
            skills.put(SkillType.SWORDS, Integer.valueOf(statValues.get(8)));
            skills.put(SkillType.AXES, Integer.valueOf(statValues.get(9)));
            skills.put(SkillType.ACROBATICS, Integer.valueOf(statValues.get(10)));
            skills.put(SkillType.FISHING, Integer.valueOf(statValues.get(11)));
        }

        HashMap<Integer, ArrayList<String>> experience = DatabaseManager.read("SELECT taming, mining, repair, woodcutting, unarmed, herbalism, excavation, archery, swords, axes, acrobatics, fishing FROM " + tablePrefix + "experience WHERE user_id = " + userId);
        ArrayList<String> experienceValues = experience.get(1);

        if (experienceValues == null) {
            DatabaseManager.write("INSERT INTO " + tablePrefix + "experience (user_id) VALUES (" + userId + ")");
            mcMMO.p.getLogger().warning(playerName + "does not exist in the experience table. Their experience will be reset.");
        }
        else {
            skillsXp.put(SkillType.TAMING, Integer.valueOf(experienceValues.get(0)));
            skillsXp.put(SkillType.MINING, Integer.valueOf(experienceValues.get(1)));
            skillsXp.put(SkillType.REPAIR, Integer.valueOf(experienceValues.get(2)));
            skillsXp.put(SkillType.WOODCUTTING, Integer.valueOf(experienceValues.get(3)));
            skillsXp.put(SkillType.UNARMED, Integer.valueOf(experienceValues.get(4)));
            skillsXp.put(SkillType.HERBALISM, Integer.valueOf(experienceValues.get(5)));
            skillsXp.put(SkillType.EXCAVATION, Integer.valueOf(experienceValues.get(6)));
            skillsXp.put(SkillType.ARCHERY, Integer.valueOf(experienceValues.get(7)));
            skillsXp.put(SkillType.SWORDS, Integer.valueOf(experienceValues.get(8)));
            skillsXp.put(SkillType.AXES, Integer.valueOf(experienceValues.get(9)));
            skillsXp.put(SkillType.ACROBATICS, Integer.valueOf(experienceValues.get(10)));
            skillsXp.put(SkillType.FISHING, Integer.valueOf(experienceValues.get(11)));
        }

        loaded = true;
        return true;
    }

    public void addMySQLPlayer() {
        String tablePrefix = Config.getInstance().getMySQLTablePrefix();

        DatabaseManager.write("INSERT INTO " + tablePrefix + "users (user, lastlogin) VALUES ('" + playerName + "'," + System.currentTimeMillis() / Misc.TIME_CONVERSION_FACTOR + ")");
        userId = DatabaseManager.getInt("SELECT id FROM " + tablePrefix + "users WHERE user = '" + playerName + "'");

        DatabaseManager.write("INSERT INTO " + tablePrefix + "cooldowns (user_id) VALUES (" + userId + ")");
        DatabaseManager.write("INSERT INTO " + tablePrefix + "skills (user_id) VALUES (" + userId + ")");
        DatabaseManager.write("INSERT INTO " + tablePrefix + "experience (user_id) VALUES (" + userId + ")");
    }

    public boolean load() {
        try {
            // Open the user file
            FileReader file = new FileReader(location);
            BufferedReader in = new BufferedReader(file);
            String line;

            while ((line = in.readLine()) != null) {
                // Find if the line contains the player we want.
                String[] character = line.split(":");

                if (!character[0].equalsIgnoreCase(playerName)) {
                    continue;
                }

                if (character.length > 1 && StringUtils.isInt(character[1])) {
                    skills.put(SkillType.MINING, Integer.valueOf(character[1]));
                }

                if (character.length > 4 && StringUtils.isInt(character[4])) {
                    skillsXp.put(SkillType.MINING, Integer.valueOf(character[4]));
                }

                if (character.length > 5 && StringUtils.isInt(character[5])) {
                    skills.put(SkillType.WOODCUTTING, Integer.valueOf(character[5]));
                }

                if (character.length > 6 && StringUtils.isInt(character[6])) {
                    skillsXp.put(SkillType.WOODCUTTING, Integer.valueOf(character[6]));
                }

                if (character.length > 7 && StringUtils.isInt(character[7])) {
                    skills.put(SkillType.REPAIR, Integer.valueOf(character[7]));
                }

                if (character.length > 8 && StringUtils.isInt(character[8])) {
                    skills.put(SkillType.UNARMED,  Integer.valueOf(character[8]));
                }

                if (character.length > 9 && StringUtils.isInt(character[9])) {
                    skills.put(SkillType.HERBALISM, Integer.valueOf(character[9]));
                }

                if (character.length > 10 && StringUtils.isInt(character[10])) {
                    skills.put(SkillType.EXCAVATION, Integer.valueOf(character[10]));
                }

                if (character.length > 11 && StringUtils.isInt(character[11])) {
                    skills.put(SkillType.ARCHERY, Integer.valueOf(character[11]));
                }

                if (character.length > 12 && StringUtils.isInt(character[12])) {
                    skills.put(SkillType.SWORDS, Integer.valueOf(character[12]));
                }

                if (character.length > 13 && StringUtils.isInt(character[13])) {
                    skills.put(SkillType.AXES, Integer.valueOf(character[13]));
                }

                if (character.length > 14 && StringUtils.isInt(character[14])) {
                    skills.put(SkillType.ACROBATICS, Integer.valueOf(character[14]));
                }

                if (character.length > 15 && StringUtils.isInt(character[15])) {
                    skillsXp.put(SkillType.REPAIR, Integer.valueOf(character[15]));
                }

                if (character.length > 16 && StringUtils.isInt(character[16])) {
                    skillsXp.put(SkillType.UNARMED, Integer.valueOf(character[16]));
                }

                if (character.length > 17 && StringUtils.isInt(character[17])) {
                    skillsXp.put(SkillType.HERBALISM, Integer.valueOf(character[17]));
                }

                if (character.length > 18 && StringUtils.isInt(character[18])) {
                    skillsXp.put(SkillType.EXCAVATION, Integer.valueOf(character[18]));
                }

                if (character.length > 19 && StringUtils.isInt(character[19])) {
                    skillsXp.put(SkillType.ARCHERY, Integer.valueOf(character[19]));
                }

                if (character.length > 20 && StringUtils.isInt(character[20])) {
                    skillsXp.put(SkillType.SWORDS, Integer.valueOf(character[20]));
                }

                if (character.length > 21 && StringUtils.isInt(character[21])) {
                    skillsXp.put(SkillType.AXES, Integer.valueOf(character[21]));
                }

                if (character.length > 22 && StringUtils.isInt(character[22])) {
                    skillsXp.put(SkillType.ACROBATICS, Integer.valueOf(character[22]));
                }

                if (character.length > 24 && StringUtils.isInt(character[24])) {
                    skills.put(SkillType.TAMING, Integer.valueOf(character[24]));
                }

                if (character.length > 25 && StringUtils.isInt(character[25])) {
                    skillsXp.put(SkillType.TAMING, Integer.valueOf(character[25]));
                }

                if (character.length > 26) {
                    skillsDATS.put(AbilityType.BERSERK, Integer.valueOf(character[26]));
                }

                if (character.length > 27) {
                    skillsDATS.put(AbilityType.GIGA_DRILL_BREAKER, Integer.valueOf(character[27]));
                }

                if (character.length > 28) {
                    skillsDATS.put(AbilityType.TREE_FELLER, Integer.valueOf(character[28]));
                }

                if (character.length > 29) {
                    skillsDATS.put(AbilityType.GREEN_TERRA, Integer.valueOf(character[29]));
                }

                if (character.length > 30) {
                    skillsDATS.put(AbilityType.SERRATED_STRIKES, Integer.valueOf(character[30]));
                }

                if (character.length > 31) {
                    skillsDATS.put(AbilityType.SKULL_SPLITTER, Integer.valueOf(character[31]));
                }

                if (character.length > 32) {
                    skillsDATS.put(AbilityType.SUPER_BREAKER, Integer.valueOf(character[32]));
                }

                if (character.length > 33) {
                    for (HudType type : HudType.values()) {
                        if (type.toString().equalsIgnoreCase(character[33])) {
                            hudType = type;
                        }
                    }
                }

                if (character.length > 34) {
                    skills.put(SkillType.FISHING, Integer.valueOf(character[34]));
                }

                if (character.length > 35) {
                    skillsXp.put(SkillType.FISHING, Integer.valueOf(character[35]));
                }

                if (character.length > 36) {
                    skillsDATS.put(AbilityType.BLAST_MINING, Integer.valueOf(character[36]));
                }

                loaded = true;

                in.close();
                return true;
            }

            in.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void save() {
        Long timestamp = System.currentTimeMillis();

        // If we are using mysql save to database
        if (Config.getInstance().getUseMySQL()) {
            String tablePrefix = Config.getInstance().getMySQLTablePrefix();

            DatabaseManager.write("UPDATE " + tablePrefix + "huds SET hudtype = '" + hudType.toString() + "' WHERE user_id = " + userId);
            DatabaseManager.write("UPDATE " + tablePrefix + "users SET lastlogin = " + ((int) (timestamp / Misc.TIME_CONVERSION_FACTOR)) + " WHERE id = " + userId);
            DatabaseManager.write("UPDATE " + tablePrefix + "cooldowns SET "
                    + " mining = " + skillsDATS.get(AbilityType.SUPER_BREAKER)
                    + ", woodcutting = " + skillsDATS.get(AbilityType.TREE_FELLER)
                    + ", unarmed = " + skillsDATS.get(AbilityType.BERSERK)
                    + ", herbalism = " + skillsDATS.get(AbilityType.GREEN_TERRA)
                    + ", excavation = " + skillsDATS.get(AbilityType.GIGA_DRILL_BREAKER)
                    + ", swords = " + skillsDATS.get(AbilityType.SERRATED_STRIKES)
                    + ", axes = " + skillsDATS.get(AbilityType.SKULL_SPLITTER)
                    + ", blast_mining = " + skillsDATS.get(AbilityType.BLAST_MINING)
                    + " WHERE user_id = " + userId);
            DatabaseManager.write("UPDATE " + tablePrefix + "skills SET "
                    + " taming = " + skills.get(SkillType.TAMING)
                    + ", mining = " + skills.get(SkillType.MINING)
                    + ", repair = " + skills.get(SkillType.REPAIR)
                    + ", woodcutting = " + skills.get(SkillType.WOODCUTTING)
                    + ", unarmed = " + skills.get(SkillType.UNARMED)
                    + ", herbalism = " + skills.get(SkillType.HERBALISM)
                    + ", excavation = " + skills.get(SkillType.EXCAVATION)
                    + ", archery = " + skills.get(SkillType.ARCHERY)
                    + ", swords = " + skills.get(SkillType.SWORDS)
                    + ", axes = " + skills.get(SkillType.AXES)
                    + ", acrobatics = " + skills.get(SkillType.ACROBATICS)
                    + ", fishing = " + skills.get(SkillType.FISHING)
                    + " WHERE user_id = " + userId);
            DatabaseManager.write("UPDATE " + tablePrefix + "experience SET "
                    + "  taming = " + skillsXp.get(SkillType.TAMING)
                    + ", mining = " + skillsXp.get(SkillType.MINING)
                    + ", repair = " + skillsXp.get(SkillType.REPAIR)
                    + ", woodcutting = " + skillsXp.get(SkillType.WOODCUTTING)
                    + ", unarmed = " + skillsXp.get(SkillType.UNARMED)
                    + ", herbalism = " + skillsXp.get(SkillType.HERBALISM)
                    + ", excavation = " + skillsXp.get(SkillType.EXCAVATION)
                    + ", archery = " + skillsXp.get(SkillType.ARCHERY)
                    + ", swords = " + skillsXp.get(SkillType.SWORDS)
                    + ", axes = " + skillsXp.get(SkillType.AXES)
                    + ", acrobatics = " + skillsXp.get(SkillType.ACROBATICS)
                    + ", fishing = " + skillsXp.get(SkillType.FISHING)
                    + " WHERE user_id = " + userId);
        }
        else {
            // Otherwise save to flatfile
            try {
                // Open the file
                FileReader file = new FileReader(location);
                BufferedReader in = new BufferedReader(file);
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
                        writer.append(skills.get(SkillType.MINING)).append(":");
                        writer.append(":");
                        writer.append(":");
                        writer.append(skillsXp.get(SkillType.MINING)).append(":");
                        writer.append(skills.get(SkillType.WOODCUTTING)).append(":");
                        writer.append(skillsXp.get(SkillType.WOODCUTTING)).append(":");
                        writer.append(skills.get(SkillType.REPAIR)).append(":");
                        writer.append(skills.get(SkillType.UNARMED)).append(":");
                        writer.append(skills.get(SkillType.HERBALISM)).append(":");
                        writer.append(skills.get(SkillType.EXCAVATION)).append(":");
                        writer.append(skills.get(SkillType.ARCHERY)).append(":");
                        writer.append(skills.get(SkillType.SWORDS)).append(":");
                        writer.append(skills.get(SkillType.AXES)).append(":");
                        writer.append(skills.get(SkillType.ACROBATICS)).append(":");
                        writer.append(skillsXp.get(SkillType.REPAIR)).append(":");
                        writer.append(skillsXp.get(SkillType.UNARMED)).append(":");
                        writer.append(skillsXp.get(SkillType.HERBALISM)).append(":");
                        writer.append(skillsXp.get(SkillType.EXCAVATION)).append(":");
                        writer.append(skillsXp.get(SkillType.ARCHERY)).append(":");
                        writer.append(skillsXp.get(SkillType.SWORDS)).append(":");
                        writer.append(skillsXp.get(SkillType.AXES)).append(":");
                        writer.append(skillsXp.get(SkillType.ACROBATICS)).append(":");
                        writer.append(":");
                        writer.append(skills.get(SkillType.TAMING)).append(":");
                        writer.append(skillsXp.get(SkillType.TAMING)).append(":");
                        writer.append(skillsDATS.get(AbilityType.BERSERK)).append(":");
                        writer.append(skillsDATS.get(AbilityType.GIGA_DRILL_BREAKER)).append(":");
                        writer.append(skillsDATS.get(AbilityType.TREE_FELLER)).append(":");
                        writer.append(skillsDATS.get(AbilityType.GREEN_TERRA)).append(":");
                        writer.append(skillsDATS.get(AbilityType.SERRATED_STRIKES)).append(":");
                        writer.append(skillsDATS.get(AbilityType.SKULL_SPLITTER)).append(":");
                        writer.append(skillsDATS.get(AbilityType.SUPER_BREAKER)).append(":");
                        writer.append(hudType.toString()).append(":");
                        writer.append(skills.get(SkillType.FISHING)).append(":");
                        writer.append(skillsXp.get(SkillType.FISHING)).append(":");
                        writer.append(skillsDATS.get(AbilityType.BLAST_MINING)).append(":");
                        writer.append(System.currentTimeMillis() / Misc.TIME_CONVERSION_FACTOR).append(":");
                        writer.append("\r\n");
                    }
                }

                in.close();

                // Write the new file
                FileWriter out = new FileWriter(location);
                out.write(writer.toString());
                out.close();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void addPlayer() {
        try {
            // Open the file to write the player
            FileWriter file = new FileWriter(location, true);
            BufferedWriter out = new BufferedWriter(file);

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
            out.append(hudType.toString()).append(":"); // HUD
            out.append("0:"); // Fishing
            out.append("0:"); // FishingXp
            out.append("0:"); // Blast Mining
            out.append(String.valueOf(System.currentTimeMillis() / Misc.TIME_CONVERSION_FACTOR)).append(":"); // LastLogin

            // Add more in the same format as the line above

            out.newLine();
            out.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * mySQL Stuff
     */

    public int getMySQLuserId() {
        return userId;
    }

    public boolean isLoaded() {
        return loaded;
    }

    /*
     * HUD Stuff
     */

    public HudType getHudType() {
        return hudType;
    }

    public McMMOHud getSpoutHud() {
        return spoutHud;
    }

    public void setSpoutHud(McMMOHud spoutHud) {
        this.spoutHud = spoutHud;
    }

    public void setHudType(HudType hudType) {
        this.hudType = hudType;
    }

    /*
     * Cooldowns
     */

    /**
     * Get the current DATS of a skill.
     *
     * @param abilityType Ability to get the DATS for
     * @return the DATS for the ability
     */
    public long getSkillDATS(AbilityType abilityType) {
        return skillsDATS.get(abilityType);
    }

    /**
     * Set the current DATS of a skill.
     *
     * @param abilityType Ability to set the DATS for
     * @param DATS the DATS of the ability
     */
    public void setSkillDATS(AbilityType abilityType, long DATS) {
        int wearsOff = (int) (DATS * .001D);

        skillsDATS.put(abilityType, wearsOff);
    }

    /**
     * Reset all skill cooldowns.
     */
    public void resetCooldowns() {
        for (AbilityType x : skillsDATS.keySet()) {
            skillsDATS.put(x, 0);
        }
    }

    /*
     * Xp Functions
     */

    public int getSkillLevel(SkillType skillType) {
        if (skillType.isChildSkill()) {
            return getChildSkillLevel(skillType);
        }

        return skills.get(skillType);
    }

    public int getChildSkillLevel(SkillType skillType) {
        Set<SkillType> parents = FamilyTree.getParents(skillType);
        int sum = 0;

        for (SkillType parent : parents) {
            sum += Math.min(getSkillLevel(parent), 1000);
        }

        return sum / parents.size();
    }

    public int getSkillXpLevel(SkillType skillType) {
        return skillsXp.get(skillType);
    }

    public void setSkillXpLevel(SkillType skillType, int newValue) {
        if (skillType.isChildSkill()) {
            return;
        }

        skillsXp.put(skillType, newValue);
    }

    public void skillUp(SkillType skillType, int newValue) {
        skills.put(skillType, skills.get(skillType) + newValue);
    }

    /**
     * Remove Xp from a skill.
     *
     * @param skillType Type of skill to modify
     * @param xp Amount of xp to remove
     */
    public void removeXp(SkillType skillType, int xp) {
        if (skillType.isChildSkill()) {
            return;
        }

        skillsXp.put(skillType, skillsXp.get(skillType) - xp);
    }

    /**
     * Modify a skill level.
     *
     * @param skillType Type of skill to modify
     * @param newValue New level value for the skill
     */
    public void modifySkill(SkillType skillType, int newValue) {
        if (skillType.isChildSkill()) {
            return;
        }

        skills.put(skillType, newValue);
        skillsXp.put(skillType, 0);
    }

    /**
     * Add levels to a skill.
     *
     * @param skillType Type of skill to add levels to
     * @param levels Number of levels to add
     */
    public void addLevels(SkillType skillType, int levels) {
        if (skillType.isChildSkill()) {
            return;
        }

        skills.put(skillType, skills.get(skillType) + levels);
        skillsXp.put(skillType, 0);
    }

    /**
     * Get the amount of Xp remaining before the next level.
     *
     * @param skillType Type of skill to check
     * @return the Xp remaining until next level
     */
    public int getXpToLevel(SkillType skillType) {
        return 1020 + (skills.get(skillType) *  Config.getInstance().getFormulaMultiplierCurve());
    }
}
