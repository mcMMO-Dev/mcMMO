//This is where the bulk of the plugin is
import java.util.Locale;
import java.util.logging.Logger;
import java.util.logging.Level;
public class vminecraft extends Plugin {
    @Override
    public void disable() {
        //I have to include this to compile, not sure why.
    }

    @Override
    public void enable() {
        //I have to include this to compile, not sure why.
    }
    static final Logger log = Logger.getLogger("Minecraft");

    @Override
    public void onLogin(Player player)
    {
        settings.getInstance().rules();
        settings.getInstance().loadSettings();
    }
    private String rules[];
    public boolean onChat(Player player, String message){
        settings.getInstance().rules();
        settings.getInstance().loadSettings(); //So you can disable/enable things in the txt files without having to reload the server
        String temp2 = "<" + player.getColor() + player.getName()  +  Colors.White +"> "; //Copies the formatting of id.java
        String adminchat = Colors.DarkPurple + "{" + player.getColor() + player.getName()  +  Colors.DarkPurple +"}" + Colors.White + " "; //Special formatting for adminchat
        String message2 = ""; //Used for greentext and FFF
        String check = temp2+message; //Calculates how long your message will be including your name in the equation, this prevents minecraft clients from crashing when a color code is inserted after a linebreak
        if (settings.getInstance().adminchat()&&message.startsWith("@") && (player.isInGroup("mods") || player.isInGroup("admins") || player.isInGroup("superadmins"))) {
            for (Player p : etc.getServer().getPlayerList()) {
                                        String blaa = "";
                if (p != null) {
                    if (player.isInGroup("mods") || (player.isInGroup("admins")) || (player.isInGroup("superadmins"))) {
                        for ( int x = 1; x< message.length(); x++) {
                        blaa+=message.charAt(x);
                        }
                        if (p.isInGroup("superadmins") || p.isInGroup("mods") || p.isInGroup("admins")){
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
            id.a.log(Level.INFO, "<"+player.getName()+"> "+message);
            message = Colors.LightGreen + message;
            message2 = temp2 + message;
            other.gmsg(message2);            
            return true;
        }
        //FFF
        if (settings.getInstance().FFF()&&message.startsWith("FFF")) {
            id.a.log(Level.INFO, "<"+player.getName()+"> "+message);
            message = Colors.Red + message;
            message2 = temp2 + message;
            other.gmsg(message2);            
            return true;
        }
        //QuakeColors
        if(settings.getInstance().quakeColors()&&message.length()>2 && lengthCheck(check)) {
			String temp = "";
			for(int x = 0; x< message.length(); x++)
			{
				if(message.charAt(x)=='^'&&x!=message.length()-1)
				{
					temp+=colorChange(message.charAt(x+1));
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
        //Replacement for /tp
        if(split[0].equalsIgnoreCase("/tp")) {
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
        if ((split[0].equalsIgnoreCase("/tphere") || split[0].equalsIgnoreCase("/s"))) {
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
        if(split[0].equalsIgnoreCase("/kick")) {
            Player playerTarget = etc.getServer().matchPlayer(split[1]);
            if (playerTarget != null && !playerTarget.hasControlOver(player)) {
            other.gmsg(player.getColor()+player.getName()+Colors.Blue+" has kicked "+Colors.Red+playerTarget.getColor()+playerTarget.getName());
            }
        }
        if(split[0].equalsIgnoreCase("/ban")) {
            Player playerTarget = etc.getServer().matchPlayer(split[1]);
            if (playerTarget != null && !playerTarget.hasControlOver(player)) {
            other.gmsg(player.getColor()+player.getName()+Colors.Blue+" has banned "+Colors.Red+playerTarget.getColor()+playerTarget.getName());
            }
        }
        if(split[0].equalsIgnoreCase("/ipban")) {
            Player playerTarget = etc.getServer().matchPlayer(split[1]);
            if (playerTarget != null && !playerTarget.hasControlOver(player)) {
            other.gmsg(player.getColor()+player.getName()+Colors.Blue+" has IP banned "+Colors.Red+playerTarget.getColor()+playerTarget.getName());
            }
        }
        if(split[0].equalsIgnoreCase("/time")) {
            if (split.length <= 2) {
                other.gmsg(Colors.Blue+"Time changes thanks to "+player.getColor()+player.getName());
                return false;
            }
        }
        //Rules
        if(split[0].equalsIgnoreCase("/rules")) {
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
                    if(lengthCheck(temp2))
                    {
                           id.a.log(Level.INFO, player.getName()+" fabulously said \""+ str+"\"");
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
                    } else {
                            player.sendMessage(Colors.Rose + "Message is too long");
                    }
                    return true;
                }
        //Promote
        if (settings.getInstance().cmdPromote() && split[0].equalsIgnoreCase("/promote")) {
	if(split.length != 2)
	{
		player.sendMessage(Colors.Rose + "Usage is /promote [Player]");
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
		if(playerTarget.isInGroup("admins"))
		{
			player.sendMessage(Colors.Rose + "You can not promote " + split[1] + " any higher.");
		}
		if(playerTarget.isInGroup("mods") && (player.isInGroup("superadmins")))
		{
			playerTarget.setGroups(ranks.Admins);
			etc.getInstance().getDataSource().modifyPlayer(playerTarget);
			String message = Colors.Yellow + split[1] + " was promoted to" + Colors.Rose + " Admin";
			other.gmsg(message);
		}
		else if (playerTarget.isInGroup("trusted") && (player.isInGroup("admins") || player.isInGroup("superadmins")))
		{
			playerTarget.setGroups(ranks.Mods);
			etc.getInstance().getDataSource().modifyPlayer(playerTarget);
			String message = Colors.Yellow + split[1] + " was promoted to" + Colors.DarkPurple + " Mod";
			other.gmsg(message);
		}
		else if (playerTarget.isInGroup("default") && (player.isInGroup("mods") || player.isInGroup("admins") || player.isInGroup("superadmins")))
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
         //Whois will display info about a player
         if (settings.getInstance().cmdWhoIs()&&split[0].equalsIgnoreCase("/whois")) {
            if (split.length < 2) {
                    player.sendMessage(Colors.Rose + "Usage is /whois [player]");
            }
            String admin ="";
            String group ="";
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
                    etc.getInstance().addCommand("/whois", "/whois [user]");
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
                    if (playerTarget.isInGroup("superadmins")){
                        group = "superadmins";
                    }else if(playerTarget.isInGroup("admins")){
                        group = "admins";
                    }else if(player.isInGroup("mods")){
                        group = "mods";
                    }else if(player.isInGroup("trusted")){
                        group = "trusted";
                    }else{
                        group = "Default";
                        }
                    //Displaying the information
                    player.sendMessage(Colors.Blue + "Whois results for "+split[1]+".");
                    //Group
                    player.sendMessage(Colors.Blue + "Group: "+group);
                    //Admin
                    player.sendMessage(Colors.Blue+"Admin: "+admin);
                    //IP
                    player.sendMessage(Colors.Blue+"IP: "+IP);
                    //Restrictions
                    player.sendMessage(Colors.Blue+"Can ignore restrictions: "+ignore);
                    return true;
        } else {
                        player.sendMessage(Colors.Rose+"Player not found.");
            }
        }
        //Say
        if (split[0].equalsIgnoreCase("/say")) {
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

    //Calculates how long the specified String is to prevent linebreaks when using scripts that insert color codes, designed to be used with playername included
    private boolean lengthCheck(String str)
	{
		int length = 0;
		for(int x = 0; x<str.length(); x++)
		{
			if("i;,.:|!".indexOf(str.charAt(x)) != -1)
			{
				length+=2;
			}
			else if("l'".indexOf(str.charAt(x)) != -1)
			{
				length+=3;
			}
			else if("tI[]".indexOf(str.charAt(x)) != -1)
			{
				length+=4;
			}
			else if("kf{}<>\"*()".indexOf(str.charAt(x)) != -1)
			{
				length+=5;
			}
			else if("hequcbrownxjmpsvazydgTHEQUCKBROWNFXJMPSVLAZYDG1234567890#\\/?$%-=_+&".indexOf(str.charAt(x)) != -1)
			{
				length+=6;
			}
			else if("@~".indexOf(str.charAt(x)) != -1)
			{
				length+=7;
			}
			else if(str.charAt(x)==' ')
			{
				length+=4;
			}
		}
		if(length<=316)
		{
			return true;
		} else { return false; }

	}
    //QuakeColors Part 2
    private String colorChange(char colour)
	{
		String color = "";
		switch(colour)
		{
			case '0':
				color = Colors.Black;
				break;
			case '1':
				color = Colors.Navy;
				break;
			case '2':
				color = Colors.Green;
				break;
			case '3':
				color = Colors.Blue;
				break;
			case '4':
				color = Colors.Red;
				break;
			case '5':
				color = Colors.Purple;
				break;
			case '6':
				color = Colors.Gold;
				break;
			case '7':
				color = Colors.LightGray;
				break;
			case '8':
				color = Colors.Gray;
				break;
			case '9':
				color = Colors.DarkPurple;
				break;
			case 'a':
				color = Colors.LightGreen;
				break;
			case 'b':
				color = Colors.LightBlue;
				break;
			case 'c':
				color = Colors.Rose;
				break;
			case 'd':
				color = Colors.LightPurple;
				break;
			case 'e':
				color = Colors.Yellow;
				break;
			case 'f':
				color = Colors.White;
				break;
			case 'A':
				color = Colors.LightGreen;
				break;
			case 'B':
				color = Colors.LightBlue;
				break;
			case 'C':
				color = Colors.Rose;
				break;
			case 'D':
				color = Colors.LightPurple;
				break;
			case 'E':
				color = Colors.Yellow;
				break;
			case 'F':
				color = Colors.White;
				break;
			default:
				color = Colors.White;
				break;
		}
		return color;
	}
}