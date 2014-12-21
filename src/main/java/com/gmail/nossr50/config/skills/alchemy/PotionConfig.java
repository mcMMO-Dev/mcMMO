package com.gmail.nossr50.config.skills.alchemy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.ConfigLoader;
import com.gmail.nossr50.datatypes.skills.alchemy.AlchemyPotion;

public class PotionConfig extends ConfigLoader {
    private static PotionConfig instance;

    private List<ItemStack> concoctionsIngredientsTierOne = new ArrayList<ItemStack>();
    private List<ItemStack> concoctionsIngredientsTierTwo = new ArrayList<ItemStack>();
    private List<ItemStack> concoctionsIngredientsTierThree = new ArrayList<ItemStack>();
    private List<ItemStack> concoctionsIngredientsTierFour = new ArrayList<ItemStack>();
    private List<ItemStack> concoctionsIngredientsTierFive = new ArrayList<ItemStack>();
    private List<ItemStack> concoctionsIngredientsTierSix = new ArrayList<ItemStack>();
    private List<ItemStack> concoctionsIngredientsTierSeven = new ArrayList<ItemStack>();
    private List<ItemStack> concoctionsIngredientsTierEight = new ArrayList<ItemStack>();

    private Map<Short, AlchemyPotion> potionMap = new HashMap<Short, AlchemyPotion>();

    private PotionConfig() {
        super("potions.yml");
        loadKeys();
    }

    public static PotionConfig getInstance() {
        if (instance == null) {
            instance = new PotionConfig();
        }

        return instance;
    }

    @Override
    protected void loadKeys() {
        loadConcoctions();
        loadPotionMap();
    }

    private void loadConcoctions() {
        ConfigurationSection concoctionSection = config.getConfigurationSection("Concoctions");

        loadConcoctionsTier(concoctionsIngredientsTierOne, concoctionSection.getStringList("Tier_One_Ingredients"));
        loadConcoctionsTier(concoctionsIngredientsTierTwo, concoctionSection.getStringList("Tier_Two_Ingredients"));
        loadConcoctionsTier(concoctionsIngredientsTierThree, concoctionSection.getStringList("Tier_Three_Ingredients"));
        loadConcoctionsTier(concoctionsIngredientsTierFour, concoctionSection.getStringList("Tier_Four_Ingredients"));
        loadConcoctionsTier(concoctionsIngredientsTierFive, concoctionSection.getStringList("Tier_Five_Ingredients"));
        loadConcoctionsTier(concoctionsIngredientsTierSix, concoctionSection.getStringList("Tier_Six_Ingredients"));
        loadConcoctionsTier(concoctionsIngredientsTierSeven, concoctionSection.getStringList("Tier_Seven_Ingredients"));
        loadConcoctionsTier(concoctionsIngredientsTierEight, concoctionSection.getStringList("Tier_Eight_Ingredients"));

        concoctionsIngredientsTierTwo.addAll(concoctionsIngredientsTierOne);
        concoctionsIngredientsTierThree.addAll(concoctionsIngredientsTierTwo);
        concoctionsIngredientsTierFour.addAll(concoctionsIngredientsTierThree);
        concoctionsIngredientsTierFive.addAll(concoctionsIngredientsTierFour);
        concoctionsIngredientsTierSix.addAll(concoctionsIngredientsTierFive);
        concoctionsIngredientsTierSeven.addAll(concoctionsIngredientsTierSix);
        concoctionsIngredientsTierEight.addAll(concoctionsIngredientsTierSeven);
    }

    private void loadConcoctionsTier(List<ItemStack> ingredientList, List<String> ingredientStrings) {
        if (ingredientStrings != null && ingredientStrings.size() > 0) {
            for (String ingredientString : ingredientStrings) {
                ItemStack ingredient = loadIngredient(ingredientString);

                if (ingredient != null) {
                    ingredientList.add(ingredient);
                }
            }
        }
    }

    /**
     * Find the Potions configuration section and load all defined potions.
     */
    private void loadPotionMap() {
        ConfigurationSection potionSection = config.getConfigurationSection("Potions");
        int pass = 0;
        int fail = 0;

        for (String dataValue : potionSection.getKeys(false)) {
            AlchemyPotion potion = loadPotion(potionSection.getConfigurationSection(dataValue));

            if (potion != null) {
                potionMap.put(potion.getDataValue(), potion);
                pass++;
            }
            else {
                fail++;
            }
        }

        mcMMO.p.debug("Loaded " + pass + " Alchemy potions, skipped " + fail + ".");
    }

    /**
     * Parse a ConfigurationSection representing a AlchemyPotion.
     * Returns null if input cannot be parsed.
     *
     * @param potion_section ConfigurationSection to be parsed.
     *
     * @return Parsed AlchemyPotion.
     */
    private AlchemyPotion loadPotion(ConfigurationSection potion_section) {
        try {
            short dataValue = Short.parseShort(potion_section.getName());

            String name = potion_section.getString("Name");
            if (name != null) {
                name = ChatColor.translateAlternateColorCodes('&', name);
            }

            List<String> lore = new ArrayList<String>();
            if (potion_section.contains("Lore")) {
                for (String line : potion_section.getStringList("Lore")) {
                    lore.add(ChatColor.translateAlternateColorCodes('&', line));
                }
            }

            List<PotionEffect> effects = new ArrayList<PotionEffect>();
            if (potion_section.contains("Effects")) {
                for (String effect : potion_section.getStringList("Effects")) {
                    String[] parts = effect.split(" ");

                    PotionEffectType type = parts.length > 0 ? PotionEffectType.getByName(parts[0]) : null;
                    int amplifier = parts.length > 1 ? Integer.parseInt(parts[1]) : 0;
                    int duration = parts.length > 2 ? Integer.parseInt(parts[2]) : 0;

                    if (type != null) {
                        effects.add(new PotionEffect(type, duration, amplifier));
                    }
                    else {
                        mcMMO.p.getLogger().warning("Failed to parse effect for potion " + name + ": " + effect);
                    }
                }
            }

            Map<ItemStack, Short> children = new HashMap<ItemStack, Short>();
            if (potion_section.contains("Children")) {
                for (String child : potion_section.getConfigurationSection("Children").getKeys(false)) {
                    ItemStack ingredient = loadIngredient(child);
                    if (ingredient != null) {
                        children.put(ingredient, Short.parseShort(potion_section.getConfigurationSection("Children").getString(child)));
                    }
                    else {
                        mcMMO.p.getLogger().warning("Failed to parse child for potion " + name + ": " + child);
                    }
                }
            }

            return new AlchemyPotion(dataValue, name, lore, effects, children);
        }
        catch (Exception e) {
            mcMMO.p.getLogger().warning("Failed to load Alchemy potion: " + potion_section.getName());
            return null;
        }
    }

    /**
     * Parse a string representation of an ingredient.
     * Format: '&lt;MATERIAL&gt;[:data]'
     * Returns null if input cannot be parsed.
     *
     * @param ingredient String representing an ingredient.
     *
     * @return Parsed ingredient.
     */
    private ItemStack loadIngredient(String ingredient) {
        if (ingredient == null || ingredient.isEmpty()) {
            return null;
        }

        String[] parts = ingredient.split(":");

        Material material = parts.length > 0 ? Material.getMaterial(parts[0]) : null;
        short data = parts.length > 1 ? Short.parseShort(parts[1]) : 0;

        if (material != null) {
            return new ItemStack(material, 1, data);
        }

        return null;
    }

    public List<ItemStack> getIngredients(int tier) {
        switch (tier) {
            case 8:
                return concoctionsIngredientsTierEight;
            case 7:
                return concoctionsIngredientsTierSeven;
            case 6:
                return concoctionsIngredientsTierSix;
            case 5:
                return concoctionsIngredientsTierFive;
            case 4:
                return concoctionsIngredientsTierFour;
            case 3:
                return concoctionsIngredientsTierThree;
            case 2:
                return concoctionsIngredientsTierTwo;
            case 1:
            default:
                return concoctionsIngredientsTierOne;
        }
    }

    public boolean isValidPotion(ItemStack item) {
        return potionMap.containsKey(item.getDurability());
    }

    public AlchemyPotion getPotion(short durability) {
        return potionMap.get(durability);
    }
}
