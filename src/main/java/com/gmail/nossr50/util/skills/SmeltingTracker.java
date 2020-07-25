package com.gmail.nossr50.util.skills;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.skills.smelting.Smelting;
import com.gmail.nossr50.util.EventUtils;
import com.gmail.nossr50.util.Permissions;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Furnace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class SmeltingTracker {

    private HashMap<Furnace, OfflinePlayer> furnaceOwners;

    public SmeltingTracker() {
        furnaceOwners = new HashMap<>();
    }

    private void changeFurnaceOwnership(Furnace furnace, Player player) {
        furnaceOwners.put(furnace, player);
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

    public void removeFurnaceOwner(Furnace furnace) {
        furnaceOwners.remove(furnace);
    }

    public void processFurnaceOwnership(Furnace furnace, Player player) {
        if(!Permissions.skillEnabled(player, PrimarySkillType.SMELTING))
            return;

        changeFurnaceOwnership(furnace, player);
    }

    public void untrackFurnace(Furnace furnace) {
        furnaceOwners.remove(furnace);
    }
}
