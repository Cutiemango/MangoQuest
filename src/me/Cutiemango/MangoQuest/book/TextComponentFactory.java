package me.Cutiemango.MangoQuest.book;

import me.Cutiemango.MangoQuest.I18n;
import me.Cutiemango.MangoQuest.Main;
import me.Cutiemango.MangoQuest.QuestUtil;
import me.Cutiemango.MangoQuest.data.QuestPlayerData;
import me.Cutiemango.MangoQuest.manager.QuestChatManager;
import me.Cutiemango.MangoQuest.manager.RequirementManager;
import me.Cutiemango.MangoQuest.model.Quest;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

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

	public static void regClickCmdEvent(TextComponent t, String command)
	{
		t.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
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
		return Main.getInstance().handler.textFactoryConvertItem(it, isFinished);
	}

	public static TextComponent convertLocHoverEvent(String name, Location loc, boolean isFinished)
	{
		return Main.getInstance().handler.textFactoryConvertLocation(name, loc, isFinished);
	}

	public static TextComponent convertViewQuest(Quest q)
	{
		if (q == null)
			return new TextComponent(I18n.locMsg("QuestEditor.NotSet"));
		TextComponent t = new TextComponent(QuestChatManager.translateColor("&0&l" + q.getQuestName()));
		t.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]
		{ new TextComponent(I18n.locMsg("QuestJourney.ClickToView", q.getQuestName())) }));
		t.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mq quest view " + q.getInternalID()));
		return t;
	}

	public static TextComponent convertRequirement(QuestPlayerData qd, Quest q)
	{
		TextComponent text = new TextComponent(QuestChatManager.translateColor("&7&o" + q.getQuestName()));
		if (!q.isRedoable() && qd.hasFinished(q))
			return regHoverEvent(text, I18n.locMsg("CommandInfo.NotRedoable"));
		else
			if (qd.hasFinished(q))
			{
				long d = qd.getDelay(qd.getFinishData(q).getLastFinish(), q.getRedoDelay());
				if (d > 0)
					return regHoverEvent(text, I18n.locMsg("QuestJourney.WaitFor", QuestUtil.convertTime(d)));
				if (!qd.hasTakenReward(q))
					return regHoverEvent(text, I18n.locMsg("QuestReward.RewardNotTaken"));
			}
			if (q.hasRequirement())
			{
				Optional<String> msg = RequirementManager.meetRequirementWith(qd.getPlayer(), q.getRequirements(), true);
				if (msg.isPresent())
					return regHoverEvent(text, msg.get());
				else
					return convertViewQuest(q);
			}
		return text;
	}

}
