package me.Cutiemango.MangoQuest.manager.database;

import me.Cutiemango.MangoQuest.DebugHandler;
import me.Cutiemango.MangoQuest.data.QuestPlayerData;
import org.bukkit.entity.Player;

import java.sql.Connection;

public class DatabaseSaver
{
	// UPDATE
	public static void savePlayerData(QuestPlayerData pd)
	{
		Connection conn = DatabaseManager.getConnection();

	}

	// INSERT INTO
	public static void firstDefaultSave(Player p)
	{

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
