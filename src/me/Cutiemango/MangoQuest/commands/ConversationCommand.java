package me.Cutiemango.MangoQuest.commands;

import org.bukkit.entity.Player;

import me.Cutiemango.MangoQuest.QuestUtil;
import me.Cutiemango.MangoQuest.conversation.ConversationProgress;
import me.Cutiemango.MangoQuest.conversation.QuestChoice;
import me.Cutiemango.MangoQuest.conversation.QuestConversation;
import me.Cutiemango.MangoQuest.conversation.StartTriggerConversation;
import me.Cutiemango.MangoQuest.data.QuestPlayerData;
import me.Cutiemango.MangoQuest.conversation.ConversationManager;
import me.Cutiemango.MangoQuest.manager.QuestBookGUIManager;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;

public class ConversationCommand {
	
	//Command: /mq conv args[1] args[2]
	public static void execute(Player sender, String args[]){
		if (args.length == 1){
			sendHelp(sender);
			return;
		}
		else if (args.length >= 2){
			QuestConversation conv = null;
			if (args.length > 2)
				conv = ConversationManager.getConversation(args[2]);
			ConversationProgress cp = ConversationManager.getConvProgress(sender);
			QuestChoice choice = ConversationManager.getChoiceProgress(sender);
			
			switch(args[1]){
			case "opennew":
				if (args.length == 3)
					if (conv != null)
						ConversationManager.startConversation(sender, conv);
				else break;
			case "open":
				if (args.length == 3)
					if (cp != null)
						ConversationManager.openConversation(sender, cp);
				else break;
			case "next":
				if (cp != null){
					cp.retrieve();
					cp.nextAction();
				}
				return;
			case "openchoice":
				if (choice != null)
					QuestBookGUIManager.openChoice(sender, choice.getQuestion(), choice.getChoices());
				return;
			case "choose":
				if (choice != null)
					choice.choose(sender, Integer.parseInt(args[2]));
				return;
			case "npc":
				NPC npc = CitizensAPI.getNPCRegistry().getById(Integer.parseInt(args[2]));
				if (npc != null)
					QuestUtil.getData(sender).talkToNPC(npc);
				return;
			case "takequest":
				if (cp.getConversation() instanceof StartTriggerConversation)
				{
					StartTriggerConversation trigger = (StartTriggerConversation)cp.getConversation();
					QuestPlayerData pd = QuestUtil.getData(sender);
					pd.takeQuest(trigger.getQuest(), false);
					cp.retrieve();
					cp.getCurrentPage().add(trigger.getAcceptMessage()).changeLine();
					cp.getActionQueue().addAll(trigger.getAcceptActions());
					cp.nextAction();
					return;
				}
			case "denyquest":
				if (cp.getConversation() instanceof StartTriggerConversation)
				{
					StartTriggerConversation trigger = (StartTriggerConversation)cp.getConversation();
					cp.retrieve();
					cp.getCurrentPage().add(trigger.getDenyMessage()).changeLine();
					cp.getActionQueue().addAll(trigger.getDenyActions());
					cp.nextAction();
					return;
				}
			default:
				break;
			}
			sendHelp(sender);
			return;
		}
	}
	
	private static void sendHelp(Player p){
		
	}

}
