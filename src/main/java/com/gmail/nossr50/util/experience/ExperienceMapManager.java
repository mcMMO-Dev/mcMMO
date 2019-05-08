package com.gmail.nossr50.util.experience;

import com.gmail.nossr50.api.exceptions.InvalidSkillException;
import com.gmail.nossr50.config.Unload;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.mcMMO;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import java.util.HashMap;

/**
 * This class handles the XP for block break related XP
 */
public class ExperienceMapManager implements Unload {
    private HashMap<PrimarySkillType, HashMap<Material, String>> skillMaterialXPMap;
    private HashMap<String, Integer> miningFullyQualifiedBlockXpMap;
    private HashMap<String, Integer> herbalismFullyQualifiedBlockXpMap;
    private HashMap<String, Integer> woodcuttingFullyQualifiedBlockXpMap;
    private HashMap<String, Integer> excavationFullyQualifiedBlockXpMap;
    private HashMap<EntityType, Float> tamingExperienceMap;

    private double globalXpMult;

    public ExperienceMapManager() {
        miningFullyQualifiedBlockXpMap = new HashMap<>();
        herbalismFullyQualifiedBlockXpMap = new HashMap<>();
        woodcuttingFullyQualifiedBlockXpMap = new HashMap<>();
        excavationFullyQualifiedBlockXpMap = new HashMap<>();
        tamingExperienceMap = new HashMap<>();

        //Register with unloader
        mcMMO.getConfigManager().registerUnloadable(this);
    }

    /**
     * Builds fully qualified name to xp value maps of blocks for XP lookups
     * This method servers two purposes
     * 1) It adds user config values to a hash table
     * 2) It converts user config values into their fully qualified names
     * <p>
     * This is done to avoid namespace conflicts, which don't happen in Bukkit but could easily happen in Sponge
     */
    public void buildBlockXPMaps() {
        buildMiningBlockXPMap();
        buildHerbalismBlockXPMap();
        buildWoodcuttingBlockXPMap();
        buildExcavationBlockXPMap();
        buildTamingXPMap();
    }

    /**
     * Taming entries in the config are case insensitive, but for faster lookups we convert them to ENUMs
     */
    private void buildTamingXPMap() {
        mcMMO.p.getLogger().info("Building Taming XP list...");
        HashMap<String, Integer> userTamingConfigMap = mcMMO.getConfigManager().getConfigExperience().getTamingExperienceMap();

        for (String s : userTamingConfigMap.keySet()) {
            boolean matchFound = false;
            for (EntityType entityType : EntityType.values()) {
                if (entityType.toString().equalsIgnoreCase(s)) {
                    //Match!
                    matchFound = true;
                    tamingExperienceMap.put(entityType, (float) userTamingConfigMap.get(s));
                }
            }
            if (!matchFound) {
                mcMMO.p.getLogger().info("Unable to find entity with matching name - " + s);
            }
        }
    }

    private void fillBlockXPMap(HashMap<String, Integer> userConfigMap, HashMap<String, Integer> fullyQualifiedBlockXPMap) {
        for (String string : userConfigMap.keySet()) {
            //matchMaterial can match fully qualified names and names without domain
            Material matchingMaterial = Material.matchMaterial(string);

            if (matchingMaterial != null) {
                //Map the fully qualified name
                fullyQualifiedBlockXPMap.put(matchingMaterial.getKey().getKey(), userConfigMap.get(string));
            } else {
                mcMMO.p.getLogger().info("Could not find a match for the block named '" + string + "' among vanilla block registers");
            }
        }
    }

    private void buildMiningBlockXPMap() {
        mcMMO.p.getLogger().info("Mapping block break XP values for Mining...");
        fillBlockXPMap(mcMMO.getConfigManager().getConfigExperience().getMiningExperienceMap(), miningFullyQualifiedBlockXpMap);
    }


    private void buildHerbalismBlockXPMap() {
        mcMMO.p.getLogger().info("Mapping block break XP values for Herbalism...");
        fillBlockXPMap(mcMMO.getConfigManager().getConfigExperience().getHerbalismXPMap(), herbalismFullyQualifiedBlockXpMap);
    }

    private void buildWoodcuttingBlockXPMap() {
        mcMMO.p.getLogger().info("Mapping block break XP values for Woodcutting...");
        fillBlockXPMap(mcMMO.getConfigManager().getConfigExperience().getWoodcuttingExperienceMap(), woodcuttingFullyQualifiedBlockXpMap);
    }

    private void buildExcavationBlockXPMap() {
        mcMMO.p.getLogger().info("Mapping block break XP values for Excavation...");
        fillBlockXPMap(mcMMO.getConfigManager().getConfigExperience().getExcavationExperienceMap(), excavationFullyQualifiedBlockXpMap);
    }

    /**
     * Change the gloabl xp multiplier, this is temporary and will not be serialiized
     *
     * @param newGlobalXpMult new global xp multiplier value
     */
    public void setGlobalXpMult(double newGlobalXpMult) {
        mcMMO.p.getLogger().info("Setting the global XP multiplier -> " + newGlobalXpMult);
        globalXpMult = newGlobalXpMult;
    }

    public void resetGlobalXpMult() {
        mcMMO.p.getLogger().info("Resetting the global XP multiplier " + globalXpMult + " -> " + getOriginalGlobalXpMult());
        globalXpMult = getOriginalGlobalXpMult();
    }

    public void setMiningFullyQualifiedBlockXpMap(HashMap<String, Integer> miningFullyQualifiedBlockXpMap) {
        mcMMO.p.getLogger().info("Registering Mining XP Values...");
        this.miningFullyQualifiedBlockXpMap = miningFullyQualifiedBlockXpMap;
    }

    public void setHerbalismFullyQualifiedBlockXpMap(HashMap<String, Integer> herbalismFullyQualifiedBlockXpMap) {
        mcMMO.p.getLogger().info("Registering Herbalism XP Values...");
        this.herbalismFullyQualifiedBlockXpMap = herbalismFullyQualifiedBlockXpMap;
    }

    public void setWoodcuttingFullyQualifiedBlockXpMap(HashMap<String, Integer> woodcuttingFullyQualifiedBlockXpMap) {
        mcMMO.p.getLogger().info("Registering Woodcutting XP Values...");
        this.woodcuttingFullyQualifiedBlockXpMap = woodcuttingFullyQualifiedBlockXpMap;
    }

    public void setExcavationFullyQualifiedBlockXpMap(HashMap<String, Integer> excavationFullyQualifiedBlockXpMap) {
        mcMMO.p.getLogger().info("Registering Excavation XP Values...");
        this.excavationFullyQualifiedBlockXpMap = excavationFullyQualifiedBlockXpMap;
    }

    /**
     * Gets the current global xp multiplier value
     * This value can be changed by the xprate command
     *
     * @return
     */
    public double getGlobalXpMult() {
        return globalXpMult;
    }

    /**
     * Gets the block break XP value for a specific skill
     *
     * @param primarySkillType target skill
     * @param material         target material
     * @return XP value for breaking this block for said skill
     * @throws InvalidSkillException for skills that don't give block break experience
     * @deprecated its faster to use direct calls to get XP, for example getMiningXP(Material material) instead of using this method
     */
    @Deprecated
    public float getBlockBreakXpValue(PrimarySkillType primarySkillType, Material material) throws InvalidSkillException {
        switch (primarySkillType) {
            case MINING:
                return getMiningXp(material);
            case HERBALISM:
                return getHerbalismXp(material);
            case EXCAVATION:
                return getExcavationXp(material);
            case WOODCUTTING:
                return getWoodcuttingXp(material);
            default:
                throw new InvalidSkillException();
        }
    }

    /**
     * Gets the taming XP for this entity
     *
     * @param entityType target entity
     * @return value of XP for this entity
     */
    public float getTamingXp(EntityType entityType) {
        return tamingExperienceMap.get(entityType);
    }

    /**
     * Gets the original value of the global XP multiplier
     * This is defined by the users config
     * This value can be different from the current working value (due to xprate etc)
     *
     * @return the original global xp multiplier value from the user config file
     */
    public double getOriginalGlobalXpMult() {
        return mcMMO.getConfigManager().getConfigExperience().getGlobalXPMultiplier();
    }

    /**
     * Determines whether or not a block has Mining XP
     *
     * @param material target block material type
     * @return true if the block has valid xp registers
     */
    public boolean hasMiningXp(Material material) {
        return miningFullyQualifiedBlockXpMap.get(material.getKey()) != null;
    }

    /**
     * Determines whether or not a block has Herbalism XP
     *
     * @param material target block material type
     * @return true if the block has valid xp registers
     */
    public boolean hasHerbalismXp(Material material) {
        return herbalismFullyQualifiedBlockXpMap.get(material.getKey()) != null;
    }

    /**
     * Determines whether or not a block has Woodcutting XP
     *
     * @param material target block material type
     * @return true if the block has valid xp registers
     */
    public boolean hasWoodcuttingXp(Material material) {
        return woodcuttingFullyQualifiedBlockXpMap.get(material.getKey()) != null;
    }

    /**
     * Determines whether or not a block has Excavation XP
     *
     * @param material target block material type
     * @return true if the block has valid xp registers
     */
    public boolean hasExcavationXp(Material material) {
        return excavationFullyQualifiedBlockXpMap.get(material.getKey()) != null;
    }

    /**
     * Gets the XP value for breaking this block from the xp map
     *
     * @param material the target block material
     * @return the raw XP value before any modifiers are applied
     */
    public int getMiningXp(Material material) {
        return miningFullyQualifiedBlockXpMap.get(material.getKey());
    }

    /**
     * Gets the XP value for breaking this block from the xp map
     *
     * @param material the target block material
     * @return the raw XP value before any modifiers are applied
     */
    public int getHerbalismXp(Material material) {
        return herbalismFullyQualifiedBlockXpMap.get(material.getKey());
    }

    /**
     * Gets the XP value for breaking this block from the xp map
     *
     * @param material the target block material
     * @return the raw XP value before any modifiers are applied
     */
    public int getWoodcuttingXp(Material material) {
        return woodcuttingFullyQualifiedBlockXpMap.get(material.getKey());
    }

    /**
     * Gets the XP value for breaking this block from the xp map
     *
     * @param material the target block material
     * @return the raw XP value before any modifiers are applied
     */
    public int getExcavationXp(Material material) {
        return excavationFullyQualifiedBlockXpMap.get(material.getKey());
    }

    @Override
    public void unload() {
        miningFullyQualifiedBlockXpMap.clear();
        woodcuttingFullyQualifiedBlockXpMap.clear();
        herbalismFullyQualifiedBlockXpMap.clear();
        excavationFullyQualifiedBlockXpMap.clear();
    }
}
