package me.Cutiemango.MangoQuest.listeners;

import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import me.Cutiemango.MangoQuest.QuestUtil;
import me.Cutiemango.MangoQuest.editor.QuestEditorListener;
import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;

public class QuestListener implements Listener
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
	public void onEntityDeath(EntityDeathEvent e){
		PlayerListener.onEntityDeath(e.getEntity());
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e){
		if (e.getBlock() != null && e.getBlock().getType() != null)
		{
			PlayerListener.onBreakBlock(e.getPlayer(), e.getBlock().getType(), e.getBlock().getData());
			QuestEditorListener.onBlockBreak(e.getPlayer(), e.getBlock(), e);
		}
	}
	
	@EventHandler
	public void onConsumeItem(PlayerItemConsumeEvent e){
		if (e.getItem() != null && !e.isCancelled())
			PlayerListener.onConsumeItem(e.getPlayer(), e.getItem());
	}
	
	@EventHandler
	public void onMove(PlayerMoveEvent e){
		if (QuestUtil.getData(e.getPlayer()) != null && e.getTo() != null)
			PlayerListener.onMove(e.getPlayer(), e.getTo());
	}
	
	@EventHandler
	public void onChat(AsyncPlayerChatEvent e){
		QuestEditorListener.onChat(e.getPlayer(), e.getMessage(), e);
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e){
		QuestEditorListener.onPlayerInteract(e.getPlayer(), e.getAction(), e.getItem());
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onNPCRightClick(NPCRightClickEvent e)
	{
		PlayerListener.onNPCRightClick(e.getClicker(), e.getNPC(), e);
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onNPCLeftClick(NPCLeftClickEvent e){
		QuestEditorListener.onNPCLeftClick(e.getClicker(), e.getNPC(), e);
	}
	
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent e){
		if (!(e.getPlayer() instanceof Player))
			return;
		QuestEditorListener.onInventoryClose((Player)e.getPlayer(), e.getInventory());
	}
	
	@EventHandler (ignoreCancelled = true)
	public void onEntityDamage(EntityDamageByEntityEvent e){
		if (e.getDamager() instanceof Player && e.getEntity() instanceof Damageable)
			QuestEditorListener.onEntityDamage((Player)e.getDamager(), e.getEntity(), e);
		else return;
	}

}
