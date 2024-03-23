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

  private final Player host;
  private final Player target;
  private final HashMap<Player, State> state;
  private final HashMap<Player, ArrayList<ItemStack>> items;
  private final HashMap<Player, Integer> coins;
  private final HashMap<Player, Integer> value;

  public Trade(Player host, Player target) {
    this.host = host;
    this.target = target;
    state = new HashMap<>();
    state.put(host, State.UNFINISHED);
    state.put(target, State.UNFINISHED);
    items = new HashMap<>();
    items.put(host, new ArrayList<>());
    items.put(target, new ArrayList<>());
    coins = new HashMap<>();
    coins.put(host, 0);
    coins.put(target, 0);
    value = new HashMap<>();
    value.put(host, 1);
    value.put(target, 1);
  }

  public Inventory createInventory(Player p) {
    Player t = getTarget(p);
    Inventory inv =
        Bukkit.createInventory(
            p, 54, Objects.requireNonNull(TradeSystem.config.getString("title")));
    inv.setItem(
        0,
        new ItemBuilder(
                new ItemStack(Material.PLAYER_HEAD),
                Objects.requireNonNull(TradeSystem.config.getString("own_head"))
                    .replace("%player%", p.getName()),
                null,
                p.getName())
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
    if (TradeSystem.config.getBoolean("coins.enabled")) {
      inv.setItem(3, getCoinsItem(p));
      inv.setItem(
          5,
          new ItemBuilder(
                  new ItemStack(Material.valueOf(TradeSystem.config.getString("coins.material"))),
                  Objects.requireNonNull(TradeSystem.config.getString("coins.name_target"))
                      .replace("%coins%", String.valueOf(coins.get(t))))
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
        new ItemBuilder(new ItemStack(Material.PLAYER_HEAD), "§7" + t.getName(), null, t.getName())
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
    return inv;
  }

  private ItemStack getCoinsItem(Player p) {
    return new ItemBuilder(
            new ItemStack(Material.valueOf(TradeSystem.config.getString("coins.material"))),
            Objects.requireNonNull(TradeSystem.config.getString("coins.name"))
                .replace("%coins%", String.valueOf(coins.get(p))),
            Objects.requireNonNull(TradeSystem.config.getString("coins.lore"))
                .replace("%value%", String.valueOf(value.get(p))))
        .getItem();
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

  public Player getTarget(Player p) {
    return p == host ? target : host;
  }

  public ArrayList<ItemStack> getItems(Player p) {
    return items.get(p);
  }

  public boolean addItem(Player p, int slot, ItemStack item) {
    for (int i : getValidSlots()) {
      if (p.getOpenInventory().getItem(i) == null) {
        Player target = getTarget(p);
        updateState(p, State.UNFINISHED);
        updateState(target, State.UNFINISHED);
        p.getOpenInventory().setItem(i, item);
        p.getInventory().setItem(slot, new ItemStack(Material.AIR));
        items.get(p).add(item);
        target.getOpenInventory().setItem(getTargetSlot(i), item);
        return true;
      }
    }
    return false;
  }

  public void removeItem(Player p, int slot, ItemStack item) {
    Player target = getTarget(p);
    updateState(p, State.UNFINISHED);
    updateState(target, State.UNFINISHED);
    p.getInventory().addItem(item);
    p.getOpenInventory().setItem(slot, new ItemStack(Material.AIR));
    items.get(p).remove(item);
    target.getOpenInventory().setItem(getTargetSlot(slot), new ItemStack(Material.AIR));
  }

  public int getCoins(Player p) {
    return coins.get(p);
  }

  public void setCoins(Player p, int coins) {
    this.coins.put(p, coins);
    updateCoinsItem(p);
  }

  public int getValue(Player p) {
    return value.get(p);
  }

  public void setValue(Player p, int value) {
    this.value.put(p, value);
    updateCoinsItem(p);
  }

  private void updateCoinsItem(Player p) {
    Player target = getTarget(p);
    updateState(p, State.UNFINISHED);
    updateState(target, State.UNFINISHED);
    p.getOpenInventory().setItem(3, getCoinsItem(p));
    p.playSound(p.getLocation(), Sound.valueOf(TradeSystem.config.getString("sound.click")), 1, 2);
    target
        .getOpenInventory()
        .setItem(
            5,
            new ItemBuilder(
                    new ItemStack(Material.valueOf(TradeSystem.config.getString("coins.material"))),
                    Objects.requireNonNull(TradeSystem.config.getString("coins.name_target"))
                        .replace("%coins%", String.valueOf(coins.get(p))))
                .getItem());
  }

  private State getState(Player p) {
    return state.get(p);
  }

  public void updateState(Player p, State state) {
    if (getState(p) == state) return;
    this.state.put(p, state);
    p.playSound(p.getLocation(), Sound.valueOf(TradeSystem.config.getString("sound.event")), 1, 1);
    // update state panes
    for (int i = 18; i < 22; i++) {
      p.getOpenInventory()
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
    Player target = getTarget(p);
    for (int i = 23; i < 27; i++) {
      target
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
      p.getOpenInventory()
          .setItem(
              22,
              new ItemBuilder(
                      new ItemStack(
                          Material.valueOf(
                              TradeSystem.config.getString("button.confirm.material"))),
                      TradeSystem.config.getString("button.confirm.name"))
                  .getItem());
    } else if (state == State.PROCESSING) {
      p.getOpenInventory()
          .setItem(
              22,
              new ItemBuilder(
                      new ItemStack(
                          Material.valueOf(TradeSystem.config.getString("button.finish.material"))),
                      TradeSystem.config.getString("button.finish.name"))
                  .getItem());
    } else if (state == State.DONE) {
      p.getOpenInventory()
          .setItem(
              22,
              new ItemBuilder(
                      new ItemStack(Material.valueOf(TradeSystem.config.getString("frame"))), " ")
                  .getItem());
      finishTrade();
    }
  }

  private void finishTrade() {
    if (getState(host) != State.DONE || getState(target) != State.DONE) return;
    // check full inventory
    if (getItems(host).size() > amountOfEmptySlots(target)) {
      target.sendMessage(
          TradeSystem.prefix + TradeSystem.messages.getString("trade.full_inventory"));
      target.closeInventory();
      return;
    }
    if (getItems(target).size() > amountOfEmptySlots(host)) {
      host.sendMessage(TradeSystem.prefix + TradeSystem.messages.getString("trade.full_inventory"));
      host.closeInventory();
      return;
    }
    // items
    for (ItemStack item : getItems(host)) {
      target.getInventory().addItem(item);
    }
    for (ItemStack item : getItems(target)) {
      host.getInventory().addItem(item);
    }
    // coins
    if (getCoins(host) > 0) {
      Bukkit.dispatchCommand(
          host,
          Objects.requireNonNull(TradeSystem.config.getString("send_coins"))
              .replace("%player%", target.getName())
              .replace("%amount%", String.valueOf(getCoins(host))));
    }
    if (getCoins(target) > 0) {
      Bukkit.dispatchCommand(
          target,
          Objects.requireNonNull(TradeSystem.config.getString("send_coins"))
              .replace("%player%", host.getName())
              .replace("%amount%", String.valueOf(getCoins(target))));
    }
    TradeSystem.tradeManager.unregisterTrade(host);
    TradeSystem.tradeManager.unregisterTrade(target);
    host.closeInventory();
    target.closeInventory();

    host.sendMessage(
        TradeSystem.prefix
            + Objects.requireNonNull(TradeSystem.messages.getString("trade.success"))
                .replace("%player%", target.getName()));
    host.playSound(
        target.getLocation(), Sound.valueOf(TradeSystem.config.getString("sound.success")), 1, 1);
    target.sendMessage(
        TradeSystem.prefix
            + Objects.requireNonNull(TradeSystem.messages.getString("trade.success"))
                .replace("%player%", host.getName()));
    target.playSound(
        target.getLocation(), Sound.valueOf(TradeSystem.config.getString("sound.success")), 1, 1);
  }

  private int amountOfEmptySlots(Player p) {
    int result = 0;
    for (int i = 0; i < 36; i++) {
      if (p.getInventory().getItem(i) == null) {
        result++;
      }
    }
    return result;
  }
}
