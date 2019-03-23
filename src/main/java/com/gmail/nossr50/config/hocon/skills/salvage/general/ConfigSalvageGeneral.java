package com.gmail.nossr50.config.hocon.skills.salvage.general;

import ninja.leaping.configurate.objectmapping.Setting;
import org.bukkit.Material;

public class ConfigSalvageGeneral {

    public static final boolean ANVIL_USE_SOUNDS_DEFAULT = true;
    public static final boolean ANVIL_MESSAGES_DEFAULT = true;
    public static final boolean ANVIL_PLACED_SOUNDS_DEFAULT = true;
    public static final boolean ENCHANTED_ITEMS_REQUIRE_CONFIRM = true;

    @Setting(value = "Anvil-Block-Material", comment = "The block used for mcMMO repairs." +
            "Default value: "+"minecraft:gold_block")
    private String salvageAnvilMaterial = Material.GOLD_BLOCK.getKey().toString();

    @Setting(value = "Anvil-Use-Sounds", comment = "If true, mcMMO will play a sound when a player uses an anvil." +
            "\nDefault value: "+ANVIL_USE_SOUNDS_DEFAULT)
    private boolean anvilUseSounds = ANVIL_USE_SOUNDS_DEFAULT;

    @Setting(value = "Anvil-Notifications", comment = "Allows helpful messages to help players understand how to use the anvil." +
            "\nDefault value: "+ANVIL_MESSAGES_DEFAULT)
    private boolean anvilMessages = ANVIL_MESSAGES_DEFAULT;

    @Setting(value = "Anvil-Placed-Sounds", comment = "Placing an anvil in the world will play a sound effect." +
            "\nDefault value: "+ANVIL_PLACED_SOUNDS_DEFAULT)
    private boolean anvilPlacedSounds = ANVIL_PLACED_SOUNDS_DEFAULT;

    @Setting(value = "Enchanted-Items-Require-Confirm", comment = "Warns players that using the anvil with an enchanted item is dangerous." +
            "\nPlayers will have to use the anvil twice in a row with an enchanted item." +
            "\nDefault value: "+ENCHANTED_ITEMS_REQUIRE_CONFIRM)
    private boolean enchantedItemsRequireConfirm = ENCHANTED_ITEMS_REQUIRE_CONFIRM;

    public Material getSalvageAnvilMaterial() {
        return Material.matchMaterial(salvageAnvilMaterial);
    }

    public boolean isAnvilUseSounds() {
        return anvilUseSounds;
    }

    public boolean isAnvilMessages() {
        return anvilMessages;
    }

    public boolean isAnvilPlacedSounds() {
        return anvilPlacedSounds;
    }

    public boolean isEnchantedItemsRequireConfirm() {
        return enchantedItemsRequireConfirm;
    }
}
