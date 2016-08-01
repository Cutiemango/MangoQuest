package me.Cutiemango.MangoQuest.questobjects;

import org.bukkit.entity.EntityType;

import me.Cutiemango.MangoQuest.QuestUtil;
import net.md_5.bungee.api.chat.TextComponent;

public class QuestObjectKillMob extends NumerableObject{
	
	public QuestObjectKillMob(EntityType t, int i, String customname){
		type = t;
		amount = i;
		if (customname != null)
			CustomName = customname;
	}
	
	private EntityType type;
	private String CustomName;
	
	public EntityType getType(){
		return type;
	}

	public boolean hasCustomName(){
		return !(CustomName == null);
	}
	
	public String getCustomName(){
		return CustomName;
	}

	@Override
	public TextComponent toTextComponent(boolean isFinished) {
		TextComponent text = new TextComponent();
		if (isFinished)
			text = new TextComponent(QuestUtil.translateColor("&8&m&o殺死 " + amount + " &8&m&o個 "));
		else
			text = new TextComponent(QuestUtil.translateColor("&0殺死 " + amount + " &0個 "));
		if (CustomName != null)
			text.addExtra(QuestUtil.translateColor(CustomName));
		else
			text.addExtra(QuestUtil.translate(type));
		return text;
	}

	@Override
	public String toPlainText() {
		if (CustomName != null)
			return QuestUtil.translateColor("&a殺死 " + amount + " &a個 " + QuestUtil.translateColor(CustomName));
		else
			return QuestUtil.translateColor("&a殺死 " + amount + " &a個 " + QuestUtil.translate(type));
	}

}
