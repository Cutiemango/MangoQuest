package me.Cutiemango.MangoQuest.questobjects;

import org.bukkit.inventory.ItemStack;
import me.Cutiemango.MangoQuest.QuestUtil;
import me.Cutiemango.MangoQuest.I18n;
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
	public void setAmount(int i)
	{
		item.setAmount(i);
		amount = i;
	}

	@Override
	public TextComponent toTextComponent(boolean isFinished)
	{
		return super.toTextComponent(I18n.locMsg("QuestObject.ConsumeItem"), isFinished, amount, item);
	}

	@Override
	public String toPlainText()
	{
		if (item.getItemMeta().hasDisplayName())
			return I18n.locMsg("QuestObject.ConsumeItem", Integer.toString(amount), item.getItemMeta().getDisplayName());
		else
			return I18n.locMsg("QuestObject.ConsumeItem", Integer.toString(amount),
					QuestUtil.translate(item.getType(), item.getDurability()));
	}
	
	@Override
	public String toDisplayText()
	{
		if (item.getItemMeta().hasDisplayName())
			return I18n.locMsg("QuestObject.FinishMessage.ConsumeItem", Integer.toString(amount), item.getItemMeta().getDisplayName());
		else
			return I18n.locMsg("QuestObject.FinishMessage.ConsumeItem", Integer.toString(amount),
					QuestUtil.translate(item.getType(), item.getDurability()));
	}

}
