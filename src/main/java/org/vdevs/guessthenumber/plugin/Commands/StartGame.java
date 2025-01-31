package org.vdevs.guessthenumber.plugin.Commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.vdevs.guessthenumber.plugin.Main;

import java.util.HashMap;
import java.util.UUID;

public class StartGame implements CommandExecutor {

    private final Main plugin;
    private final HashMap<UUID, Long> cooldowns = new HashMap<>();


    public StartGame(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (command.getName().equalsIgnoreCase("start-game")) {
            final int COOLDOWN_TIME = plugin.getConfig().getInt("game.cooldownTime") * 1000; // Cooldown in milliseconds

            if (!(sender instanceof Player)) {
                sender.sendMessage("[GuessTheNumber] Only players can run this command!");
                return true;
            }

            Player player = (Player) sender;
            UUID playerUUID = player.getUniqueId();
            boolean enabledCoolDownSystem = plugin.getConfig().getBoolean("game.useCooldown");
            // Cooldown check
            if (enabledCoolDownSystem && !sender.hasPermission("guessthenumber.bypass_cooldown") && cooldowns.containsKey(playerUUID)) {
                long timePassed = System.currentTimeMillis() - cooldowns.get(playerUUID);
                if (timePassed < COOLDOWN_TIME) {
                    long timeLeft = (COOLDOWN_TIME - timePassed) / 1000;
                    String COOLDOWN_MESSAGE = plugin.getConfig().getString("messages.cooldown_message").replace("&", "§").replace("%time%", String.valueOf(timeLeft).replace("%player", player.getName()));
                    player.sendMessage(COOLDOWN_MESSAGE);
                    return true;
                }
            }

            if (!player.hasPermission("guessthenumber.start")) {
                player.sendMessage(plugin.getConfig().getString("messages.no_permission").replace("&", "§"));
                return true;
            }

            if (args.length != 1) {
                player.sendMessage(plugin.getConfig().getString("messages.correct_usage").replace("&", "§"));
                return true;
            }

            // Check if the game is already running
            boolean CHECK_FOR_ACTIVE_INSTANCE = plugin.getConfig().getBoolean("game.checkForActiveGame");
            boolean FORCE_CANCEL_INSTANCE = plugin.getConfig().getBoolean("game.cancelIfGameIsRunning");

            int min = plugin.getConfig().getInt("game.min");
            int max = plugin.getConfig().getInt("game.max");

            if (CHECK_FOR_ACTIVE_INSTANCE && plugin.getTargetNumber() != -1) {
                if (FORCE_CANCEL_INSTANCE) {
                    String FORCE_CANCEL_INSTANCE_MESSAGE = plugin.getConfig().getString("messages.game_already_running").replace("&", "§");
                    player.sendMessage(FORCE_CANCEL_INSTANCE_MESSAGE);
                    return true;
                }
            }

            try {
                int number = Integer.parseInt(args[0]);

                if (number < min || number > max) {
                    player.sendMessage(plugin.getConfig().getString("messages.invalid_number").replace("&", "§")
                            .replace("%min%", String.valueOf(min))
                            .replace("%max%", String.valueOf(max)));
                    return true;
                }

                // Set cooldown
                cooldowns.put(playerUUID, System.currentTimeMillis());

                plugin.setTargetNumber(number);
                plugin.setGameAuthor(player.getName());
                Bukkit.broadcastMessage(plugin.getConfig().getString("messages.start")
                        .replace("%min%", String.valueOf(min))
                        .replace("%max%", String.valueOf(max))
                        .replace("%player%", player.getName())
                        .replace("&", "§"));

            } catch (NumberFormatException e) {
                player.sendMessage(plugin.getConfig().getString("messages.invalid_number").replace("&", "§")
                        .replace("%min%", String.valueOf(min))
                        .replace("%max%", String.valueOf(max)));
            }
        }

        return true;
    }
}
