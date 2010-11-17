import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

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
        private boolean cmdRules = false;
        private boolean cmdMasstp = false;
        private boolean cmdTp = false;
        private boolean cmdTphere = false;
        private boolean globalmessages = false;
        private boolean cmdSay = false;
        private PropertiesFile properties;
	String file = "vminecraft.properties";
        public String rules[] = null;

	public void loadSettings()
        {
            File theDir = new File("vminecraft.properties");
            if(!theDir.exists())
            {
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
                writer.write("cmdPromote=true\r\n");
                writer.write("cmdDemote=true\r\n");
                writer.write("cmdMasstp=true\r\n");
                writer.write("cmdSay=true\r\n");
                writer.write("cmdTp=true\r\n");
                writer.write("cmdRules=true\r\n");
                writer.write("globalmessages=true\r\n");
                writer.write("FFF=true\r\n");
                writer.write("adminchat=true\r\n");
                writer.write("rules=Rules@#1: No griefing@#2: No griefing\r\n");
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
                properties.load();
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
                cmdRules = properties.getBoolean("cmdRules",true);
                cmdTp = properties.getBoolean("cmdTp",true);
                cmdMasstp = properties.getBoolean("cmdMasstp",true);
                cmdTphere = properties.getBoolean("cmdTphere",true);
                globalmessages = properties.getBoolean("globalmessages",true);
                cmdSay = properties.getBoolean("cmdSay",true);
                rules = properties.getString("rules", "").split("@");
                log.log(Level.INFO, "vminecraft plugin successfully loaded");

            }
            catch (Exception e)
            {
                log.log(Level.SEVERE, "vminecraft Error: ERROR LOADING PROPERTIES FILE");
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
        public boolean cmdTp() {return cmdTp;}
        public boolean cmdTphere() {return cmdTphere;}
        public boolean cmdSay() {return cmdSay;}
        public boolean cmdRules() {return cmdRules;}
        public boolean globalmessages() {return globalmessages;}
        public boolean cmdMasstp() {return cmdMasstp;}

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