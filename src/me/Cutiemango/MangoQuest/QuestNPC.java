package me.Cutiemango.MangoQuest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import me.Cutiemango.MangoQuest.conversation.QuestConversation;

public class QuestNPC
{

	public QuestNPC()
	{
		msg.put(0, "...");
	}

	private HashMap<Integer, String> msg = new HashMap<>();
	private HashMap<Integer, QuestConversation> convs = new HashMap<>();

	public String getNPCMessage(int value)
	{
		int r = 0;
		if (value == 0)
			return msg.get(0);
		for (int j : msg.keySet())
		{
			if (value < 0 && j < 0 && j > value && r > j)
				r = j;
			else
				if (value > 0 && value >= j && j > r)
					r = j;
				else
					continue;
		}

		return msg.get(r);
	}

	public List<QuestConversation> getConversations(int value)
	{
		List<QuestConversation> list = new ArrayList<>();
		for (int i : convs.keySet())
		{
			if (value >= i)
				list.add(convs.get(i));
			else
				continue;
		}
		return list;
	}

	public void put(int i, String s)
	{
		msg.put(i, s);
	}

	public void put(int i, QuestConversation q)
	{
		convs.put(i, q);
	}
}
