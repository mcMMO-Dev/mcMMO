package com.gmail.nossr50.sponge.api7;

import net.minecraft.item.Item;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.GameRegistryEvent;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.plugin.Plugin;

@Plugin(id = "mcmmo", name = "mcMMO", description = "mcMMO for Sponge")
public class API7Plugin {

    @SuppressWarnings("ConstantConditions")
    @Listener
    public void onRegister(GameRegistryEvent.Register<ItemType> event) {
        Item derp = new Item(); // NMS!!!
        event.register((ItemType) derp); // Since sponge mixes into Item, we can cast.
    }

}
