//package com.gmail.nossr50.protocollib;
//
//import com.comphenix.protocol.PacketType;
//import com.comphenix.protocol.ProtocolLibrary;
//import com.comphenix.protocol.ProtocolManager;
//import com.comphenix.protocol.events.ListenerPriority;
//import com.comphenix.protocol.events.PacketAdapter;
//import com.comphenix.protocol.events.PacketContainer;
//import com.comphenix.protocol.events.PacketEvent;
//import com.gmail.nossr50.mcMMO;
//import org.bukkit.entity.Player;
//
//public class ProtocolHandler {
//    private final mcMMO pluginRef;
//    private final ProtocolManager protocolManager;
//
//    public ProtocolHandler(mcMMO pluginRef) {
//        this.pluginRef = pluginRef;
//        this.protocolManager = ProtocolLibrary.getProtocolManager();
//        // TODO: encapsulate this in a method
//        protocolManager.addPacketListener(new PacketAdapter(pluginRef, ListenerPriority.NORMAL, PacketType.Play.Server.WINDOW_DATA) {
//            @Override
//            public void onPacketSending(PacketEvent event) {
//                Player player = event.getPlayer();
//                PacketContainer packet = event.getPacket();
//                if (packet.getIntegers().read(0) == 400) {
//                    event.setCancelled(true);
//                }
//            }
//        });
//    }
//    public void sendPacketToPlayer(Player player) {
//        final PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.WINDOW_DATA);
//        protocolManager.sendServerPacket(player, packet);
//    }
//}
