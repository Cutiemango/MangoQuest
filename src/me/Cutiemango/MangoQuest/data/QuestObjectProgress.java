package me.Cutiemango.MangoQuest.data;

import org.bukkit.entity.Player;

import me.Cutiemango.MangoQuest.conversation.ConversationProgress;
import me.Cutiemango.MangoQuest.manager.QuestGUIManager;
import me.Cutiemango.MangoQuest.questobjects.NumerableObject;
import me.Cutiemango.MangoQuest.questobjects.QuestObjectTalkToNPC;
import me.Cutiemango.MangoQuest.questobjects.SimpleQuestObject;

public class QuestObjectProgress {
	
	private boolean isFinished = false;
	private SimpleQuestObject obj;
	private ConversationProgress cp;
	private int i;
	
	public QuestObjectProgress(SimpleQuestObject object, int amount){
		obj = object;
		i = amount;
	}
	
	public void checkIfFinished(){
		if (obj instanceof QuestObjectTalkToNPC){
			if (i == 1 || (cp != null && cp.isFinished()))
				isFinished = true;
			return;
		}
		if (obj instanceof NumerableObject){
			if (((NumerableObject)obj).getAmount() == i)
				isFinished = true;
		}
		else if (obj instanceof SimpleQuestObject){
			if (i == 1)
				isFinished = true;
		}
	}
	
	public void newConversation(Player p){
		if (obj.hasConversation()){
			cp = obj.getConversation().startNewConversation(p);
			cp.nextAction();
			return;
		}
	}
	
	public void openConversation(Player p){
		if (cp == null)
			return;
		else if (cp.needReset())
			QuestGUIManager.updateConversation(p, cp);
		else if (obj.hasConversation() || cp.needReset())
			newConversation(p);
		else
			finish();
		return;
	}
	
	public SimpleQuestObject getObject(){
		return obj;
	}
	
	public int getProgress(){
		return i;
	}
	
	public boolean isFinished(){
		return isFinished;
	}
	
	public void finish(){
		isFinished = true;
	}
	
	public void setProgress(int p){
		i = p;
	}

}
