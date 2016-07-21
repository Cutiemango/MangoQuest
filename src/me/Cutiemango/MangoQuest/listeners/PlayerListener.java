package me.Cutiemango.MangoQuest.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import me.Cutiemango.MangoQuest.QuestConfigLoad;
import me.Cutiemango.MangoQuest.QuestPlayerData;
import me.Cutiemango.MangoQuest.QuestStorage;


public class PlayerListener implements Listener{
	

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e){
		Player p = e.getPlayer();
		QuestPlayerData qd = new QuestPlayerData(p);
		if (QuestPlayerData.hasConfigData(p))
			qd = new QuestPlayerData(p, QuestConfigLoad.pconfig);
		QuestStorage.Players.put(p.getName(), qd);
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e){
		Player p = e.getPlayer();
		QuestPlayerData qd = QuestStorage.Players.get(p.getName());
		qd.save();
		QuestStorage.Players.remove(p.getName());
	}
	
}
