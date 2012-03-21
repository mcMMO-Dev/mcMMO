package com.gmail.nossr50.spout;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.player.SpoutPlayer;
import org.getspout.spoutapi.sound.SoundEffect;
import org.getspout.spoutapi.sound.SoundManager;

public class SpoutSounds {

    /**
     * Play sound effect through Spout.
     *
     * @param effect The sound effect to play
     * @param player The player to play the sound to
     * @param location The location the sound should come from
     */
    public static void playSoundForPlayer(SoundEffect effect, Player player, Location location) {
        SoundManager SM = SpoutManager.getSoundManager();
        SpoutPlayer sPlayer = SpoutManager.getPlayer(player);

        SM.playSoundEffect(sPlayer, effect, location);
    }

    /**
     * Play noise on successful repair.
     *
     * @param player The player who repaired an item
     */
    public static void playRepairNoise(Player player) {
        SoundManager SM = SpoutManager.getSoundManager();
        SpoutPlayer sPlayer = SpoutManager.getPlayer(player);

        //If this is pulling from online, why have it in the jar?
        SM.playCustomSoundEffect(Bukkit.getServer().getPluginManager().getPlugin("mcMMO"), sPlayer, "repair.wav", false);
    }

    /**
     * Play noise on level-up.
     *
     * @param player The player who leveled up
     */
    protected static void playLevelUpNoise(Player player) {
        SoundManager SM = SpoutManager.getSoundManager();
        SpoutPlayer sPlayer = SpoutManager.getPlayer(player);

        //If this is pulling from online, why have it in the jar?
        SM.playCustomSoundEffect(Bukkit.getServer().getPluginManager().getPlugin("mcMMO"), sPlayer, "level.wav", false);
    }
}
