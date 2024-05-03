package com.gmail.nossr50.protocollib;

import com.gmail.nossr50.mcMMO;
import org.bukkit.plugin.Plugin;

// TODO: Finish this class
public class ProtocolLibManager {
    Plugin protocolLibPluginRef;
    mcMMO pluginRef;
    public ProtocolLibManager(mcMMO pluginRef) {
        this.pluginRef = pluginRef;
    }

    public boolean isProtocolLibPresent() {
        protocolLibPluginRef = pluginRef.getServer().getPluginManager().getPlugin("ProtocolLib");
        return protocolLibPluginRef != null;
    }
}
