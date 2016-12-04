package me.Cutiemango.MangoQuest.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.Cutiemango.MangoQuest.Main;
import me.Cutiemango.MangoQuest.QuestIO;
import me.Cutiemango.MangoQuest.QuestNPC;
import me.Cutiemango.MangoQuest.QuestStorage;
import me.Cutiemango.MangoQuest.QuestUtil;
import me.Cutiemango.MangoQuest.conversation.QuestBaseAction;
import me.Cutiemango.MangoQuest.conversation.QuestBaseAction.EnumAction;
import me.Cutiemango.MangoQuest.conversation.QuestChoice;
import me.Cutiemango.MangoQuest.conversation.QuestChoice.Choice;
import me.Cutiemango.MangoQuest.conversation.QuestConversation;
import me.Cutiemango.MangoQuest.model.Quest;
import me.Cutiemango.MangoQuest.model.QuestReward;
import me.Cutiemango.MangoQuest.model.QuestStage;
import me.Cutiemango.MangoQuest.model.QuestTrigger;
import me.Cutiemango.MangoQuest.model.QuestTrigger.TriggerObject;
import me.Cutiemango.MangoQuest.model.QuestTrigger.TriggerType;
import me.Cutiemango.MangoQuest.model.RequirementType;
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

public class QuestConfigManager {
	
	private QuestIO PlayerIO;
	private QuestIO QuestsIO;
	private QuestIO TranslateIO;
	private QuestIO NPCIO;
	
	private QuestIO ConversationIO;

	private Main plugin;
	
	public QuestConfigManager(Main pl){
		PlayerIO = new QuestIO("players.yml");
		QuestsIO = new QuestIO("quests.yml");
		TranslateIO = new QuestIO("translations.yml");
		NPCIO = new QuestIO("npc.yml");
		ConversationIO = new QuestIO("conversations.yml");
		
		plugin = pl;

		loadChoice();
		loadTranslation();
	}
	
	public QuestIO getPlayerIO(){
		return PlayerIO;
	}
	
	private void loadTranslation() {
		if (TranslateIO.isSection("Material")){
			for (String s : TranslateIO.getSection("Material")){
				if (Material.getMaterial(s) != null)
					QuestStorage.TranslateMap.put(Material.getMaterial(s), TranslateIO.getConfig().getString("Material." + s));
			}
		}
		if (TranslateIO.isSection("EntityType")){
			for (String e : TranslateIO.getSection("EntityType")){
				try{
					QuestStorage.EntityTypeMap.put(EntityType.valueOf(e), TranslateIO.getConfig().getString("EntityType." + e));
				} catch(IllegalArgumentException ex){
					continue;
				}
			}
		}
		Bukkit.getLogger().info("[MangoQuest] 翻譯檔案讀取完成！");
	}
	
	public void loadNPC(){
		int count = 0;
		if (NPCIO.isSection("NPC")){
			for (String s : NPCIO.getSection("NPC")){
				int id = Integer.parseInt(s);
				if (CitizensAPI.getNPCRegistry().getById(id) != null){
					count++;
					QuestNPC npc = new QuestNPC();
					if (NPCIO.isSection("NPC." + id + ".Messages")){
						for (String i : NPCIO.getSection("NPC." + id + ".Messages")){
							npc.put(Integer.parseInt(i), NPCIO.getString("NPC." + id + ".Messages." + i));
						}
					}
					if (NPCIO.isSection("NPC." + id + ".Conversations")){
						for (String i : NPCIO.getSection("NPC." + id + ".Conversations")){
							npc.put(Integer.parseInt(i), QuestUtil.getConvByName(NPCIO.getString("NPC." + id + ".Conversations." + i)));
						}
					}
					QuestStorage.NPCMap.put(id, npc);
				}
				else{
					Bukkit.getLogger().warning("[NPC讀取] NPC編號為 " + s + " 的NPC不存在！請至npc.yml移除該NPC之相關資料！");
					continue;
				}
			}
		}
		Bukkit.getLogger().info("[NPC讀取] NPC資料讀取完畢。成功讀取了 " + count + " 個NPC對話資料。");
	}
	
	public void removeQuest(Quest q){
		QuestsIO.getConfig().set("Quests." + q.getInternalID(), null);
		Bukkit.getLogger().log(Level.WARNING, "[任務讀取] 任務 " + q.getInternalID() + " 已經刪除所有yml相關資料。");
		QuestsIO.save();
	}
	
	@SuppressWarnings("unchecked")
	public void saveQuest(Quest q){
		QuestsIO.set("Quests." + q.getInternalID() + ".QuestName", q.getQuestName());
		QuestsIO.set("Quests." + q.getInternalID() + ".QuestOutline", q.getQuestOutline());
		if (q.isCommandQuest())
			QuestsIO.set("Quests." + q.getInternalID() + ".QuestNPC", -1);
		else
			QuestsIO.set("Quests." + q.getInternalID() + ".QuestNPC", q.getQuestNPC().getId());
		QuestsIO.set("Quests." + q.getInternalID() + ".Requirements.Level",
				q.getRequirements().get(RequirementType.LEVEL));
		QuestsIO.set("Quests." + q.getInternalID() + ".Requirements.Quest",
				q.getRequirements().get(RequirementType.QUEST));
		int i = 0;
		for (ItemStack is : (List<ItemStack>) q.getRequirements().get(RequirementType.ITEM)) {
			i++;
			QuestsIO.set("Quests." + q.getInternalID() + ".Requirements.Item." + i + ".Material",
					is.getType().toString());
			QuestsIO.set("Quests." + q.getInternalID() + ".Requirements.Item." + i + ".Amount", is.getAmount());
			if (is.hasItemMeta() && is.getItemMeta().hasDisplayName()) {
				QuestsIO.set("Quests." + q.getInternalID() + ".Requirements.Item." + i + ".ItemName",
						is.getItemMeta().getDisplayName());
			}
			if (is.hasItemMeta() && is.getItemMeta().hasLore()){
				QuestsIO.set("Quests." + q.getInternalID() + ".Requirements.Item." + i + ".ItemLore",
						is.getItemMeta().getLore());
			}
		}
		QuestsIO.set("Quests." + q.getInternalID() + ".Requirements.Scoreboard",
				q.getRequirements().get(RequirementType.SCOREBOARD));
		QuestsIO.set("Quests." + q.getInternalID() + ".Requirements.NBTTag",
				q.getRequirements().get(RequirementType.NBTTAG));
		if (q.getFailMessage() != null)
			QuestsIO.set("Quests." + q.getInternalID() + ".MessageRequirementNotMeet", q.getFailMessage());
		QuestsIO.set("Quests." + q.getInternalID() + ".Redoable", q.isRedoable());
		if (q.isRedoable())
			QuestsIO.set("Quests." + q.getInternalID() + ".RedoDelayMilliseconds", q.getRedoDelay());
		List<String> list = new ArrayList<>();
		for (QuestTrigger qt : q.getTriggers()){
			if (qt.getType().equals(TriggerType.TRIGGER_STAGE_START) || qt.getType().equals(TriggerType.TRIGGER_STAGE_FINISH)){
				list.add(qt.getType() + " " + qt.getCount() + " " + qt.getTriggerObject().toString() + " " + qt.getObject().toString());
				continue;
			}
			list.add(qt.getType() + " " + qt.getTriggerObject().toString() + " " + qt.getObject().toString());
		}
		QuestsIO.set("Quests." + q.getInternalID() + ".TriggerEvents", list);
		i = 0;
		int j = 0;
		for (QuestStage s : q.getStages()){
			i++;
			for (SimpleQuestObject obj : s.getObjects()){
				j++;
				QuestsIO.set("Quests." + q.getInternalID() + ".Stages." + i + "." + j + ".ObjectType", obj.getConfigString());
				switch(obj.getConfigString()){
				case "DELIVER_ITEM":
					QuestObjectDeliverItem o = (QuestObjectDeliverItem)obj;
					QuestsIO.set("Quests." + q.getInternalID() + ".Stages." + i + "." + j + ".TargetNPC", o.getTargetNPC().getId());
					QuestsIO.set("Quests." + q.getInternalID() + ".Stages." + i + "." + j + ".Item.Material", o.getItem().getType().toString());
					QuestsIO.set("Quests." + q.getInternalID() + ".Stages." + i + "." + j + ".Item.Amount", o.getItem().getAmount());
					if (o.getItem().hasItemMeta() && o.getItem().getItemMeta().hasDisplayName() && o.getItem().getItemMeta().hasLore()){
						QuestsIO.set("Quests." + q.getInternalID() + ".Stages." + i + "." + j + ".Item.ItemName", o.getItem().getItemMeta().getDisplayName());
						QuestsIO.set("Quests." + q.getInternalID() + ".Stages." + i + "." + j + ".Item.ItemLore", o.getItem().getItemMeta().getLore());
					}
					break;
				case "TALK_TO_NPC":
					QuestObjectTalkToNPC on = (QuestObjectTalkToNPC)obj;
					QuestsIO.set("Quests." + q.getInternalID() + ".Stages." + i + "." + j + ".TargetNPC", on.getTargetNPC().getId());
					break;
				case "KILL_MOB":
					QuestObjectKillMob om = (QuestObjectKillMob)obj;
					QuestsIO.set("Quests." + q.getInternalID() + ".Stages." + i + "." + j + ".Amount", om.getAmount());
					if (om.isMythicObject()){
						QuestsIO.set("Quests." + q.getInternalID() + ".Stages." + i + "." + j + ".MythicMob", om.getMythicMob().getInternalName());
						break;
					}
					QuestsIO.set("Quests." + q.getInternalID() + ".Stages." + i + "." + j + ".MobType", om.getType().toString());
					if (om.hasCustomName())
						QuestsIO.set("Quests." + q.getInternalID() + ".Stages." + i + "." + j + ".MobName", om.getCustomName());
					break;
				case "BREAK_BLOCK":
					QuestObjectBreakBlock ob = (QuestObjectBreakBlock)obj;
					QuestsIO.set("Quests." + q.getInternalID() + ".Stages." + i + "." + j + ".BlockType", ob.getType().toString());
					QuestsIO.set("Quests." + q.getInternalID() + ".Stages." + i + "." + j + ".Amount", ob.getAmount());
					break;
				case "CONSUME_ITEM":
					QuestObjectConsumeItem oi = (QuestObjectConsumeItem)obj;
					QuestsIO.set("Quests." + q.getInternalID() + ".Stages." + i + "." + j + ".Item.Material", oi.getItem().getType().toString());
					QuestsIO.set("Quests." + q.getInternalID() + ".Stages." + i + "." + j + ".Item.Amount", oi.getItem().getAmount());
					break;
				case "REACH_LOCATION":
					QuestObjectReachLocation or = (QuestObjectReachLocation)obj;
					String loc = or.getLocation().getWorld().getName() + ":" + or.getLocation().getX() + ":" + or.getLocation().getY() + ":" + or.getLocation().getZ();
					QuestsIO.set("Quests." + q.getInternalID() + ".Stages." + i + "." + j + ".Location", loc);
					QuestsIO.set("Quests." + q.getInternalID() + ".Stages." + i + "." + j + ".LocationName", or.getName());
					QuestsIO.set("Quests." + q.getInternalID() + ".Stages." + i + "." + j + ".Range", or.getRadius());
					break;
				}
				continue;
			}
			j = 0;
		}
		if (q.getQuestReward().hasItem()){
			int c = 0;
			for (ItemStack is : q.getQuestReward().getItems()){
				c++;
				QuestsIO.set("Quests." + q.getInternalID() + ".Rewards.Item." + c + ".Material", is.getType().toString());
				QuestsIO.set("Quests." + q.getInternalID() + ".Rewards.Item." + c + ".Amount", is.getAmount());
				if (is.hasItemMeta() && is.getItemMeta().hasDisplayName()) {
					QuestsIO.set("Quests." + q.getInternalID() + ".Rewards.Item." + c + ".ItemName",
							is.getItemMeta().getDisplayName());
				}
				if (is.hasItemMeta() && is.getItemMeta().hasLore()){
					QuestsIO.set("Quests." + q.getInternalID() + ".Rewards.Item." + c + ".ItemLore",
							is.getItemMeta().getLore());
				}
			}
		}
		if (q.getQuestReward().hasMoney())
			QuestsIO.set("Quests." + q.getInternalID() + ".Rewards.Money", q.getQuestReward().getMoney());
		if (q.getQuestReward().hasExp())
			QuestsIO.set("Quests." + q.getInternalID() + ".Rewards.Experience", q.getQuestReward().getExp());
		if (q.getQuestReward().hasFriendPoint()){
			for (Integer npc : q.getQuestReward().getFp().keySet()){
				QuestsIO.set("Quests." + q.getInternalID() + ".Rewards.FriendlyPoint." + npc, q.getQuestReward().getFp().get(npc));
			}
		}
		
		System.out.println("[任務讀取] 任務 " + q.getQuestName() + " 已經儲存完成！");
		QuestsIO.save();
	}
	
	public void loadConversation(){
		if (ConversationIO.isSection("Conversations")){
			int count = 0;
			for (String id : ConversationIO.getSection("Conversations")){
				String name = ConversationIO.getString("Conversations." + id + ".ConversationName");
				List<String> act = ConversationIO.getStringList("Conversations." + id + ".ConversationActions");
				NPC npc = CitizensAPI.getNPCRegistry().getById(ConversationIO.getInt("Conversations." + id + ".NPC"));
				QuestConversation conv = new QuestConversation(name, id, npc, loadConvAction(act), ConversationIO.getBoolean("Conversations." + id + ".好感度對話"));
				QuestStorage.Conversations.put(id, conv);
				count++;
			}
		
			System.out.println("[對話讀取] 對話已經讀取完成！讀取了 " + count + " 個對話！");
		}
	}
	
	public void loadChoice(){
		if (!ConversationIO.isSection("Choices"))
			return;
		int count = 0;
		List<Choice> list = new ArrayList<>();
		for (String id : ConversationIO.getSection("Choices")){
			TextComponent q = new TextComponent(QuestUtil.translateColor(ConversationIO.getString("Choices." + id + ".Question")));
			for (String num : ConversationIO.getSection("Choices." + id + ".Options")){
				String name = ConversationIO.getString("Choices." + id + ".Options." + num + ".OptionName");
				Choice c = new Choice(name, loadConvAction(ConversationIO.getStringList("Choices." + id + ".Options." + num + ".OptionActions")));
				list.add(Integer.parseInt(num) - 1, c);
			}
			QuestChoice choice = new QuestChoice(q, list);
			QuestStorage.Choices.put(id, choice);
			count++;
		}
		
		System.out.println("[選擇讀取] 選擇已經讀取完成！讀取了 " + count + " 個選擇！");
	}
	
	public void loadQuests(){
		if (!QuestsIO.isSection("Quests"))
			return;
		int totalcount = 0;
		for (String internal : QuestsIO.getSection("Quests")) {
			String questname = QuestsIO.getString("Quests." + internal + ".QuestName");
			List<String> questoutline = QuestsIO.getStringList("Quests." + internal + ".QuestOutline");
			List<QuestStage> stages = new ArrayList<>();
			for (String stagecount : QuestsIO.getSection("Quests." + internal + ".Stages")) {
				List<SimpleQuestObject> objs = new ArrayList<>();
				int scount = Integer.parseInt(stagecount);
				for (String objcount : QuestsIO.getSection("Quests." + internal + ".Stages." + scount)) {
					int ocount = Integer.parseInt(objcount);
					String s = QuestsIO.getString("Quests." + internal + ".Stages." + scount + "." + ocount + ".ObjectType");
					SimpleQuestObject obj = null;
					int n;
					switch (s) {
					case "DELIVER_ITEM":
						n = QuestsIO.getInt("Quests." + internal + ".Stages." + scount + "." + ocount + ".TargetNPC");
						if (CitizensAPI.getNPCRegistry().getById(n) == null){
							Bukkit.getLogger().log(Level.SEVERE, "[任務讀取] 找不到代碼為 " + n + " 的NPC，請重新設定！");
							continue;
						}
						obj = new QuestObjectDeliverItem(CitizensAPI.getNPCRegistry().getById(n),
								getItemStack(QuestsIO.getConfig(), "Quests." + internal + ".Stages." + scount + "." + ocount + ".Item"),
								QuestsIO.getInt("Quests." + internal + ".Stages." + scount + "." + ocount + ".Item.Amount"));
						break;
					case "TALK_TO_NPC":
						n = QuestsIO.getInt("Quests." + internal + ".Stages." + scount + "." + ocount + ".TargetNPC");
						if (CitizensAPI.getNPCRegistry().getById(n) == null){
							Bukkit.getLogger().log(Level.SEVERE, "[任務讀取] 找不到代碼為 " + n + " 的NPC，請重新設定！");
							continue;
						}
						obj = new QuestObjectTalkToNPC(CitizensAPI.getNPCRegistry().getById(n));
					case "KILL_MOB":
						String name = null;
						if (QuestsIO.getString("Quests." + internal + ".Stages." + scount + "." + ocount + ".MobName") != null){
							name = QuestsIO.getString("Quests." + internal + ".Stages." + scount + "." + ocount + ".MobName");
							obj = new QuestObjectKillMob(
								EntityType.valueOf(QuestsIO.getString("Quests." + internal + ".Stages." + scount + "." + ocount + ".MobType")),
								QuestsIO.getInt("Quests." + internal + ".Stages." + scount + "." + ocount + ".Amount"), name);
						}
						else if (QuestsIO.getString("Quests." + internal + ".Stages." + scount + "." + ocount + ".MythicMob") != null){
							if (!Main.instance.initManager.hasMythicMobEnabled()){
								Bukkit.getLogger().log(Level.SEVERE, "偵測到MythicMob的物件，但是未讀取到MythicMob插件連結，跳過讀取。");
								continue;
							}
							name = QuestsIO.getString("Quests." + internal + ".Stages." + scount + "." + ocount + ".MythicMob");
							try {
								obj = new QuestObjectKillMob(
											Main.instance.initManager.getMTMPlugin().getAPI().getMobAPI().getMythicMob(name),
											QuestsIO.getInt("Quests." + internal + ".Stages." + scount + "." + ocount + ".Amount"));
							} catch (Exception e) {
								Bukkit.getLogger().log(Level.SEVERE, "[任務讀取] 找不到代碼為 " + name + " 的自訂怪物，請重新設定！");
								continue;
							}
						}
						else if (QuestsIO.getString("Quests." + internal + ".Stages." + scount + "." + ocount + ".MobType") != null)
							obj = new QuestObjectKillMob(
									EntityType.valueOf(QuestsIO.getString("Quests." + internal + ".Stages." + scount + "." + ocount + ".MobType")),
									QuestsIO.getInt("Quests." + internal + ".Stages." + scount + "." + ocount + ".Amount"), null);
						break;
					case "BREAK_BLOCK":
						obj = new QuestObjectBreakBlock(Material.getMaterial(
								QuestsIO.getString("Quests." + internal + ".Stages." + scount + "." + ocount + ".BlockType")),
								QuestsIO.getInt("Quests." + internal + ".Stages." + scount + "." + ocount + ".Amount"));
						break;
					case "CONSUME_ITEM":
						obj = new QuestObjectConsumeItem(getItemStack(QuestsIO.getConfig(), "Quests." + internal + ".Stages." + scount + "." + ocount + ".Item"),
								QuestsIO.getInt("Quests." + internal + ".Stages." + scount + "." + ocount + ".Item.Amount"));
						break;
					case "REACH_LOCATION":
						String[] splited = QuestsIO.getString("Quests." + internal + ".Stages." + scount + "." + ocount + ".Location").split(":");
						Location loc = new Location(
								Bukkit.getWorld(splited[0]),
								Double.parseDouble(splited[1]),
								Double.parseDouble(splited[2]),
								Double.parseDouble(splited[3]));
						obj = new QuestObjectReachLocation(loc,
								QuestsIO.getInt("Quests." + internal + ".Stages." + scount + "." + ocount + ".Range"),
								QuestsIO.getString("Quests." + internal + ".Stages." + scount + "." + ocount + ".LocationName"));
						break;
					default:
						QuestUtil.warnCmd("錯誤：任務 " + internal + " 沒有正確的任務類別，請檢查設定檔案。");
						continue;
					}
					if (QuestsIO.getString("Quests." + internal + ".Stages." + scount + "." + ocount + ".ActivateConversation") != null){
						QuestConversation conv = QuestUtil.getConvByName(QuestsIO.getString("Quests." + internal + ".Stages." + scount + "." + ocount + ".ActivateConversation"));
						if (conv != null)
							obj.setConversation(conv);
						else{
							QuestUtil.warnCmd("錯誤：任務 " + internal + " 沒有輸入正確的觸發對話ID，跳過讀取該物件。");
							continue;
						}
					}
					objs.add(obj);
				}
				
				QuestStage qs = new QuestStage(null, null, objs);
				stages.add(qs);
			}
			QuestReward reward = new QuestReward();
			if (QuestsIO.isSection("Quests." + internal + ".Rewards.Item")){
				for (String temp : QuestsIO.getSection("Quests." + internal + ".Rewards.Item")) {
					reward.addItem(getItemStack(QuestsIO.getConfig(), "Quests." + internal + ".Rewards.Item." + Integer.parseInt(temp)));
				}
			}
			if (QuestsIO.getDouble("Quests." + internal + ".Rewards.Money") != 0)
				reward.addMoney(QuestsIO.getDouble("Quests." + internal + ".Rewards.Money"));
			if (QuestsIO.getInt("Quests." + internal + ".Rewards.Experience") != 0)
				reward.addExp(QuestsIO.getInt("Quests." + internal + ".Rewards.Experience"));
			if (QuestsIO.isSection("Quests." + internal + ".Rewards.FriendlyPoint")){
				for (String s : QuestsIO.getSection("Quests." + internal + ".Rewards.FriendlyPoint")){
					reward.addFriendPoint(Integer.parseInt(s), QuestsIO.getInt("Quests." + internal + ".Rewards.FriendlyPoint." + s));
				}
			}
			
			if (plugin.initManager.hasCitizensEnabled() && QuestsIO.contains("Quests." + internal + ".QuestNPC")){
				NPC npc = null;
				if (!(QuestsIO.getInt("Quests." + internal + ".QuestNPC") == -1)
						&& CitizensAPI.getNPCRegistry().getById(QuestsIO.getInt("Quests." + internal + ".QuestNPC")) != null)
					npc = CitizensAPI.getNPCRegistry().getById(0);
					Quest quest = new Quest(internal, questname, questoutline, reward, stages, npc);
					if (QuestsIO.getString("Quests." + internal + ".MessageRequirementNotMeet") != null)
						quest.setFailMessage(QuestsIO.getString("Quests." + internal + ".MessageRequirementNotMeet"));
					//Requirements
					if (QuestsIO.isSection("Quests." + internal + ".Requirements")){
						if (QuestsIO.getInt("Quests." + internal + ".Requirements.Level") != 0)
							quest.getRequirements().put(RequirementType.LEVEL, QuestsIO.getInt("Quests." + internal + ".Requirements.Level"));
						if (QuestsIO.getStringList("Quests." + internal + ".Requirements.Quest") != null){
							quest.getRequirements().put(RequirementType.QUEST, QuestsIO.getStringList("Quests." + internal + ".Requirements.Quest"));
						}
						if (QuestsIO.isSection("Quests." + internal + ".Requirements.Item")){
							List<ItemStack> l = new ArrayList<>();
							for (String i : QuestsIO.getSection("Quests." + internal + ".Requirements.Item")) {
								l.add(getItemStack(QuestsIO.getConfig(), "Quests." + internal + ".Requirements.Item." + i));
							}
							quest.getRequirements().put(RequirementType.ITEM, l);
						}
						if (QuestsIO.getStringList("Quests." + internal + ".Requirements.Scoreboard") != null){
							quest.getRequirements().put(RequirementType.SCOREBOARD, QuestsIO.getStringList("Quests." + internal + ".Requirements.Scoreboard"));
						}
						if (QuestsIO.getStringList("Quests." + internal + ".Requirements.NBTTag") != null){
							quest.getRequirements().put(RequirementType.NBTTAG, QuestsIO.getStringList("Quests." + internal + ".Requirements.NBTTag"));
						}
					}
					
					//Triggers
					if (QuestsIO.getStringList("Quests." + internal + ".TriggerEvents") != null){
						List<QuestTrigger> list = new ArrayList<>();
						for (String tri : QuestsIO.getStringList("Quests." + internal + ".TriggerEvents")){
							String[] Stri = tri.split(" ");
							QuestTrigger trigger = null;
							TriggerType type = TriggerType.valueOf(Stri[0]);
							TriggerObject obj;
							switch(type){
							case TRIGGER_STAGE_START:
							case TRIGGER_STAGE_FINISH:
								obj = TriggerObject.valueOf(Stri[2]);
								String s = Stri[3];
								if (obj.equals(TriggerObject.COMMAND)){
									if (Stri.length > 4){
										for (int k = 4; k < Stri.length; k++){
											s += " " + Stri[k];
										}
									}
								}
								trigger = new QuestTrigger(type, obj, Integer.parseInt(Stri[1]), s);
								break;
							default:
								obj = TriggerObject.valueOf(Stri[1]);
								String t = Stri[2];
								if (obj.equals(TriggerObject.COMMAND)){
									if (Stri.length > 3){
										for (int k = 3; k < Stri.length; k++){
											t += " " + Stri[k];
										}
									}
								}
								trigger = new QuestTrigger(type, obj, t);
								break;
							}
							list.add(trigger);
						}
						quest.setTriggers(list);
					}
					if (QuestsIO.getBoolean("Quests." + internal + ".Redoable")){
						quest.setRedoable(true);
						quest.setRedoDelay(QuestsIO.getLong("Quests." + internal + ".RedoDelayMilliseconds"));
					}
					QuestStorage.Quests.put(internal, quest);
					totalcount++;
			}else{
				Bukkit.getLogger().log(Level.SEVERE, "[任務讀取] 任務 " + questname + " 的NPC有誤，請重新設定！");
				Bukkit.getLogger().log(Level.SEVERE, "[任務讀取] 任務 " + questname + " 已經跳過讀取。");
				continue;
			}
		}
		Bukkit.getLogger().log(Level.INFO, "[任務讀取] 任務已經讀取完畢。讀取了 " + totalcount + " 個任務。");
	}
	
	private List<QuestBaseAction> loadConvAction(List<String> fromlist){
		List<QuestBaseAction> list = new ArrayList<>();
		EnumAction e = null;
		for (String s : fromlist){
			if (s.contains("#")){
				try{
					e = EnumAction.valueOf(s.split("#")[0]);
				}catch(Exception ex){
					QuestUtil.warnCmd("不正確的對話動作(EnumAction)： " + s.split("#")[0]);
					continue;
				}
				if (e != null){
					QuestBaseAction action;
					switch(e){
					case CHANGE_CONVERSATION:
					case CHOICE:
					case NPC_TALK:
					case COMMAND:
					case WAIT:
					case SENTENCE:
					case FINISH:
						action = new QuestBaseAction(e, s.split("#")[1]);
						break;
					case BUTTON:
					case CHANGE_LINE:
					case CHANGE_PAGE:
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
	
	private ItemStack getItemStack(FileConfiguration config, String path) {
		Material m = Material.getMaterial(config.getString(path + ".Material"));
		int amount = config.getInt(path + ".Amount");
		ItemStack is = new ItemStack(m, amount);
		if (config.getString(path + ".ItemName") != null) {
			String name = ChatColor.translateAlternateColorCodes('&', config.getString(path + ".ItemName"));
			List<String> lore = new ArrayList<>();
			for (String s : config.getStringList(path + ".ItemLore")) {
				lore.add(ChatColor.translateAlternateColorCodes('&', s));
			}
			ItemMeta im = is.getItemMeta();
			im.setDisplayName(name);
			im.setLore(lore);
			is.setItemMeta(im);
		}
		return is;
	}

}
