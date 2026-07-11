package com.gmail.nossr50.util;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.player.PlayerProfile;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

public record TestPlayerMock(Player player, PlayerInventory playerInventory,
                             Location playerLocation, PlayerProfile playerProfile,
                             McMMOPlayer mmoPlayer) {
}
