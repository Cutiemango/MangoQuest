package me.Cutiemango.MangoQuest.model;

import java.util.ArrayList;
import java.util.List;
import me.Cutiemango.MangoQuest.questobjects.SimpleQuestObject;

public class QuestStage
{

	public QuestStage(List<SimpleQuestObject> obj)
	{
		AllObjects = obj;
	}

	private List<SimpleQuestObject> AllObjects = new ArrayList<>();

	public List<SimpleQuestObject> getObjects()
	{
		return AllObjects;
	}

	public SimpleQuestObject getObject(int index)
	{
		return AllObjects.get(index);
	}

}
