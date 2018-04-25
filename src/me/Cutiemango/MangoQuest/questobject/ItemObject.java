package me.Cutiemango.MangoQuest.questobject;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import me.Cutiemango.MangoQuest.I18n;
import me.Cutiemango.MangoQuest.Main;
import me.Cutiemango.MangoQuest.editor.EditorListenerObject;
import me.Cutiemango.MangoQuest.editor.EditorListenerObject.ListeningType;
import me.Cutiemango.MangoQuest.manager.QuestBookGUIManager;
import me.Cutiemango.MangoQuest.manager.QuestChatManager;

public abstract class ItemObject extends NumerableObject
{
	protected ItemStack item;

	public ItemStack getItem()
	{
		return item;
	}

	public void setItem(ItemStack is)
	{
		item = is;
	}
	
	@Override
	public boolean receiveCommandInput(Player sender, String type, String obj)
	{
		switch (type)
		{
			case "item":
				ItemStack item = Main.getInstance().handler.getItemInMainHand(sender);
				if (item == null || item.getType() == Material.AIR)
				{
					QuestChatManager.info(sender, I18n.locMsg("EditorMessage.ItemInHand"));
					return false;
				}
				else
				{
					setItem(item);
					QuestChatManager.info(sender, I18n.locMsg("EditorMessage.ItemRegistered"));
				}
				break;
			default:
				return super.receiveCommandInput(sender, type, obj);
		}
		return true;
	}
	
	@Override
	public EditorListenerObject createCommandOutput(Player sender, String command, String type)
	{
		EditorListenerObject obj = null;
		switch (type)
		{
			case "item":
				obj = new EditorListenerObject(ListeningType.ITEM, command, null);
				QuestBookGUIManager.openInfo(sender, I18n.locMsg("EditorMessage.RightClick"));
				break;
			default:
				return super.createCommandOutput(sender, command, type);
		}
		return obj;
	}

}
