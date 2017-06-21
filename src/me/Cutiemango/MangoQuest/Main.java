package me.Cutiemango.MangoQuest;

import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import me.Cutiemango.MangoQuest.commands.AdminCommand;
import me.Cutiemango.MangoQuest.commands.CommandReceiver;
import me.Cutiemango.MangoQuest.data.QuestPlayerData;
import me.Cutiemango.MangoQuest.listeners.MythicListener;
import me.Cutiemango.MangoQuest.listeners.QuestListener;
import me.Cutiemango.MangoQuest.manager.QuestChatManager;
import me.Cutiemango.MangoQuest.manager.QuestConfigManager;
import me.Cutiemango.MangoQuest.manager.QuestInitializer;
import me.Cutiemango.MangoQuest.versions.QuestVersionHandler;
import me.Cutiemango.MangoQuest.versions.Version_v1_10_R1;
import me.Cutiemango.MangoQuest.versions.Version_v1_11_R1;
import me.Cutiemango.MangoQuest.versions.Version_v1_12_R1;
import me.Cutiemango.MangoQuest.versions.Version_v1_8_R1;
import me.Cutiemango.MangoQuest.versions.Version_v1_8_R2;
import me.Cutiemango.MangoQuest.versions.Version_v1_8_R3;
import me.Cutiemango.MangoQuest.versions.Version_v1_9_R1;
import me.Cutiemango.MangoQuest.versions.Version_v1_9_R2;

public class Main extends JavaPlugin
{

	public static Main instance;

	public QuestInitializer initManager;
	public QuestVersionHandler handler;
	public QuestConfigManager configManager;
	
	private static boolean VERSION_HIGHER_THAN_1_12 = false;

	@Override
	public void onEnable()
	{
		instance = this;
		getCommand("mq").setExecutor(new CommandReceiver());
		getCommand("mqa").setExecutor(new AdminCommand());
		
		configManager = new QuestConfigManager(this);
		configManager.loadConfig();
		
		initManager = new QuestInitializer(this);
		initManager.initPlugins();

		getServer().getPluginManager().registerEvents(new QuestListener(), this);

		if (initManager.hasMythicMobEnabled())
			getServer().getPluginManager().registerEvents(new MythicListener(), this);

		String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
		switch (version)
		{
			case "v1_8_R1":
				handler = new Version_v1_8_R1();
				break;
			case "v1_8_R2":
				handler = new Version_v1_8_R2();
				break;
			case "v1_8_R3":
				handler = new Version_v1_8_R3();
				break;
			case "v1_9_R1":
				handler = new Version_v1_9_R1();
				break;
			case "v1_9_R2":
				handler = new Version_v1_9_R2();
				break;
			case "v1_10_R1":
				handler = new Version_v1_10_R1();
				break;
			case "v1_11_R1":
				handler = new Version_v1_11_R1();
				break;
			case "v1_12_R1":
				handler = new Version_v1_12_R1();
				VERSION_HIGHER_THAN_1_12 = true;
				break;
			default:
				QuestChatManager.logCmd(Level.SEVERE,  Questi18n.localizeMessage("Cmdlog.VersionNotSupported1"));
				QuestChatManager.logCmd(Level.SEVERE, Questi18n.localizeMessage("Cmdlog.VersionNotSupported2"));
				break;
		}

		QuestChatManager.logCmd(Level.INFO, Questi18n.localizeMessage("Cmdlog.LoadedNMSVersion", version));

		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				configManager.load();
				for (Player p : Bukkit.getOnlinePlayers())
				{
					QuestPlayerData qd = new QuestPlayerData(p);
					if (QuestPlayerData.hasConfigData(p))
						qd = new QuestPlayerData(p, configManager.getPlayerIO());
					QuestStorage.Players.put(p.getName(), qd);
				}
				this.cancel();
			}
		}.runTaskLater(this, 5L);
	}

	@Override
	public void onDisable()
	{
		QuestChatManager.logCmd(Level.INFO, Questi18n.localizeMessage("Cmdlog.Disabled"));
		savePlayers();
	}

	public void reload()
	{
		savePlayers();
		QuestStorage.clear();

		configManager = new QuestConfigManager(this);
		configManager.load();

		for (Player p : Bukkit.getOnlinePlayers())
		{
			QuestPlayerData qd = new QuestPlayerData(p);
			if (QuestPlayerData.hasConfigData(p))
				qd = new QuestPlayerData(p, configManager.getPlayerIO());
			QuestStorage.Players.put(p.getName(), qd);
		}
	}
	
	public static boolean isUsingUpdatedVersion()
	{
		return VERSION_HIGHER_THAN_1_12;
	}
	
	public void savePlayers()
	{
		for (Player p : Bukkit.getOnlinePlayers())
		{
			QuestUtil.getData(p).save();
			QuestChatManager.info(p, Questi18n.localizeMessage("CommandInfo.PlayerDataSaving"));
		}
	}
	
	public static void debug(String msg)
	{
		QuestChatManager.logCmd(Level.INFO, "[DEBUG]" + msg);
	}

}
