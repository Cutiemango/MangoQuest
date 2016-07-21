package me.Cutiemango.MangoQuest;

import java.util.logging.Level;

import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import me.Cutiemango.MangoQuest.listeners.PlayerListener;
import me.Cutiemango.MangoQuest.listeners.QuestListener;
import net.citizensnpcs.api.CitizensPlugin;
import net.milkbowl.vault.Vault;
import net.milkbowl.vault.economy.Economy;

public class Main extends JavaPlugin{
	
	public static Main instance;
	public static Economy economy = null;
	public CitizensPlugin citizens;
	public Vault vault = null;
	
	@Override
	public void onEnable(){
		instance = this;
		
		linkOtherPlugins();
		
		getCommand("mq").setExecutor(new QuestCommand());
		getServer().getPluginManager().registerEvents(new QuestListener(), this);
		getServer().getPluginManager().registerEvents(new PlayerListener(), this);
		new QuestConfigLoad(this);
		
		getLogger().info("已經開啟！");
	}
	
	@Override
	public void onDisable(){
		getLogger().info("已經關閉！");
	}
	
	private void linkOtherPlugins() {
		try {
			if (getServer().getPluginManager().getPlugin("Citizens") != null) {
				citizens = (CitizensPlugin) getServer().getPluginManager().getPlugin("Citizens");
			}
			getLogger().log(Level.INFO, "Citizens插件已經連結成功。");
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "沒有符合版本的Citizens插件。NPC功能將不會啟動。");

		}

		RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager()
				.getRegistration(net.milkbowl.vault.economy.Economy.class);
		if (economyProvider != null) {
			economy = economyProvider.getProvider();
			getLogger().log(Level.INFO, "Vault插件已經連結成功。");
		}
	}

}
