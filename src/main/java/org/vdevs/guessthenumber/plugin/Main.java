package org.vdevs.guessthenumber.plugin;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.vdevs.guessthenumber.plugin.Commands.StartGame;
import org.vdevs.guessthenumber.plugin.Commands.StopGame;
import org.vdevs.guessthenumber.plugin.Events.ChatListener;
import org.vdevs.guessthenumber.plugin.Storage.GetStorageType;
import org.vdevs.guessthenumber.plugin.Storage.MySQLDatabase;
import org.vdevs.guessthenumber.plugin.Storage.SQLiteDatabase;
import org.vdevs.guessthenumber.plugin.Storage.YAMLDatabase;
public class Main extends JavaPlugin {
    private SQLiteDatabase sqliteDatabase;
    private YAMLDatabase yamlDatabase;

    private MySQLDatabase mySQLDatabase;
    private int targetNumber = -1;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        // Initialize the GetStorageType class to get storage type from config
        GetStorageType getStorageType = new GetStorageType(this);
        String storageType = getStorageType.getStorageType();

        // Set the appropriate storage based on the config value
        if (storageType.equals("sqlite")) {
            sqliteDatabase = new SQLiteDatabase();
            try {
                sqliteDatabase.connect();
                getLogger().info("Using SQLite as a storage for GuessTheNumber !");
            } catch (Exception e) {
                getLogger().warning("Failed to connect to SQLite database.");
                e.printStackTrace();
            }
        }
        else if(storageType.equals("mysql")){


                // Load MySQL configuration from the config.yml
                String mysqlHost = getConfig().getString("database_settings.mysql_host");
                String mysqlPort = getConfig().getString("database_settings.mysql_port");
                String mysqlUser = getConfig().getString("database_settings.mysql_user");
                String mysqlPassword = getConfig().getString("database_settings.mysql_password");
                String mysqlDatabase = getConfig().getString("database_settings.mysql_database");
                this.mySQLDatabase = new MySQLDatabase(mysqlHost, mysqlPort, mysqlUser, mysqlPassword, mysqlDatabase);
                try {
                    mySQLDatabase.connect();
                    getLogger().info("Using MySQL as a storage for GuessTheNumber !");
                    mySQLDatabase.createTableIfNotExists();
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
        else if(storageType.equals("none")){
            getLogger().info("GuessTheNumber Wont use any storage for its data");
        }
        else {
            yamlDatabase = new YAMLDatabase(this);
            getLogger().info("Using YAML as a storage for GuessTheNumber !");
        }
        getCommand("start-game").setExecutor(new StartGame(this));
        getCommand("stop-game").setExecutor(new StopGame(this));
        Bukkit.getPluginManager().registerEvents(new ChatListener(this), this);
        getLogger().info("GuessTheNumber plugin enabled.");
    }

    @Override
    public void onDisable() {
        getLogger().info("GuessTheNumber plugin disabled.");
        // Close SQLite connection when disabling
        if (sqliteDatabase != null) {
            sqliteDatabase.close();
        }
        // Close MYSql connection when disabling
        else if(mySQLDatabase != null){
            mySQLDatabase.close();
        }
    }

    public SQLiteDatabase getSQLiteDatabase() {
        return sqliteDatabase;
    }

    public YAMLDatabase getYAMLDatabase() {
        return yamlDatabase;
    }

    public MySQLDatabase getMySQLDatabase() {
        return mySQLDatabase;
    }
    public int getTargetNumber() {
        return targetNumber;
    }

    public void setTargetNumber(int number) {
        this.targetNumber = number;
    }
}
