package com.gmail.nossr50.util.compat.layers.persistentdata;

import com.gmail.nossr50.datatypes.meta.UUIDMeta;
import com.gmail.nossr50.mcMMO;
import org.bukkit.block.Furnace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.tags.CustomItemTagContainer;
import org.bukkit.inventory.meta.tags.ItemTagType;
import org.bukkit.metadata.Metadatable;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Persistent Data API is unavailable
 */
public class SpigotTemporaryDataLayer extends AbstractPersistentDataLayer {

    private final String FURNACE_OWNER_METADATA_KEY = "mcMMO_furnace_owner";
    private final String ABILITY_TOOL_METADATA_KEY = "mcMMO_super_ability_tool";

    @Override
    public boolean initializeLayer() {
        return true;
    }

    @Override
    public UUID getFurnaceOwner(@NotNull Furnace furnace) {
        Metadatable metadatable = (Metadatable) furnace;

        if(metadatable.getMetadata(FURNACE_OWNER_METADATA_KEY).size() > 0) {
            UUIDMeta uuidMeta = (UUIDMeta) metadatable.getMetadata(FURNACE_OWNER_METADATA_KEY).get(0);
            return (UUID) uuidMeta.value();
        } else {
            return null;
        }
    }

    @Override
    public void setFurnaceOwner(@NotNull Furnace furnace, @NotNull UUID uuid) {
        Metadatable metadatable = (Metadatable) furnace;

        if(metadatable.getMetadata(FURNACE_OWNER_METADATA_KEY).size() > 0) {
            metadatable.removeMetadata(FURNACE_OWNER_METADATA_KEY, mcMMO.p);
        }

        metadatable.setMetadata(FURNACE_OWNER_METADATA_KEY, new UUIDMeta(mcMMO.p, uuid));
    }

    @Override
    public void setSuperAbilityBoostedItem(@NotNull ItemStack itemStack, int originalDigSpeed) {
        ItemMeta itemMeta = itemStack.getItemMeta();

        if(itemMeta == null) {
            mcMMO.p.getLogger().severe("Item meta should never be null for a super boosted item!");
            return;
        }

        itemMeta.getCustomTagContainer().setCustomTag(superAbilityBoosted, ItemTagType.INTEGER, originalDigSpeed);
        itemStack.setItemMeta(itemMeta);
    }

    @Override
    public boolean isSuperAbilityBoosted(@NotNull ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();

        if(itemMeta == null)
            return false;

        CustomItemTagContainer tagContainer = itemMeta.getCustomTagContainer();
        return tagContainer.hasCustomTag(superAbilityBoosted, ItemTagType.INTEGER);
    }

    @Override
    public int getSuperAbilityToolOriginalDigSpeed(@NotNull ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();

        if(itemMeta == null)
            return 0;

        CustomItemTagContainer tagContainer = itemMeta.getCustomTagContainer();

        if(tagContainer.hasCustomTag(superAbilityBoosted , ItemTagType.INTEGER)) {
            return tagContainer.getCustomTag(superAbilityBoosted, ItemTagType.INTEGER);
        } else {
            return 0;
        }
    }

    @Override
    public void removeBonusDigSpeedOnSuperAbilityTool(@NotNull ItemStack itemStack) {
        int originalSpeed = getSuperAbilityToolOriginalDigSpeed(itemStack);
        ItemMeta itemMeta = itemStack.getItemMeta();

        if(itemMeta == null)
            return;

        if(itemMeta.hasEnchant(Enchantment.DIG_SPEED)) {
            itemMeta.removeEnchant(Enchantment.DIG_SPEED);
        }


        if(originalSpeed > 0) {
            itemMeta.addEnchant(Enchantment.DIG_SPEED, originalSpeed, true);
        }

        //TODO: needed?
        itemStack.setItemMeta(itemMeta);
    }
}
