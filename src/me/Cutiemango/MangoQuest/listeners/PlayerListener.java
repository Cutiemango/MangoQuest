package me.Cutiemango.MangoQuest.listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Cancellable;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import me.Cutiemango.MangoQuest.ConfigSettings;
import me.Cutiemango.MangoQuest.Main;
import me.Cutiemango.MangoQuest.QuestStorage;
import me.Cutiemango.MangoQuest.QuestUtil;
import me.Cutiemango.MangoQuest.data.QuestPlayerData;
import me.Cutiemango.MangoQuest.editor.EditorListenerHandler;
import me.Cutiemango.MangoQuest.editor.QuestEditorManager;
import me.Cutiemango.MangoQuest.manager.QuestBookGUIManager;
import me.Cutiemango.MangoQuest.manager.QuestNPCManager;
import net.citizensnpcs.api.npc.NPC;

public class PlayerListener
{

	public static void onPlayerJoin(Player p)
	{
		QuestPlayerData qd = new QuestPlayerData(p);
		QuestStorage.Players.put(p.getName(), qd);
		qd.checkQuestFail();
		qd.checkRewardUnclaimed();
	}

	public static void onPlayerQuit(Player p)
	{
		QuestPlayerData qd = QuestUtil.getData(p);
		qd.save();
		QuestStorage.Players.remove(p.getName());
	}
	
	public static void onNPCRightClick(Player p, NPC npc, Cancellable event)
	{
		if (!ConfigSettings.USE_RIGHT_CLICK_MENU)
			return;
		if (p.isSneaking())
			return;
		event.setCancelled(true);
		if (QuestEditorManager.checkEditorMode(p, false) || EditorListenerHandler.isListening(p))
			return;
		QuestPlayerData pd = QuestUtil.getData(p);
		if (!QuestNPCManager.hasData(npc.getId()))
			return;
		if (pd.deliverItem(npc))
			return;
		else
			if (Main.instance.pluginHooker.hasShopkeepersEnabled())
			{
				if (Main.instance.pluginHooker.getShopkeepers().isShopkeeper(npc.getEntity()))
				{
					QuestBookGUIManager.openNPCInfo(p, npc, true);
					return;
				}
			}
		QuestBookGUIManager.openNPCInfo(p, npc, false);
		return;
	}

	public static void onEntityDeath(Entity e)
	{
		if (e.getLastDamageCause() instanceof EntityDamageByEntityEvent)
		{
			Entity damager = ((EntityDamageByEntityEvent) e.getLastDamageCause()).getDamager();
			if (!(damager instanceof Player || damager instanceof Projectile))
				return;
			if (Main.instance.pluginHooker.hasMythicMobEnabled() && Main.instance.pluginHooker.getMTMPlugin().getAPIHelper().isMythicMob(e))
				return;
			Player attacker;
			if (damager instanceof Projectile)
			{
				if (!(((Projectile) damager).getShooter() instanceof Player))
					return;
				attacker = (Player) ((Projectile) damager).getShooter();
			}
			else
				attacker = (Player) damager;
			QuestPlayerData qd = QuestUtil.getData(attacker);
			qd.killEntity(e);
		}
	}

	public static void onBreakBlock(Player p, Material m, short subID)
	{
		QuestPlayerData qd = QuestUtil.getData(p);
		qd.breakBlock(m, subID);
	}

	public static void onConsumeItem(Player p, ItemStack is)
	{
		QuestPlayerData qd = QuestUtil.getData(p);
		qd.consumeItem(is);
	}

	public static void onMove(Player p, Location loc)
	{
		QuestPlayerData qd = QuestUtil.getData(p);
		qd.reachLocation(loc);
	}
}
