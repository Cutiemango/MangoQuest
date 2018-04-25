package me.Cutiemango.MangoQuest.listeners;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
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
import me.old.RPGshop.GUIManager;
import net.citizensnpcs.api.npc.NPC;
import ru.nightexpress.unrealshop.shop.ShopManager;
import ru.nightexpress.unrealshop.shop.objects.UShop;

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
		if (qd != null)
			qd.save();
		QuestStorage.Players.remove(p.getName());
	}

	public static void onNPCRightClick(Player p, NPC npc, Cancellable event)
	{
		if (!ConfigSettings.USE_RIGHT_CLICK_MENU)
			return;
		if (p.isSneaking())
			return;
		if (!QuestNPCManager.hasData(npc.getId()))
			return;
		event.setCancelled(true);
		if (QuestEditorManager.checkEditorMode(p, false) || EditorListenerHandler.isListening(p))
			return;
		QuestPlayerData pd = QuestUtil.getData(p);
		if (pd.deliverItem(npc))
		{
			Main.debug("Blocked shopkeepers event, event cancelled = " + event.isCancelled());
			return;
		}
		else
			if (Main.getHooker().hasShopkeepersEnabled())
			{
				if (Main.getHooker().getShopkeepers().isShopkeeper(npc.getEntity()))
				{
					QuestBookGUIManager.openNPCInfo(p, npc, true);
					return;
				}
			}
		if (Bukkit.getServer().getPluginManager().isPluginEnabled("UnrealShop"))
		{
			for (UShop localUShop : ShopManager.getShops())
			{
				if (ArrayUtils.contains(localUShop.getNpcId(), npc.getId()))
				{
					QuestBookGUIManager.openNPCInfo(p, npc, true);
					return;
				}
			}
		}
		else
			if (Main.getHooker().hasRPGshopEnabled())
			{
				Main.debug(GUIManager.hasShop(npc) + "");
				if (GUIManager.hasShop(npc))
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
			if (Main.getHooker().hasMythicMobEnabled())
			{
				if (Main.getHooker().getMythicMobsAPI().isMythicMob(e))
				{
					qd.killMythicMob(Main.getHooker().getMythicMobsAPI().getMythicMobInstance(e).getType().getInternalName());
					return;
				}
			}
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
