package com.sebbaindustries.zanmarkdefender;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class Core extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            /*
             * We register the EventListener here, when PlaceholderAPI is installed.
             * Since all events are in the main class (this class), we simply use "this"
             */
            Bukkit.getPluginManager().registerEvents(this, this);
        } else {
            /*
             * We inform about the fact that PlaceholderAPI isn't installed and then
             * disable this plugin to prevent issues.
             */
            getLogger().log(Level.WARNING, "Could not find PlaceholderAPI! This plugin is required.");
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    /**
     * On /msg command
     * @param e Event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCommand(PlayerCommandPreprocessEvent e) {
        if (e.getMessage().startsWith("/msg")) {
            Player player = e.getPlayer();
            String message = e.getMessage();
            check(player, message);
        }
    }

    /**
     * Async chat listener
     * @param e Event
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void AsyncChatEvent(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        String message = e.getMessage().toLowerCase();
        check(player, message);

    }

    /**
     * Check if string contains word "zanmark" and bans the player if their playtime is under 25minutes
     * @param player Player instance
     * @param message Message or command
     */
    private void check(Player player, String message) {
        if (message.contains("zanmark")) {
            int time = getTime(player);
            if (time <= 25) {
                ban(player);
            }
            return;
        }
        message = message.replaceAll(" ", "");
        if (message.contains("zanmark")) {
            int time = getTime(player);
            if (time <= 25) {
                ban(player);
            }
        }
    }

    /**
     * Gets players play time on the server
     * @param player Player instance
     * @return Playtime
     */
    private int getTime(Player player) {
        String pTime = "%statistic_minutes_played%";
        pTime = PlaceholderAPI.setPlaceholders(player, pTime);
        return parseInt(pTime);
    }

    /**
     * Yeets the player, we need to execute this in main thread because bukkit...
     * @param player Player Instance
     */
    private void ban(Player player) {
        Bukkit.getScheduler().runTask(this, () -> {
            ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
            String command = "banip -s " + player.getName();
            Bukkit.dispatchCommand(console, command);
        });
    }

    /**
     * Parses string and returns int.
     * @param s String
     * @return Integer
     */
    private int parseInt(String s) {
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            getLogger().log(Level.WARNING, "Error while parsing playertime!");
            return 1000;
        }
    }

}
