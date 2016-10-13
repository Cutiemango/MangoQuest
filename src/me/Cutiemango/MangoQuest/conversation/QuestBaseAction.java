package me.Cutiemango.MangoQuest.conversation;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import me.Cutiemango.MangoQuest.Main;
import me.Cutiemango.MangoQuest.QuestUtil;
import me.Cutiemango.MangoQuest.TextComponentFactory;

public class QuestBaseAction {
	
	private EnumAction action;
	private Object obj;
	
	public QuestBaseAction(EnumAction act, Object o){
		action = act;
		obj = o;
		
		switch(act){
		case WAIT:
			if (!(o instanceof Integer))
				QuestUtil.warnCmd("ERROR: EnumAction.WAIT used a wrong object value.");
			break;
		case BUTTON:
			break;
		case CHANGE_LINE:
			break;
		case CHANGE_PAGE:
			break;
		case CHOICE:
			if (!(o instanceof QuestChoice))
				QuestUtil.warnCmd("ERROR: EnumAction.CHOICE used a wrong object value.");
			break;
		case COMMAND:
			if (!(o instanceof String))
				QuestUtil.warnCmd("ERROR: EnumAction.COMMAND used a wrong object value.");
			break;
		case SENTENCE:
			if (!(o instanceof String))
				QuestUtil.warnCmd("ERROR: EnumAction.SENTENCE used a wrong object value.");
			break;
		case CHANGE_CONVERSATION:
			if (!(o instanceof QuestConversation))
				QuestUtil.warnCmd("ERROR: EnumAction.CHANGE_CONVERSATION used a wrong object value.");
			break;
		}
	}
	
	public enum EnumAction{
		CHANGE_PAGE, CHANGE_LINE, CHANGE_CONVERSATION, SENTENCE,
		CHOICE, BUTTON, COMMAND, WAIT
	}
	
	public void execute(final ConversationProgress cp){
		switch(action){
		case BUTTON:
			cp.getCurrentPage().addExtra(TextComponentFactory.regClickCmdEvent("&0[▼]", "/mq nextconv"));
			cp.getCurrentPage().addExtra("\n");
			return;
		case CHANGE_CONVERSATION:
			// TODO Complicated
			break;
		case CHANGE_LINE:
			cp.getCurrentPage().addExtra("\n");
			break;
		case CHANGE_PAGE:
			cp.newPage();
			break;
		case CHOICE:
			((QuestChoice)obj).apply(cp);
			break;
		case COMMAND:
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), (String)obj);
			break;
		case SENTENCE:
			cp.getCurrentPage().addExtra(QuestUtil.translateColor((String)obj));
			cp.getCurrentPage().addExtra("\n");
			break;
		case WAIT:
			new BukkitRunnable(){
				@Override
				public void run() {
					cp.nextAction();
					return;
				}
			}.runTaskLater(Main.instance, Long.parseLong(obj.toString()));
			return;
		default:
			return;
		}
	}
	
	public EnumAction getActionType(){
		return action;
	}

}