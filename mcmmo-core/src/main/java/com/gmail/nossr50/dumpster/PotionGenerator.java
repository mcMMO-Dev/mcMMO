//package com.gmail.nossr50.skills.alchemy;
//
//import com.gmail.nossr50.datatypes.skills.alchemy.AlchemyPotion;
//import com.gmail.nossr50.mcMMO;
//import org.bukkit.ChatColor;
//import org.bukkit.Color;
//import org.bukkit.Material;
//import org.bukkit.potion.PotionData;
//import org.bukkit.potion.PotionEffect;
//import org.bukkit.potion.PotionEffectType;
//import org.bukkit.potion.PotionType;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Map.Entry;
//
///**
// * This class is an abomination
// * Alchemy will be rewritten, unfortunately during 2.2 (the config rewrite) I stumbled upon this class
// * This class is far from proper, however due to the nature of how the current implementation of Alchemy works, it'd be a royal pain to rewrite this class to "fix" it
// * Especially considering I plan to rewrite Alchemy, it does not seem worth the time involved to fix this monster
// *
// * Shield your eyes
// * It was worse before I cleaned it up
// */
//public class PotionGenerator {
//    private HashMap<String, AlchemyPotion> potionHashMap;
////    private HashMap<ProtoPotion, HashMap<Ingredient, ProtoPotion>> childPotionMap; //Yuck
//
//    public PotionGenerator() {
//        potionHashMap = new HashMap<>();
////        childPotionMap = new HashMap<>();
//        init();
//    }
//
//    private void init() {
//        Map<ProtoPotion, Map<Ingredient, ProtoPotion>> vanillaPotions = new HashMap<>();
//        Map<ProtoPotion, Map<Ingredient, ProtoPotion>> mcMMOPotions = new HashMap<>();
//
//        populateVanillaPotions(vanillaPotions);
//        populateCustomPotions(mcMMOPotions);
//
//        List<ProtoPotion> sorted = new ArrayList<>();
//
//        sorted.addAll(vanillaPotions.keySet());
//        sorted.addAll(mcMMOPotions.keySet());
//
//        sorted.sort((a, b) -> {
//            // All normal potions first
//            if (a.mat == Material.POTION && b.mat != Material.POTION) {
//                return -1;
//            }
//            if (b.mat == Material.POTION && a.mat != Material.POTION) {
//                return 1;
//            }
//            // All splash potions second
//            if (a.mat == Material.SPLASH_POTION && b.mat != Material.SPLASH_POTION) {
//                return -1;
//            }
//            if (b.mat == Material.SPLASH_POTION && a.mat != Material.SPLASH_POTION) {
//                return 1;
//            }
//            // Vanilla Potions first
//            if (a.effect == null && b.effect != null) {
//                return -1;
//            }
//            if (b.effect == null && a.effect != null) {
//                return 1;
//            }
//            // Vanilla potions
//            if (a.effect == null && b.effect == null) {
//                // Order by PotionType
//                if (a.data.getType() != b.data.getType()) {
//                    return Integer.compare(a.data.getType().ordinal(), b.data.getType().ordinal());
//                }
//                // Plain before extended or upgraded
//                if (!a.data.isExtended() && !a.data.isUpgraded() && (b.data.isExtended() || b.data.isUpgraded())) {
//                    return -1;
//                }
//                if (!b.data.isExtended() && !b.data.isUpgraded() && (a.data.isExtended() || a.data.isUpgraded())) {
//                    return 1;
//                }
//                // Extended before Upgraded
//                if (a.data.isExtended() && b.data.isUpgraded()) {
//                    return -1;
//                }
//                if (b.data.isExtended() && a.data.isUpgraded()) {
//                    return -1;
//                }
//                // Same potion somehow?
//                return 0;
//            }
//            // mcMMO Potions
//            else {
//                if ((a.baseName.contains("II") || a.baseName.contains("EXTENDED")) && !(b.baseName.contains("II") || b.baseName.contains("EXTENDED"))) {
//                    return 1;
//                }
//                if ((b.baseName.contains("II") || b.baseName.contains("EXTENDED")) && !(a.baseName.contains("II") || a.baseName.contains("EXTENDED"))) {
//                    return -1;
//                }
//                if (!a.baseName.contains("II") && b.baseName.contains("II")) {
//                    return -1;
//                }
//                if (!b.baseName.contains("II") && a.baseName.contains("II")) {
//                    return 1;
//                }
//                return a.baseName.split("_")[0].compareTo(b.baseName.split("_")[0]);
//            }
//        });
//
//        /* Hacky solution, this entire class disgusts me */
//        HashMap<String, AlchemyPotion> potionHashMap = new HashMap<>();
//
//        for(ProtoPotion potion : sorted)
//        {
//            AlchemyPotion alchemyPotion;
//
//            if(vanillaPotions.containsKey(potion)) {
//                potion getChildren(potion);
//
//            } else {
//                getChildren(potion);
//            }
//            potionHashMap.put(alchemyPotion.getName(), alchemyPotion);
//        }
//
////        for (ProtoPotion potion : sorted) {
////            System.out.println("    " + potion.name + ":");
////            Map<Ingredient, ProtoPotion> children;
////            if (vanillaPotions.containsKey(potion)) {
////                children = vanillaPotions.get(potion);
////            } else {
////                System.out.println("        Name: " + prettify(potion.name));
////                children = mcMMOPotions.get(potion);
////            }
////            System.out.println("        Material: " + potion.mat.name());
////            System.out.println("        PotionData:");
////            System.out.println("            PotionType: " + potion.data.getType().name());
////            if (potion.data.isExtended()) {
////                System.out.println("            Extended: true");
////            }
////            if (potion.data.isUpgraded()) {
////                System.out.println("            Upgraded: true");
////            }
////            if (potion.effect != null) {
////                System.out.println("        Effects: [\"" + getName(potion.effect.getType()) + " " + potion.effect.getAmplifier() + " " + potion.effect.getDuration() + "\"]");
////            }
////            if (children == null || children.isEmpty()) {
////                continue;
////            }
////            System.out.println("        Children:");
////            for (Entry<Ingredient, ProtoPotion> child : children.entrySet()) {
////                System.out.println("            " + child.getKey().name + ": " + child.getValue().name);
////            }
////        }
//    }
//
//    public HashMap<String, AlchemyPotion> getPotionHashMap() {
//        return potionHashMap;
//    }
//
//    /**
//     * I just want anyone who reads this to know
//     * This entire class is an abomination
//     * What you see below is a hacky solution to keep Alchemy functioning with the new config system
//     * Alchemy will be rewritten, until then, this disgusting class exists.
//     * @param protoPotion target ProtoPotion
//     * @return converted ProtoPotion
//     */
//    private AlchemyPotion convertWriteableToAlchemyPotion(ProtoPotion protoPotion, ) {
//        try {
//
//            String name = protoPotion.name;
//
//            if (name != null) {
//                name = prettify(ChatColor.translateAlternateColorCodes('&', name));
//            }
//
//            PotionData data = protoPotion.data;
//            Material material = Material.POTION;
//
//            if(protoPotion.mat != null)
//                material = protoPotion.mat;
//
//            //Lore is unused as far as I can tell
//            List<String> lore = new ArrayList<>();
//
//            List<PotionEffect> effects = new ArrayList<>();
//            effects.add(protoPotion.effect);
//
//            Color color = this.generateColor(effects);
//
//
//            return new AlchemyPotion(material, data, name, lore, effects, color, getChildren(protoPotion));
//        } catch (Exception e) {
//            mcMMO.p.getLogger().warning("Failed to load Alchemy potion: " + potion_section.getString());
//            return null;
//        }
//    }
//
//    public Color generateColor(List<PotionEffect> effects) {
//        if (effects != null && !effects.isEmpty()) {
//            List<Color> colors = new ArrayList<>();
//            for (PotionEffect effect : effects) {
//                if (effect.getType().getColor() != null) {
//                    colors.add(effect.getType().getColor());
//                }
//            }
//            if (!colors.isEmpty()) {
//                if (colors.size() > 1) {
//                    return calculateAverageColor(colors);
//                }
//                return colors.get(0);
//            }
//        }
//        return null;
//    }
//
//    public Color calculateAverageColor(List<Color> colors) {
//        int red = 0;
//        int green = 0;
//        int blue = 0;
//        for (Color color : colors) {
//            red += color.getRed();
//            green += color.getGreen();
//            blue += color.getBlue();
//        }
//        Color color = Color.fromRGB(red / colors.size(), green / colors.size(), blue / colors.size());
//        return color;
//    }
//
//    private static String prettify(String name) {
//        String[] substrings = name.split("_");
//        String prettyString = "";
//        int size = 1;
//
//        for (String string : substrings) {
//            prettyString = prettyString.concat(getCapitalized(string));
//
//            if (size < substrings.length) {
//                prettyString = prettyString.concat(" ");
//            }
//
//            size++;
//        }
//
//        return prettyString;
//    }
//
//    public static String getCapitalized(String target) {
//        if (target.equals("II")) { // hacks
//            return target;
//        }
//        return target.substring(0, 1).toUpperCase() + target.substring(1).toLowerCase(Locale.ENGLISH);
//    }
//
//    private static String getName(PotionEffectType type) {
//        switch (type.getId()) {
//            case 1:
//                return "SPEED";
//            case 2:
//                return "SLOW";
//            case 3:
//                return "FAST_DIGGING";
//            case 4:
//                return "SLOW_DIGGING";
//            case 5:
//                return "INCREASE_DAMAGE";
//            case 6:
//                return "HEAL";
//            case 7:
//                return "HARM";
//            case 8:
//                return "JUMP";
//            case 9:
//                return "CONFUSION";
//            case 10:
//                return "REGENERATION";
//            case 11:
//                return "DAMAGE_RESISTANCE";
//            case 12:
//                return "FIRE_RESISTANCE";
//            case 13:
//                return "WATER_BREATHING";
//            case 14:
//                return "INVISIBILITY";
//            case 15:
//                return "BLINDNESS";
//            case 16:
//                return "NIGHT_VISION";
//            case 17:
//                return "HUNGER";
//            case 18:
//                return "WEAKNESS";
//            case 19:
//                return "POISON";
//            case 20:
//                return "WITHER";
//            case 21:
//                return "HEALTH_BOOST";
//            case 22:
//                return "ABSORPTION";
//            case 23:
//                return "SATURATION";
//            case 24:
//                return "GLOWING";
//            case 25:
//                return "LEVITATION";
//            case 26:
//                return "LUCK";
//            case 27:
//                return "UNLUCK";
//            case 28:
//                return "SLOW_FALLING";
//            case 29:
//                return "CONDUIT_POWER";
//            case 30:
//                return "DOLPHINS_GRACE";
//            default:
//                return "UNKNOWN_EFFECT_TYPE_" + type.getId();
//        }
//    }
//
//    private void populateVanillaPotions(Map<ProtoPotion, Map<Ingredient, ProtoPotion>> vanillaPotions) {
//        for (PotionType type : PotionType.values()) {
//            for (Material material : new Material[]{Material.POTION, Material.SPLASH_POTION, Material.LINGERING_POTION}) {
//                ProtoPotion protoPotion = new ProtoPotion(material, type);
//                getChildren(protoPotion);
//                vanillaPotions.put(protoPotion, getChildren(protoPotion));
//                if (type.isExtendable()) {
//                    protoPotion = new ProtoPotion(material, new PotionData(type, true, false));
//                    vanillaPotions.put(protoPotion, getChildren(protoPotion));
//                }
//                if (type.isUpgradeable()) {
//                    protoPotion = new ProtoPotion(material, new PotionData(type, false, true));
//                    vanillaPotions.put(protoPotion, getChildren(protoPotion));
//                }
//            }
//        }
//        for (Entry<ProtoPotion, Map<Ingredient, ProtoPotion>> entry : vanillaPotions.entrySet()) {
//            if (entry.getKey().mat == Material.POTION) {
//                entry.getValue().put(new Ingredient(Material.GUNPOWDER), new ProtoPotion(Material.SPLASH_POTION, entry.getKey().data));
//            }
//            if (entry.getKey().mat == Material.SPLASH_POTION) {
//                entry.getValue().put(new Ingredient(Material.DRAGON_BREATH), new ProtoPotion(Material.LINGERING_POTION, entry.getKey().data));
//            }
//        }
//
//        //Store children
//    }
//
//    private HashMap<Ingredient, ProtoPotion> getChildren(ProtoPotion protoPotion) {
//        HashMap<Ingredient, ProtoPotion> children = new HashMap<>();
//
//        switch (protoPotion.data.getType()) {
//            case WATER:
//                assert (!protoPotion.data.isExtended());
//                assert (!protoPotion.data.isUpgraded());
//                children.put(new Ingredient(Material.NETHER_WART), new ProtoPotion(protoPotion.mat, PotionType.AWKWARD));
//                children.put(new Ingredient(Material.FERMENTED_SPIDER_EYE), new ProtoPotion(protoPotion.mat, PotionType.WEAKNESS));
//                children.put(new Ingredient(Material.REDSTONE), new ProtoPotion(protoPotion.mat, PotionType.MUNDANE));
//                children.put(new Ingredient(Material.GLOWSTONE_DUST), new ProtoPotion(protoPotion.mat, PotionType.THICK));
//                children.put(new Ingredient(Material.BLAZE_POWDER), new ProtoPotion(protoPotion.mat, PotionType.MUNDANE));
//                children.put(new Ingredient(Material.SUGAR), new ProtoPotion(protoPotion.mat, PotionType.MUNDANE));
//                children.put(new Ingredient(Material.RABBIT_FOOT), new ProtoPotion(protoPotion.mat, PotionType.MUNDANE));
//                children.put(new Ingredient(Material.SPIDER_EYE), new ProtoPotion(protoPotion.mat, PotionType.MUNDANE));
//                children.put(new Ingredient(Material.MAGMA_CREAM), new ProtoPotion(protoPotion.mat, PotionType.MUNDANE));
//                children.put(new Ingredient(Material.GLISTERING_MELON_SLICE), new ProtoPotion(protoPotion.mat, PotionType.MUNDANE));
//                children.put(new Ingredient(Material.GHAST_TEAR), new ProtoPotion(protoPotion.mat, PotionType.MUNDANE));
//                return children;
//            case AWKWARD:
//                assert (!protoPotion.data.isExtended());
//                assert (!protoPotion.data.isUpgraded());
//                children.put(new Ingredient(Material.GOLDEN_CARROT), new ProtoPotion(protoPotion.mat, PotionType.NIGHT_VISION));
//                children.put(new Ingredient(Material.RABBIT_FOOT), new ProtoPotion(protoPotion.mat, PotionType.JUMP));
//                children.put(new Ingredient(Material.MAGMA_CREAM), new ProtoPotion(protoPotion.mat, PotionType.FIRE_RESISTANCE));
//                children.put(new Ingredient(Material.SUGAR), new ProtoPotion(protoPotion.mat, PotionType.SPEED));
//                children.put(new Ingredient(Material.PUFFERFISH), new ProtoPotion(protoPotion.mat, PotionType.WATER_BREATHING));
//                children.put(new Ingredient(Material.GLISTERING_MELON_SLICE), new ProtoPotion(protoPotion.mat, PotionType.INSTANT_HEAL));
//                children.put(new Ingredient(Material.SPIDER_EYE), new ProtoPotion(protoPotion.mat, PotionType.POISON));
//                children.put(new Ingredient(Material.GHAST_TEAR), new ProtoPotion(protoPotion.mat, PotionType.REGEN));
//                children.put(new Ingredient(Material.BLAZE_POWDER), new ProtoPotion(protoPotion.mat, PotionType.STRENGTH));
//                children.put(new Ingredient(Material.TURTLE_HELMET), new ProtoPotion(protoPotion.mat, PotionType.TURTLE_MASTER));
//                children.put(new Ingredient(Material.PHANTOM_MEMBRANE), new ProtoPotion(protoPotion.mat, PotionType.SLOW_FALLING));
//                // mcMMO custom potions
//                double mod = 1;
//                if (protoPotion.mat == Material.SPLASH_POTION) {
//                    mod = 0.75;
//                }
//                if (protoPotion.mat == Material.LINGERING_POTION) {
//                    mod = 0.25;
//                }
//                children.put(new Ingredient(Material.BROWN_MUSHROOM), new ProtoPotion(protoPotion.mat, PotionType.UNCRAFTABLE, new PotionEffect(PotionEffectType.CONFUSION, (int) (450 * mod), 0), "NAUSEA"));
//                children.put(new Ingredient(Material.CARROT), new ProtoPotion(protoPotion.mat, PotionType.UNCRAFTABLE, new PotionEffect(PotionEffectType.FAST_DIGGING, (int) (3600 * mod), 0), "HASTE"));
//                children.put(new Ingredient(Material.SLIME_BALL), new ProtoPotion(protoPotion.mat, PotionType.UNCRAFTABLE, new PotionEffect(PotionEffectType.SLOW_DIGGING, (int) (3600 * mod), 0), "DULLNESS"));
//                children.put(new Ingredient(Material.GOLDEN_APPLE), new ProtoPotion(protoPotion.mat, PotionType.UNCRAFTABLE, new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, (int) (450 * mod), 0), "RESISTANCE"));
//                children.put(new Ingredient(Material.INK_SAC), new ProtoPotion(protoPotion.mat, PotionType.UNCRAFTABLE, new PotionEffect(PotionEffectType.BLINDNESS, (int) (225 * mod), 0), "BLINDNESS"));
//                children.put(new Ingredient(Material.ROTTEN_FLESH), new ProtoPotion(protoPotion.mat, PotionType.UNCRAFTABLE, new PotionEffect(PotionEffectType.HUNGER, (int) (900 * mod), 0), "HUNGER"));
//                children.put(new Ingredient(Material.POISONOUS_POTATO), new ProtoPotion(protoPotion.mat, PotionType.UNCRAFTABLE, new PotionEffect(PotionEffectType.WITHER, (int) (450 * mod), 0), "DECAY"));
//                children.put(new Ingredient(Material.QUARTZ), new ProtoPotion(protoPotion.mat, PotionType.UNCRAFTABLE, new PotionEffect(PotionEffectType.ABSORPTION, (int) (1800 * mod), 0), "ABSORPTION"));
//                children.put(new Ingredient(Material.FERN), new ProtoPotion(protoPotion.mat, PotionType.UNCRAFTABLE, new PotionEffect(PotionEffectType.SATURATION, (int) (8 * mod), 0), "SATURATION"));
//                children.put(new Ingredient(Material.APPLE), new ProtoPotion(protoPotion.mat, PotionType.UNCRAFTABLE, new PotionEffect(PotionEffectType.HEALTH_BOOST, (int) (1800 * mod), 0), "HEALTH_BOOST"));
//                return children;
//            case FIRE_RESISTANCE:
//                assert (!protoPotion.data.isUpgraded());
//                if (protoPotion.data.isExtended()) {
//                    children.put(new Ingredient(Material.FERMENTED_SPIDER_EYE), new ProtoPotion(protoPotion.mat, new PotionData(PotionType.SLOWNESS, true, false)));
//                } else {
//                    children.put(new Ingredient(Material.FERMENTED_SPIDER_EYE), new ProtoPotion(protoPotion.mat, PotionType.SLOWNESS));
//                    children.put(new Ingredient(Material.REDSTONE), new ProtoPotion(protoPotion.mat, new PotionData(protoPotion.data.getType(), true, false)));
//                }
//                return children;
//            case INSTANT_DAMAGE:
//                assert (!protoPotion.data.isExtended());
//                if (!protoPotion.data.isUpgraded()) {
//                    children.put(new Ingredient(Material.GLOWSTONE_DUST), new ProtoPotion(protoPotion.mat, new PotionData(protoPotion.data.getType(), false, true)));
//                }
//                return children;
//            case INSTANT_HEAL:
//                assert (!protoPotion.data.isExtended());
//                if (!protoPotion.data.isUpgraded()) {
//                    children.put(new Ingredient(Material.FERMENTED_SPIDER_EYE), new ProtoPotion(protoPotion.mat, PotionType.INSTANT_DAMAGE));
//                    children.put(new Ingredient(Material.GLOWSTONE_DUST), new ProtoPotion(protoPotion.mat, new PotionData(protoPotion.data.getType(), false, true)));
//                } else {
//                    children.put(new Ingredient(Material.FERMENTED_SPIDER_EYE), new ProtoPotion(protoPotion.mat, new PotionData(PotionType.INSTANT_DAMAGE, false, true)));
//                }
//                return children;
//            case INVISIBILITY:
//                assert (!protoPotion.data.isUpgraded());
//                if (!protoPotion.data.isExtended()) {
//                    children.put(new Ingredient(Material.REDSTONE), new ProtoPotion(protoPotion.mat, new PotionData(protoPotion.data.getType(), true, false)));
//                }
//                return children;
//            case JUMP:
//                if (!protoPotion.data.isUpgraded() && !protoPotion.data.isExtended()) {
//                    children.put(new Ingredient(Material.FERMENTED_SPIDER_EYE), new ProtoPotion(protoPotion.mat, PotionType.SLOWNESS));
//                    children.put(new Ingredient(Material.GLOWSTONE_DUST), new ProtoPotion(protoPotion.mat, new PotionData(protoPotion.data.getType(), false, true)));
//                    children.put(new Ingredient(Material.REDSTONE), new ProtoPotion(protoPotion.mat, new PotionData(protoPotion.data.getType(), true, false)));
//                }
//                return children;
//            case NIGHT_VISION:
//                assert (!protoPotion.data.isUpgraded());
//                if (!protoPotion.data.isExtended()) {
//                    children.put(new Ingredient(Material.FERMENTED_SPIDER_EYE), new ProtoPotion(protoPotion.mat, PotionType.INVISIBILITY));
//                    children.put(new Ingredient(Material.REDSTONE), new ProtoPotion(protoPotion.mat, new PotionData(protoPotion.data.getType(), true, false)));
//                } else {
//                    children.put(new Ingredient(Material.FERMENTED_SPIDER_EYE), new ProtoPotion(protoPotion.mat, new PotionData(PotionType.INVISIBILITY, true, false)));
//                }
//                return children;
//            case POISON:
//                if (!protoPotion.data.isUpgraded() && !protoPotion.data.isExtended()) {
//                    children.put(new Ingredient(Material.FERMENTED_SPIDER_EYE), new ProtoPotion(protoPotion.mat, PotionType.INSTANT_DAMAGE));
//                    children.put(new Ingredient(Material.GLOWSTONE_DUST), new ProtoPotion(protoPotion.mat, new PotionData(protoPotion.data.getType(), false, true)));
//                    children.put(new Ingredient(Material.REDSTONE), new ProtoPotion(protoPotion.mat, new PotionData(protoPotion.data.getType(), true, false)));
//                } else {
//                    children.put(new Ingredient(Material.FERMENTED_SPIDER_EYE), new ProtoPotion(protoPotion.mat, new PotionData(PotionType.INSTANT_DAMAGE, false, true)));
//                }
//                return children;
//            case REGEN:
//                if (!protoPotion.data.isUpgraded() && !protoPotion.data.isExtended()) {
//                    children.put(new Ingredient(Material.GLOWSTONE_DUST), new ProtoPotion(protoPotion.mat, new PotionData(protoPotion.data.getType(), false, true)));
//                    children.put(new Ingredient(Material.REDSTONE), new ProtoPotion(protoPotion.mat, new PotionData(protoPotion.data.getType(), true, false)));
//                }
//                return children;
//            case SLOWNESS:
//                assert (!protoPotion.data.isUpgraded());
//                if (!protoPotion.data.isExtended()) {
//                    children.put(new Ingredient(Material.REDSTONE), new ProtoPotion(protoPotion.mat, new PotionData(protoPotion.data.getType(), true, false)));
//                }
//                return children;
//            case SLOW_FALLING:
//                assert (!protoPotion.data.isUpgraded());
//                if (!protoPotion.data.isExtended()) {
//                    children.put(new Ingredient(Material.REDSTONE), new ProtoPotion(protoPotion.mat, new PotionData(protoPotion.data.getType(), true, false)));
//                }
//                return children;
//            case SPEED:
//                if (!protoPotion.data.isUpgraded() && !protoPotion.data.isExtended()) {
//                    children.put(new Ingredient(Material.FERMENTED_SPIDER_EYE), new ProtoPotion(protoPotion.mat, PotionType.SLOWNESS));
//                    children.put(new Ingredient(Material.GLOWSTONE_DUST), new ProtoPotion(protoPotion.mat, new PotionData(protoPotion.data.getType(), false, true)));
//                    children.put(new Ingredient(Material.REDSTONE), new ProtoPotion(protoPotion.mat, new PotionData(protoPotion.data.getType(), true, false)));
//                } else {
//                    children.put(new Ingredient(Material.FERMENTED_SPIDER_EYE), new ProtoPotion(protoPotion.mat, new PotionData(PotionType.SLOWNESS, true, false)));
//                }
//                return children;
//            case STRENGTH:
//                if (!protoPotion.data.isUpgraded() && !protoPotion.data.isExtended()) {
//                    children.put(new Ingredient(Material.GLOWSTONE_DUST), new ProtoPotion(protoPotion.mat, new PotionData(protoPotion.data.getType(), false, true)));
//                    children.put(new Ingredient(Material.REDSTONE), new ProtoPotion(protoPotion.mat, new PotionData(protoPotion.data.getType(), true, false)));
//                }
//                return children;
//            case TURTLE_MASTER:
//                if (!protoPotion.data.isUpgraded() && !protoPotion.data.isExtended()) {
//                    children.put(new Ingredient(Material.GLOWSTONE_DUST), new ProtoPotion(protoPotion.mat, new PotionData(protoPotion.data.getType(), false, true)));
//                    children.put(new Ingredient(Material.REDSTONE), new ProtoPotion(protoPotion.mat, new PotionData(protoPotion.data.getType(), true, false)));
//                }
//                return children;
//            case WATER_BREATHING:
//                assert (!protoPotion.data.isUpgraded());
//                if (!protoPotion.data.isExtended()) {
//                    children.put(new Ingredient(Material.FERMENTED_SPIDER_EYE), new ProtoPotion(protoPotion.mat, PotionType.INSTANT_DAMAGE));
//                    children.put(new Ingredient(Material.REDSTONE), new ProtoPotion(protoPotion.mat, new PotionData(protoPotion.data.getType(), true, false)));
//                }
//                return children;
//            case WEAKNESS:
//                assert (!protoPotion.data.isUpgraded());
//                if (!protoPotion.data.isExtended()) {
//                    children.put(new Ingredient(Material.REDSTONE), new ProtoPotion(protoPotion.mat, new PotionData(protoPotion.data.getType(), true, false)));
//                }
//                return children;
//            case LUCK:
//            case MUNDANE:
//            case THICK:
//            case UNCRAFTABLE:
//                assert (!protoPotion.data.isExtended());
//                assert (!protoPotion.data.isUpgraded());
//                return children;
//            default:
//                return children;
//        }
//    }
//
//    private static void populateCustomPotions(Map<ProtoPotion, Map<Ingredient, ProtoPotion>> customPotions) {
//        for (Material material : new Material[]{Material.POTION, Material.SPLASH_POTION, Material.LINGERING_POTION}) {
//            ProtoPotion newPotion;
//            double mod = 1;
//            if (material == Material.SPLASH_POTION) {
//                mod = 0.75;
//            }
//            if (material == Material.LINGERING_POTION) {
//                mod = 0.25;
//            }
//            HashMap<Ingredient, ProtoPotion> newPotionChildren;
//            newPotion = new ProtoPotion(material, PotionType.UNCRAFTABLE, new PotionEffect(PotionEffectType.CONFUSION, (int) (450 * mod), 0), "NAUSEA");
//            newPotionChildren = new HashMap<>();
//            newPotionChildren.put(new Ingredient(Material.GLOWSTONE_DUST), new ProtoPotion(material, PotionType.UNCRAFTABLE, new PotionEffect(newPotion.effect.getType(), newPotion.effect.getDuration() / 2, 1), newPotion.baseName + "_II"));
//            newPotionChildren.put(new Ingredient(Material.REDSTONE), new ProtoPotion(material, PotionType.UNCRAFTABLE, new PotionEffect(newPotion.effect.getType(), newPotion.effect.getDuration() * 2, 0), newPotion.baseName + "_EXTENDED"));
//            for (ProtoPotion child : newPotionChildren.values()) {
//                customPotions.put(child, new HashMap<>());
//            }
//            customPotions.put(newPotion, newPotionChildren);
//            newPotion = new ProtoPotion(material, PotionType.UNCRAFTABLE, new PotionEffect(PotionEffectType.FAST_DIGGING, (int) (3600 * mod), 0), "HASTE");
//            newPotionChildren = new HashMap<>();
//            newPotionChildren.put(new Ingredient(Material.GLOWSTONE_DUST), new ProtoPotion(material, PotionType.UNCRAFTABLE, new PotionEffect(newPotion.effect.getType(), newPotion.effect.getDuration() / 2, 1), newPotion.baseName + "_II"));
//            newPotionChildren.put(new Ingredient(Material.REDSTONE), new ProtoPotion(material, PotionType.UNCRAFTABLE, new PotionEffect(newPotion.effect.getType(), newPotion.effect.getDuration() * 2, 0), newPotion.baseName + "_EXTENDED"));
//            for (ProtoPotion child : newPotionChildren.values()) {
//                customPotions.put(child, new HashMap<>());
//            }
//            customPotions.put(newPotion, newPotionChildren);
//            newPotion = new ProtoPotion(material, PotionType.UNCRAFTABLE, new PotionEffect(PotionEffectType.SLOW_DIGGING, (int) (3600 * mod), 0), "DULLNESS");
//            newPotionChildren = new HashMap<>();
//            newPotionChildren.put(new Ingredient(Material.GLOWSTONE_DUST), new ProtoPotion(material, PotionType.UNCRAFTABLE, new PotionEffect(newPotion.effect.getType(), newPotion.effect.getDuration() / 2, 1), newPotion.baseName + "_II"));
//            newPotionChildren.put(new Ingredient(Material.REDSTONE), new ProtoPotion(material, PotionType.UNCRAFTABLE, new PotionEffect(newPotion.effect.getType(), newPotion.effect.getDuration() * 2, 0), newPotion.baseName + "_EXTENDED"));
//            for (ProtoPotion child : newPotionChildren.values()) {
//                customPotions.put(child, new HashMap<>());
//            }
//            customPotions.put(newPotion, newPotionChildren);
//            newPotion = new ProtoPotion(material, PotionType.UNCRAFTABLE, new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, (int) (450 * mod), 0), "RESISTANCE");
//            newPotionChildren = new HashMap<>();
//            newPotionChildren.put(new Ingredient(Material.GLOWSTONE_DUST), new ProtoPotion(material, PotionType.UNCRAFTABLE, new PotionEffect(newPotion.effect.getType(), newPotion.effect.getDuration() / 2, 1), newPotion.baseName + "_II"));
//            newPotionChildren.put(new Ingredient(Material.REDSTONE), new ProtoPotion(material, PotionType.UNCRAFTABLE, new PotionEffect(newPotion.effect.getType(), newPotion.effect.getDuration() * 2, 0), newPotion.baseName + "_EXTENDED"));
//            for (ProtoPotion child : newPotionChildren.values()) {
//                customPotions.put(child, new HashMap<>());
//            }
//            customPotions.put(newPotion, newPotionChildren);
//            newPotion = new ProtoPotion(material, PotionType.UNCRAFTABLE, new PotionEffect(PotionEffectType.BLINDNESS, (int) (225 * mod), 0), "BLINDNESS");
//            newPotionChildren = new HashMap<>();
//            newPotionChildren.put(new Ingredient(Material.GLOWSTONE_DUST), new ProtoPotion(material, PotionType.UNCRAFTABLE, new PotionEffect(newPotion.effect.getType(), newPotion.effect.getDuration() / 2, 1), newPotion.baseName + "_II"));
//            newPotionChildren.put(new Ingredient(Material.REDSTONE), new ProtoPotion(material, PotionType.UNCRAFTABLE, new PotionEffect(newPotion.effect.getType(), newPotion.effect.getDuration() * 2, 0), newPotion.baseName + "_EXTENDED"));
//            for (ProtoPotion child : newPotionChildren.values()) {
//                customPotions.put(child, new HashMap<>());
//            }
//            customPotions.put(newPotion, newPotionChildren);
//            newPotion = new ProtoPotion(material, PotionType.UNCRAFTABLE, new PotionEffect(PotionEffectType.HUNGER, (int) (900 * mod), 0), "HUNGER");
//            newPotionChildren = new HashMap<>();
//            newPotionChildren.put(new Ingredient(Material.GLOWSTONE_DUST), new ProtoPotion(material, PotionType.UNCRAFTABLE, new PotionEffect(newPotion.effect.getType(), newPotion.effect.getDuration() / 2, 1), newPotion.baseName + "_II"));
//            newPotionChildren.put(new Ingredient(Material.REDSTONE), new ProtoPotion(material, PotionType.UNCRAFTABLE, new PotionEffect(newPotion.effect.getType(), newPotion.effect.getDuration() * 2, 0), newPotion.baseName + "_EXTENDED"));
//            for (ProtoPotion child : newPotionChildren.values()) {
//                customPotions.put(child, new HashMap<>());
//            }
//            customPotions.put(newPotion, newPotionChildren);
//            newPotion = new ProtoPotion(material, PotionType.UNCRAFTABLE, new PotionEffect(PotionEffectType.WITHER, (int) (450 * mod), 0), "DECAY");
//            newPotionChildren = new HashMap<>();
//            newPotionChildren.put(new Ingredient(Material.GLOWSTONE_DUST), new ProtoPotion(material, PotionType.UNCRAFTABLE, new PotionEffect(newPotion.effect.getType(), newPotion.effect.getDuration() / 2, 1), newPotion.baseName + "_II"));
//            newPotionChildren.put(new Ingredient(Material.REDSTONE), new ProtoPotion(material, PotionType.UNCRAFTABLE, new PotionEffect(newPotion.effect.getType(), newPotion.effect.getDuration() * 2, 0), newPotion.baseName + "_EXTENDED"));
//            for (ProtoPotion child : newPotionChildren.values()) {
//                customPotions.put(child, new HashMap<>());
//            }
//            customPotions.put(newPotion, newPotionChildren);
//            newPotion = new ProtoPotion(material, PotionType.UNCRAFTABLE, new PotionEffect(PotionEffectType.ABSORPTION, (int) (1800 * mod), 0), "ABSORPTION");
//            newPotionChildren = new HashMap<>();
//            newPotionChildren.put(new Ingredient(Material.GLOWSTONE_DUST), new ProtoPotion(material, PotionType.UNCRAFTABLE, new PotionEffect(newPotion.effect.getType(), newPotion.effect.getDuration() / 2, 1), newPotion.baseName + "_II"));
//            newPotionChildren.put(new Ingredient(Material.REDSTONE), new ProtoPotion(material, PotionType.UNCRAFTABLE, new PotionEffect(newPotion.effect.getType(), newPotion.effect.getDuration() * 2, 0), newPotion.baseName + "_EXTENDED"));
//            for (ProtoPotion child : newPotionChildren.values()) {
//                customPotions.put(child, new HashMap<>());
//            }
//            customPotions.put(newPotion, newPotionChildren);
//            newPotion = new ProtoPotion(material, PotionType.UNCRAFTABLE, new PotionEffect(PotionEffectType.SATURATION, (int) (8 * mod), 0), "SATURATION");
//            newPotionChildren = new HashMap<>();
//            newPotionChildren.put(new Ingredient(Material.GLOWSTONE_DUST), new ProtoPotion(material, PotionType.UNCRAFTABLE, new PotionEffect(newPotion.effect.getType(), newPotion.effect.getDuration() / 2, 1), newPotion.baseName + "_II"));
//            newPotionChildren.put(new Ingredient(Material.REDSTONE), new ProtoPotion(material, PotionType.UNCRAFTABLE, new PotionEffect(newPotion.effect.getType(), newPotion.effect.getDuration() * 2, 0), newPotion.baseName + "_EXTENDED"));
//            for (ProtoPotion child : newPotionChildren.values()) {
//                customPotions.put(child, new HashMap<>());
//            }
//            customPotions.put(newPotion, newPotionChildren);
//            newPotion = new ProtoPotion(material, PotionType.UNCRAFTABLE, new PotionEffect(PotionEffectType.HEALTH_BOOST, (int) (1800 * mod), 0), "HEALTH_BOOST");
//            newPotionChildren = new HashMap<>();
//            newPotionChildren.put(new Ingredient(Material.GLOWSTONE_DUST), new ProtoPotion(material, PotionType.UNCRAFTABLE, new PotionEffect(newPotion.effect.getType(), newPotion.effect.getDuration() / 2, 1), newPotion.baseName + "_II"));
//            newPotionChildren.put(new Ingredient(Material.REDSTONE), new ProtoPotion(material, PotionType.UNCRAFTABLE, new PotionEffect(newPotion.effect.getType(), newPotion.effect.getDuration() * 2, 0), newPotion.baseName + "_EXTENDED"));
//            for (ProtoPotion child : newPotionChildren.values()) {
//                customPotions.put(child, new HashMap<>());
//            }
//            customPotions.put(newPotion, newPotionChildren);
//        }
//
//        // Add all material state changes
//        for (Entry<ProtoPotion, Map<Ingredient, ProtoPotion>> entry : customPotions.entrySet()) {
//            if (entry.getKey().mat == Material.POTION) {
//                PotionEffect effect = new PotionEffect(entry.getKey().effect.getType(), (int) (entry.getKey().effect.getDuration() * 0.75), entry.getKey().effect.getAmplifier());
//                entry.getValue().put(new Ingredient(Material.GUNPOWDER), new ProtoPotion(Material.SPLASH_POTION, entry.getKey().data, effect, entry.getKey().baseName));
//            } else if (entry.getKey().mat == Material.SPLASH_POTION) {
//                PotionEffect effect = new PotionEffect(entry.getKey().effect.getType(), (int) (entry.getKey().effect.getDuration() * 0.33), entry.getKey().effect.getAmplifier());
//                entry.getValue().put(new Ingredient(Material.DRAGON_BREATH), new ProtoPotion(Material.LINGERING_POTION, entry.getKey().data, effect, entry.getKey().baseName));
//            }
//        }
//    }
//
//    public static class Ingredient {
//
//        public Material mat;
//        public int data;
//        public String name;
//
//        public Ingredient(Material mat) {
//            this.mat = mat;
//            this.data = 0;
//            name = mat.name();
//        }
//
//    }
//
//    public static class ProtoPotion {
//
//        public String name;
//        public Material mat;
//        public PotionData data;
//        public PotionEffect effect;
//        public String baseName;
//
//        public ProtoPotion(PotionData data) {
//            this(Material.POTION, data);
//        }
//
//        public ProtoPotion(Material type, PotionData data) {
//            this(type, data, null, getMCName(data.getType()));
//        }
//
//        public ProtoPotion(Material mat, PotionType type, PotionEffect effect, String baseName) {
//            this(mat, new PotionData(type, false, false), effect, baseName);
//        }
//
//        public ProtoPotion(Material type, PotionData data, PotionEffect effect, String baseName) {
//            this.data = data;
//            this.effect = effect;
//            this.mat = type;
//            this.baseName = baseName;
//            this.name = "POTION_OF_" + baseName;
//            if (mat == Material.SPLASH_POTION) {
//                this.name = "SPLASH_" + this.name;
//            }
//            if (mat == Material.LINGERING_POTION) {
//                this.name = "LINGERING_" + this.name;
//            }
//            if (data.isExtended()) {
//                this.name += "_EXTENDED";
//            }
//            if (data.isUpgraded()) {
//                this.name += "_II";
//            }
//        }
//
//        public ProtoPotion(PotionType type) {
//            this(new PotionData(type, false, false));
//        }
//
//        public ProtoPotion(Material mat, PotionType type) {
//            this(mat, new PotionData(type, false, false));
//        }
//
//        private static String getMCName(PotionType type) {
//            switch (type) {
//                case INSTANT_DAMAGE:
//                    return "HARMING";
//                case INSTANT_HEAL:
//                    return "HEALING";
//                case JUMP:
//                    return "LEAPING";
//                case REGEN:
//                    return "REGENERATION";
//                case SPEED:
//                    return "SWIFTNESS";
//                case UNCRAFTABLE:
//                    return "EMPTY";
//                case LUCK:
//                case MUNDANE:
//                case NIGHT_VISION:
//                case POISON:
//                case INVISIBILITY:
//                case SLOWNESS:
//                case AWKWARD:
//                case STRENGTH:
//                case THICK:
//                case FIRE_RESISTANCE:
//                case WATER:
//                case WATER_BREATHING:
//                case WEAKNESS:
//                case TURTLE_MASTER:
//                case SLOW_FALLING:
//                    return type.name();
//                default:
//                    return "";
//            }
//        }
//
//        public int hashCode() {
//            return name.hashCode();
//        }
//
//        public boolean equals(Object obj) {
//            if (!(obj instanceof ProtoPotion)) {
//                return false;
//            }
//            return name.equals(((ProtoPotion) obj).name);
//        }
//
//        public void setChildren()
//    }
//}
