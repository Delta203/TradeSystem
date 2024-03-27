package de.tradesystem.delta203.spigot.trade;

import java.util.ArrayList;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class TradePlayer {

  private final Player p;
  private Trade.State state;
  private final ArrayList<ItemStack> items;
  private int coins;
  private int value;

  public TradePlayer(Player p) {
    this.p = p;
    state = Trade.State.UNFINISHED;
    items = new ArrayList<>();
    coins = 0;
    value = 1;
  }

  public Player getPlayer() {
    return p;
  }

  public Trade.State getState() {
    return state;
  }

  public void setState(Trade.State state) {
    this.state = state;
  }

  public ArrayList<ItemStack> getItems() {
    return items;
  }

  public int getCoins() {
    return coins;
  }

  public void setCoins(int coins) {
    this.coins = coins;
  }

  public int getValue() {
    return value;
  }

  public void setValue(int value) {
    this.value = value;
  }

  public int amountOfEmptySlots() {
    int result = 0;
    for (int i = 0; i < 36; i++) {
      if (p.getInventory().getItem(i) == null) {
        result++;
      }
    }
    return result;
  }
}
