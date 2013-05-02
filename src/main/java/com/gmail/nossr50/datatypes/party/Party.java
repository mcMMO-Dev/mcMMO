package com.gmail.nossr50.datatypes.party;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.party.ShareHandler.ShareMode;

public class Party {
    private LinkedHashSet<String> members = new LinkedHashSet<String>();
    private String leader;
    private String name;
    private String password;
    private boolean locked;

    private ShareMode xpShareMode   = ShareMode.NONE;
    private ShareMode itemShareMode = ShareMode.NONE;

    private boolean shareLootDrops        = true;
    private boolean shareMiningDrops      = true;
    private boolean shareHerbalismDrops   = true;
    private boolean shareWoodcuttingDrops = true;
    private boolean shareMiscDrops        = true;

    public LinkedHashSet<String> getMembers() {
        return members;
    }

    public List<Player> getOnlineMembers() {
        List<Player> onlineMembers = new ArrayList<Player>();

        for (String memberName : members) {
            OfflinePlayer member = mcMMO.p.getServer().getOfflinePlayer(memberName);
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

    public List<String> getItemShareCategories() {
        List<String> shareCategories = new ArrayList<String>();

        if (shareLootDrops) {
            shareCategories.add(LocaleLoader.getString("Party.ItemShare.Category.Loot"));
        }

        if (shareMiningDrops) {
            shareCategories.add(LocaleLoader.getString("Party.ItemShare.Category.Mining"));
        }

        if (shareHerbalismDrops) {
            shareCategories.add(LocaleLoader.getString("Party.ItemShare.Category.Herbalism"));
        }

        if (shareWoodcuttingDrops) {
            shareCategories.add(LocaleLoader.getString("Party.ItemShare.Category.Woodcutting"));
        }

        if (shareMiscDrops) {
            shareCategories.add(LocaleLoader.getString("Party.ItemShare.Category.Misc"));
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

    public void setXpShareMode(ShareMode xpShareMode) {
        this.xpShareMode = xpShareMode;
    }

    public ShareMode getXpShareMode() {
        return xpShareMode;
    }

    public void setItemShareMode(ShareMode itemShareMode) {
        this.itemShareMode = itemShareMode;
    }

    public ShareMode getItemShareMode() {
        return itemShareMode;
    }

    public boolean sharingDrops(ItemShareType shareType) {
        switch (shareType) {
            case HERBALISM:
                return shareHerbalismDrops;

            case LOOT:
                return shareLootDrops;

            case MINING:
                return shareMiningDrops;

            case MISC:
                return shareMiscDrops;

            case WOODCUTTING:
                return shareWoodcuttingDrops;

            default:
                return false;
        }
    }

    public void setSharingDrops(ItemShareType shareType, boolean enabled) {
        switch (shareType) {
            case HERBALISM:
                shareHerbalismDrops = enabled;
                break;

            case LOOT:
                shareLootDrops = enabled;
                break;

            case MINING:
                shareMiningDrops = enabled;
                break;

            case MISC:
                shareMiscDrops = enabled;
                break;

            case WOODCUTTING:
                shareWoodcuttingDrops = enabled;
                break;

            default:
                return;
        }
    }
}
