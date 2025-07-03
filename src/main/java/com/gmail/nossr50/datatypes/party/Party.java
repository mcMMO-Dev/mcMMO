package com.gmail.nossr50.datatypes.party;

import com.gmail.nossr50.chat.SamePartyPredicate;
import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.datatypes.experience.FormulaType;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.EventUtils;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.sounds.SoundManager;
import com.gmail.nossr50.util.sounds.SoundType;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.function.Predicate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Party {

    private static final DecimalFormat percent = new DecimalFormat("##0.00%",
            DecimalFormatSymbols.getInstance(Locale.US));

    private final @NotNull Predicate<CommandSender> samePartyPredicate;
    private final LinkedHashMap<UUID, String> members = new LinkedHashMap<>();
    private final List<Player> onlineMembers = new ArrayList<>();

    private PartyLeader leader;
    private String name;
    private String password;
    private boolean locked;
    private Party ally;
    private int level;
    private float xp;

    private ShareMode xpShareMode = ShareMode.NONE;
    private ShareMode itemShareMode = ShareMode.NONE;

    private boolean shareLootDrops = true;
    private boolean shareMiningDrops = true;
    private boolean shareHerbalismDrops = true;
    private boolean shareWoodcuttingDrops = true;
    private boolean shareMiscDrops = true;

    public Party(String name) {
        this.name = name;
        samePartyPredicate = new SamePartyPredicate<>(this);
    }

    public Party(PartyLeader leader, String name) {
        this.leader = leader;
        this.name = name;
        this.locked = true;
        this.level = 0;
        samePartyPredicate = new SamePartyPredicate<>(this);
    }

    public Party(PartyLeader leader, String name, String password) {
        this.leader = leader;
        this.name = name;
        this.password = password;
        this.locked = true;
        this.level = 0;
        samePartyPredicate = new SamePartyPredicate<>(this);
    }

    public Party(PartyLeader leader, String name, String password, boolean locked) {
        this.leader = leader;
        this.name = name;
        this.password = password;
        this.locked = locked;
        this.level = 0;
        samePartyPredicate = new SamePartyPredicate<>(this);
    }

    public LinkedHashMap<UUID, String> getMembers() {
        return members;
    }

    public List<Player> getOnlineMembers() {
        return onlineMembers;
    }

    public List<Player> getVisibleMembers(Player player) {
        ArrayList<Player> visibleMembers = new ArrayList<>();

        for (Player p : onlineMembers) {
            if (player.canSee(p)) {
                visibleMembers.add(p);
            }
        }

        return visibleMembers;
    }

    public List<String> getOnlinePlayerNames(CommandSender sender) {
        Player player = sender instanceof Player ? (Player) sender : null;
        List<String> onlinePlayerNames = new ArrayList<>();

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
        List<String> shareCategories = new ArrayList<>();

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
        return (mcMMO.getFormulaManager().getXPtoNextLevel(level, formulaType)) * (
                getOnlineMembers().size() + mcMMO.p.getGeneralConfig().getPartyXpCurveMultiplier());
    }

    public String getXpToLevelPercentage() {
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

        if (!mcMMO.p.getGeneralConfig().getPartyInformAllMembers()) {
            Player leader = mcMMO.p.getServer().getPlayer(this.leader.getUniqueId());

            if (leader != null) {
                leader.sendMessage(
                        LocaleLoader.getString("Party.LevelUp", levelsGained, getLevel()));

                if (mcMMO.p.getGeneralConfig().getLevelUpSoundsEnabled()) {
                    SoundManager.sendSound(leader, leader.getLocation(), SoundType.LEVEL_UP);
                }
            }
        } else {
            mcMMO.p.getPartyManager().informPartyMembersLevelUp(this, levelsGained, getLevel());
        }

    }

    public boolean hasReachedLevelCap() {
        return mcMMO.p.getGeneralConfig().getPartyLevelCap() < getLevel() + 1;
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
        }
    }

    public boolean hasMember(String memberName) {
        return this.getMembers().values().stream().anyMatch(memberName::equalsIgnoreCase);
    }

    public boolean hasMember(UUID uuid) {
        return this.getMembers().containsKey(uuid);
    }

    /**
     * Makes a formatted list of party members based on the perspective of a target player Players
     * that are hidden will be shown as offline (formatted in the same way) Party leader will be
     * formatted a specific way as well
     *
     * @param player target player to use as POV
     * @return formatted list of party members from the POV of a player
     */
    public String createMembersList(Player player) {
        StringBuilder memberList = new StringBuilder();
        List<String> coloredNames = new ArrayList<>();

        for (UUID playerUUID : members.keySet()) {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerUUID);

            if (offlinePlayer.isOnline() && player.canSee((Player) offlinePlayer)) {
                ChatColor onlineColor =
                        leader.getUniqueId().equals(playerUUID) ? ChatColor.GOLD : ChatColor.GREEN;
                coloredNames.add(onlineColor + offlinePlayer.getName());
            } else {
                coloredNames.add(ChatColor.DARK_GRAY + members.get(playerUUID));
            }
        }

        buildChatMessage(memberList, coloredNames.toArray(new String[0]));
        return memberList.toString();
    }

    private void buildChatMessage(@NotNull StringBuilder stringBuilder, String @NotNull [] names) {
        for (int i = 0; i < names.length; i++) {
            if (i + 1 >= names.length) {
                stringBuilder
                        .append(names[i]);
            } else {
                stringBuilder
                        .append(names[i])
                        .append(" ");
            }
        }
    }

    /**
     * Get the near party members.
     *
     * @param mmoPlayer The player to check
     * @return the near party members
     */
    public List<Player> getNearMembers(McMMOPlayer mmoPlayer) {
        List<Player> nearMembers = new ArrayList<>();
        Party party = mmoPlayer.getParty();

        if (party != null) {
            Player player = mmoPlayer.getPlayer();
            double range = mcMMO.p.getGeneralConfig().getPartyShareRange();

            for (Player member : party.getOnlineMembers()) {
                if (!player.equals(member) && member.isValid() && Misc.isNear(player.getLocation(),
                        member.getLocation(), range)) {
                    nearMembers.add(member);
                }
            }
        }

        return nearMembers;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (!(obj instanceof Party other)) {
            return false;
        }

        if ((this.getName() == null) || (other.getName() == null)) {
            return false;
        }

        return this.getName().equals(other.getName());
    }

    public @NotNull Predicate<CommandSender> getSamePartyPredicate() {
        return samePartyPredicate;
    }
}
