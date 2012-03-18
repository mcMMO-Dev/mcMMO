package com.gmail.nossr50.config;

import java.util.HashMap;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.gmail.nossr50.mcMMO;

public class Misc {
    public HashMap<Entity, Integer> arrowTracker = new HashMap<Entity, Integer>();
    public HashMap<Integer, Player> tntTracker = new HashMap<Integer, Player>();
    mcMMO plugin;

    public Misc(mcMMO mcMMO) {
        this.plugin = mcMMO;
    }
}