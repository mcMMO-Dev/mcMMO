package com.gmail.nossr50.util.spout;

import org.bukkit.entity.Player;
import org.getspout.spoutapi.SpoutManager;

import com.gmail.nossr50.mcMMO;

public class SpoutSoundUtils {
    /**
     * Play noise on level-up.
     *
     * @param player The player who leveled up
     */
    protected static void playLevelUpNoise(Player player, mcMMO plugin) {
        SpoutManager.getSoundManager().playCustomSoundEffect(plugin, SpoutManager.getPlayer(player), "level.wav", false);
    }
}
