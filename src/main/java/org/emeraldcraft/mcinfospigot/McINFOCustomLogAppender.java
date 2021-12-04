package org.emeraldcraft.mcinfospigot;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;

public class McINFOCustomLogAppender extends AbstractAppender{

    private boolean isEnabled;
    private Bot bot;

    public McINFOCustomLogAppender(Bot bot) {
        super("MCINFOLogger", null, PatternLayout.newBuilder().withPattern("[%d{HH:mm:ss} %level]: %msg").build(), false);
        this.bot = bot;
        Bukkit.getLogger().log(Level.INFO, "Initiated logger. Will now send the messages to the database.");
        start();
        LocalDateTime date = LocalDateTime.ofInstant(Instant.ofEpochMilli(System.currentTimeMillis()), ZoneId.systemDefault());
        String formattedDate = date.format(DateTimeFormatter.ofPattern("HH:mm:ss "));
        String message = "[" + formattedDate  + " MCINFO]: " +  "The logger has been initiated. You should now see console messages in this window. ";
        this.bot = bot;
        bot.getDatabase().sendLogMessage(message);
        isEnabled = true;
    }

    @Override
    public void append(LogEvent event) {
        if(!isEnabled) return;
        LocalDateTime date = LocalDateTime.ofInstant(Instant.ofEpochMilli(System.currentTimeMillis()), ZoneId.systemDefault());
        String formattedDate = date.format(DateTimeFormatter.ofPattern("HH:mm:ss "));
        String message = "[" + formattedDate + event.getLevel() + "]: " +  ChatColor.stripColor(event.getMessage().getFormattedMessage());
        bot.getDatabase().sendLogMessage(message);
    }
    public void setEnabled(boolean isEnabled){
        this.isEnabled = isEnabled;
    }
}
