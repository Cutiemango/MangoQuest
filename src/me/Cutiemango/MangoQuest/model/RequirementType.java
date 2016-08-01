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
	
//	public static boolean vertify(RequirementType t, Object o){
//		switch (t){
//		case QUEST:
//			if (!(o instanceof List))
//				return false;
//		case LEVEL:
//			if (!(o instanceof Integer))
//				return false;
//		case MONEY:
//			if (!(o instanceof Double))
//				return false;
//		case ITEM:
//			if (!(o instanceof List))
//				return false;
//		case SCOREBOARD:
//			if (!(o instanceof List))
//				return false;
//		case NBTTAG:
//			if (!(o instanceof List))
//				return false;
//		}
//		return true;
//	}
}
