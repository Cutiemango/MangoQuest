package me.Cutiemango.MangoQuest.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobDeathEvent;
import me.Cutiemango.MangoQuest.QuestUtil;
import me.Cutiemango.MangoQuest.data.QuestPlayerData;

public class MythicListener implements Listener
{

	@EventHandler
	public void onMythicMobDeath(MythicMobDeathEvent e)
	{
		if (e.getKiller() != null && e.getKiller() instanceof Player && e.getMobType() != null)
		{
			Player attacker = (Player) e.getKiller();
			QuestPlayerData qd = QuestUtil.getData(attacker);
			qd.killMythicMob(e.getMobType());
		}
	}
}
