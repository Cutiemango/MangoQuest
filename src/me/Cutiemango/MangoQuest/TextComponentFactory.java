package me.Cutiemango.MangoQuest;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import me.Cutiemango.MangoQuest.data.QuestPlayerData;
import me.Cutiemango.MangoQuest.manager.QuestChatManager;
import me.Cutiemango.MangoQuest.model.Quest;
import me.Cutiemango.MangoQuest.model.Quest.FailResult;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class TextComponentFactory
{

	// Note:
	// The command argument here is "/" needed.

	public static TextComponent regClickCmdEvent(String text, String command)
	{
		TextComponent t = new TextComponent(QuestChatManager.translateColor(text));
		t.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
		return t;
	}

	public static TextComponent regClickCmdEvent(TextComponent t, String command)
	{
		t.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
		return t;
	}

	public static TextComponent regHoverEvent(String text, String s)
	{
		TextComponent t = new TextComponent(QuestChatManager.translateColor(text));
		t.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]
		{ new TextComponent(QuestChatManager.translateColor(s)) }));
		return t;
	}

	public static TextComponent regHoverEvent(TextComponent t, String s)
	{
		t.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]
		{ new TextComponent(QuestChatManager.translateColor(s)) }));
		return t;
	}

	public static TextComponent regChangePageEvent(String text, Integer page)
	{
		TextComponent t = new TextComponent(QuestChatManager.translateColor(text));
		t.setClickEvent(new ClickEvent(ClickEvent.Action.CHANGE_PAGE, page.toString()));
		return t;
	}

	public static TextComponent convertItemHoverEvent(ItemStack it, boolean isFinished)
	{
		return Main.instance.handler.textFactoryConvertItem(it, isFinished);
	}

	public static TextComponent convertLocHoverEvent(String name, Location loc, boolean isFinished)
	{
		return Main.instance.handler.textFactoryConvertLocation(name, loc, isFinished);
	}

	public static TextComponent convertViewQuest(Quest q)
	{
		TextComponent t = new TextComponent(ChatColor.BOLD + q.getQuestName());
		t.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]
		{ new TextComponent(QuestChatManager.translateColor("&e點擊以查看 " + q.getQuestName() + " &e的詳細資料")) }));
		t.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mq quest view " + q.getInternalID()));
		return t;
	}

	public static TextComponent convertRequirement(QuestPlayerData qd, Quest q)
	{
		TextComponent text = new TextComponent(QuestChatManager.translateColor("&7&o" + q.getQuestName()));
		if (!q.isRedoable() && qd.hasFinished(q))
			return regHoverEvent(text, "&c此為一次性任務。");
		else
			if (qd.hasFinished(q))
			{
				long d = qd.getDelay(qd.getFinishData(q).getLastFinish(), q.getRedoDelay());
				if (d > 0)
					return regHoverEvent(text, "&c你必須再等待 " + QuestUtil.convertTime(d) + " 才能再度接取這個任務。");
			}
			else
				if (q.hasRequirement())
				{
					FailResult f = q.meetRequirementWith(qd.getPlayer());
					if (!f.succeed())
						return regHoverEvent(text, f.getMessage());
					else
						return convertViewQuest(q);
				}
		return text;
	}

}
