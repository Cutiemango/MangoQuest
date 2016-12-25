package me.Cutiemango.MangoQuest.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import me.Cutiemango.MangoQuest.Main;
import me.Cutiemango.MangoQuest.QuestUtil;
import me.Cutiemango.MangoQuest.data.QuestPlayerData;
import me.Cutiemango.MangoQuest.manager.QuestEditorManager;
import me.Cutiemango.MangoQuest.manager.QuestGUIManager;
import net.citizensnpcs.api.event.NPCRightClickEvent;

public class QuestListener implements Listener{
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onNPCRightClick(NPCRightClickEvent e){
		Player p = e.getClicker();
		if (QuestEditorManager.isInEditorMode(p))
			return;
		QuestPlayerData pd = QuestUtil.getData(p);
		if (Main.instance.isUsingOldVersion()){
			if (p.getItemInHand().getType().equals(Material.AIR) ||
					p.getInventory().getItemInMainHand() == null){
				QuestGUIManager.openNPCInfo(p, e.getNPC());
				return;
			}
		}
		else if (p.getInventory().getItemInMainHand().getType().equals(Material.AIR) ||
				p.getInventory().getItemInMainHand() == null){
			QuestGUIManager.openNPCInfo(p, e.getNPC());
			return;
		}
		pd.deliverItem(e.getNPC());
	}
	

}
