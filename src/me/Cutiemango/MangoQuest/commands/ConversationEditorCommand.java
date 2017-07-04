package me.Cutiemango.MangoQuest.commands;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.entity.Player;
import me.Cutiemango.MangoQuest.I18n;
import me.Cutiemango.MangoQuest.commands.edtior.CommandEditConv;
import me.Cutiemango.MangoQuest.commands.edtior.CommandNewAction;
import me.Cutiemango.MangoQuest.commands.edtior.CommandNewConv;
import me.Cutiemango.MangoQuest.commands.edtior.CommandRemoveConv;
import me.Cutiemango.MangoQuest.conversation.QuestConversation;
import me.Cutiemango.MangoQuest.conversation.ConversationManager;
import me.Cutiemango.MangoQuest.editor.ConversationEditorManager;
import me.Cutiemango.MangoQuest.manager.QuestChatManager;
import me.Cutiemango.MangoQuest.manager.QuestConfigManager;

public class ConversationEditorCommand
{
	private static List<String> confirm = new ArrayList<>();
	
	public static void execute(Player sender, String[] args)
	{
		QuestConversation conv = ConversationEditorManager.getEditingConversation(sender);
		if (args.length == 1)
		{
			ConversationEditorManager.mainGUI(sender);
			return;
		}
		else
			if (args.length == 2)
			{
				switch (args[1])
				{
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
						if (conv.getActions().isEmpty())
						{
							QuestChatManager.error(sender, I18n.locMsg("EditorMessage.ConversationEmpty"));
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
						if (conv.getActions().isEmpty())
						{
							QuestChatManager.error(sender, I18n.locMsg("EditorMessage.ConversationEmpty"));
							return;
						}
						QuestConfigManager.getSaver().saveConversation(conv);
						QuestChatManager.info(sender, I18n.locMsg("EditorMessage.ConvSaveCfgSuccess", conv.getName()));
						break;
					case "sl":
					case "savelocal":
						if (!ConversationEditorManager.checkEditorMode(sender, true))
							return;
						if (conv.getActions().isEmpty())
						{
							QuestChatManager.error(sender, I18n.locMsg("EditorMessage.ConversationEmpty"));
							return;
						}
						QuestConversation.synchronizeLocal(conv);
						QuestChatManager.info(sender, I18n.locMsg("EditorMessage.ConvSaveSevSuccess", conv.getName()));
						break;
				}
				ConversationEditorManager.exit(sender);
				return;
			}
			else
			{
				switch (args[1])
				{
					case "select":
						if (ConversationManager.getConversation(args[2]) == null)
						{
							QuestChatManager.error(sender, I18n.locMsg("CommandInfo.ConversationNotFound"));
							return;
						}
						if (ConversationEditorManager.checkEditorMode(sender, false))
						{
							if (!confirm.contains(sender.getName()))
							{
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
