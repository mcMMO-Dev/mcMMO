package com.gmail.nossr50.party;

import java.util.List;

import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.config.party.ItemWeightConfig;
import com.gmail.nossr50.datatypes.party.Party;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.util.ItemUtils;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.player.UserManager;

public final class ShareHandler {
    public enum ShareMode {
        NONE,
        EQUAL,
        RANDOM;

        public static ShareMode getFromString(String string) {
            try {
                return valueOf(string);
            }
            catch (IllegalArgumentException exception) {
                return NONE;
            }
            catch (NullPointerException exception) {
                return NONE;
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
    public static boolean handleXpShare(int xp, McMMOPlayer mcMMOPlayer, SkillType skillType) {
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

                double splitXp = xp / partySize * shareBonus;
                int roundedXp = (int) Math.ceil(splitXp);

                for (Player member : nearMembers) {
                    UserManager.getPlayer(member).beginUnsharedXpGain(skillType, roundedXp);
                }

                mcMMOPlayer.beginUnsharedXpGain(skillType, roundedXp);

                return true;
            case NONE:
                // Fallthrough
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
    public static boolean handleItemShare(PlayerPickupItemEvent event, McMMOPlayer mcMMOPlayer) {
        Item item = event.getItem();
        ItemStack itemStack = item.getItemStack();
        Party party = mcMMOPlayer.getParty();
        Player player = mcMMOPlayer.getPlayer();
        Player winningPlayer = null;

        ItemStack newStack = itemStack.clone();
        newStack.setAmount(1);

        if (ItemUtils.isMobDrop(itemStack) && !party.sharingLootDrops()) {
            return false;
        }
        else if (ItemUtils.isMiningDrop(itemStack) && !party.sharingMiningDrops()) {
            return false;
        }
        else if (ItemUtils.isHerbalismDrop(itemStack) && !party.sharingHerbalismDrops()) {
            return false;
        }
        else if (ItemUtils.isWoodcuttingDrop(itemStack) && !party.sharingWoodcuttingDrops()) {
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

                event.setCancelled(true);
                item.remove();
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

                event.setCancelled(true);
                item.remove();

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
                // Fallthrough
            default:
                return false;
        }
    }
}
