package me.Cutiemango.MangoQuest.questobjects;

import org.bukkit.inventory.ItemStack;
import me.Cutiemango.MangoQuest.QuestUtil;
import me.Cutiemango.MangoQuest.book.InteractiveText;
import me.Cutiemango.MangoQuest.book.QuestBookPage;
import me.Cutiemango.MangoQuest.I18n;
import me.Cutiemango.MangoQuest.QuestIO;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;

public class QuestObjectConsumeItem extends ItemObject
{
	// Reserved for initializing with load()
	public QuestObjectConsumeItem(){}

	public QuestObjectConsumeItem(ItemStack is, int i)
	{
		item = is;
		amount = i;
	}
	
	@Override
	public String getConfigString()
	{
		return "CONSUME_ITEM";
	}

	@Override
	public String getObjectName()
	{
		return I18n.locMsg("QuestObjectName.ConsumeItem");
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
		return super.toTextComponent(ChatColor.stripColor(I18n.locMsg("QuestObject.ConsumeItem")), isFinished, amount, item);
	}
	
	@Override
	public String toDisplayText()
	{
		if (item.getItemMeta().hasDisplayName())
			return I18n.locMsg("QuestObject.ConsumeItem", Integer.toString(amount), item.getItemMeta().getDisplayName());
		else
			return I18n.locMsg("QuestObject.ConsumeItem", Integer.toString(amount),
					QuestUtil.translate(item.getType(), item.getDurability()));
	}

	@Override
	public void formatEditorPage(QuestBookPage page, int stage, int obj)
	{
		page.add(I18n.locMsg("QuestEditor.ConsumeItem"));
		page.add(new InteractiveText("").showItem(item)).endNormally();
		page.add(new InteractiveText(I18n.locMsg("QuestEditor.Edit")).clickCommand("/mq e edit object " + stage + " " + obj + " item")).changeLine();
		super.formatEditorPage(page, stage, obj);
	}
	
	@Override
	public boolean load(QuestIO config, String path)
	{
		item = config.getItemStack(path + "Item");
		amount = item.getAmount();
		super.load(config, path);
		return true;
	}

	@Override
	public void save(QuestIO config, String objpath)
	{
		config.set(objpath + "Item", item);
		super.save(config, objpath);
	}

}
