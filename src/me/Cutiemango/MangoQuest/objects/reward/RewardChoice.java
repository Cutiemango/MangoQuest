package me.Cutiemango.MangoQuest.objects.reward;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import me.Cutiemango.MangoQuest.manager.reward.QuestRewardManager;

public class RewardChoice
{
	public RewardChoice(List<ItemStack> items)
	{
		rewards = items;
	}
	
	public List<ItemStack> rewards = new ArrayList<>(QuestRewardManager.MAXMIUM_CHOICES);
	
	public void executeReward(Player p)
	{
		for (ItemStack item : rewards)
		{
			QuestRewardManager.giveRewardItem(p, item);
		}
	}
	
	public List<ItemStack> getItems()
	{
		return rewards;
	}
	
	public void addItem(ItemStack item)
	{
		rewards.add(item);
	}
}
