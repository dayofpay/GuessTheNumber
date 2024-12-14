package org.vdevs.guessthenumber.plugin.Commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.vdevs.guessthenumber.plugin.Main;

public class StartGame implements CommandExecutor {

    private final Main plugin;

    public StartGame(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(command.getName().equals("start-game")){

            if (!sender.hasPermission("guessthenumber.start")) {
                sender.sendMessage(plugin.getConfig().getString("messages.no_permission").replace("&", "§"));
                return true;
            }

            if (args.length != 1) {
                sender.sendMessage(plugin.getConfig().getString("messages.correct_usage").replace("&","§"));
                return true;
            }
            // Check if game is already running
            boolean CHECK_FOR_ACTIVE_INSTANCE = plugin.getConfig().getBoolean("game.checkForActiveGame");
            boolean FORCE_CANCEL_INSTANCE = plugin.getConfig().getBoolean("game.cancelIfGameIsRunning");

            int min = plugin.getConfig().getInt("game.min");
            int max = plugin.getConfig().getInt("game.max");
            if(CHECK_FOR_ACTIVE_INSTANCE && plugin.getTargetNumber() != -1){
                if(FORCE_CANCEL_INSTANCE){
                    String FORCE_CANCEL_INSTANCE_MESSAGE = plugin.getConfig().getString("messages.game_already_running").replace("&","§");

                    sender.sendMessage(FORCE_CANCEL_INSTANCE_MESSAGE);

                    return true;
                }
            }
            try {
                int number = Integer.parseInt(args[0]);

                if (number < min || number > max) {
                    sender.sendMessage(plugin.getConfig().getString("messages.invalid_number").replace("&", "§").replace("%min%", String.valueOf(min)).replace("%max%", String.valueOf(max)));
                    return true;
                }

                plugin.setTargetNumber(number);
                Bukkit.broadcastMessage(plugin.getConfig().getString("messages.start")
                        .replace("%min%", String.valueOf(min))
                        .replace("%max%", String.valueOf(max))
                        .replace("%player%",sender.getName())
                        .replace("&", "§"));
            } catch (NumberFormatException e) {
                sender.sendMessage(plugin.getConfig().getString("messages.invalid_number").replace("&", "§").replace("%min%", String.valueOf(min)).replace("%max%", String.valueOf(max)));
            }
        }

        return true;
    }
}
