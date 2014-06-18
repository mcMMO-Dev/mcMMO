package com.gmail.nossr50.skills.salvage;

import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.material.MaterialData;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.skills.SkillManager;
import com.gmail.nossr50.skills.salvage.Salvage.Tier;
import com.gmail.nossr50.skills.salvage.salvageables.Salvageable;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.StringUtils;
import com.gmail.nossr50.util.skills.SkillUtils;

public class SalvageManager extends SkillManager {
    private boolean placedAnvil;
    private int     lastClick;

    public SalvageManager(McMMOPlayer mcMMOPlayer) {
        super(mcMMOPlayer, SkillType.SALVAGE);
    }

    /**
     * Handles notifications for placing an anvil.
     */
    public void placedAnvilCheck() {
        Player player = getPlayer();

        if (getPlacedAnvil()) {
            return;
        }

        if (Config.getInstance().getSalvageAnvilMessagesEnabled()) {
            player.sendMessage(LocaleLoader.getString("Salvage.Listener.Anvil"));
        }

        if (Config.getInstance().getSalvageAnvilPlaceSoundsEnabled()) {
            player.playSound(player.getLocation(), Sound.ANVIL_LAND, Misc.ANVIL_USE_VOLUME, Misc.ANVIL_USE_PITCH);
        }

        togglePlacedAnvil();
    }

    public void handleSalvage(Location location, ItemStack item) {
        Player player = getPlayer();

        Salvageable salvageable = mcMMO.getSalvageableManager().getSalvageable(item.getType());

        // Permissions checks on material and item types
        if (!Permissions.salvageItemType(player, salvageable.getSalvageItemType())) {
            player.sendMessage(LocaleLoader.getString("mcMMO.NoPermission"));
            return;
        }

        if (!Permissions.salvageMaterialType(player, salvageable.getSalvageMaterialType())) {
            player.sendMessage(LocaleLoader.getString("mcMMO.NoPermission"));
            return;
        }

        int skillLevel = getSkillLevel();
        int minimumSalvageableLevel = salvageable.getMinimumLevel();

        // Level check
        if (skillLevel < minimumSalvageableLevel) {
            player.sendMessage(LocaleLoader.getString("Salvage.Skills.Adept.Level", minimumSalvageableLevel, StringUtils.getPrettyItemString(item.getType())));
            return;
        }

        if (item.getDurability() != 0 && (getSkillLevel() < Salvage.advancedSalvageUnlockLevel || !Permissions.advancedSalvage(player))) {
            player.sendMessage(LocaleLoader.getString("Salvage.Skills.Adept.Damaged"));
            return;
        }

        int salvageableAmount = Salvage.calculateSalvageableAmount(item.getDurability(), salvageable.getMaximumDurability(), salvageable.getMaximumQuantity());

        if (salvageableAmount == 0) {
            player.sendMessage(LocaleLoader.getString("Salvage.Skills.TooDamaged"));
            return;
        }

        salvageableAmount = Math.max((int) (salvageableAmount * getMaxSalvagePercentage()), 1); // Always get at least something back, if you're capable of salvaging it.

        player.setItemInHand(new ItemStack(Material.AIR));
        location.add(0, 1, 0);

        Map<Enchantment, Integer> enchants = item.getEnchantments();

        if (!enchants.isEmpty()) {
            ItemStack enchantBook = arcaneSalvageCheck(enchants);

            if (enchantBook != null) {
                Misc.dropItem(location, enchantBook);
            }
        }

        byte salvageMaterialMetadata = (salvageable.getSalvageMaterialMetadata() != (byte) -1) ? salvageable.getSalvageMaterialMetadata() : 0;

        Misc.dropItems(location, new MaterialData(salvageable.getSalvageMaterial(), salvageMaterialMetadata).toItemStack(salvageableAmount), 1);

        // BWONG BWONG BWONG - CLUNK!
        if (Config.getInstance().getSalvageAnvilUseSoundsEnabled()) {
            player.playSound(player.getLocation(), Sound.ANVIL_USE, Misc.ANVIL_USE_VOLUME, Misc.ANVIL_USE_PITCH);
            player.playSound(player.getLocation(), Sound.ITEM_BREAK, 1.0F, 1.0F);
        }

        player.sendMessage(LocaleLoader.getString("Salvage.Skills.Success"));
    }

    public double getMaxSalvagePercentage() {
        return Math.min((((Salvage.salvageMaxPercentage / Salvage.salvageMaxPercentageLevel) * getSkillLevel()) / 100.0D), Salvage.salvageMaxPercentage / 100.0D);
    }

    /**
     * Gets the Arcane Salvage rank
     *
     * @return the current Arcane Salvage rank
     */
    public int getArcaneSalvageRank() {
        int skillLevel = getSkillLevel();

        for (Tier tier : Tier.values()) {
            if (skillLevel >= tier.getLevel()) {
                return tier.toNumerical();
            }
        }

        return 0;
    }

    public double getExtractFullEnchantChance() {
        int skillLevel = getSkillLevel();

        for (Tier tier : Tier.values()) {
            if (skillLevel >= tier.getLevel()) {
                return tier.getExtractFullEnchantChance();
            }
        }

        return 0;
    }

    public double getExtractPartialEnchantChance() {
        int skillLevel = getSkillLevel();

        for (Tier tier : Tier.values()) {
            if (skillLevel >= tier.getLevel()) {
                return tier.getExtractPartialEnchantChance();
            }
        }

        return 0;
    }

    private ItemStack arcaneSalvageCheck(Map<Enchantment, Integer> enchants) {
        Player player = getPlayer();

        if (getArcaneSalvageRank() == 0 || !Permissions.arcaneSalvage(player)) {
            player.sendMessage(LocaleLoader.getString("Salvage.Skills.ArcaneFailed"));
            return null;
        }

        ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
        EnchantmentStorageMeta enchantMeta = (EnchantmentStorageMeta) book.getItemMeta();

        boolean downgraded = false;

        for (Entry<Enchantment, Integer> enchant : enchants.entrySet()) {
            int successChance = Misc.getRandom().nextInt(activationChance);

            if (!Salvage.arcaneSalvageEnchantLoss || getExtractFullEnchantChance() > successChance) {
                enchantMeta.addStoredEnchant(enchant.getKey(), enchant.getValue(), true);
            }
            else if (enchant.getValue() > 1 && Salvage.arcaneSalvageDowngrades && getExtractPartialEnchantChance() > successChance) {
                enchantMeta.addStoredEnchant(enchant.getKey(), enchant.getValue() - 1, true);
                downgraded = true;
            }
            else {
                downgraded = true;
            }
        }

        Map<Enchantment, Integer> newEnchants = enchantMeta.getStoredEnchants();

        if (newEnchants.isEmpty()) {
            player.sendMessage(LocaleLoader.getString("Salvage.Skills.ArcaneFailed"));
            return null;
        }

        if (downgraded || newEnchants.size() < enchants.size()) {
            player.sendMessage(LocaleLoader.getString("Salvage.Skills.ArcanePartial"));
        }
        else {
            player.sendMessage(LocaleLoader.getString("Salvage.Skills.ArcaneSuccess"));
        }

        book.setItemMeta(enchantMeta);
        return book;
    }

    /**
     * Check if the player has tried to use an Anvil before.
     * @param actualize
     *
     * @return true if the player has confirmed using an Anvil
     */
    public boolean checkConfirmation(boolean actualize) {
        Player player = getPlayer();
        long lastUse = getLastAnvilUse();

        if (!SkillUtils.cooldownExpired(lastUse, 3) || !Config.getInstance().getSalvageConfirmRequired()) {
            return true;
        }

        if (!actualize) {
            return false;
        }

        actualizeLastAnvilUse();

        player.sendMessage(LocaleLoader.getString("Skills.ConfirmOrCancel", LocaleLoader.getString("Salvage.Pretty.Name")));

        return false;
    }

    /*
     * Salvage Anvil Placement
     */

    public boolean getPlacedAnvil() {
        return placedAnvil;
    }

    public void togglePlacedAnvil() {
        placedAnvil = !placedAnvil;
    }

    /*
     * Salvage Anvil Usage
     */

    public int getLastAnvilUse() {
        return lastClick;
    }

    public void setLastAnvilUse(int value) {
        lastClick = value;
    }

    public void actualizeLastAnvilUse() {
        lastClick = (int) (System.currentTimeMillis() / Misc.TIME_CONVERSION_FACTOR);
    }
}
