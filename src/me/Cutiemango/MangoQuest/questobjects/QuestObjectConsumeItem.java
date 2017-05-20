package me.Cutiemango.MangoQuest.questobjects;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import me.Cutiemango.MangoQuest.QuestUtil;
import me.Cutiemango.MangoQuest.Questi18n;
import net.md_5.bungee.api.chat.TextComponent;

public class QuestObjectConsumeItem extends ItemObject
{

	public QuestObjectConsumeItem(ItemStack is, int i)
	{
		item = is;
		amount = i;
		config = "CONSUME_ITEM";
	}

	@Override
	public TextComponent toTextComponent(boolean isFinished)
	{
		return super.toTextComponent(Questi18n.localizeMessage("QuestObject.ConsumeItem"), isFinished, amount, item);
	}

	@Override
	public String toPlainText()
	{
		if (item.getItemMeta().hasDisplayName())
			return ChatColor.GREEN
					+ Questi18n.localizeMessage("QuestObject.ConsumeItem", Integer.toString(amount), item.getItemMeta().getDisplayName());
		else
			return ChatColor.GREEN + Questi18n.localizeMessage("QuestObject.ConsumeItem", Integer.toString(amount),
					QuestUtil.translate(item.getType(), item.getDurability()));
	}

}
