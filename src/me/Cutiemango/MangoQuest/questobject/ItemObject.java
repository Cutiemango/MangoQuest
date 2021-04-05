package me.Cutiemango.MangoQuest.questobject;

import me.Cutiemango.MangoQuest.I18n;
import me.Cutiemango.MangoQuest.editor.EditorListenerObject;
import me.Cutiemango.MangoQuest.editor.EditorListenerObject.ListeningType;
import me.Cutiemango.MangoQuest.manager.QuestBookGUIManager;
import me.Cutiemango.MangoQuest.manager.QuestChatManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class ItemObject extends NumerableObject
{
	protected ItemStack item;

	public ItemStack getItem() {
		return item;
	}

	public void setItem(ItemStack is) {
		item = is;
	}

	@Override
	public boolean receiveCommandInput(Player sender, String type, String obj) {
		if (type.equals("item")) {
			ItemStack item = sender.getInventory().getItemInMainHand();
			if (item == null || item.getType() == Material.AIR) {
				QuestChatManager.info(sender, I18n.locMsg("EditorMessage.ItemInHand"));
				return false;
			} else {
				setItem(item);
				QuestChatManager.info(sender, I18n.locMsg("EditorMessage.ItemRegistered"));
				return true;
			}
		}
		return super.receiveCommandInput(sender, type, obj);
	}

	@Override
	public EditorListenerObject createCommandOutput(Player sender, String command, String type) {
		EditorListenerObject obj;
		if (type.equals("item")) {
			obj = new EditorListenerObject(ListeningType.ITEM, command, null);
			QuestBookGUIManager.openInfo(sender, I18n.locMsg("EditorMessage.RightClick"));
			return obj;
		}
		return super.createCommandOutput(sender, command, type);
	}

}
