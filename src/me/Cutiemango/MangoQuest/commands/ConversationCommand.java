package me.Cutiemango.MangoQuest.commands;

import me.Cutiemango.MangoQuest.ConfigSettings;
import me.Cutiemango.MangoQuest.Main;
import me.Cutiemango.MangoQuest.QuestUtil;
import me.Cutiemango.MangoQuest.conversation.*;
import me.Cutiemango.MangoQuest.manager.QuestBookGUIManager;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.entity.Player;

public class ConversationCommand
{
	// Command: /mq conv args[1] args[2]
	public static void execute(Player sender, String[] args) {
		if (args.length == 1)
			sendHelp(sender);
		else if (args.length >= 2) {
			QuestConversation conv = null;
			if (args.length > 2)
				conv = ConversationManager.getConversation(args[2]);
			ConversationProgress cp = ConversationManager.getConvProgress(sender);
			QuestChoice choice = ConversationManager.getChoiceProgress(sender);

			switch (args[1]) {
				case "opennew":
					if (conv != null)
						ConversationManager.startConversation(sender, conv);
					return;
				case "open":
					if (cp != null)
						ConversationManager.openConversation(sender, cp);
					return;
				case "next":
					if (cp != null) {
						cp.retrieve();
						cp.nextAction();
					}
					return;
				case "skip":
					if (ConfigSettings.ENABLE_SKIP && cp != null)
						cp.rush();
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
					NPC npc = Main.getHooker().getNPC(args[2]);
					if (npc != null)
						QuestUtil.getData(sender).talkToNPC(npc);
					return;
				case "takequest":
					if (cp.getConversation() instanceof StartTriggerConversation) {
						StartTriggerConversation trigger = (StartTriggerConversation) cp.getConversation();
						cp.retrieve();
						cp.getCurrentPage().add(trigger.getAcceptMessage()).changeLine();
						cp.getActionQueue().addAll(trigger.getAcceptActions());
						cp.nextAction();
						return;
					}
				case "denyquest":
					if (cp.getConversation() instanceof StartTriggerConversation) {
						StartTriggerConversation trigger = (StartTriggerConversation) cp.getConversation();
						cp.retrieve();
						cp.getCurrentPage().add(trigger.getDenyMessage()).changeLine();
						cp.getActionQueue().addAll(trigger.getDenyActions());
						cp.nextAction();
					}
				default:
					break;
			}
			sendHelp(sender);
		}
	}

	private static void sendHelp(Player p) {

	}
}
