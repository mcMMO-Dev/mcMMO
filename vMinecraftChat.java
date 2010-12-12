import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

//=====================================================================
//Class:	vMinecraftChat
//Use:		Encapsulates all chat commands added by this mod
//Author:	nossr50, TrapAlice, cerevisiae
//=====================================================================
public class vMinecraftChat {
    protected static final Logger log = Logger.getLogger("Minecraft");
    protected static final int lineLength = 312;
	//The array of colors to use
	protected static final String[] rainbow = new String[] {
			Colors.Red,
			Colors.Rose,
			Colors.Gold,
			Colors.Yellow,
			Colors.LightGreen,
			Colors.Green,
			Colors.LightBlue,
			Colors.Blue, 
			Colors.Navy, 
			Colors.DarkPurple, 
			Colors.Purple,
			Colors.LightPurple};

	//=====================================================================
	//Function:	gmsg
	//Input:	String msg: The message to be broadcast to all players
	//Output:	None 
	//Use:		Outputs a message to everybody
	//=====================================================================
    public static void gmsg(Player sender, String msg){
    	if(sender.isMuted())
    		sender.sendMessage(Colors.Red + "You have been muted.");
    	
        for (Player receiver : etc.getServer().getPlayerList()) {
        	
            if (receiver == null) {return;}
            
        	if(vMinecraftUsers.getProfile(receiver) == null)
        		return;
        	//Check if the person has the sender ignored
        	if(!vMinecraftUsers.getProfile(receiver).isIgnored(sender))
    		{
    	    	String[] message = applyColors(wordWrap(msg));
    	    	for(String out : message)
    	    		receiver.sendMessage(out);
    		}
        }
    }

	//=====================================================================
	//Function:	sendMessage
	//Input:	String msg: The message to be broadcast to all players
	//Output:	None 
	//Use:		Outputs a message to everybody
	//=====================================================================
    public static void sendMessage(Player sender, Player receiver, String msg){
    	if(sender.isMuted())
    		sender.sendMessage(Colors.Red + "You have been muted.");
    	
    	//Check if the receiver has the sender ignored
    	if(vMinecraftUsers.getProfile(receiver) == null)
    		return;
		if(!vMinecraftUsers.getProfile(receiver).isIgnored(sender))
		{
	    	String[] message = applyColors(wordWrap(msg));
	    	for(String out : message)
	    		receiver.sendMessage(out);
	    //Tell them if they are
		} else
    		sendMessage(sender, sender, Colors.Rose + receiver.getName()
    				+ " has you on their ignore list.");
    }

	//=====================================================================
	//Function:	wordWrap
	//Input:	String msg: The message to be wrapped
	//Output:	String[]: The array of substrings 
	//Use:		Cuts the message apart into whole words short enough to fit
    //			on one line
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
    		out.add( etc.combineSplit(0,
    				words.toArray(new String[words.size()]), " ") + " " );
    	}
    	//Convert to an array and return
    	return out.toArray(new String[out.size()]);
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
			if(str.charAt(x) == '^' || str.charAt(x) == Colors.White.charAt(0))
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
	//Function:	wordCut
	//Input:	String str: The string to find the length of
	//Output:	String[]: The cut up word
	//Use:		Cuts apart a word that is too long to fit on one line
	//=====================================================================
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
    
	//=====================================================================
	//Function:	charLength
	//Input:	char x: The character to find the length of.
	//Output:	int: The length of the character
	//Use:		Finds the visual length of the character on the screen.
	//=====================================================================
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
	//Function:	rainbow
	//Input:	String msg: The string to colorify
	//Output:	String: The rainbowed result
	//Use:		Rainbowifies a string;
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
	//Function:	getName
	//Input:	Player player: The player to get name as color
	//Output:	String: The name colored 
	//Use:		Returns the colored name;
	//=====================================================================
    public static String getName(Player player){
    	
    	//Add the nickname or the name if there is none
    	String output = vMinecraftUsers.getProfile(player).getNick();
    	
    	if(output.isEmpty())
    		output = player.getName();
    	
    	//Add the color if there is one
    	if(player.getColor() != null && player.getColor() != "")
    		output = player.getColor().substring(0,2) + output;
    	
    	//Add the tag if there is one
    	output = vMinecraftUsers.getProfile(player).getTag() + output;
    	
    	//Add the suffix if there is one
    	output += vMinecraftUsers.getProfile(player).getSuffix();
    	
    	output = Colors.White + output;
    	
    	/*if(playerPrefix != null && !playerPrefix.isEmpty())
    		output = applyColors(playerPrefix.substring(1)) + output;*/
    	
    	//Return the name
        return output;
    }
    
	//=====================================================================
	//Function:	colorChange
	//Input:	char colour: The color code to find the color for
	//Output:	String: The color that the code identified 
	//Use:		Finds a color giving a color code
	//=====================================================================
	public static String colorChange(char colour)
	{
		String color = "";
		switch(colour)
		{
			case '0':
				color = Colors.Black;
				break;
			case '1':
				color = Colors.Navy;
				break;
			case '2':
				color = Colors.Green;
				break;
			case '3':
				color = Colors.Blue;
				break;
			case '4':
				color = Colors.Red;
				break;
			case '5':
				color = Colors.Purple;
				break;
			case '6':
				color = Colors.Gold;
					break;
			case '7':
				color = Colors.LightGray;
				break;
			case '8':
				color = Colors.Gray;
				break;
			case '9':
				color = Colors.DarkPurple;
				break;
			case 'a':
				color = Colors.LightGreen;
				break;
			case 'b':
				color = Colors.LightBlue;
				break;
			case 'c':
				color = Colors.Rose;
				break;
			case 'd':
				color = Colors.LightPurple;
				break;
			case 'e':
				color = Colors.Yellow;
				break;
			case 'f':
				color = Colors.White;
				break;
			case 'A':
				color = Colors.LightGreen;
				break;
			case 'B':
				color = Colors.LightBlue;
				break;
			case 'C':
				color = Colors.Rose;
				break;
			case 'D':
				color = Colors.LightPurple;
				break;
			case 'E':
				color = Colors.Yellow;
				break;
			case 'F':
				color = Colors.White;
				break;
			case 'R':
				color = "^r";
				break;
			case 'r':
				color = "^r";
				break;
			default:
				color = null;
				break;
		}
		return color;
	}
	  
	//=====================================================================
	//Function:	adminChat
	//Input:	Player player: The player talking
    //			String message: The message to apply the effect to
	//Output:	boolean: If this feature is enabled
	//Use:		Sends messages only to admins
	//=====================================================================
	public static boolean adminChat(Player player, String message){
		
		//Check if the player can use this feature
		if(player.isAdmin() || player.canUseCommand("/adminchat"))
		{
			//Special formatting for adminchat {Username}
	        String adminchat = Colors.DarkPurple + "{" + getName(player)
	        +  Colors.DarkPurple +"} ";
	        
	        //Cut off the @ prefix
	        if(message.startsWith("@"))
	        	message = message.substring(1, message.length());
	        
	        //Get the player from the playerlist to send the message to.
			for (Player p: etc.getServer().getPlayerList()) {
				
				//If p is not null
				if (p != null) {
					
					//And if p is an admin or has access to adminchat send message
					if (p.isAdmin() || (p.canUseCommand("/adminchat"))) {
						sendMessage(player, p, adminchat + message);
					}
				}
			}

		    //So you can read adminchat from the server console
			log.log(Level.INFO, "@" + "<" + player.getName() + "> " + message); 
			return true;
		}
		return false;
	}

	//=====================================================================
	//Function:	quote
	//Input:	Player player: The player talking
    //			String message: The message to apply the effect to
	//Output:	boolean: If this feature is enabled
	//Use:		Displays a message as a quote
	//=====================================================================
	public static boolean quote(Player player, String message)
	{
		//Format the name
		String playerName = Colors.White + "<" + getName(player)
				+ Colors.White + "> ";
		if(vMinecraftSettings.getInstance().greentext()) {
			//Log the chat
			log.log(Level.INFO, "<"+player.getName()+"> " + message);

			//Output the message
			gmsg(player, playerName + Colors.LightGreen + message);
			return true;
		}
		return false;
	}

	//=====================================================================
	//Function:	rage
	//Input:	Player player: The player talking
    //			String message: The message to apply the effect to
	//Output:	boolean: If this feature is enabled
	//Use:		Displays a message in red
	//=====================================================================
	public static boolean rage(Player player, String message)
	{
		//Format the name
		String playerName = Colors.White + "<"
				+ getName(player) + Colors.White +"> ";
		if (vMinecraftSettings.getInstance().FFF()) {
			log.log(Level.INFO, "<"+player.getName()+"> "+message);
			
			//Output the message
			gmsg(player, playerName + Colors.Red +  message);
			return true;
		}
		return false;
	}
    
    //=====================================================================
	//Function:	quakeColors
	//Input:	Player player: The player talking
    //			String message: The message to apply the effect to
	//Output:	boolean: If this feature is enabled
	//Use:		Displays a message in red
	//=====================================================================
	public static boolean quakeColors(Player player, String message)
	{
		//Format the name
		String playerName = Colors.White + "<"
				+ getName(player) + Colors.White +"> ";
		if(vMinecraftSettings.getInstance().quakeColors()) {

			String color = vMinecraftUsers.getProfile(player).getColor();
			//Log the chat
			log.log(Level.INFO, "<"+player.getName()+"> " + message);
			
			//Output the message
			gmsg(player, playerName + color + message);

			//Loop through the string finding the color codes and inserting them
			return true;
		}
		return false;
	}
    
	//=====================================================================
	//Function:	emote
	//Input:	Player player: The player talking
    //          	String message: The message to apply the effect to
	//Output:	boolean: If this feature is enabled
	//Use:		/me but with our custom colors applied
	//=====================================================================
    public static boolean emote(Player player, String message)
    {
		gmsg(player, "* " + getName(player) + " " + Colors.White + message);
        return true;
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
			String recentColor = Colors.White;
			
			//Go through each line
			int counter = 0;
			int i = 0;
			boolean taste = false;
			
			for(String msg: message)
			{	
				//Start the line with the most recent color
				String temp = "";
				if(!recentColor.equals("^r") && recentColor != null)
					temp += recentColor;
				
				//Loop through looking for a color code
				for(int x = 0; x< msg.length(); x++)
				{
					//If the char is a ^ or �
					if(taste || msg.charAt(x) == '^'
							|| msg.charAt(x) == Colors.Red.charAt(0))
					{
						if(x != msg.length() - 1)
						{
							//If the following character is a color code
							if(vMinecraftChat.colorChange(msg.charAt(x+1)) != null)
							{
								//Set the most recent color to the new color
								recentColor = vMinecraftChat.colorChange(msg.charAt(x+1));
								
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
										&& msg.charAt(x) != Colors.Red.charAt(0))
									{
										temp += rainbow[i] + msg.charAt(x);
										
										if(msg.charAt(x) != ' ') i++;
										if(i == rainbow.length) i = 0;
										x++;
									}
									
									//If it reached another color instead of the end
									if(x < msg.length() && msg.charAt(x) == '^'
											|| x < msg.length()
											&&  msg.charAt(x) == Colors.Red.charAt(0) )
									{
										taste = false;
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