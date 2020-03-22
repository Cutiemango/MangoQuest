package me.Cutiemango.MangoQuest.manager;

import me.Cutiemango.MangoQuest.I18n;
import me.Cutiemango.MangoQuest.questobject.CustomQuestObject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

public class CustomObjectManager
{
	private static Set<Class<? extends CustomQuestObject>> objectList = new HashSet<>();
	private static HashMap<String, Class<? extends CustomQuestObject>> loadedObjects = new HashMap<>();
	
	public static void registerObject(Class<? extends CustomQuestObject> obj)
	{
		if (obj == null)
			return;
		objectList.add(obj);
	}
	
	public static void loadCustomObjects()
	{
		for (Class<? extends CustomQuestObject> obj : objectList)
		{
			loadedObjects.put(obj.getName(), obj);
			QuestChatManager.logCmd(Level.INFO, I18n.locMsg("CustomObject.ObjectRegistered", obj.getName()));
		}
	}

	public static CustomQuestObject getSpecificObject(String className)
	{
		try
		{
			return loadedObjects.get(className).newInstance();
			
		}
		catch (Exception e)
		{
			QuestChatManager.logCmd(Level.SEVERE, I18n.locMsg("CustomObject.ObjectNotFound", className));
			e.printStackTrace();
			return null;
		}
	}
	
	public static boolean exist(String className)
	{
		return loadedObjects.containsKey(className);
	}
}
