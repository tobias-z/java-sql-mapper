package com.github.tobias_z.api.database;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.github.tobias_z.DBConfig;
import com.github.tobias_z.Database;
import com.github.tobias_z.SQLQuery;
import com.github.tobias_z.entities.Role;
import com.github.tobias_z.utils.BeforeEachSetup;
import com.github.tobias_z.api.connection.DBConfigArgumentProvider;
import com.github.tobias_z.utils.SetupIntegrationTests;
import com.github.tobias_z.entities.NoIncrement;
import com.github.tobias_z.entities.User;
import com.github.tobias_z.exceptions.DatabaseException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

public class GetTest extends SetupIntegrationTests {

    SQLQuery insertUserQuery;
    SQLQuery insertNoIncrementQuery;
    String username = "Bob";
    String message = "Hello Bob";

    private static Database DB;

    User user;

    private final BeforeEachSetup beforeEach = (database) -> {
        insertUserQuery = new SQLQuery("INSERT INTO users (name, active, role) VALUES (:name, :active, :role)")
            .addParameter("name", username)
            .addParameter("active", false)
            .addParameter("role", Role.ADMIN);
        insertNoIncrementQuery = new SQLQuery("INSERT INTO no_increment (message, role) VALUES (:message, :role)")
            .addParameter("message", message)
            .addParameter("role", Role.EMPLOYEE);
        user = database.insert(insertUserQuery, User.class);
        database.insert(insertNoIncrementQuery);
    };

    @ParameterizedTest(name = "{1}")
    @ArgumentsSource(DBConfigArgumentProvider.class)
    @DisplayName("should return a user")
    void shouldReturnAUser(DBConfig dbConfig, String dbName, String migrateFile) throws Exception {
        DB = setupTest(dbConfig, beforeEach, migrateFile);
        User foundUser = DB.get(user.getId(), User.class);
        assertEquals(user.getId(), foundUser.getId());
        assertEquals(user.getName(), foundUser.getName());
    }

    @ParameterizedTest(name = "{1}")
    @ArgumentsSource(DBConfigArgumentProvider.class)
    @DisplayName("should throw exception if incorrect primary key")
    void shouldThrowExceptionIfIncorrectPrimaryKey(DBConfig dbConfig, String dbName, String migrateFile)
        throws Exception {
        DB = setupTest(dbConfig, beforeEach, migrateFile);
        assertThrows(DatabaseException.class, () -> DB.get(10, User.class));
    }

    @ParameterizedTest(name = "{1}")
    @ArgumentsSource(DBConfigArgumentProvider.class)
    @DisplayName("should return a NoIncrement")
    void shouldReturnANoIncrement(DBConfig dbConfig, String dbName, String migrateFile) throws Exception {
        DB = setupTest(dbConfig, beforeEach, migrateFile);
        NoIncrement noIncrement = DB.get(message, NoIncrement.class);
        assertEquals(message, noIncrement.getMessage());
    }

}
