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
import me.Cutiemango.MangoQuest.manager.QuestConfigManager;
import me.Cutiemango.MangoQuest.manager.QuestInitializer;
import me.Cutiemango.MangoQuest.versions.QuestVersionHandler;
import me.Cutiemango.MangoQuest.versions.Version_v1_10_R1;
import me.Cutiemango.MangoQuest.versions.Version_v1_11_R1;
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
			default:
				QuestChatManager.logCmd(Level.SEVERE, "您的伺服器版本不支援此插件，可支援的版本：1.8~1.11。");
				QuestChatManager.logCmd(Level.SEVERE, "插件功能將無法運作，請考慮移除。");
				break;
		}

		QuestChatManager.logCmd(Level.SEVERE, "讀取伺服器版本號為：NMS " + version + "。");

		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				configManager.loadTranslation();
				configManager.loadChoice();
				configManager.loadConversation();
				configManager.loadQuests();
				configManager.loadNPC();
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
		QuestChatManager.logCmd(Level.INFO, "已經關閉！");
		for (Player p : Bukkit.getOnlinePlayers())
		{
			QuestUtil.getData(p).save();
			QuestChatManager.info(p, "&b玩家資料儲存中...");
		}
	}

	public void reload()
	{
		for (Player p : Bukkit.getOnlinePlayers())
		{
			QuestUtil.getData(p).save();
			QuestChatManager.info(p, "&b玩家資料儲存中...");
		}
		QuestStorage.clear();

		configManager = new QuestConfigManager(this);

		configManager.loadConversation();
		configManager.loadQuests();
		configManager.loadNPC();

		for (Player p : Bukkit.getOnlinePlayers())
		{
			QuestPlayerData qd = new QuestPlayerData(p);
			if (QuestPlayerData.hasConfigData(p))
				qd = new QuestPlayerData(p, configManager.getPlayerIO());
			QuestStorage.Players.put(p.getName(), qd);
		}
	}

}
