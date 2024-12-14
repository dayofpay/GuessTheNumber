package org.vdevs.guessthenumber.plugin.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.vdevs.guessthenumber.plugin.Main;

public class ReloadGame implements CommandExecutor {

    private final Main plugin;

    public ReloadGame(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (command.getName().equalsIgnoreCase("reload-game")) {

            // Check if the sender has permission to reload
            if (!sender.hasPermission("guessthenumber.reload")) {
                sender.sendMessage(plugin.getConfig().getString("messages.no_permission").replace("&", "§"));
                return true;
            }

            try {
                // Reload the configuration
                plugin.reloadConfig();


                if (plugin.getDataFolder().exists()) {
                    plugin.getConfig().load(plugin.getDataFolder() + "/config.yml");
                }
                String RELOAD_MESSAGE = plugin.getConfig().getString("messages.reload_message").replace("&","§");
                sender.sendMessage(RELOAD_MESSAGE.replace("&","§").
                        replace("%player%",sender.getName()
                ).replace("%version%",plugin.getDescription().getVersion()));
            } catch (Exception e) {
                String ERROR_MESSAGE = plugin.getConfig().getString("messages.plugin_error").replace("&","§");
                sender.sendMessage(ERROR_MESSAGE);
                e.printStackTrace();
            }
        }

        return true;
    }
}
