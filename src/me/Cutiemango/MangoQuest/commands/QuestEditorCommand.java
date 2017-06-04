package me.Cutiemango.MangoQuest.commands;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.entity.Player;
import me.Cutiemango.MangoQuest.Main;
import me.Cutiemango.MangoQuest.QuestChatManager;
import me.Cutiemango.MangoQuest.QuestUtil;
import me.Cutiemango.MangoQuest.Questi18n;
import me.Cutiemango.MangoQuest.commands.edtior.CommandAddnew;
import me.Cutiemango.MangoQuest.commands.edtior.CommandEdit;
import me.Cutiemango.MangoQuest.commands.edtior.CommandNewQuest;
import me.Cutiemango.MangoQuest.commands.edtior.CommandRemove;
import me.Cutiemango.MangoQuest.editor.QuestEditorManager;
import me.Cutiemango.MangoQuest.model.Quest;

public class QuestEditorCommand
{

	private static List<String> confirm = new ArrayList<>();

	// Command: /mq editor args[1] args[2]
	public static void execute(Player sender, String[] args)
	{
		if (!sender.isOp())
			return;
		Quest q = QuestEditorManager.getCurrentEditingQuest(sender);
		if (args.length == 1)
		{
			QuestEditorManager.mainGUI(sender);
			return;
		}
		else
			if (args.length == 2)
			{
				switch (args[1])
				{
					case "edit":
						QuestEditorManager.editGUI(sender);
						return;
					case "remove":
						QuestEditorManager.removeGUI(sender);
						return;
					case "help":
						QuestChatManager.info(sender, Questi18n.localizeMessage("EditorMessage.UseCommand"));
						return;
					case "exit":
						QuestEditorManager.exit(sender);
						QuestEditorManager.mainGUI(sender);
						return;
					case "gui":
						QuestEditorManager.editQuest(sender);
						return;
					case "newquest":
						CommandNewQuest.execute(sender, args);
						return;
					case "sa":
					case "saveall":
						if (!QuestEditorManager.isInEditorMode(sender))
						{
							QuestChatManager.error(sender, Questi18n.localizeMessage("EditorMessage.NotInEditor"));
							return;
						}
						if (q.getStages().isEmpty())
						{
							QuestChatManager.error(sender, Questi18n.localizeMessage("EditorMessage.QuestEmpty"));
							return;
						}
						Main.instance.configManager.saveQuest(q);
						Quest.synchronizeLocal(q);
						QuestChatManager.info(sender, "&a" + Questi18n.localizeMessage("EditorMessage.SaveConfigSuccess", q.getQuestName()));
						QuestChatManager.info(sender, "&b" + Questi18n.localizeMessage("EditorMessage.SaveServerSuccess", q.getQuestName()));
						break;
					case "sc":
					case "savecfg":
						if (!QuestEditorManager.isInEditorMode(sender))
						{
							QuestChatManager.error(sender, Questi18n.localizeMessage("EditorMessage.NotInEditor"));
							return;
						}
						if (q.getStages().isEmpty())
						{
							QuestChatManager.error(sender, Questi18n.localizeMessage("EditorMessage.QuestEmpty"));
							return;
						}
						Main.instance.configManager.saveQuest(q);
						QuestChatManager.info(sender, "&a" + Questi18n.localizeMessage("EditorMessage.SaveConfigSuccess", q.getQuestName()));
						break;
					case "sl":
					case "savelocal":
						if (!QuestEditorManager.isInEditorMode(sender))
						{
							QuestChatManager.error(sender, Questi18n.localizeMessage("EditorMessage.NotInEditor"));
							return;
						}
						if (q.getStages().isEmpty())
						{
							QuestChatManager.error(sender, Questi18n.localizeMessage("EditorMessage.QuestEmpty"));
							return;
						}
						Quest.synchronizeLocal(q);
						QuestChatManager.info(sender, "&b" + Questi18n.localizeMessage("EditorMessage.SaveServerSuccess", q.getQuestName()));
						break;
				}
			}
			else
			{
				switch (args[1])
				{
					case "select":
						if (QuestUtil.getQuest(args[2]) == null)
						{
							QuestChatManager.error(sender, Questi18n.localizeMessage("EditorMessage.QuestNotFound"));
							return;
						}
						if (QuestEditorManager.isInEditorMode(sender))
						{
							if (!confirm.contains(sender.getName()))
							{
								QuestChatManager.error(sender, Questi18n.localizeMessage("EditorMessage.QuitEditing"));
								confirm.add(sender.getName());
								return;
							}
						}
						QuestEditorManager.edit(sender, QuestUtil.getQuest(args[2]).clone());
						QuestEditorManager.editQuest(sender);
						confirm.remove(sender.getName());
						QuestChatManager.info(sender, "&c" + Questi18n.localizeMessage("EditorMessage.Entered"));
						return;
					case "addnew":
						CommandAddnew.execute(q, sender, args);
						return;
					case "edit":
						CommandEdit.execute(q, sender, args);
						return;
					case "newquest":
						CommandNewQuest.execute(sender, args);
						return;
					case "remove":
						CommandRemove.execute(q, sender, args);
						return;
				}
				QuestEditorManager.mainGUI(sender);
			}
	}

}
