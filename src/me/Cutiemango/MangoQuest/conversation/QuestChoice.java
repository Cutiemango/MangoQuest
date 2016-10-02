package me.Cutiemango.MangoQuest.conversation;

import java.util.List;

import org.bukkit.entity.Player;

import me.Cutiemango.MangoQuest.QuestStorage;
import me.Cutiemango.MangoQuest.QuestUtil;
import me.Cutiemango.MangoQuest.TextComponentFactory;

public class QuestChoice {

	public static class Choice{
		private String s;
		private List<QuestBaseAction> act;
		
		public Choice(String c, List<QuestBaseAction> action){
			s = c;
			act = action;
		}
		
		public String getContent(){
			return s;
		}
		
		public List<QuestBaseAction> getActions(){
			return act;
		}
		
	}
	
	
	public QuestChoice(Choice[] c){
		for (int i = 0; i < 4; i++){
			choices[i] = c[i];
		}
	}
	

	private Choice[] choices = new Choice[4];
	
	public void apply(ConversationProgress cp){
		QuestStorage.ChoiceProgresses.put(cp.getOwner().getName(), this);
		cp.getCurrentPage().addExtra(
				TextComponentFactory.registerChangePageEvent("[選擇]", cp.newPage()));
		cp.getCurrentPage().addExtra("\n");
		for (int i = 0; i < 4; i++){
			cp.getCurrentPage().addExtra(
					TextComponentFactory.registerClickCommandEvent("- " + choices[i].getContent(), "/mq choose " + i));
			cp.getCurrentPage().addExtra("\n");
		}
	}
	
	public void choose(Player p, int i){
		int count = 0;
		for (QuestBaseAction act : choices[i].getActions()){
			QuestUtil.getConvProgress(p).getActionQueue().add(count, act);
			count++;
		}
		count = 0;
		QuestUtil.getConvProgress(p).nextAction();
	}

}
