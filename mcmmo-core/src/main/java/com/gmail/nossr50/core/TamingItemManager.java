package com.gmail.nossr50.core;

import com.gmail.nossr50.datatypes.skills.subskills.taming.CallOfTheWildType;
import com.gmail.nossr50.mcMMO;
import org.bukkit.Material;

import java.util.HashMap;

//TODO: 2.2 - Need better cross-version support
public class TamingItemManager {
    private HashMap<CallOfTheWildType, Material> cotwToSummonItemMap;
    private HashMap<Material, CallOfTheWildType> summonItemToCotwMap;
    private final mcMMO pluginRef;

    public TamingItemManager(mcMMO pluginRef) {
        this.pluginRef = pluginRef;
        initMaps();
    }

    private void initMaps() {
        cotwToSummonItemMap = new HashMap<>();
        summonItemToCotwMap = new HashMap<>();

        registerCatSummonItem();
        registerWolfSummonItem();
        registerHorseSummonItem();
    }

    private void registerCatSummonItem() {
        //TODO: Remove the ENUM, use string only
        registerSummonItemRelationships(CallOfTheWildType.CAT, Material.COD);
    }

    private void registerWolfSummonItem() {
        //TODO: Remove the ENUM, use string only
        registerSummonItemRelationships(CallOfTheWildType.WOLF, Material.BONE);
    }

    private void registerHorseSummonItem() {
        //TODO: Remove the ENUM, use string only
        registerSummonItemRelationships(CallOfTheWildType.HORSE, Material.APPLE);
    }

    public void registerSummonItemRelationships(CallOfTheWildType callOfTheWildType, Material defaultType) {
        //TODO: Unnecessarily complicated
        String materialString = pluginRef.getConfigManager().getConfigTaming().getSubSkills().getCallOfTheWild().getCOTWSummon(callOfTheWildType).getItemType().getKey().toString();
        pluginRef.getLogger().info("Registering COTW Summon Item - "+callOfTheWildType.toString()+" | "+materialString);
        Material material = Material.matchMaterial(materialString);

        if(material != null) {
            summonItemToCotwMap.put(material, callOfTheWildType);
            cotwToSummonItemMap.put(callOfTheWildType, material);
        } else {
            pluginRef.getLogger().severe("Item not found for COTW summon! Reverting to backup named: "+defaultType.toString());
            summonItemToCotwMap.put(defaultType, callOfTheWildType);
            cotwToSummonItemMap.put(callOfTheWildType, defaultType);
        }

    }

    public Material getEntitySummonItem(CallOfTheWildType callOfTheWildType) {
        return cotwToSummonItemMap.get(callOfTheWildType);
    }

    public CallOfTheWildType getCallType(Material material) {
        //TODO: Remove the ENUM, use string only
        //TODO: Remove the ENUM, use string only
        //TODO: Remove the ENUM, use string only
        //TODO: Remove the ENUM, use string only
        //TODO: Remove the ENUM, use string only
        //TODO: Remove the ENUM, use string only
        //TODO: Remove the ENUM, use string only
        //TODO: Remove the ENUM, use string only
        //TODO: Remove the ENUM, use string only
        //TODO: Remove the ENUM, use string only
        //TODO: Remove the ENUM, use string only
        //TODO: Remove the ENUM, use string only
        //TODO: Remove the ENUM, use string only

        return summonItemToCotwMap.get(material);
    }

    public boolean isCOTWItem(Material material) {
        return summonItemToCotwMap.get(material) != null;
    }
}
