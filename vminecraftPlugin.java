import java.util.logging.Logger;

//=====================================================================
//Class:	vMinecraftPlugin
//Use:		Starts the plugin
//Author:	nossr50, TrapAlice, cerevisiae
//=====================================================================
public class vminecraftPlugin extends Plugin {
    static final vminecraftListener listener = new vminecraftListener();
    protected static final Logger log = Logger.getLogger("Minecraft");
    
	public void enable() {
		vminecraftSettings.getInstance().loadSettings();
		vminecraftCommands.loadCommands();
    }

    public void disable() {
        //And remove the commands here.
    }

    public void initialize() {
        //Here we add the hook we're going to use. In this case it's the arm swing event.
        etc.getLoader().addListener(PluginLoader.Hook.CHAT, listener, this, PluginListener.Priority.MEDIUM);
        etc.getLoader().addListener(PluginLoader.Hook.COMMAND, listener, this, PluginListener.Priority.HIGH);
        if(etc.getInstance().isHealthEnabled()){
        	etc.getLoader().addListener(PluginLoader.Hook.HEALTH_CHANGE, listener, this, PluginListener.Priority.MEDIUM);
        }
    }
}
