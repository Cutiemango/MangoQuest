package me.Cutiemango.MangoQuest.manager;

import java.util.HashMap;
import me.Cutiemango.MangoQuest.I18n;
import me.Cutiemango.MangoQuest.Main;
import me.Cutiemango.MangoQuest.objects.QuestNPC;
import net.citizensnpcs.api.npc.NPC;

public class QuestNPCManager
{
	private static HashMap<Integer, QuestNPC> npcStorage = new HashMap<>();
	
	public static void registerNPC(NPC npc)
	{
		npcStorage.put(npc.getId(), new QuestNPC(npc));
	}

	public static void registerNPC(int id)
	{
		if (!QuestValidater.validateNPC(Integer.toString(id)))
			return;
		npcStorage.put(id, new QuestNPC(Main.getHooker().getNPC(id)));
	}
	
	public static void registerNPC(NPC npc, QuestNPC npcdata)
	{
		npcStorage.put(npc.getId(), npcdata);
	}
	
	public static boolean hasData(int id)
	{
		return npcStorage.containsKey(id);
	}
	
	public static QuestNPC getNPCData(int id)
	{
		return npcStorage.get(id);
	}
	
	public static void unregister(NPC npc)
	{
		npcStorage.remove(npc.getId());
	}
	
	public static String getNPCMessage(int id, int fpoint)
	{
		if (!hasData(id))
			return I18n.locMsg("QuestJounery.DefaultNPCTalk");
		return npcStorage.get(id).getMessage(fpoint);
		
	}
	
	public static void clear()
	{
		npcStorage = new HashMap<>();
	}
}
