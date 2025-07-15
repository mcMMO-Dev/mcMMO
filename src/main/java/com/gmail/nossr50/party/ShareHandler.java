package com.gmail.nossr50.party;

import com.gmail.nossr50.config.party.ItemWeightConfig;
import com.gmail.nossr50.datatypes.experience.XPGainReason;
import com.gmail.nossr50.datatypes.experience.XPGainSource;
import com.gmail.nossr50.datatypes.party.ItemShareType;
import com.gmail.nossr50.datatypes.party.Party;
import com.gmail.nossr50.datatypes.party.ShareMode;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.player.UserManager;
import java.util.List;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class ShareHandler {
    private ShareHandler() {
    }

    /**
     * Distribute Xp amongst party members.
     *
     * @param xp Xp without party sharing
     * @param mmoPlayer Player initiating the Xp gain
     * @param primarySkillType Skill being used
     * @return True is the xp has been shared
     */
    public static boolean handleXpShare(float xp, McMMOPlayer mmoPlayer,
            PrimarySkillType primarySkillType, XPGainReason xpGainReason) {
        Party party = mmoPlayer.getParty();

        if (party.getXpShareMode() != ShareMode.EQUAL) {
            return false;
        }

        List<Player> nearMembers = mcMMO.p.getPartyManager().getNearVisibleMembers(mmoPlayer);

        if (nearMembers.isEmpty()) {
            return false;
        }

        nearMembers.add(mmoPlayer.getPlayer());

        int partySize = nearMembers.size();
        double shareBonus = Math.min(mcMMO.p.getGeneralConfig().getPartyShareBonusBase()
                        + (partySize * mcMMO.p.getGeneralConfig().getPartyShareBonusIncrease()),
                mcMMO.p.getGeneralConfig().getPartyShareBonusCap());
        float splitXp = (float) (xp / partySize * shareBonus);

        for (Player member : nearMembers) {
            //Profile not loaded
            if (UserManager.getPlayer(member) == null) {
                continue;
            }

            UserManager.getPlayer(member)
                    .beginUnsharedXpGain(primarySkillType, splitXp, xpGainReason,
                            XPGainSource.PARTY_MEMBERS);
        }

        return true;
    }

    /**
     * Distribute Items amongst party members.
     *
     * @param drop Item that will get shared
     * @param mmoPlayer Player who picked up the item
     * @return True if the item has been shared
     */
    public static boolean handleItemShare(Item drop, McMMOPlayer mmoPlayer) {
        ItemStack itemStack = drop.getItemStack();
        ItemShareType dropType = ItemShareType.getShareType(itemStack);

        if (dropType == null) {
            return false;
        }

        Party party = mmoPlayer.getParty();

        if (!party.sharingDrops(dropType)) {
            return false;
        }

        ShareMode shareMode = party.getItemShareMode();

        if (shareMode == ShareMode.NONE) {
            return false;
        }

        List<Player> nearMembers = mcMMO.p.getPartyManager().getNearMembers(mmoPlayer);

        if (nearMembers.isEmpty()) {
            return false;
        }

        Player winningPlayer = null;
        ItemStack newStack = itemStack.clone();

        nearMembers.add(mmoPlayer.getPlayer());
        int partySize = nearMembers.size();

        drop.remove();
        newStack.setAmount(1);

        switch (shareMode) {
            case EQUAL:
                int itemWeight = ItemWeightConfig.getInstance().getItemWeight(itemStack.getType());

                for (int i = 0; i < itemStack.getAmount(); i++) {
                    int highestRoll = 0;

                    for (Player member : nearMembers) {
                        McMMOPlayer mcMMOMember = UserManager.getPlayer(member);

                        //Profile not loaded
                        if (UserManager.getPlayer(member) == null) {
                            continue;
                        }

                        int itemShareModifier = mcMMOMember.getItemShareModifier();
                        int diceRoll = Misc.getRandom().nextInt(itemShareModifier);

                        if (diceRoll <= highestRoll) {
                            mcMMOMember.setItemShareModifier(itemShareModifier + itemWeight);
                            continue;
                        }

                        highestRoll = diceRoll;

                        if (winningPlayer != null) {
                            McMMOPlayer mcMMOWinning = UserManager.getPlayer(winningPlayer);
                            mcMMOWinning.setItemShareModifier(
                                    mcMMOWinning.getItemShareModifier() + itemWeight);
                        }

                        winningPlayer = member;
                    }

                    McMMOPlayer mcMMOTarget = UserManager.getPlayer(winningPlayer);
                    mcMMOTarget.setItemShareModifier(
                            mcMMOTarget.getItemShareModifier() - itemWeight);
                    awardDrop(winningPlayer, newStack);
                }

                return true;

            case RANDOM:
                for (int i = 0; i < itemStack.getAmount(); i++) {
                    winningPlayer = nearMembers.get(Misc.getRandom().nextInt(partySize));
                    awardDrop(winningPlayer, newStack);
                }

                return true;

            default:
                return false;
        }
    }

    public static XPGainReason getSharedXpGainReason(XPGainReason xpGainReason) {
        if (xpGainReason == XPGainReason.PVE) {
            return XPGainReason.SHARED_PVE;
        } else if (xpGainReason == XPGainReason.PVP) {
            return XPGainReason.SHARED_PVP;
        } else {
            return xpGainReason;
        }
    }

    private static void awardDrop(Player winningPlayer, ItemStack drop) {
        if (winningPlayer.getInventory().addItem(drop).size() != 0) {
            winningPlayer.getWorld().dropItem(winningPlayer.getLocation(), drop);
        }

        winningPlayer.updateInventory();
    }
}
