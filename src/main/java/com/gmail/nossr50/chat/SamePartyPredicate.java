package com.gmail.nossr50.chat;

import com.gmail.nossr50.datatypes.party.Party;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.util.player.UserManager;
import java.util.function.Predicate;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class SamePartyPredicate<T extends CommandSender> implements Predicate<T> {

    final Party party;

    public SamePartyPredicate(Party party) {
        this.party = party;
    }

    @Override
    public boolean test(T t) {
        //Include the console in the audience
        if (t instanceof ConsoleCommandSender) {
            //Party audiences are special, we exclude console from them to avoid double
            // messaging since we send a more verbose version to consoles
            return false;
        } else {
            if (t instanceof Player player) {
                final McMMOPlayer mmoPlayer = UserManager.getPlayer(player);
                if (mmoPlayer != null) {
                    return mmoPlayer.getParty() == party;
                }
            }
        }
        return false;
    }
}
