package me.Cutiemango.MangoQuest.conversation;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import me.Cutiemango.MangoQuest.Main;
import me.Cutiemango.MangoQuest.QuestUtil;
import me.Cutiemango.MangoQuest.TextComponentFactory;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;

public class QuestBaseAction {
	
	private EnumAction action;
	private String obj;
	
	public QuestBaseAction(EnumAction act, String st){
		action = act;
		obj = st;
	}
	
	public enum EnumAction{
		CHANGE_PAGE, CHANGE_LINE, CHANGE_CONVERSATION, SENTENCE, NPC_TALK,
		CHOICE, BUTTON, COMMAND, WAIT
	}
	
	public void execute(final ConversationProgress cp){
		switch(action){
		case BUTTON:
			cp.getCurrentPage().addExtra(TextComponentFactory.regClickCmdEvent("&0[▼]", "/mq conv next"));
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
			QuestChoice c = QuestUtil.getChoiceByName(obj);
			if (c == null){
				QuestUtil.warnCmd("錯誤： 找不到指定的選擇 - " + obj);
				return;
			}
			c.apply(cp);
			break;
		case COMMAND:
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), obj);
			break;
		case SENTENCE:
			cp.getCurrentPage().addExtra(QuestUtil.translateColor(obj));
			cp.getCurrentPage().addExtra("\n");
			break;
		case NPC_TALK:
			String[] split = obj.split("@");
			NPC npc = CitizensAPI.getNPCRegistry().getById(Integer.parseInt(split[1]));
			if (npc != null){
				cp.getCurrentPage().addExtra(QuestUtil.translateColor(npc.getName() + "&0：「" + split[0] + "」"));
				cp.getCurrentPage().addExtra("\n");
			}
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