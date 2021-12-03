package org.emeraldcraft.mcinfospigot;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.emeraldcraft.mcinfospigot.Listeners.Bukkit.PlayerDiscordChat;
import org.emeraldcraft.mcinfospigot.Listeners.Bukkit.PlayerLeaveJoinListener;
import org.emeraldcraft.mcinfospigot.Listeners.JDA.DiscordChat;
import org.emeraldcraft.mcinfospigot.Listeners.JDA.GuildMemberChangeVoiceChannel;
import org.emeraldcraft.mcinfospigot.Listeners.JDA.McExecuteCommand;
import org.jetbrains.annotations.NotNull;

import javax.security.auth.login.LoginException;
import java.awt.*;
import java.sql.SQLException;
import java.util.Objects;
import java.util.logging.Level;

public final class MCInfo extends JavaPlugin {

    private static Database database;
    private static JDA bot;
    private Logger logger;
    private static McINFOCustomLogAppender customLogger;

    public static Database getDatabase() {
        return database;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onEnable() {
        // Plugin startup logic

        this.saveDefaultConfig();
        Bukkit.getPluginManager().registerEvents(new PlayerLeaveJoinListener(), this);
        this.getCommand("dc").setExecutor(new PlayerDiscordChat());
        this.getCommand("discordchat").setExecutor(new PlayerDiscordChat());

        String botToken = getConfig().getString("bot-token");
        String url = getConfig().getString("mysql.database-url");
        Integer port = getConfig().getInt("mysql.database-port");
        String dbname = getConfig().getString("mysql.database-name");
        String username = getConfig().getString("mysql.database-username");
        String password = getConfig().getString("mysql.database-password");

        try {
            bot = JDABuilder.createDefault(botToken)
                    .setActivity(Activity.listening("/mcserver"))
                    .setAutoReconnect(true)
                    .addEventListeners(new McExecuteCommand(), new DiscordChat(), new GuildMemberChangeVoiceChannel())
                    .enableIntents(GatewayIntent.GUILD_MEMBERS)
                    .enableCache(CacheFlag.VOICE_STATE)
                    .setMemberCachePolicy(MemberCachePolicy.VOICE)
                    .build();
        } catch (LoginException e) {
            e.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(MCInfo.this);
            return;
        }
        boolean foundCommand = false;
        for (Command command : bot.retrieveCommands().complete()) {
            if (command.getName().equalsIgnoreCase("mcserver")) {
                foundCommand = true;
                break;
            }
        }

        if(!foundCommand) {
            Bukkit.getLogger().log(Level.INFO, "Had to upsert the command.");
            bot.upsertCommand("mcserver", "Minecraft Server command.")
                    .addSubcommands(new SubcommandData("info", "Get information about the minecraft server."))
                    .addSubcommands(new SubcommandData("execute", "Execute a minecraft server command").addOption(OptionType.STRING, "command", "The command that you want to run", true))
                    .queue();
        }
        database = new Database(url, port, dbname, username, password);
        try {
            database.testConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Bukkit.getServer().getScheduler().runTask(this, () -> {
            chat("The server has come online!", Color.GREEN);
            MCInfo.getDatabase().updateServerInfo(true);
            Bukkit.getScheduler().scheduleAsyncRepeatingTask(MCInfo.this, () -> MCInfo.getDatabase().updateTPS(true), 1, (10 * 20));
        });
        if(getConfig().getBoolean("javafxgui.enabled")){
            Bukkit.getScheduler().scheduleAsyncRepeatingTask(this, () -> MCInfo.getDatabase().executeAdminCommands(), getConfig().getInt("javafxgui.command-check-delay") * 20L, 5 * 20);
            if(getConfig().getBoolean("javafxgui.send-log-messages"))
                Bukkit.getLogger().log(Level.INFO, "JavaFXGUI log messages enabled. I will now send the console messages to the GUI.");
                Bukkit.getScheduler().runTaskLater(this, () -> {
                    logger = (Logger) LogManager.getRootLogger();
                    logger.addAppender(new McINFOCustomLogAppender());
                }, 5 * 20);
        }



    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        customLogger.setEnabled(false);
        if(Bukkit.isStopping()) {
            Bukkit.getScheduler().cancelTasks(this);
            chat("Server is shutting down!", Color.RED);
            MCInfo.getDatabase().updateServerInfo(false);
            MCInfo.getDatabase().closeConnection();
            if(bot != null){
                bot.shutdownNow();
            }
            Bukkit.getLogger().log(Level.INFO, "Everything shutoff correctly.");
        }
    }
    public static JDA getBot(){
        return bot;
    }
    public static void chat(String msg, Color color){
        for(TextChannel channel : bot.getTextChannels()){
            if(channel.getName().equalsIgnoreCase(JavaPlugin.getPlugin(MCInfo.class).getConfig().getString("bot-channel"))){
                EmbedBuilder embedBuilder = new EmbedBuilder();
                        embedBuilder.setAuthor(JavaPlugin.getPlugin(MCInfo.class).getConfig().getString("server-name"));
                        embedBuilder.setColor(color);
                        embedBuilder.setTitle(msg);
                        embedBuilder.setFooter("Made by EmerqldWither");
                if(Objects.requireNonNull(getFileConfig().getString("roleid")).equalsIgnoreCase("0")){
                    channel.sendMessageEmbeds(embedBuilder.build()).queue();
                    return;
                }
                String roleID = getFileConfig().getString("roleid");
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
    public static void setCustomLogger(@NotNull McINFOCustomLogAppender customLogger){
        if(MCInfo.customLogger == null){
            MCInfo.customLogger = customLogger;
        }
    }

    public static McINFOCustomLogAppender getCustomLogger() {
        return customLogger;
    }

    public static FileConfiguration getFileConfig(){
        return JavaPlugin.getPlugin(MCInfo.class).getConfig();
    }

}
