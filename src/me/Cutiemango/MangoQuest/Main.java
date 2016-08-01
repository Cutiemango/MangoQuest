package me.Cutiemango.MangoQuest;

import java.util.logging.Level;

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
import net.citizensnpcs.api.CitizensPlugin;
import net.milkbowl.vault.Vault;
import net.milkbowl.vault.economy.Economy;

public class Main extends JavaPlugin{
	
	public static Main instance;
	public static Economy economy;
	public CitizensPlugin citizens;
	public Vault vault = null;
	
	private QuestConfigLoad cfg;
	
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
		
		getLogger().info("已經開啟！");
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
			}
			if (setupEconomy())
				getLogger().log(Level.INFO, "金錢插件已經連結成功。");
			else
				getLogger().log(Level.INFO, "未連結金錢插件。");
			if (getServer().getPluginManager().getPlugin("Vault") != null) {
				vault = (Vault) getServer().getPluginManager().getPlugin("Vault");
			}
			getLogger().log(Level.INFO, "Citizens插件已經連結成功。");
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
