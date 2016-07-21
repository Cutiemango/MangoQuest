package me.Cutiemango.MangoQuest;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import me.Cutiemango.MangoQuest.questobjects.QuestObjectItemDeliver;
import me.Cutiemango.MangoQuest.questobjects.QuestObjectTalkToNPC;
import me.Cutiemango.MangoQuest.questobjects.SimpleQuestObject;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;

public class QuestConfigLoad {
	
	public static FileConfiguration pconfig;
	private FileConfiguration qconfig;
	private Main plugin;
	
	public QuestConfigLoad(Main pl){
		plugin = pl;
		init();
		new BukkitRunnable(){
			@Override
			public void run() {
				loadQuests();
			}
		}.runTaskLater(plugin, 5L);
	}
	
	private void init(){
		File file = new File(plugin.getDataFolder(), "players.yml");
		
		if (!file.exists()){
			plugin.saveResource("players.yml", true);
			Bukkit.getLogger().log(Level.SEVERE, "[MangoQuest] 找不到players.yml，建立新檔案！");
		}
		pconfig = YamlConfiguration.loadConfiguration(file);
		
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
					case "ITEM_DELIVER":
						obj = new QuestObjectItemDeliver(CitizensAPI.getNPCRegistry()
								.getById(qconfig.getInt("任務列表." + internal + ".任務內容." + scount + "." + ocount + ".目標NPC")),
						QuestUtil.getItemStack(qconfig, "任務列表." + internal + ".任務內容." + scount + "." + ocount + ".物品"),
						qconfig.getInt("任務列表." + internal + ".任務內容." + scount + "." + ocount + ".物品.數量"));
						break;
					case "TALK_TO_NPC":
						obj = new QuestObjectTalkToNPC(CitizensAPI.getNPCRegistry()
								.getById(qconfig.getInt("任務列表." + internal + ".任務內容." + scount + "." + ocount + ".目標NPC")));
						break;
					default:
						break;
					}
					objs.add(obj);
				}
				QuestStage qs = new QuestStage(null, null, objs);
				stages.add(qs);
			}
			QuestReward reward = new QuestReward(QuestUtil.getItemStack(qconfig, "任務列表." + internal + ".任務獎勵.物品.1"));
			for (String temp : qconfig.getConfigurationSection("任務列表." + internal + ".任務獎勵.物品").getKeys(false)) {
				int count = Integer.parseInt(temp);
				if (count == 1)
					continue;
				reward.add(QuestUtil.getItemStack(qconfig, "任務列表." + internal + ".任務獎勵.物品." + count));
			}
			
			if (plugin.citizens != null && qconfig.contains("任務列表." + internal + ".任務NPC")){
				if (CitizensAPI.getNPCRegistry().getById(0) != null){
					NPC npc = CitizensAPI.getNPCRegistry().getById(0);
					Quest quest = new Quest(internal, questname, questoutline, reward, stages, npc);
					QuestStorage.Quests.put(internal, quest);
					Bukkit.getLogger().log(Level.INFO, "任務 " + questname + " 已經讀取成功！");
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
