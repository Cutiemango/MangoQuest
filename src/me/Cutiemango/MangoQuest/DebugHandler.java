package me.Cutiemango.MangoQuest;

import java.util.logging.Level;
import me.Cutiemango.MangoQuest.manager.QuestChatManager;

public class DebugHandler
{
	public static int DEBUG_LEVEL = 0;
	
	public static void log(int lvl, String s)
	{
		if (DEBUG_LEVEL >= lvl)
			QuestChatManager.logCmd(Level.INFO, "[Debug] " + s);
	}
	
	public static void debug(String s)
	{
		QuestChatManager.logCmd(Level.INFO, "[Debug] " + s);
	}

}
