package me.Cutiemango.MangoQuest.listeners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import me.Cutiemango.MangoQuest.Main;
import me.Cutiemango.MangoQuest.QuestUtil;
import me.Cutiemango.MangoQuest.Questi18n;
import me.Cutiemango.MangoQuest.manager.QuestEditorManager;
import me.Cutiemango.MangoQuest.model.Quest;
import me.Cutiemango.MangoQuest.model.RequirementType;
import net.citizensnpcs.api.event.NPCLeftClickEvent;

public class QuestEditorListener implements Listener
{

	public static HashMap<String, String> CurrentListening = new HashMap<>();

	@EventHandler
	public void onChat(final AsyncPlayerChatEvent e)
	{
		final Player p = e.getPlayer();
		if (CurrentListening.containsKey(p.getName()))
		{
			final String msg = e.getMessage();
			new BukkitRunnable()
			{
				@Override
				public void run()
				{
					p.performCommand(CurrentListening.get(p.getName()) + msg);
					CurrentListening.remove(p.getName());
				}
			}.runTaskAsynchronously(Main.instance);
			if (e.getMessage().contains("cancel"))
				QuestUtil.info(p, Questi18n.localizeMessage("EditorMessage.CancelEntry"));
			else
				QuestUtil.info(p, Questi18n.localizeMessage("EditorMessage.YourEntry", msg));
			e.setCancelled(true);
		}
		else
			return;
	}

	@EventHandler
	public void onRightClick(PlayerInteractEvent e)
	{
		Player p = e.getPlayer();
		if (e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK))
		{
			if (e.getItem() != null && !e.getItem().getType().equals(Material.AIR))
			{
				if (CurrentListening.containsKey(p.getName())
						&& (CurrentListening.get(p.getName()).contains("item") || CurrentListening.get(p.getName()).contains("ITEM")))
				{
					p.performCommand(CurrentListening.get(p.getName()) + "hand");
					CurrentListening.remove(p.getName());
				}
				else
					return;
			}
		}
		return;
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e)
	{
		Player p = e.getPlayer();
		if (p.isOp() && QuestEditorManager.isInEditorMode(p) && e.getBlock() != null && e.getBlock().getType() != null)
		{
			if (CurrentListening.containsKey(p.getName()) && CurrentListening.get(p.getName()).contains("block"))
			{
				e.setCancelled(true);
				p.performCommand(CurrentListening.get(p.getName()) + e.getBlock().getType().toString() + ":" + e.getBlock().getData());
				CurrentListening.remove(p.getName());
			}
			else
				return;
		}
	}

	@EventHandler
	public void onEntityDamage(EntityDamageByEntityEvent e)
	{
		if (e.getDamager() instanceof Player && e.getEntity() instanceof Damageable)
		{
			Player p = (Player) e.getDamager();
			if (QuestEditorManager.isInEditorMode(p) && CurrentListening.containsKey(p.getName()))
			{
				e.setCancelled(true);
				if (CurrentListening.get(p.getName()).contains("mtmmob"))
					if (Main.instance.initManager.hasMythicMobEnabled() && Main.instance.initManager.getMythicMobsAPI().isMythicMob(e.getEntity()))
						p.performCommand(CurrentListening.get(p.getName())
								+ Main.instance.initManager.getMythicMobsAPI().getMythicMobInstance(e.getEntity()).getType().getInternalName());
					else
						p.performCommand(CurrentListening.get(p.getName()) + e.getEntity().getType().toString());
				else
					if (CurrentListening.get(p.getName()).contains("mobname"))
						if (e.getEntity().getCustomName() != null)
							p.performCommand(CurrentListening.get(p.getName()) + e.getEntity().getCustomName());
						else
							p.performCommand(CurrentListening.get(p.getName()) + QuestUtil.translate(e.getEntity().getType()));
					else
						if (CurrentListening.get(p.getName()).contains("mobtype"))
							p.performCommand(CurrentListening.get(p.getName()) + e.getEntity().getType().toString());
				CurrentListening.remove(p.getName());
			}
			else
				return;
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onNPCLeftClick(NPCLeftClickEvent e)
	{
		Player p = e.getClicker();
		if (CurrentListening.containsKey(p.getName()))
		{
			p.performCommand(CurrentListening.get(p.getName()) + e.getNPC().getId());
			QuestUtil.info(p, Questi18n.localizeMessage("EditorMessage.NPCSelcted", e.getNPC().getName()));
			CurrentListening.remove(p.getName());
			e.setCancelled(true);
			return;
		}
	}

	@EventHandler
	public void onInventoryClose(InventoryCloseEvent e)
	{
		if (!(e.getPlayer() instanceof Player))
			return;
		Player p = (Player) e.getPlayer();
		if (CurrentListening.containsKey(p.getName()) && QuestEditorManager.isInEditorMode(p))
		{
			if (!CurrentListening.get(p.getName()).contains("inv"))
				return;
			Quest q = QuestEditorManager.getCurrentEditingQuest(p);
			Inventory inv = e.getInventory();
			List<ItemStack> list = new ArrayList<>();
			for (ItemStack is : inv.getContents())
			{
				if (is == null || is.getType().equals(Material.AIR))
					continue;
				else
					list.add(is);
			}
			if (inv.getName().contains("Reward"))
			{
				q.getQuestReward().setItemReward(list);
				QuestEditorManager.editQuest(p);
			}
			else
				if (inv.getName().contains("Requirement"))
				{
					q.getRequirements().put(RequirementType.ITEM, list);
					QuestEditorManager.editQuestRequirement(p);
				}
			QuestUtil.info(p, Questi18n.localizeMessage("EditorMessage.ItemSaved"));
			CurrentListening.remove(p.getName());
			return;
		}
	}

	public static void registerListeningObject(Player p, String cmd)
	{
		CurrentListening.put(p.getName(), cmd);
	}

	@SuppressWarnings("unchecked")
	public static void registerGUI(Player p, String obj)
	{
		if (QuestEditorManager.isInEditorMode(p))
		{
			if (obj.equalsIgnoreCase("reward"))
				QuestEditorManager.generateEditItemGUI(p, "Reward", QuestEditorManager.getCurrentEditingQuest(p).getQuestReward().getItems());
			else
				if (obj.equalsIgnoreCase("requirement"))
					QuestEditorManager.generateEditItemGUI(p, "Requirement",
							(List<ItemStack>) QuestEditorManager.getCurrentEditingQuest(p).getRequirements().get(RequirementType.ITEM));
				else
					return;
			CurrentListening.put(p.getName(), "inv");
		}
	}

}
