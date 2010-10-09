import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.minecraft.server.MinecraftServer;

public class settings {
	private final static Object syncLock = new Object();
        protected static final Logger log = Logger.getLogger("Minecraft");
        private static volatile settings instance;
	static boolean toggle = true;
        private boolean adminChat = false;
	private boolean greentext = false;
	private boolean FFF = false;
	private boolean quakeColors = false;
	private boolean cmdFabulous = false;
        private boolean cmdPromote = false;
        private boolean cmdDemote = false;
        private boolean cmdWhoIs = false;
        private PropertiesFile properties;
	String file = "vminecraft.properties";
        public String rules[] = null;

public void rules() {
    try{
       rules = properties.getString("rules", "Rules@#1: No griefing").split("@");
    }
    catch (Exception e) {
        log.log(Level.SEVERE, "Vminecraft: "+ e.getMessage() );
        rules = new String[]{"Rules@#1: No griefing"};
    }
}
	public  void loadSettings()
	//Will create a file if it doesn't exist
        {
            if (properties == null) {
            properties = new PropertiesFile("vminecraft.properties");
        } else {
            properties.load();
        }
		try{
                    Scanner scanner = new Scanner(new File(file));
                        while (scanner.hasNextLine()) {
                            String line  = scanner.nextLine();
                            if( line.startsWith("#") || line.equals(""))
                            {
                                continue;
                            }
                            String[] split = line.split("=");
                            if(split[0].equalsIgnoreCase("adminchat"))
                            {
                                if(split[1].equalsIgnoreCase("true"))
                                {
                                    adminChat = true;
                                }
                                   else adminChat = false;
                            }
                            if(split[0].equalsIgnoreCase("Greentext"))
                            {
                                if(split[1].equalsIgnoreCase("true"))
                                {
                                    greentext = true;
                                }
                                   else greentext = false;
                            }
                            if(split[0].equalsIgnoreCase("FFF"))
                            {
                                if(split[1].equalsIgnoreCase("true"))
                                {
                                    FFF = true;
                                }
                                   else FFF = false;
                            }
                            if(split[0].equalsIgnoreCase("QuakeColors"))
                            {
                                if(split[1].equalsIgnoreCase("true"))
                                {
                                    quakeColors = true;
                                }
                                   else quakeColors = false;
                            }
                            if(split[0].equalsIgnoreCase("cmdFabulous"))
                            {
                                if(split[1].equalsIgnoreCase("true"))
                                {
                                    cmdFabulous = true;
                                }
                                   else cmdFabulous = false;
                            }
                            if(split[0].equalsIgnoreCase("cmdPromote"))
                            {
                                if(split[1].equalsIgnoreCase("true"))
                                {
                                    cmdPromote = true;
                                }
                                   else cmdPromote = false;
                            }
                            if(split[0].equalsIgnoreCase("cmdDemote"))
                            {
                                if(split[1].equalsIgnoreCase("true"))
                                {
                                    cmdDemote = true;
                                }
                                   else cmdDemote = false;
                            }
                            if(split[0].equalsIgnoreCase("cmdWhoIs"))
                            {
                                if(split[1].equalsIgnoreCase("true"))
                                {
                                    cmdWhoIs = true;
                                }
                                   else cmdWhoIs = false;
                            }
                        }
                        scanner.close();
                }
                catch (Exception e) {
                    log.log(Level.SEVERE, "Vminecraft: "+ e.getMessage() );
                }

	}

        public boolean adminchat() {return adminChat;}
        public boolean greentext() {return greentext;}
        public boolean FFF() {return FFF;}
        public boolean quakeColors() {return quakeColors;}
        public boolean cmdFabulous() {return cmdFabulous;}
        public boolean cmdPromote() {return cmdPromote;}
        public boolean cmdDemote() {return cmdDemote;}
        public boolean cmdWhoIs() {return cmdWhoIs;}

        public static settings getInstance() {
        if (instance == null) {
            instance = new settings();
        }

        return instance;
    }
        //Will return the rules
        public String[] getRules() {
        return rules;
    }

}