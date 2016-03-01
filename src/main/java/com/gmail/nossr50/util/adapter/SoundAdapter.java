package com.gmail.nossr50.util.adapter;

import org.bukkit.Sound;

public class SoundAdapter {
    public static final Sound FIZZ;
    public static final Sound LEVEL_UP;
    public static final Sound FIREWORK_BLAST_FAR;
    public static final Sound ITEM_PICKUP;
    public static final Sound GHAST_SCREAM;
    public static final Sound ANVIL_LAND;
    public static final Sound ANVIL_USE;
    public static final Sound ITEM_BREAK;
    public static final Sound BAT_TAKEOFF;

    static {
        Sound temp = null;
        try {
            temp = Sound.valueOf("BLOCK_FIRE_EXTINGUISH");
        } catch (Exception e) {
            temp = Sound.valueOf("FIZZ");
        } finally {
            FIZZ = temp;
        }
        temp = null;
        try {
            temp = Sound.valueOf("ENTITY_PLAYER_LEVELUP");
        } catch (Exception e) {
            temp = Sound.valueOf("LEVEL_UP");
        } finally {
            LEVEL_UP = temp;
        }
        temp = null;
        try {
            temp = Sound.valueOf("ENTITY_GHAST_SCREAM");
        } catch (Exception e) {
            temp = Sound.valueOf("GHAST_SCREAM");
        } finally {
            GHAST_SCREAM = temp;
        }
        temp = null;
        try {
            temp = Sound.valueOf("ENTITY_ITEM_PICKUP");
        } catch (Exception e) {
            temp = Sound.valueOf("ITEM_PICKUP");
        } finally {
            ITEM_PICKUP = temp;
        }
        temp = null;
        try {
            temp = Sound.valueOf("ENTITY_ITEM_BREAK");
        } catch (Exception e) {
            temp = Sound.valueOf("ITEM_BREAK");
        } finally {
            ITEM_BREAK = temp;
        }
        temp = null;
        try {
            temp = Sound.valueOf("BLOCK_ANVIL_USE");
        } catch (Exception e) {
            temp = Sound.valueOf("ANVIL_USE");
        } finally {
            ANVIL_USE = temp;
        }
        temp = null;
        try {
            temp = Sound.valueOf("BLOCK_ANVIL_LAND");
        } catch (Exception e) {
            temp = Sound.valueOf("ANVIL_LAND");
        } finally {
            ANVIL_LAND = temp;
        }
        temp = null;
        try {
            temp = Sound.valueOf("ENTITY_BAT_TAKEOFF");
        } catch (Exception e) {
            temp = Sound.valueOf("BAT_TAKEOFF");
        } finally {
            BAT_TAKEOFF = temp;
        }
        temp = null;
        try {
            temp = Sound.valueOf("ENTITY_FIREWORK_BLAST_FAR");
        } catch (Exception e) {
            temp = Sound.valueOf("FIREWORK_LARGE_BLAST2");
        } finally {
            FIREWORK_BLAST_FAR = temp;
        }
    }
    
}
