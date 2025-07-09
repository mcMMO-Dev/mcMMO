package com.gmail.nossr50.datatypes;

import com.gmail.nossr50.datatypes.party.Party;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.player.UserManager;
import java.util.function.Predicate;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

//TODO: Allow for offline players to broadcast
public class PowerLevelUpBroadcastPredicate<T extends CommandSender> implements Predicate<T> {

    private final @NotNull T broadcaster;

    public PowerLevelUpBroadcastPredicate(@NotNull T broadcaster) {
        this.broadcaster = broadcaster;
    }

    @Override
    public boolean test(@NotNull T t) {
        Player broadcastingPlayer = (Player) broadcaster; //Always a player no need to check cast

        //Broadcaster should be online
        if (!broadcastingPlayer.isOnline()) {
            return false;
        }

        McMMOPlayer mmoBroadcastingPlayer = UserManager.getPlayer(broadcastingPlayer);

        if (mmoBroadcastingPlayer == null) {
            //This should never be null, but just in case...
            mcMMO.p.getLogger()
                    .severe("McMMOPlayer was null for broadcaster in LevelUpBroadcastPredicate when it should never be null!");
            return false;
        }

        if (t instanceof Player listeningPlayer) {

            //Party Member Check
            if (mcMMO.p.getGeneralConfig().isPowerLevelUpBroadcastsPartyMembersOnly()) {
                McMMOPlayer mmoListeningPlayer = UserManager.getPlayer(listeningPlayer);

                if (mmoListeningPlayer == null) {
                    return false; //No profile so therefor no party
                }

                Party playerWhoLeveledParty = mmoBroadcastingPlayer.getParty();
                Party broadcastRecipientParty = mmoListeningPlayer.getParty();

                if (playerWhoLeveledParty == null || broadcastRecipientParty == null) {
                    return false; //No party on either player when being in the same party is required
                }

                if (!playerWhoLeveledParty.equals(broadcastRecipientParty)) {
                    return false; //Not in the same party when it is required
                }
            }

            //Same world check
            if (isPowerLevelUpBroadcastsSameWorldOnly()) {
                if (!mmoBroadcastingPlayer.getPlayer().getWorld()
                        .equals(listeningPlayer.getWorld())) {
                    return false; //Not in the same world when its required
                }

                //Distance checks
                if (mcMMO.p.getGeneralConfig().shouldPowerLevelUpBroadcastsRestrictDistance()) {
                    if (!Misc.isNear(mmoBroadcastingPlayer.getPlayer().getLocation(),
                            listeningPlayer.getLocation(),
                            mcMMO.p.getGeneralConfig().getPowerLevelUpBroadcastRadius())) {
                        return false;
                    }
                }
            }

            //Visibility checks
            return listeningPlayer.canSee(mmoBroadcastingPlayer.getPlayer())
                    || listeningPlayer
                    == mmoBroadcastingPlayer.getPlayer(); //Player who leveled should be invisible to this player so don't send the message
        } else {
            //Send out to console
            return mcMMO.p.getGeneralConfig().shouldPowerLevelUpBroadcastToConsole();
        }
    }

    private static boolean isPowerLevelUpBroadcastsSameWorldOnly() {
        return mcMMO.p.getGeneralConfig().isPowerLevelUpBroadcastsSameWorldOnly();
    }

    @Override
    public String toString() {
        return "PowerLevelUpBroadcastPredicate{" +
                "broadcaster=" + broadcaster +
                '}';
    }
}
