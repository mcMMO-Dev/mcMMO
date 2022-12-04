package com.gmail.nossr50.util.skills;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.player.UserManager;
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

        McMMOPlayer mcMMOPlayer = UserManager.getPlayer(player);

        /*
            Debug output
         */
        printOwnershipGainDebug(furnace, mcMMOPlayer);

        printOwnershipLossDebug(furnace);

        setFurnaceOwner(furnace, player);
    }

    private void setFurnaceOwner(Furnace furnace, Player player) {
        mcMMO.getMetadataService().getBlockMetadataService().setFurnaceOwner(furnace, player.getUniqueId());
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
        OfflinePlayer furnaceOwner = getFurnaceOwner(furnace);

        if(furnaceOwner != null && furnaceOwner.isOnline()) {
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

    public @Nullable OfflinePlayer getFurnaceOwner(Furnace furnace) {
        UUID uuid = mcMMO.getMetadataService().getBlockMetadataService().getFurnaceOwner(furnace);

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
        if(!mcMMO.p.getSkillTools().doesPlayerHaveSkillPermission(player, PrimarySkillType.SMELTING))
            return;

        //Don't swap ownership if its the same player
        if(getFurnaceOwner(furnace) != null) {
            if(getFurnaceOwner(furnace).getUniqueId().equals(player.getUniqueId()))
                return;
        }

        changeFurnaceOwnership(furnace, player);
    }
}
