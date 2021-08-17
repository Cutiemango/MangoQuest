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
		try(PreparedStatement insertupdate = conn.prepareStatement("INSERT INTO mq_playerdata (UUID, LastKnownID) values (?, ?)"
				+ " ON DUPLICATE KEY UPDATE mq_playerdata set LastKnownID = ? WHERE UUID = ?");)
		{
			
			insertupdate.setNString(1, p.getUniqueId().toString());
			insertupdate.setNString(2, p.getName());
			insertupdate.setNString(3, p.getName());
			insertupdate.setNString(4, p.getUniqueId().toString());
			insertupdate.execute();
	
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}


	
	private static void saveFriendPoint(int npc, int friendPoint, int PDID)
	{
		Connection conn = DatabaseManager.getConnection();
		try(PreparedStatement insertupdate = conn.prepareStatement("INSERT INTO mq_friendpoint (PDID, NPC, FriendPoint) values (?, ?, ?)"
				+ " ON DUPLICATE KEY UPDATE mq_friendpoint set FriendPoint = ? WHERE PDID = ? AND NPC = ?"))
		{
			insertupdate.setInt(1, PDID);
			insertupdate.setInt(2, npc);
			insertupdate.setInt(3, friendPoint);
			insertupdate.setInt(4, friendPoint);
			insertupdate.setInt(5, PDID);
			insertupdate.setInt(6, npc);
			insertupdate.execute();
			
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
		try(PreparedStatement insertupdate = conn.prepareStatement("INSERT INTO mq_questprogress (PDID, QuestID, QuestObjectProgress, QuestStage, TakeStamp, Version) values (?, ?, ?, ?, ?, ?) "
				+ "ON DUPLICATE KEY UPDATE mq_questprogress set QuestObjectProgress = ?, QuestStage = ?, Version = ? WHERE PDID = ? AND QuestID = ?");)
		{
			insertupdate.setInt(1, PDID);
			insertupdate.setNString(2, questProgress.getQuest().getInternalID());
			insertupdate.setNString(3, JSONSerializer.jsonSerialize(questProgress.getCurrentObjects()));
			insertupdate.setInt(4, questProgress.getCurrentStage());
			insertupdate.setLong(5, questProgress.getTakeTime());
			insertupdate.setLong(6, questProgress.getQuest().getVersion().getTimeStamp());
		    insertupdate.setNString(7, JSONSerializer.jsonSerialize(questProgress.getCurrentObjects()));
			insertupdate.setInt(8, questProgress.getCurrentStage());
			insertupdate.setLong(9, questProgress.getQuest().getVersion().getTimeStamp());
			insertupdate.setInt(10, PDID);
			insertupdate.setNString(11, questProgress.getQuest().getInternalID());
			insertupdate.execute();
			
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
		try(PreparedStatement insertupdate = conn.prepareStatement("INSERT INTO mq_finishedquest (PDID, QuestID, LastFinishTime, FinishedTimes, RewardTaken) values (?, ?, ?, ?, ?) "
				+ "ON DUPLICATE KEY UPDATE mq_finishedquest set FinishedTimes = ?, RewardTaken = ?, LastFinishTime = ? WHERE PDID = ? AND QuestID = ?");)
		{
			insertupdate.setInt(1, PDID);
			insertupdate.setNString(2, questData.getQuest().getInternalID());
			insertupdate.setLong(3, questData.getLastFinish());
			insertupdate.setInt(4, questData.getFinishedTimes());
			insertupdate.setBoolean(5, questData.isRewardTaken());
			insertupdate.setInt(6, questData.getFinishedTimes());
			insertupdate.setInt(7, questData.isRewardTaken() ? 1 : 0);
			insertupdate.setLong(8, questData.getLastFinish());
			insertupdate.setInt(9, PDID);
			insertupdate.setNString(10, questData.getQuest().getInternalID());
			insertupdate.execute();
		
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	
}
