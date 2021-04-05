package me.Cutiemango.MangoQuest.manager;

import io.lumine.xikage.mythicmobs.mobs.MythicMob;
import me.Cutiemango.MangoQuest.DebugHandler;
import me.Cutiemango.MangoQuest.Main;
import me.Cutiemango.MangoQuest.conversation.FriendConversation;
import me.Cutiemango.MangoQuest.conversation.QuestConversation;
import me.Cutiemango.MangoQuest.conversation.StartTriggerConversation;
import me.Cutiemango.MangoQuest.model.Quest;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class QuestValidater
{
	public static class WrappedWeakItem
	{
		public WrappedWeakItem(ItemStack item) {
			type = item.getType();
			if (item.getItemMeta() != null) {
				hasItemMeta = true;
				displayName = item.getItemMeta().getDisplayName();
				lore = Optional.ofNullable(item.getItemMeta().getLore());
			}
		}

		private Material type;
		private boolean hasItemMeta;
		private String displayName = "";
		private Optional<List<String>> lore = Optional.empty();

		public Material getType() {
			return type;
		}

		public boolean hasItemMeta() {
			return hasItemMeta;
		}

		public String getDisplayName() {
			return displayName;
		}

		public boolean hasDisplayName() {
			return !displayName.equals("");
		}

		public Optional<List<String>> getLore() {
			return lore;
		}

		@Override
		public int hashCode() {
			return Objects.hash(type, hasItemMeta, displayName, lore);
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof WrappedWeakItem))
				return false;
			WrappedWeakItem other = (WrappedWeakItem) obj;
			if (type != other.getType() || hasItemMeta != other.hasItemMeta())
				return false;
			if (hasItemMeta) {
				if (!displayName.equals(other.getDisplayName()))
					return false;
				return lore.equals(other.getLore());
			}
			return true;
		}

	}

	public static boolean isWorld(String s) {
		return s != null && Bukkit.getWorld(s) != null;
	}

	public static boolean weakItemCheck(ItemStack original, ItemStack compare) {
		if (original.getType().equals(compare.getType())) {
			Optional<ItemMeta> oriMeta = Optional.ofNullable(original.getItemMeta()), cmpMeta = Optional.ofNullable(compare.getItemMeta());
			if (oriMeta.isPresent() == cmpMeta.isPresent()) {
				if (oriMeta.isPresent()) {
					ItemMeta im = oriMeta.get(), mm = cmpMeta.get();

					// Optional.equals(): The other object is considered equal if:
					// it is also an Optional,
					// and both instances have no value present,
					// or the present values are "equal to" each other via equals().
					Optional<String> oriName = Optional.ofNullable(im.getDisplayName()), cmpName = Optional.ofNullable(mm.getDisplayName());
					// Name check
					if (!oriName.equals(cmpName)) {
						DebugHandler.log(5, "[ItemCheck] Item displayName mismatch.");
						return false;
					}

					// List.equals() returns true if and only if:
					// The specified object is also a list,
					// both lists have the same size,
					// and all corresponding pairs of elements in the two lists are equal.
					Optional<List<String>> oriLore = Optional.ofNullable(im.getLore()), cmpLore = Optional.ofNullable(mm.getLore());

					// Lore check
					if (!oriLore.equals(cmpLore)) {
						DebugHandler.log(5, "[ItemCheck] Item lore mismatch.");
						return false;
					}
				}
				return true;
			}
			DebugHandler.log(5, "[ItemCheck] ItemMeta mismatch.");
			return false;
		}
		DebugHandler.log(5, "[ItemCheck] Type is not correct.");
		return false;
	}

	public static boolean validateMythicMob(String id) {
		if (!Main.getHooker().hasMythicMobEnabled())
			return false;
		MythicMob m = Main.getHooker().getMythicMob(id);
		return m != null;
	}

	public static boolean validateNPC(String id) {
		if (!validateInteger(id))
			return false;
		NPC npc = Main.getHooker().getNPC(id);
		return npc != null;
	}

	public static boolean validateInteger(String number) {
		try {
			Integer.parseInt(number);
		}
		catch (NumberFormatException | NullPointerException e) {
			return false;
		}
		return true;
	}

	public static boolean detailedValidate(Quest before, Quest after) {
		if (before == null || after == null)
			return false;
		if (!before.getInternalID().equals(after.getInternalID()))
			return false;
		if (!before.getQuestName().equals(after.getQuestName()))
			return false;
		if (!(before.isCommandQuest() == after.isCommandQuest()))
			return false;
		if (!before.isCommandQuest()) {
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
		if (before.isRedoable()) {
			if (!(before.getRedoDelay() == after.getRedoDelay()))
				return false;
		}
		if (!before.getRequirements().equals(after.getRequirements()))
			return false;
		if (!before.getStages().equals(after.getStages()))
			return false;
		return before.getTriggerMap().equals(after.getTriggerMap());
	}

	public static boolean detailedValidate(QuestConversation before, QuestConversation after) {
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
		if (before instanceof FriendConversation) {
			if (!(after instanceof FriendConversation)) return false;
			else if (((FriendConversation) before).getReqPoint() != ((FriendConversation) after).getReqPoint()) return false;
		}
		if (before instanceof StartTriggerConversation) {
			if (!(after instanceof StartTriggerConversation)) return false;
			else if (!((StartTriggerConversation) before).getQuest().equals(((StartTriggerConversation) after).getQuest())) return false;
		}
		return true;
	}

	public static boolean weakValidate(Quest before, Quest after) {
		if (before == null || after == null)
			return false;
		if (!before.getInternalID().equals(after.getInternalID()))
			return false;
		return before.getQuestName().equals(after.getQuestName());
	}
}
