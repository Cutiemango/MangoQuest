package me.Cutiemango.MangoQuest.manager;

import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import com.nisovin.shopkeepers.ShopkeepersPlugin;
import com.sucy.skill.SkillAPI;
import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.api.bukkit.BukkitAPIHelper;
import me.Cutiemango.MangoQuest.Main;
import me.old.RPGshop.RPGshop;
import me.Cutiemango.MangoQuest.I18n;
import net.citizensnpcs.api.CitizensPlugin;
import net.milkbowl.vault.Vault;
import net.milkbowl.vault.economy.Economy;

public class PluginHooker
{

	public PluginHooker(Main main)
	{
		plugin = main;
	}

	private Main plugin;

	private Economy economy;
	private CitizensPlugin citizens;
	private Vault vault;
	private MythicMobs MTMplugin;
	private ShopkeepersPlugin shopkeepers;
	private SkillAPI skillapi;
	private RPGshop rpgshop;

	public void hookPlugins()
	{
		try
		{
			citizens = (CitizensPlugin) plugin.getServer().getPluginManager().getPlugin("Citizens");
			if (citizens != null)
				QuestChatManager.logCmd(Level.INFO, I18n.locMsg("PluginHooker.CitizensHooked"));
			else
			{
				QuestChatManager.logCmd(Level.SEVERE, I18n.locMsg("PluginHooker.PluginNotHooked"));
				QuestChatManager.logCmd(Level.SEVERE, I18n.locMsg("PluginHooker.CitizensNotHooked1"));
				QuestChatManager.logCmd(Level.SEVERE, I18n.locMsg("PluginHooker.CitizensNotHooked2"));
				QuestChatManager.logCmd(Level.SEVERE, I18n.locMsg("PluginHooker.PleaseInstall"));
			}
		
			vault = (Vault) plugin.getServer().getPluginManager().getPlugin("Vault");
			if (vault != null)
				QuestChatManager.logCmd(Level.INFO, I18n.locMsg("PluginHooker.VaultHooked"));
			else
			{
				QuestChatManager.logCmd(Level.SEVERE, I18n.locMsg("PluginHooker.PluginNotHooked"));
				QuestChatManager.logCmd(Level.SEVERE, I18n.locMsg("PluginHooker.VaultNotHooked"));
				QuestChatManager.logCmd(Level.SEVERE, I18n.locMsg("PluginHooker.PleaseInstall"));
			}
			
			MTMplugin = (MythicMobs) plugin.getServer().getPluginManager().getPlugin("MythicMobs");
			if (MTMplugin != null)
				QuestChatManager.logCmd(Level.INFO, I18n.locMsg("PluginHooker.MythicMobsHooked"));
			else
				Main.debug(I18n.locMsg("PluginHooker.MythicMobsNotHooked"));
			
			shopkeepers = (ShopkeepersPlugin) plugin.getServer().getPluginManager().getPlugin("Shopkeepers");
			if (shopkeepers != null)
				QuestChatManager.logCmd(Level.INFO, I18n.locMsg("PluginHooker.ShopkeepersHooked"));
			else
				Main.debug(I18n.locMsg("PluginHooker.ShopkeepersNotHooked"));
			
			skillapi = (SkillAPI) plugin.getServer().getPluginManager().getPlugin("SkillAPI");
			if (skillapi != null)
				QuestChatManager.logCmd(Level.INFO, I18n.locMsg("PluginHooker.SkillAPIHooked"));
			else
				Main.debug(I18n.locMsg("PluginHooker.SkillAPINotHooked"));
			rpgshop = (RPGshop) plugin.getServer().getPluginManager().getPlugin("RPGshop");
			if (rpgshop != null)
				QuestChatManager.logCmd(Level.INFO, I18n.locMsg("PluginHooker.RPGshopHooked"));
		}
		catch (Exception e){}


		RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServicesManager().getRegistration(Economy.class);
		if (economyProvider != null && economyProvider.getProvider() != null)
		{
			economy = economyProvider.getProvider();
			QuestChatManager.logCmd(Level.INFO, I18n.locMsg("PluginHooker.EconomyHooked"));
		}
		else
			QuestChatManager.logCmd(Level.SEVERE, I18n.locMsg("PluginHooker.EconomyNotHooked"));
	}

	public Economy getEconomy()
	{
		return economy;
	}

	public boolean hasEconomyEnabled()
	{
		return economy != null;
	}

	public MythicMobs getMTMPlugin()
	{
		return MTMplugin;
	}

	public Vault getVault()
	{
		return vault;
	}

	public ShopkeepersPlugin getShopkeepers()
	{
		return shopkeepers;
	}


	public boolean hasMythicMobEnabled()
	{
		return MTMplugin != null;
	}

	public boolean hasCitizensEnabled()
	{
		return citizens != null;
	}

	public boolean hasShopkeepersEnabled()
	{
		return shopkeepers != null;
	}
	
	public boolean hasSkillAPIEnabled()
	{
		return skillapi != null;
	}

	public BukkitAPIHelper getMythicMobsAPI()
	{
		return MTMplugin.getAPIHelper();
	}
}
