package de.tradesystem.delta203.spigot.listners;

import de.tradesystem.delta203.spigot.TradeSystem;
import de.tradesystem.delta203.spigot.trade.Trade;
import java.util.Objects;

import de.tradesystem.delta203.spigot.trade.TradePlayer;
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
    if (!e.getView().getTitle().equals(TradeSystem.config.getString("title"))) return;
    e.setCancelled(true);
    Trade trade = TradeSystem.tradeManager.getTrade(p);
    if (trade == null) {
      e.getView().close();
      return;
    }
    // valid
    TradePlayer tp = trade.getPlayer(p);
    if (e.getCurrentItem() == null) return;
    if (e.getCurrentItem().hasItemMeta()
        && Objects.requireNonNull(e.getCurrentItem().getItemMeta())
            .getDisplayName()
            .equals(
                Objects.requireNonNull(TradeSystem.config.getString("coins.name"))
                    .replace("%coins%", String.valueOf(tp.getCoins())))) {
      switch (e.getClick()) {
        case LEFT:
          if (tp.getCoins() + tp.getValue() <= 10000000) { // limit 10m
            trade.setCoins(tp, tp.getCoins() + tp.getValue());
          }
          break;
        case RIGHT:
          if (tp.getCoins() - tp.getValue() >= 0) {
            trade.setCoins(tp, tp.getCoins() - tp.getValue());
          }
          break;
        case SHIFT_LEFT:
          if (tp.getValue() * 10 <= 100000) { // limit 100k
            trade.setValue(tp, tp.getValue() * 10);
          }
          break;
        case SHIFT_RIGHT:
          if (tp.getValue() / 10 >= 1) {
            trade.setValue(tp, tp.getValue() / 10);
          }
          break;
      }
      return;
    } else if (e.getCurrentItem().hasItemMeta()
        && Objects.requireNonNull(e.getCurrentItem().getItemMeta())
            .getDisplayName()
            .equals(TradeSystem.config.getString("button.confirm.name"))) {
      trade.updateState(tp, Trade.State.PROCESSING);
      return;
    } else if (e.getCurrentItem().hasItemMeta()
        && Objects.requireNonNull(e.getCurrentItem().getItemMeta())
            .getDisplayName()
            .equals(TradeSystem.config.getString("button.finish.name"))) {
      trade.updateState(tp, Trade.State.DONE);
      return;
    }

    // trade
    if (trade.getValidSlots().contains(e.getRawSlot())) {
      trade.removeItem(tp, e.getRawSlot(), e.getCurrentItem());
    } else {
      if (e.getClickedInventory() == null) return;
      if (e.getClickedInventory().getType() != InventoryType.PLAYER) return;
      if (!trade.addItem(tp, e.getSlot(), e.getCurrentItem())) {
        tp.getPlayer()
            .playSound(
                tp.getPlayer().getLocation(),
                Sound.valueOf(TradeSystem.config.getString("sound.cancelled")),
                1,
                1);
      }
    }
  }
}
