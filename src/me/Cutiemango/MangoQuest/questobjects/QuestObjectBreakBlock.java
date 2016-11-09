package me.Cutiemango.MangoQuest.questobjects;

import org.bukkit.Bukkit;
import org.bukkit.Material;

import me.Cutiemango.MangoQuest.QuestUtil;
import net.md_5.bungee.api.chat.TextComponent;

public class QuestObjectBreakBlock extends NumerableObject{
	
	public QuestObjectBreakBlock(Material m, int i){
		if (!m.isBlock()){
			Bukkit.getLogger().warning("ERROR: argument of QuestObjectBreakBlock does not have a matched Material Object.");
			return;
		}
		block = m;
		amount = i;
		config = "BREAK_BLOCK";
	}
	
	private Material block;

	@Override
	public TextComponent toTextComponent(boolean isFinished) {
		return super.toTextComponent("挖掘 %0 個 %1", isFinished, amount, block);
	}

	@Override
	public String toPlainText() {
		return QuestUtil.translateColor("&a挖掘 " + amount + " &a個 " + QuestUtil.translate(block, (byte)0));
	}
	
	public Material getType(){
		return block;
	}
}
