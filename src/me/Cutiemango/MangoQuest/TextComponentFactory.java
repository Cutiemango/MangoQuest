package me.Cutiemango.MangoQuest;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import me.Cutiemango.MangoQuest.model.Quest;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class TextComponentFactory {
	
	//Note:
	//The command argument here is "/" needed.
	
	public static TextComponent regClickCmdEvent(String text, String command){
		TextComponent t = new TextComponent(QuestUtil.translateColor(text));
		t.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
		return t;
	}
	
	public static TextComponent regClickCmdEvent(TextComponent t, String command){
		t.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
		return t;
	}
	
	public static TextComponent regHoverEvent(String text, String s){
		TextComponent t = new TextComponent(QuestUtil.translateColor(text));
		t.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,  new BaseComponent[]{new TextComponent(
				QuestUtil.translateColor(s))}));
		return t;
	}
	
	public static TextComponent regHoverEvent(TextComponent t, String s){
		t.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,  new BaseComponent[]{new TextComponent(
				QuestUtil.translateColor(s))}));
		return t;
	}
	
	public static TextComponent regChangePageEvent(String text, Integer page){
		TextComponent t = new TextComponent(QuestUtil.translateColor(text));
		t.setClickEvent(new ClickEvent(ClickEvent.Action.CHANGE_PAGE, page.toString()));
		return t;
	}
	
	public static TextComponent convertItemHoverEvent(ItemStack it, boolean f) {
		return Main.instance.handler.textFactoryConvertItem(it, f);
	}
	
	public static TextComponent convertLocHoverEvent(String name, Location loc, boolean isFinished){
		return Main.instance.handler.textFactoryConvertLocation(name, loc, isFinished);
	}
	
	public static TextComponent convertViewQuest(Quest q){
		TextComponent t = new TextComponent(ChatColor.BOLD + q.getQuestName());
		t.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,  new BaseComponent[]{new TextComponent(QuestUtil.translateColor("&e點擊以查看 " + q.getQuestName() + " &e的詳細資料"))}));
		t.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mq view " + q.getInternalID()));
		return t;
	}

}
