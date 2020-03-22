package me.Cutiemango.MangoQuest.questobject.objects;

import me.Cutiemango.MangoQuest.I18n;
import me.Cutiemango.MangoQuest.QuestIO;
import me.Cutiemango.MangoQuest.QuestUtil;
import me.Cutiemango.MangoQuest.book.InteractiveText;
import me.Cutiemango.MangoQuest.book.QuestBookPage;
import me.Cutiemango.MangoQuest.editor.EditorListenerObject;
import me.Cutiemango.MangoQuest.questobject.ItemObject;
import me.Cutiemango.MangoQuest.questobject.interfaces.EditorObject;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class QuestObjectConsumeItem extends ItemObject implements EditorObject
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
					QuestUtil.translate(item.getType()));
	}

	@Override
	public void formatEditorPage(QuestBookPage page, int stage, int obj)
	{
		page.add(I18n.locMsg("QuestEditor.ConsumeItem")).endNormally();
		page.add(new InteractiveText("").showItem(item)).endNormally();
		page.add(new InteractiveText(I18n.locMsg("QuestEditor.Edit")).clickCommand("/mq e edit object " + stage + " " + obj + " item")).changeLine();
		super.formatEditorPage(page, stage, obj);
	}
	
	@Override
	public boolean load(QuestIO config, String path)
	{
		item = config.getItemStack(path + "Item");
		amount = item.getAmount();
		return super.load(config, path);
	}

	@Override
	public void save(QuestIO config, String objpath)
	{
		config.set(objpath + "Item", item);
		super.save(config, objpath);
	}

	@Override
	public boolean receiveCommandInput(Player sender, String type, String obj)
	{
		return super.receiveCommandInput(sender, type, obj);
	}
	@Override
	public EditorListenerObject createCommandOutput(Player sender, String command, String type)
	{
		return super.createCommandOutput(sender, command, type);
	}

}
