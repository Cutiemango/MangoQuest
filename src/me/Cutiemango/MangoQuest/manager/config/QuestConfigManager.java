package me.Cutiemango.MangoQuest.manager.config;

import me.Cutiemango.MangoQuest.DebugHandler;
import me.Cutiemango.MangoQuest.QuestIO;

public class QuestConfigManager
{
	protected QuestIO translateIO;
	protected QuestIO npcIO;
	protected QuestIO configIO;
	protected QuestIO globalQuest;
	protected QuestIO globalConv;
	
	private static QuestConfigLoader loader;
	private static QuestConfigSaver saver;

	public QuestConfigManager()
	{
		configIO = new QuestIO("config.yml", false, true, false);
		loader = new QuestConfigLoader(this);
		loader.loadConfig();
		saver = new QuestConfigSaver(this);
	}
	
	public void loadFile()
	{
		globalQuest = new QuestIO("quest", "globalquest.yml");
		globalConv = new QuestIO("conversation", "globalconv.yml");
		
		translateIO = new QuestIO("translations.yml", true, true, true);
		npcIO = new QuestIO("npc.yml", true, false, false);
		
		DebugHandler.log(1, "[Config] File Loaded.");
	}
	
	public static QuestConfigLoader getLoader()
	{
		return loader;
	}
	
	public static QuestConfigSaver getSaver()
	{
		return saver;
	}
	
	
}
