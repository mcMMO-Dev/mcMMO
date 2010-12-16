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
    	
    	//register: Registers a function for use with a command
    	//String: The command that will be used
    	//String: The name of the function that will be called when
    	//		  the command is used
    	//String(Optional): The help menu description
    	
    	//Administrative
        cl.register("/prefix", "prefix", "Set your name color and prefix");
        cl.register("/rprefix", "removeTag", "Remove your name color and prefix");
        cl.register("/nick", "nickName", "Set your display name");
        cl.register("/rnick", "removeNick", "Reset your display name to your account name");
        cl.register("/suffix", "suffix", "Set your suffix");
        cl.register("/rsuffix", "removeSuffix", "Remove your suffix");
        cl.register("/vminecraft", "vminecrafthelp");
        cl.register("/reload", "reload");
        cl.register("/whois", "whois", "/whois [user]");
        cl.register("/say", "say");
        cl.register("/a", "adminChatToggle", "Toggle admin chat for every message");
        cl.register("/modify", "modify");
        cl.register("/rules", "rules", "Displays the rules");
        cl.register("/who", "who");
        cl.register("/promote", "promote", "Promote a player one rank");
        cl.register("/demote", "demote", "Demote a player one rank");

        //Movement
        cl.register("/tp", "teleport");
        cl.register("/tphere", "tphere");
        cl.register("/masstp", "masstp", "Teleports those with lower permissions to you");

        //Health
        cl.register("/ezmodo", "invuln", "Toggle invulnerability");
        cl.register("/ezlist", "ezlist", "List invulnerable players");
        cl.register("/heal", "heal", "heal yourself or other players");
        cl.register("/suicide", "suicide", "Kill yourself... you loser");
        cl.register("/slay", "slay", "Kill target player");

        //Social
        cl.register("/colors", "colors", "Set your default chat color: /colors <Color Char>");
        cl.register("/me", "me");
        cl.register("/fabulous", "fabulous", "makes text SUUUPER");
        cl.register("/msg", "message", "Send a message to a player /msg [Player] [Message]");
        cl.register("/reply", "reply", "Reply to a player /reply [Message], Alias: /r");
        cl.register("/ignore", "addIgnored", "Adds a user to your ignore list");
        cl.register("/unignore", "removeIgnored", "Removes a user from your ignore list");
        cl.register("/ignorelist", "ignoreList", "Lists the players you have ignored");
        
        //registerAlias: Runs the second command when the first command is called
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
        cl.registerAlias("/t", "/msg");
        cl.registerAlias("/tell", "/msg");
        cl.registerAlias("/wrists", "/suicide");
        cl.registerAlias("/kill", "/suicide");
        cl.registerAlias("/ci", "/clearinventory");
        
        //registerMessage: Displays a message whenever a command is used
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
    }
    //=====================================================================
	//Function:	vminecrafthelp (/vhelp or /vminecraft)
	//Input:	Player player: The player using the command
	//Output:	int: Exit Code
	//Use:		Displays the current status of most vMinecraft settings
    //              and provides some useful tips.
	//=====================================================================
    public static int vminecrafthelp(Player player, String[] args){
        vMinecraftChat.sendMessage(player, player, Colors.Yellow
        		+ "Chat Settings");
        vMinecraftChat.sendMessage(player, player, Colors.DarkPurple 
        		+ "Admin Chat: " + vMinecraftSettings.getInstance()
        		.adminchat());
        vMinecraftChat.sendMessage(player, player, Colors.DarkPurple 
        		+ "FFF turns red: " + vMinecraftSettings.getInstance()
        		.FFF());
        vMinecraftChat.sendMessage(player, player, Colors.DarkPurple 
        		+ "Greentext After >: " + vMinecraftSettings.getInstance()
        		.greentext());
        vMinecraftChat.sendMessage(player, player, Colors.DarkPurple 
        		+ "Quake Color Script: " + vMinecraftSettings.getInstance()
        		.quakeColors());
        vMinecraftChat.sendMessage(player, player, Colors.Yellow 
        		+ "Enabled Commands are TRUE, disabled are FALSE");
        vMinecraftChat.sendMessage(player, player, Colors.DarkPurple 
        		+ "Command /ezmodo: " + vMinecraftSettings.getInstance()
        		.cmdEzModo());
        vMinecraftChat.sendMessage(player, player, Colors.DarkPurple 
        		+ "Command /fabulous: " + vMinecraftSettings.getInstance()
        		.cmdFabulous());
        vMinecraftChat.sendMessage(player, player, Colors.DarkPurple 
        		+ "Command /rules: " + vMinecraftSettings.getInstance()
        		.cmdRules());
        vMinecraftChat.sendMessage(player, player, Colors.DarkPurple 
        		+ "Command /heal: " + vMinecraftSettings.getInstance()
        		.cmdHeal());
        vMinecraftChat.sendMessage(player, player, Colors.DarkPurple 
        		+ "Command /masstp: " + vMinecraftSettings.getInstance()
        		.cmdMasstp());
        vMinecraftChat.sendMessage(player, player, Colors.DarkPurple 
        		+ "Command /say: " + vMinecraftSettings.getInstance()
        		.cmdSay());
        vMinecraftChat.sendMessage(player, player, Colors.DarkPurple 
        		+ "Command /suicide: " + vMinecraftSettings.getInstance()
        		.cmdSuicide());
        vMinecraftChat.sendMessage(player, player, Colors.DarkPurple 
        		+ "Command /whois: " + vMinecraftSettings.getInstance()
        		.cmdWhoIs());
        vMinecraftChat.sendMessage(player, player, Colors.DarkPurple 
        		+ "Command /tp won't work on higher ranked players: "
        		+ vMinecraftSettings.getInstance().cmdTp());
        vMinecraftChat.sendMessage(player, player, Colors.DarkPurple 
        		+ "Command /tphere won't work on higher ranked players: " 
        		+ vMinecraftSettings.getInstance().cmdTphere());
        vMinecraftChat.sendMessage(player, player, Colors.Yellow 
        		+ "Other Settings");
        vMinecraftChat.sendMessage(player, player, Colors.DarkPurple 
        		+ "Command /who: " + vMinecraftSettings.getInstance()
        		.cmdWho());
        vMinecraftChat.sendMessage(player, player, Colors.DarkPurple 
        		+ "COLORED PLAYER LIST IS DEPENDENT ON /who BEING TRUE!");
        vMinecraftChat.sendMessage(player, player, Colors.DarkPurple 
        		+ "Global Messages: " + vMinecraftSettings.getInstance()
        		.globalmessages());
        return EXIT_SUCCESS;
    }
    //=====================================================================
	//Function:	prefix (/prefix)
	//Input:	Player player: The player using the command
        //		String[] args: The name of the player
	//Output:	int: Exit Code
	//Use:		Freezes a player in place
	//=====================================================================
    public static int freeze(Player player, String[] args){
        if(player.canUseCommand("/freeze") && vMinecraftSettings.getInstance().freeze()){
            if (args.length < 2){
                vMinecraftChat.gmsg(Colors.Rose + "Usage is /freeze [Player]");
                return EXIT_SUCCESS;
            }
            Player other = etc.getServer().matchPlayer(args[0]);
            if (other == null)
            {
                vMinecraftChat.gmsg(Colors.Rose + "The player you specified could not be found");
                return EXIT_SUCCESS;
            }
            if(player != other && other.hasControlOver(player)){
                vMinecraftChat.gmsg(Colors.Rose + "The player you specified has a higher rank than you");
                return EXIT_SUCCESS;
            }
            if(vMinecraftSettings.frozenplayers.contains(other)){
                vMinecraftSettings.getInstance().removeFrozen(other.getName());
                vMinecraftChat.gmsg(player.getName() + Colors.Blue + " has unfrozen " + other.getName());
                return EXIT_SUCCESS;
            }
            vMinecraftSettings.getInstance().addFrozen(other.getName());
            vMinecraftChat.gmsg(player.getName() + Colors.Blue + " has frozen " + other.getName());
            return EXIT_SUCCESS;
        }
        return EXIT_SUCCESS;
    }
    //=====================================================================
	//Function:	prefix (/prefix)
	//Input:	Player player: The player using the command
    //			String[] args: The color and the prefix
	//Output:	int: Exit Code
	//Use:		Changes your name color and prefix
	//=====================================================================
    public static int prefix(Player player, String[] args){
    	
    	//if the player can prefix others
        if(player.canUseCommand("/prefixother") && vMinecraftSettings.getInstance().prefix()){
            
            //Check if there are enough arguments
            if(args.length < 2){
                vMinecraftChat.sendMessage(player, player, Colors.Rose + "Usage is /prefix [Player] [Color Code] <Tag>");
                player.sendMessage(Colors.DarkPurple + "Example: /prefix " + player.getName() + " e ^0[^a<3^0]");
                vMinecraftChat.sendMessage(player, player, Colors.DarkPurple + "This would produce a name like... " + Colors.Black + "[" + Colors.LightGreen + "<3" + Colors.Black + "]" + Colors.Yellow + player.getName());
                return EXIT_SUCCESS;
            }
            
            //Check if the player exists
            Player other = etc.getServer().matchPlayer(args[0]);
            if(other == null)
            {
                vMinecraftChat.sendMessage(player, player, Colors.Rose
                		+ "The player you specified could not be found");
                return EXIT_SUCCESS;
            }
            
            //Check if they are a higher rank than the other person
            if(player != other && other.hasControlOver(player))
            {
                vMinecraftChat.sendMessage(player, player, Colors.Rose
                		+ "The player you specified is a higher rank than you.");
                return EXIT_SUCCESS;
            }

            
            //Check if the prefix is too long
            if(vMinecraftChat.msgLength(args[1]) > 60)
            {
                vMinecraftChat.sendMessage(player, player, Colors.Rose
                		+ "The prefix you entered was too long.");
                return EXIT_SUCCESS;
            }
            if(args.length >= 2 && args[0] != null)
            {
                other.setPrefix(args[1]);
                player.sendMessage(Colors.Rose + "Name color changed");
                FlatFileSource ffs = new FlatFileSource();
                ffs.modifyPlayer(other);
            }
            
            if(args.length >= 3 && args[1] != null)
            {
               vMinecraftUsers.players.findProfile(other).setTag(args[2]);
	           player.sendMessage(Colors.LightGreen + "Prefix changed");
            }
            return EXIT_SUCCESS;
        }
        //If the player can set their prefix
        if(!player.canUseCommand("/prefix")&& vMinecraftSettings.getInstance().prefix()){
            return EXIT_FAIL;
        }
        
        //Check if there are enough arguments
        if(args.length < 1){
            vMinecraftChat.sendMessage(player, player, Colors.Rose + "Usage is /prefix [Color Code] <Tag>");
            player.sendMessage(Colors.DarkPurple + "Example: /prefix e ^0[^a<3^0]");
            vMinecraftChat.sendMessage(player, player, Colors.DarkPurple + "This would produce a name like... " + Colors.Black + "[" + Colors.LightGreen + "<3" + Colors.Black + "]" + Colors.Yellow + player.getName());
            return EXIT_SUCCESS;
        }       
        //Name color
        if(args.length >= 1 && args[0] != null){
            player.setPrefix(args[0]);
            player.sendMessage(Colors.Rose + "Name color changed");
        }
        //Prefix
        if(args.length >= 2 && args[1] != null){
        //Check if the prefix is too long        
	        if(vMinecraftChat.msgLength(args[1]) > 60)
	        {
	            vMinecraftChat.sendMessage(player, player, Colors.Rose
	            		+ "The prefix you entered was too long.");
	            return EXIT_SUCCESS;
	        }
	           vMinecraftUsers.players.findProfile(player).setTag(args[1]);
	           player.sendMessage(Colors.LightGreen + "Prefix changed");
        }
        return EXIT_SUCCESS;
    }
    
    //=====================================================================
	//Function:	removeTag (/rprefix)
	//Input:	Player player: The player using the command
    //			String[] args: Ignored
	//Output:	int: Exit Code
	//Use:		Removes your prefix
	//=====================================================================
    public static int removeTag(Player player, String[] args){
    	
    	//if the player can suffix others
        if(player.canUseCommand("/prefixother")&& vMinecraftSettings.getInstance().prefix()){
            if(args.length < 1){
                vMinecraftChat.sendMessage(player, player, Colors.Rose
                		+ "Usage is /rprefix [Player]");
                return EXIT_SUCCESS;
            }
            
            //Check if the player exists
            Player other = etc.getServer().matchPlayer(args[0]);
            if(other == null)
            {
                vMinecraftChat.sendMessage(player, player, Colors.Rose
                		+ "The player you specified could not be found");
                return EXIT_SUCCESS;
            }
            
            //Check if they are a higher rank than the other person
            if(player != other && other.hasControlOver(player))
            {
                vMinecraftChat.sendMessage(player, player, Colors.Rose
                		+ "The player you specified is a higher rank than you.");
                return EXIT_SUCCESS;
            }
            
            vMinecraftUsers.getProfile(other).setTag("");
	        player.sendMessage(Colors.LightGreen + "Prefix Removed");
            
            return EXIT_SUCCESS;
        }
        
        //Check if the player can set their own prefix.
        if(!player.canUseCommand("/prefix")&& vMinecraftSettings.getInstance().prefix()){
            return EXIT_FAIL;
        }
        if(args.length < 1){
            vMinecraftChat.sendMessage(player, player, Colors.Rose
            		+ "Usage is /rprefix");
            return EXIT_SUCCESS;
        }
        vMinecraftUsers.getProfile(player).setTag("");
        player.sendMessage(Colors.LightGreen + "Prefix Removed");
        
        return EXIT_SUCCESS;
    }
    
    //=====================================================================
	//Function:	nickName (/nick)
	//Input:	Player player: The player using the command
    //			String[] args: The color and the prefix
	//Output:	int: Exit Code
	//Use:		Changes your name
	//=====================================================================
    public static int nickName(Player player, String[] args){
    	
    	//if the player can nickname others
        if(player.canUseCommand("/nickother") && vMinecraftSettings.getInstance().nick()){
            if(args.length < 2){
                vMinecraftChat.sendMessage(player, player, Colors.Rose
                		+ "Usage is /nick [Player] [Name]");
                return EXIT_SUCCESS;
            }
            
            //Check if the nickname is too long
            if(vMinecraftChat.msgLength(args[1]) > 85)
            {
                vMinecraftChat.sendMessage(player, player, Colors.Rose
                		+ "The nick you entered was too long.");
                return EXIT_SUCCESS;
            }
            
            //Check if the player exists
            Player other = etc.getServer().matchPlayer(args[0]);
            if(other == null)
            {
                vMinecraftChat.sendMessage(player, player, Colors.Rose
                		+ "The player you specified could not be found");
                return EXIT_SUCCESS;
            }
            
            //Check if they are a higher rank than the other person
            if(player != other && other.hasControlOver(player))
            {
                vMinecraftChat.sendMessage(player, player, Colors.Rose
                		+ "The player you specified is a higher rank than you.");
                return EXIT_SUCCESS;
            }
            
            vMinecraftUsers.getProfile(other).setNick(args[1]);
            player.sendMessage(Colors.LightGreen + "Nickname Set");
            
            return EXIT_SUCCESS;
        }
        
        //Make sure they can nickname themselves
        if(!player.canUseCommand("/nick")){
            return EXIT_FAIL;
        }
        
        //Check if the nickname is too long
        if(vMinecraftChat.msgLength(args[1]) > 85)
        {
            vMinecraftChat.sendMessage(player, player, Colors.Rose
            		+ "The nick you entered was too long.");
            return EXIT_SUCCESS;
        }
        
        if(args.length < 1){
            vMinecraftChat.sendMessage(player, player, Colors.Rose
            		+ "Usage is /nick [Name]");
            return EXIT_SUCCESS;
        }
        vMinecraftUsers.getProfile(player).setNick(args[0]);
        player.sendMessage(Colors.LightGreen + "Nickname Set");
        
        return EXIT_SUCCESS;
    }
    
    //=====================================================================
	//Function:	removeNick (/rnick)
	//Input:	Player player: The player using the command
    //			String[] args: Ignored
	//Output:	int: Exit Code
	//Use:		Removes your nick
	//=====================================================================
    public static int removeNick(Player player, String[] args){
    	
    	//if the player can nick others
        if(player.canUseCommand("/nickother")&& vMinecraftSettings.getInstance().nick()){
            if(args.length < 1){
                vMinecraftChat.sendMessage(player, player, Colors.Rose
                		+ "Usage is /rnick [Player]");
                return EXIT_SUCCESS;
            }
            
            //Check if the player exists
            Player other = etc.getServer().matchPlayer(args[0]);
            if(other == null)
            {
                vMinecraftChat.sendMessage(player, player, Colors.Rose
                		+ "The player you specified could not be found");
                return EXIT_SUCCESS;
            }
            
            //Check if they are a higher rank than the other person
            if(player != other && other.hasControlOver(player))
            {
                vMinecraftChat.sendMessage(player, player, Colors.Rose
                		+ "The player you specified is a higher rank than you.");
                return EXIT_SUCCESS;
            }
            
            vMinecraftUsers.getProfile(other).setNick("");
            player.sendMessage(Colors.LightGreen + "Nickname Removed");
            
            return EXIT_SUCCESS;
        }
        
        //Check if the player can set their own nick.
        if(!player.canUseCommand("/nick")&& vMinecraftSettings.getInstance().nick()){
            return EXIT_FAIL;
        }
        if(args.length < 1){
            vMinecraftChat.sendMessage(player, player, Colors.Rose
            		+ "Usage is /rnick");
            return EXIT_SUCCESS;
        }
        vMinecraftUsers.getProfile(player).setNick("");
        player.sendMessage(Colors.LightGreen + "Nickname Removed");
        
        return EXIT_SUCCESS;
    }
    
    //=====================================================================
	//Function:	suffix (/suffix)
	//Input:	Player player: The player using the command
    //			String[] args: The color and the suffix
	//Output:	int: Exit Code
	//Use:		Changes your suffix
	//=====================================================================
    public static int suffix(Player player, String[] args){
    	
    	//if the player can suffix others
        if(player.canUseCommand("/suffixother")&& vMinecraftSettings.getInstance().suffix()){
            if(args.length < 2){
                vMinecraftChat.sendMessage(player, player, Colors.Rose
                		+ "Usage is /suffix [Player] [Name]");
                return EXIT_SUCCESS;
            }
            
            //Check if the suffix is too long
            if(vMinecraftChat.msgLength(args[1]) > 60)
            {
                vMinecraftChat.sendMessage(player, player, Colors.Rose
                		+ "The suffix you entered was too long.");
                return EXIT_SUCCESS;
            }
            
            //Check if the player exists
            Player other = etc.getServer().matchPlayer(args[0]);
            if(other == null)
            {
                vMinecraftChat.sendMessage(player, player, Colors.Rose
                		+ "The player you specified could not be found");
                return EXIT_SUCCESS;
            }
            
            //Check if they are a higher rank than the other person
            if(player != other && other.hasControlOver(player))
            {
                vMinecraftChat.sendMessage(player, player, Colors.Rose
                		+ "The player you specified is a higher rank than you.");
                return EXIT_SUCCESS;
            }
            vMinecraftUsers.getProfile(other).setSuffix(args[1]);
            player.sendMessage(Colors.LightGreen + "Suffix Set");
            
            return EXIT_SUCCESS;
        }
        
        //Check if the player can set their own suffix.
        if(!player.canUseCommand("/suffix")&& vMinecraftSettings.getInstance().suffix()){
            return EXIT_FAIL;
        }
        if(args.length < 1){
            vMinecraftChat.sendMessage(player, player, Colors.Rose
            		+ "Usage is /suffix [Suffix]");
            return EXIT_SUCCESS;
        }
        
        //Check if the suffix is too long
        if(vMinecraftChat.msgLength(args[1]) > 60)
        {
            vMinecraftChat.sendMessage(player, player, Colors.Rose
            		+ "The suffix you entered was too long.");
            return EXIT_SUCCESS;
        }
        vMinecraftUsers.getProfile(player).setSuffix(args[0]);
        player.sendMessage(Colors.LightGreen + "Suffix Set");
        
        return EXIT_SUCCESS;
    }
    
    //=====================================================================
	//Function:	removeSuffix (/rsuffix)
	//Input:	Player player: The player using the command
    //			String[] args: Ignored
	//Output:	int: Exit Code
	//Use:		Removes your suffix
	//=====================================================================
    public static int removeSuffix(Player player, String[] args){
    	
    	//if the player can suffix others
        if(player.canUseCommand("/suffixother")&& vMinecraftSettings.getInstance().suffix()){
            if(args.length < 1){
                vMinecraftChat.sendMessage(player, player, Colors.Rose
                		+ "Usage is /rsuffix [Player]");
                return EXIT_SUCCESS;
            }
            
            //Check if the player exists
            Player other = etc.getServer().matchPlayer(args[0]);
            if(other == null)
            {
                vMinecraftChat.sendMessage(player, player, Colors.Rose
                		+ "The player you specified could not be found");
                return EXIT_SUCCESS;
            }
            
            //Check if they are a higher rank than the other person
            if(player != other && other.hasControlOver(player))
            {
                vMinecraftChat.sendMessage(player, player, Colors.Rose
                		+ "The player you specified is a higher rank than you.");
                return EXIT_SUCCESS;
            }
            vMinecraftUsers.getProfile(other).setSuffix("");
            player.sendMessage(Colors.LightGreen + "Suffix Removed");
            
            return EXIT_SUCCESS;
        }
        
        //Check if the player can set their own suffix.
        if(!player.canUseCommand("/suffix")&& vMinecraftSettings.getInstance().suffix()){
            return EXIT_FAIL;
        }
        if(args.length < 1){
            vMinecraftChat.sendMessage(player, player, Colors.Rose
            		+ "Usage is /rsuffix");
            return EXIT_SUCCESS;
        }
        vMinecraftUsers.getProfile(player).setSuffix("");
        player.sendMessage(Colors.LightGreen + "Suffix Removed");
        
        return EXIT_SUCCESS;
    }
    
    //=====================================================================
	//Function:	colors (/colors)
	//Input:	Player player: The player using the command
	//Output:	int: Exit Code
	//Use:		Displays a list of all colors and color codes
	//=====================================================================
    public static int colors(Player player, String[] args){
    	if(args.length > 0&& vMinecraftSettings.getInstance().colors())
    	{
    		vMinecraftUsers.getProfile(player).setColor(args[0]);
    		vMinecraftChat.sendMessage(player, player, "^" + args[0].charAt(0)
    				+ "Default chat color set.");
    	} else {
	        player.sendMessage(Colors.Rose + "You use these color codes like in quake or MW2.");
	        player.sendMessage(Colors.Rose + "^4 would make text " + Colors.Red
	        		+ "red" + Colors.Rose + ", ^a would make it " + Colors.LightGreen 
	        		+ "light green" + Colors.Rose + ".");
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
	        		+ Colors.LightGreen 	+ "A"
	        		+ Colors.LightBlue 		+ "B"
	        		+ Colors.Rose 			+ "C"
	        		+ Colors.LightPurple	+ "D"
	        		+ Colors.Yellow			+ "E"
	        		+ Colors.White			+ "F"
					+ "^r"					+ "[R]ainbow");
    	}
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
        if (args.length < 1) return EXIT_FAIL;
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
        
        //Make sure a player is specified
        if (args.length < 2) {
        	vMinecraftChat.sendMessage(player, player, Colors.Rose
        			+ "Usage is /msg [player] [message]");
            return EXIT_SUCCESS;
        }
        
        //Make sure the player exists
        Player toPlayer = etc.getServer().matchPlayer(args[0]);
        if (toPlayer == null || args.length < 1) {
        	vMinecraftChat.sendMessage(player, player, Colors.Rose
        			+ "No player by the name of " + args[0] + " could be found.");
            return EXIT_SUCCESS;
        }

        String msg = etc.combineSplit(1, args, " ");
    	//Send the message to the targeted player and the sender
        vMinecraftChat.sendMessage(player, toPlayer,
        		Colors.LightGreen + "[From:" + vMinecraftChat.getName(player)
        		+ Colors.LightGreen + "] " + msg);
        vMinecraftChat.sendMessage(player, player,
        		Colors.LightGreen + "[To:" + vMinecraftChat.getName(toPlayer)
        		+ Colors.LightGreen + "] " + msg);
        //Set the last massager for each player
        vMinecraftUsers.getProfile(player).setMessage(toPlayer);
        vMinecraftUsers.getProfile(toPlayer).setMessage(player);
        
        //Display the message to the log
        log.log(Level.INFO, player.getName() + " whispered to " + toPlayer.getName()
        		+ ": " + msg);
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
    	if(vMinecraftUsers.getProfile(player) == null ) {
    		vMinecraftChat.sendMessage(player, player,
    				Colors.Rose + "The person you last message has logged off");
        	return EXIT_SUCCESS;
    	}

    	//Make sure a message is specified
    	if (args.length < 1) {
    		vMinecraftChat.sendMessage(player, player,
    				Colors.Rose + "Usage is /reply [Message]");
        	return EXIT_SUCCESS;
    	}
    	
    	//Make sure the player they're talking to is online
    	Player toPlayer = vMinecraftUsers.getProfile(player).getMessage();
    	if (toPlayer == null) {
    		vMinecraftChat.sendMessage(player, player,
    				Colors.Rose + "The person you last message has logged off");
        	return EXIT_SUCCESS;
    	}
    	
        String msg = etc.combineSplit(0, args, " ");
        
    	//Send the message to the targeted player and the sender
        vMinecraftChat.sendMessage(player, toPlayer,
        		Colors.LightGreen + "[From:" + vMinecraftChat.getName(player)
        		+ Colors.LightGreen + "] " + msg);
        vMinecraftChat.sendMessage(player, player,
        		Colors.LightGreen + "[To:" + vMinecraftChat.getName(toPlayer)
        		+ Colors.LightGreen + "] " + msg);
        
        //Set the last messager for each player
        vMinecraftUsers.getProfile(player).setMessage(toPlayer);
        vMinecraftUsers.getProfile(toPlayer).setMessage(player);
        
        //Display the message to the log
        log.log(Level.INFO, player.getName() + " whispered to " + toPlayer.getName()
        		+ ": " + msg);
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
    	if(args.length < 1 && vMinecraftSettings.getInstance().ignore())
    	{
			vMinecraftChat.sendMessage(player, player,
					Colors.Rose + "Usage: /ignore [Player]");
	    	return EXIT_SUCCESS;
    	}
    	
		//Find the player and make sure they exist
    	Player ignore = etc.getServer().matchPlayer(args[0]);
    	if(ignore == null&& vMinecraftSettings.getInstance().ignore())
    	{
			vMinecraftChat.sendMessage(player, player, Colors.Rose
					+ "The person you tried to ignore is not logged in.");
	    	return EXIT_SUCCESS;
    	}
    	
    	if(!player.hasControlOver(ignore)&& vMinecraftSettings.getInstance().ignore())
    	{
			vMinecraftChat.sendMessage(player, player, Colors.Rose
					+ "You can't ignore someone a higher rank than you.");
	    	return EXIT_SUCCESS;
    	}
    	
		//Don't let the player ignore themselves
		if(ignore.getName().equalsIgnoreCase(player.getName()))
		{		
			vMinecraftChat.sendMessage(player, player,
					Colors.Rose + "You cannot ignore yourself");
	    	return EXIT_SUCCESS;
		}
		
		//Attempt to ignore the player and report accordingly
		if(vMinecraftUsers.getProfile(player).addIgnore(ignore))
			vMinecraftChat.sendMessage(player, player, Colors.Rose
					+ ignore.getName() + " has been successfuly ignored.");
		else
			vMinecraftChat.sendMessage(player, player, Colors.Rose
					+ "You are already ignoring " + ignore.getName());


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
    	if(args.length < 1&& vMinecraftSettings.getInstance().ignore())
    	{
			vMinecraftChat.sendMessage(player, player,
					Colors.Rose + "Usage: /unignore [Player]");
	    	return EXIT_SUCCESS;
    	}
    	
		//Find the player and make sure they exist
    	Player ignore = etc.getServer().matchPlayer(args[0]);
    	if(ignore == null&& vMinecraftSettings.getInstance().ignore())
    	{
			vMinecraftChat.sendMessage(player, player,
					Colors.Rose + "The person you tried to unignore is not logged in.");
			return EXIT_SUCCESS;
    	}
    	
		//Attempt to ignore the player and report accordingly
		if(vMinecraftUsers.getProfile(player).removeIgnore(ignore))
			vMinecraftChat.sendMessage(player, player,
					Colors.Rose + ignore.getName()+ " has been successfuly " +
							"unignored.");
		else
			vMinecraftChat.sendMessage(player, player,
					Colors.Rose + "You are not currently ignoring " + ignore.getName());
		
    	return EXIT_SUCCESS;
    }

	//=====================================================================
	//Function:	ignoreList (/ignorelist)
	//Input:	Player player: The player using the command
    //			String[] args: Ignored
	//Output:	int: Exit Code
	//Use:		Lists the player you have ignored
	//=====================================================================
    public static int ignoreList(Player player, String[] args)
    {
        if (vMinecraftSettings.getInstance().ignore()){
    	//Get the ignore list
    	String[] list = vMinecraftUsers.getProfile(player).listIgnore();
    	
    	//Find the last page number
    	int lastPage = (int)list.length / 5;
    	if((int)list.length % 5 > 0)
    		lastPage++;
    	
    	//Find the page number the player wants displayed
    	int page = 0;
    	if(args.length > 0 && Integer.valueOf(args[0]) > 0
    			&& Integer.valueOf(args[0]) <= lastPage)
    		page = Integer.valueOf(args[0]) - 1;
    		
    	//Display the header
		vMinecraftChat.sendMessage(player, player,
				Colors.Rose + "Ignore List [" + page + "/"
				+ lastPage + "]");
		
		//Display up to 5 people
    	for(int i = 0; i < 5 && i + (page * 5) < list.length; i++)
    		vMinecraftChat.sendMessage(player, player,
    				Colors.Rose + list[i+ (page * 5)]);
    	
    	return EXIT_SUCCESS;
        }
        return EXIT_FAIL;
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
		//Make sure the user has access to the command
		if(!player.canUseCommand("/a")) return EXIT_FAIL;
		
	    if(!vMinecraftSettings.getInstance().adminChatToggle()) return EXIT_FAIL;
	    
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
		//Make sure the user has access to the command
		if(!player.canUseCommand("/heal")) return EXIT_FAIL;
		
        if(!vMinecraftSettings.getInstance().cmdHeal()) return EXIT_FAIL;

    	//If a target wasn't specified, heal the user.
        if (args.length < 1){
        	player.setHealth(20);
        	player.sendMessage("Your health is restored");
    		return EXIT_SUCCESS;
        }
        
        //If a target was specified, try to find them and then heal them
        //Otherwise report the error
    	Player playerTarget = etc.getServer().matchPlayer(args[0]);
    	if (playerTarget == null){
    		player.sendMessage(Colors.Rose 
    				+ "Couldn't find that player");
    		return EXIT_SUCCESS;
    	}
    	
		playerTarget.setHealth(20);
		player.sendMessage(Colors.Blue + "You have healed " 
				+ vMinecraftChat.getName(playerTarget));
		playerTarget.sendMessage(Colors.Blue 
				+ "You have been healed by " 
				+ vMinecraftChat.getName(player));
		return EXIT_SUCCESS;
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
		//Make sure the user has access to the command
		if(!player.canUseCommand("/suicide")) return EXIT_FAIL;
		
        if(!vMinecraftSettings.getInstance().cmdSuicide()) return EXIT_FAIL;
    
    	//Set your health to 0. Not much to it.
        player.setHealth(0);
        return EXIT_SUCCESS;
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
		//Make sure the user has access to the command
		if(!player.canUseCommand("/tp")) return EXIT_FAIL;
		//Get if the command is enabled
		if(!vMinecraftSettings.getInstance().cmdTp())return EXIT_FAIL;
		
		//Make sure a player has been specified and return an error if not
		if (args.length < 1) {
			player.sendMessage(Colors.Rose + "Correct usage is: /tp [player]");
			return EXIT_SUCCESS;
		}

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
			vMinecraftChat.gmsg( player, vMinecraftChat.getName(player)
					+ Colors.LightBlue + " has teleported to "
					+ vMinecraftChat.getName(playerTarget));
			log.log(Level.INFO, player.getName() + " teleported to " +
					playerTarget.getName());
			player.teleportTo(playerTarget);
			
		}
		return EXIT_SUCCESS;
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
		//Make sure the user has access to the command
		if(!player.canUseCommand("/masstp")) return EXIT_FAIL;
		
		//If the command is enabled
		if(!vMinecraftSettings.getInstance().cmdMasstp())return EXIT_FAIL;
	
		//Go through all players and move them to the user
		for (Player p : etc.getServer().getPlayerList()) {
			if (!p.hasControlOver(player)) {
				p.teleportTo(player);
			}
		}
		//Inform the user that the command has executed successfully
		player.sendMessage(Colors.Blue + "Summoning successful.");
		
		return EXIT_SUCCESS;
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
		//Make sure the user has access to the command
		if(!player.canUseCommand("/tphere")) return EXIT_FAIL;
		
		//Check if the command is enabled.
		if (!vMinecraftSettings.getInstance().cmdTphere())return EXIT_FAIL;
		
		//Make sure a player is specified
		if (args.length < 1) {
			player.sendMessage(Colors.Rose + "Correct usage" +
					" is: /tphere [player]");
			return EXIT_SUCCESS;
		}
		
		//Get the player by name
		Player playerTarget = etc.getServer().matchPlayer(args[0]);
		
		//If the target doesn't exist
		if(playerTarget == null)
			player.sendMessage(Colors.Rose + "Can't find user "
					+ args[0] + ".");
		//If the player has a higher rank than the user, return error
		else if (!player.hasControlOver(playerTarget)) 
			player.sendMessage(Colors.Red + "That player has higher" +
					" permissions than you.");
		//If the user teleports themselves, mock them
		else if (player.getName().equalsIgnoreCase(args[0])) 
			player.sendMessage(Colors.Rose + "Wow look at that! You" +
					" teleported yourself to yourself!");
		//If the target exists, teleport them to the user
		else {
			log.log(Level.INFO, player.getName() + " teleported "
					+ player.getName() + " to their self.");
			playerTarget.teleportTo(player);
		}
		return EXIT_SUCCESS;
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
		//Make sure the user has access to the command
		if(!player.canUseCommand("/reload")) return EXIT_FAIL;
		
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
		if(!vMinecraftSettings.getInstance().cmdRules()
				&& vMinecraftSettings.getInstance().getRules().length > 0
				&& !vMinecraftSettings.getInstance().getRules()[0].isEmpty()) {
			return EXIT_FAIL;
		}
			
		//Apply QuakeCode Colors to the rules
		String[] rules = vMinecraftChat.applyColors(
				vMinecraftSettings.getInstance().getRules());
		//Display them
		for (String str : rules ) {
			if(!str.isEmpty())
				player.sendMessage(Colors.Blue + str);
			else
				player.sendMessage(Colors.Blue
						+ "!!!The Rules Have Not Been Set!!!");
		}
		return EXIT_SUCCESS;
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
		if(!vMinecraftSettings.getInstance().cmdFabulous()) return EXIT_FAIL;
		
		//Make sure a message has been specified
		if (args.length < 1) {
			player.sendMessage(Colors.Rose + "Usage /fabulous [Message]");
			return EXIT_SUCCESS;
		}
			
		//Format the name
		String playerName = Colors.White + "<"
				+ vMinecraftChat.getName(player) + Colors.White +"> ";
		
		//Merge the message again
		 String str = etc.combineSplit(0, args, " ");
		
		//Output for server
		log.log(Level.INFO, player.getName()+" fabulously said \""+ str+"\"");
		
		//Prepend the player name and cut into lines.
		vMinecraftChat.gmsg(player, playerName + vMinecraftChat.rainbow(str));

		return EXIT_SUCCESS;
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
		//Make sure the user has access to the command
		if(!player.canUseCommand("/whois")) return EXIT_FAIL;
		
		//If the command is enabled
		if (!vMinecraftSettings.getInstance().cmdWhoIs()) return EXIT_FAIL;
		
		//If a player is specified
		if (args.length < 1) 
		{
			player.sendMessage(Colors.Rose + "Usage is /whois [player]");
			return EXIT_SUCCESS;
		}
		
		//Get the player by name
		Player playerTarget = etc.getServer().matchPlayer(args[0]);
		
		//If the player exists
		if (playerTarget == null){
			player.sendMessage(Colors.Rose+"Player not found.");
			return EXIT_SUCCESS;
		}

		//Displaying the information
		player.sendMessage(Colors.Blue + "Whois results for " +
				vMinecraftChat.getName(playerTarget));
		//Group
		for(String group: playerTarget.getGroups())
		player.sendMessage(Colors.Blue + "Groups: " + group);
		
		//Only let admins see this info
		if(player.isAdmin())
		{
			//Admin
			player.sendMessage(Colors.Blue+"Admin: " +
					String.valueOf(playerTarget.isAdmin()));
			//IP
			player.sendMessage(Colors.Blue+"IP: " + playerTarget.getIP());
			//Restrictions
			player.sendMessage(Colors.Blue+"Can ignore restrictions: " +
					String.valueOf(playerTarget.canIgnoreRestrictions()));
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
		if (!vMinecraftSettings.getInstance().cmdWho()) return EXIT_FAIL;
	
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

	//=====================================================================
	//Function:	say (/say)
	//Input:	Player player: The player using the command
    //			String[] args: The message to apply the effect to
	//Output:	int: Exit Code
	//Use:		Announces the message to all players
	//=====================================================================
	public static int say(Player player, String[] args)
	{
		//Make sure the user has access to the command
		if(!player.canUseCommand("/say")) return EXIT_FAIL;
		
		//Check if the command is enabled
		if (!vMinecraftSettings.getInstance().cmdSay()) return EXIT_FAIL;
		
		//Make sure a message is supplied or output an error
		if (args.length < 1) {
			player.sendMessage(Colors.Rose + "Usage is /say [message]");
		}
		
		//Display the message globally
		vMinecraftChat.gmsg(player, Colors.Yellow
				+ etc.combineSplit(0, args, " "));
		return EXIT_SUCCESS;
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
		//Make sure the user has access to the command
		if(!player.canUseCommand("/slay")) return EXIT_FAIL;
		
		//Check if the command is enabled
		if(!vMinecraftSettings.getInstance().cmdEzModo()) return EXIT_FAIL;
	
		//Get the player by name
		Player playerTarget = etc.getServer().matchPlayer(args[0]);
		
		//If the player doesn't exist don't run
		if(playerTarget == null)
		{
			player.sendMessage(Colors.Rose + "Usage is /slay [Player]");
			return EXIT_SUCCESS;
		}
		
		//If the player isn't invulnerable kill them
		if (vMinecraftSettings.getInstance()
				.isEzModo(playerTarget.getName())) {
			player.sendMessage(Colors.Rose + "That player is currently in" +
					" ezmodo! Hahahaha");
		}
		
		playerTarget.setHealth(0);
		vMinecraftChat.gmsg(player, vMinecraftChat.getName(player)
				+ Colors.LightBlue + " has slain "
				+ vMinecraftChat.getName(playerTarget));
		//Otherwise output error to the user
		
		return EXIT_SUCCESS;
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
		//Make sure the user has access to the command
		if(!player.canUseCommand("/ezmodo")) return EXIT_FAIL;
		
		//If the command is enabled
		if (!vMinecraftSettings.getInstance().cmdEzModo()) return EXIT_FAIL;
		
		//If the player is already invulnerable, turn ezmodo off.
		if (vMinecraftSettings.getInstance().isEzModo(player.getName())) {
			player.sendMessage(Colors.Red + "ezmodo = off");
			vMinecraftSettings.getInstance().removeEzModo(player.getName());
			
		//Otherwise make them invulnerable
		} else {
			player.sendMessage(Colors.LightBlue + "eh- maji? ezmodo!?");
			player.sendMessage(Colors.Rose + "kimo-i");
			player.sendMessage(Colors.LightBlue + "Easy Mode ga yurusareru" +
					" no wa shougakusei made dayo ne");
			player.sendMessage(Colors.Red + "**Laughter**");
			vMinecraftSettings.getInstance().addEzModo(player.getName());
		}
        return EXIT_SUCCESS;
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
		//Make sure the user has access to the command
		if(!player.canUseCommand("/ezmodo")) return EXIT_FAIL;
		//If the feature is enabled list the players
        if(!vMinecraftSettings.getInstance().cmdEzModo()) return EXIT_FAIL;
        
        player.sendMessage("Ezmodo: " + vMinecraftSettings.getInstance().ezModoList());
        return EXIT_SUCCESS;
	}

	//=====================================================================
	//Function:	modify (/modify)
	//Input:	Player player: The player using the command
    //			String[] args: Player, Command, Arguments
	//Output:	int: Exit Code
	//Use:		Display help for modifying features of players
	//=====================================================================
	public static int modify(Player player, String[] args)
	{
		if(player.canUseCommand("/prefixother"))
			vMinecraftChat.sendMessage(player, player, "/prefix [Player]" +
					" [Color] (Tag) - Set a players prefix and tag.");
		else if(player.canUseCommand("/prefix"))
			vMinecraftChat.sendMessage(player, player, "/prefix [Color]" +
					" (Tag) - Set your prefix and tag.");
		
		if(player.canUseCommand("/nickother"))
			vMinecraftChat.sendMessage(player, player, "/nick [Player]" +
					" [Nickname] - Set a players nickname.");
		else if(player.canUseCommand("/nick"))
			vMinecraftChat.sendMessage(player, player, "/nick [Nick]" +
					" - Set your nickname.");
		
		if(player.canUseCommand("/suffixother"))
			vMinecraftChat.sendMessage(player, player, "/suffix [Player]" +
					" [Suffix] - Set a players suffix.");
		else if(player.canUseCommand("/suffix"))
			vMinecraftChat.sendMessage(player, player, "/suffix [Suffix]" +
					" - Set your suffix.");
		
		if(player.canUseCommand("/suffixother"))
			vMinecraftChat.sendMessage(player, player, "/suffix [Player]" +
					" [Suffix] - Set a players suffix.");
		else if(player.canUseCommand("/suffix"))
			vMinecraftChat.sendMessage(player, player, "/suffix [Suffix]" +
					" - Set your suffix.");
		
		if(player.canUseCommand("/vranks"))
		{
			vMinecraftChat.sendMessage(player, player, "/promote [Player]" +
			" - Promotes a player one rank");
			vMinecraftChat.sendMessage(player, player, "/demote [Player]" +
			" - Demotes a player one rank");
		}
		return EXIT_SUCCESS;
	}

	//=====================================================================
	//Function:	promote (/promote)
	//Input:	Player player: The player using the command
    //			String[] args: Player to promote
	//Output:	int: Exit Code
	//Use:		Attempt to promote a player one rank
	//=====================================================================
	public static int promote(Player player, String[] args)
	{
		//Check if they can promote
		if(!player.canUseCommand("/promote")) return EXIT_FAIL;
		
		//Check if they specified a player
		if(args.length < 1)
		{
			vMinecraftChat.sendMessage(player, Colors.Rose + "Usage: /promote [Player] (Rank)");
			return EXIT_SUCCESS;
		}
		
		//Try to find the player
		Player target = etc.getServer().matchPlayer(args[0]);
		if(target == null)
		{
			vMinecraftChat.sendMessage(player, Colors.Rose + "The player specified could not be found");
			return EXIT_SUCCESS;
		}
		
		//Get the list of ranks
		String[] ranks = vMinecraftSettings.getInstance().getRanks();

		//Find the targets current rank number
		String[] tarGroups = target.getGroups();
		int tarRank = 0,
			tarPos = 0;
		boolean leave = false;
		for(String rank : ranks)
		{
			for(String group : tarGroups)
			{
				if(rank.equalsIgnoreCase(group))
				{
					leave = true;
					break;
				}
				else
					tarPos++;
			}
			if(leave)
				break;
			tarRank++;
			tarPos = 0;
		}
		if(!leave)
		{
			tarRank = 0;
			tarPos = 0;
			if(tarGroups != null)
			{
				String[] tempGroups = new String[tarGroups.length + 1];
				System.arraycopy(tarGroups, 0, tempGroups, 1, tarGroups.length);
				tarGroups = tempGroups;
			} else
				tarGroups = new String[1];
		}
		
		leave = false;
		//Get the player's rank
		String[] myGroups = player.getGroups();
		int myRank = 0;
		
		for(String rank : ranks)
		{
			for(String group : myGroups)
				if(rank.equalsIgnoreCase(group))
				{

					leave = true;
					break;
				}
			if(leave)
				break;
			myRank++;
		}
		if(!leave)
			myRank = 0;
		
		//Make sure they're not promoting to their rank or higher
		if(myRank <= tarRank + 1)
		{
			vMinecraftChat.sendMessage(player, Colors.Rose + "You cannot promote someone to" +
					" your rank or higher.");
			return EXIT_SUCCESS;
		}
		
		tarGroups[tarPos] = ranks[tarRank + 1];
		target.setGroups(tarGroups);

		//Make sure the player is in the files
        FlatFileSource ffs = new FlatFileSource();
        if(!ffs.doesPlayerExist(target.getName()))
        {
			vMinecraftChat.sendMessage(player, Colors.Rose + "Adding player.");
			ffs.addPlayer(target);
        }
        else
        {
        	ffs.modifyPlayer(target);
        }
        
		vMinecraftChat.sendMessage(player, Colors.Rose + target.getName()
				+ " has been promoted to " + ranks[tarRank + 1] + ".");
		vMinecraftChat.sendMessage(target, Colors.Rose + "You have been promoted to "
				+ ranks[tarRank + 1] + ".");
		
		return EXIT_SUCCESS;
	}

	//=====================================================================
	//Function:	demote (/demote)
	//Input:	Player player: The player using the command
    //			String[] args: Player to promote
	//Output:	int: Exit Code
	//Use:		Attempt to promote a player one rank
	//=====================================================================
	public static int demote(Player player, String[] args)
	{
		//Check if they can demote
		if(!player.canUseCommand("/demote")) return EXIT_FAIL;
		
		//Check if they specified a player
		if(args.length < 1)
		{
			vMinecraftChat.sendMessage(player, Colors.Rose + "Usage: /demote [Player] (Rank)");
			return EXIT_SUCCESS;
		}
		
		//Try to find the player
		Player target = etc.getServer().matchPlayer(args[0]);
		if(target == null)
		{
			vMinecraftChat.sendMessage(player, Colors.Rose + "The player specified could not be found");
			return EXIT_SUCCESS;
		}
		
		//Get the list of ranks
		String[] ranks = vMinecraftSettings.getInstance().getRanks();

		//Find the targets current rank number
		String[] tarGroups = target.getGroups();
		int tarRank = 0,
			tarPos = 0;
		boolean leave = false;
		for(String rank : ranks)
		{
			for(String group : tarGroups)
			{
				if(rank.equalsIgnoreCase(group))
				{
					leave = true;
					break;
				}
				else
					tarPos++;
			}
			if(leave)
				break;
			tarRank++;
			tarPos = 0;
		}
		if(!leave)
		{
			tarRank = 0;
			tarPos = 0;
			if(tarGroups != null)
			{
				String[] tempGroups = new String[tarGroups.length + 1];
				System.arraycopy(tarGroups, 0, tempGroups, 1, tarGroups.length);
				tarGroups = tempGroups;
			} else
				tarGroups = new String[1];
		}
		
		leave = false;
		//Get the player's rank
		String[] myGroups = player.getGroups();
		int myRank = 0;
		
		for(String rank : ranks)
		{
			for(String group : myGroups)
				if(rank.equalsIgnoreCase(group))
				{
					leave = true;
					break;
				}
			if(leave)
				break;
			myRank++;
		}
		if(!leave)
		{
			myRank = 0;
		}
		
		//Make sure they're not demoting to their rank or higher
		if(myRank <= tarRank)
		{
			vMinecraftChat.sendMessage(player, Colors.Rose + "You cannot demote someone who is" +
					" your rank or higher.");
			return EXIT_SUCCESS;
		}
		
		if(tarRank - 1 < 0)
		{
			vMinecraftChat.sendMessage(player, Colors.Rose + target.getName() + " is already the" +
					" lowest rank.");
			return EXIT_SUCCESS;
			
		}
		
			tarGroups[tarPos] = ranks[tarRank - 1];
			target.setGroups(tarGroups);

			//Make sure the player is in the files
	        FlatFileSource ffs = new FlatFileSource();
	        if(!ffs.doesPlayerExist(target.getName()))
	        {
				vMinecraftChat.sendMessage(player, Colors.Rose + "Adding player.");
				ffs.addPlayer(target);
	        }
	        else
	        {
	        	ffs.modifyPlayer(target);
	        }
	        
			vMinecraftChat.sendMessage(player, Colors.Rose + target.getName()
					+ " has been demoted to " + ranks[tarRank - 1] + ".");
			vMinecraftChat.sendMessage(target, Colors.Rose + "You have been demoted to "
					+ ranks[tarRank - 1] + ".");
		
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
	//			Player player: The player calling the command
	//			String[] arg: The arguments being input for the command
	//Output:	boolean: If the command was called successfully
	//Use:		Attempts to call a command
	//=====================================================================
	public int call(String name, Player player, String[] arg){
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
	//Function:	toString
	//Input:	None
	//Output:	String: A string representation of the aliases in the list
	//Use:		Displays all the aliases in thel ist
	//=====================================================================
	public String toString()
	{
		String temp = "";
		int i = 0;
		for(command comm : commands)
		{
			temp += comm.toString();
			if(i < commands.size() - 1)
				temp +=",";
		}
		return temp;
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

		//=====================================================================
		//Function:	toString
		//Input:	None
		//Output:	String: null
		//Use:		Returns null
		//=====================================================================
		public String toString() { return null; }
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

		//=====================================================================
		//Function:	toString
		//Input:	None
		//Output:	String: A string representation of this command.
		//			command@referencedcommand arg1 arg2 argn
		//Use:		Returns the string representation of the alias
		//=====================================================================
		public String toString()
		{
			String temp = getName();
			temp += '@';
			temp += reference;
			temp += etc.combineSplit(0, args, " ");
			return temp;
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
			if(!player.canUseCommand(super.commandName)) return EXIT_FAIL;
			
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

		//=====================================================================
		//Function:	toString
		//Input:	None
		//Output:	String: null
		//Use:		Returns null
		//=====================================================================
		public String toString() { return null; }
	}
}