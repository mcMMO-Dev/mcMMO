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
    public static double getDistance(Player player1, Player player2)
    {
    return Math.sqrt(Math.pow(player1.getX() - player2.getX(), 2) + Math.pow(player1.getY() - player2.getY(), 2)
    + Math.pow(player1.getZ() - player2.getZ(), 2));
    }
}
