package com.gmail.nossr50.util;

import static java.util.Objects.requireNonNull;

import com.gmail.nossr50.api.ItemSpawnReason;
import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.config.party.ItemWeightConfig;
import com.gmail.nossr50.datatypes.treasure.EnchantmentWrapper;
import com.gmail.nossr50.datatypes.treasure.FishingTreasureBook;
import com.gmail.nossr50.events.items.McMMOItemSpawnEvent;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.skills.smelting.Smelting;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ItemUtils {
    // Use custom name if available
    private static final Method customName;

    static {
        customName = getCustomNameMethod();
    }

    private ItemUtils() {
        // private constructor
    }

    private static Method getCustomNameMethod() {
        try {
            return ItemMeta.class.getMethod("customName", Component.class);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    /**
     * Sets the item name using the new API if available or falls back to the old API.
     *
     * @param itemMeta The item meta to set the name on
     * @param name The name to set
     */
    public static void customName(ItemMeta itemMeta, Component name, String fallbackName) {
        if (customName != null) {
            setItemNameModern(itemMeta, name);
        } else {
            itemMeta.setDisplayName(ChatColor.RESET + fallbackName);
        }
    }

    private static void setItemNameModern(ItemMeta itemMeta, Component name) {
        try {
            customName.invoke(itemMeta, name);
        } catch (IllegalAccessException | InvocationTargetException e) {
            mcMMO.p.getLogger().severe("Failed to set item name: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * Checks if the item is a bow.
     *
     * @param item Item to check
     * @return true if the item is a bow, false otherwise
     */
    // TODO: Unit tests
    public static boolean isBow(@NotNull ItemStack item) {
        return mcMMO.getMaterialMapStore().isBow(item.getType().getKey().getKey());
    }

    /**
     * Exhaustive lookup for a Material by name.
     * <p>
     * This method will first try a normal lookup, then a legacy lookup, then a lookup by ENUM name,
     * and finally a lookup by ENUM name with legacy name.
     *
     * @param materialName The name of the material to lookup
     * @return The Material if found, or null if not found
     */
    public static @Nullable Material exhaustiveMaterialLookup(@NotNull String materialName) {
        requireNonNull(materialName, "materialName cannot be null");

        // First try a normal lookup
        Material material = Material.matchMaterial(materialName);

        // If that fails, try a legacy lookup
        if (material == null) {
            material = Material.matchMaterial(materialName, true);
        }

        // try to match to Material ENUM
        if (material == null) {
            material = Material.getMaterial(materialName.toUpperCase(Locale.ENGLISH));
        }

        // try to match to Material ENUM with legacy name
        if (material == null) {
            material = Material.getMaterial(materialName.toUpperCase(Locale.ENGLISH), true);
        }
        return material;
    }

    /**
     * Checks if a player has an item in their inventory or offhand.
     *
     * @param player Player to check
     * @param material Material to check for
     * @return true if the player has the item in their inventory or offhand, false otherwise
     */
    public static boolean hasItemIncludingOffHand(Player player, Material material) {
        // Checks main inventory / item bar
        boolean containsInMain = player.getInventory().contains(material);

        if (containsInMain) {
            return true;
        }

        return player.getInventory().getItemInOffHand().getType() == material;
    }

    /**
     * Removes an item from a player's inventory, including their offhand.
     *
     * @param player Player to remove the item from
     * @param material Material to remove
     * @param amount Amount of the material to remove
     */
    public static void removeItemIncludingOffHand(@NotNull Player player,
            @NotNull Material material, int amount) {
        // Checks main inventory / item bar
        if (player.getInventory().contains(material)) {
            player.getInventory().removeItem(new ItemStack(material, amount));
            return;
        }

        // Check off-hand
        final ItemStack offHandItem = player.getInventory().getItemInOffHand();
        if (offHandItem.getType() == material) {
            int newAmount = offHandItem.getAmount() - amount;
            if (newAmount > 0) {
                offHandItem.setAmount(newAmount);
            } else {
                player.getInventory().setItemInOffHand(new ItemStack(Material.AIR));
            }
        }
    }

    public static boolean hasItemInEitherHand(@NotNull Player player, Material material) {
        return player.getInventory().getItemInMainHand().getType() == material
                || player.getInventory().getItemInOffHand().getType() == material;
    }

    public static boolean doesPlayerHaveEnchantmentOnArmor(@NotNull Player player,
            @NotNull String enchantmentByName) {
        Enchantment enchantment = getEnchantment(enchantmentByName);

        if (enchantment == null) {
            return false;
        }

        return doesPlayerHaveEnchantmentOnArmor(player, enchantment);
    }

    public static boolean doesPlayerHaveEnchantmentOnArmor(@NotNull Player player,
            @NotNull Enchantment enchantment) {
        for (ItemStack itemStack : player.getInventory().getArmorContents()) {
            if (itemStack != null) {
                if (hasEnchantment(itemStack, enchantment)) {
                    return true;
                }
            }
        }

        return false;
    }

    public static boolean doesPlayerHaveEnchantmentOnArmorOrHands(@NotNull Player player,
            @NotNull String enchantmentName) {
        Enchantment enchantment = getEnchantment(enchantmentName);

        if (enchantment == null) {
            return false;
        }

        return doesPlayerHaveEnchantmentOnArmorOrHands(player, enchantment);
    }

    public static boolean doesPlayerHaveEnchantmentOnArmorOrHands(@NotNull Player player,
            @NotNull Enchantment enchantment) {
        if (doesPlayerHaveEnchantmentOnArmor(player, enchantment)) {
            return true;
        }

        return doesPlayerHaveEnchantmentInHands(player, enchantment);
    }

    public static boolean doesPlayerHaveEnchantmentInHands(@NotNull Player player,
            @NotNull NamespacedKey enchantmentNameKey) {
        Enchantment enchantment = Enchantment.getByKey(enchantmentNameKey);

        if (enchantment == null) {
            return false;
        }

        return doesPlayerHaveEnchantmentInHands(player, enchantment);
    }

    public static boolean doesPlayerHaveEnchantmentInHands(@NotNull Player player,
            @NotNull String enchantmentName) {
        Enchantment enchantment = getEnchantment(enchantmentName);

        if (enchantment == null) {
            return false;
        }

        return doesPlayerHaveEnchantmentInHands(player, enchantment);
    }

    public static boolean doesPlayerHaveEnchantmentInHands(@NotNull Player player,
            @NotNull Enchantment enchantment) {
        return hasEnchantment(player.getInventory().getItemInMainHand(), enchantment) ||
                hasEnchantment(player.getInventory().getItemInOffHand(), enchantment);
    }

    public static boolean hasEnchantment(@NotNull ItemStack itemStack,
            @NotNull Enchantment enchantment) {
        if (itemStack.getItemMeta() != null) {
            return itemStack.getItemMeta().hasEnchant(enchantment);
        }

        return false;
    }

    public static @Nullable Enchantment getEnchantment(@NotNull String enchantmentName) {
        for (Enchantment enchantment : Enchantment.values()) {
            if (enchantment.getKey().getKey().equalsIgnoreCase(enchantmentName)) {
                return enchantment;
            }
        }

        return null;
    }

    /**
     * Checks if the item is a crossbow.
     *
     * @param item Item to check
     * @return true if the item is a crossbow, false otherwise
     */
    public static boolean isCrossbow(@NotNull ItemStack item) {
        return mcMMO.getMaterialMapStore().isCrossbow(item.getType().getKey().getKey());
    }

    /**
     * Checks if the item is a trident.
     *
     * @param item Item to check
     * @return true if the item is a trident, false otherwise
     */
    public static boolean isTrident(@NotNull ItemStack item) {
        return mcMMO.getMaterialMapStore().isTrident(item.getType().getKey().getKey());
    }

    /**
     * Checks if the item is a mace.
     *
     * @param item Item to check
     * @return true if the item is a mace, false otherwise
     */
    public static boolean isMace(@NotNull ItemStack item) {
        return mcMMO.getMaterialMapStore().isMace(item.getType().getKey().getKey());
    }

    /**
     * Checks if the item is a spear.
     * @param item Item to check
     *
     * @return true if the item is a spear, false otherwise
     */
    public static boolean isSpear(@NotNull ItemStack item) {
        return mcMMO.getMaterialMapStore().isSpear(item.getType().getKey().getKey());
    }

    /**
     * Checks if the item is a sword.
     *
     * @param item Item to check
     * @return true if the item is a sword, false otherwise
     */
    public static boolean isSword(@NotNull ItemStack item) {
        return mcMMO.getMaterialMapStore().isSword(item.getType().getKey().getKey());
    }

    /**
     * Checks if the item is a hoe.
     *
     * @param item Item to check
     * @return true if the item is a hoe, false otherwise
     */
    public static boolean isHoe(@NotNull ItemStack item) {
        return mcMMO.getMaterialMapStore().isHoe(item.getType().getKey().getKey());
    }

    /**
     * Checks if the item is a shovel.
     *
     * @param item Item to check
     * @return true if the item is a shovel, false otherwise
     */
    public static boolean isShovel(@NotNull ItemStack item) {
        return mcMMO.getMaterialMapStore().isShovel(item.getType().getKey().getKey());
    }

    /**
     * Checks if the item is an axe.
     *
     * @param item Item to check
     * @return true if the item is an axe, false otherwise
     */
    public static boolean isAxe(@NotNull ItemStack item) {
        return mcMMO.getMaterialMapStore().isAxe(item.getType().getKey().getKey());
    }

    /**
     * Checks if the item is a pickaxe.
     *
     * @param item Item to check
     * @return true if the item is a pickaxe, false otherwise
     */
    public static boolean isPickaxe(@NotNull ItemStack item) {
        return mcMMO.getMaterialMapStore().isPickAxe(item.getType().getKey().getKey());
    }

    /**
     * Checks if the item counts as unarmed.
     *
     * @param item Item to check
     * @return true if the item counts as unarmed, false otherwise
     */
    public static boolean isUnarmed(ItemStack item) {
        if (mcMMO.p.getGeneralConfig().getUnarmedItemsAsUnarmed()) {
            return !isMinecraftTool(item);
        }

        return item.getType() == Material.AIR;
    }

    /**
     * Checks to see if an item is a wearable armor piece.
     *
     * @param item Item to check
     * @return true if the item is armor, false otherwise
     */
    public static boolean isArmor(ItemStack item) {
        return mcMMO.getMaterialMapStore().isArmor(item.getType());
    }

    /**
     * Checks to see if an item is a leather armor piece.
     *
     * @param item Item to check
     * @return true if the item is leather armor, false otherwise
     */
    public static boolean isLeatherArmor(ItemStack item) {
        return mcMMO.getMaterialMapStore().isLeatherArmor(item.getType());
    }

    /**
     * Checks to see if an item is a gold armor piece.
     *
     * @param item Item to check
     * @return true if the item is gold armor, false otherwise
     */
    public static boolean isGoldArmor(ItemStack item) {
        return mcMMO.getMaterialMapStore().isGoldArmor(item.getType().getKey().getKey());
    }

    /**
     * Checks to see if an item is an iron armor piece.
     *
     * @param item Item to check
     * @return true if the item is iron armor, false otherwise
     */
    public static boolean isIronArmor(ItemStack item) {
        return mcMMO.getMaterialMapStore().isIronArmor(item.getType().getKey().getKey());
    }

    public static boolean isCopperArmor(ItemStack item) {
        return mcMMO.getMaterialMapStore().isCopperArmor(item.getType().getKey().getKey());
    }

    /**
     * Checks to see if an item is a diamond armor piece.
     *
     * @param item Item to check
     * @return true if the item is diamond armor, false otherwise
     */
    public static boolean isDiamondArmor(ItemStack item) {
        return mcMMO.getMaterialMapStore().isDiamondArmor(item.getType().getKey().getKey());
    }

    public static boolean isNetheriteArmor(ItemStack itemStack) {
        return mcMMO.getMaterialMapStore().isNetheriteArmor(itemStack.getType().getKey().getKey());
    }

    public static boolean isNetheriteTool(ItemStack itemStack) {
        return mcMMO.getMaterialMapStore().isNetheriteTool(itemStack.getType().getKey().getKey());
    }

    /**
     * Checks to see if an item is a chainmail armor piece.
     *
     * @param item Item to check
     * @return true if the item is chainmail armor, false otherwise
     */
    public static boolean isChainmailArmor(ItemStack item) {
        return mcMMO.getMaterialMapStore().isChainmailArmor(item.getType().getKey().getKey());
    }

    /**
     * Checks to see if an item is a *vanilla* tool.
     *
     * @param item Item to check
     * @return true if the item is a tool, false otherwise
     */
    public static boolean isMinecraftTool(ItemStack item) {
        return mcMMO.getMaterialMapStore().isTool(item.getType().getKey().getKey());
    }

    /**
     * Checks to see if an item is a stone tool.
     *
     * @param item Item to check
     * @return true if the item is a stone tool, false otherwise
     */
    public static boolean isStoneTool(ItemStack item) {
        return mcMMO.getMaterialMapStore().isStoneTool(item.getType().getKey().getKey());
    }

    /**
     * Checks to see if an item is a wooden tool.
     *
     * @param item Item to check
     * @return true if the item is a wooden tool, false otherwise
     */
    public static boolean isWoodTool(ItemStack item) {
        return mcMMO.getMaterialMapStore().isWoodTool(item.getType().getKey().getKey());
    }

    /**
     * Checks to see if an item is a string tool.
     *
     * @param item Item to check
     * @return true if the item is a string tool, false otherwise
     */
    public static boolean isStringTool(ItemStack item) {
        return mcMMO.getMaterialMapStore().isStringTool(item.getType().getKey().getKey());
    }

    public static boolean isPrismarineTool(ItemStack item) {
        return mcMMO.getMaterialMapStore().isPrismarineTool(item.getType().getKey().getKey());
    }

    public static boolean isCopperTool(ItemStack item) {
        return mcMMO.getMaterialMapStore().isCopperTool(item.getType().getKey().getKey());
    }

    /**
     * Checks to see if an item is a gold tool.
     *
     * @param item Item to check
     * @return true if the item is a stone tool, false otherwise
     */
    public static boolean isGoldTool(ItemStack item) {
        return mcMMO.getMaterialMapStore().isGoldTool(item.getType().getKey().getKey());
    }

    /**
     * Checks to see if an item is an iron tool.
     *
     * @param item Item to check
     * @return true if the item is an iron tool, false otherwise
     */
    public static boolean isIronTool(ItemStack item) {
        return mcMMO.getMaterialMapStore().isIronTool(item.getType().getKey().getKey());
    }

    /**
     * Checks to see if an item is a diamond tool.
     *
     * @param item Item to check
     * @return true if the item is a diamond tool, false otherwise
     */
    public static boolean isDiamondTool(ItemStack item) {
        return mcMMO.getMaterialMapStore().isDiamondTool(item.getType().getKey().getKey());
    }

    /**
     * Checks to see if an item is enchantable.
     *
     * @param item Item to check
     * @return true if the item is enchantable, false otherwise
     */
    public static boolean isEnchantable(ItemStack item) {
        return mcMMO.getMaterialMapStore().isEnchantable(item.getType().getKey().getKey());
    }

    public static boolean isSmeltable(ItemStack item) {
        return item != null && Smelting.getSmeltXP(item) >= 1;
    }

    public static boolean isSmelted(ItemStack item) {
        if (item == null) {
            return false;
        }

        for (Recipe recipe : mcMMO.p.getServer().getRecipesFor(item)) {
            if (recipe instanceof FurnaceRecipe
                    && ((FurnaceRecipe) recipe).getInput().getType().isBlock()
                    && MaterialUtils.isOre(((FurnaceRecipe) recipe).getInput().getType())) {
                return true;
            }
        }

        return false;
    }

    /**
     * Check if an item is sharable.
     *
     * @param item Item that will get shared
     * @return True if the item can be shared.
     */
    public static boolean isSharable(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) {
            return false;
        }

        return isMiningDrop(item)
                || isWoodcuttingDrop(item)
                || isMobDrop(item)
                || isHerbalismDrop(item)
                || isMiscDrop(item);
    }

    /**
     * Checks to see if an item is a mining drop.
     *
     * @param item Item to check
     * @return true if the item is a mining drop, false otherwise
     */
    public static boolean isMiningDrop(ItemStack item) {
        //TODO: 1.14 This needs to be updated
        return switch (item.getType()) { // Should we also have Glowing Redstone Ore here?
            // Should we also have Glowstone here?
            case COAL, COAL_ORE, DIAMOND, DIAMOND_ORE, EMERALD, EMERALD_ORE, GOLD_ORE, IRON_ORE,
                 LAPIS_ORE,
                 REDSTONE_ORE, REDSTONE, GLOWSTONE_DUST, QUARTZ, NETHER_QUARTZ_ORE, LAPIS_LAZULI ->
                    true;
            default -> false;
        };
    }

    /**
     * Checks to see if an item is a herbalism drop.
     *
     * @param item Item to check
     * @return true if the item is a herbalism drop, false otherwise
     */
    public static boolean isHerbalismDrop(ItemStack item) {
        //TODO: 1.14 This needs to be updated
        return switch (item.getType().getKey().getKey().toLowerCase()) {
            case "wheat", "wheat_seeds", "carrot", "chorus_fruit", "chorus_flower", "potato",
                 "beetroot", "beetroots",
                 "beetroot_seeds", "nether_wart", "brown_mushroom", "red_mushroom", "rose_bush",
                 "dandelion", "cactus",
                 "sugar_cane", "melon", "melon_seeds", "pumpkin", "pumpkin_seeds", "lily_pad",
                 "vine", "tall_grass",
                 "cocoa_beans" -> true;
            default -> false;
        };
    }


    /**
     * Checks to see if an item is a mob drop.
     *
     * @param item Item to check
     * @return true if the item is a mob drop, false otherwise
     */
    public static boolean isMobDrop(ItemStack item) {
        //TODO: 1.14 This needs to be updated
        return switch (item.getType()) {
            case STRING, FEATHER, CHICKEN, COOKED_CHICKEN, LEATHER, BEEF, COOKED_BEEF, PORKCHOP,
                 COOKED_PORKCHOP,
                 WHITE_WOOL, BLACK_WOOL, BLUE_WOOL, BROWN_WOOL, CYAN_WOOL, GRAY_WOOL, GREEN_WOOL,
                 LIGHT_BLUE_WOOL,
                 LIGHT_GRAY_WOOL, LIME_WOOL, MAGENTA_WOOL, ORANGE_WOOL, PINK_WOOL, PURPLE_WOOL,
                 RED_WOOL, YELLOW_WOOL,
                 IRON_INGOT, SNOWBALL, BLAZE_ROD, SPIDER_EYE, GUNPOWDER, ENDER_PEARL, GHAST_TEAR,
                 MAGMA_CREAM, BONE,
                 ARROW, SLIME_BALL, NETHER_STAR, ROTTEN_FLESH, GOLD_NUGGET, EGG, ROSE_BUSH, COAL ->
                    true;
            default -> false;
        };
    }

    /**
     * Checks to see if an item is a woodcutting drop.
     *
     * @param item Item to check
     * @return true if the item is a woodcutting drop, false otherwise
     */
    public static boolean isWoodcuttingDrop(ItemStack item) {
        return switch (item.getType().toString()) {
            case "ACACIA_LOG", "BIRCH_LOG", "DARK_OAK_LOG", "PALE_OAK_LOG", "JUNGLE_LOG", "OAK_LOG",
                 "SPRUCE_LOG",
                 "STRIPPED_ACACIA_LOG", "STRIPPED_BIRCH_LOG", "STRIPPED_DARK_OAK_LOG",
                 "STRIPPED_PALE_OAK_LOG",
                 "STRIPPED_JUNGLE_LOG", "STRIPPED_OAK_LOG", "STRIPPED_SPRUCE_LOG",
                 "STRIPPED_MANGROVE_LOG",
                 "ACACIA_SAPLING", "SPRUCE_SAPLING", "BIRCH_SAPLING", "DARK_OAK_SAPLING",
                 "PALE_OAK_SAPLING",
                 "JUNGLE_SAPLING", "OAK_SAPLING", "ACACIA_LEAVES", "BIRCH_LEAVES",
                 "DARK_OAK_LEAVES", "PALE_OAK_LEAVES",
                 "JUNGLE_LEAVES", "OAK_LEAVES", "SPRUCE_LEAVES", "BEE_NEST", "APPLE" -> true;
            default -> false;
        };
    }

    /**
     * Checks to see if an item is a miscellaneous drop. These items are read from the config file
     *
     * @param item Item to check
     * @return true if the item is a miscellaneous drop, false otherwise
     */
    public static boolean isMiscDrop(ItemStack item) {
        return ItemWeightConfig.getInstance().getMiscItems().contains(item.getType());
    }

    // TODO: This is used exclusively for Chimaera Wing... should revisit this sometime
    public static boolean isMcMMOItem(ItemStack item) {
        if (!item.hasItemMeta()) {
            return false;
        }

        ItemMeta itemMeta = item.getItemMeta();

        if (itemMeta == null) {
            return false;
        }

        return itemMeta.getLore() != null
                && itemMeta.getLore().contains("mcMMO Item");
    }

    public static boolean isChimaeraWing(ItemStack item) {
        if (!isMcMMOItem(item)) {
            return false;
        }

        ItemMeta itemMeta = item.getItemMeta();

        if (itemMeta == null) {
            return false;
        }

        return itemMeta.hasDisplayName() && itemMeta.getDisplayName()
                .equals(ChatColor.GOLD + LocaleLoader.getString("Item.ChimaeraWing.Name"));
    }

    public static void removeAbilityLore(@NotNull ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();

        if (itemMeta == null) {
            return;
        }

        if (itemMeta.hasLore()) {
            List<String> itemLore = itemMeta.getLore();

            if (itemLore == null) {
                return;
            }

            if (itemLore.remove("mcMMO Ability Tool")) {
                itemMeta.setLore(itemLore);
                itemStack.setItemMeta(itemMeta);
            }
        }
    }

    public static void addDigSpeedToItem(@NotNull ItemStack itemStack,
            int existingEnchantLevel) {
        ItemMeta itemMeta = itemStack.getItemMeta();

        if (itemMeta == null) {
            return;
        }

        itemMeta.addEnchant(mcMMO.p.getEnchantmentMapper().getEfficiency(),
                existingEnchantLevel + mcMMO.p.getAdvancedConfig().getEnchantBuff(), true);
        itemStack.setItemMeta(itemMeta);
    }

    public static boolean canBeSuperAbilityDigBoosted(@NotNull ItemStack itemStack) {
        return isShovel(itemStack) || isPickaxe(itemStack);
    }

    public static @NotNull ItemStack createEnchantBook(
            @NotNull FishingTreasureBook fishingTreasureBook) {
        ItemStack itemStack = fishingTreasureBook.getDrop().clone();
        EnchantmentWrapper enchantmentWrapper = getRandomEnchantment(
                fishingTreasureBook.getLegalEnchantments());
        ItemMeta itemMeta = itemStack.getItemMeta();

        if (itemMeta == null) {
            return itemStack;
        }

        EnchantmentStorageMeta enchantmentStorageMeta = (EnchantmentStorageMeta) itemMeta;
        enchantmentStorageMeta.addStoredEnchant(
                enchantmentWrapper.getEnchantment(),
                enchantmentWrapper.getEnchantmentLevel(),
                ExperienceConfig.getInstance().allowUnsafeEnchantments());
        itemStack.setItemMeta(enchantmentStorageMeta);
        return itemStack;
    }

    public static @NotNull EnchantmentWrapper getRandomEnchantment(
            @NotNull List<EnchantmentWrapper> enchantmentWrappers) {
        Collections.shuffle(enchantmentWrappers, Misc.getRandom());

        int randomIndex = Misc.getRandom().nextInt(enchantmentWrappers.size());
        return enchantmentWrappers.get(randomIndex);
    }

    /**
     * Drop items at a given location.
     *
     * @param location The location to drop the items at
     * @param itemStacks The items to drop
     */
    public static void spawnItems(@Nullable Player player,
            @NotNull Location location,
            @NotNull Collection<ItemStack> itemStacks,
            @NotNull ItemSpawnReason itemSpawnReason) {
        for (ItemStack is : itemStacks) {
            spawnItem(player, location, is, itemSpawnReason);
        }
    }

    /**
     * Drop items at a given location.
     *
     * @param player player to drop the items for
     * @param location The location to drop the items at
     * @param itemStacks The items to drop
     * @param blackList The items to skip
     * @param itemSpawnReason the reason for the item drop
     */
    public static void spawnItems(@Nullable Player player,
            @NotNull Location location,
            @NotNull Collection<ItemStack> itemStacks,
            @NotNull Collection<Material> blackList,
            @NotNull ItemSpawnReason itemSpawnReason) {
        for (ItemStack is : itemStacks) {
            // Skip blacklisted items
            if (blackList.contains(is.getType())) {
                continue;
            }
            spawnItem(player, location, is, itemSpawnReason);
        }
    }

    /**
     * Drop items at a given location.
     *
     * @param location The location to drop the items at
     * @param is The items to drop
     * @param quantity The amount of items to drop
     */
    public static void spawnItems(@Nullable Player player,
            @NotNull Location location,
            @NotNull ItemStack is,
            int quantity,
            @NotNull ItemSpawnReason itemSpawnReason) {
        for (int i = 0; i < quantity; i++) {
            spawnItem(player, location, is, itemSpawnReason);
        }
    }

    /**
     * Drop an item at a given location.
     *
     * @param location The location to drop the item at
     * @param itemStack The item to drop
     * @param itemSpawnReason the reason for the item drop
     * @return Dropped Item entity or null if invalid or cancelled
     */
    public static @Nullable Item spawnItem(@Nullable Player player,
            @NotNull Location location,
            @NotNull ItemStack itemStack,
            @NotNull ItemSpawnReason itemSpawnReason) {
        if (itemStack.getType() == Material.AIR || location.getWorld() == null) {
            return null;
        }

        // We can't get the item until we spawn it and we want to make it cancellable, so we have a custom event.
        final McMMOItemSpawnEvent event = new McMMOItemSpawnEvent(location, itemStack,
                itemSpawnReason, player);
        mcMMO.p.getServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return null;
        }

        return location.getWorld().dropItem(location, event.getItemStack());
    }

    /**
     * Drop an item at a given location.
     *
     * @param location The location to drop the item at
     * @param itemStack The item to drop
     * @param itemSpawnReason the reason for the item drop
     * @return Dropped Item entity or null if invalid or cancelled
     */
    public static @Nullable Item spawnItemNaturally(@Nullable Player player,
            @NotNull Location location,
            @NotNull ItemStack itemStack,
            @NotNull ItemSpawnReason itemSpawnReason) {
        if (itemStack.getType() == Material.AIR || location.getWorld() == null) {
            return null;
        }

        // We can't get the item until we spawn it and we want to make it cancellable, so we have a custom event.
        final McMMOItemSpawnEvent event = new McMMOItemSpawnEvent(location, itemStack,
                itemSpawnReason, player);
        mcMMO.p.getServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return null;
        }

        return location.getWorld().dropItemNaturally(location, event.getItemStack());
    }

    /**
     * Drop items at a given location.
     *
     * @param fromLocation The location to drop the items at
     * @param is The items to drop
     * @param speed the speed that the item should travel
     * @param quantity The amount of items to drop
     */
    public static void spawnItemsTowardsLocation(@Nullable Player player,
            @NotNull Location fromLocation,
            @NotNull Location toLocation,
            @NotNull ItemStack is,
            int quantity,
            double speed,
            @NotNull ItemSpawnReason itemSpawnReason) {
        for (int i = 0; i < quantity; i++) {
            spawnItemTowardsLocation(player, fromLocation, toLocation, is, speed, itemSpawnReason);
        }
    }

    /**
     * Drop an item at a given location. This method is fairly expensive as it creates clones of
     * everything passed to itself since they are mutable objects
     *
     * @param fromLocation The location to drop the item at
     * @param toLocation The location the item will travel towards
     * @param itemToSpawn The item to spawn
     * @param speed the speed that the item should travel
     * @return Dropped Item entity or null if invalid or cancelled
     */
    public static @Nullable Item spawnItemTowardsLocation(@Nullable Player player,
            @NotNull Location fromLocation,
            @NotNull Location toLocation,
            @NotNull ItemStack itemToSpawn,
            double speed,
            @NotNull ItemSpawnReason itemSpawnReason) {
        if (itemToSpawn.getType() == Material.AIR) {
            return null;
        }

        //Work with fresh copies of everything
        ItemStack clonedItem = itemToSpawn.clone();
        Location spawnLocation = fromLocation.clone();
        Location targetLocation = toLocation.clone();

        if (spawnLocation.getWorld() == null) {
            return null;
        }

        // We can't get the item until we spawn it and we want to make it cancellable, so we have a custom event.
        McMMOItemSpawnEvent event = new McMMOItemSpawnEvent(spawnLocation, clonedItem,
                itemSpawnReason, player);
        mcMMO.p.getServer().getPluginManager().callEvent(event);
        clonedItem = event.getItemStack();

        //Something cancelled the event so back out
        if (event.isCancelled()) {
            return null;
        }

        //Use the item from the event
        Item spawnedItem = spawnLocation.getWorld().dropItem(spawnLocation, clonedItem);
        Vector vecFrom = spawnLocation.clone().toVector().clone();
        Vector vecTo = targetLocation.clone().toVector().clone();

        //Vector which is pointing towards out target location
        Vector direction = vecTo.subtract(vecFrom).normalize();

        //Modify the speed of the vector
        direction = direction.multiply(speed);
        spawnedItem.setVelocity(direction);
        return spawnedItem;
    }

    public static void spawnItemsFromCollection(@NotNull Player player,
            @NotNull Location location,
            @NotNull Collection<ItemStack> drops,
            @NotNull ItemSpawnReason itemSpawnReason) {
        requireNonNull(drops, "drops cannot be null");
        for (ItemStack drop : drops) {
            spawnItem(player, location, drop, itemSpawnReason);
        }
    }

    /**
     * Drops only the first n items in a collection Size should always be a positive integer above
     * 0
     *
     * @param location target drop location
     * @param drops collection to iterate over
     * @param sizeLimit the number of drops to process
     */
    public static void spawnItemsFromCollection(@Nullable Player player,
            @NotNull Location location,
            @NotNull Collection<ItemStack> drops,
            @NotNull ItemSpawnReason itemSpawnReason,
            int sizeLimit) {
        // TODO: This doesn't make much sense, unit test time?
        final ItemStack[] arrayDrops = drops.toArray(new ItemStack[0]);

        for (int i = 0; i < sizeLimit - 1; i++) {
            spawnItem(player, location, arrayDrops[i], itemSpawnReason);
        }
    }

    /**
     * Spawn items form a collection if conditions are met. Each item is tested against the
     * condition and spawned if it passes.
     *
     * @param potentialItemDrops The collection of items to iterate over, each one is tested and
     * spawned if the predicate is true
     * @param predicate The predicate to test the item against
     * @param itemSpawnReason The reason for the item drop
     * @param spawnLocation The location to spawn the item at
     * @param player The player to spawn the item for
     */
    public static void spawnItemsConditionally(@NotNull Collection<ItemStack> potentialItemDrops,
            @NotNull Predicate<ItemStack> predicate,
            @NotNull ItemSpawnReason itemSpawnReason,
            @NotNull Location spawnLocation,
            @NotNull Player player) {
        potentialItemDrops.stream()
                .filter(predicate)
                .forEach(itemStack -> spawnItem(player, spawnLocation, itemStack, itemSpawnReason));
    }
}
