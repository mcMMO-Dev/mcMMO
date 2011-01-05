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
    public static void sendInvisible(Player player){
        for (Player p : etc.getServer().getPlayerList())
                {
                    if (vMinecraftParty.getDistance(player, p) <= vMinecraftSettings.range && p.getUser() != player.getUser())
                    {
                    p.getUser().a.b(new dv(player.getUser().g));
                    }
                }
    }
    public static void sendNotInvisible(Player player){
        for (Player p : etc.getServer().getPlayerList())
                {
                    if (vMinecraftParty.getDistance(player, p) < vMinecraftSettings.range && p.getUser() != player.getUser())
                    {
                    p.getUser().a.b(new d(player.getUser()));
                    }
                }
    }
}
