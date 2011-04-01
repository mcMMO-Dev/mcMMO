package com.gmail.nossr50.vChat;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.plugin.Plugin;
import org.bukkit.ChatColor;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.gmail.nossr50.mcConfig;
import com.gmail.nossr50.mcMMO;

/**
* Handle events for all Player related events
* @author nossr50
*/
public class vPlayerListener extends PlayerListener {
    private final vChat plugin;
    protected static final Logger log = Logger.getLogger("Minecraft");
    //The length of a text box line in pixels
    protected static final int lineLength = 312;
    //Characters we will split the line at
    protected static final String lineSplit = "/- ";

    public vPlayerListener(vChat instance) {
        plugin = instance;
    }
    public void onPlayerJoin(PlayerJoinEvent event) {
    	Player player = event.getPlayer();
    	vUsers.addUser(player);
    	player.sendMessage(ChatColor.YELLOW+"This server is running vChat");
    	player.sendMessage(ChatColor.YELLOW+"Type /color or /prefix to do some thangs");
    	player.sendMessage(ChatColor.DARK_AQUA+"Currently running in Linux");
    	player.sendMessage(ChatColor.DARK_AQUA+"Steam community: vminecraft");
    }
    public Boolean isPlayer(String playername){
    	for(Player derp : plugin.getServer().getOnlinePlayers()){
    		if(derp.getName().toLowerCase().equals(playername.toLowerCase())){
    			return true;
    		}
    	}
    	return false;
    }
    //Special Color Codes
    protected static final String[] rainbow = new String[] {
    	ChatColor.DARK_RED.toString(),
    	ChatColor.RED.toString(),
    	ChatColor.GOLD.toString(),
    	ChatColor.YELLOW.toString(),
    	ChatColor.GREEN.toString(),
    	ChatColor.DARK_GREEN.toString(),
    	ChatColor.BLUE.toString(),
    	ChatColor.DARK_BLUE.toString(),
    	ChatColor.AQUA.toString(),
    	ChatColor.DARK_AQUA.toString(),
    	ChatColor.DARK_PURPLE.toString(),
    	ChatColor.LIGHT_PURPLE.toString()
    	};
    protected static final String[] xmas = new String[] {
    	ChatColor.DARK_RED.toString(),
    	ChatColor.DARK_RED.toString(),
    	ChatColor.WHITE.toString(),
    	ChatColor.WHITE.toString(),
    	ChatColor.DARK_GREEN.toString(),
    	ChatColor.DARK_GREEN.toString(),
    	};
    
    public void onPlayerChat(PlayerChatEvent event) {
    	Player player = event.getPlayer();
    	String message = event.getMessage();
    	String split[] = event.getMessage().split(" ");
    	Player[] players = plugin.getServer().getOnlinePlayers();
    	Plugin tester = plugin.getServer().getPluginManager().getPlugin("mcMMO");
        if (tester == null) {
        } else {
            try {
                mcMMO plugin = (mcMMO)tester;
                if (plugin.isPartyChatToggled(player) || plugin.isAdminChatToggled(player)) {
                    return;
                } else {
                	if(split[0].startsWith(">"))
                		quote(player, message, players);	
                	else{
                		quakeColors(player, message, players);
                	}
                }
            } catch (ClassCastException ex) {
                player.sendMessage("There's a plugin disguised as mcMMO! It's not the one I was expecting!");
            }
        }
    	event.setCancelled(true);
    }
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
    	Player player = event.getPlayer();
    	String message = event.getMessage();
    	String split[] = event.getMessage().split(" ");
    	/*
    	 * COLORS
    	 */
    	if(split[0].equalsIgnoreCase("/color")){
    		event.setCancelled(true);
    		if(split.length > 1)
        	{
        		vUsers.getProfile(player).setColor(split[1]);
        		player.sendMessage(ChatColor.RED
        				+ "Default chat color set.");
        	} else {
    	        player.sendMessage(ChatColor.RED + "You use these color codes like in quake or MW2.");
    	        player.sendMessage(ChatColor.RED + "^4 would make text " + ChatColor.DARK_RED
    	        		+ "red" + ChatColor.RED + ", ^a would make it " + ChatColor.GREEN 
    	        		+ "light green" + ChatColor.RED + ".");
    	        player.sendMessage(
    	        		  ChatColor.BLACK			+ "0"
    	        		+ ChatColor.DARK_BLUE			+ "1"
    	        		+ ChatColor.DARK_GREEN			+ "2"
    	        		+ ChatColor.DARK_AQUA			+ "3"
    	        		+ ChatColor.DARK_RED 			+ "4"
    	        		+ ChatColor.DARK_PURPLE 		+ "5"
    	        		+ ChatColor.GOLD 			+ "6"
    	        		+ ChatColor.GRAY 		+ "7"
    	        		+ ChatColor.DARK_GRAY 			+ "8"
    	        		+ ChatColor.BLUE 	+ "9"
    	        		+ ChatColor.GREEN 	+ "A"
    	        		+ ChatColor.AQUA 		+ "B"
    	        		+ ChatColor.RED 			+ "C"
    	        		+ ChatColor.LIGHT_PURPLE	+ "D"
    	        		+ ChatColor.YELLOW			+ "E"
    	        		+ ChatColor.WHITE			+ "F");
    					//+ "^r"					+ "[R]ainbow")
        	}
            event.setCancelled(true);
        }
    	/*
    	 * PREFIX
    	 */
    	if(split[0].equalsIgnoreCase("/prefix")){
    		event.setCancelled(true);
    		if(split.length < 3 && player.isOp()){
                player.sendMessage( ChatColor.RED + "Usage is /prefix [Player] [Color Code] <Tag>");
                player.sendMessage(ChatColor.RED + "Example: /prefix " + player.getName() + " e ^0[^a<3^0]");
                player.sendMessage( ChatColor.RED + "This would produce a name like... " + ChatColor.BLACK + "[" + ChatColor.GREEN + "<3" +ChatColor.BLACK + "]" + ChatColor.YELLOW + player.getName());
                return;
            }
            
    		if(player.isOp()){
            //Check if the player exists
            Player other = plugin.getServer().getPlayer(split[1]);
            if(other == null)
            {
                player.sendMessage( ChatColor.RED
                		+ "The player you specified could not be found");
                return;
            }
            
            if(split.length >= 3 && split[2] != null)
            {
            	vUsers.getProfile(other).setPrefix(split[2]);
                player.sendMessage(ChatColor.RED + "Name color changed");
            }
            if(split.length >= 4 && msgLength(split[3]) > 60)
            {
                player.sendMessage( ChatColor.RED
                		+ "The prefix you entered was too long.");
                return;
            }
            if(split.length >= 4 && split[3] != null)
            {
               vUsers.players.findProfile(other).setTag(split[3]);
	           player.sendMessage(ChatColor.GREEN + "Prefix changed");
                   log.log(Level.INFO, player + " changed their prefix to " + split[3]);
            }
            return;
        }
        if(split.length < 2){
        	 player.sendMessage( ChatColor.RED + "Usage is /prefix [Color Code] <Tag>");
             player.sendMessage(ChatColor.RED + "Example: /prefix " + player.getName() + " e ^0[^a<3^0]");
             player.sendMessage( ChatColor.RED + "This would produce a name like... " + ChatColor.BLACK + "[" + ChatColor.GREEN + "<3" + ChatColor.BLACK + "]" + ChatColor.YELLOW + player.getName());
             return;
        }       
        //Name color
        if(split.length >= 2 && split[1] != null){
            vUsers.getProfile(player).setPrefix(split[1]);
            player.sendMessage(ChatColor.RED + "Name color changed");
        }
        //Prefix
        if(split.length >= 3 && split[2] != null){
        //Check if the prefix is too long        
	        if(msgLength(split[1]) > 60)
	        {
	            player.sendMessage( ChatColor.RED
	            		+ "The prefix you entered was too long.");
	            return;
	        }
	           vUsers.players.findProfile(player).setTag(split[2]);
	           player.sendMessage(ChatColor.GREEN + "Prefix changed");
        }
    	}
    	/*
    	 * SUFFIX	
    	 */
    	if(split[0].equalsIgnoreCase("/suffix")){
    		event.setCancelled(true);
    	}
    	if(split[0].equalsIgnoreCase("/rprefix")){
    		
    		if(!player.isOp()){
    			player.sendMessage("Op Only");
    		}
    		if(split.length < 2){
    			player.sendMessage("Usage is /rprefix <name>");
    			return;
    		}
    		if(isPlayer(split[1])){
    			Player target = plugin.getServer().getPlayer(split[1]);
    			vUsers.getProfile(target).setPrefix("");
    			vUsers.getProfile(target).setTag("");
    		}
    	}
    }
    //=====================================================================
	//Function:	quakeColors
	//Input:	Player player: The player talking
    //			String message: The message to apply the effect to
	//Output:	boolean: If this feature is enabled
	//Use:		Displays a message in red
	//=====================================================================
	public static void quakeColors(Player player, String message, Player[] players)
	{
		//Format the name
		String playerName = "<"
				//Insert their tag
				+ vUsers.getProfile(player).getTag()
				//Color their name
				+ colorChange(vUsers.getProfile(player).getPrefix().charAt(0))
				//Insert their name
				+ player.getName() +ChatColor.WHITE+ "> ";

			String color = vUsers.getProfile(player).getColor();
			//Log the chat
			log.log(Level.INFO, "<"+player.getName()+"> " + message);
			
			//Output the message
			gmsg(player, playerName + color + message, players);

			//Loop through the string finding the color codes and inserting them
	}
  //=====================================================================
  //Function: gmsg
  //Input: Player sender: The player sending the message
  // String msg: The message to be broadcast to all players
  //Output: None
  //Use: Outputs a message to everybody
  //=====================================================================
      public static void gmsg(Player sender, String msg, Player[] players){
    	/* Disabled for now
       if(sender != null && sender.isMuted())
       sender.sendMessage(ChatColor.DARK_RED + "You have been muted.");
       	*/
      
          for (Player receiver : players) {
          
              if (receiver == null) return;
              
           //if(vUsers.getProfile(receiver) == null) return;
          
           //Check if the person has the sender ignored
           /* Disabled for now
           if(sender != null)
           if(vUsers.getProfile(receiver).isIgnored(sender))
           return;
           */
		  String[] message = applyColors(wordWrap(msg));
		  for(String out : message)
		  receiver.sendMessage(out);
          }
      }
    //=====================================================================
    //Function: gmsg
    //Input: String msg: The message to be broadcast to all players
    //Output: None
    //Use: Outputs a message to everybody
    //=====================================================================
        public static void gmsg(String msg){gmsg(null, msg, null);}
        public static void gmsg(Player player, String msg){gmsg(player, msg, null);}
      //=====================================================================
      //Function: wordWrap
      //Input: String msg: The message to be wrapped
      //Output: String[]: The array of substrings
      //Use: Cuts the message apart into whole words short enough to fit
          // on one line
      //=====================================================================
          public static String[] wordWrap(String msg){
           //Split each word apart
           ArrayList<String> split = new ArrayList<String>();
           for(String in : msg.split(" "))
           split.add(in);
          
           //Create an arraylist for the output
           ArrayList<String> out = new ArrayList<String>();
           //While i is less than the length of the array of words
           while(!split.isEmpty()){
           int len = 0;
              
           //Create an arraylist to hold individual words
           ArrayList<String> words = new ArrayList<String>();

           //Loop through the words finding their length and increasing
           //j, the end point for the sub string
           while(!split.isEmpty() && split.get(0) != null && len <= lineLength)
           {
           int wordLength = msgLength(split.get(0)) + 4;
          
           //If a word is too long for a line
           if(wordLength > lineLength)
           {
               String[] tempArray = wordCut(len, split.remove(0));
               words.add(tempArray[0]);
               split.add(tempArray[1]);
           }

           //If the word is not too long to fit
           len += wordLength;
           if( len < lineLength)
           words.add(split.remove(0));
           }
           //Merge them and add them to the output array.
           out.add(combineSplit(words.toArray(new String[words.size()]), " ") + " " );
           }
           //Convert to an array and return
           return out.toArray(new String[out.size()]);
          }
          
          //CombineSplit
          public static String combineSplit(String[] array, String merge) {
        	    String out = "";
        	    for(String word : array)
        	        out += word + merge;
        	    return out;
        	}

      	//=====================================================================
      	//Function:	msgLength
      	//Input:	String str: The string to find the length of
      	//Output:	int: The length on the screen of a string
      	//Use:		Finds the length on the screen of a string. Ignores colors.
      	//=====================================================================
          public static int msgLength(String str){
      		int length = 0;
      		//Loop through all the characters, skipping any color characters
      		//and their following color codes
      		for(int x = 0; x<str.length(); x++)
      		{
      			if((x+1 <= str.length()) && (str.charAt(x) == '^' || str.charAt(x) == ChatColor.WHITE.toString().charAt(0)))
      			{
                                      if(colorChange(str.charAt(x + 1)) != null)
      				{
      					x++;
                                              continue;
      				}
      			}
      			int len = charLength(str.charAt(x));
      			length += len;
      		}
      		return length;
          }
  //=====================================================================
  //Function: colorChange
  //Input: char colour: The color code to find the color for
  //Output: String: The color that the code identified
  //Use: Finds a color giving a color code
  //=====================================================================
    public static String colorChange(char colour)
    {
	    String color;
	    switch(colour)
	    {
	    case '0':
	    color = ChatColor.BLACK.toString();
	    break;
	    case '1':
	    color = ChatColor.DARK_BLUE.toString();
	    break;
	    case '2':
	    color = ChatColor.DARK_GREEN.toString();
	    break;
	    case '3':
	    color = ChatColor.DARK_AQUA.toString();
	    break;
	    case '4':
	    color = ChatColor.DARK_RED.toString();
	    break;
	    case '5':
	    color = ChatColor.DARK_PURPLE.toString();
	    break;
	    case '6':
	    color = ChatColor.GOLD.toString();
	    break;
	    case '7':
	    color = ChatColor.GRAY.toString();
	    break;
	    case '8':
	    color = ChatColor.DARK_GRAY.toString();
	    break;
	    case '9':
	    color = ChatColor.BLUE.toString();
	    break;
	    case 'a':
	    color = ChatColor.GREEN.toString();
	    break;
	    case 'b':
	    color = ChatColor.AQUA.toString();
	    break;
	    case 'c':
	    color = ChatColor.RED.toString();
	    break;
	    case 'd':
	    color = ChatColor.LIGHT_PURPLE.toString();
	    break;
	    case 'e':
	    color = ChatColor.YELLOW.toString();
	    break;
	    case 'f':
	    color = ChatColor.WHITE.toString();
	    break;
	    case 'A':
	    color = ChatColor.GREEN.toString();
	    break;
	    case 'B':
	    color = ChatColor.AQUA.toString();
	    break;
	    case 'C':
	    color = ChatColor.RED.toString();
	    break;
	    case 'D':
	    color = ChatColor.LIGHT_PURPLE.toString();
	    break;
	    case 'E':
	    color = ChatColor.YELLOW.toString();
	    break;
	    case 'F':
	    color = ChatColor.WHITE.toString();
	    break;
	    case 'R':
	    color = "^r";
	    break;
	    case 'r':
	    color = "^r";
	    break;
        case 'x':
        color = "^x";
        break;
        case 'X':
        color = "^x";
        break;
        default:
        color = null;
        break;
    }
                    return color;
    }
    
    private static String[] wordCut(int lengthBefore, String str){
    	int length = lengthBefore;
    	//Loop through all the characters, skipping any color characters
    	//and their following color codes
    	String[] output = new String[2];
    	int x = 0;
    	while(length < lineLength && x < str.length())
    	{
    	int len = charLength(str.charAt(x));
    	if( len > 0)
    	length += len;
    	else
    	x++;
    	x++;
    	}
    	if(x > str.length())
    	x = str.length();
    	//Add the substring to the output after cutting it
    	output[0] = str.substring(0, x);
    	//Add the last of the string to the output.
    	output[1] = str.substring(x);
    	return output;
    	    }
    
    private static int charLength(char x)
    {
    if("i.:,;|!".indexOf(x) != -1)
	return 2;
	else if("l'".indexOf(x) != -1)
	return 3;
	else if("tI[]".indexOf(x) != -1)
	return 4;
	else if("fk{}<>\"*()".indexOf(x) != -1)
	return 5;
	else if("abcdeghjmnopqrsuvwxyzABCDEFGHJKLMNOPQRSTUVWXYZ1234567890\\/#?$%-=_+&^".indexOf(x) != -1)
	return 6;
	else if("@~".indexOf(x) != -1)
	return 7;
	else if(x==' ')
	return 4;
	else
	return -1;
    }
    
  //=====================================================================
  //Function: rainbow
  //Input: String msg: The string to colorify
  //Output: String: The rainbowed result
  //Use: Rainbowifies a string;
  //=====================================================================
      public static String rainbow(String msg){
       String temp = "";
	  int counter=0;
	  //Loop through the message applying the colors
	  for(int x=0; x<msg.length(); x++)
	  {
	  temp += rainbow[counter]+msg.charAt(x);
	
	  if(msg.charAt(x)!=' ') counter++;
	  if(counter==rainbow.length) counter = 0;
	  }
	  return temp;
      }
  //=====================================================================
  //Function: xmas
  //Input: String msg: The string to colorify
  //Output: String: The xmas colored result
  //Use: Makes a string more festive
  //=====================================================================
      public static String xmas(String msg){
       String temp = "";
	  int counter=0;
	  //Loop through the message applying the colors
	  for(int x=0; x<msg.length(); x++)
	  {
	  temp += xmas[counter]+msg.charAt(x);
	
	  if(msg.charAt(x)!=' ') counter++;
	  if(counter==xmas.length) counter = 0;
	  }
	  return temp;
      }
      
    //=====================================================================
    //Function: quote
    //Input: Player player: The player talking
        // String message: The message to apply the effect to
    //Output: boolean: If this feature is enabled
    //Use: Displays a message as a quote
    //=====================================================================
    public void quote(Player player, String message, Player[] players)
    {
    //Format the name
    	//Format the name
		String playerName = "<"+
				//Insert their tag
				vUsers.getProfile(player).getTag()
				//Color their name
				+ colorChange(vUsers.getProfile(player).getPrefix().charAt(0))
				//Insert their name
				+ player.getName() +ChatColor.WHITE+ "> ";
    //Log the chat
    log.log(Level.INFO, "<"+player.getName()+"> " + message);
    //Output the message
    gmsg(player, playerName + ChatColor.GREEN + message, players);
    }
    
    //=====================================================================
	//Function:	applyColors
	//Input:	String[] message: The lines to be colored
	//Output:	String[]: The lines, but colorful
	//Use:		Colors each line
	//=====================================================================
	public static String[] applyColors(String[] message)
	{
		if(message != null && message[0] != null && !message[0].isEmpty()){
			//The color to start the line with
			String recentColor = ChatColor.WHITE.toString();
			
			//Go through each line
			int counter = 0;
			int i = 0;
			boolean taste = false;
            boolean xmasparty = false;
			
			for(String msg: message)
			{	
				//Start the line with the most recent color
				String temp = "";
				if(!recentColor.equals("^r") && !recentColor.equals("^x") && recentColor != null)
					temp += recentColor;
				
				//Loop through looking for a color code
				for(int x = 0; x< msg.length(); x++)
				{
					//If the char is a ^ or
					if(taste || msg.charAt(x) == '^'
							|| msg.charAt(x) == ChatColor.DARK_RED.toString().charAt(0))
					{
						if(x != msg.length() - 1)
						{
							//If the following character is a color code
							if(colorChange(msg.charAt(x+1)) != null)
							{
								//Set the most recent color to the new color
								recentColor = colorChange(msg.charAt(x+1));
								
								//If the color specified is rainbow
								if(taste || recentColor.equals("^r"))
								{
									/*
									//Skip the quake code for rainbow
									if(recentColor.equals("^r"))
									{
										x += 2;
									}
									
									//Taste keeps it going with rainbow if there
									//are more lines
									taste = true;
									//Loop through the message applying the colors
									while(x < msg.length() && msg.charAt(x) != '^'
										&& msg.charAt(x) != ChatColor.DARK_RED.toString().charAt(0))
									{
										temp += rainbow[i] + msg.charAt(x);
										if(msg.charAt(x) != ' ') i++;
										if(i == rainbow.length) i = 0;
										x++;
									}
									
									//If it reached another color instead of the end
									if(x < msg.length() && msg.charAt(x) == '^')
									{
										taste = false;
										i = 0;
										x--;
									}
									*/
								}
                                if(xmasparty || recentColor.equals("^x"))
								{
                                	/*
									//Skip the quake code for xmas
									if(recentColor.equals("^x"))
									{
										x += 2;
									}
									
									//xmasparty keeps it going with xmas if there
									//are more lines
									xmasparty = true;
									//Loop through the message applying the colors
									while(x < msg.length() && msg.charAt(x) != '^'
										&& msg.charAt(x) != ChatColor.DARK_RED.toString().charAt(0))
									{
										temp += xmas[i] + msg.charAt(x);
										
										if(msg.charAt(x) != ' ') i++;
										if(i == xmas.length) i = 0;
										x++;
									}
									
									//If it reached another color instead of the end
									if(x < msg.length() && msg.charAt(x) == '^'
											|| x < msg.length()
											&&  msg.charAt(x) == ChatColor.DARK_RED.toString().charAt(0) )
									{
										xmasparty = false;
										i = 0;
										x--;
									}
									*/
								}
								else
                                                                
								{
									//Add the color
									temp += recentColor;
									//Skip these chars
									x++;
								}
								
							//Otherwise ignore it.
							} else {
								temp += msg.charAt(x);
							}
						//Insert the character
						} else {
							temp += msg.charAt(x);
						}
					} else {
						temp += msg.charAt(x);
					}
				}
				//Replace the message with the colorful message
				message[counter] = temp;
				counter++;
			}
		}
		return message;
	}
}
