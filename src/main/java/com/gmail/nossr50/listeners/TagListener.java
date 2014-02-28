package com.gmail.nossr50.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.util.player.UserManager;
import com.gmail.nossr50.util.scoreboards.ScoreboardManager;

import org.kitteh.tag.AsyncPlayerReceiveNameTagEvent;

public class TagListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onAsyncPlayerReceiveNameTag(AsyncPlayerReceiveNameTagEvent event) {
        Player player = event.getNamedPlayer();
        McMMOPlayer mcMMOPlayer = UserManager.getPlayer(player, true);

        if (mcMMOPlayer == null) {
            return;
        }

        String tag = event.getTag();
        String colorlessTag = ChatColor.stripColor(tag);

        if (colorlessTag.equals(tag)) {
            mcMMOPlayer.setNameTag(null);
            return;
        }

        if (colorlessTag.equals(player.getName()) && (mcMMOPlayer.getNameTag() == null || !mcMMOPlayer.getNameTag().equals(tag))) {
            mcMMOPlayer.setNameTag(tag);
            ScoreboardManager.tagUpdate(player);
        }

        /*
        mbax:
            With TagAPI, you can set the name tag over anybody's head to any value.
            You can have five people with the name 'Notch' (And one named 'notch' for
            good measure). That makes it difficult to properly utilize the scoreboard
            feature to match a changed name to a score for below name objective display.

            Additionally, every single player can be seeing a different username over
            the head of the same player. So even if you're just trying to detect colored usernames
            you could be encountering 16 different colors before a username (15, generally, who does white?)
            and maybe some format codes as well. The Scoreboard API doesn't support per-player setting of names,
               so you'd have to add an entry for each.

            At best, what you could do is listen to TagAPI's event on MONITOR priority and track all the names.
            Check if a set name is the same as their actual name, sans colors. If yes, store that and each time
            you update a player's below-name objective, update an OfflinePlayer with that colored version as well.
            This is not a good idea if the objective will ever change DisplaySlot, particularly if it goes to the
            sidebar, since you'll then need to clear the color ones.
         */
    }
}
