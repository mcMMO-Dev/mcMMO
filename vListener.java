import java.util.logging.Level;
import java.util.logging.Logger;
//=====================================================================
//Class:	vMinecraftListener
//Use:		The listener to catch incoming chat and commands
//Author:	nossr50, TrapAlice, cerevisiae
//=====================================================================
public class vListener extends PluginListener {
    protected static final Logger log = Logger.getLogger("Minecraft");
    //On console stuff    
    public boolean onConsoleCommand(String[] split) {
    String server = Colors.LightGreen + "[Server]" + Colors.DarkPurple;
    if(split[0].equalsIgnoreCase("say"))
    {
        if(split.length > 1){
        String args = " " + etc.combineSplit(1, split, " ");
        vChat.gmsg(server + args);
        return true;
        }
        return false;
    }
    if(split[0].equalsIgnoreCase("stop"))
        vChat.gmsg(server + "shutting down the server");
        return false;
    }
	
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
             if(vConfig.getInstance().isFrozen(player.getName())){
                 player.teleportTo(from);
             }
             vCom.updateInvisibleForAll();
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

    	if (message.startsWith("@") ||
            vConfig.getInstance().isAdminToggled(player.getName()))
            return vChat.adminChat(player, message);
        //PartyChat
        if((message.startsWith("!")) ||
                vConfig.getInstance().isPartyToggled(player.getName()))
                return vChat.partyChat(player, message);
        //Quote (Greentext)     
    	else if (message.startsWith(">"))
    		return vChat.quote(player, message);	
        //Rage (FFF)
        else if (message.startsWith("FFF"))
        	return vChat.rage(player, message);
    	//Send through quakeColors otherwise
        else
        	return vChat.quakeColors(player, message);
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
        int exitCode = vCom.cl.call(split[0], player, args);
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
            vUsers.getProfile(player).isDead(true);
        }
        if (player.getHealth() > 1 && vUsers.getProfile(player).isDead()){
                if(vConfig.getInstance().playerspawn())
                {
                Warp home = null;
                if (etc.getDataSource().getHome(player.getName()) != null){
                home = etc.getDataSource().getHome(player.getName());
                player.teleportTo(home.Location);
                player.sendMessage(Colors.DarkPurple + "Return here with /myspawn");
                player.sendMessage(Colors.DarkPurple + "The penalty for returning is the loss of inventory");
                }
                if(player.canUseCommand("/sethome"))
                player.sendMessage(Colors.DarkPurple + "Set your own spawn with /sethome");
                }
                vUsers.getProfile(player).isDead(false);
                if(!vUsers.getProfile(player).isSilent())
                vChat.gmsg(Colors.Gray + player.getName() + " " + vConfig.randomDeathMsg());
        }
        return false;
    }

    public void onLogin(Player player){
    	vChat.sendMessage(player, player, Colors.Rose + "There are currently " + etc.getServer().getPlayerList().size() + " players online.");
        vUsers.addUser(player);
    }

    public void onDisconnect(Player player){
        vUsers.removeUser(player);
    }
    
    public boolean onIgnite(Block block, Player player) {
        
        if(vConfig.getInstance().stopFire()){
            //There are 3 ways fire can spread
            //1 = lava, 2 = lighter, 3 = spread (other fire blocks)
            //Stop lava from spreading
            if(block.getStatus() == 1 && vConfig.getInstance().lavaSpread()){
                return true;
            }
            //Stop fire from spreading fire
            if (block.getStatus() == 3 && vConfig.getInstance().stopFire()){
                return true;
            }
            //Checking to see if any of the blocks fire is trying to spread to is on the "fireblockan" list
            if (block.getStatus() == 3){
                int x,
                        y,
                        z;
                x = block.getX();
                y = block.getY();
                z = block.getZ();
                //Finding out the blockid of the current blocks fire is trying to spread to
                int blockid = etc.getServer().getBlockIdAt(x, y, z);
                //Check to see the blockid doesn't match anything on the list
                for(x = 0; x >= vConfig.fireblockan.size(); x++){
                    if (vConfig.fireblockan.get(x) == blockid){
                        return true;
                    }
                }
                
            }
            //Stop players without permission from being able to set fires
            if(block.getStatus() == 2 && !player.canUseCommand("/flint")){
                return true;
            }
        }
        return false;
    }
    
    public boolean onDamage(PluginLoader.DamageType type, BaseEntity attacker, BaseEntity defender, int amount) {
        //Invincibility for EzModo players
        //This also checks if the defender is a player
        if(defender.isPlayer()){
            Player dplayer = defender.getPlayer();
            if(vConfig.getInstance().isEzModo(dplayer.getName())){
                return true;
            }
            //So far we've checked if the defender is a player, next we check if the attacker is one
            if(attacker != null && attacker.isPlayer()){
                //If the attacker is not null and is a player we assign the attacker to a new player variable
                Player aplayer = attacker.getPlayer();
                //Then we preceed to check if they are in the same party, the code for this is stored elsewhere
                if(vUsers.getProfile(dplayer).inParty()){
                    //If they are in the same party we tell onDamage to return true stopping the damage code from executing
                    if(vmc.inSameParty(aplayer, dplayer)){
                        return true;
                        //if they aren't we tell it to return false, making the damage happen
                    } else{
                        return false;
                    }
                }
                else {
                    return false;
                }
            }
        }
        return false;
    }
}
