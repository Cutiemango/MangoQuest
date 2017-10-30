package me.Cutiemango.MangoQuest.manager;

import java.util.HashMap;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import me.Cutiemango.MangoQuest.I18n;
import me.Cutiemango.MangoQuest.QuestUtil;
import me.Cutiemango.MangoQuest.model.Quest;
import me.Cutiemango.MangoQuest.objects.QuestReward;
import me.Cutiemango.MangoQuest.objects.RewardCache;

public class QuestRewardManager implements Listener
{
	
	public static final int MAXMIUM_CHOICES = 9;
	private static HashMap<String, RewardCache> rewardCache = new HashMap<>();
	
	public static void generateEditRewardGUI(Player p, QuestReward reward)
	{
		
	}
	
	public static void giveRewardItem(Player p, ItemStack is)
	{
		if (p.getInventory().firstEmpty() == -1)
		{
			QuestChatManager.info(p, I18n.locMsg("QuestReward.RewardDropped"));
			p.getWorld().dropItem(p.getLocation(), is);
		}
		else
		{
			p.getInventory().addItem(is);
			if (is.hasItemMeta() && is.getItemMeta().hasDisplayName())
				QuestChatManager.info(p, I18n.locMsg("QuestReward.GiveItemReward", is.getItemMeta().getDisplayName(),
						Integer.toString(is.getAmount())));
			else
				QuestChatManager.info(p, I18n.locMsg("QuestReward.GiveItemReward",
						QuestUtil.translate(is.getType(), is.getDurability()), Integer.toString(is.getAmount())));
		}
		return;
	}
	
	public static RewardCache getRewardCache(Player p)
	{
		return rewardCache.get(p.getName());
	}
	
	public static boolean hasRewardCache(Player p)
	{
		return rewardCache.containsKey(p.getName());
	}
	
	public static void removeCache(Player p)
	{
		rewardCache.remove(p.getName());
	}
	
	public static void registerCache(Player p, Quest q)
	{
		RewardCache cache = new RewardCache(p, q);
		rewardCache.put(p.getName(), cache);
		cache.openGUI();
	}
}
