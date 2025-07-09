package com.gmail.nossr50.datatypes.treasure;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.LogUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FishingTreasureBook extends FishingTreasure {
    private final @Nullable Set<Enchantment> blackListedEnchantments;
    private final @Nullable Set<Enchantment> whiteListedEnchantments;
    private final @NotNull List<EnchantmentWrapper> legalEnchantments; //TODO: Make immutable

    public FishingTreasureBook(@NotNull ItemStack enchantedBook, int xp,
            @Nullable Set<Enchantment> blackListedEnchantments,
            @Nullable Set<Enchantment> whiteListedEnchantments) {
        super(enchantedBook, xp);

        this.blackListedEnchantments = blackListedEnchantments;
        this.whiteListedEnchantments = whiteListedEnchantments;
        this.legalEnchantments = new ArrayList<>();

        initLegalEnchantments();
    }

    private void initLegalEnchantments() {
        LogUtils.debug(mcMMO.p.getLogger(), "Registering enchantments for Fishing Book...");

        for (Enchantment enchantment : Enchantment.values()) {
            if (isEnchantAllowed(enchantment)) {
                addAllLegalEnchants(enchantment);
            }
        }
    }

    /**
     * Get all the enchantments which can drop for this book This list can be empty, but should in
     * practice never be empty...
     *
     * @return all the enchantments that can drop for this book
     */
    public @NotNull List<EnchantmentWrapper> getLegalEnchantments() {
        return legalEnchantments;
    }

    private @Nullable Set<Enchantment> getBlacklistedEnchantments() {
        return blackListedEnchantments;
    }

    private @Nullable Set<Enchantment> getWhitelistedEnchantments() {
        return whiteListedEnchantments;
    }

    private void addAllLegalEnchants(@NotNull Enchantment enchantment) {
        int legalEnchantCap = enchantment.getMaxLevel();

        for (int i = 0; i < legalEnchantCap; i++) {
            int enchantLevel = i + 1;
            EnchantmentWrapper enchantmentWrapper = new EnchantmentWrapper(enchantment,
                    enchantLevel);
            legalEnchantments.add(enchantmentWrapper);
//            mcMMO.p.getLogger().info("Fishing treasure book enchantment added: " + enchantmentWrapper);
        }
    }

    private boolean isEnchantAllowed(@NotNull Enchantment enchantment) {
        if (whiteListedEnchantments != null && !whiteListedEnchantments.isEmpty()) {
            return whiteListedEnchantments.contains(enchantment);
        } else if (blackListedEnchantments != null && !blackListedEnchantments.isEmpty()) {
            return !blackListedEnchantments.contains(enchantment);
        } else {
            return true;
        }
    }
}
