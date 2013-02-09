package com.gmail.nossr50.party;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class Party {
    private List<OfflinePlayer> members = new ArrayList<OfflinePlayer>();
    private String leader;
    private String name;
    private String password;
    private boolean locked;
    private ShareHandler.ShareMode xpShareMode;
    private ShareHandler.ShareMode itemShareMode;

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
}
