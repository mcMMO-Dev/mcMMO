import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
//=====================================================================
//Class:	vminecraftSettings
//Use:		Controls the settings for vminecraft
//Author:	nossr50, TrapAlice, cerevisiae
//=====================================================================
public class vConfig {
	//private final static Object syncLock = new Object();
	protected static final Logger log = Logger.getLogger("Minecraft");
	private static volatile vConfig instance;    
        static int range;


	//The feature settings
	static boolean toggle			= true,
				   adminChat		= false,
                                   groupcoloredbrackets = false,
                                   partyChat = false,
				   greentext		= false,
				   FFF				= false,
				   quakeColors		= false,
				   prefix 			= false,
				   suffix 			= false,
				   ignore 			= false,
				   colors 			= false,
				   nick 			= false,
                                   playerspawn = false,
                                   freeze = false,
                                   lavaspread = false,
                                   colorsrequirepermission = false,
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
        //An array of player currently toggled for party chat
        static ArrayList<String> partyChatList = new ArrayList<String>();
        //An array of blocks that won't catch on fire
        static public ArrayList<Integer> fireblockan;
    
	
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
				writer.append("#This plugin is modular\r\n");
				writer.append("#Turn any features you don't want to false and they won't be running\r\n");
				writer.append("#If you edit this file and save it, then use /reload it will reload the settings\r\n");
                                writer.append("#Chat Options\r\n");
                                writer.append("#Group prefix colors apply to player brackets\r\n");
                                writer.append("groupcoloredbrackets=true\r\n");
                                writer.append("#Allows the use of color codes following ^ symbol\r\n");
                                writer.append("ColoredChat=true\r\n");
                                writer.append("#Require per player permission for quakecolors\r\n");
                                writer.append("colorsrequirepermissions=false\r\n");
                                writer.append("#use /coloruse to give players permission if this is enabled\r\n");
                                writer.append("#Text following a > will be colored green to mimic quoting of popular internet message boards\r\n");
				writer.append("QuotesAreGreen=true\r\n");
                                writer.append("#Turns any chat message starting with FFF automagically blood red\r\n");
                                writer.append("FFF=true\r\n");
                                writer.append("\r\n");
                                writer.append("#Admin Settings\r\n");
                                writer.append("#Enables or disables players spawning to their home location\r\n");
                                writer.append("playerspawn=true\r\n");
                                writer.append("#Enables or disables the admin only chat\r\n");
                                writer.append("adminchat=true\r\n");
                                writer.append("#Lets non admins use admin chat if they have the /adminchat command permission\r\n");
                                writer.append("/adminchat=true\r\n");
                                writer.append("#Enables overriding of regular /tp and /tphere to make it so you can only teleport to players with lower permissions, and only bring players of lower permissions to you\r\n");
                                writer.append("/tp=true\r\n");
                                writer.append("/tphere=true\r\n");
                                writer.append("#Mass Tp uses the same concept, anyone with this command only brings those with lower permissions to themselves\r\n");
                                writer.append("/masstp=true\r\n");
                                writer.append("\r\n");
                                writer.append("#Server Settings\r\n");
                                writer.append("#Enables or Disables the following commands, give groups/users permissions to use these commands for them to work\r\n");
				writer.append("/fabulous=true\r\n");
                                writer.append("/prefix=true\r\n");
                                writer.append("/freeze=true\r\n");
                                writer.append("/suffix=true\r\n");
                                writer.append("/ignore=true\r\n");
                                writer.append("/colors=true\r\n");
				writer.append("/whois=true\r\n");
                                writer.append("/nick=true\r\n");
				writer.append("/who=true\r\n");
				writer.append("/promote=true\r\n");
				writer.append("/demote=true\r\n");
				writer.append("/say=true\r\n");
				writer.append("/rules=true\r\n");
				writer.append("/suicide=true\r\n");
				writer.append("/ezmodo=true\r\n");
                                writer.append("#Global Messages\r\n");
                                writer.append("#Enable or Disable sending announcements about sensitive commands to the entire server\r\n");
                                writer.append("globalmessages=true\r\n");
				writer.append("#Adding player names to this list will have them start off in ezmodo\r\n");
				writer.append("ezModo=\r\n");
                                writer.append("#Stop fire from spreading\r\n");
				writer.append("stopFire=false\r\n");
                                writer.append("#Stop lava from spreading fire");
                                writer.append("lavaspread=false");
                                writer.append("#Blocks disabled from fire");
                                writer.append("fireblocks=");
                                writer.append("\r\n");
                                writer.append("#Organize your player ranks from lowest to highest.\r\n");
                                writer.append("ranks=\r\n");
				writer.append("#Write the rules to be shown when /rules is used here, it works just like the MOTD does\r\n");
				writer.append("rules=Rules@#1: No griefing@#2: No griefing\r\n");
				writer.append("#The Random Death messages, seperate them by comma. All death messages start with the player name and a space.\r\n");
				writer.append("deathMessages=is no more,died horribly,went peacefully\r\n");
                                writer.append("#Enable whether or not players can toggle party chat");
                                writer.append("partychat=true");
                                writer.append("hiddendistance=1024");
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
                        groupcoloredbrackets = properties.getBoolean("groupcoloredbrackets",true);
			adminChat = properties.getBoolean("adminchat",true);
                        partyChat = properties.getBoolean("partychat",true);
                        playerspawn = properties.getBoolean("playerspawn",true);
			greentext = properties.getBoolean("QuotesAreGreen",true);
			FFF = properties.getBoolean("FFF",true);
			quakeColors = properties.getBoolean("ColoredChat",true);
                        colorsrequirepermission = properties.getBoolean("colorsrequirepermission",true);
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
                        lavaspread = properties.getBoolean("lavaspread",true);
			rules = properties.getString("rules", "").split("@");
			deathMessages = properties.getString("deathmessages", "").split(",");
			String[] tempEz = properties.getString("ezModo").split(",");
                        String[] fireblocks = properties.getString("fireblocks").split(",");
                        fireblockan = new ArrayList<Integer>();
                         for(String str : fireblocks)
                        {
                        if(!str.isEmpty())
                            fireblockan.add(Integer.parseInt(str));
                        }
			ezModo = new ArrayList<String>();
                        ezModo.addAll(Arrays.asList(tempEz));
			ranks = properties.getString("ranks").split(",");
                        range = properties.getInt("hiddendistance",1024);
			log.log(Level.INFO, "vminecraft plugin successfully loaded");
		}
		catch (Exception e)
		{
			log.log(Level.SEVERE, "vminecraft Error: ERROR LOADING PROPERTIES FILE {0}", e);
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
        public boolean groupcoloredbrackets(){return groupcoloredbrackets;}
        public boolean partyChat() {return partyChat;}
        public boolean adminChatToggle() {return cmdAdminToggle;}
	public boolean greentext() {return greentext;}
	public boolean FFF() {return FFF;}
	public boolean quakeColors() {return quakeColors;}
        public boolean prefix() {return prefix;}
        public boolean suffix() {return suffix;}
        public boolean ignore() {return ignore;}
        public boolean colors() {return colors;}
        public boolean nick() {return nick;}
        public boolean playerspawn() {return playerspawn;}
        public boolean colorsreq() {return colorsrequirepermission;}
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
        public boolean lavaSpread() {return lavaspread;}
        public boolean cmdSuicide() {return cmdSuicide;}
        public boolean cmdHeal() {return cmdHeal;}
        public ArrayList<Integer> getFireBlockIds() {return fireblockan;}
        public String[] getRanks() {return ranks;}
	
	//EzModo methods
    public boolean cmdEzModo() {return cmdEzModo;}
	public boolean isEzModo(String playerName) {return ezModo.contains(playerName);}
        public boolean isFrozen(String playerName) {return frozenplayers.contains(playerName);}
        public boolean isAdminToggled(String playerName) {return adminChatList.contains(playerName);}
        public boolean isPartyToggled(String playerName) {return partyChatList.contains(playerName);}
	public void removeEzModo(String playerName) {ezModo.remove(ezModo.indexOf(playerName));}
        public void removePartyToggled(String playerName) {partyChatList.remove(partyChatList.indexOf(playerName));}
        public void removeAdminToggled(String playerName) {adminChatList.remove(adminChatList.indexOf(playerName));}
	public void addEzModo(String playerName) {ezModo.add(playerName);}
        public void addPartyToggled(String playerName) {partyChatList.add(playerName);}
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
	public static vConfig getInstance() {
		if (instance == null) {
			instance = new vConfig();
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