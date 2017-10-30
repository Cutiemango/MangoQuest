package me.Cutiemango.MangoQuest.objects;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
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
	
	private Set<Integer> choice = new HashSet<Integer>();
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
			
		}.runTaskLater(Main.instance, 5L);
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
			
		}.runTaskLater(Main.instance, 5L);
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
		QuestUtil.getData(owner).rewardClaimed(quest);
		QuestRewardManager.removeCache(owner);
	}
	
	public void openGUI()
	{
		Inventory inv = Bukkit.createInventory(null, 27, I18n.locMsg("QuestReward.RewardTitle"));
		for (int i = 0; i < 27; i++)
		{
			if (inv.getItem(i) == null || inv.getItem(i).getType().equals(Material.AIR))
				inv.setItem(i, glassPane(reward.getRewardAmount()));
		}
		
		for (int i = 0; i < reward.getChoiceAmount(); i++)
		{
			ItemStack item = itemButton(reward.getChoice(i), i);
			if (choice.contains(i))
				item = selectedItem(item);
			inv.setItem(9 + getSlot(reward.getChoiceAmount(), i), item);
			
		}

		for (int i = 0; i < 27; i++)
		{
			if (inv.getItem(i) == null || inv.getItem(i).getType().equals(Material.AIR))
				inv.setItem(i, glassPane(reward.getRewardAmount()));
		}
		owner.openInventory(inv);
	}
	
	private ItemStack glassPane(int amount)
	{
		ItemStack item = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte)QuestUtil.randomInteger(0, 15));
		ItemMeta im = item.getItemMeta();
		im.setDisplayName(I18n.locMsg("QuestReward.RewardGlassPane", Integer.toString(amount)));
		item.setItemMeta(im);
		return QuestUtil.addUnstealableTag(item);
	}
	
	private ItemStack itemButton(RewardChoice rc, int index)
	{
		ItemStack firstItem = rc.getItems().get(0);
		ItemStack target = new ItemStack(firstItem.getType(), firstItem.getAmount(), (short)firstItem.getDurability());
		ItemMeta im = target.getItemMeta();
		List<String> lore = new ArrayList<>();
		im.setDisplayName(QuestChatManager.translateColor("&f" + getItemName(firstItem)));
		for (ItemStack item : rc.getItems())
		{
			lore.add(QuestChatManager.translateColor("&f- " + getItemName(item)));
		}
		lore.add(ChatColor.BLACK + "" + index);
		im.setLore(lore);
		target.setItemMeta(im);
		return target;
	}
	
	private String getItemName(ItemStack item)
	{
		String name = "";
		if (item.hasItemMeta() && item.getItemMeta().hasDisplayName())
			name = QuestChatManager.translateColor(item.getItemMeta().getDisplayName() + " &f" + ChatColor.stripColor(I18n.locMsg("QuestEditor.Amount", Integer.toString(item.getAmount()))));
		else
			name = QuestChatManager.translateColor(QuestUtil.translate(item.getType(), item.getDurability()) + " &f" + ChatColor.stripColor(I18n.locMsg("QuestEditor.Amount", Integer.toString(item.getAmount()))));
		return name;
	}
	
	private ItemStack selectedItem(ItemStack is)
	{
		ItemStack item = is.clone();
		ItemMeta im = item.getItemMeta();
		im.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS);
		item.setItemMeta(im);
		item.addUnsafeEnchantment(Enchantment.LURE, 9);
		return item;
	}
	
	private int getSlot(int amount, int x)
	{
		if (amount % 2 == 0)
		{
			int[] arySlot = new int[]
			{ 3, 5, 1, 7, 2, 6, 0, 8 };
			return arySlot[x];
		}
		else
		{
			int[] arySlot = new int[]
			{ 4, 2, 6, 3, 5, 1, 7, 0, 8 };
			return arySlot[x];
		}
	}
}
