package org.emeraldcraft.mcinfospigot.Listeners.Bukkit;

import dev.vankka.mcdiscordreserializer.discord.DiscordSerializer;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.dv8tion.jda.api.entities.TextChannel;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.emeraldcraft.mcinfospigot.Bot;
import org.emeraldcraft.mcinfospigot.MCInfo;

import java.util.List;

public class PlayerChatListener implements Listener {
    private final Bot bot;
    public PlayerChatListener(Bot bot) {
        this.bot = bot;
    }
    @EventHandler
    public void onPlayerChat(AsyncChatEvent event){
        if(bot.isChatToggled(event.getPlayer().getUniqueId())){
            String prefix = ChatColor.BLUE + "[Discord] ";

            String msg = "[SERVER CHAT] " + event.getPlayer().getName() + ": " + DiscordSerializer.INSTANCE.serialize(event.message());
            List<TextChannel> textChannels = bot.getBot().getTextChannels();

            for(TextChannel textChannel : textChannels){
                if(textChannel.getName().equalsIgnoreCase(JavaPlugin.getPlugin(MCInfo.class).getConfig().getString("bot-channel"))){
                    textChannel.sendMessage(msg).queue();
                }
            }

            Component msgPlayer = Component.text(prefix + ChatColor.GREEN + event.getPlayer().getName() + ChatColor.WHITE + " [YOU]: " + ChatColor.GRAY).append(event.message());
            event.getPlayer().sendMessage(msgPlayer);
            for(Player otherPlayer : Bukkit.getOnlinePlayers()){
                if(otherPlayer.getUniqueId() != event.getPlayer().getUniqueId()){
                    Component message = Component.text(prefix + ChatColor.GREEN + event.getPlayer().getName() + ChatColor.WHITE + " [MC]: "+ ChatColor.GRAY).append(event.message());
                    otherPlayer.sendMessage(message);
                }
            }
            event.setCancelled(true);
        }
    }
}
