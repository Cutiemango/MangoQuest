package me.Cutiemango.MangoQuest;

import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import com.nisovin.shopkeepers.ShopkeepersPlugin;
import net.citizensnpcs.api.CitizensPlugin;
import net.elseland.xikage.MythicMobs.MythicMobs;
import net.elseland.xikage.MythicMobs.API.IMobsAPI;
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
			plugin.getLogger().info("Citizens插件連接成功！");
		}
		catch (NoClassDefFoundError | NullPointerException e)
		{
			plugin.getLogger().severe("未連結NPC插件，發生此錯誤的原因可能有：");
			plugin.getLogger().severe("- 您未安裝Citizens插件");
			plugin.getLogger().severe("- 您安裝的Citizens插件與此伺服器端不相容。");
			plugin.getLogger().severe("請檢查並修復此錯誤，若您確認安裝了正確的版本，請聯絡插件開發者。");
		}

		try
		{
			vault = (Vault) plugin.getServer().getPluginManager().getPlugin("Vault");
			plugin.getLogger().info("Vault插件已經連結成功。");
		}
		catch (NoClassDefFoundError | NullPointerException e)
		{
			plugin.getLogger().severe("未連結Vault插件，發生此錯誤的原因可能有：");
			plugin.getLogger().severe("- 您未安裝Vault插件");
			plugin.getLogger().severe("請檢查並修復此錯誤，若您確認安裝了正確的版本，請聯絡插件開發者。");
		}

		try
		{
			MTMplugin = (MythicMobs) plugin.getServer().getPluginManager().getPlugin("MythicMobs");
			plugin.getLogger().info("MythicMobs - 自訂怪物插件已經連結成功！");
		}
		catch (NoClassDefFoundError | NullPointerException e)
		{
			plugin.getLogger().severe("無法偵測到MythicMobs插件！某些功能無法使用！");
		}

		if (plugin.getServer().getPluginManager().getPlugin("Shopkeepers") != null)
		{
			shopkeepers = (ShopkeepersPlugin) plugin.getServer().getPluginManager().getPlugin("Shopkeepers");
			plugin.getLogger().info("Shopkeepers - 交易商人插件已經連結成功！");
		}

		RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServicesManager().getRegistration(Economy.class);
		if (economyProvider != null && economyProvider.getProvider() != null)
		{
			economy = economyProvider.getProvider();
			plugin.getLogger().info("經濟插件已經連結成功。");
		}
		else
			plugin.getLogger().severe("未連結金錢插件，請安裝金錢插件否則無法使用！");
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

	public IMobsAPI getMythicMobsAPI()
	{
		return MTMplugin.getAPI().getMobAPI();
	}
}
