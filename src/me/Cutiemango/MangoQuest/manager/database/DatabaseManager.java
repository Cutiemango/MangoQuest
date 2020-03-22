package me.Cutiemango.MangoQuest.manager.database;

import com.mysql.jdbc.Connection;
import me.Cutiemango.MangoQuest.ConfigSettings;
import me.Cutiemango.MangoQuest.I18n;
import me.Cutiemango.MangoQuest.manager.QuestChatManager;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;

public class DatabaseManager
{
	private static Connection _connection;

	public static Connection getConnection()
	{
		try
		{
			if (_connection == null || _connection.isClosed())
				_connection = (Connection) DriverManager.getConnection(String.format("jdbc:mysql://%s:%s/%s?useSSL=false", ConfigSettings.DATABASE_ADDRESS, ConfigSettings.DATABASE_PORT, ConfigSettings.DATABASE_USER), ConfigSettings.DATABASE_USER, ConfigSettings.DATABASE_PASSWORD);
			return _connection;
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			QuestChatManager.logCmd(Level.SEVERE, I18n.locMsg("Database.InvalidCredentials"));
		}
		return null;
	}
}
