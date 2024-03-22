package de.tradesystem.delta203.spigot.trade;

import de.tradesystem.delta203.spigot.TradeSystem;
import de.tradesystem.delta203.spigot.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class Trade {

  private final Player host;
  private final Player target;
  private final HashMap<Player, ArrayList<ItemStack>> items;

  public Trade(Player host, Player target) {
    this.host = host;
    this.target = target;
    items = new HashMap<>();
    items.put(host, new ArrayList<>());
    items.put(target, new ArrayList<>());
  }

  public Inventory createInventory(Player p) {
    Player t = p == host ? target : host;
    Inventory inv =
        Bukkit.createInventory(
            p, 54, Objects.requireNonNull(TradeSystem.configYml.get().getString("title")));
    inv.setItem(
        0,
        new ItemBuilder(
                new ItemStack(Material.PLAYER_HEAD),
                Objects.requireNonNull(TradeSystem.configYml.get().getString("own_head"))
                    .replace("%player%", p.getName()),
                null,
                p.getName())
            .getSkullItem());
    inv.setItem(
        1,
        new ItemBuilder(
                new ItemStack(
                    Material.valueOf(TradeSystem.configYml.get().getString("placeholder"))),
                " ")
            .getItem());
    inv.setItem(
        2,
        new ItemBuilder(
                new ItemStack(
                    Material.valueOf(TradeSystem.configYml.get().getString("placeholder"))),
                " ")
            .getItem());
    inv.setItem(
        3,
        new ItemBuilder(
                new ItemStack(
                    Material.valueOf(TradeSystem.configYml.get().getString("coins.material"))),
                TradeSystem.configYml.get().getString("coins.name"),
                TradeSystem.configYml.get().getString("coins.lore"))
            .getItem());
    inv.setItem(
        4,
        new ItemBuilder(
                new ItemStack(Material.valueOf(TradeSystem.configYml.get().getString("frame"))),
                " ")
            .getItem());
    inv.setItem(
        5,
        new ItemBuilder(
                new ItemStack(
                    Material.valueOf(TradeSystem.configYml.get().getString("coins.material"))),
                "§7Coins§8: §7" + p.getName())
            .getItem());
    inv.setItem(
        6,
        new ItemBuilder(
                new ItemStack(
                    Material.valueOf(TradeSystem.configYml.get().getString("placeholder"))),
                " ")
            .getItem());
    inv.setItem(
        7,
        new ItemBuilder(
                new ItemStack(
                    Material.valueOf(TradeSystem.configYml.get().getString("placeholder"))),
                " ")
            .getItem());
    inv.setItem(
        8,
        new ItemBuilder(new ItemStack(Material.PLAYER_HEAD), "§7" + t.getName(), null, t.getName())
            .getSkullItem());
    for (int i = 9; i < 18; i++) {
      inv.setItem(
          i,
          new ItemBuilder(
                  new ItemStack(Material.valueOf(TradeSystem.configYml.get().getString("frame"))),
                  " ")
              .getItem());
    }
    for (int i = 18; i < 27; i++) {
      inv.setItem(
          i,
          new ItemBuilder(
                  new ItemStack(
                      Material.valueOf(
                          TradeSystem.configYml.get().getString("unfinished.material"))),
                  TradeSystem.configYml.get().getString("unfinished.name"))
              .getItem());
    }
    inv.setItem(
        22,
        new ItemBuilder(
                new ItemStack(
                    Material.valueOf(
                        TradeSystem.configYml.get().getString("button.confirm.material"))),
                TradeSystem.configYml.get().getString("button.confirm.name"))
            .getItem());
    inv.setItem(
        31,
        new ItemBuilder(
                new ItemStack(Material.valueOf(TradeSystem.configYml.get().getString("frame"))),
                " ")
            .getItem());
    inv.setItem(
        40,
        new ItemBuilder(
                new ItemStack(Material.valueOf(TradeSystem.configYml.get().getString("frame"))),
                " ")
            .getItem());
    inv.setItem(
        49,
        new ItemBuilder(
                new ItemStack(Material.valueOf(TradeSystem.configYml.get().getString("frame"))),
                " ")
            .getItem());
    return inv;
  }

  public Player getTarget(Player p) {
    return p == host ? target : host;
  }

  public List<Integer> getValidSlots() {
    return Arrays.asList(27, 28, 29, 30, 36, 37, 38, 39, 45, 46, 47, 48);
  }

  public int getTargetSlot(int i) {
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

  public void removeItem(Player p, int slot, ItemStack item) {
    p.getInventory().addItem(item);
    p.getOpenInventory().setItem(slot, new ItemStack(Material.AIR));
    items.get(p).remove(item);
    Player target = getTarget(p);
    target.getOpenInventory().setItem(getTargetSlot(slot), new ItemStack(Material.AIR));
  }

  public boolean addItem(Player p, int slot, ItemStack item) {
    for (int i : getValidSlots()) {
      if (p.getOpenInventory().getItem(i) == null) {
        p.getOpenInventory().setItem(i, item);
        p.getInventory().setItem(slot, new ItemStack(Material.AIR));
        items.get(p).add(item);
        Player target = getTarget(p);
        target.getOpenInventory().setItem(getTargetSlot(i), item);
        return true;
      }
    }
    return false;
  }

  public ArrayList<ItemStack> getItems(Player p) {
    return items.get(p);
  }
}
