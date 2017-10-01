package me.Cutiemango.MangoQuest.model;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import com.sucy.skill.SkillAPI;
import me.Cutiemango.MangoQuest.Main;
import me.Cutiemango.MangoQuest.QuestStorage;
import me.Cutiemango.MangoQuest.QuestUtil;
import me.Cutiemango.MangoQuest.I18n;
import me.Cutiemango.MangoQuest.data.QuestPlayerData;
import me.Cutiemango.MangoQuest.data.QuestProgress;
import me.Cutiemango.MangoQuest.manager.QuestChatManager;
import me.Cutiemango.MangoQuest.manager.QuestValidater;
import me.Cutiemango.MangoQuest.questobjects.SimpleQuestObject;
import net.citizensnpcs.api.npc.NPC;
import net.md_5.bungee.api.ChatColor;

public class Quest
{
	// Only Initialize with Command
	public Quest()
	{
		for (RequirementType t : RequirementType.values())
		{
			switch (t)
			{
				case ITEM:
					requirements.put(t, new ArrayList<ItemStack>());
					break;
				case LEVEL:
					requirements.put(t, 0);
					break;
				case MONEY:
					requirements.put(t, 0.0D);
					break;
				case NBTTAG:
					requirements.put(t, new ArrayList<String>());
					break;
				case QUEST:
					requirements.put(t, new ArrayList<String>());
					break;
				case SCOREBOARD:
					requirements.put(t, new ArrayList<String>());
					break;
				case SKILLAPI_CLASS:
					requirements.put(t, "none");
					break;
				case SKILLAPI_LEVEL:
					requirements.put(t, 0);
					break;
				default:
					break;
			}
		}
		version = QuestVersion.instantVersion();
	}

	public Quest(String InternalID, String name, List<String> QuestOutline, QuestReward reward, List<QuestStage> stages, NPC npc)
	{
		this.InternalID = InternalID;
		this.QuestName = QuestChatManager.translateColor(name);
		this.outline = QuestOutline;
		this.reward = reward;
		this.stages = stages;
		this.QuestNPC = npc;

		for (RequirementType t : RequirementType.values())
		{
			switch (t)
			{
				case ITEM:
					requirements.put(t, new ArrayList<ItemStack>());
					break;
				case LEVEL:
					requirements.put(t, 0);
					break;
				case MONEY:
					requirements.put(t, 0.0D);
					break;
				case NBTTAG:
					requirements.put(t, new ArrayList<String>());
					break;
				case QUEST:
					requirements.put(t, new ArrayList<String>());
					break;
				case SCOREBOARD:
					requirements.put(t, new ArrayList<String>());
					break;
				case SKILLAPI_CLASS:
					requirements.put(t, "none");
					break;
				case SKILLAPI_LEVEL:
					requirements.put(t, 0);
					break;
			}
		}

		version = QuestVersion.instantVersion();
	}

	private NPC QuestNPC;
	private String InternalID;
	private String QuestName;

	private List<String> outline = new ArrayList<>();

	private List<QuestStage> stages = new ArrayList<>();
	private QuestReward reward = new QuestReward();
	private QuestSetting setting = new QuestSetting();

	private EnumMap<RequirementType, Object> requirements = new EnumMap<>(RequirementType.class);
	private EnumMap<TriggerType, List<TriggerObject>> triggerMap = new EnumMap<>(TriggerType.class);


	private QuestVersion version;

	public String getInternalID()
	{
		return InternalID;
	}

	public void setInternalID(String internalID)
	{
		InternalID = internalID;
	}

	public String getQuestName()
	{
		return QuestName;
	}

	public void setQuestName(String s)
	{
		QuestName = s;
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
		return QuestNPC;
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
		QuestNPC = npc;
	}

	public boolean isCommandQuest()
	{
		return QuestNPC == null;
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
	
	public void trigger(Player p, int index, TriggerType type, int stage)
	{
		if (!hasTrigger(type))
			return;
		if (triggerMap.get(type).isEmpty() || triggerMap.get(type).size() <= index)
			return;
		triggerMap.get(type).get(index).trigger(p, index, type, stage, this);
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
		return I18n.locMsg("QuestQuitMsg.QuitQuest") + setting.quitAcceptMsg;
	}
	
	public void setQuitAcceptMsg(String s)
	{
		setting.quitAcceptMsg = s;
	}
	
	public String getQuitCancelMsg()
	{
		return I18n.locMsg("QuestQuitMsg.Cancel") + setting.quitCancelMsg;
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

	@SuppressWarnings("unchecked")
	public FailResult meetRequirementWith(Player p)
	{
		QuestPlayerData pd = QuestUtil.getData(p);
		for (RequirementType t : requirements.keySet())
		{
			Object value = requirements.get(t);
			switch (t)
			{
				case QUEST:
					for (String q : (List<String>) value)
					{
						if (!pd.hasFinished(QuestUtil.getQuest(q)))
							return new FailResult(RequirementType.QUEST, QuestUtil.getQuest(q));
					}
					break;
				case LEVEL:
					if (!(p.getLevel() >= (Integer) value))
						return new FailResult(RequirementType.LEVEL, (Integer) value);
					break;
				case MONEY:
					if (Main.instance.pluginHooker.hasEconomyEnabled())
					{
						if (!(Main.instance.pluginHooker.getEconomy().getBalance(p) >= (Double) value))
							return new FailResult(RequirementType.MONEY, (Double) value);
					}
					break;
				case ITEM:
					for (ItemStack i : (List<ItemStack>) value)
					{
						if (i == null)
							continue;
						if (!p.getInventory().containsAtLeast(i, i.getAmount()))
							return new FailResult(RequirementType.ITEM, i);
					}
					break;
				case SCOREBOARD:
					for (String s : (List<String>) value)
					{
						s = s.replace(" ", "");
						String[] split;
						if (s.contains(">="))
						{
							split = s.split(">=");
							if (Bukkit.getScoreboardManager().getMainScoreboard().getObjective(split[0]) == null)
							{
								QuestChatManager.logCmd(Level.WARNING, "任務 " + InternalID + " 的記分板內容有錯誤，找不到伺服器上名為 " + split[0] + " 的記分板物件資料！");
								return new FailResult(RequirementType.SCOREBOARD, "");
							}
							if (!(Bukkit.getScoreboardManager().getMainScoreboard().getObjective(split[0]).getScore(p.getName()).getScore() >= Integer
									.parseInt(split[1])))
								return new FailResult(RequirementType.SCOREBOARD, "");
						}
						else
							if (s.contains("<="))
							{
								split = s.split("<=");
								if (Bukkit.getScoreboardManager().getMainScoreboard().getObjective(split[0]) == null)
								{
									QuestChatManager.logCmd(Level.WARNING, "任務 " + InternalID + " 的記分板內容有錯誤，找不到伺服器上名為 " + split[0] + " 的記分板物件資料！");
									return new FailResult(RequirementType.SCOREBOARD, "");
								}
								if (!(Bukkit.getScoreboardManager().getMainScoreboard().getObjective(split[0]).getScore(p.getName())
										.getScore() <= Integer.parseInt(split[1])))
									return new FailResult(RequirementType.SCOREBOARD, "");
							}
							else
								if (s.contains("=="))
								{
									split = s.split("==");
									if (Bukkit.getScoreboardManager().getMainScoreboard().getObjective(split[0]) == null)
									{
										QuestChatManager.logCmd(Level.WARNING, "任務 " + InternalID + " 的記分板內容有錯誤，找不到伺服器上名為 " + split[0] + " 的記分板物件資料！");
										return new FailResult(RequirementType.SCOREBOARD, "");
									}
									if (!(Bukkit.getScoreboardManager().getMainScoreboard().getObjective(split[0]).getScore(p.getName())
											.getScore() == Integer.parseInt(split[1])))
										return new FailResult(RequirementType.SCOREBOARD, "");
								}
					}
					break;
				case NBTTAG:
					for (String n : (List<String>) value)
					{
						if (!Main.instance.handler.hasTag(p, n))
							return new FailResult(RequirementType.NBTTAG, "");
					}
					break;
				case SKILLAPI_CLASS:
					if (!Main.instance.pluginHooker.hasSkillAPIEnabled())
						break;
					if (SkillAPI.hasPlayerData(p))
					{
						if (((String)value).equalsIgnoreCase("none"))
							break;
						if (SkillAPI.getClass((String)value) == null)
							return new FailResult(RequirementType.SKILLAPI_CLASS, I18n.locMsg("Requirements.NotMeet.BadConfig") + "沒有名為 " + value + "的職業。");
						if (!SkillAPI.getPlayerData(p).isClass(SkillAPI.getClass((String)value)))
								return new FailResult(RequirementType.SKILLAPI_CLASS, SkillAPI.getClass((String)value).getName());
					}
					break;
				case SKILLAPI_LEVEL:
					if (!Main.instance.pluginHooker.hasSkillAPIEnabled())
						break;
					if (SkillAPI.hasPlayerData(p))
					{
						if (!(SkillAPI.getPlayerData(p).getMainClass().getLevel() >= (Integer)value))
							return new FailResult(RequirementType.SKILLAPI_LEVEL, value);
					}
					break;
			}
		}
		return new FailResult(null, "");
	}

	@Override
	public Quest clone()
	{
		Quest q = new Quest(InternalID, QuestName, outline, reward, stages, QuestNPC);
		q.setRequirements(requirements);
		q.registerVersion(version);
		q.setTriggers(triggerMap);
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

	public class FailResult
	{
		Object obj;
		RequirementType type;

		public FailResult(RequirementType t, Object o)
		{
			type = t;
			obj = o;
		}

		public boolean succeed()
		{
			return type == null;
		}

		public RequirementType getFailType()
		{
			return type;
		}

		public String getMessage()
		{
			String s = "";
			if (type == null)
				return s;
			switch (type)
			{
				case ITEM:
					ItemStack item = (ItemStack) obj;
					if (item.hasItemMeta() && item.getItemMeta().hasDisplayName())
						s = ChatColor.RED + I18n.locMsg("Requirements.NotMeet.Item") + item.getItemMeta().getDisplayName();
					else
						s = ChatColor.RED + I18n.locMsg("Requirements.NotMeet.Item")
								+ QuestUtil.translate(item.getType(), item.getDurability());
					break;
				case LEVEL:
					s = ChatColor.RED + I18n.locMsg("Requirements.NotMeet.Level") + (Integer) obj;
					break;
				case MONEY:
					s = ChatColor.RED + I18n.locMsg("Requirements.NotMeet.Money") + (Double) obj;
					break;
				case SCOREBOARD:
				case NBTTAG:
					s = ChatColor.RED + I18n.locMsg("Requirements.NotMeet.Special");
					break;
				case QUEST:
					if (obj == null)
						s = ChatColor.RED + I18n.locMsg("Requirements.NotMeet.Special");
					else
						s = ChatColor.RED + I18n.locMsg("Requirements.NotMeet.Quest") + ((Quest) obj).getQuestName();
					break;
				case SKILLAPI_CLASS:
					s = ChatColor.RED + I18n.locMsg("Requirements.NotMeet.SkillAPIClass") + obj.toString();
					break;
				case SKILLAPI_LEVEL:
					s = ChatColor.RED + I18n.locMsg("Requirements.NotMeet.SkillAPILevel") + (Integer) obj;
					break;

			}
			return s;
		}

	}

}
