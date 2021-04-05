package me.Cutiemango.MangoQuest.manager;

import me.Cutiemango.MangoQuest.I18n;
import me.Cutiemango.MangoQuest.Main;
import me.Cutiemango.MangoQuest.data.QuestPlayerData;
import me.Cutiemango.MangoQuest.model.Quest;
import me.Cutiemango.MangoQuest.objects.GUIOption;
import me.Cutiemango.MangoQuest.objects.QuestNPC;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.entity.Entity;

import java.util.HashMap;

public class QuestNPCManager
{
	private static final HashMap<Integer, QuestNPC> npcStorage = new HashMap<>();
	private static final HashMap<String, GUIOption> optionStorage = new HashMap<>();

	public static GUIOption getOption(String s) {
		return optionStorage.get(s);
	}

	public static void registerOption(String s, GUIOption option) {
		optionStorage.put(s, option);
	}

	public static void effectTask(QuestPlayerData pd) {
		for (Entity e : pd.getPlayer().getNearbyEntities(16d, 4d, 16d)) {
			if (CitizensAPI.getNPCRegistry().isNPC(e)) {
				NPC npc = CitizensAPI.getNPCRegistry().getNPC(e);
				if (!hasData(npc.getId()))
					continue;
				for (Quest q : getNPCData(npc.getId()).getGivenQuests()) {
					if (!q.isRedoable() && pd.hasFinished(q))
						continue;
					if (pd.hasFinished(q) && !pd.hasTakenReward(q) && q.getQuestReward().getRewardNPC().getId() == npc.getId())
						Main.getInstance().handler.playNPCEffect(pd.getPlayer(), e.getLocation());
					else if (pd.canTake(q, false) || !pd.getNPCtoTalkWith(npc).isEmpty())
						Main.getInstance().handler.playNPCEffect(pd.getPlayer(), e.getLocation());

				}
			}
		}
	}

	public static void registerNPC(NPC npc) {
		if (npcStorage.containsKey(npc.getId()))
			return;
		npcStorage.put(npc.getId(), new QuestNPC(npc));
	}

	public static void registerNPC(int id) {
		if (!QuestValidater.validateNPC(Integer.toString(id)) || npcStorage.containsKey(id))
			return;
		npcStorage.put(id, new QuestNPC(Main.getHooker().getNPC(id)));
	}

	public static void updateNPC(NPC npc, QuestNPC npcdata) {
		npcStorage.put(npc.getId(), npcdata);
	}

	public static boolean hasData(int id) {
		return npcStorage.containsKey(id);
	}

	public static QuestNPC getNPCData(int id) {
		return npcStorage.get(id);
	}

	public static void unregister(NPC npc) {
		npcStorage.remove(npc.getId());
	}

	public static String getNPCMessage(int id, int fpoint) {
		if (!hasData(id) || npcStorage.get(id) == null)
			return I18n.locMsg("QuestJourney.DefaultNPCTalk");
		return npcStorage.get(id).getMessage(fpoint);

	}

	public static void clear() {
		npcStorage.clear();
		optionStorage.clear();
	}
}
