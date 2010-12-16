import java.io.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
//=====================================================================
//Class:	vminecraftSettings
//Use:		Controls the settings for vminecraft
//Author:	nossr50, TrapAlice, cerevisiae
//=====================================================================
public class vMinecraftSettings {
	//private final static Object syncLock = new Object();
	protected static final Logger log = Logger.getLogger("Minecraft");
	private static volatile vMinecraftSettings instance;    


	//The feature settings
	static boolean toggle			= true,
				   adminChat		= false,
				   greentext		= false,
				   FFF				= false,
				   quakeColors		= false,
				   prefix 			= false,
				   suffix 			= false,
				   ignore 			= false,
				   colors 			= false,
				   nick 			= false,
                freeze = false,
				   cmdFabulous		= false,
				   cmdPromote		= false,
				   cmdDemote		= false,
				   cmdWhoIs			= false,
				   cmdRules			= false,
				   cmdMasstp		= false,
				   cmdTp			= false,
				   cmdTphere		= false,
				   globalmessages	= false,
				   cmdSay			= false,
				   cmdWho			= false,
				   stopFire			= false,
				   stopTnt			= false,
				   cmdHeal  		= false,
				   cmdSuicide		= false,
				   cmdAdminToggle	= false,
				   cmdEzModo		= false;
	//An array of players currently in ezmodo
	static ArrayList<String> ezModo = new ArrayList<String>();
        //An array of players currently frozen
        static ArrayList<String> frozenplayers = new ArrayList<String>();
    //An array of players currently toggled for admin chat
    static ArrayList<String> adminChatList = new ArrayList<String>();
    //An array of blocks that won't catch on fire
        

	
	private PropertiesFile properties;
	String file = "vminecraft.properties";
	public String rules[] = new String[0];
    public static String deathMessages[] = new String[0];
    public static String ranks[] = new String[0];

	//=====================================================================
	//Function:	loadSettings
	//Input:	None
	//Output:	None
	//Use:		Loads the settings from the properties
	//=====================================================================
	public void loadSettings()
	{
		File theDir = new File("vminecraft.properties");
		if(!theDir.exists()){
			String location = "vminecraft.properties";
			properties = new PropertiesFile("vminecraft.properties");
			FileWriter writer = null;
			try {
				writer = new FileWriter(location);
				writer.write("#This plugin is modular\r\n");
				writer.write("#Turn any features you don't want to false and they won't be running\r\n");
				writer.write("#If you edit this file and save it, then use /reload it will reload the settings\r\n");
                writer.write("#Chat Options\r\n");
                writer.write("#Allows the use of color codes following ^ symbol\r\n");
                writer.write("ColoredChat=true\r\n");
                writer.write("#Text following a > will be colored green to mimic quoting of popular internet message boards\r\n");
				writer.write("QuotesAreGreen=true\r\n");
                writer.write("#Turns any chat message starting with FFF automagically blood red\r\n");
                writer.write("FFF=true\r\n");
                writer.write("\r\n");
                writer.write("#Admin Settings\r\n");
                writer.write("#Enables or disables the admin only chat\r\n");
                writer.write("adminchat=true\r\n");
                writer.write("#Lets non admins use admin chat if they have the /adminchat command permission\r\n");
                writer.write("/adminchat=true\r\n");
                writer.write("#Enables overriding of regular /tp and /tphere to make it so you can only teleport to players with lower permissions, and only bring players of lower permissions to you\r\n");
                writer.write("/tp=true\r\n");
                writer.write("/tphere=true\r\n");
                writer.write("#Mass Tp uses the same concept, anyone with this command only brings those with lower permissions to themselves\r\n");
                writer.write("/masstp=true\r\n");
                writer.write("\r\n");
                writer.write("#Server Settings\r\n");
                writer.write("#Enables or Disables the following commands, give groups/users permissions to use these commands for them to work\r\n");
				writer.write("/fabulous=true\r\n");
                writer.write("/prefix=true\r\n");
                writer.write("/freeze=true\r\n");
                writer.write("/suffix=true\r\n");
                writer.write("/ignore=true\r\n");
                writer.write("/colors=true\r\n");
				writer.write("/whois=true\r\n");
                writer.write("/nick=true\r\n");
				writer.write("/who=true\r\n");
				writer.write("/promote=true\r\n");
				writer.write("/demote=true\r\n");
				writer.write("/say=true\r\n");
				writer.write("/rules=true\r\n");
				writer.write("/suicide=true\r\n");
				writer.write("/ezmodo=true\r\n");
                writer.write("#Global Messages\r\n");
                writer.write("#Enable or Disable sending announcements about sensitive commands to the entire server\r\n");
                writer.write("globalmessages=true\r\n");
                writer.write("\r\n");
				writer.write("#Adding player names to this list will have them start off in ezmodo\r\n");
				writer.write("ezModo=\r\n");
                writer.write("#Stop fire from spreading\r\n");
				writer.write("stopFire=false\r\n");
                writer.write("\r\n");
                writer.write("#Organize your player ranks from lowest to highest.\r\n");
                writer.write("ranks=default,trusted,mods,admins,superadmins\r\n");
				writer.write("#Write the rules to be shown when /rules is used here, it works just like the MOTD does\r\n");
				writer.write("rules=Rules@#1: No griefing@#2: No griefing\r\n");
				writer.write("#The Random Death messages, seperate them by comma. All death messages start with the player name and a space.\r\n");
				writer.write("deathMessages=is no more,died horribly,went peacefully\r\n");
			} catch (Exception e) {
				log.log(Level.SEVERE, "Exception while creating " + location, e);
			} finally {
				try {
					if (writer != null) {
						writer.close();
					}
				} catch (IOException e) {
					log.log(Level.SEVERE, "Exception while closing writer for " + location, e);
				}
			}

		} else {
			properties = new PropertiesFile("vminecraft.properties");
			try {
				properties.load();
			} catch (IOException e) {
				log.log(Level.SEVERE, "Exception while loading vminecraft.properties", e);
			}
		}

		try {
			adminChat = properties.getBoolean("adminchat",true);
			greentext = properties.getBoolean("QuotesAreGreen",true);
			FFF = properties.getBoolean("FFF",true);
			quakeColors = properties.getBoolean("ColoredChat",true);
            prefix = properties.getBoolean("prefix",true);
            suffix = properties.getBoolean("suffix",true);
            ignore = properties.getBoolean("ignore",true);
            colors = properties.getBoolean("colors",true);
            nick = properties.getBoolean("nick",true);
            freeze = properties.getBoolean("/freeze",true);
			cmdFabulous = properties.getBoolean("/fabulous",true);
			cmdPromote = properties.getBoolean("/promote",true);
			cmdDemote = properties.getBoolean("/demote",true);
			cmdWhoIs = properties.getBoolean("/whois",true);
			cmdWho = properties.getBoolean("/who",true);
			cmdRules = properties.getBoolean("/rules",true);
			cmdTp = properties.getBoolean("/tp",true);
			cmdMasstp = properties.getBoolean("/masstp",true);
			cmdTphere = properties.getBoolean("/tphere",true);
			cmdSuicide = properties.getBoolean("/suicide", true);
			cmdHeal = properties.getBoolean("/heal",true);
			cmdAdminToggle = properties.getBoolean("/adminchat", true);
			globalmessages = properties.getBoolean("globalmessages",true);
			cmdSay = properties.getBoolean("/say",true);
			cmdEzModo = properties.getBoolean("/ezmodo",true);
			stopFire = properties.getBoolean("stopFire",true);
			rules = properties.getString("rules", "").split("@");
			deathMessages = properties.getString("deathmessages", "").split(",");
			String[] tempEz = properties.getString("ezModo").split(",");
			ezModo = new ArrayList<String>();
			for(String ezName : tempEz)
				ezModo.add(ezName);
			
			ranks = properties.getString("ranks").split(",");
			

			
			log.log(Level.INFO, "vminecraft plugin successfully loaded");

		}
		catch (Exception e)
		{
			log.log(Level.SEVERE, "vminecraft Error: ERROR LOADING PROPERTIES FILE");
		}
	}

	//=====================================================================
	//Function:	adminchat, greentext, FFF, quakeColors, cmdFabulous,
	//			cmdPromote, cmdDemote, cmdWhoIs, cmdTp, cmdTphere, cmdSay
	//			cmdRules, globalmessages, cmdMasstp, cmdEzModo
	//Input:	None
	//Output:	Boolan: If the feature is enabled
	//Use:		Returns if the feature is enabled
	//=====================================================================
	public boolean adminchat() {return adminChat;}
        public boolean adminChatToggle() {return cmdAdminToggle;}
	public boolean greentext() {return greentext;}
	public boolean FFF() {return FFF;}
	public boolean quakeColors() {return quakeColors;}
        public boolean prefix() {return prefix;}
        public boolean suffix() {return suffix;}
        public boolean ignore() {return ignore;}
        public boolean colors() {return colors;}
        public boolean nick() {return nick;}
        public boolean freeze() {return freeze;}
	public boolean cmdFabulous() {return cmdFabulous;}
	public boolean cmdPromote() {return cmdPromote;}
	public boolean cmdDemote() {return cmdDemote;}
	public boolean cmdWhoIs() {return cmdWhoIs;}
	public boolean cmdTp() {return cmdTp;}
	public boolean cmdTphere() {return cmdTphere;}
	public boolean cmdSay() {return cmdSay;}
	public boolean cmdRules() {return cmdRules;}
	public boolean globalmessages() {return globalmessages;}
	public boolean cmdMasstp() {return cmdMasstp;}
	public boolean cmdWho() {return cmdWho;}
	public boolean stopFire() {return stopFire;}
	public boolean stopTnt() {return stopTnt;}
        public boolean cmdSuicide() {return cmdSuicide;}
        public boolean cmdHeal() {return cmdHeal;}
        
        public String[] getRanks() {return ranks;}
	
	//EzModo methods
    public boolean cmdEzModo() {return cmdEzModo;}
	public boolean isEzModo(String playerName) {return ezModo.contains(playerName);}
        public boolean isFrozen(String playerName) {return frozenplayers.contains(playerName);}
        public boolean isAdminToggled(String playerName) {return adminChatList.contains(playerName);}
	public void removeEzModo(String playerName) {ezModo.remove(ezModo.indexOf(playerName));}
        public void removeAdminToggled(String playerName) {adminChatList.remove(adminChatList.indexOf(playerName));}
	public void addEzModo(String playerName) {ezModo.add(playerName);}
        public void addAdminToggled(String playerName) {adminChatList.add(playerName);}
        public void addFrozen(String playerName) {frozenplayers.add(playerName);}
        public void removeFrozen (String playerName) {frozenplayers.remove(frozenplayers.indexOf(playerName));}
	public String ezModoList() {return ezModo.toString();}
	
    //Random death message method
    public static String randomDeathMsg() {
    	if (deathMessages == null) {
    		return "died";
    	}
    	return deathMessages[ (int) (Math.random() * deathMessages.length)];
	}
	
	//=====================================================================
	//Function:	getInstance
	//Input:	None
	//Output:	vminecraftSettings: The instance of the settings
	//Use:		Returns the instance of the settings
	//=====================================================================
	public static vMinecraftSettings getInstance() {
		if (instance == null) {
			instance = new vMinecraftSettings();
		}
		return instance;	
	}

	//=====================================================================
	//Function:	getRules
	//Input:	None
	//Output:	String[]: The list of rules
	//Use:		Gets the array containing the rules
	//=====================================================================
	public String[] getRules() {
		return rules;
	}

}