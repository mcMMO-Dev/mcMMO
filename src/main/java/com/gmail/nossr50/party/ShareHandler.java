package com.gmail.nossr50.party;

import java.util.List;

import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.config.party.ItemWeightConfig;
import com.gmail.nossr50.datatypes.party.ItemShareType;
import com.gmail.nossr50.datatypes.party.Party;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.commands.CommandUtils;
import com.gmail.nossr50.util.player.UserManager;

public final class ShareHandler {
    public enum ShareMode {
        NONE,
        EQUAL,
        RANDOM;

        public static ShareMode getShareMode(String string) {
            try {
                return valueOf(string);
            }
            catch (IllegalArgumentException ex) {
                if (string.equalsIgnoreCase("even")) {
                    return EQUAL;
                }
                else if (CommandUtils.shouldDisableToggle(string)) {
                    return NONE;
                }

                return null;
            }
        }
    };

    private ShareHandler() {}

    private static List<Player> nearMembers;
    private static int partySize;

    /**
     * Distribute Xp amongst party members.
     *
     * @param xp Xp without party sharing
     * @param mcMMOPlayer Player initiating the Xp gain
     * @param skillType Skill being used
     * @return True is the xp has been shared
     */
    public static boolean handleXpShare(float xp, McMMOPlayer mcMMOPlayer, SkillType skillType) {
        Party party = mcMMOPlayer.getParty();

        switch (party.getXpShareMode()) {
            case EQUAL:
                Player player = mcMMOPlayer.getPlayer();
                nearMembers = PartyManager.getNearMembers(player, party, Config.getInstance().getPartyShareRange());

                if (nearMembers.isEmpty()) {
                    return false;
                }

                double partySize = nearMembers.size() + 1;
                double shareBonus = Config.getInstance().getPartyShareBonusBase() + partySize * Config.getInstance().getPartyShareBonusIncrease();

                if (shareBonus > Config.getInstance().getPartyShareBonusCap()) {
                    shareBonus = Config.getInstance().getPartyShareBonusCap();
                }

                float splitXp = (float) (xp / partySize * shareBonus);

                for (Player member : nearMembers) {
                    UserManager.getPlayer(member).beginUnsharedXpGain(skillType, splitXp);
                }

                mcMMOPlayer.beginUnsharedXpGain(skillType, splitXp);

                return true;

            case NONE:
            default:
                return false;
        }
    }

    /**
     * Distribute Items amongst party members.
     *
     * @param item Item that will get shared
     * @param mcMMOPlayer Player who picked up the item
     * @return True if the item has been shared
     */
    public static boolean handleItemShare(Item drop, McMMOPlayer mcMMOPlayer) {
        ItemStack itemStack = drop.getItemStack();
        Party party = mcMMOPlayer.getParty();
        Player player = mcMMOPlayer.getPlayer();
        Player winningPlayer = null;

        ItemStack newStack = itemStack.clone();
        newStack.setAmount(1);

        ItemShareType dropType = ItemShareType.getShareType(itemStack);

        if (dropType == null || !party.sharingDrops(dropType)) {
            return false;
        }

        switch (party.getItemShareMode()) {
            case EQUAL:
                McMMOPlayer mcMMOTarget;
                nearMembers = PartyManager.getNearMembers(player, party, Config.getInstance().getPartyShareRange());

                if (nearMembers.isEmpty()) {
                    return false;
                }

                nearMembers.add(player);
                partySize = nearMembers.size();

                drop.remove();
                int itemWeight = ItemWeightConfig.getInstance().getItemWeight(itemStack.getType());

                for (int i = 0; i < itemStack.getAmount(); i++) {
                    int highestRoll = 0;

                    for (Player member : nearMembers) {
                        McMMOPlayer mcMMOMember = UserManager.getPlayer(member);
                        int itemShareModifier = mcMMOMember.getItemShareModifier();
                        int diceRoll = Misc.getRandom().nextInt(itemShareModifier);

                        if (diceRoll > highestRoll) {
                            highestRoll = diceRoll;

                            if (winningPlayer != null) {
                                McMMOPlayer mcMMOWinning = UserManager.getPlayer(winningPlayer);
                                mcMMOWinning.setItemShareModifier(mcMMOWinning.getItemShareModifier() + itemWeight);
                            }

                            winningPlayer = member;
                        }
                        else {
                            mcMMOMember.setItemShareModifier(itemShareModifier + itemWeight);
                        }
                    }

                    mcMMOTarget = UserManager.getPlayer(winningPlayer);
                    mcMMOTarget.setItemShareModifier(mcMMOTarget.getItemShareModifier() - itemWeight);

                    if (winningPlayer.getInventory().addItem(newStack).size() != 0) {
                        winningPlayer.getWorld().dropItemNaturally(winningPlayer.getLocation(), newStack);
                    }

                    winningPlayer.updateInventory();
                }
                return true;

            case RANDOM:
                nearMembers = PartyManager.getNearMembers(player, party, Config.getInstance().getPartyShareRange());

                if (nearMembers.isEmpty()) {
                    return false;
                }

                partySize = nearMembers.size() + 1;

                drop.remove();

                for (int i = 0; i < itemStack.getAmount(); i++) {
                    int randomMember = Misc.getRandom().nextInt(partySize);

                    if (randomMember >= nearMembers.size()) {
                        winningPlayer = player;
                    }
                    else {
                        winningPlayer = nearMembers.get(randomMember);
                    }

                    if (winningPlayer.getInventory().addItem(newStack).size() != 0) {
                        winningPlayer.getWorld().dropItemNaturally(winningPlayer.getLocation(), newStack);
                    }

                    winningPlayer.updateInventory();
                }
                return true;

            case NONE:
            default:
                return false;
        }
    }
}
