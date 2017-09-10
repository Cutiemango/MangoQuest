package me.Cutiemango.MangoQuest.editor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import me.Cutiemango.MangoQuest.Main;
import me.Cutiemango.MangoQuest.QuestUtil;
import me.Cutiemango.MangoQuest.editor.EditorListenerObject.ListeningType;
import me.Cutiemango.MangoQuest.ConfigSettings;
import me.Cutiemango.MangoQuest.I18n;
import me.Cutiemango.MangoQuest.manager.QuestChatManager;
import me.Cutiemango.MangoQuest.model.Quest;
import me.Cutiemango.MangoQuest.model.RequirementType;
import net.citizensnpcs.api.npc.NPC;

public class EditorListenerHandler
{

	public static HashMap<String, EditorListenerObject> CurrentListening = new HashMap<>();

	public static void onChat(final Player p, final String msg, AsyncPlayerChatEvent event)
	{
		if (CurrentListening.containsKey(p.getName()))
		{
			EditorListenerObject obj = CurrentListening.get(p.getName());
			if (msg.contains("cancel"))
				QuestChatManager.info(p, I18n.locMsg("EditorMessage.CancelEntry"));
			else
				QuestChatManager.info(p, I18n.locMsg("EditorMessage.YourEntry", msg));
			obj.execute(p, msg);
			event.setCancelled(true);
			return;
		}
		else
			return;
	}

	public static void onPlayerInteract(Player p, Action act, ItemStack is, Cancellable event)
	{
		if (!preCondiction(p))
			return;
		EditorListenerObject obj = CurrentListening.get(p.getName());
		if (act.equals(Action.RIGHT_CLICK_AIR) || act.equals(Action.RIGHT_CLICK_BLOCK))
		{
			if (is != null && !is.getType().equals(Material.AIR))
			{
				if (obj.getType().equals(ListeningType.ITEM))
				{
					event.setCancelled(true);
					obj.execute(p, "hand");
				}
				else
					return;
			}
		}
		return;
	}

	@SuppressWarnings("deprecation")
	public static void onBlockBreak(Player p, Block b, Cancellable event)
	{
		if (!preCondiction(p))
			return;
		EditorListenerObject obj = CurrentListening.get(p.getName());
		if (obj.getType().equals(ListeningType.BLOCK))
		{
			obj.execute(p, b.getType().toString() + ":" + b.getData());
			event.setCancelled(true);
		}
		else
			return;
	}

	public static void onEntityDamage(Player p, Entity e, Cancellable event)
	{
		if (!preCondiction(p))
			return;
		EditorListenerObject obj = CurrentListening.get(p.getName());
		event.setCancelled(true);
		if (obj.getType().equals(ListeningType.MTMMOB_LEFT_CLICK))
		{
			if (Main.instance.initManager.hasMythicMobEnabled() && Main.instance.initManager.getMythicMobsAPI().isMythicMob(e))
				obj.execute(p, Main.instance.initManager.getMythicMobsAPI().getMythicMobInstance(e).getType().getInternalName());
		}
		else if (obj.getType().equals(ListeningType.MOB_LEFT_CLICK))
			obj.execute(p, e.getType().toString());
		else if (obj.getType().equals(ListeningType.STRING))
		{
			if (e.getCustomName() != null)
				obj.execute(p, e.getCustomName());
			else
				obj.execute(p, QuestUtil.translate(e.getType()));
		}
		return;
	}

	public static void onNPCLeftClick(Player p, NPC npc, Cancellable event)
	{
		if (!preCondiction(p))
		{
			if (ConfigSettings.DEBUG_MODE)
				Main.debug("Player " + p.getName() + " does not have the precondition to edit.");
			return;
		}
		EditorListenerObject obj = CurrentListening.get(p.getName());
		if (obj.getType().equals(ListeningType.NPC_LEFT_CLICK))
		{
			obj.execute(p, Integer.toString(npc.getId()));
			QuestChatManager.info(p, I18n.locMsg("EditorMessage.NPCSelcted", npc.getName()));
		}
		else
		{
			if (ConfigSettings.DEBUG_MODE)
				Main.debug("Object was not NPC_LEFT_CLICK.");
		}
		event.setCancelled(true);
		return;
	}

	public static void onInventoryClose(Player p, Inventory inv)
	{
		if (!preCondiction(p))
			return;
		EditorListenerObject obj = CurrentListening.get(p.getName());
		if (!obj.getType().equals(ListeningType.OPEN_INVENTORY))
			return;
		Quest q = QuestEditorManager.getCurrentEditingQuest(p);
		List<ItemStack> list = new ArrayList<>();
		for (ItemStack is : inv.getContents())
		{
			if (is == null || is.getType().equals(Material.AIR))
				continue;
			else
				list.add(is);
		}
		if (inv.getName().contains("Reward"))
		{
			q.getQuestReward().setItemReward(list);
			QuestEditorManager.editQuest(p);
		}
		else
			if (inv.getName().contains("Requirement"))
			{
				q.getRequirements().put(RequirementType.ITEM, list);
				QuestEditorManager.editQuestRequirement(p);
			}
		obj.execute(p, "item");
		QuestChatManager.info(p, I18n.locMsg("EditorMessage.ItemSaved"));
		return;
	}

	public static void register(Player p, EditorListenerObject obj)
	{
		CurrentListening.put(p.getName(), obj);
	}

	public static boolean preCondiction(Player p)
	{
		return CurrentListening.containsKey(p.getName()) && ((QuestEditorManager.checkEditorMode(p, false) || ConversationEditorManager.checkEditorMode(p, false)));
	}

	@SuppressWarnings("unchecked")
	public static void registerGUI(Player p, String obj)
	{
		if (QuestEditorManager.checkEditorMode(p, false))
		{
			if (obj.equalsIgnoreCase("reward"))
				QuestEditorManager.generateEditItemGUI(p, "Reward", QuestEditorManager.getCurrentEditingQuest(p).getQuestReward().getItems());
			else
				if (obj.equalsIgnoreCase("requirement"))
					QuestEditorManager.generateEditItemGUI(p, "Requirement",
							(List<ItemStack>) QuestEditorManager.getCurrentEditingQuest(p).getRequirements().get(RequirementType.ITEM));
				else
					return;
			CurrentListening.put(p.getName(), new EditorListenerObject(ListeningType.OPEN_INVENTORY, obj));
		}
	}

}
