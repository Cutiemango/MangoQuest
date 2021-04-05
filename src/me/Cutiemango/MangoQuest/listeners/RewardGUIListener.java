package me.Cutiemango.MangoQuest.listeners;

import me.Cutiemango.MangoQuest.*;
import me.Cutiemango.MangoQuest.editor.EditorListenerHandler;
import me.Cutiemango.MangoQuest.editor.EditorListenerObject;
import me.Cutiemango.MangoQuest.editor.EditorListenerObject.ListeningType;
import me.Cutiemango.MangoQuest.editor.QuestEditorManager;
import me.Cutiemango.MangoQuest.manager.QuestChatManager;
import me.Cutiemango.MangoQuest.manager.QuestRewardManager;
import me.Cutiemango.MangoQuest.objects.reward.QuestGUIItem;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class RewardGUIListener
{
	public static void onInventoryClick(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
		InventoryView inv = e.getView();

		if (e.getCurrentItem() != null) {
			int i = getIndex(e.getCurrentItem());
			if (inv.getTitle().contains(QuestChatManager.translateColor(I18n.locMsg("QuestReward.RewardTitle")))) {
				e.setCancelled(true);
				if (i == -1)
					return;
				if (e.getClick().isLeftClick())
					QuestUtil.executeSyncCommand(p, "mq q reward add " + i);
				else if (e.getClick().isRightClick())
					QuestUtil.executeSyncCommand(p, "mq q reward remove " + i);
				p.closeInventory();
			} else if (inv.getTitle().contains(QuestChatManager.translateColor(I18n.locMsg("QuestReward.RewardEditTitle")))) {
				if (QuestGUIItem.isGUIItem(e.getCurrentItem())) {
					if (e.getCurrentItem().getType().equals(Material.ANVIL)) {
						p.closeInventory();
						EditorListenerHandler.register(p, new EditorListenerObject(ListeningType.STRING, "mq e edit reward choiceamount",
								Syntax.of("I", I18n.locMsg("Syntax.Number"), "")));
						QuestChatManager.info(p, I18n.locMsg("QuestReward.EnterRewardAmount"));
						return;
					} else if (e.getCurrentItem().getType().equals(Material.WRITABLE_BOOK)) {
						p.closeInventory();
						QuestEditorManager.editQuest(p);
						return;
					} else if (e.getCurrentItem().getType().equals(Material.PLAYER_HEAD)) {
						p.closeInventory();
						QuestUtil.executeSyncCommand(p, "mq e edit reward npc");
						return;
					}
				}
				e.setCancelled(true);
				if (i == -1)
					return;

				new BukkitRunnable()
				{
					@Override
					public void run() {
						QuestRewardManager.openEditRewardGUI(p, i);
					}
				}.runTaskLater(Main.getInstance(), 3L);
			} else if (inv.getTitle().contains(QuestChatManager.translateColor(I18n.locMsg("QuestReward.ChoiceEditTitle")))) {
				if (e.getCurrentItem().getType().equals(Material.BARRIER) && QuestGUIItem.isGUIItem(e.getCurrentItem())) {
					e.setCancelled(true);
					p.closeInventory();
					String index = inv.getTitle().substring(inv.getTitle().length() - 1);
					QuestRewardManager.removeRewardChoice(p, Integer.parseInt(index));
					QuestRewardManager.openEditMainGUI(p);
				}
			}
		}
	}

	// Edit GUIs
	public static void onInventoryClose(InventoryCloseEvent e) {
		// Prevent Stealing
		Player p = (Player) e.getPlayer();
		for (int i = 0; i < p.getInventory().getContents().length; i++) {
			ItemStack item = p.getInventory().getContents()[i];
			if (item == null || item.getType() == Material.AIR)
				continue;
			if (QuestGUIItem.isGUIItem(item))
				p.getInventory().remove(item);
		}

		InventoryView inv = e.getView();
		if (inv.getTitle().contains(QuestChatManager.translateColor(I18n.locMsg("QuestReward.ChoiceEditTitle")))) {
			int index = Integer.parseInt(inv.getTitle().split("@")[1]);
			List<ItemStack> l = new ArrayList<>();
			for (ItemStack item : e.getInventory().getContents()) {
				if (item == null || item.getType().equals(Material.AIR) || QuestGUIItem.isGUIItem(item))
					continue;
				l.add(item);
			}
			QuestRewardManager.saveItemChoice(p, l, index);
			new BukkitRunnable()
			{
				@Override
				public void run() {
					QuestRewardManager.openEditMainGUI(p);
				}
			}.runTaskLater(Main.getInstance(), 3L);
		}

	}

	private static int getIndex(ItemStack item) {
		int index = -1;
		if (item != null && item.hasItemMeta()) {
			List<String> lore = item.getItemMeta().getLore();
			try {
				index = Integer.parseInt(ChatColor.stripColor(lore.get(lore.size() - 1)));
			}
			catch (Exception ignored) {
			}
		}
		return index;
	}
}
