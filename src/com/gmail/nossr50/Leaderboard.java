/*
	This file is part of mcMMO.

    mcMMO is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    mcMMO is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with mcMMO.  If not, see <http://www.gnu.org/licenses/>.
*/
package com.gmail.nossr50;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.gmail.nossr50.config.LoadProperties;
import com.gmail.nossr50.datatypes.PlayerStat;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.datatypes.Tree;

public class Leaderboard 
{
	static String location = "plugins/mcMMO/FlatFileStuff/mcmmo.users"; //$NON-NLS-1$
	protected static final Logger log = Logger.getLogger("Minecraft"); //$NON-NLS-1$
	
	/*
	 * Read from the file
	 */
	public static void makeLeaderboards()
	{
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
		Tree Enchanting = new Tree();
		Tree Alchemy = new Tree();
		Tree PowerLevel = new Tree();
		
		//Add Data To Trees
		try {
        	//Open the user file
        	FileReader file = new FileReader(location);
        	BufferedReader in = new BufferedReader(file);
        	String line = ""; //$NON-NLS-1$
        	ArrayList<String> players = new ArrayList<String>();
        	while((line = in.readLine()) != null)
        	{
        		String[] character = line.split(":"); //$NON-NLS-1$
        		String p = character[0];
        		
        		//Prevent the same player from being added multiple times
        		if(players.contains(p))
        			continue;
        		else
        			players.add(p);

    			int Plvl = 0;
    			
    			if(character.length > 1 && m.isInt(character[1]))
    			{
    				Mining.add(p, Integer.valueOf(character[1]));
    				Plvl += Integer.valueOf(character[1]);
    			}
    			if(character.length > 5 && m.isInt(character[5])){
    				WoodCutting.add(p, Integer.valueOf(character[5]));
    				Plvl += Integer.valueOf(character[5]);
    			}
    			if(character.length > 7 && m.isInt(character[7])){
    				Repair.add(p, Integer.valueOf(character[7]));
    				Plvl += Integer.valueOf(character[7]);
    			}
    			if(character.length > 8 && m.isInt(character[8])){
    				Unarmed.add(p, Integer.valueOf(character[8]));
    				Plvl += Integer.valueOf(character[8]);
    			}
    			if(character.length > 9 && m.isInt(character[9])){
    				Herbalism.add(p, Integer.valueOf(character[9]));
    				Plvl += Integer.valueOf(character[9]);
    			}
    			if(character.length > 10 && m.isInt(character[10])){
    				Excavation.add(p, Integer.valueOf(character[10]));
    				Plvl += Integer.valueOf(character[10]);
    			}
    			if(character.length > 11 && m.isInt(character[11])){
    				Archery.add(p, Integer.valueOf(character[11]));
    				Plvl += Integer.valueOf(character[11]);
    			}
    			if(character.length > 12 && m.isInt(character[12])){
    				Swords.add(p, Integer.valueOf(character[12]));
    				Plvl += Integer.valueOf(character[12]);
    			}
    			if(character.length > 13 && m.isInt(character[13])){
    				Axes.add(p, Integer.valueOf(character[13]));
    				Plvl += Integer.valueOf(character[13]);
    			}
    			if(character.length > 14 && m.isInt(character[14])){
    				Acrobatics.add(p, Integer.valueOf(character[14]));
    				Plvl += Integer.valueOf(character[14]);
    			}
    			if(character.length > 24 && m.isInt(character[24])){
    				Taming.add(p, Integer.valueOf(character[24]));
    				Plvl += Integer.valueOf(character[24]);
    			}
    			if(character.length > 34 && m.isInt(character[34]))
    			{
    				Fishing.add(p, Integer.valueOf(character[34]));
    				Plvl += Integer.valueOf(character[34]);
    			}
    			
    			PowerLevel.add(p, Plvl);
        	}
        	in.close();
        } catch (Exception e) {
            log.log(Level.SEVERE, "Exception while reading " //$NON-NLS-1$
            		+ location + " (Are you sure you formatted it correctly?)", e); //$NON-NLS-1$
        }
        //Write the leader board files
        leaderWrite(Mining.inOrder(), SkillType.MINING); //$NON-NLS-1$
        leaderWrite(WoodCutting.inOrder(), SkillType.WOODCUTTING); //$NON-NLS-1$
        leaderWrite(Repair.inOrder(), SkillType.REPAIR); //$NON-NLS-1$
        leaderWrite(Unarmed.inOrder(), SkillType.UNARMED); //$NON-NLS-1$
        leaderWrite(Herbalism.inOrder(), SkillType.HERBALISM); //$NON-NLS-1$
        leaderWrite(Excavation.inOrder(), SkillType.EXCAVATION); //$NON-NLS-1$
        leaderWrite(Archery.inOrder(), SkillType.ARCHERY); //$NON-NLS-1$
        leaderWrite(Swords.inOrder(), SkillType.SWORDS); //$NON-NLS-1$
        leaderWrite(Axes.inOrder(), SkillType.AXES); //$NON-NLS-1$
        leaderWrite(Acrobatics.inOrder(), SkillType.ACROBATICS); //$NON-NLS-1$
        leaderWrite(Taming.inOrder(), SkillType.TAMING); //$NON-NLS-1$
        leaderWrite(Fishing.inOrder(), SkillType.FISHING); //$NON-NLS-1$
        leaderWrite(Alchemy.inOrder(), SkillType.ALCHEMY); //$NON-NLS-1$
        leaderWrite(Enchanting.inOrder(), SkillType.ENCHANTING); //$NON-NLS-1$
        leaderWrite(PowerLevel.inOrder(), SkillType.ALL); //$NON-NLS-1$
	}
	public static void leaderWrite(PlayerStat[] ps, SkillType skillType)
	{
		String theLocation = "plugins/mcMMO/FlatFileStuff/Leaderboards/" + skillType + ".mcmmo"; //$NON-NLS-1$ //$NON-NLS-2$
		//CHECK IF THE FILE EXISTS
		File theDir = new File(theLocation);
		if(!theDir.exists())
		{
			//properties = new PropertiesFile(location);
			FileWriter writer = null;
			try {
				writer = new FileWriter(theLocation);
			} catch (Exception e) {
				log.log(Level.SEVERE, "Exception while creating " + theLocation, e); //$NON-NLS-1$
			} finally {
				try {
					if (writer != null) {
						writer.close();
					}
				} catch (IOException e) {
					log.log(Level.SEVERE, "Exception while closing writer for " + theLocation, e); //$NON-NLS-1$
				}
			}
		} else {
			try {
	        	FileReader file = new FileReader(theLocation);
	
	    		//HERP
	            BufferedReader in = new BufferedReader(file);
	            StringBuilder writer = new StringBuilder();
	            
	        	for(PlayerStat p : ps)
	        	{
	        		if(p.name.equals("$mcMMO_DummyInfo")) //$NON-NLS-1$
	        			continue;
	        		if(p.statVal == 0)
	        			continue;
	        		writer.append(p.name + ":" + p.statVal); //$NON-NLS-1$
	        		writer.append("\r\n");  //$NON-NLS-1$
	        	}
	        	
	        	in.close();
	        	//Write the new file
	            FileWriter out = new FileWriter(theLocation);
	            out.write(writer.toString());
	            out.close();
	        } catch (Exception e) {
	                log.log(Level.SEVERE, "Exception while writing to " + theLocation + " (Are you sure you formatted it correctly?)", e); //$NON-NLS-1$ //$NON-NLS-2$
	        }
		}
		//Create/open the file
		//Loop through backward writing each player
		//Close the file
	}
	
	public static String[] retrieveInfo(String skillName, int pagenumber)
	{
		String theLocation = "plugins/mcMMO/FlatFileStuff/Leaderboards/" + skillName + ".mcmmo"; //$NON-NLS-1$ //$NON-NLS-2$
		try {
        	FileReader file = new FileReader(theLocation);
        	BufferedReader in = new BufferedReader(file);
        	
        	int destination = (pagenumber - 1) * 10; //How many lines to skip through
        	int x = 0; //how many lines we've gone through
        	int y = 0; //going through the lines
        	String line = ""; //$NON-NLS-1$
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
            log.log(Level.SEVERE, "Exception while reading " //$NON-NLS-1$
            		+ theLocation + " (Are you sure you formatted it correctly?)", e); //$NON-NLS-1$
        }
        return null; //Shouldn't get here
	}
	public static void updateLeaderboard(PlayerStat ps, SkillType skillType)
	{
		if(LoadProperties.useMySQL)
			return;
		String theLocation = "plugins/mcMMO/FlatFileStuff/Leaderboards/" + skillType + ".mcmmo"; //$NON-NLS-1$ //$NON-NLS-2$
		try {
        	//Open the file
        	FileReader file = new FileReader(theLocation);
            BufferedReader in = new BufferedReader(file);
            StringBuilder writer = new StringBuilder();
        	String line = ""; //$NON-NLS-1$
        	Boolean inserted = false;
        	//While not at the end of the file
        	while((line = in.readLine()) != null)
        	{
        		//Insert the player into the line before it finds a smaller one
        		if(Integer.valueOf(line.split(":")[1]) < ps.statVal && !inserted) //$NON-NLS-1$
        		{
        			writer.append(ps.name + ":" + ps.statVal).append("\r\n"); //$NON-NLS-1$ //$NON-NLS-2$
        			inserted = true;
        		}
        		//Write anything that isn't the player already in the file so we remove the duplicate
        		if(!line.split(":")[0].equalsIgnoreCase(ps.name)) //$NON-NLS-1$
        		{
                    writer.append(line).append("\r\n"); //$NON-NLS-1$
        		}
        	}
        	
        	if(!inserted)
        	{
    			writer.append(ps.name + ":" + ps.statVal).append("\r\n"); //$NON-NLS-1$ //$NON-NLS-2$
        	}
        	
        	in.close();
        	//Write the new file
            FileWriter out = new FileWriter(theLocation);
            out.write(writer.toString());
            out.close();
        } catch (Exception e) {
                log.log(Level.SEVERE, "Exception while writing to " + theLocation + " (Are you sure you formatted it correctly?)", e); //$NON-NLS-1$ //$NON-NLS-2$
        }
	}
}
