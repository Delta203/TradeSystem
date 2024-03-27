package de.tradesystem.delta203.spigot.utils;

import de.tradesystem.delta203.spigot.TradeSystem;
import de.tradesystem.delta203.spigot.files.FileManager;
import de.tradesystem.delta203.spigot.mysql.MySQlManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Interface {

  public enum Type {
    MYSQL,
    CONFIG
  }

  private final Type type;
  private MySQlManager mysql;
  private FileManager configYml;

  public Interface(Type type) {
    this.type = type;
    if (type == Type.MYSQL) registerMySQl();
    else if (type == Type.CONFIG) registerConfig();
  }

  private void registerMySQl() {
    String url = TradeSystem.config.getString("interface.mysql.url");
    int port = TradeSystem.config.getInt("interface.mysql.port");
    String database = TradeSystem.config.getString("interface.mysql.database");
    String user = TradeSystem.config.getString("interface.mysql.user");
    String password = TradeSystem.config.getString("interface.mysql.password");
    mysql = new MySQlManager(url, port, database, user, password);
    mysql.connect();
  }

  private void registerConfig() {
    configYml =
        new FileManager(
            TradeSystem.config.getString("interface.config.path"),
            TradeSystem.config.getString("interface.config.file"));
    configYml.create();
    Bukkit.getConsoleSender()
        .sendMessage(TradeSystem.prefix + TradeSystem.messages.getString("file_loaded"));
  }

  public int getCoins(Player p) {
    if (type == Type.MYSQL) {
      String nameTable = TradeSystem.config.getString("interface.mysql.table.name");
      String uuidTable = TradeSystem.config.getString("interface.mysql.table.columns.uuid");
      String coinsTable = TradeSystem.config.getString("interface.mysql.table..columns.coins");
      try {
        PreparedStatement ps =
            mysql.connection.prepareStatement(
                "SELECT " + coinsTable + " FROM " + nameTable + " WHERE " + uuidTable + " = ?");
        ps.setString(1, p.getUniqueId().toString());
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
          return rs.getInt(coinsTable);
        }
      } catch (SQLException e) {
        e.printStackTrace();
      }
    } else if (type == Type.CONFIG) {
      String item =
          Objects.requireNonNull(TradeSystem.config.getString("interface.config.item"))
              .replace("%uuid%", p.getUniqueId().toString());
      configYml.load();
      return configYml.get().getInt(item);
    }
    return 0;
  }

  public void setCoins(Player p, int coins) {
    if (type == Type.MYSQL) {
      String nameTable = TradeSystem.config.getString("interface.mysql.table.name");
      String uuidTable = TradeSystem.config.getString("interface.mysql.table.columns.uuid");
      String coinsTable = TradeSystem.config.getString("interface.mysql.table..columns.coins");
      try {
        PreparedStatement ps =
            mysql.connection.prepareStatement(
                "UPDATE " + nameTable + " SET " + coinsTable + " = ? WHERE " + uuidTable + " = ?");
        ps.setInt(1, coins);
        ps.setString(2, p.getUniqueId().toString());
        ps.executeUpdate();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    } else if (type == Type.CONFIG) {
      String item =
          Objects.requireNonNull(TradeSystem.config.getString("interface.config.item"))
              .replace("%uuid%", p.getUniqueId().toString());
      configYml.load();
      configYml.get().set(item, coins);
      configYml.save();
    }
  }
}
