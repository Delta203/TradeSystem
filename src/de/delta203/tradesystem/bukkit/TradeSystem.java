package de.delta203.tradesystem.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import de.delta203.tradesystem.bukkit.commands.Command_Trade;
import de.delta203.tradesystem.bukkit.files.ConfigYML;
import de.delta203.tradesystem.bukkit.files.MessagesYML;
import de.delta203.tradesystem.bukkit.mysql.MySQl;
import de.delta203.tradesystem.bukkit.mysql.modules.GetMySQl_Logs;

public class TradeSystem extends JavaPlugin {

	public static TradeSystem plugin;
	
	@Override
	public void onEnable() {
		plugin = this;
		
		MessagesYML.create();
		MessagesYML.load();
		ConfigYML.create();
		ConfigYML.load();
		Bukkit.getConsoleSender().sendMessage(MessagesYML.getMessage("str_prefix") + MessagesYML.getMessage("str_plugin_load"));
		Bukkit.getConsoleSender().sendMessage(MessagesYML.getMessage("str_prefix") + MessagesYML.getMessage("str_files_registered"));
		
		MySQl.connect(ConfigYML.get().getString("str_mysql_url"), ConfigYML.get().getInt("int_mysql_port"), ConfigYML.get().getString("str_mysql_database"), ConfigYML.get().getString("str_mysql_user"), ConfigYML.get().getString("str_mysql_password"));
		Bukkit.getConsoleSender().sendMessage(MessagesYML.getMessage("str_prefix") + MessagesYML.getMessage("str_mysql_connected"));
		
		MySQl.createTables();
		Bukkit.getConsoleSender().sendMessage(MessagesYML.getMessage("str_prefix") + MessagesYML.getMessage("str_mysql_registered"));
		
		String[] s = {"player1", "player2"};
		GetMySQl_Logs.writeLog(s, "content");
		if(ConfigYML.get().getInt("int_autodelete_time") > 0)
			Bukkit.getConsoleSender().sendMessage(MessagesYML.getMessage("str_prefix") + MessagesYML.getMessage("str_log_autodelete").replace("%count%", String.valueOf(GetMySQl_Logs.autoDelete())));
		
		getCommand("trade").setExecutor(new Command_Trade());
		
		
		Bukkit.getConsoleSender().sendMessage(MessagesYML.getMessage("str_prefix") + MessagesYML.getMessage("str_plugin_loaded"));
	}
	
	@Override
	public void onDisable() {
		
	}
}
