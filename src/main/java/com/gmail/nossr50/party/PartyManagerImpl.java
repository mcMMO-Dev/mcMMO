package com.gmail.nossr50.party;

import com.neetgames.mcmmo.party.Party;
import com.neetgames.mcmmo.party.PartyManager;
import com.gmail.nossr50.events.party.McMMOPartyAllianceChangeEvent;
import com.gmail.nossr50.events.party.McMMOPartyChangeEvent;
import com.gmail.nossr50.events.party.McMMOPartyChangeEvent.EventReason;
import com.gmail.nossr50.mcMMO;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;

/**
 * About mcMMO parties
 * Parties are identified by a {@link String} name
 * Parties always have a party leader, if the party leader is not defined mcMMO will force party leadership onto someone in the party
 */
//TODO: Needs to be optimized, currently all parties are loaded into memory, it should be changed to as needed, but then we need to handle loading asynchronously and accommodate for that
public final class PartyManagerImpl implements PartyManager {
    private final @NotNull HashMap<String, Party> parties;
    private final @NotNull File partyFile;

    public PartyManagerImpl() {
        String partiesFilePath = mcMMO.getFlatFileDirectory() + "parties.yml";
        partyFile = new File(partiesFilePath);
        parties = new HashMap<>();
    }

//    /**
//     * Load party file.
//     */
//    public void loadParties() {
//        if (!partyFile.exists()) {
//            return;
//        }
//
//        if (mcMMO.getUpgradeManager().shouldUpgrade(UpgradeType.ADD_UUIDS_PARTY)) {
//            loadAndUpgradeParties();
//            return;
//        }
//
//        try {
//            YamlConfiguration partiesFile;
//            partiesFile = YamlConfiguration.loadConfiguration(partyFile);
//
//            ArrayList<Party> hasAlly = new ArrayList<>();
//
//            for (String partyName : partiesFile.getConfigurationSection("").getKeys(false)) {
//                Party party = new Party(partyName);
//
//                String[] leaderSplit = partiesFile.getString(partyName + ".Leader").split("[|]");
//                party.setLeader(new PartyLeader(UUID.fromString(leaderSplit[0]), leaderSplit[1]));
//                party.setPartyPassword(partiesFile.getString(partyName + ".Password"));
//                party.setPartyLock(partiesFile.getBoolean(partyName + ".Locked"));
//                party.setLevel(partiesFile.getInt(partyName + ".Level"));
//                party.setXp(partiesFile.getInt(partyName + ".Xp"));
//
//                if (partiesFile.getString(partyName + ".Ally") != null) {
//                    hasAlly.add(party);
//                }
//
//                party.setXpShareMode(ShareMode.getShareMode(partiesFile.getString(partyName + ".ExpShareMode", "NONE")));
//                party.setItemShareMode(ShareMode.getShareMode(partiesFile.getString(partyName + ".ItemShareMode", "NONE")));
//
//                for (ItemShareType itemShareType : ItemShareType.values()) {
//                    party.setSharingDrops(itemShareType, partiesFile.getBoolean(partyName + ".ItemShareType." + itemShareType.toString(), true));
//                }
//
//                LinkedHashMap<UUID, String> members = party.getMembers();
//
//                for (String memberEntry : partiesFile.getStringList(partyName + ".Members")) {
//                    String[] memberSplit = memberEntry.split("[|]");
//                    members.put(UUID.fromString(memberSplit[0]), memberSplit[1]);
//                }
//
//                parties.add(party);
//            }
//
//            mcMMO.p.getLogger().info("Loaded (" + parties.size() + ") Parties...");
//
//            for (Party party : hasAlly) {
//                party.setAlly(mcMMO.getPartyManager().getParty(partiesFile.getString(party.getPartyName() + ".Ally")));
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    /**
//     * Save party file.
//     */
//    public void saveParties() {
//        if (partyFile.exists()) {
//            if (!partyFile.delete()) {
//                mcMMO.p.getLogger().warning("Could not delete party file. Party saving failed!");
//                return;
//            }
//        }
//
//        YamlConfiguration partiesFile = new YamlConfiguration();
//
//        mcMMO.p.getLogger().info("Saving Parties... (" + parties.size() + ")");
//        for (Party party : parties) {
//            String partyName = party.getPartyName();
//            PartyLeader leader = party.getLeader();
//
//            partiesFile.set(partyName + ".Leader", leader.getUniqueId().toString() + "|" + leader.getPlayerName());
//            partiesFile.set(partyName + ".Password", party.getPartyPassword());
//            partiesFile.set(partyName + ".Locked", party.isLocked());
//            partiesFile.set(partyName + ".Level", party.getLevel());
//            partiesFile.set(partyName + ".Xp", (int) party.getXp());
//            partiesFile.set(partyName + ".Ally", (party.getAlly() != null) ? party.getAlly().getPartyName() : "");
//            partiesFile.set(partyName + ".ExpShareMode", party.getXpShareMode().toString());
//            partiesFile.set(partyName + ".ItemShareMode", party.getItemShareMode().toString());
//
//            for (ItemShareType itemShareType : ItemShareType.values()) {
//                partiesFile.set(partyName + ".ItemShareType." + itemShareType.toString(), party.sharingDrops(itemShareType));
//            }
//
//            List<String> members = new ArrayList<>();
//
//            for (Entry<UUID, String> memberEntry : party.getMembers().entrySet()) {
//                String memberUniqueId = memberEntry.getKey() == null ? "" : memberEntry.getKey().toString();
//                String memberName = memberEntry.getValue();
//
//                if (!members.contains(memberName)) {
//                    members.add(memberUniqueId + "|" + memberName);
//                }
//            }
//
//            partiesFile.set(partyName + ".Members", members);
//        }
//
//        try {
//            partiesFile.save(partyFile);
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void loadAndUpgradeParties() {
//        YamlConfiguration partiesFile = YamlConfiguration.loadConfiguration(partyFile);
//
//        if (!partyFile.renameTo(new File(mcMMO.getFlatFileDirectory() + "parties.yml.converted"))) {
//            mcMMO.p.getLogger().severe("Could not rename parties.yml to parties.yml.converted!");
//            return;
//        }
//
//        ArrayList<Party> hasAlly = new ArrayList<>();
//
//        for (String partyName : partiesFile.getConfigurationSection("").getKeys(false)) {
//            Party party = new Party(partyName);
//
//            String leaderName = partiesFile.getString(partyName + ".Leader");
//            PlayerProfile profile = mcMMO.getDatabaseManager().queryPlayerDataByUUID(leaderName, false);
//
//            if (!profile.isLoaded()) {
//                mcMMO.p.getLogger().warning("Could not find UUID in database for party leader " + leaderName + " in party " + partyName);
//                continue;
//            }
//
//            UUID leaderUniqueId = profile.getUniqueId();
//
//            party.setLeader(new PartyLeader(leaderUniqueId, leaderName));
//            party.setPartyPassword(partiesFile.getString(partyName + ".Password"));
//            party.setPartyLock(partiesFile.getBoolean(partyName + ".Locked"));
//            party.setLevel(partiesFile.getInt(partyName + ".Level"));
//            party.setXp(partiesFile.getInt(partyName + ".Xp"));
//
//            if (partiesFile.getString(partyName + ".Ally") != null) {
//                hasAlly.add(party);
//            }
//
//            party.setXpShareMode(ShareMode.getShareMode(partiesFile.getString(partyName + ".ExpShareMode", "NONE")));
//            party.setItemShareMode(ShareMode.getShareMode(partiesFile.getString(partyName + ".ItemShareMode", "NONE")));
//
//            for (ItemShareType itemShareType : ItemShareType.values()) {
//                party.setSharingDrops(itemShareType, partiesFile.getBoolean(partyName + ".ItemShareType." + itemShareType.toString(), true));
//            }
//
//            LinkedHashMap<UUID, String> members = party.getMembers();
//
//            for (String memberName : partiesFile.getStringList(partyName + ".Members")) {
//                PlayerProfile memberProfile = mcMMO.getDatabaseManager().queryPlayerDataByUUID(memberName, false);
//
//                if (!memberProfile.isLoaded()) {
//                    mcMMO.p.getLogger().warning("Could not find UUID in database for party member " + memberName + " in party " + partyName);
//                    continue;
//                }
//
//                UUID memberUniqueId = memberProfile.getUniqueId();
//
//                members.put(memberUniqueId, memberName);
//            }
//
//            parties.add(party);
//        }
//
//        mcMMO.p.getLogger().info("Loaded (" + parties.size() + ") Parties...");
//
//        for (Party party : hasAlly) {
//            party.setAlly(mcMMO.getPartyManager().getParty(partiesFile.getString(party.getPartyName() + ".Ally")));
//        }
//
//        mcMMO.getUpgradeManager().setUpgradeCompleted(UpgradeType.ADD_UUIDS_PARTY);
//    }
}
