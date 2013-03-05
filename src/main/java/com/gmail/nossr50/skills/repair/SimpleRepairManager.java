package com.gmail.nossr50.skills.repair;

import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.events.skills.repair.McMMOPlayerRepairCheckEvent;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.StringUtils;
import com.gmail.nossr50.util.skills.SkillUtils;

public class SimpleRepairManager implements RepairManager {
    private HashMap<Integer, Repairable> repairables;

    protected SimpleRepairManager() {
        this(55);
    }

    protected SimpleRepairManager(int repairablesSize) {
        this.repairables = new HashMap<Integer, Repairable>(repairablesSize);
    }

    @Override
    public void registerRepairable(Repairable repairable) {
        Integer itemId = repairable.getItemId();
        repairables.put(itemId, repairable);
    }

    @Override
    public void registerRepairables(List<Repairable> repairables) {
        for (Repairable repairable : repairables) {
            registerRepairable(repairable);
        }
    }

    @Override
    public boolean isRepairable(int itemId) {
        return repairables.containsKey(itemId);
    }

    @Override
    public boolean isRepairable(ItemStack itemStack) {
        return isRepairable(itemStack.getTypeId());
    }

    @Override
    public Repairable getRepairable(int id) {
        return repairables.get(id);
    }

    @Override
    public void handleRepair(McMMOPlayer mcMMOPlayer, ItemStack item) {
        Player player = mcMMOPlayer.getPlayer();
        Repairable repairable = repairables.get(item.getTypeId());

        // Permissions checks on material and item types
        if (!repairable.getRepairItemType().getPermissions(player)) {
            player.sendMessage(LocaleLoader.getString("mcMMO.NoPermission"));
            return;
        }

        if (!repairable.getRepairMaterialType().getPermissions(player)) {
            player.sendMessage(LocaleLoader.getString("mcMMO.NoPermission"));
            return;
        }

        int skillLevel = mcMMOPlayer.getProfile().getSkillLevel(SkillType.REPAIR);

        // Level check
        if (skillLevel < repairable.getMinimumLevel()) {
            player.sendMessage(LocaleLoader.getString("Repair.Skills.Adept", repairable.getMinimumLevel(), StringUtils.getPrettyItemString(item.getTypeId())));
            return;
        }

        PlayerInventory inventory = player.getInventory();

        // Check if they have the proper material to repair with
        if (!inventory.contains(repairable.getRepairMaterialId())) {
            String message = LocaleLoader.getString("Skills.NeedMore", StringUtils.getPrettyItemString(repairable.getRepairMaterialId()));
            if (repairable.getRepairMaterialMetadata() != (byte) -1) {
                // TODO: Do something nicer than append the metadata as a :# ?
                if (findInInventory(inventory, repairable.getRepairMaterialId(), repairable.getRepairMaterialMetadata()) == -1) {
                    message += ":" + repairable.getRepairMaterialMetadata();
                }
            }
            player.sendMessage(message);
            return;
        }

        short startDurability = item.getDurability();

        // Do not repair if at full durability
        if (startDurability <= 0) {
            player.sendMessage(LocaleLoader.getString("Repair.Skills.FullDurability"));
            return;
        }

        // Do not repair stacked items
        if (item.getAmount() != 1) {
            player.sendMessage(LocaleLoader.getString("Repair.Skills.StackedItems"));
            return;
        }

        // Clear ability buffs before trying to repair.
        SkillUtils.removeAbilityBuff(item);

        // Lets get down to business,
        // To defeat, the huns.
        int baseRepairAmount = repairable.getBaseRepairDurability(); // Did they send me daughters?
        short newDurability = Repair.repairCalculate(player, skillLevel, startDurability, baseRepairAmount); // When I asked for sons?

        // We're going to hold onto our repair item location
        int repairItemLocation;
        if (repairable.getRepairMaterialMetadata() == (byte) -1) {
            repairItemLocation = findInInventory(inventory, repairable.getRepairMaterialId());
        }
        else {
            // Special case for when the repairable has metadata that must be addressed
            repairItemLocation = findInInventory(inventory, repairable.getRepairMaterialId(), repairable.getRepairMaterialMetadata());
        }

        // This should never happen, but if it does we need to complain loudly about it.
        if (repairItemLocation == -1) {
            player.sendMessage(LocaleLoader.getString("Repair.Error"));
            return;
        }

        // Call event
        McMMOPlayerRepairCheckEvent event = new McMMOPlayerRepairCheckEvent(player, (short) (startDurability - newDurability), inventory.getItem(repairItemLocation), item);
        Bukkit.getServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return;
        }

        // Handle the enchants
        if (AdvancedConfig.getInstance().getArcaneForgingEnchantLossEnabled() && !Permissions.arcaneBypass(player)) {
            // Generalize away enchantment work
            Repair.addEnchants(player, item);
        }

        // Remove the item
        removeOneFrom(inventory, repairItemLocation);

        // Give out XP like candy
        Repair.xpHandler(mcMMOPlayer, startDurability, newDurability, repairable.getXpMultiplier());

        // Repair the item!
        item.setDurability(newDurability);
    }

    /**
     * Decrease the amount of items in this slot by one
     *
     * @param inventory PlayerInventory to work in
     * @param index Item index to decrement
     */
    private void removeOneFrom(PlayerInventory inventory, int index) {
        ItemStack item = inventory.getItem(index).clone();
        item.setAmount(1);

        inventory.removeItem(item);
    }

    /**
     * Search the inventory for an item and return the index.
     *
     * @param inventory PlayerInventory to scan
     * @param itemId Item id to look for
     * @return index location where the item was found, or -1 if not found
     */
    private int findInInventory(PlayerInventory inventory, int itemId) {
        int location = inventory.first(itemId);

        // VALIDATE
        if (inventory.getItem(location).getTypeId() == itemId) {
            return location;
        }

        return -1;
    }

    /**
     * Search the inventory for an item and return the index.
     *
     * @param inventory PlayerInventory to scan
     * @param itemId Item id to look for
     * @param metadata Metadata to look for
     * @return index location where the item was found, or -1 if not found
     */
    private int findInInventory(PlayerInventory inventory, int itemId, byte metadata) {
        int location = -1;

        for (ItemStack item : inventory.getContents()) {
            if (item == null) {
                continue;
            }

            if (item.getTypeId() == itemId && item.getData().getData() == metadata) {
                return location;
            }
        }

        return location;
    }
}
