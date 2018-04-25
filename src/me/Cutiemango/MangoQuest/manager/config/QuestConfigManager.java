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
		ConfigIO = new QuestIO("config.yml", false, true, false);
		loader = new QuestConfigLoader(this);
		loader.loadConfig();
		saver = new QuestConfigSaver(this);
	}
	
	public void loadFile()
	{
		QuestsIO = new QuestIO("quests.yml", true, true, false);
		TranslateIO = new QuestIO("translations.yml", true, true, true);
		NPCIO = new QuestIO("npc.yml", true, false, false);
		ConversationIO = new QuestIO("conversations.yml", false, false, false);
		AdvancementIO = new QuestIO("advancements.yml", false, true, false);
		
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
