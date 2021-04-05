package me.Cutiemango.MangoQuest.event;

import me.Cutiemango.MangoQuest.data.QuestPlayerData;
import me.Cutiemango.MangoQuest.model.Quest;
import me.Cutiemango.MangoQuest.questobject.SimpleQuestObject;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class QuestObjectProgressEvent extends Event
{
	private static final HandlerList handlers = new HandlerList();
	private final QuestPlayerData data;
	private final Quest quest;
	private final SimpleQuestObject questObject;
	
	public QuestObjectProgressEvent(QuestPlayerData which, Quest q, SimpleQuestObject obj)
	{
		data = which;
		quest = q;
		questObject = obj;
	}
	
	public QuestPlayerData getPlayerData()
	{
		return data;
	}

	public SimpleQuestObject getQuestObject()
	{
		return questObject;
	}

	public Quest getQuest()
	{
		return quest;
	}
	
	public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
