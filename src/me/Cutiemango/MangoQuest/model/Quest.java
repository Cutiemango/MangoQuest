package me.Cutiemango.MangoQuest.model;

import me.Cutiemango.MangoQuest.DebugHandler;
import me.Cutiemango.MangoQuest.I18n;
import me.Cutiemango.MangoQuest.QuestStorage;
import me.Cutiemango.MangoQuest.QuestUtil;
import me.Cutiemango.MangoQuest.data.QuestPlayerData;
import me.Cutiemango.MangoQuest.data.QuestProgress;
import me.Cutiemango.MangoQuest.manager.QuestChatManager;
import me.Cutiemango.MangoQuest.manager.QuestValidater;
import me.Cutiemango.MangoQuest.objects.QuestStage;
import me.Cutiemango.MangoQuest.objects.QuestVersion;
import me.Cutiemango.MangoQuest.objects.RequirementType;
import me.Cutiemango.MangoQuest.objects.reward.QuestReward;
import me.Cutiemango.MangoQuest.objects.trigger.TriggerObject;
import me.Cutiemango.MangoQuest.objects.trigger.TriggerTask;
import me.Cutiemango.MangoQuest.objects.trigger.TriggerType;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;

public class Quest
{
	// Only Initialize with Command
	public Quest()
	{
		initRequirements();
		version = QuestVersion.instantVersion();
	}

	public Quest(String internalID, String name, List<String> questOutline, QuestReward reward, List<QuestStage> stages, NPC npc)
	{
		this.internalID = internalID;
		this.questName = QuestChatManager.translateColor(name);
		this.outline = questOutline;
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
					case ALLOW_DESCENDANT:
						requirements.put(RequirementType.ALLOW_DESCENDANT, false);
						break;
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
					case PERMISSION:
						requirements.put(RequirementType.PERMISSION, new ArrayList<String>());
						break;
					case QUEST:
						requirements.put(RequirementType.QUEST, new ArrayList<String>());
						break;
					case QRPG_CLASS:
					case SKILLAPI_CLASS:
						requirements.put(t, "none");
						break;
					case QRPG_LEVEL:
					case SKILLAPI_LEVEL:
						requirements.put(t, 0);
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

	public QuestSetting.RedoSetting getRedoSetting()
	{
		return setting.redoSetting;
	}

	public boolean isRedoable()
	{
		return setting.redoSetting != QuestSetting.RedoSetting.ONCE_ONLY;
	}

	public void setRedoSetting(QuestSetting.RedoSetting redo)
	{
		setting.redoSetting = redo;
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

	public int getResetDay()
	{
		return setting.resetDay;
	}

	public int getResetHour()
	{
		return setting.resetHour;
	}

	public void setResetDay(int day)
	{
		if (day < 1 || day > 7)
			return;
		setting.resetDay = day;
	}

	public void setResetHour(int hour)
	{
		if (hour < 0 || hour > 23)
			return;
		setting.resetHour = hour;
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
		DebugHandler.log(5, "[Triggers] Trigger task of %s started with stage %d.", type.toString(), stage);
		task.start();
	}

	@Override
	public Quest clone()
	{
		Quest q = new Quest(internalID, questName, new ArrayList<>(outline), reward, new ArrayList<>(stages), questNPC);
		q.setRequirements(new EnumMap<>(requirements));
		q.registerVersion(version);
		q.setTriggers(new EnumMap<>(triggerMap));
		q.registerSettings(setting);
		return q;
	}

	public static void synchronizeLocal(Quest q)
	{
		for (Player p : Bukkit.getOnlinePlayers())
		{
			QuestPlayerData pd = QuestUtil.getData(p);
			for (QuestProgress qp : pd.getProgresses())
			{
				if (QuestValidater.detailedValidate(q, qp.getQuest()))
				{
					pd.forceQuit(q, true);
					break;
				}
			}
		}
		QuestStorage.localQuests.put(q.getInternalID(), q);
	}


}
