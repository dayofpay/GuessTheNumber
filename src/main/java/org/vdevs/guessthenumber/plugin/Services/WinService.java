package org.vdevs.guessthenumber.plugin.Services;

import org.bukkit.entity.Player;
import org.vdevs.guessthenumber.plugin.Main;
import org.vdevs.guessthenumber.plugin.Storage.MySQLDatabase;
import org.vdevs.guessthenumber.plugin.Storage.SQLiteDatabase;
import org.vdevs.guessthenumber.plugin.Storage.YAMLDatabase;
import org.vdevs.guessthenumber.plugin.Storage.GetStorageType;

public class WinService {
    private final Main plugin;

    public WinService(Main plugin) {
        this.plugin = plugin;
    }

    // Increment the player's win count and return the updated count
    public int handleWin(Player player) {
        String storageType = new GetStorageType(plugin).getStorageType();
        int wins = 0;

        if (storageType.equals("sqlite")) {
            SQLiteDatabase sqliteDatabase = plugin.getSQLiteDatabase();
            sqliteDatabase.addWin(player.getName());
            wins = sqliteDatabase.getWins(player.getName());
        } else if (storageType.equals("yaml")) {
            YAMLDatabase yamlDatabase = plugin.getYAMLDatabase();
            yamlDatabase.addWin(player.getName());
            wins = yamlDatabase.getWins(player.getName());
        }
        else if(storageType.equals("mysql")){
            MySQLDatabase mySQLDatabase = plugin.getMySQLDatabase();
            mySQLDatabase.addWin(player.getName());
            wins = mySQLDatabase.getWins(player.getName());
        }
        else if(storageType.equals("none")){

        }
//for next update        player.sendMessage("You have won " + wins + " times (" + storageType.toUpperCase() + ")!");

        return wins;
    }
}
