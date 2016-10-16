package me.Cutiemango.MangoQuest.commands;

import org.bukkit.entity.Player;

import me.Cutiemango.MangoQuest.QuestUtil;
import me.Cutiemango.MangoQuest.conversation.QuestChoice;
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
			switch(args[1]){
			case "opennew":
				if (args.length == 3)
					if (QuestUtil.getConvByName(args[2]) != null)
						QuestUtil.getConvByName(args[2]).startNewConversation(sender);
				else break;
			case "open":
				if (args.length == 3)
					if (QuestUtil.getConvProgress(sender) != null)
						QuestGUIManager.updateConversation(sender, QuestUtil.getConvProgress(sender));
				else break;
			case "next":
				if (QuestUtil.getConvProgress(sender) != null){
					QuestUtil.getConvProgress(sender).retrieve();
					QuestUtil.getConvProgress(sender).nextAction();
				}
				return;
			case "openchoice":
				if (QuestUtil.getChoice(sender) != null){
					QuestChoice c = QuestUtil.getChoice(sender);
					QuestGUIManager.openChoice(sender, c.getQuestion(), c.getChoices());
				}
				return;
			case "choose":
				if (QuestUtil.getChoice(sender) != null)
					QuestUtil.getChoice(sender).choose(sender, Integer.parseInt(args[2]));
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
