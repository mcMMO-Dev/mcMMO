import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
public class vmc {
private static volatile vmc instance;     
protected static final Logger log = Logger.getLogger("Minecraft");
private PropertiesFile properties;
String location = "groups.txt";    
    //Check if two players are in the same party
    public static boolean inSameParty(Player playera, Player playerb){
        if(vUsers.getProfile(playera).getParty().equals(vUsers.getProfile(playerb).getParty())){
            return true;
        } else {
            return false;
        }
    }
    //Get the distance between two players
    public static double getDistance(Player player1, Player player2)
    {
    return Math.sqrt(Math.pow(player1.getX() - player2.getX(), 2) + Math.pow(player1.getY() - player2.getY(), 2)
    + Math.pow(player1.getZ() - player2.getZ(), 2));
    }
    //Send the "invisibility" toggle to players near the hidden player
    public static void sendInvisible(Player player){
        for (Player p : etc.getServer().getPlayerList())
                {
                    if (getDistance(player, p) <= vConfig.range && p.getUser() != player.getUser())
                    {
                    p.getUser().a.b(new dv(player.getUser().g));
                    }
                }
    }
    //Send "visibility" toggle to invisible players turning them back to normal
    public static void sendNotInvisible(Player player){
        for (Player p : etc.getServer().getPlayerList())
                {
                    if (getDistance(player, p) < vConfig.range && p.getUser() != player.getUser())
                    {
                    p.getUser().a.b(new d(player.getUser()));
                    }
                }
    }
    public String getGroupPrefix(Player player){
        String groups[] = player.getGroups();
        String groupline[] = null;
        String prefix = Colors.White;
        if(vConfig.getInstance().groupcoloredbrackets() && groups[0].toString().length() > 0){
        //Read the file
        properties = new PropertiesFile(location);
			try {
				properties.load();
			} catch (IOException e) {
				log.log(Level.SEVERE, "Exception while loading " + location, e);
			}
                        //Grab the line with the same group as the player
                        groupline = properties.getString(groups[0]).split(":");
                        //Check if the prefix is null or not
                        if(!groupline[0].isEmpty())
                        {
                        //vChat.colorChange(groupline[0].charAt(0));
                        prefix = groupline[0];
                        prefix = vChat.colorChange(prefix.charAt(0));
                        }
        }
                        return prefix;
    }
    
    public static vmc getInstance() {
		if (instance == null) {
			instance = new vmc();
		}
		return instance;	
	}
}
