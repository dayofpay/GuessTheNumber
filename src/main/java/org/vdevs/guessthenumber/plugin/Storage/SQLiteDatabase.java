package org.vdevs.guessthenumber.plugin.Storage;

import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLiteDatabase {
    private final String dbUrl = "jdbc:sqlite:plugins/GuessTheNumber/wins.db"; // Path to your database
    private Connection connection;

    // Connect to the database
    public void connect() throws SQLException {
        try {
            // Ensure the SQLite JDBC driver is loaded
            Class.forName("org.sqlite.JDBC");

            // Establish the connection
            this.connection = DriverManager.getConnection(dbUrl);
            Bukkit.getConsoleSender().sendMessage("SQLite database connection established.");

            // Create the 'wins' table if it doesn't exist
            createTableIfNotExists();
        } catch (ClassNotFoundException e) {
            Bukkit.getConsoleSender().sendMessage("SQLite JDBC Driver not found!");
            throw new SQLException("SQLite JDBC Driver not found!", e);
        }
    }

    // Create the 'wins' table if it does not exist
    private void createTableIfNotExists() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS wins (" +
                "player_name TEXT PRIMARY KEY, " +
                "win_count INTEGER)";
        try (PreparedStatement stmt = connection.prepareStatement(createTableSQL)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            Bukkit.getConsoleSender().sendMessage("Error creating 'wins' table: " + e.getMessage());
        }
    }

    // Increment the player's win count
    public void addWin(String playerName) {
        try {
            // Check if player already has a win count in the database
            String checkQuery = "SELECT * FROM wins WHERE player_name = ?";
            try (PreparedStatement stmt = connection.prepareStatement(checkQuery)) {
                stmt.setString(1, playerName);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    // Player exists, increment their win count
                    int currentWins = rs.getInt("win_count");
                    String updateQuery = "UPDATE wins SET win_count = ? WHERE player_name = ?";
                    try (PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {
                        updateStmt.setInt(1, currentWins + 1);
                        updateStmt.setString(2, playerName);
                        updateStmt.executeUpdate();
                    }
                } else {
                    // Player does not exist, insert a new record
                    String insertQuery = "INSERT INTO wins (player_name, win_count) VALUES (?, 1)";
                    try (PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) {
                        insertStmt.setString(1, playerName);
                        insertStmt.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            Bukkit.getConsoleSender().sendMessage("Error adding win for player " + playerName + ": " + e.getMessage());
        }
    }

    // Get the win count for a player
    public int getWins(String playerName) {
        try {
            String query = "SELECT win_count FROM wins WHERE player_name = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, playerName);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return rs.getInt("win_count");
                }
            }
        } catch (SQLException e) {
            Bukkit.getConsoleSender().sendMessage("Error getting win count for player " + playerName + ": " + e.getMessage());
        }
        return 0;  // Return 0 if no wins are found
    }

    // Close the connection
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                Bukkit.getConsoleSender().sendMessage("SQLite database connection closed.");
            }
        } catch (SQLException e) {
            Bukkit.getConsoleSender().sendMessage("Error closing SQLite connection: " + e.getMessage());
        }
    }
}
