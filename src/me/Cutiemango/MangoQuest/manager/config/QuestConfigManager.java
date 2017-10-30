package me.Cutiemango.MangoQuest.manager.config;

import me.Cutiemango.MangoQuest.QuestIO;

public class QuestConfigManager
{
	protected QuestIO QuestsIO;
	protected QuestIO TranslateIO;
	protected QuestIO NPCIO;
	protected QuestIO ConfigIO;

	protected QuestIO ConversationIO;
	protected QuestIO AdvancementIO;
	
	private static QuestConfigLoader loader;
	private static QuestConfigSaver saver;

	public QuestConfigManager()
	{
		ConfigIO = new QuestIO("config.yml", false, true);
		loader = new QuestConfigLoader(this);
		loader.loadConfig();
		saver = new QuestConfigSaver(this);
	}
	
	public void loadFile()
	{
		QuestsIO = new QuestIO("quests.yml", true, true);
		TranslateIO = new QuestIO("translations.yml", true, true);
		NPCIO = new QuestIO("npc.yml", true, false);
		ConversationIO = new QuestIO("conversations.yml", false, false);
		AdvancementIO = new QuestIO("advancements.yml", false, true);
		
		loader.init();
		saver.init();
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
