package de.tradesystem.delta203.spigot.trade;

import org.bukkit.entity.Player;

import java.util.HashMap;

public class TradeManager {

  private final HashMap<Player, Player> invites;
  private final HashMap<Player, Trade> trades;

  public TradeManager() {
    invites = new HashMap<>();
    trades = new HashMap<>();
  }

  public Player getInvited(Player p) {
    if (!invites.containsKey(p)) return null;
    return invites.get(p);
  }

  public void registerInvite(Player p, Player target) {
    invites.put(p, target);
    invites.put(target, p);
  }

  public void unregisterInvite(Player p) {
    invites.remove(p);
  }

  public boolean inviteValid(Player p) {
    if (!invites.containsKey(p)) return false;
    Player target = getInvited(p);
    if (target == null) return false;
    return getInvited(target) == p;
  }

  public Trade getTrade(Player p) {
    if (!trades.containsKey(p)) return null;
    return trades.get(p);
  }

  public void createTrade(Player p, Player target) {
    TradePlayer tpP = new TradePlayer(p);
    TradePlayer tpT = new TradePlayer(target);
    Trade trade = new Trade(tpP, tpT);
    trades.put(tpP.getPlayer(), trade);
    trades.put(tpT.getPlayer(), trade);
  }

  public void unregisterTrade(Player p) {
    trades.remove(p);
  }
}
