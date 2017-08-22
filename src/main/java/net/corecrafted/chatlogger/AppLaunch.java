package net.corecrafted.chatlogger;

import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Logger;

public class AppLaunch extends JavaPlugin {

    PluginDescriptionFile pdf = getDescription();
    private File configf;
    private FileConfiguration config;
    private ConsoleCommandSender console = getServer().getConsoleSender();
    private Logger logger = getLogger();
    private Connection conn;

    @Override
    public void onEnable() {
        logger.info("ChatLogger "+pdf.getVersion()+" has been enabled");
        loadConfig();
        getServer().getPluginManager().registerEvents(new MainListener(this),this);
        if (config.getString("server-name").equals("none")){
            logger.warning("Server name not defined , please define it first");
            getServer().getPluginManager().disablePlugin(this);
        }
        createTables();
    }

    public void loadConfig() {
        configf = new File(getDataFolder(), "config.yml");

        if (!configf.exists()) {
            configf.getParentFile().mkdir();
            saveResource("config.yml",false);
        }

        config=new YamlConfiguration();
        try {
            config.load(configf);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    private void createTables(){
        try {
            conn = DriverManager.getConnection("jdbc:mysql://" + getConfig().getString("database.host") + "/" + getConfig().getString("database.schema"),getConfig().getString("database.username"), getConfig().getString("database.password"));
            PreparedStatement statement = conn.prepareStatement("create table IF NOT EXISTS log( id int auto_increment primary key, server varchar(32) null, player varchar(36) null, message varchar(256) null, datetime mediumtext null) ; ");
            statement.execute();
            statement=conn.prepareStatement("create table IF NOT EXISTS uuid_map ( uuid varchar(36) not null primary key, playername varchar(45) null ) ; ");
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @Override
    public FileConfiguration getConfig() {
        return config;
    }

    public ConsoleCommandSender getConsole() {
        return console;
    }
}

