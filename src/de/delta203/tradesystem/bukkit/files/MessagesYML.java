package de.delta203.tradesystem.bukkit.files;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import de.delta203.tradesystem.bukkit.TradeSystem;

public class MessagesYML {

	private static File file = new File(TradeSystem.plugin.getDataFolder(), "messages.yml");
	private static FileConfiguration cfg;
	
	public static FileConfiguration get() {
		return cfg;
	}
	
	public static void load() {
		try {
			cfg.load(file);
		}catch(FileNotFoundException e) {
			e.printStackTrace();
		}catch(IOException e) {
			e.printStackTrace();
		}catch(InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}

	public static void save() {
		try {
			cfg.save(file);
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void create() {
		if(!TradeSystem.plugin.getDataFolder().exists()) TradeSystem.plugin.getDataFolder().mkdir();
		try {
			if(!file.exists()) {
				Files.copy(TradeSystem.plugin.getResource("messages.yml"), file.toPath());
				cfg = YamlConfiguration.loadConfiguration(file);
			}else {
				cfg = YamlConfiguration.loadConfiguration(file);
			}
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String getMessage(String key) {
		return cfg.getString(key).replace('&', '§');
	}
}
