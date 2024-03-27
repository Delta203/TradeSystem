package de.tradesystem.delta203.spigot.mysql;

import de.tradesystem.delta203.spigot.TradeSystem;
import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * <b>MySQl Manager</b><br>
 * This is content from Spigot Utils <small>(Useful classes for making a spigot plugin)</small>.
 *
 * @see <a href="https://github.com/Delta203/SpigotUtils">Spigot Utils</a>
 * @author Delta203
 * @version 1.0
 */
public class MySQlManager {

  private final String host;
  private final int port;
  private final String database;
  private final String name;
  private final String password;

  /** The connection object for MySQl. */
  public Connection connection;

  /**
   * Register a MySQlManager to create a MySQl connection and operate with a MySQl database.
   *
   * @param host the host of the MySQl server
   * @param port the port of the MySQl server
   * @param database the name of the MySQl database
   * @param name the username for authentication
   * @param password the password for authentication
   */
  public MySQlManager(String host, int port, String database, String name, String password) {
    this.host = host;
    this.port = port;
    this.database = database;
    this.name = name;
    this.password = password;
  }

  /**
   * Checks if the connection to the MySQl database is established.
   *
   * @return the connection is established
   */
  public boolean isConnected() {
    return connection != null;
  }

  /** Connects to the MySQl database using the provided data. */
  public void connect() {
    if (isConnected()) return;
    try {
      connection =
          DriverManager.getConnection(
              "jdbc:mysql://" + host + ":" + port + "/" + database + "?autoReconnect=true",
              name,
              password);
      Bukkit.getConsoleSender()
          .sendMessage(TradeSystem.prefix + TradeSystem.messages.getString("mysql_connected"));
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /** Disconnects from the MySQl database. */
  public void disconnect() {
    if (!isConnected()) return;
    try {
      connection.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}
