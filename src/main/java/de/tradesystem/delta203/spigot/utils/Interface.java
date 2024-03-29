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
    FILE
  }

  private enum DataType {
    INTEGER,
    DOUBLE
  }

  private final Type type;
  private DataType dataType;
  private MySQlManager mysql;

  public Interface(Type type) {
    this.type = type;
    dataType = DataType.INTEGER;
    if (Objects.equals(TradeSystem.config.getString("interface.datatype"), "double")) {
      dataType = DataType.DOUBLE;
    }
    if (type == Type.MYSQL) registerMySQl();
    else if (type == Type.FILE) {
      Bukkit.getConsoleSender()
          .sendMessage(TradeSystem.prefix + TradeSystem.messages.getString("file_loaded"));
    }
    Bukkit.getConsoleSender()
        .sendMessage(
            TradeSystem.prefix
                + Objects.requireNonNull(TradeSystem.messages.getString("data_type_loaded"))
                    .replace("%datatype%", dataType.name()));
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

  private FileManager getPlayerConfig(Player p) {
    String path = TradeSystem.config.getString("interface.file.path");
    String file =
        Objects.requireNonNull(TradeSystem.config.getString("interface.file.file"))
            .replace("%uuid%", p.getUniqueId().toString())
            .replace("%player%", p.getName());
    FileManager playerConfig = new FileManager(path, file);
    playerConfig.create();
    playerConfig.load();
    return playerConfig;
  }

  public double getCoins(Player p) {
    switch (type) {
      case MYSQL:
        {
          String nameTable = TradeSystem.config.getString("interface.mysql.table.name");
          String uuidTable = TradeSystem.config.getString("interface.mysql.table.columns.uuid");
          String coinsTable = TradeSystem.config.getString("interface.mysql.table.columns.coins");
          try {
            PreparedStatement ps =
                mysql.connection.prepareStatement(
                    "SELECT " + coinsTable + " FROM " + nameTable + " WHERE " + uuidTable + " = ?");
            ps.setString(1, p.getUniqueId().toString());
            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getDouble(coinsTable);
          } catch (SQLException e) {
            e.printStackTrace();
          }
          break;
        }
      case FILE:
        {
          String item =
              Objects.requireNonNull(TradeSystem.config.getString("interface.file.item"))
                  .replace("%uuid%", p.getUniqueId().toString())
                  .replace("%player%", p.getName());
          FileManager fileYml = getPlayerConfig(p);
          if (fileYml.get().get(item) instanceof String) {
            // Some plugins store money as string as well
            return Double.parseDouble(Objects.requireNonNull(fileYml.get().getString(item)));
          } else {
            return fileYml.get().getDouble(item);
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
          String coinsTable = TradeSystem.config.getString("interface.mysql.table.columns.coins");
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
            if (dataType == DataType.DOUBLE) ps.setDouble(1, getCoins(p) + coins);
            else ps.setInt(1, (int) (getCoins(p) + coins));
            ps.setString(2, p.getUniqueId().toString());
            ps.executeUpdate();
          } catch (SQLException e) {
            e.printStackTrace();
          }
          break;
        }
      case FILE:
        {
          String command =
              Objects.requireNonNull(TradeSystem.config.getString("interface.file.command.give"))
                  .replace("%uuid%", p.getUniqueId().toString())
                  .replace("%player%", p.getName())
                  .replace("%coins%", String.valueOf(coins))
                  .replace(
                      "%COINS%",
                      String.valueOf(
                          dataType == DataType.DOUBLE
                              ? getCoins(p) + coins
                              : (int) getCoins(p) + coins));
          if (coins < 0) {
            coins = coins * (-1);
            command =
                Objects.requireNonNull(TradeSystem.config.getString("interface.file.command.take"))
                    .replace("%uuid%", p.getUniqueId().toString())
                    .replace("%player%", p.getName())
                    .replace("%coins%", String.valueOf(coins))
                    .replace(
                        "%COINS%",
                        String.valueOf(
                            dataType == DataType.DOUBLE
                                ? getCoins(p) - coins
                                : (int) getCoins(p) - coins));
          }
          Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
          break;
        }
    }
  }
}
