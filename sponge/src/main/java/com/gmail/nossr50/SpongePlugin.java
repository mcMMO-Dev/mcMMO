package com.gmail.nossr50;

import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.plugin.Plugin;

@Plugin(
    id = "mcmmo",
    authors = {"nossr50", "gabizou", "bloodmage"},
    version = "2.2.0-SNAPSHOT",
    name = "mcMMO",
    url = "https://www.mcmmo.org/",
    description = "mcMMO plugin for Sponge"
)
public class SpongePlugin {


    @Listener
    public void onGamePreInit(GamePreInitializationEvent event) {

    }

}
