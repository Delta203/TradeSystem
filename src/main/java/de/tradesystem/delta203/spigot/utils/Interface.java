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
    CONFIG,
    ESSENTIALS
  }

  private final Type type;
  private MySQlManager mysql;
  private FileManager configYml;

  public Interface(Type type) {
    this.type = type;
    if (type == Type.MYSQL) registerMySQl();
    else if (type == Type.CONFIG) registerConfig();
    else if (type == Type.ESSENTIALS) {
      Bukkit.getConsoleSender()
          .sendMessage(TradeSystem.prefix + TradeSystem.messages.getString("essentials_loaded"));
    }
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

  private FileManager getPlayerConfig(Player p) {
    String path = TradeSystem.config.getString("interface.essentials.path");
    String file =
        Objects.requireNonNull(TradeSystem.config.getString("interface.essentials.file"))
            .replace("%uuid%", p.getUniqueId().toString());
    FileManager playerConfig = new FileManager(path, file);
    playerConfig.create();
    playerConfig.load();
    return playerConfig;
  }

  public int getCoins(Player p) {
    switch (type) {
      case MYSQL:
        {
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
          break;
        }
      case CONFIG:
        {
          String item =
              Objects.requireNonNull(TradeSystem.config.getString("interface.config.item"))
                  .replace("%uuid%", p.getUniqueId().toString());
          configYml.load();
          return configYml.get().getInt(item);
        }
      case ESSENTIALS:
        {
          String item =
              Objects.requireNonNull(TradeSystem.config.getString("interface.essentials.item"));
          FileManager playerConfig = getPlayerConfig(p);
          if (playerConfig.get().get(item) instanceof String) {
            // Essentials stores money as string
            return Integer.decode(Objects.requireNonNull(playerConfig.get().getString(item)));
          } else {
            return playerConfig.get().getInt(item);
          }
        }
    }
    return 0;
  }

  public void addCoins(Player p, int coins) {
    switch (type) {
      case MYSQL:
        {
          String nameTable = TradeSystem.config.getString("interface.mysql.table.name");
          String uuidTable = TradeSystem.config.getString("interface.mysql.table.columns.uuid");
          String coinsTable = TradeSystem.config.getString("interface.mysql.table..columns.coins");
          try {
            PreparedStatement ps =
                mysql.connection.prepareStatement(
                    "UPDATE "
                        + nameTable
                        + " SET "
                        + coinsTable
                        + " = ? WHERE "
                        + uuidTable
                        + " = ?");
            ps.setInt(1, getCoins(p) + coins);
            ps.setString(2, p.getUniqueId().toString());
            ps.executeUpdate();
          } catch (SQLException e) {
            e.printStackTrace();
          }
          break;
        }
      case CONFIG:
        {
          String item =
              Objects.requireNonNull(TradeSystem.config.getString("interface.config.item"))
                  .replace("%uuid%", p.getUniqueId().toString());
          configYml.load();
          configYml.get().set(item, getCoins(p) + coins);
          configYml.save();
          break;
        }
      case ESSENTIALS:
        {
          String command =
              Objects.requireNonNull(
                      TradeSystem.config.getString("interface.essentials.command.give"))
                  .replace("%uuid%", p.getUniqueId().toString())
                  .replace("%coins%", String.valueOf(coins))
                  .replace("%COINS%", String.valueOf(getCoins(p) + coins));
          if (coins < 0) {
            command =
                Objects.requireNonNull(
                        TradeSystem.config.getString("interface.essentials.command.take"))
                    .replace("%uuid%", p.getUniqueId().toString())
                    .replace("%coins%", String.valueOf(coins))
                    .replace("%COINS%", String.valueOf(getCoins(p) + coins));
          }
          Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
          break;
        }
    }
  }
}
