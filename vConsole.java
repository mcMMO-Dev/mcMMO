public class vConsole extends PluginListener {
public boolean onConsoleCommand(String[] split) {
    String args = etc.combineSplit(1, split, ""); 
    //Return true if you don't want the server command to be parsed by the server.
    String server = Colors.LightGreen + "[Server] " + Colors.DarkPurple;
    if(split[0].equalsIgnoreCase("stop")){
        vChat.gmsg(server + "Rebooting the server");
        return false;
    }
    //Make say nicer
    if(split[0].equalsIgnoreCase("say"))
    {
        //Send out the message
        vChat.gmsg(server + args);
        return true;
    }
        return false;
    }
}