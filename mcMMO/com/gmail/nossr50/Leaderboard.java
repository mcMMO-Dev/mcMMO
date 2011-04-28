package com.gmail.nossr50;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.gmail.nossr50.config.LoadProperties;
import com.gmail.nossr50.datatypes.PlayerStat;
import com.gmail.nossr50.datatypes.Tree;

public class Leaderboard {
	static String location = "plugins/mcMMO/mcmmo.users";
	protected static final Logger log = Logger.getLogger("Minecraft");
	
	/*
	 * Read from the file
	 */
	public static void makeLeaderboards(){
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
		Tree PowerLevel = new Tree();
		
		//Add Data To Trees
		try {
        	//Open the user file
        	FileReader file = new FileReader(location);
        	BufferedReader in = new BufferedReader(file);
        	String line = "";
        	while((line = in.readLine()) != null)
        	{
        		
        		String[] character = line.split(":");
        		String p = character[0];


    			int Plvl = 0;
    			
    			if(character.length > 1 && isInt(character[1]))
    			{
    				Mining.add(p, Integer.valueOf(character[1]));
    				Plvl += Integer.valueOf(character[1]);
    			}
    			if(character.length > 5 && isInt(character[5])){
    				WoodCutting.add(p, Integer.valueOf(character[5]));
    				Plvl += Integer.valueOf(character[5]);
    			}
    			if(character.length > 7 && isInt(character[7])){
    				Repair.add(p, Integer.valueOf(character[7]));
    				Plvl += Integer.valueOf(character[7]);
    			}
    			if(character.length > 8 && isInt(character[8])){
    				Unarmed.add(p, Integer.valueOf(character[8]));
    				Plvl += Integer.valueOf(character[8]);
    			}
    			if(character.length > 9 && isInt(character[9])){
    				Herbalism.add(p, Integer.valueOf(character[9]));
    				Plvl += Integer.valueOf(character[9]);
    			}
    			if(character.length > 10 && isInt(character[10])){
    				Excavation.add(p, Integer.valueOf(character[10]));
    				Plvl += Integer.valueOf(character[10]);
    			}
    			if(character.length > 11 && isInt(character[11])){
    				Archery.add(p, Integer.valueOf(character[11]));
    				Plvl += Integer.valueOf(character[11]);
    			}
    			if(character.length > 12 && isInt(character[12])){
    				Swords.add(p, Integer.valueOf(character[12]));
    				Plvl += Integer.valueOf(character[12]);
    			}
    			if(character.length > 13 && isInt(character[13])){
    				Axes.add(p, Integer.valueOf(character[13]));
    				Plvl += Integer.valueOf(character[13]);
    			}
    			if(character.length > 14 && isInt(character[14])){
    				Acrobatics.add(p, Integer.valueOf(character[14]));
    				Plvl += Integer.valueOf(character[14]);
    			}
    			if(character.length > 24 && isInt(character[24])){
    				Taming.add(p, Integer.valueOf(character[24]));
    				Plvl += Integer.valueOf(character[24]);
    			}
    			
    			PowerLevel.add(p, Plvl);
        	}
        	in.close();
        } catch (Exception e) {
            log.log(Level.SEVERE, "Exception while reading "
            		+ location + " (Are you sure you formatted it correctly?)", e);
        }
        //Write the leader board files
        leaderWrite(Mining.inOrder(), "mining");
        leaderWrite(WoodCutting.inOrder(), "woodcutting");
        leaderWrite(Repair.inOrder(), "repair");
        leaderWrite(Unarmed.inOrder(), "unarmed");
        leaderWrite(Herbalism.inOrder(), "herbalism");
        leaderWrite(Excavation.inOrder(), "excavation");
        leaderWrite(Archery.inOrder(), "archery");
        leaderWrite(Swords.inOrder(), "swords");
        leaderWrite(Axes.inOrder(), "axes");
        leaderWrite(Acrobatics.inOrder(), "acrobatics");
        leaderWrite(Taming.inOrder(), "taming");
        leaderWrite(PowerLevel.inOrder(), "powerlevel");
	}
	public static void leaderWrite(PlayerStat[] ps, String statName)
	{
		String theLocation = "plugins/mcMMO/" + statName + ".mcmmo";
		//CHECK IF THE FILE EXISTS
		File theDir = new File(theLocation);
		if(!theDir.exists()){
			//properties = new PropertiesFile(location);
			FileWriter writer = null;
			try {
				writer = new FileWriter(theLocation);
			} catch (Exception e) {
				log.log(Level.SEVERE, "Exception while creating " + theLocation, e);
			} finally {
				try {
					if (writer != null) {
						writer.close();
					}
				} catch (IOException e) {
					log.log(Level.SEVERE, "Exception while closing writer for " + theLocation, e);
				}
			}
		} else {
			try {
	        	FileReader file = new FileReader(theLocation);
	
	    		//HERP
	            BufferedReader in = new BufferedReader(file);
	            StringBuilder writer = new StringBuilder();
	        	String line = "";
	        	for(PlayerStat p : ps)
	        	{
	        		if(p.name.equals("$mcMMO_DummyInfo"))
	        			continue;
	        		writer.append(p.name + ":" + p.statVal);
	        		writer.append("\r\n"); 
	        	}
	        	in.close();
	        	//Write the new file
	            FileWriter out = new FileWriter(theLocation);
	            out.write(writer.toString());
	            out.close();
	        } catch (Exception e) {
	                log.log(Level.SEVERE, "Exception while writing to " + theLocation + " (Are you sure you formatted it correctly?)", e);
	        }
		}
		//Create/open the file
		//Loop through backward writing each player
		//Close the file
	}
	
	public static String[] retrieveInfo(String statName, int pagenumber){
		String theLocation = "plugins/mcMMO/" + statName + ".mcmmo";
		try {
        	FileReader file = new FileReader(theLocation);
        	BufferedReader in = new BufferedReader(file);
        	
        	int destination = (pagenumber - 1) * 10; //How many lines to skip through
        	int x = 0; //how many lines we've gone through
        	int y = 0; //going through the lines
        	String line = "";
        	String[] info = new String[10]; //what to return
        	while((line = in.readLine()) != null && y < 10)
        	{
        		x++;
        		if(x >= destination && y < 10){
        			info[y] = line.toString();
        			y++;
        		}
        	}
        	in.close();
        	return info;
        } catch (Exception e) {
            log.log(Level.SEVERE, "Exception while reading "
            		+ theLocation + " (Are you sure you formatted it correctly?)", e);
        }
        return null; //Shouldn't get here
	}
	public static void updateLeaderboard(PlayerStat ps, String statName){
		if(LoadProperties.useMySQL)
			return;
		String theLocation = "plugins/mcMMO/" + statName + ".mcmmo";
		try {
        	//Open the file
        	FileReader file = new FileReader(theLocation);
            BufferedReader in = new BufferedReader(file);
            StringBuilder writer = new StringBuilder();
        	String line = "";
        	Boolean inserted = false;
        	//While not at the end of the file
        	while((line = in.readLine()) != null)
        	{
        		//Insert the player into the line before it finds a smaller one
        		if(Integer.valueOf(line.split(":")[1]) < ps.statVal && !inserted)
        		{
        			writer.append(ps.name + ":" + ps.statVal).append("\r\n");
        			inserted = true;
        		}
        		//Write anything that isn't the player already in the file so we remove the duplicate
        		if(!line.split(":")[0].equalsIgnoreCase(ps.name))
        		{
                    writer.append(line).append("\r\n");
        		}
        	}
        	
        	if(!inserted)
        	{
    			writer.append(ps.name + ":" + ps.statVal).append("\r\n");
        	}
        	
        	in.close();
        	//Write the new file
            FileWriter out = new FileWriter(theLocation);
            out.write(writer.toString());
            out.close();
        } catch (Exception e) {
                log.log(Level.SEVERE, "Exception while writing to " + theLocation + " (Are you sure you formatted it correctly?)", e);
        }
	}
	public static boolean isInt(String string){
		try {
		    int x = Integer.parseInt(string);
		}
		catch(NumberFormatException nFE) {
		    return false;
		}
		return true;
	}
}
