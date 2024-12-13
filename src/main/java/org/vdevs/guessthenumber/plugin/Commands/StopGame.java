package org.vdevs.guessthenumber.plugin.Commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.vdevs.guessthenumber.plugin.Main;

public class StopGame implements CommandExecutor {

    private final Main plugin;

    public StopGame(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {


        if(command.getName().equals("stop-game")){
            try{
                // Check if the game is actually running
                if(!sender.hasPermission("guessnumber.cancel")){
                    sender.sendMessage(plugin.getConfig().getString("messages.no_permission").replace("&", "§"));
                    return true;
                }
                boolean isRunning = plugin.getTargetNumber() != -1;
                if(!isRunning){
                    String NOT_RUNNING_MESSAGE = plugin.getConfig().getString("messages.game_not_running").replace("&","§");

                    sender.sendMessage(NOT_RUNNING_MESSAGE);

                    return true;
                }
                boolean sendGlobally = plugin.getConfig().getBoolean("game.displayCancel");
                String AUTHOR_STOP_MESSAGE = plugin.getConfig().getString("messages.author_stop_message").replace("&","§").replace("%player%",sender.getName());;
                String GLOBAL_STOP_MESSAGE = plugin.getConfig().getString("messages.global_stop_message").replace("&","§").replace("%player%",sender.getName());
                sender.sendMessage(AUTHOR_STOP_MESSAGE);
                if(sendGlobally) {Bukkit.broadcastMessage(GLOBAL_STOP_MESSAGE);}
                plugin.setTargetNumber(-1);
            }catch(Exception error){
                String ERROR_MESSAGE = plugin.getConfig().getString("messages.plugin_error").replace("&","§");

                sender.sendMessage(ERROR_MESSAGE);
            }
        }

        return true;
    }
}
