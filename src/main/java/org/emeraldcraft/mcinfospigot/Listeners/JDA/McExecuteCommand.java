package org.emeraldcraft.mcinfospigot.Listeners.JDA;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.emeraldcraft.mcinfospigot.Bot;
import org.emeraldcraft.mcinfospigot.MCInfo;

import java.awt.*;
import java.util.Objects;

public class McExecuteCommand extends ListenerAdapter {
    private Bot bot;

    public McExecuteCommand(Bot bot) {
        this.bot = bot;
    }

    @Override
    public void onSlashCommand(SlashCommandEvent event){
        if(event.getSubcommandName() != null) {
            if (event.getSubcommandName().equalsIgnoreCase("execute")) {
                if(!JavaPlugin.getProvidingPlugin(MCInfo.class).getConfig().getBoolean("mcexecute-enabled")){
                    Bukkit.getScheduler().runTask(JavaPlugin.getProvidingPlugin(MCInfo.class), () -> {
                        EmbedBuilder embedBuilder = new EmbedBuilder();
                        embedBuilder.setAuthor(JavaPlugin.getPlugin(MCInfo.class).getConfig().getString("server-name"));
                        embedBuilder.setColor(Color.RED);
                        embedBuilder.setTitle(":no_entry_sign: Sorry, but this feature has been disabled by the server administrators! :no_entry_sign:\n Please talk to the server admins if you would like to ");
                        embedBuilder.setFooter("Made by EmerqldWither");
                        event.replyEmbeds(embedBuilder.build()).setEphemeral(true).queue();
                    });
                    return;
                }

                Bukkit.getScheduler().runTask(JavaPlugin.getProvidingPlugin(MCInfo.class), () -> {
                    Color color;
                    if(event.getMember() != null || event.getMember().getColor() != null){
                        color = event.getMember().getColor();
                    }
                    else {
                        color = new Color(255, 255, 255);
                    }
                    Bukkit.broadcastMessage(ChatColor.BLUE + "[Discord] " + ChatColor.DARK_AQUA + "User '" + net.md_5.bungee.api.ChatColor.of(color) + event.getUser().getName() + ChatColor.DARK_AQUA + "' has executed the command '" + event.getOption("command").getAsString() + "'!");
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), event.getOption("command").getAsString());
                });
                for (TextChannel textChannel : bot.getBot().getTextChannels()) {
                    if (textChannel.getName().equalsIgnoreCase(JavaPlugin.getProvidingPlugin(MCInfo.class).getConfig().getString("bot-logs-channel"))) {
                        textChannel.sendMessage("User **'" + event.getUser().getName() + "'** has executed the command **'" + Objects.requireNonNull(event.getOption("command")).getAsString() + "'**!").queue();
                        break;
                    }
                }
                event.reply(new MessageBuilder(":white_check_mark: Successfully ran the command!").build()).queue();
            }
        }
    }

}
