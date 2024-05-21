//package com.gmail.nossr50.protocollib;
//
//import com.gmail.nossr50.mcMMO;
//import org.bukkit.Bukkit;
//import org.bukkit.entity.Player;
//import org.bukkit.plugin.Plugin;
//
//public class ProtocolLibManager {
//    final Plugin protocolLibPluginRef;
//    final mcMMO pluginRef;
//    final ProtocolHandler protocolHandler;
//
//    public ProtocolLibManager(mcMMO pluginRef) {
//        this.pluginRef = pluginRef;
//
//        if (isProtocolLibPresent()) {
//            protocolLibPluginRef = pluginRef.getServer().getPluginManager().getPlugin("ProtocolLib");
//            protocolHandler = new ProtocolHandler(pluginRef);
//            pluginRef.getLogger().info("ProtocolLib found, enabling ProtocolLib features.");
//        } else {
//            protocolLibPluginRef = null;
//            protocolHandler = null;
//            pluginRef.getLogger().info("No ProtocolLib found, some completely optional cosmetic features will not be enabled.");
//        }
//    }
//
//    public boolean usingProtocolLib() {
//        return protocolLibPluginRef != null && protocolHandler != null;
//    }
//
//    private boolean isProtocolLibPresent() {
//        return Bukkit.getServer().getPluginManager().getPlugin("ProtocolLib") != null;
//    }
//
//    public void sendPacketToPlayer(Player player) {
//        if (usingProtocolLib()) {
//            protocolHandler.sendPacketToPlayer(player);
//        }
//    }
//}
