package com.gmail.nossr50.datatypes.party;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import com.gmail.nossr50.party.ShareHandler;

public class Party {
    private List<OfflinePlayer> members = new ArrayList<OfflinePlayer>();
    private String leader;
    private String name;
    private String password;
    private boolean locked;

    private ShareHandler.ShareMode xpShareMode   = ShareHandler.ShareMode.NONE;
    private ShareHandler.ShareMode itemShareMode = ShareHandler.ShareMode.NONE;

    private boolean shareLootDrops        = true;
    private boolean shareMiningDrops      = true;
    private boolean shareHerbalismDrops   = true;
    private boolean shareWoodcuttingDrops = true;

    public List<OfflinePlayer> getMembers() {
        return members;
    }

    public List<Player> getOnlineMembers() {
        List<Player> onlineMembers = new ArrayList<Player>();

        for (OfflinePlayer member : members) {
            if (member.isOnline()) {
                onlineMembers.add(member.getPlayer());
            }
        }

        return onlineMembers;
    }

    public String getLeader() {
        return leader;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public boolean isLocked() {
        return locked;
    }

    public boolean sharingLootDrops() {
        return shareLootDrops;
    }

    public boolean sharingMiningDrops() {
        return shareMiningDrops;
    }

    public boolean sharingHerbalismDrops() {
        return shareHerbalismDrops;
    }

    public boolean sharingWoodcuttingDrops() {
        return shareWoodcuttingDrops;
    }

    public List<String> getItemShareCategories() {
        List<String> shareCategories = new ArrayList<String>();

        // TODO Locale the category names!
        if (sharingLootDrops()) {
            shareCategories.add("Loot");
        }

        if (sharingMiningDrops()) {
            shareCategories.add("Mining");
        }

        if (sharingHerbalismDrops()) {
            shareCategories.add("Herbalism");
        }

        if (sharingWoodcuttingDrops()) {
            shareCategories.add("Woodcutting");
        }

        return shareCategories;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLeader(String leader) {
        this.leader = leader;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public void setXpShareMode(ShareHandler.ShareMode xpShareMode) {
        this.xpShareMode = xpShareMode;
    }

    public ShareHandler.ShareMode getXpShareMode() {
        return xpShareMode;
    }

    public void setItemShareMode(ShareHandler.ShareMode itemShareMode) {
        this.itemShareMode = itemShareMode;
    }

    public ShareHandler.ShareMode getItemShareMode() {
        return itemShareMode;
    }

    public void setSharingLootDrops(boolean enabled) {
        shareLootDrops = enabled;
    }

    public void setSharingMiningDrops(boolean enabled) {
        shareMiningDrops = enabled;
    }

    public void setSharingHerbalismDrops(boolean enabled) {
        shareHerbalismDrops = enabled;
    }

    public void setSharingWoodcuttingDrops(boolean enabled) {
        shareWoodcuttingDrops = enabled;
    }
}
