package de.tradesystem.delta203.spigot.listners;

import de.tradesystem.delta203.spigot.TradeSystem;
import de.tradesystem.delta203.spigot.trade.Trade;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;

public class Click implements Listener {

  @EventHandler
  public void onClick(InventoryClickEvent e) {
    Player p = (Player) e.getWhoClicked();
    if (!e.getView().getTitle().equals(TradeSystem.configYml.get().getString("title"))) return;
    e.setCancelled(true);
    Trade trade = TradeSystem.tradeManager.getTrade(p);
    if (trade == null) {
      e.getView().close();
      return;
    }
    // valid
    if (e.getCurrentItem() == null) return;
    if (trade.getValidSlots().contains(e.getRawSlot())) {
      trade.removeItem(p, e.getRawSlot(), e.getCurrentItem());
    } else {
      if (e.getClickedInventory() == null) return;
      if (e.getClickedInventory().getType() != InventoryType.PLAYER) return;
      if (!trade.addItem(p, e.getSlot(), e.getCurrentItem())) {
        p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 1);
      }
    }
  }
}
