package me.Cutiemango.MangoQuest.model;

import java.util.ArrayList;
import java.util.List;

import me.Cutiemango.MangoQuest.questobjects.SimpleQuestObject;

public class QuestStage {
	
	public QuestStage(String sm, String fm, List<SimpleQuestObject> obj){
		startMessage = sm;
		finishMessage = fm;
		AllObjects = obj;
	}

	private String startMessage = null;
	private String finishMessage = null;
	private List<SimpleQuestObject> AllObjects = new ArrayList<>();

	public String getStartMessage() {
		return (startMessage != null) ? startMessage : null;
	}

	public void setStartMessage(String msg) {
		this.startMessage = msg;
	}

	public String getFinishMessage() {
		return (finishMessage != null) ? finishMessage : null;
	}

	public void setFinishMessage(String msg) {
		this.finishMessage = msg;
	}

	public boolean hasStartMessage() {
		return (this.startMessage != null);
	}

	public boolean hasFinishMessage() {
		return (this.finishMessage != null);
	}
	
	public List<SimpleQuestObject> getObjects(){
		return AllObjects;
	}
	
	public SimpleQuestObject getObject(int index){
		return AllObjects.get(index);
	}

}
