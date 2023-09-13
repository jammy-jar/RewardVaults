package me.jammy.rewardvaults.db;

import lombok.Getter;
import me.jammy.rewardvaults.RewardVaults;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConnectionHandler {

    private static final RewardVaults plugin = RewardVaults.getInstance();
    private static final Configuration config = plugin.getConfig();
    private static final Logger log = plugin.getLogger();
    @Getter
    private static Connection conn;
    @Getter
    private static final String storageMethod = config.getString("storage-method");

    public static void connect() {
        try {
            if (storageMethod.equalsIgnoreCase("sqlite"))
                connectSQLite();
            else if (storageMethod.equalsIgnoreCase("mysql"))
                connectMySQL();
        } catch (SQLException e) {
            log.warning(e.getMessage());
        }
    }

    public static void connectSQLite() throws SQLException {
        String url = "jdbc:sqlite:" + plugin.getDataFolder() + "/quests.db";
        conn = DriverManager.getConnection(url);
        log.info("Connection to SQLite has been established!");
    }

    public static void connectMySQL() throws SQLException {
        ConfigurationSection dataConf = config.getConfigurationSection("data");

        String user = dataConf.getString("username");
        String password = dataConf.getString("password");
        String url = "jdbc:mysql://" + dataConf.getString("address") + "/" + dataConf.getString("database");

        conn = DriverManager.getConnection(url, user, password);
        log.info("Connection to SQLite has been established!");
    }

    public static void disconnect() {
        try {
            conn.close();
        } catch (SQLException ignored) {
        }
    }

    public static void initDb() throws SQLException, IOException {
        connect();
        String setup;
        try (InputStream in = plugin.getResource("dbsetup_" + storageMethod.toLowerCase() + ".sql")) {
            assert in != null;
            //noinspection BlockingMethodInNonBlockingContext
            setup = new String(in.readAllBytes());
        } catch (IOException e) {
            log.log(Level.SEVERE, "Could not read db setup file.", e);
            throw e;
        }
        String[] queries = setup.split(";");

        for (String query : queries) {
            if (query.isBlank()) continue;
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.execute();
            }
        }
        log.info("§2Database setup complete.");
    }
}
