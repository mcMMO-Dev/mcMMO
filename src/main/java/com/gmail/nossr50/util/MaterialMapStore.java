package com.gmail.nossr50.util;

import org.bukkit.Material;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;

/**
 * Stores hash tables for item and block names
 * This allows for better support across multiple versions of Minecraft
 *
 * This is a temporary class, mcMMO is spaghetti and I'l clean it up later
 *
 */
public class MaterialMapStore {

    private final HashSet<String> abilityBlackList;
    private final HashSet<String> toolBlackList;
    private final HashSet<String> mossyWhiteList;
    private final HashSet<String> leavesWhiteList;
    private final HashSet<String> herbalismAbilityBlackList;
    private final HashSet<String> blockCrackerWhiteList;
    private final HashSet<String> canMakeShroomyWhiteList;
    private final HashSet<String> multiBlockPlant;
    private final HashSet<String> foodItemWhiteList;
    private final HashSet<String> glassBlocks;

    private final HashSet<String> netheriteArmor;
    private final HashSet<String> netheriteTools;
    private final HashSet<String> woodTools;
    private final HashSet<String> stoneTools;
    private final HashSet<String> leatherArmor;
    private final HashSet<String> ironArmor;
    private final HashSet<String> ironTools;
    private final HashSet<String> stringTools;
    private final HashSet<String> goldArmor;
    private final HashSet<String> goldTools;
    private final HashSet<String> chainmailArmor;
    private final HashSet<String> diamondArmor;
    private final HashSet<String> diamondTools;
    private final HashSet<String> armors;

    private final HashSet<String> swords;
    private final HashSet<String> axes;
    private final HashSet<String> hoes;
    private final HashSet<String> shovels;
    private final HashSet<String> pickAxes;
    private final HashSet<String> tridents;
    private final HashSet<String> bows;
    private final HashSet<String> tools;

    private final HashSet<String> enchantables;

    private final HashSet<String> ores;

    private final HashMap<String, Integer> tierValue;


    public MaterialMapStore()
    {
        abilityBlackList = new HashSet<>();
        toolBlackList = new HashSet<>();
        mossyWhiteList = new HashSet<>();
        leavesWhiteList = new HashSet<>();
        herbalismAbilityBlackList = new HashSet<>();
        blockCrackerWhiteList = new HashSet<>();
        canMakeShroomyWhiteList = new HashSet<>();
        multiBlockPlant = new HashSet<>();
        foodItemWhiteList = new HashSet<>();
        glassBlocks = new HashSet<>();

        leatherArmor = new HashSet<>();
        ironArmor = new HashSet<>();
        chainmailArmor = new HashSet<>();
        goldArmor = new HashSet<>();
        diamondArmor = new HashSet<>();
        netheriteArmor = new HashSet<>();
        armors = new HashSet<>();

        woodTools = new HashSet<>();
        stoneTools = new HashSet<>();
        ironTools = new HashSet<>();
        goldTools = new HashSet<>();
        diamondTools = new HashSet<>();
        netheriteTools = new HashSet<>();
        bows = new HashSet<>();
        stringTools = new HashSet<>();
        tools = new HashSet<>();

        swords = new HashSet<>();
        axes = new HashSet<>();
        pickAxes = new HashSet<>();
        shovels = new HashSet<>();
        hoes = new HashSet<>();
        tridents = new HashSet<>();

        enchantables = new HashSet<>();

        ores = new HashSet<>();

        tierValue = new HashMap<>();

        fillVanillaMaterialRegisters();
    }

    public boolean isMultiBlockPlant(Material material)
    {
        return multiBlockPlant.contains(material.getKey().getKey());
    }

    public boolean isAbilityActivationBlackListed(Material material)
    {
        return abilityBlackList.contains(material.getKey().getKey());
    }

    public boolean isToolActivationBlackListed(Material material)
    {
        return toolBlackList.contains(material.getKey().getKey());
    }

    public boolean isMossyWhiteListed(Material material)
    {
        return mossyWhiteList.contains(material.getKey().getKey());
    }

    public boolean isLeavesWhiteListed(Material material)
    {
        return leavesWhiteList.contains(material.getKey().getKey());
    }

    public boolean isHerbalismAbilityWhiteListed(Material material)
    {
        return herbalismAbilityBlackList.contains(material.getKey().getKey());
    }

    public boolean isBlockCrackerWhiteListed(Material material)
    {
        return blockCrackerWhiteList.contains(material.getKey().getKey());
    }

    public boolean isShroomyWhiteListed(Material material)
    {
        return canMakeShroomyWhiteList.contains(material.getKey().getKey());
    }

    private void fillVanillaMaterialRegisters()
    {
        fillAbilityBlackList();
        fillToolBlackList();
        fillMossyWhiteList();
        fillLeavesWhiteList();
        fillHerbalismAbilityBlackList();
        fillBlockCrackerWhiteList();
        fillShroomyWhiteList();
        fillMultiBlockPlantSet();
        fillFoodWhiteList();
        fillGlassBlockWhiteList();
        fillArmors();
        fillTools();
        fillEnchantables();
        fillOres();

        fillTierMap();
    }

    private void fillTierMap() {
        for(String id : leatherArmor) {
            tierValue.put(id, 1);
        }

        for(String id : ironArmor) {
            tierValue.put(id, 2);
        }

        for(String id : goldArmor) {
            tierValue.put(id, 3);
        }

        for(String id : chainmailArmor) {
            tierValue.put(id, 3);
        }

        for(String id : diamondArmor) {
            tierValue.put(id, 6);
        }

        for(String id : netheriteArmor) {
            tierValue.put(id, 12);
        }
    }

    private void fillOres() {
        ores.add("coal_ore");
        ores.add("diamond_ore");
        ores.add("nether_quartz_ore");
        ores.add("quartz_ore"); //Pre 1.13
        ores.add("gold_ore");
        ores.add("iron_ore");
        ores.add("lapis_ore");
        ores.add("redstone_ore");
        ores.add("emerald_ore");
        ores.add("ancient_debris");
        ores.add("nether_gold_ore");
        ores.add("gilded_blackstone");
    }

    private void fillArmors() {
        fillLeatherArmorWhiteList();
        fillIronArmorWhiteList();
        fillChainmailWhiteList();
        fillGoldArmorWhiteList();
        fillDiamondArmorWhiteList();
        fillnetheriteArmorWhiteList();

        //Add all armors to armors hashset
        armors.addAll(leatherArmor);
        armors.addAll(ironArmor);
        armors.addAll(chainmailArmor);
        armors.addAll(goldArmor);
        armors.addAll(diamondArmor);
        armors.addAll(netheriteArmor);

        armors.add("turtle_shell");
    }

    private void fillEnchantables() {
        enchantables.addAll(armors);
        enchantables.addAll(swords);
        enchantables.addAll(axes);
        enchantables.addAll(hoes);
        enchantables.addAll(pickAxes);
        enchantables.addAll(tridents);
        enchantables.addAll(bows);

        enchantables.add("shears");
        enchantables.add("fishing_rod");
        enchantables.add("carrot_on_a_stick");
        enchantables.add("enchanted_book");
        enchantables.add("flint_and_steel");
        enchantables.add("turtle_shell");
    }

    private void fillTools() {
        fillWoodToolsWhiteList();
        fillStoneToolsWhiteList();
        fillIronToolsWhiteList();
        fillGoldToolsWhiteList();
        fillDiamondToolsWhiteList();
        fillnetheriteToolsWhiteList();

        fillSwords();
        fillAxes();
        fillPickAxes();
        fillHoes();
        fillShovels();
        fillTridents();
        fillStringTools();
        fillBows();

        //Tools collection
        tools.addAll(woodTools);
        tools.addAll(stoneTools);
        tools.addAll(ironTools);
        tools.addAll(goldTools);
        tools.addAll(diamondTools);
        tools.addAll(netheriteTools);
        tools.addAll(tridents);
        tools.addAll(stringTools);
        tools.addAll(bows);
    }

    private void fillBows() {
        bows.add("bow");
    }

    private void fillStringTools() {
        stringTools.add("bow");
        stringTools.add("fishing_rod");
        stringTools.add("carrot_on_a_stick");
    }

    private void fillTridents() {
        tridents.add("trident");
    }

    private void fillSwords() {
        swords.add("wood_sword");
        swords.add("wooden_sword");
        swords.add("stone_sword");
        swords.add("iron_sword");
        swords.add("gold_sword");
        swords.add("golden_sword");
        swords.add("diamond_sword");
        swords.add("netherite_sword");
    }

    private void fillAxes() {
        axes.add("wood_axe");
        axes.add("wooden_axe");
        axes.add("stone_axe");
        axes.add("iron_axe");
        axes.add("gold_axe");
        axes.add("golden_axe");
        axes.add("diamond_axe");
        axes.add("netherite_axe");
    }

    private void fillPickAxes() {
        pickAxes.add("wood_pickaxe");
        pickAxes.add("wooden_pickaxe");
        pickAxes.add("stone_pickaxe");
        pickAxes.add("iron_pickaxe");
        pickAxes.add("gold_pickaxe");
        pickAxes.add("golden_pickaxe");
        pickAxes.add("diamond_pickaxe");
        pickAxes.add("netherite_pickaxe");
    }

    private void fillHoes() {
        hoes.add("wood_hoe");
        hoes.add("wooden_hoe");
        hoes.add("stone_hoe");
        hoes.add("iron_hoe");
        hoes.add("gold_hoe");
        hoes.add("golden_hoe");
        hoes.add("diamond_hoe");
        hoes.add("netherite_hoe");
    }

    private void fillShovels() {
        shovels.add("wood_shovel");
        shovels.add("wooden_shovel");
        shovels.add("stone_shovel");
        shovels.add("iron_shovel");
        shovels.add("gold_shovel");
        shovels.add("golden_shovel");
        shovels.add("diamond_shovel");
        shovels.add("netherite_shovel");
    }

    private void fillLeatherArmorWhiteList() {
        leatherArmor.add("leather_helmet");
        leatherArmor.add("leather_chestplate");
        leatherArmor.add("leather_leggings");
        leatherArmor.add("leather_boots");
    }

    private void fillIronArmorWhiteList() {
        ironArmor.add("iron_helmet");
        ironArmor.add("iron_chestplate");
        ironArmor.add("iron_leggings");
        ironArmor.add("iron_boots");
    }

    private void fillChainmailWhiteList() {
        chainmailArmor.add("chainmail_helmet");
        chainmailArmor.add("chainmail_chestplate");
        chainmailArmor.add("chainmail_leggings");
        chainmailArmor.add("chainmail_boots");
    }

    private void fillGoldArmorWhiteList() {
        goldArmor.add("gold_helmet");
        goldArmor.add("gold_chestplate");
        goldArmor.add("gold_leggings");
        goldArmor.add("gold_boots");

        //Gold became Golden post 1.13
        goldArmor.add("golden_helmet");
        goldArmor.add("golden_chestplate");
        goldArmor.add("golden_leggings");
        goldArmor.add("golden_boots");
    }

    private void fillDiamondArmorWhiteList() {
        diamondArmor.add("diamond_helmet");
        diamondArmor.add("diamond_chestplate");
        diamondArmor.add("diamond_leggings");
        diamondArmor.add("diamond_boots");
    }

    private void fillnetheriteArmorWhiteList() {
        netheriteArmor.add("netherite_helmet");
        netheriteArmor.add("netherite_chestplate");
        netheriteArmor.add("netherite_leggings");
        netheriteArmor.add("netherite_boots");
    }

    private void fillWoodToolsWhiteList() {
        woodTools.add("wood_sword");
        woodTools.add("wood_axe");
        woodTools.add("wood_hoe");
        woodTools.add("wood_pickaxe");
        woodTools.add("wood_shovel");

        //Wood became wooden post 1.13
        woodTools.add("wooden_sword");
        woodTools.add("wooden_axe");
        woodTools.add("wooden_hoe");
        woodTools.add("wooden_pickaxe");
        woodTools.add("wooden_shovel");
    }

    private void fillStoneToolsWhiteList() {
        stoneTools.add("stone_sword");
        stoneTools.add("stone_axe");
        stoneTools.add("stone_hoe");
        stoneTools.add("stone_pickaxe");
        stoneTools.add("stone_shovel");
    }

    private void fillIronToolsWhiteList() {
        ironTools.add("iron_sword");
        ironTools.add("iron_axe");
        ironTools.add("iron_hoe");
        ironTools.add("iron_pickaxe");
        ironTools.add("iron_shovel");

        //Used for repair, remove in 2.2
        //TODO: Remove in 2.2
        //TODO: Remove in 2.2
        //TODO: Remove in 2.2
        //TODO: Remove in 2.2
        //TODO: Remove in 2.2
        //TODO: Remove in 2.2
        //TODO: Remove in 2.2
        //TODO: Remove in 2.2
        //TODO: Remove in 2.2
        //TODO: Remove in 2.2
        //TODO: Remove in 2.2
        //TODO: Remove in 2.2
        //TODO: Remove in 2.2
        //TODO: Remove in 2.2
        //TODO: Remove in 2.2
        //TODO: Remove in 2.2
        //TODO: Remove in 2.2
        //TODO: Remove in 2.2
        //TODO: Remove in 2.2
        //TODO: Remove in 2.2
        ironTools.add("bucket");
        ironTools.add("flint_and_steel");
        ironTools.add("shears");
    }

    private void fillGoldToolsWhiteList() {
        goldTools.add("gold_sword");
        goldTools.add("gold_axe");
        goldTools.add("gold_hoe");
        goldTools.add("gold_pickaxe");
        goldTools.add("gold_shovel");

        //Gold became golden post 1.13
        goldTools.add("golden_sword");
        goldTools.add("golden_axe");
        goldTools.add("golden_hoe");
        goldTools.add("golden_pickaxe");
        goldTools.add("golden_shovel");
    }

    private void fillDiamondToolsWhiteList() {
        diamondTools.add("diamond_sword");
        diamondTools.add("diamond_axe");
        diamondTools.add("diamond_hoe");
        diamondTools.add("diamond_pickaxe");
        diamondTools.add("diamond_shovel");
    }

    private void fillnetheriteToolsWhiteList() {
        netheriteTools.add("netherite_sword");
        netheriteTools.add("netherite_axe");
        netheriteTools.add("netherite_hoe");
        netheriteTools.add("netherite_pickaxe");
        netheriteTools.add("netherite_shovel");
    }

    private void fillGlassBlockWhiteList() {
        glassBlocks.add("glass");
        glassBlocks.add("glass_pane");
        glassBlocks.add("black_stained_glass");
        glassBlocks.add("black_stained_glass_pane");
        glassBlocks.add("blue_stained_glass");
        glassBlocks.add("blue_stained_glass_pane");
        glassBlocks.add("brown_stained_glass");
        glassBlocks.add("brown_stained_glass_pane");
        glassBlocks.add("cyan_stained_glass");
        glassBlocks.add("cyan_stained_glass_pane");
        glassBlocks.add("gray_stained_glass");
        glassBlocks.add("gray_stained_glass_pane");
        glassBlocks.add("green_stained_glass");
        glassBlocks.add("green_stained_glass_pane");
        glassBlocks.add("light_blue_stained_glass");
        glassBlocks.add("light_blue_stained_glass_pane");
        glassBlocks.add("light_gray_stained_glass");
        glassBlocks.add("light_gray_stained_glass_pane");
        glassBlocks.add("lime_stained_glass");
        glassBlocks.add("lime_stained_glass_pane");
        glassBlocks.add("magenta_stained_glass");
        glassBlocks.add("magenta_stained_glass_pane");
        glassBlocks.add("orange_stained_glass");
        glassBlocks.add("orange_stained_glass_pane");
        glassBlocks.add("pink_stained_glass");
        glassBlocks.add("pink_stained_glass_pane");
        glassBlocks.add("purple_stained_glass");
        glassBlocks.add("purple_stained_glass_pane");
        glassBlocks.add("red_stained_glass");
        glassBlocks.add("red_stained_glass_pane");
        glassBlocks.add("white_stained_glass");
        glassBlocks.add("white_stained_glass_pane");
        glassBlocks.add("yellow_stained_glass");
        glassBlocks.add("yellow_stained_glass_pane");
    }

    private void fillFoodWhiteList() {
        foodItemWhiteList.add("apple");
        foodItemWhiteList.add("baked_potato");
        foodItemWhiteList.add("beetroot");
        foodItemWhiteList.add("beetroot_soup");
        foodItemWhiteList.add("bread");
        foodItemWhiteList.add("cake");
        foodItemWhiteList.add("carrot");
        foodItemWhiteList.add("chorus_fruit");
        foodItemWhiteList.add("cooked_chicken");
        foodItemWhiteList.add("cooked_cod");
        foodItemWhiteList.add("cooked_mutton");
        foodItemWhiteList.add("cooked_porkchop");
        foodItemWhiteList.add("cooked_rabbit");
        foodItemWhiteList.add("cooked_salmon");
        foodItemWhiteList.add("cookie");
        foodItemWhiteList.add("dried_kelp");
        foodItemWhiteList.add("golden_apple");
        foodItemWhiteList.add("enchanted_golden_apple");
        foodItemWhiteList.add("golden_carrot");
        foodItemWhiteList.add("melon_slice");
        foodItemWhiteList.add("mushroom_stew");
        foodItemWhiteList.add("poisonous_potato");
        foodItemWhiteList.add("potato");
        foodItemWhiteList.add("pumpkin_pie");
        foodItemWhiteList.add("rabbit_stew");
        foodItemWhiteList.add("raw_beef");
        foodItemWhiteList.add("raw_chicken");
        foodItemWhiteList.add("raw_cod");
        foodItemWhiteList.add("raw_mutton");
        foodItemWhiteList.add("raw_porkchop");
        foodItemWhiteList.add("raw_rabbit");
        foodItemWhiteList.add("raw_salmon");
        foodItemWhiteList.add("rotten_flesh");
        foodItemWhiteList.add("suspicious_stew");
        foodItemWhiteList.add("sweet_berries");
        foodItemWhiteList.add("tropical_fish");
    }

    /**
     * Checks if a Material is used for Armor
     * @param material target material
     * @return true if it is used for armor
     */
    public boolean isArmor(Material material) {
        return isArmor(material.getKey().getKey());
    }

    /**
     * Checks if the id provided is used as armor
     * @param id target item id
     * @return true if the item id matches armor
     */
    public boolean isArmor(String id) {
        return armors.contains(id);
    }

    public boolean isTool(Material material) {
        return isTool(material.getKey().getKey());
    }

    public boolean isTool(String id) {
        return tools.contains(id);
    }

    public boolean isEnchantable(Material material) {
        return isEnchantable(material.getKey().getKey());
    }

    public boolean isEnchantable(String id) {
        return enchantables.contains(id);
    }

    public boolean isOre(Material material) {
        return isOre(material.getKey().getKey());
    }

    public boolean isOre(String id) {
        return ores.contains(id);
    }

    public boolean isBow(Material material) {
        return isBow(material.getKey().getKey());
    }

    public boolean isBow(String id) {
        return bows.contains(id);
    }

    public boolean isLeatherArmor(Material material) {
        return isLeatherArmor(material.getKey().getKey());
    }

    public boolean isLeatherArmor(String id) {
        return leatherArmor.contains(id);
    }

    public boolean isIronArmor(Material material) {
        return isIronArmor(material.getKey().getKey());
    }

    public boolean isIronArmor(String id) {
        return ironArmor.contains(id);
    }

    public boolean isGoldArmor(Material material) {
        return isGoldArmor(material.getKey().getKey());
    }

    public boolean isGoldArmor(String id) {
        return goldArmor.contains(id);
    }

    public boolean isDiamondArmor(Material material) {
        return isDiamondArmor(material.getKey().getKey());
    }

    public boolean isDiamondArmor(String id) {
        return diamondArmor.contains(id);
    }

    public boolean isChainmailArmor(Material material) {
        return isChainmailArmor(material.getKey().getKey());
    }

    public boolean isChainmailArmor(String id) {
        return chainmailArmor.contains(id);
    }

    public boolean isNetheriteArmor(Material material) {
        return isNetheriteArmor(material.getKey().getKey());
    }

    public boolean isNetheriteArmor(String id) {
        return netheriteArmor.contains(id);
    }

    public boolean isWoodTool(Material material) {
        return isWoodTool(material.getKey().getKey());
    }

    public boolean isWoodTool(String id) {
        return woodTools.contains(id);
    }

    public boolean isStoneTool(Material material) {
        return isStoneTool(material.getKey().getKey());
    }

    public boolean isStoneTool(String id) {
        return stoneTools.contains(id);
    }

    public boolean isIronTool(Material material) {
        return isIronTool(material.getKey().getKey());
    }

    public boolean isIronTool(String id) {
        return ironTools.contains(id);
    }

    public boolean isGoldTool(Material material) {
        return isGoldTool(material.getKey().getKey());
    }

    public boolean isGoldTool(String id) {
        return goldTools.contains(id);
    }

    public boolean isDiamondTool(Material material) {
        return isDiamondTool(material.getKey().getKey());
    }

    public boolean isDiamondTool(String id) {
        return diamondTools.contains(id);
    }

    public boolean isSword(Material material) {
        return isSword(material.getKey().getKey());
    }

    public boolean isSword(String id) {
        return swords.contains(id);
    }

    public boolean isAxe(Material material) {
        return isAxe(material.getKey().getKey());
    }

    public boolean isAxe(String id) {
        return axes.contains(id);
    }

    public boolean isPickAxe(Material material) {
        return isPickAxe(material.getKey().getKey());
    }

    public boolean isPickAxe(String id) {
        return pickAxes.contains(id);
    }

    public boolean isShovel(Material material) {
        return isShovel(material.getKey().getKey());
    }

    public boolean isShovel(String id) {
        return shovels.contains(id);
    }

    public boolean isHoe(Material material) {
        return isHoe(material.getKey().getKey());
    }

    public boolean isHoe(String id) {
        return hoes.contains(id);
    }

    public boolean isNetheriteTool(Material material) {
        return isNetheriteTool(material.getKey().getKey());
    }

    public boolean isNetheriteTool(String id) {
        return netheriteTools.contains(id);
    }

    public boolean isStringTool(Material material) {
        return isStringTool(material.getKey().getKey());
    }

    public boolean isStringTool(String id) {
        return stringTools.contains(id);
    }

    public boolean isGlass(Material material) {
        return glassBlocks.contains(material.getKey().getKey());
    }

    public boolean isFood(Material material) {
        return foodItemWhiteList.contains(material.getKey().getKey());
    }

    private void fillMultiBlockPlantSet()
    {
        //Single Block Plants
//        plantBlockSet.add("melon");
//        plantBlockSet.add("pumpkin");
//        plantBlockSet.add("potatoes");
//        plantBlockSet.add("carrots");
//        plantBlockSet.add("beetroots");
//        plantBlockSet.add("nether_wart");
//        plantBlockSet.add("grass");
//        plantBlockSet.add("fern");
//        plantBlockSet.add("large_fern");

        //Multi-Block Plants
        multiBlockPlant.add("cactus");
        multiBlockPlant.add("chorus_plant");
        multiBlockPlant.add("chorus_flower");
        multiBlockPlant.add("sugar_cane");
        multiBlockPlant.add("kelp_plant");
        multiBlockPlant.add("kelp");
        multiBlockPlant.add("tall_seagrass");
        multiBlockPlant.add("large_fern");
        multiBlockPlant.add("tall_grass");
        multiBlockPlant.add("bamboo");
        multiBlockPlant.add("weeping_vines_plant");
        multiBlockPlant.add("twisted_vines_plant");
    }

    private void fillShroomyWhiteList()
    {
        canMakeShroomyWhiteList.add("dirt");
        canMakeShroomyWhiteList.add("grass");
        canMakeShroomyWhiteList.add("grass_path");
    }

    private void fillBlockCrackerWhiteList()
    {
        blockCrackerWhiteList.add("stone_bricks");
        blockCrackerWhiteList.add("infested_stone_bricks");

    }

    private void fillHerbalismAbilityBlackList()
    {
        herbalismAbilityBlackList.add("dirt");
        herbalismAbilityBlackList.add("grass");
        herbalismAbilityBlackList.add("grass_path");
        herbalismAbilityBlackList.add("farmland");
    }

    private void fillLeavesWhiteList()
    {
        leavesWhiteList.add("oak_leaves");
        leavesWhiteList.add("acacia_leaves");
        leavesWhiteList.add("birch_leaves");
        leavesWhiteList.add("dark_oak_leaves");
        leavesWhiteList.add("jungle_leaves");
        leavesWhiteList.add("spruce_leaves");
        leavesWhiteList.add("nether_wart_block");
        leavesWhiteList.add("warped_wart_block");
    }

    private void fillMossyWhiteList()
    {
        mossyWhiteList.add("cobblestone");
        mossyWhiteList.add("dirt");
        mossyWhiteList.add("grass_path");
        mossyWhiteList.add("stone_bricks");
        mossyWhiteList.add("cobblestone_wall");
    }

    private void fillAbilityBlackList()
    {
        abilityBlackList.add("warped_fence_gate");
        abilityBlackList.add("crimson_fence_gate");
        abilityBlackList.add("warped_pressure_plate");
        abilityBlackList.add("crimson_pressure_plate");
        abilityBlackList.add("warped_button");
        abilityBlackList.add("crimson_button");
        abilityBlackList.add("warped_door");
        abilityBlackList.add("crimson_door");
        abilityBlackList.add("warped_trapdoor");
        abilityBlackList.add("crimson_trapdoor");
        abilityBlackList.add("black_bed");
        abilityBlackList.add("blue_bed");
        abilityBlackList.add("brown_bed");
        abilityBlackList.add("cyan_bed");
        abilityBlackList.add("gray_bed");
        abilityBlackList.add("green_bed");
        abilityBlackList.add("light_blue_bed");
        abilityBlackList.add("light_gray_bed");
        abilityBlackList.add("lime_bed");
        abilityBlackList.add("magenta_bed");
        abilityBlackList.add("orange_bed");
        abilityBlackList.add("pink_bed");
        abilityBlackList.add("purple_bed");
        abilityBlackList.add("red_bed");
        abilityBlackList.add("white_bed");
        abilityBlackList.add("yellow_bed");
        abilityBlackList.add("brewing_stand");
        abilityBlackList.add("bookshelf");
        abilityBlackList.add("cake");
        abilityBlackList.add("chest");
        abilityBlackList.add("dispenser");
        abilityBlackList.add("enchanting_table");
        abilityBlackList.add("ender_chest");
        abilityBlackList.add("oak_fence_gate");
        abilityBlackList.add("acacia_fence_gate");
        abilityBlackList.add("dark_oak_fence_gate");
        abilityBlackList.add("spruce_fence_gate");
        abilityBlackList.add("birch_fence_gate");
        abilityBlackList.add("jungle_fence_gate");
        abilityBlackList.add("furnace");
        abilityBlackList.add("jukebox");
        abilityBlackList.add("lever");
        abilityBlackList.add("note_block");
        abilityBlackList.add("stone_button");
        abilityBlackList.add("oak_button");
        abilityBlackList.add("birch_button");
        abilityBlackList.add("acacia_button");
        abilityBlackList.add("dark_oak_button");
        abilityBlackList.add("jungle_button");
        abilityBlackList.add("spruce_button");
        abilityBlackList.add("acacia_trapdoor");
        abilityBlackList.add("birch_trapdoor");
        abilityBlackList.add("dark_oak_trapdoor");
        abilityBlackList.add("jungle_trapdoor");
        abilityBlackList.add("oak_trapdoor");
        abilityBlackList.add("spruce_trapdoor");
        abilityBlackList.add("acacia_sign");
        abilityBlackList.add("acacia_wall_sign");
        abilityBlackList.add("birch_sign");
        abilityBlackList.add("birch_wall_sign");
        abilityBlackList.add("dark_oak_sign");
        abilityBlackList.add("dark_oak_wall_sign");
        abilityBlackList.add("jungle_sign");
        abilityBlackList.add("jungle_wall_sign");
        abilityBlackList.add("spruce_sign");
        abilityBlackList.add("spruce_wall_sign");
        abilityBlackList.add("oak_sign");
        abilityBlackList.add("oak_wall_sign");
        abilityBlackList.add("crafting_table");
        abilityBlackList.add("beacon");
        abilityBlackList.add("anvil");
        abilityBlackList.add("dropper");
        abilityBlackList.add("hopper");
        abilityBlackList.add("trapped_chest");
        abilityBlackList.add("iron_door");
        abilityBlackList.add("iron_trapdoor");
        abilityBlackList.add("oak_door");
        abilityBlackList.add("acacia_door");
        abilityBlackList.add("spruce_door");
        abilityBlackList.add("birch_door");
        abilityBlackList.add("jungle_door");
        abilityBlackList.add("dark_oak_door");
        abilityBlackList.add("oak_fence");
        abilityBlackList.add("acacia_fence");
        abilityBlackList.add("dark_oak_fence");
        abilityBlackList.add("birch_fence");
        abilityBlackList.add("jungle_fence");
        abilityBlackList.add("spruce_fence");
        abilityBlackList.add("armor_stand");
        abilityBlackList.add("black_shulker_box");
        abilityBlackList.add("blue_shulker_box");
        abilityBlackList.add("brown_shulker_box");
        abilityBlackList.add("cyan_shulker_box");
        abilityBlackList.add("gray_shulker_box");
        abilityBlackList.add("green_shulker_box");
        abilityBlackList.add("light_blue_shulker_box");
        abilityBlackList.add("lime_shulker_box");
        abilityBlackList.add("magenta_shulker_box");
        abilityBlackList.add("orange_shulker_box");
        abilityBlackList.add("pink_shulker_box");
        abilityBlackList.add("purple_shulker_box");
        abilityBlackList.add("red_shulker_box");
        abilityBlackList.add("light_gray_shulker_box");
        abilityBlackList.add("white_shulker_box");
        abilityBlackList.add("yellow_shulker_box");
        abilityBlackList.add("shulker_box");
        abilityBlackList.add("wall_sign"); //1.13 and lower?
        abilityBlackList.add("sign"); //1.13 and lower?
        abilityBlackList.add("cartography_table");
        abilityBlackList.add("grindstone");
        abilityBlackList.add("lectern");
        abilityBlackList.add("loom");
        abilityBlackList.add("scaffolding");
        abilityBlackList.add("smoker");
        abilityBlackList.add("stonecutter");
        abilityBlackList.add("sweet_berry_bush");
        abilityBlackList.add("bell");
        abilityBlackList.add("barrel");
        abilityBlackList.add("blast_furnace");
        abilityBlackList.add("campfire");
        abilityBlackList.add("soul_campfire");
        abilityBlackList.add("composter");
        abilityBlackList.add("lodestone");
        abilityBlackList.add("respawn_anchor");
    }
    
    private void fillToolBlackList()
    {
        //TODO: Add anvils / missing logs
        //TODO: Reorganize this list, can we also dynamically populate some of this?
        toolBlackList.add("black_bed");
        toolBlackList.add("blue_bed");
        toolBlackList.add("brown_bed");
        toolBlackList.add("cyan_bed");
        toolBlackList.add("gray_bed");
        toolBlackList.add("green_bed");
        toolBlackList.add("light_blue_bed");
        toolBlackList.add("light_gray_bed");
        toolBlackList.add("lime_bed");
        toolBlackList.add("magenta_bed");
        toolBlackList.add("orange_bed");
        toolBlackList.add("pink_bed");
        toolBlackList.add("purple_bed");
        toolBlackList.add("red_bed");
        toolBlackList.add("white_bed");
        toolBlackList.add("yellow_bed");
        toolBlackList.add("brewing_stand");
        toolBlackList.add("bookshelf");
        toolBlackList.add("cake");
        toolBlackList.add("chest");
        toolBlackList.add("dispenser");
        toolBlackList.add("enchanting_table");
        toolBlackList.add("ender_chest");
        toolBlackList.add("oak_fence_gate");
        toolBlackList.add("acacia_fence_gate");
        toolBlackList.add("dark_oak_fence_gate");
        toolBlackList.add("spruce_fence_gate");
        toolBlackList.add("birch_fence_gate");
        toolBlackList.add("jungle_fence_gate");
        toolBlackList.add("furnace");
        toolBlackList.add("jukebox");
        toolBlackList.add("lever");
        toolBlackList.add("note_block");
        toolBlackList.add("stone_button");
        toolBlackList.add("oak_button");
        toolBlackList.add("birch_button");
        toolBlackList.add("acacia_button");
        toolBlackList.add("dark_oak_button");
        toolBlackList.add("jungle_button");
        toolBlackList.add("spruce_button");
        toolBlackList.add("acacia_trapdoor");
        toolBlackList.add("birch_trapdoor");
        toolBlackList.add("dark_oak_trapdoor");
        toolBlackList.add("jungle_trapdoor");
        toolBlackList.add("oak_trapdoor");
        toolBlackList.add("spruce_trapdoor");
        toolBlackList.add("crafting_table");
        toolBlackList.add("beacon");
        toolBlackList.add("anvil");
        toolBlackList.add("dropper");
        toolBlackList.add("hopper");
        toolBlackList.add("trapped_chest");
        toolBlackList.add("iron_door");
        toolBlackList.add("iron_trapdoor");
        toolBlackList.add("oak_door");
        toolBlackList.add("acacia_door");
        toolBlackList.add("spruce_door");
        toolBlackList.add("birch_door");
        toolBlackList.add("jungle_door");
        toolBlackList.add("dark_oak_door");
        toolBlackList.add("oak_fence");
        toolBlackList.add("acacia_fence");
        toolBlackList.add("dark_oak_fence");
        toolBlackList.add("birch_fence");
        toolBlackList.add("jungle_fence");
        toolBlackList.add("spruce_fence");
        toolBlackList.add("armor_stand");
        toolBlackList.add("black_shulker_box");
        toolBlackList.add("blue_shulker_box");
        toolBlackList.add("brown_shulker_box");
        toolBlackList.add("cyan_shulker_box");
        toolBlackList.add("gray_shulker_box");
        toolBlackList.add("green_shulker_box");
        toolBlackList.add("light_blue_shulker_box");
        toolBlackList.add("lime_shulker_box");
        toolBlackList.add("magenta_shulker_box");
        toolBlackList.add("orange_shulker_box");
        toolBlackList.add("pink_shulker_box");
        toolBlackList.add("purple_shulker_box");
        toolBlackList.add("red_shulker_box");
        toolBlackList.add("light_gray_shulker_box");
        toolBlackList.add("white_shulker_box");
        toolBlackList.add("yellow_shulker_box");
        toolBlackList.add("shulker_box");
        toolBlackList.add("acacia_sign");
        toolBlackList.add("acacia_wall_sign");
        toolBlackList.add("birch_sign");
        toolBlackList.add("birch_wall_sign");
        toolBlackList.add("dark_oak_sign");
        toolBlackList.add("dark_oak_wall_sign");
        toolBlackList.add("jungle_sign");
        toolBlackList.add("jungle_wall_sign");
        toolBlackList.add("spruce_sign");
        toolBlackList.add("spruce_wall_sign");
        toolBlackList.add("oak_sign");
        toolBlackList.add("oak_wall_sign");
        toolBlackList.add("stripped_acacia_log");
        toolBlackList.add("stripped_acacia_wood");
        toolBlackList.add("stripped_birch_log");
        toolBlackList.add("stripped_birch_wood");
        toolBlackList.add("stripped_dark_oak_log");
        toolBlackList.add("stripped_dark_oak_wood");
        toolBlackList.add("stripped_jungle_log");
        toolBlackList.add("stripped_jungle_wood");
        toolBlackList.add("stripped_oak_log");
        toolBlackList.add("stripped_oak_wood");
        toolBlackList.add("stripped_spruce_log");
        toolBlackList.add("stripped_spruce_wood");
        toolBlackList.add("acacia_log");
        toolBlackList.add("acacia_wood");
        toolBlackList.add("birch_log");
        toolBlackList.add("birch_wood");
        toolBlackList.add("dark_oak_log");
        toolBlackList.add("dark_oak_wood");
        toolBlackList.add("jungle_log");
        toolBlackList.add("jungle_wood");
        toolBlackList.add("oak_log");
        toolBlackList.add("oak_wood");
        toolBlackList.add("spruce_log");
        toolBlackList.add("bell");
        toolBlackList.add("barrel");
        toolBlackList.add("blast_furnace");
        toolBlackList.add("campfire");
        toolBlackList.add("soul_campfire");
        toolBlackList.add("cartography_table");
        toolBlackList.add("composter");
        toolBlackList.add("grindstone");
        toolBlackList.add("lectern");
        toolBlackList.add("loom");
        toolBlackList.add("smoker");
        toolBlackList.add("stonecutter");
        toolBlackList.add("lodestone");
        toolBlackList.add("respawn_anchor");
    }

    public HashSet<String> getNetheriteArmor() {
        return netheriteArmor;
    }

    public HashSet<String> getNetheriteTools() {
        return netheriteTools;
    }


    public int getTier(Material material) {
        return getTier(material.getKey().getKey());
    }

    public int getTier(String id) {
        return tierValue.getOrDefault(id, 1); //1 for unknown items
    }

    private void addToHashSet(String string, HashSet<String> stringHashSet)
    {
        stringHashSet.add(string.toLowerCase(Locale.ENGLISH));
    }
}
