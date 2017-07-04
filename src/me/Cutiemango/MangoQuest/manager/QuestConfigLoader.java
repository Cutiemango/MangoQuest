package me.Cutiemango.MangoQuest.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import me.Cutiemango.MangoQuest.I18n;
import me.Cutiemango.MangoQuest.Main;
import me.Cutiemango.MangoQuest.QuestConfigSettings;
import me.Cutiemango.MangoQuest.QuestIO;
import me.Cutiemango.MangoQuest.QuestStorage;
import me.Cutiemango.MangoQuest.QuestUtil;
import me.Cutiemango.MangoQuest.advancements.QuestAdvancement;
import me.Cutiemango.MangoQuest.advancements.QuestAdvancement.FrameType;
import me.Cutiemango.MangoQuest.advancements.QuestAdvancement.Trigger;
import me.Cutiemango.MangoQuest.conversation.ConversationManager;
import me.Cutiemango.MangoQuest.conversation.FriendConversation;
import me.Cutiemango.MangoQuest.conversation.QuestBaseAction;
import me.Cutiemango.MangoQuest.conversation.QuestChoice;
import me.Cutiemango.MangoQuest.conversation.QuestConversation;
import me.Cutiemango.MangoQuest.conversation.StartTriggerConversation;
import me.Cutiemango.MangoQuest.conversation.QuestBaseAction.EnumAction;
import me.Cutiemango.MangoQuest.conversation.QuestChoice.Choice;
import me.Cutiemango.MangoQuest.model.Quest;
import me.Cutiemango.MangoQuest.model.QuestNPCData;
import me.Cutiemango.MangoQuest.model.QuestReward;
import me.Cutiemango.MangoQuest.model.QuestStage;
import me.Cutiemango.MangoQuest.model.QuestTrigger;
import me.Cutiemango.MangoQuest.model.QuestVersion;
import me.Cutiemango.MangoQuest.model.RequirementType;
import me.Cutiemango.MangoQuest.model.QuestTrigger.TriggerObject;
import me.Cutiemango.MangoQuest.model.QuestTrigger.TriggerType;
import me.Cutiemango.MangoQuest.questobjects.QuestObjectBreakBlock;
import me.Cutiemango.MangoQuest.questobjects.QuestObjectConsumeItem;
import me.Cutiemango.MangoQuest.questobjects.QuestObjectDeliverItem;
import me.Cutiemango.MangoQuest.questobjects.QuestObjectKillMob;
import me.Cutiemango.MangoQuest.questobjects.QuestObjectReachLocation;
import me.Cutiemango.MangoQuest.questobjects.QuestObjectTalkToNPC;
import me.Cutiemango.MangoQuest.questobjects.SimpleQuestObject;
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
		loadConversation();
		loadQuests();
		loadStartConversation();
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
				QuestAdvancement ad = new QuestAdvancement(new NamespacedKey(Main.instance, "story:" + key));
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
				ad.addTrigger(new Trigger(me.Cutiemango.MangoQuest.advancements.QuestAdvancement.TriggerType.IMPOSSIBLE, "mangoquest"));
				ad.build().add();
				count++;
			}
		}
		QuestChatManager.logCmd(Level.INFO, "讀取了 " + count + " 個進度檔案(測試中)。");
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
		for (NPC npc : CitizensAPI.getNPCRegistry())
		{
			QuestStorage.NPCMap.put(npc.getId(), new QuestNPCData());
		}
		if (npc.isSection("NPC"))
		{
			for (String s : npc.getSection("NPC"))
			{
				int id = Integer.parseInt(s);
				if (CitizensAPI.getNPCRegistry().getById(id) != null)
				{
					count++;
					QuestNPCData npcdata = QuestStorage.NPCMap.get(id);
					if (npc.isSection("NPC." + id + ".Messages"))
					{
						for (String i : npc.getSection("NPC." + id + ".Messages"))
						{
							List<String> list = npc.getStringList("NPC." + id + ".Messages." + i);
							Set<String> set = new HashSet<>();
							set.addAll(list);
							npcdata.putMessage(Integer.parseInt(i), set);
						}
					}
					QuestStorage.NPCMap.put(id, npcdata);
				}
				else
				{
					QuestChatManager.logCmd(Level.WARNING, I18n.locMsg("Cmdlog.NPCNotValid", s));
					continue;
				}
			}
		}
		QuestChatManager.logCmd(Level.INFO, I18n.locMsg("Cmdlog.NPCLoaded", Integer.toString(count)));
	}

	public void loadConfig()
	{
		if (config.getString("language") != null)
		{
			String[] lang = config.getString("language").split("_");
			if (lang.length > 1)
			{
				QuestConfigSettings.LOCALE_USING = new Locale(lang[0], lang[1]);
				I18n.init(QuestConfigSettings.LOCALE_USING);
				QuestChatManager.logCmd(Level.INFO, I18n.locMsg("Cmdlog.UsingLocale", config.getString("language")));
				return;
			}
		}
		QuestConfigSettings.LOCALE_USING = QuestConfigSettings.DEFAULT_LOCALE;
		I18n.init(QuestConfigSettings.LOCALE_USING);
		QuestChatManager.logCmd(Level.WARNING, I18n.locMsg("Cmdlog.LocaleNotFound"));
		QuestChatManager.logCmd(Level.INFO, I18n.locMsg("Cmdlog.UsingDefaultLocale", QuestConfigSettings.DEFAULT_LOCALE.toString()));
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
		List<Choice> list = new ArrayList<>();
		for (String id : conv.getSection("Choices"))
		{
			TextComponent q = new TextComponent(QuestChatManager.translateColor(conv.getString("Choices." + id + ".Question")));
			for (String num : conv.getSection("Choices." + id + ".Options"))
			{
				String name = conv.getString("Choices." + id + ".Options." + num + ".OptionName");
				Choice c = new Choice(name, loadConvAction(conv.getStringList("Choices." + id + ".Options." + num + ".OptionActions")));
				list.add(Integer.parseInt(num) - 1, c);
			}
			QuestChoice choice = new QuestChoice(q, list);
			QuestStorage.Choices.put(id, choice);
			count++;
		}

		QuestChatManager.logCmd(Level.INFO, I18n.locMsg("Cmdlog.ChoiceLoaded", Integer.toString(count)));
	}

	public void loadQuests()
	{
		if (!quest.isSection("Quests"))
			return;
		int totalcount = 0;
		for (String internal : quest.getSection("Quests"))
		{
			String questname = quest.getString("Quests." + internal + ".QuestName");
			List<String> questoutline = quest.getStringList("Quests." + internal + ".QuestOutline");
			List<QuestStage> stages = new ArrayList<>();
			for (String stagecount : quest.getSection("Quests." + internal + ".Stages"))
			{
				List<SimpleQuestObject> objs = new ArrayList<>();
				int scount = Integer.parseInt(stagecount);
				for (String objcount : quest.getSection("Quests." + internal + ".Stages." + scount))
				{
					int ocount = Integer.parseInt(objcount);
					String s = quest.getString("Quests." + internal + ".Stages." + scount + "." + ocount + ".ObjectType");
					SimpleQuestObject obj = null;
					int n;
					switch (s)
					{
						case "DELIVER_ITEM":
							n = quest.getInt("Quests." + internal + ".Stages." + scount + "." + ocount + ".TargetNPC");
							if (CitizensAPI.getNPCRegistry().getById(n) == null)
							{
								QuestChatManager.logCmd(Level.WARNING, I18n.locMsg("Cmdlog.NPCNotValid", Integer.toString(n)));
								continue;
							}
							obj = new QuestObjectDeliverItem(CitizensAPI.getNPCRegistry().getById(n),
									quest.getItemStack("Quests." + internal + ".Stages." + scount + "." + ocount + ".Item"),
									quest.getInt("Quests." + internal + ".Stages." + scount + "." + ocount + ".Item.Amount"));
							break;
						case "TALK_TO_NPC":
							n = quest.getInt("Quests." + internal + ".Stages." + scount + "." + ocount + ".TargetNPC");
							if (CitizensAPI.getNPCRegistry().getById(n) == null)
							{
								QuestChatManager.logCmd(Level.WARNING, I18n.locMsg("Cmdlog.NPCNotValid", Integer.toString(n)));
								continue;
							}
							obj = new QuestObjectTalkToNPC(CitizensAPI.getNPCRegistry().getById(n));
						case "KILL_MOB":
							String name = null;
							if (quest.getString("Quests." + internal + ".Stages." + scount + "." + ocount + ".MythicMob") != null)
							{
								if (!Main.instance.initManager.hasMythicMobEnabled())
								{
									QuestChatManager.logCmd(Level.SEVERE, I18n.locMsg("Cmdlog.MTMNotInstalled"));
									continue;
								}
								name = quest.getString("Quests." + internal + ".Stages." + scount + "." + ocount + ".MythicMob");
								try
								{
									obj = new QuestObjectKillMob(Main.instance.initManager.getMTMPlugin().getAPIHelper().getMythicMob(name),
											quest.getInt("Quests." + internal + ".Stages." + scount + "." + ocount + ".Amount"));
								}
								catch (Exception e)
								{
									QuestChatManager.logCmd(Level.WARNING, I18n.locMsg("Cmdlog.MTMMobNotFound", name));
									continue;
								}
							}
							else
								if (quest.getString("Quests." + internal + ".Stages." + scount + "." + ocount + ".MobName") != null)
								{
									name = quest.getString("Quests." + internal + ".Stages." + scount + "." + ocount + ".MobName");
									obj = new QuestObjectKillMob(
											EntityType.valueOf(
													quest.getString("Quests." + internal + ".Stages." + scount + "." + ocount + ".MobType")),
											quest.getInt("Quests." + internal + ".Stages." + scount + "." + ocount + ".Amount"), name);
								}
								else
									if (quest.getString("Quests." + internal + ".Stages." + scount + "." + ocount + ".MobType") != null)
										obj = new QuestObjectKillMob(
												EntityType.valueOf(
														quest.getString("Quests." + internal + ".Stages." + scount + "." + ocount + ".MobType")),
												quest.getInt("Quests." + internal + ".Stages." + scount + "." + ocount + ".Amount"), null);
							break;
						case "BREAK_BLOCK":
							obj = new QuestObjectBreakBlock(
									Material.getMaterial(
											quest.getString("Quests." + internal + ".Stages." + scount + "." + ocount + ".BlockType")),
									Short.parseShort(
											Integer.toString(quest.getInt("Quests." + internal + ".Stages." + scount + "." + ocount + ".SubID"))),
									quest.getInt("Quests." + internal + ".Stages." + scount + "." + ocount + ".Amount"));
							break;
						case "CONSUME_ITEM":
							obj = new QuestObjectConsumeItem(
									quest.getItemStack("Quests." + internal + ".Stages." + scount + "." + ocount + ".Item"),
									quest.getInt("Quests." + internal + ".Stages." + scount + "." + ocount + ".Item.Amount"));
							break;
						case "REACH_LOCATION":
							String[] splited = quest.getString("Quests." + internal + ".Stages." + scount + "." + ocount + ".Location").split(":");
							Location loc = new Location(Bukkit.getWorld(splited[0]), Double.parseDouble(splited[1]), Double.parseDouble(splited[2]),
									Double.parseDouble(splited[3]));
							obj = new QuestObjectReachLocation(loc,
									quest.getInt("Quests." + internal + ".Stages." + scount + "." + ocount + ".Range"),
									quest.getString("Quests." + internal + ".Stages." + scount + "." + ocount + ".LocationName"));
							break;
						default:
							QuestChatManager.logCmd(Level.WARNING, I18n.locMsg("Cmdlog.NoValidObject", internal));
							continue;
					}
					if (quest.getString("Quests." + internal + ".Stages." + scount + "." + ocount + ".ActivateConversation") != null)
					{
						QuestConversation conv = ConversationManager.getConversation(
								quest.getString("Quests." + internal + ".Stages." + scount + "." + ocount + ".ActivateConversation"));
						if (conv != null)
							obj.setConversation(conv);
						else
						{
							QuestChatManager.logCmd(Level.WARNING, I18n.locMsg("Cmdlog.NoValidConversation", internal));
							continue;
						}
					}
					objs.add(obj);
				}

				QuestStage qs = new QuestStage(null, null, objs);
				stages.add(qs);
			}
			QuestReward reward = new QuestReward();
			if (quest.isSection("Quests." + internal + ".Rewards.Item"))
			{
				for (String temp : quest.getSection("Quests." + internal + ".Rewards.Item"))
				{
					reward.addItem(quest.getItemStack("Quests." + internal + ".Rewards.Item." + Integer.parseInt(temp)));
				}
			}
			if (quest.getDouble("Quests." + internal + ".Rewards.Money") != 0)
				reward.addMoney(quest.getDouble("Quests." + internal + ".Rewards.Money"));
			if (quest.getInt("Quests." + internal + ".Rewards.Experience") != 0)
				reward.addExp(quest.getInt("Quests." + internal + ".Rewards.Experience"));
			if (quest.isSection("Quests." + internal + ".Rewards.FriendlyPoint"))
			{
				for (String s : quest.getSection("Quests." + internal + ".Rewards.FriendlyPoint"))
				{
					reward.addFriendPoint(Integer.parseInt(s), quest.getInt("Quests." + internal + ".Rewards.FriendlyPoint." + s));
				}
			}

			if (quest.getStringList("Quests." + internal + ".Rewards.Commands") != null)
			{
				List<String> l = quest.getStringList("Quests." + internal + ".Rewards.Commands");
				for (String s : l)
				{
					reward.addCommand(s);
				}
			}

			if (Main.instance.initManager.hasCitizensEnabled() && quest.contains("Quests." + internal + ".QuestNPC"))
			{
				NPC npc = null;
				if (!(quest.getInt("Quests." + internal + ".QuestNPC") == -1)
						&& CitizensAPI.getNPCRegistry().getById(quest.getInt("Quests." + internal + ".QuestNPC")) != null)
					npc = CitizensAPI.getNPCRegistry().getById(quest.getInt("Quests." + internal + ".QuestNPC"));
				Quest q = new Quest(internal, questname, questoutline, reward, stages, npc);
				if (quest.getString("Quests." + internal + ".MessageRequirementNotMeet") != null)
					q.setFailMessage(quest.getString("Quests." + internal + ".MessageRequirementNotMeet"));
				// Requirements
				if (quest.isSection("Quests." + internal + ".Requirements"))
				{
					if (quest.getInt("Quests." + internal + ".Requirements.Level") != 0)
						q.getRequirements().put(RequirementType.LEVEL, quest.getInt("Quests." + internal + ".Requirements.Level"));
					if (quest.getStringList("Quests." + internal + ".Requirements.Quest") != null)
						q.getRequirements().put(RequirementType.QUEST, quest.getStringList("Quests." + internal + ".Requirements.Quest"));
					if (quest.isSection("Quests." + internal + ".Requirements.Item"))
					{
						List<ItemStack> l = new ArrayList<>();
						for (String i : quest.getSection("Quests." + internal + ".Requirements.Item"))
						{
							l.add(quest.getItemStack("Quests." + internal + ".Requirements.Item." + i));
						}
						q.getRequirements().put(RequirementType.ITEM, l);
					}
					if (quest.getStringList("Quests." + internal + ".Requirements.Scoreboard") != null)
						q.getRequirements().put(RequirementType.SCOREBOARD,
								quest.getStringList("Quests." + internal + ".Requirements.Scoreboard"));
					if (quest.getStringList("Quests." + internal + ".Requirements.NBTTag") != null)
						q.getRequirements().put(RequirementType.NBTTAG, quest.getStringList("Quests." + internal + ".Requirements.NBTTag"));
				}

				// Triggers
				if (quest.getStringList("Quests." + internal + ".TriggerEvents") != null)
				{
					List<QuestTrigger> list = new ArrayList<>();
					for (String tri : quest.getStringList("Quests." + internal + ".TriggerEvents"))
					{
						String[] Stri = tri.split(" ");
						QuestTrigger trigger = null;
						TriggerType type = TriggerType.valueOf(Stri[0]);
						TriggerObject obj;
						switch (type)
						{
							case TRIGGER_STAGE_START:
							case TRIGGER_STAGE_FINISH:
								obj = TriggerObject.valueOf(Stri[2]);
								String s = Stri[3];
								if (Stri.length > 4)
								{
									for (int k = 4; k < Stri.length; k++)
									{
										s += " " + Stri[k];
									}
								}
								trigger = new QuestTrigger(type, obj, Integer.parseInt(Stri[1]), s);
								break;
							default:
								obj = TriggerObject.valueOf(Stri[1]);
								String t = Stri[2];
								if (Stri.length > 3)
								{
									for (int k = 3; k < Stri.length; k++)
									{
										t += " " + Stri[k];
									}
								}
								trigger = new QuestTrigger(type, obj, t);
								break;
						}
						list.add(trigger);
					}
					q.setTriggers(list);
				}
				if (quest.getBoolean("Quests." + internal + ".Redoable"))
				{
					q.setRedoable(true);
					q.setRedoDelay(quest.getLong("Quests." + internal + ".RedoDelayMilliseconds"));
				}
				if (quest.getLong("Quests." + internal + ".Version") == 0L)
				{
					QuestVersion ver = QuestVersion.instantVersion();
					quest.set("Quests." + internal + ".Version", ver.getVersion());
					q.registerVersion(ver);
				}
				else
				{
					QuestVersion qc = new QuestVersion(quest.getLong("Quests." + internal + ".Version"));
					q.registerVersion(qc);
				}
				QuestStorage.Quests.put(internal, q);
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
}
