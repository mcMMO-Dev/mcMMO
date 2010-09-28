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
    }