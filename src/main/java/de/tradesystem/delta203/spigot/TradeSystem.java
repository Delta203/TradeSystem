package de.tradesystem.delta203.spigot;

import de.tradesystem.delta203.spigot.commands.Commands;
import de.tradesystem.delta203.spigot.files.FileManager;
import de.tradesystem.delta203.spigot.listners.Click;
import de.tradesystem.delta203.spigot.listners.Close;
import de.tradesystem.delta203.spigot.trade.TradeManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class TradeSystem extends JavaPlugin {

  public static TradeSystem plugin;
  public static String prefix = "";
  public static FileManager configYml;
  public static FileManager messagesYml;
  public static TradeManager tradeManager;

  @Override
  public void onEnable() {
    plugin = this;
    configYml = new FileManager("config.yml");
    configYml.create();
    configYml.load();
    messagesYml = new FileManager("messages.yml");
    messagesYml.create();
    messagesYml.load();
    prefix = messagesYml.get().getString("prefix");
    tradeManager = new TradeManager();

    Objects.requireNonNull(getCommand("trade")).setExecutor(new Commands());
    Bukkit.getPluginManager().registerEvents(new Click(), this);
    Bukkit.getPluginManager().registerEvents(new Close(), this);

    Bukkit.getConsoleSender()
        .sendMessage(prefix + Objects.requireNonNull(messagesYml.get().getString("loaded")));
  }
}
