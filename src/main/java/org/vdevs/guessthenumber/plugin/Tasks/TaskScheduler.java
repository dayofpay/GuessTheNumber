package org.vdevs.guessthenumber.plugin.Tasks;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.vdevs.guessthenumber.plugin.Main;

import java.util.concurrent.ThreadLocalRandom;

public class TaskScheduler extends BukkitRunnable {

    private final Main plugin;

    public TaskScheduler(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        // Check if a game is already running
        if (plugin.getTargetNumber() != -1) {
            plugin.getLogger().warning("[GuessTheNumber] Auto game scheduler was called but a game is already running!");
            return;
        }

        int min = plugin.getConfig().getInt("game.min");
        int max = plugin.getConfig().getInt("game.max");

        if (min >= max) {
            plugin.getLogger().severe("[GuessTheNumber] Invalid min and max range in config. Game not started.");
            return;
        }

        int randomNumber = ThreadLocalRandom.current().nextInt(min, max + 1);

        plugin.getLogger().info("[GuessTheNumber] Starting a new game with random number between " + min + " and " + max);
        plugin.getLogger().info("[GuessTheNumber] Selected number is " + randomNumber);

        plugin.setTargetNumber(randomNumber);
        plugin.setGameAuthor("CONSOLE");

        String message = plugin.getConfig().getString("messages.start", "")
                .replace("%min%", String.valueOf(min))
                .replace("%max%", String.valueOf(max))
                .replace("%player%", "CONSOLE")
                .replace("&", "§");

        Bukkit.broadcastMessage(message);
    }

    public void start() {
        plugin.getLogger().info("[GuessTheNumber] Auto game scheduler is starting...");

        if (!plugin.getConfig().getBoolean("game.enable_auto_game_scheduler")) {
            plugin.getLogger().warning("[GuessTheNumber] Auto game scheduler is disabled in config.");
            return;
        }

        int delayInSeconds = plugin.getConfig().getInt("game.game_scheduler_cooldown", 3600);
        long delayInTicks = delayInSeconds * 20L;

        this.runTaskTimerAsynchronously((JavaPlugin) plugin, delayInTicks, delayInTicks);

        plugin.getLogger().info("[GuessTheNumber] Auto game scheduler started successfully!");
    }
}
