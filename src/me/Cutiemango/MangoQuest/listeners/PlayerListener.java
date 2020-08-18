package me.Cutiemango.MangoQuest.listeners;

import com.nisovin.shopkeepers.api.ShopkeepersAPI;
import me.Cutiemango.MangoQuest.ConfigSettings;
import me.Cutiemango.MangoQuest.DebugHandler;
import me.Cutiemango.MangoQuest.Main;
import me.Cutiemango.MangoQuest.QuestStorage;
import me.Cutiemango.MangoQuest.QuestUtil;
import me.Cutiemango.MangoQuest.data.QuestPlayerData;
import me.Cutiemango.MangoQuest.editor.EditorListenerHandler;
import me.Cutiemango.MangoQuest.editor.QuestEditorManager;
import me.Cutiemango.MangoQuest.manager.QuestBookGUIManager;
import me.Cutiemango.MangoQuest.manager.QuestNPCManager;
import me.Cutiemango.MangoQuest.questobject.objects.QuestObjectFishing;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Cancellable;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;


public class PlayerListener
{
	public static void onPlayerJoin(Player p)
	{
		DebugHandler.log(2, "[Listener] Player " + p.getName() + " logged in.");
		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				QuestPlayerData qd = new QuestPlayerData(p);
				qd.load(ConfigSettings.SAVE_TYPE);
				QuestStorage.Players.put(p.getName(), qd);
				qd.checkQuestFail();
				qd.checkUnclaimedReward();
			}
		}.runTaskLater(Main.getInstance(), 20L);
	}

	public static void onPlayerQuit(Player p)
	{
		QuestPlayerData qd = QuestUtil.getData(p);
		if (qd != null)
			qd.save();
		QuestStorage.Players.remove(p.getName());
		DebugHandler.log(2, "[Listener] Saved data of player " + p.getName() + ".");
	}

	public static void onNPCRightClick(Player p, NPC npc, Cancellable event)
	{
		if (!ConfigSettings.USE_RIGHT_CLICK_MENU)
		{
			DebugHandler.log(4, "[Listener] Event cancelled because useRightClickMenu=false.");
			return;
		}
		if (p.isSneaking())
			return;
		if (!QuestNPCManager.hasData(npc.getId()))
		{
			DebugHandler.log(4, "[Listener] Event cancelled because NPC data of id=" + npc.getId() + " does not exist.");
			return;
		}
		event.setCancelled(true);
		if (QuestEditorManager.checkEditorMode(p, false) || EditorListenerHandler.isListening(p))
		{
			DebugHandler.log(4, "[Listener] Event cancelled because the player is in editor mode.");
			return;
		}

		// Player can only access npc gui if they have no item held in hand.
		// Else the system will try to detect if there are compatible item-delivering quests.
		if (p.getInventory().getItemInMainHand() == null || p.getInventory().getItemInMainHand().getType() == Material.AIR)
		{
			if (Main.getHooker().hasShopkeepersEnabled())
			{
				if (ShopkeepersAPI.getShopkeeperRegistry().isShopkeeper(npc.getEntity()))
				{
					// close the shopkeeper's trading ui, because there's no way we can handle it before it fires
					p.closeInventory();
					QuestBookGUIManager.openNPCInfo(p, npc, true);
					DebugHandler.log(4, "[Listener] Shopkeepers NPC detected(id=" + npc.getId() + "), opening trading book GUI...");
					return;
				}
			}
			QuestBookGUIManager.openNPCInfo(p, npc, false);
			DebugHandler.log(4, "[Listener] Opening NPC info(id=" + npc.getId() + ") for " + p.getName() + "...");
		}
		else
		{
			if (Main.getHooker().hasShopkeepersEnabled() && ShopkeepersAPI.getShopkeeperRegistry().isShopkeeper(npc.getEntity()))
			{
				p.closeInventory();
				DebugHandler.log(4, "[Listener] Shopkeepers NPC detected, closing trading window.");
			}
			QuestPlayerData pd = QuestUtil.getData(p);
			if (pd.deliverItem(npc))
			{
				DebugHandler.log(3, "[Listener] Player " + p.getName() + " tried to deliver item.");
				return;
			}
		}
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
			if (qd != null)
			{
				if (Main.getHooker().hasMythicMobEnabled() && Main.getHooker().getMythicMobsAPI().isMythicMob(e))
				{
					String type = Main.getHooker().getMythicMobsAPI().getMythicMobInstance(e).getType().getInternalName();
					DebugHandler.log(4, "[Listener] Player " + attacker.getName() + " killed MythicMob, id: %s", type);
					qd.killMythicMob(type);
				}
				else
				{
					DebugHandler.log(4, "[Listener] Player " + attacker.getName() + " killed normal mob: %s", e.getType().toString());
					qd.killMob(e);
				}
			}
		}
	}
	public static void onFish(Player p, Item item)
	{
		QuestPlayerData qd = QuestUtil.getData(p);
		if (QuestObjectFishing.FISHES.contains(item.getItemStack().getType()))
			qd.catchFish();
	}


	public static void onBreakBlock(Player p, Material m)
	{
		QuestPlayerData qd = QuestUtil.getData(p);
		if (qd == null)
		{
			DebugHandler.log(4, "[Listener] Player " + p.getName() + " has no player data.");
			return;
		}
		qd.breakBlock(m);
		DebugHandler.log(4, "[Listener] Player " + p.getName() + " broke block mat=" + m.toString());
	}

	public static void onConsumeItem(Player p, ItemStack is)
	{
		QuestPlayerData qd = QuestUtil.getData(p);
		if (qd == null)
		{
			DebugHandler.log(4, "[Listener] Player " + p.getName() + " has no player data.");
			return;
		}
		qd.consumeItem(is);
		DebugHandler.log(4, "[Listener] Player " + p.getName() + " consumed item mat=" + is.getType().toString());
	}

	public static void onMove(Player p, Location loc)
	{
		QuestPlayerData qd = QuestUtil.getData(p);
		qd.reachLocation(loc);
	}
}
