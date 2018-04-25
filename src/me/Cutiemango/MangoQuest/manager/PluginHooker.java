package me.Cutiemango.MangoQuest.manager;

import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import com.nisovin.shopkeepers.ShopkeepersPlugin;
import com.sucy.skill.SkillAPI;
import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.api.bukkit.BukkitAPIHelper;
import io.lumine.xikage.mythicmobs.mobs.MythicMob;
import me.Cutiemango.MangoQuest.Main;
import me.old.RPGshop.RPGshop;
import me.Cutiemango.MangoQuest.I18n;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.CitizensPlugin;
import net.citizensnpcs.api.npc.NPC;
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
	private ShopkeepersPlugin shopkeepers;
	private SkillAPI skillapi;
	private RPGshop rpgshop;

	public void hookPlugins()
	{
		try
		{
			if (plugin.getServer().getPluginManager().isPluginEnabled("Citizens"))
			{
				citizens = (CitizensPlugin) plugin.getServer().getPluginManager().getPlugin("Citizens");
				QuestChatManager.logCmd(Level.INFO, I18n.locMsg("PluginHooker.CitizensHooked"));
			}
			else
			{
				QuestChatManager.logCmd(Level.SEVERE, I18n.locMsg("PluginHooker.PluginNotHooked"));
				QuestChatManager.logCmd(Level.SEVERE, I18n.locMsg("PluginHooker.CitizensNotHooked1"));
				QuestChatManager.logCmd(Level.SEVERE, I18n.locMsg("PluginHooker.CitizensNotHooked2"));
				QuestChatManager.logCmd(Level.SEVERE, I18n.locMsg("PluginHooker.PleaseInstall"));
			}
		
			
			if (plugin.getServer().getPluginManager().isPluginEnabled("Vault"))
			{
				vault = (Vault) plugin.getServer().getPluginManager().getPlugin("Vault");
				QuestChatManager.logCmd(Level.INFO, I18n.locMsg("PluginHooker.VaultHooked"));
			}
			else
			{
				QuestChatManager.logCmd(Level.SEVERE, I18n.locMsg("PluginHooker.PluginNotHooked"));
				QuestChatManager.logCmd(Level.SEVERE, I18n.locMsg("PluginHooker.VaultNotHooked"));
				QuestChatManager.logCmd(Level.SEVERE, I18n.locMsg("PluginHooker.PleaseInstall"));
			}

			if (plugin.getServer().getPluginManager().isPluginEnabled("MythicMobs"))
			{
				QuestChatManager.logCmd(Level.INFO, I18n.locMsg("PluginHooker.MythicMobsHooked"));
			}
			else
				Main.debug(I18n.locMsg("PluginHooker.MythicMobsNotHooked"));
			
			if (plugin.getServer().getPluginManager().isPluginEnabled("Shopkeepers"))
			{
				shopkeepers = (ShopkeepersPlugin) plugin.getServer().getPluginManager().getPlugin("Shopkeepers");
				QuestChatManager.logCmd(Level.INFO, I18n.locMsg("PluginHooker.ShopkeepersHooked"));
			}
			else
				Main.debug(I18n.locMsg("PluginHooker.ShopkeepersNotHooked"));
			
			
			if (plugin.getServer().getPluginManager().isPluginEnabled("SkillAPI"))
			{
				QuestChatManager.logCmd(Level.INFO, I18n.locMsg("PluginHooker.SkillAPIHooked"));
				skillapi = (SkillAPI) plugin.getServer().getPluginManager().getPlugin("SkillAPI");
			}
			else
				Main.debug(I18n.locMsg("PluginHooker.SkillAPINotHooked"));
			
			
			if (plugin.getServer().getPluginManager().isPluginEnabled("RPGshop"))
			{
				rpgshop = (RPGshop) plugin.getServer().getPluginManager().getPlugin("RPGshop");
				QuestChatManager.logCmd(Level.INFO, I18n.locMsg("PluginHooker.RPGshopHooked"));
			}
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
		return plugin.getServer().getPluginManager().isPluginEnabled("MythicMobs");
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
	
	public boolean hasRPGshopEnabled()
	{
		return rpgshop != null;
	}

	public BukkitAPIHelper getMythicMobsAPI()
	{
		return MythicMobs.inst().getAPIHelper();
	}
	
	public MythicMob getMythicMob(String id)
	{
		if (!hasMythicMobEnabled())
			return null;
		return getMythicMobsAPI().getMythicMob(id);
	}
	
	public NPC getNPC(String id)
	{
		if (!hasCitizensEnabled() || !QuestValidater.validateInteger(id))
			return null;
		return CitizensAPI.getNPCRegistry().getById(Integer.parseInt(id));
	}
	
	public NPC getNPC(int id)
	{
		if (!hasCitizensEnabled())
			return null;
		return CitizensAPI.getNPCRegistry().getById(id);
	}
}
