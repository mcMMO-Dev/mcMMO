package com.gmail.nossr50.util.compat.layers.persistentdata;

import com.gmail.nossr50.api.exceptions.IncompleteNamespacedKeyRegister;
import com.gmail.nossr50.datatypes.meta.UUIDMeta;
import com.gmail.nossr50.mcMMO;
import org.bukkit.block.Furnace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.tags.CustomItemTagContainer;
import org.bukkit.inventory.meta.tags.ItemTagType;
import org.bukkit.metadata.Metadatable;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.UUID;

/**
 * Persistent Data API is unavailable
 */
public class SpigotPersistentDataLayer_1_13 extends AbstractPersistentDataLayer {

    private final @NotNull String KEY_FURNACE_OWNER = "mcMMO_furnace_owner";
    private final @NotNull EnumMap<MobMetaFlagType, String> mobFlagKeyMap;

    public SpigotPersistentDataLayer_1_13() {
        mobFlagKeyMap = new EnumMap<>(MobMetaFlagType.class);
        initMobFlagKeyMap();
    }

    @Override
    public boolean initializeLayer() {
        return true;
    }

    private void initMobFlagKeyMap() throws IncompleteNamespacedKeyRegister {
        for(MobMetaFlagType flagType : MobMetaFlagType.values()) {
            switch(flagType) {
                case MOB_SPAWNER_MOB:
                    mobFlagKeyMap.put(flagType, STR_MOB_SPAWNER_MOB);
                    break;
                case EGG_MOB:
                    mobFlagKeyMap.put(flagType, STR_EGG_MOB);
                    break;
                case NETHER_PORTAL_MOB:
                    mobFlagKeyMap.put(flagType, STR_NETHER_PORTAL_MOB);
                    break;
                case COTW_SUMMONED_MOB:
                    mobFlagKeyMap.put(flagType, STR_COTW_SUMMONED_MOB);
                    break;
                case PLAYER_BRED_MOB:
                    mobFlagKeyMap.put(flagType, STR_PLAYER_BRED_MOB);
                    break;
                case PLAYER_TAMED_MOB:
                    mobFlagKeyMap.put(flagType, STR_PLAYER_TAMED_MOB);
                    break;
                case EXPLOITED_ENDERMEN:
                    mobFlagKeyMap.put(flagType, STR_EXPLOITED_ENDERMEN);
                    break;
                default:
                    throw new IncompleteNamespacedKeyRegister("Missing flag register for: "+flagType.toString());
            }
        }
    }

    @Override
    public boolean hasMobFlag(@NotNull MobMetaFlagType flag, @NotNull LivingEntity livingEntity) {
        return livingEntity.hasMetadata(mobFlagKeyMap.get(flag));
    }

    @Override
    public boolean hasMobFlags(@NotNull LivingEntity livingEntity) {
        for(String currentKey : mobFlagKeyMap.values()) {
            if(livingEntity.hasMetadata(currentKey)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void addMobFlags(@NotNull LivingEntity sourceEntity, @NotNull LivingEntity targetEntity) {
        for(MobMetaFlagType flag : MobMetaFlagType.values()) {
            if(hasMobFlag(flag, sourceEntity)) {
                flagMetadata(flag, targetEntity);
            }
        }
    }

    @Override
    public void flagMetadata(@NotNull MobMetaFlagType flag, @NotNull LivingEntity livingEntity) {
        if(!hasMobFlag(flag, livingEntity)) {
            livingEntity.setMetadata(mobFlagKeyMap.get(flag), mcMMO.metadataValue);
        }
    }

    @Override
    public void removeMobFlag(@NotNull MobMetaFlagType flag, @NotNull LivingEntity livingEntity) {
        if(hasMobFlag(flag, livingEntity)) {
            livingEntity.removeMetadata(mobFlagKeyMap.get(flag), mcMMO.p);
        }
    }

    @Override
    public UUID getFurnaceOwner(@NotNull Furnace furnace) {
        Metadatable metadatable = (Metadatable) furnace;

        if(metadatable.getMetadata(KEY_FURNACE_OWNER).size() > 0) {
            UUIDMeta uuidMeta = (UUIDMeta) metadatable.getMetadata(KEY_FURNACE_OWNER).get(0);
            return (UUID) uuidMeta.value();
        } else {
            return null;
        }
    }

    @Override
    public void setFurnaceOwner(@NotNull Furnace furnace, @NotNull UUID uuid) {
        Metadatable metadatable = (Metadatable) furnace;

        if(metadatable.getMetadata(KEY_FURNACE_OWNER).size() > 0) {
            metadatable.removeMetadata(KEY_FURNACE_OWNER, mcMMO.p);
        }

        metadatable.setMetadata(KEY_FURNACE_OWNER, new UUIDMeta(mcMMO.p, uuid));
    }

    @Override
    public void setSuperAbilityBoostedItem(@NotNull ItemStack itemStack, int originalDigSpeed) {
        ItemMeta itemMeta = itemStack.getItemMeta();

        if(itemMeta == null) {
            mcMMO.p.getLogger().severe("Item meta should never be null for a super boosted item!");
            return;
        }

        itemMeta.getCustomTagContainer().setCustomTag(NSK_SUPER_ABILITY_BOOSTED_ITEM, ItemTagType.INTEGER, originalDigSpeed);
        itemStack.setItemMeta(itemMeta);
    }

    @Override
    public boolean isSuperAbilityBoosted(@NotNull ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();

        if(itemMeta == null)
            return false;

        CustomItemTagContainer tagContainer = itemMeta.getCustomTagContainer();
        return tagContainer.hasCustomTag(NSK_SUPER_ABILITY_BOOSTED_ITEM, ItemTagType.INTEGER);
    }

    @Override
    public int getSuperAbilityToolOriginalDigSpeed(@NotNull ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();

        if(itemMeta == null)
            return 0;

        CustomItemTagContainer tagContainer = itemMeta.getCustomTagContainer();

        if(tagContainer.hasCustomTag(NSK_SUPER_ABILITY_BOOSTED_ITEM, ItemTagType.INTEGER)) {
            return tagContainer.getCustomTag(NSK_SUPER_ABILITY_BOOSTED_ITEM, ItemTagType.INTEGER);
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
