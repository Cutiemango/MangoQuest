package me.Cutiemango.MangoQuest.model;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import me.Cutiemango.MangoQuest.QuestUtil;

public class QuestTrigger {
	
	TriggerType t;
	TriggerObject o;
	Object value;
	int count;
	
	public QuestTrigger(TriggerType type, TriggerObject obj, Object arg){
		t = type;
		o = obj;
		value = arg;
	}
	
	public QuestTrigger(TriggerType type, TriggerObject obj, int i, Object arg){
		if (!type.equals(TriggerType.TRIGGER_STAGE_START) && !type.equals(TriggerType.TRIGGER_STAGE_FINISH)){
			Bukkit.getLogger().warning("解析任務事件時出現錯誤。");
			Bukkit.getLogger().warning("ERROR: " + type.toString() + " should not use this constructor.");
		}
		t = type;
		count = i;
		o = obj;
		value = arg;
	}
	
	public enum TriggerType{
		TRIGGER_ON_TAKE, TRIGGER_ON_QUIT, TRIGGER_ON_FINISH,
		TRIGGER_STAGE_START, TRIGGER_STAGE_FINISH;
	}
	
	public enum TriggerObject{
		COMMAND, SEND_TITLE, SEND_SUBTITLE, SEND_MESSAGE,
		TELEPORT;
	}
	
	public void trigger(Player p){
		String replaced = "";
		Location loc = p.getLocation();
		if (value instanceof CharSequence){
			replaced = ((String)value).replace("<player>", p.getName());
			replaced = QuestUtil.translateColor(replaced);
		}
		else{
			Bukkit.getLogger().severe("觸發任務事件時出現錯誤。");
			Bukkit.getLogger().severe("ERROR: " + t.toString() + " does not have a matched Object value.");
		}
		switch(o){
		case COMMAND:
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), replaced);
			break;
		case SEND_TITLE:
			QuestUtil.sendTitle(p, 5, 5, 5, replaced, null);
			break;
		case SEND_SUBTITLE:
			QuestUtil.sendTitle(p, 5, 5, 5, null, replaced);
			break;
		case SEND_MESSAGE:
			p.sendMessage(replaced);
			break;
		case TELEPORT:
			String[] splited = ((String)value).split(":");
			loc = new Location(
					Bukkit.getWorld(splited[0]),
					Double.parseDouble(splited[1]),
					Double.parseDouble(splited[2]),
					Double.parseDouble(splited[3]));
			p.teleport(loc);
			break;
		}
		
	}
	
	public TriggerType getType(){
		return t;
	}
	
	public int getCount(){
		return count;
	}

}
