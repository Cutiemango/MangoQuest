package me.Cutiemango.MangoQuest.manager.database;

import me.Cutiemango.MangoQuest.DebugHandler;
import me.Cutiemango.MangoQuest.data.QuestPlayerData;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseSaver
{
	// UPDATE
	public static void savePlayerData(QuestPlayerData pd)
	{
		Connection conn = DatabaseManager.getConnection();
		try
		{
			saveLoginData(pd, conn);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	private static void saveLoginData(QuestPlayerData pd, Connection conn) throws SQLException
	{
		PreparedStatement ps = conn.prepareStatement("INSERT INTO `mq_playerdata` (`PDID`, `LastKnownID`, `UUID`) VALUES (?, ?, ?)");

	}

	public static void overrideFromYml(Player p)
	{
		DebugHandler.log(5, "[Database] Overriding database data from yml...");

		QuestPlayerData qd = new QuestPlayerData(p);
		qd.loadFromYml();

		savePlayerData(qd);
		DebugHandler.log(5, "[Database] Override completed.");
	}
}
