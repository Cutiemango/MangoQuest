package me.Cutiemango.MangoQuest.listeners;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import me.Cutiemango.MangoQuest.I18n;
import me.Cutiemango.MangoQuest.Main;
import me.Cutiemango.MangoQuest.QuestUtil;
import me.Cutiemango.MangoQuest.Syntax;
import me.Cutiemango.MangoQuest.editor.EditorListenerHandler;
import me.Cutiemango.MangoQuest.editor.EditorListenerObject;
import me.Cutiemango.MangoQuest.editor.EditorListenerObject.ListeningType;
import me.Cutiemango.MangoQuest.manager.QuestChatManager;
import me.Cutiemango.MangoQuest.manager.reward.QuestRewardManager;
import me.Cutiemango.MangoQuest.objects.QuestGUIItem;
import net.md_5.bungee.api.ChatColor;

public class RewardGUIListener
{
	public static void onInventoryClick(InventoryClickEvent e)
	{
		Inventory inv = e.getClickedInventory();
		Player p = (Player)e.getWhoClicked();
		int i = getIndex(e.getCurrentItem());
		if (e.getCurrentItem() != null && e.getClickedInventory() != null)
		{
			if (inv.getTitle().contains(I18n.locMsg("QuestReward.RewardTitle")))
			{
				e.setCancelled(true);
				if (i == -1)
					return;
				if (e.getClick().isLeftClick())
					QuestUtil.executeCommandAsync(p, "mq q reward add " + i);
				else if (e.getClick().isRightClick())
					QuestUtil.executeCommandAsync(p, "mq q reward remove " + i);
				p.closeInventory();
				return;
			}
			else if (inv.getTitle().contains(I18n.locMsg("QuestReward.RewardEditTitle")))
			{
				if (e.getCurrentItem().getType().equals(Material.ANVIL))
				{
					EditorListenerHandler.register(p, new EditorListenerObject(ListeningType.STRING, "mq e edit reward choiceamount", Syntax.of("I", I18n.locMsg("Syntax.Number"), "")));
					QuestChatManager.info(p, I18n.locMsg("QuestReward.EnterRewardAmount"));
					p.closeInventory();
					return;
				}
				e.setCancelled(true);
				if (i == -1)
					return;
				
				new BukkitRunnable()
				{
					@Override
					public void run()
					{
						QuestRewardManager.openEditRewardGUI(p, i);
					}
				}.runTaskLater(Main.instance, 3L);
				
				return;
			}
		}
	}
	
	// Edit GUIs
	public static void onInventoryClose(InventoryCloseEvent e)
	{
		// Prevent Stealing
		Player p = (Player)e.getPlayer();
		for (int i = 0; i < p.getInventory().getContents().length; i++)
		{
			ItemStack item = p.getInventory().getContents()[i];
			if (item == null || item.getType() == Material.AIR)
				continue;
			if (QuestGUIItem.isGUIItem(item))
			{
				p.getInventory().remove(item);;
				continue;
			}
		}
		
		Inventory inv = e.getInventory();
		if (inv != null)
		{
			if (inv.getTitle().contains(I18n.locMsg("QuestReward.ChoiceEditTitle")))
			{
				int index = Integer.parseInt(inv.getTitle().split(":")[1]);
				List<ItemStack> l = new ArrayList<>();
				for (ItemStack item : inv.getContents())
				{
					if (item == null || item.getType().equals(Material.AIR))
						continue;
					l.add(item);
				}
				QuestRewardManager.saveItemChoice(p, l, index);
				new BukkitRunnable()
				{
					@Override
					public void run()
					{
						QuestRewardManager.openEditMainGUI(p);	
					}
				}.runTaskLater(Main.instance, 3L);
			}
		}
		
	}
	
	private static int getIndex(ItemStack item)
	{
		int index = -1;
		if (item != null && item.hasItemMeta())
		{
			List<String> lore = item.getItemMeta().getLore();
			try
			{
				index = Integer.parseInt(ChatColor.stripColor(lore.get(lore.size()-1)));
			} catch (Exception ex) {};
		}
		
		return index;
	}
}
