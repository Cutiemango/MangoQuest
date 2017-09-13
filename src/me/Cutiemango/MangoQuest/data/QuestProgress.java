package me.Cutiemango.MangoQuest.data;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.entity.Player;
import me.Cutiemango.MangoQuest.QuestIO;
import me.Cutiemango.MangoQuest.QuestUtil;
import me.Cutiemango.MangoQuest.I18n;
import me.Cutiemango.MangoQuest.manager.QuestChatManager;
import me.Cutiemango.MangoQuest.model.Quest;
import me.Cutiemango.MangoQuest.model.TriggerType;
import me.Cutiemango.MangoQuest.questobjects.NumerableObject;
import me.Cutiemango.MangoQuest.questobjects.QuestObjectReachLocation;
import me.Cutiemango.MangoQuest.questobjects.QuestObjectTalkToNPC;
import me.Cutiemango.MangoQuest.questobjects.SimpleQuestObject;

public class QuestProgress
{

	public QuestProgress(Quest quest, Player owner)
	{
		this.quest = quest;
		this.owner = owner;
		CurrentStage = 0;
		objlist = new ArrayList<>();
		for (SimpleQuestObject o : quest.getStage(CurrentStage).getObjects())
		{
			objlist.add(new QuestObjectProgress(o, 0));
		}
	}

	public QuestProgress(Quest q, Player p, int s, List<QuestObjectProgress> o)
	{
		quest = q;
		owner = p;
		CurrentStage = s;
		objlist = o;
	}

	private Quest quest;
	private Player owner;
	private int CurrentStage;
	private List<QuestObjectProgress> objlist;

	public void finish()
	{
		quest.trigger(owner, 0, TriggerType.TRIGGER_ON_FINISH, -1);
		QuestPlayerData pd = QuestUtil.getData(owner);
		pd.addFinishedQuest(quest);
		quest.getQuestReward().giveRewardTo(owner);
		QuestChatManager.info(owner, I18n.locMsg("CommandInfo.CompleteMessage", quest.getQuestName()));
		pd.removeProgress(quest);
	}

	public void save(QuestIO io)
	{
		io.set("QuestProgress." + quest.getInternalID() + ".QuestStage", CurrentStage);
		io.set("QuestProgress." + quest.getInternalID() + ".Version", quest.getVersion().getVersion());
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
		quest.trigger(owner, 0, TriggerType.TRIGGER_STAGE_FINISH, CurrentStage + 1);
		quest.trigger(owner, 0, TriggerType.TRIGGER_STAGE_START, CurrentStage + 2);
		if (CurrentStage + 1 < quest.getStages().size())
		{
			CurrentStage++;
			QuestChatManager.info(owner, I18n.locMsg("CommandInfo.ProgressMessage", quest.getQuestName(), Integer.toString(CurrentStage),
					Integer.toString(quest.getStages().size())));
			objlist = new ArrayList<>();
			for (SimpleQuestObject o : quest.getStage(CurrentStage).getObjects())
			{
				objlist.add(new QuestObjectProgress(o, 0));
			}
		}
		else
			if (CurrentStage + 1 >= quest.getStages().size())
				finish();
	}

	public List<QuestObjectProgress> getCurrentObjects()
	{
		return objlist;
	}

	public int getCurrentStage()
	{
		return CurrentStage;
	}

	public Quest getQuest()
	{
		return this.quest;
	}

	public Player getOwner()
	{
		return this.owner;
	}
}
