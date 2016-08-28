package me.Cutiemango.MangoQuest;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import me.Cutiemango.MangoQuest.commands.QuestCommand;
import me.Cutiemango.MangoQuest.commands.QuestEditorCommand;
import me.Cutiemango.MangoQuest.data.QuestPlayerData;
import me.Cutiemango.MangoQuest.editor.QuestEditorListener;
import me.Cutiemango.MangoQuest.listeners.PlayerListener;
import me.Cutiemango.MangoQuest.listeners.QuestListener;
import me.Cutiemango.MangoQuest.versions.QuestVersionHandler;
import me.Cutiemango.MangoQuest.versions.Version_v1_10_R1;
import me.Cutiemango.MangoQuest.versions.Version_v1_9_R1;
import me.Cutiemango.MangoQuest.versions.Version_v1_9_R2;
import net.citizensnpcs.api.CitizensPlugin;
import net.milkbowl.vault.Vault;
import net.milkbowl.vault.economy.Economy;

public class Main extends JavaPlugin{
	
	public static Main instance;
	
	public static Economy economy;
	public CitizensPlugin citizens;
	public Vault vault;
	
	public QuestVersionHandler handler;
	
	public QuestConfigLoad cfg;
	
	@Override
	public void onEnable(){
		instance = this;
		
		getCommand("mq").setExecutor(new QuestCommand());
		getCommand("mqe").setExecutor(new QuestEditorCommand());

		getServer().getPluginManager().registerEvents(new QuestListener(), this);
		getServer().getPluginManager().registerEvents(new PlayerListener(), this);
		getServer().getPluginManager().registerEvents(new QuestEditorListener(), this);
		cfg = new QuestConfigLoad(this);
		
		linkOtherPlugins();
		
		String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
		switch(version){
			case "v1_9_R1":
				handler = new Version_v1_9_R1();
				getLogger().info("成功讀取伺服器版本，插件成功開啟：NMS v1_9_R1。");
				break;
			case "v1_9_R2":
				handler = new Version_v1_9_R2();
				getLogger().info("成功讀取伺服器版本，插件成功開啟：NMS v1_9_R2。");
				break;
			case "v1_10_R1":
				handler = new Version_v1_10_R1();
				getLogger().info("成功讀取伺服器版本，插件成功開啟：NMS v1_10_R1。");
				break;
			default:
				getLogger().severe("您的伺服器版本不支援此插件，可支援的版本：1.9~1.10.2。");
				getLogger().severe("插件功能將無法運作，請考慮移除。");
				break;
		}
		
		new BukkitRunnable(){
			@Override
			public void run() {
				cfg.loadQuests();
				for (Player p : Bukkit.getOnlinePlayers()){
					QuestPlayerData qd = new QuestPlayerData(p);
					if (QuestPlayerData.hasConfigData(p))
						qd = new QuestPlayerData(p, QuestConfigLoad.pconfig);
					QuestStorage.Players.put(p.getName(), qd);
				}
			}
		}.runTaskLater(this, 5L);
	}
	
	@Override
	public void onDisable(){
		getLogger().info("已經關閉！");
		for (Player p : Bukkit.getOnlinePlayers()){
			QuestUtil.getData(p).save();
			QuestUtil.info(p, "&b玩家資料儲存中...");
		}
	}
	
	private void linkOtherPlugins() {
		try {
			if (getServer().getPluginManager().getPlugin("Citizens") != null) {
				citizens = (CitizensPlugin) getServer().getPluginManager().getPlugin("Citizens");
				getLogger().info("Citizens插件已經連結成功。");
			}
			else
				getLogger().severe("未連結NPC插件，請安裝Citizens插件否則插件無法運作！");
			
			if (getServer().getPluginManager().getPlugin("Vault") != null) {
				vault = (Vault) getServer().getPluginManager().getPlugin("Vault");
				getLogger().info("Vault插件已經連結成功。");
			}
			else
				getLogger().severe("未連結Vault，請重新安裝否則插件無法運作！");
			
			if (setupEconomy())
				getLogger().info("經濟插件已經連結成功。");
			else
				getLogger().severe("未連結金錢插件，請安裝iEconomy等經濟插件！");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private boolean setupEconomy() {
		RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServicesManager().getRegistration(Economy.class);
		if (economyProvider != null) {
			economy = economyProvider.getProvider();
		}

		return (economy != null);
	}

}
