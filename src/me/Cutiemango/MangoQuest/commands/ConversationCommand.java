package me.Cutiemango.MangoQuest.commands;

import org.bukkit.entity.Player;

import me.Cutiemango.MangoQuest.QuestUtil;
import me.Cutiemango.MangoQuest.conversation.ConversationProgress;
import me.Cutiemango.MangoQuest.conversation.QuestChoice;
import me.Cutiemango.MangoQuest.conversation.QuestConversation;
import me.Cutiemango.MangoQuest.conversation.QuestConversationManager;
import me.Cutiemango.MangoQuest.manager.QuestGUIManager;
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
			QuestConversation conv = QuestConversationManager.getConversation(args[2]);
			ConversationProgress cp = QuestConversationManager.getConvProgress(sender);
			QuestChoice choice = QuestConversationManager.getChoiceProgress(sender);
			
			switch(args[1]){
			case "opennew":
				if (args.length == 3)
					if (conv != null)
						QuestConversationManager.startConversation(sender, conv);
				else break;
			case "open":
				if (args.length == 3)
					if (cp != null)
						QuestConversationManager.openConversation(sender, cp);
				else break;
			case "next":
				if (cp != null){
					cp.retrieve();
					cp.nextAction();
				}
				return;
			case "openchoice":
				if (choice != null)
					QuestGUIManager.openChoice(sender, choice.getQuestion(), choice.getChoices());
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
