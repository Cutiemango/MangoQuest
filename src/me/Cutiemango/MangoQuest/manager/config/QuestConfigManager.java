package me.Cutiemango.MangoQuest.manager.config;

import me.Cutiemango.MangoQuest.DebugHandler;
import me.Cutiemango.MangoQuest.QuestIO;

public class QuestConfigManager
{
	private QuestIO translation;
	private QuestIO npcData;
	private QuestIO config;

	private QuestIO globalQuest;
	private QuestIO globalConv;

	private static QuestConfigLoader loader;
	private static QuestConfigSaver saver;

	public QuestConfigManager() {
		config = new QuestIO("config.yml", false, true, false);

		loader = new QuestConfigLoader(this);
		loader.loadConfig();
		saver = new QuestConfigSaver(this);

		loadFile();
	}

	public void loadFile() {
		globalQuest = new QuestIO("quest", "globalquest.yml");
		globalConv = new QuestIO("conversation", "globalconv.yml");

		translation = new QuestIO("translations.yml", true, true, true);
		npcData = new QuestIO("npc.yml", true, false, false);

		DebugHandler.log(1, "[Config] File Loaded.");
	}

	public static QuestConfigLoader getLoader() {
		return loader;
	}

	public static QuestConfigSaver getSaver() {
		return saver;
	}

	protected QuestIO getTranslation() {
		return translation;
	}

	protected QuestIO getNPC() {
		return npcData;
	}

	protected QuestIO getConfig() {
		return config;
	}

	protected QuestIO getGlobalQuest() {
		return globalQuest;
	}

	protected QuestIO getGlobalConversation() {
		return globalConv;
	}
}
