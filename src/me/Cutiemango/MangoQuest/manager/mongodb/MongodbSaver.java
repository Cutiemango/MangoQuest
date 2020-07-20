package me.Cutiemango.MangoQuest.manager.mongodb;

import com.mongodb.client.MongoCollection;
import me.Cutiemango.MangoQuest.data.QuestFinishData;
import me.Cutiemango.MangoQuest.data.QuestObjectProgress;
import me.Cutiemango.MangoQuest.data.QuestPlayerData;
import me.Cutiemango.MangoQuest.data.QuestProgress;
import org.bson.Document;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class MongodbSaver
{

    public static void savePlayerData(QuestPlayerData pd)
    {
        MongoCollection<Document> collection = MongodbManager.getCollection();

        Player p = pd.getPlayer();
        Document playerData = new Document();
        playerData.append("UUID", p.getUniqueId().toString())
                  .append("LastKnownID", p.getName())
                  .append("FinishedConv", pd.getFinishedConversations())
                  .append("FinishedQuests", getFinishedQuestsBSON(pd.getFinishQuests()))
                  .append("FriendPoints", pd.getFriendPointStorage().entrySet().stream()
                                            .collect(Collectors.toMap(kv -> kv.getKey().toString(), Map.Entry::getValue)))
                  .append("QuestProgress", getQuestProgressBSON(pd.getProgresses()));

        Document res = collection.findOneAndReplace(new Document("UUID", p.getUniqueId().toString()), playerData);

        // no entry to replace
        if(res == null)
            collection.insertOne(playerData);
    }

    private static Document getQuestProgressBSON(Set<QuestProgress> quests)
    {
        Document data = new Document();
        for(QuestProgress q : quests)
            data.put(q.getQuest().getInternalID(), getQuestDataBSON(q));
        return data;
    }

    private static Document getFinishedQuestsBSON(Set<QuestFinishData> quests)
    {
        Document data = new Document();
        for(QuestFinishData q : quests)
            data.put(q.getQuest().getInternalID(), getQuestDataBSON(q));
        return data;
    }

    private static Document getQuestDataBSON(QuestFinishData quest)
    {
        Document data = new Document();
        data.put("FinishedTimes", quest.getFinishedTimes());
        data.put("LastFinishTime", quest.getLastFinish());
        data.put("RewardTaken", quest.isRewardTaken());
        return data;
    }

    private static Document getQuestDataBSON(QuestProgress quest)
    {
        Document data = new Document();
        data.put("Stage", quest.getCurrentStage());
        data.put("TakeStamp", quest.getTakeTime());
        data.put("Version", quest.getQuest().getVersion().getTimeStamp());
        data.put("ObjectProgress", quest.getCurrentObjects().stream().map(QuestObjectProgress::getProgress).collect(Collectors.toList()));
        return data;
    }

}
