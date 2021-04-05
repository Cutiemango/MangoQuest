package me.Cutiemango.MangoQuest.commands;

import me.Cutiemango.MangoQuest.DebugHandler;
import me.Cutiemango.MangoQuest.I18n;
import me.Cutiemango.MangoQuest.commands.edtior.CommandEditConv;
import me.Cutiemango.MangoQuest.commands.edtior.CommandNewAction;
import me.Cutiemango.MangoQuest.commands.edtior.CommandNewConv;
import me.Cutiemango.MangoQuest.commands.edtior.CommandRemoveConv;
import me.Cutiemango.MangoQuest.conversation.ConversationManager;
import me.Cutiemango.MangoQuest.conversation.ConversationProgress;
import me.Cutiemango.MangoQuest.conversation.QuestBaseAction;
import me.Cutiemango.MangoQuest.conversation.QuestConversation;
import me.Cutiemango.MangoQuest.conversation.StartTriggerConversation;
import me.Cutiemango.MangoQuest.editor.ConversationEditorManager;
import me.Cutiemango.MangoQuest.manager.QuestChatManager;
import me.Cutiemango.MangoQuest.manager.config.QuestConfigManager;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ConversationEditorCommand
{
	private static List<String> confirm = new ArrayList<>();

	public static void execute(Player sender, String[] args) {
		if (!sender.hasPermission("MangoQuest.ConversationEditor")) {
			QuestChatManager.error(sender, I18n.locMsg("CommandInfo.NoPermission"));
			return;
		}
		QuestConversation conv = ConversationEditorManager.getEditingConversation(sender);
		if (args.length == 1)
			ConversationEditorManager.mainGUI(sender);
		else if (args.length == 2) {
			switch (args[1]) {
				case "cp":
					if (ConversationManager.hasConvProgress(sender)) {
						ConversationProgress cp = ConversationManager.getConvProgress(sender);
						DebugHandler.log(5, "There are " + cp.getActionQueue().size() + " actions left.");
						for (QuestBaseAction act : cp.getActionQueue()) {
							DebugHandler.log(5, "Type: " + act.getActionType() + ", Object: " + act.getObject());
						}
						return;
					}
					return;
				case "modconv":
					if (!ConversationEditorManager.checkEditorMode(sender, false))
						return;
					ConversationManager.simulateConversation(sender, conv);
					return;
				case "edit":
					ConversationEditorManager.editGUI(sender);
					return;
				case "new":
					CommandNewAction.execute(conv, sender, args);
					return;
				case "remove":
					ConversationEditorManager.removeGUI(sender);
					return;
				case "exit":
					ConversationEditorManager.exit(sender);
					ConversationEditorManager.mainGUI(sender);
					return;
				case "gui":
					ConversationEditorManager.editConversation(sender);
					return;
				case "newconv":
					CommandNewConv.execute(sender, args);
					return;
				case "sa":
				case "saveall":
					if (!ConversationEditorManager.checkEditorMode(sender, true))
						return;
					if (conv.getActions().isEmpty()) {
						QuestChatManager.error(sender, I18n.locMsg("EditorMessage.ConversationEmpty"));
						return;
					} else if (conv instanceof StartTriggerConversation && ((StartTriggerConversation) conv).getQuest() == null) {
						QuestChatManager.error(sender, I18n.locMsg("EditorMessage.TriggerQuestEmpty"));
						return;
					}
					QuestConfigManager.getSaver().saveConversation(conv);
					QuestConversation.synchronizeLocal(conv);
					QuestChatManager.info(sender, I18n.locMsg("EditorMessage.ConvSaveCfgSuccess", conv.getName()));
					QuestChatManager.info(sender, I18n.locMsg("EditorMessage.ConvSaveSevSuccess", conv.getName()));
					break;
				case "sc":
				case "savecfg":
					if (!ConversationEditorManager.checkEditorMode(sender, true))
						return;
					if (conv.getActions().isEmpty()) {
						QuestChatManager.error(sender, I18n.locMsg("EditorMessage.ConversationEmpty"));
						return;
					} else if (conv instanceof StartTriggerConversation && ((StartTriggerConversation) conv).getQuest() == null) {
						QuestChatManager.error(sender, I18n.locMsg("EditorMessage.TriggerQuestEmpty"));
						return;
					}
					QuestConfigManager.getSaver().saveConversation(conv);
					QuestChatManager.info(sender, I18n.locMsg("EditorMessage.ConvSaveCfgSuccess", conv.getName()));
					break;
				case "sl":
				case "savelocal":
					if (!ConversationEditorManager.checkEditorMode(sender, true))
						return;
					if (conv.getActions().isEmpty()) {
						QuestChatManager.error(sender, I18n.locMsg("EditorMessage.ConversationEmpty"));
						return;
					}
					QuestConversation.synchronizeLocal(conv);
					QuestChatManager.info(sender, I18n.locMsg("EditorMessage.ConvSaveSevSuccess", conv.getName()));
					break;
			}
			ConversationEditorManager.exit(sender);
		} else {
			switch (args[1]) {
				case "select":
					if (ConversationManager.getConversation(args[2]) == null) {
						QuestChatManager.error(sender, I18n.locMsg("CommandInfo.ConversationNotFound"));
						return;
					}
					if (ConversationEditorManager.checkEditorMode(sender, false)) {
						if (!confirm.contains(sender.getName())) {
							QuestChatManager.error(sender, I18n.locMsg("EditorMessage.QuitEditing"));
							confirm.add(sender.getName());
							return;
						}
					}
					ConversationEditorManager.edit(sender, ConversationManager.getConversation(args[2]).clone());
					ConversationEditorManager.editConversation(sender);
					confirm.remove(sender.getName());
					QuestChatManager.info(sender, "&c" + I18n.locMsg("EditorMessage.Entered"));
					return;
				case "new":
					CommandNewAction.execute(conv, sender, args);
					return;
				case "edit":
					CommandEditConv.execute(conv, sender, args);
					return;
				case "newconv":
					CommandNewConv.execute(sender, args);
					return;
				case "remove":
					CommandRemoveConv.execute(conv, sender, args);
					return;
			}
			ConversationEditorManager.mainGUI(sender);
		}
	}
}
