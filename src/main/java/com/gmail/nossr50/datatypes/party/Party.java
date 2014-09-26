package com.gmail.nossr50.datatypes.party;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.datatypes.experience.FormulaType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.party.PartyManager;
import com.gmail.nossr50.util.EventUtils;
import com.gmail.nossr50.util.Misc;

public class Party {
    private final LinkedHashMap<UUID, String> members = new LinkedHashMap<UUID, String>();
    private final List<Player> onlineMembers = new ArrayList<Player>();

    private PartyLeader leader;
    private String name;
    private String password;
    private boolean locked;
    private Party ally;
    private int level;
    private float xp;

    private ShareMode xpShareMode   = ShareMode.NONE;
    private ShareMode itemShareMode = ShareMode.NONE;

    private boolean shareLootDrops        = true;
    private boolean shareMiningDrops      = true;
    private boolean shareHerbalismDrops   = true;
    private boolean shareWoodcuttingDrops = true;
    private boolean shareMiscDrops        = true;

    public Party(String name) {
        this.name = name;
    }

    public Party(PartyLeader leader, String name) {
        this.leader = leader;
        this.name = name;
        this.locked = true;
        this.level = 0;
    }

    public Party(PartyLeader leader, String name, String password) {
        this.leader = leader;
        this.name = name;
        this.password = password;
        this.locked = true;
        this.level = 0;
    }

    public Party(PartyLeader leader, String name, String password, boolean locked) {
        this.leader = leader;
        this.name = name;
        this.password = password;
        this.locked = locked;
        this.level = 0;
    }

    public LinkedHashMap<UUID, String> getMembers() {
        return members;
    }

    public List<Player> getOnlineMembers() {
        return onlineMembers;
    }

    public List<String> getOnlinePlayerNames(CommandSender sender) {
        Player player = sender instanceof Player ? (Player) sender : null;
        List<String> onlinePlayerNames = new ArrayList<String>();

        for (Player onlinePlayer : getOnlineMembers()) {
            if (player != null && player.canSee(onlinePlayer)) {
                onlinePlayerNames.add(onlinePlayer.getName());
            }
        }

        return onlinePlayerNames;
    }

    public boolean addOnlineMember(Player player) {
        return onlineMembers.add(player);
    }

    public boolean removeOnlineMember(Player player) {
        return onlineMembers.remove(player);
    }

    public String getName() {
        return name;
    }

    public PartyLeader getLeader() {
        return leader;
    }

    public String getPassword() {
        return password;
    }

    public boolean isLocked() {
        return locked;
    }

    public Party getAlly() {
        return ally;
    }

    public List<String> getItemShareCategories() {
        List<String> shareCategories = new ArrayList<String>();

        for (ItemShareType shareType : ItemShareType.values()) {
            if (sharingDrops(shareType)) {
                shareCategories.add(shareType.getLocaleString());
            }
        }

        return shareCategories;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLeader(PartyLeader leader) {
        this.leader = leader;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public void setAlly(Party ally) {
        this.ally = ally;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public float getXp() {
        return xp;
    }

    public void setXp(float xp) {
        this.xp = xp;
    }

    public void addXp(float xp) {
        setXp(getXp() + xp);
    }

    protected float levelUp() {
        float xpRemoved = getXpToLevel();

        setLevel(getLevel() + 1);
        setXp(getXp() - xpRemoved);

        return xpRemoved;
    }

    public int getXpToLevel() {
        FormulaType formulaType = ExperienceConfig.getInstance().getFormulaType();
        return (mcMMO.getFormulaManager().getCachedXpToLevel(level, formulaType)) * (getOnlineMembers().size() + Config.getInstance().getPartyXpCurveMultiplier());
    }

    public String getXpToLevelPercentage() {
        DecimalFormat percent = new DecimalFormat("##0.00%");
        return percent.format(this.getXp() / getXpToLevel());
    }

    /**
     * Applies an experience gain
     *
     * @param xp Experience amount to add
     */
    public void applyXpGain(float xp) {
        if (!EventUtils.handlePartyXpGainEvent(this, xp)) {
            return;
        }

        if (getXp() < getXpToLevel()) {
            return;
        }

        int levelsGained = 0;
        float xpRemoved = 0;

        while (getXp() >= getXpToLevel()) {
            if (hasReachedLevelCap()) {
                setXp(0);
                return;
            }

            xpRemoved += levelUp();
            levelsGained++;
        }

        if (!EventUtils.handlePartyLevelChangeEvent(this, levelsGained, xpRemoved)) {
            return;
        }

        if (!Config.getInstance().getPartyInformAllMembers()) {
            Player leader = mcMMO.p.getServer().getPlayer(this.leader.getUniqueId());

            if (leader != null) {
                leader.sendMessage(LocaleLoader.getString("Party.LevelUp", levelsGained, getLevel()));

                if (Config.getInstance().getLevelUpSoundsEnabled()) {
                    leader.playSound(leader.getLocation(), Sound.LEVEL_UP, Misc.LEVELUP_VOLUME, Misc.LEVELUP_PITCH);
                }
            }
            return;
        }

        PartyManager.informPartyMembersLevelUp(this, levelsGained, getLevel());
    }

    public boolean hasReachedLevelCap() {
        return Config.getInstance().getPartyLevelCap() < getLevel() + 1;
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

    public boolean hasMember(String memberName) {
        return this.getMembers().values().contains(memberName);
    }

    public boolean hasMember(UUID uuid) {
        return this.getMembers().keySet().contains(uuid);
    }

    public String createMembersList(String playerName, List<Player> nearMembers) {
        StringBuilder memberList = new StringBuilder();

        for (Entry<UUID, String> memberEntry : this.getMembers().entrySet()) {
            UUID uuid = memberEntry.getKey();
            String memberName = memberEntry.getValue();

            Player member = mcMMO.p.getServer().getPlayer(uuid);

            if (this.getLeader().getUniqueId().equals(uuid)) {
                memberList.append(ChatColor.GOLD);

                if (member == null) {
                    memberName = memberName.substring(0, 1) + ChatColor.GRAY + ChatColor.ITALIC + "" + memberName.substring(1);
                }
            }
            else if (member != null) {
                memberList.append(ChatColor.WHITE);
            }
            else {
                memberList.append(ChatColor.GRAY);
            }

            if (!nearMembers.contains(member) && !playerName.equalsIgnoreCase(memberName)) {
                memberList.append(ChatColor.ITALIC).append("");
            }

            memberList.append(memberName).append(ChatColor.RESET).append(" ");
        }

        return memberList.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (!(obj instanceof Party)) {
            return false;
        }

        Party other = (Party) obj;

        if ((this.getName() == null) || (other.getName() == null)) {
            return false;
        }

        return this.getName().equals(other.getName());
    }
}
