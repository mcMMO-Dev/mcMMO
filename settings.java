import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.File;
import java.io.FileWriter;

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
        private boolean cmdTp = false;
        private boolean cmdTphere = false;
        private boolean globalmessages = false;
        private boolean cmdSay = false;
        private PropertiesFile properties;
	String file = "vminecraft.properties";
        public String rules[] = null;


	public void loadSettings()
        {

            if(properties == null)
            {
                properties = new PropertiesFile("vminecraft.properties");
            } else {
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
                cmdTphere = properties.getBoolean("cmdTphere",true);
                globalmessages = properties.getBoolean("globalmessages",true);
                cmdSay = properties.getBoolean("cmdSay",true);
                rules = properties.getString("rules", "").split("@");
                id.a.log(Level.INFO, "vminecraft plugin successfully loaded");

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