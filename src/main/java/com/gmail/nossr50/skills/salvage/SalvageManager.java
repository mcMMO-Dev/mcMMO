package com.gmail.nossr50.skills.salvage;

import com.gmail.nossr50.datatypes.interactions.NotificationType;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.skills.SkillManager;
import com.gmail.nossr50.skills.salvage.salvageables.Salvageable;
import com.gmail.nossr50.util.EventUtils;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.StringUtils;
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
    private int lastClick;

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

        if (pluginRef.getConfigManager().getConfigSalvage().getGeneral().isAnvilMessages()) {
            pluginRef.getNotificationManager().sendPlayerInformation(player, NotificationType.SUBSKILL_MESSAGE, "Salvage.Listener.Anvil");
        }

        if (pluginRef.getConfigManager().getConfigSalvage().getGeneral().isAnvilPlacedSounds()) {
            SoundManager.sendSound(player, player.getLocation(), SoundType.ANVIL);
        }

        togglePlacedAnvil();
    }

    public void handleSalvage(Location location, ItemStack item) {
        Player player = getPlayer();

        Salvageable salvageable = pluginRef.getSalvageableManager().getSalvageable(item.getType());

        if (item.getItemMeta().isUnbreakable()) {
            pluginRef.getNotificationManager().sendPlayerInformation(player, NotificationType.SUBSKILL_MESSAGE_FAILED, "Anvil.Unbreakable");
            return;
        }

        // Permissions checks on material and item types
        if (!Permissions.salvageItemType(player, salvageable.getSalvageItemType())) {
            pluginRef.getNotificationManager().sendPlayerInformation(player, NotificationType.NO_PERMISSION, "mcMMO.NoPermission");
            return;
        }

        if (!Permissions.salvageMaterialType(player, salvageable.getSalvageItemMaterialCategory())) {
            pluginRef.getNotificationManager().sendPlayerInformation(player, NotificationType.NO_PERMISSION, "mcMMO.NoPermission");
            return;
        }

        /*int skillLevel = getSkillLevel();
        int minimumSalvageableLevel = salvageable.getMinimumLevel();*/

        // Level check
        if (!RankUtils.hasUnlockedSubskill(player, SubSkillType.SALVAGE_ARCANE_SALVAGE)) {
            pluginRef.getNotificationManager().sendPlayerInformation(player, NotificationType.REQUIREMENTS_NOT_MET, "Salvage.Skills.Adept.Level", String.valueOf(RankUtils.getUnlockLevel(SubSkillType.SALVAGE_ARCANE_SALVAGE)), StringUtils.getPrettyItemString(item.getType()));
            return;
        }

        int potentialSalvageYield = Salvage.calculateSalvageableAmount(item.getDurability(), salvageable.getMaximumDurability(), salvageable.getMaximumQuantity());

        if (potentialSalvageYield <= 0) {
            pluginRef.getNotificationManager().sendPlayerInformation(player, NotificationType.SUBSKILL_MESSAGE_FAILED, "Salvage.Skills.TooDamaged");
            return;
        }

        potentialSalvageYield = Math.min(potentialSalvageYield, getSalvageLimit()); // Always get at least something back, if you're capable of salvaging it.

        player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
        location.add(0.5, 1, 0.5);

        Map<Enchantment, Integer> enchants = item.getEnchantments();

        ItemStack enchantBook = null;
        if (!enchants.isEmpty()) {
            enchantBook = arcaneSalvageCheck(enchants);
        }

        //Lottery on Salvageable Amount

        int lotteryResults = 1;
        int chanceOfSuccess = 99;

        for(int x = 0; x < potentialSalvageYield-1; x++) {

            if(RandomChanceUtil.rollDice(chanceOfSuccess, 100)) {
                chanceOfSuccess-=2;
                Math.max(chanceOfSuccess, 95);

                lotteryResults+=1;
            }
        }

        if(lotteryResults == potentialSalvageYield && potentialSalvageYield != 1 && RankUtils.isPlayerMaxRankInSubSkill(player, SubSkillType.SALVAGE_ARCANE_SALVAGE)) {
            pluginRef.getNotificationManager().sendPlayerInformationChatOnly(player, "Salvage.Skills.Lottery.Perfect", String.valueOf(lotteryResults), StringUtils.getPrettyItemString(item.getType()));
        } else if(salvageable.getMaximumQuantity() == 1 || getSalvageLimit() >= salvageable.getMaximumQuantity()) {
            pluginRef.getNotificationManager().sendPlayerInformationChatOnly(player,  "Salvage.Skills.Lottery.Normal", String.valueOf(lotteryResults), StringUtils.getPrettyItemString(item.getType()));
        } else {
            pluginRef.getNotificationManager().sendPlayerInformationChatOnly(player,  "Salvage.Skills.Lottery.Untrained", String.valueOf(lotteryResults), StringUtils.getPrettyItemString(item.getType()));
        }

        ItemStack salvageResults = new ItemStack(salvageable.getSalvagedItemMaterial(), lotteryResults);

        //Call event
        if (EventUtils.callSalvageCheckEvent(player, item, salvageResults, enchantBook).isCancelled()) {
            return;
        }

        Location anvilLoc = location.clone();
        Location playerLoc = player.getLocation().clone();
        double distance = anvilLoc.distance(playerLoc);

        double speedLimit = .6;
        double minSpeed = .3;

        //Clamp the speed and vary it by distance
        double vectorSpeed = Math.min(speedLimit, Math.max(minSpeed, distance * .2));

        //Add a very small amount of height
        anvilLoc.add(0, .1, 0);

        if (enchantBook != null) {
            Misc.spawnItemTowardsLocation(anvilLoc.clone(), playerLoc.clone(), enchantBook, vectorSpeed);
        }

        Misc.spawnItemTowardsLocation(anvilLoc.clone(), playerLoc.clone(), salvageResults, vectorSpeed);

        // BWONG BWONG BWONG - CLUNK!
        if (pluginRef.getConfigManager().getConfigSalvage().getGeneral().isAnvilUseSounds()) {
            SoundManager.sendSound(player, player.getLocation(), SoundType.ITEM_BREAK);
        }

        pluginRef.getNotificationManager().sendPlayerInformation(player, NotificationType.SUBSKILL_MESSAGE, "Salvage.Skills.Success");
    }

    /*public double getMaxSalvagePercentage() {
        return Math.min((((Salvage.salvageMaxPercentage / Salvage.salvageMaxPercentageLevel) * getSkillLevel()) / 100.0D), Salvage.salvageMaxPercentage / 100.0D);
    }*/

    public int getSalvageLimit() {
        return (RankUtils.getRank(getPlayer(), SubSkillType.SALVAGE_SCRAP_COLLECTOR));
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
        if (Permissions.hasSalvageEnchantBypassPerk(getPlayer()))
            return 100.0D;

        return pluginRef.getConfigManager().getConfigSalvage().getConfigArcaneSalvage().getExtractFullEnchantChance().get(getArcaneSalvageRank());
    }

    public double getExtractPartialEnchantChance() {
        return pluginRef.getConfigManager().getConfigSalvage().getConfigArcaneSalvage().getExtractPartialEnchantChance().get(getArcaneSalvageRank());
    }

    private ItemStack arcaneSalvageCheck(Map<Enchantment, Integer> enchants) {
        Player player = getPlayer();

        if (!RankUtils.hasUnlockedSubskill(player, SubSkillType.SALVAGE_ARCANE_SALVAGE) || !Permissions.arcaneSalvage(player)) {
            pluginRef.getNotificationManager().sendPlayerInformationChatOnly(player, "Salvage.Skills.ArcaneFailed");
            return null;
        }

        ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
        EnchantmentStorageMeta enchantMeta = (EnchantmentStorageMeta) book.getItemMeta();

        boolean downgraded = false;
        int arcaneFailureCount = 0;

        for (Entry<Enchantment, Integer> enchant : enchants.entrySet()) {

            int enchantLevel = enchant.getValue();

            if(!pluginRef.getConfigManager().getConfigExploitPrevention().getConfigSectionExploitSkills().getConfigSectionExploitSalvage().isAllowUnsafeEnchants()) {
                if(enchantLevel > enchant.getKey().getMaxLevel()) {
                    enchantLevel = enchant.getKey().getMaxLevel();
                }
            }

            if (!Salvage.arcaneSalvageEnchantLoss
                    || Permissions.hasSalvageEnchantBypassPerk(player)
                    || RandomChanceUtil.checkRandomChanceExecutionSuccess(new RandomChanceSkillStatic(getExtractFullEnchantChance(), getPlayer(), SubSkillType.SALVAGE_ARCANE_SALVAGE))) {

                enchantMeta.addStoredEnchant(enchant.getKey(), enchantLevel, true);
            }
            else if (enchantLevel > 1
                    && Salvage.arcaneSalvageDowngrades
                    && RandomChanceUtil.checkRandomChanceExecutionSuccess(new RandomChanceSkillStatic(getExtractPartialEnchantChance(), getPlayer(), SubSkillType.SALVAGE_ARCANE_SALVAGE))) {
                enchantMeta.addStoredEnchant(enchant.getKey(), enchantLevel - 1, true);
                downgraded = true;
            } else {
                arcaneFailureCount++;
            }
        }

        if(failedAllEnchants(arcaneFailureCount, enchants.entrySet().size()))
        {
            pluginRef.getNotificationManager().sendPlayerInformationChatOnly(player,  "Salvage.Skills.ArcaneFailed");
            return null;
        } else if(downgraded)
        {
            pluginRef.getNotificationManager().sendPlayerInformationChatOnly(player,  "Salvage.Skills.ArcanePartial");
        }

        book.setItemMeta(enchantMeta);
        return book;
    }

    private boolean failedAllEnchants(int arcaneFailureCount, int size) {
        return arcaneFailureCount == size;
    }

    /**
     * Check if the player has tried to use an Anvil before.
     *
     * @param actualize
     * @return true if the player has confirmed using an Anvil
     */
    public boolean checkConfirmation(boolean actualize) {
        Player player = getPlayer();
        long lastUse = getLastAnvilUse();

        if (!SkillUtils.cooldownExpired(lastUse, 3) || !pluginRef.getConfigManager().getConfigSalvage().getGeneral().isEnchantedItemsRequireConfirm()) {
            return true;
        }

        if (!actualize) {
            return false;
        }

        actualizeLastAnvilUse();

        pluginRef.getNotificationManager().sendPlayerInformation(player, NotificationType.SUBSKILL_MESSAGE, "Skills.ConfirmOrCancel", pluginRef.getLocaleManager().getString("Salvage.Pretty.Name"));

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
