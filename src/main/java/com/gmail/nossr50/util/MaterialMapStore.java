package com.gmail.nossr50.util;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

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

    private final @NotNull HashSet<String> abilityBlackList;
    private final @NotNull HashSet<String> toolBlackList;
    private final @NotNull HashSet<String> mossyWhiteList;
    private final @NotNull HashSet<String> treeFellerDestructibleWhiteList;
    private final @NotNull HashSet<String> herbalismAbilityBlackList;
    private final @NotNull HashSet<String> blockCrackerWhiteList;
    private final @NotNull HashSet<String> canMakeShroomyWhiteList;
    private final @NotNull HashSet<String> multiBlockPlant;
    private final @NotNull HashSet<String> multiBlockHangingPlant;
    private final @NotNull HashSet<String> foodItemWhiteList;
    private final @NotNull HashSet<String> glassBlocks;

    private final @NotNull HashSet<String> netheriteArmor;
    private final @NotNull HashSet<String> netheriteTools;
    private final @NotNull HashSet<String> woodTools;
    private final @NotNull HashSet<String> stoneTools;
    private final @NotNull HashSet<String> leatherArmor;
    private final @NotNull HashSet<String> ironArmor;
    private final @NotNull HashSet<String> ironTools;
    private final @NotNull HashSet<String> stringTools;
    private final @NotNull HashSet<String> goldArmor;
    private final @NotNull HashSet<String> goldTools;
    private final @NotNull HashSet<String> chainmailArmor;
    private final @NotNull HashSet<String> diamondArmor;
    private final @NotNull HashSet<String> diamondTools;
    private final @NotNull HashSet<String> armors;

    private final @NotNull HashSet<String> swords;
    private final @NotNull HashSet<String> axes;
    private final @NotNull HashSet<String> hoes;
    private final @NotNull HashSet<String> shovels;
    private final @NotNull HashSet<String> pickAxes;
    private final @NotNull HashSet<String> tridents;
    private final @NotNull HashSet<String> bows;
    private final @NotNull HashSet<String> crossbows;
    private final @NotNull HashSet<String> tools;

    private final @NotNull HashSet<String> enchantables;

    private final @NotNull HashSet<String> ores;
    private final @NotNull HashSet<String> intendedToolPickAxe;
    private final @NotNull HashSet<String> intendedToolShovel;

    private final @NotNull HashMap<String, Integer> tierValue;


    public MaterialMapStore()
    {
        abilityBlackList = new HashSet<>();
        toolBlackList = new HashSet<>();
        mossyWhiteList = new HashSet<>();
        treeFellerDestructibleWhiteList = new HashSet<>();
        herbalismAbilityBlackList = new HashSet<>();
        blockCrackerWhiteList = new HashSet<>();
        canMakeShroomyWhiteList = new HashSet<>();
        multiBlockPlant = new HashSet<>();
        multiBlockHangingPlant = new HashSet<>();
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
        crossbows = new HashSet<>();
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
        intendedToolPickAxe = new HashSet<>();
        intendedToolShovel = new HashSet<>();

        tierValue = new HashMap<>();

        fillVanillaMaterialRegisters();
    }

    private void fillVanillaMaterialRegisters() {
        //The order matters
        fillAbilityBlackList();
        fillToolBlackList();
        fillMossyWhiteList();
        fillTreeFellerDestructibleWhiteList();
        fillHerbalismAbilityBlackList();
        fillBlockCrackerWhiteList();
        fillShroomyWhiteList();
        fillMultiBlockPlantSet();
        fillMultiBlockHangingPlantSet();
        fillFoodWhiteList();
        fillGlassBlockWhiteList();
        fillArmors();
        fillTools();
        fillEnchantables();
        fillOres();
        fillIntendedTools();

        fillTierMap();
    }

    public boolean isMultiBlockPlant(@NotNull Material material)
    {
        return multiBlockPlant.contains(material.getKey().getKey());
    }

    public boolean isMultiBlockHangingPlant(@NotNull Material material) {
        return multiBlockHangingPlant.contains(material.getKey().getKey());
    }

    public boolean isAbilityActivationBlackListed(@NotNull Material material)
    {
        return abilityBlackList.contains(material.getKey().getKey());
    }

    public boolean isToolActivationBlackListed(@NotNull Material material)
    {
        return toolBlackList.contains(material.getKey().getKey());
    }

    public boolean isMossyWhiteListed(@NotNull Material material)
    {
        return mossyWhiteList.contains(material.getKey().getKey());
    }

    public boolean isTreeFellerDestructible(@NotNull Material material)
    {
        return treeFellerDestructibleWhiteList.contains(material.getKey().getKey());
    }

    public boolean isHerbalismAbilityWhiteListed(@NotNull Material material)
    {
        return herbalismAbilityBlackList.contains(material.getKey().getKey());
    }

    public boolean isBlockCrackerWhiteListed(@NotNull Material material)
    {
        return blockCrackerWhiteList.contains(material.getKey().getKey());
    }

    public boolean isShroomyWhiteListed(@NotNull Material material)
    {
        return canMakeShroomyWhiteList.contains(material.getKey().getKey());
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
        ores.add("lapis_lazuli_ore");
        ores.add("redstone_ore");
        ores.add("emerald_ore");
        ores.add("ancient_debris");
        ores.add("nether_gold_ore");
        ores.add("gilded_blackstone");

        //1.17 Mining Ore Blocks
        ores.add("deepslate_redstone_ore");
        ores.add("deepslate_copper_ore");
        ores.add("deepslate_coal_ore");
        ores.add("deepslate_diamond_ore");
        ores.add("deepslate_emerald_ore");
        ores.add("deepslate_iron_ore");
        ores.add("deepslate_gold_ore");
//        ores.add("deepslate_lapis_lazuli_ore");
        ores.add("deepslate_lapis_ore");
        ores.add("copper_ore");
    }

    private void fillIntendedTools() {
        intendedToolPickAxe.addAll(ores);

        intendedToolPickAxe.add("lapis_lazuli_ore");
        intendedToolPickAxe.add("ice");
        intendedToolPickAxe.add("packed_ice");
        intendedToolPickAxe.add("blue_ice");
        intendedToolPickAxe.add("frosted_ice");
        intendedToolPickAxe.add("anvil");
        intendedToolPickAxe.add("bell");
        intendedToolPickAxe.add("block_of_redstone");
        intendedToolPickAxe.add("brewing_stand");
        intendedToolPickAxe.add("cauldron");
        intendedToolPickAxe.add("chain");
        intendedToolPickAxe.add("hopper");
        intendedToolPickAxe.add("iron_bars");
        intendedToolPickAxe.add("iron_door");
        intendedToolPickAxe.add("iron_trapdoor");
        intendedToolPickAxe.add("lantern");
        intendedToolPickAxe.add("weighted_pressure_plates");
        intendedToolPickAxe.add("block_of_iron");
        intendedToolPickAxe.add("copper_blocks");
        intendedToolPickAxe.add("cut_copper");
        intendedToolPickAxe.add("cut_copper_slab");
        intendedToolPickAxe.add("cut_copper_stairs");
        intendedToolPickAxe.add("lapis_lazuli_block");
        intendedToolPickAxe.add("lightning_rod");
        intendedToolPickAxe.add("block_of_diamond");
        intendedToolPickAxe.add("block_of_emerald");
        intendedToolPickAxe.add("block_of_gold");
        intendedToolPickAxe.add("block_of_netherite");
        intendedToolPickAxe.add("piston");
        intendedToolPickAxe.add("sticky_piston");
        intendedToolPickAxe.add("conduit");
        intendedToolPickAxe.add("shulker_box");
        intendedToolPickAxe.add("element_constructor"); //be & ee
        intendedToolPickAxe.add("compound_creator"); //be & ee
        intendedToolPickAxe.add("material_reducer"); //be & ee
        intendedToolPickAxe.add("activator_rail");
        intendedToolPickAxe.add("detector_rail");
        intendedToolPickAxe.add("powered_rail");
        intendedToolPickAxe.add("rail");
        intendedToolPickAxe.add("andesite");
        intendedToolPickAxe.add("basalt");
        intendedToolPickAxe.add("blackstone");
        intendedToolPickAxe.add("blast_furnace");
        intendedToolPickAxe.add("block_of_coal");
        intendedToolPickAxe.add("block_of_quartz");
        intendedToolPickAxe.add("bricks");
        intendedToolPickAxe.add("cobblestone");
        intendedToolPickAxe.add("cobblestone_wall");
        intendedToolPickAxe.add("concrete");
        intendedToolPickAxe.add("dark_prismarine");
        intendedToolPickAxe.add("diorite");
        intendedToolPickAxe.add("dispenser");
        intendedToolPickAxe.add("dripstone_block");
        intendedToolPickAxe.add("dropper");
        intendedToolPickAxe.add("enchantment_table");
        intendedToolPickAxe.add("end_stone");
        intendedToolPickAxe.add("ender_chest");
        intendedToolPickAxe.add("furnace");
        intendedToolPickAxe.add("glazed_terracotta");
        intendedToolPickAxe.add("granite");
        intendedToolPickAxe.add("grindstone");
        intendedToolPickAxe.add("heat_block"); //be & ee
        intendedToolPickAxe.add("lodestone");
        intendedToolPickAxe.add("mossy_cobblestone");
        intendedToolPickAxe.add("nether_bricks");
        intendedToolPickAxe.add("nether_brick_fence");
        intendedToolPickAxe.add("nether_gold_ore");
        intendedToolPickAxe.add("nether_quartz_ore");
        intendedToolPickAxe.add("netherrack");
        intendedToolPickAxe.add("observer");
        intendedToolPickAxe.add("prismarine");
        intendedToolPickAxe.add("prismarine_bricks");
        intendedToolPickAxe.add("pointed_dripstone");
        intendedToolPickAxe.add("polished_andesite");
        intendedToolPickAxe.add("polished_blackstone");
        intendedToolPickAxe.add("polished_blackstone_bricks");
        intendedToolPickAxe.add("polished_diorite");
        intendedToolPickAxe.add("polished_granite");
        intendedToolPickAxe.add("red_sandstone");
        intendedToolPickAxe.add("sandstone");
        intendedToolPickAxe.add("smoker");
        intendedToolPickAxe.add("spawner");
        intendedToolPickAxe.add("stonecutter");
//        intendedToolPickAxe.add("slabs");
        intendedToolPickAxe.add("colored_terracotta");
//        intendedToolPickAxe.add("stairs");
        intendedToolPickAxe.add("smooth_stone");
        intendedToolPickAxe.add("stone");
        intendedToolPickAxe.add("stone_bricks");
        intendedToolPickAxe.add("stone_button");
        intendedToolPickAxe.add("stone_pressure_plate");
        intendedToolPickAxe.add("terracotta");
        intendedToolPickAxe.add("ancient_debris");
        intendedToolPickAxe.add("crying_obsidian");
        intendedToolPickAxe.add("glowing_obsidian"); //be
        intendedToolPickAxe.add("obsidian");
        intendedToolPickAxe.add("respawn_anchor");

        //slabs
        intendedToolPickAxe.add("petrified_oak_slab");
        intendedToolPickAxe.add("stone_slab");
        intendedToolPickAxe.add("smooth_stone_slab");
        intendedToolPickAxe.add("cobblestone_slab");
        intendedToolPickAxe.add("mossy_cobblestone_slab");
        intendedToolPickAxe.add("stone_brick_slab");
        intendedToolPickAxe.add("mossy_stone_brick_slab");
        intendedToolPickAxe.add("andesite_slab");
        intendedToolPickAxe.add("polished_andesite_slab");
        intendedToolPickAxe.add("diorite_slab");
        intendedToolPickAxe.add("polished_diorite_slab");
        intendedToolPickAxe.add("granite_slab");
        intendedToolPickAxe.add("polished_granite_slab");
        intendedToolPickAxe.add("sandstone_slab");
        intendedToolPickAxe.add("cut_sandstone_slab");
        intendedToolPickAxe.add("smooth_sandstone_slab");
        intendedToolPickAxe.add("red_sandstone_slab");
        intendedToolPickAxe.add("cut_red_sandstone_slab");
        intendedToolPickAxe.add("smooth_red_sandstone_slab");
        intendedToolPickAxe.add("brick_slab");
        intendedToolPickAxe.add("prismarine_brick_slab");
        intendedToolPickAxe.add("dark_prismarine_slab");
        intendedToolPickAxe.add("nether_brick_slab");
        intendedToolPickAxe.add("red_netherbrick_slab");
        intendedToolPickAxe.add("quartz_slab");
        intendedToolPickAxe.add("smooth_quartz_slab");
        intendedToolPickAxe.add("purpur_slab");
        intendedToolPickAxe.add("end_stone_brick_slab");
        intendedToolPickAxe.add("blackstone_slab");
        intendedToolPickAxe.add("polished_blackstone_slab");
        intendedToolPickAxe.add("polished_blackstone_brick_slab");
        intendedToolPickAxe.add("lightly_weathered_cut_copper_slab");
        intendedToolPickAxe.add("semi_weathered_cut_copper_slab");
        intendedToolPickAxe.add("waxed_semi_weathered_cut_copper_slab");
        intendedToolPickAxe.add("weathered_cut_copper_slab");
        intendedToolPickAxe.add("waxed_cut_copper_slab");
        intendedToolPickAxe.add("waxed_lightly_weathered_cut_copper_slab");

        //stairs (not all of these exist, just copied the above list and replaced slab with stairs)
        intendedToolPickAxe.add("petrified_oak_stairs");
        intendedToolPickAxe.add("stone_stairs");
        intendedToolPickAxe.add("smooth_stone_stairs");
        intendedToolPickAxe.add("cobblestone_stairs");
        intendedToolPickAxe.add("mossy_cobblestone_stairs");
        intendedToolPickAxe.add("stone_brick_stairs");
        intendedToolPickAxe.add("mossy_stone_brick_stairs");
        intendedToolPickAxe.add("andesite_stairs");
        intendedToolPickAxe.add("polished_andesite_stairs");
        intendedToolPickAxe.add("diorite_stairs");
        intendedToolPickAxe.add("polished_diorite_stairs");
        intendedToolPickAxe.add("granite_stairs");
        intendedToolPickAxe.add("polished_granite_stairs");
        intendedToolPickAxe.add("sandstone_stairs");
        intendedToolPickAxe.add("cut_sandstone_stairs");
        intendedToolPickAxe.add("smooth_sandstone_stairs");
        intendedToolPickAxe.add("red_sandstone_stairs");
        intendedToolPickAxe.add("cut_red_sandstone_stairs");
        intendedToolPickAxe.add("smooth_red_sandstone_stairs");
        intendedToolPickAxe.add("brick_stairs");
        intendedToolPickAxe.add("prismarine_brick_stairs");
        intendedToolPickAxe.add("dark_prismarine_stairs");
        intendedToolPickAxe.add("nether_brick_stairs");
        intendedToolPickAxe.add("red_netherbrick_stairs");
        intendedToolPickAxe.add("quartz_stairs");
        intendedToolPickAxe.add("smooth_quartz_stairs");
        intendedToolPickAxe.add("purpur_stairs");
        intendedToolPickAxe.add("end_stone_brick_stairs");
        intendedToolPickAxe.add("blackstone_stairs");
        intendedToolPickAxe.add("polished_blackstone_stairs");
        intendedToolPickAxe.add("polished_blackstone_brick_stairs");
        intendedToolPickAxe.add("lightly_weathered_cut_copper_stairs");
        intendedToolPickAxe.add("semi_weathered_cut_copper_stairs");
        intendedToolPickAxe.add("waxed_semi_weathered_cut_copper_stairs");
        intendedToolPickAxe.add("weathered_cut_copper_stairs");
        intendedToolPickAxe.add("waxed_cut_copper_stairs");
        intendedToolPickAxe.add("waxed_lightly_weathered_cut_copper_stairs");

        //1.17 Mining (non-ores)
        intendedToolPickAxe.add("calcite");
        intendedToolPickAxe.add("smooth_basalt");
        intendedToolPickAxe.add("block_of_amethyst");
        intendedToolPickAxe.add("small_amethyst_bud");
        intendedToolPickAxe.add("medium_amethyst_bud");
        intendedToolPickAxe.add("large_amethyst_bud");
        intendedToolPickAxe.add("amethyst_cluster");
        intendedToolPickAxe.add("budding_amethyst");
        intendedToolPickAxe.add("deepslate");
        intendedToolPickAxe.add("cobbled_deepslate");
        intendedToolPickAxe.add("tuff");
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
        enchantables.addAll(crossbows);

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
        fillCrossbows();

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

    private void fillCrossbows() {
        crossbows.add("crossbow");
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
        //TODO: Remove in config update
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
    public boolean isArmor(@NotNull Material material) {
        return isArmor(material.getKey().getKey());
    }

    /**
     * Checks if the id provided is used as armor
     * @param id target item id
     * @return true if the item id matches armor
     */
    public boolean isArmor(@NotNull String id) {
        return armors.contains(id);
    }

    public boolean isTool(@NotNull Material material) {
        return isTool(material.getKey().getKey());
    }

    public boolean isTool(@NotNull String id) {
        return tools.contains(id);
    }

    public boolean isEnchantable(@NotNull Material material) {
        return isEnchantable(material.getKey().getKey());
    }

    public boolean isEnchantable(@NotNull String id) {
        return enchantables.contains(id);
    }

    public boolean isOre(@NotNull Material material) {
        return isOre(material.getKey().getKey());
    }

    public boolean isOre(@NotNull String id) {
        return ores.contains(id);
    }

    public boolean isBow(@NotNull Material material) {
        return isBow(material.getKey().getKey());
    }

    public boolean isBow(@NotNull String id) {
        return bows.contains(id);
    }

    public boolean isCrossbow(@NotNull Material material) {
        return isCrossbow(material.getKey().getKey());
    }

    public boolean isCrossbow(@NotNull String id) {
        return crossbows.contains(id);
    }

    public boolean isLeatherArmor(@NotNull Material material) {
        return isLeatherArmor(material.getKey().getKey());
    }

    public boolean isLeatherArmor(@NotNull String id) {
        return leatherArmor.contains(id);
    }

    public boolean isIronArmor(@NotNull Material material) {
        return isIronArmor(material.getKey().getKey());
    }

    public boolean isIronArmor(@NotNull String id) {
        return ironArmor.contains(id);
    }

    public boolean isGoldArmor(@NotNull Material material) {
        return isGoldArmor(material.getKey().getKey());
    }

    public boolean isGoldArmor(@NotNull String id) {
        return goldArmor.contains(id);
    }

    public boolean isDiamondArmor(@NotNull Material material) {
        return isDiamondArmor(material.getKey().getKey());
    }

    public boolean isDiamondArmor(@NotNull String id) {
        return diamondArmor.contains(id);
    }

    public boolean isChainmailArmor(@NotNull Material material) {
        return isChainmailArmor(material.getKey().getKey());
    }

    public boolean isChainmailArmor(@NotNull String id) {
        return chainmailArmor.contains(id);
    }

    public boolean isNetheriteArmor(@NotNull Material material) {
        return isNetheriteArmor(material.getKey().getKey());
    }

    public boolean isNetheriteArmor(@NotNull String id) {
        return netheriteArmor.contains(id);
    }

    public boolean isWoodTool(@NotNull Material material) {
        return isWoodTool(material.getKey().getKey());
    }

    public boolean isWoodTool(@NotNull String id) {
        return woodTools.contains(id);
    }

    public boolean isStoneTool(@NotNull Material material) {
        return isStoneTool(material.getKey().getKey());
    }

    public boolean isStoneTool(@NotNull String id) {
        return stoneTools.contains(id);
    }

    public boolean isIronTool(@NotNull Material material) {
        return isIronTool(material.getKey().getKey());
    }

    public boolean isIronTool(@NotNull String id) {
        return ironTools.contains(id);
    }

    public boolean isGoldTool(@NotNull Material material) {
        return isGoldTool(material.getKey().getKey());
    }

    public boolean isGoldTool(@NotNull String id) {
        return goldTools.contains(id);
    }

    public boolean isDiamondTool(@NotNull Material material) {
        return isDiamondTool(material.getKey().getKey());
    }

    public boolean isDiamondTool(@NotNull String id) {
        return diamondTools.contains(id);
    }

    public boolean isSword(@NotNull Material material) {
        return isSword(material.getKey().getKey());
    }

    public boolean isSword(@NotNull String id) {
        return swords.contains(id);
    }

    public boolean isAxe(@NotNull Material material) {
        return isAxe(material.getKey().getKey());
    }

    public boolean isAxe(@NotNull String id) {
        return axes.contains(id);
    }

    public boolean isPickAxe(@NotNull Material material) {
        return isPickAxe(material.getKey().getKey());
    }

    public boolean isPickAxe(@NotNull String id) {
        return pickAxes.contains(id);
    }

    public boolean isShovel(@NotNull Material material) {
        return isShovel(material.getKey().getKey());
    }

    public boolean isShovel(@NotNull String id) {
        return shovels.contains(id);
    }

    public boolean isHoe(@NotNull Material material) {
        return isHoe(material.getKey().getKey());
    }

    public boolean isHoe(@NotNull String id) {
        return hoes.contains(id);
    }

    public boolean isNetheriteTool(@NotNull Material material) {
        return isNetheriteTool(material.getKey().getKey());
    }

    public boolean isNetheriteTool(@NotNull String id) {
        return netheriteTools.contains(id);
    }

    public boolean isStringTool(@NotNull Material material) {
        return isStringTool(material.getKey().getKey());
    }

    public boolean isStringTool(@NotNull String id) {
        return stringTools.contains(id);
    }

    public boolean isGlass(@NotNull Material material) {
        return glassBlocks.contains(material.getKey().getKey());
    }

    public boolean isFood(@NotNull Material material) {
        return foodItemWhiteList.contains(material.getKey().getKey());
    }

    private void fillMultiBlockPlantSet()
    {
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

    private void fillMultiBlockHangingPlantSet() {
        multiBlockHangingPlant.add("weeping_vines_plant");
        multiBlockHangingPlant.add("twisted_vines_plant");
        multiBlockHangingPlant.add("cave_vines_plant");
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

    private void fillTreeFellerDestructibleWhiteList()
    {
        treeFellerDestructibleWhiteList.add("oak_leaves");
        treeFellerDestructibleWhiteList.add("acacia_leaves");
        treeFellerDestructibleWhiteList.add("birch_leaves");
        treeFellerDestructibleWhiteList.add("dark_oak_leaves");
        treeFellerDestructibleWhiteList.add("jungle_leaves");
        treeFellerDestructibleWhiteList.add("spruce_leaves");
        treeFellerDestructibleWhiteList.add("azalea_leaves");
        treeFellerDestructibleWhiteList.add("flowering_azalea_leaves");
        treeFellerDestructibleWhiteList.add("nether_wart_block");
        treeFellerDestructibleWhiteList.add("warped_wart_block");
        treeFellerDestructibleWhiteList.add("brown_mushroom_block");
        treeFellerDestructibleWhiteList.add("red_mushroom_block");
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
        toolBlackList.add("sweet_berry_bush");
    }

    public boolean isIntendedToolPickaxe(@NotNull Material material) {
        return intendedToolPickAxe.contains(material.getKey().getKey());
    }

    public boolean isIntendedToolPickaxe(@NotNull String string) {
        return intendedToolPickAxe.contains(string);
    }

    public @NotNull HashSet<String> getNetheriteArmor() {
        return netheriteArmor;
    }

    public @NotNull HashSet<String> getNetheriteTools() {
        return netheriteTools;
    }


    public int getTier(@NotNull Material material) {
        return getTier(material.getKey().getKey());
    }

    public int getTier(@NotNull String id) {
        return tierValue.getOrDefault(id, 1); //1 for unknown items
    }

    private void addToHashSet(@NotNull String string, @NotNull HashSet<String> stringHashSet)
    {
        stringHashSet.add(string.toLowerCase(Locale.ENGLISH));
    }
}
