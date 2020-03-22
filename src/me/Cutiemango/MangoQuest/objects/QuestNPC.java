package me.Cutiemango.MangoQuest.objects;

import me.Cutiemango.MangoQuest.I18n;
import me.Cutiemango.MangoQuest.model.Quest;
import net.citizensnpcs.api.npc.NPC;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Random;

public class QuestNPC
{
	public QuestNPC(NPC npc)
	{
		npcReal = npc;
		HashSet<String> defSet = new HashSet<>();
		defSet.add(I18n.locMsg("QuestJourney.DefaultNPCTalk"));
		friendMessageMap.put(0, defSet);
	}
	
	private NPC npcReal;
	private HashMap<Integer, HashSet<String>> friendMessageMap = new HashMap<>();
	private HashSet<Quest> givenQuests = new LinkedHashSet<>();
	private HashSet<Quest> rewardQuests = new HashSet<>();
	private HashSet<GUIOption> options = new HashSet<>();
	
	/**
	 * This method is used to get npc's message according to given player's friend point.
	 * @return Paramater friend point's according message
	 */
	public String getMessage(int fpoint)
	{
		Random random = new Random();
		int r = 0;
		HashSet<String> set;
		if (fpoint == 0)
			set = friendMessageMap.get(0);
		else
		{
			for (int j : friendMessageMap.keySet())
			{
				if (j < 0 && j > fpoint && r > j)
					r = j;
				else
					if (fpoint > 0 && fpoint >= j && j > r)
						r = j;
			}
			set = friendMessageMap.get(r);
		}
		return set.toArray(new String[set.size()])[random.nextInt(set.size())];
	}
	
	public void registerQuest(Quest q)
	{
		givenQuests.add(q);
	}
	
	public void registerReward(Quest q)
	{
		rewardQuests.add(q);
	}
	
	public void putMessage(int fpoint, HashSet<String> msg)
	{
		friendMessageMap.put(fpoint, msg);
	}
	
	public HashSet<Quest> getGivenQuests()
	{
		return givenQuests;
	}
	
	public NPC getNPC()
	{
		return npcReal;
	}

	public HashSet<Quest> getRewardQuests()
	{
		return rewardQuests;
	}

	public HashSet<GUIOption> getOptions()
	{
		return options;
	}

	public void setOptions(HashSet<GUIOption> options)
	{
		this.options = options;
	}
}
