package me.Cutiemango.MangoQuest.exception;

import me.Cutiemango.MangoQuest.Quest;

public class InvalidQuestObjectException extends Exception{
	
	private static final long serialVersionUID = 1178471298121L;
	private final Quest quest;
	private final int stage;
	
	 public InvalidQuestObjectException(Quest quest, int stage) {
		 this.quest = quest;
		 this.stage = stage;
	 }
	 
	 public Quest getQuest(){
		 return quest;
	 }
	 
	 public int getStage(){
		 return stage;
	 }

}
