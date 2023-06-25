package de.delta203.tradesystem.bukkit.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQl {

	public static Connection con;
	
	public static void connect(String url, int port, String database, String user, String password) {
		if(isConnected()) return;
		try {
			con = DriverManager.getConnection("jdbc:mysql://" + url + ":" + port + "/" + database, user, password);
		}catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void disconnect() {
		if(!isConnected()) return;
		try {
			con.close();
		}catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	private static boolean isConnected() {
		return con != null;
	}
	
	public static void createTables() {
		if(!isConnected()) return;
		try {
			con.prepareStatement("CREATE TABLE IF NOT EXISTS TradeSystem_Logs (ID INT(16), CurrentMillis LONG, Date VARCHAR(100), Time VARCHAR(100), Players VARCHAR(100), Content LONGTEXT)").executeUpdate();
		}catch(SQLException e) {
			e.printStackTrace();
		}
	}
}
