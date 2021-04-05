package me.Cutiemango.MangoQuest.event;

import me.Cutiemango.MangoQuest.model.Quest;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class QuestTakeEvent extends Event
{
	private static final HandlerList handlers = new HandlerList();
	private final Player player;
	private final Quest quest;

	public QuestTakeEvent(Player who, Quest q)
	{
		player = who;
		quest = q;
	}

	public Player getPlayer()
	{
		return player;
	}

	public Quest getQuest()
	{
		return quest;
	}

	public HandlerList getHandlers()
	{
		return handlers;
	}

	public static HandlerList getHandlerList()
	{
		return handlers;
	}
}
