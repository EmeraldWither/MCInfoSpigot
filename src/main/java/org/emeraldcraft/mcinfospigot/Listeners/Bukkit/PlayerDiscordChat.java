package org.emeraldcraft.mcinfospigot.Listeners.Bukkit;

import net.dv8tion.jda.api.entities.TextChannel;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.emeraldcraft.mcinfospigot.Bot;
import org.emeraldcraft.mcinfospigot.MCInfo;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PlayerDiscordChat implements CommandExecutor {
    private Bot bot;

    public PlayerDiscordChat(Bot bot) {
        this.bot = bot;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        String prefix = ChatColor.BLUE + "[Discord] ";

        if(!(sender instanceof Player)){
            sender.sendMessage(prefix + ChatColor.RED + "Sorry, but you must be a player to use this command!");
            return false;
        }

        Player player = ((Player) sender).getPlayer();
        if(args.length < 1){
            sender.sendMessage(prefix + ChatColor.RED + "Please input a chat message!");
            return false;
        }
        StringBuilder stringBuilder = new StringBuilder(args[0]);
        for (int arg = 1; arg < args.length; arg++) {
            stringBuilder.append(" ").append(args[arg]);
        }
        String msg = "[SERVER CHAT] " + sender.getName() + ": " +  stringBuilder;
        List<TextChannel> textChannels = bot.getBot().getTextChannels();

        for(TextChannel textChannel : textChannels){
            if(textChannel.getName().equalsIgnoreCase(JavaPlugin.getPlugin(MCInfo.class).getConfig().getString("bot-channel"))){
                textChannel.sendMessage(msg).queue();
            }
        }
        assert player != null;
        player.sendMessage(prefix + ChatColor.GREEN + sender.getName() + ChatColor.WHITE + " [YOU] : " + ChatColor.GRAY + stringBuilder);
        for(Player otherPlayer : Bukkit.getOnlinePlayers()){
            if(otherPlayer.getUniqueId() != player.getUniqueId()){
                otherPlayer.sendMessage(prefix + ChatColor.GREEN + sender.getName() + ChatColor.WHITE + " [MC] : " + ChatColor.GRAY + stringBuilder);
            }
        }
        return true;
    }
}
