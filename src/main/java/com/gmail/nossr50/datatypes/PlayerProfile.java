package com.gmail.nossr50.datatypes;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.config.SpoutConfig;
import com.gmail.nossr50.party.Party;
import com.gmail.nossr50.party.PartyManager;
import com.gmail.nossr50.util.Database;
import com.gmail.nossr50.util.Misc;

public class PlayerProfile {

    private String playerName;
    
    /* HUD */
    private SpoutHud spoutHud;
    private HudType hudType = SpoutConfig.getInstance().defaultHudType;

    /* Party Stuff */
    private Party party;
    private Party invite;

    /* Toggles */
    private boolean loaded;

    private boolean placedAnvil;
    private boolean partyChatMode, adminChatMode;
    private boolean godMode;
    private boolean greenTerraMode, treeFellerMode, superBreakerMode, gigaDrillBreakerMode, serratedStrikesMode, skullSplitterMode, berserkMode;
    private boolean greenTerraInformed = true, berserkInformed = true, skullSplitterInformed = true, gigaDrillBreakerInformed = true,
                    superBreakerInformed = true, blastMiningInformed = true, serratedStrikesInformed = true, treeFellerInformed = true;
    private boolean hoePreparationMode, shovelPreparationMode, swordsPreparationMode, fistsPreparationMode,
                    pickaxePreparationMode, axePreparationMode;
    private boolean abilityUse = true;

    /* Timestamps */
    private int recentlyHurt;
    private int respawnATS;

    /* mySQL STUFF */
    private int userId;

    private HashMap<SkillType, Integer> skills = new HashMap<SkillType, Integer>(); //Skills and Levels
    HashMap<SkillType, Integer> skillsXp = new HashMap<SkillType, Integer>(); //Skills and XP
    HashMap<AbilityType, Integer> skillsDATS = new HashMap<AbilityType, Integer>();
    HashMap<ToolType, Integer> toolATS = new HashMap<ToolType, Integer>();

    private final static String location = mcMMO.getUsersFile();

    public PlayerProfile(String playerName, boolean addNew) {
        this.playerName = playerName;
        party = PartyManager.getInstance().getPlayerParty(playerName);

        for (AbilityType abilityType : AbilityType.values()) {
            skillsDATS.put(abilityType, 0);
        }

        for (SkillType skillType : SkillType.values()) {
            if (skillType != SkillType.ALL) {
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
        Database database = mcMMO.getPlayerDatabase();
        String tablePrefix = Config.getInstance().getMySQLTablePrefix();

        userId = database.getInt("SELECT id FROM " + tablePrefix + "users WHERE user = '" + playerName + "'");

        if (userId == 0) {
            return false;
        }
        else {
            HashMap<Integer, ArrayList<String>> huds = database.read("SELECT hudtype FROM " + tablePrefix + "huds WHERE user_id = " + userId);

            if (huds.get(1) == null) {
                database.write("INSERT INTO " + tablePrefix + "huds (user_id) VALUES (" + userId + ")");
            }
            else {
                for (HudType type : HudType.values()) {
                    if (type.toString().equals(huds.get(1).get(0))) {
                        hudType = type;
                    }
                }
            }

            /*
             * I'm still learning MySQL, this is a fix for adding a new table
             * its not pretty but it works
             */
            HashMap<Integer, ArrayList<String>> cooldowns = database.read("SELECT mining, woodcutting, unarmed, herbalism, excavation, swords, axes, blast_mining FROM " + tablePrefix + "cooldowns WHERE user_id = " + userId);
            ArrayList<String> cooldownValues = cooldowns.get(1);

            if (cooldownValues == null) {
                database.write("INSERT INTO " + tablePrefix + "cooldowns (user_id) VALUES (" + userId + ")");
                mcMMO.p.getLogger().warning(playerName + "does not exist in the cooldown table. Their cooldowns will be reset.");
            }
            else {
                skillsDATS.put(AbilityType.SUPER_BREAKER, Integer.valueOf(cooldownValues.get(0)));
                skillsDATS.put(AbilityType.TREE_FELLER, Integer.valueOf(cooldownValues.get(1)));
                skillsDATS.put(AbilityType.BERSERK, Integer.valueOf(cooldownValues.get(2)));
                skillsDATS.put(AbilityType.GREEN_TERRA, Integer.valueOf(cooldownValues.get(3)));
                skillsDATS.put(AbilityType.GIGA_DRILL_BREAKER, Integer.valueOf(cooldownValues.get(4)));
                skillsDATS.put(AbilityType.SERRATED_STRIKES, Integer.valueOf(cooldownValues.get(5)));
                skillsDATS.put(AbilityType.SKULL_SPLIITER, Integer.valueOf(cooldownValues.get(6)));
                skillsDATS.put(AbilityType.BLAST_MINING, Integer.valueOf(cooldownValues.get(7)));
            }

            HashMap<Integer, ArrayList<String>> stats = database.read("SELECT taming, mining, repair, woodcutting, unarmed, herbalism, excavation, archery, swords, axes, acrobatics, fishing FROM " + tablePrefix + "skills WHERE user_id = " + userId);
            ArrayList<String> statValues = stats.get(1);

            if (statValues == null) {
                database.write("INSERT INTO " + tablePrefix + "skills (user_id) VALUES (" + userId + ")");
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

            HashMap<Integer, ArrayList<String>> experience = database.read("SELECT taming, mining, repair, woodcutting, unarmed, herbalism, excavation, archery, swords, axes, acrobatics, fishing FROM " + tablePrefix + "experience WHERE user_id = " + userId);
            ArrayList<String> experienceValues = experience.get(1);

            if (experienceValues == null) {
                database.write("INSERT INTO " + tablePrefix + "experience (user_id) VALUES (" + userId + ")");
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
    }

    public void addMySQLPlayer() {
        Database database = mcMMO.getPlayerDatabase();
        String tablePrefix = Config.getInstance().getMySQLTablePrefix();

        database.write("INSERT INTO " + tablePrefix + "users (user, lastlogin) VALUES ('" + playerName + "'," + System.currentTimeMillis() / 1000 + ")");
        userId = database.getInt("SELECT id FROM "+tablePrefix + "users WHERE user = '" + playerName + "'");
        database.write("INSERT INTO " + tablePrefix + "cooldowns (user_id) VALUES (" + userId + ")");
        database.write("INSERT INTO " + tablePrefix + "skills (user_id) VALUES (" + userId + ")");
        database.write("INSERT INTO " + tablePrefix + "experience (user_id) VALUES (" + userId + ")");
    }

    public boolean load() {
        try {
            //Open the user file
            FileReader file = new FileReader(location);
            BufferedReader in = new BufferedReader(file);
            String line = "";

            while ((line = in.readLine()) != null) {
                //Find if the line contains the player we want.
                String[] character = line.split(":");

                if (!character[0].equals(playerName)) {
                    continue;
                }

                if (character.length > 1 && Misc.isInt(character[1]))
                    skills.put(SkillType.MINING, Integer.valueOf(character[1]));
                if (character.length > 4 && Misc.isInt(character[4]))
                    skillsXp.put(SkillType.MINING, Integer.valueOf(character[4]));
                if (character.length > 5 && Misc.isInt(character[5]))
                    skills.put(SkillType.WOODCUTTING, Integer.valueOf(character[5]));
                if (character.length > 6 && Misc.isInt(character[6]))
                    skillsXp.put(SkillType.WOODCUTTING, Integer.valueOf(character[6]));
                if (character.length > 7 && Misc.isInt(character[7]))
                    skills.put(SkillType.REPAIR, Integer.valueOf(character[7]));
                if (character.length > 8 && Misc.isInt(character[8]))
                    skills.put(SkillType.UNARMED,  Integer.valueOf(character[8]));
                if (character.length > 9 && Misc.isInt(character[9]))
                    skills.put(SkillType.HERBALISM, Integer.valueOf(character[9]));
                if (character.length > 10 && Misc.isInt(character[10]))
                    skills.put(SkillType.EXCAVATION, Integer.valueOf(character[10]));
                if (character.length > 11 && Misc.isInt(character[11]))
                    skills.put(SkillType.ARCHERY, Integer.valueOf(character[11]));
                if (character.length > 12 && Misc.isInt(character[12]))
                    skills.put(SkillType.SWORDS, Integer.valueOf(character[12]));
                if (character.length > 13 && Misc.isInt(character[13]))
                    skills.put(SkillType.AXES, Integer.valueOf(character[13]));
                if (character.length > 14 && Misc.isInt(character[14]))
                    skills.put(SkillType.ACROBATICS, Integer.valueOf(character[14]));
                if (character.length > 15 && Misc.isInt(character[15]))
                    skillsXp.put(SkillType.REPAIR, Integer.valueOf(character[15]));
                if (character.length > 16 && Misc.isInt(character[16]))
                    skillsXp.put(SkillType.UNARMED, Integer.valueOf(character[16]));
                if (character.length > 17 && Misc.isInt(character[17]))
                    skillsXp.put(SkillType.HERBALISM, Integer.valueOf(character[17]));
                if (character.length > 18 && Misc.isInt(character[18]))
                    skillsXp.put(SkillType.EXCAVATION, Integer.valueOf(character[18]));
                if (character.length > 19 && Misc.isInt(character[19]))
                    skillsXp.put(SkillType.ARCHERY, Integer.valueOf(character[19]));
                if (character.length > 20 && Misc.isInt(character[20]))
                    skillsXp.put(SkillType.SWORDS, Integer.valueOf(character[20]));
                if (character.length > 21 && Misc.isInt(character[21]))
                    skillsXp.put(SkillType.AXES, Integer.valueOf(character[21]));
                if (character.length > 22 && Misc.isInt(character[22]))
                    skillsXp.put(SkillType.ACROBATICS, Integer.valueOf(character[22]));
                if (character.length > 24 && Misc.isInt(character[24]))
                    skills.put(SkillType.TAMING, Integer.valueOf(character[24]));
                if (character.length > 25 && Misc.isInt(character[25]))
                    skillsXp.put(SkillType.TAMING, Integer.valueOf(character[25]));
                if (character.length > 26)
                    skillsDATS.put(AbilityType.BERSERK, Integer.valueOf(character[26]));
                if (character.length > 27)
                    skillsDATS.put(AbilityType.GIGA_DRILL_BREAKER, Integer.valueOf(character[27]));
                if (character.length > 28)
                    skillsDATS.put(AbilityType.TREE_FELLER, Integer.valueOf(character[28]));
                if (character.length > 29)
                    skillsDATS.put(AbilityType.GREEN_TERRA, Integer.valueOf(character[29]));
                if (character.length > 30)
                    skillsDATS.put(AbilityType.SERRATED_STRIKES, Integer.valueOf(character[30]));
                if (character.length > 31)
                    skillsDATS.put(AbilityType.SKULL_SPLIITER, Integer.valueOf(character[31]));
                if (character.length > 32)
                    skillsDATS.put(AbilityType.SUPER_BREAKER, Integer.valueOf(character[32]));
                if (character.length > 33) {
                    for (HudType type : HudType.values()) {
                        if (type.toString().equalsIgnoreCase(character[33])) {
                            hudType = type;
                        }
                    }
                }
                if (character.length > 34)
                    skills.put(SkillType.FISHING, Integer.valueOf(character[34]));
                if (character.length > 35)
                    skillsXp.put(SkillType.FISHING, Integer.valueOf(character[35]));
                if (character.length > 36)
                    skillsDATS.put(AbilityType.BLAST_MINING, Integer.valueOf(character[36]));
 
                loaded = true;

                in.close();
                return true;
            }

            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void save() {
        Long timestamp = System.currentTimeMillis() / 1000;

        // if we are using mysql save to database
        if (Config.getInstance().getUseMySQL()) {
            Database database = mcMMO.getPlayerDatabase();
            String tablePrefix = Config.getInstance().getMySQLTablePrefix();

            database.write("UPDATE " + tablePrefix + "huds SET hudtype = '" + hudType.toString() + "' WHERE user_id = " + userId);
            database.write("UPDATE " + tablePrefix + "users SET lastlogin = " + timestamp.intValue() + " WHERE id = " + userId);
            database.write("UPDATE " + tablePrefix + "cooldowns SET "
                    + " mining = " + skillsDATS.get(AbilityType.SUPER_BREAKER)
                    + ", woodcutting = " + skillsDATS.get(AbilityType.TREE_FELLER)
                    + ", unarmed = " + skillsDATS.get(AbilityType.BERSERK)
                    + ", herbalism = " + skillsDATS.get(AbilityType.GREEN_TERRA)
                    + ", excavation = " + skillsDATS.get(AbilityType.GIGA_DRILL_BREAKER)
                    + ", swords = " + skillsDATS.get(AbilityType.SERRATED_STRIKES)
                    + ", axes = " + skillsDATS.get(AbilityType.SKULL_SPLIITER)
                    + ", blast_mining = " + skillsDATS.get(AbilityType.BLAST_MINING)
                    + " WHERE user_id = " + userId);
            database.write("UPDATE " + tablePrefix + "skills SET "
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
            database.write("UPDATE " + tablePrefix + "experience SET "
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
            // otherwise save to flatfile
            try {
                //Open the file
                FileReader file = new FileReader(location);
                BufferedReader in = new BufferedReader(file);
                StringBuilder writer = new StringBuilder();
                String line = "";

                //While not at the end of the file
                while ((line = in.readLine()) != null) {
                    //Read the line in and copy it to the output it's not the player
                    //we want to edit
                    if (!line.split(":")[0].equalsIgnoreCase(playerName)) {
                        writer.append(line).append("\r\n");
                    }
                    else {
                      //Otherwise write the new player information
                        writer.append(playerName + ":");
                        writer.append(skills.get(SkillType.MINING) + ":");
                        writer.append("" + ":");
                        writer.append("" + ":");
                        writer.append(skillsXp.get(SkillType.MINING) + ":");
                        writer.append(skills.get(SkillType.WOODCUTTING) + ":");
                        writer.append(skillsXp.get(SkillType.WOODCUTTING) + ":");
                        writer.append(skills.get(SkillType.REPAIR) + ":");
                        writer.append(skills.get(SkillType.UNARMED) + ":");
                        writer.append(skills.get(SkillType.HERBALISM) + ":");
                        writer.append(skills.get(SkillType.EXCAVATION) + ":");
                        writer.append(skills.get(SkillType.ARCHERY) + ":");
                        writer.append(skills.get(SkillType.SWORDS) + ":");
                        writer.append(skills.get(SkillType.AXES) + ":");
                        writer.append(skills.get(SkillType.ACROBATICS) + ":");
                        writer.append(skillsXp.get(SkillType.REPAIR) + ":");
                        writer.append(skillsXp.get(SkillType.UNARMED) + ":");
                        writer.append(skillsXp.get(SkillType.HERBALISM) + ":");
                        writer.append(skillsXp.get(SkillType.EXCAVATION) + ":");
                        writer.append(skillsXp.get(SkillType.ARCHERY) + ":");
                        writer.append(skillsXp.get(SkillType.SWORDS) + ":");
                        writer.append(skillsXp.get(SkillType.AXES) + ":");
                        writer.append(skillsXp.get(SkillType.ACROBATICS) + ":");
                        writer.append("" + ":");
                        writer.append(skills.get(SkillType.TAMING) + ":");
                        writer.append(skillsXp.get(SkillType.TAMING) + ":");
                        //Need to store the DATS of abilities nao
                        //Berserk, Gigadrillbreaker, Tree Feller, Green Terra, Serrated Strikes, Skull Splitter, Super Breaker
                        writer.append(String.valueOf(skillsDATS.get(AbilityType.BERSERK)) + ":");
                        writer.append(String.valueOf(skillsDATS.get(AbilityType.GIGA_DRILL_BREAKER)) + ":");
                        writer.append(String.valueOf(skillsDATS.get(AbilityType.TREE_FELLER)) + ":");
                        writer.append(String.valueOf(skillsDATS.get(AbilityType.GREEN_TERRA)) + ":");
                        writer.append(String.valueOf(skillsDATS.get(AbilityType.SERRATED_STRIKES)) + ":");
                        writer.append(String.valueOf(skillsDATS.get(AbilityType.SKULL_SPLIITER)) + ":");
                        writer.append(String.valueOf(skillsDATS.get(AbilityType.SUPER_BREAKER)) + ":");
                        writer.append(hudType.toString() + ":");
                        writer.append(skills.get(SkillType.FISHING) + ":");
                        writer.append(skillsXp.get(SkillType.FISHING) + ":");
                        writer.append(String.valueOf(skillsDATS.get(AbilityType.BLAST_MINING)) + ":");
                        writer.append("\r\n");
                    }
                }

                in.close();
                //Write the new file
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
            //Open the file to write the player
            FileWriter file = new FileWriter(location, true);
            BufferedWriter out = new BufferedWriter(file);

            //Add the player to the end
            out.append(playerName + ":");
            out.append(0 + ":"); //mining
            out.append("" + ":");
            out.append("" + ":");
            out.append(0 + ":"); //XP
            out.append(0 + ":"); //woodcutting
            out.append(0 + ":"); //woodCuttingXP
            out.append(0 + ":"); //repair
            out.append(0 + ":"); //unarmed
            out.append(0 + ":"); //herbalism
            out.append(0 + ":"); //excavation
            out.append(0 + ":"); //archery
            out.append(0 + ":"); //swords
            out.append(0 + ":"); //axes
            out.append(0 + ":"); //acrobatics
            out.append(0 + ":"); //repairXP
            out.append(0 + ":"); //unarmedXP
            out.append(0 + ":"); //herbalismXP
            out.append(0 + ":"); //excavationXP
            out.append(0 + ":"); //archeryXP
            out.append(0 + ":"); //swordsXP
            out.append(0 + ":"); //axesXP
            out.append(0 + ":"); //acrobaticsXP
            out.append("" + ":");
            out.append(0 + ":"); //taming
            out.append(0 + ":"); //tamingXP
            out.append(0 + ":"); //DATS
            out.append(0 + ":"); //DATS
            out.append(0 + ":"); //DATS
            out.append(0 + ":"); //DATS
            out.append(0 + ":"); //DATS
            out.append(0 + ":"); //DATS
            out.append(0 + ":"); //DATS
            out.append(SpoutConfig.getInstance().defaultHudType.toString() + ":");//HUD
            out.append(0 + ":"); //Fishing
            out.append(0 +":"); //FishingXP
            out.append(0 + ":"); //Blast Mining

            //Add more in the same format as the line above

            out.newLine();
            out.close();
        } catch (Exception e) {
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
     * God Mode
     */

    public boolean getGodMode() {
        return godMode;
    }

    public void toggleGodMode() {
        godMode = !godMode;
    }

    /*
     * Anvil Placement
     */

    public void togglePlacedAnvil() {
        placedAnvil = !placedAnvil;
    }

    public Boolean getPlacedAnvil() {
        return placedAnvil;
    }

    /*
     * HUD Stuff
     */

    public HudType getHudType() {
        return hudType;
    }

    public SpoutHud getSpoutHud() {
        return spoutHud;
    }

    public void setSpoutHud(SpoutHud spoutHud) {
        this.spoutHud = spoutHud;
    }

    public void setHudType(HudType hudType) {
        this.hudType = hudType;
    }

    /*
     * Chat Stuff
     */

    public boolean getAdminChatMode() {
        return adminChatMode;
    }

    public void toggleAdminChat() {
        adminChatMode = !adminChatMode;
    }

    public boolean getPartyChatMode() {
        return partyChatMode;
    }

    public void togglePartyChat() {
        partyChatMode = !partyChatMode;
    }

    /*
     * Tools
     */

    /**
     * Reset the prep modes of all tools.
     */
    public void resetToolPrepMode() {
        for (ToolType tool : ToolType.values()) {
            setToolPreparationMode(tool, false);
        }
    }

    /**
     * Get the current prep mode of a tool.
     *
     * @param tool Tool to get the mode for
     * @return true if the tool is prepped, false otherwise
     */
    public boolean getToolPreparationMode(ToolType tool) {
        switch (tool) {
        case AXE:
            return axePreparationMode;

        case FISTS:
            return fistsPreparationMode;

        case HOE:
            return hoePreparationMode;

        case PICKAXE:
            return pickaxePreparationMode;

        case SHOVEL:
            return shovelPreparationMode;

        case SWORD:
            return swordsPreparationMode;

        default:
            return false;
        }
    }

    /**
     * Set the current prep mode of a tool.
     *
     * @param tool Tool to set the mode for
     * @param bool true if the tool should be prepped, false otherwise
     */
    public void setToolPreparationMode(ToolType tool, boolean bool) {
        switch (tool) {
        case AXE:
            axePreparationMode = bool;
            break;

        case FISTS:
            fistsPreparationMode = bool;
            break;

        case HOE:
            hoePreparationMode = bool;
            break;

        case PICKAXE:
            pickaxePreparationMode = bool;
            break;

        case SHOVEL:
            shovelPreparationMode = bool;
            break;

        case SWORD:
            swordsPreparationMode = bool;
            break;

        default:
            break;
        }
    }

    /**
     * Get the current prep ATS of a tool.
     *
     * @param tool Tool to get the ATS for
     * @return the ATS for the tool
     */
    public long getToolPreparationATS(ToolType tool) {
        return toolATS.get(tool);
    }

    /**
     * Set the current prep ATS of a tool.
     *
     * @param tool Tool to set the ATS for
     * @param ATS the ATS of the tool
     */
    public void setToolPreparationATS(ToolType tool, long ATS) {
        int startTime = (int) (ATS / 1000);
        toolATS.put(tool, startTime);
    }

    /*
     * Abilities
     */

    /**
     * Reset the prep modes of all tools.
     */
    public void resetAbilityMode() {
        for (AbilityType ability : AbilityType.values()) {
            setAbilityMode(ability, false);
        }
    }

    /**
     * Get the mode of an ability.
     *
     * @param ability The ability to check
     * @return true if the ability is enabled, false otherwise
     */
    public boolean getAbilityMode(AbilityType ability) {
        switch (ability) {
        case BERSERK:
            return berserkMode;

        case SUPER_BREAKER:
            return superBreakerMode;

        case GIGA_DRILL_BREAKER:
            return gigaDrillBreakerMode;

        case GREEN_TERRA:
            return greenTerraMode;

        case SKULL_SPLIITER:
            return skullSplitterMode;

        case TREE_FELLER:
            return treeFellerMode;

        case SERRATED_STRIKES:
            return serratedStrikesMode;

        default:
            return false;
        }
    }

    /**
     * Set the mode of an ability.
     *
     * @param ability The ability to check
     * @param bool True if the ability is active, false otherwise
     */
    public void setAbilityMode(AbilityType ability, boolean bool) {
        switch (ability) {
        case BERSERK:
            berserkMode = bool;
            break;

        case SUPER_BREAKER:
            superBreakerMode = bool;
            break;

        case GIGA_DRILL_BREAKER:
            gigaDrillBreakerMode = bool;
            break;

        case GREEN_TERRA:
            greenTerraMode = bool;
            break;

        case SKULL_SPLIITER:
            skullSplitterMode = bool;
            break;

        case TREE_FELLER:
            treeFellerMode = bool;
            break;

        case SERRATED_STRIKES:
            serratedStrikesMode = bool;
            break;

        default:
            break;
        }
    }

    /**
     * Get the informed state of an ability
     *
     * @param ability The ability to check
     * @return true if the ability is informed, false otherwise
     */
    public boolean getAbilityInformed(AbilityType ability) {
        switch (ability) {
        case BERSERK:
            return berserkInformed;

        case BLAST_MINING:
            return blastMiningInformed;

        case SUPER_BREAKER:
            return superBreakerInformed;

        case GIGA_DRILL_BREAKER:
            return gigaDrillBreakerInformed;

        case GREEN_TERRA:
            return greenTerraInformed;

        case SKULL_SPLIITER:
            return skullSplitterInformed;

        case TREE_FELLER:
            return treeFellerInformed;

        case SERRATED_STRIKES:
            return serratedStrikesInformed;

        default:
            return false;
        }
    }

    /**
     * Set the informed state of an ability.
     *
     * @param ability The ability to check
     * @param bool True if the ability is informed, false otherwise
     */
    public void setAbilityInformed(AbilityType ability, boolean bool) {
        switch (ability) {
        case BERSERK:
            berserkInformed = bool;
            break;

        case BLAST_MINING:
            blastMiningInformed = bool;
            break;

        case SUPER_BREAKER:
            superBreakerInformed = bool;
            break;

        case GIGA_DRILL_BREAKER:
            gigaDrillBreakerInformed = bool;
            break;

        case GREEN_TERRA:
            greenTerraInformed = bool;
            break;

        case SKULL_SPLIITER:
            skullSplitterInformed = bool;
            break;

        case TREE_FELLER:
            treeFellerInformed = bool;
            break;

        case SERRATED_STRIKES:
            serratedStrikesInformed = bool;
            break;

        default:
            break;
        }
    }

    public boolean getAbilityUse() {
        return abilityUse;
    }

    public void toggleAbilityUse() {
        abilityUse = !abilityUse;
    }

    /*
     * Recently Hurt
     */

    public int getRecentlyHurt() {
        return recentlyHurt;
    }

    public void setRecentlyHurt(int value) {
        recentlyHurt = value;
    }

    public void actualizeRecentlyHurt() {
        respawnATS = (int) (System.currentTimeMillis() / 1000);
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
    * Exploit Prevention
    */

    public int getRespawnATS() {
        return respawnATS;
    }

    public void actualizeRespawnATS() {
        respawnATS = (int) (System.currentTimeMillis() / 1000);
    }

    /*
     * XP Functions
     */

    public int getSkillLevel(SkillType skillType) {
        return skills.get(skillType);
    }

    public int getSkillXpLevel(SkillType skillType) {
        return skillsXp.get(skillType);
    }

    public void setSkillXPLevel(SkillType skillType, int newValue) {
        skillsXp.put(skillType, newValue);
    }

    public void skillUp(SkillType skillType, int newValue) {
        skills.put(skillType, skills.get(skillType) + newValue);
    }

//    /**
//     * Adds XP to the player, doesn't calculate for XP Rate
//     *
//     * @param skillType The skill to add XP to
//     * @param newValue The amount of XP to add
//     */
//    public void addXPOverride(SkillType skillType, int newValue) {
//        if (skillType.equals(SkillType.ALL)) {
//            for (SkillType x : SkillType.values()) {
//                if (x.equals(SkillType.ALL)) {
//                    continue;
//                }
//
//                mcMMO.p.getServer().getPluginManager().callEvent(new McMMOPlayerXpGainEvent(player, x, newValue));
//                skillsXp.put(x, skillsXp.get(x) + newValue);
//            }
//        }
//        else {
//            mcMMO.p.getServer().getPluginManager().callEvent(new McMMOPlayerXpGainEvent(player, skillType, newValue));
//            skillsXp.put(skillType, skillsXp.get(skillType) + newValue);
//            spoutHud.setLastGained(skillType);
//        }
//    }

//    /**
//     * Adds XP to the player, this ignores skill modifiers.
//     *
//     * @param skillType The skill to add XP to
//     * @param newValue The amount of XP to add
//     */
//    public void addXPOverrideBonus(SkillType skillType, int newValue) {
//        int xp = newValue * Config.getInstance().xpGainMultiplier;
//        addXPOverride(skillType, xp);
//    }

//    /**
//     * Adds XP to the player, this is affected by skill modifiers and XP Rate and Permissions
//     *
//     * @param skillType The skill to add XP to
//     * @param newvalue The amount of XP to add
//     */
//    public void addXP(SkillType skillType, int newValue) {
//        if (player.getGameMode().equals(GameMode.CREATIVE)) {
//            return;
//        }
//
//        double bonusModifier = 0;
//
//        if (inParty()) {
//            bonusModifier = partyModifier(skillType);
//        }
//
//        int xp = (int) (newValue / skillType.getXpModifier()) * Config.getInstance().xpGainMultiplier;
//
//        if (bonusModifier > 0) {
//            if (bonusModifier >= 2) {
//                bonusModifier = 2;
//            }
//
//            double trueBonus = bonusModifier * xp;
//            xp += trueBonus;
//        }
//
//        if (Config.getInstance().getToolModsEnabled()) {
//            ItemStack item = player.getItemInHand();
//            CustomTool tool = ModChecks.getToolFromItemStack(item);
//
//            if (tool != null) {
//                xp = (int) (xp * tool.getXpMultiplier());
//            }
//        }
//
//        //TODO: Can we make this so we do perks by doing "mcmmo.perks.xp.[multiplier]" ?
//        if (player.hasPermission("mcmmo.perks.xp.quadruple")) {
//            xp = xp * 4;
//        }
//        else if (player.hasPermission("mcmmo.perks.xp.triple")) {
//            xp = xp * 3;
//        }
//        else if (player.hasPermission("mcmmo.perks.xp.150percentboost")) {
//            xp = (int) (xp * 2.5);
//        }
//        else if (player.hasPermission("mcmmo.perks.xp.double")) {
//            xp = xp * 2;
//        }
//        else if (player.hasPermission("mcmmo.perks.xp.50percentboost")) {
//            xp = (int) (xp * 1.5);
//        }
//
//        mcMMO.p.getServer().getPluginManager().callEvent(new McMMOPlayerXpGainEvent(player, skillType, xp));
//        skillsXp.put(skillType, skillsXp.get(skillType) + xp);
//        spoutHud.setLastGained(skillType);
//    }

    /**
     * Remove XP from a skill.
     *
     * @param skillType Type of skill to modify
     * @param xp Amount of xp to remove
     */
    public void removeXP(SkillType skillType, int xp) {
        if (skillType.equals(SkillType.ALL)) {
            for (SkillType skill : SkillType.values()) {
                if (skill.equals(SkillType.ALL)) {
                    continue;
                }

                skillsXp.put(skill, skillsXp.get(skill) - xp);
            }
        }
        else {
            skillsXp.put(skillType, skillsXp.get(skillType) - xp);
        }
    }

    /**
     * Modify a skill level.
     *
     * @param skillType Type of skill to modify
     * @param newValue New level value for the skill
     */
    public void modifySkill(SkillType skillType, int newValue) {
        if (skillType.equals(SkillType.ALL)) {
            for (SkillType skill : SkillType.values()) {
                if (skill.equals(SkillType.ALL)) {
                    continue;
                }

                skills.put(skill, newValue);
                skillsXp.put(skill, 0);
            }
        }
        else {
            skills.put(skillType, newValue);
            skillsXp.put(skillType, 0);
        }
    }

    /**
     * Add levels to a skill.
     *
     * @param skillType Type of skill to add levels to
     * @param levels Number of levels to add
     */
    public void addLevels(SkillType skillType, int levels) {
        if (skillType.equals(SkillType.ALL)) {
            for (SkillType skill : SkillType.values()) {
                if (skill.equals(SkillType.ALL)) {
                    continue;
                }

                skills.put(skill, skills.get(skill) + levels);
                skillsXp.put(skill, 0);
            }
        }
        else {
            skills.put(skillType, skills.get(skillType) + levels);
            skillsXp.put(skillType, 0);
        }
    }

    /**
     * Get the amount of XP remaining before the next level.
     *
     * @param skillType Type of skill to check
     * @return the XP remaining until next level
     */
    public int getXpToLevel(SkillType skillType) {
        return 1020 + (skills.get(skillType) *  Config.getInstance().getFormulaMultiplierCurve());
    }

//    /**
//     * Gets the power level of a player.
//     *
//     * @return the power level of the player
//     */
//    public int getPowerLevel() {
//        int powerLevel = 0;
//
//        for (SkillType type : SkillType.values()) {
//            if (type.getPermissions(player)) {
//                powerLevel += getSkillLevel(type);
//            }
//        }
//
//        return powerLevel;
//    }

//    /**
//     * Calculate the party XP modifier.
//     *
//     * @param skillType Type of skill to check
//     * @return the party bonus multiplier
//     */
//    private double partyModifier(SkillType skillType) {
//        double bonusModifier = 0.0;
//
//        for (Player member : party.getOnlineMembers()) {
//            if (party.getLeader().equals(member.getName())) {
//                if (Misc.isNear(player.getLocation(), member.getLocation(), 25.0)) {
//                    PlayerProfile PartyLeader = Users.getProfile(member);
//                    int leaderSkill = PartyLeader.getSkillLevel(skillType);
//                    int playerSkill = getSkillLevel(skillType);
//
//                    if (leaderSkill >= playerSkill) {
//                        int difference = leaderSkill - playerSkill;
//                        bonusModifier = (difference * 0.75) / 100.0;
//                    }
//                }
//            }
//        }
//
//        return bonusModifier;
//    }

    /*
     * Party Stuff
     */

    public void setInvite(Party invite) {
        this.invite = invite;
    }

    public Party getInvite() {
        return invite;
    }

    public boolean hasPartyInvite() {
        if (invite != null) {
            return true;
        }
        else {
            return false;
        }
    }

    public void setParty(Party party) {
        this.party = party;
    }

    public Party getParty() {
        return party;
    }

    public boolean inParty() {
        if (party != null) {
            return true;
        }
        else {
            return false;
        }
    }

    public void removeParty() {
        party = null;
    }

    public void removeInvite() {
        invite = null;
    }
}
