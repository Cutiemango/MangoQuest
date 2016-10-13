package me.Cutiemango.MangoQuest.conversation;

import java.util.List;

import org.bukkit.entity.Player;

import me.Cutiemango.MangoQuest.QuestStorage;
import me.Cutiemango.MangoQuest.QuestUtil;
import me.Cutiemango.MangoQuest.TextComponentFactory;
import me.Cutiemango.MangoQuest.manager.QuestGUIManager;
import net.md_5.bungee.api.chat.TextComponent;

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
	
	
	public QuestChoice(TextComponent q, Choice[] c){
		question = q;
		for (int i = 0; i < 4; i++){
			choices[i] = c[i];
		}
	}
	

	private Choice[] choices = new Choice[4];
	private TextComponent question;

	public TextComponent getQuestion(){
		return question;
	}
	
	public Choice[] getChoices(){
		return choices;
	}
	
	public void apply(ConversationProgress cp){
		QuestStorage.ChoiceProgresses.put(cp.getOwner().getName(), this);
		cp.getCurrentPage().addExtra(
				TextComponentFactory.regHoverEvent(TextComponentFactory.regClickCmdEvent((TextComponent)question.duplicate(), "/mq openchoice"),
						QuestUtil.translateColor("&e[點擊以前往問題介面]"))
				);
		QuestGUIManager.openChoice(cp.getOwner(), question, choices);
	}
	
	public void choose(Player p, int i){
		int count = 0;
		ConversationProgress cp = QuestStorage.ConvProgresses.get(p.getName());
		cp.retrieve();
		cp.getCurrentPage().addExtra(question);
		for (QuestBaseAction act : choices[i].getActions()){
			QuestUtil.getConvProgress(p).getActionQueue().add(count, act);
			count++;
		}
		count = 0;
		QuestUtil.getConvProgress(p).nextAction();
	}

}
