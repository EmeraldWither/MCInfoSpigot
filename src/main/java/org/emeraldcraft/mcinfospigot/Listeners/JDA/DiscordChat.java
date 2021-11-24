package org.emeraldcraft.mcinfospigot.Listeners.JDA;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.emeraldcraft.mcinfospigot.MCInfo;

import java.awt.*;

public class DiscordChat extends ListenerAdapter {

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if (event.getChannel().getName().equalsIgnoreCase(JavaPlugin.getPlugin(MCInfo.class).getConfig().getString("bot-channel"))) {
            if (event.getAuthor().isBot()) {
                return;
            }
            Color color = new Color(255, 255, 255);
            if(event.getMember() != null && event.getMember().getColor() != null){
                color = event.getMember().getColor();
            }
            if(color == null){
                color = new Color(255, 255, 255);
            }

            Bukkit.broadcastMessage(ChatColor.BLUE + "[Discord] " + net.md_5.bungee.api.ChatColor.of(color) + event.getAuthor().getName() + ChatColor.GRAY + ": " + event.getMessage().getContentDisplay());
            if (event.getMessage().getContentDisplay().contains("hello") || event.getMessage().getContentDisplay().contains("Hello")) {
                event.getMessage().addReaction("U+1F44B").queue();
            }
        }
    }
}
