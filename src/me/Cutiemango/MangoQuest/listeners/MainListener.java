package me.Cutiemango.MangoQuest.listeners;

import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import me.Cutiemango.MangoQuest.QuestUtil;
import me.Cutiemango.MangoQuest.editor.ConversationEditorManager;
import me.Cutiemango.MangoQuest.editor.EditorListenerHandler;
import me.Cutiemango.MangoQuest.editor.QuestEditorManager;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.NPCDamageByEntityEvent;
import net.citizensnpcs.api.event.NPCLeftClickEvent;

public class MainListener implements Listener
{
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e)
	{
		PlayerListener.onPlayerJoin(e.getPlayer());
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e)
	{
		PlayerListener.onPlayerQuit(e.getPlayer());
	}

	@EventHandler
	public void onEntityDeath(EntityDeathEvent e)
	{
		PlayerListener.onEntityDeath(e.getEntity());
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent e)
	{
		if (e.getBlock() != null && e.getBlock().getType() != null)
		{
			PlayerListener.onBreakBlock(e.getPlayer(), e.getBlock().getType());
			EditorListenerHandler.onBlockBreak(e.getPlayer(), e.getBlock(), e);
		}
	}

	@EventHandler
	public void onConsumeItem(PlayerItemConsumeEvent e)
	{
		if (e.getItem() != null && !e.isCancelled())
			PlayerListener.onConsumeItem(e.getPlayer(), e.getItem());
	}

	@EventHandler
	public void onMove(PlayerMoveEvent e)
	{
		if (QuestUtil.getData(e.getPlayer()) != null && e.getTo() != null)
			PlayerListener.onMove(e.getPlayer(), e.getTo());
	}

	@EventHandler
	public void onChat(AsyncPlayerChatEvent e)
	{
		EditorListenerHandler.onChat(e.getPlayer(), e.getMessage(), e);
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e)
	{
		EditorListenerHandler.onPlayerInteract(e.getPlayer(), e.getAction(), e.getItem(), e);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onInteractEntity(PlayerInteractEntityEvent e)
	{
		if (e.getHand().equals(EquipmentSlot.OFF_HAND))
			return;
		if (CitizensAPI.getNPCRegistry().isNPC(e.getRightClicked()))
			PlayerListener.onNPCRightClick(e.getPlayer(), CitizensAPI.getNPCRegistry().getNPC(e.getRightClicked()), e);
	}
	
	@EventHandler(priority = EventPriority.LOWEST , ignoreCancelled = true)
	public void onNPCDamage(NPCDamageByEntityEvent e)
	{
		if (!(e.getDamager() instanceof Player))
			return;
		Player p = (Player) e.getDamager();
		if (QuestEditorManager.checkEditorMode(p, false) || ConversationEditorManager.checkEditorMode(p, false))
			e.setCancelled(true);
		return;
	}

	@EventHandler(priority = EventPriority.LOWEST , ignoreCancelled = true)
	public void onNPCLeftClick(NPCLeftClickEvent e)
	{
		EditorListenerHandler.onNPCLeftClick(e.getClicker(), e.getNPC(), e);
	}

	@EventHandler
	public void onInventoryClose(InventoryCloseEvent e)
	{
		if (!(e.getPlayer() instanceof Player))
			return;
		EditorListenerHandler.onInventoryClose((Player) e.getPlayer(), e.getInventory(), e.getView());
		RewardGUIListener.onInventoryClose(e);
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent e)
	{
		if (e.getClickedInventory() == null || e.getView().getTitle() == null || !(e.getWhoClicked() instanceof Player))
			return;
		RewardGUIListener.onInventoryClick(e);
	}

	@EventHandler(ignoreCancelled = true)
	public void onEntityDamage(EntityDamageByEntityEvent e)
	{
		if (e.getDamager() instanceof Player && e.getEntity() instanceof Damageable)
			EditorListenerHandler.onEntityDamage((Player) e.getDamager(), e.getEntity(), e);
		else
			return;
	}

}
