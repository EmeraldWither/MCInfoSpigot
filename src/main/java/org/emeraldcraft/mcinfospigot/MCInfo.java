package org.emeraldcraft.mcinfospigot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.emeraldcraft.mcinfospigot.Listeners.Bukkit.PlayerDiscordChat;
import org.emeraldcraft.mcinfospigot.Listeners.Bukkit.PlayerLeaveJoinListener;
import org.emeraldcraft.mcinfospigot.Listeners.JDA.DiscordChat;
import org.emeraldcraft.mcinfospigot.Listeners.JDA.GuildMemberChangeVoiceChannel;
import org.emeraldcraft.mcinfospigot.Listeners.JDA.McExecuteCommand;

import javax.security.auth.login.LoginException;
import java.awt.*;
import java.sql.SQLException;
import java.util.logging.Level;

public final class MCInfo extends JavaPlugin {
    private Bot botClass;
    @SuppressWarnings("ConstantConditions")
    @Override
    public void onEnable() {
        // Plugin startup logic

        this.saveDefaultConfig();

        String botToken = getConfig().getString("bot-token");
        String url = getConfig().getString("mysql.database-url");
        Integer port = getConfig().getInt("mysql.database-port");
        String dbname = getConfig().getString("mysql.database-name");
        String username = getConfig().getString("mysql.database-username");
        String password = getConfig().getString("mysql.database-password");

        try {
            JDA bot = JDABuilder.createDefault(botToken)
                    .setActivity(Activity.listening("/mcserver"))
                    .setAutoReconnect(true)
                    .enableIntents(GatewayIntent.GUILD_MEMBERS)
                    .enableCache(CacheFlag.VOICE_STATE)
                    .setMemberCachePolicy(MemberCachePolicy.VOICE)
                    .build();
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
            Database database = new Database(url, port, dbname, username, password);
            try {
                database.testConnection();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            Logger logger = (Logger) LogManager.getRootLogger();
            botClass = new Bot(database, bot, logger);
            Bukkit.getServer().getScheduler().runTask(this, () -> {
                botClass.chat("The server has come online!", Color.GREEN);
                database.updateServerInfo(true);
                Bukkit.getScheduler().scheduleAsyncRepeatingTask(MCInfo.this, () -> database.updateTPS(true), 1, (10 * 20));
            });
            if(getConfig().getBoolean("javafxgui.enabled")){
                Bukkit.getScheduler().scheduleAsyncRepeatingTask(this, database::executeAdminCommands, getConfig().getInt("javafxgui.command-check-delay") * 20L, 5 * 20);
                if(getConfig().getBoolean("javafxgui.send-log-messages"))
                    Bukkit.getLogger().log(Level.INFO, "JavaFXGUI log messages enabled. I will now send the console messages to the GUI.");
                Bukkit.getScheduler().runTaskLater(this, () -> {
                    McINFOCustomLogAppender mcINFOCustomLogAppender = new McINFOCustomLogAppender(botClass);
                    logger.addAppender(mcINFOCustomLogAppender);
                    botClass.setCustomLogAppender(mcINFOCustomLogAppender);
                }, 5 * 20);
            }
            Bukkit.getPluginManager().registerEvents(new PlayerLeaveJoinListener(botClass), this);
            this.getCommand("dc").setExecutor(new PlayerDiscordChat(botClass));
            this.getCommand("discordchat").setExecutor(new PlayerDiscordChat(botClass));
            bot.addEventListener(new McExecuteCommand(botClass), new DiscordChat(), new GuildMemberChangeVoiceChannel());

        } catch (LoginException e) {
            e.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(MCInfo.this);
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        botClass.getCustomLogAppender().setEnabled(false);
        if(Bukkit.isStopping()) {
            Bukkit.getScheduler().cancelTasks(this);
            botClass.chat("Server is shutting down!", Color.RED);
            botClass.getDatabase().updateServerInfo(false);
            botClass.getDatabase().closeConnection();
            if(botClass.getBot() != null){
                botClass.getBot().shutdownNow();
            }
            Bukkit.getLogger().log(Level.INFO, "Everything shutoff correctly.");
        }
    }

}
