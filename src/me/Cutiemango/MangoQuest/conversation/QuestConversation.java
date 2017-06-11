package me.Cutiemango.MangoQuest.conversation;

import java.util.List;
import net.citizensnpcs.api.npc.NPC;

public class QuestConversation
{

	private List<QuestBaseAction> action;
	private String name;
	private String id;
	private NPC npc;

	private boolean isFriendConv;

	public QuestConversation(String s, String internal, NPC n, List<QuestBaseAction> list, boolean f)
	{
		name = s;
		action = list;
		id = internal;
		npc = n;
		isFriendConv = f;
	}


	public List<QuestBaseAction> getActions()
	{
		return action;
	}

	public String getName()
	{
		return name;
	}

	public String getInternalID()
	{
		return id;
	}

	public NPC getNPC()
	{
		return npc;
	}

	public boolean hasNPC()
	{
		return npc != null;
	}

	public boolean isFriendConv()
	{
		return isFriendConv;
	}

}
