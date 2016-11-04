package me.Cutiemango.MangoQuest.questobjects;

import org.bukkit.inventory.ItemStack;

import me.Cutiemango.MangoQuest.QuestUtil;
import net.md_5.bungee.api.chat.TextComponent;

public class QuestObjectItemConsume extends NumerableObject{
	
	public QuestObjectItemConsume(ItemStack is, int i){
		item = is;
		amount = i;
		config = "CONSUME_ITEM";
	}
	
	private ItemStack item;

	@Override
	public TextComponent toTextComponent(boolean isFinished) {
		return super.toTextComponent("食用 %0 個 %1", isFinished, amount, item);
	}

	@Override
	public String toPlainText() {
		if (item.getItemMeta().hasDisplayName())
			return QuestUtil.translateColor("&a食用 " + amount + " 個 " + item.getItemMeta().getDisplayName());
		else
			return QuestUtil.translateColor("&a食用 " + amount + " 個 " + QuestUtil.translate(item.getType(), item.getDurability()));
	}
	
	public ItemStack getItem(){
		return item;
	}

}
