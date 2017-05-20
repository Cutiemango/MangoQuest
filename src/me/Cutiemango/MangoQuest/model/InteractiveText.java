package me.Cutiemango.MangoQuest.model;

import org.bukkit.inventory.ItemStack;
import me.Cutiemango.MangoQuest.QuestUtil;
import me.Cutiemango.MangoQuest.TextComponentFactory;
import net.md_5.bungee.api.chat.TextComponent;

public class InteractiveText
{

	public InteractiveText(String text)
	{
		target = new TextComponent(QuestUtil.translateColor(text));
	}

	private TextComponent target;
	private boolean hasClickCmd;
	private boolean hasShowText;
	private boolean hasShowItem;

	public InteractiveText clickCommand(String command)
	{
		if (!hasClickCmd)
			TextComponentFactory.regClickCmdEvent(target, command);
		return this;
	}

	public InteractiveText showItem(ItemStack item)
	{
		if (!hasShowItem)
			target.addExtra(TextComponentFactory.convertItemHoverEvent(item, false));
		return this;
	}

	public InteractiveText showText(String text)
	{
		if (!hasShowText)
			TextComponentFactory.regHoverEvent(target, text);
		return this;
	}

	public TextComponent get()
	{
		return target;
	}

}
