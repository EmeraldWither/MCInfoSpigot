package org.emeraldcraft.mcinfospigot;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import org.apache.logging.log4j.core.Logger;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.awt.*;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Level;

public class Bot {
    private final Database database;
    private final JDA bot;
    private McINFOCustomLogAppender customLogAppender = null;
    private final Logger logger;
    private final HashMap<UUID, Boolean> isChatToggled = new HashMap<>();
    public Bot(Database database, JDA bot, Logger logger){
        this.database = database;
        this.bot = bot;
        this.logger = logger;
    }

    public Database getDatabase() {
        return database;
    }

    public JDA getBot() {
        return bot;
    }
    public void setCustomLogAppender(McINFOCustomLogAppender mcINFOCustomLogAppender){
        if(this.customLogAppender == null){
            this.customLogAppender  = mcINFOCustomLogAppender;
        }
    }
    public McINFOCustomLogAppender getCustomLogAppender() {
        return customLogAppender;
    }

    public Logger getLogger() {
        return logger;
    }
    public void chat(String msg, Color color){
        for(TextChannel channel : bot.getTextChannels()){
            if(channel.getName().equalsIgnoreCase(JavaPlugin.getPlugin(MCInfo.class).getConfig().getString("bot-channel"))){
                EmbedBuilder embedBuilder = new EmbedBuilder();
                embedBuilder.setAuthor(JavaPlugin.getPlugin(MCInfo.class).getConfig().getString("server-name"));
                embedBuilder.setColor(color);
                embedBuilder.setTitle(msg);
                embedBuilder.setFooter("Made by EmerqldWither");
                if(Objects.requireNonNull(JavaPlugin.getProvidingPlugin(MCInfo.class).getConfig().getString("roleid")).equalsIgnoreCase("0")){
                    channel.sendMessageEmbeds(embedBuilder.build()).queue();
                    return;
                }
                String roleID = JavaPlugin.getProvidingPlugin(MCInfo.class).getConfig().getString("roleid");
                if(roleID == null){
                    Bukkit.getLogger().log(Level.SEVERE, "Your RoleID is incorrect. Unable to send the message as a ping.");
                    channel.sendMessageEmbeds(embedBuilder.build()).queue();
                }
                assert roleID != null;
                MessageBuilder messageBuilder = new MessageBuilder(Objects.requireNonNull(channel.getGuild().getRoleById(roleID)).getAsMention()).setEmbeds(embedBuilder.build());
                channel.sendMessage(messageBuilder.build()).mentionRoles(roleID).queue();
            }
        }
    }
    public boolean isChatToggled(UUID uuid) {
        boolean toggled = false;
        if(isChatToggled.containsKey(uuid)){
            toggled = isChatToggled.get(uuid);
        }
        return toggled;
    }
    public void toggleChat(UUID uuid){
        if(isChatToggled.containsKey(uuid)){
            isChatToggled.put(uuid, !isChatToggled.get(uuid));
        }else{
            isChatToggled.put(uuid, true);
        }

    }
}
