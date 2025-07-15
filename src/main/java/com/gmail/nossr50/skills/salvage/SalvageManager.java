package com.gmail.nossr50.skills.salvage;

import com.gmail.nossr50.api.ItemSpawnReason;
import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.datatypes.interactions.NotificationType;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.skills.SkillManager;
import com.gmail.nossr50.skills.salvage.salvageables.Salvageable;
import com.gmail.nossr50.util.EventUtils;
import com.gmail.nossr50.util.ItemUtils;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.player.NotificationManager;
import com.gmail.nossr50.util.random.ProbabilityUtil;
import com.gmail.nossr50.util.skills.RankUtils;
import com.gmail.nossr50.util.skills.SkillUtils;
import com.gmail.nossr50.util.sounds.SoundManager;
import com.gmail.nossr50.util.sounds.SoundType;
import com.gmail.nossr50.util.text.StringUtils;
import java.util.Map;
import java.util.Map.Entry;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

public class SalvageManager extends SkillManager {
    private boolean placedAnvil;
    private int lastClick;

    public SalvageManager(McMMOPlayer mmoPlayer) {
        super(mmoPlayer, PrimarySkillType.SALVAGE);
    }

    /**
     * Handles notifications for placing an anvil.
     */
    public void placedAnvilCheck() {
        Player player = getPlayer();

        if (getPlacedAnvil()) {
            return;
        }

        if (mcMMO.p.getGeneralConfig().getSalvageAnvilMessagesEnabled()) {
            NotificationManager.sendPlayerInformation(player, NotificationType.SUBSKILL_MESSAGE,
                    "Salvage.Listener.Anvil");
        }

        if (mcMMO.p.getGeneralConfig().getSalvageAnvilPlaceSoundsEnabled()) {
            SoundManager.sendSound(player, player.getLocation(), SoundType.ANVIL);
        }

        togglePlacedAnvil();
    }

    public void handleSalvage(Location location, ItemStack item) {
        final Player player = getPlayer();

        final Salvageable salvageable = mcMMO.getSalvageableManager()
                .getSalvageable(item.getType());
        final ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            if (meta.hasCustomModelData() && !mcMMO.p.getCustomItemSupportConfig()
                    .isCustomSalvageAllowed()) {
                NotificationManager.sendPlayerInformation(player,
                        NotificationType.SUBSKILL_MESSAGE_FAILED,
                        "Anvil.Salvage.Reject.CustomModelData");
                return;
            }
            if (meta.isUnbreakable()) {
                NotificationManager.sendPlayerInformation(player,
                        NotificationType.SUBSKILL_MESSAGE_FAILED, "Anvil.Unbreakable");
                return;
            }
        }

        // Permissions checks on material and item types
        if (!Permissions.salvageItemType(player, salvageable.getSalvageItemType())) {
            NotificationManager.sendPlayerInformation(player, NotificationType.NO_PERMISSION,
                    "mcMMO.NoPermission");
            return;
        }

        if (!Permissions.salvageMaterialType(player, salvageable.getSalvageMaterialType())) {
            NotificationManager.sendPlayerInformation(player, NotificationType.NO_PERMISSION,
                    "mcMMO.NoPermission");
            return;
        }

        /*int skillLevel = getSkillLevel();*/
        int minimumSalvageableLevel = salvageable.getMinimumLevel();

        // Level check
        if (getSkillLevel() < minimumSalvageableLevel) {
            NotificationManager.sendPlayerInformation(player, NotificationType.REQUIREMENTS_NOT_MET,
                    "Salvage.Skills.Adept.Level",
                    String.valueOf(minimumSalvageableLevel),
                    StringUtils.getPrettyMaterialString(item.getType()));
            return;
        }

        int durability = meta instanceof Damageable ? ((Damageable) meta).getDamage() : 0;
        int potentialSalvageYield = Salvage.calculateSalvageableAmount(durability,
                salvageable.getMaximumDurability(), salvageable.getMaximumQuantity());

        if (potentialSalvageYield <= 0) {
            NotificationManager.sendPlayerInformation(player,
                    NotificationType.SUBSKILL_MESSAGE_FAILED, "Salvage.Skills.TooDamaged");
            return;
        }

        potentialSalvageYield = Math.min(potentialSalvageYield, getSalvageLimit(
                getPlayer())); // Always get at least something back, if you're capable of salvaging it.

        location.add(0.5, 1, 0.5);

        Map<Enchantment, Integer> enchants = item.getEnchantments();

        ItemStack enchantBook = null;
        if (!enchants.isEmpty()) {
            enchantBook = arcaneSalvageCheck(enchants);
        }

        ItemStack salvageResults = new ItemStack(salvageable.getSalvageMaterial(),
                potentialSalvageYield);

        //Call event
        if (EventUtils.callSalvageCheckEvent(player, item, salvageResults, enchantBook)
                .isCancelled()) {
            return;
        }

        NotificationManager.sendPlayerInformationChatOnly(player, "Salvage.Skills.Lottery.Normal",
                String.valueOf(potentialSalvageYield),
                StringUtils.getPrettyMaterialString(item.getType()));

        player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));

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
            ItemUtils.spawnItemTowardsLocation(getPlayer(), anvilLoc.clone(), playerLoc.clone(),
                    enchantBook, vectorSpeed, ItemSpawnReason.SALVAGE_ENCHANTMENT_BOOK);
        }

        ItemUtils.spawnItemTowardsLocation(getPlayer(), anvilLoc.clone(), playerLoc.clone(),
                salvageResults, vectorSpeed, ItemSpawnReason.SALVAGE_MATERIALS);

        // BWONG BWONG BWONG - CLUNK!
        if (mcMMO.p.getGeneralConfig().getSalvageAnvilUseSoundsEnabled()) {
            SoundManager.sendSound(player, player.getLocation(), SoundType.ITEM_BREAK);
        }

        NotificationManager.sendPlayerInformation(player, NotificationType.SUBSKILL_MESSAGE,
                "Salvage.Skills.Success");
    }

    /*public double getMaxSalvagePercentage() {
        return Math.min((((Salvage.salvageMaxPercentage / Salvage.salvageMaxPercentageLevel) * getSkillLevel()) / 100.0D), Salvage.salvageMaxPercentage / 100.0D);
    }*/

    public static int getSalvageLimit(Player player) {
        if (RankUtils.getRank(player, SubSkillType.SALVAGE_SCRAP_COLLECTOR) == 1) {
            return 1;
        } else {
            var curRank = RankUtils.getRank(player, SubSkillType.SALVAGE_SCRAP_COLLECTOR);
            return curRank * 2;
        }
    }

    /**
     * Gets the Arcane Salvage rank
     *
     * @return the current Arcane Salvage rank
     */
    public int getArcaneSalvageRank() {
        return RankUtils.getRank(getPlayer(), SubSkillType.SALVAGE_ARCANE_SALVAGE);
    }

    public double getExtractFullEnchantChance() {
        if (Permissions.hasSalvageEnchantBypassPerk(getPlayer())) {
            return 100.0D;
        }

        return mcMMO.p.getAdvancedConfig()
                .getArcaneSalvageExtractFullEnchantsChance(getArcaneSalvageRank());
    }

    public double getExtractPartialEnchantChance() {
        return mcMMO.p.getAdvancedConfig()
                .getArcaneSalvageExtractPartialEnchantsChance(getArcaneSalvageRank());
    }

    private ItemStack arcaneSalvageCheck(Map<Enchantment, Integer> enchants) {
        Player player = getPlayer();

        if (!RankUtils.hasUnlockedSubskill(player, SubSkillType.SALVAGE_ARCANE_SALVAGE)
                || !Permissions.arcaneSalvage(player)) {
            NotificationManager.sendPlayerInformationChatOnly(player,
                    "Salvage.Skills.ArcaneFailed");
            return null;
        }

        ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
        EnchantmentStorageMeta enchantMeta = (EnchantmentStorageMeta) book.getItemMeta();

        boolean downgraded = false;
        int arcaneFailureCount = 0;

        for (Entry<Enchantment, Integer> enchant : enchants.entrySet()) {

            int enchantLevel = enchant.getValue();

            if (!ExperienceConfig.getInstance().allowUnsafeEnchantments()) {
                if (enchantLevel > enchant.getKey().getMaxLevel()) {
                    enchantLevel = enchant.getKey().getMaxLevel();
                }
            }

            if (!Salvage.arcaneSalvageEnchantLoss
                    || Permissions.hasSalvageEnchantBypassPerk(player)
                    || ProbabilityUtil.isStaticSkillRNGSuccessful(
                    PrimarySkillType.SALVAGE, mmoPlayer, getExtractFullEnchantChance())) {
                enchantMeta.addStoredEnchant(enchant.getKey(), enchantLevel, true);
            } else if (enchantLevel > 1
                    && Salvage.arcaneSalvageDowngrades
                    && ProbabilityUtil.isStaticSkillRNGSuccessful(
                    PrimarySkillType.SALVAGE, mmoPlayer, getExtractPartialEnchantChance())) {
                enchantMeta.addStoredEnchant(enchant.getKey(), enchantLevel - 1, true);
                downgraded = true;
            } else {
                arcaneFailureCount++;
            }
        }

        if (failedAllEnchants(arcaneFailureCount, enchants.size())) {
            NotificationManager.sendPlayerInformationChatOnly(player,
                    "Salvage.Skills.ArcaneFailed");
            return null;
        } else if (downgraded) {
            NotificationManager.sendPlayerInformationChatOnly(player,
                    "Salvage.Skills.ArcanePartial");
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

        if (!SkillUtils.cooldownExpired(lastUse, 3) || !mcMMO.p.getGeneralConfig()
                .getSalvageConfirmRequired()) {
            return true;
        }

        if (!actualize) {
            return false;
        }

        actualizeLastAnvilUse();

        NotificationManager.sendPlayerInformation(player, NotificationType.SUBSKILL_MESSAGE,
                "Skills.ConfirmOrCancel", LocaleLoader.getString("Salvage.Pretty.Name"));

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
