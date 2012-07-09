package com.gmail.nossr50.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.PlayerStat;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.datatypes.Tree;

public class Leaderboard {
    private static mcMMO plugin = mcMMO.p;
    private static String leaderboardsDirectory = mcMMO.getLeaderboardDirectory();
    private final static String location = mcMMO.getUsersFile();

    /**
     * Create the leaderboards.
     */
    public static void makeLeaderboards() {
        //Make Trees
        Tree Mining = new Tree();
        Tree WoodCutting = new Tree();
        Tree Herbalism = new Tree();
        Tree Excavation = new Tree();
        Tree Acrobatics = new Tree();
        Tree Repair = new Tree();
        Tree Swords = new Tree();
        Tree Axes = new Tree();
        Tree Archery = new Tree();
        Tree Unarmed = new Tree();
        Tree Taming = new Tree();
        Tree Fishing = new Tree();
        Tree PowerLevel = new Tree();

        //Add Data To Trees
        try {
            FileReader file = new FileReader(location);
            BufferedReader in = new BufferedReader(file);
            String line = "";
            ArrayList<String> players = new ArrayList<String>();

            while ((line = in.readLine()) != null) {
                String[] character = line.split(":");
                String p = character[0];
                int powerLevel = 0;

                //Prevent the same player from being added multiple times
                if (players.contains(p)) {
                    continue;
                }
                else {
                    players.add(p);
                }

                if (character.length > 1 && Misc.isInt(character[1])) {
                    Mining.add(p, Integer.valueOf(character[1]));
                    powerLevel += Integer.valueOf(character[1]);
                }

                if (character.length > 5 && Misc.isInt(character[5])) {
                    WoodCutting.add(p, Integer.valueOf(character[5]));
                    powerLevel += Integer.valueOf(character[5]);
                }

                if (character.length > 7 && Misc.isInt(character[7])) {
                    Repair.add(p, Integer.valueOf(character[7]));
                    powerLevel += Integer.valueOf(character[7]);
                }

                if (character.length > 8 && Misc.isInt(character[8])) {
                    Unarmed.add(p, Integer.valueOf(character[8]));
                    powerLevel += Integer.valueOf(character[8]);
                }

                if (character.length > 9 && Misc.isInt(character[9])) {
                    Herbalism.add(p, Integer.valueOf(character[9]));
                    powerLevel += Integer.valueOf(character[9]);
                }

                if (character.length > 10 && Misc.isInt(character[10])) {
                    Excavation.add(p, Integer.valueOf(character[10]));
                    powerLevel += Integer.valueOf(character[10]);
                }

                if (character.length > 11 && Misc.isInt(character[11])) {
                    Archery.add(p, Integer.valueOf(character[11]));
                    powerLevel += Integer.valueOf(character[11]);
                }

                if (character.length > 12 && Misc.isInt(character[12])) {
                    Swords.add(p, Integer.valueOf(character[12]));
                    powerLevel += Integer.valueOf(character[12]);
                }

                if (character.length > 13 && Misc.isInt(character[13])) {
                    Axes.add(p, Integer.valueOf(character[13]));
                    powerLevel += Integer.valueOf(character[13]);
                }

                if (character.length > 14 && Misc.isInt(character[14])) {
                    Acrobatics.add(p, Integer.valueOf(character[14]));
                    powerLevel += Integer.valueOf(character[14]);
                }

                if (character.length > 24 && Misc.isInt(character[24])) {
                    Taming.add(p, Integer.valueOf(character[24]));
                    powerLevel += Integer.valueOf(character[24]);
                }

                if (character.length > 34 && Misc.isInt(character[34])) {
                    Fishing.add(p, Integer.valueOf(character[34]));
                    powerLevel += Integer.valueOf(character[34]);
                }

                PowerLevel.add(p, powerLevel);
            }
            in.close();
        }
        catch (Exception e) {
            plugin.getLogger().severe(("Exception while reading " + location + " (Are you sure you formatted it correctly?)" + e.toString()));
        }

        //Write the leader board files
        leaderWrite(Mining.inOrder(), SkillType.MINING);
        leaderWrite(WoodCutting.inOrder(), SkillType.WOODCUTTING);
        leaderWrite(Repair.inOrder(), SkillType.REPAIR);
        leaderWrite(Unarmed.inOrder(), SkillType.UNARMED);
        leaderWrite(Herbalism.inOrder(), SkillType.HERBALISM);
        leaderWrite(Excavation.inOrder(), SkillType.EXCAVATION);
        leaderWrite(Archery.inOrder(), SkillType.ARCHERY);
        leaderWrite(Swords.inOrder(), SkillType.SWORDS);
        leaderWrite(Axes.inOrder(), SkillType.AXES);
        leaderWrite(Acrobatics.inOrder(), SkillType.ACROBATICS);
        leaderWrite(Taming.inOrder(), SkillType.TAMING);
        leaderWrite(Fishing.inOrder(), SkillType.FISHING);
        leaderWrite(PowerLevel.inOrder(), SkillType.ALL);
    }

    /**
     * Write to the leaderboards.
     *
     * @param ps Stats to write to the leaderboard
     * @param skillType Skill type to write the leaderboard of
     */
    private static void leaderWrite(PlayerStat[] ps, SkillType skillType) {
        String theLocation = leaderboardsDirectory + skillType.toString().toLowerCase() + ".mcmmo";
        File theDir = new File(theLocation);

        //CHECK IF THE FILE EXISTS
        if (!theDir.exists()) {
            FileWriter writer = null;

            try {
                writer = new FileWriter(theLocation);
            }
            catch (Exception e) {
                plugin.getLogger().severe(("Exception while creating " + theLocation + e.toString()));
            }
            finally {
                try {
                    if (writer != null) {
                        writer.close();
                    }
                }
                catch (IOException e) {
                    plugin.getLogger().severe("Exception while closing writer for " + theLocation + e.toString());
                }
            }
        }
        else {
            try {
                FileReader file = new FileReader(theLocation);
                BufferedReader in = new BufferedReader(file);
                StringBuilder writer = new StringBuilder();

                for (PlayerStat p : ps) {
                    if (p.name.equals("$mcMMO_DummyInfo")) {
                        continue;
                    }

                    if (p.statVal == 0) {
                        continue;
                    }

                    writer.append(p.name + ":" + p.statVal);
                    writer.append("\r\n");
                }

                in.close();
                FileWriter out = new FileWriter(theLocation);
                out.write(writer.toString());
                out.close();
            }
            catch (Exception e) {
                plugin.getLogger().severe("Exception while writing to " + theLocation + " (Are you sure you formatted it correctly?)" + e.toString());
            }
        }
    }

    /**
     * Retrieve leaderboard info.
     *
     * @param skillName Skill to retrieve info on.
     * @param pagenumber Which page in the leaderboards to retrieve
     * @return the requested leaderboard information
     */
    public static String[] retrieveInfo(String skillName, int pagenumber) {
        String theLocation = leaderboardsDirectory + skillName.toLowerCase() + ".mcmmo";

        try {
            FileReader file = new FileReader(theLocation);
            BufferedReader in = new BufferedReader(file);
            int destination;

          //How many lines to skip through
            if (pagenumber == 1) {
                destination = 0;
            }
            else {
                destination = (pagenumber * 10) - 9;
            }

            int x = 0; //how many lines we've gone through
            int y = 0; //going through the lines
            String line = "";
            String[] info = new String[10]; //what to return

            while ((line = in.readLine()) != null && y < 10) {
                x++;

                if (x >= destination && y < 10) {
                    info[y] = line.toString();
                    y++;
                }
            }

            in.close();
            return info;
        }
        catch (Exception e) {
            plugin.getLogger().severe("Exception while reading " + theLocation + " (Are you sure you formatted it correctly?)" + e.toString());
        }

        return null; //Shouldn't get here
    }

    /**
     * Update the leaderboards.
     *
     * @param ps Stats to update the leaderboard with.
     * @param skillType Skill whose leaderboard is being updated.
     */
    public static void updateLeaderboard(PlayerStat ps, SkillType skillType) {
        if (Config.getInstance().getUseMySQL()) {
            return;
        }

        String theLocation = leaderboardsDirectory + skillType.toString().toLowerCase() + ".mcmmo";

        try {
            FileReader file = new FileReader(theLocation);
            BufferedReader in = new BufferedReader(file);
            StringBuilder writer = new StringBuilder();
            String line = "";
            Boolean inserted = false;

            while ((line = in.readLine()) != null) {

                //Insert the player into the line before it finds a smaller one
                if (Integer.valueOf(line.split(":")[1]) < ps.statVal && !inserted) {
                    writer.append(ps.name + ":" + ps.statVal).append("\r\n");
                    inserted = true;
                }

                //Write anything that isn't the player already in the file so we remove the duplicate
                if (!line.split(":")[0].equalsIgnoreCase(ps.name)) {
                    writer.append(line).append("\r\n");
                }
            }

            if(!inserted) {
                writer.append(ps.name + ":" + ps.statVal).append("\r\n");
            }

            in.close();

            //Write the new file
            FileWriter out = new FileWriter(theLocation);
            out.write(writer.toString());
            out.close();
        }
        catch (Exception e) {
            plugin.getLogger().severe("Exception while writing to " + theLocation + " (Are you sure you formatted it correctly?)" + e.toString());
        }
    }
}
