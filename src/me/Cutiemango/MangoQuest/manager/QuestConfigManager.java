package me.Cutiemango.MangoQuest.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

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
import me.Cutiemango.MangoQuest.questobjects.QuestObjectItemConsume;
import me.Cutiemango.MangoQuest.questobjects.QuestObjectItemDeliver;
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
		for (String s : TranslateIO.getConfig().getConfigurationSection("Material").getKeys(false)){
			if (Material.getMaterial(s) != null)
				QuestStorage.TranslateMap.put(Material.getMaterial(s), TranslateIO.getConfig().getString("Material." + s));
		}
		for (String e : TranslateIO.getConfig().getConfigurationSection("EntityType").getKeys(false)){
			try{
				QuestStorage.EntityTypeMap.put(EntityType.valueOf(e), TranslateIO.getConfig().getString("EntityType." + e));
			} catch(IllegalArgumentException ex){
				continue;
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
					for (String i : NPCIO.getSection("NPC." + s + ".開啟介面訊息")){
						npc.put(Integer.parseInt(i), NPCIO.getString("NPC." + s + ".開啟介面訊息." + i));
					}
					for (String i : NPCIO.getSection("NPC." + s + ".對話")){
						npc.put(Integer.parseInt(i), QuestUtil.getConvByName(NPCIO.getString("NPC." + s + ".對話." + i)));
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
		QuestsIO.getConfig().set("任務列表." + q.getInternalID(), null);
		Bukkit.getLogger().log(Level.WARNING, "[任務讀取] 任務 " + q.getInternalID() + " 已經刪除所有yml相關資料。");
		QuestsIO.save();
	}
	
	@SuppressWarnings("unchecked")
	public void saveQuest(Quest q){
		QuestsIO.set("任務列表." + q.getInternalID() + ".任務名稱", q.getQuestName());
		QuestsIO.set("任務列表." + q.getInternalID() + ".任務提要", q.getQuestOutline());
		QuestsIO.set("任務列表." + q.getInternalID() + ".任務NPC", q.getQuestNPC().getId());
		QuestsIO.set("任務列表." + q.getInternalID() + ".任務需求.Level", q.getRequirements().get(RequirementType.LEVEL));
		QuestsIO.set("任務列表." + q.getInternalID() + ".任務需求.Quest", q.getRequirements().get(RequirementType.QUEST));
		int i = 0;
		for (ItemStack is : (List<ItemStack>)q.getRequirements().get(RequirementType.ITEM)){
			i++;
			QuestsIO.set("任務列表." + q.getInternalID() + ".任務需求.Item." + i + ".類別", is.getType().toString());
			QuestsIO.set("任務列表." + q.getInternalID() + ".任務需求.Item." + i + ".數量", is.getAmount());
			if (is.hasItemMeta() && is.getItemMeta().hasDisplayName() && is.getItemMeta().hasLore()){
				QuestsIO.set("任務列表." + q.getInternalID() + ".任務需求.Item." + i + ".名稱", is.getItemMeta().getDisplayName());
				QuestsIO.set("任務列表." + q.getInternalID() + ".任務需求.Item." + i + ".註解", is.getItemMeta().getLore());
			}
		}
		QuestsIO.set("任務列表." + q.getInternalID() + ".任務需求.Scoreboard", q.getRequirements().get(RequirementType.SCOREBOARD));
		QuestsIO.set("任務列表." + q.getInternalID() + ".任務需求.NBTTag", q.getRequirements().get(RequirementType.NBTTAG));
		if (q.getFailMessage() != null)
			QuestsIO.set("任務列表." + q.getInternalID() + ".不符合任務需求訊息", q.getFailMessage());
		QuestsIO.set("任務列表." + q.getInternalID() + ".可重複執行", q.isRedoable());
		if (q.isRedoable())
			QuestsIO.set("任務列表." + q.getInternalID() + ".重複執行時間", q.getRedoDelay());
		List<String> list = new ArrayList<>();
		for (QuestTrigger qt : q.getTriggers()){
			if (qt.getType().equals(TriggerType.TRIGGER_STAGE_START) || qt.getType().equals(TriggerType.TRIGGER_STAGE_FINISH)){
				list.add(qt.getType() + " " + qt.getCount() + " " + qt.getTriggerObject().toString() + " " + qt.getObject().toString());
				continue;
			}
			list.add(qt.getType() + " " + qt.getTriggerObject().toString() + " " + qt.getObject().toString());
		}
		QuestsIO.set("任務列表." + q.getInternalID() + ".任務觸發事件", list);
		i = 0;
		int j = 0;
		for (QuestStage s : q.getStages()){
			i++;
			for (SimpleQuestObject obj : s.getObjects()){
				j++;
				QuestsIO.set("任務列表." + q.getInternalID() + ".任務內容." + i + "." + j + ".任務種類", obj.getConfigString());
				switch(obj.getConfigString()){
				case "DELIVER_ITEM":
					QuestObjectItemDeliver o = (QuestObjectItemDeliver)obj;
					QuestsIO.set("任務列表." + q.getInternalID() + ".任務內容." + i + "." + j + ".目標NPC", o.getTargetNPC().getId());
					QuestsIO.set("任務列表." + q.getInternalID() + ".任務內容." + i + "." + j + ".物品.類別", o.getDeliverItem().getType().toString());
					QuestsIO.set("任務列表." + q.getInternalID() + ".任務內容." + i + "." + j + ".物品.數量", o.getDeliverItem().getAmount());
					if (o.getDeliverItem().hasItemMeta() && o.getDeliverItem().getItemMeta().hasDisplayName() && o.getDeliverItem().getItemMeta().hasLore()){
						QuestsIO.set("任務列表." + q.getInternalID() + ".任務內容." + i + "." + j + ".物品.名稱", o.getDeliverItem().getItemMeta().getDisplayName());
						QuestsIO.set("任務列表." + q.getInternalID() + ".任務內容." + i + "." + j + ".物品.註解", o.getDeliverItem().getItemMeta().getLore());
					}
					break;
				case "TALK_TO_NPC":
					QuestObjectTalkToNPC on = (QuestObjectTalkToNPC)obj;
					QuestsIO.set("任務列表." + q.getInternalID() + ".任務內容." + i + "." + j + ".目標NPC", on.getTargetNPC().getId());
					break;
				case "KILL_MOB":
					QuestObjectKillMob om = (QuestObjectKillMob)obj;
					QuestsIO.set("任務列表." + q.getInternalID() + ".任務內容." + i + "." + j + ".怪物類型", om.getType().toString());
					QuestsIO.set("任務列表." + q.getInternalID() + ".任務內容." + i + "." + j + ".數量", om.getAmount());
					if (om.hasCustomName())
						QuestsIO.set("任務列表." + q.getInternalID() + ".任務內容." + i + "." + j + ".怪物名稱", om.getCustomName());
					break;
				case "BREAK_BLOCK":
					QuestObjectBreakBlock ob = (QuestObjectBreakBlock)obj;
					QuestsIO.set("任務列表." + q.getInternalID() + ".任務內容." + i + "." + j + ".方塊", ob.getType().toString());
					QuestsIO.set("任務列表." + q.getInternalID() + ".任務內容." + i + "." + j + ".數量", ob.getAmount());
					break;
				case "CONSUME_ITEM":
					QuestObjectItemConsume oi = (QuestObjectItemConsume)obj;
					QuestsIO.set("任務列表." + q.getInternalID() + ".任務內容." + i + "." + j + ".物品.類別", oi.getItem().getType().toString());
					QuestsIO.set("任務列表." + q.getInternalID() + ".任務內容." + i + "." + j + ".物品.數量", oi.getItem().getAmount());
					break;
				case "REACH_LOCATION":
					QuestObjectReachLocation or = (QuestObjectReachLocation)obj;
					String loc = or.getLocation().getWorld().getName() + ":" + or.getLocation().getX() + ":" + or.getLocation().getY() + ":" + or.getLocation().getZ();
					QuestsIO.set("任務列表." + q.getInternalID() + ".任務內容." + i + "." + j + ".地點", loc);
					QuestsIO.set("任務列表." + q.getInternalID() + ".任務內容." + i + "." + j + ".名稱", or.getName());
					QuestsIO.set("任務列表." + q.getInternalID() + ".任務內容." + i + "." + j + ".範圍", or.getRadius());
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
				QuestsIO.set("任務列表." + q.getInternalID() + ".任務獎勵.物品." + c + ".類別", is.getType().toString());
				QuestsIO.set("任務列表." + q.getInternalID() + ".任務獎勵.物品." + c + ".數量", is.getAmount());
			}
		}
		if (q.getQuestReward().hasMoney())
			QuestsIO.set("任務列表." + q.getInternalID() + ".任務獎勵.金錢", q.getQuestReward().getMoney());
		
		System.out.println("[任務讀取] 任務 " + q.getQuestName() + " 已經儲存完成！");
		QuestsIO.save();
	}
	
	public void loadConversation(){
		if (!ConversationIO.isSection("任務對話"))
			return;
		int count = 0;
		for (String id : ConversationIO.getSection("任務對話")){
			String name = ConversationIO.getString("任務對話." + id + ".對話名稱");
			List<String> act = ConversationIO.getStringList("任務對話." + id + ".對話內容");
			NPC npc = CitizensAPI.getNPCRegistry().getById(ConversationIO.getInt("任務對話." + id + ".對話NPC"));
			QuestConversation conv = new QuestConversation(name, id, npc, loadConvAction(act));
			QuestStorage.Conversations.put(id, conv);
			count++;
		}
		
		System.out.println("[對話讀取] 對話已經讀取完成！讀取了 " + count + " 個對話！");
	}
	
	public void loadChoice(){
		if (!ConversationIO.isSection("選擇"))
			return;
		int count = 0;
		Choice[] array = new Choice[4];
		for (String id : ConversationIO.getSection("選擇")){
			TextComponent q = new TextComponent(QuestUtil.translateColor(ConversationIO.getString("選擇." + id + ".問題")));
			for (String num : ConversationIO.getSection("選擇." + id + ".選項")){
				String name = ConversationIO.getString("選擇." + id + ".選項." + num + ".選項名稱");
				Choice c = new Choice(name, loadConvAction(ConversationIO.getStringList("選擇." + id + ".選項." + num + ".選項動作")));
				array[Integer.parseInt(num) - 1] = c;
			}
			QuestChoice choice = new QuestChoice(q, array);
			QuestStorage.Choices.put(id, choice);
			count++;
		}
		
		System.out.println("[選擇讀取] 選擇已經讀取完成！讀取了 " + count + " 個選擇！");
	}
	
	public void loadQuests(){
		if (!QuestsIO.isSection("任務列表"))
			return;
		int totalcount = 0;
		for (String internal : QuestsIO.getSection("任務列表")) {
			String questname = QuestsIO.getString("任務列表." + internal + ".任務名稱");
			String questoutline = QuestsIO.getString("任務列表." + internal + ".任務提要");
			List<QuestStage> stages = new ArrayList<>();
			for (String stagecount : QuestsIO.getSection("任務列表." + internal + ".任務內容")) {
				List<SimpleQuestObject> objs = new ArrayList<>();
				int scount = Integer.parseInt(stagecount);
				for (String objcount : QuestsIO.getSection("任務列表." + internal + ".任務內容." + scount)) {
					int ocount = Integer.parseInt(objcount);
					String s = QuestsIO.getString("任務列表." + internal + ".任務內容." + scount + "." + ocount + ".任務種類");
					SimpleQuestObject obj = null;
					int n;
					switch (s) {
					case "DELIVER_ITEM":
						n = QuestsIO.getInt("任務列表." + internal + ".任務內容." + scount + "." + ocount + ".目標NPC");
						obj = new QuestObjectItemDeliver(CitizensAPI.getNPCRegistry().getById(n),
						QuestUtil.getItemStack(QuestsIO.getConfig(), "任務列表." + internal + ".任務內容." + scount + "." + ocount + ".物品"),
						QuestsIO.getInt("任務列表." + internal + ".任務內容." + scount + "." + ocount + ".物品.數量"));
						if (CitizensAPI.getNPCRegistry().getById(n) == null)
							Bukkit.getLogger().log(Level.SEVERE, "[任務讀取] 找不到代碼為 " + n + " 的NPC，請重新設定！");
						break;
					case "TALK_TO_NPC":
						n = QuestsIO.getInt("任務列表." + internal + ".任務內容." + scount + "." + ocount + ".目標NPC");
						obj = new QuestObjectTalkToNPC(CitizensAPI.getNPCRegistry().getById(n));
						if (CitizensAPI.getNPCRegistry().getById(n) == null)
							Bukkit.getLogger().log(Level.SEVERE, "[任務讀取] 找不到代碼為 " + n + " 的NPC，請重新設定！");
						break;
					case "KILL_MOB":
						String name = null;
						if (QuestsIO.getString("任務列表." + internal + ".任務內容." + scount + "." + ocount + ".怪物名稱") != null)
							name = QuestsIO.getString("任務列表." + internal + ".任務內容." + scount + "." + ocount + ".怪物名稱");
						obj = new QuestObjectKillMob(
								EntityType.valueOf(QuestsIO.getString("任務列表." + internal + ".任務內容." + scount + "." + ocount + ".怪物類型")),
								QuestsIO.getInt("任務列表." + internal + ".任務內容." + scount + "." + ocount + ".數量"), name);
						break;
					case "BREAK_BLOCK":
						obj = new QuestObjectBreakBlock(Material.getMaterial(
								QuestsIO.getString("任務列表." + internal + ".任務內容." + scount + "." + ocount + ".方塊")),
								QuestsIO.getInt("任務列表." + internal + ".任務內容." + scount + "." + ocount + ".數量"));
						break;
					case "CONSUME_ITEM":
						obj = new QuestObjectItemConsume(QuestUtil.getItemStack(QuestsIO.getConfig(), "任務列表." + internal + ".任務內容." + scount + "." + ocount + ".物品"),
								QuestsIO.getInt("任務列表." + internal + ".任務內容." + scount + "." + ocount + ".物品.數量"));
						break;
					case "REACH_LOCATION":
						String[] splited = QuestsIO.getString("任務列表." + internal + ".任務內容." + scount + "." + ocount + ".地點").split(":");
						Location loc = new Location(
								Bukkit.getWorld(splited[0]),
								Double.parseDouble(splited[1]),
								Double.parseDouble(splited[2]),
								Double.parseDouble(splited[3]));
						obj = new QuestObjectReachLocation(loc,
								QuestsIO.getInt("任務列表." + internal + ".任務內容." + scount + "." + ocount + ".範圍"),
								QuestsIO.getString("任務列表." + internal + ".任務內容." + scount + "." + ocount + ".名稱"));
						break;
					default:
						QuestUtil.warnCmd("錯誤：任務 " + internal + " 沒有正確的任務內容類別，請檢查設定檔案。");
						break;
					}
					if (QuestsIO.getString("任務列表." + internal + ".任務內容." + scount + "." + ocount + ".觸發對話") != null){
						QuestConversation conv = QuestUtil.getConvByName(QuestsIO.getString("任務列表." + internal + ".任務內容." + scount + "." + ocount + ".觸發對話"));
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
			if (QuestsIO.isSection("任務列表." + internal + ".任務獎勵.物品")){
				for (String temp : QuestsIO.getSection("任務列表." + internal + ".任務獎勵.物品")) {
					reward.addItem(QuestUtil.getItemStack(QuestsIO.getConfig(), "任務列表." + internal + ".任務獎勵.物品." + Integer.parseInt(temp)));
				}
			}
			if (QuestsIO.getDouble("任務列表." + internal + ".任務獎勵.金錢") != 0)
				reward.addMoney(QuestsIO.getDouble("任務列表." + internal + ".任務獎勵.金錢"));
			if (QuestsIO.getInt("任務列表." + internal + ".任務獎勵.經驗值") != 0)
				reward.addExp(QuestsIO.getInt("任務列表." + internal + ".任務獎勵.經驗值"));
			if (QuestsIO.isSection("任務列表." + internal + ".任務獎勵.友好度")){
				for (String s : QuestsIO.getSection("任務列表." + internal + ".任務獎勵.友好度")){
					reward.addFriendPoint(Integer.parseInt(s), QuestsIO.getInt("任務列表." + internal + ".任務獎勵.友好度." + s));
				}
			}
			
			if (plugin.citizens != null && QuestsIO.contains("任務列表." + internal + ".任務NPC")){
				NPC npc = null;
				if (!(QuestsIO.getInt("任務列表." + internal + ".任務NPC") == -1)
						&& CitizensAPI.getNPCRegistry().getById(QuestsIO.getInt("任務列表." + internal + ".任務NPC")) != null)
					npc = CitizensAPI.getNPCRegistry().getById(0);
					Quest quest = new Quest(internal, questname, questoutline, reward, stages, npc);
					if (QuestsIO.getString("任務列表." + internal + ".不符合任務需求訊息") != null)
						quest.setFailMessage(QuestsIO.getString("任務列表." + internal + ".不符合任務需求訊息"));
					
					//Requirements
					if (QuestsIO.isSection("任務列表." + internal + ".任務需求")){
						if (QuestsIO.getInt("任務列表." + internal + ".任務需求.Level") != 0)
							quest.getRequirements().put(RequirementType.LEVEL, QuestsIO.getInt("任務列表." + internal + ".任務需求.Level"));
						if (QuestsIO.getStringList("任務列表." + internal + ".任務需求.Quest") != null){
							quest.getRequirements().put(RequirementType.QUEST, QuestsIO.getStringList("任務列表." + internal + ".任務需求.Quest"));
						}
						if (QuestsIO.isSection("任務列表." + internal + ".任務需求.Item")){
							List<ItemStack> l = new ArrayList<>();
							for (String i : QuestsIO.getSection("任務列表." + internal + ".任務需求.Item")) {
								l.add(QuestUtil.getItemStack(QuestsIO.getConfig(), "任務列表." + internal + ".任務需求.Item." + i));
							}
							quest.getRequirements().put(RequirementType.ITEM, l);
						}
						if (QuestsIO.getStringList("任務列表." + internal + ".任務需求.Scoreboard") != null){
							quest.getRequirements().put(RequirementType.SCOREBOARD, QuestsIO.getStringList("任務列表." + internal + ".任務需求.Scoreboard"));
						}
						if (QuestsIO.getStringList("任務列表." + internal + ".任務需求.NBTTag") != null){
							quest.getRequirements().put(RequirementType.NBTTAG, QuestsIO.getStringList("任務列表." + internal + ".任務需求.NBTTag"));
						}
					}
					
					//Triggers
					if (QuestsIO.getStringList("任務列表." + internal + ".任務觸發事件") != null){
						List<QuestTrigger> list = new ArrayList<>();
						for (String tri : QuestsIO.getStringList("任務列表." + internal + ".任務觸發事件")){
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
					if (QuestsIO.getBoolean("任務列表." + internal + ".可重複執行")){
						quest.setRedoable(true);
						quest.setRedoDelay(QuestsIO.getLong("任務列表." + internal + ".重複執行時間"));
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

}
