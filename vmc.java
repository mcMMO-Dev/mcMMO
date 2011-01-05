public class vmc {
    
    //Check if two players are in the same party
    public static boolean inSameParty(Player playera, Player playerb){
        if(vUsers.getProfile(playera).getParty().equals(vUsers.getProfile(playerb).getParty())){
            return true;
        } else {
            return false;
        }
    }
    //Get the distance between two players
    public static double getDistance(Player player1, Player player2)
    {
    return Math.sqrt(Math.pow(player1.getX() - player2.getX(), 2) + Math.pow(player1.getY() - player2.getY(), 2)
    + Math.pow(player1.getZ() - player2.getZ(), 2));
    }
    //Send the "invisibility" toggle to players near the hidden player
    public static void sendInvisible(Player player){
        for (Player p : etc.getServer().getPlayerList())
                {
                    if (vmc.getDistance(player, p) <= vConfig.range && p.getUser() != player.getUser())
                    {
                    p.getUser().a.b(new dv(player.getUser().g));
                    }
                }
    }
    //Send "visibility" toggle to invisible players turning them back to normal
    public static void sendNotInvisible(Player player){
        for (Player p : etc.getServer().getPlayerList())
                {
                    if (vmc.getDistance(player, p) < vConfig.range && p.getUser() != player.getUser())
                    {
                    p.getUser().a.b(new d(player.getUser()));
                    }
                }
    }    
}
