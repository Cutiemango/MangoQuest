package me.Cutiemango.MangoQuest.model;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import me.Cutiemango.MangoQuest.QuestStorage;
import me.Cutiemango.MangoQuest.QuestUtil;
import me.Cutiemango.MangoQuest.DebugHandler;
import me.Cutiemango.MangoQuest.I18n;
import me.Cutiemango.MangoQuest.data.QuestPlayerData;
import me.Cutiemango.MangoQuest.data.QuestProgress;
import me.Cutiemango.MangoQuest.manager.QuestChatManager;
import me.Cutiemango.MangoQuest.manager.QuestValidater;
import me.Cutiemango.MangoQuest.objects.QuestStage;
import me.Cutiemango.MangoQuest.objects.QuestVersion;
import me.Cutiemango.MangoQuest.objects.requirement.RequirementType;
import me.Cutiemango.MangoQuest.objects.reward.QuestReward;
import me.Cutiemango.MangoQuest.objects.trigger.TriggerObject;
import me.Cutiemango.MangoQuest.objects.trigger.TriggerTask;
import me.Cutiemango.MangoQuest.objects.trigger.TriggerType;
import me.Cutiemango.MangoQuest.questobject.SimpleQuestObject;
import net.citizensnpcs.api.npc.NPC;

public class Quest
{
	// Only Initialize with Command
	public Quest()
	{
		initRequirements();
		version = QuestVersion.instantVersion();
	}

	public Quest(String InternalID, String name, List<String> QuestOutline, QuestReward reward, List<QuestStage> stages, NPC npc)
	{
		this.internalID = InternalID;
		this.questName = QuestChatManager.translateColor(name);
		this.outline = QuestOutline;
		this.reward = reward;
		this.stages = stages;
		this.questNPC = npc;

		initRequirements();
		version = QuestVersion.instantVersion();
		if (!reward.hasRewardNPC())
			reward.setRewardNPC(npc);
	}
	
	public void initRequirements()
	{
		for (RequirementType t : RequirementType.values())
		{
			if (!requirements.containsKey(t))
			{
				switch (t)
				{
					case FRIEND_POINT:
						requirements.put(RequirementType.FRIEND_POINT, new HashMap<Integer, Integer>());
						break;
					case ITEM:
						requirements.put(RequirementType.ITEM, new ArrayList<ItemStack>());
						break;
					case LEVEL:
						requirements.put(RequirementType.LEVEL, 0);
						break;
					case MONEY:
						requirements.put(RequirementType.MONEY, 0d);
						break;
					case QUEST:
						requirements.put(RequirementType.QUEST, new ArrayList<String>());
						break;
					case SKILLAPI_CLASS:
						requirements.put(RequirementType.SKILLAPI_CLASS, "none");
						break;
					case SKILLAPI_LEVEL:
						requirements.put(RequirementType.SKILLAPI_LEVEL, 0);
						break;
				}
			}
		}
	}
	
	private NPC questNPC;
	private String internalID;
	private String questName;

	private List<String> outline = new ArrayList<>();

	private List<QuestStage> stages = new ArrayList<>();
	private QuestReward reward = new QuestReward();
	private QuestSetting setting = new QuestSetting();

	private EnumMap<RequirementType, Object> requirements = new EnumMap<>(RequirementType.class);
	private EnumMap<TriggerType, List<TriggerObject>> triggerMap = new EnumMap<>(TriggerType.class);


	private QuestVersion version;

	public String getInternalID()
	{
		return internalID;
	}

	public void setInternalID(String id)
	{
		internalID = id;
	}

	public String getQuestName()
	{
		return questName;
	}

	public void setQuestName(String s)
	{
		questName = s;
	}

	public List<String> getQuestOutline()
	{
		return outline;
	}

	public void setQuestOutline(List<String> s)
	{
		outline = s;
	}

	public QuestReward getQuestReward()
	{
		return this.reward;
	}

	public NPC getQuestNPC()
	{
		return questNPC;
	}

	public QuestVersion getVersion()
	{
		return version;
	}

	public void registerVersion(QuestVersion ver)
	{
		version = ver;
	}

	public void setQuestNPC(NPC npc)
	{
		questNPC = npc;
	}

	public boolean isCommandQuest()
	{
		return questNPC == null;
	}
	
	public boolean usePermission()
	{
		return setting.usePermission;
	}
	
	public boolean hasWorldLimit()
	{
		return setting.worldLimit != null;
	}
	
	public World getWorldLimit()
	{
		return setting.worldLimit;
	}
	
	public void setWorldLimit(World w)
	{
		setting.worldLimit = w;
	}
	
	public void setUsePermission(boolean b)
	{
		setting.usePermission = b;
	}

	public List<QuestStage> getStages()
	{
		return stages;
	}

	public List<SimpleQuestObject> getAllObjects()
	{
		List<SimpleQuestObject> list = new ArrayList<>();
		for (QuestStage qs : stages)
		{
			list.addAll(qs.getObjects());
		}
		return list;
	}

	public QuestStage getStage(int index)
	{
		return stages.get(index);
	}

	public EnumMap<RequirementType, Object> getRequirements()
	{
		return requirements;
	}

	public EnumMap<TriggerType, List<TriggerObject>> getTriggerMap()
	{
		return triggerMap;
	}
	
	public List<TriggerObject> getTrigger(TriggerType type)
	{
		return triggerMap.get(type);
	}

	public void setTriggers(EnumMap<TriggerType, List<TriggerObject>> map)
	{
		triggerMap = map;
	}

	public boolean hasTrigger(TriggerType type)
	{
		return triggerMap.containsKey(type);
	}

	public boolean hasRequirement()
	{
		return !requirements.isEmpty();
	}

	public String getFailMessage()
	{
		return setting.failRequirementMessage;
	}

	public void setFailMessage(String s)
	{
		setting.failRequirementMessage = s;
	}
	
	public String getQuitAcceptMsg()
	{
		return (setting.quitAcceptMsg == null) ? I18n.locMsg("QuestQuitMsg.DefaultQuit") : setting.quitAcceptMsg;
	}
	
	public void setQuitAcceptMsg(String s)
	{
		setting.quitAcceptMsg = s;
	}
	
	public String getQuitCancelMsg()
	{
		return (setting.quitCancelMsg == null) ? I18n.locMsg("QuestQuitMsg.DefaultCancel") : setting.quitCancelMsg;
	}
	
	public void setQuitCancelMsg(String s)
	{
		setting.quitCancelMsg = s;
	}

	public boolean useCustomFailMessage()
	{
		return setting.useCustomFailMessage;
	}

	public void setUseCustomFailMessage(boolean b)
	{
		setting.useCustomFailMessage = b;
	}

	public boolean isRedoable()
	{
		return setting.isRedoable;
	}

	public void setRedoable(boolean b)
	{
		setting.isRedoable = b;
	}
	
	public boolean isTimeLimited()
	{
		return setting.isTimeLimited;
	}
	
	public void setTimeLimited(boolean b)
	{
		setting.isTimeLimited = b;
	}
	
	public boolean isQuitable()
	{
		return setting.isQuitable;
	}
	
	public void setQuitable(boolean b)
	{
		setting.isQuitable = b;
	}

	public long getRedoDelay()
	{
		return setting.redoDelay;
	}

	public void setRedoDelay(long delay)
	{
		setting.redoDelay = delay;
	}
	
	public long getTimeLimit()
	{
		return setting.timeLimit;
	}
	
	public void setTimeLimit(long limit)
	{
		setting.timeLimit = limit;
	}

	public void setRequirements(EnumMap<RequirementType, Object> m)
	{
		requirements = m;
	}
	
	public QuestSetting getSettings()
	{
		return setting;
	}
	
	public void registerSettings(QuestSetting s)
	{
		setting = s;
	}
	
	public void trigger(Player p, TriggerType type, int stage)
	{
		if (!hasTrigger(type) || triggerMap.get(type).isEmpty())
			return;
		TriggerTask task = new TriggerTask(p, triggerMap.get(type));
		if (type.hasStage())
			task.withStage(stage);
		DebugHandler.log(5, "[Triggers] Triggertask of " + type.toString() + " started with stage " + stage + ".");
		task.start();
	}

	@Override
	public Quest clone()
	{
		Quest q = new Quest(internalID, questName, new ArrayList<String>(outline), reward, new ArrayList<QuestStage>(stages), questNPC);
		q.setRequirements(new EnumMap<RequirementType, Object>(requirements));
		q.registerVersion(version);
		q.setTriggers(new EnumMap<TriggerType, List<TriggerObject>>(triggerMap));
		q.registerSettings(setting);
		return q;
	}

	public static void synchronizeLocal(Quest q)
	{
		for (Player p : Bukkit.getOnlinePlayers())
		{
			QuestPlayerData pd = QuestUtil.getData(p);
			Iterator<QuestProgress> it = pd.getProgresses().iterator();
			while (it.hasNext())
			{
				QuestProgress qp = it.next();
				if (QuestValidater.detailedValidate(q, qp.getQuest()))
				{
					pd.forceQuit(q, true);
					break;
				}
				else
					continue;
			}
		}
		QuestStorage.Quests.put(q.getInternalID(), q);
	}


}
