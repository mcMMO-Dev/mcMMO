//Vminecraft plugin by nossr50 & TrapAlice
import java.util.logging.Level;
import java.util.logging.Logger;
    
    public class vminecraftListener extends PluginListener {
    protected static final Logger log = Logger.getLogger("Minecraft");
        public void disable() {
            log.log(Level.INFO, "vminecraft disabled");
    }
	private ArrayList<String> ezmodo = new ArrayList<String>(); //An array of players currently in ezmodo

    public void enable() {
        settings.getInstance().loadSettings(); //Load the settings files
        log.log(Level.INFO, "vminecraft enabled");
    }
	public void onPlayerMove () {
		if (ezmodo.contains(player.getName())){
		if (player.getHealth() < 30) 
						{
						player.setHealth(30);
						}
				     }
	}
    public boolean onChat(Player player, String message){
        String temp2 = "<" + player.getColor() + player.getName()  +  Colors.White +"> "; //Copies the formatting of id.java
        String adminchat = Colors.DarkPurple + "{" + player.getColor() + player.getName()  +  Colors.DarkPurple +"}" + Colors.White + " "; //Special formatting for adminchat
        String message2 = ""; //Used for greentext and FFF
        String check = temp2+message; //Calculates how long your message will be including your name in the equation, this prevents minecraft clients from crashing when a color code is inserted after a linebreak
        if (settings.getInstance().adminchat()&&message.startsWith("@") && (player.isAdmin() || player.canUseCommand("/adminchat"))) {
            for (Player p : etc.getServer().getPlayerList()) {
                                        String blaa = "";
                if (p != null) {
                    if (p.isAdmin() || (p.canUseCommand("/adminchat"))) {
                        for ( int x = 1; x< message.length(); x++) {
                        blaa+=message.charAt(x);
                        }
                        if (p.isAdmin() || (p.canUseCommand("/adminchat"))){
                        if (p != null) {
                                p.sendMessage(adminchat+blaa);
                           }
                        }
                                                }
                                }
                    }
            log.log(Level.INFO, "@"+temp2+message); //So you can read adminchat from the server console
            return true;

      }
        //Greentext
        if (settings.getInstance().greentext()&&message.startsWith(">")) {
            log.log(Level.INFO, "<"+player.getName()+"> "+message);
            message = Colors.LightGreen + message;
            message2 = temp2 + message;
            other.gmsg(message2);
            return true;
        }
        //FFF
        if (settings.getInstance().FFF()&&message.startsWith("FFF")) {
            log.log(Level.INFO, "<"+player.getName()+"> "+message);
            message = Colors.Red + message;
            message2 = temp2 + message;
            other.gmsg(message2);
            return true;
        }
        //QuakeColors
        if(settings.getInstance().quakeColors()&&message.length()>2 && other.lengthCheck(check)) {
			String temp = "";
			for(int x = 0; x< message.length(); x++)
			{
				if(message.charAt(x)=='^'&&x!=message.length()-1)
				{
					temp+=other.colorChange(message.charAt(x+1));
					x+=1;
				}
				else{
					temp+=message.charAt(x);
				}
			}
                        log.log(Level.INFO, "<"+player.getName()+"> "+message);
			message = temp2 + temp + " ";
                        for (Player p : etc.getServer().getPlayerList()) {
                                if (p != null) {
                                     other.gmsg(message);
                                     return true;
                                }
                            }
		}
        return false;
    }

        public boolean onCommand(Player player, String[] split) {
        if(!player.canUseCommand(split[0])) {
           return false;
        }
        if(settings.getInstance().cmdMasstp() && split[0].equalsIgnoreCase("/masstp")) {                        
            for (Player p : etc.getServer().getPlayerList()) {
            if (!p.hasControlOver(player)) {
                p.teleportTo(player);
            }
            
        }
            player.sendMessage(Colors.Blue+"Summoning successful.");
        }
		//ezmodo
		if (split[0].equals("/ezmodo")) {
				if (ezmodo.contains(player.getName())) {
					player.sendMessage(Colors.Red + "ezmodo = off");
					ezmodo.remove(ezmodo.indexOf(player.getName()));
				} else {
					player.sendMessage(Colors.LightBlue + "eh- maji? ezmodo!?");
					player.sendMessage(Colors.Rose + "kimo-i");
					player.sendMessage(Colors.LightBlue + "Easy Mode ga yurusareru no wa shougakusei made dayo ne");
					player.sendMessage(Colors.Red + "**Laughter**");
					ezmodo.add(player.getName());
					player.setHealth(30);
				}
				return true;
			}
        //Replacement for /tp
        if(settings.getInstance().cmdTp() && split[0].equalsIgnoreCase("/tp")) {
            {
                if (split.length < 2) {
                    player.sendMessage(Colors.Rose + "Correct usage is: /tp [player]");
                    return true;
                }

                Player playerTarget = etc.getServer().matchPlayer(split[1]);

                if (player.getName().equalsIgnoreCase(split[1])) {
                    player.sendMessage(Colors.Rose + "You're already here!");
                    return true;
                }

                if (!player.hasControlOver(playerTarget)) {
                    player.sendMessage(Colors.Red + "That player has higher permissions than you.");
                    return true;
                }

                if (playerTarget != null) {
                    log.log(Level.INFO, player.getName() + " teleported to " + playerTarget.getName());
                    player.teleportTo(playerTarget);
                    return true;
                } else {
                    player.sendMessage(Colors.Rose + "Can't find user " + split[1] + ".");
                    return true;
                }
            }
        }
        //Replacement for /tphere
        if (settings.getInstance().cmdTphere() && (split[0].equalsIgnoreCase("/tphere") || split[0].equalsIgnoreCase("/s"))) {
                if (split.length < 2) {
                    player.sendMessage(Colors.Rose + "Correct usage is: /tphere [player]");
                    return true;
                }

                Player playerTarget = etc.getServer().matchPlayer(split[1]);

                if (!player.hasControlOver(playerTarget)) {
                    player.sendMessage(Colors.Red + "That player has higher permissions than you.");
                    return true;
                }
                if (player.getName().equalsIgnoreCase(split[1])) {
                    player.sendMessage(Colors.Rose + "Wow look at that! You teleported yourself to yourself!");
                    return true;
                }

                if (playerTarget != null) {
                    log.log(Level.INFO, player.getName() + " teleported " + player.getName() + " to their self.");
                    playerTarget.teleportTo(player);
                } else {
                    player.sendMessage(Colors.Rose + "Can't find user " + split[1] + ".");
                }
        }
        //Global messages that should only parse when a command can be successful
        if(settings.getInstance().globalmessages() && split[0].equalsIgnoreCase("/kick")) {
            Player playerTarget = etc.getServer().matchPlayer(split[1]);
            if (playerTarget != null && !playerTarget.hasControlOver(player)) {
            other.gmsg(player.getColor()+player.getName()+Colors.Blue+" has kicked "+Colors.Red+playerTarget.getColor()+playerTarget.getName());
            }
        }
        if(settings.getInstance().globalmessages() && split[0].equalsIgnoreCase("/ban")) {
            Player playerTarget = etc.getServer().matchPlayer(split[1]);
            if (playerTarget != null && !playerTarget.hasControlOver(player)) {
            other.gmsg(player.getColor()+player.getName()+Colors.Blue+" has banned "+Colors.Red+playerTarget.getColor()+playerTarget.getName());
            }
        }
        if(settings.getInstance().globalmessages() && split[0].equalsIgnoreCase("/ipban")) {
            Player playerTarget = etc.getServer().matchPlayer(split[1]);
            if (playerTarget != null && !playerTarget.hasControlOver(player)) {
            other.gmsg(player.getColor()+player.getName()+Colors.Blue+" has IP banned "+Colors.Red+playerTarget.getColor()+playerTarget.getName());
            }
        }
        if(settings.getInstance().globalmessages() && split[0].equalsIgnoreCase("/time")) {
            if (split.length <= 2) {
                other.gmsg(Colors.Blue+"Time changes thanks to "+player.getColor()+player.getName());
                return false;
            }
        }
        //Should only reload vminecraft settings if the player is able to use /reload
        if(split[0].equalsIgnoreCase("/reload") && player.canUseCommand("/reload")) {
            settings.getInstance().loadSettings();
            return false;
        }
        //Rules
        if(settings.getInstance().cmdRules() && split[0].equalsIgnoreCase("/rules")) {           
           for (String str : settings.getInstance().getRules()) {
           player.sendMessage(Colors.Blue+str);
            }
           return true;
        }
        //Fabulous
        if(split[0].equalsIgnoreCase("/fabulous") && settings.getInstance().cmdFabulous()) {                    
                    if (split.length == 1) {return false;}
                    String temp = "";
                    String str = "";
                    str = etc.combineSplit(1, split, " ");
                    String temp2 = "<" + player.getName()  + "> "+str;
                    String[] rainbow = new String[] {Colors.Red, Colors.Rose, Colors.Yellow, Colors.Green, Colors.Blue, Colors.LightPurple, Colors.Purple};
                    int counter=0;
                    if(other.lengthCheck(temp2))
                    {
                    log.log(Level.INFO, player.getName()+" fabulously said \""+ str+"\"");
                    for(int x=0; x<str.length(); x++)
                    {
                            temp+=rainbow[counter]+str.charAt(x);
                            counter++;
                            if(str.charAt(x)==' ') { counter--;}
                            if(counter==-1){counter = 6; }
                            if(counter==7){counter = 0; }
                    }
                    str = temp+" ";
                    String message = "<" + player.getColor() + player.getName() + Colors.White + "> " + str;

                            other.gmsg(message);
                            return true;
                    } else {
                            player.sendMessage(Colors.Rose + "Message is too long");
                    }
                    return true;
                }
        /*
        //Promote
        if (settings.getInstance().cmdPromote() && split[0].equalsIgnoreCase("/promote")) {
	if(split.length != 2)
	{
		player.sendMessage(Colors.Rose + "Usage is /promote [Player]");

	}

	Player playerTarget = null;
        if(split.length==2){
	for( Player p : etc.getServer().getPlayerList())
	{
		if (p.getName().equalsIgnoreCase(split[1]))
		{
			playerTarget = p;
		}
	}

	if( playerTarget!=null)
	{
            String playerTargetGroup[] = playerTarget.getGroups();
            String playerGroup[] = player.getGroups();
            player.sendMessage("Debug data:");
            player.sendMessage("PlayerTarget: "+playerTargetGroup[0]);
            player.sendMessage("Player: "+playerGroup[0]);
		if(playerTargetGroup[0].equals("admins"))
		{
			player.sendMessage(Colors.Rose + "You can not promote " + split[1] + " any higher.");
		}
		if(playerTargetGroup[0].equals("mods") && (playerGroup[0].equals("owner")))
		{
			playerTarget.setGroups(ranks.Admins);
			etc.getInstance().getDataSource().modifyPlayer(playerTarget);
			String message = Colors.Yellow + split[1] + " was promoted to" + Colors.Rose + " Admin";
			other.gmsg(message);
		}
		else if (playerTargetGroup[0].equals("trusted") && (playerGroup[0].equals("admins") || playerGroup[0].equals("owner")))
		{
			playerTarget.setGroups(ranks.Mods);
                        playerTargetGroup[0]="Mods";
			etc.getInstance().getDataSource().modifyPlayer(playerTarget);
			String message = Colors.Yellow + split[1] + " was promoted to" + Colors.DarkPurple + " Mod";
			other.gmsg(message);
		}
		else if (playerTargetGroup[0].equals("default") && (playerGroup[0].equals("mods") || playerGroup[0].equals("admins") || player.isInGroup("owner")))
		{
			playerTarget.setGroups(ranks.Trusted);
                        etc.getInstance().getDataSource().modifyPlayer(playerTarget);
                        String message = Colors.Yellow + split[1] + " was promoted to" + Colors.LightGreen + " Trusted";
                        other.gmsg(message);
		}
                return true;
	}
	else{
		player.sendMessage(Colors.Rose + "Player not found");
	}
	log.log(Level.INFO, "Command used by " + player + " " + split[0] +" "+split[1]+" ");
}
        }
        //Demote
                if (settings.getInstance().cmdPromote() && split[0].equalsIgnoreCase("/promote"))
{
	if(split.length != 2)
	{
		player.sendMessage(Colors.Rose + "Usage is /demote [Player]");
	}

	Player playerTarget = null;

	for( Player p : etc.getServer().getPlayerList())
	{
		if (p.getName().equalsIgnoreCase(split[1]))
		{
			playerTarget = p;
		}
	}

	if( playerTarget!=null)
	{
		if(playerTarget.isInGroup("admins") && (player.isInGroup("superadmins")))
		{
                    playerTarget.setGroups(ranks.Mods);
                    etc.getInstance().getDataSource().modifyPlayer(playerTarget);
                    String message = Colors.Yellow + split[1] + " was demoted to" + Colors.DarkPurple + " Mod";
                    other.gmsg(message);
		}
		if(playerTarget.isInGroup("mods") && (player.isInGroup("admins") || player.isInGroup("superadmins")))
		{
			playerTarget.setGroups(ranks.Trusted);
			etc.getInstance().getDataSource().modifyPlayer(playerTarget);
			String message = Colors.Yellow + split[1] + " was demoted to" + Colors.LightGreen + " Trusted";
			other.gmsg(message);
		}
		else if (playerTarget.isInGroup("trusted") && (player.isInGroup("mods") || player.isInGroup("superadmins") || player.isInGroup("admins")))
		{
			playerTarget.setGroups(ranks.Def);
			etc.getInstance().getDataSource().modifyPlayer(playerTarget);
			String message = Colors.Yellow + split[1] + " was demoted to" + Colors.White + " Default";
			other.gmsg(message);
		}
		else if (playerTarget.isInGroup("default") && (player.isInGroup("mods") || player.isInGroup("admins") || player.isInGroup("superadmins")))
		{
                player.sendMessage(Colors.Rose + "You can not demote " + split[1] + " any lower.");
		}
	}
	else{
		player.sendMessage(Colors.Rose + "Player not found");
	}
	log.log(Level.INFO, "Command used by " + player + " " + split[0] +" "+split[1]+" ");
        return true;
}
         * 
         */
         //Whois will display info about a player
         if (settings.getInstance().cmdWhoIs() && split[0].equalsIgnoreCase("/whois")) {            
            if (split.length < 2) {
                    player.sendMessage(Colors.Rose + "Usage is /whois [player]");
            }
            String admin ="";
            String ignore ="";
            String IP = "";
            Player playerTarget = null;
	for( Player p : etc.getServer().getPlayerList())
	{
		if (p.getName().equalsIgnoreCase(split[1]))
		{
			playerTarget = p;
		}
	}
        if (playerTarget != null){

                    IP = playerTarget.getIP();
                    if (playerTarget.canIgnoreRestrictions()) {
                        ignore = "True";
                    } else {
                            ignore ="False";
                        }
                    if (playerTarget.canIgnoreRestrictions()) {
                        admin = "True";
                    } else {
                        admin = "False";
                    }


                    //Displaying the information
                    player.sendMessage(Colors.Blue + "Whois results for "+split[1]+".");
                    //Group
                    for (String group : playerTarget.getGroups()) {
                    player.sendMessage(Colors.Blue + "Groups: "+group);
            }
                    //Admin
                    player.sendMessage(Colors.Blue+"Admin: "+admin);
                    //IP
                    player.sendMessage(Colors.Blue+"IP: "+IP);
                    //Restrictions
                    player.sendMessage(Colors.Blue+"Can ignore restrictions: "+ignore);


        } else {
                        player.sendMessage(Colors.Rose+"Player not found.");
            }
        return true;
        }
        //Say
        if (settings.getInstance().cmdSay() && (split[0].equalsIgnoreCase("/say"))) {
                      String sayan;
                      sayan = etc.combineSplit(1, split, " ");
                      other.gmsg(Colors.Yellow+sayan);
                  }
        //Should this be included?
        else {
            return false;
               }
        //Needs to be included
        return true;
        }
    }
