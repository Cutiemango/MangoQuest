package me.Cutiemango.MangoQuest.book;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import me.Cutiemango.MangoQuest.data.QuestPlayerData;
import me.Cutiemango.MangoQuest.manager.QuestChatManager;
import me.Cutiemango.MangoQuest.model.Quest;
import net.citizensnpcs.api.npc.NPC;
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
	private boolean hasShowNPC = false;
	private boolean hasShowQuest = false;
	private boolean hasShowRequirement = false;
	
	private ItemStack itemToShow = null;
	private String command = "";
	private String textToShow = "";
	private NPC npcToShow;
	private Quest quest;
	private QuestPlayerData data;
	

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
		if (item == null)
			return this;
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
	
	public InteractiveText showNPCInfo(NPC npc)
	{
		npcToShow = npc;
		if (!hasShowNPC && npc != null)
			target.addExtra(TextComponentFactory.convertLocHoverEvent(npc.getName(), npc.getStoredLocation(), false));
		hasShowNPC = true;
		return this;
	}
	
	
	public InteractiveText showQuest(Quest q)
	{
		quest = q;
		if (!hasShowQuest)
			target = TextComponentFactory.convertViewQuest(q);
		hasShowQuest = true;
		return this;
	}
	
	public InteractiveText showRequirement(QuestPlayerData qd, Quest q)
	{
		data = qd;
		quest = q;
		if (!hasShowRequirement)
			target = TextComponentFactory.convertRequirement(qd, q);
		hasShowRequirement = true;
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
			if (!im.hasDisplayName())
				im.setDisplayName(ChatColor.WHITE + ChatColor.stripColor(s));
			else
				im.setDisplayName(s);
			item.setItemMeta(im);
			text = TextComponentFactory.convertItemHoverEvent(item, false);
		}
		if (hasShowNPC)
			text = TextComponentFactory.convertLocHoverEvent(npcToShow.getName(), npcToShow.getStoredLocation(), false);
		else if (hasShowQuest)
			text = TextComponentFactory.convertViewQuest(quest);
		else if (hasShowRequirement)
			text = TextComponentFactory.convertRequirement(data, quest);
		text.setText(QuestChatManager.translateColor(s));
		return text;
	}

	public TextComponent get()
	{
		return target;
	}

}
