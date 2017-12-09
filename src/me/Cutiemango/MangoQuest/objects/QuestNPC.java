package me.Cutiemango.MangoQuest.objects;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import me.Cutiemango.MangoQuest.model.Quest;
import net.citizensnpcs.api.npc.NPC;

public class QuestNPC
{
	public QuestNPC(NPC npc)
	{
		npcReal = npc;
	}
	
	private NPC npcReal;
	private HashMap<Integer, Set<String>> friendMessageMap = new HashMap<>();
	private Set<Quest> givenQuests;
	
	/**
	 * This method is used to get npc's message according to given player's friend point.
	 * @return Paramater friend point's according message
	 */
	public String getMessage(int fpoint)
	{
		Random random = new Random();
		int r = 0;
		Set<String> set = new HashSet<>();
		if (fpoint == 0)
			set = friendMessageMap.get(0);
		else
		{
			for (int j : friendMessageMap.keySet())
			{
				if (fpoint < 0 && j < 0 && j > fpoint && r > j)
					r = j;
				else
					if (fpoint > 0 && fpoint >= j && j > r)
						r = j;
					else
						continue;
			}
			set = friendMessageMap.get(r);
		}
		return set.toArray(new String[set.size()])[random.nextInt(set.size())];
	}
	
	public void registerQuest(Quest q)
	{
		givenQuests.add(q);
	}
	
	public void putMessage(int fpoint, Set<String> msg)
	{
		friendMessageMap.put(fpoint, msg);
	}
	
	public Set<Quest> getGivenQuests()
	{
		return givenQuests;
	}
	
	public NPC getNPC()
	{
		return npcReal;
	}
}
