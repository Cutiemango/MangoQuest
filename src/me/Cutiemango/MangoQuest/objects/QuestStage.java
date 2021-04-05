package me.Cutiemango.MangoQuest.objects;

import me.Cutiemango.MangoQuest.questobject.SimpleQuestObject;

import java.util.ArrayList;
import java.util.List;

public class QuestStage
{

	public QuestStage(List<SimpleQuestObject> obj)
	{
		objects = obj;
	}

	private List<SimpleQuestObject> objects;

	public List<SimpleQuestObject> getObjects()
	{
		return objects;
	}

	public SimpleQuestObject getObject(int index)
	{
		return objects.get(index);
	}

}
