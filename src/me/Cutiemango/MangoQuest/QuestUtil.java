package me.Cutiemango.MangoQuest;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import me.Cutiemango.MangoQuest.conversation.QuestConversation;
import me.Cutiemango.MangoQuest.data.QuestPlayerData;
import me.Cutiemango.MangoQuest.manager.QuestChatManager;
import me.Cutiemango.MangoQuest.model.Quest;
import me.Cutiemango.MangoQuest.model.QuestNPC;
import net.citizensnpcs.api.npc.NPC;

public class QuestUtil
{

	public static void sendTitle(Player p, Integer fadeIn, Integer stay, Integer fadeOut, String title, String subtitle)
	{
		Main.instance.handler.sendTitle(p, fadeIn, stay, fadeOut, title, subtitle);
	}

	public static List<Quest> getGivenNPCQuests(NPC npc)
	{
		List<Quest> l = new ArrayList<>();
		for (Quest q : QuestStorage.Quests.values())
		{
			if (q.isCommandQuest())
				continue;
			else
				if (q.getQuestNPC().equals(npc))
					l.add(q);
				else
					continue;
		}
		return l;
	}

	public static <T> List<T> convert(Set<T> set)
	{
		return new ArrayList<T>(set);
	}

	public enum QuestTitleEnum
	{
		ACCEPT, FINISH, QUIT;
	}

	public static void sendQuestTitle(Player target, Quest quest, QuestTitleEnum e)
	{
		switch (e)
		{
			case ACCEPT:
				sendTitle(target, 1, 3, 1, "&b&l接受任務", quest.getQuestName());
				break;
			case FINISH:
				sendTitle(target, 1, 3, 1, "&6&l完成任務", quest.getQuestName());
				break;
			case QUIT:
				sendTitle(target, 1, 3, 1, "&c&l放棄任務", quest.getQuestName());
				break;
		}
	}

	public static QuestPlayerData getData(Player p)
	{
		return QuestStorage.Players.get(p.getName());
	}

	public static String getNPCMessage(int id, int fp)
	{
		if (!QuestStorage.NPCMap.containsKey(id))
			QuestStorage.NPCMap.put(id, new QuestNPC());
		return QuestStorage.NPCMap.get(id).getNPCMessage(fp);
	}

	public static List<QuestConversation> getConversations(int id, int fp)
	{
		if (!QuestStorage.NPCMap.containsKey(id))
			QuestStorage.NPCMap.put(id, new QuestNPC());
		return QuestStorage.NPCMap.get(id).getConversations(fp);
	}

	public static Quest getQuest(String s)
	{
		return QuestStorage.Quests.get(s);
	}
	
	public static void clearData(Player p){
		QuestStorage.Players.put(p.getName(), new QuestPlayerData(p));
		Main.instance.configManager.clearPlayerData(p);
		QuestChatManager.info(p, Questi18n.localizeMessage("CommandInfo.PlayerDataRemoved"));
	}

	public static String convertTime(long l)
	{
		String s = "";

		long days = l / 86400000;
		long hours = (l % 86400000) / 3600000;
		long minutes = ((l % 86400000) % 3600000) / 60000;
		long seconds = (((l % 86400000) % 3600000) % 60000) / 1000;

		if (days > 0)
			s += days + " 天,";
		if (hours > 0)
			s += hours + " 小時,";
		if (minutes > 0)
			s += minutes + " 分鐘,";
		if (seconds > 0)
			s += seconds + " 秒";
		return s;
	}

	public static boolean compareItem(ItemStack one, ItemStack two, boolean ignoreAmount)
	{
		if (one == null && two != null || one != null && two == null)
			return false;
		if (one == null && two == null)
			return true;
		if (one.getType().name() != two.getType().name())
			return false;
		else
			if ((one.getAmount() != two.getAmount()) && ignoreAmount == false)
				return false;
			else
				if (one.getData().equals(two.getData()) == false)
					return false;
		if (one.hasItemMeta() || two.hasItemMeta())
			if (one.hasItemMeta() != two.hasItemMeta())
				return false;
			else
				if (one.getItemMeta().hasDisplayName() != two.getItemMeta().hasDisplayName())
					return false;
				else
					if (one.getItemMeta().hasLore() != two.getItemMeta().hasLore())
						return false;
					else
					{
						if (one.getItemMeta().hasDisplayName() && two.getItemMeta().hasDisplayName())
						{
							if (!one.getItemMeta().getDisplayName().equals(two.getItemMeta().getDisplayName()))
									return false;
						}
						else if (one.getItemMeta().hasLore() && two.getItemMeta().hasLore())
						{
							if (!one.getItemMeta().getLore().equals(two.getItemMeta().getLore()))
								return false;
						}
					}
		if (one.getEnchantments().equals(two.getEnchantments()) == false)
			return false;
		else
			return true;
	}

	@SafeVarargs
	public static <T> List<T> createList(T... args)
	{
		List<T> list = new ArrayList<T>();
		for (T obj : args)
		{
			list.add(obj);
		}
		return list;
	}

	public static String translate(Material mat, short data)
	{
		if (!QuestStorage.TranslateMap.containsKey(mat))
			return "未知的物品";
		return QuestStorage.TranslateMap.get(mat).get(data);
	}

	public static String translate(EntityType e)
	{
		if (!QuestStorage.EntityTypeMap.containsKey(e))
			return "未知的生物";
		else
			return QuestStorage.EntityTypeMap.get(e);
	}
}
