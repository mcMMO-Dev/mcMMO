package com.gmail.nossr50.util;

import org.bukkit.Material;

import java.util.HashSet;

/**
 * Stores hash tables for item and block names
 * This allows for better support across multiple versions of Minecraft
 *
 * This is a temporary class, mcMMO is spaghetti and I'l clean it up later
 *
 */
public class MaterialMapStore {

    private HashSet<String> abilityBlackList;
    private HashSet<String> toolBlackList;
    private HashSet<String> mossyWhiteList;
    private HashSet<String> leavesWhiteList;
    private HashSet<String> herbalismAbilityBlackList;
    private HashSet<String> blockCrackerWhiteList;
    private HashSet<String> canMakeShroomyWhiteList;
    private HashSet<String> multiBlockPlant;
    private HashSet<String> foodItemWhiteList;

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

        fillHardcodedHashSets();
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

    private void fillHardcodedHashSets()
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
        abilityBlackList.add("iron_block");
        abilityBlackList.add("gold_block");
        abilityBlackList.add("bell");
        abilityBlackList.add("barrel");
        abilityBlackList.add("blast_furnace");
        abilityBlackList.add("campfire");
        abilityBlackList.add("composter");

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
        toolBlackList.add("iron_block");
        toolBlackList.add("gold_block");
        toolBlackList.add("bell");
        toolBlackList.add("barrel");
        toolBlackList.add("blast_furnace");
        toolBlackList.add("campfire");
        toolBlackList.add("cartography_table");
        toolBlackList.add("composter");
        toolBlackList.add("grindstone");
        toolBlackList.add("lectern");
        toolBlackList.add("loom");
        toolBlackList.add("smoker");
        toolBlackList.add("stonecutter");
    }

    private void addToHashSet(String string, HashSet<String> stringHashSet)
    {
        stringHashSet.add(string.toLowerCase());
    }
}
