
import java.util.logging.Level;

public class vminecraft extends Plugin {

    @Override
    public void disable() {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void enable() {
        //throw new UnsupportedOperationException("Not supported yet.");
    }
    public boolean onChat(Player player, String message){
        String playerb = player.getName();
        String temp2 = "<" + etc.getInstance().getUserColor(playerb) + player.getName()  +  Colors.White +"> ";
        String adminchat = Colors.LightGreen + "{" + etc.getInstance().getUserColor(playerb) + player.getName()  +  Colors.LightGreen +"}" + Colors.White + " ";
        String message2 = "";
        String check = temp2+message;
        if (message.startsWith("@") && (etc.getInstance().isUserInGroup(player, "mods") || etc.getInstance().isUserInGroup(player, "admins") || etc.getInstance().isUserInGroup(player, "superadmins"))) {
            for (Player p : etc.getServer().getPlayerList()) {
                if (p != null) {
                    if (etc.getInstance().isUserInGroup(p, "mods") || (etc.getInstance().isUserInGroup(p, "admins")) || (etc.getInstance().isUserInGroup(p, "superadmins"))) {
                        String blaa = "";
                        for ( int x = 1; x< message.length(); x++) {
                        blaa+=message.charAt(x);
                        }
                        p.sendMessage(adminchat+blaa);
                        id.a.log(Level.INFO, "@"+message);
                                                }
                                }
                    }
            return true;
      }
        if (message.startsWith(">")) {
            message = Colors.LightGreen + message;
            message2 = temp2 + message;
            other.gmsg(message2);
            id.a.log(Level.INFO, message2);
            return true;
        }
        if (message.startsWith("FFF")) {
            message = Colors.Red + message;
            message2 = temp2 + message;
            other.gmsg(message2);
            id.a.log(Level.INFO, message2);
            return true;
        }
        if(message.length()>2 && lengthCheck(check)) {
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
			message = temp2 + temp + " ";
                        for (Player p : etc.getServer().getPlayerList()) {
                                if (p != null) {
                                     other.gmsg(message);
                                     id.a.log(Level.INFO, message);
                                     return true;
                                }
                            }                                                
		}
        return false;
    } //end of onchat
    public boolean onCommand(Player player, String[] split) {
        if (!etc.getInstance().canUseCommand(player.getName(), split[0])) {
            return false;
        }
        if (split[0].equalsIgnoreCase("/fabulous")) {
            etc.getInstance().addCommand("/fabulous", "/fabulous <message>");
                    if (split.length == 1) {return false;}
                    String temp = "";
                    String str = "";
                    //str = paramString.substring(paramString.indexOf(" ")).trim();
                    str = id.combineSplit(1, split, " ");
                    String temp2 = "<" + player.getName()  + "> "+str;
                    String[] rainbow = new String[] {Colors.Red, Colors.Rose, Colors.Yellow, Colors.Green, Colors.Blue, Colors.LightPurple, Colors.Purple};
                    int counter=0;
                    if(lengthCheck(temp2))
                    {
                    for(int x=0; x<str.length(); x++)
                    {
                            temp+=rainbow[counter]+str.charAt(x);
                            counter++;
                            if(str.charAt(x)==' ') { counter--;}
                            if(counter==-1){counter = 6; }
                            if(counter==7){counter = 0; }
                    }
                    str = temp+" ";
                    String message = "<" + etc.getInstance().getUserColor(player.getName()) + player.getName() + Colors.White + "> " + str;
                            id.a.log(Level.INFO, "[F]"+str);
                            other.gmsg(message);
                    } else {
                            player.sendMessage(Colors.Rose + "Message is too long");
                    }
                }
        else if (split[0].equalsIgnoreCase("/promote")) {
                User user2 = etc.getInstance().getUser(split[1]);
                if (split.length < 2) {
                    player.sendMessage(Colors.Rose + "Usage is /promote [player]");
                }
                if(user2 == null) {
                    player.sendMessage(Colors.Rose + "Player does not exist.");
                    return false;
                }
                //ea player = match(split[1]);
                User user = etc.getInstance().getUser(split[1]);
                boolean newUser = false;
                if (user == null) {
                    player.sendMessage(Colors.Rose + "Adding new user.");
                    newUser = true;
                    user = new User();
                    user.Name = split[1];
                    user.Administrator = false;
                    user.CanModifyWorld = true;
                    user.IgnoreRestrictions = false;
                    user.Commands = new String[]{""};
                    user.Prefix = "";
                    return false;
                }
                if (etc.getInstance().isUserInGroup(split[1], "admins") && (etc.getInstance().isUserInGroup(player, "admins") || etc.getInstance().isUserInGroup(player, "superadmins"))) {
                    player.sendMessage(Colors.Rose + "You cannot promote " + split[1] + " any higher.");
                } else if (etc.getInstance().isUserInGroup(split[1], "mods") && etc.getInstance().isUserInGroup(player, "superadmins")) {
                    user.Groups = ranks.Admins;
                    etc.getInstance().getDataSource().modifyUser(user);
                    String message = Colors.Yellow + split[1] + " was promoted to" + Colors.Rose + " Admin";
                    other.gmsg(message);
                } else if (etc.getInstance().isUserInGroup(split[1], "trusted") && etc.getInstance().isUserInGroup(player, "admins")) {
                    user.Groups = ranks.Mods;
                    etc.getInstance().getDataSource().modifyUser(user);
                    String message = Colors.Yellow + split[1] + " was promoted to" + Colors.DarkPurple + " Mods";
                    other.gmsg(message);
                  } else if (etc.getInstance().isUserInGroup(split[1], "default") && etc.getInstance().isUserInGroup(player, "mods")) {
                    user.Groups = ranks.Trusted;
                    etc.getInstance().getDataSource().modifyUser(user);
                    String message = Colors.Yellow + split[1] + " was promoted to" + Colors.LightGreen + " Trusted";
                    other.gmsg(message);
                } else player.sendMessage(Colors.Rose + "That didn't work");
                  if (newUser) {
                    etc.getInstance().getDataSource().addUser(user);
                } else {
                    etc.getInstance().getDataSource().modifyUser(user);
                }

            }
                else if (split[0].equalsIgnoreCase("/demote")) {
                    etc.getInstance().addCommand("/demote", "/demote [user]");
                if (split.length < 2) {
                    player.sendMessage(Colors.Rose + "Usage is /demote [player]");
                }
                if(player == null) {
                    player.sendMessage(Colors.Rose + "Player does not exist.");
                    return false;
                }
                User user = etc.getInstance().getUser(split[1]);
                boolean newUser = false;
                if (user == null) {
                    player.sendMessage(Colors.Rose + "Adding new user.");
                    newUser = true;
                    user = new User();
                    user.Name = split[1];
                    user.Administrator = false;
                    user.CanModifyWorld = true;
                    user.IgnoreRestrictions = false;
                    user.Commands = new String[]{""};
                    user.Prefix = "";
                }
                if (etc.getInstance().isUserInGroup(split[1], "admins")&& etc.getInstance().isUserInGroup(player, "superadmins")) {
                    user.Groups = ranks.Mods;
                    etc.getInstance().getDataSource().modifyUser(user);
                    String message = Colors.Yellow + split[1] + " was demoted to" + Colors.DarkPurple + " Mod";
                    other.gmsg(message);
                } else if (etc.getInstance().isUserInGroup(split[1], "mods")&& etc.getInstance().isUserInGroup(player, "admins")) {
                    user.Groups = ranks.Trusted;
                    etc.getInstance().getDataSource().modifyUser(user);
                    String message = Colors.Yellow + split[1] + " was demoted to" + Colors.LightGreen + " Trusted";
                    other.gmsg(message);
                  } else if (etc.getInstance().isUserInGroup(split[1], "trusted")&& etc.getInstance().isUserInGroup(player, "mods")) {
                    user.Groups = ranks.Def;
                    etc.getInstance().getDataSource().modifyUser(user);
                    String message = Colors.Yellow + split[1] + " was demoted to" + Colors.White + " Default";
                    other.gmsg(message);
                } else if (etc.getInstance().isUserInGroup(split[1], "default")) {
                    player.sendMessage(Colors.Rose + "You cannot demote " + split[1] + " any lower.");
                } else player.sendMessage(Colors.Rose + "That didn't work");
                  if (newUser) {
                    etc.getInstance().getDataSource().addUser(user);
                } else {
                    etc.getInstance().getDataSource().modifyUser(user);
                }

            }
        else {
            return false;
        }
        return true;
    }
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