package net.corecrafted.chatlogger;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MainListener implements Listener {
    AppLaunch plugin;
    Connection conn;

    public MainListener(AppLaunch plugin) {
        this.plugin = plugin;
        try {
            conn = DriverManager.getConnection("jdbc:mysql://" + plugin.getConfig().getString("database.host") + "/" + plugin.getConfig().getString("database.schema"), plugin
                    .getConfig().getString("database.username"), plugin.getConfig().getString("database.password"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        try {

            PreparedStatement stmt = conn.prepareStatement("REPLACE INTO uuid_map (uuid,playername) VALUES (?,?)");
            stmt.setString(1, p.getUniqueId().toString());
            stmt.setString(2, p.getName());
            stmt.execute();
            plugin.getLogger().info("Updated UUID Link: " + p.getUniqueId().toString() + " | " + p.getName());
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        String msg = e.getMessage();
        try {
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO log (player, message, server,datetime) VALUES (?,?,?,?)");
            stmt.setString(1,p.getUniqueId().toString());
            stmt.setString(2,msg);
            stmt.setString(3,plugin.getConfig().getString("server-name"));
            stmt.setLong(4,System.currentTimeMillis());
            stmt.execute();
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
    }
}

