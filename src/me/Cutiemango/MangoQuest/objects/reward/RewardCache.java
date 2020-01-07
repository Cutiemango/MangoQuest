package me.Cutiemango.MangoQuest.objects.reward;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import me.Cutiemango.MangoQuest.DebugHandler;
import me.Cutiemango.MangoQuest.I18n;
import me.Cutiemango.MangoQuest.Main;
import me.Cutiemango.MangoQuest.QuestUtil;
import me.Cutiemango.MangoQuest.manager.QuestChatManager;
import me.Cutiemango.MangoQuest.manager.QuestRewardManager;
import me.Cutiemango.MangoQuest.model.Quest;
import net.md_5.bungee.api.ChatColor;

public class RewardCache
{
	public RewardCache(Player p, Quest q)
	{
		owner = p;
		quest = q;
		reward = q.getQuestReward();
	}
	
	private Set<Integer> choice = new HashSet<>();
	private Quest quest;
	private QuestReward reward;
	private Player owner;
	
	public void addChoice(Integer i)
	{
		if (choice.size() + 1 > reward.getRewardAmount())
			return;
		choice.add(i);
		if (choice.size() == reward.getRewardAmount())
		{
			finish();
			return;
		}
		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				openGUI();
			}
			
		}.runTaskLater(Main.getInstance(), 5L);
		DebugHandler.log(4, "[Listener] Player " + owner.getName() + " added reward choice of " + i + " in quest " + quest.getQuestName());
	}
	
	public void removeChoice(Integer i)
	{
		choice.remove(i);
		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				openGUI();
			}
			
		}.runTaskLater(Main.getInstance(), 5L);
		DebugHandler.log(4, "[Listener] Player " + owner.getName() + " removed reward choice of " + i + " in quest " + quest.getQuestName());
	}
	
	public Player getOwner()
	{
		return owner;
	}
	
	public void finish()
	{
		for (int i : choice)
		{
			reward.getChoice(i).executeReward(owner);
		}
		QuestChatManager.info(owner, I18n.locMsg("QuestReward.RewardReceived"));
		QuestUtil.getData(owner).claimReward(quest);
		QuestRewardManager.removeCache(owner);
		DebugHandler.log(3, "[Listener] Player " + owner.getName() + " has claimed reward of quest " + quest.getQuestName());
	}
	
	public void openGUI()
	{
		Inventory inv = Bukkit.createInventory(null, 27, I18n.locMsg("QuestReward.RewardTitle"));
		
		for (int i = 0; i < reward.getChoiceAmount(); i++)
		{
			ItemStack item = itemButton(reward.getChoice(i), i);
			if (choice.contains(i))
				item = selectedItem(item);
			inv.setItem(QuestRewardManager.getRewardSlot(reward.getChoiceAmount(), i), item);
			
		}

		for (int i = 0; i < 27; i++)
		{
			if (inv.getItem(i) == null || inv.getItem(i).getType().equals(Material.AIR))
				inv.setItem(i, glassPane(reward.getRewardAmount()));
		}
		owner.openInventory(inv);
		DebugHandler.log(3, "[Listener] Opened reward inventory of quest " + quest.getQuestName() + " for " + owner.getName());
	}

	private ItemStack glassPane(int amount)
	{
		QuestGUIItem glassPane = new QuestGUIItem(Material.LIGHT_GRAY_STAINED_GLASS_PANE, 1);
		glassPane.setName(I18n.locMsg("QuestReward.RewardGlassPane", Integer.toString(amount)));
		return glassPane.get();
	}
	
	private ItemStack itemButton(RewardChoice rc, int index)
	{
		ItemStack firstItem = rc.getItems().get(0);
		QuestGUIItem button = new QuestGUIItem(firstItem.getType(), firstItem.getAmount());
		List<String> lore = new ArrayList<>();
		for (ItemStack item : rc.getItems())
		{
			lore.add(QuestChatManager.translateColor("&f- " + QuestRewardManager.getItemName(item)));
		}
		lore.add(ChatColor.BLACK + "" + index);
		button.setName("&f" + QuestRewardManager.getItemName(firstItem));
		button.setLore(lore);
		return button.get();
	}
	
	private ItemStack selectedItem(ItemStack is)
	{
		QuestGUIItem guiItem = new QuestGUIItem(is);
		guiItem.glowEffect();
		return guiItem.get();
	}
}
