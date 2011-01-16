package com.bukkit.nossr50.vChat;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.ChatColor;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    public void onPlayerJoin(PlayerEvent event) {
    	Player player = event.getPlayer();
    	vUsers.addUser(player);
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
    	event.setCancelled(true);
    	Player[] players = plugin.getServer().getOnlinePlayers();
    	//Quotes
    	if(split[0].startsWith(">"))
    		quote(player, message, players);	
    	else{
    		quakeColors(player, message, players);
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
				+ player.getName() + "> ";

			//String color = vUsers.getProfile(player).getColor();
			//Log the chat
			log.log(Level.INFO, "<"+player.getName()+"> " + message);
			
			//Output the message
			gmsg(player, playerName + message, players);

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
    String playerName = ChatColor.WHITE + "<" + player.getName() + "> ";
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
									if(x < msg.length() && msg.charAt(x) == '^'
											/* Not sure what this check is for
											 * || x < msg.length() &&  msg.charAt(x) == ChatColor.DARK_RED.toString().charAt(0)*/)
									{
										taste = false;
										i = 0;
										x--;
									}
								}
                                if(xmasparty || recentColor.equals("^x"))
								{
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
