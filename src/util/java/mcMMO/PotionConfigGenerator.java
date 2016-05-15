package com.gmail.nossr50;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Material;
import org.bukkit.potion.PotionType;

import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PotionConfigGenerator {

    public static class Ingredient {

        public Material mat;
        public int      data;
        public String   name;

        public Ingredient(Material mat) {
            this.mat = mat;
            this.data = 0;
            name = mat.name();
        }

        public Ingredient(Material mat, int data) {
            this.mat = mat;
            this.data = data;
            name = "'" + mat.name() + ":" + data + "'";
        }
    }
    public static class WriteablePotion {

        public String       name;
        public Material     mat;
        public PotionData   data;
        public PotionEffect effect;
        public String       baseName;

        public WriteablePotion(PotionData data) {
            this(Material.POTION, data);
        }

        public WriteablePotion(Material type, PotionData data) {
            this(type, data, null, getMCName(data.getType()));
        }

        public WriteablePotion(Material mat, PotionType type, PotionEffect effect, String baseName) {
            this(mat, new PotionData(type, false, false), effect, baseName);
        }

        public WriteablePotion(Material type, PotionData data, PotionEffect effect, String baseName) {
            this.data = data;
            this.effect = effect;
            this.mat = type;
            this.baseName = baseName;
            this.name = "POTION_OF_" + baseName;
            if(mat == Material.NETHER_WARTS){
                this.mat = Material.NETHER_STALK;
            }
            if (mat == Material.SPLASH_POTION) {
                this.name = "SPLASH_" + this.name;
            }
            if (mat == Material.LINGERING_POTION) {
                this.name = "LINGERING_" + this.name;
            }
            if (data.isExtended()) {
                this.name += "_EXTENDED";
            }
            if (data.isUpgraded()) {
                this.name += "_II";
            }
        }

        public WriteablePotion(PotionType type) {
            this(new PotionData(type, false, false));
        }

        public WriteablePotion(Material mat, PotionType type) {
            this(mat, new PotionData(type, false, false));
        }

        private static String getMCName(PotionType type) {
            switch (type) {
                case INSTANT_DAMAGE :
                    return "HARMING";
                case INSTANT_HEAL :
                    return "HEALING";
                case JUMP :
                    return "LEAPING";
                case REGEN :
                    return "REGENERATION";
                case SPEED :
                    return "SWIFTNESS";
                case UNCRAFTABLE :
                    return "EMPTY";
                case LUCK :
                case MUNDANE :
                case NIGHT_VISION :
                case POISON :
                case INVISIBILITY :
                case SLOWNESS :
                case AWKWARD :
                case STRENGTH :
                case THICK :
                case FIRE_RESISTANCE :
                case WATER :
                case WATER_BREATHING :
                case WEAKNESS :
                    return type.name();
                default :
                    return "";
            }
        }

        public int hashCode() {
            return name.hashCode();
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof WriteablePotion)) {
                return false;
            }
            return name.equals(((WriteablePotion) obj).name);
        }
    }

    public static void main(String[] args) throws IOException {
        Map<WriteablePotion, Map<Ingredient, WriteablePotion>> vanillaPotions = new HashMap<WriteablePotion, Map<Ingredient, WriteablePotion>>();
        populateVanillaPotions(vanillaPotions);
        Map<WriteablePotion, Map<Ingredient, WriteablePotion>> mcMMOPotions = new HashMap<WriteablePotion, Map<Ingredient, WriteablePotion>>();
        populateCustomPotions(mcMMOPotions);
        List<WriteablePotion> sorted = new ArrayList<WriteablePotion>();
        sorted.addAll(vanillaPotions.keySet());
        sorted.addAll(mcMMOPotions.keySet());
        sorted.sort(new Comparator<WriteablePotion>() {

            @Override
            public int compare(WriteablePotion a, WriteablePotion b) {
                // All normal potions first
                if (a.mat == Material.POTION && b.mat != Material.POTION) {
                    return -1;
                }
                if (b.mat == Material.POTION && a.mat != Material.POTION) {
                    return 1;
                }
                // All splash potions second
                if (a.mat == Material.SPLASH_POTION && b.mat != Material.SPLASH_POTION) {
                    return -1;
                }
                if (b.mat == Material.SPLASH_POTION && a.mat != Material.SPLASH_POTION) {
                    return 1;
                }
                // Vanilla Potions first
                if (a.effect == null && b.effect != null) {
                    return -1;
                }
                if (b.effect == null && a.effect != null) {
                    return 1;
                }
                // Vanilla potions
                if (a.effect == null && b.effect == null) {
                    // Order by PotionType
                    if (a.data.getType() != b.data.getType()) {
                        return Integer.compare(a.data.getType().ordinal(), b.data.getType().ordinal());
                    }
                    // Plain before extended or upgraded
                    if (!a.data.isExtended() && !a.data.isUpgraded() && (b.data.isExtended() || b.data.isUpgraded())) {
                        return -1;
                    }
                    if (!b.data.isExtended() && !b.data.isUpgraded() && (a.data.isExtended() || a.data.isUpgraded())) {
                        return 1;
                    }
                    // Extended before Upgraded
                    if (a.data.isExtended() && b.data.isUpgraded()) {
                        return -1;
                    }
                    if (b.data.isExtended() && a.data.isUpgraded()) {
                        return -1;
                    }
                    // Same potion somehow?
                    return 0;
                }
                // mcMMO Potions
                else {
                    if ((a.baseName.contains("II") || a.baseName.contains("EXTENDED")) && !(b.baseName.contains("II") || b.baseName.contains("EXTENDED"))) {
                        return 1;
                    }
                    if ((b.baseName.contains("II") || b.baseName.contains("EXTENDED")) && !(a.baseName.contains("II") || a.baseName.contains("EXTENDED"))) {
                        return -1;
                    }
                    if (!a.baseName.contains("II") && b.baseName.contains("II")) {
                        return -1;
                    }
                    if (!b.baseName.contains("II") && a.baseName.contains("II")) {
                        return 1;
                    }
                    return a.baseName.split("_")[0].compareTo(b.baseName.split("_")[0]);
                }
            }

        });
        for (WriteablePotion potion : sorted) {
            System.out.println("    " + potion.name + ":");
            Map<Ingredient, WriteablePotion> children;
            if (vanillaPotions.containsKey(potion)) {
                children = vanillaPotions.get(potion);
            } else {
                System.out.println("        Name: " + prettify(potion.name));
                children = mcMMOPotions.get(potion);
            }
            System.out.println("        Material: " + potion.mat.name());
            System.out.println("        PotionData:");
            System.out.println("            PotionType: " + potion.data.getType().name());
            if (potion.data.isExtended()) {
                System.out.println("            Extended: true");
            }
            if (potion.data.isUpgraded()) {
                System.out.println("            Upgraded: true");
            }
            if (potion.effect != null) {
                System.out.println("        Effects: [\"" + getName(potion.effect.getType()) + " " + potion.effect.getAmplifier() + " " + potion.effect.getDuration() + "\"]");
            }
            if (children == null || children.isEmpty()) {
                continue;
            }
            System.out.println("        Children:");
            for (Entry<Ingredient, WriteablePotion> child : children.entrySet()) {
                System.out.println("            " + child.getKey().name + ": " + child.getValue().name);
            }
        }
    }

    private static String prettify(String name) {
        String[] substrings = name.split("_");
        String prettyString = "";
        int size = 1;

        for (String string : substrings) {
            prettyString = prettyString.concat(getCapitalized(string));

            if (size < substrings.length) {
                prettyString = prettyString.concat(" ");
            }

            size++;
        }

        return prettyString;
    }

    public static String getCapitalized(String target) {
        if (target.equals("II")) { // hacks
            return target;
        }
        return target.substring(0, 1).toUpperCase() + target.substring(1).toLowerCase();
    }

    private static String getName(PotionEffectType type) {
        switch (type.getId()) {
            case 1 :
                return "SPEED";
            case 2 :
                return "SLOW";
            case 3 :
                return "FAST_DIGGING";
            case 4 :
                return "SLOW_DIGGING";
            case 5 :
                return "INCREASE_DAMAGE";
            case 6 :
                return "HEAL";
            case 7 :
                return "HARM";
            case 8 :
                return "JUMP";
            case 9 :
                return "CONFUSION";
            case 10 :
                return "REGENERATION";
            case 11 :
                return "DAMAGE_RESISTANCE";
            case 12 :
                return "FIRE_RESISTANCE";
            case 13 :
                return "WATER_BREATHING";
            case 14 :
                return "INVISIBILITY";
            case 15 :
                return "BLINDNESS";
            case 16 :
                return "NIGHT_VISION";
            case 17 :
                return "HUNGER";
            case 18 :
                return "WEAKNESS";
            case 19 :
                return "POISON";
            case 20 :
                return "WITHER";
            case 21 :
                return "HEALTH_BOOST";
            case 22 :
                return "ABSORPTION";
            case 23 :
                return "SATURATION";
            case 24 :
                return "GLOWING";
            case 25 :
                return "LEVITATION";
            case 26 :
                return "LUCK";
            case 27 :
                return "UNLUCK";
            default :
                return "UNKNOWN_EFFECT_TYPE_" + type.getId();
        }
    }

    private static void populateVanillaPotions(Map<WriteablePotion, Map<Ingredient, WriteablePotion>> vanillaPotions) {
        for (PotionType type : PotionType.values()) {
            for (Material material : new Material[]{Material.POTION, Material.SPLASH_POTION, Material.LINGERING_POTION}) {
                WriteablePotion data = new WriteablePotion(material, type);
                HashMap<Ingredient, WriteablePotion> children = new HashMap<Ingredient, WriteablePotion>();
                getChildren(data, children);
                vanillaPotions.put(data, children);
                if (type.isExtendable()) {
                    data = new WriteablePotion(material, new PotionData(type, true, false));
                    children = new HashMap<Ingredient, WriteablePotion>();
                    getChildren(data, children);
                    vanillaPotions.put(data, children);
                }
                if (type.isUpgradeable()) {
                    data = new WriteablePotion(material, new PotionData(type, false, true));
                    children = new HashMap<Ingredient, WriteablePotion>();
                    getChildren(data, children);
                    vanillaPotions.put(data, children);
                }
            }
        }
        for (Entry<WriteablePotion, Map<Ingredient, WriteablePotion>> entry : vanillaPotions.entrySet()) {
            if (entry.getKey().mat == Material.POTION) {
                entry.getValue().put(new Ingredient(Material.SULPHUR), new WriteablePotion(Material.SPLASH_POTION, entry.getKey().data));
            }
            if (entry.getKey().mat == Material.SPLASH_POTION) {
                entry.getValue().put(new Ingredient(Material.DRAGONS_BREATH), new WriteablePotion(Material.LINGERING_POTION, entry.getKey().data));
            }
        }
    }

    private static void getChildren(WriteablePotion current, HashMap<Ingredient, WriteablePotion> children) {
        switch (current.data.getType()) {
            case WATER :
                assert(!current.data.isExtended());
                assert(!current.data.isUpgraded());
                children.put(new Ingredient(Material.NETHER_STALK), new WriteablePotion(current.mat, PotionType.AWKWARD));
                children.put(new Ingredient(Material.FERMENTED_SPIDER_EYE), new WriteablePotion(current.mat, PotionType.WEAKNESS));
                children.put(new Ingredient(Material.REDSTONE), new WriteablePotion(current.mat, PotionType.MUNDANE));
                children.put(new Ingredient(Material.GLOWSTONE_DUST), new WriteablePotion(current.mat, PotionType.THICK));
                children.put(new Ingredient(Material.BLAZE_POWDER), new WriteablePotion(current.mat, PotionType.MUNDANE));
                children.put(new Ingredient(Material.SUGAR), new WriteablePotion(current.mat, PotionType.MUNDANE));
                children.put(new Ingredient(Material.RABBIT_FOOT), new WriteablePotion(current.mat, PotionType.MUNDANE));
                children.put(new Ingredient(Material.SPIDER_EYE), new WriteablePotion(current.mat, PotionType.MUNDANE));
                children.put(new Ingredient(Material.MAGMA_CREAM), new WriteablePotion(current.mat, PotionType.MUNDANE));
                children.put(new Ingredient(Material.SPECKLED_MELON), new WriteablePotion(current.mat, PotionType.MUNDANE));
                children.put(new Ingredient(Material.GHAST_TEAR), new WriteablePotion(current.mat, PotionType.MUNDANE));
                return;
            case AWKWARD :
                assert(!current.data.isExtended());
                assert(!current.data.isUpgraded());
                children.put(new Ingredient(Material.GOLDEN_CARROT), new WriteablePotion(current.mat, PotionType.NIGHT_VISION));
                children.put(new Ingredient(Material.RABBIT_FOOT), new WriteablePotion(current.mat, PotionType.JUMP));
                children.put(new Ingredient(Material.MAGMA_CREAM), new WriteablePotion(current.mat, PotionType.FIRE_RESISTANCE));
                children.put(new Ingredient(Material.SUGAR), new WriteablePotion(current.mat, PotionType.SPEED));
                children.put(new Ingredient(Material.RAW_FISH, 3), new WriteablePotion(current.mat, PotionType.WATER_BREATHING));
                children.put(new Ingredient(Material.SPECKLED_MELON), new WriteablePotion(current.mat, PotionType.INSTANT_HEAL));
                children.put(new Ingredient(Material.SPIDER_EYE), new WriteablePotion(current.mat, PotionType.POISON));
                children.put(new Ingredient(Material.GHAST_TEAR), new WriteablePotion(current.mat, PotionType.REGEN));
                children.put(new Ingredient(Material.BLAZE_POWDER), new WriteablePotion(current.mat, PotionType.STRENGTH));
                // mcMMO custom potions
                double mod = 1;
                if (current.mat == Material.SPLASH_POTION) {
                    mod = 0.75;
                }
                if (current.mat == Material.LINGERING_POTION) {
                    mod = 0.25;
                }
                children.put(new Ingredient(Material.BROWN_MUSHROOM), new WriteablePotion(current.mat, PotionType.UNCRAFTABLE, new PotionEffect(PotionEffectType.CONFUSION, (int) (450 * mod), 0), "NAUSEA"));
                children.put(new Ingredient(Material.CARROT_ITEM), new WriteablePotion(current.mat, PotionType.UNCRAFTABLE, new PotionEffect(PotionEffectType.FAST_DIGGING, (int) (3600 * mod), 0), "HASTE"));
                children.put(new Ingredient(Material.SLIME_BALL), new WriteablePotion(current.mat, PotionType.UNCRAFTABLE, new PotionEffect(PotionEffectType.SLOW_DIGGING, (int) (3600 * mod), 0), "DULLNESS"));
                children.put(new Ingredient(Material.GOLDEN_APPLE), new WriteablePotion(current.mat, PotionType.UNCRAFTABLE, new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, (int) (450 * mod), 0), "RESISTANCE"));
                children.put(new Ingredient(Material.INK_SACK), new WriteablePotion(current.mat, PotionType.UNCRAFTABLE, new PotionEffect(PotionEffectType.BLINDNESS, (int) (225 * mod), 0), "BLINDNESS"));
                children.put(new Ingredient(Material.ROTTEN_FLESH), new WriteablePotion(current.mat, PotionType.UNCRAFTABLE, new PotionEffect(PotionEffectType.HUNGER, (int) (900 * mod), 0), "HUNGER"));
                children.put(new Ingredient(Material.POISONOUS_POTATO), new WriteablePotion(current.mat, PotionType.UNCRAFTABLE, new PotionEffect(PotionEffectType.WITHER, (int) (450 * mod), 0), "DECAY"));
                children.put(new Ingredient(Material.QUARTZ), new WriteablePotion(current.mat, PotionType.UNCRAFTABLE, new PotionEffect(PotionEffectType.ABSORPTION, (int) (1800 * mod), 0), "ABSORPTION"));
                children.put(new Ingredient(Material.LONG_GRASS, 2), new WriteablePotion(current.mat, PotionType.UNCRAFTABLE, new PotionEffect(PotionEffectType.SATURATION, (int) (8 * mod), 0), "SATURATION"));
                children.put(new Ingredient(Material.APPLE), new WriteablePotion(current.mat, PotionType.UNCRAFTABLE, new PotionEffect(PotionEffectType.HEALTH_BOOST, (int) (1800 * mod), 0), "HEALTH_BOOST"));
                return;
            case FIRE_RESISTANCE :
                assert(!current.data.isUpgraded());
                if (current.data.isExtended()) {
                    children.put(new Ingredient(Material.FERMENTED_SPIDER_EYE), new WriteablePotion(current.mat, new PotionData(PotionType.SLOWNESS, true, false)));
                } else {
                    children.put(new Ingredient(Material.FERMENTED_SPIDER_EYE), new WriteablePotion(current.mat, PotionType.SLOWNESS));
                    children.put(new Ingredient(Material.REDSTONE), new WriteablePotion(current.mat, new PotionData(current.data.getType(), true, false)));
                }
                return;
            case INSTANT_DAMAGE :
                assert(!current.data.isExtended());
                if (!current.data.isUpgraded()) {
                    children.put(new Ingredient(Material.GLOWSTONE_DUST), new WriteablePotion(current.mat, new PotionData(current.data.getType(), false, true)));
                }
                return;
            case INSTANT_HEAL :
                assert(!current.data.isExtended());
                if (!current.data.isUpgraded()) {
                    children.put(new Ingredient(Material.FERMENTED_SPIDER_EYE), new WriteablePotion(current.mat, PotionType.INSTANT_DAMAGE));
                    children.put(new Ingredient(Material.GLOWSTONE_DUST), new WriteablePotion(current.mat, new PotionData(current.data.getType(), false, true)));
                } else {
                    children.put(new Ingredient(Material.FERMENTED_SPIDER_EYE), new WriteablePotion(current.mat, new PotionData(PotionType.INSTANT_DAMAGE, false, true)));
                }
                return;
            case INVISIBILITY :
                assert(!current.data.isUpgraded());
                if (!current.data.isExtended()) {
                    children.put(new Ingredient(Material.REDSTONE), new WriteablePotion(current.mat, new PotionData(current.data.getType(), true, false)));
                }
                return;
            case JUMP :
                if (!current.data.isUpgraded() && !current.data.isExtended()) {
                    children.put(new Ingredient(Material.FERMENTED_SPIDER_EYE), new WriteablePotion(current.mat, PotionType.SLOWNESS));
                    children.put(new Ingredient(Material.GLOWSTONE_DUST), new WriteablePotion(current.mat, new PotionData(current.data.getType(), false, true)));
                    children.put(new Ingredient(Material.REDSTONE), new WriteablePotion(current.mat, new PotionData(current.data.getType(), true, false)));
                }
                return;
            case NIGHT_VISION :
                assert(!current.data.isUpgraded());
                if (!current.data.isExtended()) {
                    children.put(new Ingredient(Material.FERMENTED_SPIDER_EYE), new WriteablePotion(current.mat, PotionType.INVISIBILITY));
                    children.put(new Ingredient(Material.REDSTONE), new WriteablePotion(current.mat, new PotionData(current.data.getType(), true, false)));
                } else {
                    children.put(new Ingredient(Material.FERMENTED_SPIDER_EYE), new WriteablePotion(current.mat, new PotionData(PotionType.INVISIBILITY, true, false)));
                }
                return;
            case POISON :
                if (!current.data.isUpgraded() && !current.data.isExtended()) {
                    children.put(new Ingredient(Material.FERMENTED_SPIDER_EYE), new WriteablePotion(current.mat, PotionType.INSTANT_DAMAGE));
                    children.put(new Ingredient(Material.GLOWSTONE_DUST), new WriteablePotion(current.mat, new PotionData(current.data.getType(), false, true)));
                    children.put(new Ingredient(Material.REDSTONE), new WriteablePotion(current.mat, new PotionData(current.data.getType(), true, false)));
                } else {
                    children.put(new Ingredient(Material.FERMENTED_SPIDER_EYE), new WriteablePotion(current.mat, new PotionData(PotionType.INSTANT_DAMAGE, false, true)));
                }
                return;
            case REGEN :
                if (!current.data.isUpgraded() && !current.data.isExtended()) {
                    children.put(new Ingredient(Material.GLOWSTONE_DUST), new WriteablePotion(current.mat, new PotionData(current.data.getType(), false, true)));
                    children.put(new Ingredient(Material.REDSTONE), new WriteablePotion(current.mat, new PotionData(current.data.getType(), true, false)));
                }
                return;
            case SLOWNESS :
                assert(!current.data.isUpgraded());
                if (!current.data.isExtended()) {
                    children.put(new Ingredient(Material.REDSTONE), new WriteablePotion(current.mat, new PotionData(current.data.getType(), true, false)));
                }
                return;
            case SPEED :
                if (!current.data.isUpgraded() && !current.data.isExtended()) {
                    children.put(new Ingredient(Material.FERMENTED_SPIDER_EYE), new WriteablePotion(current.mat, PotionType.SLOWNESS));
                    children.put(new Ingredient(Material.GLOWSTONE_DUST), new WriteablePotion(current.mat, new PotionData(current.data.getType(), false, true)));
                    children.put(new Ingredient(Material.REDSTONE), new WriteablePotion(current.mat, new PotionData(current.data.getType(), true, false)));
                } else {
                    children.put(new Ingredient(Material.FERMENTED_SPIDER_EYE), new WriteablePotion(current.mat, new PotionData(PotionType.SLOWNESS, true, false)));
                }
                return;
            case STRENGTH :
                if (!current.data.isUpgraded() && !current.data.isExtended()) {
                    children.put(new Ingredient(Material.GLOWSTONE_DUST), new WriteablePotion(current.mat, new PotionData(current.data.getType(), false, true)));
                    children.put(new Ingredient(Material.REDSTONE), new WriteablePotion(current.mat, new PotionData(current.data.getType(), true, false)));
                }
                return;
            case WATER_BREATHING :
                assert(!current.data.isUpgraded());
                if (!current.data.isExtended()) {
                    children.put(new Ingredient(Material.FERMENTED_SPIDER_EYE), new WriteablePotion(current.mat, PotionType.INSTANT_DAMAGE));
                    children.put(new Ingredient(Material.REDSTONE), new WriteablePotion(current.mat, new PotionData(current.data.getType(), true, false)));
                }
                return;
            case WEAKNESS :
                assert(!current.data.isUpgraded());
                if (!current.data.isExtended()) {
                    children.put(new Ingredient(Material.REDSTONE), new WriteablePotion(current.mat, new PotionData(current.data.getType(), true, false)));
                }
                return;
            case LUCK :
            case MUNDANE :
            case THICK :
            case UNCRAFTABLE :
                assert(!current.data.isExtended());
                assert(!current.data.isUpgraded());
                return;
            default :
                assert(false);
                break;
        }
    }

    private static void populateCustomPotions(Map<WriteablePotion, Map<Ingredient, WriteablePotion>> mcMMOPotions) {
        for (Material material : new Material[]{Material.POTION, Material.SPLASH_POTION, Material.LINGERING_POTION}) {
            WriteablePotion data = new WriteablePotion(material, PotionType.AWKWARD);
            double mod = 1;
            if (material == Material.SPLASH_POTION) {
                mod = 0.75;
            }
            if (material == Material.LINGERING_POTION) {
                mod = 0.25;
            }
            HashMap<Ingredient, WriteablePotion> children = new HashMap<Ingredient, WriteablePotion>();
            data = new WriteablePotion(material, PotionType.UNCRAFTABLE, new PotionEffect(PotionEffectType.CONFUSION, (int) (450 * mod), 0), "NAUSEA");
            children = new HashMap<Ingredient, WriteablePotion>();
            children.put(new Ingredient(Material.GLOWSTONE_DUST), new WriteablePotion(material, PotionType.UNCRAFTABLE, new PotionEffect(data.effect.getType(), data.effect.getDuration() / 2, 1), data.baseName + "_II"));
            children.put(new Ingredient(Material.REDSTONE), new WriteablePotion(material, PotionType.UNCRAFTABLE, new PotionEffect(data.effect.getType(), data.effect.getDuration() * 2, 0), data.baseName + "_EXTENDED"));
            for (WriteablePotion child : children.values()) {
                mcMMOPotions.put(child, new HashMap<Ingredient, WriteablePotion>());
            }
            mcMMOPotions.put(data, children);
            data = new WriteablePotion(material, PotionType.UNCRAFTABLE, new PotionEffect(PotionEffectType.FAST_DIGGING, (int) (3600 * mod), 0), "HASTE");
            children = new HashMap<Ingredient, WriteablePotion>();
            children.put(new Ingredient(Material.GLOWSTONE_DUST), new WriteablePotion(material, PotionType.UNCRAFTABLE, new PotionEffect(data.effect.getType(), data.effect.getDuration() / 2, 1), data.baseName + "_II"));
            children.put(new Ingredient(Material.REDSTONE), new WriteablePotion(material, PotionType.UNCRAFTABLE, new PotionEffect(data.effect.getType(), data.effect.getDuration() * 2, 0), data.baseName + "_EXTENDED"));
            for (WriteablePotion child : children.values()) {
                mcMMOPotions.put(child, new HashMap<Ingredient, WriteablePotion>());
            }
            mcMMOPotions.put(data, children);
            data = new WriteablePotion(material, PotionType.UNCRAFTABLE, new PotionEffect(PotionEffectType.SLOW_DIGGING, (int) (3600 * mod), 0), "DULLNESS");
            children = new HashMap<Ingredient, WriteablePotion>();
            children.put(new Ingredient(Material.GLOWSTONE_DUST), new WriteablePotion(material, PotionType.UNCRAFTABLE, new PotionEffect(data.effect.getType(), data.effect.getDuration() / 2, 1), data.baseName + "_II"));
            children.put(new Ingredient(Material.REDSTONE), new WriteablePotion(material, PotionType.UNCRAFTABLE, new PotionEffect(data.effect.getType(), data.effect.getDuration() * 2, 0), data.baseName + "_EXTENDED"));
            for (WriteablePotion child : children.values()) {
                mcMMOPotions.put(child, new HashMap<Ingredient, WriteablePotion>());
            }
            mcMMOPotions.put(data, children);
            data = new WriteablePotion(material, PotionType.UNCRAFTABLE, new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, (int) (450 * mod), 0), "RESISTANCE");
            children = new HashMap<Ingredient, WriteablePotion>();
            children.put(new Ingredient(Material.GLOWSTONE_DUST), new WriteablePotion(material, PotionType.UNCRAFTABLE, new PotionEffect(data.effect.getType(), data.effect.getDuration() / 2, 1), data.baseName + "_II"));
            children.put(new Ingredient(Material.REDSTONE), new WriteablePotion(material, PotionType.UNCRAFTABLE, new PotionEffect(data.effect.getType(), data.effect.getDuration() * 2, 0), data.baseName + "_EXTENDED"));
            for (WriteablePotion child : children.values()) {
                mcMMOPotions.put(child, new HashMap<Ingredient, WriteablePotion>());
            }
            mcMMOPotions.put(data, children);
            data = new WriteablePotion(material, PotionType.UNCRAFTABLE, new PotionEffect(PotionEffectType.BLINDNESS, (int) (225 * mod), 0), "BLINDNESS");
            children = new HashMap<Ingredient, WriteablePotion>();
            children.put(new Ingredient(Material.GLOWSTONE_DUST), new WriteablePotion(material, PotionType.UNCRAFTABLE, new PotionEffect(data.effect.getType(), data.effect.getDuration() / 2, 1), data.baseName + "_II"));
            children.put(new Ingredient(Material.REDSTONE), new WriteablePotion(material, PotionType.UNCRAFTABLE, new PotionEffect(data.effect.getType(), data.effect.getDuration() * 2, 0), data.baseName + "_EXTENDED"));
            for (WriteablePotion child : children.values()) {
                mcMMOPotions.put(child, new HashMap<Ingredient, WriteablePotion>());
            }
            mcMMOPotions.put(data, children);
            data = new WriteablePotion(material, PotionType.UNCRAFTABLE, new PotionEffect(PotionEffectType.HUNGER, (int) (900 * mod), 0), "HUNGER");
            children = new HashMap<Ingredient, WriteablePotion>();
            children.put(new Ingredient(Material.GLOWSTONE_DUST), new WriteablePotion(material, PotionType.UNCRAFTABLE, new PotionEffect(data.effect.getType(), data.effect.getDuration() / 2, 1), data.baseName + "_II"));
            children.put(new Ingredient(Material.REDSTONE), new WriteablePotion(material, PotionType.UNCRAFTABLE, new PotionEffect(data.effect.getType(), data.effect.getDuration() * 2, 0), data.baseName + "_EXTENDED"));
            for (WriteablePotion child : children.values()) {
                mcMMOPotions.put(child, new HashMap<Ingredient, WriteablePotion>());
            }
            mcMMOPotions.put(data, children);
            data = new WriteablePotion(material, PotionType.UNCRAFTABLE, new PotionEffect(PotionEffectType.WITHER, (int) (450 * mod), 0), "DECAY");
            children = new HashMap<Ingredient, WriteablePotion>();
            children.put(new Ingredient(Material.GLOWSTONE_DUST), new WriteablePotion(material, PotionType.UNCRAFTABLE, new PotionEffect(data.effect.getType(), data.effect.getDuration() / 2, 1), data.baseName + "_II"));
            children.put(new Ingredient(Material.REDSTONE), new WriteablePotion(material, PotionType.UNCRAFTABLE, new PotionEffect(data.effect.getType(), data.effect.getDuration() * 2, 0), data.baseName + "_EXTENDED"));
            for (WriteablePotion child : children.values()) {
                mcMMOPotions.put(child, new HashMap<Ingredient, WriteablePotion>());
            }
            mcMMOPotions.put(data, children);
            data = new WriteablePotion(material, PotionType.UNCRAFTABLE, new PotionEffect(PotionEffectType.ABSORPTION, (int) (1800 * mod), 0), "ABSORPTION");
            children = new HashMap<Ingredient, WriteablePotion>();
            children.put(new Ingredient(Material.GLOWSTONE_DUST), new WriteablePotion(material, PotionType.UNCRAFTABLE, new PotionEffect(data.effect.getType(), data.effect.getDuration() / 2, 1), data.baseName + "_II"));
            children.put(new Ingredient(Material.REDSTONE), new WriteablePotion(material, PotionType.UNCRAFTABLE, new PotionEffect(data.effect.getType(), data.effect.getDuration() * 2, 0), data.baseName + "_EXTENDED"));
            for (WriteablePotion child : children.values()) {
                mcMMOPotions.put(child, new HashMap<Ingredient, WriteablePotion>());
            }
            mcMMOPotions.put(data, children);
            data = new WriteablePotion(material, PotionType.UNCRAFTABLE, new PotionEffect(PotionEffectType.SATURATION, (int) (8 * mod), 0), "SATURATION");
            children = new HashMap<Ingredient, WriteablePotion>();
            children.put(new Ingredient(Material.GLOWSTONE_DUST), new WriteablePotion(material, PotionType.UNCRAFTABLE, new PotionEffect(data.effect.getType(), data.effect.getDuration() / 2, 1), data.baseName + "_II"));
            children.put(new Ingredient(Material.REDSTONE), new WriteablePotion(material, PotionType.UNCRAFTABLE, new PotionEffect(data.effect.getType(), data.effect.getDuration() * 2, 0), data.baseName + "_EXTENDED"));
            for (WriteablePotion child : children.values()) {
                mcMMOPotions.put(child, new HashMap<Ingredient, WriteablePotion>());
            }
            mcMMOPotions.put(data, children);
            data = new WriteablePotion(material, PotionType.UNCRAFTABLE, new PotionEffect(PotionEffectType.HEALTH_BOOST, (int) (1800 * mod), 0), "HEALTH_BOOST");
            children = new HashMap<Ingredient, WriteablePotion>();
            children.put(new Ingredient(Material.GLOWSTONE_DUST), new WriteablePotion(material, PotionType.UNCRAFTABLE, new PotionEffect(data.effect.getType(), data.effect.getDuration() / 2, 1), data.baseName + "_II"));
            children.put(new Ingredient(Material.REDSTONE), new WriteablePotion(material, PotionType.UNCRAFTABLE, new PotionEffect(data.effect.getType(), data.effect.getDuration() * 2, 0), data.baseName + "_EXTENDED"));
            for (WriteablePotion child : children.values()) {
                mcMMOPotions.put(child, new HashMap<Ingredient, WriteablePotion>());
            }
            mcMMOPotions.put(data, children);
        }
        
        // Add all material state changes
        for (Entry<WriteablePotion, Map<Ingredient, WriteablePotion>> entry : mcMMOPotions.entrySet()) {
            if (entry.getKey().mat == Material.POTION) {
                PotionEffect effect = new PotionEffect(entry.getKey().effect.getType(), (int) (entry.getKey().effect.getDuration() * 0.75), entry.getKey().effect.getAmplifier());
                entry.getValue().put(new Ingredient(Material.SULPHUR), new WriteablePotion(Material.SPLASH_POTION, entry.getKey().data, effect, entry.getKey().baseName));
            } else if (entry.getKey().mat == Material.SPLASH_POTION) {
                PotionEffect effect = new PotionEffect(entry.getKey().effect.getType(), (int) (entry.getKey().effect.getDuration() * 0.33), entry.getKey().effect.getAmplifier());
                entry.getValue().put(new Ingredient(Material.DRAGONS_BREATH), new WriteablePotion(Material.LINGERING_POTION, entry.getKey().data, effect, entry.getKey().baseName));
            }
        }
    }
}
