package de.tradesystem.delta203.spigot.trade;

import de.tradesystem.delta203.spigot.TradeSystem;
import de.tradesystem.delta203.spigot.utils.ItemBuilder;
import java.util.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class Trade {

  public enum State {
    UNFINISHED,
    PROCESSING,
    DONE
  }

  private final TradePlayer host;
  private final TradePlayer target;

  public Trade(TradePlayer host, TradePlayer target) {
    this.host = host;
    this.target = target;
    createInventory(host);
    createInventory(target);
  }

  public TradePlayer getPlayer(Player p) {
    return p == host.getPlayer() ? host : target;
  }

  public TradePlayer getTarget(TradePlayer tp) {
    return tp == host ? target : host;
  }

  private void createInventory(TradePlayer tp) {
    TradePlayer target = getTarget(tp);
    Inventory inv =
        Bukkit.createInventory(
            tp.getPlayer(), 54, Objects.requireNonNull(TradeSystem.config.getString("title")));
    inv.setItem(
        0,
        new ItemBuilder(
                new ItemStack(Material.PLAYER_HEAD),
                Objects.requireNonNull(TradeSystem.config.getString("own_head"))
                    .replace("%player%", tp.getPlayer().getName()),
                null,
                tp.getPlayer().getName())
            .getSkullItem());
    inv.setItem(
        1,
        new ItemBuilder(
                new ItemStack(Material.valueOf(TradeSystem.config.getString("placeholder"))), " ")
            .getItem());
    inv.setItem(
        2,
        new ItemBuilder(
                new ItemStack(Material.valueOf(TradeSystem.config.getString("placeholder"))), " ")
            .getItem());
    inv.setItem(
        4,
        new ItemBuilder(new ItemStack(Material.valueOf(TradeSystem.config.getString("frame"))), " ")
            .getItem());
    if (TradeSystem.inter != null) {
      inv.setItem(3, getCoinsItem(tp));
      inv.setItem(
          5,
          new ItemBuilder(
                  new ItemStack(Material.valueOf(TradeSystem.config.getString("coins.material"))),
                  Objects.requireNonNull(TradeSystem.config.getString("coins.name_target"))
                      .replace("%coins%", String.valueOf(target.getCoins())))
              .getItem());
    } else {
      inv.setItem(
          3,
          new ItemBuilder(
                  new ItemStack(Material.valueOf(TradeSystem.config.getString("placeholder"))), " ")
              .getItem());
      inv.setItem(
          5,
          new ItemBuilder(
                  new ItemStack(Material.valueOf(TradeSystem.config.getString("placeholder"))), " ")
              .getItem());
    }
    inv.setItem(
        6,
        new ItemBuilder(
                new ItemStack(Material.valueOf(TradeSystem.config.getString("placeholder"))), " ")
            .getItem());
    inv.setItem(
        7,
        new ItemBuilder(
                new ItemStack(Material.valueOf(TradeSystem.config.getString("placeholder"))), " ")
            .getItem());
    inv.setItem(
        8,
        new ItemBuilder(
                new ItemStack(Material.PLAYER_HEAD),
                "§7" + target.getPlayer().getName(),
                null,
                target.getPlayer().getName())
            .getSkullItem());
    for (int i = 9; i < 18; i++) {
      inv.setItem(
          i,
          new ItemBuilder(
                  new ItemStack(Material.valueOf(TradeSystem.config.getString("frame"))), " ")
              .getItem());
    }
    for (int i = 18; i < 27; i++) {
      inv.setItem(
          i,
          new ItemBuilder(
                  new ItemStack(
                      Material.valueOf(TradeSystem.config.getString("unfinished.material"))),
                  TradeSystem.config.getString("unfinished.name"))
              .getItem());
    }
    inv.setItem(
        22,
        new ItemBuilder(
                new ItemStack(
                    Material.valueOf(TradeSystem.config.getString("button.confirm.material"))),
                TradeSystem.config.getString("button.confirm.name"))
            .getItem());
    inv.setItem(
        31,
        new ItemBuilder(new ItemStack(Material.valueOf(TradeSystem.config.getString("frame"))), " ")
            .getItem());
    inv.setItem(
        40,
        new ItemBuilder(new ItemStack(Material.valueOf(TradeSystem.config.getString("frame"))), " ")
            .getItem());
    inv.setItem(
        49,
        new ItemBuilder(new ItemStack(Material.valueOf(TradeSystem.config.getString("frame"))), " ")
            .getItem());
    tp.getPlayer().openInventory(inv);
  }

  private ItemStack getCoinsItem(TradePlayer tp) {
    return new ItemBuilder(
            new ItemStack(Material.valueOf(TradeSystem.config.getString("coins.material"))),
            Objects.requireNonNull(TradeSystem.config.getString("coins.name"))
                .replace("%coins%", String.valueOf(tp.getCoins())),
            Objects.requireNonNull(TradeSystem.config.getString("coins.lore"))
                .replace("%value%", String.valueOf(tp.getValue()))
                .replace("%balance%", String.valueOf(TradeSystem.inter.getCoins(tp.getPlayer()))))
        .getItem();
  }

  private void updateCoinsItem(TradePlayer tp) {
    TradePlayer target = getTarget(tp);
    updateState(tp, State.UNFINISHED);
    updateState(target, State.UNFINISHED);
    tp.getPlayer().getOpenInventory().setItem(3, getCoinsItem(tp));
    tp.getPlayer()
        .playSound(
            tp.getPlayer().getLocation(),
            Sound.valueOf(TradeSystem.config.getString("sound.click")),
            1,
            2);
    target
        .getPlayer()
        .getOpenInventory()
        .setItem(
            5,
            new ItemBuilder(
                    new ItemStack(Material.valueOf(TradeSystem.config.getString("coins.material"))),
                    Objects.requireNonNull(TradeSystem.config.getString("coins.name_target"))
                        .replace("%coins%", String.valueOf(tp.getCoins())))
                .getItem());
  }

  public List<Integer> getValidSlots() {
    return Arrays.asList(27, 28, 29, 30, 36, 37, 38, 39, 45, 46, 47, 48);
  }

  private int getTargetSlot(int i) {
    if (i == 27) return 32;
    else if (i == 28) return 33;
    else if (i == 29) return 34;
    else if (i == 30) return 35;
    else if (i == 36) return 41;
    else if (i == 37) return 42;
    else if (i == 38) return 43;
    else if (i == 39) return 44;
    else if (i == 45) return 50;
    else if (i == 46) return 51;
    else if (i == 47) return 52;
    else if (i == 48) return 53;
    return -1;
  }

  public boolean addItem(TradePlayer tp, int slot, ItemStack item) {
    for (int i : getValidSlots()) {
      if (tp.getPlayer().getOpenInventory().getItem(i) == null) {
        TradePlayer target = getTarget(tp);
        updateState(tp, State.UNFINISHED);
        updateState(target, State.UNFINISHED);
        tp.getPlayer().getOpenInventory().setItem(i, item);
        tp.getPlayer().getInventory().setItem(slot, new ItemStack(Material.AIR));
        tp.getItems().add(item);
        target.getPlayer().getOpenInventory().setItem(getTargetSlot(i), item);
        return true;
      }
    }
    return false;
  }

  public void removeItem(TradePlayer tp, int slot, ItemStack item) {
    TradePlayer target = getTarget(tp);
    updateState(tp, State.UNFINISHED);
    updateState(target, State.UNFINISHED);
    tp.getPlayer().getInventory().addItem(item);
    tp.getPlayer().getOpenInventory().setItem(slot, new ItemStack(Material.AIR));
    tp.getItems().remove(item);
    target.getPlayer().getOpenInventory().setItem(getTargetSlot(slot), new ItemStack(Material.AIR));
  }

  public void setCoins(TradePlayer tp, int coins) {
    tp.setCoins(coins);
    updateCoinsItem(tp);
  }

  public void setValue(TradePlayer tp, int value) {
    tp.setValue(value);
    updateCoinsItem(tp);
  }

  public void updateState(TradePlayer tp, State state) {
    if (tp.getState() == state) return;
    tp.setState(state);
    tp.getPlayer()
        .playSound(
            tp.getPlayer().getLocation(),
            Sound.valueOf(TradeSystem.config.getString("sound.event")),
            1,
            1);
    // update state panes
    for (int i = 18; i < 22; i++) {
      tp.getPlayer()
          .getOpenInventory()
          .setItem(
              i,
              new ItemBuilder(
                      new ItemStack(
                          Material.valueOf(
                              TradeSystem.config.getString(
                                  state.toString().toLowerCase() + ".material"))),
                      TradeSystem.config.getString(state.toString().toLowerCase() + ".name"))
                  .getItem());
    }
    TradePlayer target = getTarget(tp);
    for (int i = 23; i < 27; i++) {
      target
          .getPlayer()
          .getOpenInventory()
          .setItem(
              i,
              new ItemBuilder(
                      new ItemStack(
                          Material.valueOf(
                              TradeSystem.config.getString(
                                  state.toString().toLowerCase() + ".material"))),
                      TradeSystem.config.getString(state.toString().toLowerCase() + ".name"))
                  .getItem());
    }
    // update button
    if (state == State.UNFINISHED) {
      tp.getPlayer()
          .getOpenInventory()
          .setItem(
              22,
              new ItemBuilder(
                      new ItemStack(
                          Material.valueOf(
                              TradeSystem.config.getString("button.confirm.material"))),
                      TradeSystem.config.getString("button.confirm.name"))
                  .getItem());
    } else if (state == State.PROCESSING) {
      tp.getPlayer()
          .getOpenInventory()
          .setItem(
              22,
              new ItemBuilder(
                      new ItemStack(
                          Material.valueOf(TradeSystem.config.getString("button.finish.material"))),
                      TradeSystem.config.getString("button.finish.name"))
                  .getItem());
    } else if (state == State.DONE) {
      tp.getPlayer()
          .getOpenInventory()
          .setItem(
              22,
              new ItemBuilder(
                      new ItemStack(Material.valueOf(TradeSystem.config.getString("frame"))), " ")
                  .getItem());
      finishTrade();
    }
  }

  private void finishTrade() {
    if (host.getState() != State.DONE || target.getState() != State.DONE) return;
    // check full inventory
    if (host.getItems().size() > target.amountOfEmptySlots()) {
      target
          .getPlayer()
          .sendMessage(TradeSystem.prefix + TradeSystem.messages.getString("trade.full_inventory"));
      target.getPlayer().closeInventory();
      return;
    }
    if (target.getItems().size() > host.amountOfEmptySlots()) {
      host.getPlayer()
          .sendMessage(TradeSystem.prefix + TradeSystem.messages.getString("trade.full_inventory"));
      host.getPlayer().closeInventory();
      return;
    }
    // check enough coins
    if (host.getCoins() > 0) {
      if (TradeSystem.inter.getCoins(host.getPlayer()) - host.getCoins() < 0) {
        host.getPlayer()
            .sendMessage(TradeSystem.prefix + TradeSystem.messages.getString("trade.no_coins"));
        host.getPlayer().closeInventory();
        return;
      }
    }
    if (target.getCoins() > 0) {
      if (TradeSystem.inter.getCoins(target.getPlayer()) - target.getCoins() < 0) {
        target
            .getPlayer()
            .sendMessage(TradeSystem.prefix + TradeSystem.messages.getString("trade.no_coins"));
        target.getPlayer().closeInventory();
        return;
      }
    }
    // items
    for (ItemStack item : host.getItems()) {
      target.getPlayer().getInventory().addItem(item);
    }
    for (ItemStack item : target.getItems()) {
      host.getPlayer().getInventory().addItem(item);
    }
    // optional coins
    if (host.getCoins() > 0) {
      TradeSystem.inter.addCoins(host.getPlayer(), -host.getCoins());
      TradeSystem.inter.addCoins(target.getPlayer(), host.getCoins());
    }
    if (target.getCoins() > 0) {
      TradeSystem.inter.addCoins(target.getPlayer(), -target.getCoins());
      TradeSystem.inter.addCoins(host.getPlayer(), target.getCoins());
    }
    TradeSystem.tradeManager.unregisterTrade(host.getPlayer());
    TradeSystem.tradeManager.unregisterTrade(target.getPlayer());
    host.getPlayer().closeInventory();
    target.getPlayer().closeInventory();

    host.getPlayer()
        .sendMessage(
            TradeSystem.prefix
                + Objects.requireNonNull(TradeSystem.messages.getString("trade.success"))
                    .replace("%player%", target.getPlayer().getName()));
    host.getPlayer()
        .playSound(
            host.getPlayer().getLocation(),
            Sound.valueOf(TradeSystem.config.getString("sound.success")),
            1,
            1);
    target
        .getPlayer()
        .sendMessage(
            TradeSystem.prefix
                + Objects.requireNonNull(TradeSystem.messages.getString("trade.success"))
                    .replace("%player%", host.getPlayer().getName()));
    target
        .getPlayer()
        .playSound(
            target.getPlayer().getLocation(),
            Sound.valueOf(TradeSystem.config.getString("sound.success")),
            1,
            1);
  }
}
