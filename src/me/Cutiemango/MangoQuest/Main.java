package me.Cutiemango.MangoQuest;

import me.Cutiemango.MangoQuest.commands.AdminCommand;
import me.Cutiemango.MangoQuest.commands.CommandReceiver;
import me.Cutiemango.MangoQuest.data.QuestPlayerData;
import me.Cutiemango.MangoQuest.listeners.MainListener;
import me.Cutiemango.MangoQuest.manager.*;
import me.Cutiemango.MangoQuest.manager.config.QuestConfigManager;
import me.Cutiemango.MangoQuest.manager.database.DatabaseManager;
import me.Cutiemango.MangoQuest.questobject.SimpleQuestObject;
import me.Cutiemango.MangoQuest.versions.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;

import java.util.logging.Level;

public class Main extends JavaPlugin
{
	private static Main instance;

	public PluginHooker pluginHooker;
	public VersionHandler handler;
	public QuestConfigManager configManager;

	private int counterTaskID = -1;

	@Override
	public void onEnable()
	{
		instance = this;
		getCommand("mq").setExecutor(new CommandReceiver());
		getCommand("mqa").setExecutor(new AdminCommand());

		configManager = new QuestConfigManager();
		pluginHooker = new PluginHooker(this);
		pluginHooker.hookPlugins();
		SimpleQuestObject.initObjectNames();
		if (ConfigSettings.USE_DATABASE)
			DatabaseManager.initPlayerDB();

		getServer().getPluginManager().registerEvents(new MainListener(), this);

		String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
		switch (version)
		{
			case "v1_13_R1":
				handler = new Version_v1_13_R1();
				break;
			case "v1_13_R2":
				handler = new Version_v1_13_R2();
				break;
			case "v1_14_R1":
				handler = new Version_v1_14_R1();
				break;
			case "v1_15_R1":
				handler = new Version_v1_15_R1();
				break;
			case "v1_16_R1":
				handler = new Version_v1_16_R1();
				QuestChatManager.logCmd(Level.WARNING, I18n.locMsg("Cmdlog.TestingVersion"));
				break;
			default:
				QuestChatManager.logCmd(Level.SEVERE, I18n.locMsg("Cmdlog.VersionNotSupported1"));
				QuestChatManager.logCmd(Level.SEVERE, I18n.locMsg("Cmdlog.VersionNotSupported2"));
				break;
		}

		QuestChatManager.logCmd(Level.INFO, I18n.locMsg("Cmdlog.LoadedNMSVersion", version));

		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				CustomObjectManager.loadCustomObjects();
				QuestConfigManager.getLoader().loadAll();
				loadPlayers();
			}
		}.runTaskLater(this, 5L);

		startCounter();

		// Use new metrics!! Yay!!
		new Metrics(this);

		DebugHandler.log(1, "Plugin Loaded!");
	}

	@Override
	public void onDisable()
	{
		stopCounter();
		savePlayers();
		DebugHandler.log(1, I18n.locMsg("Cmdlog.Disabled"));
	}

	public void reload()
	{
		savePlayers();
		QuestStorage.clear();
		QuestNPCManager.clear();

		configManager = new QuestConfigManager();
		pluginHooker = new PluginHooker(this);
		pluginHooker.hookPlugins();
		configManager.loadFile();

		SimpleQuestObject.initObjectNames();
		CustomObjectManager.loadCustomObjects();
		QuestConfigManager.getLoader().loadAll();
		loadPlayers();
	}


	public static Main getInstance()
	{
		return instance;
	}

	public static PluginHooker getHooker()
	{
		return instance.pluginHooker;
	}

	public void loadPlayers()
	{
		for (Player p : Bukkit.getOnlinePlayers())
		{
			QuestPlayerData qd = new QuestPlayerData(p);
			QuestStorage.Players.put(p.getName(), qd);
		}
	}

	public void savePlayers()
	{
		for (Player p : Bukkit.getOnlinePlayers())
		{
			if (QuestUtil.getData(p) != null)
			{
				QuestUtil.getData(p).save();
				QuestChatManager.info(p, I18n.locMsg("CommandInfo.PlayerDataSaving"));
			}
		}
	}

	public void startCounter()
	{
		counterTaskID = new BukkitRunnable()
		{
			int counter = 0;
			@Override
			public void run()
			{
				for (Player p : Bukkit.getOnlinePlayers())
				{
					QuestPlayerData pd = QuestUtil.getData(p);
					
					if (pd == null)
						continue;
					if (counter++ > ConfigSettings.PLAYER_DATA_SAVE_INTERVAL)
					{
						pd.save();
						counter = 0;
					}
					
					pd.checkQuestFail();
					
					if (ConfigSettings.USE_PARTICLE_EFFECT)
					{
						try
						{
							QuestNPCManager.effectTask(pd);
						}
						catch (Exception e)
						{
							System.out.println(e);
							e.printStackTrace();
							this.cancel();
						}
					}
					if (ConfigSettings.ENABLE_SCOREBOARD)
					{
						Bukkit.getScheduler().runTask(Main.instance, () ->
						{
							try
							{
								Scoreboard score = ScoreboardManager.update(pd);
								pd.getPlayer().setScoreboard(score);
							}
							catch (Exception e)
							{
								QuestChatManager.logCmd(Level.SEVERE, I18n.locMsg("Cmdlog.ScoreboardException"));
								System.out.println(e);
								e.printStackTrace();
								this.cancel();
							}
						});
					}
				}
			}
		}.runTaskTimer(this, 0L, 20L).getTaskId();
	}

	public void stopCounter()
	{
		if (counterTaskID != -1)
			Bukkit.getScheduler().cancelTask(counterTaskID);
	}

}
