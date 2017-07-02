package me.Cutiemango.MangoQuest.editor;

import org.bukkit.entity.Player;

public class EditorListenerObject
{
	public EditorListenerObject(ListeningType ltype, String cmd)
	{
		type = ltype;
		command = cmd;
	}
	
	private ListeningType type = ListeningType.STRING;
	private String command = "";
	
	public void execute(Player p, String obj)
	{
		p.performCommand(command + " " + obj);
		EditorListenerHandler.CurrentListening.remove(p.getName());
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
