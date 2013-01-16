package com.gmail.nossr50.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.datatypes.PlayerStat;
import com.gmail.nossr50.datatypes.SkillType;

public class Leaderboard {
    private static mcMMO plugin = mcMMO.p;
    private final static String location = mcMMO.getUsersFile();
    private static HashMap<SkillType, List<PlayerStat>> playerStatHash = new HashMap<SkillType, List<PlayerStat>>();
    private static long lastUpdate = 0;
    
    /**
     * Update the leader boards.
     */
    public static void updateLeaderboards() {
    	if(System.currentTimeMillis() < lastUpdate + 600000) {
    		return; //Only update FFS leaderboards every 10 minutes.. this puts a lot of strain on the server (depending on the size of the database) and should not be done frequently
    	}
    	
    	lastUpdate = System.currentTimeMillis(); //Log when the last update was run
    	
        //Initialize lists
        List<PlayerStat> mining, woodcutting, herbalism, excavation, acrobatics, repair, swords, axes, archery, unarmed, taming, fishing, powerlevel;

        mining = new ArrayList<PlayerStat>();
        woodcutting = new ArrayList<PlayerStat>();
        herbalism = new ArrayList<PlayerStat>();
        excavation = new ArrayList<PlayerStat>();
        acrobatics = new ArrayList<PlayerStat>();
        repair = new ArrayList<PlayerStat>();
        swords = new ArrayList<PlayerStat>();
        axes = new ArrayList<PlayerStat>();
        archery = new ArrayList<PlayerStat>();
        unarmed = new ArrayList<PlayerStat>();
        taming = new ArrayList<PlayerStat>();
        fishing = new ArrayList<PlayerStat>();
        powerlevel = new ArrayList<PlayerStat>();

        //Read from the FlatFile database and fill our arrays with information
        try {
            FileReader file = new FileReader(location);
            BufferedReader in = new BufferedReader(file);
            String line = "";
            ArrayList<String> players = new ArrayList<String>();

            while ((line = in.readLine()) != null) {
                String[] character = line.split(":");

                String p = character[0];
                int powerLevel = 0;

                //Prevent the same player from being added multiple times (I'd like to note that this shouldn't happen...)
                if (players.contains(p)) {
                    continue;
                }

                players.add(p);

                if (character.length > 1 && Misc.isInt(character[1])) {
                    mining.add(new PlayerStat(p, Integer.valueOf(character[1])));
                    powerLevel += Integer.valueOf(character[1]);
                }

                if (character.length > 5 && Misc.isInt(character[5])) {
                    woodcutting.add(new PlayerStat(p, Integer.valueOf(character[5])));
                    powerLevel += Integer.valueOf(character[5]);
                }

                if (character.length > 7 && Misc.isInt(character[7])) {
                    repair.add(new PlayerStat(p, Integer.valueOf(character[7])));
                    powerLevel += Integer.valueOf(character[7]);
                }

                if (character.length > 8 && Misc.isInt(character[8])) {
                    unarmed.add(new PlayerStat(p, Integer.valueOf(character[8])));
                    powerLevel += Integer.valueOf(character[8]);
                }

                if (character.length > 9 && Misc.isInt(character[9])) {
                    herbalism.add(new PlayerStat(p, Integer.valueOf(character[9])));
                    powerLevel += Integer.valueOf(character[9]);
                }

                if (character.length > 10 && Misc.isInt(character[10])) {
                    excavation.add(new PlayerStat(p, Integer.valueOf(character[10])));
                    powerLevel += Integer.valueOf(character[10]);
                }

                if (character.length > 11 && Misc.isInt(character[11])) {
                    archery.add(new PlayerStat(p, Integer.valueOf(character[11])));
                    powerLevel += Integer.valueOf(character[11]);
                }

                if (character.length > 12 && Misc.isInt(character[12])) {
                    swords.add(new PlayerStat(p, Integer.valueOf(character[12])));
                    powerLevel += Integer.valueOf(character[12]);
                }

                if (character.length > 13 && Misc.isInt(character[13])) {
                    axes.add(new PlayerStat(p, Integer.valueOf(character[13])));
                    powerLevel += Integer.valueOf(character[13]);
                }

                if (character.length > 14 && Misc.isInt(character[14])) {
                    acrobatics.add(new PlayerStat(p, Integer.valueOf(character[14])));
                    powerLevel += Integer.valueOf(character[14]);
                }

                if (character.length > 24 && Misc.isInt(character[24])) {
                    taming.add(new PlayerStat(p, Integer.valueOf(character[24])));
                    powerLevel += Integer.valueOf(character[24]);
                }

                if (character.length > 34 && Misc.isInt(character[34])) {
                    fishing.add(new PlayerStat(p, Integer.valueOf(character[34])));
                    powerLevel += Integer.valueOf(character[34]);
                }

                powerlevel.add(new PlayerStat(p, powerLevel));
            }
            in.close();
        }
        catch (Exception e) {
            plugin.getLogger().severe(("Exception while reading " + location + " (Are you sure you formatted it correctly?)" + e.toString()));
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
        Collections.sort(powerlevel, c);
        
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
        playerStatHash.put(SkillType.ALL, powerlevel);
    }

    /**
     * Retrieve leaderboard info.
     *
     * @param skillName Skill to retrieve info on.
     * @param pagenumber Which page in the leaderboards to retrieve
     * @return the requested leaderboard information
     */
    public static String[] retrieveInfo(SkillType skillType, int pagenumber) {
    	String[] info = new String[10];

        List<PlayerStat> statsList = playerStatHash.get(skillType);
        
        if(statsList != null) {
        	int destination;
        	
        	//How many lines to skip through
            if (pagenumber == 1) {
                destination = 0;
            }
            else {
                destination = (pagenumber * 10) - 9;
            }
            
        	int currentPos = 0;
        
	    	for(PlayerStat ps : statsList) {
	    		if(currentPos == 10)
	    			break;
	    		if(destination > 1) {
	    			destination--;
	    			continue;
	    		}
	    		
	    		info[currentPos] = ps.name+":"+ps.statVal;
	    		currentPos++;
	    	}
    	
        } else {
        	info[0] = "DummyPlayer:0"; //Coming up with a better solution soon...
        }
        
        return info;
    }
    
    public static int[] getPlayerRank(String playerName, SkillType skillType) {
    	int currentPos = 1;
        List<PlayerStat> statsList = playerStatHash.get(skillType);
        
        if(statsList != null) {
        	for(PlayerStat ps : statsList) {
        		if(ps.name.equalsIgnoreCase(playerName)) {
        			return new int[] {currentPos, ps.statVal};
        		} else {
        			currentPos++;
        			continue;
        		}
        	}
        	
        	return new int[] {0};
        } else {
        	return new int[] {0};
        }
    }
    
    private static class SkillComparator implements Comparator<PlayerStat> {
        @Override
        public int compare(PlayerStat o1, PlayerStat o2) {
            return (o2.statVal - o1.statVal);
        }
    }
}
