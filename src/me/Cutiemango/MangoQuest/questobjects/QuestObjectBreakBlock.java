package me.Cutiemango.MangoQuest.questobjects;

import org.bukkit.Bukkit;
import org.bukkit.Material;

import me.Cutiemango.MangoQuest.QuestUtil;
import net.md_5.bungee.api.chat.TextComponent;

public class QuestObjectBreakBlock extends NumerableObject{
	
	public QuestObjectBreakBlock(Material m, short s, int i){
		if (!m.isBlock()){
			Bukkit.getLogger().warning("ERROR: argument of QuestObjectBreakBlock does not have a matched Material Object.");
			return;
		}
		subID = s;
		block = m;
		amount = i;
		config = "BREAK_BLOCK";
	}
	
	private Material block;
	private short subID = 0;

	@Override
	public TextComponent toTextComponent(boolean isFinished) {
		return super.toTextComponent("挖掘 %0 個 %1%2", isFinished, amount, block, subID);
	}

	@Override
	public String toPlainText() {
		return QuestUtil.translateColor("&a挖掘 " + amount + " &a個 " + QuestUtil.translate(block, subID));
	}
	
	public Material getType(){
		return block;
	}
	
	public short getShort(){
		return subID;
	}
	
	public void setType(Material m){
		block = m;
	}
	
	public void setSubID(short s){
		subID = s;
	}
}
