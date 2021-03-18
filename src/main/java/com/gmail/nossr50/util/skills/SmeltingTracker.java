package com.gmail.nossr50.util.skills;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.Permissions;
import com.neetgames.mcmmo.player.OnlineMMOPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Furnace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class SmeltingTracker {

//    private final HashMap<Furnace, OfflinePlayer> furnaceOwners;

    private void changeFurnaceOwnership(Furnace furnace, Player player) {

        OnlineMMOPlayer mmoPlayer = mcMMO.getUserManager().queryPlayer(player);

        /*
            Debug output
         */
        printOwnershipGainDebug(furnace, mmoPlayer);

        printOwnershipLossDebug(furnace);

        setFurnaceOwner(furnace, player);
    }

    private void setFurnaceOwner(Furnace furnace, Player player) {
        mcMMO.getCompatibilityManager().getPersistentDataLayer().setFurnaceOwner(furnace, player.getUniqueId());
    }

    private void printOwnershipGainDebug(Furnace furnace, OnlineMMOPlayer mmoPlayer) {
        if(mmoPlayer != null) {
            if(mmoPlayer.isDebugMode()) {
                Misc.adaptPlayer(mmoPlayer).sendMessage("Furnace ownership " +
                        ChatColor.GREEN +"gained " + ChatColor.RESET +
                        "at location: " + furnace.getLocation().toString());
            }

        }
    }

    private void printOwnershipLossDebug(Furnace furnace) {
        OfflinePlayer furnaceOwner = getFurnaceOwner(furnace);

        if(furnaceOwner != null && furnaceOwner.isOnline()) {
            OnlineMMOPlayer furnaceOwnerProfile = mcMMO.getUserManager().queryPlayer(furnaceOwner.getPlayer());

            if(furnaceOwnerProfile != null) {
                if(furnaceOwnerProfile.isDebugMode()) {
                    furnaceOwnerProfile.getPlayer().sendMessage("Furnace ownership " +
                            ChatColor.RED + "lost " + ChatColor.RESET +
                            "at location: " + furnace.getLocation().toString());
                }
            }
        }
    }

    public @Nullable OfflinePlayer getFurnaceOwner(Furnace furnace) {
        UUID uuid = mcMMO.getCompatibilityManager().getPersistentDataLayer().getFurnaceOwner(furnace);

        if(uuid != null) {
            return Bukkit.getOfflinePlayer(uuid);
        } else {
            return null;
        }
    }

    @Nullable
    public Furnace getFurnaceFromInventory(Inventory inventory) {
        if (!(inventory instanceof FurnaceInventory)) {
            return null;
        }

        return (Furnace) inventory.getHolder();
    }

    public boolean isFurnaceOwned(Furnace furnace) {
        return getFurnaceOwner(furnace) != null;
    }

    public void processFurnaceOwnership(Furnace furnace, Player player) {
        if(!Permissions.skillEnabled(player, PrimarySkillType.SMELTING))
            return;

        //Don't swap ownership if its the same player
        if(getFurnaceOwner(furnace) != null) {
            if(getFurnaceOwner(furnace).getUniqueId().equals(player.getUniqueId()))
                return;
        }

        changeFurnaceOwnership(furnace, player);
    }
}
