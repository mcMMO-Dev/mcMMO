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
        if (player.getHealth() > 1){
	        vMinecraftUsers.getProfile(player).isDead(false);
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
    	Player defend = null;
    	if(defender != null && defender.isPlayer())
    	{
	    	defend = defender.getPlayer();
	    	if (vMinecraftSettings.getInstance().isEzModo(defend.getName())) {
	            return true;
	        }
	    	
	        if (defend.getHealth() > 0)
	        	return false;
	        if (vMinecraftUsers.getProfile(defend).isDead())
	    		return false;
	        
	        vMinecraftUsers.getProfile(defend).isDead(true);

	    	Player attack = null;
	    	if(attacker != null && attacker.isPlayer())
	    		attack = attacker.getPlayer();
	    	if(attack != null)
	    	{
                    vMinecraftChat.gmsg(defend.getName() + " was slain by " + attack.getName());
	    	}
        	if (type == type.CREEPER_EXPLOSION) {
                vMinecraftChat.gmsg(defend.getName() + Colors.Rose + " was blown to bits by a creeper");
        	} else if(type == type.FALL){
                vMinecraftChat.gmsg(defend.getName() + Colors.Rose + " fell to death!");
        	} else if(type == type.FIRE){
                vMinecraftChat.gmsg(defend.getName() + Colors.Rose + " was incinerated");
        	} else if (type == type.FIRE_TICK){
                vMinecraftChat.gmsg(defend.getName() + Colors.Rose + " Stop drop and roll, not scream, run, and burn ");
        	} else if (type == type.LAVA){
                vMinecraftChat.gmsg(defend.getName() + Colors.Rose + " drowned in lava");
        	} else if (type == type.WATER){
        		log.log(Level.INFO, "Water");
                vMinecraftChat.gmsg(defend.getName() + Colors.Rose + " should've attended that swimming class");
        	} else {
                vMinecraftChat.gmsg(Colors.Gray + defend.getName() + " " + vMinecraftSettings.randomDeathMsg());
            }
	    		
    	}
        return false;
    }
}