package org.vdevs.guessthenumber.plugin.Utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.vdevs.guessthenumber.plugin.Main;


public class MainThreadCommand {
    private final Main plugin;

    public MainThreadCommand(Main plugin) {
        this.plugin = plugin;
    }

    public void executeCommand(Player player, String command) {

        // Execute the command after updating win count
        Bukkit.getScheduler().runTask(plugin, () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command));
    }
}
