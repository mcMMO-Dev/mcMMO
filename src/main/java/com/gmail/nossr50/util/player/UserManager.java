package com.gmail.nossr50.util.player;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.runnables.player.PlayerProfileLoader;

public final class UserManager {
    private final static Map<String, McMMOPlayer>           players   = new HashMap<String, McMMOPlayer>();
    private final static Map<String, Future<PlayerProfile>> loadTasks = new HashMap<String, Future<PlayerProfile>>();
    private final static ExecutorService loadExecutor = Executors.newCachedThreadPool();

    private UserManager() {};

    /**
     * Asynchronously pre-fetch information about the player. This is intended
     * to expedite the PlayerJoinEvent.
     *
     * @param playerName The player name
     */
    public static void prefetchUserData(String playerName) {
        loadTasks.put(playerName, loadExecutor.submit(new PlayerProfileLoader(playerName)));
    }

    /**
     * Discard the information from the prefetch - for example, due to the
     * user being banned.
     *
     * @param playerName The player name
     */
    public static void discardPrefetch(String playerName) {
        Future<PlayerProfile> oldTask = loadTasks.remove(playerName);
        if (oldTask != null) {
            oldTask.cancel(false);
        }
    }

    /**
     * Add a new user. If the prefetched player information is available, it
     * will be used.
     *
     * @param player The player to create a user record for
     * @return the player's {@link McMMOPlayer} object
     */
    public static McMMOPlayer addUser(Player player) {
        String playerName = player.getName();
        McMMOPlayer mcMMOPlayer = players.get(playerName);

        if (mcMMOPlayer != null) {
            mcMMOPlayer.setPlayer(player); // The player object is different on each reconnection and must be updated
        }
        else {
            Future<PlayerProfile> task = loadTasks.remove(playerName);
            if (task != null && !task.isCancelled()) {
                try {
                    mcMMOPlayer = new McMMOPlayer(player, task.get());
                    // TODO copy any additional post-processing here
                    players.put(playerName, mcMMOPlayer);
                    return mcMMOPlayer;
                }
                catch (ExecutionException e) {
                }
                catch (InterruptedException e) {
                }
            }
            // Did not return - load on main thread
            mcMMOPlayer = new McMMOPlayer(player);
            // (start post-processing that must be copied above)
            players.put(playerName, mcMMOPlayer);
        }

        return mcMMOPlayer;
    }

    /**
     * Remove a user.
     *
     * @param playerName The name of the player to remove
     */
    public static void remove(String playerName) {
        players.remove(playerName);
        discardPrefetch(playerName);
    }

    /**
     * Clear all users.
     */
    public static void clearAll() {
        discardAllPrefetch();
        players.clear();
    }

    /**
     * Save all users.
     */
    public static void saveAll() {
        discardAllPrefetch();
        for (McMMOPlayer mcMMOPlayer : players.values()) {
            mcMMOPlayer.getProfile().save();
        }
    }

    /**
     * Discard / cancel all data prefetching.
     */
    public static void discardAllPrefetch() {
        Iterator<Future<PlayerProfile>> taskIter = loadTasks.values().iterator();
        while (taskIter.hasNext()) {
            taskIter.next().cancel(false);
            taskIter.remove();
        }
    }

    public static Map<String, McMMOPlayer> getPlayers() {
        return players;
    }

    /**
     * Get the McMMOPlayer of a player by a partial name.
     *
     * @param playerName The partial name of the player whose McMMOPlayer to retrieve
     * @return the player's McMMOPlayer object
     */
    public static McMMOPlayer getPlayer(String playerName) {
        List<Player> matches = mcMMO.p.getServer().matchPlayer(playerName);

        if (matches.size() == 1) {
            playerName = matches.get(0).getName();
        }

        return players.get(playerName);
    }

    /**
     * Get the McMMOPlayer of a player.
     *
     * @param player The player whose McMMOPlayer to retrieve
     * @return the player's McMMOPlayer object
     */
    public static McMMOPlayer getPlayer(OfflinePlayer player) {
        return players.get(player.getName());
    }
}
