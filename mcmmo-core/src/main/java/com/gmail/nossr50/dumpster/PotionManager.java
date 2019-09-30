//package com.gmail.nossr50.config;
//
//import com.gmail.nossr50.datatypes.skills.alchemy.AlchemyPotion;
//import com.gmail.nossr50.skills.alchemy.PotionGenerator;
//import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
//import org.bukkit.Material;
//import org.bukkit.inventory.ItemStack;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
///**
// * Eventually I'm going to delete all of our Alchemy code and rewrite it from scratch
// */
//@ConfigSerializable
//public class PotionManager {
//
//    /* CONSTANTS */
//    public static final String POTIONS = "Potions";
//
//    /* INGREDIENTS */
//    private List<ItemStack> ingredientTierOne;
//    private List<ItemStack> ingredientTierTwo;
//    private List<ItemStack> ingredientTierThree;
//    private List<ItemStack> ingredientTierFour;
//    private List<ItemStack> ingredientTierFive;
//    private List<ItemStack> ingredientTierSix;
//    private List<ItemStack> ingredientTierSeven;
//    private List<ItemStack> ingredientTierEight;
//
//    private Map<String, AlchemyPotion> potionMap = new HashMap<>();
//
//    public PotionManager() {
//        initIngredientLists();
//        initPotionMap();
//    }
//
//    /**
//     * I just want anyone who reads this to know
//     * This entire class is an abomination
//     * What you see below is a hacky solution to keep Alchemy functioning with the new config system
//     * Alchemy will be rewritten, until then, this disgusting class exists.
//     */
//    private void initIngredientLists() {
//        ingredientTierOne = new ArrayList<>();
//        ingredientTierTwo = new ArrayList<>();
//        ingredientTierThree = new ArrayList<>();
//        ingredientTierFour = new ArrayList<>();
//        ingredientTierFive = new ArrayList<>();
//        ingredientTierSix = new ArrayList<>();
//        ingredientTierSeven = new ArrayList<>();
//        ingredientTierEight = new ArrayList<>();
//
//        ingredientTierOne.add(new ItemStack(Material.BLAZE_POWDER));
//        ingredientTierOne.add(new ItemStack(Material.FERMENTED_SPIDER_EYE));
//        ingredientTierOne.add(new ItemStack(Material.GHAST_TEAR));
//        ingredientTierOne.add(new ItemStack(Material.GLOWSTONE_DUST));
//        ingredientTierOne.add(new ItemStack(Material.GOLDEN_CARROT));
//        ingredientTierOne.add(new ItemStack(Material.MAGMA_CREAM));
//        ingredientTierOne.add(new ItemStack(Material.NETHER_WART));
//        ingredientTierOne.add(new ItemStack(Material.REDSTONE));
//        ingredientTierOne.add(new ItemStack(Material.GLISTERING_MELON_SLICE));
//        ingredientTierOne.add(new ItemStack(Material.SPIDER_EYE));
//        ingredientTierOne.add(new ItemStack(Material.SUGAR));
//        ingredientTierOne.add(new ItemStack(Material.GUNPOWDER));
//        ingredientTierOne.add(new ItemStack(Material.PUFFERFISH));
//        ingredientTierOne.add(new ItemStack(Material.DRAGON_BREATH));
//
//        ingredientTierTwo.add(new ItemStack(Material.CARROT));
//        ingredientTierTwo.add(new ItemStack(Material.SLIME_BALL));
//        ingredientTierTwo.add(new ItemStack(Material.PHANTOM_MEMBRANE));
//
//        ingredientTierThree.add(new ItemStack(Material.QUARTZ));
//        ingredientTierThree.add(new ItemStack(Material.RED_MUSHROOM));
//
//        ingredientTierFour.add(new ItemStack(Material.APPLE));
//        ingredientTierFour.add(new ItemStack(Material.ROTTEN_FLESH));
//
//        ingredientTierFive.add(new ItemStack(Material.BROWN_MUSHROOM));
//        ingredientTierFive.add(new ItemStack(Material.INK_SAC));
//
//        ingredientTierSix.add(new ItemStack(Material.FERN));
//
//        ingredientTierSeven.add(new ItemStack(Material.POISONOUS_POTATO));
//
//        ingredientTierEight.add(new ItemStack(Material.GOLDEN_APPLE));
//    }
//
//    private void loadConcoctionsTier(List<ItemStack> ingredientList, List<String> ingredients) {
//        if (ingredients != null && ingredients.size() > 0) {
//            for (String ingredientString : ingredients) {
//                ItemStack ingredient = loadIngredient(ingredientString);
//
//                if (ingredient != null) {
//                    ingredientList.add(ingredient);
//                }
//            }
//        }
//    }
//
//    private void initPotionMap() {
//        PotionGenerator potionGenerator = new PotionGenerator();
//        potionMap = potionMap
//    }
//
//    /**
//     * Parse a string representation of an ingredient.
//     * Format: '&lt;MATERIAL&gt;[:data]'
//     * Returns null if input cannot be parsed.
//     *
//     * @param ingredient String representing an ingredient.
//     * @return Parsed ingredient.
//     */
//    private ItemStack loadIngredient(String ingredient) {
//        if (ingredient == null || ingredient.isEmpty()) {
//            return null;
//        }
//
//        Material material = Material.getMaterial(ingredient);
//
//        if (material != null) {
//            return new ItemStack(material, 1);
//        }
//
//        return null;
//    }
//
//    public List<ItemStack> getIngredients(int tier) {
//        switch (tier) {
//            case 8:
//                return ingredientTierEight;
//            case 7:
//                return ingredientTierSeven;
//            case 6:
//                return ingredientTierSix;
//            case 5:
//                return ingredientTierFive;
//            case 4:
//                return ingredientTierFour;
//            case 3:
//                return ingredientTierThree;
//            case 2:
//                return ingredientTierTwo;
//            case 1:
//            default:
//                return ingredientTierOne;
//        }
//    }
//
//    public boolean isValidPotion(ItemStack item) {
//        return getPotion(item) != null;
//    }
//
//    public AlchemyPotion getPotion(String name) {
//        return potionMap.get(name);
//    }
//
//    public AlchemyPotion getPotion(ItemStack item) {
//        for (AlchemyPotion potion : potionMap.values()) {
//            if (potion.isSimilar(item)) {
//                return potion;
//            }
//        }
//        return null;
//    }
//
//
//}
