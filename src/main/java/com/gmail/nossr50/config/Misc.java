package com.gmail.nossr50.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.gmail.nossr50.mcMMO;

public class Misc {
    public HashMap<Entity, Integer> arrowTracker = new HashMap<Entity, Integer>();
    public ArrayList<LivingEntity> bleedTracker = new ArrayList<LivingEntity>();
    public HashMap<Integer, Player> tntTracker = new HashMap<Integer, Player>();
    mcMMO plugin;

    /* BLEED QUE STUFF */
    public HashSet<LivingEntity> bleedQue = new HashSet<LivingEntity>();
    public HashSet<LivingEntity> bleedRemovalQue = new HashSet<LivingEntity>();

    public Misc(mcMMO mcMMO) {
        this.plugin = mcMMO;
    }
}