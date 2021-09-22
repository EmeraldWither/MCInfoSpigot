package org.emeraldcraft.mcinfospigot.Listeners.Bukkit;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.emeraldcraft.mcinfospigot.MCInfo;

import java.awt.*;

public class PlayerLeaveJoinListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        Bukkit.getScheduler().runTaskAsynchronously(JavaPlugin.getProvidingPlugin(MCInfo.class), new Runnable() {
            @Override
            public void run() {
                MCInfo.chat("**" + event.getPlayer().getName() +  "** has joined the server!", Color.GREEN);
                MCInfo.getDatabase().updateServerInfo(true);
            }
        });
    }
    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event){
        Bukkit.getScheduler().runTaskAsynchronously(JavaPlugin.getProvidingPlugin(MCInfo.class), new Runnable() {
            @Override
            public void run() {
                MCInfo.chat("**" + event.getPlayer().getName() +  "** has left the server!", Color.RED);
                MCInfo.getDatabase().updateServerInfo(true);
            }
        });
    }
}
