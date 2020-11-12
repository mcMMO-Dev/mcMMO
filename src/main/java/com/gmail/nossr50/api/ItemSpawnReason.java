package com.gmail.nossr50.api;

public enum ItemSpawnReason {
    ARROW_RETRIEVAL_ACTIVATED, //Players sometimes can retrieve arrows instead of losing them when hitting a mob
    EXCAVATION_TREASURE, //Any drops when excavation treasures activate fall under this
    FISHING_EXTRA_FISH, //A config setting allows more fish to be found when fishing, the extra fish are part of this
    FISHING_SHAKE_TREASURE, //When using a fishing rod on a mob and finding a treasure via Shake
    HYLIAN_LUCK_TREASURE, //When finding a treasure in grass via hylian luck
    BLAST_MINING_DEBRIS_NON_ORES, //The non-ore debris that are dropped from blast mining
    BLAST_MINING_ORES, //The ore(s) which may include player placed ores being dropped from blast mining
    BLAST_MINING_ORES_BONUS_DROP, //Any bonus ores that drop from a result of a players Mining skills
    UNARMED_DISARMED_ITEM, //When you disarm an opponent and they drop their weapon
    SALVAGE_ENCHANTMENT_BOOK, //When you salvage an enchanted item and get the enchantment back in book form
    SALVAGE_MATERIALS, //When you salvage an item and get materials back
    TREE_FELLER_DISPLACED_BLOCK,
    BONUS_DROPS, //Can be from Mining, Woodcutting, Herbalism, etc
}
