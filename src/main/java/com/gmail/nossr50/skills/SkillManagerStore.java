package com.gmail.nossr50.skills;

import java.util.HashMap;

import com.gmail.nossr50.skills.acrobatics.AcrobaticsManager;
import com.gmail.nossr50.skills.archery.ArcheryManager;
import com.gmail.nossr50.skills.axes.AxeManager;
import com.gmail.nossr50.skills.excavation.ExcavationManager;
import com.gmail.nossr50.skills.fishing.FishingManager;
import com.gmail.nossr50.skills.herbalism.HerbalismManager;
import com.gmail.nossr50.skills.mining.MiningManager;
import com.gmail.nossr50.skills.smelting.SmeltingManager;
import com.gmail.nossr50.skills.swords.SwordsManager;
import com.gmail.nossr50.skills.taming.TamingManager;
import com.gmail.nossr50.skills.unarmed.UnarmedManager;
import com.gmail.nossr50.util.player.UserManager;

public class SkillManagerStore {
    private static SkillManagerStore instance;

    private HashMap<String, AcrobaticsManager> acrobaticsManagers = new HashMap<String, AcrobaticsManager>();
    private HashMap<String, ArcheryManager> archeryManagers = new HashMap<String, ArcheryManager>();
    private HashMap<String, AxeManager> axeManagers = new HashMap<String, AxeManager>();
    private HashMap<String, ExcavationManager> excavationManagers = new HashMap<String, ExcavationManager>();
    private HashMap<String, FishingManager> fishingManagers = new HashMap<String, FishingManager>();
    private HashMap<String, HerbalismManager> herbalismManagers = new HashMap<String, HerbalismManager>();
    private HashMap<String, MiningManager> miningManagers = new HashMap<String, MiningManager>();
    private HashMap<String, SmeltingManager> smeltingManagers = new HashMap<String, SmeltingManager>();
    private HashMap<String, SwordsManager> swordsManagers = new HashMap<String, SwordsManager>();
    private HashMap<String, TamingManager> tamingManagers = new HashMap<String, TamingManager>();
    private HashMap<String, UnarmedManager> unarmedManagers = new HashMap<String, UnarmedManager>();

    public static SkillManagerStore getInstance() {
        if (instance == null) {
            instance = new SkillManagerStore();
        }

        return instance;
    }

    public AcrobaticsManager getAcrobaticsManager(String playerName) {
        if (!acrobaticsManagers.containsKey(playerName)) {
            acrobaticsManagers.put(playerName, new AcrobaticsManager(UserManager.getPlayer(playerName)));
        }

        return acrobaticsManagers.get(playerName);
    }

    public ArcheryManager getArcheryManager(String playerName) {
        if (!archeryManagers.containsKey(playerName)) {
            archeryManagers.put(playerName, new ArcheryManager(UserManager.getPlayer(playerName)));
        }

        return archeryManagers.get(playerName);
    }

    public AxeManager getAxeManager(String playerName) {
        if (!axeManagers.containsKey(playerName)) {
            axeManagers.put(playerName, new AxeManager(UserManager.getPlayer(playerName)));
        }

        return axeManagers.get(playerName);
    }

    public ExcavationManager getExcavationManager(String playerName) {
        if (!excavationManagers.containsKey(playerName)) {
            excavationManagers.put(playerName, new ExcavationManager(UserManager.getPlayer(playerName)));
        }

        return excavationManagers.get(playerName);
    }

    public FishingManager getFishingManager(String playerName) {
        if (!fishingManagers.containsKey(playerName)) {
            fishingManagers.put(playerName, new FishingManager(UserManager.getPlayer(playerName)));
        }

        return fishingManagers.get(playerName);
    }

    public HerbalismManager getHerbalismManager(String playerName) {
        if (!herbalismManagers.containsKey(playerName)) {
            herbalismManagers.put(playerName, new HerbalismManager(UserManager.getPlayer(playerName)));
        }

        return herbalismManagers.get(playerName);
    }

    public MiningManager getMiningManager(String playerName) {
        if (!miningManagers.containsKey(playerName)) {
            miningManagers.put(playerName, new MiningManager(UserManager.getPlayer(playerName)));
        }

        return miningManagers.get(playerName);
    }

    public SmeltingManager getSmeltingManager(String playerName) {
        if (!smeltingManagers.containsKey(playerName)) {
            smeltingManagers.put(playerName, new SmeltingManager(UserManager.getPlayer(playerName)));
        }

        return smeltingManagers.get(playerName);
    }

    public SwordsManager getSwordsManager(String playerName) {
        if (!swordsManagers.containsKey(playerName)) {
            swordsManagers.put(playerName, new SwordsManager(UserManager.getPlayer(playerName)));
        }

        return swordsManagers.get(playerName);
    }

    public TamingManager getTamingManager(String playerName) {
        if (!tamingManagers.containsKey(playerName)) {
            tamingManagers.put(playerName, new TamingManager(UserManager.getPlayer(playerName)));
        }

        return tamingManagers.get(playerName);
    }

    public UnarmedManager getUnarmedManager(String playerName) {
        if (!unarmedManagers.containsKey(playerName)) {
            unarmedManagers.put(playerName, new UnarmedManager(UserManager.getPlayer(playerName)));
        }

        return unarmedManagers.get(playerName);
    }
}
