package me.Cutiemango.MangoQuest.objects;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class QuestNPCData
{

	private HashMap<Integer, Set<String>> msg = new HashMap<>();
	
	public QuestNPCData()
	{
		Set<String> set = new HashSet<>();
		set.add("...");
		msg.put(0, set);
	}
	
	public String getNPCMessage(int value)
	{
		Random random = new Random();
		int r = 0;
		Set<String> set = new HashSet<>();
		if (value == 0)
			set = msg.get(0);
		else
		{
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
			set = msg.get(r);
		}
		return set.toArray(new String[set.size()])[random.nextInt(set.size())];
	}


	public void putMessage(int i, Set<String> s)
	{
		msg.put(i, s);
	}
}
