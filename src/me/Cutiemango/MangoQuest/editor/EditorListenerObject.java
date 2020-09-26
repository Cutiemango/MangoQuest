package me.Cutiemango.MangoQuest.editor;

import me.Cutiemango.MangoQuest.QuestUtil;
import me.Cutiemango.MangoQuest.Syntax;
import org.bukkit.entity.Player;

public class EditorListenerObject
{
	public EditorListenerObject(ListeningType ltype, String cmd, Syntax s)
	{
		type = ltype;

		// Prevents "/" in the command.
		command = cmd.replaceFirst("/", "");
		syntax = s;
	}

	private Syntax syntax;
	private ListeningType type;
	// No "/" needed
	private String command;

	public void execute(Player p, String obj)
	{
		if (type == ListeningType.OPEN_INVENTORY)
			return;
		if (syntax != null && !syntax.matches(p, obj))
		{
			EditorListenerHandler.unreigster(p);
			return;
		}
		QuestUtil.executeSyncCommand(p, command + " " + obj);
		EditorListenerHandler.currentListening.remove(p.getName());
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

		LOCATION,
		NPC_LEFT_CLICK,
		MOB_LEFT_CLICK,
		MTMMOB_LEFT_CLICK,
		ITEM,
		OPEN_INVENTORY,
		BLOCK,

	}
}
