package me.Cutiemango.MangoQuest.advancements;

import java.util.HashMap;
import org.bukkit.NamespacedKey;
import me.Cutiemango.MangoQuest.Main;

public class QuestAdvancementManager
{
	public static HashMap<NamespacedKey, QuestAdvancement> advancements = new HashMap<>();

	public static void addAdvancment(QuestAdvancement ad)
	{
		NamespacedKey key = ad.getKey();
		for (QuestAdvancement adAPI : advancements.values())
		{
			if (adAPI.getID().toString().equalsIgnoreCase(key.toString()))
				return;
		}
		advancements.put(ad.getKey(), ad);
	}

	public static QuestAdvancement getAdvancement(String key)
	{
		return advancements.get(new NamespacedKey(Main.getInstance(), "story:" + key));
	}

}
