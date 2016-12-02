package me.Cutiemango.MangoQuest.listeners;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import me.Cutiemango.MangoQuest.Main;
import me.Cutiemango.MangoQuest.QuestStorage;
import me.Cutiemango.MangoQuest.QuestUtil;
import me.Cutiemango.MangoQuest.data.QuestPlayerData;


public class PlayerListener implements Listener{

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e){
		Player p = e.getPlayer();
		QuestPlayerData qd = new QuestPlayerData(p);
		if (QuestPlayerData.hasConfigData(p))
			qd = new QuestPlayerData(p, Main.instance.configManager.getPlayerIO());
		QuestStorage.Players.put(p.getName(), qd);
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e){
		Player p = e.getPlayer();
		QuestPlayerData qd = QuestStorage.Players.get(p.getName());
		qd.save();
		QuestStorage.Players.remove(p.getName());
	}
	
	@EventHandler
	public void onEntityDeath(EntityDeathEvent e){
		if (e.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent){
			Entity damager = ((EntityDamageByEntityEvent)e.getEntity().getLastDamageCause()).getDamager();
			if (!(damager instanceof Player || damager instanceof Projectile))
				return;
			if (Main.instance.initManager.getMTMPlugin().getAPI().getMobAPI().isMythicMob(e.getEntity()))
				return;
			Player attacker;
			if (damager instanceof Projectile){
				if (!(((Projectile) damager).getShooter() instanceof Player))
					return;
				attacker = (Player) ((Projectile) damager).getShooter();
			}else{
				attacker = (Player) damager;
			}
			QuestPlayerData qd = QuestUtil.getData(attacker);
			qd.killEntity(e.getEntity());
		}
	}
	
	@EventHandler
	public void onBreakBlock(BlockBreakEvent e){
		Player p = e.getPlayer();
		QuestPlayerData qd = QuestUtil.getData(p);
		if (e.getBlock() != null && e.getBlock().getType() != null)
			qd.breakBlock(e.getBlock().getType());
	}
	
	@EventHandler
	public void onCosumeItem(PlayerItemConsumeEvent e){
		Player p = e.getPlayer();
		QuestPlayerData qd = QuestUtil.getData(p);
		if (e.getItem() != null && !e.isCancelled())
			qd.consumeItem(e.getItem());
	}
	
	@EventHandler
	public void onMove(PlayerMoveEvent e){
		Player p = e.getPlayer();
		QuestPlayerData qd = QuestUtil.getData(p);
		if (qd != null && e.getTo() != null)
		qd.reachLocation(e.getTo());
	}
}
