package de.delta203.tradesystem.bukkit.mysql.modules;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import de.delta203.tradesystem.bukkit.files.ConfigYML;
import de.delta203.tradesystem.bukkit.mysql.MySQl;

public class GetMySQl_Logs {

	/**
	 * @param players
	 * @param content
	 * Content-Format
	 * 	Player1: (<items>,<coins>),
	 * 	Player2: (<items>,<coins>),
	 */
	public static void writeLog(String[] players, String content) {
		try {
			PreparedStatement ps = MySQl.con.prepareStatement("INSERT INTO TradeSystem_Logs (ID, CurrentMillis, Date, Time, Players, Content) VALUES (?, ?, ?, ?, ?, ?)");
			ps.setInt(1, getID());
			ps.setLong(2, System.currentTimeMillis());
			ps.setString(3, getDate());
			ps.setString(4, getTime());
			ps.setString(5, players[0] + " | " + players[1]);
			ps.setString(6, content);
			ps.executeUpdate();
		}catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static int autoDelete() {
		int deleteCount = 0;
		try {
			PreparedStatement psCount = MySQl.con.prepareStatement("SELECT COUNT(*) FROM TradeSystem_Logs");
			ResultSet rsBefore = psCount.executeQuery();
			while(rsBefore.next()) deleteCount = rsBefore.getInt("COUNT(*)");
			
			PreparedStatement ps = MySQl.con.prepareStatement("DELETE FROM TradeSystem_Logs WHERE CurrentMillis <= ?");
			long millis = System.currentTimeMillis();
			millis -= (long)ConfigYML.get().getInt("int_autodelete_time")*86400000;
			ps.setLong(1, millis);
			ps.executeUpdate();
			
			ResultSet rsAfter = psCount.executeQuery();
			while(rsAfter.next()) deleteCount -= rsAfter.getInt("COUNT(*)");
		}catch(SQLException e) {
			e.printStackTrace();
		}
		return deleteCount;
	}
	
	private static int getID() {
		try {
			PreparedStatement ps = MySQl.con.prepareStatement("SELECT COUNT(*) FROM TradeSystem_Logs");
			ResultSet rs = ps.executeQuery();
			while(rs.next()) return rs.getInt("COUNT(*)");
		}catch(SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	private static String getDate() {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern(ConfigYML.get().getString("str_format_date"));
		LocalDateTime now = LocalDateTime.now();
		return dtf.format(now);
	}
	
	private static String getTime() {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern(ConfigYML.get().getString("str_format_time"));
		LocalDateTime now = LocalDateTime.now();
		return dtf.format(now);
	}
}
