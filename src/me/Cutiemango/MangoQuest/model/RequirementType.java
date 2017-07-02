package me.Cutiemango.MangoQuest.model;

import me.Cutiemango.MangoQuest.I18n;

public enum RequirementType{
	LEVEL(I18n.locMsg("Requirements.Level"), false),
	MONEY(I18n.locMsg("Requirements.Money"), false),
	QUEST(I18n.locMsg("Requirements.Quest"), true),
	ITEM(I18n.locMsg("Requirements.Item"), true),
	SCOREBOARD(I18n.locMsg("Requirements.Scoreboard"), true),
	NBTTAG(I18n.locMsg("Requirements.NBTTag"), true);
	
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
