package com.gmail.nossr50.skills.herbalism;

import java.util.Map;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.util.Permissions;

import com.google.common.collect.Maps;

public enum HerbalismBlock {
    BROWN_MUSHROOM(Material.BROWN_MUSHROOM, Config.getInstance().getHerbalismXPMushrooms(), Config.getInstance().getBrownMushroomsDoubleDropsEnabled()),
    CACTUS(Material.CACTUS, Config.getInstance().getHerbalismXPCactus(), Config.getInstance().getCactiDoubleDropsEnabled()),
    CARROT(Material.CARROT, Material.CARROT_ITEM, Config.getInstance().getHerbalismXPCarrot(), Config.getInstance().getCarrotDoubleDropsEnabled()),
    COCOA(Material.COCOA, new ItemStack(Material.INK_SACK, 1, DyeColor.BROWN.getDyeData()), Config.getInstance().getHerbalismXPCocoa(), Config.getInstance().getCocoaDoubleDropsEnabled()),
    CROPS(Material.CROPS, Material.WHEAT, Config.getInstance().getHerbalismXPWheat(), Config.getInstance().getWheatDoubleDropsEnabled()),
    MELON_BLOCK(Material.MELON_BLOCK, Material.MELON, Config.getInstance().getHerbalismXPMelon(), Config.getInstance().getMelonsDoubleDropsEnabled()),
    NETHER_WARTS(Material.NETHER_WARTS, Material.NETHER_STALK, Config.getInstance().getHerbalismXPNetherWart(), Config.getInstance().getNetherWartsDoubleDropsEnabled()),
    POTATO(Material.POTATO, Material.POTATO_ITEM, Config.getInstance().getHerbalismXPPotato(), Config.getInstance().getPotatoDoubleDropsEnabled()),
    PUMPKIN(Material.PUMPKIN, Config.getInstance().getHerbalismXPPumpkin(), Config.getInstance().getPumpkinsDoubleDropsEnabled()),
    RED_MUSHROOM(Material.RED_MUSHROOM, Config.getInstance().getHerbalismXPMushrooms(), Config.getInstance().getRedMushroomsDoubleDropsEnabled()),
    RED_ROSE(Material.RED_ROSE, Config.getInstance().getHerbalismXPFlowers(), Config.getInstance().getRedRosesDoubleDropsEnabled()),
    SUGAR_CANE_BLOCK(Material.SUGAR_CANE_BLOCK, Material.SUGAR_CANE, Config.getInstance().getHerbalismXPSugarCane(), Config.getInstance().getSugarCaneDoubleDropsEnabled()),
    VINE(Material.VINE, Config.getInstance().getHerbalismXPVines(), Config.getInstance().getVinesDoubleDropsEnabled()),
    WATER_LILY(Material.WATER_LILY, Config.getInstance().getHerbalismXPLilyPads(), Config.getInstance().getWaterLiliesDoubleDropsEnabled()),
    YELLOW_FLOWER(Material.YELLOW_FLOWER, Config.getInstance().getHerbalismXPFlowers(), Config.getInstance().getYellowFlowersDoubleDropsEnabled());

    private Material blockType;
    private ItemStack dropItem;
    private int xpGain;
    private boolean doubleDropsEnabled;
    private final static Map<Material, HerbalismBlock> BY_MATERIAL = Maps.newHashMap();

    private HerbalismBlock(Material blockType, int xpGain, boolean doubleDropsEnabled) {
        this(blockType, new ItemStack(blockType), xpGain, doubleDropsEnabled);
    }

    private HerbalismBlock(Material blockType, Material dropType, int xpGain, boolean doubleDropsEnabled) {
        this(blockType, new ItemStack(dropType), xpGain, doubleDropsEnabled);
    }

    private HerbalismBlock(Material blockType, ItemStack dropItem, int xpGain, boolean doubleDropsEnabled) {
        this.blockType = blockType;
        this.dropItem = dropItem;
        this.xpGain = xpGain;
        this.doubleDropsEnabled = doubleDropsEnabled;
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
        return xpGain;
    }

    public boolean canDoubleDrop() {
        return doubleDropsEnabled;
    }

    public boolean hasGreenThumbPermission(Player player) {
        return Permissions.greenThumbPlant(player, blockType);
    }

    public static HerbalismBlock getHerbalismBlock(Material blockType) {
        return BY_MATERIAL.get(blockType);
    }
}
