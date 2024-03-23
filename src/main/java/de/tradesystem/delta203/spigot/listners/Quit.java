package de.tradesystem.delta203.spigot.listners;

import de.tradesystem.delta203.spigot.TradeSystem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class Quit implements Listener {

  @EventHandler
  public void onQuit(PlayerQuitEvent e) {
    act(e.getPlayer());
  }

  @EventHandler
  public void onKick(PlayerKickEvent e) {
    act(e.getPlayer());
  }

  private void act(Player p) {
    TradeSystem.tradeManager.unregisterInvite(p);
    TradeSystem.tradeManager.unregisterTrade(p);
  }
}
