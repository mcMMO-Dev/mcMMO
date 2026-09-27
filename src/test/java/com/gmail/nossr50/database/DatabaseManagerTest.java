package com.gmail.nossr50.database;

import static com.gmail.nossr50.database.UsernamePlaceholder.INVALID_OLD_USERNAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import com.gmail.nossr50.datatypes.database.PlayerNameAndUUID;
import java.util.List;
import org.junit.jupiter.api.Test;

/** What a database manager from another plugin gets for the methods it does not override. */
class DatabaseManagerTest {
    @Test
    void storedUsersWithUUIDsShouldDefaultToTheStoredNamesWithoutUuids() {
        // Given - a database manager that only lists the names it stores
        final DatabaseManager databaseManager = mock(DatabaseManager.class, CALLS_REAL_METHODS);
        doReturn(List.of("nossr50", INVALID_OLD_USERNAME)).when(databaseManager)
                .getStoredUsers();

        // When - the stored users are listed with their UUIDs
        final List<PlayerNameAndUUID> storedUsers = databaseManager.getStoredUsersWithUUIDs();

        // Then - each name is listed once, without a UUID
        assertThat(storedUsers).containsExactly(new PlayerNameAndUUID("nossr50", null),
                new PlayerNameAndUUID(INVALID_OLD_USERNAME, null));
    }
}
