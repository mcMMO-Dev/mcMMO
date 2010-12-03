import java.util.logging.Level;
import java.util.logging.Logger;
//=====================================================================
//Class:	vMinecraftListener
//Use:		The listener to catch incoming chat and commands
//Author:	nossr50, TrapAlice, cerevisiae
//=====================================================================
public class vMinecraftListener extends PluginListener {
    public int damagetype;
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
    	if (vMinecraftSettings.getInstance().isEzModo(player.getName())) {
            return oldValue > newValue;
        }
        return false;
    }

    public void onLogin(Player player){
        vMinecraftUsers.addUser(player);
    }
    public boolean onDamage(PluginLoader.DamageType type, BaseEntity attacker, BaseEntity defender, int amount) {
        if(defender.isPlayer()){
        Player player = (Player)defender;
        if (attacker.isPlayer()) {
            Player pAttacker = (Player)attacker;
            if(player.getHealth() < 1){
                vMinecraftChat.gmsg(player, pAttacker.getName() + " has murdered " + player.getName());
            }
        }
     if (player.getHealth() < 1 && !attacker.isPlayer()) {
         if (type == type.CREEPER_EXPLOSION) {
            damagetype = 1; //Creeper
         } else if(type == type.FALL){
             damagetype = 2; //Fall
         } else if(type == type.FIRE){
             damagetype = 3; //Fire going to make it share with firetick since its similar
         } else if (type == type.FIRE_TICK){
             damagetype = 4; //Firetick
         } else if (type == type.LAVA){
             damagetype = 5; //Lava
         } else if (type == type.WATER){
             damagetype = 6; //Water
         }
         //This should trigger the player death message
         } else if (player.getHealth() < 1 && attacker.isPlayer()){
             damagetype = 7; //Player
         }
            if(damagetype == 1){
                vMinecraftChat.gmsg(player,player.getName() + Colors.Red + " was blown to bits by a creeper");
                damagetype = 0;
            } else if (damagetype == 2) {
                    vMinecraftChat.gmsg(player,player.getName() + Colors.Red + " fell to death!");
                    damagetype = 0;
                } else if (damagetype ==3){
                    vMinecraftChat.gmsg(player, player.getName() + Colors.Red + " was incinerated");
                    damagetype = 0;
                } else if (damagetype == 4){
                    vMinecraftChat.gmsg(player, Colors.Red + " Stop drop and roll, not scream, run, and burn " + player.getName());
                    damagetype = 0;
                } else if (damagetype == 5){
                    vMinecraftChat.gmsg(player, Colors.Red + player.getName() + " drowned in lava");
                    damagetype = 0;
                } else if (damagetype == 6){
                    vMinecraftChat.gmsg(player, Colors.Blue + player.getName() + " should've attended that swimming class");
                    damagetype = 0;
                } else if (damagetype == 7){
                Player pAttacker = (Player)attacker;
                vMinecraftChat.gmsg(player, pAttacker.getName() + " has murdered " + player.getName());
                damagetype = 0;
                } 
                else {
    		vMinecraftChat.gmsg(player, Colors.Gray + player.getName() + " " + vMinecraftSettings.randomDeathMsg());
            }
        }
                return false;
    }

}