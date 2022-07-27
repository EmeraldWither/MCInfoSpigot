package org.emeraldcraft.mcinfospigot.Listeners.Bukkit;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.emeraldcraft.mcinfospigot.Bot;
import org.emeraldcraft.mcinfospigot.MCInfo;

import java.awt.*;

public class PlayerLeaveJoinListener implements Listener {
    private Bot bot;

    public PlayerLeaveJoinListener(Bot bot) {
        this.bot = bot;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        Bukkit.getScheduler().runTaskAsynchronously(JavaPlugin.getProvidingPlugin(MCInfo.class), () -> {
            bot.chat("**" + event.getPlayer().getName() +  "** has joined the server!", Color.GREEN);
            bot.getDatabase().updateServerInfo(true);
        });
    }
    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event){
        Bukkit.getScheduler().runTaskAsynchronously(JavaPlugin.getProvidingPlugin(MCInfo.class), () -> {
            bot.chat("**" + event.getPlayer().getName() +  "** has left the server!", Color.RED);
            bot.getDatabase().updateServerInfo(true);
        });
    }
}
