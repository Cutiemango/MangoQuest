package me.Cutiemango.MangoQuest.model;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.Cutiemango.MangoQuest.Main;
import me.Cutiemango.MangoQuest.QuestUtil;
import me.Cutiemango.MangoQuest.data.QuestPlayerData;

public class QuestRequirement {

	RequirementType type;
	Object value;
	
	public QuestRequirement(RequirementType t , Object o){
		type = t;
		switch(t){
		case QUEST:
			if (!(o instanceof String)){
				Bukkit.getLogger().warning("解析任務需求時出現錯誤。");
				Bukkit.getLogger().warning("ERROR: RequirementType.QUEST doesn't have a matched Object String[InternalID].");
			}
			break;
		case ITEM:
			if (!(o instanceof ItemStack)){
				Bukkit.getLogger().warning("解析任務需求時出現錯誤。");
				Bukkit.getLogger().warning("ERROR: RequirementType.ITEM doesn't have a matched Object ItemStack.");
			}
			break;
		default:
			break;
		}
		value = o;
	}
	
	public enum RequirementType{
		LEVEL, MONEY, QUEST, ITEM;
	}
	
	public boolean meetRequirementWith(Player p){
		QuestPlayerData pd = QuestUtil.getData(p);
		switch (type){
		case QUEST:
			if (pd.hasFinished(QuestUtil.getQuest((String)value)))
				return true;
			break;
		case LEVEL:
			if (p.getLevel() > (Integer)value)
				return true;
			break;
		case MONEY:
			if (Main.economy.has(p, (Double)value))
				return true;
			break;
		case ITEM:
			if (p.getInventory().contains((ItemStack)value))
				return true;
			break;
		}
		return false;
	}
	
	public RequirementType getType(){
		return type;
	}

}
