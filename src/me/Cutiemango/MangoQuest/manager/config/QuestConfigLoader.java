package me.Cutiemango.MangoQuest.manager.config;

import me.Cutiemango.MangoQuest.*;
import me.Cutiemango.MangoQuest.conversation.*;
import me.Cutiemango.MangoQuest.conversation.QuestBaseAction.EnumAction;
import me.Cutiemango.MangoQuest.conversation.QuestChoice.Choice;
import me.Cutiemango.MangoQuest.manager.CustomObjectManager;
import me.Cutiemango.MangoQuest.manager.QuestChatManager;
import me.Cutiemango.MangoQuest.manager.QuestNPCManager;
import me.Cutiemango.MangoQuest.manager.QuestValidater;
import me.Cutiemango.MangoQuest.model.Quest;
import me.Cutiemango.MangoQuest.objects.GUIOption;
import me.Cutiemango.MangoQuest.objects.QuestNPC;
import me.Cutiemango.MangoQuest.objects.QuestStage;
import me.Cutiemango.MangoQuest.objects.QuestVersion;
import me.Cutiemango.MangoQuest.objects.RequirementType;
import me.Cutiemango.MangoQuest.objects.reward.QuestReward;
import me.Cutiemango.MangoQuest.objects.reward.RewardChoice;
import me.Cutiemango.MangoQuest.objects.trigger.TriggerObject;
import me.Cutiemango.MangoQuest.objects.trigger.TriggerObject.TriggerObjectType;
import me.Cutiemango.MangoQuest.objects.trigger.TriggerType;
import me.Cutiemango.MangoQuest.questobject.SimpleQuestObject;
import me.Cutiemango.MangoQuest.questobject.objects.*;
import net.citizensnpcs.api.npc.NPC;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.*;
import java.util.logging.Level;

public class QuestConfigLoader
{
	public QuestConfigLoader(QuestConfigManager cm)
	{
		manager = cm;
	}
	
	private QuestConfigManager manager;

	public void loadAll()
	{
		loadTranslation();
		loadChoice();
		loadQuests();
		loadConversation();
		loadGUIOptions();
		loadNPC();
		
		SimpleQuestObject.initObjectNames();
	}

	public void loadConfig()
	{
		QuestIO config = manager.getConfig();
		// Load i18n
		boolean useModifiedLanguage = false;

		if (config.getBoolean("useModifiedLanguage"))
			useModifiedLanguage = true;

		if (config.getString("language") != null)
		{
			String[] lang = config.getString("language").split("_");
			if (lang.length > 1)
			{
				ConfigSettings.LOCALE_USING = new Locale(lang[0], lang[1]);
				I18n.init(ConfigSettings.LOCALE_USING, useModifiedLanguage);
				QuestChatManager.logCmd(Level.INFO, I18n.locMsg("Cmdlog.UsingLocale", config.getString("language")));
			}
		}
		else
		{
			ConfigSettings.LOCALE_USING = ConfigSettings.DEFAULT_LOCALE;
			I18n.init(ConfigSettings.LOCALE_USING, useModifiedLanguage);
			QuestChatManager.logCmd(Level.WARNING, I18n.locMsg("Cmdlog.LocaleNotFound"));
			QuestChatManager.logCmd(Level.INFO, I18n.locMsg("Cmdlog.UsingDefaultLocale", ConfigSettings.DEFAULT_LOCALE.toString()));
			config.set("language", ConfigSettings.DEFAULT_LOCALE.toString());
		}

		// Use weak item check
		ConfigSettings.USE_WEAK_ITEM_CHECK = config.getBoolean("useWeakItemCheck");
		DebugHandler.log(5, "[Config] useWeakItemCheck=" + ConfigSettings.USE_WEAK_ITEM_CHECK);

		// Enable Skip
		ConfigSettings.ENABLE_SKIP = config.getBoolean("enableSkip");
		DebugHandler.log(5, "[Config] enableSkip=" + ConfigSettings.ENABLE_SKIP);


		// Save Interval
		if (config.getInt("saveIntervalInSeconds") != 0)
			ConfigSettings.PLAYER_DATA_SAVE_INTERVAL = config.getInt("saveIntervalInSeconds");
		else
		{
			ConfigSettings.PLAYER_DATA_SAVE_INTERVAL = 600;
			config.set("saveIntervalInSeconds", 600);
		}

		// Debug mode
		DebugHandler.DEBUG_LEVEL = config.getInt("debugLevel");
		if (config.getInt("debugLevel") > 0)
			QuestChatManager.logCmd(Level.WARNING, I18n.locMsg("Cmdlog.DebugMode", Integer.toString(DebugHandler.DEBUG_LEVEL)));
		else
			config.set("debugLevel", 0);

		// Rightclick Settings
		ConfigSettings.USE_RIGHT_CLICK_MENU = config.getBoolean("useRightClickMenu");
		DebugHandler.log(5, "[Config] useRightClickMenu=" + ConfigSettings.USE_RIGHT_CLICK_MENU);

		// Login Message
		ConfigSettings.POP_LOGIN_MESSAGE = config.getBoolean("popLoginMessage");
		DebugHandler.log(5, "[Config] popLoginMessage=" + ConfigSettings.POP_LOGIN_MESSAGE);

		// Plugin Prefix
		if (config.getString("pluginPrefix") != null)
			QuestStorage.prefix = QuestChatManager.translateColor(config.getString("pluginPrefix"));
		else
			config.set("pluginPrefix", "&6MangoQuest>");

		// Maximum Quests
		if (config.getInt("maxQuestAmount") != 0)
			ConfigSettings.MAXIMUM_QUEST_AMOUNT = config.getInt("maxQuestAmount");
		else
		{
			ConfigSettings.MAXIMUM_QUEST_AMOUNT = 4;
			config.set("maxQuestAmount", 4);
		}

		// Scoreboard settings
		if (!config.contains("enableScoreboard"))
			config.set("enableScoreboard", true);
		ConfigSettings.ENABLE_SCOREBOARD = config.getBoolean("enableScoreboard");

		DebugHandler.log(5, "[Config] enableScoreboard=" + ConfigSettings.ENABLE_SCOREBOARD);

		if (!config.contains("scoreboardMaxCanTakeQuestAmount"))
			config.set("scoreboardMaxCanTakeQuestAmount", 3);
		ConfigSettings.MAXIMUM_DISPLAY_QUEST_AMOUNT = config.getInt("scoreboardMaxCanTakeQuestAmount");

		// Particle Settings
		if (!config.contains("useParticleEffect"))
			config.set("useParticleEffect", true);
		ConfigSettings.USE_PARTICLE_EFFECT = config.getBoolean("useParticleEffect");

		DebugHandler.log(5, "[Config] useParticleEffect=" + ConfigSettings.USE_PARTICLE_EFFECT);

		// Database Settings
		ConfigSettings.USE_DATABASE = config.getBoolean("useDatabase");
		DebugHandler.log(5, "[Config] useDatabase=" + ConfigSettings.USE_DATABASE);

		if (ConfigSettings.USE_DATABASE)
		{
			ConfigSettings.DATABASE_ADDRESS = config.getString("databaseAddress");
			ConfigSettings.DATABASE_PORT = config.getInt("databasePort");
			ConfigSettings.DATABASE_USER = config.getString("databaseUser");
			ConfigSettings.DATABASE_PASSWORD = config.getString("databasePassword");

			DebugHandler.log(5, "[Config] Database login credentials loaded!");
			DebugHandler.log(5, String.format("[Config] address=%s, port=%d, user=%s, pw=%s", ConfigSettings.DATABASE_ADDRESS, ConfigSettings.DATABASE_PORT, ConfigSettings.DATABASE_USER, ConfigSettings.DATABASE_PASSWORD));
		}
	}

	public void loadTranslation()
	{
		QuestIO translation = manager.getTranslation();
		if (translation.isSection("Material"))
		{
			for (String s : translation.getSection("Material"))
			{
				Material mat = Material.getMaterial(s);
				if (mat != null)
					QuestStorage.TranslationMap.put(mat, translation.getString("Material." + s));
				else
					DebugHandler.log(5, "Material " + s + " is null during the compatible search. Skipping...");
			}
		}
		
		if (translation.isSection("EntityType"))
		{
			for (String e : translation.getSection("EntityType"))
			{
				try
				{
					QuestStorage.EntityTypeMap.put(EntityType.valueOf(e), translation.getConfig().getString("EntityType." + e));
				}
				catch (IllegalArgumentException ignored)
				{
				}
			}
		}
		QuestChatManager.logCmd(Level.INFO, I18n.locMsg("Cmdlog.TranslationLoaded"));
	}
	
	private List<File> getAllFiles(String directoryName)
	{
		File directory = new File(directoryName);
		if (!directory.exists())
			directory.mkdirs();

		List<File> resultList = new ArrayList<>();

		File[] fList = directory.listFiles();
		resultList.addAll(Arrays.asList(fList));
		for (File file : fList)
		{
			if (file.isDirectory())
				resultList.addAll(getAllFiles(file.getAbsolutePath()));
		}
		return resultList;
	}

	public void loadNPC()
	{
		QuestIO npc = manager.getNPC();
		int count = 0;
		HashMap<Integer, Integer> cloneMap = new HashMap<>();
		if (npc.contains("NPC") && npc.isSection("NPC"))
		{
			for (Integer id : npc.getIntegerSection("NPC"))
			{
				DebugHandler.log(5, "[Config] Loading NPC data id=" + id);
				if (!QuestValidater.validateNPC(Integer.toString(id)))
				{
					QuestChatManager.logCmd(Level.WARNING, I18n.locMsg("Cmdlog.NPCNotValid", Integer.toString(id)));
					continue;
				}
				NPC npcReal = Main.getHooker().getNPC(id);
				QuestNPC npcdata = QuestNPCManager.hasData(id) ? QuestNPCManager.getNPCData(id) : new QuestNPC(npcReal);
				if (npc.getString("NPC." + id + ".Clone") != null)
					cloneMap.put(id, npc.getInt("NPC." + id + ".Clone"));
				else
				{
					if (npc.isSection("NPC." + id + ".Messages"))
					{
						for (String i : npc.getSection("NPC." + id + ".Messages"))
						{
							List<String> list = npc.getStringList("NPC." + id + ".Messages." + i);
							HashSet<String> set = new HashSet<>();
							set.addAll(list);
							npcdata.putMessage(Integer.parseInt(i), set);
						}
					}
					if (npc.getStringList("NPC." + id + ".GUIOptions") != null)
					{
						HashSet<GUIOption> set = new HashSet<>();
						for (String s : npc.getStringList("NPC." + id + ".GUIOptions"))
						{
							if (s == null)
							{
								QuestChatManager.logCmd(Level.SEVERE, I18n.locMsg("Cmdlog.OptionNotFound", s, Integer.toString(id)));
								continue;
							}
							GUIOption option = QuestNPCManager.getOption(s);
							set.add(option);
						}
						npcdata.setOptions(set);
					}
				}
				QuestNPCManager.updateNPC(npcReal, npcdata);
				DebugHandler.log(5, "[Config] Successfully loaded NPC data id=" + id);
				count++;
			}
		}
		for (Integer id : cloneMap.keySet())
		{
			QuestNPCManager.updateNPC(Main.getHooker().getNPC(id), QuestNPCManager.getNPCData(cloneMap.get(id)));
		}
		QuestChatManager.logCmd(Level.INFO, I18n.locMsg("Cmdlog.NPCLoaded", Integer.toString(count)));
	}

	public void loadConversation()
	{
		List<File> files = getAllFiles(Main.getInstance().getDataFolder() + File.separator + "conversation");
		
		int count = 0;
		for (File f : files)
		{
			QuestIO conv = new QuestIO(f);
			
			if (conv.isSection("Conversations"))
			{
				for (String id : conv.getSection("Conversations"))
				{
					String name = conv.getString("Conversations." + id + ".ConversationName");
					List<String> act = conv.getStringList("Conversations." + id + ".ConversationActions");
					NPC npc = Main.getHooker().getNPC(conv.getInt("Conversations." + id + ".NPC"));
					QuestConversation qc;
					if (conv.getBoolean("Conversations." + id + ".FriendConversation"))
					{
						qc = new FriendConversation(name, id, npc, loadConvAction(act), conv.getInt("Conversations." + id + ".FriendPoint"));
						QuestStorage.FriendConvs.add((FriendConversation)qc);
						QuestStorage.Conversations.put(id, (FriendConversation)qc);
					}
					else if (conv.getBoolean("Conversations." + id + ".StartTriggerConversation"))
					{
						Quest q = QuestUtil.getQuest(conv.getString("Conversations." + id + ".StartQuest"));
						if (q != null)
						{
							StartTriggerConversation sconv = new StartTriggerConversation(name, id, npc, loadConvAction(act), q);
							sconv.setAcceptActions(loadConvAction(conv.getStringList("Conversations." + id + ".AcceptActions")));
							sconv.setDenyActions(loadConvAction(conv.getStringList("Conversations." + id + ".DenyActions")));
							sconv.setAcceptMessage(conv.getString("Conversations." + id + ".AcceptMessage"));
							sconv.setDenyMessage(conv.getString("Conversations." + id + ".DenyMessage"));
							sconv.setQuestFullMessage(conv.getString("Conversations." + id + ".QuestFullMessage"));
							QuestStorage.Conversations.put(id, sconv);
							QuestStorage.StartConvs.put(q, sconv);
						}
					}
					else
					{
						qc = new QuestConversation(name, id, npc, loadConvAction(act));
						QuestStorage.Conversations.put(id, qc);
					}
					DebugHandler.log(5, "[Config] Successfully loaded conversation id=" + id);
					count++;
				}
			}
		}
		
		QuestChatManager.logCmd(Level.INFO, I18n.locMsg("Cmdlog.ConversationLoaded", Integer.toString(count)));
	}

	public void loadChoice()
	{
		List<File> files = getAllFiles(Main.getInstance().getDataFolder() + File.separator + "choice");
		
		int count = 0;
		for (File f : files)
		{
			QuestIO choice = new QuestIO(f);
			if (!choice.isSection("Choices"))
				continue;
			for (String id : choice.getSection("Choices"))
			{
				List<Choice> list = new ArrayList<>();
				TextComponent q = new TextComponent(QuestChatManager.translateColor(choice.getString("Choices." + id + ".Question")));
				for (int i : choice.getIntegerSection("Choices." + id + ".Options"))
				{
					String name = choice.getString("Choices." + id + ".Options." + i + ".OptionName");
					Choice c = new Choice(name, loadConvAction(choice.getStringList("Choices." + id + ".Options." + i + ".OptionActions")));
					if (choice.isSection("Choices." + id + ".Options." + i + ".FriendPointReq"))
					{
						for (int npc : choice.getIntegerSection("Choices." + id + ".Options." + i + ".FriendPointReq"))
						{
							c.setFriendPointReq(npc, choice.getInt("Choices." + id + ".Options." + i + ".FriendPointReq." + npc));
						}
					}
					list.add(i - 1, c);
				}
				QuestChoice c = new QuestChoice(q, list);
				QuestStorage.Choices.put(id, c);
				DebugHandler.log(5, "[Config] Successfully loaded choice id=" + id);
				count++;
			}
		}
		
		QuestChatManager.logCmd(Level.INFO, I18n.locMsg("Cmdlog.ChoiceLoaded", Integer.toString(count)));
	}
	
	public void loadGUIOptions()
	{
		QuestIO npc = manager.getNPC();
		if (!npc.isSection("GUIOptions"))
			return;
		int count = 0;
		for (String internal : npc.getSection("GUIOptions"))
		{
			String path = "GUIOptions." + internal + ".";
			String displayText = npc.getString(path + "DisplayText");
			List<TriggerObject> list = new ArrayList<>();
			for (String s : npc.getStringList(path + "ClickEvent"))
			{
				String[] split = s.split(" ");
				list.add(new TriggerObject(TriggerObjectType.valueOf(split[0]), QuestUtil.convertArgsString(split, 1), -1));
			}
			
			GUIOption option = new GUIOption(internal, displayText, list);
			
			if (npc.getString(path + "HoverText") != null)
				option.setHoverText(npc.getString(path + "HoverText"));
			if (npc.isSection(path + "Requirements"))
				option.setRequirementMap(loadRequirements(npc, path));
			QuestNPCManager.registerOption(internal, option);
			DebugHandler.log(5, "[Config] Successfully loaded GUIOption id=" + internal);
			count++;
		}
		
		QuestChatManager.logCmd(Level.INFO, I18n.locMsg("Cmdlog.OptionLoaded", Integer.toString(count)));
	}

	public void loadQuests()
	{
		List<File> files = getAllFiles(Main.getInstance().getDataFolder() + File.separator + "quest");
		
		int count = 0;
		for (File f : files)
		{
			QuestIO quest = new QuestIO(f);
			if (!quest.isSection("Quests"))
				continue;
			for (String internal : quest.getSection("Quests"))
			{
				String qpath = "Quests." + internal + ".";
				String questname = quest.getString(qpath + "QuestName");
				List<String> questoutline = quest.getStringList(qpath + "QuestOutline");
				
				// Stages
				List<QuestStage> stages = loadStages(quest, internal);
				QuestReward reward = loadReward(quest, internal);
				
				if (Main.getHooker().hasCitizensEnabled() && quest.contains(qpath + "QuestNPC"))
				{
					NPC npc = null;
					if (quest.getInt(qpath + "QuestNPC") != -1
							&& QuestValidater.validateNPC(Integer.toString(quest.getInt(qpath + "QuestNPC"))))
						npc = Main.getHooker().getNPC(quest.getInt(qpath + "QuestNPC"));
		
					registerNPC(npc);
					
					Quest q = new Quest(internal, questname, questoutline, reward, stages, npc);
					if (quest.getString(qpath + "MessageRequirementNotMeet") != null)
						q.setFailMessage(quest.getString(qpath + "MessageRequirementNotMeet"));
					
					// Requirements
					q.setRequirements(loadRequirements(quest, qpath));;
					q.initRequirements();

					// Triggers
					loadTriggers(quest, q);
					
					if (quest.getBoolean(qpath + "Redoable"))
					{
						q.setRedoable(true);
						q.setRedoDelay(quest.getLong(qpath + "RedoDelayMilliseconds"));
					}
					if (quest.getLong(qpath + "Version") == 0L)
					{
						QuestVersion ver = QuestVersion.instantVersion();
						quest.set(qpath + "Version", ver.getVersion());
						q.registerVersion(ver);
					}
					else
					{
						QuestVersion qc = new QuestVersion(quest.getLong(qpath + "Version"));
						q.registerVersion(qc);
					}
					
					q.getSettings().toggle(quest.getBoolean(qpath + "Visibility.onTake"),
											quest.getBoolean(qpath + "Visibility.onProgress"),
											quest.getBoolean(qpath + "Visibility.onFinish"),
											quest.getBoolean(qpath + "Visibility.onInteraction"));
					q.setQuitable(quest.getBoolean(qpath + "QuitSettings.Quitable"));
					if (quest.getString(qpath + "WorldLimit") != null && Bukkit.getWorld(quest.getString(qpath + "WorldLimit")) != null)
						q.setWorldLimit(Bukkit.getWorld(quest.getString(qpath + "WorldLimit")));
					if (quest.getBoolean(qpath + "TimeLimited"))
					{
						q.setTimeLimited(quest.getBoolean(qpath + "TimeLimited"));
						q.setTimeLimit(quest.getLong(qpath + "TimeLimitMilliseconds"));
					}
					q.setUsePermission(quest.getBoolean(qpath + "UsePermission"));
					q.setQuitAcceptMsg(quest.getString(qpath + "QuitSettings.QuitAcceptMsg"));
					q.setQuitCancelMsg(quest.getString(qpath + "QuitSettings.QuitCancelMsg"));
					
					QuestStorage.Quests.put(internal, q);
					if (npc != null)
					{
						QuestNPCManager.getNPCData(npc.getId()).registerQuest(q);
						if (!reward.hasRewardNPC())
							reward.setRewardNPC(npc);
						DebugHandler.log(5, "[Config] Successfully registered Quest of id=" + q.getInternalID() + " into NPC of id=" + npc.getId() + "'s data.");
					}
					if (reward.hasRewardNPC())
					{
						registerNPC(reward.getRewardNPC());
						QuestNPCManager.getNPCData(reward.getRewardNPC().getId()).registerReward(q);
					}
					count++;
				}
				else
				{
					QuestChatManager.logCmd(Level.SEVERE, I18n.locMsg("Cmdlog.NPCError", questname));
					continue;
				}
			}
		}
		QuestChatManager.logCmd(Level.INFO, I18n.locMsg("Cmdlog.QuestLoaded", Integer.toString(count)));
	}
	
	private List<QuestStage> loadStages(QuestIO quest, String id)
	{
		String qpath = "Quests." + id + ".";
		List<QuestStage> list = new ArrayList<>();
		if (quest.isSection(qpath + "Stages"))
		{
			for (String stagecount : quest.getSection(qpath + "Stages"))
			{
				List<SimpleQuestObject> objs = new ArrayList<>();
				int scount = Integer.parseInt(stagecount);
				for (String objcount : quest.getSection(qpath + "Stages." + scount))
				{
					int ocount = Integer.parseInt(objcount);
					String loadPath = qpath + "Stages." + scount + "." + ocount + ".";
					String objType = quest.getString(loadPath + "ObjectType");
					SimpleQuestObject obj;
					switch (objType)
					{
						case "DELIVER_ITEM":
							obj = new QuestObjectDeliverItem();
							break;
						case "TALK_TO_NPC":
							obj = new QuestObjectTalkToNPC();
							break;
						case "KILL_MOB":
							obj = new QuestObjectKillMob();
							break;
						case "BREAK_BLOCK":
							obj = new QuestObjectBreakBlock();
							break;
						case "CONSUME_ITEM":
							obj = new QuestObjectConsumeItem();
							break;
						case "REACH_LOCATION":
							obj = new QuestObjectReachLocation();
							break;
						case "FISHING":
							obj = new QuestObjectFishing();
							break;
						case "CUSTOM_OBJECT":
							if (CustomObjectManager.hasCustomObject(quest.getString(loadPath + "ObjectClass")))
								obj = CustomObjectManager.getSpecificObject(quest.getString(loadPath + "ObjectClass"));
							else
							{
								QuestChatManager.logCmd(Level.SEVERE, I18n.locMsg("CustomObject.ObjectNotFound", quest.getString(loadPath + "ObjectClass")));
								continue;
							}
							break;
						default:
							QuestChatManager.logCmd(Level.WARNING, I18n.locMsg("Cmdlog.NoValidObject", id));
							continue;
					}
					if (!obj.load(quest, loadPath))
					{
						QuestChatManager.logCmd(Level.SEVERE, I18n.locMsg("Cmdlog.ObjectLoadingError", id, Integer.toString(scount), Integer.toString(ocount)));
						continue;
					}
					if (quest.getString(qpath + "Stages." + scount + "." + ocount + ".ActivateConversation") != null)
						obj.setConversation(quest.getString(qpath + "Stages." + scount + "." + ocount + ".ActivateConversation"));
					objs.add(obj);
				}
				QuestStage qs = new QuestStage(objs);
				list.add(qs);
			}
		}
		return list;
	}
	
	private EnumMap<RequirementType, Object> loadRequirements(QuestIO config, String path)
	{
		EnumMap<RequirementType, Object> map = new EnumMap<>(RequirementType.class);
		if (config.isSection(path + "Requirements"))
		{
			map.put(RequirementType.LEVEL, config.getInt(path + "Requirements.Level"));
			map.put(RequirementType.MONEY, config.getDouble(path + "Requirements.Money"));
			
			List<String> list = new ArrayList<>();
			if (config.getStringList(path + "Requirements.Quest") != null)
				list = config.getStringList(path + "Requirements.Quest");
			map.put(RequirementType.QUEST, list);
			
			List<ItemStack> l = new ArrayList<>();
			if (config.isSection(path + "Requirements.Item"))
			{
				for (String i : config.getSection(path + "Requirements.Item"))
				{
					l.add(config.getItemStack(path + "Requirements.Item." + i));
				} 
			}
			map.put(RequirementType.ITEM, l);
			
			HashMap<Integer, Integer> fMap = new HashMap<>();
			if (config.isSection(path + "Requirements.FriendPoint"))
			{
				for (Integer id : config.getIntegerSection(path + "Requirements.FriendPoint"))
				{
					fMap.put(id, config.getInt(path + "Requirements.FriendPoint." + id));
				}
			}
			map.put(RequirementType.FRIEND_POINT, fMap);

			if (Main.getHooker().hasSkillAPIEnabled())
			{
				map.put(RequirementType.SKILLAPI_CLASS, "none");
				map.put(RequirementType.SKILLAPI_LEVEL, 0);
				if (config.getString(path + "Requirements.SkillAPIClass") != null)
					map.put(RequirementType.SKILLAPI_CLASS, config.getString(path + "Requirements.SkillAPIClass"));
				if (config.getInt(path + "Requirements.SkillAPILevel") != 0)
					map.put(RequirementType.SKILLAPI_LEVEL, config.getInt(path + "Requirements.SkillAPILevel"));
			}
		}
		return map;
	}
	
	private void loadTriggers(QuestIO quest, Quest q)
	{
		String triggerPath = "Quests." + q.getInternalID() + ".TriggerEvents";
		EnumMap<TriggerType, List<TriggerObject>> map = new EnumMap<>(TriggerType.class);
		if (quest.isSection(triggerPath))
		{
			for (String type : quest.getSection(triggerPath))
			{
				TriggerType t = TriggerType.valueOf(type);
				List<TriggerObject> list = new ArrayList<>();
				switch(t)
				{
					case TRIGGER_ON_FINISH:
					case TRIGGER_ON_QUIT:
					case TRIGGER_ON_TAKE:
						for (String obj : quest.getStringList(triggerPath + "." + type))
						{
							String[] split = obj.split(" ");
							String object = QuestUtil.convertArgsString(split, 1);
							list.add(new TriggerObject(TriggerObjectType.valueOf(split[0]), object, -1));
						}
						break;
					case TRIGGER_STAGE_FINISH:
					case TRIGGER_STAGE_START:
						for (String obj : quest.getStringList(triggerPath + "." + type))
						{
							String[] split = obj.split(" ");
							String object = QuestUtil.convertArgsString(split, 2);
							list.add(new TriggerObject(TriggerObjectType.valueOf(split[1]), object, Integer.parseInt(split[0])));
						}
						break;
				}
				map.put(t, list);
			}
		}
		q.setTriggers(map);
		return;
	}
	
	private QuestReward loadReward(QuestIO quest, String id)
	{
		String qpath = "Quests." + id + ".";
		QuestReward reward = new QuestReward();
		reward.setRewardAmount(quest.getInt(qpath + "Rewards.RewardAmount"));
		reward.setInstantGiveReward(quest.getBoolean(qpath + "Rewards.InstantGiveReward"));
		
		if (quest.isSection(qpath + "Rewards.Choice"))
		{
			List<RewardChoice> list = new ArrayList<>();
			for (int index : quest.getIntegerSection(qpath + "Rewards.Choice"))
			{
				if (index > 9)
					continue;
				RewardChoice choice = new RewardChoice(new ArrayList<ItemStack>());
				for (int itemIndex : quest.getIntegerSection(qpath + "Rewards.Choice." + index))
				{
					choice.addItem(quest.getItemStack(qpath + "Rewards.Choice." + index + "." + itemIndex));
				}
				list.add(choice);
			}
			reward.setChoice(list);
		}
		
		if (quest.getString(qpath + "Rewards.RewardNPC") != null)
		{
			if (QuestValidater.validateNPC(quest.getString(qpath + "Rewards.RewardNPC")))
				reward.setRewardNPC(Main.getHooker().getNPC(quest.getString(qpath + "Rewards.RewardNPC")));
		}
		
		if (quest.getDouble(qpath + "Rewards.Money") != 0)
			reward.addMoney(quest.getDouble(qpath + "Rewards.Money"));
		if (quest.getInt(qpath + "Rewards.Experience") != 0)
			reward.addExp(quest.getInt(qpath + "Rewards.Experience"));
		if (quest.isSection(qpath + "Rewards.FriendlyPoint"))
		{
			for (String s : quest.getSection(qpath + "Rewards.FriendlyPoint"))
			{
				reward.addFriendPoint(Integer.parseInt(s), quest.getInt(qpath + "Rewards.FriendlyPoint." + s));
			}
		}

		if (quest.getStringList(qpath + "Rewards.Commands") != null)
		{
			List<String> l = quest.getStringList(qpath + "Rewards.Commands");
			for (String s : l)
			{
				reward.addCommand(s);
			}
		}
		if (Main.getHooker().hasSkillAPIEnabled())
		{
			if (quest.getInt(qpath + "Rewards.SkillAPIExp") != 0)
				reward.setSkillAPIExp(quest.getInt(qpath + "Rewards.SkillAPIExp"));
		}
		return reward;
	}
	
	

	private List<QuestBaseAction> loadConvAction(List<String> fromlist)
	{
		List<QuestBaseAction> list = new ArrayList<>();
		EnumAction e = null;
		for (String s : fromlist)
		{
			if (s.contains("#"))
			{
				try
				{
					e = EnumAction.valueOf(s.split("#")[0]);
				}
				catch (Exception ex)
				{
					QuestChatManager.logCmd(Level.WARNING, I18n.locMsg("Cmdlog.EnumActionError", s.split("#")[0]));
					continue;
				}
				if (e != null)
				{
					QuestBaseAction action;
					switch (e)
					{
						case CHOICE:
						case NPC_TALK:
						case WAIT:
						case SENTENCE:
						case FINISH:
						case COMMAND:
						case COMMAND_PLAYER:
						case COMMAND_PLAYER_OP:
							action = new QuestBaseAction(e, s.split("#")[1]);
							break;
						case BUTTON:
						case CHANGE_LINE:
						case CHANGE_PAGE:
						case TAKE_QUEST:
						case EXIT:
						default:
							action = new QuestBaseAction(e, null);
							break;
					}
					list.add(action);
				}
			}
		}
		return list;
	}
	
	private void registerNPC(NPC npc)
	{
		if (npc != null && !QuestNPCManager.hasData(npc.getId()))
		{
			DebugHandler.log(5, "[Config] NPC of id=" + npc.getId() + " registered.");
			QuestNPCManager.registerNPC(npc);
		}
	}
}
