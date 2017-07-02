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
						// TODO
//						if (q.getStages().isEmpty())
//						{
//							QuestChatManager.error(sender, Questi18n.localizeMessage("EditorMessage.QuestEmpty"));
//							return;
//						}
//						Main.instance.configManager.saveQuest(q);
//						Quest.synchronizeLocal(q);
//						QuestChatManager.info(sender, "&a" + Questi18n.localizeMessage("EditorMessage.SaveConfigSuccess", q.getQuestName()));
//						QuestChatManager.info(sender, "&b" + Questi18n.localizeMessage("EditorMessage.SaveServerSuccess", q.getQuestName()));
						ConversationEditorManager.exit(sender);
						break;
					case "sc":
					case "savecfg":
						if (!ConversationEditorManager.checkEditorMode(sender, true))
							return;
//						if (q.getStages().isEmpty())
//						{
//							QuestChatManager.error(sender, Questi18n.localizeMessage("EditorMessage.QuestEmpty"));
//							return;
//						}
//						Main.instance.configManager.saveQuest(q);
//						QuestChatManager.info(sender, Questi18n.localizeMessage("EditorMessage.SaveConfigSuccess", q.getQuestName()));
						ConversationEditorManager.exit(sender);
						break;
					case "sl":
					case "savelocal":
						if (!ConversationEditorManager.checkEditorMode(sender, true))
							return;
//						if (q.getStages().isEmpty())
//						{
//							QuestChatManager.error(sender, Questi18n.localizeMessage("EditorMessage.QuestEmpty"));
//							return;
//						}
//						Quest.synchronizeLocal(q);
//						QuestChatManager.info(sender, Questi18n.localizeMessage("EditorMessage.SaveServerSuccess", q.getQuestName()));
						ConversationEditorManager.exit(sender);
						break;
				}
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
