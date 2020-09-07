package me.Cutiemango.MangoQuest.data;

import me.Cutiemango.MangoQuest.I18n;
import me.Cutiemango.MangoQuest.QuestIO;
import me.Cutiemango.MangoQuest.QuestUtil;
import me.Cutiemango.MangoQuest.event.QuestFinishEvent;
import me.Cutiemango.MangoQuest.manager.QuestChatManager;
import me.Cutiemango.MangoQuest.model.Quest;
import me.Cutiemango.MangoQuest.objects.reward.QuestReward;
import me.Cutiemango.MangoQuest.objects.trigger.TriggerType;
import me.Cutiemango.MangoQuest.questobject.NumerableObject;
import me.Cutiemango.MangoQuest.questobject.SimpleQuestObject;
import me.Cutiemango.MangoQuest.questobject.objects.QuestObjectReachLocation;
import me.Cutiemango.MangoQuest.questobject.objects.QuestObjectTalkToNPC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class QuestProgress
{

	public QuestProgress(Quest q, Player p)
	{
		quest = q;
		owner = p;
		currentStage = 0;
		objlist = new ArrayList<>();
		for (SimpleQuestObject o : quest.getStage(currentStage).getObjects())
			objlist.add(new QuestObjectProgress(o, 0));

		takeStamp = System.currentTimeMillis();
	}

	public QuestProgress(Quest q, Player p, int s, List<QuestObjectProgress> o, long stamp)
	{
		quest = q;
		owner = p;
		currentStage = s;
		objlist = o;
		takeStamp = stamp;
	}

	private Quest quest;
	private Player owner;
	private int currentStage;
	private List<QuestObjectProgress> objlist;
	private long takeStamp;

	public void finish()
	{
		quest.trigger(owner, TriggerType.TRIGGER_ON_FINISH, -1);
		QuestPlayerData pd = QuestUtil.getData(owner);
		QuestReward reward = quest.getQuestReward();

		boolean giveItem = reward.instantGiveReward() || !reward.hasMultipleChoices();

		pd.addFinishedQuest(quest, giveItem);

		if (giveItem)
			reward.executeItemReward(owner);
		else
			pd.getFinishData(quest).setRewardTaken(false);

		reward.executeReward(owner);
		QuestChatManager.info(owner, I18n.locMsg("CommandInfo.CompleteMessage", quest.getQuestName()));
		pd.removeProgress(quest);
		pd.save();
		Bukkit.getPluginManager().callEvent(new QuestFinishEvent(owner, quest));
	}

	public void save(QuestIO io)
	{
		io.set("QuestProgress." + quest.getInternalID() + ".QuestStage", currentStage);
		io.set("QuestProgress." + quest.getInternalID() + ".Version", quest.getVersion().getTimeStamp());
		io.set("QuestProgress." + quest.getInternalID() + ".TakeStamp", takeStamp);
		int t = 0;
		int value = 0;
		for (QuestObjectProgress qop : objlist)
		{
			if (qop.isFinished())
			{
				if (qop.getObject() instanceof QuestObjectTalkToNPC || qop.getObject() instanceof QuestObjectReachLocation)
					value = 1;
				else
					if (qop.getObject() instanceof NumerableObject)
						value = ((NumerableObject) qop.getObject()).getAmount();
			}
			else
				value = qop.getProgress();
			io.set("QuestProgress." + quest.getInternalID() + ".QuestObjectProgress." + t, value);
			t++;
		}
	}

	public void checkIfnextStage()
	{
		for (QuestObjectProgress o : objlist)
		{
			if (!o.isFinished())
				return;
		}
		nextStage();
	}

	public void nextStage()
	{
		quest.trigger(owner, TriggerType.TRIGGER_STAGE_FINISH, currentStage+1);
		if (currentStage+1 < quest.getStages().size())
		{
			currentStage++;
			QuestChatManager.info(owner, I18n.locMsg("CommandInfo.ProgressMessage", quest.getQuestName(), Integer.toString(currentStage),
					Integer.toString(quest.getStages().size())));
			objlist = new ArrayList<>();
			for (SimpleQuestObject o : quest.getStage(currentStage).getObjects())
			{
				objlist.add(new QuestObjectProgress(o, 0));
			}
			quest.trigger(owner, TriggerType.TRIGGER_STAGE_START, currentStage+1);
		}
		else finish();
	}

	public List<QuestObjectProgress> getCurrentObjects()
	{
		return objlist;
	}

	public int getCurrentStage()
	{
		return currentStage;
	}

	public Quest getQuest()
	{
		return this.quest;
	}

	public Player getOwner()
	{
		return this.owner;
	}
	
	public long getTakeTime()
	{
		return takeStamp;
	}
}
