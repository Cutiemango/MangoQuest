package me.Cutiemango.MangoQuest.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import me.Cutiemango.MangoQuest.QuestUtil;
import me.Cutiemango.MangoQuest.data.QuestPlayerData;
import net.citizensnpcs.api.event.NPCRightClickEvent;

public class QuestListener implements Listener{
	
	@EventHandler
	public void onNPCRightClick(NPCRightClickEvent e){
		Player p = e.getClicker();
		QuestPlayerData pd = QuestUtil.getData(p);
		if (p.getInventory().getItemInMainHand().getType().equals(Material.AIR))
			pd.talkToNPC(e.getNPC());
		else
			pd.deliverItem(e.getNPC());
	}
	

}
