package net.vicnix.core.provider;

import lombok.Getter;
import net.vicnix.core.VicnixCore;
import net.vicnix.core.utils.VicnixIcon;
import org.bukkit.configuration.MemorySection;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

public class MysqlProvider {

    @Getter
    private final static MysqlProvider instance = new MysqlProvider();

    private final Map<String, Object> data = new HashMap<>();

    @Getter
    private Connection connection;

    public void init() throws SQLException {
        MemorySection section = (MemorySection) VicnixCore.getInstance().getConfig().get("mysql");

        if (section == null) {
            return;
        }

        for (String key : section.getKeys(false)) {
            this.data.put(key, section.get(key));
        }

        try {
            Class.forName("com.mysql.jdbc.Driver");

            intentConnect(this.data);

            this.createTables();
        } catch (SQLException e) {
            if (e.getErrorCode() == 1049) {
                createDatabase(this.data);

                this.init();

                return;
            }

            throw new SQLException(e.getMessage(), e);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Can't load JDBC Driver", e);
        }
    }

    public void setPlayerIcon(String name, VicnixIcon icon) {
        if (connection == null) {
            VicnixCore.getInstance().getLogger().warning("Mysql not was initialized");

            return;
        }

        UUID uuid = getPlayerUniqueId(name);

        if (uuid == null) {
            return;
        }

        if (VicnixCore.iconList.containsKey(uuid)) {
            VicnixCore.iconList.put(uuid, icon);

            VicnixCore.updateTabEntry(uuid);
        }

        setPlayerIcon(uuid, icon);
    }

    public void setPlayerIcon(UUID uuid, VicnixIcon icon) {
        if (connection == null) {
            VicnixCore.getInstance().getLogger().warning("Mysql not was initialized");

            return;
        }

        try {
            PreparedStatement preparedStatement;

            if (this.getPlayerIconId(uuid) == -1) {
                preparedStatement = this.connection.prepareStatement("INSERT INTO user_icons(uuid, iconId) VALUES (?, ?)");

                preparedStatement.setString(1, uuid.toString());
                preparedStatement.setInt(2, icon.getId());
            } else {
                preparedStatement = this.connection.prepareStatement("UPDATE user_icons SET iconId = ? WHERE uuid = ?");

                preparedStatement.setInt(1, icon.getId());
                preparedStatement.setString(2, uuid.toString());
            }

            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getPlayerIconId(UUID uuid) {
        if (connection == null) {
            VicnixCore.getInstance().getLogger().warning("Mysql not was initialized");

            return -1;
        }

        int rowId = -1;

        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement("SELECT * FROM user_icons WHERE uuid = ?");

            preparedStatement.setString(1, uuid.toString());

            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()) {
                rowId = rs.getInt("iconId");
            }

            rs.close();
            preparedStatement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return rowId;
    }

    public UUID getPlayerUniqueId(String name) {
        if (connection == null) {
            VicnixCore.getInstance().getLogger().warning("Mysql not was initialized");

            return null;
        }

        UUID uuid = null;

        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement("SELECT * FROM users WHERE username = ?");

            preparedStatement.setString(1, name);

            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()) {
                uuid = UUID.fromString(rs.getString("uuid"));
            }

            rs.close();
            preparedStatement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return uuid;
    }

    public String getPlayerName(UUID uuid) {
        if (connection == null) {
            VicnixCore.getInstance().getLogger().warning("Mysql not was initialized");

            return null;
        }

        String name = null;

        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement("SELECT * FROM users WHERE uuid = ?");

            preparedStatement.setString(1, uuid.toString());

            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()) {
                name = rs.getString("username");
            }

            rs.close();
            preparedStatement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return name;
    }

    public void createUser(String name, UUID uuid) {
        if (connection == null) {
            VicnixCore.getInstance().getLogger().warning("Mysql not was initialized");

            return;
        }

        try {
            PreparedStatement preparedStatement;

            if (this.getPlayerName(uuid) == null) {
                preparedStatement = this.connection.prepareStatement("INSERT INTO users (username, uuid, lastOnline) VALUES (?, ?, ?)");

                preparedStatement.setString(1, name);
                preparedStatement.setString(2, uuid.toString());
                preparedStatement.setString(3, "");
            } else {
                preparedStatement = this.connection.prepareStatement("UPDATE users SET username = ? WHERE uuid = ?");

                preparedStatement.setString(1, name);
                preparedStatement.setString(2, uuid.toString());
            }

            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void intentConnect(Map<String, Object> data) throws SQLException {
        if (data.isEmpty()) {
            return;
        }

        Properties properties = new Properties();

        properties.setProperty("user", (String) data.get("user"));
        properties.setProperty("password", (String) data.get("password"));
        properties.setProperty("autoReconnect", "true");
        properties.setProperty("verifyServerCertificate", "false");
        properties.setProperty("useSSL", "false");
        properties.setProperty("requireSSL", "false");

        this.connection = DriverManager.getConnection("jdbc:mysql://" + data.get("host") + ":" + data.get("port") + "/" + data.get("dbname"), properties);
    }

    private void createDatabase(Map<String, Object> data) throws SQLException {
        this.connection = DriverManager.getConnection("jdbc:mysql://" + data.get("host") + ":" + data.get("port"),  (String) data.get("user"), (String) data.get("password"));

        Statement statement = this.connection.createStatement();

        statement.executeUpdate("CREATE DATABASE " + data.get("dbname"));

        this.connection.close();

        this.connection = null;
    }

    private void createTables() {
        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement("CREATE TABLE IF NOT EXISTS user_icons(row_id INT AUTO_INCREMENT PRIMARY KEY, uuid TEXT, iconId VARCHAR(30))");

            preparedStatement.executeUpdate();
            preparedStatement.close();

            preparedStatement = this.connection.prepareStatement("CREATE TABLE IF NOT EXISTS users(rowId INT AUTO_INCREMENT PRIMARY KEY, username VARCHAR(16), uuid TEXT, lastOnline TEXT)");

            preparedStatement.executeUpdate();
            preparedStatement.close();

            preparedStatement = this.connection.prepareStatement("CREATE TABLE IF NOT EXISTS user_colors(row_id INT AUTO_INCREMENT PRIMARY KEY, uuid TEXT, color VARCHAR(16))");

            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}