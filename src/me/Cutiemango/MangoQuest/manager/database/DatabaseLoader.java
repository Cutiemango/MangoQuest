package me.Cutiemango.MangoQuest.manager.database;

import me.Cutiemango.MangoQuest.DebugHandler;
import me.Cutiemango.MangoQuest.QuestUtil;
import me.Cutiemango.MangoQuest.conversation.ConversationManager;
import me.Cutiemango.MangoQuest.data.QuestFinishData;
import me.Cutiemango.MangoQuest.data.QuestPlayerData;
import me.Cutiemango.MangoQuest.data.QuestProgress;
import me.Cutiemango.MangoQuest.model.Quest;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class DatabaseLoader
{
	public static void initPlayerDB()
	{
		DebugHandler.log(5, "[Database] Initializing player's data table...");

		Connection conn = DatabaseManager.getConnection();
		try
		{
			// Table playerData init
			PreparedStatement playerData = conn.prepareStatement(
					"CREATE TABLE IF NOT EXISTS `mq_playerdata`" +
							"(`PDID` INT(11) NOT NULL AUTO_INCREMENT COMMENT '玩家資料流水號'," +
							"`LastKnownID` VARCHAR(16) NOT NULL DEFAULT '' COMMENT '玩家最後顯示名稱'," +
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
							"`QuestID` VARCHAR(128) NOT NULL DEFAULT '' COMMENT '任務名稱(ID)'," +
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

			// Table questProgress
			PreparedStatement finishedQuest = conn.prepareStatement(
					"CREATE TABLE IF NOT EXISTS `mq_questprogress`" +
							"(`FQID` INT(11) NOT NULL AUTO_INCREMENT COMMENT '完成任務流水號'," +
							"`PDID` INT(11) NOT NULL COMMENT '玩家資料流水號'," +
							"`QuestID` VARCHAR(128) NOT NULL DEFAULT '' COMMENT '任務名稱(ID)'," +
							"`FinishedTimes` INT(11) NOT NULL COMMENT '完成次數'," +
							"`TakeStamp` BIGINT(20) NOT NULL DEFAULT '0' COMMENT '上次完成時間'," +
							"`RewardTaken` BIT(1) NOT NULL DEFAULT '0' COMMENT '是否已領取獎勵'," +

							"PRIMARY KEY (`FQID`)," +
							"UNIQUE INDEX `FQID` (`FQID`))" +

							"COLLATE='utf8mb4_0900_ai_ci'" +
							"ENGINE=InnoDB;" +
							"AUTO_INCREMENT=0" +
							"DEFAULT CHARSET=utf8mb4");
			finishedQuest.execute();
			finishedQuest.close();

			// Table friendPoint
			PreparedStatement friendPoint = conn.prepareStatement(
					"CREATE TABLE IF NOT EXISTS `mq_friendpoint`" +
							"(`FPID` INT(11) NOT NULL AUTO_INCREMENT COMMENT '好感度流水號'," +
							"`PDID` INT(11) NOT NULL COMMENT '玩家資料流水號'," +
							"`NPC` INT(11) NOT NULL DEFAULT '' COMMENT 'NPC代號(ID)'," +
							"`FriendPoint` INT(11) DEFAULT '0' NOT NULL COMMENT '好感度'," +

							"PRIMARY KEY (`FPID`)," +
							"UNIQUE INDEX `FPID` (`FPID`))" +

							"COLLATE='utf8mb4_0900_ai_ci'" +
							"ENGINE=InnoDB;" +
							"AUTO_INCREMENT=0" +
							"DEFAULT CHARSET=utf8mb4");
			friendPoint.execute();
			friendPoint.close();

			// Table finishedConv
			PreparedStatement finishedConv = conn.prepareStatement(
					"CREATE TABLE IF NOT EXISTS `mq_finishedconv`" +
							"(`FCID` INT(11) NOT NULL AUTO_INCREMENT COMMENT '完成對話流水號'," +
							"`PDID` INT(11) NOT NULL COMMENT '玩家資料流水號'," +
							"`ConvID` VARCHAR(128) NOT NULL DEFAULT '' COMMENT '對話名稱(ID)'," +

							"PRIMARY KEY (`FCID`)," +
							"UNIQUE INDEX `FCID` (`FCID`))" +

							"COLLATE='utf8mb4_0900_ai_ci'" +
							"ENGINE=InnoDB;" +
							"AUTO_INCREMENT=0" +
							"DEFAULT CHARSET=utf8mb4");
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

	public static QuestPlayerData loadPlayer(Player p)
	{
		QuestPlayerData qd = new QuestPlayerData(p);

		try
		{
			Connection conn = DatabaseManager.getConnection();
			PreparedStatement query = conn.prepareStatement("SELECT * FROM `mq_playerdata` WHERE `UUID` = ?");
			ResultSet rsRet = query.executeQuery();
			if (rsRet.next())
			{
				int PDID = rsRet.getInt("PDID");
				Set<QuestProgress> prog = getQuestProgress(p, PDID);
				Set<QuestFinishData> data = getFinishedData(PDID);
				Set<String> convs = getFinishedConversations(PDID);
				HashMap<Integer, Integer> fp = getFriendPointMap(PDID);

				qd = new QuestPlayerData(p, prog, data, convs, fp);

				DebugHandler.log(2, "[Database] Player " + p.getName() + "'s data has been loaded from the database.");
			}
			conn.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}

		return qd;
	}

	private static Set<QuestProgress> getQuestProgress(Player p, int PDID)
	{
		Set<QuestProgress> prog = new HashSet<>();
		Connection conn = DatabaseManager.getConnection();

		try
		{
			PreparedStatement query = conn.prepareStatement("SELECT * FROM `mq_questprogress` WHERE `PDID` = ?");
			query.setInt(1, PDID);

			ResultSet rsRet = query.executeQuery();
			while (rsRet.next())
			{
				if (QuestUtil.getQuest(rsRet.getString("QuestID")) == null)
					continue;
				Quest q = QuestUtil.getQuest(rsRet.getString("QuestID"));
				int stage = rsRet.getInt("QuestStage");
				prog.add(new QuestProgress(q, p, stage, JSONSerializer.jsonDeserialize(q.getStage(stage).getObjects(), rsRet.getString("QuestObjectProgress"))));
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}

		return prog;
	}

	private static Set<QuestFinishData> getFinishedData(int PDID)
	{
		Set<QuestFinishData> data = new HashSet<>();
		Connection conn = DatabaseManager.getConnection();

		try
		{
			PreparedStatement query = conn.prepareStatement("SELECT * FROM `mq_finishedquest` WHERE `PDID` = ?");
			query.setInt(1, PDID);

			ResultSet rsRet = query.executeQuery();
			while (rsRet.next())
			{
				if (QuestUtil.getQuest(rsRet.getString("QuestID")) == null)
					continue;
				Quest q = QuestUtil.getQuest(rsRet.getString("QuestID"));
				data.add(new QuestFinishData(q, rsRet.getInt("FinishedTimes"), rsRet.getLong("LastFinishTime"), rsRet.getBoolean("RewardTaken")));
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		return data;
	}

	private static HashMap<Integer, Integer> getFriendPointMap(int PDID)
	{
		HashMap<Integer, Integer> map = new HashMap<>();
		Connection conn = DatabaseManager.getConnection();

		try
		{
			PreparedStatement query = conn.prepareStatement("SELECT * FROM `mq_friendpoint` WHERE `PDID` = ?");
			query.setInt(1, PDID);

			ResultSet rsRet = query.executeQuery();
			while (rsRet.next())
			{
				map.put(rsRet.getInt("NPC"), rsRet.getInt("FriendPoint"));
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		return map;
	}

	private static Set<String> getFinishedConversations(int PDID)
	{
		Set<String> set = new HashSet<>();
		Connection conn = DatabaseManager.getConnection();

		try
		{
			PreparedStatement query = conn.prepareStatement("SELECT * FROM `mq_finishedconv` WHERE `PDID` = ?");
			query.setInt(1, PDID);

			ResultSet rsRet = query.executeQuery();
			while (rsRet.next())
			{
				if (ConversationManager.getConversation(rsRet.getString("ConvID")) == null)
					continue;
				set.add(rsRet.getString("ConvID"));
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}

		return set;
	}
}
