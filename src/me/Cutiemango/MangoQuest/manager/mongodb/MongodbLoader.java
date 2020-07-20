package me.Cutiemango.MangoQuest.manager.mongodb;

import com.mongodb.client.MongoCollection;
import me.Cutiemango.MangoQuest.*;
import me.Cutiemango.MangoQuest.data.QuestFinishData;
import me.Cutiemango.MangoQuest.data.QuestObjectProgress;
import me.Cutiemango.MangoQuest.data.QuestPlayerData;
import me.Cutiemango.MangoQuest.data.QuestProgress;
import me.Cutiemango.MangoQuest.manager.QuestChatManager;
import me.Cutiemango.MangoQuest.model.Quest;
import me.Cutiemango.MangoQuest.questobject.SimpleQuestObject;
import org.bson.Document;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.*;

public class MongodbLoader
{

	public static void loadPlayer(QuestPlayerData pd)
	{
		MongoCollection<Document> collection = MongodbManager.getCollection();
		Player p = pd.getPlayer();
		Document data = collection.find(new Document("UUID", p.getUniqueId().toString())).first();

		if(data == null)
		{
			// player is not present in database, try loading from yml
			File f = new File(Main.getInstance().getDataFolder() + "/data/", p.getUniqueId() + ".yml");
			if(f.exists())
				pd.load(ConfigSettings.SaveType.YML);
			MongodbSaver.savePlayerData(pd);
			return;
		}

		pd.getFriendPointStorage().putAll(getFriendPoints(data));
		pd.getFinishedConversations().addAll(data.getList("FinishedConv", String.class));
		pd.getProgresses().addAll(getProgressSet(pd.getPlayer(), data));
		pd.getFinishQuests().addAll(getFinishedQuests(data));

		DebugHandler.log(5, "%s's data loaded", data.get("LastKnownID"));
	}

	private static Set<QuestFinishData> getFinishedQuests(Document data) {
		Set<QuestFinishData> quests = new HashSet<>();
		data.get("FinishedQuests", Document.class)
			.forEach((questID, questData) -> quests.add(getQuestFinishData(questID, (Document) questData)));
		return  quests;
	}

	private static QuestFinishData getQuestFinishData(String questID, Document questData) {
		Quest quest = QuestUtil.getQuest(questID);
		int finishedTimes = questData.getInteger("FinishedTimes");
		long lastFinishTime = questData.getLong("LastFinishTime");
		boolean rewardTaken = questData.getBoolean("RewardTaken");
		return new QuestFinishData(quest, finishedTimes, lastFinishTime, rewardTaken);
	}

	private static Set<QuestProgress> getProgressSet(Player p, Document data)
	{
		Set<QuestProgress> quests = new HashSet<>();
		data.get("QuestProgress", Document.class).forEach(
				(questID, questData) ->  quests.add(toQuestProgress(p, questID, (Document) questData))
		);
		return quests;
	}

	private static QuestProgress toQuestProgress(Player player, String questID, Document data)
	{
		Quest q = QuestUtil.getQuest(questID);
		long version = data.getLong("Version");
		if (q.getVersion().getTimeStamp() != version)
		{
			QuestChatManager.error(player, I18n.locMsg("CommandInfo.OutdatedQuestVersion", q.getInternalID()));
			return null;
		}

		int stage = data.getInteger("Stage");
		long takeStamp = data.getLong("TakeStamp");

		Iterator<SimpleQuestObject> objIter = q.getStage(stage).getObjects().iterator();
		Iterator<Integer> progressIter = data.getList("ObjectProgress", Integer.class).iterator();
		List<QuestObjectProgress> objectives = new ArrayList<>();
		while(objIter.hasNext() && progressIter.hasNext())
			objectives.add(new QuestObjectProgress(objIter.next(), progressIter.next()));
		objectives.forEach(QuestObjectProgress::checkIfFinished);
		return new QuestProgress(q, player, stage, objectives, takeStamp);
	}

	private static HashMap<Integer, Integer> getFriendPoints(Document data)
	{
		HashMap<Integer, Integer> friendPoints = new HashMap<>();
		data.get("FriendPoints", Document.class).forEach(
				(key, value) -> friendPoints.put(Integer.parseInt(key), (Integer) value));

		return friendPoints;
	}


}
