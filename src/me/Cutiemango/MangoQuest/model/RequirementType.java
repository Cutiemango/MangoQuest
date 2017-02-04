package me.Cutiemango.MangoQuest.model;

public enum RequirementType{
	LEVEL("等級需求", false), MONEY("金錢需求", false),
	QUEST("任務需求", true), ITEM("物品需求", true), SCOREBOARD("記分板分數需求", true), NBTTAG("記分板Tag需求", true);
	
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
