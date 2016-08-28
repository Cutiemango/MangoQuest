package me.Cutiemango.MangoQuest.questobjects;

import org.bukkit.inventory.ItemStack;

import me.Cutiemango.MangoQuest.QuestUtil;
import me.Cutiemango.MangoQuest.TextComponentFactory;
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
		TextComponent text = new TextComponent();
		if (isFinished){
			text = new TextComponent(QuestUtil.translateColor("&8&m&o使用 "));
			text.addExtra(QuestUtil.translateColor(amount + " &8&m&o個 "));
			text.addExtra(TextComponentFactory.convertItemStacktoHoverEvent(item, true));
			return text;
		}
		else{
			text = new TextComponent(QuestUtil.translateColor("&0使用 "));
			text.addExtra(QuestUtil.translateColor(amount + " &0個 "));
			text.addExtra(TextComponentFactory.convertItemStacktoHoverEvent(item, false));
			return text;
		}
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
