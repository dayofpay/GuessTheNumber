package org.vdevs.guessthenumber.plugin.Events;

import com.connorlinfoot.titleapi.TitleAPI;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.vdevs.guessthenumber.plugin.Main;
import org.vdevs.guessthenumber.plugin.Services.WinService;
import org.vdevs.guessthenumber.plugin.Utils.MainThreadCommand;

import java.util.List;

public class ChatListener implements Listener {

    private final Main plugin;

    public ChatListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        if (plugin.getTargetNumber() == -1) return;  // Check if the game has started
        boolean isNumber = true;
        Player player = event.getPlayer();
        String message = event.getMessage();

        try{
            Number num = Integer.parseInt(message);

        }catch(NumberFormatException exception){
            isNumber = false;
        }
        // Check if the plugin requires permission to participate
        if (plugin.getConfig().getBoolean("permissions.require_permission_to_participate") && !player.hasPermission("guessthenumber.guess") && isNumber) {
            // If the player doesn't have permission, cancel the event and send a message
            event.setCancelled(true);  // Optionally, cancel the event to prevent the message from appearing in chat
            String noPermissionMessage = plugin.getConfig().getString("messages.no_permission_to_participate", "&cYou do not have permission to participate in the game.")
                    .replace("&", "§");
            player.sendMessage(noPermissionMessage);
            return;
        }

       if(isNumber){
           try {
               // Check if the message is a valid number
               int guess = Integer.parseInt(message);

               if (guess == plugin.getTargetNumber()) {
                   boolean preventAuthorToJoin = plugin.getConfig().getBoolean("game.preventAuthorParticipation");
                   if(preventAuthorToJoin && player.getName().equals(plugin.getTargetAuthor())){
                       String preventAuthorToJoinMessage = plugin.getConfig().getString("messages.author_participation_message", "&cYou cannot join the game because you are the author.").replace("&", "§");
                       player.sendMessage(preventAuthorToJoinMessage);
                       return;
                   }
                   // Announce the winner
                   String winMessage = plugin.getConfig().getString("messages.win", "&aYou won!")
                           .replace("%player%", player.getName())
                           .replace("%number%", String.valueOf(plugin.getTargetNumber()))
                           .replace("&", "§");
                   Bukkit.broadcastMessage(winMessage);
                   // Execute reward commands
                   List<String> rewards = plugin.getConfig().getStringList("rewards");
                   for (String command : rewards) {
                       String formattedCommand = command.replace("%player%", player.getName());
                       MainThreadCommand mainThreadCommand = new MainThreadCommand(plugin);
                       // Ensure command execution on the main thread and pass the player object
                       mainThreadCommand.executeCommand(player, formattedCommand);
                   }
                   WinService win_service_hook = new WinService(plugin);

                   win_service_hook.handleWin(player);
                   if (plugin.getConfig().getBoolean("game.sendTitle")) {
                       String titleHeader = plugin.getConfig().getString("titles.header")
                               .replace("&", "§")
                               .replace("%player%", player.getName())
                               .replace("%winner%", player.getName())
                               .replace("%number%", String.valueOf(plugin.getTargetNumber()));

                       String subtitle = plugin.getConfig().getString("titles.subtitle")
                               .replace("%player%", player.getName())
                               .replace("%number%", String.valueOf(plugin.getTargetNumber()));

                       String TITLE_MODE = plugin.getConfig().getString("game.title_mode");
                       plugin.getLogger().info("TITLE_MODE: " + TITLE_MODE);
                       if (TITLE_MODE.equals("WINNER_ONLY")) {
                           TitleAPI.sendTitle(player, titleHeader, subtitle, 10, 40, 10);
                       } else if (TITLE_MODE.equals("ALL_PLAYERS")) {
                           for (Player bukkitPlayer : Bukkit.getOnlinePlayers()) {
                               TitleAPI.sendTitle(bukkitPlayer, titleHeader, subtitle, 10, 40, 10);
                           }
                       }
                       else{
                           plugin.getLogger().info("Unknown title mode: " + TITLE_MODE);
                       }
                   }

                   // Reset the game
                   plugin.setTargetNumber(-1);
                   plugin.setGameAuthor("");
               } else {
                   if (plugin.getConfig().getBoolean("game.displayWrong")) {
                       // Get and format the wrong guess message
                       String displayWrongMessage = plugin.getConfig()
                               .getString("messages.wrong_number", "&cIncorrect guess, please try again!")
                               .replace("%player%", player.getName())  // Replace %player% with player's name
                               .replace("%min%", plugin.getConfig().getString("game.min"))  // Replace %min% with the min value
                               .replace("%max%", plugin.getConfig().getString("game.max"))  // Replace %max% with the max value
                               .replace("&", "§");  // Replace '&' with '§' for color codes

                       // Send the formatted message to the player
                       player.sendMessage(displayWrongMessage);
                   }

               }
           } catch (NumberFormatException e) {
               // If the message is not a number, send an invalid number message and cancel the event
               String invalidMessage = plugin.getConfig().getString("messages.invalid_number", "&cYou must enter a valid number.")
                       .replace("&", "§");
               player.sendMessage(invalidMessage);
               event.setCancelled(true);  // Cancel the event to prevent the non-numeric message from being sent
           }
       }
    }
}
