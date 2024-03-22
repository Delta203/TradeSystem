package de.tradesystem.delta203.spigot.commands;

import de.tradesystem.delta203.spigot.TradeSystem;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Commands implements TabExecutor {

  @Override
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    if (!(sender instanceof Player p)) {
      sender.sendMessage(
          TradeSystem.prefix + TradeSystem.messagesYml.get().getString("be_a_player"));
      return false;
    }
    if (!p.hasPermission("tradesystem.use")) return false;
    if (args.length == 2) {
      if (args[0].equalsIgnoreCase("invite")) {
        Player target = Bukkit.getPlayer(args[1]);
        // target is online
        if (target == null) {
          p.sendMessage(
              TradeSystem.prefix
                  + Objects.requireNonNull(TradeSystem.messagesYml.get().getString("not_online"))
                      .replace("%player%", args[1]));
          return false;
        }
        // target is not player
        if (target == p) {
          p.sendMessage(
              TradeSystem.prefix + TradeSystem.messagesYml.get().getString("not_yourself"));
          return false;
        }
        // target is not already invited
        if (TradeSystem.tradeManager.getInvited(p) == target) {
          p.sendMessage(
              TradeSystem.prefix
                  + Objects.requireNonNull(
                          TradeSystem.messagesYml.get().getString("request.already"))
                      .replace("%player%", target.getName()));
          return false;
        }
        // target is in different world
        if (p.getWorld() != target.getWorld()) {
          p.sendMessage(
              TradeSystem.prefix + TradeSystem.messagesYml.get().getString("be_in_same_world"));
          return false;
        }
        // disabled world
        if (TradeSystem.configYml
            .get()
            .getStringList("disabled_worlds")
            .contains(p.getWorld().getName())) {
          p.sendMessage(
              TradeSystem.prefix + TradeSystem.messagesYml.get().getString("disabled_world"));
          return false;
        }
        // valid
        TradeSystem.tradeManager.registerInvite(p, target);
        p.sendMessage(
            TradeSystem.prefix
                + Objects.requireNonNull(TradeSystem.messagesYml.get().getString("request.sent"))
                    .replace("%player%", target.getName()));

        TextComponent message =
            new TextComponent(
                TradeSystem.prefix
                    + Objects.requireNonNull(
                            TradeSystem.messagesYml.get().getString("request.received"))
                        .replace("%player%", p.getName()));
        TextComponent accept =
            new TextComponent(TradeSystem.messagesYml.get().getString("request.accept"));
        accept.setClickEvent(
            new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/trade accept " + p.getName()));
        accept.setHoverEvent(
            new HoverEvent(
                HoverEvent.Action.SHOW_TEXT, new Text("§a/trade accept " + p.getName())));
        message.addExtra(accept);
        target.spigot().sendMessage(message);
        target.playSound(target.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
      } else if (args[0].equalsIgnoreCase("accept")) {
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null
            || target == p
            || !TradeSystem.tradeManager.inviteValid(p)
            || p.getWorld() != target.getWorld()
            || TradeSystem.configYml
                .get()
                .getStringList("disabled_worlds")
                .contains(p.getWorld().getName())) {
          p.sendMessage(
              TradeSystem.prefix + TradeSystem.messagesYml.get().getString("requesting.no_invite"));
          TradeSystem.tradeManager.unregisterInvite(p);
          return false;
        }
        // valid
        TradeSystem.tradeManager.unregisterInvite(p);
        TradeSystem.tradeManager.unregisterInvite(target);
        TradeSystem.tradeManager.createTrade(p, target);
      } else {
        sendHelp(p);
      }
    } else {
      sendHelp(p);
    }
    return false;
  }

  @Override
  public List<String> onTabComplete(
      CommandSender sender, Command cmd, String label, String[] args) {
    if (args.length <= 1) {
      List<String> arguments = new ArrayList<>();
      arguments.add("invite");
      arguments.add("accept");
      return arguments;
    }
    return null;
  }

  private void sendHelp(Player p) {
    p.sendMessage(TradeSystem.prefix + TradeSystem.messagesYml.get().getString("help.title"));
    p.sendMessage(Objects.requireNonNull(TradeSystem.messagesYml.get().getString("help.invite")));
    p.sendMessage(Objects.requireNonNull(TradeSystem.messagesYml.get().getString("help.accept")));
  }
}
