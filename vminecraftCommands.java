import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

//=====================================================================
//Class:	vminecraftCommands
//Use:		Encapsulates all commands added by this mod
//Author:	nos, trapalice, cerevisiae
//=====================================================================
public class vminecraftCommands{
	//Log output
    protected static final Logger log = Logger.getLogger("Minecraft");
    static final int EXIT_FAIL = 0,
    		  		 EXIT_SUCCESS = 1,
    		  		 EXIT_CONTINUE = 2;
    
    //The list of commands for vminecraft
    public static commandList cl = new commandList();

	//=====================================================================
	//Function:	loadCommands
	//Input:	None
	//Output:	None
	//Use:		Imports all the commands into the command list
	//=====================================================================
    public static void loadCommands(){
		//If we had commands we would add them here.
        cl.register("/tp", "teleport");
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
        cl.register("/suicide", "suicide", "kill yourself... you loser");

        cl.register("/modify", "modifySplit");

        cl.registerAlias("/playerlist", "/who");
        cl.registerAlias("/wrists", "/suicide");
    }

    //Heal
    public static int heal(Player player, String[] args)
    {
        if(vminecraftSettings.getInstance().cmdHeal())
        {
            if (args[1] == null){
            if (player.getHealth() < 20){
                vminecraftChat.gmsg("Your health is restored");
                return EXIT_SUCCESS;
            }
            else if (args[1] != null){
                Player playerTarget = etc.getServer().matchPlayer(args[1]);
                if (playerTarget != null){
                    playerTarget.setHealth(20);
                    vminecraftChat.gmsg(Colors.Blue + "You have healed " + playerTarget.getColor() + playerTarget.getName());
                    vminecraftChat.gmsg(Colors.Blue + "You have been healed by " + player.getColor() + player.getName());
                    return EXIT_SUCCESS;
                }
                else if (playerTarget == null){
                    vminecraftChat.gmsg(Colors.Rose + "Couldn't find that player");
                    return EXIT_FAIL;
                    }

                }
            }
        }
        return EXIT_FAIL;
    }
    //Suicide
    public static int suicide(Player player, String[] args)
    {
        if(vminecraftSettings.getInstance().cmdSuicide())
        {
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
		if(vminecraftSettings.getInstance().cmdTp())
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
		if(vminecraftSettings.getInstance().cmdMasstp()) {
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
		if (vminecraftSettings.getInstance().cmdTphere()) {
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
	//Use:		Reloads the settings for vminecraft
	//=====================================================================
	public static int reload(Player player, String[] args)
	{
		vminecraftSettings.getInstance().loadSettings();
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
		if(vminecraftSettings.getInstance().cmdRules()
				&& vminecraftSettings.getInstance().getRules().length != 0) {
			//Display them
			for (String str : vminecraftSettings.getInstance().getRules()) {
				if(str.isEmpty())
					player.sendMessage(Colors.Blue+str);
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
		if(vminecraftSettings.getInstance().cmdFabulous()) {
			
			//Format the name
			String playerName = Colors.White + "<"
					+ vminecraftChat.nameColor(player) + Colors.White +"> ";
			//Make sure a message has been specified
			if (args.length < 1) {return EXIT_FAIL;}
			String str  = " ";
			
			//Merge the message again
			str = etc.combineSplit(0, args, " ");
			
			//Output for server
			log.log(Level.INFO, player.getName()+" fabulously said \""+ str+"\"");
			
			//Prepend the player name and cut into lines.
			String[] message = vminecraftChat.wordWrap(playerName + str);

			//Output the message
			for(String msg: message)
			{
				if (msg.contains(playerName))
					vminecraftChat.gmsg( playerName
							+ vminecraftChat.rainbow(
									msg.substring(playerName.length())));
				else
					vminecraftChat.gmsg(vminecraftChat.rainbow(msg));
			}

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
		if (vminecraftSettings.getInstance().cmdWhoIs()) {
			//If a player is specified
			if (args.length < 1) 
				player.sendMessage(Colors.Rose + "Usage is /whois [player]");
			else {
				//Get the player by name
				Player playerTarget = null;
				for( Player p : etc.getServer().getPlayerList())
				{
					if (p.getName().equalsIgnoreCase(args[0]))
					{
						playerTarget = p;
					}
				}
				//If the player exists
				if (playerTarget != null){

					//Displaying the information
					player.sendMessage(Colors.Blue + "Whois results for " +
							vminecraftChat.nameColor(playerTarget));
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
		if (vminecraftSettings.getInstance().cmdWho()) {
			//Loop through all players counting them and adding to the list
			int count=0;
			String tempList = "";
			for( Player p : etc.getServer().getPlayerList())
			{
				if(p != null){
					if(count == 0)
						tempList += vminecraftChat.nameColor(p);
					else
						tempList += Colors.White + ", " + vminecraftChat.nameColor(p);
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
			String[] tempOut = vminecraftChat.wordWrap(Colors.Rose + "Player List ("
					+ count + "/" + maxPlayers +"): " + tempList);
			for(String msg: tempOut)
				player.sendMessage( msg );
			
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
		if (vminecraftSettings.getInstance().cmdSay()) {   
			//Make sure a message is supplied or output an error
			if (args.length < 1) {
				player.sendMessage(Colors.Rose + "Usage is /say [message]");
			}
			//Display the message globally
			vminecraftChat.gmsg(Colors.Yellow + etc.combineSplit(0, args, " "));
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
		if(vminecraftSettings.getInstance().cmdEzModo()) {
			//Get the player by name
			Player playerTarget = etc.getServer().matchPlayer(args[0]);
			//If the player doesn't exist don't run
			if(playerTarget == null)
				return EXIT_FAIL;
			//If the player isn't invulnerable kill them
			if (!vminecraftSettings.getInstance().isEzModo(playerTarget.getName())) {
				playerTarget.setHealth(0);
				vminecraftChat.gmsg(vminecraftChat.nameColor(player)
						+ Colors.LightBlue + " has slain "
						+ vminecraftChat.nameColor(playerTarget));
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
		if (vminecraftSettings.getInstance().cmdEzModo()) {
			//If the player is already invulnerable, turn ezmodo off.
			if (vminecraftSettings.getInstance().isEzModo(player.getName())) {
				player.sendMessage(Colors.Red + "ezmodo = off");
				vminecraftSettings.getInstance().removeEzModo(player.getName());
			//Otherwise make them invulnerable
			} else {
				player.sendMessage(Colors.LightBlue + "eh- maji? ezmodo!?");
				player.sendMessage(Colors.Rose + "kimo-i");
				player.sendMessage(Colors.LightBlue + "Easy Mode ga yurusareru no wa shougakusei made dayo ne");
				player.sendMessage(Colors.Red + "**Laughter**");
				vminecraftSettings.getInstance().addEzModo(player.getName());
				player.setHealth(vminecraftSettings.getInstance().ezModoHealth());
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
        if(vminecraftSettings.getInstance().cmdEzModo()) {
            player.sendMessage("Ezmodo: " + vminecraftSettings.getInstance().ezModoList());
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
		if(args[1].equals("commands"))
			return EXIT_FAIL;
		else if (args[1].equals("tag"))
			playerTag(player, args);
		return EXIT_CONTINUE;
	}

	//=====================================================================
	//Function:	playerTag (/modify player tag *)
	//Input:	Player player: The player using the command
    //			String[] args: Player, Command, Arguments
	//Output:	int: Exit Code
	//Use:		List all invulnerable players
	//=====================================================================
	public static int playerTag(Player player, String[] args)
	{
		return EXIT_SUCCESS;
	}

    //Disable using /modify to add commands (need to make a boolean settings for this)

    //ezlist
//ezmodo
	
	
	 /*
    //Promote
    if (vminecraftSettings.getInstance().cmdPromote() && split[0].equalsIgnoreCase("/promote")) {
if(split.length != 2)
{
	player.sendMessage(Colors.Rose + "Usage is /promote [Player]");

}

Player playerTarget = null;
    if(split.length==2){
for( Player p : etc.getServer().getPlayerList())
{
	if (p.getName().equalsIgnoreCase(split[1]))
	{
		playerTarget = p;
	}
}

if( playerTarget!=null)
{
        String playerTargetGroup[] = playerTarget.getGroups();
        String playerGroup[] = player.getGroups();
        player.sendMessage("Debug data:");
        player.sendMessage("PlayerTarget: "+playerTargetGroup[0]);
        player.sendMessage("Player: "+playerGroup[0]);
	if(playerTargetGroup[0].equals("admins"))
	{
		player.sendMessage(Colors.Rose + "You can not promote " + split[1] + " any higher.");
	}
	if(playerTargetGroup[0].equals("mods") && (playerGroup[0].equals("owner")))
	{
		playerTarget.setGroups(ranks.Admins);
		etc.getInstance().getDataSource().modifyPlayer(playerTarget);
		String message = Colors.Yellow + split[1] + " was promoted to" + Colors.Rose + " Admin";
		other.gmsg(message);
	}
	else if (playerTargetGroup[0].equals("trusted") && (playerGroup[0].equals("admins") || playerGroup[0].equals("owner")))
	{
		playerTarget.setGroups(ranks.Mods);
                    playerTargetGroup[0]="Mods";
		etc.getInstance().getDataSource().modifyPlayer(playerTarget);
		String message = Colors.Yellow + split[1] + " was promoted to" + Colors.DarkPurple + " Mod";
		other.gmsg(message);
	}
	else if (playerTargetGroup[0].equals("default") && (playerGroup[0].equals("mods") || playerGroup[0].equals("admins") || player.isInGroup("owner")))
	{
		playerTarget.setGroups(ranks.Trusted);
                    etc.getInstance().getDataSource().modifyPlayer(playerTarget);
                    String message = Colors.Yellow + split[1] + " was promoted to" + Colors.LightGreen + " Trusted";
                    other.gmsg(message);
	}
            return true;
}
else{
	player.sendMessage(Colors.Rose + "Player not found");
}
log.log(Level.INFO, "Command used by " + player + " " + split[0] +" "+split[1]+" ");
}
    }
    //Demote
            if (vminecraftSettings.getInstance().cmdPromote() && split[0].equalsIgnoreCase("/promote"))
{
if(split.length != 2)
{
	player.sendMessage(Colors.Rose + "Usage is /demote [Player]");
}

Player playerTarget = null;

for( Player p : etc.getServer().getPlayerList())
{
	if (p.getName().equalsIgnoreCase(split[1]))
	{
		playerTarget = p;
	}
}

if( playerTarget!=null)
{
	if(playerTarget.isInGroup("admins") && (player.isInGroup("superadmins")))
	{
                playerTarget.setGroups(ranks.Mods);
                etc.getInstance().getDataSource().modifyPlayer(playerTarget);
                String message = Colors.Yellow + split[1] + " was demoted to" + Colors.DarkPurple + " Mod";
                other.gmsg(message);
	}
	if(playerTarget.isInGroup("mods") && (player.isInGroup("admins") || player.isInGroup("superadmins")))
	{
		playerTarget.setGroups(ranks.Trusted);
		etc.getInstance().getDataSource().modifyPlayer(playerTarget);
		String message = Colors.Yellow + split[1] + " was demoted to" + Colors.LightGreen + " Trusted";
		other.gmsg(message);
	}
	else if (playerTarget.isInGroup("trusted") && (player.isInGroup("mods") || player.isInGroup("superadmins") || player.isInGroup("admins")))
	{
		playerTarget.setGroups(ranks.Def);
		etc.getInstance().getDataSource().modifyPlayer(playerTarget);
		String message = Colors.Yellow + split[1] + " was demoted to" + Colors.White + " Default";
		other.gmsg(message);
	}
	else if (playerTarget.isInGroup("default") && (player.isInGroup("mods") || player.isInGroup("admins") || player.isInGroup("superadmins")))
	{
            player.sendMessage(Colors.Rose + "You can not demote " + split[1] + " any lower.");
	}
}
else{
	player.sendMessage(Colors.Rose + "Player not found");
}
log.log(Level.INFO, "Command used by " + player + " " + split[0] +" "+split[1]+" ");
    return true;
}*/
}

//=====================================================================
//Class:	commandList
//Use:		The list of commands that will be checked for
//Author:	cerevisiae
//=====================================================================
class commandList {
	command[] commands;
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
		commands = new command[0];
	}

	//=====================================================================
	//Function:	register
	//Input:	String name: The name of the command
	//			String func: The function to be called
	//Output:	boolean: Whether the command was input successfully or not
	//Use:		Registers a command to the command list for checking later
	//=====================================================================
	public boolean register(String name, String func){
		
		//If the command list isn't empty
		if(commands.length > 0)
		{
			//Check to make sure the command doesn't already exist
			for(int i = 0; i < commands.length; i++)
				if(commands[i].getName().equalsIgnoreCase(name))
					return false;
			
			//Create a new temp array
			command[] temp = new command[commands.length + 1];
			//Copy the old command list over
			System.arraycopy(commands, 0, temp, 0, commands.length);
			//Set commands to equal the new array
			commands = temp;
		} else {
			commands = new command[1];
		}

		//Add the new function to the list
		commands[commands.length - 1] = new command(name, func);
		
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
	public boolean registerAlias(String name, String com, String[] args){
		
		//If the command list isn't empty
		if(commands.length > 0)
		{
			//Check to make sure the command doesn't already exist
			for(int i = 0; i < commands.length; i++)
				if(commands[i].getName().equalsIgnoreCase(name))
					return false;
			
			//Create a new temp array
			command[] temp = new command[commands.length + 1];
			//Copy the old command list over
			System.arraycopy(commands, 0, temp, 0, commands.length);
			//Set commands to equal the new array
			commands = temp;
		} else {
			commands = new command[1];
		}

		//Add the new function to the list
		commands[commands.length - 1] = new commandRef(name, com, args);
		
		//exit successfully
		return true;
	}
	
	//=====================================================================
	//Function:	register
	//Input:	String name: The name of the command
	//			String func: The function to be called
	//Output:	boolean: Whether the command was input successfully or not
	//Use:		Registers a command to the command list for checking later
	//=====================================================================
	public boolean registerAlias(String name, String com){
		
		//If the command list isn't empty
		if(commands.length > 0)
		{
			//Check to make sure the command doesn't already exist
			for(int i = 0; i < commands.length; i++)
				if(commands[i].getName().equalsIgnoreCase(name))
					return false;
			
			//Create a new temp array
			command[] temp = new command[commands.length + 1];
			//Copy the old command list over
			System.arraycopy(commands, 0, temp, 0, commands.length);
			//Set commands to equal the new array
			commands = temp;
		} else {
			commands = new command[1];
		}
		
		//Add the new function to the list
		commands[commands.length - 1] = new commandRef(name, com);
		
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
		private class command{
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
						m = vminecraftCommands.class.getMethod(function, Player.class, String[].class);
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
		private class commandRef extends command{
			private String reference;
			private String[] args;

			//=====================================================================
			//Function:	command
			//Input:	String name: The command name
			//			String com: The command to run
			//			String[] arg: the arguments to apply
			//Output:	None
			//Use:		Initialize the command
			//=====================================================================
			public commandRef(String name, String com, String[] arg){
				super(name, "");
				reference = com;
				args = arg;
			}

			//=====================================================================
			//Function:	command
			//Input:	String name: The command name
			//			String com: The command to run
			//Output:	None
			//Use:		Initialize the command
			//=====================================================================
			public commandRef(String name, String com){
				super(name, "");
				reference = com;
				args = null;
			}


			//=====================================================================
			//Function:	call
			//Input:	String[] arg: The arguments for the command
			//Output:	boolean: If the command was called successfully
			//Use:		Attempts to call the command
			//=====================================================================
			int call(Player player, String[] arg)
			{
				if(args != null) {
					String[] temp = new String[args.length];
					System.arraycopy(args, 0, temp, 0, args.length);
					//Insert the arguments into the pre-set arguments
					int lastSet = 0,
						argCount = 0;
					for(String argument : temp)
					{
						if(argument.startsWith("%"))
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
					//Append the rest of the arguments to the argument array
					if(lastSet < temp.length + arg.length - argCount)
					{
						String[] temp2 = new String[temp.length + arg.length - argCount];
						System.arraycopy(temp, 0, temp2, 0, temp.length);
						System.arraycopy(arg, argCount, temp2,
							temp.length, arg.length - argCount);
						temp = temp2;
					}
					
				//Call the referenced command
					player.command(reference + " " + etc.combineSplit(0, temp, " "));
				} else
					player.command(reference);
				return EXIT_SUCCESS;
			}
		}
}