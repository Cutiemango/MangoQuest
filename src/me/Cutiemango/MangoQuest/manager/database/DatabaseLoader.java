package me.Cutiemango.MangoQuest.manager.database;

import me.Cutiemango.MangoQuest.DebugHandler;
import me.Cutiemango.MangoQuest.Main;
import me.Cutiemango.MangoQuest.QuestUtil;
import me.Cutiemango.MangoQuest.conversation.ConversationManager;
import me.Cutiemango.MangoQuest.data.QuestFinishData;
import me.Cutiemango.MangoQuest.data.QuestPlayerData;
import me.Cutiemango.MangoQuest.data.QuestProgress;
import me.Cutiemango.MangoQuest.model.Quest;
import org.bukkit.entity.Player;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class DatabaseLoader
{

	public static QuestPlayerData loadPlayer(Player p)
	{
		try
		{
			Connection conn = DatabaseManager.getConnection();
			PreparedStatement query = conn.prepareStatement("SELECT * FROM `mq_playerdata` WHERE `UUID` = ?");
			query.setNString(1, p.getUniqueId().toString());
			ResultSet rsRet = query.executeQuery();
			if (rsRet.next())
			{
				int PDID = rsRet.getInt("PDID");
				Set<QuestProgress> prog = getQuestProgress(p, PDID);
				Set<QuestFinishData> data = getFinishedData(PDID);
				Set<String> convs = getFinishedConversations(PDID);
				HashMap<Integer, Integer> fp = getFriendPointMap(PDID);

				DebugHandler.log(2, "[Database] Player " + p.getName() + "'s data has been loaded from the database.");
				return new QuestPlayerData(p, prog, data, convs, fp, PDID);
			} else {
				// No previous player data found in database, initializing one
				File f = new File(Main.getInstance().getDataFolder() + "/data/" , p.getUniqueId() + ".yml");
				DatabaseSaver.savePlayer(p);
				return new QuestPlayerData(p, !f.exists());
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		return null;
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
				data.add(new QuestFinishData(q, rsRet.getInt("FinishedTimes"), rsRet.getLong("TakeStamp"), rsRet.getInt("RewardTaken") != 0));
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
