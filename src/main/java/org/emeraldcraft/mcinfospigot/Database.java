package org.emeraldcraft.mcinfospigot;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.*;
import java.util.UUID;
import java.util.logging.Level;

public class Database {
    private String url;
    private Integer port;
    private String username;
    private String name;
    private String password;
    private Connection connection;
    private boolean isEnabled = false;
    private int internalLogID = 1;
    public Database(String url, Integer port, String name , String username, String password){
        this.url = url;
        this.name = name;
        this.port = port;
        this.username = username;
        this.password = password;
    }
    public void testConnection() throws SQLException {
        try {
            //Class.forName("com.mysql.cj.jdbc.Driver");

            this.openConnection();
            if(getConnection() != null && !getConnection().isClosed()){

                Bukkit.getLogger().log(Level.INFO, "Test database connection successful! You are good to go!");
                isEnabled = true;
                return;
            }
            isEnabled = false;
            Bukkit.getLogger().log(Level.INFO, "There was a problem while opening up the database connection. ");
            Bukkit.getPluginManager().disablePlugin(JavaPlugin.getProvidingPlugin(MCInfo.class));
        } catch (SQLException e) {
            e.printStackTrace();
            isEnabled = false;
            Bukkit.getLogger().log(Level.INFO, "There was a problem while opening up the database connection. ");
            Bukkit.getPluginManager().disablePlugin(JavaPlugin.getProvidingPlugin(MCInfo.class));
        }
    }
    public void openConnection(){
        try {
            String url = "jdbc:mysql://" + this.url + ":" + this.port + "/" + this.name;
            // try catch to get any SQL errors (for example connections errors)
            connection = DriverManager.getConnection(url, username, password);

            // with the method getConnection() from DriverManager, we're trying to set
            // the connection's url, username, password to the variables we made earlier and
            // trying to get a connection at the same time. JDBC allows us to do this.
        } catch (SQLException e) { // catching errors
            e.printStackTrace(); // prints out SQLException errors to the console (if any)
        }
    }
    public void closeConnection(){
        try {
            if(getConnection() != null && !getConnection().isClosed()){
                connection.close();
                connection = null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Getter and Setters
    public String getUrl(){
        return url;
    }
    public String getUsername() {
        return username;
    }
    public String getPassword() {
        return password;
    }
    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getPort() {
        return port;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public Connection getConnection(){
        return this.connection;
    }

    public void updateServerInfo(boolean isOnline) {
        int onlinePlayers = Bukkit.getOnlinePlayers().size();
        int maxPlayers = Bukkit.getMaxPlayers();
        String mcVersion = Bukkit.getMinecraftVersion();

        try {
            if (getConnection() == null || getConnection().isClosed() || getConnection().isValid(1000)) {
                openConnection();
            }
            Connection connection = getConnection();
            String sqlcreateTable = "create table if not exists serverinfo(onlinePlayers integer(7), maxPlayers integer(10), isOnline boolean, mcVersion varchar(7), tps integer(3), serverName varchar(1000));";
            String sqlins = "insert into serverinfo(onlinePlayers, maxPlayers, isOnline, mcVersion, tps, serverName) values(?, ?, ?, ?, ?, ?);";
            String sqlSelect = "SELECT * from serverinfo;";


            connection.prepareStatement(sqlcreateTable).execute();

            PreparedStatement stmt2 = connection.prepareStatement(sqlSelect);
            ResultSet results = stmt2.executeQuery();
            boolean hasData = false;
            while (results.next()) {
                String sqlupdateOnlinePlayers = "update serverinfo set onlinePlayers = ?;";
                PreparedStatement updateOnlinePlayers = connection.prepareStatement(sqlupdateOnlinePlayers);
                updateOnlinePlayers.setInt(1, onlinePlayers);
                updateOnlinePlayers.executeUpdate();

                String sqlupdateMaxPlayers = "update serverinfo set maxPlayers = ?;";
                PreparedStatement updateMaxPlayers = connection.prepareStatement(sqlupdateMaxPlayers);
                updateMaxPlayers.setInt(1, maxPlayers);
                updateMaxPlayers.executeUpdate();

                String sqlUpdateOnline = "update serverinfo set isOnline = ?;";
                PreparedStatement updateIsOnline = connection.prepareStatement(sqlUpdateOnline);
                updateIsOnline.setBoolean(1, isOnline);
                updateIsOnline.executeUpdate();

                String sqlUpdateMCVersion = "update serverinfo set mcVersion = ?;";
                PreparedStatement updateMCVersion = connection.prepareStatement(sqlUpdateMCVersion);
                updateMCVersion.setString(1, mcVersion);
                updateMCVersion.executeUpdate();

                String sqlUpdateServerName = "update serverinfo set serverName = ?;";
                PreparedStatement updateServerName = connection.prepareStatement(sqlUpdateServerName);
                updateServerName.setString(1, MCInfo.getFileConfig().getString("server-name"));
                updateServerName.executeUpdate();

                String sqlUpdateTPS = "update serverinfo set tps = ?;";
                PreparedStatement updateTPS = connection.prepareStatement(sqlUpdateTPS);
                updateTPS.setInt(1, (int)Bukkit.getTPS()[0]);
                updateTPS.executeUpdate();

                hasData = true;
            }
            if (!hasData) {
                //Insert Data
                PreparedStatement stmt3 = connection.prepareStatement(sqlins);
                stmt3.setInt(1, onlinePlayers);
                stmt3.setInt(2, maxPlayers);
                stmt3.setBoolean(3, isOnline);
                stmt3.setString(4, mcVersion);
                stmt3.setInt(5, (int)Bukkit.getTPS()[0]);
                stmt3.setString(6, MCInfo.getFileConfig().getString("server-name"));
                stmt3.executeUpdate();
            }

        } catch (SQLException e) {
            Bukkit.getLogger().log(Level.SEVERE, " A database error has occurred!");
            e.printStackTrace();
        }
    }

    public void updateTPS(boolean isOnline) {
        int onlinePlayers = Bukkit.getOnlinePlayers().size();
        int maxPlayers = Bukkit.getMaxPlayers();
        String mcVersion = Bukkit.getMinecraftVersion();
        try {
            if (getConnection() == null || getConnection().isClosed()) {
                openConnection();
            }
            Connection connection = this.connection;
            String sqlcreateTable = "create table if not exists serverinfo(onlinePlayers integer(7), maxPlayers integer(10), isOnline boolean, mcVersion varchar(7), tps integer(3), serverName varchar(1000));";
            String sqlins = "insert into serverinfo(onlinePlayers, maxPlayers, isOnline, mcVersion, tps, serverName) values(?, ?, ?, ?, ?, ?);";
            String sqlSelect = "SELECT * from serverinfo";

            // Create table
            PreparedStatement stmt = connection.prepareStatement(sqlcreateTable);
            stmt.execute();

            PreparedStatement stmt2 = connection.prepareStatement(sqlSelect);
            ResultSet results = stmt2.executeQuery();
            boolean hasData = false;
            while (results.next()) {
                String sqlUpdateTPS = "update serverinfo set tps = ?;";
                PreparedStatement updateTPS = connection.prepareStatement(sqlUpdateTPS);
                updateTPS.setInt(1, (int)Bukkit.getTPS()[0]);
                updateTPS.executeUpdate();

                hasData = true;
            }
            if(!hasData){
                PreparedStatement stmt3 = connection.prepareStatement(sqlins);
                stmt3.setInt(1, onlinePlayers);
                stmt3.setInt(2, maxPlayers);
                stmt3.setBoolean(3, isOnline);
                stmt3.setString(4, mcVersion);
                stmt.setInt(5, (int)Bukkit.getTPS()[0]);
                stmt3.setString(6, MCInfo.getFileConfig().getString("server-name"));
                stmt3.executeUpdate();
            }

        } catch (SQLException e) {
            Bukkit.getLogger().log(Level.SEVERE, " A database error has occurred!");
            e.printStackTrace();
        }
    }
    public void executeAdminCommands(){
        try {
            if (getConnection() == null || getConnection().isClosed()) {
                openConnection();
            }
            Connection connection = getConnection();
            String sqlcreateTable = "CREATE TABLE IF NOT EXISTS commands(commandID varchar(200), command varchar(1000));";
            String selectCommands = "SELECT * FROM commands";
            String deleteCommand = "DELETE FROM commands WHERE commandID = ?;";

            // Create table
            PreparedStatement stmt = connection.prepareStatement(sqlcreateTable);
            stmt.executeUpdate();

            PreparedStatement getCommands = connection.prepareStatement(selectCommands);
            ResultSet commands = getCommands.executeQuery();
            while (commands.next()){
                String commandID = commands.getString(1);
                String command = commands.getString(2);
                Bukkit.getScheduler().runTask(MCInfo.getProvidingPlugin(MCInfo.class), () -> {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                    //Bukkit.getLogger().log(Level.INFO, "Dispatched command: " + command);
                });
                PreparedStatement deleteCommandStatement = connection.prepareStatement(deleteCommand);
                deleteCommandStatement.setString(1, commandID);
                deleteCommandStatement.executeUpdate();

            }
        }
        catch (SQLException e){
            Bukkit.getLogger().log(Level.INFO, "A database error has occurred!");
            closeConnection();
            e.printStackTrace();
        }
    }
    public void sendLogMessage(String msg){
        try {
            if (getConnection() == null || getConnection().isClosed()) {
                openConnection();
            }
            Connection connection = getConnection();
            String sqlcreateTable = "CREATE TABLE IF NOT EXISTS logs(logID integer(100), logMessage varchar(10000));";
            String insertLog = "INSERT INTO logs VALUES (?, ?);";

            // Create table
            PreparedStatement stmt = connection.prepareStatement(sqlcreateTable);
            stmt.executeUpdate();

            PreparedStatement insertLogStatement = connection.prepareStatement(insertLog);
            insertLogStatement.setInt(1, internalLogID);
            insertLogStatement.setString(2, msg);
            insertLogStatement.executeUpdate();
            internalLogID++;
        }
        catch (SQLException e){
            Bukkit.getLogger().log(Level.INFO, "A database error has occurred!");
            closeConnection();
            e.printStackTrace();
        }
    }

}
