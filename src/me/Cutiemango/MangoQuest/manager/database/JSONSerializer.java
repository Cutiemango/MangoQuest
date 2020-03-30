package me.Cutiemango.MangoQuest.manager.database;

import me.Cutiemango.MangoQuest.data.QuestObjectProgress;
import me.Cutiemango.MangoQuest.manager.QuestChatManager;
import me.Cutiemango.MangoQuest.questobject.SimpleQuestObject;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class JSONSerializer
{
	public static String jsonSerialize(List<QuestObjectProgress> lst)
	{
		JSONObject json = new JSONObject();
		for (int i = 0; i < lst.size(); i++)
			json.put(Integer.toString(i), lst.get(i).getProgress());
		return json.toJSONString();
	}

	public static List<QuestObjectProgress> jsonDeserialize(List<SimpleQuestObject> objs, String obj)
	{
		List<QuestObjectProgress> prog = new ArrayList<>();
		JSONParser parser = new JSONParser();
		try
		{
			JSONObject json = (JSONObject) parser.parse(obj);
			for (int i = 0; i < json.keySet().size(); i++)
				prog.add(new QuestObjectProgress(objs.get(0), (Integer) json.get(Integer.toString(i))));
		}
		catch (ParseException e)
		{
			QuestChatManager.logCmd(Level.WARNING, "An error occured whilest decoding json.");
		}
		return prog;
	}
}
