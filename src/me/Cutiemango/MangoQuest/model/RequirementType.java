package me.Cutiemango.MangoQuest.model;

import me.Cutiemango.MangoQuest.Questi18n;

public enum RequirementType{
	LEVEL(Questi18n.localizeMessage("Requirements.Level"), false),
	MONEY(Questi18n.localizeMessage("Requirements.Money"), false),
	QUEST(Questi18n.localizeMessage("Requirements.Quest"), true),
	ITEM(Questi18n.localizeMessage("Requirements.Item"), true),
	SCOREBOARD(Questi18n.localizeMessage("Requirements.Scoreboard"), true),
	NBTTAG(Questi18n.localizeMessage("Requirements.NBTTag"), true);
	
	private String name;
	private boolean index;
	
	RequirementType(String s, boolean b){
		name = s;
		index = b;
	}
	
	public String toCustomString(){
		return name;
	}
	
	public boolean hasIndex(){
		return index;
	}

}
