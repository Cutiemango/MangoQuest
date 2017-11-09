package me.Cutiemango.MangoQuest.manager;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import me.Cutiemango.MangoQuest.I18n;
import me.Cutiemango.MangoQuest.conversation.FriendConversation;
import me.Cutiemango.MangoQuest.conversation.QuestConversation;
import me.Cutiemango.MangoQuest.conversation.StartTriggerConversation;
import me.Cutiemango.MangoQuest.model.Quest;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;

public class QuestValidater
{
	
	public static boolean isWorld(String s)
	{
		return s != null && Bukkit.getWorld(s) != null;
	}
	
	public static boolean validateNPC(CommandSender sender, String id, boolean msg)
	{
		NPC npc = CitizensAPI.getNPCRegistry().getById(Integer.parseInt(id));
		boolean result = npc != null;
		if (msg && !result)
			QuestChatManager.error(sender, I18n.locMsg("CommandInfo.InvalidArgument"));
		return result;
	}

	public static boolean validateInteger(CommandSender sender, String number, boolean msg)
	{
		try
		{
			Integer.parseInt(number);
		}
		catch (NumberFormatException | NullPointerException e)
		{
			QuestChatManager.error(sender, I18n.locMsg("CommandInfo.InvalidArgument"));
			return false;
		}
		return true;
	}
	
	public static boolean detailedValidate(Quest before, Quest after)
	{
		if (before == null || after == null)
			return false;
		if (!before.getInternalID().equals(after.getInternalID()))
			return false;
		if (!before.getQuestName().equals(after.getQuestName()))
			return false;
		if (!(before.isCommandQuest() == after.isCommandQuest()))
			return false;
		if (!before.isCommandQuest())
		{
			if (!(before.getQuestNPC().getId() == after.getQuestNPC().getId()))
				return false;
		}
		if (!before.getQuestOutline().equals(after.getQuestOutline()))
			return false;
		if (!before.getFailMessage().equals(after.getFailMessage()))
			return false;
		if (!before.getQuestReward().equals(after.getQuestReward()))
			return false;
		if (!(before.isRedoable() == after.isRedoable()))
			return false;
		if (before.isRedoable())
		{
			if (!(before.getRedoDelay() == after.getRedoDelay()))
				return false;
		}
		if (!before.getRequirements().equals(after.getRequirements()))
			return false;
		if (!before.getStages().equals(after.getStages()))
			return false;
		if (!before.getTriggerMap().equals(after.getTriggerMap()))
			return false;
		return true;
	}
	
	public static boolean detailedValidate(QuestConversation before, QuestConversation after)
	{
		if (before == null || after == null)
			return false;
		if (!before.getInternalID().equals(after.getInternalID()))
			return false;
		if (!before.getName().equals(after.getName()))
			return false;
		if (!(before.getNPC().getId() == after.getNPC().getId()))
			return false;
		if (!before.getActions().equals(after.getActions()))
			return false;
		if (!(before instanceof FriendConversation && after instanceof FriendConversation))
			return false;
		if (before instanceof FriendConversation && after instanceof FriendConversation)
			if (!(((FriendConversation)before).getReqPoint() == ((FriendConversation)after).getReqPoint()))
				return false;
		if (!(before instanceof StartTriggerConversation && after instanceof StartTriggerConversation))
			return false;
		if (before instanceof StartTriggerConversation && after instanceof StartTriggerConversation)
			if (!((StartTriggerConversation)before).equals(((StartTriggerConversation)after).getQuest()))
				return false;
		return true;
	}

	public static boolean weakValidate(Quest before, Quest after)
	{
		if (before == null || after == null)
			return false;
		if (!before.getInternalID().equals(after.getInternalID()))
			return false;
		if (!before.getQuestName().equals(after.getQuestName()))
			return false;
		return true;
	}
	
	public static boolean compareItem(ItemStack one, ItemStack two, boolean ignoreAmount)
	{
		if (one == null && two != null || one != null && two == null)
			return false;
		if (one == null && two == null)
			return true;
		if (one.getType().name() != two.getType().name())
			return false;
		else
			if ((one.getAmount() != two.getAmount()) && ignoreAmount == false)
				return false;
			else
				if (one.getData().equals(two.getData()) == false)
					return false;
		if (one.hasItemMeta() || two.hasItemMeta())
			if (one.hasItemMeta() != two.hasItemMeta())
				return false;
			else
				if (one.getItemMeta().hasDisplayName() != two.getItemMeta().hasDisplayName())
					return false;
				else
					if (one.getItemMeta().hasLore() != two.getItemMeta().hasLore())
						return false;
					else
					{
						if (one.getItemMeta().hasDisplayName() && two.getItemMeta().hasDisplayName())
						{
							if (!one.getItemMeta().getDisplayName().equals(two.getItemMeta().getDisplayName()))
								return false;
						}
						else
							if (one.getItemMeta().hasLore() && two.getItemMeta().hasLore())
							{
								if (!one.getItemMeta().getLore().equals(two.getItemMeta().getLore()))
									return false;
							}
					}
		if (one.getEnchantments().equals(two.getEnchantments()) == false)
			return false;
		else
			return true;
	}
}
