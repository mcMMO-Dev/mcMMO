//Contains all the java methods for vminecraft
public class other {
    public static other gmsg;
    public static other gmsg(String msg){
        for (Player p : etc.getServer().getPlayerList()) {
            if (p != null) {
                p.sendMessage(msg);
            }
        }
        return gmsg;
    }

    public static boolean lengthCheck(String str)
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
     
     public static String colorChange(char colour)
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