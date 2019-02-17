package com.gmail.nossr50.skills.salvage;

import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.config.MainConfig;
import com.gmail.nossr50.datatypes.interactions.NotificationType;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.skills.SkillManager;
import com.gmail.nossr50.skills.salvage.salvageables.Salvageable;
import com.gmail.nossr50.util.EventUtils;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.StringUtils;
import com.gmail.nossr50.util.player.NotificationManager;
import com.gmail.nossr50.util.random.RandomChanceSkillStatic;
import com.gmail.nossr50.util.random.RandomChanceUtil;
import com.gmail.nossr50.util.skills.RankUtils;
import com.gmail.nossr50.util.skills.SkillUtils;
import com.gmail.nossr50.util.sounds.SoundManager;
import com.gmail.nossr50.util.sounds.SoundType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import java.util.Map;
import java.util.Map.Entry;

public class SalvageManager extends SkillManager {
    private boolean placedAnvil;
    private int     lastClick;

    public SalvageManager(McMMOPlayer mcMMOPlayer) {
        super(mcMMOPlayer, PrimarySkillType.SALVAGE);
    }

    /**
     * Handles notifications for placing an anvil.
     */
    public void placedAnvilCheck() {
        Player player = getPlayer();

        if (getPlacedAnvil()) {
            return;
        }

        if (MainConfig.getInstance().getSalvageAnvilMessagesEnabled()) {
            NotificationManager.sendPlayerInformation(player, NotificationType.SUBSKILL_MESSAGE, "Salvage.Listener.Anvil");
        }

        if (MainConfig.getInstance().getSalvageAnvilPlaceSoundsEnabled()) {
            SoundManager.sendSound(player, player.getLocation(), SoundType.ANVIL);
        }

        togglePlacedAnvil();
    }

    public void handleSalvage(Location location, ItemStack item) {
        Player player = getPlayer();

        Salvageable salvageable = mcMMO.getSalvageableManager().getSalvageable(item.getType());

        if (item.getItemMeta().isUnbreakable()) {
            NotificationManager.sendPlayerInformation(player, NotificationType.SUBSKILL_MESSAGE_FAILED, "Anvil.Unbreakable");
            return;
        }

        // Permissions checks on material and item types
        if (!Permissions.salvageItemType(player, salvageable.getSalvageItemType())) {
            NotificationManager.sendPlayerInformation(player, NotificationType.NO_PERMISSION, "mcMMO.NoPermission");
            return;
        }

        if (!Permissions.salvageMaterialType(player, salvageable.getSalvageMaterialType())) {
            NotificationManager.sendPlayerInformation(player, NotificationType.NO_PERMISSION, "mcMMO.NoPermission");
            return;
        }

        /*int skillLevel = getSkillLevel();
        int minimumSalvageableLevel = salvageable.getMinimumLevel();*/

        // Level check
        if (!RankUtils.hasUnlockedSubskill(player, SubSkillType.SALVAGE_ARCANE_SALVAGE)) {
            NotificationManager.sendPlayerInformation(player, NotificationType.REQUIREMENTS_NOT_MET, "Salvage.Skills.Adept.Level", String.valueOf(RankUtils.getUnlockLevel(SubSkillType.SALVAGE_ARCANE_SALVAGE)), StringUtils.getPrettyItemString(item.getType()));
            return;
        }

        if (item.getDurability() != 0 && (!RankUtils.hasUnlockedSubskill(player, SubSkillType.SALVAGE_ADVANCED_SALVAGE) || !Permissions.advancedSalvage(player))) {
            NotificationManager.sendPlayerInformation(player, NotificationType.SUBSKILL_MESSAGE_FAILED, "Salvage.Skills.Adept.Damaged");
            return;
        }

        int salvageableAmount = Salvage.calculateSalvageableAmount(item.getDurability(), salvageable.getMaximumDurability(), salvageable.getMaximumQuantity());

        if (salvageableAmount == 0) {
            NotificationManager.sendPlayerInformation(player, NotificationType.SUBSKILL_MESSAGE_FAILED, "Salvage.Skills.TooDamaged");
            player.sendMessage(LocaleLoader.getString("Salvage.Skills.TooDamaged"));
            return;
        }

        salvageableAmount = Math.min(salvageableAmount, getSalvageableAmount()); // Always get at least something back, if you're capable of salvaging it.


        player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
        location.add(0.5, 1, 0.5);

        Map<Enchantment, Integer> enchants = item.getEnchantments();

        ItemStack enchantBook = null;
        if (!enchants.isEmpty()) {
            enchantBook = arcaneSalvageCheck(enchants);
        }

        ItemStack salvageResults = new ItemStack(salvageable.getSalvageMaterial(), salvageableAmount);

        //Call event
        if (EventUtils.callSalvageCheckEvent(player, item, salvageResults, enchantBook).isCancelled()) {
            return;
        }

        if (enchantBook != null) {
            Misc.dropItem(location, enchantBook);
        }

        Misc.dropItems(location, salvageResults, 1);

        // BWONG BWONG BWONG - CLUNK!
        if (MainConfig.getInstance().getSalvageAnvilUseSoundsEnabled()) {
            SoundManager.sendSound(player, player.getLocation(), SoundType.ANVIL);
            SoundManager.sendSound(player, player.getLocation(), SoundType.ITEM_BREAK);

            //player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0F, 1.0F);
        }

        NotificationManager.sendPlayerInformation(player, NotificationType.SUBSKILL_MESSAGE, "Salvage.Skills.Success");
    }

    /*public double getMaxSalvagePercentage() {
        return Math.min((((Salvage.salvageMaxPercentage / Salvage.salvageMaxPercentageLevel) * getSkillLevel()) / 100.0D), Salvage.salvageMaxPercentage / 100.0D);
    }*/

    public int getSalvageableAmount() {
        return (RankUtils.getRank(getPlayer(), SubSkillType.SALVAGE_ARCANE_SALVAGE) * 1);
    }

    /**
     * Gets the Arcane Salvage rank
     *
     * @return the current Arcane Salvage rank
     */
    public int getArcaneSalvageRank() {
        return RankUtils.getRank(getPlayer(), SubSkillType.SALVAGE_ARCANE_SALVAGE);
    }

    /*public double getExtractFullEnchantChance() {
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
    }*/

    public double getExtractFullEnchantChance() {
        return AdvancedConfig.getInstance().getArcaneSalvageExtractFullEnchantsChance(getArcaneSalvageRank());
    }

    public double getExtractPartialEnchantChance() {
        return AdvancedConfig.getInstance().getArcaneSalvageExtractPartialEnchantsChance(getArcaneSalvageRank());
    }

    private ItemStack arcaneSalvageCheck(Map<Enchantment, Integer> enchants) {
        Player player = getPlayer();

        if (!RankUtils.hasUnlockedSubskill(player, SubSkillType.SALVAGE_ARCANE_SALVAGE) || !Permissions.arcaneSalvage(player)) {
            NotificationManager.sendPlayerInformation(player, NotificationType.SUBSKILL_MESSAGE_FAILED, "Salvage.Skills.ArcaneFailed");
            return null;
        }

        ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
        EnchantmentStorageMeta enchantMeta = (EnchantmentStorageMeta) book.getItemMeta();

        boolean downgraded = false;
        boolean arcaneFailure = false;

        for (Entry<Enchantment, Integer> enchant : enchants.entrySet()) {
            if (!Salvage.arcaneSalvageEnchantLoss
                    || RandomChanceUtil.checkRandomChanceExecutionSuccess(new RandomChanceSkillStatic(getExtractFullEnchantChance(), getPlayer(), SubSkillType.SALVAGE_ARCANE_SALVAGE))) {
                enchantMeta.addStoredEnchant(enchant.getKey(), enchant.getValue(), true);
            }
            else if (enchant.getValue() > 1
                    && Salvage.arcaneSalvageDowngrades
                    && RandomChanceUtil.checkRandomChanceExecutionSuccess(new RandomChanceSkillStatic(getExtractPartialEnchantChance(), getPlayer(), SubSkillType.SALVAGE_ARCANE_SALVAGE))) {
                enchantMeta.addStoredEnchant(enchant.getKey(), enchant.getValue() - 1, true);
                downgraded = true;
            }
            else {
                arcaneFailure = true;
                downgraded = true;
            }
        }

        if(!arcaneFailure)
        {
            Map<Enchantment, Integer> newEnchants = enchantMeta.getStoredEnchants();

            if (downgraded || newEnchants.size() < enchants.size()) {
                NotificationManager.sendPlayerInformation(player, NotificationType.SUBSKILL_MESSAGE_FAILED, "Salvage.Skills.ArcanePartial");
            }
            else {
                NotificationManager.sendPlayerInformation(player, NotificationType.SUBSKILL_MESSAGE_FAILED, "Salvage.Skills.ArcanePartial");
            }

            book.setItemMeta(enchantMeta);
        } else {
            if(enchantMeta.getStoredEnchants().size() > 0)
            {
                NotificationManager.sendPlayerInformation(player, NotificationType.SUBSKILL_MESSAGE_FAILED, "Salvage.Skills.ArcaneFailed");
            }
            return null;
        }

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

        if (!SkillUtils.cooldownExpired(lastUse, 3) || !MainConfig.getInstance().getSalvageConfirmRequired()) {
            return true;
        }

        if (!actualize) {
            return false;
        }

        actualizeLastAnvilUse();

        NotificationManager.sendPlayerInformation(player, NotificationType.SUBSKILL_MESSAGE, "Skills.ConfirmOrCancel", LocaleLoader.getString("Salvage.Pretty.Name"));

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
