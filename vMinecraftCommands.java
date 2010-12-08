import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

//=====================================================================
//Class:	vMinecraftCommands
//Use:		Encapsulates all commands added by this mod
//Author:	nos, trapalice, cerevisiae
//=====================================================================
public class vMinecraftCommands{

	//Log output
    protected static final Logger log = Logger.getLogger("Minecraft");
    static final int EXIT_FAIL = 0,
    		  		 EXIT_SUCCESS = 1,
    		  		 EXIT_CONTINUE = 2;
    
    //The list of commands for vMinecraft
    public static commandList cl = new commandList();

	//=====================================================================
	//Function:	loadCommands
	//Input:	None
	//Output:	None
	//Use:		Imports all the commands into the command list
	//=====================================================================
    public static void loadCommands(){
		//If we had commands we would add them here.
    	
    	//register
    	//String: The command that will be used
    	//String: The name of the function that will be called when
    	//		  the command is used
    	//String(Optional): The help menu description
        cl.register("/tp", "teleport");
        cl.register("/vminecraft", "vminecrafthelp");
        cl.register("/colors", "colors");
        cl.register("/masstp", "masstp", "Teleports those with lower permissions to you");
        cl.register("/reload", "reload");
        cl.register("/rules", "rules", "Displays the rules");
        cl.register("/fabulous", "fabulous", "makes text SUUUPER");
        cl.register("/whois", "whois", "/whois [user]");
        cl.register("/who", "who");
        cl.register("/say", "say");
        cl.register("/slay", "slay", "Kill target player");
        cl.register("/ezmodo", "invuln", "Toggle invulnerability");
        cl.register("/ezlist", "ezlist", "List invulnerable players");
        cl.register("/heal", "heal", "heal yourself or other players");
        cl.register("/suicide", "suicide", "Kill yourself... you loser");
        cl.register("/a", "adminChatToggle", "Toggle admin chat for every message");
        cl.register("/modify", "modifySplit");
        cl.register("/me", "me");
        cl.register("/msg", "message", "Send a message to a player /msg [Player] [Message]");
        cl.register("/reply", "reply", "Reply to a player /reply [Message], Alias: /r");
        cl.register("/ignore", "addIgnored", "Adds a user to your ignore list");
        cl.register("/unignore", "removeIgnored", "Removes a user from your ignore list");
        
        //registerAlias
        //String: The command that this will be called by
        //String: The message that will be called when the first is entered
        //		  Can be modified with %# to have it insert a player
        //		  argument into that position.
        //		  EX: Aliased command is
        //		  cl.registerAlias("/test", "/i %0 100")
        //		  Player uses /test wood
        //		  The %0 will be replaced with wood for this instance
        //		  and Player will be given 100 wood.
        cl.registerAlias("/playerlist", "/who");
        cl.registerAlias("/vhelp", "/vminecraft");
        cl.registerAlias("/r", "/reply");
        cl.registerAlias("/w", "/msg");
        cl.registerAlias("/wrists", "/suicide");
        cl.registerAlias("/ci", "/clearinventory");
        
        //registerMessage
        //String:  The command it will run on
        //String:  What will be displayed
        //		   %p  is the player calling the command
        //		   %#  is the argument number of the command.
        //		   %#p is an argument number that will be required to be
        //			   an online player
        //String:  The color the message will be
        //int:	   The number of arguments required for the message to appear
        //boolean: If the message should only display for admins
        cl.registerMessage("/kick", "%p has kicked %0p", Colors.Blue, 1, false);
        cl.registerMessage("/ban", "%p has banned %0p", Colors.Blue, 1, false);
        cl.registerMessage("/ipban", "%p has IP banned %0p", Colors.Blue, 1, false);
        cl.registerMessage("/time", "Time change thanks to %p", Colors.Blue, 1, true);
        cl.registerMessage("/tp", "%p has teleported to %0p", Colors.Blue, 1, true);
    }
        //=====================================================================
	//Function:	vminecrafthelp (/vhelp or /vminecraft)
	//Input:	Player player: The player using the command
	//Output:	int: Exit Code
	//Use:		Displays the current status of most vMinecraft settings
        //              and provides some useful tips.
	//=====================================================================
    public static int vminecrafthelp(Player player, String[] args){
        vMinecraftChat.sendMessage(player, player, Colors.Yellow + "Chat Settings");
        vMinecraftChat.sendMessage(player, player, Colors.LightPurple + "Admin Chat: " + vMinecraftSettings.getInstance().adminchat());
        vMinecraftChat.sendMessage(player, player, Colors.LightPurple + "FFF turns red: " + vMinecraftSettings.getInstance().FFF());
        vMinecraftChat.sendMessage(player, player, Colors.LightPurple + "Greentext After >: " + vMinecraftSettings.getInstance().greentext());
        vMinecraftChat.sendMessage(player, player, Colors.LightPurple + "Quake Color Script: " + vMinecraftSettings.getInstance().quakeColors());
        vMinecraftChat.sendMessage(player, player, Colors.Yellow + "Enabled Commands are TRUE, disabled are FALSE");
        vMinecraftChat.sendMessage(player, player, Colors.LightPurple + "Command /ezmodo: " + vMinecraftSettings.getInstance().cmdEzModo());
        vMinecraftChat.sendMessage(player, player, Colors.LightPurple + "Command /fabulous: " + vMinecraftSettings.getInstance().cmdFabulous());
        vMinecraftChat.sendMessage(player, player, Colors.LightPurple + "Command /rules: " + vMinecraftSettings.getInstance().cmdRules());
        vMinecraftChat.sendMessage(player, player, Colors.LightPurple + "Command /heal: " + vMinecraftSettings.getInstance().cmdHeal());
        vMinecraftChat.sendMessage(player, player, Colors.LightPurple + "Command /masstp: " + vMinecraftSettings.getInstance().cmdMasstp());
        vMinecraftChat.sendMessage(player, player, Colors.LightPurple + "Command /say: " + vMinecraftSettings.getInstance().cmdSay());
        vMinecraftChat.sendMessage(player, player, Colors.LightPurple + "Command /suicide: " + vMinecraftSettings.getInstance().cmdSuicide());
        vMinecraftChat.sendMessage(player, player, Colors.LightPurple + "Command /whois: " + vMinecraftSettings.getInstance().cmdWhoIs());
        vMinecraftChat.sendMessage(player, player, Colors.LightPurple + "Command /tp won't work on higher ranked players: " + vMinecraftSettings.getInstance().cmdTp());
        vMinecraftChat.sendMessage(player, player, Colors.LightPurple + "Command /tphere won't work on higher ranked players: " + vMinecraftSettings.getInstance().cmdTphere());
        vMinecraftChat.sendMessage(player, player, Colors.Yellow + "Other Settings");
        vMinecraftChat.sendMessage(player, player, Colors.LightPurple + "Command /who: " + vMinecraftSettings.getInstance().cmdWho());
        vMinecraftChat.sendMessage(player, player, Colors.LightPurple + "COLORED PLAYER LIST IS DEPENDENT ON /who BEING TRUE!");
        vMinecraftChat.sendMessage(player, player, Colors.LightPurple + "Global Messages: " + vMinecraftSettings.getInstance().globalmessages());
        return EXIT_SUCCESS;
    }
    
    //=====================================================================
	//Function:	colors (/colors)
	//Input:	Player player: The player using the command
	//Output:	int: Exit Code
	//Use:		Displays a list of all colors and color codes
	//=====================================================================
    public static int colors(Player player, String[] args){
        vMinecraftChat.sendMessage(player, player,
        		  Colors.Black			+ "0"
        		+ Colors.Navy			+ "1"
        		+ Colors.Green			+ "2"
        		+ Colors.Blue			+ "3"
        		+ Colors.Red 			+ "4"
        		+ Colors.Purple 		+ "5"
        		+ Colors.Gold 			+ "6"
        		+ Colors.LightGray 		+ "7"
        		+ Colors.Gray 			+ "8"
        		+ Colors.DarkPurple 	+ "9"
        		+ Colors.LightGreen 	+ "a"
        		+ Colors.LightBlue 		+ "b"
        		+ Colors.Rose 			+ "c"
        		+ Colors.LightPurple	+ "d"
        		+ Colors.White			+ "f");
        return EXIT_SUCCESS;
    }
    
    //=====================================================================
	//Function:	me (/me)
	//Input:	Player player: The player using the command
    //			String[] args: Will contain the message the player sends
	//Output:	int: Exit Code
	//Use:		The player uses this to emote, but now its colorful.
	//=====================================================================
    public static int me(Player player, String[] args)
    {
        String str = etc.combineSplit(0, args, " ");
        if (args.length < 1) {return EXIT_FAIL;}
        vMinecraftChat.emote(player, str);
        return EXIT_SUCCESS;
    }

    //=====================================================================
	//Function:	message (/msg, /w, /whisper)
	//Input:	Player player: The player using the command
    //			String[] args: Will contain the target player name and
    //						   message the player sends
	//Output:	int: Exit Code
	//Use:		Send a message to a player
	//=====================================================================
    public static int message(Player player, String[] args)
    {
        if (args.length > 1) {
            String msg = etc.combineSplit(1, args, " ");
            Player toPlayer = etc.getServer().matchPlayer(args[0]);
            if (toPlayer != null && args.length > 0) {
        	//Send the message to the targeted player and the sender
	        vMinecraftChat.sendMessage(player, toPlayer,
	        		Colors.LightGreen + "[From:" + vMinecraftChat.getName(player)
	        		+ Colors.LightGreen + "] " + msg);
	        vMinecraftChat.sendMessage(player, player,
	        		Colors.LightGreen + "[To:" + vMinecraftChat.getName(toPlayer)
	        		+ Colors.LightGreen + "] " + msg);
            //Set the last massager for each player
            vMinecraftUsers.players.findProfile(player).setMessage(toPlayer);
            vMinecraftUsers.players.findProfile(toPlayer).setMessage(player);
            
            //Display the message to the log
            log.log(Level.INFO, player.getName() + " whispered to " + toPlayer.getName()
            		+ ": " + msg);
            } else {
            	vMinecraftChat.sendMessage(player, player, Colors.Rose
            			+ "No player by the name of " + args[0] + " could be found.");
            }
        } else {
        	vMinecraftChat.sendMessage(player, player, Colors.Rose
        			+ "Usage is /msg [player] [message]");
        }
        return EXIT_SUCCESS;
    }

    //=====================================================================
	//Function:	reply (/r, /reply)
	//Input:	Player player: The player using the command
    //			String[] args: Will contain the message the player sends
	//Output:	int: Exit Code
	//Use:		Send a message to a player
	//=====================================================================
    public static int reply(Player player, String[] args)
    {
    	//If the profile exists for the player
    	if(vMinecraftUsers.players.findProfile(player) != null )
    	{
        	Player toPlayer = vMinecraftUsers.players.findProfile(player).getMessage();
        	if (toPlayer != null && args.length > 0) {
    	        String msg = etc.combineSplit(0, args, " ");
    	        
            	//Send the message to the targeted player and the sender
    	        vMinecraftChat.sendMessage(player, toPlayer,
    	        		Colors.LightGreen + "[From:" + vMinecraftChat.getName(player)
    	        		+ Colors.LightGreen + "] " + msg);
    	        vMinecraftChat.sendMessage(player, player,
    	        		Colors.LightGreen + "[To:" + vMinecraftChat.getName(toPlayer)
    	        		+ Colors.LightGreen + "] " + msg);
    	        
    	        //Set the last messager for each player
    	        vMinecraftUsers.players.findProfile(player).setMessage(toPlayer);
    	        vMinecraftUsers.players.findProfile(toPlayer).setMessage(player);
                
                //Display the message to the log
                log.log(Level.INFO, player.getName() + " whispered to " + toPlayer.getName()
                		+ ": " + msg);
        	} else {
        		vMinecraftChat.sendMessage(player, player,
        				Colors.Rose + "The person you last message has logged off");
        	}
    	}
    	return EXIT_SUCCESS;
    }

	//=====================================================================
	//Function:	addIgnored (/ignore)
	//Input:	Player player: The player using the command
    //			String[] args: The name of the player to ignore
	//Output:	int: Exit Code
	//Use:		Adds a player to the ignore list
	//=====================================================================
    public static int addIgnored(Player player, String[] args)
    {
    	//Make sure the player gave you a user to ignore
    	if(args.length > 0)
    	{
    		//Find the player and make sure they exist
        	Player ignore = etc.getServer().matchPlayer(args[0]);
        	if(ignore != null)
        	{
        		//Don't let the player ignore themselves
        		if(!ignore.getName().equalsIgnoreCase(player.getName()))
        		{
	        		//Attempt to ignore the player and report accordingly
	        		if(vMinecraftUsers.players.findProfile(player).addIgnore(ignore))
	        			vMinecraftChat.sendMessage(player, player,
	        					Colors.Rose + ignore.getName()+ " has been successfuly " +
	        							"ignored.");
	        		else
	        			vMinecraftChat.sendMessage(player, player,
	        					Colors.Rose + "You are already ignoring " + ignore.getName());
        		} else
        			vMinecraftChat.sendMessage(player, player,
        					Colors.Rose + "You cannot ignore yourself");
        	}
        	else
    			vMinecraftChat.sendMessage(player, player,
    					Colors.Rose + "The person you tried to ignore is not logged in.");

    	}
    	else
			vMinecraftChat.sendMessage(player, player,
					Colors.Rose + "Usage: /ignore [Player]");
    	return EXIT_SUCCESS;
    }

	//=====================================================================
	//Function:	removeIgnored (/unignore)
	//Input:	Player player: The player using the command
    //			String[] args: The name of the player to stop ignoring
	//Output:	int: Exit Code
	//Use:		Removes a player from the ignore list
	//=====================================================================
    public static int removeIgnored(Player player, String[] args)
    {
    	//Make sure the player gave you a user to ignore
    	if(args.length > 0)
    	{
    		//Find the player and make sure they exist
        	Player ignore = etc.getServer().matchPlayer(args[0]);
        	if(ignore != null)
        	{
        		//Attempt to ignore the player and report accordingly
        		if(vMinecraftUsers.players.findProfile(player).removeIgnore(ignore))
        			vMinecraftChat.sendMessage(player, player,
        					Colors.Rose + ignore.getName()+ " has been successfuly " +
        							"unignored.");
        		else
        			vMinecraftChat.sendMessage(player, player,
        					Colors.Rose + "You are not currently ignoring " + ignore.getName());
        	}
        	else
    			vMinecraftChat.sendMessage(player, player,
    					Colors.Rose + "The person you tried to unignore is not logged in.");
    	}
    	else
			vMinecraftChat.sendMessage(player, player,
					Colors.Rose + "Usage: /unignore [Player]");
    	return EXIT_SUCCESS;
    }
    
	//=====================================================================
	//Function:	adminChatToggle (/a)
	//Input:	Player player: The player using the command
    //			String[] args: Ignored
	//Output:	int: Exit Code
	//Use:		Toggles the player into admin chat. Every message they
        //              send will be piped to admin chat.
	//=====================================================================
    public static int adminChatToggle(Player player, String[] args)
	{
	    if(vMinecraftSettings.getInstance().adminChatToggle())
	    {
			//If the player is already toggled for admin chat, remove them
			if (vMinecraftSettings.getInstance().isAdminToggled(player.getName())) {
	                    player.sendMessage(Colors.Red + "Admin Chat Toggle = off");
	                    vMinecraftSettings.getInstance().removeAdminToggled(player.getName());
			//Otherwise include them
		} else {
	                player.sendMessage(Colors.Blue + "Admin Chat Toggled on");
	                vMinecraftSettings.getInstance().addAdminToggled(player.getName());
			}
	       return EXIT_SUCCESS;		
	    }
	    return EXIT_FAIL;
	}
	//=====================================================================
	//Function:	heal (/heal)
	//Input:	Player player: The player using the command
    //			String[] args: The arguments for the command. Should be a
    //						   player name or blank
	//Output:	int: Exit Code
	//Use:		Heals yourself or a specified player.
	//=====================================================================
    public static int heal(Player player, String[] args)
    {
        if(vMinecraftSettings.getInstance().cmdHeal())
        {
        	//If a target wasn't specified, heal the user.
            if (args.length < 1){
            	player.setHealth(20);
            	player.sendMessage("Your health is restored");
            //If a target was specified, try to find them and then heal them
            //Otherwise report the error
            } else if (args.length > 0){
            	Player playerTarget = etc.getServer().matchPlayer(args[0]);
            		
            	if (playerTarget != null){
            		playerTarget.setHealth(20);
            		player.sendMessage(Colors.Blue + "You have healed " + vMinecraftChat.getName(playerTarget));
            		playerTarget.sendMessage(Colors.Blue + "You have been healed by " + vMinecraftChat.getName(player));
            	}
            	else if (playerTarget == null){
            		player.sendMessage(Colors.Rose + "Couldn't find that player");
            	}
            }
    		return EXIT_SUCCESS;
        }
        return EXIT_FAIL;
	}

	//=====================================================================
	//Function:	suicide (/suicide, /wrists)
	//Input:	Player player: The player using the command
    //			String[] args: Ignored
	//Output:	int: Exit Code
	//Use:		Kills yourself
	//=====================================================================
    public static int suicide(Player player, String[] args)
    {
        if(vMinecraftSettings.getInstance().cmdSuicide())
        {
        	//Set your health to 0. Not much to it.
            player.setHealth(0);
            return EXIT_SUCCESS;
        }
        return EXIT_FAIL;
    }
    
	//=====================================================================
	//Function:	teleport (/tp)
	//Input:	Player player: The player using the command
    //			String[] args: The arguments for the command. Should be a
    //						   player name
	//Output:	int: Exit Code
	//Use:		Teleports the user to another player
	//=====================================================================
	public static int teleport(Player player, String[] args)
	{
		//Get if the command is enabled
		if(vMinecraftSettings.getInstance().cmdTp())
		{
			//Make sure a player has been specified and return an error if not
			if (args.length < 1) {
				player.sendMessage(Colors.Rose + "Correct usage is: /tp [player]");
			} else {

				//Find the player by name
				Player playerTarget = etc.getServer().matchPlayer(args[0]);
				
				//Target player isn't found
				if(playerTarget == null)
					player.sendMessage(Colors.Rose + "Can't find user "
							+ args[0] + ".");
				//If it's you, return witty message
				else if (player.getName().equalsIgnoreCase(args[0]))
					player.sendMessage(Colors.Rose + "You're already here!");
					
				//If the player is higher rank than you, inform the user
				else if (!player.hasControlOver(playerTarget))
					player.sendMessage(Colors.Red +
							"That player has higher permissions than you.");
				
				//If the player exists transport the user to the player
				else {
					log.log(Level.INFO, player.getName() + " teleported to " +
							playerTarget.getName());
					player.teleportTo(playerTarget);
					
				//Otherwise inform the user that the player doesn't exist
				}
			}
			return EXIT_SUCCESS;
		}
		return EXIT_FAIL;
	}
    
	//=====================================================================
	//Function:	masstp (/masstp)
	//Input:	Player player: The player using the command
    //			String[] args: Should be empty or is ignored
	//Output:	int: Exit Code
	//Use:		Teleports all players to the user
	//=====================================================================
	public static int masstp(Player player, String[] args)
	{
		//If the command is enabled
		if(vMinecraftSettings.getInstance().cmdMasstp()) {
			//Go through all players and move them to the user
			for (Player p : etc.getServer().getPlayerList()) {
				if (!p.hasControlOver(player)) {
					p.teleportTo(player);
				}
			}
			//Inform the user that the command has executed successfully
			player.sendMessage(Colors.Blue+"Summoning successful.");
			
			return EXIT_SUCCESS;
		}
		return EXIT_FAIL;
	}
    
	//=====================================================================
	//Function:	tphere (/tphere)
	//Input:	Player player: The player using the command
    //			String[] args: The arguments for the command. Should be a
    //						   player name
	//Output:	int: Exit Code
	//Use:		Teleports the user to another player
	//=====================================================================
	public static int tphere(Player player, String[] args)
	{
		//Check if the command is enabled.
		if (vMinecraftSettings.getInstance().cmdTphere()) {
			//Make sure a player is specified
			if (args.length < 1) {
				player.sendMessage(Colors.Rose + "Correct usage is: /tphere [player]");
			} else {
				//Get the player by name
				Player playerTarget = etc.getServer().matchPlayer(args[0]);
				
				//If the target doesn't exist
				if(playerTarget == null)
					player.sendMessage(Colors.Rose + "Can't find user " + args[0] + ".");
				//If the player has a higher rank than the user, return error
				else if (!player.hasControlOver(playerTarget)) 
					player.sendMessage(Colors.Red + "That player has higher permissions than you.");
				//If the user teleports themselves, mock them
				else if (player.getName().equalsIgnoreCase(args[0])) 
					player.sendMessage(Colors.Rose + "Wow look at that! You teleported yourself to yourself!");
				//If the target exists, teleport them to the user
				 else {
					log.log(Level.INFO, player.getName() + " teleported " + player.getName() + " to their self.");
					playerTarget.teleportTo(player);
				}
			}
			return EXIT_SUCCESS;
		}
		return EXIT_FAIL;
	}
    
	//=====================================================================
	//Function:	reload (/reload)
	//Input:	Player player: The player using the command
    //			String[] args: Ignored
	//Output:	int: Exit Code
	//Use:		Reloads the settings for vMinecraft
	//=====================================================================
	public static int reload(Player player, String[] args)
	{
		vMinecraftSettings.getInstance().loadSettings();
		return EXIT_FAIL;
	}

	//=====================================================================
	//Function:	rules (/rules)
	//Input:	Player player: The player using the command
    //			String[] args: Ignored
	//Output:	int: Exit Code
	//Use:		Lists the rules
	//=====================================================================
	public static int rules(Player player, String[] args)
	{
		//If the rules exist
		if(vMinecraftSettings.getInstance().cmdRules()
				&& vMinecraftSettings.getInstance().getRules().length > 0) {
			
			//Apply QuakeCode Colors to the rules
			String[] rules = vMinecraftChat.applyColors(
					vMinecraftSettings.getInstance().getRules());
			//Display them
			for (String str : rules ) {
				if(!str.isEmpty())
					player.sendMessage(Colors.Blue + str);
				else
					player.sendMessage(Colors.Blue + "!!!The Rules Have Not Been Set!!!");
			}
			return EXIT_SUCCESS;
		}
		return EXIT_FAIL;
	}

	//=====================================================================
	//Function:	fabulous (/fabulous)
	//Input:	Player player: The player using the command
    //			String[] args: The message to apply the effect to
	//Output:	int: Exit Code
	//Use:		Makes the text rainbow colored
	//=====================================================================
	public static int fabulous(Player player, String[] args)
	{
		//If the command is enabled
		if(vMinecraftSettings.getInstance().cmdFabulous()) {
			
			//Format the name
			String playerName = Colors.White + "<"
					+ vMinecraftChat.getName(player) + Colors.White +"> ";
			//Make sure a message has been specified
			if (args.length < 1) {return EXIT_FAIL;}
			String str  = " ";
			
			//Merge the message again
			str = etc.combineSplit(0, args, " ");
			
			//Output for server
			log.log(Level.INFO, player.getName()+" fabulously said \""+ str+"\"");
			
			//Prepend the player name and cut into lines.
			vMinecraftChat.gmsg(player, playerName + vMinecraftChat.rainbow(str));

			return EXIT_SUCCESS;
		}
		return EXIT_FAIL;
	}

	//=====================================================================
	//Function:	whois (/whois)
	//Input:	Player player: The player using the command
    //			String[] args: The player to find info on
	//Output:	int: Exit Code
	//Use:		Displays information about the player specified
	//=====================================================================
	public static int whois(Player player, String[] args)
	{
		//If the command is enabled
		if (vMinecraftSettings.getInstance().cmdWhoIs()) {
			//If a player is specified
			if (args.length < 1) 
				player.sendMessage(Colors.Rose + "Usage is /whois [player]");
			else {
				//Get the player by name
				Player playerTarget = etc.getServer().matchPlayer(args[0]);
				
				//If the player exists
				if (playerTarget != null){

					//Displaying the information
					player.sendMessage(Colors.Blue + "Whois results for " +
							vMinecraftChat.getName(playerTarget));
					//Group
					for(String group: playerTarget.getGroups())
					player.sendMessage(Colors.Blue + "Groups: " + group);
					//Admin
					player.sendMessage(Colors.Blue+"Admin: " +
							String.valueOf(playerTarget.isAdmin()));
					//IP
					player.sendMessage(Colors.Blue+"IP: " + playerTarget.getIP());
					//Restrictions
					player.sendMessage(Colors.Blue+"Can ignore restrictions: " +
							String.valueOf(playerTarget.canIgnoreRestrictions()));

				//Give the user an error if the player doesn't exist
				} else {
					player.sendMessage(Colors.Rose+"Player not found.");
				}
			}
			return EXIT_SUCCESS;
		}
		return EXIT_SUCCESS;
	}

	//=====================================================================
	//Function:	who (/who)
	//Input:	Player player: The player using the command
    //			String[] args: Ignored
	//Output:	int: Exit Code
	//Use:		Displays the connected players
	//=====================================================================
	public static int who(Player player, String[] args)
	{
		//If the command is enabled
		if (vMinecraftSettings.getInstance().cmdWho()) {
			//Loop through all players counting them and adding to the list
			int count=0;
			String tempList = "";
			for( Player p : etc.getServer().getPlayerList())
			{
				if(p != null){
					if(count == 0)
						tempList += vMinecraftChat.getName(p);
					else
						tempList += Colors.White + ", " + vMinecraftChat.getName(p);
					count++;
				}
			}
			//Get the max players from the config
			PropertiesFile server = new PropertiesFile("server.properties");
			try {
				server.load();
			} catch (IOException e) {
				e.printStackTrace();
			}
			int maxPlayers = server.getInt("max-players");
			
			//Output the player list
			vMinecraftChat.sendMessage(player, player, Colors.Rose + "Player List ("
					+ count + "/" + maxPlayers +"): " + tempList);
			
			return EXIT_SUCCESS;
		}
		return EXIT_FAIL;
	}

	//=====================================================================
	//Function:	say (/say)
	//Input:	Player player: The player using the command
    //			String[] args: The message to apply the effect to
	//Output:	int: Exit Code
	//Use:		Announces the message to all players
	//=====================================================================
	public static int say(Player player, String[] args)
	{
		//If the command is enabled
		if (vMinecraftSettings.getInstance().cmdSay()) {   
			//Make sure a message is supplied or output an error
			if (args.length < 1) {
				player.sendMessage(Colors.Rose + "Usage is /say [message]");
			}
			//Display the message globally
			vMinecraftChat.gmsg(player, Colors.Yellow + etc.combineSplit(0, args, " "));
			return EXIT_SUCCESS;
		}
		return EXIT_FAIL;
	}

	//=====================================================================
	//Function:	slay (/slay)
	//Input:	Player player: The player using the command
    //			String[] args: The target for the command
	//Output:	int: Exit Code
	//Use:		Kill the target player
	//=====================================================================
	public static int slay(Player player, String[] args)
	{
		//Check if the command is enabled
		if(vMinecraftSettings.getInstance().cmdEzModo()) {
			//Get the player by name
			Player playerTarget = etc.getServer().matchPlayer(args[0]);
			//If the player doesn't exist don't run
			if(playerTarget == null)
				return EXIT_FAIL;
			//If the player isn't invulnerable kill them
			if (!vMinecraftSettings.getInstance().isEzModo(playerTarget.getName())) {
				playerTarget.setHealth(0);
				vMinecraftChat.gmsg(player, vMinecraftChat.getName(player)
						+ Colors.LightBlue + " has slain "
						+ vMinecraftChat.getName(playerTarget));
			//Otherwise output error to the user
			} else {
				player.sendMessage(Colors.Rose + "That player is currently in ezmodo! Hahahaha");
			}
			return EXIT_SUCCESS;
		}
		return EXIT_FAIL;
	}

	//=====================================================================
	//Function:	invuln (/ezmodo)
	//Input:	Player player: The player using the command
    //			String[] args: The target for the command
	//Output:	int: Exit Code
	//Use:		Kill the target player
	//=====================================================================
	public static int invuln(Player player, String[] args)
	{
		//If the command is enabled
		if (vMinecraftSettings.getInstance().cmdEzModo()) {
			//If the player is already invulnerable, turn ezmodo off.
			if (vMinecraftSettings.getInstance().isEzModo(player.getName())) {
				player.sendMessage(Colors.Red + "ezmodo = off");
				vMinecraftSettings.getInstance().removeEzModo(player.getName());
			//Otherwise make them invulnerable
			} else {
				player.sendMessage(Colors.LightBlue + "eh- maji? ezmodo!?");
				player.sendMessage(Colors.Rose + "kimo-i");
				player.sendMessage(Colors.LightBlue + "Easy Mode ga yurusareru no wa shougakusei made dayo ne");
				player.sendMessage(Colors.Red + "**Laughter**");
				vMinecraftSettings.getInstance().addEzModo(player.getName());
			}
            return EXIT_SUCCESS;
		}
		return EXIT_FAIL;
	}

	//=====================================================================
	//Function:	ezlist (/ezlist)
	//Input:	Player player: The player using the command
    //			String[] args: Ignored
	//Output:	int: Exit Code
	//Use:		List all invulnerable players
	//=====================================================================
	public static int ezlist(Player player, String[] args)
	{
		//If the feature is enabled list the players
        if(vMinecraftSettings.getInstance().cmdEzModo()) {
            player.sendMessage("Ezmodo: " + vMinecraftSettings.getInstance().ezModoList());
            return EXIT_SUCCESS;
        }
        return EXIT_FAIL;
	}

	//=====================================================================
	//Function:	modifySplit (/modify)
	//Input:	Player player: The player using the command
    //			String[] args: Player, Command, Arguments
	//Output:	int: Exit Code
	//Use:		List all invulnerable players
	//=====================================================================
	public static int modifySplit(Player player, String[] args)
	{
		//Exploit fix for people giving themselves commands
		if(args[2].equals("commands")){
			return EXIT_FAIL;
                }
		return EXIT_CONTINUE;
	}

	//=====================================================================
	//Function:	Time Reverse
	//Input:	long time: The time to reverse to.
	//Output:	int: Exit Code
	//Use:		List all invulnerable players
	//=====================================================================
	public static int timeReverse(long tarTime)
	{
		long curTime = etc.getServer().getRelativeTime();
		//if(cur)
		return EXIT_SUCCESS;
	}
}

//=====================================================================
//Class:	commandList
//Use:		The list of commands that will be checked for
//Author:	cerevisiae
//=====================================================================
class commandList {
	ArrayList<command> commands;
	protected static final Logger log = Logger.getLogger("Minecraft");
	static final int EXIT_FAIL = 0,
					 EXIT_SUCCESS = 1,
					 EXIT_CONTINUE = 2;
  
	//=====================================================================
	//Function:	commandList
	//Input:	None
	//Output:	None
	//Use:		Initialize the array of commands
	//=====================================================================
	public commandList(){
		commands = new ArrayList<command>();
	}

	//=====================================================================
	//Function:	register
	//Input:	String name: The name of the command
	//			String func: The function to be called
	//Output:	boolean: Whether the command was input successfully or not
	//Use:		Registers a command to the command list for checking later
	//=====================================================================
	public boolean register(String name, String func)
	{
		//Check to make sure the command doesn't already exist
		for(command temp : commands)
			if(temp.getName().equalsIgnoreCase(name))
				return false;

		//Add the new function to the list
		commands.add(new command(name, func));
		
		//exit successfully
		return true;
	}

	//=====================================================================
	//Function:	register
	//Input:	String name: The name of the command
	//			String func: The function to be called
	//			String info: The information for the command to put in help
	//Output:	boolean: Whether the command was input successfully or not
	//Use:		Registers a command to the command list for checking later
	//=====================================================================
	public boolean register(String name, String func, String info){
		//Add to the /help list
		etc.getInstance().addCommand(name, info);
		
		//Finish registering
		return register(name, func);
	}
	
	//=====================================================================
	//Function:	register
	//Input:	String name: The name of the command
	//			String func: The function to be called
	//Output:	boolean: Whether the command was input successfully or not
	//Use:		Registers a command to the command list for checking later
	//=====================================================================
	public boolean registerAlias(String name, String com)
	{
		//Check to make sure the command doesn't already exist
		for(command temp : commands)
			if(temp.getName().equalsIgnoreCase(name))
				return false;

		//Add the new function to the list
		commands.add(new commandRef(name, com));
		
		//exit successfully
		return true;
	}
	
	//=====================================================================
	//Function:	registerMessage
	//Input:	String name: The name of the command
	//			String msg: The message to be displayed
	//			boolean admin: If the message is displayed to admins only
	//Output:	boolean: Whether the command was input successfully or not
	//Use:		Registers a command to the command list for checking later
	//=====================================================================
	public boolean registerMessage(String name, String msg, String clr, int args, boolean admin)
	{
		//Check to make sure the command doesn't already exist
		for(command temp : commands)
			if(temp.getName().equalsIgnoreCase(name))
				return false;

		//Add the new function to the list
		commands.add(new commandAnnounce(name, msg, clr, args, admin));
		
		//exit successfully
		return true;
	}

	//=====================================================================
	//Function:	call
	//Input:	String name: The name of the command to be run
	//Output:	boolean: If the command was called successfully
	//Use:		Attempts to call a command
	//=====================================================================
	public int call(String name, Player player, String[] arg){
		//Make sure the user has access to the command
		if(!player.canUseCommand(name)) {
			return EXIT_FAIL;
		}
		//Search for the command
		for(command cmd : commands)
		{
			//When found
			if(cmd.getName().equalsIgnoreCase(name))
			{
				try {
					//Call the command and return results
					return cmd.call(player, arg);
				} catch (SecurityException e) {
					log.log(Level.SEVERE, "Exception while running command", e);
				} catch (IllegalArgumentException e) {
					log.log(Level.SEVERE, "The Command Entered Doesn't Exist", e);
					return EXIT_FAIL;
				}
			}
		}
		
		//Something went wrong
		return EXIT_FAIL;
	}
	
	
	
	//=====================================================================
	//Class:	command
	//Use:		The specific command
	//Author:	cerevisiae
	//=====================================================================
	private class command
	{
		private String commandName;
		private String function;

		//=====================================================================
		//Function:	command
		//Input:	None
		//Output:	None
		//Use:		Initialize the command
		//=====================================================================
		public command(String name, String func){
			commandName = name;
			function = func;
		}


		//=====================================================================
		//Function:	getName
		//Input:	None
		//Output:	String: The command name
		//Use:		Returns the command name
		//=====================================================================
		public String getName(){return commandName;}


		//=====================================================================
		//Function:	call
		//Input:	String[] arg: The arguments for the command
		//Output:	boolean: If the command was called successfully
		//Use:		Attempts to call the command
		//=====================================================================
		int call(Player player, String[] arg)
		{
			
				Method m;
				try {
					m = vMinecraftCommands.class.getMethod(function, Player.class, String[].class);
					m.setAccessible(true);
					return (Integer) m.invoke(null, player, arg);
				} catch (SecurityException e) {
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
				return 1;
		}
	}
	
	//=====================================================================
	//Class:	commandRef
	//Use:		A command referencing another command
	//Author:	cerevisiae
	//=====================================================================
	private class commandRef extends command
	{
		private String reference;
		private String[] args;

		//=====================================================================
		//Function:	command
		//Input:	String name: The command name
		//			String com: The command to run
		//Output:	None
		//Use:		Initialize the command
		//=====================================================================
		public commandRef(String name, String com){
			super(name, "");
			
			//Get the reference name
			String[]temp = com.split(" ");
			reference = temp[0];
			
			//Get the arguments
			args = new String[temp.length - 1];
			System.arraycopy(temp, 1, args, 0, temp.length - 1);
		}


		//=====================================================================
		//Function:	call
		//Input:	String[] arg: The arguments for the command
		//Output:	boolean: If the command was called successfully
		//Use:		Attempts to call the command
		//=====================================================================
		int call(Player player, String[] arg)
		{
			String[] temp = new String[0];
			int lastSet = 0,
			argCount = 0;
			
			//If there are args set with the function
			if(args != null && args.length > 0) {
				temp = new String[args.length];
				System.arraycopy(args, 0, temp, 0, args.length);
				//Insert the arguments into the pre-set arguments
				for(String argument : temp)
				{
					if(argument.startsWith("%") && argument.length() > 1)
					{
						int argNum = Integer.parseInt(argument.substring(1));
						if( argNum < arg.length )
						{
							temp[lastSet] = arg[argNum];
							argCount++;
						}
					}
					lastSet++;
				}
			}
			
			//If there are args being input
			if(arg.length > 0) {
				//Append the rest of the arguments to the argument array
				if(lastSet < temp.length + arg.length - argCount)
				{
					String[] temp2 = new String[temp.length + arg.length - argCount];
					System.arraycopy(temp, 0, temp2, 0, temp.length);
					System.arraycopy(arg, argCount, temp2,
						temp.length, arg.length - argCount);
					temp = temp2;
				}
				
				log.log(Level.INFO, reference + " " + etc.combineSplit(0, temp, " "));
			//Call the referenced command
				player.command(reference + " " + etc.combineSplit(0, temp, " "));
			} else
				player.command(reference);
			return EXIT_SUCCESS;
		}
	}
	
	//=====================================================================
	//Class:	commandAnnounce
	//Use:		Announces when a command is used
	//Author:	cerevisiae
	//=====================================================================
	private class commandAnnounce extends command
	{
		private String message;
		private boolean admin;
		private int minArgs;
		private String color;

		//=====================================================================
		//Function:	commandAnnounce
		//Input:	String name: The command name
		//			String msg: The message to announce
		//Output:	None
		//Use:		Initialize the command
		//=====================================================================
		public commandAnnounce(String name, String msg, String clr, int args, boolean admn){
			super(name, "");
			message = msg;
			admin = admn;
			minArgs = args;
			color = clr;
		}


		//=====================================================================
		//Function:	call
		//Input:	String[] arg: The arguments for the command
		//Output:	boolean: If the command was called successfully
		//Use:		Attempts to call the command
		//=====================================================================
		int call(Player player, String[] arg)
		{
			//Make sure the player can use the command first
			if(!player.canUseCommand(super.commandName))
				return EXIT_FAIL;
			
			//Make sure the command is long enough to fire
			if(minArgs < arg.length)
				return EXIT_FAIL;
			
			if(vMinecraftSettings.getInstance().globalmessages())
			{
				//Split up the message
				String[] temp = message.split(" ");
				
				//Insert the arguments into the message
				int i = 0;
				for(String argument : temp)
				{
					if(argument.startsWith("%") && argument.length() > 1)
					{
						char position = argument.charAt(1);
						//Replace %p with the player name
						if(position == 'p')
							temp[i] = vMinecraftChat.getName(player) + color;
						else if( Character.isDigit(position) && Character.getNumericValue(position) < arg.length )
						{
							//If the argument is specified to be a player insert it if the
							//player is found or exit if they aren't
							if(argument.length() > 2 && argument.charAt(2) == 'p')
							{
								Player targetName = etc.getServer().matchPlayer(arg[Character.getNumericValue(position)]);
								if(targetName != null)
									temp[i] = vMinecraftChat.getName(targetName) + color;
								else
									return EXIT_FAIL;
							}
							//Replace %# with the argument at position #
							else
								temp[i] = arg[Character.getNumericValue(position)];
						}
					}
					i++;
				}
				message = etc.combineSplit(0, temp, " ");
				
				//If it's an admin message only
				if(admin)
				{
					for (Player p: etc.getServer().getPlayerList()) {
						//If p is not null
						if (p != null) {
							//And if p is an admin or has access to adminchat send message
							if (p.isAdmin()) {
								vMinecraftChat.sendMessage(player, p, color + message);
							}
						}
					}
				} else
					vMinecraftChat.gmsg(player, message);
			}
			return EXIT_FAIL;
		}
	}
}