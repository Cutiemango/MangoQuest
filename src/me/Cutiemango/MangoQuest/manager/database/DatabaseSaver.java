package me.Cutiemango.MangoQuest.manager.database;

import me.Cutiemango.MangoQuest.data.QuestFinishData;
import me.Cutiemango.MangoQuest.data.QuestPlayerData;
import me.Cutiemango.MangoQuest.data.QuestProgress;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;

/**
 * PreparedStatement.executeQuery().next() returns true if the query is not empty.
 */
public class DatabaseSaver
{
	/**
	 * Saves the entire player data into the database.
	 * PDID is required.
	 * @param pd player's data to be saved
	 */
	public static void savePlayerData(QuestPlayerData pd)
	{
		int PDID = pd.getPDID();
		saveLoginData(pd.getPlayer());

		pd.getFinishQuests().forEach(questData -> saveFinishedQuest(questData, PDID));
		pd.getProgresses().forEach(questProgress -> saveQuestProgress(questProgress, PDID));
		pd.getFriendPointStorage().forEach((id, value) -> saveFriendPoint(id, value, PDID));
		pd.getFinishedConversations().forEach(convID -> saveFinishedConversation(convID, PDID));

		removeFinishedQuests(pd.getProgresses(), PDID);
	}

	public static void saveLoginData(Player p)
	{
		Connection conn = DatabaseManager.getConnection();
		try
		{
			PreparedStatement select = conn.prepareStatement("SELECT * FROM mq_playerdata WHERE UUID = ?");
			select.setNString(1, p.getUniqueId().toString());

			if (select.executeQuery().next())
			{
				PreparedStatement update = conn.prepareStatement("UPDATE mq_playerdata set LastKnownID = ? WHERE UUID = ?");
				update.setNString(1, p.getName());
				update.setNString(2, p.getUniqueId().toString());
				update.execute();
			}
			else
			{
				PreparedStatement insert = conn.prepareStatement("INSERT INTO mq_playerdata (UUID, LastKnownID) values (?, ?)");
				insert.setNString(1, p.getUniqueId().toString());
				insert.setNString(2, p.getName());
				insert.execute();
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	private static void saveFriendPoint(int npc, int friendPoint, int PDID)
	{
		Connection conn = DatabaseManager.getConnection();
		try
		{
			PreparedStatement select = conn.prepareStatement("SELECT * FROM mq_friendpoint WHERE PDID = ? AND NPC = ?");
			select.setInt(1, PDID);
			select.setInt(2, npc);
			if (select.executeQuery().next())
			{
				PreparedStatement update = conn.prepareStatement("UPDATE mq_friendpoint set FriendPoint = ? WHERE PDID = ? AND NPC = ?");
				update.setInt(1, friendPoint);
				update.setInt(2, PDID);
				update.setInt(3, npc);
				update.execute();
			}
			else
			{
				PreparedStatement insert = conn.prepareStatement("INSERT INTO mq_friendpoint (PDID, NPC, FriendPoint) values (?, ?, ?)");
				insert.setInt(1, PDID);
				insert.setInt(2, npc);
				insert.setInt(3, friendPoint);
				insert.execute();
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	private static void saveFinishedConversation(String convID, int PDID)
	{
		Connection conn = DatabaseManager.getConnection();
		try
		{
			PreparedStatement select = conn.prepareStatement("SELECT * FROM mq_finishedconv WHERE PDID = ? AND ConvID = ?");
			select.setInt(1, PDID);
			select.setNString(2, convID);

			if (!select.executeQuery().next())
			{
				PreparedStatement insert = conn.prepareStatement("INSERT INTO mq_finishedconv (PDID, ConvID) values (?, ?)");
				insert.setInt(1, PDID);
				insert.setNString(2, convID);
				insert.execute();
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	private static void saveQuestProgress(QuestProgress questProgress, int PDID)
	{
		Connection conn = DatabaseManager.getConnection();
		try
		{
			PreparedStatement select = conn.prepareStatement("SELECT * FROM mq_questprogress WHERE PDID = ? AND QuestID = ?");
			select.setInt(1, PDID);
			select.setNString(2, questProgress.getQuest().getInternalID());

			if (select.executeQuery().next())
			{
				PreparedStatement update = conn.prepareStatement(
						"UPDATE mq_questprogress set QuestObjectProgress = ?, QuestStage = ?, Version = ? WHERE PDID = ? AND QuestID = ?");
				update.setNString(1, JSONSerializer.jsonSerialize(questProgress.getCurrentObjects()));
				update.setInt(2, questProgress.getCurrentStage());
				update.setLong(3, questProgress.getQuest().getVersion().getTimeStamp());
				update.setInt(4, PDID);
				update.setNString(5, questProgress.getQuest().getInternalID());
				update.execute();
			}
			else
			{
				PreparedStatement insert = conn.prepareStatement(
						"INSERT INTO mq_questprogress (PDID, QuestID, QuestObjectProgress, QuestStage, TakeStamp, Version) values (?, ?, ?, ?, ?, ?)");
				insert.setInt(1, PDID);
				insert.setNString(2, questProgress.getQuest().getInternalID());
				insert.setNString(3, JSONSerializer.jsonSerialize(questProgress.getCurrentObjects()));
				insert.setInt(4, questProgress.getCurrentStage());
				insert.setLong(5, questProgress.getTakeTime());
				insert.setLong(6, questProgress.getQuest().getVersion().getTimeStamp());
				insert.execute();
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	private static void removeFinishedQuests(Set<QuestProgress> progresses, int PDID)
	{
		Connection conn = DatabaseManager.getConnection();
		try
		{
			PreparedStatement select = conn.prepareStatement("SELECT * FROM mq_questprogress WHERE PDID = ?");
			select.setInt(1, PDID);
			ResultSet results = select.executeQuery();
			while (results.next())
			{
				String questID = results.getString("QuestID");
				if (progresses.stream().noneMatch(questProgress -> questProgress.getQuest().getInternalID().equals(questID)))
				{
					PreparedStatement delete = conn.prepareStatement("DELETE FROM mq_questprogress WHERE PDID = ? AND QuestID = ?");
					delete.setInt(1, PDID);
					delete.setNString(2, questID);
					delete.execute();
				}
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	private static void saveFinishedQuest(QuestFinishData questData, int PDID)
	{
		Connection conn = DatabaseManager.getConnection();
		try
		{
			PreparedStatement select = conn.prepareStatement("SELECT * FROM mq_finishedquest WHERE PDID = ? AND QuestID = ?");
			select.setInt(1, PDID);
			select.setNString(2, questData.getQuest().getInternalID());
			if (select.executeQuery().next())
			{
				PreparedStatement update = conn.prepareStatement(
						"UPDATE mq_finishedquest set FinishedTimes = ?, RewardTaken = ?, LastFinishTime = ? WHERE PDID = ? AND QuestID = ?");
				update.setInt(1, questData.getFinishedTimes());
				update.setInt(2, questData.isRewardTaken() ? 1 : 0);
				update.setLong(3, questData.getLastFinish());
				update.setInt(4, PDID);
				update.setNString(5, questData.getQuest().getInternalID());
				update.execute();
			}
			else
			{
				PreparedStatement insert = conn.prepareStatement(
						"INSERT INTO mq_finishedquest (PDID, QuestID, LastFinishTime, FinishedTimes, RewardTaken) values (?, ?, ?, ?, ?)");
				insert.setInt(1, PDID);
				insert.setNString(2, questData.getQuest().getInternalID());
				insert.setLong(3, questData.getLastFinish());
				insert.setInt(4, questData.getFinishedTimes());
				insert.setBoolean(5, questData.isRewardTaken());
				insert.execute();
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
}
