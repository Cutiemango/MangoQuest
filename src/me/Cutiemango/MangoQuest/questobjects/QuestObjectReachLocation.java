package me.Cutiemango.MangoQuest.questobjects;

import org.bukkit.Location;

import me.Cutiemango.MangoQuest.QuestUtil;
import me.Cutiemango.MangoQuest.TextComponentFactory;
import net.md_5.bungee.api.chat.TextComponent;

public class QuestObjectReachLocation extends SimpleQuestObject{
	
	public QuestObjectReachLocation(Location l, int i, String s){
		loc = l;
		radius = i;
		name = QuestUtil.translateColor(s);
	}
	
	private Location loc;
	private int radius;
	private String name;

	@Override
	public TextComponent toTextComponent(boolean isFinished) {
		TextComponent text = new TextComponent();
		if (isFinished)
			text = new TextComponent(QuestUtil.translateColor("&8&m&o到達地點 "));
		else
			text = new TextComponent(QuestUtil.translateColor("&0到達地點 "));
		text.addExtra(TextComponentFactory.convertLocationtoHoverEvent(name, loc, isFinished));
		return text;
	}

	@Override
	public String toPlainText() {
		return QuestUtil.translateColor("&a到達地點 " + name);
	}
	
	public Location getLocation(){
		return loc;
	}
	
	public String getName(){
		return name;
	}
	
	public int getRadius(){
		return radius;
	}

}
