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
		if (value instanceof String)
			value = QuestUtil.translateColor((String)value);
	}
	
	public QuestTrigger(TriggerType type, TriggerObject obj, int i, Object arg){
		if (!type.hasStage())
			QuestUtil.warnCmd("ERROR: " + type.toString() + " should not use this constructor.");
		
		t = type;
		count = i;
		o = obj;
		value = arg;
		if (value instanceof String)
			value = QuestUtil.translateColor((String)value);
	}
	
	public enum TriggerType{
		TRIGGER_ON_TAKE("接受時觸發", false), TRIGGER_ON_QUIT("放棄時觸發", false), TRIGGER_ON_FINISH("完成時觸發", false),
		TRIGGER_STAGE_START("第N階段開始時觸發", true), TRIGGER_STAGE_FINISH("第N階段完成時觸發", true);
		
		private String name;
		private boolean hasStage;
		
		TriggerType(String s, boolean b){
			name = s;
			hasStage = b;
		}
		
		public String toCustomString(){
			return name;
		}
		
		public boolean hasStage(){
			return hasStage;
		}
		
		public String toCustomString(int i){
			return name.replace("N", Integer.toString(i));
		}
	}
	
	public enum TriggerObject{
		COMMAND("指令"),
		SEND_TITLE("發送標題"), SEND_SUBTITLE("發送副標題"), SEND_MESSAGE("發送訊息"),
		TELEPORT("傳送玩家");
		
		private String name;
		
		TriggerObject(String s){
			name = s;
		}
		
		public String toCustomString(){
			return name;
		}
	}
	
	public void trigger(Player p){
		String replaced = "";
		Location loc = p.getLocation();
		if (value instanceof CharSequence){
			replaced = ((String)value).replace("<player>", p.getName());
			replaced = QuestUtil.translateColor(replaced);
		}
		else{
			QuestUtil.warnCmd("ERROR: " + t.toString() + " does not have a matched Object value.");
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
	
	public TriggerObject getTriggerObject(){
		return o;
	}
	
	public Object getObject(){
		return value;
	}

}
