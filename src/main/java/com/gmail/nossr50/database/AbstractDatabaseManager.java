package com.gmail.nossr50.database;

import com.gmail.nossr50.api.exceptions.ProfileRetrievalException;
import com.gmail.nossr50.datatypes.player.PersistentPlayerDataBuilder;
import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.mcMMO;
import org.apache.commons.lang.NullArgumentException;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractDatabaseManager implements DatabaseManager {
    @Override
    public @Nullable PlayerProfile initPlayerProfile(@NotNull Player player) throws Exception {
        //First we attempt to load the player data
        try {
            PlayerProfile playerProfile = queryPlayerDataByUUID(player.getUniqueId(), player.getName());
            if(playerProfile != null) {
                return playerProfile;
            }
            //If we fail to load the player data due to either missing data for the player or corrupted/invalid data, we create a new profile for this player
        } catch (ProfileRetrievalException | NullArgumentException e) {
            mcMMO.p.getLogger().info("Making new player data in DB for user name:"+player.getName().toString()+", uuid:" + player.getUniqueId().toString());
            //Add data for this player into DB with default values
            //TODO: have this use the PersistentPlayerData object created below to initialize defaults
            insertNewUser(player.getName(), player.getUniqueId());
            //Construct player data object
            PersistentPlayerDataBuilder persistentPlayerDataBuilder = new PersistentPlayerDataBuilder();
            //Return player profile
            return new PlayerProfile(persistentPlayerDataBuilder.buildNewPlayerData(player.getUniqueId(), player.getName()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null; //Some critical failure happened
    }
}
