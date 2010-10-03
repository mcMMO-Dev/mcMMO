//This is where the bulk of the plugin is
import java.util.logging.Logger;
import java.util.logging.Level;
public class vminecraft extends Plugin {
    //settings Settings;
    @Override
    public void disable() {
        //throw new UnsupportedOperationException("Not supported yet.");
        //I have to include this to compile, not sure why.
    }

    @Override
    public void enable() {
        //throw new UnsupportedOperationException("Not supported yet.");
        //I have to include this to compile, not sure why.
    }
    static final Logger log = Logger.getLogger("Minecraft");

    @Override
    public void onLogin(Player player)
    {
        settings.getInstance().loadSettings();
    }

    public boolean onChat(Player player, String message){
        //Settings.loadSettings();
        settings.getInstance().loadSettings();
        String playerb = player.getName(); //Used to get names from players, can't remember why I can't just use 'player'
        String temp2 = "<" + player.getColor() + player.getName()  +  Colors.White +"> "; //Inserts a name before the message
        String adminchat = Colors.LightGreen + "{" + player.getColor() + player.getName()  +  Colors.LightGreen +"}" + Colors.White + " "; //Inserts names admin chat style before the message
        String message2 = ""; //Used for greentext and FFF
        String check = temp2+message; //Calculates how long your message will be including your name in the equation, this prevents minecraft clients from crashing when a color code is inserted after a linebreak
        if (settings.getInstance().adminchat()&&message.startsWith("@") && (player.isInGroup("mods") || player.isInGroup("admins") || player.isInGroup("superadmins"))) {
            for (Player p : etc.getServer().getPlayerList()) {
                if (p != null) {
                    if (player.isInGroup("mods") || (player.isInGroup("admins")) || (player.isInGroup("superadmins"))) {
                        String blaa = "";
                        for ( int x = 1; x< message.length(); x++) {
                        blaa+=message.charAt(x);
                        }
                        p.sendMessage(adminchat+blaa);
                        log.log(Level.INFO, "@"+message);
                                                }
                                }
                    }
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
        if (player.canUseCommand(split[0])) {
            return false;
        }
        //Fabulous
        if (split[0].equalsIgnoreCase("/fabulous")&&settings.getInstance().cmdFabulous()) {
            etc.getInstance().addCommand("/fabulous", "/fabulous <message>");
                    if (split.length == 1) {return false;}
                    String temp = "";
                    String str = "";
                    //str = paramString.substring(paramString.indexOf(" ")).trim();
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
                }
        //Promote
        else if (settings.getInstance().cmdPromote() && split[0].equalsIgnoreCase("/promote"))
{
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
			etc.getInstance().getDataSource().modifyPlayer(player);
			String message = Colors.Yellow + split[1] + " was promoted to" + Colors.Rose + " Admin";
			other.gmsg(message);
		}
		else if (playerTarget.isInGroup("trusted") && (player.isInGroup("admins") || player.isInGroup("superadmins")))
		{
			playerTarget.setGroups(ranks.Mods);
			etc.getInstance().getDataSource().modifyPlayer(player);
			String message = Colors.Yellow + split[1] + " was promoted to" + Colors.DarkPurple + " Mod";
			other.gmsg(message);
		}
		else if (playerTarget.isInGroup("default") && (player.isInGroup("mods") || player.isInGroup("admins") || player.isInGroup("superadmins")))
		{
			player.setGroups(ranks.Trusted);
            etc.getInstance().getDataSource().modifyPlayer(player);
            String message = Colors.Yellow + split[1] + " was promoted to" + Colors.LightGreen + " Trusted";
            other.gmsg(message);
		}
	}
	else{
		player.sendMessage(Colors.Rose + "Player not found");
	}
	log.log(Level.INFO, "Command used by " + player + " " + split[0] +" "+split[1]+" ");
}
        //Demote
                else if (settings.getInstance().cmdPromote() && split[0].equalsIgnoreCase("/promote"))
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
                    String message = Colors.Yellow + split[1] + " was demoted to" + Colors.DarkPurple + " Mod";
                    other.gmsg(message);
		}
		if(playerTarget.isInGroup("mods") && (player.isInGroup("admins") || player.isInGroup("superadmins")))
		{
			playerTarget.setGroups(ranks.Trusted);
			etc.getInstance().getDataSource().modifyPlayer(player);
			String message = Colors.Yellow + split[1] + " was demoted to" + Colors.LightGreen + " Trusted";
			other.gmsg(message);
		}
		else if (playerTarget.isInGroup("trusted") && (player.isInGroup("mods") || player.isInGroup("superadmins") || player.isInGroup("admins")))
		{
			playerTarget.setGroups(ranks.Def);
			etc.getInstance().getDataSource().modifyPlayer(player);
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
}
          //Whois will display info about a player
         else if (settings.getInstance().cmdWhoIs()&&split[0].equalsIgnoreCase("/whois")) {
            String admin ="";
            String group ="";
            String ignore ="";
                    log.log(Level.INFO, "Command used by " + player + " " + split[0] +" "+split[1]+" ");
                    etc.getInstance().addCommand("/whois", "/whois [user]");
                if (split.length < 2) {
                    player.sendMessage(Colors.Rose + "Usage is /whois [player]");
            }
                    if (player.canIgnoreRestrictions()) {
                        ignore = "True";
                    } else {
                            ignore ="False";
                        }
                    if (player.canIgnoreRestrictions()) {
                        admin = "True";
                    } else {
                        admin = "False";
            }
                    if (player.isInGroup("superadmins")){
                        group = "superadmins";
            }else if(player.isInGroup("admins")){
                group = "admins";
                    }else if(player.isInGroup("mods")){
                        group = "mods";
                    }else if(player.isInGroup("trusted")){
                        group = "trusted";
                    }else{
                        group = "Default";
                        }
                    player.sendMessage(Colors.LightGreen + "Info for "+split[1]+": Admin("+admin+") Ignoresrestrictions("+ignore+") Group("+group+").");
                  } else {
            return false;
        }
        return true;
    }

    public void onKick(Player player, String reason)
    {
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