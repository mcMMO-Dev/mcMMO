package com.gmail.nossr50.util.compat.layers.persistentdata;

import com.gmail.nossr50.api.exceptions.IncompleteNamespacedKeyRegister;
import com.gmail.nossr50.mcMMO;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Furnace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.UUID;

public class SpigotPersistentDataLayer_1_14 extends AbstractPersistentDataLayer {

    private final @NotNull EnumMap<MobMetaFlagType, NamespacedKey> mobFlagKeyMap;

    public SpigotPersistentDataLayer_1_14() {
        mobFlagKeyMap = new EnumMap<>(MobMetaFlagType.class);
        initMobFlagKeyMap();
    }

    @Override
    public boolean initializeLayer() {
        return true;
    }

    /**
     * Registers the namespaced keys required by the API (CB/Spigot)
     */
    private void initMobFlagKeyMap() throws IncompleteNamespacedKeyRegister {
        for(MobMetaFlagType mobMetaFlagType : MobMetaFlagType.values()) {
            switch(mobMetaFlagType) {

                case MOB_SPAWNER_MOB:
                    mobFlagKeyMap.put(mobMetaFlagType, NSK_MOB_SPAWNER_MOB);
                    break;
                case EGG_MOB:
                    mobFlagKeyMap.put(mobMetaFlagType, NSK_EGG_MOB);
                    break;
                case NETHER_PORTAL_MOB:
                    mobFlagKeyMap.put(mobMetaFlagType, NSK_NETHER_GATE_MOB);
                    break;
                case COTW_SUMMONED_MOB:
                    mobFlagKeyMap.put(mobMetaFlagType, NSK_COTW_SUMMONED_MOB);
                    break;
                case PLAYER_BRED_MOB:
                    mobFlagKeyMap.put(mobMetaFlagType, NSK_PLAYER_BRED_MOB);
                    break;
                case EXPLOITED_ENDERMEN:
                    mobFlagKeyMap.put(mobMetaFlagType, NSK_EXPLOITED_ENDERMEN);
                    break;
                case PLAYER_TAMED_MOB:
                    mobFlagKeyMap.put(mobMetaFlagType, NSK_PLAYER_TAMED_MOB);
                    break;
                default:
                    throw new IncompleteNamespacedKeyRegister("missing namespaced key register for type: "+ mobMetaFlagType.toString());
            }
        }
    }

    @Override
    public boolean hasMobFlag(@NotNull MobMetaFlagType flag, @NotNull LivingEntity livingEntity) {
        return livingEntity.getPersistentDataContainer().has(mobFlagKeyMap.get(flag), PersistentDataType.SHORT);
    }

    @Override
    public boolean hasMobFlags(@NotNull LivingEntity livingEntity) {
        for(NamespacedKey currentKey : mobFlagKeyMap.values()) {
            if(livingEntity.getPersistentDataContainer().has(currentKey, PersistentDataType.BYTE)) {
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
            PersistentDataContainer persistentDataContainer = livingEntity.getPersistentDataContainer();
            persistentDataContainer.set(mobFlagKeyMap.get(flag), PersistentDataType.BYTE, SIMPLE_FLAG_VALUE);
        }
    }

    @Override
    public void removeMobFlag(@NotNull MobMetaFlagType flag, @NotNull LivingEntity livingEntity) {
        if(hasMobFlag(flag, livingEntity)) {
            PersistentDataContainer persistentDataContainer = livingEntity.getPersistentDataContainer();
            persistentDataContainer.remove(mobFlagKeyMap.get(flag));
        }
    }

    @Override
    public @Nullable UUID getFurnaceOwner(@NotNull Furnace furnace) {
        //Get container from entity
        PersistentDataContainer dataContainer = ((PersistentDataHolder) furnace).getPersistentDataContainer();

        //Too lazy to make a custom data type for this stuff
        Long mostSigBits = dataContainer.get(NSK_FURNACE_UUID_MOST_SIG, PersistentDataType.LONG);
        Long leastSigBits = dataContainer.get(NSK_FURNACE_UUID_LEAST_SIG, PersistentDataType.LONG);

        if(mostSigBits != null && leastSigBits != null) {
            return new UUID(mostSigBits, leastSigBits);
        } else {
            return null;
        }
    }

    @Override
    public void setFurnaceOwner(@NotNull Furnace furnace, @NotNull UUID uuid) {
        PersistentDataContainer dataContainer = ((PersistentDataHolder) furnace).getPersistentDataContainer();

        dataContainer.set(NSK_FURNACE_UUID_MOST_SIG, PersistentDataType.LONG, uuid.getMostSignificantBits());
        dataContainer.set(NSK_FURNACE_UUID_LEAST_SIG, PersistentDataType.LONG, uuid.getLeastSignificantBits());

        furnace.update();
    }

    @Override
    public void setSuperAbilityBoostedItem(@NotNull ItemStack itemStack, int originalDigSpeed) {
        if(itemStack.getItemMeta() == null) {
            mcMMO.p.getLogger().severe("Can not assign persistent data to an item with null item metadata");
            return;
        }

        ItemMeta itemMeta = itemStack.getItemMeta();
        PersistentDataContainer dataContainer = itemMeta.getPersistentDataContainer();

        dataContainer.set(NSK_SUPER_ABILITY_BOOSTED_ITEM, PersistentDataType.INTEGER, originalDigSpeed);

        itemStack.setItemMeta(itemMeta);
    }

    @Override
    public boolean isSuperAbilityBoosted(@NotNull ItemStack itemStack) {
        if(itemStack.getItemMeta() == null)
            return false;

        ItemMeta itemMeta = itemStack.getItemMeta();
        //Get container from entity
        PersistentDataContainer dataContainer = itemMeta.getPersistentDataContainer();

        //If this value isn't null, then the tool can be considered dig speed boosted
        Integer boostValue = dataContainer.get(NSK_SUPER_ABILITY_BOOSTED_ITEM, PersistentDataType.INTEGER);

        return boostValue != null;
    }

    @Override
    public int getSuperAbilityToolOriginalDigSpeed(@NotNull ItemStack itemStack) {
        //Get container from entity
        ItemMeta itemMeta = itemStack.getItemMeta();

        if(itemMeta == null)
            return 0;

        PersistentDataContainer dataContainer = itemMeta.getPersistentDataContainer();

        if(dataContainer.get(NSK_SUPER_ABILITY_BOOSTED_ITEM, PersistentDataType.INTEGER) == null) {
            mcMMO.p.getLogger().severe("Value should never be null for a boosted item");
            return 0;
        } else {
            //Too lazy to make a custom data type for this stuff
            Integer boostValue = dataContainer.get(NSK_SUPER_ABILITY_BOOSTED_ITEM, PersistentDataType.INTEGER);
            return Math.max(boostValue, 0);
        }
    }

    @Override
    public void removeBonusDigSpeedOnSuperAbilityTool(@NotNull ItemStack itemStack) {
        int originalSpeed = getSuperAbilityToolOriginalDigSpeed(itemStack);
        ItemMeta itemMeta = itemStack.getItemMeta();

        //TODO: can be optimized
        if(itemMeta.hasEnchant(Enchantment.DIG_SPEED)) {
            itemMeta.removeEnchant(Enchantment.DIG_SPEED);
        }

        if(originalSpeed > 0) {
            itemMeta.addEnchant(Enchantment.DIG_SPEED, originalSpeed, true);
        }

        PersistentDataContainer dataContainer = itemMeta.getPersistentDataContainer();
        dataContainer.remove(NSK_SUPER_ABILITY_BOOSTED_ITEM); //Remove persistent data

        //TODO: needed?
        itemStack.setItemMeta(itemMeta);
    }
}
