package com.gmail.nossr50.config.skills.alchemy;

import com.gmail.nossr50.config.LegacyConfigLoader;
import com.gmail.nossr50.datatypes.skills.alchemy.AlchemyPotion;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.ItemUtils;
import com.gmail.nossr50.util.PotionUtil;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.codehaus.plexus.util.StringUtils;
import org.jetbrains.annotations.VisibleForTesting;

import java.io.File;
import java.util.*;

import static com.gmail.nossr50.util.PotionUtil.*;
import static com.gmail.nossr50.util.text.StringUtils.convertKeyToName;

public class PotionConfig extends LegacyConfigLoader {

    private final List<ItemStack> concoctionsIngredientsTierOne = new ArrayList<>();
    private final List<ItemStack> concoctionsIngredientsTierTwo = new ArrayList<>();
    private final List<ItemStack> concoctionsIngredientsTierThree = new ArrayList<>();
    private final List<ItemStack> concoctionsIngredientsTierFour = new ArrayList<>();
    private final List<ItemStack> concoctionsIngredientsTierFive = new ArrayList<>();
    private final List<ItemStack> concoctionsIngredientsTierSix = new ArrayList<>();
    private final List<ItemStack> concoctionsIngredientsTierSeven = new ArrayList<>();
    private final List<ItemStack> concoctionsIngredientsTierEight = new ArrayList<>();

    /**
     * Map of potion names to AlchemyPotion objects.
     */
    private final Map<String, AlchemyPotion> loadedPotions = new HashMap<>();

    public PotionConfig() {
        super("potions.yml");
    }

    @VisibleForTesting
    PotionConfig(File file) {
        super(file);
    }

    @Override
    protected void loadKeys() {
    }

    public void loadPotions() {
        loadConcoctions();
        loadPotionMap();
    }

    @VisibleForTesting
    void loadConcoctions() {
        final ConfigurationSection concoctionSection = config.getConfigurationSection("Concoctions");

        // Load the ingredients for each tier
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
        if (ingredientStrings != null && !ingredientStrings.isEmpty()) {
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
    int loadPotionMap() {
        ConfigurationSection potionSection = config.getConfigurationSection("Potions");
        int potionsLoaded = 0;
        int failures = 0;

        for (String potionName : potionSection.getKeys(false)) {
            AlchemyPotion potion = loadPotion(potionSection.getConfigurationSection(potionName));

            if (potion != null) {
                loadedPotions.put(potionName, potion);
                potionsLoaded++;
            } else {
                failures++;
            }
        }

        mcMMO.p.getLogger().info("Loaded " + potionsLoaded + " Alchemy potions, skipped " + failures + ".");
        return potionsLoaded;
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
            final String key = potion_section.getName();

            final ConfigurationSection potionData = potion_section.getConfigurationSection("PotionData");
            boolean extended = false;
            boolean upgraded = false;

            if (potionData != null) {
                extended = potionData.getBoolean("Extended", false);
                upgraded = potionData.getBoolean("Upgraded", false);
            }

            Material material;
            final String materialString = potion_section.getString("Material", null);
            if (materialString != null) {
                material = ItemUtils.exhaustiveMaterialLookup(materialString);
                if (material == null) {
                    mcMMO.p.getLogger().warning("PotionConfig: Failed to parse material for potion " + key + ": " + materialString);
                    mcMMO.p.getLogger().warning("PotionConfig: Defaulting to POTION");
                    material = Material.POTION;
                }
            } else {
                mcMMO.p.getLogger().warning("PotionConfig: Missing Material config entry for potion " + key + "," +
                        " from configuration section: " + potion_section + ", defaulting to POTION");
                material = Material.POTION;
            }

            final ItemStack itemStack = new ItemStack(material, 1);
            final PotionMeta potionMeta = (PotionMeta) itemStack.getItemMeta();

            if (potionMeta == null) {
                mcMMO.p.getLogger().severe("PotionConfig: Failed to get PotionMeta for " + key + ", from configuration section:" +
                        " " + potion_section);
                return null;
            }

            // extended and upgraded seem to be mutually exclusive
            if (extended && upgraded) {
                mcMMO.p.getLogger().warning("Potion " + key + " has both Extended and Upgraded set to true," +
                        " defaulting to Extended.");
                upgraded = false;
            }

            String potionTypeStr = potionData.getString("PotionType", null);
            if (potionTypeStr == null) {
                mcMMO.p.getLogger().severe("PotionConfig: Missing PotionType for " + key + ", from configuration section:" +
                        " " + potion_section);
                return null;
            }

            PotionType potionType = matchPotionType(potionTypeStr, upgraded, extended);
            if (potionType == null) {
                // try matching to key
                mcMMO.p.getLogger().warning("Failed to match potion type, trying to match with config key...");
                matchPotionType(key, upgraded, extended);
            }

            if (potionType == null) {
                mcMMO.p.getLogger().severe("PotionConfig: Failed to parse potion type for: " + potionTypeStr
                        + ", upgraded: " + upgraded + ", extended: " + extended + " for potion " + key
                        + ", from configuration section: " + potion_section);
                return null;
            }

            // Set base potion type
            // NOTE: extended/ignored are effectively ignored here on 1.20.5 and later
            PotionUtil.setBasePotionType(potionMeta, potionType, extended, upgraded);

//            // Use the name of the potion to indicate upgrade status if not set in PotionData
//            if(convertPotionConfigName(key).toUpperCase().contains("STRONG"))
//                upgraded = true;
//
//            if(convertPotionConfigName(key).toUpperCase().contains("LONG"))
//                extended = true;

            List<String> lore = new ArrayList<>();
            if (potion_section.contains("Lore")) {
                for (String line : potion_section.getStringList("Lore")) {
                    lore.add(ChatColor.translateAlternateColorCodes('&', line));
                }
            }
            potionMeta.setLore(lore);

            if (potion_section.contains("Effects")) {
                for (String effect : potion_section.getStringList("Effects")) {
                    String[] parts = effect.split(" ");

                    PotionEffectType type = parts.length > 0 ? PotionEffectType.getByName(parts[0]) : null;
                    int amplifier = parts.length > 1 ? Integer.parseInt(parts[1]) : 0;
                    int duration = parts.length > 2 ? Integer.parseInt(parts[2]) : 0;

                    if (type != null) {
                        potionMeta.addCustomEffect(new PotionEffect(type, duration, amplifier), true);
                    } else {
                        mcMMO.p.getLogger().severe("PotionConfig: Failed to parse effect for potion " + key + ": " + effect);
                    }
                }
            }

            Color color;
            if (potion_section.contains("Color")) {
                color = Color.fromRGB(potion_section.getInt("Color"));
            } else {
                color = this.generateColor(potionMeta.getCustomEffects());
            }
            potionMeta.setColor(color);

            final Map<ItemStack, String> children = new HashMap<>();
            if (potion_section.contains("Children")) {
                for (String child : potion_section.getConfigurationSection("Children").getKeys(false)) {
                    ItemStack ingredient = loadIngredient(child);
                    if (ingredient != null) {
                        children.put(ingredient, potion_section.getConfigurationSection("Children").getString(child));
                    } else {
                        mcMMO.p.getLogger().severe("PotionConfig: Failed to parse child for potion " + key + ": " + child);
                    }
                }
            }

            // Set the name of the potion
            setPotionDisplayName(potion_section, potionMeta);

            // TODO: Might not need to .setItemMeta
            itemStack.setItemMeta(potionMeta);
            return new AlchemyPotion(itemStack, children);
        } catch (Exception e) {
            mcMMO.p.getLogger().warning("PotionConfig: Failed to load Alchemy potion: " + potion_section.getName());
            return null;
        }
    }

    private void setPotionDisplayName(ConfigurationSection section, PotionMeta potionMeta) {
        String configuredName = section.getString("Name", null);
        if (configuredName != null) {
            potionMeta.setItemName(configuredName);
        }
//
//        // Potion is water, but has effects
//        if (isPotionTypeWater(potionMeta)
//                && (PotionUtil.hasBasePotionEffects(potionMeta) || !potionMeta.getCustomEffects().isEmpty())) {
//            // If we don't set a name for these potions, they will simply be called "Water Potion"
//            final String name = section.getName().toUpperCase().replace("_", " ");
//            potionMeta.setDisplayName(name);
//            System.out.println("DEBUG: Renaming potion to " + name);
//        }
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

    /**
     * Get the ingredients for the given tier.
     * @param tier Tier to get ingredients for.
     * @return List of ingredients for the given tier.
     */
    public List<ItemStack> getIngredients(int tier) {
        return switch (tier) {
            case 8 -> concoctionsIngredientsTierEight;
            case 7 -> concoctionsIngredientsTierSeven;
            case 6 -> concoctionsIngredientsTierSix;
            case 5 -> concoctionsIngredientsTierFive;
            case 4 -> concoctionsIngredientsTierFour;
            case 3 -> concoctionsIngredientsTierThree;
            case 2 -> concoctionsIngredientsTierTwo;
            default -> concoctionsIngredientsTierOne;
        };
    }

    /**
     * Check if the given ItemStack is a valid potion.
     * @param item ItemStack to be checked.
     * @return True if the given ItemStack is a valid potion, false otherwise.
     */
    public boolean isValidPotion(ItemStack item) {
        return getPotion(item) != null;
    }

    /**
     * Get the AlchemyPotion that corresponds to the given name.
     * @param name Name of the potion to be checked.
     * @return AlchemyPotion that corresponds to the given name.
     */
    public AlchemyPotion getPotion(String name) {
        return loadedPotions.get(name);
    }

    /**
     * Get the AlchemyPotion that corresponds to the given ItemStack.
     *
     * @param item ItemStack to be checked.
     *
     * @return AlchemyPotion that corresponds to the given ItemStack.
     */
    public AlchemyPotion getPotion(ItemStack item) {
        for (AlchemyPotion potion : loadedPotions.values()) {
            if (potion.isSimilarPotion(item)) {
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
