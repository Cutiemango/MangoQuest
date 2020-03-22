package me.Cutiemango.MangoQuest.manager.database;

import me.Cutiemango.MangoQuest.data.QuestPlayerData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseLoader
{
	public static void initPlayerDB()
	{
		Connection conn = DatabaseManager.getConnection();
		try
		{
			// Table playerData init
			PreparedStatement playerData = conn.prepareStatement(
					"CREATE TABLE IF NOT EXISTS `mq_playerdata`" +
							"(`PDID` INT(11) NOT NULL AUTO_INCREMENT COMMENT '玩家資料流水號'," +
							"`LastKnownID` VARCHAR(100) NOT NULL DEFAULT '' COMMENT '玩家最後顯示名稱'," +
							"`UUID` VARCHAR(36) NOT NULL DEFAULT '' COMMENT '玩家UUID'," +
							"PRIMARY KEY (`PDID`)," +
							"UNIQUE INDEX `PDID` (`PDID`))" +

							"COLLATE='utf8mb4_0900_ai_ci'" +
							"ENGINE=InnoDB;" +
							"AUTO_INCREMENT=0" +
							"DEFAULT CHARSET=utf8mb4");
			playerData.execute();
			playerData.close();

			// Table questProgress
			PreparedStatement questProgress = conn.prepareStatement(
					"CREATE TABLE IF NOT EXISTS `mq_questprogress`" +
							"(`QPDID` INT(11) NOT NULL AUTO_INCREMENT COMMENT '任務進度流水號'," +
							"`PDID` INT(11) NOT NULL COMMENT '玩家資料流水號'," +
							"`QuestName` VARCHAR(128) NOT NULL DEFAULT '' COMMENT '任務名稱'," +
							"`QuestStage` INT(4) NOT NULL COMMENT '任務階段'," +
							"`Version` BIGINT(20) NOT NULL DEFAULT '0' COMMENT '任務版本'," +
							"`TakeStamp` BIGINT(20) NOT NULL DEFAULT '0' COMMENT '任務接取時間'," +
							"`QuestObjectProgress` VARCHAR(255) NOT NULL DEFAULT '' COMMENT '任務物件進度資料'," +

							"PRIMARY KEY (`QPDID`)," +
							"UNIQUE INDEX `QPDID` (`QPDID`))" +

							"COLLATE='utf8mb4_0900_ai_ci'" +
							"ENGINE=InnoDB;" +
							"AUTO_INCREMENT=0" +
							"DEFAULT CHARSET=utf8mb4");
			questProgress.execute();
			questProgress.close();

			conn.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	public static void loadPlayer(QuestPlayerData data)
	{
		Connection conn = DatabaseManager.getConnection();
		try
		{
			PreparedStatement query = conn.prepareStatement("SELECT * FROM `mq_playerdata` WHERE `UUID` = ?");
			query.setString(1, data.getPlayer().getUniqueId().toString());

			ResultSet rsRet = query.executeQuery();
			if (rsRet.next())
			{

			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
}
