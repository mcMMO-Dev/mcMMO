package com.gmail.nossr50.party;

import java.util.List;

import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.McMMOPlayer;
import com.gmail.nossr50.skills.utilities.SkillType;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Users;

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
            List<Player> nearMembers = PartyManager.getNearMembers(player, party, Config.getInstance().getPartyShareRange());

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
                Users.getPlayer(member).beginUnsharedXpGain(skillType, roundedXp);
            }

            mcMMOPlayer.beginUnsharedXpGain(skillType, roundedXp);

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
    public static boolean handleItemShare(PlayerPickupItemEvent event, McMMOPlayer mcMMOPlayer) {
        Item item = event.getItem();
        ItemStack itemStack = item.getItemStack();
        Party party = mcMMOPlayer.getParty();

        switch (party.getItemShareMode()) {
        case EQUAL:

            return false;
        case RANDOM:
            Player player = mcMMOPlayer.getPlayer();
            List<Player> nearMembers = PartyManager.getNearMembers(player, party, Config.getInstance().getPartyShareRange());

            if (nearMembers.isEmpty()) {
                return false;
            }
            int partySize = nearMembers.size() + 1;

            event.setCancelled(true);
            item.remove();

            Player targetPlayer;

            ItemStack newStack = itemStack.clone();
            newStack.setAmount(1);

            //TODO Improve this, if possible make this faster.
            for (int i = 0; i < itemStack.getAmount(); i++) {
                int randomMember = Misc.getRandom().nextInt(partySize);
                if (randomMember >= nearMembers.size()) {
                    targetPlayer = player;
                } else {
                    targetPlayer = nearMembers.get(randomMember);
                }
                targetPlayer.getInventory().addItem(newStack);
                targetPlayer.updateInventory();
            }
            return true;
        case NONE:
        default:
            return false;
        }
    }
}

