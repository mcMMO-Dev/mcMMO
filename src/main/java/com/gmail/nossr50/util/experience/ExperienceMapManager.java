package com.gmail.nossr50.util.experience;

import com.gmail.nossr50.config.Unload;
import com.gmail.nossr50.mcMMO;
import org.bukkit.Material;

import java.util.HashMap;

/**
 * This class handles the XP for block break related XP
 */
public class ExperienceMapManager implements Unload {
    public HashMap<Material, Integer> miningXpMap;
    public HashMap<Material, Integer> herbalismXpMap;
    public HashMap<Material, Integer> woodcuttingXpMap;
    public HashMap<Material, Integer> excavationXpMap;

    public ExperienceMapManager()
    {
        miningXpMap = new HashMap<>();
        herbalismXpMap = new HashMap<>();
        woodcuttingXpMap = new HashMap<>();
        excavationXpMap = new HashMap<>();

        //Register with unloader
        mcMMO.getConfigManager().registerUnloadable(this);
    }

    /**
     * Determines whether or not a block has Mining XP
     * @param material target block material type
     * @return true if the block has valid xp registers
     */
    public boolean hasMiningXp(Material material)
    {
        return miningXpMap.get(material) != null;
    }

    /**
     * Determines whether or not a block has Herbalism XP
     * @param material target block material type
     * @return true if the block has valid xp registers
     */
    public boolean hasHerbalismXp(Material material)
    {
        return herbalismXpMap.get(material) != null;
    }

    /**
     * Determines whether or not a block has Woodcutting XP
     * @param material target block material type
     * @return true if the block has valid xp registers
     */
    public boolean hasWoodcuttingXp(Material material)
    {
        return woodcuttingXpMap.get(material) != null;
    }

    /**
     * Determines whether or not a block has Excavation XP
     * @param material target block material type
     * @return true if the block has valid xp registers
     */
    public boolean hasExcavationXp(Material material)
    {
        return excavationXpMap.get(material) != null;
    }

    /**
     * Gets the XP value for breaking this block from the xp map
     * @param material the target block material
     * @return the raw XP value before any modifiers are applied
     */
    public int getMiningXp(Material material)
    {
        return miningXpMap.get(material);
    }

    /**
     * Gets the XP value for breaking this block from the xp map
     * @param material the target block material
     * @return the raw XP value before any modifiers are applied
     */
    public int getHerbalismXp(Material material)
    {
        return herbalismXpMap.get(material);
    }

    /**
     * Gets the XP value for breaking this block from the xp map
     * @param material the target block material
     * @return the raw XP value before any modifiers are applied
     */
    public int getWoodcuttingXp(Material material)
    {
        return woodcuttingXpMap.get(material);
    }

    /**
     * Gets the XP value for breaking this block from the xp map
     * @param material the target block material
     * @return the raw XP value before any modifiers are applied
     */
    public int getExcavationXp(Material material)
    {
        return excavationXpMap.get(material);
    }

    @Override
    public void unload() {
        miningXpMap.clear();
        woodcuttingXpMap.clear();
        herbalismXpMap.clear();
        excavationXpMap.clear();
    }
}
