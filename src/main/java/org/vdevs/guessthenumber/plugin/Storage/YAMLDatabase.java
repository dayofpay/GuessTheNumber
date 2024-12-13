package org.vdevs.guessthenumber.plugin.Storage;

import org.bukkit.configuration.file.FileConfiguration;
import org.vdevs.guessthenumber.plugin.Main;

public class YAMLDatabase {
    private final Main plugin;
    private final FileConfiguration config;

    public YAMLDatabase(Main plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
    }

    // Increment the player's win count
    public void addWin(String playerName) {
        int currentWins = config.getInt("wins." + playerName, 0);
        config.set("wins." + playerName, currentWins + 1);
        plugin.saveConfig();
    }

    // Get the win count for a player
    public int getWins(String playerName) {
        return config.getInt("wins." + playerName, 0);
    }
}
