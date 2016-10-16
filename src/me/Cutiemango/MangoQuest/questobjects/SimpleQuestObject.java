package me.Cutiemango.MangoQuest.questobjects;

import me.Cutiemango.MangoQuest.conversation.QuestConversation;
import net.md_5.bungee.api.chat.TextComponent;

public abstract class SimpleQuestObject {
	
	public abstract TextComponent toTextComponent(boolean isFinished);

	public abstract String toPlainText();
	
	protected String config;
	protected QuestConversation conv = null;
	
	public String getConfigString(){
		return config;
	}
	
	public void setConfigString(String s){
		config = s;
	}
	
	public QuestConversation getConversation(){
		return conv;
	}
	
	public boolean hasConversation(){
		return !(conv == null);
	}
	
	public void setConversation(QuestConversation qc){
		conv = qc;
	}

}
