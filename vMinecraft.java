import java.util.logging.Logger;

//=====================================================================
//Class:	vMinecraftPlugin
//Use:		Starts the plugin
//Author:	nossr50, TrapAlice, cerevisiae
//=====================================================================
public class vMinecraft extends Plugin {
    static final vMinecraftListener listener = new vMinecraftListener();
    protected static final Logger log = Logger.getLogger("Minecraft");
    
	public void enable() {
		vMinecraftSettings.getInstance().loadSettings();
        vMinecraftUsers.getInstance().loadUsers();
		vMinecraftCommands.loadCommands();
                if (etc.getServer().getTime() == 0){
                    vMinecraftChat.gmsg(Colors.Rose + "The sun has risen, it is now safe to punch trees");
                }
                if (etc.getServer().getTime() == 13000){
                    vMinecraftChat.gmsg(Colors.Rose + "What a terrible night to have a curse");
                }

    }

    public void disable() {
        //And remove the commands here.
    }

    public void initialize() {
        //Here we add the hook we're going to use. In this case it's the arm swing event.
        etc.getLoader().addListener(PluginLoader.Hook.CHAT, listener, this, PluginListener.Priority.MEDIUM);
        etc.getLoader().addListener(PluginLoader.Hook.LOGIN, listener, this, PluginListener.Priority.MEDIUM);
        etc.getLoader().addListener(PluginLoader.Hook.COMMAND, listener, this, PluginListener.Priority.HIGH);
        etc.getLoader().addListener(PluginLoader.Hook.IGNITE, listener, this, PluginListener.Priority.HIGH);
        etc.getLoader().addListener(PluginLoader.Hook.DAMAGE, listener, this, PluginListener.Priority.MEDIUM);
        etc.getLoader().addListener(PluginLoader.Hook.EXPLODE, listener, this, PluginListener.Priority.HIGH);
        etc.getLoader().addListener(PluginLoader.Hook.LIQUID_DESTROY, listener, this, PluginListener.Priority.MEDIUM);
        etc.getLoader().addListener(PluginLoader.Hook.HEALTH_CHANGE, listener, this, PluginListener.Priority.MEDIUM);
        etc.getLoader().addListener(PluginLoader.Hook.PLAYER_MOVE, listener, this, PluginListener.Priority.MEDIUM);
        }
    }

