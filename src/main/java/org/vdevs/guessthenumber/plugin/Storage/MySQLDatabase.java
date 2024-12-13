package org.vdevs.guessthenumber.plugin.Storage;

import java.sql.*;

public class MySQLDatabase {
    private String dbUrl;
    private String dbUser;
    private String dbPassword;
    private String dbDatabase;
    private Connection connection;

    // Constructor to initialize MySQL settings
    public MySQLDatabase(String host, String port, String user, String password, String database) {
        this.dbUrl = "jdbc:mysql://" + host + ":" + port + "/" + database;
        this.dbUser = user;
        this.dbPassword = password;
        this.dbDatabase = database; // Ensure dbDatabase is initialized
    }

    // Connect to the MySQL database
    public void connect() throws SQLException {
        try {
            // Ensure the MySQL JDBC driver is loaded
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establish the connection
            this.connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
            System.out.println("MySQL database connection established.");
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL JDBC Driver not found!");
            throw new SQLException("MySQL JDBC Driver not found!", e);
        }
    }

    // Create the 'wins' table if it does not exist
    public void createTableIfNotExists() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS wins (" +
                "player_name VARCHAR(255) PRIMARY KEY, " +
                "win_count INT)";
        try (PreparedStatement stmt = connection.prepareStatement(createTableSQL)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
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
            e.printStackTrace();
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
            e.printStackTrace();
        }
        return 0;  // Return 0 if no wins are found
    }

    // Close the connection
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
