package org.emeraldcraft.mcinfospigot.Listeners.JDA;

import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.emeraldcraft.mcinfospigot.MCInfo;
import org.jetbrains.annotations.NotNull;

public class GuildMemberChangeVoiceChannel extends ListenerAdapter {

    @Override
    public void onGuildVoiceJoin(@NotNull GuildVoiceJoinEvent event){
        Bukkit.getScheduler().runTask(JavaPlugin.getProvidingPlugin(MCInfo.class), new Runnable() {
            @Override
            public void run() {
                Bukkit.broadcastMessage(ChatColor.BLUE + "[Discord] " + ChatColor.GREEN + "\"" + event.getMember().getUser().getName() + "\" has joined the voice channel \"" + event.getChannelJoined().getName() + "\"!");
                Bukkit.getOnlinePlayers().forEach(player -> player.playSound(Sound.sound(Key.key("minecraft:entity.arrow.hit_player"), Sound.Source.PLAYER, 100, 100)));
            }
        });
    }
    @Override
    public void onGuildVoiceLeave(@NotNull GuildVoiceLeaveEvent event){
        Bukkit.getScheduler().runTask(JavaPlugin.getProvidingPlugin(McExecuteCommand.class), new Runnable() {
            @Override
            public void run() {
                Bukkit.broadcastMessage(ChatColor.BLUE + "[Discord] " + ChatColor.RED + "\"" + event.getMember().getUser().getName() + "\" has disconnected from the voice channel \"" + event.getChannelLeft().getName() + "\"!");
                Bukkit.getOnlinePlayers().forEach(player -> player.playSound(Sound.sound(Key.key("minecraft:entity.enderman.hurt"), Sound.Source.PLAYER, 100, 100)));
            }
        });
    }
    @Override
    public void onGuildVoiceMove(@NotNull GuildVoiceMoveEvent event){
        Bukkit.getScheduler().runTask(JavaPlugin.getProvidingPlugin(MCInfo.class), new Runnable() {
            @Override
            public void run() {
                Bukkit.broadcastMessage(ChatColor.BLUE + "[Discord] " + ChatColor.AQUA + "\"" + event.getMember().getUser().getName() + "\" has joined the voice channel \"" + event.getChannelJoined().getName() + "\", from the \"" + event.getChannelLeft().getName() + "\" voice channel!");
                Bukkit.getOnlinePlayers().forEach(player -> player.playSound(Sound.sound(Key.key("minecraft:entity.arrow.hit_player"), Sound.Source.PLAYER, 100, 100)));

            }
        });
    }

}
