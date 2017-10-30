package me.Cutiemango.MangoQuest.listeners;

import java.util.List;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import me.Cutiemango.MangoQuest.I18n;
import me.Cutiemango.MangoQuest.QuestUtil;
import me.Cutiemango.MangoQuest.editor.QuestEditorManager;
import net.md_5.bungee.api.ChatColor;

public class RewardGUIListener
{
	public static void onInventoryClick(InventoryClickEvent e)
	{
		Inventory inv = e.getClickedInventory();
		Player p = (Player)e.getWhoClicked();
		if (e.getCurrentItem() != null && e.getClickedInventory() != null)
		{
			int index = 0;
			if (inv.getTitle().contains(I18n.locMsg("QuestReward.RewardTitle")))
			{
				e.setCancelled(true);
				if (e.getCurrentItem() != null && e.getCurrentItem().hasItemMeta())
				{
					List<String> lore = e.getCurrentItem().getItemMeta().getLore();
					try
					{
						index = Integer.parseInt(ChatColor.stripColor(lore.get(lore.size()-1)));
					} catch (Exception ex) {};
				}
				if (e.getClick().isLeftClick())
					QuestUtil.executeCommandAsync(p, "mq q reward add " + index);
				else if (e.getClick().isRightClick())
					QuestUtil.executeCommandAsync(p, "mq q reward remove " + index);
				p.closeInventory();
				return;
			}
			else if (inv.getTitle().contains(I18n.locMsg("QuestReward.RewardEditTitle")))
			{
				if (QuestEditorManager.checkEditorMode(p, false))
				{
					e.setCancelled(true);
					QuestUtil.executeCommandAsync(p, "mq e edit reward item " + index);
					return;
				}
			}
		}
	}
	
	// Edit GUIs
	public static void onInventoryClose(InventoryCloseEvent e)
	{
		
	}
}
