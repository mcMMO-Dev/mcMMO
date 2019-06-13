package com.gmail.nossr50.util.experience;

import com.gmail.nossr50.api.exceptions.UndefinedSkillBehaviour;
import com.gmail.nossr50.datatypes.experience.SpecialXPKey;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.mcMMO;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import java.util.HashMap;

/**
 * This class handles the XP maps for various skills
 */
public class ExperienceManager {
    private HashMap<PrimarySkillType, HashMap<Material, String>> skillMaterialXPMap;
    private HashMap<String, Integer> miningFullyQualifiedBlockXpMap;
    private HashMap<String, Integer> herbalismFullyQualifiedBlockXpMap;
    private HashMap<String, Integer> woodcuttingFullyQualifiedBlockXpMap;
    private HashMap<String, Integer> excavationFullyQualifiedBlockXpMap;
    private HashMap<String, Integer> furnaceFullyQualifiedItemXpMap;
    private HashMap<EntityType, Double> tamingExperienceMap;
    private HashMap<EntityType, Double> combatXPMultiplierMap;
    private HashMap<SpecialXPKey, Double> specialCombatXPMultiplierMap; //Applies to "groups" of things for convenience

    private double globalXpMult;

    public ExperienceManager() {
        initExperienceMaps();
        registerDefaultValues();

        //Register with unloader
    }

    private void initExperienceMaps() {
        miningFullyQualifiedBlockXpMap = new HashMap<>();
        herbalismFullyQualifiedBlockXpMap = new HashMap<>();
        woodcuttingFullyQualifiedBlockXpMap = new HashMap<>();
        excavationFullyQualifiedBlockXpMap = new HashMap<>();
        furnaceFullyQualifiedItemXpMap = new HashMap<>();
        combatXPMultiplierMap = new HashMap<>();
        specialCombatXPMultiplierMap = new HashMap<>();
        tamingExperienceMap = new HashMap<>();
    }

    private void registerDefaultValues() {
        fillCombatXPMultiplierMap(mcMMO.getConfigManager().getConfigExperience().getCombatExperienceMap());
        registerSpecialCombatXPMultiplierMap(mcMMO.getConfigManager().getConfigExperience().getSpecialCombatExperienceMap());
        buildBlockXPMaps();
        buildFurnaceXPMap();
    }

    /**
     * Fills the combat XP multiplier map with values from a platform generic map
     * Platform safe map, is just a map which uses strings to define target entities/etc
     * Platform safe maps are converted to ENUMs for the platform for convenience
     *
     * @param platformSafeMap the platform safe map
     */
    public void fillCombatXPMultiplierMap(HashMap<String, Double> platformSafeMap) {
        mcMMO.p.getLogger().info("Registering combat XP values...");
        for (String entityString : platformSafeMap.keySet()) {
            //Iterate over all EntityType(s)
            for (EntityType type : EntityType.values()) {
                //Match ignoring case
                if (entityString.equalsIgnoreCase(entityString)) {
                    //Check for duplicates and warn the admin
                    if (combatXPMultiplierMap.containsKey(entityString)) {
                        mcMMO.p.getLogger().severe("Entity named " + entityString + " has multiple values in the combat experience config!");
                    }
                    //Match found
                    combatXPMultiplierMap.put(type, platformSafeMap.get(entityString));
                } else {
                    //Log an error so the admin can deal with figuring it out
                    mcMMO.p.getLogger().severe("No entity could be matched for the combat experience config value named - " + entityString);
                }
            }
        }
    }

    /**
     * Registers the map values for special combat XP to the specified map
     *
     * @param map target map
     */
    public void registerSpecialCombatXPMultiplierMap(HashMap<SpecialXPKey, Double> map) {
        mcMMO.p.getLogger().info("Registering special combat XP values...");
        specialCombatXPMultiplierMap = map;
    }

    private void buildFurnaceXPMap() {
        mcMMO.p.getLogger().info("Mapping xp values for furnaces...");
        fillBlockXPMap(mcMMO.getConfigManager().getConfigExperience().getSmeltingExperienceMap(), furnaceFullyQualifiedItemXpMap);
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
                    tamingExperienceMap.put(entityType, (double) userTamingConfigMap.get(s));
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
                fullyQualifiedBlockXPMap.put(matchingMaterial.getKey(), userConfigMap.get(string));
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

    /**
     * Reset the Global XP multiplier to its original value
     */
    public void resetGlobalXpMult() {
        mcMMO.p.getLogger().info("Resetting the global XP multiplier " + globalXpMult + " -> " + getOriginalGlobalXpMult());
        globalXpMult = getOriginalGlobalXpMult();
    }

    /**
     * Set the mining block XP map to the provided one
     *
     * @param miningFullyQualifiedBlockXpMap the XP map to change to
     */
    public void setMiningFullyQualifiedBlockXpMap(HashMap<String, Integer> miningFullyQualifiedBlockXpMap) {
        mcMMO.p.getLogger().info("Changing Mining XP Values...");
        this.miningFullyQualifiedBlockXpMap = miningFullyQualifiedBlockXpMap;
    }

    /**
     * Set the mining block XP map to the provided one
     *
     * @param herbalismFullyQualifiedBlockXpMap the XP map to change to
     */
    public void setHerbalismFullyQualifiedBlockXpMap(HashMap<String, Integer> herbalismFullyQualifiedBlockXpMap) {
        mcMMO.p.getLogger().info("Changing Herbalism XP Values...");
        this.herbalismFullyQualifiedBlockXpMap = herbalismFullyQualifiedBlockXpMap;
    }

    /**
     * Set the mining block XP map to the provided one
     *
     * @param woodcuttingFullyQualifiedBlockXpMap the XP map to change to
     */
    public void setWoodcuttingFullyQualifiedBlockXpMap(HashMap<String, Integer> woodcuttingFullyQualifiedBlockXpMap) {
        mcMMO.p.getLogger().info("Changin Woodcutting XP Values...");
        this.woodcuttingFullyQualifiedBlockXpMap = woodcuttingFullyQualifiedBlockXpMap;
    }

    /**
     * Set the mining block XP map to the provided one
     *
     * @param excavationFullyQualifiedBlockXpMap the XP map to change to
     */
    public void setExcavationFullyQualifiedBlockXpMap(HashMap<String, Integer> excavationFullyQualifiedBlockXpMap) {
        mcMMO.p.getLogger().info("Changing Excavation XP Values...");
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
     * @throws UndefinedSkillBehaviour for skills that don't give block break experience
     * @deprecated its faster to use direct calls to get XP, for example getMiningXP(Material material) instead of using this method
     */
    @Deprecated
    public double getBlockBreakXpValue(PrimarySkillType primarySkillType, Material material) throws UndefinedSkillBehaviour {
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
                throw new UndefinedSkillBehaviour(primarySkillType);
        }
    }

    /**
     * Gets the taming XP for this entity
     *
     * @param entityType target entity
     * @return value of XP for this entity
     */
    public double getTamingXp(EntityType entityType) {
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

    /**
     * Gets the XP value for converting an item in the furnace
     *
     * @param material the target item material value
     * @return the raw XP value before any modifiers are applied
     */
    public int getFurnaceItemXP(Material material) {
        return furnaceFullyQualifiedItemXpMap.get(material.getKey());
    }

    /**
     * Get the XP multiplier value for a special XP group
     *
     * @param specialXPKey target special XP group
     * @return XP multiplier for target special XP group
     */
    public double getSpecialCombatXP(SpecialXPKey specialXPKey) {
        return specialCombatXPMultiplierMap.get(specialXPKey);
    }

    /**
     * Gets the combat XP multiplier for this entity type
     *
     * @param entityType target entity type
     * @return the combat XP multiplier for this entity
     */
    public double getCombatXPMultiplier(EntityType entityType) {
        return combatXPMultiplierMap.get(entityType);
    }

    /**
     * Returns true/false if a EntityType has a defined XP multiplier (from the config typically)
     *
     * @param entityType target entity type
     * @return true if entity type has XP
     */
    public boolean hasCombatXP(EntityType entityType) {
        return combatXPMultiplierMap.get(entityType) != null;
    }
}
