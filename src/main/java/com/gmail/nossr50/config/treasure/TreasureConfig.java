package com.gmail.nossr50.config.treasure;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Dye;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

import com.gmail.nossr50.config.ConfigLoader;
import com.gmail.nossr50.datatypes.treasure.ExcavationTreasure;
import com.gmail.nossr50.datatypes.treasure.FishingTreasure;
import com.gmail.nossr50.datatypes.treasure.HylianTreasure;
import com.gmail.nossr50.datatypes.treasure.ShakeTreasure;

public class TreasureConfig extends ConfigLoader {
    private static TreasureConfig instance;

    public List<ExcavationTreasure> excavationFromDirt      = new ArrayList<ExcavationTreasure>();
    public List<ExcavationTreasure> excavationFromGrass     = new ArrayList<ExcavationTreasure>();
    public List<ExcavationTreasure> excavationFromSand      = new ArrayList<ExcavationTreasure>();
    public List<ExcavationTreasure> excavationFromGravel    = new ArrayList<ExcavationTreasure>();
    public List<ExcavationTreasure> excavationFromClay      = new ArrayList<ExcavationTreasure>();
    public List<ExcavationTreasure> excavationFromMycel     = new ArrayList<ExcavationTreasure>();
    public List<ExcavationTreasure> excavationFromSoulSand  = new ArrayList<ExcavationTreasure>();
    public List<ExcavationTreasure> excavationFromSnow      = new ArrayList<ExcavationTreasure>();

    public List<HylianTreasure> hylianFromBushes  = new ArrayList<HylianTreasure>();
    public List<HylianTreasure> hylianFromFlowers = new ArrayList<HylianTreasure>();
    public List<HylianTreasure> hylianFromPots    = new ArrayList<HylianTreasure>();

    public List<ShakeTreasure> shakeFromBlaze       = new ArrayList<ShakeTreasure>();
    public List<ShakeTreasure> shakeFromCaveSpider  = new ArrayList<ShakeTreasure>();
    public List<ShakeTreasure> shakeFromSpider      = new ArrayList<ShakeTreasure>();
    public List<ShakeTreasure> shakeFromChicken     = new ArrayList<ShakeTreasure>();
    public List<ShakeTreasure> shakeFromCow         = new ArrayList<ShakeTreasure>();
    public List<ShakeTreasure> shakeFromCreeper     = new ArrayList<ShakeTreasure>();
    public List<ShakeTreasure> shakeFromEnderman    = new ArrayList<ShakeTreasure>();
    public List<ShakeTreasure> shakeFromGhast       = new ArrayList<ShakeTreasure>();
    public List<ShakeTreasure> shakeFromHorse       = new ArrayList<ShakeTreasure>();
    public List<ShakeTreasure> shakeFromIronGolem   = new ArrayList<ShakeTreasure>();
    public List<ShakeTreasure> shakeFromMagmaCube   = new ArrayList<ShakeTreasure>();
    public List<ShakeTreasure> shakeFromMushroomCow = new ArrayList<ShakeTreasure>();
    public List<ShakeTreasure> shakeFromPig         = new ArrayList<ShakeTreasure>();
    public List<ShakeTreasure> shakeFromPigZombie   = new ArrayList<ShakeTreasure>();
    public List<ShakeTreasure> shakeFromSheep       = new ArrayList<ShakeTreasure>();
    public List<ShakeTreasure> shakeFromSkeleton    = new ArrayList<ShakeTreasure>();
    public List<ShakeTreasure> shakeFromSlime       = new ArrayList<ShakeTreasure>();
    public List<ShakeTreasure> shakeFromSnowman     = new ArrayList<ShakeTreasure>();
    public List<ShakeTreasure> shakeFromSquid       = new ArrayList<ShakeTreasure>();
    public List<ShakeTreasure> shakeFromWitch       = new ArrayList<ShakeTreasure>();
    public List<ShakeTreasure> shakeFromZombie      = new ArrayList<ShakeTreasure>();

    public List<FishingTreasure> fishingRewards = new ArrayList<FishingTreasure>();

    private TreasureConfig() {
        super("treasures.yml");
        loadKeys();
    }

    public static TreasureConfig getInstance() {
        if (instance == null) {
            instance = new TreasureConfig();
        }

        return instance;
    }

    @Override
    protected void loadKeys() {
        loadTreaures("Fishing");
        loadTreaures("Excavation");
        loadTreaures("Hylian");

        for (EntityType entity : EntityType.values()) {
            if (entity.isAlive()) {
                loadTreaures("Shake." + entity.toString());
            }
        }
    }

    private void loadTreaures(String type) {
        boolean isFishing = type.equals("Fishing");
        boolean isShake = type.contains("Shake");
        boolean isExcavation = type.equals("Excavation");
        boolean isHylian = type.equals("Hylian");

        ConfigurationSection treasureSection = config.getConfigurationSection(type);

        if (treasureSection == null) {
            return;
        }

        for (String treasureName : treasureSection.getKeys(false)) {
            // Validate all the things!
            List<String> reason = new ArrayList<String>();

            /*
             * Material, Amount, and Data
             */
            Material material = treasureName.contains("POTION") ? Material.POTION : Material.matchMaterial(treasureName);
            int amount = config.getInt(type + "." + treasureName + ".Amount");
            int data = config.getInt(type + "." + treasureName + ".Data");

            if (material == null) {
                reason.add("Invalid material: " + treasureName);
            }

            if (amount < 1) {
                reason.add("Invalid amount: " + amount);
            }

            if (material != null && material.isBlock() && (data > 127 || data < -128)) {
                reason.add("Invalid data: " + data);
            }

            /*
             * XP, Drop Chance, and Drop Level
             */

            int xp = config.getInt(type + "." + treasureName + ".XP");
            double dropChance = config.getDouble(type + "." + treasureName + ".Drop_Chance");
            int dropLevel = config.getInt(type + "." + treasureName + ".Drop_Level");

            if (xp < 0) {
                reason.add("Invalid xp: " + xp);
            }

            if (dropChance < 0.0D) {
                reason.add("Invalid Drop_Chance: " + dropChance);
            }

            if (dropLevel < 0) {
                reason.add("Invalid Drop_Level: " + dropLevel);
            }

            /*
             * Specific Types
             */
            int maxLevel = 0;

            if (isFishing) {
                maxLevel = config.getInt(type + "." + treasureName + ".Max_Level");

                if (maxLevel < -1) {
                    reason.add("Invalid Max_Level: " + maxLevel);
                }

                if (maxLevel != -1 && maxLevel < dropLevel) {
                    reason.add("Max_Level must be -1 or greater than Drop_Level!");
                }
            }

            /*
             * Itemstack
             */
            ItemStack item = null;

            if (treasureName.contains("POTION")) {
                String potionType = treasureName.substring(7);

                try {
                    item = new Potion(PotionType.valueOf(potionType.toUpperCase().trim())).toItemStack(amount);
                }
                catch (IllegalArgumentException ex) {
                    reason.add("Invalid Potion_Type: " + potionType);
                }
            }
            else if (config.contains(type + "." + treasureName + ".Dye_Color")) {
                String color = config.getString("Fishing." + treasureName + ".Dye_Color");

                try {
                    Dye dye = new Dye();
                    dye.setColor(DyeColor.valueOf(color.toUpperCase().trim()));

                    item = dye.toItemStack(amount);
                }
                catch (IllegalArgumentException ex) {
                    reason.add("Invalid Dye_Color: " + color);
                }
            }
            else {
                item = new ItemStack(material, amount, (short) data);
            }

            if (noErrorsInConfig(reason)) {
                if (isFishing) {
                    fishingRewards.add(new FishingTreasure(item, xp, dropChance, dropLevel, maxLevel));
                }
                else if (isShake) {
                    ShakeTreasure shakeTreasure = new ShakeTreasure(item, xp, dropChance, dropLevel);

                    if (type.equals("Shake.BLAZE")) {
                        shakeFromBlaze.add(shakeTreasure);
                    }
                    else if (type.equals("Shake.CAVE_SPIDER")) {
                        shakeFromCaveSpider.add(shakeTreasure);
                    }
                    else if (type.equals("Shake.CHICKEN")) {
                        shakeFromChicken.add(shakeTreasure);
                    }
                    else if (type.equals("Shake.COW")) {
                        shakeFromCow.add(shakeTreasure);
                    }
                    else if (type.equals("Shake.CREEPER")) {
                        shakeFromCreeper.add(shakeTreasure);
                    }
                    else if (type.equals("Shake.ENDERMAN")) {
                        shakeFromEnderman.add(shakeTreasure);
                    }
                    else if (type.equals("Shake.GHAST")) {
                        shakeFromGhast.add(shakeTreasure);
                    }
                    else if (type.equals("Shake.HORSE")) {
                        shakeFromHorse.add(shakeTreasure);
                    }
                    else if (type.equals("Shake.IRON_GOLEM")) {
                        shakeFromIronGolem.add(shakeTreasure);
                    }
                    else if (type.equals("Shake.MAGMA_CUBE")) {
                        shakeFromMagmaCube.add(shakeTreasure);
                    }
                    else if (type.equals("Shake.MUSHROOM_COW")) {
                        shakeFromMushroomCow.add(shakeTreasure);
                    }
                    else if (type.equals("Shake.PIG")) {
                        shakeFromPig.add(shakeTreasure);
                    }
                    else if (type.equals("Shake.PIG_ZOMBIE")) {
                        shakeFromPigZombie.add(shakeTreasure);
                    }
                    else if (type.equals("Shake.SHEEP")) {
                        shakeFromSheep.add(shakeTreasure);
                    }
                    else if (type.equals("Shake.SKELETON")) {
                        shakeFromSkeleton.add(shakeTreasure);
                    }
                    else if (type.equals("Shake.SLIME")) {
                        shakeFromSlime.add(shakeTreasure);
                    }
                    else if (type.equals("Shake.SPIDER")) {
                        shakeFromSpider.add(shakeTreasure);
                    }
                    else if (type.equals("Shake.SNOWMAN")) {
                        shakeFromSnowman.add(shakeTreasure);
                    }
                    else if (type.equals("Shake.SQUID")) {
                        shakeFromSquid.add(shakeTreasure);
                    }
                    else if (type.equals("Shake.WITCH")) {
                        shakeFromWitch.add(shakeTreasure);
                    }
                    else if (type.equals("Shake.ZOMBIE")) {
                        shakeFromZombie.add(shakeTreasure);
                    }
                }
                else if (isExcavation) {
                    ExcavationTreasure excavationTreasure = new ExcavationTreasure(item, xp, dropChance, dropLevel);
                    List<String> dropList = config.getStringList(type + "." + treasureName + ".Drops_From");

                    if (dropList.contains("Dirt")) {
                        excavationFromDirt.add(excavationTreasure);
                    }

                    if (dropList.contains("Grass")) {
                        excavationFromGrass.add(excavationTreasure);
                    }

                    if (dropList.contains("Sand")) {
                        excavationFromSand.add(excavationTreasure);
                    }

                    if (dropList.contains("Gravel")) {
                        excavationFromGravel.add(excavationTreasure);
                    }

                    if (dropList.contains("Clay")) {
                        excavationFromClay.add(excavationTreasure);
                    }

                    if (dropList.contains("Mycelium")) {
                        excavationFromMycel.add(excavationTreasure);
                    }

                    if (dropList.contains("Soul_Sand")) {
                        excavationFromSoulSand.add(excavationTreasure);
                    }

                    if (dropList.contains("Snow")) {
                        excavationFromSnow.add(excavationTreasure);
                    }
                }
                else if (isHylian) {
                    HylianTreasure hylianTreasure = new HylianTreasure(item, xp, dropChance, dropLevel);
                    List<String> dropList = config.getStringList(type + "." + treasureName + ".Drops_From");

                    if (dropList.contains("Bushes")) {
                        hylianFromBushes.add(hylianTreasure);
                    }

                    if (dropList.contains("Flowers")) {
                        hylianFromFlowers.add(hylianTreasure);
                    }

                    if (dropList.contains("Pots")) {
                        hylianFromPots.add(hylianTreasure);
                    }
                }
            }
        }
    }
}