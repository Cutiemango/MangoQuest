package me.Cutiemango.MangoQuest;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import me.Cutiemango.MangoQuest.book.FlexiableBook;
import me.Cutiemango.MangoQuest.book.QuestBookPage;
import me.Cutiemango.MangoQuest.conversation.FriendConversation;
import me.Cutiemango.MangoQuest.data.QuestPlayerData;
import me.Cutiemango.MangoQuest.model.Quest;
import me.Cutiemango.MangoQuest.model.QuestNPCData;
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

	public static QuestPlayerData getData(Player p)
	{
		return QuestStorage.Players.get(p.getName());
	}

	public static String getNPCMessage(int id, int fp)
	{
		if (!QuestStorage.NPCMap.containsKey(id))
			QuestStorage.NPCMap.put(id, new QuestNPCData());
		return QuestStorage.NPCMap.get(id).getNPCMessage(fp);
	}

	public static Set<FriendConversation> getConversations(NPC npc, int fp)
	{
		Set<FriendConversation> set = new HashSet<>();
		for (FriendConversation conv : QuestStorage.FriendConvs)
		{
			if (conv.getNPC().equals(npc) && (fp >= conv.getReqPoint()))
				set.add(conv);
		}
		return set;
	}

	public static Quest getQuest(String s)
	{
		return QuestStorage.Quests.get(s);
	}
	
	public static void checkOutOfBounds(QuestBookPage page, FlexiableBook book)
	{
		if (page.pageOutOfBounds())
		{
			book.newPage();
			book.getLastEditingPage().add(book.getPage(book.size() - 2).getTextleft());
		}
		return;
	}

	public static String convertTime(long l)
	{
		String s = "";

		long days = l / 86400000;
		long hours = (l % 86400000) / 3600000;
		long minutes = ((l % 86400000) % 3600000) / 60000;
		long seconds = (((l % 86400000) % 3600000) % 60000) / 1000;
		
		if (l == 0)
		{
			s = I18n.locMsg("TimeFormat.NoCooldown");
			return s;
		}
		
		if (days > 0)
			s += days + " " + I18n.locMsg("TimeFormat.Day") + ",";
		if (hours > 0)
			s += hours + " " + I18n.locMsg("TimeFormat.Hour") + ",";
		if (minutes > 0)
			s += minutes + " " + I18n.locMsg("TimeFormat.Minute") + ",";
		if (seconds > 0)
			s += seconds + " " + I18n.locMsg("TimeFormat.Second");
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
		if (QuestStorage.TranslateMap.get(mat).get(data) == null)
			return QuestStorage.TranslateMap.get(mat).get(0);
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
