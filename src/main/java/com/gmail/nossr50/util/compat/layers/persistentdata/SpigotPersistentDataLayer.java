package com.gmail.nossr50.util.compat.layers.persistentdata;

import com.gmail.nossr50.mcMMO;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Furnace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class SpigotPersistentDataLayer extends AbstractPersistentDataLayer {

    /*
     * Don't modify these keys
     */
    public static final String FURNACE_UUID_MOST_SIG = "furnace_uuid_most_sig";
    public static final String FURNACE_UUID_LEAST_SIG = "furnace_uuid_least_sig";
    public static final String SUPER_ABILITY_BOOSTED = "super_ability_boosted";

    private NamespacedKey furnaceOwner_MostSig_Key;
    private NamespacedKey furnaceOwner_LeastSig_Key;
    private NamespacedKey superAbilityBoosted;


    @Override
    public boolean initializeLayer() {
        initNamespacedKeys();
        return true;
    }

    private void initNamespacedKeys() {
        furnaceOwner_MostSig_Key = getNamespacedKey(FURNACE_UUID_MOST_SIG);
        furnaceOwner_LeastSig_Key = getNamespacedKey(FURNACE_UUID_LEAST_SIG);
        superAbilityBoosted = getNamespacedKey(SUPER_ABILITY_BOOSTED);
    }

    @NotNull
    public NamespacedKey getNamespacedKey(String key) {
        return new NamespacedKey(mcMMO.p, key);
    }

    @Override
    public @Nullable UUID getFurnaceOwner(Furnace furnace) {
        //Get container from entity
        PersistentDataContainer dataContainer = ((PersistentDataHolder) furnace).getPersistentDataContainer();

        //Too lazy to make a custom data type for this stuff
        Long mostSigBits = dataContainer.get(furnaceOwner_MostSig_Key, PersistentDataType.LONG);
        Long leastSigBits = dataContainer.get(furnaceOwner_LeastSig_Key, PersistentDataType.LONG);

        if(mostSigBits != null && leastSigBits != null) {
            return new UUID(mostSigBits, leastSigBits);
        } else {
            return null;
        }
    }

    @Override
    public void setFurnaceOwner(Furnace furnace, UUID uuid) {
        PersistentDataContainer dataContainer = ((PersistentDataHolder) furnace).getPersistentDataContainer();

        dataContainer.set(furnaceOwner_MostSig_Key, PersistentDataType.LONG, uuid.getMostSignificantBits());
        dataContainer.set(furnaceOwner_LeastSig_Key, PersistentDataType.LONG, uuid.getLeastSignificantBits());

        furnace.update();
    }

    @Override
    public void setSuperAbilityBoostedItem(ItemStack itemStack, int originalDigSpeed) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        PersistentDataContainer dataContainer = ((PersistentDataHolder) itemMeta).getPersistentDataContainer();

        dataContainer.set(superAbilityBoosted, PersistentDataType.INTEGER, originalDigSpeed);

        itemStack.setItemMeta(itemMeta);
    }

    @Override
    public boolean isSuperAbilityBoosted(@NotNull ItemMeta itemMeta) {
        //Get container from entity
        PersistentDataContainer dataContainer = ((PersistentDataHolder) itemMeta).getPersistentDataContainer();

        //If this value isn't null, then the tool can be considered dig speed boosted
        Integer boostValue = dataContainer.get(superAbilityBoosted, PersistentDataType.INTEGER);

        return boostValue != null;
    }

    @Override
    public int getSuperAbilityToolOriginalDigSpeed(@NotNull ItemMeta itemMeta) {
        //Get container from entity
        PersistentDataContainer dataContainer = ((PersistentDataHolder) itemMeta).getPersistentDataContainer();

        if(dataContainer.get(superAbilityBoosted, PersistentDataType.INTEGER) == null) {
            mcMMO.p.getLogger().severe("Value should never be null for a boosted item");
            return 0;
        } else {
            //Too lazy to make a custom data type for this stuff
            Integer boostValue = dataContainer.get(superAbilityBoosted, PersistentDataType.INTEGER);
            return Math.max(boostValue, 0);
        }
    }

    @Override
    public void removeBonusDigSpeedOnSuperAbilityTool(@NotNull ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();

        //TODO: can be optimized
        int originalSpeed = getSuperAbilityToolOriginalDigSpeed(itemMeta);

        if(itemMeta.hasEnchant(Enchantment.DIG_SPEED)) {
            itemMeta.removeEnchant(Enchantment.DIG_SPEED);
        }

        if(originalSpeed > 0) {
            itemMeta.addEnchant(Enchantment.DIG_SPEED, originalSpeed, true);
        }

        PersistentDataContainer dataContainer = ((PersistentDataHolder) itemMeta).getPersistentDataContainer();
        dataContainer.remove(superAbilityBoosted); //Remove persistent data

        //TODO: needed?
        itemStack.setItemMeta(itemMeta);
    }
}
