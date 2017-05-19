package me.Cutiemango.MangoQuest.listeners;

import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import me.Cutiemango.MangoQuest.Main;
import me.Cutiemango.MangoQuest.QuestUtil;
import me.Cutiemango.MangoQuest.Questi18n;
import me.Cutiemango.MangoQuest.data.QuestPlayerData;
import me.Cutiemango.MangoQuest.manager.QuestEditorManager;
import me.Cutiemango.MangoQuest.manager.QuestGUIManager;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;

public class QuestListener implements Listener
{

	@EventHandler(priority = EventPriority.LOW)
	public void onEntityInteract(PlayerInteractEntityEvent e)
	{
		Player p = e.getPlayer();
		if (e.getRightClicked() instanceof LivingEntity)
		{
			if (CitizensAPI.getNPCRegistry().getNPC(e.getRightClicked()) != null)
			{
				NPC npc = CitizensAPI.getNPCRegistry().getNPC(e.getRightClicked());
				if (p.isSneaking())
					return;
				e.setCancelled(true);
				if (QuestEditorManager.isInEditorMode(p))
				{
					QuestUtil.info(p, Questi18n.localizeMessage("EditorMessage.PleaseQuitEdtior"));
					return;
				}
				QuestPlayerData pd = QuestUtil.getData(p);
				if (Main.instance.handler.getItemInMainHand(p).getType().equals(Material.AIR) || Main.instance.handler.getItemInMainHand(p) == null)
				{
					if (Main.instance.initManager.hasShopkeepersEnabled())
					{
						if (Main.instance.initManager.getShopkeepers().isShopkeeper(npc.getEntity()))
						{
							QuestGUIManager.openNPCInfo(p, npc, true);
							return;
						}
					}
					QuestGUIManager.openNPCInfo(p, npc, false);
					return;
				}
				pd.deliverItem(npc);
				return;
			}
		}
		return;
	}

}
