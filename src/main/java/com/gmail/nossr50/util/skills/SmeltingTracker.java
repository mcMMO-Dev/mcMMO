package com.gmail.nossr50.util.skills;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.skills.smelting.Smelting;
import com.gmail.nossr50.util.EventUtils;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.player.UserManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Furnace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class SmeltingTracker {

    private final HashMap<Furnace, OfflinePlayer> furnaceOwners;

    public SmeltingTracker() {
        furnaceOwners = new HashMap<>();
    }

    private void changeFurnaceOwnership(Furnace furnace, Player player) {

        McMMOPlayer mcMMOPlayer = UserManager.getPlayer(player);

        /*
            Debug output
         */
        printOwnershipGainDebug(furnace, mcMMOPlayer);

        printOwnershipLossDebug(furnace);

        furnaceOwners.put(furnace, player);
    }

    private void printOwnershipGainDebug(Furnace furnace, McMMOPlayer mcMMOPlayer) {
        if(mcMMOPlayer != null) {
            if(mcMMOPlayer.isDebugMode()) {
                mcMMOPlayer.getPlayer().sendMessage("Furnace ownership " +
                        ChatColor.GREEN +"gained " + ChatColor.RESET +
                        "at location: " + furnace.getLocation().toString());
            }

        }
    }

    private void printOwnershipLossDebug(Furnace furnace) {
        if(furnaceOwners.get(furnace) != null) {
            OfflinePlayer furnaceOwner = furnaceOwners.get(furnace);

            if(furnaceOwner.isOnline()) {
                McMMOPlayer furnaceOwnerProfile = UserManager.getPlayer(furnaceOwner.getPlayer());

                if(furnaceOwnerProfile != null) {
                    if(furnaceOwnerProfile.isDebugMode()) {
                        furnaceOwnerProfile.getPlayer().sendMessage("Furnace ownership " +
                                ChatColor.RED + "lost " + ChatColor.RESET +
                                "at location: " + furnace.getLocation().toString());
                    }
                }
            }
        }
    }

    @Nullable
    public Furnace getFurnaceFromInventory(Inventory inventory) {
        if (!(inventory instanceof FurnaceInventory)) {
            return null;
        }

        return (Furnace) inventory.getHolder();
    }

    @Nullable
    public OfflinePlayer getPlayerFromFurnace(Furnace furnace) {
        return furnaceOwners.get(furnace);
    }

    public boolean isFurnaceOwned(Furnace furnace) {
        return furnaceOwners.get(furnace) != null;
    }

    public void processFurnaceOwnership(Furnace furnace, Player player) {
        if(!Permissions.skillEnabled(player, PrimarySkillType.SMELTING))
            return;

        changeFurnaceOwnership(furnace, player);
    }

    public void untrackFurnace(Furnace furnace) {
        printOwnershipLossDebug(furnace);
        furnaceOwners.remove(furnace);
    }
}
