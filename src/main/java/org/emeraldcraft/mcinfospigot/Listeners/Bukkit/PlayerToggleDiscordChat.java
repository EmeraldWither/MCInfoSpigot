package org.emeraldcraft.mcinfospigot.Listeners.Bukkit;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.emeraldcraft.mcinfospigot.Bot;
import org.jetbrains.annotations.NotNull;

public class PlayerToggleDiscordChat implements CommandExecutor {
    private Bot bot;
    public PlayerToggleDiscordChat(Bot bot) {
        this.bot = bot;
    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player player)) return false;
        bot.toggleChat(player.getUniqueId());
        if(bot.isChatToggled(player.getUniqueId())){
            player.sendMessage(ChatColor.GREEN + "Discord chat enabled!");
        } else {
            player.sendMessage(ChatColor.RED + "Discord chat disabled!");
        }
        return false;
    }
}
