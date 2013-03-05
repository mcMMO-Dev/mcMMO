package com.gmail.nossr50.skills.herbalism;

import java.util.Map;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.util.Permissions;
import com.google.common.collect.Maps;

public enum HerbalismBlock {
    BROWN_MUSHROOM(Material.BROWN_MUSHROOM),
    CACTUS(Material.CACTUS),
    CARROT(Material.CARROT, Material.CARROT_ITEM),
    COCOA(Material.COCOA, new ItemStack(Material.INK_SACK, 1, DyeColor.BROWN.getDyeData())),
    CROPS(Material.CROPS, Material.WHEAT),
    MELON_BLOCK(Material.MELON_BLOCK, Material.MELON),
    NETHER_WARTS(Material.NETHER_WARTS, Material.NETHER_STALK),
    POTATO(Material.POTATO, Material.POTATO_ITEM),
    PUMPKIN(Material.PUMPKIN),
    RED_MUSHROOM(Material.RED_MUSHROOM),
    RED_ROSE(Material.RED_ROSE),
    SUGAR_CANE_BLOCK(Material.SUGAR_CANE_BLOCK, Material.SUGAR_CANE),
    VINE(Material.VINE),
    WATER_LILY(Material.WATER_LILY),
    YELLOW_FLOWER(Material.YELLOW_FLOWER);

    private Material  blockType;
    private ItemStack dropItem;

    private final static Map<Material, HerbalismBlock> BY_MATERIAL = Maps.newHashMap();

    private HerbalismBlock(Material blockType) {
        this(blockType, new ItemStack(blockType));
    }

    private HerbalismBlock(Material blockType, Material dropType) {
        this(blockType, new ItemStack(dropType));
    }

    private HerbalismBlock(Material blockType, ItemStack dropItem) {
        this.blockType = blockType;
        this.dropItem = dropItem;
    }

    static {
        for (HerbalismBlock herbalismBlock : values()) {
            BY_MATERIAL.put(herbalismBlock.blockType, herbalismBlock);
        }
    }

    public ItemStack getDropItem() {
        return dropItem;
    }

    public int getXpGain() {
        return Config.getInstance().getXp(SkillType.HERBALISM, blockType);
    }

    public boolean canDoubleDrop() {
        return Config.getInstance().getDoubleDropsEnabled(SkillType.HERBALISM, blockType);
    }

    public boolean hasGreenThumbPermission(Player player) {
        return Permissions.greenThumbPlant(player, blockType);
    }

    public static HerbalismBlock getHerbalismBlock(Material blockType) {
        return BY_MATERIAL.get(blockType);
    }
}
