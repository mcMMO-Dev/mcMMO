package com.gmail.nossr50.config.skills.alchemy;

import com.gmail.nossr50.config.LegacyConfigLoader;
import com.gmail.nossr50.datatypes.skills.alchemy.AlchemyPotion;
import com.gmail.nossr50.mcMMO;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PotionConfig extends LegacyConfigLoader {
    private static PotionConfig instance;

    private final List<ItemStack> concoctionsIngredientsTierOne = new ArrayList<>();
    private final List<ItemStack> concoctionsIngredientsTierTwo = new ArrayList<>();
    private final List<ItemStack> concoctionsIngredientsTierThree = new ArrayList<>();
    private final List<ItemStack> concoctionsIngredientsTierFour = new ArrayList<>();
    private final List<ItemStack> concoctionsIngredientsTierFive = new ArrayList<>();
    private final List<ItemStack> concoctionsIngredientsTierSix = new ArrayList<>();
    private final List<ItemStack> concoctionsIngredientsTierSeven = new ArrayList<>();
    private final List<ItemStack> concoctionsIngredientsTierEight = new ArrayList<>();

    private final Map<String, AlchemyPotion> potionMap = new HashMap<>();

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

        for (String potionName : potionSection.getKeys(false)) {
            AlchemyPotion potion = loadPotion(potionSection.getConfigurationSection(potionName));

            if (potion != null) {
                potionMap.put(potionName, potion);
                pass++;
            } else {
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


            String name = potion_section.getString("Name");
            if (name != null) {
                name = ChatColor.translateAlternateColorCodes('&', name);
            }

            PotionData data;
            if (!potion_section.contains("PotionData")) { // Backwards config compatability
                short dataValue = Short.parseShort(potion_section.getName());
                Potion potion = Potion.fromDamage(dataValue);
                data = new PotionData(potion.getType(), potion.hasExtendedDuration(), potion.getLevel() == 2);
            } else {
                ConfigurationSection potionData = potion_section.getConfigurationSection("PotionData");
                data = new PotionData(PotionType.valueOf(potionData.getString("PotionType", "WATER")), potionData.getBoolean("Extended", false), potionData.getBoolean("Upgraded", false));
            }

            Material material = Material.POTION;
            String mat = potion_section.getString("Material", null);
            if (mat != null) {
                material = Material.valueOf(mat);
            }

            List<String> lore = new ArrayList<>();
            if (potion_section.contains("Lore")) {
                for (String line : potion_section.getStringList("Lore")) {
                    lore.add(ChatColor.translateAlternateColorCodes('&', line));
                }
            }

            List<PotionEffect> effects = new ArrayList<>();
            if (potion_section.contains("Effects")) {
                for (String effect : potion_section.getStringList("Effects")) {
                    String[] parts = effect.split(" ");

                    PotionEffectType type = parts.length > 0 ? PotionEffectType.getByName(parts[0]) : null;
                    int amplifier = parts.length > 1 ? Integer.parseInt(parts[1]) : 0;
                    int duration = parts.length > 2 ? Integer.parseInt(parts[2]) : 0;

                    if (type != null) {
                        effects.add(new PotionEffect(type, duration, amplifier));
                    } else {
                        mcMMO.p.getLogger().warning("Failed to parse effect for potion " + name + ": " + effect);
                    }
                }
            }

            Color color;
            if (potion_section.contains("Color")) {
                color = Color.fromRGB(potion_section.getInt("Color"));
            } else {
                color = this.generateColor(effects);
            }

            Map<ItemStack, String> children = new HashMap<>();
            if (potion_section.contains("Children")) {
                for (String child : potion_section.getConfigurationSection("Children").getKeys(false)) {
                    ItemStack ingredient = loadIngredient(child);
                    if (ingredient != null) {
                        children.put(ingredient, potion_section.getConfigurationSection("Children").getString(child));
                    } else {
                        mcMMO.p.getLogger().warning("Failed to parse child for potion " + name + ": " + child);
                    }
                }
            }

            return new AlchemyPotion(material, data, name, lore, effects, color, children);
        } catch (Exception e) {
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

        Material material = Material.getMaterial(ingredient);

        if (material != null) {
            return new ItemStack(material, 1);
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
        return getPotion(item) != null;
    }

    public AlchemyPotion getPotion(String name) {
        return potionMap.get(name);
    }

    public AlchemyPotion getPotion(ItemStack item) {
        for (AlchemyPotion potion : potionMap.values()) {
            if (potion.isSimilar(item)) {
                return potion;
            }
        }
        return null;
    }

    public Color generateColor(List<PotionEffect> effects) {
        if (effects != null && !effects.isEmpty()) {
            List<Color> colors = new ArrayList<>();
            for (PotionEffect effect : effects) {
                if (effect.getType().getColor() != null) {
                    colors.add(effect.getType().getColor());
                }
            }
            if (!colors.isEmpty()) {
                if (colors.size() > 1) {
                    return calculateAverageColor(colors);
                }
                return colors.get(0);
            }
        }
        return null;
    }

    public Color calculateAverageColor(List<Color> colors) {
        int red = 0;
        int green = 0;
        int blue = 0;
        for (Color color : colors) {
            red += color.getRed();
            green += color.getGreen();
            blue += color.getBlue();
        }
        return Color.fromRGB(red / colors.size(), green / colors.size(), blue / colors.size());
    }

}
