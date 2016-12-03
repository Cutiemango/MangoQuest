package me.Cutiemango.MangoQuest.listeners;

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import me.Cutiemango.MangoQuest.QuestUtil;
import me.Cutiemango.MangoQuest.manager.QuestEditorManager;
import net.citizensnpcs.api.event.NPCLeftClickEvent;

public class QuestEditorListener implements Listener{
	
	public static HashMap<String, String> CurrentListening = new HashMap<>();
	
	@EventHandler
	public void onChat(AsyncPlayerChatEvent e){
		Player p = e.getPlayer();
		if (CurrentListening.containsKey(p.getName())){
			p.performCommand(CurrentListening.get(p.getName()) + e.getMessage());
			CurrentListening.remove(p.getName());
			if (e.getMessage().contains("cancel"))
				QuestUtil.info(p, "&d已取消輸入。");
			else
				QuestUtil.info(p, "&7您的輸入： " + e.getMessage());
			e.setCancelled(true);
		}
		else return;
	}
	
	@EventHandler
	public void onRightClick(PlayerInteractEvent e){
		Player p = e.getPlayer();
		if (e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
			if (e.getItem() != null && !e.getItem().getType().equals(Material.AIR)){
				if (CurrentListening.containsKey(p.getName()) &&
						(CurrentListening.get(p.getName()).contains("item") || CurrentListening.get(p.getName()).contains("ITEM"))){
					p.performCommand(CurrentListening.get(p.getName()) + "hand");
					CurrentListening.remove(p.getName());
				}
				else return;
			}
		}
		return;
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e){
		Player p = e.getPlayer();
		if (p.isOp() && QuestEditorManager.isInEditorMode(p)
				&& e.getBlock() != null && e.getBlock().getType() != null){
			if (CurrentListening.containsKey(p.getName()) && CurrentListening.get(p.getName()).contains("block")){
				e.setCancelled(true);
				p.performCommand(CurrentListening.get(p.getName()) + e.getBlock().getType().toString());
				CurrentListening.remove(p.getName());
			}
			else return;
		}
	}
	
	@EventHandler
	public void onEntityDamage(EntityDamageByEntityEvent e){
		if (e.getDamager() instanceof Player && e.getEntity() instanceof Damageable){
			Player p = (Player) e.getDamager();
			if (QuestEditorManager.isInEditorMode(p) && CurrentListening.containsKey(p.getName())){
				e.setCancelled(true);
				p.performCommand(CurrentListening.get(p.getName()) + e.getEntity().getType().toString());
				CurrentListening.remove(p.getName());
			}
			else return;
		}
	}
	
	@EventHandler
	public void onNPCLeftClick(NPCLeftClickEvent e){
		Player p = e.getClicker();
		if (CurrentListening.containsKey(p.getName())){
			p.performCommand(CurrentListening.get(p.getName()) + e.getNPC().getId());
			QuestUtil.info(p, "&a你選取了 " + e.getNPC().getName() + " &a！");
			CurrentListening.remove(p.getName());
			e.setCancelled(true);
			return;
		}
	}
	
	public static void registerListeningObject(Player p, String cmd){
		CurrentListening.put(p.getName(), cmd);
	}
}
