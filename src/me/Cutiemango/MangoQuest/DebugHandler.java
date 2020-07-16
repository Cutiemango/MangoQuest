package me.Cutiemango.MangoQuest;

import me.Cutiemango.MangoQuest.manager.QuestChatManager;

import java.util.logging.Level;

public class DebugHandler
{
	public static int DEBUG_LEVEL = 0;
	
	public static void log(int lvl, String s, Object... args)
	{
		if (DEBUG_LEVEL >= lvl)
			QuestChatManager.logCmd(Level.INFO, "[Debug] " + String.format(s, args));
	}
	
	public static void debug(String s)
	{
		QuestChatManager.logCmd(Level.INFO, "[Debug] " + s);
	}

}
