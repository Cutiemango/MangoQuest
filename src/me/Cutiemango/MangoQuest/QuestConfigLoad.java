package me.Cutiemango.MangoQuest;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

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

public class QuestConfigLoad {
	
	public static FileConfiguration pconfig;
	private FileConfiguration qconfig;
	private FileConfiguration tconfig;
	private Main plugin;
	
	public QuestConfigLoad(Main pl){
		plugin = pl;
		init();
		loadTranslation();
	}
	
	private void loadTranslation() {
		for (String s : tconfig.getConfigurationSection("Material").getKeys(false)){
			if (Material.getMaterial(s) != null)
				QuestStorage.TranslateMap.put(Material.getMaterial(s), tconfig.getString("Material." + s));
		}
		for (String e : tconfig.getConfigurationSection("EntityType").getKeys(false)){
			try{
				QuestStorage.EntityTypeMap.put(EntityType.valueOf(e), tconfig.getString("EntityType." + e));
			} catch(IllegalArgumentException ex){
				continue;
			}
		}
		Bukkit.getLogger().log(Level.INFO, "[MangoQuest] 翻譯檔案讀取完成！");
	}
	
	@SuppressWarnings("unchecked")
	public void saveQuest(Quest q){
		qconfig.set("任務列表." + q.getInternalID() + ".任務名稱", q.getQuestName());
		qconfig.set("任務列表." + q.getInternalID() + ".任務提要", q.getQuestOutline());
		qconfig.set("任務列表." + q.getInternalID() + ".任務NPC", q.getQuestNPC().getId());
		qconfig.set("任務列表." + q.getInternalID() + ".任務需求.Level", q.getRequirements().get(RequirementType.LEVEL));
		qconfig.set("任務列表." + q.getInternalID() + ".任務需求.Quest", q.getRequirements().get(RequirementType.QUEST));
		int i = 0;
		for (ItemStack is : (List<ItemStack>)q.getRequirements().get(RequirementType.ITEM)){
			i++;
			qconfig.set("任務列表." + q.getInternalID() + ".任務需求.Item." + i + ".類別", is.getType().toString());
			qconfig.set("任務列表." + q.getInternalID() + ".任務需求.Item." + i + ".數量", is.getAmount());
			if (is.hasItemMeta() && is.getItemMeta().hasDisplayName() && is.getItemMeta().hasLore()){
				qconfig.set("任務列表." + q.getInternalID() + ".任務需求.Item." + i + ".名稱", is.getItemMeta().getDisplayName());
				qconfig.set("任務列表." + q.getInternalID() + ".任務需求.Item." + i + ".註解", is.getItemMeta().getLore());
			}
		}
		qconfig.set("任務列表." + q.getInternalID() + ".任務需求.Scoreboard", q.getRequirements().get(RequirementType.SCOREBOARD));
		qconfig.set("任務列表." + q.getInternalID() + ".任務需求.NBTTag", q.getRequirements().get(RequirementType.NBTTAG));
		if (q.getFailMessage() != null)
			qconfig.set("任務列表." + q.getInternalID() + ".不符合任務需求訊息", q.getFailMessage());
		qconfig.set("任務列表." + q.getInternalID() + ".可重複執行", q.isRedoable());
		if (q.isRedoable())
			qconfig.set("任務列表." + q.getInternalID() + ".重複執行時間", q.getRedoDelay());
		List<String> list = new ArrayList<>();
		for (QuestTrigger qt : q.getTriggers()){
			if (qt.getType().equals(TriggerType.TRIGGER_STAGE_START) || qt.getType().equals(TriggerType.TRIGGER_STAGE_FINISH)){
				list.add(qt.getType() + " " + qt.getCount() + " " + qt.getTriggerObject().toString() + " " + qt.getObject().toString());
				continue;
			}
			list.add(qt.getType() + " " + qt.getTriggerObject().toString() + " " + qt.getObject().toString());
		}
		qconfig.set("任務列表." + q.getInternalID() + ".任務觸發事件", list);
		i = 0;
		int j = 0;
		for (QuestStage s : q.getStages()){
			i++;
			for (SimpleQuestObject obj : s.getObjects()){
				j++;
				qconfig.set("任務列表." + q.getInternalID() + ".任務內容." + i + "." + j + ".任務種類", obj.getConfigString());
				switch(obj.getConfigString()){
				case "DELIVER_ITEM":
					QuestObjectItemDeliver o = (QuestObjectItemDeliver)obj;
					qconfig.set("任務列表." + q.getInternalID() + ".任務內容." + i + "." + j + ".目標NPC", o.getTargetNPC().getId());
					qconfig.set("任務列表." + q.getInternalID() + ".任務內容." + i + "." + j + ".物品.類別", o.getDeliverItem().getType().toString());
					qconfig.set("任務列表." + q.getInternalID() + ".任務內容." + i + "." + j + ".物品.數量", o.getDeliverItem().getAmount());
					if (o.getDeliverItem().hasItemMeta() && o.getDeliverItem().getItemMeta().hasDisplayName() && o.getDeliverItem().getItemMeta().hasLore()){
						qconfig.set("任務列表." + q.getInternalID() + ".任務內容." + i + "." + j + ".物品.名稱", o.getDeliverItem().getItemMeta().getDisplayName());
						qconfig.set("任務列表." + q.getInternalID() + ".任務內容." + i + "." + j + ".物品.註解", o.getDeliverItem().getItemMeta().getLore());
					}
					break;
				case "TALK_TO_NPC":
					QuestObjectTalkToNPC on = (QuestObjectTalkToNPC)obj;
					qconfig.set("任務列表." + q.getInternalID() + ".任務內容." + i + "." + j + ".目標NPC", on.getTargetNPC().getId());
					break;
				case "KILL_MOB":
					QuestObjectKillMob om = (QuestObjectKillMob)obj;
					qconfig.set("任務列表." + q.getInternalID() + ".任務內容." + i + "." + j + ".怪物類型", om.getType().toString());
					qconfig.set("任務列表." + q.getInternalID() + ".任務內容." + i + "." + j + ".數量", om.getAmount());
					if (om.hasCustomName())
						qconfig.set("任務列表." + q.getInternalID() + ".任務內容." + i + "." + j + ".怪物名稱", om.getCustomName());
					break;
				case "BREAK_BLOCK":
					QuestObjectBreakBlock ob = (QuestObjectBreakBlock)obj;
					qconfig.set("任務列表." + q.getInternalID() + ".任務內容." + i + "." + j + ".方塊", ob.getType().toString());
					qconfig.set("任務列表." + q.getInternalID() + ".任務內容." + i + "." + j + ".數量", ob.getAmount());
					break;
				case "CONSUME_ITEM":
					QuestObjectItemConsume oi = (QuestObjectItemConsume)obj;
					qconfig.set("任務列表." + q.getInternalID() + ".任務內容." + i + "." + j + ".物品.類別", oi.getItem().getType().toString());
					qconfig.set("任務列表." + q.getInternalID() + ".任務內容." + i + "." + j + ".物品.數量", oi.getItem().getAmount());
					break;
				case "REACH_LOCATION":
					QuestObjectReachLocation or = (QuestObjectReachLocation)obj;
					String loc = or.getLocation().getWorld().getName() + ":" + or.getLocation().getX() + ":" + or.getLocation().getY() + ":" + or.getLocation().getZ();
					qconfig.set("任務列表." + q.getInternalID() + ".任務內容." + i + "." + j + ".地點", loc);
					qconfig.set("任務列表." + q.getInternalID() + ".任務內容." + i + "." + j + ".名稱", or.getName());
					qconfig.set("任務列表." + q.getInternalID() + ".任務內容." + i + "." + j + ".範圍", or.getRadius());
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
				qconfig.set("任務列表." + q.getInternalID() + ".任務獎勵.物品." + c + ".類別", is.getType().toString());
				qconfig.set("任務列表." + q.getInternalID() + ".任務獎勵.物品." + c + ".數量", is.getAmount());
			}
		}
		if (q.getQuestReward().hasMoney())
			qconfig.set("任務列表." + q.getInternalID() + ".任務獎勵.金錢", q.getQuestReward().getMoney());
		
		System.out.println("任務 " + q.getQuestName() + " 已經儲存完成！");
		
		try{
			qconfig.save(new File(plugin.getDataFolder(), "quests.yml"));
		}catch(IOException e){
			e.printStackTrace();
		}
		
	}

	private void init(){
		File file = new File(plugin.getDataFolder(), "players.yml");
		
		if (!file.exists()){
			plugin.saveResource("players.yml", true);
			Bukkit.getLogger().log(Level.SEVERE, "[MangoQuest] 找不到players.yml，建立新檔案！");
		}
		pconfig = YamlConfiguration.loadConfiguration(file);
		
		file = new File(plugin.getDataFolder(), "translations.yml");
		if (!file.exists()){
			plugin.saveResource("translations.yml", true);
			Bukkit.getLogger().log(Level.SEVERE, "[MangoQuest] 找不到translations.yml，建立新檔案！");
		}
		
		tconfig = YamlConfiguration.loadConfiguration(file);
		
		file = new File(this.plugin.getDataFolder(), "quests.yml");
		if (!file.exists()){
			plugin.saveResource("quests.yml", true);
			Bukkit.getLogger().log(Level.SEVERE, "[MangoQuest] 找不到quests.yml，建立新檔案！");
		}
		
		qconfig = YamlConfiguration.loadConfiguration(file);
	}
	
	public void loadQuests(){
		if (qconfig.getConfigurationSection("任務列表") == null)
			return;
		for (String internal : qconfig.getConfigurationSection("任務列表").getKeys(false)) {
			String questname = qconfig.getString("任務列表." + internal + ".任務名稱");
			String questoutline = qconfig.getString("任務列表." + internal + ".任務提要");
			List<QuestStage> stages = new ArrayList<>();
			for (String stagecount : qconfig.getConfigurationSection("任務列表." + internal + ".任務內容").getKeys(false)) {
				List<SimpleQuestObject> objs = new ArrayList<>();
				int scount = Integer.parseInt(stagecount);
				for (String objcount : qconfig.getConfigurationSection("任務列表." + internal + ".任務內容." + scount).getKeys(false)) {
					int ocount = Integer.parseInt(objcount);
					String s = qconfig.getString("任務列表." + internal + ".任務內容." + scount + "." + ocount + ".任務種類");
					SimpleQuestObject obj = null;
					switch (s) {
					case "DELIVER_ITEM":
						obj = new QuestObjectItemDeliver(CitizensAPI.getNPCRegistry()
								.getById(qconfig.getInt("任務列表." + internal + ".任務內容." + scount + "." + ocount + ".目標NPC")),
						QuestUtil.getItemStack(qconfig, "任務列表." + internal + ".任務內容." + scount + "." + ocount + ".物品"),
						qconfig.getInt("任務列表." + internal + ".任務內容." + scount + "." + ocount + ".物品.數量"));
						break;
					case "TALK_TO_NPC":
						obj = new QuestObjectTalkToNPC(CitizensAPI.getNPCRegistry()
								.getById(qconfig.getInt("任務列表." + internal + ".任務內容." + scount + "." + ocount + ".目標NPC")));
						break;
					case "KILL_MOB":
						String name = null;
						if (qconfig.getString("任務列表." + internal + ".任務內容." + scount + "." + ocount + ".怪物名稱") != null)
							name = qconfig.getString("任務列表." + internal + ".任務內容." + scount + "." + ocount + ".怪物名稱");
						obj = new QuestObjectKillMob(
								EntityType.valueOf(qconfig.getString("任務列表." + internal + ".任務內容." + scount + "." + ocount + ".怪物類型")),
								qconfig.getInt("任務列表." + internal + ".任務內容." + scount + "." + ocount + ".數量"), name);
						break;
					case "BREAK_BLOCK":
						obj = new QuestObjectBreakBlock(Material.getMaterial(
								qconfig.getString("任務列表." + internal + ".任務內容." + scount + "." + ocount + ".方塊")),
								qconfig.getInt("任務列表." + internal + ".任務內容." + scount + "." + ocount + ".數量"));
						break;
					case "CONSUME_ITEM":
						obj = new QuestObjectItemConsume(QuestUtil.getItemStack(qconfig, "任務列表." + internal + ".任務內容." + scount + "." + ocount + ".物品"),
								qconfig.getInt("任務列表." + internal + ".任務內容." + scount + "." + ocount + ".物品.數量"));
						break;
					case "REACH_LOCATION":
						String[] splited = qconfig.getString("任務列表." + internal + ".任務內容." + scount + "." + ocount + ".地點").split(":");
						Location loc = new Location(
								Bukkit.getWorld(splited[0]),
								Double.parseDouble(splited[1]),
								Double.parseDouble(splited[2]),
								Double.parseDouble(splited[3]));
						obj = new QuestObjectReachLocation(loc,
								qconfig.getInt("任務列表." + internal + ".任務內容." + scount + "." + ocount + ".範圍"),
								qconfig.getString("任務列表." + internal + ".任務內容." + scount + "." + ocount + ".名稱"));
						break;
					default:
						QuestUtil.warnCmd("錯誤：任務 " + internal + " 沒有正確的任務內容類別，請檢查設定檔案。");
						break;
					}
					objs.add(obj);
				}
				QuestStage qs = new QuestStage(null, null, objs);
				stages.add(qs);
			}
			QuestReward reward = new QuestReward();
			if (qconfig.isConfigurationSection("任務列表." + internal + ".任務獎勵.物品")){
				for (String temp : qconfig.getConfigurationSection("任務列表." + internal + ".任務獎勵.物品").getKeys(false)) {
					reward.addItem(QuestUtil.getItemStack(qconfig, "任務列表." + internal + ".任務獎勵.物品." + Integer.parseInt(temp)));
				}
			}
			if (qconfig.getDouble("任務列表." + internal + ".任務獎勵.金錢") != 0)
				reward.addMoney(qconfig.getDouble("任務列表." + internal + ".任務獎勵.金錢"));
			if (qconfig.getInt("任務列表." + internal + ".任務獎勵.經驗值") != 0)
				reward.addExp(qconfig.getInt("任務列表." + internal + ".任務獎勵.經驗值"));
			
			if (plugin.citizens != null && qconfig.contains("任務列表." + internal + ".任務NPC")){
				if (CitizensAPI.getNPCRegistry().getById(0) != null){
					NPC npc = CitizensAPI.getNPCRegistry().getById(0);
					Quest quest = new Quest(internal, questname, questoutline, reward, stages, npc);
					if (qconfig.getString("任務列表." + internal + ".不符合任務需求訊息") != null)
						quest.setFailMessage(qconfig.getString("任務列表." + internal + ".不符合任務需求訊息"));
					
					//Requirements
					if (qconfig.isConfigurationSection("任務列表." + internal + ".任務需求")){
						if (qconfig.getInt("任務列表." + internal + ".任務需求.Level") != 0)
							quest.getRequirements().put(RequirementType.LEVEL, qconfig.getInt("任務列表." + internal + ".任務需求.Level"));
						if (qconfig.getStringList("任務列表." + internal + ".任務需求.Quest") != null){
							quest.getRequirements().put(RequirementType.QUEST, qconfig.getStringList("任務列表." + internal + ".任務需求.Quest"));
						}
						if (qconfig.isConfigurationSection("任務列表." + internal + ".任務需求.Item")){
							List<ItemStack> l = new ArrayList<>();
							for (String i : qconfig.getConfigurationSection("任務列表." + internal + ".任務需求.Item").getKeys(false)) {
								l.add(QuestUtil.getItemStack(qconfig, "任務列表." + internal + ".任務需求.Item." + i));
							}
							quest.getRequirements().put(RequirementType.ITEM, l);
						}
						if (qconfig.getStringList("任務列表." + internal + ".任務需求.Scoreboard") != null){
							quest.getRequirements().put(RequirementType.SCOREBOARD, qconfig.getStringList("任務列表." + internal + ".任務需求.Scoreboard"));
						}
						if (qconfig.getStringList("任務列表." + internal + ".任務需求.NBTTag") != null){
							quest.getRequirements().put(RequirementType.NBTTAG, qconfig.getStringList("任務列表." + internal + ".任務需求.NBTTag"));
						}
					}
					
					//Triggers
					if (qconfig.getStringList("任務列表." + internal + ".任務觸發事件") != null){
						List<QuestTrigger> list = new ArrayList<>();
						for (String tri : qconfig.getStringList("任務列表." + internal + ".任務觸發事件")){
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
					if (qconfig.getBoolean("任務列表." + internal + ".可重複執行")){
						quest.setRedoable(true);
						quest.setRedoDelay(qconfig.getLong("任務列表." + internal + ".重複執行時間"));
					}
					QuestStorage.Quests.put(internal, quest);
					Bukkit.getLogger().log(Level.INFO, "任務 " + questname + " 已經讀取完畢。");
				}else{
					Bukkit.getLogger().log(Level.SEVERE, "任務 " + questname + " 的NPC ID 無法讀取！");
					Bukkit.getLogger().log(Level.SEVERE, "任務 " + questname + " 已經跳過讀取。");
					continue;
				}
			}else{
				Bukkit.getLogger().log(Level.SEVERE, qconfig.getInt("任務列表." + internal + ".任務NPC") + "");
				Bukkit.getLogger().log(Level.SEVERE, "任務 " + questname + " 的NPC ID 不正確！請重新確認！");
				Bukkit.getLogger().log(Level.SEVERE, "任務 " + questname + " 已經跳過讀取。");
				continue;
			}
		}
	}

}
