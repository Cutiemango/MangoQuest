package me.Cutiemango.MangoQuest.listeners;

import me.Cutiemango.MangoQuest.DebugHandler;
import me.Cutiemango.MangoQuest.QuestUtil;
import me.Cutiemango.MangoQuest.data.QuestPlayerData;
import me.Cutiemango.MangoQuest.editor.ConversationEditorManager;
import me.Cutiemango.MangoQuest.editor.EditorListenerHandler;
import me.Cutiemango.MangoQuest.editor.QuestEditorManager;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.NPCDamageByEntityEvent;
import net.citizensnpcs.api.event.NPCLeftClickEvent;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Item;
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
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;

public class MainListener implements Listener
{

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		PlayerListener.onPlayerJoin(e.getPlayer());
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		PlayerListener.onPlayerQuit(e.getPlayer());
	}

	@EventHandler
	public void onEntityDeath(EntityDeathEvent e) {
		PlayerListener.onEntityDeath(e.getEntity());
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		Player p = e.getPlayer();
		QuestPlayerData qd = QuestUtil.getData(p);
		if (qd == null) {
			DebugHandler.log(5, "[Listener] Player %s has no player data.", p.getName());
			return;
		}
		if (e.isCancelled()) {
			DebugHandler.log(5, "[Listener] Detected player %s broke a block, but the event is cancelled.", p.getName());
			return;
		}
		if (e.getBlock() != null && e.getBlock().getType() != null) {
			PlayerListener.onBreakBlock(p, e.getBlock().getType());
			EditorListenerHandler.onBlockBreak(p, e.getBlock(), e);
		}
	}

	@EventHandler
	public void onConsumeItem(PlayerItemConsumeEvent e) {
		Player p = e.getPlayer();
		QuestPlayerData qd = QuestUtil.getData(p);
		if (qd == null) {
			DebugHandler.log(4, "[Listener] Player " + p.getName() + " has no player data.");
			return;
		}
		if (e.getItem() != null && !e.isCancelled())
			PlayerListener.onConsumeItem(p, e.getItem());
	}

	@EventHandler
	public void onMove(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		QuestPlayerData qd = QuestUtil.getData(p);
		if (qd != null && e.getTo() != null)
			PlayerListener.onMove(p, e.getTo());
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onInteractEntity(PlayerInteractEntityEvent e) {
		if (e.getHand().equals(EquipmentSlot.OFF_HAND))
			return;
		Player p = e.getPlayer();
		QuestPlayerData qd = QuestUtil.getData(p);
		if (qd == null) {
			DebugHandler.log(4, "[Listener] Player " + p.getName() + " has no player data.");
			return;
		}
		if (CitizensAPI.getNPCRegistry().isNPC(e.getRightClicked()))
			PlayerListener.onNPCRightClick(p, CitizensAPI.getNPCRegistry().getNPC(e.getRightClicked()), e);
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onNPCDamage(NPCDamageByEntityEvent e) {
		if (!(e.getDamager() instanceof Player))
			return;
		Player p = (Player) e.getDamager();
		QuestPlayerData qd = QuestUtil.getData(p);
		if (qd == null) {
			DebugHandler.log(4, "[Listener] Player " + p.getName() + " has no player data.");
			return;
		}
		if (QuestEditorManager.checkEditorMode(p, false) || ConversationEditorManager.checkEditorMode(p, false))
			e.setCancelled(true);
		return;
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onNPCLeftClick(NPCLeftClickEvent e) {
		EditorListenerHandler.onNPCLeftClick(e.getClicker(), e.getNPC(), e);
	}

	@EventHandler
	public void onInventoryClose(InventoryCloseEvent e) {
		if (!(e.getPlayer() instanceof Player))
			return;
		EditorListenerHandler.onInventoryClose((Player) e.getPlayer(), e.getInventory(), e.getView());
		RewardGUIListener.onInventoryClose(e);
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		if (!(e.getWhoClicked() instanceof Player))
			return;
		Player p = (Player) e.getWhoClicked();
		QuestPlayerData qd = QuestUtil.getData(p);
		if (qd == null) {
			DebugHandler.log(4, "[Listener] Player " + p.getName() + " has no player data.");
			return;
		}
		if (e.getClickedInventory() == null || e.getView().getTitle() == null)
			return;
		RewardGUIListener.onInventoryClick(e);
	}

	@EventHandler(ignoreCancelled = true)
	public void onEntityDamage(EntityDamageByEntityEvent e) {
		if (!(e.getDamager() instanceof Player) || !(e.getEntity() instanceof Damageable))
			return;
		Player p = (Player) e.getDamager();
		QuestPlayerData qd = QuestUtil.getData(p);
		if (qd == null) {
			DebugHandler.log(4, "[Listener] Player " + p.getName() + " has no player data.");
			return;
		}
		EditorListenerHandler.onEntityDamage(p, e.getEntity(), e);
	}

	@EventHandler
	public void onFish(PlayerFishEvent e) {
		if (e.getState().equals(PlayerFishEvent.State.CAUGHT_FISH)) {
			Item item = (Item) e.getCaught();
			PlayerListener.onFish(e.getPlayer(), item);
		}
	}

	@EventHandler
	public void onChat(AsyncPlayerChatEvent e) {
		Player p = e.getPlayer();
		EditorListenerHandler.onChat(p, e.getMessage(), e);
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		QuestPlayerData qd = QuestUtil.getData(p);
		if (qd == null) {
			DebugHandler.log(4, "[Listener] Player " + p.getName() + " has no player data.");
			return;
		}
		EditorListenerHandler.onPlayerInteract(p, e.getAction(), e.getItem(), e);
	}

}
