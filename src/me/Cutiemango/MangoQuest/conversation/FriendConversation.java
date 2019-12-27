package me.Cutiemango.MangoQuest.conversation;

import java.util.List;
import net.citizensnpcs.api.npc.NPC;

public class FriendConversation extends QuestConversation
{

	public FriendConversation(String s, String internal, NPC n, List<QuestBaseAction> list, int point)
	{
		super(s, internal, n, list);
		requireFriendPoint = point;
	}
	
	public FriendConversation(QuestConversation conv, int point)
	{
		super(conv.getName(), conv.getInternalID(), conv.getNPC(), conv.getActions());
		requireFriendPoint = point;
	}
	
	private int requireFriendPoint;
	
	public int getReqPoint()
	{
		return requireFriendPoint;
	}
	
	public void setReqPoint(int i)
	{
		requireFriendPoint = i;
	}
	
	@Override
	public FriendConversation clone()
	{
		return new FriendConversation(name, id, npc, action, requireFriendPoint);
	}
	
	public QuestConversation simpleClone()
	{
		return new QuestConversation(name, id, npc, action);
	}
	
}
