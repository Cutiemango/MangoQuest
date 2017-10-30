package me.Cutiemango.MangoQuest.editor;

import org.bukkit.entity.Player;
import me.Cutiemango.MangoQuest.QuestUtil;
import me.Cutiemango.MangoQuest.Syntax;

public class EditorListenerObject
{
	public EditorListenerObject(ListeningType ltype, String cmd, Syntax s)
	{
		type = ltype;
		command = cmd;
		syntax = s;
	}

	private Syntax syntax;
	private ListeningType type = ListeningType.STRING;
	private String command = "";

	public void execute(Player p, String obj)
	{
		if (type == ListeningType.OPEN_INVENTORY)
			return;
		if (syntax != null && !syntax.matches(p, obj))
		{
			EditorListenerHandler.unreigster(p);
			return;
		}
		QuestUtil.executeCommandAsync(p, command + " " + obj);
		EditorListenerHandler.CurrentListening.remove(p.getName());
		return;
	}

	public ListeningType getType()
	{
		return type;
	}

	public String getCommand()
	{
		return command;
	}

	public enum ListeningType
	{
		STRING,
		ENTITY_TYPE,

		LOCATION,
		NPC_LEFT_CLICK,
		MOB_LEFT_CLICK,
		MTMMOB_LEFT_CLICK,
		ITEM,
		OPEN_INVENTORY,
		BLOCK,

	}
}
