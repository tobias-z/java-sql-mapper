package com.tobias_z.api.database;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import com.tobias_z.DBConfig;
import com.tobias_z.Database;
import com.tobias_z.SQLQuery;
import com.tobias_z.utils.BeforeEachSetup;
import com.tobias_z.api.connection.DBConfigArgumentProvider;
import com.tobias_z.utils.SetupIntegrationTests;
import com.tobias_z.entities.NoIncrement;
import com.tobias_z.entities.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

public class UpdateTest extends SetupIntegrationTests {

    private static Database DB;

    SQLQuery insertUserQuery;
    SQLQuery insertNoIncrementQuery;
    String username = "Bob";
    String message = "Hello Bob";

    SQLQuery updateUserQuery;
    SQLQuery updateNoIncrementQuery;

    String newName = "Updated Bob";
    String newMessage = "This is an updated message";

    User user;

    private final BeforeEachSetup beforeEach = (database) -> {
        insertUserQuery = new SQLQuery("INSERT INTO users (name) VALUES (:name)")
            .addParameter("name", username);
        insertNoIncrementQuery = new SQLQuery("INSERT INTO no_increment (message) VALUES (:message)")
            .addParameter("message", message);
        user = database.insert(insertUserQuery, User.class);
        database.insert(insertUserQuery);
        database.insert(insertUserQuery);
        database.insert(insertNoIncrementQuery);
        updateUserQuery = new SQLQuery("UPDATE users SET name = :name WHERE id = :id")
            .addParameter("name", newName)
            .addParameter("id", user.getId());
        updateNoIncrementQuery = new SQLQuery(
            "UPDATE no_increment SET message = :newMessage WHERE message = :message")
            .addParameter("newMessage", newMessage)
            .addParameter("message", message);
    };

    @ParameterizedTest(name = "{1}")
    @ArgumentsSource(DBConfigArgumentProvider.class)
    @DisplayName("should update a users name to a different value")
    void shouldUpdateAUsersNameToADifferentValue(DBConfig dbConfig, String dbName, String migrateFile)
        throws Exception {
        DB = setupTest(dbConfig, beforeEach, migrateFile);
        User updatedUser = DB.update(updateUserQuery, User.class);
        assertEquals(user.getId(), updatedUser.getId());
        assertEquals(newName, updatedUser.getName());
    }

    @ParameterizedTest(name = "{1}")
    @ArgumentsSource(DBConfigArgumentProvider.class)
    @DisplayName("should not throw exception when updating user")
    void shouldNotThrowExceptionWhenUpdatingUser(DBConfig dbConfig, String dbName, String migrateFile)
        throws Exception {
        DB = setupTest(dbConfig, beforeEach, migrateFile);
        assertDoesNotThrow(() -> DB.update(updateUserQuery));
    }

    @ParameterizedTest(name = "{1}")
    @ArgumentsSource(DBConfigArgumentProvider.class)
    @DisplayName("should not throw exception when updating no increment")
    void shouldNotThrowExceptionWhenUpdatingNoIncrement(DBConfig dbConfig, String dbName,
        String migrateFile)
        throws Exception {
        DB = setupTest(dbConfig, beforeEach, migrateFile);
        assertDoesNotThrow(() -> DB.update(updateNoIncrementQuery));
    }

    @ParameterizedTest(name = "{1}")
    @ArgumentsSource(DBConfigArgumentProvider.class)
    @DisplayName("should be able to update primary key of string")
    void shouldBeAbleToUpdatePrimaryKeyOfString(DBConfig dbConfig, String dbName, String migrateFile)
        throws Exception {
        DB = setupTest(dbConfig, beforeEach, migrateFile);
        NoIncrement updatedNoIncrement = DB.update(updateNoIncrementQuery, NoIncrement.class);
        assertEquals(newMessage, updatedNoIncrement.getMessage());
        assertNotEquals(message, updatedNoIncrement.getMessage());
    }

}
