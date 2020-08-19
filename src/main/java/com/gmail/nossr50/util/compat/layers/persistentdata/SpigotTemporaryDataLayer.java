package com.gmail.nossr50.util.compat.layers.persistentdata;

import com.gmail.nossr50.datatypes.meta.SuperAbilityToolMeta;
import com.gmail.nossr50.datatypes.meta.UUIDMeta;
import com.gmail.nossr50.mcMMO;
import org.bukkit.block.Furnace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
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
    public UUID getFurnaceOwner(Furnace furnace) {
        Metadatable metadatable = (Metadatable) furnace;

        if(metadatable.getMetadata(FURNACE_OWNER_METADATA_KEY).size() > 0) {
            UUIDMeta uuidMeta = (UUIDMeta) metadatable.getMetadata(FURNACE_OWNER_METADATA_KEY).get(0);
            return (UUID) uuidMeta.value();
        } else {
            return null;
        }
    }

    @Override
    public void setFurnaceOwner(@NotNull Furnace furnace, UUID uuid) {
        Metadatable metadatable = (Metadatable) furnace;

        if(metadatable.getMetadata(FURNACE_OWNER_METADATA_KEY).size() > 0) {
            metadatable.removeMetadata(FURNACE_OWNER_METADATA_KEY, mcMMO.p);
        }

        metadatable.setMetadata(FURNACE_OWNER_METADATA_KEY, new UUIDMeta(mcMMO.p, uuid));
    }

    @Override
    public void setSuperAbilityBoostedItem(@NotNull ItemStack itemStack, int originalDigSpeed) {
        Metadatable metadatable = getMetadatable(itemStack);
        metadatable.setMetadata(ABILITY_TOOL_METADATA_KEY, new SuperAbilityToolMeta(originalDigSpeed, mcMMO.p));
    }

    private Metadatable getMetadatable(@NotNull ItemStack itemStack) {
        return (Metadatable) itemStack;
    }

    @Override
    public boolean isSuperAbilityBoosted(@NotNull ItemStack itemStack) {
        Metadatable metadatable = getMetadatable(itemStack);
        return metadatable.getMetadata(ABILITY_TOOL_METADATA_KEY).size() > 0;
    }

    @Override
    public int getSuperAbilityToolOriginalDigSpeed(@NotNull ItemStack itemStack) {
        Metadatable metadatable = getMetadatable(itemStack);

        if(metadatable.getMetadata(ABILITY_TOOL_METADATA_KEY).size() > 0) {
            SuperAbilityToolMeta toolMeta = (SuperAbilityToolMeta) metadatable.getMetadata(ABILITY_TOOL_METADATA_KEY).get(0);
            return toolMeta.asInt();
        } else {
//            mcMMO.p.getLogger().info("Original dig enchant speed could not be found on item! Most likely it was lost from a server restart.");
            return 0;
        }
    }

    @Override
    public void removeBonusDigSpeedOnSuperAbilityTool(@NotNull ItemStack itemStack) {
        int originalSpeed = getSuperAbilityToolOriginalDigSpeed(itemStack);
        ItemMeta itemMeta = itemStack.getItemMeta();

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
