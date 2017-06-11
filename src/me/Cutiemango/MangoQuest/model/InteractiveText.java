package me.Cutiemango.MangoQuest.model;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import me.Cutiemango.MangoQuest.TextComponentFactory;
import me.Cutiemango.MangoQuest.manager.QuestChatManager;
import net.md_5.bungee.api.chat.TextComponent;

public class InteractiveText
{

	public InteractiveText(String text)
	{
		target = new TextComponent(QuestChatManager.translateColor(text));
	}

	private TextComponent target;
	private boolean hasClickCmd = false;
	private boolean hasShowText = false;
	private boolean hasShowItem = false;
	private ItemStack itemToShow = null;
	private String command = "";
	private String textToShow = "";

	public InteractiveText clickCommand(String cmd)
	{
		command = cmd;
		if (!hasClickCmd)
			TextComponentFactory.regClickCmdEvent(target, cmd);
		hasClickCmd = true;
		return this;
	}

	public InteractiveText showItem(ItemStack item)
	{
		itemToShow = item.clone();
		if (!hasShowItem && itemToShow != null)
			target.addExtra(TextComponentFactory.convertItemHoverEvent(itemToShow, false));
		hasShowItem = true;
		return this;
	}

	public InteractiveText showText(String text)
	{
		textToShow = text;
		if (!hasShowText)
			TextComponentFactory.regHoverEvent(target, text);
		hasShowText = true;
		return this;
	}
	
	public TextComponent toggleAlignText(String s){
		TextComponent text = new TextComponent(QuestChatManager.translateColor(s));
		if (hasClickCmd)
			TextComponentFactory.regClickCmdEvent(text, command);
		if (hasShowText)
			TextComponentFactory.regHoverEvent(text, textToShow);
		if (hasShowItem)
		{
			ItemStack item = itemToShow.clone();
			ItemMeta im = item.getItemMeta();
			im.setDisplayName(s);
			item.setItemMeta(im);
			text = TextComponentFactory.convertItemHoverEvent(item, false);
		}
		return text;
	}

	public TextComponent get()
	{
		return target;
	}

}
