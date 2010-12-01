//=====================================================================
//Class:	vMinecraftAnnouncements
//Use:		Encapsulates all announcements broadcast when commands are
//			run
//Author:	nossr50, TrapAlice, cerevisiae
//=====================================================================
public class vMinecraftAnnouncements {

	//=====================================================================
	//Function:	onCommand
	//Input:	Player player: The player calling the command
	//			String[] split: The arguments
	//Output:	boolean: If the user has access to the command
	//					 and it is enabled
	//Use:		Checks if /kick, /ban, /ipban, and /time are run and
	//			displays a global message
	//=====================================================================
	public boolean onCommand(Player player, String[] split) {
		if(!player.canUseCommand(split[0])) {
			return false;
		}
		//Only run if the global message feature is enabled
		if(vMinecraftSettings.getInstance().globalmessages())
		{
			//Global messages that should only parse when a command can be successful
			if(split[0].equalsIgnoreCase("/kick")) {
				Player playerTarget = etc.getServer().matchPlayer(split[1]);
				if (playerTarget != null && !playerTarget.hasControlOver(player)) {
					vMinecraftChat.gmsg(player.getColor()+player.getName()+Colors.Blue+" has kicked "+Colors.Red+playerTarget.getColor()+playerTarget.getName());
				}
			}
			if(split[0].equalsIgnoreCase("/ban")) {
				Player playerTarget = etc.getServer().matchPlayer(split[1]);
				if (playerTarget != null && !playerTarget.hasControlOver(player)) {
					vMinecraftChat.gmsg(player.getColor()+player.getName()+Colors.Blue+" has banned "+Colors.Red+playerTarget.getColor()+playerTarget.getName());
				}
			}
			if(split[0].equalsIgnoreCase("/ipban")) {
				Player playerTarget = etc.getServer().matchPlayer(split[1]);
				if (playerTarget != null && !playerTarget.hasControlOver(player)) {
					vMinecraftChat.gmsg(player.getColor()+player.getName()+Colors.Blue+" has IP banned "+Colors.Red+playerTarget.getColor()+playerTarget.getName());
				}
			}
			if(split[0].equalsIgnoreCase("/time")) {
				if (split.length <= 2) {
					vMinecraftChat.gmsg(Colors.Blue+"Time changes thanks to "+player.getColor()+player.getName());
				}
			}
		}
	    
		return true;
	}
}
