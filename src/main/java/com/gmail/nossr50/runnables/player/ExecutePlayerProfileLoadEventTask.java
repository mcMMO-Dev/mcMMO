package com.gmail.nossr50.runnables.player;

import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.EventUtils;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ExecutePlayerProfileLoadEventTask extends BukkitRunnable {
    private Player player;
    private PlayerProfile profile;
    public ExecutePlayerProfileLoadEventTask(Player player, PlayerProfile profile){
        this.player = player;
        this.profile = profile;
    }
    @Override
    public void run() {
        EventUtils.callPlayerProfileLoadEvent(this.player, this.profile);
    }
}
