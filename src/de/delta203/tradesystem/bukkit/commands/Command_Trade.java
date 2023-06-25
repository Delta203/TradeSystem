package de.delta203.tradesystem.bukkit.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.delta203.tradesystem.bukkit.files.MessagesYML;

public class Command_Trade implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if((sender instanceof Player)) {
			sender.sendMessage(MessagesYML.getMessage("str_you_must_be_a_player"));
			return false;
		}
		if(!sender.hasPermission("tradesystem.use")) {
			sender.sendMessage(MessagesYML.getMessage("str_no_permission"));
			return false;
		}
		
		if(args.length == 0) {
			sendHelp(sender);
		}
		return false;
	}

	private void sendHelp(CommandSender sender) {
		sender.sendMessage(MessagesYML.getMessage("str_help_title"));
		sender.sendMessage(MessagesYML.getMessage("str_help_1"));
		sender.sendMessage("/tradeaccept");
		sender.sendMessage("/tradedeny");
	}
}
