package me.Cutiemango.MangoQuest.manager.database;

import me.Cutiemango.MangoQuest.ConfigSettings;
import me.Cutiemango.MangoQuest.DebugHandler;
import me.Cutiemango.MangoQuest.I18n;
import me.Cutiemango.MangoQuest.manager.QuestChatManager;

import java.sql.Connection;
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
				_connection = DriverManager.getConnection(String.format("jdbc:mysql://%s:%s/%s?useSSL=false", ConfigSettings.DATABASE_ADDRESS, ConfigSettings.DATABASE_PORT, ConfigSettings.DATABASE_NAME), ConfigSettings.DATABASE_USER, ConfigSettings.DATABASE_PASSWORD);
			return _connection;
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			QuestChatManager.logCmd(Level.SEVERE, I18n.locMsg("Database.InvalidCredentials"));
		}
		return null;
	}


	public static void initPlayerDB()
	{
		DebugHandler.log(5, "[Database] Initializing player's data table...");

		Connection conn = getConnection();
		try
		{
			// Table playerData init
			PreparedStatement playerData = conn.prepareStatement(
					"CREATE TABLE IF NOT EXISTS `mq_playerdata`(" +
						"    `PDID` INT NOT NULL AUTO_INCREMENT COMMENT '玩家資料流水號'," +
						"    `LastKnownID` VARCHAR(16) NOT NULL DEFAULT '' COMMENT '玩家最後顯示名稱'," +
						"    `UUID` VARCHAR(36) NOT NULL DEFAULT '' COMMENT '玩家UUID'," +
						"    PRIMARY KEY (`PDID`)," +
						"    UNIQUE INDEX `PDID` (`PDID`)" +
						") " +
						"ENGINE=InnoDB " +
						"DEFAULT CHARSET=utf8mb4 " +
						"COLLATE=utf8mb4_unicode_ci " +
						"AUTO_INCREMENT=0");
			playerData.execute();
			playerData.close();

			// Table questProgress
			PreparedStatement questProgress = conn.prepareStatement(
					"CREATE TABLE IF NOT EXISTS `mq_questprogress`(" +
						"    `QPDID` INT(11) NOT NULL AUTO_INCREMENT COMMENT '任務進度流水號'," +
						"    `PDID` INT(11) NOT NULL COMMENT '玩家資料流水號'," +
						"    `QuestID` VARCHAR(128) NOT NULL DEFAULT '' COMMENT '任務名稱(ID)'," +
						"    `QuestStage` INT(4) NOT NULL COMMENT '任務階段'," +
						"    `Version` BIGINT(20) NOT NULL DEFAULT '0' COMMENT '任務版本'," +
						"    `TakeStamp` BIGINT(20) NOT NULL DEFAULT '0' COMMENT '任務接取時間'," +
						"    `QuestObjectProgress` VARCHAR(1023) NOT NULL DEFAULT '' COMMENT '任務物件進度資料'," +
						"    PRIMARY KEY (`PDID`,`QuestID`)," +
						"    UNIQUE INDEX `QPDID` (`QPDID`)" +
						") " +
						"ENGINE=InnoDB " +
						"DEFAULT CHARSET=utf8mb4 " +
						"COLLATE=utf8mb4_unicode_ci " +
						"AUTO_INCREMENT=0");
			questProgress.execute();
			questProgress.close();

			// Table questProgress
			PreparedStatement finishedQuest = conn.prepareStatement(
					"CREATE TABLE IF NOT EXISTS `mq_finishedquest`(" +
						"    `FQID` INT(11) NOT NULL AUTO_INCREMENT COMMENT '完成任務流水號'," +
						"    `PDID` INT(11) NOT NULL COMMENT '玩家資料流水號'," +
						"    `QuestID` VARCHAR(128) NOT NULL DEFAULT '' COMMENT '任務名稱(ID)'," +
						"    `FinishedTimes` INT(11) NOT NULL COMMENT '完成次數'," +
						"    `LastFinishTime` BIGINT(20) NOT NULL DEFAULT '0' COMMENT '上次完成時間'," +
						"    `RewardTaken` INT(11) NOT NULL DEFAULT 0 COMMENT '是否已領取獎勵'," +
						"    PRIMARY KEY (`PDID`,`QuestID`)," +
						"    UNIQUE INDEX `FQID` (`FQID`)" +
						") " +
						"ENGINE=InnoDB " +
						"DEFAULT CHARSET=utf8mb4 " +
						"COLLATE=utf8mb4_unicode_ci " +
						"AUTO_INCREMENT=0");
			finishedQuest.execute();
			finishedQuest.close();

			// Table friendPoint
			PreparedStatement friendPoint = conn.prepareStatement(
					"CREATE TABLE IF NOT EXISTS `mq_friendpoint`(" +
						"	`FPID` INT(11) NOT NULL AUTO_INCREMENT COMMENT '好感度流水號'," +
						"	`PDID` INT(11) NOT NULL COMMENT '玩家資料流水號'," +
						"	`NPC` INT(11) NOT NULL COMMENT 'NPC代號(ID)'," +
						"	`FriendPoint` INT(11) DEFAULT '0' NOT NULL COMMENT '好感度'," +
						"	PRIMARY KEY (`PDID`,`NPC`)," +
						"	UNIQUE INDEX `FPID` (`FPID`)" +
						") " +
						"ENGINE=InnoDB " +
						"DEFAULT CHARSET=utf8mb4 " +
						"COLLATE=utf8mb4_unicode_ci " +
						"AUTO_INCREMENT=0");
			friendPoint.execute();
			friendPoint.close();

			// Table finishedConv
			PreparedStatement finishedConv = conn.prepareStatement(
					"CREATE TABLE IF NOT EXISTS `mq_finishedconv`(" +
						"	`FCID` INT(11) NOT NULL AUTO_INCREMENT COMMENT '完成對話流水號'," +
						"	`PDID` INT(11) NOT NULL COMMENT '玩家資料流水號'," +
						"	`ConvID` VARCHAR(128) NOT NULL DEFAULT '' COMMENT '對話名稱(ID)'," +
						"	PRIMARY KEY (`PDID`,`ConvID`)," +
						"	UNIQUE INDEX `FCID` (`FCID`)" +
						") " +
						"ENGINE=InnoDB " +
						"DEFAULT CHARSET=utf8mb4 " +
						"COLLATE=utf8mb4_unicode_ci " +
						"AUTO_INCREMENT=0");
			finishedConv.execute();
			finishedConv.close();

			conn.close();

			DebugHandler.log(5, "[Database] Initialization complete!");
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
}
