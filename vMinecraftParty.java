import java.util.ArrayList;
public class vMinecraftParty {
    
    //Check if two players are in the same party
    public static boolean inSameParty(Player playera, Player playerb){
        if(vMinecraftUsers.getProfile(playera).getParty().equals(vMinecraftUsers.getProfile(playerb).getParty())){
            return true;
        } else {
            return false;
        }
    }
}
