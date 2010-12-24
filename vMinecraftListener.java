import java.util.logging.Level;
import java.util.logging.Logger;
//=====================================================================
//Class:	vMinecraftListener
//Use:		The listener to catch incoming chat and commands
//Author:	nossr50, TrapAlice, cerevisiae
//=====================================================================
public class vMinecraftListener extends PluginListener {
	protected static final Logger log = Logger.getLogger("Minecraft");
	
	//=====================================================================
	//Function:	disable
	//Input:	None
	//Output:	None
	//Use:		Disables vMinecraft, but why would you want to do that? ;)
	//=====================================================================
	public void disable() {
		log.log(Level.INFO, "vMinecraft disabled");
	}
         public void onPlayerMove(Player player, Location from, Location to) {
             if(vMinecraftSettings.getInstance().isFrozen(player.getName())){
                 player.teleportTo(from);
             }
    }
	
	//=====================================================================
	//Function:	onChat
	//Input:	Player player: The player calling the command
	//			String message: The message to color
	//Output:	boolean: If the user has access to the command
	//					 and it is enabled
	//Use:		Checks for quote, rage, and colors
	//=====================================================================
        
    public boolean onChat(Player player, String message){

    	//Quote (Greentext)
    	if (message.startsWith("@") ||
    			vMinecraftSettings.getInstance().isAdminToggled(player.getName()))
    		return vMinecraftChat.adminChat(player, message);
    	
    	else if (message.startsWith(">"))
    		return vMinecraftChat.quote(player, message);
        	
        //Rage (FFF)
        else if (message.startsWith("FFF"))
        	return vMinecraftChat.rage(player, message);
    	
    	//Send through quakeColors otherwise
        else
        	return vMinecraftChat.quakeColors(player, message);
    }
    
	//=====================================================================
	//Function:	onCommand
	//Input:	Player player: The player calling the command
	//			String[] split: The arguments
	//Output:	boolean: If the user has access to the command
	//					 and it is enabled
	//Use:		Checks for exploits and runs the commands
	//=====================================================================
	public boolean onCommand(Player player, String[] split) {

        //Copy the arguments into their own array.
	    String[] args = new String[split.length - 1];
        System.arraycopy(split, 1, args, 0, args.length);

        //Return the results of the command
        int exitCode = vMinecraftCommands.cl.call(split[0], player, args);
        if(exitCode == 0)
        	return false;
        else if(exitCode == 1)
        	return true;
        else
        	return false;
        
	}
    
	//=====================================================================
	//Function:	onHealthChange
	//Input:	Player player: The player calling the command
	//			int oldValue: The old health value;
	//			int newValue: The new health value
	//Output:	boolean: If the user has access to the command
	//					 and it is enabled
	//Use:		Checks for exploits and runs the commands
	//=====================================================================
    public boolean onHealthChange(Player player,int oldValue,int newValue){
        //Sets a player as dead
        if (player.getHealth() < 1){
            vMinecraftUsers.getProfile(player).isDead(true);
        }
        if (player.getHealth() > 1 && vMinecraftUsers.getProfile(player).isDead()){
                if(vMinecraftSettings.getInstance().playerspawn())
                {
                Warp home = null;
                home = etc.getDataSource().getHome(player.getName());
                player.teleportTo(home.Location);
                //Makes sure the player has a custom home before telling them about /myspawn
                if(etc.getServer().getSpawnLocation() != etc.getDataSource().getHome(player.getName()).Location){
                vMinecraftChat.sendMessage(player, Colors.DarkPurple + "Return here with /myspawn, the penalty for returning is the complete loss of inventory");
                } else {
                    vMinecraftChat.sendMessage(player, Colors.DarkPurple + "Set your own spawn with /myspawn");
                }
                }
                vMinecraftUsers.getProfile(player).isDead(false);
                vMinecraftChat.gmsg(Colors.Gray + player.getName() + " " + vMinecraftSettings.randomDeathMsg());
        }
        return false;
    }

    public void onLogin(Player player){
    	vMinecraftChat.sendMessage(player, player, Colors.Rose + "There are currently " + etc.getServer().getPlayerList().size() + " players online.");
        vMinecraftUsers.addUser(player);
    }

    public void onDisconnect(Player player){
        vMinecraftUsers.removeUser(player);
    }
    
    public boolean onIgnite(Block block, Player player) {
        if(vMinecraftSettings.stopFire){
            if(block.getStatus() == 3 || block.getStatus() == 1){
                return true;
            }
            if(block.getStatus() == 2 && !player.isAdmin()){
                return true;
            }
        }
        return false;
    }
    
    public boolean onDamage(PluginLoader.DamageType type, BaseEntity attacker, BaseEntity defender, int amount) {   	

        return false;
    }
}