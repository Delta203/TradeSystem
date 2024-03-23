package de.tradesystem.delta203.spigot.listners;

import de.tradesystem.delta203.spigot.TradeSystem;
import de.tradesystem.delta203.spigot.trade.Trade;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

public class Close implements Listener {

  @EventHandler
  public void onClose(InventoryCloseEvent e) {
    Player p = (Player) e.getPlayer();
    if (!e.getView().getTitle().equals(TradeSystem.config.getString("title"))) return;
    Trade trade = TradeSystem.tradeManager.getTrade(p);
    if (trade == null) return;
    // valid
    for (ItemStack item : trade.getItems(p)) {
      p.getInventory().addItem(item);
    }
    p.sendMessage(TradeSystem.prefix + TradeSystem.messages.getString("trade.cancelled"));
    p.playSound(
        p.getLocation(), Sound.valueOf(TradeSystem.config.getString("sound.cancelled")), 1, 1);
    TradeSystem.tradeManager.unregisterTrade(p);
    Player target = trade.getTarget(p);
    target.getOpenInventory().close();
  }
}
