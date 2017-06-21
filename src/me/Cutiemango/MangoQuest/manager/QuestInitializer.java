package me.Cutiemango.MangoQuest.manager;

import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import com.nisovin.shopkeepers.ShopkeepersPlugin;
import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.api.bukkit.BukkitAPIHelper;
import me.Cutiemango.MangoQuest.Main;
import me.Cutiemango.MangoQuest.Questi18n;
import net.citizensnpcs.api.CitizensPlugin;
import net.milkbowl.vault.Vault;
import net.milkbowl.vault.economy.Economy;

public class QuestInitializer
{

	public QuestInitializer(Main main)
	{
		plugin = main;
	}

	private Main plugin;

	private Economy economy;
	private CitizensPlugin citizens;
	private Vault vault;
	private MythicMobs MTMplugin;
	private ShopkeepersPlugin shopkeepers;

	public void initPlugins()
	{
		try
		{
			citizens = (CitizensPlugin) plugin.getServer().getPluginManager().getPlugin("Citizens");
			if (citizens != null)
				QuestChatManager.logCmd(Level.INFO, Questi18n.localizeMessage("PluginInitializer.CitizensHooked"));
			else throw new NullPointerException();
		}
		catch (NoClassDefFoundError | NullPointerException e)
		{
			QuestChatManager.logCmd(Level.SEVERE, Questi18n.localizeMessage("PluginInitializer.PluginNotHooked"));
			QuestChatManager.logCmd(Level.SEVERE, Questi18n.localizeMessage("PluginInitializer.CitizensNotHooked1"));
			QuestChatManager.logCmd(Level.SEVERE, Questi18n.localizeMessage("PluginInitializer.CitizensNotHooked2"));
			QuestChatManager.logCmd(Level.SEVERE, Questi18n.localizeMessage("PluginInitializer.PleaseInstall"));
		}

		try
		{
			vault = (Vault) plugin.getServer().getPluginManager().getPlugin("Vault");
			if (vault != null)
				QuestChatManager.logCmd(Level.INFO, Questi18n.localizeMessage("PluginInitializer.VaultHooked"));
			else throw new NullPointerException();
		}
		catch (NoClassDefFoundError | NullPointerException e)
		{
			QuestChatManager.logCmd(Level.SEVERE, Questi18n.localizeMessage("PluginInitializer.PluginNotHooked"));
			QuestChatManager.logCmd(Level.SEVERE, Questi18n.localizeMessage("PluginInitializer.VaultNotHooked"));
			QuestChatManager.logCmd(Level.SEVERE, Questi18n.localizeMessage("PluginInitializer.PleaseInstall"));
		}

		try
		{
			MTMplugin = (MythicMobs) plugin.getServer().getPluginManager().getPlugin("MythicMobs");
			if (MTMplugin != null)
				QuestChatManager.logCmd(Level.INFO, Questi18n.localizeMessage("PluginInitializer.MythicMobsHooked"));
			else throw new NullPointerException();
		}
		catch (NoClassDefFoundError | NullPointerException e)
		{
			QuestChatManager.logCmd(Level.WARNING, Questi18n.localizeMessage("PluginInitializer.MythicMobsNotHooked"));
		}
	
		try
		{
			shopkeepers = (ShopkeepersPlugin) plugin.getServer().getPluginManager().getPlugin("Shopkeepers");
			if (shopkeepers != null)
				QuestChatManager.logCmd(Level.INFO, Questi18n.localizeMessage("PluginInitializer.ShopkeepersHooked"));
			else throw new NullPointerException();
		}
		catch (NoClassDefFoundError | NullPointerException e)
		{
			QuestChatManager.logCmd(Level.WARNING, Questi18n.localizeMessage("PluginInitializer.ShopkeepersNotHooked"));
		}

		RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServicesManager().getRegistration(Economy.class);
		if (economyProvider != null && economyProvider.getProvider() != null)
		{
			economy = economyProvider.getProvider();
			QuestChatManager.logCmd(Level.INFO, Questi18n.localizeMessage("PluginInitializer.EconomyHooked"));
		}
		else
			QuestChatManager.logCmd(Level.SEVERE, Questi18n.localizeMessage("PluginInitializer.EconomyNotHooked"));
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

	public BukkitAPIHelper getMythicMobsAPI()
	{
		return MTMplugin.getAPIHelper();
	}
}
