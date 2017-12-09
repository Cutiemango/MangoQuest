package me.Cutiemango.MangoQuest;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import me.Cutiemango.MangoQuest.book.FlexiableBook;
import me.Cutiemango.MangoQuest.book.QuestBookPage;
import me.Cutiemango.MangoQuest.conversation.FriendConversation;
import me.Cutiemango.MangoQuest.data.QuestPlayerData;
import me.Cutiemango.MangoQuest.manager.QuestChatManager;
import me.Cutiemango.MangoQuest.model.Quest;
import net.citizensnpcs.api.npc.NPC;

public class QuestUtil
{

	public static void sendTitle(Player p, Integer fadeIn, Integer stay, Integer fadeOut, String title, String subtitle)
	{
		Main.instance.handler.sendTitle(p, fadeIn, stay, fadeOut, title, subtitle);
	}
	
	public static int randomInteger(int min, int max)
	{
		return new Random().nextInt(max - min + 1) + min;
	}

	public static <T> List<T> convert(Set<T> set)
	{
		return new ArrayList<T>(set);
	}

	public static QuestPlayerData getData(Player p)
	{
		return QuestStorage.Players.get(p.getName());
	}

	public static final double cut(double d)
	{
		return Math.floor((d * 100)) / 100;
	}

	public static void executeCommandAsync(Player p, String command)
	{
		if (!Bukkit.isPrimaryThread())
			Bukkit.getScheduler().runTask(Main.instance, new Runnable()
			{
				@Override
				public void run()
				{
					p.performCommand(command);
				}
			});
		else
			p.performCommand(command);
	}

	public static String convertArgsString(String[] array, int startIndex)
	{
		String s = "";
		for (int i = startIndex; i < array.length; i++)
		{
			s = s + array[i];
			if (i + 1 == array.length)
				break;
			else
				s += " ";
		}
		return s;
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
		{
			s += days + " " + I18n.locMsg("TimeFormat.Day");
			if (hours > 0 || minutes > 0 || seconds > 0)
				s += ", ";
		}
		if (hours > 0)
		{
			s += hours + " " + I18n.locMsg("TimeFormat.Hour");
			if (minutes > 0 || seconds > 0)
				s += ", ";
		}
		if (minutes > 0)
		{
			s += minutes + " " + I18n.locMsg("TimeFormat.Minute");
			if (seconds > 0)
				s += ", ";
		}
		if (seconds > 0)
			s += seconds + " " + I18n.locMsg("TimeFormat.Second");
		return s;
	}

	public static String getItemName(ItemStack is)
	{
		if (is.hasItemMeta() && is.getItemMeta().hasDisplayName())
			return is.getItemMeta().getDisplayName();
		else
			return translate(is.getType(), is.getDurability());
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
		else
			return QuestStorage.TranslateMap.get(mat).get(data);
	}
	
	public static String translate(ItemStack item)
	{
		if (item == null)
			return "未知的物品";
		if (item.hasItemMeta() && item.getItemMeta().hasDisplayName())
			return QuestChatManager.trimColor(item.getItemMeta().getDisplayName());
		else
			return translate(item.getType(), item.getDurability());	
	}

	public static String translate(EntityType e)
	{
		if (!QuestStorage.EntityTypeMap.containsKey(e))
			return "未知的生物";
		else
			return QuestStorage.EntityTypeMap.get(e);
	}
}
