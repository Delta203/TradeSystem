package de.tradesystem.delta203.spigot;

import de.tradesystem.delta203.spigot.commands.Commands;
import de.tradesystem.delta203.spigot.files.FileManager;
import de.tradesystem.delta203.spigot.listners.Click;
import de.tradesystem.delta203.spigot.listners.Close;
import de.tradesystem.delta203.spigot.listners.Quit;
import de.tradesystem.delta203.spigot.trade.TradeManager;
import de.tradesystem.delta203.spigot.utils.Interface;
import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class TradeSystem extends JavaPlugin {

  public static TradeSystem plugin;
  public static String prefix = "";
  public static Configuration config;
  public static Configuration messages;
  public static TradeManager tradeManager;
  public static Interface inter;

  @Override
  public void onEnable() {
    plugin = this;
    FileManager configYml = new FileManager("config.yml");
    configYml.create();
    configYml.load();
    config = configYml.get();
    FileManager messagesYml = new FileManager("messages.yml");
    messagesYml.create();
    messagesYml.load();
    messages = messagesYml.get();

    prefix = messages.getString("prefix");
    tradeManager = new TradeManager();
    if (config.getBoolean("interface.enabled")) {
      Interface.Type type =
          Interface.Type.valueOf(
              Objects.requireNonNull(config.getString("interface.type")).toUpperCase());
      inter = new Interface(type);
    }

    Objects.requireNonNull(getCommand("trade")).setExecutor(new Commands());
    Bukkit.getPluginManager().registerEvents(new Click(), this);
    Bukkit.getPluginManager().registerEvents(new Close(), this);
    Bukkit.getPluginManager().registerEvents(new Quit(), this);

    Bukkit.getConsoleSender()
        .sendMessage(prefix + Objects.requireNonNull(messages.getString("loaded")));
  }
}
