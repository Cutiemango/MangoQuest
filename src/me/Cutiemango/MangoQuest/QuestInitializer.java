package me.Cutiemango.MangoQuest;

import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

import net.citizensnpcs.api.CitizensPlugin;
import net.elseland.xikage.MythicMobs.MythicMobs;
import net.milkbowl.vault.Vault;
import net.milkbowl.vault.economy.Economy;

public class QuestInitializer {
	
	public QuestInitializer(Main main){
		plugin = main;
	}
	
	public Main plugin;
	
	public Economy economy;
	public CitizensPlugin citizens;
	public Vault vault;
	public MythicMobs MTMplugin;
	
	public void initPlugins(){
		try {
			if (plugin.getServer().getPluginManager().getPlugin("Citizens") != null) {
				citizens = (CitizensPlugin) plugin.getServer().getPluginManager().getPlugin("Citizens");
				plugin.getLogger().info("Citizens插件已經連結成功。");
			}
			else
				plugin.getLogger().severe("未連結NPC插件，請安裝Citizens插件否則插件無法運作！");
			
			if (plugin.getServer().getPluginManager().getPlugin("Vault") != null) {
				vault = (Vault) plugin.getServer().getPluginManager().getPlugin("Vault");
				plugin.getLogger().info("Vault插件已經連結成功。");
			}
			else
				plugin.getLogger().severe("未連結Vault，請重新安裝否則插件無法運作！");
			
			if (plugin.getServer().getPluginManager().getPlugin("MythicMobs") != null){
				MTMplugin = (MythicMobs) plugin.getServer().getPluginManager().getPlugin("MythicMobs");
				plugin.getLogger().info("MythicMobs - 自訂怪物插件已經連結成功！");
			}
			else
				plugin.getLogger().severe("無法偵測到MythicMobs插件！某些功能無法使用！");
			
			RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServicesManager().getRegistration(Economy.class);
			if (economyProvider != null) {
				economy = economyProvider.getProvider();
			}
			
			if (economy != null)
				plugin.getLogger().info("經濟插件已經連結成功。");
			else
				plugin.getLogger().severe("未連結金錢插件，金錢功能將無法使用！");

		} catch (Exception e) {
			plugin.getLogger().severe("連結前置插件時發生錯誤。請檢查是否已經安裝所有前置插件。");
			e.printStackTrace();
		}
	}
	
	public Economy getEconomy(){
		return economy;
	}
	
	public boolean hasEconomyEnabled(){
		return economy != null;
	}
	
	public MythicMobs getMTMPlugin(){
		return MTMplugin;
	}
	
	public boolean hasMythicMobEnabled(){
		return MTMplugin != null;
	}
	
	public boolean hasCitizensEnabled(){
		return citizens != null;
	}
}
