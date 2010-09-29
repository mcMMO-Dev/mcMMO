//This doesn't do anything yet, eventually you will be able to toggle features by writing true or false in vminecraft-config.txt
//This is high up on my priority list
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
public class txtfiles {
    	static final Logger log = Logger.getLogger("Minecraft");
	private final static Object syncLock = new Object();        
	static boolean toggle = true;
        private PropertiesFile properties;
	//Unfinished was interrupted in the middle of making this shit, where we can triggle toggles in a text file for commands
        //example return true for greentext=true in vminecraft.properties file would disable that code
	}