package me.Cutiemango.MangoQuest.conversation;

import java.util.List;

import org.bukkit.entity.Player;

import me.Cutiemango.MangoQuest.QuestStorage;
import me.Cutiemango.MangoQuest.manager.QuestGUIManager;

public class QuestConversation {
	
	private List<QuestBaseAction> action;
	private String name;
	
	public QuestConversation(String s, List<QuestBaseAction> list){
		name = s;
		action = list;
	}
	
	public void startNewConversation(Player p){
		ConversationProgress cp = new ConversationProgress(p, this);
		QuestStorage.ConvProgresses.put(p.getName(), cp);
		QuestGUIManager.openConversation(p);
		cp.nextAction();
		return;
	}
	
	public List<QuestBaseAction> getActions(){
		return action;
	}
	
	public String getName(){
		return name;
	}

}
