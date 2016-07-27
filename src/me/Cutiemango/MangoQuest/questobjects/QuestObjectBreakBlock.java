package me.Cutiemango.MangoQuest.questobjects;

import org.bukkit.Bukkit;
import org.bukkit.Material;

import me.Cutiemango.MangoQuest.QuestUtil;
import net.md_5.bungee.api.chat.TextComponent;

public class QuestObjectBreakBlock extends SimpleQuestObject{
	
	public QuestObjectBreakBlock(Material m, int i){
		if (!m.isBlock()){
			Bukkit.getLogger().warning("ERROR: argument of QuestObjectBreakBlock does not have a matched Material Object.");
			return;
		}
		block = m;
		amount = i;
	}
	
	private Material block;
	private int amount;

	@Override
	public TextComponent toTextComponent(boolean isFinished) {
		TextComponent text = new TextComponent();
		if (isFinished)
			text = new TextComponent(QuestUtil.translateColor("&8&m&o«õ±¸ " + amount + " &8&m&o­Ó "));
		else
			text = new TextComponent(QuestUtil.translateColor("&0«õ±¸ " + amount + " &0­Ó "));
		text.addExtra(QuestUtil.translate(block));
		return text;
	}

	@Override
	public String toPlainText() {
		return QuestUtil.translateColor("&a«õ±¸ " + amount + " &a­Ó " + QuestUtil.translate(block));
	}
	
	public Material getType(){
		return block;
	}
	
	public int getAmount(){
		return amount;
	}
}
