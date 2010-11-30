import java.io.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Random;
//=====================================================================
//Class:	vminecraftSettings
//Use:		Controls the settings for vminecraft
//Author:	nossr50, TrapAlice, cerevisiae
//=====================================================================
public class vminecraftSettings {
	//private final static Object syncLock = new Object();
	protected static final Logger log = Logger.getLogger("Minecraft");
	private static volatile vminecraftSettings instance;


	//The feature settings
	static boolean toggle			= true,
				   adminChat		= false,
				   greentext		= false,
				   FFF				= false,
				   quakeColors		= false,
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
				   cmdEzModo		= false;
	//An array of players currently in ezmodo
	static ArrayList<String> ezModo = new ArrayList<String>();
	//The max health for ezModo
	static int ezHealth = 30;
	
	private PropertiesFile properties;
	String file = "vminecraft.properties";
	public String rules[] = new String[0];
        public static String deathMessages[] = new String[0];

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
				writer.write("greentext=true\r\n");
				writer.write("quakeColors=true\r\n");
				writer.write("cmdTphere=true\r\n");
				writer.write("cmdFabulous=true\r\n");
				writer.write("cmdWhoIs=true\r\n");
				writer.write("cmdWho=true\r\n");
				writer.write("cmdPromote=true\r\n");
				writer.write("cmdDemote=true\r\n");
				writer.write("cmdMasstp=true\r\n");
				writer.write("cmdSay=true\r\n");
				writer.write("cmdTp=true\r\n");
				writer.write("cmdRules=true\r\n");
				writer.write("globalmessages=true\r\n");
				writer.write("FFF=true\r\n");
				writer.write("adminchat=true\r\n");
				writer.write("cmdEzModo=true\r\n");
				writer.write("#Adding player names to this list will have them start off in ezmodo\r\n");
				writer.write("ezModo=\r\n");
				writer.write("#The health ezmodo people will have while in ezmodo. Don't set to 0\r\n");
				writer.write("ezHealth=30\r\n");
				writer.write("stopFire=false");
                                writer.write("stopTnt=false");
				writer.write("rules=Rules@#1: No griefing@#2: No griefing\r\n");
                                writer.write("#Death messages, seperate them by comma. All death messages start with the player name and a space.");
                                writer.write("deathMessages=is no more,died horribly,went peacefully");
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
			greentext = properties.getBoolean("greentext",true);
			FFF = properties.getBoolean("FFF",true);
			quakeColors = properties.getBoolean("quakeColors",true);
			cmdFabulous = properties.getBoolean("cmdFabulous",true);
			cmdPromote = properties.getBoolean("cmdPromote",true);
			cmdDemote = properties.getBoolean("cmdDemote",true);
			cmdWhoIs = properties.getBoolean("cmdWhoIs",true);
			cmdWho = properties.getBoolean("cmdWho",true);
			cmdRules = properties.getBoolean("cmdRules",true);
			cmdTp = properties.getBoolean("cmdTp",true);
			cmdMasstp = properties.getBoolean("cmdMasstp",true);
			cmdTphere = properties.getBoolean("cmdTphere",true);
			globalmessages = properties.getBoolean("globalmessages",true);
			cmdSay = properties.getBoolean("cmdSay",true);
			cmdEzModo = properties.getBoolean("cmdEzModo",true);
			stopFire = properties.getBoolean("stopFire",true);
			stopTnt = properties.getBoolean("stopTNT",true);
			rules = properties.getString("rules", "").split("@");
                        deathMessages = properties.getString("deathmessages", "").split(",");
			String[] tempEz = properties.getString("ezModo").split(",");
			ezModo = new ArrayList<String>();
			for(int i = 0; i < tempEz.length; i++)
				ezModo.add(tempEz[i]);
			
			ezHealth = properties.getInt("ezHealth");
			
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
	public boolean greentext() {return greentext;}
	public boolean FFF() {return FFF;}
	public boolean quakeColors() {return quakeColors;}
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
	public boolean cmdEzModo() {return cmdEzModo;}
	public boolean cmdWho() {return cmdWho;}
	public boolean stopFire() {return stopFire;}
	public boolean stopTnt() {return stopTnt;}
	
	//EzModo methods
	public boolean isEzModo(String playerName) {return ezModo.contains(playerName);}
	public void removeEzModo(String playerName) {ezModo.remove(ezModo.indexOf(playerName));}
	public void addEzModo(String playerName) {ezModo.add(playerName);}
	public int ezModoHealth() {return ezHealth;}
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
	public static vminecraftSettings getInstance() {
		if (instance == null) {
			instance = new vminecraftSettings();
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