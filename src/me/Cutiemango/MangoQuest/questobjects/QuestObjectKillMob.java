package me.Cutiemango.MangoQuest.questobjects;

import org.bukkit.entity.EntityType;

import me.Cutiemango.MangoQuest.QuestUtil;
import net.elseland.xikage.MythicMobs.Mobs.MythicMob;
import net.md_5.bungee.api.chat.TextComponent;

public class QuestObjectKillMob extends NumerableObject{
	
	public QuestObjectKillMob(EntityType t, int i, String customname){
		type = t;
		amount = i;
		config = "KILL_MOB";
		CustomName = customname;
	}
	
	public QuestObjectKillMob(MythicMob mmMob, int i){
		mtmMob = mmMob;
		amount = i;
		config = "KILL_MOB";
		type = EntityType.valueOf(mmMob.getEntityType().toUpperCase());
		CustomName = mmMob.getDisplayName();
	}
	
	private EntityType type;
	private String CustomName;
	private MythicMob mtmMob;
	
	public EntityType getType(){
		return type;
	}

	public boolean hasCustomName(){
		return !(CustomName == null);
	}
	
	public String getCustomName(){
		return CustomName;
	}
	
	public MythicMob getMythicMob(){
		return mtmMob;
	}
	
	public void setCustomName(String s){
		CustomName = s;
	}
	
	public void setMythicMob(MythicMob m){
		mtmMob = m;
	}
	
	public void setType(EntityType t){
		type = t;
	}
	
	
	public boolean isMythicObject(){
		return mtmMob != null;
	}

	@Override
	public TextComponent toTextComponent(boolean isFinished) {
		if (hasCustomName())
			return super.toTextComponent("殺死 %0 個 %1", isFinished, amount, CustomName);
		else
			return super.toTextComponent("殺死 %0 個 %1", isFinished, amount, type);
	}

	@Override
	public String toPlainText() {
		if (CustomName != null)
			return QuestUtil.translateColor("&a殺死 " + amount + " &a個 " + QuestUtil.translateColor(CustomName));
		else
			return QuestUtil.translateColor("&a殺死 " + amount + " &a個 " + QuestUtil.translate(type));
	}

}
