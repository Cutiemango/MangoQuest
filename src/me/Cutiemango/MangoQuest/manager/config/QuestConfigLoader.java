package me.Cutiemango.MangoQuest.manager.config;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import me.Cutiemango.MangoQuest.I18n;
import me.Cutiemango.MangoQuest.Main;
import me.Cutiemango.MangoQuest.ConfigSettings;
import me.Cutiemango.MangoQuest.QuestIO;
import me.Cutiemango.MangoQuest.QuestStorage;
import me.Cutiemango.MangoQuest.QuestUtil;
import me.Cutiemango.MangoQuest.advancements.QuestAdvancement;
import me.Cutiemango.MangoQuest.advancements.QuestAdvancement.FrameType;
import me.Cutiemango.MangoQuest.advancements.QuestAdvancement.Trigger;
import me.Cutiemango.MangoQuest.conversation.FriendConversation;
import me.Cutiemango.MangoQuest.conversation.QuestBaseAction;
import me.Cutiemango.MangoQuest.conversation.QuestChoice;
import me.Cutiemango.MangoQuest.conversation.QuestConversation;
import me.Cutiemango.MangoQuest.conversation.StartTriggerConversation;
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
import me.Cutiemango.MangoQuest.objects.requirement.RequirementType;
import me.Cutiemango.MangoQuest.objects.reward.QuestReward;
import me.Cutiemango.MangoQuest.objects.reward.RewardChoice;
import me.Cutiemango.MangoQuest.objects.trigger.TriggerObject;
import me.Cutiemango.MangoQuest.objects.trigger.TriggerType;
import me.Cutiemango.MangoQuest.objects.trigger.TriggerObject.TriggerObjectType;
import me.Cutiemango.MangoQuest.questobject.SimpleQuestObject;
import me.Cutiemango.MangoQuest.questobject.objects.QuestObjectBreakBlock;
import me.Cutiemango.MangoQuest.questobject.objects.QuestObjectConsumeItem;
import me.Cutiemango.MangoQuest.questobject.objects.QuestObjectDeliverItem;
import me.Cutiemango.MangoQuest.questobject.objects.QuestObjectKillMob;
import me.Cutiemango.MangoQuest.questobject.objects.QuestObjectReachLocation;
import me.Cutiemango.MangoQuest.questobject.objects.QuestObjectTalkToNPC;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.md_5.bungee.api.chat.TextComponent;

public class QuestConfigLoader
{
	public QuestConfigLoader(QuestConfigManager cm)
	{
		manager = cm;
		config = manager.ConfigIO;
	}
	
	private QuestConfigManager manager;
	
	private QuestIO quest;
	private QuestIO conv;
	private QuestIO advancement;
	private QuestIO translation;
	private QuestIO npc;
	private QuestIO config;
	
	public void init()
	{
		quest = manager.QuestsIO;
		conv = manager.ConversationIO;
		advancement = manager.AdvancementIO;
		translation = manager.TranslateIO;
		npc = manager.NPCIO;
	}
	
	public void loadAll()
	{
		loadTranslation();
		loadChoice();
		loadQuests();
		loadConversation();
		loadStartConversation();
		loadGUIOptions();
		loadNPC();
		
		SimpleQuestObject.initObjectNames();
		
		if (Main.isUsingUpdatedVersion())
			loadAdvancement();
	}
	
	@SuppressWarnings("deprecation")
	public void loadAdvancement()
	{
		int count = 0;
		if (advancement.isSection("Advancements"))
		{
			for (String key : advancement.getSection("Advancements"))
			{
				QuestAdvancement ad = new QuestAdvancement(new NamespacedKey(Main.getInstance(), "story:" + key));
				if (advancement.getString("Advancements." + key + ".Title") != null)
					ad.title(QuestChatManager.translateColor(advancement.getString("Advancements." + key + ".Title")));
				if (advancement.getString("Advancements." + key + ".Description") != null)
					ad.description(QuestChatManager.translateColor(advancement.getString("Advancements." + key + ".Description")));
				if (advancement.getString("Advancements." + key + ".Frame") != null)
					ad.frame(FrameType.valueOf(advancement.getString("Advancements." + key + ".Frame")));
				if (advancement.getString("Advancements." + key + ".Icon") != null)
				{
					if (advancement.getString("Advancements." + key + ".Icon").contains(":"))
					{
						String[] split = advancement.getString("Advancements." + key + ".Icon").split(":");
						ad.icon(Material.getMaterial(Integer.parseInt(split[0])));
						ad.iconSubID(Short.parseShort(split[1]));
					}
					else
						ad.icon(Material.getMaterial(advancement.getInt("Advancements." + key + ".Icon")));
				}
				ad.announcement(advancement.getBoolean("Advancements." + key + ".Announcement"));
				if (advancement.getString("Advancements." + key + ".Background") != null)
					ad.background(advancement.getString("Advancements." + key + ".Background"));
				if (advancement.getString("Advancements." + key + ".Parent") != null)
					ad.parent("mangoquest:story:" + advancement.getString("Advancements." + key + ".Parent"));
				ad.addTrigger(new Trigger(QuestAdvancement.TriggerType.IMPOSSIBLE, "mangoquest"));
				ad.build().add();
				count++;
			}
		}
		QuestChatManager.logCmd(Level.INFO, I18n.locMsg("Cmdlog.AdvancementLoaded", Integer.toString(count)));
	}

	public void loadTranslation()
	{
		if (translation.isSection("Material"))
		{
			for (String s : translation.getSection("Material"))
			{
				if (Material.getMaterial(s) == null)
					continue;
				HashMap<Short, String> map = new HashMap<>();
				if (translation.isSection("Material." + s))
				{
					for (String index : translation.getSection("Material." + s))
					{
						Short data = Short.parseShort(index);
						map.put(data, translation.getString("Material." + s + "." + index));
					}
				}
				else
				{
					map.put((short) 0, translation.getString("Material." + s));
				}
				QuestStorage.TranslateMap.put(Material.getMaterial(s), map);
				map = new HashMap<>();
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
				catch (IllegalArgumentException ex)
				{
					continue;
				}
			}
		}
		QuestChatManager.logCmd(Level.INFO, I18n.locMsg("Cmdlog.TranslationLoaded"));
	}

	public void loadNPC()
	{
		int count = 0;
		HashMap<Integer, Integer> cloneMap = new HashMap<>();
		if (npc.isSection("NPC"))
		{
			for (Integer id : npc.getIntegerSection("NPC"))
			{
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
							GUIOption option = QuestNPCManager.getOption(s);
							if (s == null)
							{
								QuestChatManager.logCmd(Level.SEVERE, I18n.locMsg("Cmdlog.OptionNotFound", s, Integer.toString(id)));
								continue;
							}
							set.add(option);
						}
						npcdata.setOptions(set);
					}
				}
				QuestNPCManager.updateNPC(npcReal, npcdata);
				count++;
			}
		}
		for (Integer id : cloneMap.keySet())
		{
			QuestNPCManager.updateNPC(Main.getHooker().getNPC(id), QuestNPCManager.getNPCData(cloneMap.get(id)));
		}
		QuestChatManager.logCmd(Level.INFO, I18n.locMsg("Cmdlog.NPCLoaded", Integer.toString(count)));
	}

	public void loadConfig()
	{
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
				I18n.init(ConfigSettings.LOCALE_USING, !useModifiedLanguage);
				QuestChatManager.logCmd(Level.INFO, I18n.locMsg("Cmdlog.UsingLocale", config.getString("language")));
			}
		}
		else
		{
			ConfigSettings.LOCALE_USING = ConfigSettings.DEFAULT_LOCALE;
			I18n.init(ConfigSettings.LOCALE_USING, !useModifiedLanguage);
			QuestChatManager.logCmd(Level.WARNING, I18n.locMsg("Cmdlog.LocaleNotFound"));
			QuestChatManager.logCmd(Level.INFO, I18n.locMsg("Cmdlog.UsingDefaultLocale", ConfigSettings.DEFAULT_LOCALE.toString()));
			config.set("language", ConfigSettings.DEFAULT_LOCALE.toString());
		}
		
		// Debug mode
		ConfigSettings.DEBUG_MODE = config.getBoolean("debug");
		if (config.getBoolean("debug"))
			QuestChatManager.logCmd(Level.WARNING, I18n.locMsg("Cmdlog.DebugMode"));
		else
			config.set("debug", false);
		
		// Rightclick Settings
		ConfigSettings.USE_RIGHT_CLICK_MENU = config.getBoolean("useRightClickMenu");
		
		// Login Message
		ConfigSettings.POP_LOGIN_MESSAGE = config.getBoolean("popLoginMessage");
		
		// Plugin Prefix
		if (config.getString("pluginPrefix") != null)
			QuestStorage.prefix = QuestChatManager.translateColor(config.getString("pluginPrefix"));
		else
			config.set("pluginPrefix", "&6MangoQuest>");
		
		// Maxium Quests
		if (config.getInt("maxQuestAmount") != 0)
			ConfigSettings.MAXIUM_QUEST_AMOUNT = config.getInt("maxQuestAmount");
		else
		{
			ConfigSettings.MAXIUM_QUEST_AMOUNT = 4;
			config.set("maxQuestAmount", 4);
		}
		
		// Scoreboard settings
		if (!config.contains("enableScoreboard"))
			config.set("enableScoreboard", true);
		ConfigSettings.ENABLE_SCOREBOARD = config.getBoolean("enableScoreboard");
		
		if (!config.contains("scoreboardMaxCanTakeQuestAmount"))
			config.set("scoreboardMaxCanTakeQuestAmount", 3);
		ConfigSettings.MAXMIUM_DISPLAY_TAKEQUEST_AMOUNT = config.getInt("scoreboardMaxCanTakeQuestAmount");
		
		// Particle Settings
		if (!config.contains("useParticleEffect"))
			config.set("useParticleEffect", true);
		ConfigSettings.USE_PARTICLE_EFFECT = config.getBoolean("useParticleEffect");
	}

	public void loadConversation()
	{
		if (conv.isSection("Conversations"))
		{
			int count = 0;
			for (String id : conv.getSection("Conversations"))
			{
				String name = conv.getString("Conversations." + id + ".ConversationName");
				List<String> act = conv.getStringList("Conversations." + id + ".ConversationActions");
				NPC npc = CitizensAPI.getNPCRegistry().getById(conv.getInt("Conversations." + id + ".NPC"));
				QuestConversation qc;
				if (conv.getBoolean("Conversations." + id + ".FriendConversation"))
				{
					qc = new FriendConversation(name, id, npc, loadConvAction(act), conv.getInt("Conversations." + id + ".FriendPoint"));
					QuestStorage.FriendConvs.add((FriendConversation)qc);
				}
				else if (conv.getBoolean("Conversations." + id + ".StartTriggerConversation"))
					continue;
				else
					qc = new QuestConversation(name, id, npc, loadConvAction(act));
				QuestStorage.Conversations.put(id, qc);
				count++;
			}

			QuestChatManager.logCmd(Level.INFO, I18n.locMsg("Cmdlog.ConversationLoaded", Integer.toString(count)));
		}
	}
	
	public void loadStartConversation()
	{
		if (conv.isSection("Conversations"))
		{
			int count = 0;
			for (String id : conv.getSection("Conversations"))
			{
				if (conv.getBoolean("Conversations." + id + ".StartTriggerConversation"))
				{
					Quest q = QuestUtil.getQuest(conv.getString("Conversations." + id + ".StartQuest"));
					if (q != null)
					{
						String name = conv.getString("Conversations." + id + ".ConversationName");
						List<String> act = conv.getStringList("Conversations." + id + ".ConversationActions");
						NPC npc = CitizensAPI.getNPCRegistry().getById(conv.getInt("Conversations." + id + ".NPC"));
						StartTriggerConversation sconv = new StartTriggerConversation(name, id, npc, loadConvAction(act), q);
						sconv.setAcceptActions(loadConvAction(conv.getStringList("Conversations." + id + ".AcceptActions")));
						sconv.setDenyActions(loadConvAction(conv.getStringList("Conversations." + id + ".DenyActions")));
						sconv.setAcceptMessage(conv.getString("Conversations." + id + ".AcceptMessage"));
						sconv.setDenyMessage(conv.getString("Conversations." + id + ".DenyMessage"));
						sconv.setQuestFullMessage(conv.getString("Conversations." + id + ".QuestFullMessage"));
						QuestStorage.Conversations.put(id, sconv);
						QuestStorage.StartConvs.put(q, sconv);
						count++;
					}
				}
			}
			QuestChatManager.logCmd(Level.INFO, I18n.locMsg("Cmdlog.TriggerConversationLoaded", Integer.toString(count)));
			return;
		}
	}

	public void loadChoice()
	{
		if (!conv.isSection("Choices"))
			return;
		int count = 0;
		for (String id : conv.getSection("Choices"))
		{
			List<Choice> list = new ArrayList<>();
			TextComponent q = new TextComponent(QuestChatManager.translateColor(conv.getString("Choices." + id + ".Question")));
			for (int i : conv.getIntegerSection("Choices." + id + ".Options"))
			{
				String name = conv.getString("Choices." + id + ".Options." + i + ".OptionName");
				Choice c = new Choice(name, loadConvAction(conv.getStringList("Choices." + id + ".Options." + i + ".OptionActions")));
				if (conv.isSection("Choices." + id + ".Options." + i + ".FriendPointReq"))
				{
					for (int npc : conv.getIntegerSection("Choices." + id + ".Options." + i + ".FriendPointReq"))
					{
						c.setFriendPointReq(npc, conv.getInt("Choices." + id + ".Options." + i + ".FriendPointReq." + npc));
					}
				}
				list.add(i - 1, c);
			}
			QuestChoice choice = new QuestChoice(q, list);
			QuestStorage.Choices.put(id, choice);
			count++;
		}

		QuestChatManager.logCmd(Level.INFO, I18n.locMsg("Cmdlog.ChoiceLoaded", Integer.toString(count)));
	}
	
	public void loadGUIOptions()
	{
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
			count++;
		}
		QuestChatManager.logCmd(Level.INFO, I18n.locMsg("Cmdlog.OptionLoaded", Integer.toString(count)));
	}

	public void loadQuests()
	{
		if (!quest.isSection("Quests"))
			return;
		int totalcount = 0;
		for (String internal : quest.getSection("Quests"))
		{
			String qpath = "Quests." + internal + ".";
			String questname = quest.getString(qpath + "QuestName");
			List<String> questoutline = quest.getStringList(qpath + "QuestOutline");
			
			// Stages
			List<QuestStage> stages = loadStages(internal);
			QuestReward reward = loadReward(internal);
			
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
				loadTriggers(q);
				
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
										quest.getBoolean(qpath + "Visibility.onFinish"));
				q.setQuitable(quest.getBoolean(qpath + "QuitSettings.Quitable"));
				if (quest.getString(qpath + "WorldLimit") != null && Bukkit.getWorld(quest.getString(qpath + "WorldLimit")) != null)
					q.setWorldLimit(Bukkit.getWorld(quest.getString(qpath + "WorldLimit")));
				if (quest.getBoolean(qpath + "TImeLimited"))
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
					Main.debug("NPC: " + npc.getId() + ", Quest: " + q.getInternalID());
				}
				if (reward.hasRewardNPC())
				{
					registerNPC(reward.getRewardNPC());
					QuestNPCManager.getNPCData(reward.getRewardNPC().getId()).registerReward(q);
				}
				totalcount++;
			}
			else
			{
				QuestChatManager.logCmd(Level.SEVERE, I18n.locMsg("Cmdlog.NPCError", questname));
				continue;
			}
		}
		quest.save();
		QuestChatManager.logCmd(Level.INFO, I18n.locMsg("Cmdlog.QuestLoaded", Integer.toString(totalcount)));
	}
	
	private List<QuestStage> loadStages(String id)
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
					SimpleQuestObject obj = null;
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
						case "CUSTOM_OBJECT":
							if (CustomObjectManager.exist(quest.getString(loadPath + "ObjectClass")))
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
	
	private void loadTriggers(Quest q)
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
	
	private QuestReward loadReward(String id)
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
						case COMMAND:
						case WAIT:
						case SENTENCE:
						case FINISH:
						case GIVE_ADVANCEMENT:
							action = new QuestBaseAction(e, s.split("#")[1]);
							break;
						case BUTTON:
						case CHANGE_LINE:
						case CHANGE_PAGE:
						case TAKE_QUEST:
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
		if (npc != null)
		{
			Main.debug("NPC registered: " + npc.getId());
			QuestNPCManager.registerNPC(npc);
		}
	}
}
